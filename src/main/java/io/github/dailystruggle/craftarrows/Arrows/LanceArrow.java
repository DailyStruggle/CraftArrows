package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class LanceArrow extends CombatArrowVariant {
    private int pierceDistance = 5;

    private double targetDamage = 6.0D;

    private double pierceDamage = 3.0D;

    public LanceArrow(FileConfiguration config) {
        super(config, "Recipes.", "Lance", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Lance.PierceDistance", 5));
                this.add(new ConfigValue<>("Recipes.Lance.TargetDamage", 6.0));
                this.add(new ConfigValue<>("Recipes.Lance.PierceDamage", 3.0));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.pierceDistance = Convert.Convert(Integer.class, configValues.get(0).getValue(config));
        this.targetDamage = Convert.Convert(Double.class, configValues.get(1).getValue(config));
        this.pierceDamage = Convert.Convert(Double.class, configValues.get(2).getValue(config));
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Entity hit = event.getHitEntity();
        Vector direction = projectile.getLocation().getDirection().normalize().multiply(new Vector(-1, -1, 1));
        ArrayList<LivingEntity> entitiesHit = new ArrayList<>();
        Location start = projectile.getLocation();
        Location end = projectile.getLocation().add(direction.multiply(this.pierceDistance));
        while (start.distanceSquared(end) > 1.0D) {
            start = start.add(direction.normalize());
            projectile.teleport(start);
            for (Entity entity : projectile.getNearbyEntities(0.5D, 0.5D, 0.5D)) {
                if (entity instanceof LivingEntity && entity != hit && !entitiesHit.contains(entity))
                    entitiesHit.add((LivingEntity) entity);
            }
        }
        if (hit instanceof LivingEntity)
            ((LivingEntity) hit).damage(this.targetDamage);
        for (LivingEntity livingEntity : entitiesHit)
            livingEntity.damage(this.pierceDamage);
        projectile.remove();
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }

    public void onEntityHit(EntityDamageByEntityEvent event, Projectile projectile, LivingEntity living) {
        event.setCancelled(true);
    }
}
