package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Listener.CraftArrowListener;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class ExplosiveArrowVariant extends ArrowVariant {
    private float power;

    private boolean breakBlocks;

    private boolean setFire = false;

    public ExplosiveArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Explosive", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Explosive.Power", 4.0F));
                this.add(new ConfigValue<>("Recipes.Explosive.BreakBlocks", true));
                this.add(new ConfigValue<>("Recipes.Explosive.SetFire", false));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.power = Convert.Convert(Float.class, configValues.get(0).getValue(config)).floatValue();
        this.breakBlocks = ((Boolean) configValues.get(1).getValue(config)).booleanValue();
        this.setFire = ((Boolean) configValues.get(2).getValue(config)).booleanValue();
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        Entity entity = event.getHitEntity();
        if (entity instanceof LivingEntity) {
            Bukkit.getScheduler().runTaskLater((Plugin) CraftArrows.instance, () -> {
                ((LivingEntity) entity).setNoDamageTicks(15);
                explode(event.getEntity().getLocation());
            }, 1L);
        } else {
            explode(event.getEntity().getLocation());
        }
    }

    public void onBlockHit(ProjectileHitEvent event) {
        explode(event.getEntity().getLocation());
    }

    private void explode(Location location) {
        CraftArrowListener.ignoredExplosions.add(location);
        TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
        tnt.setFuseTicks(1);
        Bukkit.getScheduler().runTaskLater(CraftArrows.instance, () -> CraftArrowListener.ignoredExplosions.remove(location), 3L);
    }
}
