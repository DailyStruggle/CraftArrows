package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Objects.ArrowRecipe;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class ArrowVariant {
    private final String name;
    private final ArrowRecipe recipe;
    private final ArrayList<ConfigValue<?>> configValues = new ArrayList<>();
    private String id;

    public ArrowVariant(FileConfiguration config, String path, String name) {
        this.name = name;
        this.recipe = new ArrowRecipe(config, path, name);
        loadDetails(config);
    }

    public ArrowVariant(FileConfiguration config, String path, String name, List<ConfigValue<?>> configValues) {
        setDefaults(config, configValues);
        this.name = name;
        this.recipe = new ArrowRecipe(config, path, name);
        this.configValues.addAll(configValues);
        loadDetails(config);
    }

    private void setDefaults(FileConfiguration config, List<ConfigValue<?>> configValues) {
        for (ConfigValue<?> configValue : configValues)
            configValue.setDefault(config);
    }

    public String getName() {
        return this.name;
    }

    public ArrowRecipe getRecipe() {
        return this.recipe;
    }

    public abstract void onShoot(EntityShootBowEvent paramEntityShootBowEvent);

    public abstract void onEntityHit(ProjectileHitEvent paramProjectileHitEvent);

    public abstract void onBlockHit(ProjectileHitEvent paramProjectileHitEvent);

    protected abstract void loadDetails(FileConfiguration paramFileConfiguration);

    protected ArrayList<ConfigValue<?>> getConfigValues() {
        return this.configValues;
    }
}
