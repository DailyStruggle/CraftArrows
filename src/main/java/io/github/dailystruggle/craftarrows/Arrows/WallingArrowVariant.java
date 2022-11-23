package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import io.github.dailystruggle.craftarrows.Util.DirectionHelper;
import io.github.dailystruggle.craftarrows.Util.OutputHandler;
import io.github.dailystruggle.craftarrows.Util.TryParse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class WallingArrowVariant extends ArrowVariant {
    private boolean cleanUp;
    private int delay;
    private ArrayList<Material> blocks;
    private final Random random = new Random();
    private Vector wallSize;
    private final HashMap<Entity, DirectionHelper.Direction> projectileDirections = new HashMap<>();

    public WallingArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Walling", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Walling.CleanUp", true));
                this.add(new ConfigValue<>("Recipes.Walling.CleanUpDelay", 100));
                this.add(new ConfigValue<>("Recipes.Walling.Blocks", new ArrayList<String>() {
                    {
                        this.add("DIRT");
                        this.add("STONE");
                        this.add("COBBLESTONE");
                    }
                }));
                this.add(new ConfigValue<>("Recipes.Walling.Wall.Width", 5));
                this.add(new ConfigValue<>("Recipes.Walling.Wall.Length", 2));
                this.add(new ConfigValue<>("Recipes.Walling.Wall.Height", 3));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = this.getConfigValues();
        this.cleanUp = Boolean.parseBoolean(configValues.get(0).getValue(config).toString());
        this.delay = Convert.Convert(Integer.class, ((ConfigValue)configValues.get(1)).getValue(config));
        this.blocks = new ArrayList<>();

        for(String material : ((List<?>) configValues.get(2).getValue(config)).stream().map(Object::toString)
                .collect(Collectors.toList())) {
            if (TryParse.parseMaterial(material)) {
                this.blocks.add(Material.valueOf(material));
            } else {
                OutputHandler.PrintError(
                    "Material, "
                        + OutputHandler.HIGHLIGHT
                        + material
                        + OutputHandler.ERROR
                        + " is not a valid material (in "
                        + "Recipes"
                        + "."
                        + "Walling"
                        + ".Blocks)"
                );
            }
        }

        int width = Convert.ConvertToInteger(configValues.get(3).getValue(config));
        int length = Convert.ConvertToInteger((configValues.get(4)).getValue(config));
        int height = Convert.ConvertToInteger((configValues.get(5)).getValue(config));
        this.wallSize = new Vector(width, height, length);
    }

    public void onShoot(EntityShootBowEvent event) {
        this.projectileDirections.put(event.getProjectile(), DirectionHelper.getCardinalDirection(event.getEntity()));
    }

    public void onEntityHit(ProjectileHitEvent event) {
        this.makeWall(this.projectileDirections.remove(event.getEntity()), Objects.requireNonNull(event.getHitEntity()).getLocation().getBlock());
    }

    public void onBlockHit(ProjectileHitEvent event) {
        this.makeWall(this.projectileDirections.remove(event.getEntity()), Objects.requireNonNull(event.getHitBlock()));
    }

    private Material getNextMaterial() {
        return this.blocks.get(this.random.nextInt(this.blocks.size()));
    }

    private void makeWall(DirectionHelper.Direction direction, Block block) {
        while(block.getType() != Material.AIR) {
            block = block.getRelative(0, 1, 0);
        }

        int startX = 0;
        int endX = 0;
        int startZ = 0;
        int endZ = 0;
        int height = this.wallSize.getBlockY();
        switch(direction) {
            case NORTH:
                startX = block.getX() + 1 + this.wallSize.getBlockX() / 2;
                endX = startX - this.wallSize.getBlockX();
                startZ = block.getZ();
                endZ = startZ - this.wallSize.getBlockZ();
                break;
            case SOUTH:
                startX = block.getX() - this.wallSize.getBlockX() / 2;
                endX = startX + this.wallSize.getBlockX();
                startZ = block.getZ();
                endZ = startZ + this.wallSize.getBlockZ();
                break;
            case EAST:
                startX = block.getX();
                endX = startX + this.wallSize.getBlockZ();
                startZ = block.getZ() - this.wallSize.getBlockX() / 2;
                endZ = startZ + this.wallSize.getBlockX();
                break;
            case WEST:
                startX = block.getX();
                endX = startX - this.wallSize.getBlockZ();
                startZ = block.getZ() + 1 + this.wallSize.getBlockX() / 2;
                endZ = startZ - this.wallSize.getBlockX();
        }

        ArrayList<Block> blocks = new ArrayList<>();

        for(int x = Math.min(startX, endX); x < Math.max(startX, endX); ++x) {
            for(int z = Math.min(startZ, endZ); z < Math.max(startZ, endZ); ++z) {
                for(int y = block.getY(); y < block.getY() + height; ++y) {
                    Block target = block.getWorld().getBlockAt(x, y, z);
                    if (target.getType() == Material.AIR) {
                        blocks.add(target);
                        target.setType(this.getNextMaterial());
                    }
                }
            }
        }

        if (this.cleanUp) {
            Bukkit.getScheduler().runTaskLater(CraftArrows.instance, () -> {
                for(Block targetx : blocks) {
                    targetx.setType(Material.AIR);
                }
            }, this.delay);
        }
    }
}
