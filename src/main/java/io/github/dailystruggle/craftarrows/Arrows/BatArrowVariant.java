package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class BatArrowVariant extends ArrowVariant {
    private double Speed;

    private double Damage;

    public BatArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Bat", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Bat.Speed", 1.0));
                this.add(new ConfigValue<>("Recipes.Bat.Damage", 6.0));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = this.getConfigValues();
        this.Speed = Convert.ConvertToDouble(configValues.get(0).getValue(config));
        this.Damage = Convert.ConvertToDouble(configValues.get(1).getValue(config));
    }

    public void onShoot(EntityShootBowEvent event) {
        Bukkit.getScheduler().runTaskLater(CraftArrows.instance, () -> new BatTask((Arrow) event.getProjectile(), this.Speed, this.Damage), 1L);
    }

    public void onEntityHit(ProjectileHitEvent event) {
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }

    private class BatTask extends BukkitRunnable {
        Entity shooter;

        Vector direction;

        Bat bat;

        double speed;

        double damage;

        private BatTask(Arrow arrow, double speed, double damage) {
            this.speed = speed;
            this.damage = damage;
            this.shooter = (Entity) arrow.getShooter();
            this.direction = this.shooter.getLocation().getDirection().normalize().multiply(speed);
            Location spawn = arrow.getLocation();
            spawn.setYaw(this.shooter.getLocation().getYaw());
            spawn.setPitch(this.shooter.getLocation().getPitch());
            this.bat = arrow.getWorld().spawn(spawn, Bat.class);
            this.bat.setAwake(true);
            this.bat.setAI(false);
            this.bat.setInvulnerable(true);
            this.bat.setGravity(false);
            this.bat.setGliding(true);
            runTaskTimer(CraftArrows.instance, 0L, 1L);
            arrow.remove();
        }

        public void run() {
            this.bat.teleport(this.bat.getLocation().add(this.direction));
            LivingEntity closest = null;
            double distance = 1000.0D;
            for (Entity entity : this.bat.getNearbyEntities(0.5D, 0.5D, 0.5D)) {
                if (entity != this.shooter && entity instanceof LivingEntity) {
                    double entityDistance = entity.getLocation().distanceSquared(this.bat.getLocation());
                    if (entityDistance < distance) {
                        closest = (LivingEntity) entity;
                        distance = entityDistance;
                    }
                }
            }
            if (closest != null) {
                cancel();
                closest.damage(this.damage);
            } else if ((this.bat.getLocation().getBlock().getType() != Material.AIR && this.bat
                    .getLocation().getBlock().getType() != Material.CAVE_AIR) || !this.bat.isValid()) {
                cancel();
            }
        }

        public void cancel() {
            super.cancel();
            this.bat.remove();
        }
    }
}
