package org.team498.C2024;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;

public final class Constants {
    public static final double CAN_TIMEOUT_SECONDS = 0.001;

    public static final class OIConstants {
        public static final int DRIVER_CONTROLLER_ID = 0;
        public static final int OPERATOR_CONTROLLER_ID = 1;
    }

    public static final class DrivetrainConstants {
        public static final double MAX_VELOCITY_METERS_PER_SECOND = 5.94;
        public static final double MAX_ACCELERATION_METERS_PER_SECOND_SQUARED = 20; //TODO: test for best traction/speed

        public static final double MAX_AUTO_VELOCITY = 3;
        public static final double MAX_AUTO_ACCELERATION = 3;

        public static final float SLOW_SPEED_SCALAR = 0.5f;
        public static final float TARGET_SPEED_SCALAR = 0.8f;
        public static final float AUTO_SPEED_SCALAR = 0.3f;
        public static final short FULL_SPEED_SCALAR = 1;

        public static final double SWERVE_MODULE_DISTANCE_FROM_CENTER = 10.75;

        public static final double MK4I_DRIVE_REDUCTION_L3 = 5.35;
        public static final double MK4I_STEER_REDUCTION_L3 = 21.428571428571428571428571428571; // 150 / 7

        public static final double DRIVE_WHEEL_DIAMETER = 4;
        public static final double DRIVE_WHEEL_CIRCUMFERENCE = DRIVE_WHEEL_DIAMETER * Math.PI;

        public static final double FL_MODULE_OFFSET = -0.984863;
        public static final double FR_MODULE_OFFSET = -0.707031;
        public static final double BL_MODULE_OFFSET = -0.716553 + 0.5;
        public static final double BR_MODULE_OFFSET = -0.960449 + 0.5;

        public static final double ROBOT_WIDTH = 26.5 + 6; // Robot width with bumpers, in inches

        public static final class AngleConstants {
            public static final double P = 5;
            public static final double I = 0;
            public static final double D = 0;
            public static final double EPSILON = 1.5;

            // Constraints for the profiled angle controller
            public static final double MAX_ANGULAR_SPEED_DEGREES_PER_SECOND = 720;
            public static final double MAX_ANGULAR_SPEED_DEGREES_PER_SECOND_SQUARED = Math.pow(MAX_ANGULAR_SPEED_DEGREES_PER_SECOND, 2);

            public static final TrapezoidProfile.Constraints CONTROLLER_CONSTRAINTS = new TrapezoidProfile.Constraints(
                    MAX_ANGULAR_SPEED_DEGREES_PER_SECOND,
                    MAX_ANGULAR_SPEED_DEGREES_PER_SECOND_SQUARED
            );

        }

        public static final class PoseConstants {
            public static final double P = 2.5;
            public static final double I = 0;
            public static final double D = 0;

            public static final double EPSILON = 0.05;
        }
    }

    public static final class ShooterConstants {
        // fP, fI, fD are the feedMotor Constants for the shooter
        public static final double fP = 0.00028;
        public static final double fI = 0;
        public static final double fD = 0;
        public static final double fV = 0.00025;
        // P, I, D are the flywheel Constants for the shooter
        public static final double P = 0.0003; // 0.001 //3.273
        public static final double I = 0;
        public static final double D = 0;
        public static final double S = 0;
        public static final double V = 0.00013;   //0.0005
        public static final double A = 0;

        public static final double GEAR_RATIO = 36.0 / 18.0;
        public static final double CIRCUMFERENCE = Units.inchesToMeters(3) * Math.PI;

        
        public static final double MAX_RPM = 6000 * GEAR_RATIO;
        public static final double MAX_MPS = MAX_RPM * CIRCUMFERENCE;
        public static final double SPIN_DIFF = 1000;

        public static final class AngleConstants{
            public static final double P = 0.05;
            public static final double I = 0;
            public static final double D = 0.0;
            public static final double S = 0;
            public static final double V = 0;
            public static final double G = 0; //0.03
            public static final double MAX_ANGLE = 73;
            public static final double MIN_ANGLE = 32;
            public static final double AUTO_MIN_ANGLE = 29;

            public static final double ANGLE_OFFSET = 0.18900256; //0.07161433; //0.08792
        }
    }

    public static final class IntakeConstants {
        public static final double P = 0.14;
        public static final double I = 0;
        public static final double D = 0; //0.15
        public static final double S = 0;
        public static final double V = 0;
        public static final double G = 0;
        public static final double dV = 0;//0.2 / 1800.0; // 20% power per 6 m/s
        public static final double rV = 0;//0.3 / (3 * Math.PI); // 50% power per 3pi r/s (120 deg/s)

        public static final double ENCODER_OFFSET = 6.21;
    }

    public static final class HopperConstants {
        public static final double P = 0.25;
        public static final double I = 0;
        public static final double D = 0;
    }

    public static final class ClimberConstants {

            public static final double MAX_RPM = 0;
            
        }

}
