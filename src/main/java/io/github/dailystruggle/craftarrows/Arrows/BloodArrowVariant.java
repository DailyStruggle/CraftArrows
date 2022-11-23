package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;

public class BloodArrowVariant extends ArrowVariant {
    private double healPower;

    public BloodArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Blood", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Blood.HealPower", 4.0));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.healPower = Convert.Convert(Double.class, configValues.get(0).getValue(config));
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity().getShooter();
            entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + this.healPower));
        }
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }
}
