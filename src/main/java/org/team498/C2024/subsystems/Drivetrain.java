package org.team498.C2024.subsystems;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static org.team498.C2024.Constants.DrivetrainConstants.*;

import org.team498.C2024.Robot;
import org.team498.lib.drivers.Gyro;
import org.team498.lib.wpilib.ChassisSpeeds;

public class Drivetrain extends SubsystemBase {
    private final SwerveModule[] modules;

    private SwerveModuleState[] stateSetpoints;
    
    private final Gyro gyro = Gyro.getInstance();
    
    private final SwerveDriveKinematics kinematics;

    private final SwerveDrivePoseEstimator poseEstimator;

    private final ProfiledPIDController angleController = new ProfiledPIDController(0, 0, 0, null, 0);

    private final PIDController xController = new PIDController(0, 0, 0, 0);
    private final SlewRateLimiter xLimiter = new SlewRateLimiter(MAX_ACCELERATION_METERS_PER_SECOND_SQUARED);
    private final SlewRateLimiter yLimiter = new SlewRateLimiter(MAX_ACCELERATION_METERS_PER_SECOND_SQUARED);
    private final PIDController yController = new PIDController(0, 0, 0, 0);
    private final Field2d field2d = new Field2d();
    public Drivetrain(){
        modules = new SwerveModule[]{
            new SwerveModule(0, 0, 0, getName(), 0),
            new SwerveModule(0, 0, 0, getName(), 0),
            new SwerveModule(0, 0, 0, getName(), 0),
            new SwerveModule(0, 0, 0, getName(), 0) 
        };

         angleController.enableContinuousInput(-180, 180);
        angleController.setTolerance(0);
        xController.setTolerance(0);
        yController.setTolerance(0);
        xLimiter.reset(0);
        yLimiter.reset(0);

        kinematics = new SwerveDriveKinematics(getModuleTranslations());

        poseEstimator = new SwerveDrivePoseEstimator(kinematics, Rotation2d.fromDegrees(getYaw()), getModulePositions(), new Pose2d(), 
            VecBuilder.fill(Units.inchesToMeters(3), Units.inchesToMeters(3), Math.toDegrees(4)), // Odometry standard deviation
            VecBuilder.fill(Units.inchesToMeters(10), Units.inchesToMeters(10), Math.toDegrees(15))); // Vision standard deviation

        stateSetpoints = getModuleStates();

    }

    @Override
    public void periodic() {
        field2d.setRobotPose(getPose().getX(), getPose().getY(), getPose().getRotation());
        SmartDashboard.putData(field2d);
        SmartDashboard.putNumber("Pitch", gyro.pitch());
        for (int i = 0; i < modules.length; i++) {
            //modules[i].setBrakeMode(RobotState.isEnabled());
            if (RobotState.isDisabled()) {
                modules[i].updateIntegratedEncoder();
            }
        }
        poseEstimator.update(Rotation2d.fromDegrees(getYaw()), getModulePositions());
    }

    public void drive(double vx, double vy, double degreesPerSecond, boolean fieldOriented) {
        ChassisSpeeds speeds = fieldOriented
                               ? ChassisSpeeds.fromFieldRelativeSpeeds(-vx, -vy, Math.toRadians(degreesPerSecond), getYaw())
                               : new ChassisSpeeds(-vx, -vy, Math.toRadians(degreesPerSecond));

        speeds.vxMetersPerSecond = xLimiter.calculate(speeds.vxMetersPerSecond);
        speeds.vyMetersPerSecond = yLimiter.calculate(speeds.vyMetersPerSecond);
        speeds = updateSpeeds(speeds);
        stateSetpoints = kinematics.toSwerveModuleStates(speeds);

        setModuleStates(stateSetpoints);
    }

    private ChassisSpeeds updateSpeeds(ChassisSpeeds speeds){
        double dt = Robot.DEFAULT_PERIOD *2;
        Pose2d newPose = new Pose2d(speeds.vxMetersPerSecond *dt, speeds.vyMetersPerSecond *dt, Rotation2d.fromRadians(speeds.omegaRadiansPerSecond *dt));
        Twist2d twist = new Pose2d().log(newPose);
        return new ChassisSpeeds(twist.dx/dt, twist.dy/dt, twist.dtheta/dt);
    }   

    public void setModuleStates(SwerveModuleState[] states) {
        for (int i = 0; i < modules.length; i++) modules[i].setState(states[i]);
    }

    public SwerveModulePosition[] getModulePositions() {
        var positions = new SwerveModulePosition[modules.length];
        for (int i = 0; i < modules.length; i++) positions[i] = new SwerveModulePosition(modules[i].getPosition(), Rotation2d.fromDegrees(modules[i].getAngle()));
        return positions;
    }

    public SwerveModuleState[] getModuleStates() {
        var states = new SwerveModuleState[modules.length];
        for (int i = 0; i < modules.length; i++) states[i] = new SwerveModuleState(modules[i].getSpeed(), Rotation2d.fromDegrees(modules[i].getAngle()));
        return states;
    }

public void setPositionGoal(Pose2d pose) {xController.setSetpoint(pose.getX()); yController.setSetpoint(pose.getY()); setAngleGoal(pose.getRotation().getDegrees()); /*Logger.getInstance().recordOutput("TargetPose", pose);*/}
    public ChassisSpeeds calculatePositionSpeed() {return new ChassisSpeeds(xController.calculate(getPose().getX()), yController.calculate(getPose().getY()), calculateAngleSpeed());}
    public boolean atPositionGoal() {return (Math.abs(xController.getPositionError()) < PoseConstants.EPSILON) && (Math.abs(yController.getPositionError()) < PoseConstants.EPSILON) && atAngleGoal();}

    public void setXGoal(double pose) {xController.setSetpoint(pose);}
    public double calculateXSpeed() {return xController.calculate(getPose().getX());}
    public boolean atXGoal() {return Math.abs(xController.getPositionError()) < PoseConstants.EPSILON;}
    
    public void setYGoal(double pose) {yController.setSetpoint(pose);}
    public double calculateYSpeed() {return yController.calculate(getPose().getY());}
    public boolean atYGoal() {return Math.abs(yController.getPositionError()) < PoseConstants.EPSILON;}

    public void setAngleGoal(double angle) {angleController.setGoal(angle);}
    public double calculateAngleSpeed() {return angleController.calculate(getYaw());}
    public boolean atAngleGoal() {return Math.abs(angleController.getPositionError()) < AngleConstants.EPSILON;}

    public Pose2d getPose() {return poseEstimator.getEstimatedPosition();}
    public void setPose(Pose2d pose) {poseEstimator.resetPosition(Rotation2d.fromDegrees(getYaw()), getModulePositions(), pose);}
    public double getYaw() {return gyro.yaw();}
    public void setYaw(double angle) {gyro.setYaw(angle); angleController.reset(angle);}
    /** Return a double array with a value for yaw pitch and roll in that order */
    //public double[] getGyro() {return new double[] {gyroInputs.yaw, gyroInputs.pitch, gyroInputs.roll};}

    public void stop() {drive(0, 0, 0, false);}
    public void X() {setModuleStates(new SwerveModuleState[] {new SwerveModuleState(0, Rotation2d.fromDegrees(45)), new SwerveModuleState(0, Rotation2d.fromDegrees(-45)), new SwerveModuleState(0, Rotation2d.fromDegrees(-45)), new SwerveModuleState(0, Rotation2d.fromDegrees(45))});}

    public ChassisSpeeds getCurrentSpeeds() {return ChassisSpeeds.fromWPIChassisSpeeds(kinematics.toChassisSpeeds(getModuleStates()));}

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
