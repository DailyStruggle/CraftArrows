package io.github.dailystruggle.craftarrows.Objects;

public class Setting<T> implements ISetting<T> {
    private final String name;
    private final String identifier;
    private T value;

    public Setting(String name, String identifier, T value) {
        this.name = name;
        this.identifier = identifier;
        this.value = value;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getName() {
        return this.name;
    }

    public T getValue() {
        return this.value;
    }

    public T setValue(T newValue) {
        T oldValue = this.value;
        this.value = newValue;
        return oldValue;
    }
}
