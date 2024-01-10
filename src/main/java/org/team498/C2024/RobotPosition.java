package org.team498.C2024;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import org.team498.C2024.Constants.DrivetrainConstants;
import org.team498.C2024.subsystems.Drivetrain;
import org.team498.lib.field.BaseRegion;
import org.team498.lib.field.Point;
import org.team498.lib.util.RotationUtil;
import org.team498.lib.wpilib.ChassisSpeeds;

public class RobotPosition {
    private static final Drivetrain drivetrain = Drivetrain.getInstance();
    public static final double scoringOffset = Units.inchesToMeters((DrivetrainConstants.ROBOT_WIDTH / 2) + 10);

    public static boolean inRegion(BaseRegion region) {
        return region.contains(Point.fromPose2d(drivetrain.getPose()));
    }

    public static boolean isNear(Pose2d pose, double epsilon) {
        return Math.hypot(drivetrain.getPose().getX() - pose.getX(), drivetrain.getPose().getY() - pose.getY()) < epsilon;
    }

    private static double distanceTo(Point point, Pose2d reference) {
        double x = point.getX();
        double y = point.getY();

        double xDiff = x - reference.getX();
        double yDiff = y - reference.getY();

        return Math.hypot(xDiff, yDiff);
    }

    private static double distanceTo(Point point) {return distanceTo(point, drivetrain.getPose());}

    public static double calculateDegreesToTarget(Pose2d target) {
        Pose2d currentPose = drivetrain.getPose();
        ChassisSpeeds currentSpeeds = drivetrain.getCurrentSpeeds();

        // Estimate the future pose of the robot to compensate for lag
        double newX = currentPose.getX() + (currentSpeeds.vxMetersPerSecond * (Robot.DEFAULT_PERIOD * (Robot.isReal() ? 10 : 0)));
        double newY = currentPose.getY() + (currentSpeeds.vyMetersPerSecond * (Robot.DEFAULT_PERIOD * (Robot.isReal() ? 10 : 0)));

        Pose2d futurePose = new Pose2d(newX, newY, new Rotation2d());

        // Calculate the angle between the target and the current robot position.
        double angle = Math.toDegrees(Math.atan2(-futurePose.getY() + target.getY(), -futurePose.getX() + target.getX()));

        // Normalize the angle to a number between 0 and 360.
        angle = RotationUtil.toSignedDegrees(angle);

        // Return the angle to which the turret needs to be adjusted.
        return angle;
    }

    private static Transform2d getVelocity(double loopCycles) {
        var currentSpeeds = drivetrain.getCurrentSpeeds();
        return new Transform2d(new Translation2d(
            currentSpeeds.vxMetersPerSecond * (Robot.DEFAULT_PERIOD * loopCycles), 
            currentSpeeds.vyMetersPerSecond * (Robot.DEFAULT_PERIOD * loopCycles)),
            Rotation2d.fromRadians(currentSpeeds.omegaRadiansPerSecond * (Robot.DEFAULT_PERIOD * loopCycles)));
    }

    private static Transform2d getVelocitySquared(double loopCycles) {
        var currentSpeeds = drivetrain.getCurrentSpeeds();
        var x = currentSpeeds.vxMetersPerSecond * (Robot.DEFAULT_PERIOD * loopCycles);
        var y = currentSpeeds.vyMetersPerSecond * (Robot.DEFAULT_PERIOD * loopCycles);
        var r = Rotation2d.fromRadians(currentSpeeds.omegaRadiansPerSecond * (Robot.DEFAULT_PERIOD * loopCycles));
        return new Transform2d(new Translation2d(Math.copySign(x * x, x), Math.copySign(y * y, y)), r);
    }

    public static Pose2d getFuturePose(double loopCycles) {
        return drivetrain.getPose().transformBy(getVelocity(loopCycles));
    }
}