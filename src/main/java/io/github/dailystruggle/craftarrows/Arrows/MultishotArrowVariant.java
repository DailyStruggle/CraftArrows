package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Listener.CraftArrowListener;
import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;

public class MultishotArrowVariant extends ArrowVariant {
    private double delay;

    private double arrowCount;

    private boolean removeOnLand;

    public MultishotArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Multishot", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Multishot.ArrowCount", 3));
                this.add(new ConfigValue<>("Recipes.Multishot.DelayBetweenShots", 4));
                this.add(new ConfigValue<>("Recipes.Multishot.RemoveOnLand", true));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.arrowCount = Convert.Convert(Integer.class, configValues.get(0).getValue(config)).intValue();
        this.delay = Convert.Convert(Double.class, configValues.get(1).getValue(config)).doubleValue();
        this.removeOnLand = ((Boolean) configValues.get(2).getValue(config)).booleanValue();
    }

    public void onShoot(EntityShootBowEvent event) {
        for (int i = 1; i < this.arrowCount; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(CraftArrows.instance, () -> {
                Arrow arrow = event.getEntity().launchProjectile(Arrow.class);
                if (this.removeOnLand) CraftArrowListener.removeArrowsOnHit.add(arrow);
            }, (long) this.delay * i);
        }
    }

    public void onEntityHit(ProjectileHitEvent event) {
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }
}
