package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

public class WitherArrowVariant extends ArrowVariant {
    private boolean breakBlocks = false;

    public WitherArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Wither", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Wither.BreakBlocks", false));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.breakBlocks = ((Boolean) configValues.get(0).getValue(config)).booleanValue();
    }

    public void onShoot(EntityShootBowEvent event) {
        Projectile projectile = event.getEntity().launchProjectile(WitherSkull.class);
        projectile.setMetadata("BreakBlocks", new FixedMetadataValue(CraftArrows.instance, Boolean.valueOf(this.breakBlocks)));
        event.setProjectile(projectile);
    }

    public void onEntityHit(ProjectileHitEvent event) {
    }

    public void onBlockHit(ProjectileHitEvent event) {
    }
}
