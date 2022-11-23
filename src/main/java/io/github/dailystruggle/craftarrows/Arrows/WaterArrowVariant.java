package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;

public class WaterArrowVariant extends ArrowVariant {
    private boolean cleanUp;

    private int delay;

    public WaterArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Water", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Water.CleanUpWater", true));
                this.add(new ConfigValue<>("Recipes.Water.CleanUpWaterDelay", 100));
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
        setWater(event.getHitEntity().getLocation().getBlock());
    }

    public void onBlockHit(ProjectileHitEvent event) {
        setWater(event.getHitBlock());
    }

    private void setWater(Block block) {
        while (block.getType() != Material.AIR)
            block = block.getRelative(0, 1, 0);
        Block finalBlock = block;
        finalBlock.setType(Material.WATER);
        if (this.cleanUp)
            Bukkit.getScheduler().runTaskLater(CraftArrows.instance, () -> finalBlock.setType(Material.AIR), this.delay);
    }
}
