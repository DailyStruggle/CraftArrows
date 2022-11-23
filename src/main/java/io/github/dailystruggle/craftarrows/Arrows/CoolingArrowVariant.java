package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class CoolingArrowVariant extends ArrowVariant {
    private boolean cleanUp;

    private int delay;

    public CoolingArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Cooling", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Cooling.CleanUp", true));
                this.add(new ConfigValue<>("Recipes.Cooling.CleanUpDelay", 600));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.cleanUp = (Boolean) configValues.get(0).getValue(config);
        this.delay = Convert.Convert(Integer.class, configValues.get(1).getValue(config));
    }

    public void onShoot(EntityShootBowEvent event) {
        final Arrow arrow = (Arrow) event.getProjectile();
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                Location location = arrow.getLocation();
                Block block = location.getBlock();
                Material material = block.getType();
                if (material == Material.LAVA) {
                    arrow.remove();
                    CoolingArrowVariant.this.cool(location);
                    cancel();
                }
            }
        };
        runnable.runTaskTimer(CraftArrows.instance, 1L, 1L);
    }

    public void onEntityHit(ProjectileHitEvent event) {
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }

    private void cool(Location location) {
        HashMap<Block, Material> changed = new HashMap<>();
        Block block = location.getBlock();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block relative = block.getRelative(x, y, z);
                    if (relative.getType() == Material.LAVA) {
                        changed.put(relative, relative.getType());
                        relative.setType(Material.OBSIDIAN);
                    }
                }
            }
        }
        if (this.cleanUp)
            Bukkit.getScheduler().runTaskLater(CraftArrows.instance, () -> {
                for (Block changedBlock : changed.keySet())
                    changedBlock.setType(changed.get(changedBlock));
            }, this.delay);
    }
}
