package org.team498.C2024;

import org.team498.lib.util.LinearInterpolator;

public class ShooterUtil {
    private static double[][] data = new double[][] {
        {4.0, 29},
        {3.3, 32},
        {3, 34},
        {2.8, 35},
        {2.5, 38},
        {2.4, 39},
        {2.3, 40.5},
        {2.2, 41},
        {2.1, 43},
        {2.0, 44},
        {1.7, 46},
        {1.5, 52},
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
