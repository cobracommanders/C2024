package org.team498.C2024;

import org.team498.lib.util.LinearInterpolator;

public class ShooterUtil {
    private static double[][] data = new double[][] {
        {3.8, 28},
        {3.5, 30},
        {3.4, 3.5},
        {3.3, 31.5},
        {3, 33},
        {2.7, 34},
        {2.6, 37},
        {2.5, 38},
        {2.4, 39},
        {2.2, 41},
        {2.1, 42},
        {2.0, 44},
        {1.7, 46},
        {1.3, 55},
    };
    private static double[][] speedData = new double[][] {
        {0, 3500},
        {3.7, 3500},
        {4.0, 2000}
    };
    private static LinearInterpolator interpolator = new LinearInterpolator(data);
    private static LinearInterpolator speedInterpolator = new LinearInterpolator(speedData);

    public static double getShooterAngle(double distance) {
        return interpolator.getInterpolatedValue(distance);
    }

    public static double getShooterSpeed(double distance) {
        return speedInterpolator.getInterpolatedValue(distance);
    }
}
