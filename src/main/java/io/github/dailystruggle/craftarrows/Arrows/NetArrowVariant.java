package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;

public class NetArrowVariant extends ArrowVariant {
    private boolean cleanUp;

    private int delay;

    public NetArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Net", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Net.CleanUpWebs", 100));
                this.add(new ConfigValue<>("Recipes.Net.CleanUpWebsDelay", true));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.cleanUp = ((Boolean) configValues.get(0).getValue(config)).booleanValue();
        this.delay = Convert.Convert(Integer.class, configValues.get(1).getValue(config)).intValue();
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        net(event.getHitEntity().getLocation());
    }

    public void onBlockHit(ProjectileHitEvent event) {
        net(event.getHitBlock().getLocation());
    }

    private void net(Location location) {
        HashMap<Block, Material> changed = new HashMap<>();
        Block block = location.getBlock();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block relative = block.getRelative(x, y, z);
                    if (relative.getType() == Material.AIR && relative.getRelative(0, -1, 0).getType() != Material.AIR) {
                        changed.put(relative, relative.getType());
                        relative.setType(Material.COBWEB);
                    }
                }
            }
        }
        if (this.cleanUp)
            Bukkit.getScheduler().runTaskLater((Plugin) CraftArrows.instance, () -> {
                for (Block changedBlock : changed.keySet())
                    changedBlock.setType(changed.get(changedBlock));
            }, this.delay);
    }
}
