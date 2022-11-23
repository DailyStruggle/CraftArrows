package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;

public class LightningArrowVariant extends ArrowVariant {
    private int count;

    public LightningArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Lightning", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Lightning.Count", 2));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.count = Convert.Convert(Integer.class, configValues.get(0).getValue(config)).intValue();
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        strikeLightning(event.getHitEntity().getLocation());
    }

    public void onBlockHit(ProjectileHitEvent event) {
        strikeLightning(event.getHitBlock().getLocation());
    }

    private void strikeLightning(Location location) {
        for (int i = 0; i < this.count; i++)
            Bukkit.getScheduler().runTaskLater(CraftArrows.instance, () -> location.getWorld().strikeLightning(location), (4 * i));
    }
}
