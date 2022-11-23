package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;

public class FireArrowVariant extends ArrowVariant {
    private boolean burnBlocks;

    private int duration = 60;

    public FireArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Fire", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Fire.BurnBlocks", true));
                this.add(new ConfigValue<>("Recipes.Fire.Duration", 60));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.burnBlocks = (Boolean) configValues.get(0).getValue(config);
        this.duration = Convert.Convert(Integer.class, configValues.get(1).getValue(config));
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        if (event.getHitEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getHitEntity();
            entity.setFireTicks(this.duration);
        }
    }

    public void onBlockHit(ProjectileHitEvent event) {
        Block block = event.getHitBlock();
        HashMap<Block, Material> changed = new HashMap<>();
        Location loc = block.getLocation();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block relative = block.getRelative(x, y, z);
                    if (relative.getType() == Material.AIR || relative.getType() == Material.SNOW) {
                        changed.put(relative, relative.getType());
                        relative.setType(Material.FIRE);
                    }
                }
            }
        }
        Bukkit.getScheduler().runTaskLater((Plugin) CraftArrows.instance, () -> {
            for (Block changedBlock : changed.keySet())
                changedBlock.setType(changed.get(changedBlock));
        }, this.duration);
    }
}
