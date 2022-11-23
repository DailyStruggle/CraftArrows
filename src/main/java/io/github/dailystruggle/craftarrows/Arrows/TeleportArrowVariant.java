package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class TeleportArrowVariant extends ArrowVariant {
    private final HashMap<Entity, Entity> shooters = new HashMap<>();

    private Double damageTaken = Double.valueOf(0.0D);

    public TeleportArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Teleport", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Teleport.DamageTaken", 1.0));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.damageTaken = Double.valueOf(((Double) configValues.get(0).getValue(config)).doubleValue());
    }

    public void onShoot(EntityShootBowEvent event) {
        this.shooters.put(event.getProjectile(), event.getEntity());
    }

    public void onEntityHit(ProjectileHitEvent event) {
        if (this.shooters.containsKey(event.getEntity()))
            teleport(event.getEntity(), event.getHitEntity().getLocation());
        this.shooters.get(event.getEntity()).teleport(event.getHitEntity());
    }

    public void onBlockHit(ProjectileHitEvent event) {
        if (this.shooters.containsKey(event.getEntity())) {
            Entity shooter = this.shooters.get(event.getEntity());
            teleport(shooter, event.getHitBlock().getLocation().add(0.5D, 1.0D, 0.5D), shooter.getLocation().getYaw(), shooter.getLocation().getPitch());
        }
    }

    private void teleport(Entity entity, Location location) {
        entity.teleport(location);
        if (entity instanceof LivingEntity && this.damageTaken.doubleValue() > 0.0D)
            ((LivingEntity) entity).damage(this.damageTaken.doubleValue());
    }

    private void teleport(Entity entity, Location location, float yaw, float pitch) {
        location.setYaw(yaw);
        location.setPitch(pitch);
        teleport(entity, location);
    }
}
