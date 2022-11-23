package io.github.dailystruggle.craftarrows.Data;

import io.github.dailystruggle.craftarrows.Arrows.ArrowVariant;
import io.github.dailystruggle.craftarrows.CraftArrows;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;

public class ArrowManager {
    private static final HashMap<String, ArrowVariant> entityArrowVariants = new HashMap<>();

    private static final HashMap<String, Double> entityDamage = new HashMap<>();

    public static void RegisterArrow(Entity entity, ArrowVariant variant) {
        entity.setMetadata(".craftarrows.arrowvariant", new FixedMetadataValue(CraftArrows.instance, true));
        entityArrowVariants.put(entity.getUniqueId().toString(), variant);
    }

    public static ArrowVariant GetVariant(Entity entity) {
        if (entity.hasMetadata(".craftarrows.arrowvariant"))
            return entityArrowVariants.getOrDefault(entity.getUniqueId().toString(), null);
        return null;
    }

    public static void RegisterDamage(Entity entity, double damage) {
        entity.setMetadata("craftarrows.damage", new FixedMetadataValue(CraftArrows.instance, true));
        entityDamage.put(entity.getUniqueId().toString(), damage);
    }

    public static Double GetDamage(Entity entity) {
        if (entity.hasMetadata("craftarrows.damage"))
            return entityDamage.getOrDefault(entity.getUniqueId().toString(), null);
        return null;
    }

    public static void RemoveArrow(Entity entity) {
        entityArrowVariants.remove(entity.getUniqueId().toString());
        entityDamage.remove(entity.getUniqueId().toString());
        entity.removeMetadata("craftarrows.damage", CraftArrows.instance);
        entity.removeMetadata(".craftarrows.arrowvariant", CraftArrows.instance);
    }
}
