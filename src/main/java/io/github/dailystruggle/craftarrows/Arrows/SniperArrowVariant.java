package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class SniperArrowVariant extends ArrowVariant {
    private double velocityMultiplier;

    public SniperArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Sniper", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Sniper.VelocityMultiplier", 2.0));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.velocityMultiplier = Convert.Convert(Double.class, configValues.get(0).getValue(config)).doubleValue();
    }

    public void onShoot(EntityShootBowEvent event) {
        Entity projectile = event.getProjectile();
        projectile.setGravity(false);
        Vector velocity = projectile.getVelocity().multiply(this.velocityMultiplier);
        projectile.setVelocity(velocity);
        new SniperRunnable(projectile, velocity);
    }

    public void onEntityHit(ProjectileHitEvent event) {
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }

    private class SniperRunnable extends BukkitRunnable {
        private final Entity arrow;

        private final Vector velocity;
        private final int maxCount = 20;
        private int count = 0;

        public SniperRunnable(Entity arrow, Vector velocity) {
            this.arrow = arrow;
            this.velocity = velocity;
            runTaskTimer(CraftArrows.instance, 2L, 2L);
        }

        public void run() {
            if (this.count >= this.maxCount)
                this.arrow.remove();
            if (this.arrow.isValid() && !this.arrow.isDead() && this.count < this.maxCount) {
                this.arrow.setVelocity(this.velocity);
            } else {
                cancel();
            }
            this.count++;
        }
    }
}
