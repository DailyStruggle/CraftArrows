package io.github.dailystruggle.craftarrows.Util;

public class Convert {
    public static <T> T Convert(Class<T> classType, Object value) {
        if (classType == Integer.class)
            return (T) ConvertToInteger(value);
        if (classType == Double.class)
            return (T) ConvertToDouble(value);
        if (classType == Float.class)
            return (T) ConvertToFloat(value);
        return null;
    }

    public static Double ConvertToDouble(Object value) {
        if (value instanceof Integer)
            return (double) (Integer) value;
        if (value instanceof Double)
            return (Double) value;
        if (value instanceof Float)
            return (double) (Float) value;
        if (TryParse.parseDouble(value.toString()))
            return Double.parseDouble(value.toString());
        return 0.0D;
    }

    public static Float ConvertToFloat(Object value) {
        if (value instanceof Integer)
            return (float) (Integer) value;
        if (value instanceof Double)
            return (float) ((Double) value).doubleValue();
        if (value instanceof Float)
            return (Float) value;
        if (TryParse.parseFloat(value.toString()))
            return Float.parseFloat(value.toString());
        return 0.0F;
    }

    public static Integer ConvertToInteger(Object value) {
        if (value instanceof Integer)
            return (Integer) value;
        if (value instanceof Double)
            return (int) ((Double) value).doubleValue();
        if (value instanceof Float)
            return (int) ((Float) value).floatValue();
        if (TryParse.parseInt(value.toString()))
            return Integer.parseInt(value.toString());
        return 0;
    }
}
