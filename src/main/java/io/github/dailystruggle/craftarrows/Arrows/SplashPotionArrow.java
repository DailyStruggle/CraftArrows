package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class SplashPotionArrow extends PotionArrowVariant {
    private double radius;

    private int color;

    public SplashPotionArrow(FileConfiguration config, String name, PotionEffectType type) {
        super(config, name, type, new ConfigValue("Recipes." + name + ".Radius",
                Integer.valueOf(2)), new ConfigValue("Recipes." + name + ".Color",
                Integer.valueOf(2)));
    }

    protected void loadDetails(FileConfiguration config) {
        super.loadDetails(config);
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.radius = Convert.Convert(Integer.class, configValues.get(2).getValue(config)).intValue();
        this.color = Convert.Convert(Integer.class, configValues.get(3).getValue(config)).intValue();
    }

    public void onEntityHit(ProjectileHitEvent event) {
        splash(event.getHitEntity().getLocation(), event.getEntity());
        if (event.getHitEntity() instanceof LivingEntity)
            ((LivingEntity) event.getHitEntity()).addPotionEffect(new PotionEffect(this.type, this.duration, this.potionLevel));
    }

    public void onBlockHit(ProjectileHitEvent event) {
        splash(event.getHitBlock().getLocation().add(0.0D, 1.0D, 0.0D), event.getEntity());
    }

    private void splash(Location location, Entity entity) {
        location.getWorld().playSound(location, Sound.ENTITY_SPLASH_POTION_BREAK, 1.0F, 1.0F);
        location.getWorld().playEffect(location, Effect.POTION_BREAK, this.color);
        for (Entity ent : entity.getNearbyEntities(this.radius, this.radius, this.radius)) {
            if (ent instanceof LivingEntity)
                ((LivingEntity) ent).addPotionEffect(this.potionEffect);
        }
    }
}
