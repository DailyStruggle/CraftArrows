package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Random;

public class TrickOrTreatArrow extends ArrowVariant {
    private final boolean causesFoodPoisoning = true;
    private final Random random = new Random();
    private double trickChance = 30.0D;
    private double trickDamage = 6.0D;
    private int trickHunger = -6;
    private double treatHealth = 4.0D;
    private int treatHunger = 4;

    public TrickOrTreatArrow(FileConfiguration config) {
        super(config, "Recipes.", "TrickOrTreat", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.TrickOrTreat.Trick.Chance", 30.0));
                this.add(new ConfigValue<>("Recipes.TrickOrTreat.Trick.Damage", 6.0));
                this.add(new ConfigValue<>("Recipes.TrickOrTreat.Trick.Hunger", -6));
                this.add(new ConfigValue<>("Recipes.TrickOrTreat.Treat.Health", 4.0));
                this.add(new ConfigValue<>("Recipes.TrickOrTreat.Treat.Hunger", 4));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.trickChance = Convert.Convert(Double.class, configValues.get(0).getValue(config)).doubleValue();
        this.trickDamage = Convert.Convert(Double.class, configValues.get(1).getValue(config)).doubleValue();
        this.trickHunger = Convert.Convert(Integer.class, configValues.get(2).getValue(config)).intValue();
        this.treatHealth = Convert.Convert(Double.class, configValues.get(3).getValue(config)).doubleValue();
        this.treatHunger = Convert.Convert(Integer.class, configValues.get(4).getValue(config)).intValue();
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        Entity entity = event.getHitEntity();
        if (entity instanceof LivingEntity)
            if (this.random.nextDouble() * 100.0D < this.trickChance) {
                ((LivingEntity) entity).damage(this.trickDamage);
                if (entity instanceof Player) {
                    if (this.causesFoodPoisoning)
                        ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 1, 100));
                    ((Player) entity).setFoodLevel(Math.max(0, ((Player) entity).getFoodLevel() + this.trickHunger));
                }
            } else {
                ((LivingEntity) entity).setHealth(Math.max(((LivingEntity) entity).getMaxHealth(), ((LivingEntity) entity).getHealth() + this.treatHealth));
                if (entity instanceof Player)
                    ((Player) entity).setFoodLevel(Math.max(0, ((Player) entity).getFoodLevel() + this.treatHunger));
            }
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }
}
