package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Data.ArrowManager;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import io.github.dailystruggle.craftarrows.Util.VectorHelper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class HomingArrowVariant extends CombatArrowVariant {
    private double velocity;

    private double damage;

    public HomingArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Homing", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Homing.Velocity", 0.6));
                this.add(new ConfigValue<>("Recipes.Homing.Damage", 5.0));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.velocity = Convert.Convert(Double.class, configValues.get(0).getValue(config));
        this.damage = Convert.Convert(Double.class, configValues.get(1).getValue(config));
    }

    public void onShoot(EntityShootBowEvent event) {
        Entity projectile = event.getProjectile();
        projectile.setGravity(false);
        projectile.setVelocity(projectile.getVelocity().normalize().multiply(100));
    }

    public void onEntityHit(EntityDamageByEntityEvent event, Projectile projectile, LivingEntity entity) {
        if (projectile.getShooter() instanceof LivingEntity) {
            event.setCancelled(true);
            HomingArrowRunnable runnable = new HomingArrowRunnable((LivingEntity) projectile.getShooter(), this.velocity, this.damage);
            runnable.setTarget(entity);
            runnable.runTaskTimer(CraftArrows.instance, 0L, 1L);
        }
    }

    public void onEntityHit(ProjectileHitEvent event) {
    }

    public void onBlockHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof LivingEntity) {
            HomingArrowRunnable runnable = new HomingArrowRunnable((LivingEntity) event.getEntity().getShooter(), this.velocity, this.damage);
            runnable.runTaskTimer(CraftArrows.instance, 0L, 5L);
            event.getEntity().remove();
        }
    }

    private class HomingArrowRunnable extends BukkitRunnable {
        private final double targetVelocity;
        private final Arrow arrow;
        private Entity target;
        private double currentVelocity = 1.0D;
        private double heightDifference = 1.0D;

        public HomingArrowRunnable(LivingEntity entity, double velocity, double damage) {
            this.targetVelocity = velocity;
            if (this.currentVelocity < this.targetVelocity)
                this.currentVelocity = this.targetVelocity;
            this.arrow = entity.launchProjectile(Arrow.class);
            this.arrow.setMetadata("craftarrows.Remove", new FixedMetadataValue(CraftArrows.instance, true));
            this.arrow.setGravity(false);
            ArrowManager.RegisterDamage(this.arrow, damage);
            this.heightDifference = getHeightDifference();
            this.arrow.setVelocity(this.arrow.getVelocity().normalize().multiply(this.currentVelocity));
        }

        public void run() {
            if (this.arrow.isValid()) {
                if (this.currentVelocity < this.targetVelocity) {
                    this.currentVelocity += 0.1D;
                } else if (this.currentVelocity > this.targetVelocity) {
                    this.currentVelocity -= 0.1D;
                }
                if (this.target != null) {
                    if (this.target.isValid()) {
                        this.arrow.setVelocity(VectorHelper.getDirectionVector(this.arrow.getLocation(), this.target.getLocation().add(0.0D, 1.0D, 0.0D), this.currentVelocity));
                    } else {
                        this.arrow.setGravity(true);
                        cancel();
                    }
                } else {
                    this.arrow.setVelocity(this.arrow.getVelocity().normalize().multiply(this.currentVelocity));
                }
            } else {
                cancel();
            }
        }

        private double getHeightDifference() {
            if (this.target instanceof org.bukkit.entity.CaveSpider || this.target instanceof org.bukkit.entity.Spider)
                return 0.5D;
            if (this.target instanceof org.bukkit.entity.Player)
                return 1.5D;
            return 1.0D;
        }

        public void setTarget(Entity entity) {
            this.target = entity;
        }
    }
}
