package org.team498.C2024;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

import org.team498.C2024.Constants.DrivetrainConstants;
import org.team498.C2024.subsystems.Drivetrain;
import org.team498.C2024.subsystems.Shooter;
import org.team498.lib.field.BaseRegion;
import org.team498.lib.field.Point;
import org.team498.lib.util.PoseUtil;
import org.team498.lib.util.RotationUtil;
import org.team498.lib.wpilib.ChassisSpeeds;

public class RobotPosition {
    private static final Drivetrain drivetrain = Drivetrain.getInstance();
    private static final Shooter shooter = Shooter.getInstance();
    public static final double scoringOffset = Units.inchesToMeters((DrivetrainConstants.ROBOT_WIDTH / 2) + 10);

    public static final double defaultTOF = 0.4;

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

    public static double distanceTo(Pose2d pose) {
        return distanceTo(new Point(pose.getX(), pose.getY()));
    }

    private static double distanceTo(Point point) {return distanceTo(point, drivetrain.getPose());}

    public static double calculateDegreesToTarget(Pose2d target, double tof) {
        Pose2d currentPose = drivetrain.getPose();
        ChassisSpeeds currentSpeeds = drivetrain.getCurrentSpeeds();

        // Estimate the future pose of the robot to compensate for lag
        double newX;
        double newY;
        if (Robot.alliance.get() == Alliance.Red) {
            newX = currentPose.getX() + (-currentSpeeds.vxMetersPerSecond * tof);
            newY = currentPose.getY() + (-currentSpeeds.vyMetersPerSecond * tof);
        } else {
            newX = currentPose.getX() + (currentSpeeds.vxMetersPerSecond * tof);
            newY = currentPose.getY() + (currentSpeeds.vyMetersPerSecond * tof);
        }
        

        Pose2d futurePose = new Pose2d(newX, newY, new Rotation2d());

        // Calculate the angle between the target and the current robot position.
        double angle = Math.toDegrees(Math.atan2(-futurePose.getY() + target.getY(), -futurePose.getX() + target.getX()));

        // Normalize the angle to a number between 0 and 360.
        angle = RotationUtil.toSignedDegrees(angle);

        // Return the angle to which the turret needs to be adjusted.
        return angle;
    }
    public static double calculateDegreesToTarget(Pose2d target) {
        return calculateDegreesToTarget(target, defaultTOF);
    }

    public static double distanceToSpeakerStatic(){
        Pose2d speakerPose = FieldPositions.getSpeaker();
        Point speakerPoint = new Point(speakerPose.getX(), speakerPose.getY());
        return distanceTo(speakerPoint, getFuturePose(defaultTOF));
    }

    public static double distanceToSpeaker(){
        Pose2d speakerPose = FieldPositions.getSpeaker();
        Point speakerPoint = new Point(speakerPose.getX(), speakerPose.getY());
        return distanceTo(speakerPoint, getFuturePose());
    }

    private static Transform2d getVelocity(double tof) {
        var currentSpeeds = drivetrain.getCurrentSpeeds();
        return new Transform2d(new Translation2d(
            -currentSpeeds.vxMetersPerSecond * (tof),
            -currentSpeeds.vyMetersPerSecond * (tof)),
            Rotation2d.fromRadians(currentSpeeds.omegaRadiansPerSecond * (tof)));
    }

    // private static Transform2d getVelocitySquared(double loopCycles) {
    //     var currentSpeeds = drivetrain.getCurrentSpeeds();
    //     var x = currentSpeeds.vxMetersPerSecond * (Robot.DEFAULT_PERIOD * loopCycles);
    //     var y = currentSpeeds.vyMetersPerSecond * (Robot.DEFAULT_PERIOD * loopCycles);
    //     var r = Rotation2d.fromRadians(currentSpeeds.omegaRadiansPerSecond * (Robot.DEFAULT_PERIOD * loopCycles));
    //     return new Transform2d(new Translation2d(Math.copySign(x * x, x), Math.copySign(y * y, y)), r);
    //}

    public static  double calculateDegreesToSpeaker(){
        Pose2d blueSpeaker = FieldPositions.blueSpeaker.toPose2d();
        if (Robot.alliance.get() == Alliance.Red) return calculateDegreesToTarget(PoseUtil.flip(blueSpeaker), shooter.getTimeOfFlight());
        return calculateDegreesToTarget(blueSpeaker, shooter.getTimeOfFlight());
    }

    public static  double calculateDegreesToNote(Point note){
        Pose2d notePose = note.toPose2d();
        if (Robot.alliance.get() == Alliance.Red) return calculateDegreesToTarget(PoseUtil.flip(notePose));
        return calculateDegreesToTarget(notePose);
    }

    public static double calculateLimelightAngleToNote(double distance) {
        // equation derived from notebook/limelight_model.ipynb
        return 3.226 * Math.pow(distance, 5) - 28.95 * Math.pow(distance, 4) + 105.4 * Math.pow(distance, 3) - 200 * Math.pow(distance, 2) + 211.2 * distance - 31.05;
    }
    public static double calculateLimelightAngleToNote(Point note) {
        return calculateLimelightAngleToNote(distanceTo(note));
    }
    

    public static Pose2d getPose() {
        return drivetrain.getPose();
    }
    public static Pose2d getFuturePose(double tof) {
        return drivetrain.getPose().transformBy(getVelocity(tof));
    }
    public static Pose2d getFuturePose() {
        return drivetrain.getPose().transformBy(getVelocity(shooter.getTimeOfFlight()));
    }
    public static Transform2d getFutureVelocity() {
        return getVelocity(shooter.getTimeOfFlight());
    }
    public static double getSpeakerRelativeVelocity() {
        Pose2d robotPose = getFuturePose();
        Pose2d speakerPose = FieldPositions.getSpeaker();
        Transform2d robotVelocity = getFutureVelocity();
        double x = robotVelocity.getX();
        double y = robotVelocity.getY();
        double r = Math.sqrt(x * x + y * y);
        
        double velocityDirection = Math.atan2(y, x);
        double positionDirection = Math.atan2(robotPose.getY() - speakerPose.getY(), robotPose.getX() - speakerPose.getX());// + Math.toRadians(Robot.rotationOffset);
        double angleDifference = velocityDirection - positionDirection;
        angleDifference = (angleDifference + Math.PI) % (2 * Math.PI) - Math.PI;
        if (Math.abs(angleDifference) > Math.PI / 2 && Robot.alliance.get() == Alliance.Blue) {
            r = -r; // Moving towards the speaker, make r negative
        } else if (Math.abs(angleDifference) <= Math.PI / 2 && Robot.alliance.get() != Alliance.Blue) {
            r = -r;
        }
        return r;
    }
}