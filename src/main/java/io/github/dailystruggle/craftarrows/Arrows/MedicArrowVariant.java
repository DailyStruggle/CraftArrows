package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;

public class MedicArrowVariant extends CombatArrowVariant {
    private double healPower;

    public MedicArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Medic", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Medic.HealPower", 8.0));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.healPower = Convert.Convert(Double.class, configValues.get(0).getValue(config)).doubleValue();
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(EntityDamageByEntityEvent event, Projectile projectile, LivingEntity entity) {
        entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + this.healPower));
        event.setCancelled(true);
    }

    public void onEntityHit(ProjectileHitEvent event) {
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }
}
