package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import io.github.dailystruggle.craftarrows.Util.VectorHelper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class PullArrowVariant extends ArrowVariant {
    private final HashMap<Entity, Entity> entitiesWhoShot = new HashMap<>();

    private double pullAmount;

    public PullArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Pull", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Pull.Power", 2.0));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.pullAmount = Convert.Convert(Double.class, configValues.get(0).getValue(config)).doubleValue();
    }

    public void onShoot(EntityShootBowEvent event) {
        this.entitiesWhoShot.put(event.getProjectile(), event.getEntity());
    }

    public void onEntityHit(ProjectileHitEvent event) {
        Entity shooter = this.entitiesWhoShot.get(event.getEntity());
        if (shooter != null) {
            Entity shot = event.getHitEntity();
            shot.setVelocity(VectorHelper.getDirectionVector(shot.getLocation(), shooter.getLocation(), this.pullAmount));
        }
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }
}
