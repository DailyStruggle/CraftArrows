package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class TorpedoArrowVariant extends ArrowVariant {
    private boolean AirGravity;

    private boolean WaterGravity;

    private double MinSpeed;

    private double MaxSpeed;

    private double AddedAirGravity;

    public TorpedoArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Torpedo", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Torpedo.AirGravity", true));
                this.add(new ConfigValue<>("Recipes.Torpedo.WaterGravity", false));
                this.add(new ConfigValue<>("Recipes.Torpedo.MinSpeed", 0.2));
                this.add(new ConfigValue<>("Recipes.Torpedo.MaxSpeed", 1.0));
                this.add(new ConfigValue<>("Recipes.Torpedo.AddedAirGravity", 0.1));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.AirGravity = ((Boolean) configValues.get(0).getValue(config)).booleanValue();
        this.WaterGravity = ((Boolean) configValues.get(1).getValue(config)).booleanValue();
        this.MinSpeed = Convert.Convert(Double.class, configValues.get(2).getValue(config)).doubleValue();
        this.MaxSpeed = Convert.Convert(Double.class, configValues.get(3).getValue(config)).doubleValue();
        this.AddedAirGravity = Convert.Convert(Double.class, configValues.get(4).getValue(config)).doubleValue();
    }

    public void onShoot(EntityShootBowEvent event) {
        new TorpedoTask((Arrow) event.getProjectile(), this);
    }

    public void onEntityHit(ProjectileHitEvent event) {
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }

    private class TorpedoTask extends BukkitRunnable {
        private final Arrow arrow;
        private final TorpedoArrowVariant variant;
        private final Vector extraGravity;
        private double speed;

        private TorpedoTask(Arrow arrow, TorpedoArrowVariant variant) {
            this.arrow = arrow;
            this.speed = arrow.getVelocity().length();
            this.variant = variant;
            this.extraGravity = new Vector(0.0D, -variant.AddedAirGravity, 0.0D);
            runTaskTimer(CraftArrows.instance, 0L, 1L);
        }

        public void run() {
            Material blockType = this.arrow.getLocation().getBlock().getType();
            if (!this.arrow.isValid() || this.arrow.isDead()) {
                cancel();
                return;
            }
            if (blockType == Material.WATER) {
                this.arrow.setGravity(this.variant.WaterGravity);
            } else if (blockType == Material.AIR || blockType == Material.CAVE_AIR || blockType == Material.VOID_AIR) {
                this.arrow.setGravity(this.variant.AirGravity);
                this.speed = 0.0D;
            } else {
                cancel();
            }
            if (!this.arrow.hasGravity()) {
                if (this.speed <= this.variant.MinSpeed) {
                    this.speed = this.variant.MinSpeed;
                } else if (this.speed <= this.variant.MaxSpeed) {
                    this.speed += (this.variant.MaxSpeed - this.variant.MinSpeed) / 10.0D;
                } else {
                    this.speed = this.variant.MaxSpeed;
                }
                Location newLocation = this.arrow.getLocation();
                Vector direction = this.arrow.getLocation().getDirection().normalize().multiply(this.speed);
                direction.setX(-direction.getX());
                direction.setY(-direction.getY());
                this.arrow.setVelocity(direction);
                newLocation.getWorld().spawnParticle(Particle.WATER_BUBBLE, newLocation, 5, 0.25D, 0.25D, 0.25D, 0.0D);
            } else {
                this.arrow.setVelocity(this.arrow.getVelocity().add(this.extraGravity));
            }
        }

        public void cancel() {
            super.cancel();
            this.arrow.remove();
        }
    }
}
