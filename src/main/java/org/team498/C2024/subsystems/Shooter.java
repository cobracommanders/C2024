package org.team498.C2024.subsystems;

import org.team498.C2024.Constants;
import org.team498.C2024.Ports;
import org.team498.C2024.RobotPosition;
import org.team498.C2024.State;
import org.team498.C2024.StateController;
import org.team498.C2024.Constants.ShooterConstants;
import org.team498.C2024.Constants.ShooterConstants.AngleConstants;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
/*
 * This is an example Flywheel subsystem that can be used for reference while writing your own subsystems :)
 * The mechanism uses two NEO brushless motors controlled by Spark Maxes
 * They are mounted opposite each other and power a single-wheel flywheel with velocity control
 * While this code is for a Flywheel, the ideas here can be used for any velocity-based subsystem
 */
public class Shooter extends SubsystemBase {
    // all variable/object declaration goes at the top of the class. 
    // They can be instantiated (given values) later, but they must be declared here

    // Motors will almost always be private because they will only be controlled using public methods. There should be NO global use where motors 
    private final TalonFX leftMotor; // Declaration for a NEO or NEO550 brushless motor
    private final TalonFX rightMotor; // We use left / right descriptors to make identification easier in testing and communication
    private final CANSparkMax angleMotor; //Declaration for a Built-in NEO/NEO550 encoder
    private final CANSparkMax feedMotor;
    
    private final CANcoder angleEncoder;

    private final PIDController rightController; //Declaration for a P Controller
    private final PIDController leftController; 
    private final PIDController feedController;
    private final PIDController angleController;

    private final SimpleMotorFeedforward rightFeedForward;
    private final SimpleMotorFeedforward leftFeedForward;
    private final SimpleMotorFeedforward feedFeedforward;
    private final ArmFeedforward angleFeedForward;

    // Variables will store the current properties of the subsystem
    private double rightSpeed;
    private double leftSpeed;
    private double angle;
    private double feedSpeed;
    private boolean isManual;
    private double manualSpeed;
    private double manualSpeedRight;
    private double manualSpeedLeft;
    private double manualSpeedFeed;
    private State.Shooter currentState;

    private boolean isActivated = true;
    
    // Constructor: Configure Motor Controller settings and  
    // Instantiate all objects (assign values to every variable and object)
    public Shooter() {
        leftMotor = new TalonFX(Ports.ShooterPorts.LEFT_MOTOR);
        rightMotor = new TalonFX(Ports.ShooterPorts.RIGHT_MOTOR);
        feedMotor = new CANSparkMax(Ports.ShooterPorts.FEED_MOTOR, MotorType.kBrushless);
        angleMotor = new CANSparkMax(Ports.ShooterPorts.ANGLE_MOTOR, MotorType.kBrushless); //this can be left or right motor, whichever is most convenient
        
        angleEncoder = new CANcoder(Ports.ShooterPorts.ANGLE_ENCODER);

        // Use the subsystems constants to instantiate PID and Feedforward
        rightController = new PIDController(Constants.ShooterConstants.P,Constants.ShooterConstants.I, Constants.ShooterConstants.D);
        leftController = new PIDController(Constants.ShooterConstants.P,Constants.ShooterConstants.I, Constants.ShooterConstants.D);
        feedController = new PIDController(Constants.ShooterConstants.fP, Constants.ShooterConstants.fI, Constants.ShooterConstants.fD);
        angleController = new PIDController(Constants.ShooterConstants.AngleConstants.P, Constants.ShooterConstants.AngleConstants.I, Constants.ShooterConstants.AngleConstants.D);

        rightFeedForward = new SimpleMotorFeedforward(Constants.ShooterConstants.S, Constants.ShooterConstants.V, Constants.ShooterConstants.A);
        leftFeedForward = new SimpleMotorFeedforward(Constants.ShooterConstants.S, Constants.ShooterConstants.V, Constants.ShooterConstants.A);
        feedFeedforward = new SimpleMotorFeedforward(0, ShooterConstants.fV, 0);
        angleFeedForward = new ArmFeedforward(Constants.ShooterConstants.AngleConstants.S, Constants.ShooterConstants.AngleConstants.G, Constants.ShooterConstants.AngleConstants.V);

        // Instantiate variables to intitial values
        currentState = State.Shooter.IDLE;
        rightSpeed = State.Shooter.IDLE.speed;
        leftSpeed = State.Shooter.IDLE.speed;
        feedSpeed = State.Shooter.IDLE.feedSpeed;
        angle = State.Shooter.IDLE.angle;

        rightController.setTolerance(0.1);
        leftController.setTolerance(0.1);
        feedController.setTolerance(0.1);
        angleController.setTolerance(0.1);

        // reset motor defaults to ensure all settings are clear
        rightMotor.getConfigurator().apply(new TalonFXConfiguration().withCurrentLimits(new CurrentLimitsConfigs().withSupplyCurrentLimit(35).withSupplyCurrentLimitEnable(true)));
        leftMotor.getConfigurator().apply(new TalonFXConfiguration().withCurrentLimits(new CurrentLimitsConfigs().withSupplyCurrentLimit(35).withSupplyCurrentLimitEnable(true)));
        feedMotor.restoreFactoryDefaults();
        angleMotor.restoreFactoryDefaults();

        CANcoderConfiguration canCoderConfig = new CANcoderConfiguration();
        canCoderConfig.MagnetSensor.MagnetOffset = Constants.ShooterConstants.AngleConstants.ANGLE_OFFSET;
        canCoderConfig.MagnetSensor.AbsoluteSensorRange = AbsoluteSensorRangeValue.Unsigned_0To1;
        angleEncoder.getConfigurator().apply(canCoderConfig);
    }

    // This method will run every 10-20 milliseconds (about 50-100 times in one second)
    @Override
    public void periodic() {
        SmartDashboard.putNumber("Shooter Angle", getAngle());
        SmartDashboard.putNumber("left flywheel speed", getLeftSpeedRPM());
        SmartDashboard.putNumber("right flywheel speed", getRightSpeedRPM());
        SmartDashboard.putNumber("left flywheel setpoint", leftSpeed);
        SmartDashboard.putNumber("right flywheel setpoint", rightSpeed);
        SmartDashboard.putNumber("feed flywheel speed", getFeedSpeedRPM());
        SmartDashboard.putNumber("feed flywheel setpoint", feedSpeed);
        
        SmartDashboard.putNumber("Adjusted Angle", calculateAngle(RobotPosition.distanceToSpeaker()));
        SmartDashboard.putNumber("Adjusted Velocity", calculateSpeed(RobotPosition.distanceToSpeaker()));
        // This condition will reduce CPU utilization when the motor is not meant to run and save power because 
        // it will not actively deccelerate the wheel
        if (currentState == State.Shooter.CRESCENDO){
            this.leftSpeed = calculateSpeed(RobotPosition.distanceToSpeaker());
            this.rightSpeed = this.leftSpeed * 0.6;
            // this.feedSpeed = currentState.feedSpeed;//feedController.calculate(getFeedSpeedRPM(), feedSpeed) + feedFeedforward.calculate(feedSpeed);
            this.angle = calculateAngle(RobotPosition.distanceToSpeaker());
        }

        else if (currentState == State.Shooter.VISION) {
            this.angle = RobotPosition.calculateLimelightAngleToNote(StateController.getInstance().getNote());
        }
        if (this.angle > 78) {
            this.angle = 78;
        } else if (this.angle < 33) {
            this.angle = 33;
        }

        double rightShooterSpeed = 0;
        double leftShooterSpeed = 0; // We will use this variable to keep track of our desired speed
        double feedShooterSpeed = 0;
        double angleSpeed = 0;

        if (isActivated) {
            leftShooterSpeed = leftController.calculate(getLeftSpeedRPM(), leftSpeed) + leftFeedForward.calculate(leftSpeed);
            rightShooterSpeed = rightController.calculate(getRightSpeedRPM(), rightSpeed) + rightFeedForward.calculate(rightSpeed);
            feedShooterSpeed = feedController.calculate(getFeedSpeedRPM(), feedSpeed) + feedFeedforward.calculate(feedSpeed);
            angleSpeed = angleController.calculate(getAngle(), this.angle);
        }

        if (isManual) {
            angleSpeed = manualSpeed;
            rightShooterSpeed = manualSpeedRight;
            leftShooterSpeed = manualSpeedLeft;
            feedShooterSpeed = manualSpeedFeed;

        }
            
        
        if ((getAngle() < AngleConstants.MIN_ANGLE && angleSpeed < 0) || (getAngle() > AngleConstants.MAX_ANGLE && angleSpeed > 0)) {
            rightShooterSpeed = 0;
            leftShooterSpeed = 0;
            feedShooterSpeed = 0;
            angleSpeed = 0;
        }
        // set(manualSpeedRight, manualSpeedLeft);
        set(rightShooterSpeed, leftShooterSpeed); // set the motor behavior using set() to interact with the controllers
        setFeed(feedShooterSpeed);
        setAngle(angleSpeed + angleFeedForward.calculate(Units.degreesToRadians(getAngle()), 0));
        // We divide by MAX_RPM to scale to {-1, 1}
    }
    
    //sets top speed and bottom speed
    private void set(double rightSpeed, double leftSpeed) {
        leftMotor.set(-leftSpeed);
        rightMotor.set(rightSpeed); // invert speed on right side (assuming the motor is facing opposite the left)
    }

    //sets speed for feedForward
    private void setFeed(double speed){
        feedMotor.set(-speed);
    }
    
    //sets speed for angleMotor
    private void setAngle(double speed){
        angleMotor.set(-speed);
    }

    public void setAngleManual(boolean isManual, double speed, double flywheelSpeed){
        this.isManual = isManual;
        this.manualSpeed = speed;
        this.manualSpeedRight = flywheelSpeed;
        this.manualSpeedLeft = flywheelSpeed;
        this.manualSpeedFeed = flywheelSpeed == 0.0 ? 0.0 : 1.0;
    }

    /**
     * sets states for PID controllers and motor speeds
     */
    public void setState(State.Shooter state) {
        isActivated = true;
        currentState = state; // update state
        leftSpeed = state.speed;
        rightSpeed = state.speed * 0.7;
        feedSpeed = state.feedSpeed;
        angle = state.angle;
        rightController.setSetpoint(this.leftSpeed); // update pController
        leftController.setSetpoint(this.rightSpeed);
        feedController.setSetpoint(this.feedSpeed);
        angleController.setSetpoint(this.angle);
    }

    // Getter method to retrieve current State
    public State.Shooter getState() {
        return currentState;
    }

    /**
     * returns leftController, rightController, and angleController setpoints
     */
    public boolean atSetpoint(){
        return angleController.atSetpoint();
        // return leftController.atSetpoint() && rightController.atSetpoint() && angleController.atSetpoint();
    }

    /**
     * returns angle of angleEncoder
     */
    public double getAngle() {
        return angleEncoder.getAbsolutePosition().getValueAsDouble() * 360;
    }
    public double getLeftSpeedMPS() {
        return -leftMotor.getVelocity().getValueAsDouble() * ShooterConstants.GEAR_RATIO * ShooterConstants.CIRCUMFERENCE;
    }

    public double getLeftSpeedRPM() {
        return -leftMotor.getVelocity().getValueAsDouble() * 60 * ShooterConstants.GEAR_RATIO;
    }
    public double getRightSpeedMPS() {
        return rightMotor.getVelocity().getValueAsDouble() * ShooterConstants.GEAR_RATIO * ShooterConstants.CIRCUMFERENCE;
    }
    public double getRightSpeedRPM() {
        return rightMotor.getVelocity().getValueAsDouble() * 60 * ShooterConstants.GEAR_RATIO;
    }

    public double getFeedSpeedRPM() {
        return -feedMotor.getEncoder().getVelocity();
    }

    public boolean shooterState(){
        return getState() == State.Shooter.CRESCENDO || getState() == State.Shooter.PODIUM || getState() == State.Shooter.SUBWOOFER;
    }

    private static double RPM_to_MPS(double rpm) {
        return (rpm / 60.0) * ShooterConstants.CIRCUMFERENCE;
    }
    private static double MPS_to_RPM (double mps) {
        return (mps * 60.0) / ShooterConstants.CIRCUMFERENCE;
    }


    // private double calculateRightSpeed(double distance){
    //     double v = 0;
    //     double theta = 0;
    //     return offsetVelocity(RobotPosition.getSpeakerRelativeVelocity(), v, theta);
    // }

    private double calculateSpeed(double distance){
        double v = RPM_to_MPS(currentState.speed);
        double theta = this.angle;
        return MPS_to_RPM(offsetVelocity(RobotPosition.getSpeakerRelativeVelocity(), v, theta));
    }    
    
    // private double calculateFeedSpeed(double distance){
    //     return 0;
    // }

    private double calculateAngle(double distance){
        double v = RPM_to_MPS(currentState.speed);
        double theta = 2.634 * distance * distance - 21.619 * distance + 73.023; //angle in degrees as given in team498/notebook/shooter_model.ipynb
        return offsetAngle(RobotPosition.getSpeakerRelativeVelocity(), v, theta);
    }

    private double offsetAngle(double r, double v, double theta) {
        return Units.radiansToDegrees(Math.atan((v * Math.sin(Units.degreesToRadians(theta))) / (v * Math.cos(Units.degreesToRadians(theta)) - r)));
    }
    private double offsetVelocity(double r, double v, double theta) {
        return Math.sqrt(Math.pow(v * Math.sin(Units.degreesToRadians(theta)), 2) + Math.pow(v * Math.cos(Units.degreesToRadians(theta)) - r, 2));
    }
    
    // Using static instances to reference the flywheel object ensures that we only use ONE FLywheel throughout the code 
    // This makes it very easy to access the flywheel object
    private static Shooter instance;

    public static Shooter getInstance() {
        if (instance == null) instance = new Shooter(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
