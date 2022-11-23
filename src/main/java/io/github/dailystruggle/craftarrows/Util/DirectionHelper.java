package io.github.dailystruggle.craftarrows.Util;

import org.bukkit.entity.LivingEntity;

public class DirectionHelper {
    public static Direction getCardinalDirection(LivingEntity entity) {
        double rot = ((entity.getLocation().getYaw() - 90.0F) % 360.0F);
        if (rot < 0.0D)
            rot += 360.0D;
        return getDirection(rot);
    }

    private static Direction getDirection(double rot) {
        if (0.0D <= rot && rot < 45.0D)
            return Direction.WEST;
        if (45.0D <= rot && rot < 135.0D)
            return Direction.NORTH;
        if (135.0D <= rot && rot < 225.0D)
            return Direction.EAST;
        if (225.0D <= rot && rot < 315.0D)
            return Direction.SOUTH;
        if (315.0D <= rot && rot < 360.0D)
            return Direction.WEST;
        return null;
    }

    public enum Direction {
        NORTH, EAST, SOUTH, WEST
    }
}
