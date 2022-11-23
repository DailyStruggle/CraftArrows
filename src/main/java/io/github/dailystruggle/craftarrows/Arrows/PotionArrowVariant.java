package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;

public class PotionArrowVariant extends ArrowVariant {
    protected int potionLevel;
    protected int duration;
    protected PotionEffectType type;
    protected PotionEffect potionEffect;

    public PotionArrowVariant(FileConfiguration config, final String name, PotionEffectType type) {
        super(config, "Recipes.", name, new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes." + name + ".Power", 0));
                this.add(new ConfigValue<>("Recipes." + name + ".Duration", 60));
            }
        });
        this.type = type;
        this.potionEffect = new PotionEffect(type, this.duration, this.potionLevel);
    }

    public PotionArrowVariant(FileConfiguration config, final String name, PotionEffectType type, final ConfigValue<?>... configValues) {
        super(config, "Recipes.", name, new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes." + name + ".Power", 0));
                this.add(new ConfigValue<>("Recipes." + name + ".Duration", 60));

                Collections.addAll(this, configValues);
            }
        });
        this.type = type;
        this.potionEffect = new PotionEffect(type, this.duration, this.potionLevel);
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = this.getConfigValues();
        this.potionLevel = Convert.Convert(Integer.class, configValues.get(0).getValue(config));
        this.duration = Convert.Convert(Integer.class, (configValues.get(1)).getValue(config));
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        if (event.getHitEntity() instanceof LivingEntity) {
            ((LivingEntity)event.getHitEntity()).addPotionEffect(new PotionEffect(this.type, this.duration, this.potionLevel));
        }
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }
}
