package org.reveno.article;

public class Utils {
    private static final long DECIMAL_POWER = (long) Math.pow(10, 6);

    public static long toLong(double value) {
        return (long)(value * DECIMAL_POWER);
    }

    public static double fromLong(long value) {
        return (double)value / DECIMAL_POWER;
    }

    public static boolean eq(double a, double b) {
        final double epsilon = 1 / DECIMAL_POWER;
        final double absA = Math.abs(a);
        final double absB = Math.abs(b);
        final double diff = Math.abs(a - b);

        if (a == b) {
            return true;
        } else if (a == 0 || b == 0 || diff < Double.MIN_NORMAL) {
            return diff < (epsilon * Double.MIN_NORMAL);
        } else {
            return diff / (absA + absB) < epsilon;
        }
    }

}
