package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EntityEquipment;

import java.util.ArrayList;

public class RazorArrowVariant extends ArrowVariant {
    private double extraDamage;

    public RazorArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Razor", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Razor.BonusDamage", 4.0));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.extraDamage = Convert.Convert(Double.class, configValues.get(0).getValue(config)).doubleValue();
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        if (event.getHitEntity() instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) event.getHitEntity();
            EntityEquipment equipment = living.getEquipment();
            if (equipment == null || ((equipment
                    .getHelmet() == null || equipment.getHelmet().getType() == Material.AIR) && (equipment
                    .getChestplate() == null || equipment.getChestplate().getType() == Material.AIR) && (equipment
                    .getLeggings() == null || equipment.getLeggings().getType() == Material.AIR) && (equipment
                    .getBoots() == null || equipment.getBoots().getType() == Material.AIR))) {
                living.setHealth(Math.max(0.0D, living.getHealth() - this.extraDamage));
                living.setLastDamageCause(new EntityDamageEvent(living, EntityDamageEvent.DamageCause.ENTITY_ATTACK, this.extraDamage));
            }
        }
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }
}
