package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Listener.CraftArrowListener;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class VolleyArrowVariant extends ArrowVariant {
    private float spread;

    private int arrowCount;

    private boolean removeOnLand;

    public VolleyArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Volley", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Volley.ArrowCount", 9));
                this.add(new ConfigValue<>("Recipes.Volley.ArrowSpread", 12));
                this.add(new ConfigValue<>("Recipes.Volley.RemoveOnLand", true));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.arrowCount = Convert.Convert(Integer.class, configValues.get(0).getValue(config)).intValue();
        this.spread = Convert.Convert(Float.class, configValues.get(1).getValue(config)).floatValue();
        this.removeOnLand = ((Boolean) configValues.get(2).getValue(config)).booleanValue();
    }

    public void onShoot(EntityShootBowEvent event) {
        spreadShot(event.getEntity(), event.getProjectile(), event.getForce() * 2.0F);
    }

    public void onEntityHit(ProjectileHitEvent event) {
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }

    private void spreadShot(Entity shooter, Entity projectile, float force) {
        Vector direction = shooter.getLocation().getDirection();
        if (shooter instanceof Player)
            direction = ((Player) shooter).getEyeLocation().getDirection();
        Location location = projectile.getLocation().add(direction.normalize());
        for (int i = 0; i < this.arrowCount - 1; i++) {
            Arrow arrow = shooter.getWorld().spawnArrow(location, projectile.getVelocity(), force, this.spread);
            if (shooter instanceof ProjectileSource)
                arrow.setShooter((ProjectileSource) shooter);
            if (this.removeOnLand)
                CraftArrowListener.removeArrowsOnHit.add(arrow);
        }
    }
}
