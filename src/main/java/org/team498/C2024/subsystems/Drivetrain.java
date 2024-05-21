package org.team498.C2024.subsystems;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static org.team498.C2024.Constants.DrivetrainConstants.*;

import java.util.Optional;

import org.photonvision.EstimatedRobotPose;
import org.team498.C2024.Constants;
import org.team498.C2024.Constants.DrivetrainConstants;
import org.team498.C2024.Constants.DrivetrainConstants.AngleConstants;
import org.team498.C2024.Constants.DrivetrainConstants.PoseConstants;
import org.team498.C2024.Ports;
import org.team498.C2024.Robot;
import org.team498.C2024.RobotPosition;
import org.team498.lib.LimelightHelpers;
import org.team498.lib.drivers.Gyro;
import org.team498.lib.util.PoseUtil;
import org.team498.lib.wpilib.ChassisSpeeds;

public class Drivetrain extends SubsystemBase {
    private final SwerveModule[] modules;

    private SwerveModuleState[] stateSetpoints;
    
    private final Gyro gyro = Gyro.getInstance();
    
    private final SwerveDriveKinematics kinematics;

    private final SwerveDrivePoseEstimator poseEstimator;

    // private final ProfiledPIDController angleController = new ProfiledPIDController(5, 0, 0.1, AngleConstants.CONTROLLER_CONSTRAINTS);
    private final PIDController angleController = new PIDController(5, 0, 0.1); // 0.08

    private final PIDController xController = new PIDController(Constants.DrivetrainConstants.PoseConstants.P, Constants.DrivetrainConstants.PoseConstants.I,  Constants.DrivetrainConstants.PoseConstants.D);
    private final SlewRateLimiter xLimiter = new SlewRateLimiter(MAX_ACCELERATION_METERS_PER_SECOND_SQUARED);
    private final SlewRateLimiter yLimiter = new SlewRateLimiter(MAX_ACCELERATION_METERS_PER_SECOND_SQUARED);
    private final PIDController yController = new PIDController(Constants.DrivetrainConstants.PoseConstants.P, Constants.DrivetrainConstants.PoseConstants.I,  Constants.DrivetrainConstants.PoseConstants.D);
    private final Field2d field2d = new Field2d();

    private double dT = 0;
    private final Timer timer = new Timer();

    private double angularAccelerationRadians = 0;
    private ChassisSpeeds acceleration = new ChassisSpeeds();

    public Drivetrain(){
        modules = new SwerveModule[]{
            new SwerveModule(Ports.DrivetrainPorts.FL_DRIVE, Ports.DrivetrainPorts.FL_STEER, Ports.DrivetrainPorts.FL_CANCODER, Ports.Accessories.DriveBus, FL_MODULE_OFFSET),
            new SwerveModule(Ports.DrivetrainPorts.FR_DRIVE, Ports.DrivetrainPorts.FR_STEER, Ports.DrivetrainPorts.FR_CANCODER, Ports.Accessories.DriveBus, FR_MODULE_OFFSET),
            new SwerveModule(Ports.DrivetrainPorts.BL_DRIVE, Ports.DrivetrainPorts.BL_STEER, Ports.DrivetrainPorts.BL_CANCODER, Ports.Accessories.DriveBus, BL_MODULE_OFFSET),
            new SwerveModule(Ports.DrivetrainPorts.BR_DRIVE, Ports.DrivetrainPorts.BR_STEER, Ports.DrivetrainPorts.BR_CANCODER, Ports.Accessories.DriveBus, BR_MODULE_OFFSET) 
        };

        angleController.enableContinuousInput(-180, 180);
        angleController.setTolerance(AngleConstants.EPSILON);
        angleController.setSetpoint(getYaw());
        // angleController.reset(getYaw());
        xController.setTolerance(0.1);
        yController.setTolerance(0.1);
        xLimiter.reset(0);
        yLimiter.reset(0);
        kinematics = new SwerveDriveKinematics(getModuleTranslations());

        poseEstimator = new SwerveDrivePoseEstimator(kinematics, Rotation2d.fromDegrees(getYaw()), getModulePositions(), new Pose2d(), 
            VecBuilder.fill(Units.inchesToMeters(3), Units.inchesToMeters(3), Math.toRadians(4)), // Odometry standard deviation
            VecBuilder.fill(Units.inchesToMeters(20), Units.inchesToMeters(20), Math.toRadians(15))); // Vision standard deviation

        stateSetpoints = getModuleStates();

    }

    ChassisSpeeds previouSpeeds = new ChassisSpeeds();
    double lastRotationSpeeds = 0;
    @Override
    public void periodic() {
        dT = timer.get();
        timer.restart();
        ChassisSpeeds currentSpeeds = getCurrentSpeeds();
        acceleration = new ChassisSpeeds(currentSpeeds.vxMetersPerSecond - previouSpeeds.vxMetersPerSecond, currentSpeeds.vyMetersPerSecond - previouSpeeds.vyMetersPerSecond, currentSpeeds.omegaRadiansPerSecond - previouSpeeds.omegaRadiansPerSecond);
        previouSpeeds = currentSpeeds;
        angularAccelerationRadians = getCurrentSpeeds().omegaRadiansPerSecond - lastRotationSpeeds;
        lastRotationSpeeds = getCurrentSpeeds().omegaRadiansPerSecond;
        field2d.setRobotPose(getPose().getX(), getPose().getY(), getPose().getRotation());
        // Optional<TimedPose> visionPose = PhotonVision.getInstance().getEstimatedPose();
        // if (visionPose.isPresent())
        // poseEstimator.addVisionMeasurement(visionPose.get().pose, visionPose.get().timeStamp);
        // Optional<EstimatedRobotPose> leftPose = PhotonVision.getInstance().leftEstimatedPose();
        // Optional<EstimatedRobotPose> rightPose = PhotonVision.getInstance().rightEstimatedPose();
        // EstimatedRobotPose resultPose = null;
        // if (leftPose.isPresent() && rightPose.isPresent())
        //     resultPose = getClosestPose(leftPose.get(), rightPose.get());
        // else if (leftPose.isPresent())
        //     resultPose = leftPose.get();
        // else if (rightPose.isPresent())
        //     resultPose = rightPose.get();
        // if (resultPose != null && RobotPosition.distanceToSpeaker(resultPose.estimatedPose.toPose2d()) < 4.5)// && RobotPosition.isNear(resultPose.estimatedPose.toPose2d(), 1.0) && !isStopped())
        //     poseEstimator.addVisionMeasurement(resultPose.estimatedPose.toPose2d(), resultPose.timestampSeconds);
        // if (resultPose != null && RobotPosition.distanceToSpeaker(resultPose.estimatedPose.toPose2d()) < 4.5 && isStopped()) 
        //     setYaw(resultPose.estimatedPose.getRotation().toRotation2d().getDegrees());

            Optional<LimelightHelpers.PoseEstimate> leftPose = Optional.of(LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("leftCamera"));
        Optional<LimelightHelpers.PoseEstimate > rightPose = Optional.of(LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("rightCamera"));
        LimelightHelpers.PoseEstimate resultPose = null;
        if (leftPose.isPresent() && rightPose.isPresent())
            resultPose = getClosestLimelightPose(leftPose.get(), rightPose.get());
        else if (leftPose.isPresent())
            resultPose = leftPose.get();
        else if (rightPose.isPresent())
            resultPose = rightPose.get();
        if (resultPose != null && RobotPosition.distanceToSpeaker(resultPose.pose) < 4.5)// && RobotPosition.isNear(resultPose.estimatedPose.toPose2d(), 1.0) && !isStopped())
            poseEstimator.addVisionMeasurement(resultPose.pose, resultPose.timestampSeconds);
        if (resultPose != null && RobotPosition.distanceToSpeaker(resultPose.pose) < 4.5 && isStopped()) 
            setYaw(resultPose.pose.getRotation().getDegrees());
        // else if (resultPose != null && isStopped()) {
        //     poseEstimator.addVisionMeasurement(resultPose.estimatedPose.toPose2d(), resultPose.timestampSeconds);
        // }
        // if (leftPose.isPresent()) {
        //     poseEstimator.addVisionMeasurement(leftPose.get().estimatedPose.toPose2d(), leftPose.get().timestampSeconds);
        // }
        // if (rightPose.isPresent()) {
        //     poseEstimator.addVisionMeasurement(rightPose.get().estimatedPose.toPose2d(), rightPose.get().timestampSeconds);
        // }
        SmartDashboard.putData(field2d);
        SmartDashboard.putNumber("Yaw", getYaw());
        SmartDashboard.putNumber("Angle Setpoint", angleController.getSetpoint());
        SmartDashboard.putNumber("Distance To Speaker", RobotPosition.distanceToSpeaker());
        
        SmartDashboard.putBoolean("left Camera Connected", PhotonVision.getInstance().leftCameraConnected());
        SmartDashboard.putBoolean("right camera connected", PhotonVision.getInstance().rightCameraConnected());

        // if (rightPose.isPresent()) {
        //     SmartDashboard.putNumber("right Camera X", rightPose.get().estimatedPose.getX());
        //     SmartDashboard.putNumber("right Camera Y", rightPose.get().estimatedPose.getY());
        // }
        // if (leftPose.isPresent()) {
        //     SmartDashboard.putNumber("left Camera X", leftPose.get().estimatedPose.getX());
        //     SmartDashboard.putNumber("left Camera Y", leftPose.get().estimatedPose.getY());
        //}
        for (int i = 0; i < modules.length; i++) {
            //modules[i].setBrakeMode(RobotState.isEnabled());
            SmartDashboard.putNumber(i + " CanCoder Value", modules[i].getAngle());
            SmartDashboard.putNumber(i + " Velocity Setpoint", modules[i].getSpeed());
            SmartDashboard.putNumber(i + " Velocity Real", modules[i].getDriveMotorSpeed());
            SmartDashboard.putNumber(i + "Drive Setpoint", modules[i].driveSetpoint * Units.inchesToMeters(DRIVE_WHEEL_CIRCUMFERENCE) / DrivetrainConstants.MK4I_DRIVE_REDUCTION_L3);
            SmartDashboard.putNumber(i + "Steer Setpoint", modules[i].steerSetpoint);
            
        }
        poseEstimator.update(Rotation2d.fromDegrees(getYaw()), getModulePositions());
        
    }

    public void updateIntegratedEncoders() {
        // timer.restart();
        for(SwerveModule module : modules) {
            module.updateIntegratedEncoder();
        }
        // System.out.println(timer.get());
    }

    public void enableBrakeMode(boolean setBrake) {
        for (int i = 0; i < modules.length; i++) {
            modules[i].enableBrakeMode(setBrake);
        }
    }

    private EstimatedRobotPose getClosestPose(EstimatedRobotPose leftPose, EstimatedRobotPose rightPose){
        double leftDistance = RobotPosition.distanceTo(leftPose.estimatedPose.toPose2d());
        double rightDistance = RobotPosition.distanceTo(rightPose.estimatedPose.toPose2d());
        return (leftDistance > rightDistance) ? rightPose : leftPose;
    }
    private LimelightHelpers.PoseEstimate getClosestLimelightPose(LimelightHelpers.PoseEstimate leftPose, LimelightHelpers.PoseEstimate rightPose){
        double leftDistance = RobotPosition.distanceTo(leftPose.pose);
        double rightDistance = RobotPosition.distanceTo(rightPose.pose);
        return (leftDistance > rightDistance) ? rightPose : leftPose;
    }
    private Pose2d averagePoses(EstimatedRobotPose pose1, EstimatedRobotPose pose2) {
        return new Pose2d(new Translation2d((pose1.estimatedPose.getX() + pose2.estimatedPose.getX()) / 2, (pose1.estimatedPose.getY() + pose2.estimatedPose.getY()) / 2), Rotation2d.fromDegrees((pose1.estimatedPose.getRotation().toRotation2d().getDegrees() + pose2.estimatedPose.getRotation().toRotation2d().getDegrees()) / 2));
    }

    private boolean isStopped() {
        return Math.abs(getCurrentSpeeds().omegaRadiansPerSecond) <= 0.5 && Math.abs(getCurrentSpeeds().vxMetersPerSecond) < 0.1 && Math.abs(getCurrentSpeeds().vyMetersPerSecond) < 0.1;
    }

    PIDController twistXController = new PIDController(0.025, 0, 0);
    PIDController twistYController = new PIDController(0.025, 0, 0);

    // PIDController accelController = new PIDController(100e10, 0, 0);

    public void drive(double vx, double vy, double degreesPerSecond, boolean fieldOriented) {
        ChassisSpeeds speeds = fieldOriented
                               ? ChassisSpeeds.fromFieldRelativeSpeeds(vx, vy, Math.toRadians(degreesPerSecond), getYaw())
                               : new ChassisSpeeds(vx, vy, Math.toRadians(degreesPerSecond));

        speeds.vxMetersPerSecond = xLimiter.calculate(speeds.vxMetersPerSecond);
        speeds.vyMetersPerSecond = yLimiter.calculate(speeds.vyMetersPerSecond);
        Translation2d desiredFuture = new Translation2d(speeds.vxMetersPerSecond / dT, speeds.vyMetersPerSecond / dT);
        // double desiredFVel = Math.toDegrees(speeds.omegaRadiansPerSecond) / dT;
        //speeds = ChassisSpeeds.fromWPIChassisSpeeds(ChassisSpeeds.discretize(speeds, dT * -9));
        speeds = updateSpeeds(speeds, acceleration, dT * -6);
        Translation2d realFuture = new Translation2d(speeds.vxMetersPerSecond / dT, speeds.vyMetersPerSecond / dT);
        // double realFVel = Math.toDegrees(speeds.omegaRadiansPerSecond) / dT;
        // double diffX = desiredFuture.minus(realFuture).getX();
        // double diffY = desiredFuture.minus(realFuture).getY();

        double diffX = twistXController.calculate(realFuture.getX(), desiredFuture.getX());
        double diffY = twistYController.calculate(realFuture.getY(), desiredFuture.getY());
        // double accel = accelController.calculate(realFVel, desiredFVel);
        speeds = new ChassisSpeeds(speeds.vxMetersPerSecond - diffX, speeds.vyMetersPerSecond - diffY, speeds.omegaRadiansPerSecond);
        SmartDashboard.putNumber("xDiff", diffX);
        SmartDashboard.putNumber("yDiff", diffY);

        // SmartDashboard.putNumber("angular accel", accel);
        
        // speeds = updateSpeeds(speeds, acceleration, dT * 14);
        stateSetpoints = kinematics.toSwerveModuleStates(speeds);
        SwerveDriveKinematics.desaturateWheelSpeeds(stateSetpoints, DrivetrainConstants.MAX_VELOCITY_METERS_PER_SECOND);

        setModuleStates(stateSetpoints);
    }

    private ChassisSpeeds updateSpeeds(ChassisSpeeds speeds, ChassisSpeeds acceleration, double dT){
        // double dt = Robot.DEFAULT_PERIOD * -15;
        double dt = dT;
        Pose2d newPose = new Pose2d(
             speeds.vxMetersPerSecond *dt,// +acceleration.vxMetersPerSecond *dt * dt,
             speeds.vyMetersPerSecond *dt,// +acceleration.vyMetersPerSecond *dt*dt,
             Rotation2d.fromRadians(speeds.omegaRadiansPerSecond *dt)// -acceleration.omegaRadiansPerSecond *dt*dt)
        );
        Twist2d twist = new Pose2d().log(newPose);
        return new ChassisSpeeds(twist.dx/dt, twist.dy/dt, twist.dtheta/dt);
    }

    public void setModuleStates(SwerveModuleState[] states) {
        for (int i = 0; i < modules.length; i++) modules[i].setState(states[i]);
    }

    public SwerveModulePosition[] getModulePositions() {
        var positions = new SwerveModulePosition[modules.length];
        for (int i = 0; i < modules.length; i++) positions[i] = new SwerveModulePosition(modules[i].getPosition(), Rotation2d.fromDegrees(modules[i].getAngle()).plus(Rotation2d.fromDegrees(180)));
        return positions;
    }

    public SwerveModuleState[] getModuleStates() {
        var states = new SwerveModuleState[modules.length];
        for (int i = 0; i < modules.length; i++) states[i] = new SwerveModuleState(modules[i].getDriveMotorSpeed(), Rotation2d.fromDegrees(modules[i].getAngle()));
        return states;
    }

    public void setPositionGoal(Pose2d pose) {xController.setSetpoint(pose.getX()); yController.setSetpoint(pose.getY()); setAngleGoal(pose.getRotation().getDegrees()); /*Logger.getInstance().recordOutput("TargetPose", pose);*/}
    public ChassisSpeeds calculatePositionSpeed() {return new ChassisSpeeds(calculateXSpeed(), calculateYSpeed(), calculateAngleSpeed(false));}
    public boolean atPositionGoal() {return (Math.abs(xController.getPositionError()) < PoseConstants.EPSILON) && (Math.abs(yController.getPositionError()) < PoseConstants.EPSILON) && atAngleGoal();}

    public void setXGoal(double pose) {xController.setSetpoint(pose);}
    public double calculateXSpeed() {return -xController.calculate(getPose().getX());}
    public boolean atXGoal() {return Math.abs(xController.getPositionError()) < PoseConstants.EPSILON;}
    
    public void setYGoal(double pose) {yController.setSetpoint(pose);}
    public double calculateYSpeed() {return -yController.calculate(getPose().getY());}
    public boolean atYGoal() {return Math.abs(yController.getPositionError()) < PoseConstants.EPSILON;}

    public void setAngleGoal(double angle) {angleController.setSetpoint(angle);}
    public double calculateAngleSpeed(boolean isTele) {
        double speed = -angleController.calculate(getYaw());
        // if (!isTele) {
        //     double error = Math.abs(angleController.getPositionError());
        //     if (error < 20) {
        //         speed /= 3;
        //     }
        //     if (error < 50) {
        //         speed /= 2;
        //     }
        //     else if (error < 100.0) {
        //         speed /= 1.5;
        //     }
        // }
        
        return speed;
    }
    public boolean atAngleGoal() {return Math.abs(angleController.getPositionError()) < AngleConstants.EPSILON;}

    public Pose2d getPose() {return poseEstimator.getEstimatedPosition();}
    public void setPose(Pose2d pose) {poseEstimator.resetPosition(Rotation2d.fromDegrees(getYaw()), getModulePositions(), pose);}
    public double getYaw() {return gyro.yaw();}
    public void setYaw(double angle) {gyro.setYaw(angle); angleController.setSetpoint(angle);}
    /** Return a double array with a value for yaw pitch and roll in that order */
    //public double[] getGyro() {return new double[] {gyroInputs.yaw, gyroInputs.pitch, gyroInputs.roll};}

    public void stop() {drive(0, 0, 0, false);}
    public void X() {setModuleStates(new SwerveModuleState[] {new SwerveModuleState(0, Rotation2d.fromDegrees(45)), new SwerveModuleState(0, Rotation2d.fromDegrees(-45)), new SwerveModuleState(0, Rotation2d.fromDegrees(-45)), new SwerveModuleState(0, Rotation2d.fromDegrees(45))});}

    public ChassisSpeeds getCurrentSpeeds() {return ChassisSpeeds.fromWPIChassisSpeeds(kinematics.toChassisSpeeds(getModuleStates()));}

    public double getRobotRelativeYAcceleration() {
        double sum = 0;
        for (SwerveModule swerveModule : modules) {
            sum += swerveModule.getYAcceleration();
        }
        return sum;
    }

    private Translation2d[] getModuleTranslations() {
        double moduleDistance = Units.inchesToMeters(SWERVE_MODULE_DISTANCE_FROM_CENTER);
        Translation2d FL_ModulePosition = new Translation2d(moduleDistance, moduleDistance);
        Translation2d FR_ModulePosition = new Translation2d(moduleDistance, -moduleDistance);
        Translation2d BL_ModulePosition = new Translation2d(-moduleDistance, moduleDistance);
        Translation2d BR_ModulePosition = new Translation2d(-moduleDistance, -moduleDistance);
        return new Translation2d[] {FL_ModulePosition, FR_ModulePosition, BL_ModulePosition, BR_ModulePosition};
    }

    private static Drivetrain instance;

    public static Drivetrain getInstance() {
        if (instance == null) {
            instance = new Drivetrain();
        }
        return instance;
    }
}
