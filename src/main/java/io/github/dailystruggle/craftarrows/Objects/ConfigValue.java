package io.github.dailystruggle.craftarrows.Objects;

import io.github.dailystruggle.craftarrows.Util.ConfigHelper;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigValue<T> {
    private final T value;
    private String path;

    public ConfigValue(String path, T defaultValue) {
        this.path = path;
        this.value = defaultValue;
    }

    public T getValue(FileConfiguration config) {
        try {
            T value = (T) config.get(this.path);
            return value;
        } catch (Exception exception) {
            return this.value;
        }
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDefault(FileConfiguration config) {
        if (!ConfigHelper.ConfigContainsPath(config, this.path))
            config.set(this.path, this.value);
    }
}
