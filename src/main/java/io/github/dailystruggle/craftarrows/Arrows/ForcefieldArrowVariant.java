package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Listener.CraftArrowListener;
import io.github.dailystruggle.craftarrows.Util.VectorHelper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ForcefieldArrowVariant extends ArrowVariant {
    public ForcefieldArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Forcefield");
    }

    protected void loadDetails(FileConfiguration config) {
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        Entity hitEntity = event.getHitEntity();
        for (Entity entity : event.getEntity().getNearbyEntities(5.0D, 5.0D, 5.0D)) {
            if (entity != hitEntity && !CraftArrowListener.ignoredEntities.contains(entity))
                entity.setVelocity(VectorHelper.getDirectionVector(hitEntity.getLocation(), entity.getLocation(), 2.0D));
        }
    }

    public void onBlockHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        for (Entity entity : event.getEntity().getNearbyEntities(5.0D, 5.0D, 5.0D)) {
            if (entity != projectile && !CraftArrowListener.ignoredEntities.contains(entity))
                entity.setVelocity(VectorHelper.getDirectionVector(projectile.getLocation(), entity.getLocation(), 2.0D));
        }
    }
}
