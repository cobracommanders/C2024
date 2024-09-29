package org.team498.C2024;

import org.team498.lib.util.LinearInterpolator;

public class ShooterUtil {
    private static double[][] data = new double[][] {
        {4.106, 28},
        {4.0, 28},
        {3.8, 26.5},
        {3.5, 30},
        {3.4, 30},
        {3.3, 31.5},
        {3.2, 31.5},
        {3.1, 32},
        {3, 33},
        {2.9, 33.2},
        {2.8, 34},
        {2.7, 35.5},
        {2.6, 37},
        {2.5, 38},
        {2.4, 39},
        {2.3, 40},
        {2.2, 41},
        {2.1, 42},
        {2.0, 44},
        {1.7, 46},
        {1.3, 55},
    };
    private static double[][] speedData = new double[][] {
        {0, 3500},
        {3.7, 3800},
        {4.0, 4500}
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
