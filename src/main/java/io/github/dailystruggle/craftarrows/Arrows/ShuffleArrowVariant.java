package io.github.dailystruggle.craftarrows.Arrows;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ShuffleArrowVariant extends ArrowVariant {
    public ShuffleArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Shuffle");
    }

    protected void loadDetails(FileConfiguration config) {
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Entity && event.getHitEntity() instanceof Entity) {
            Entity shooter = (Entity) event.getEntity().getShooter();
            Entity target = event.getHitEntity();
            Location a = shooter.getLocation();
            Location b = target.getLocation();
            shooter.teleport(b);
            target.teleport(a);
        }
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }
}
