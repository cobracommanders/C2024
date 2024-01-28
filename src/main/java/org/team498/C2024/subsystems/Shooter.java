package org.team498.C2024.subsystems;

import org.team498.C2024.Constants;
import org.team498.C2024.Ports;
import org.team498.C2024.RobotPosition;
import org.team498.C2024.State;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycle;
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
    private final CANSparkMax kickerMotor;

    private final PIDController rightControllere; //Declaration for a P Controller
    private final PIDController leftController; 
    private final SimpleMotorFeedforward rightFeedForward;
    private final SimpleMotorFeedforward leftFeedForward;
    private final PIDController angleController;
    private final ArmFeedforward angleFeedForward;
    private final DutyCycle angleEncoder;

    // Variables will store the current properties of the subsystem
    private double rightSpeed;
    private double leftSpeed;
    private double angle;
    private double kickerSpeed;
    private State.Shooter currentState;
    
    // Constructor: Configure Motor Controller settings and  
    // Instantiate all objects (assign values to every variable and object)
    public Shooter() {
        leftMotor = new TalonFX(Ports.ShooterPorts.LEFT_MOTOR);
        rightMotor = new TalonFX(Ports.ShooterPorts.RIGHT_MOTOR);
        kickerMotor = new CANSparkMax(Ports.ShooterPorts.KICKER_MOTOR, MotorType.kBrushless);
        angleMotor = new CANSparkMax(Ports.ShooterPorts.ANGLE_MOTOR, MotorType.kBrushless); //this can be left or right motor, whichever is most convenient

        // Use the subsystems constants to instantiate PID and Feedforward
        rightControllere = new PIDController(Constants.ShooterConstants.P,Constants.ShooterConstants.I, Constants.ShooterConstants.D);
        leftController = new PIDController(Constants.ShooterConstants.P,Constants.ShooterConstants.I, Constants.ShooterConstants.D);
        rightFeedForward = new SimpleMotorFeedforward(Constants.ShooterConstants.S, Constants.ShooterConstants.V, Constants.ShooterConstants.A);
        leftFeedForward = new SimpleMotorFeedforward(Constants.ShooterConstants.S, Constants.ShooterConstants.V, Constants.ShooterConstants.A);
        angleController = new PIDController(Constants.ShooterConstants.AngleConstants.P, Constants.ShooterConstants.AngleConstants.I, Constants.ShooterConstants.AngleConstants.D);
        angleFeedForward = new ArmFeedforward(Constants.ShooterConstants.AngleConstants.S, Constants.ShooterConstants.AngleConstants.G, Constants.ShooterConstants.AngleConstants.V);
        angleEncoder = new DutyCycle(new DigitalInput(Ports.ShooterPorts.ANGLE_ENCODER));

        // reset motor defaults to ensure all settings are clear
        leftMotor.getConfigurator().apply(new TalonFXConfiguration());
        rightMotor.getConfigurator().apply(new TalonFXConfiguration());
        angleMotor.restoreFactoryDefaults();

        // Instantiate variables to intitial values
        rightSpeed = State.Shooter.IDLE.topSpeed;
        leftSpeed = State.Shooter.IDLE.bottomSpeed;
        angle = State.Shooter.IDLE.angle;
        currentState = State.Shooter.IDLE;
    }

    // This method will run every 10-20 milliseconds (about 50-100 times in one second)
    @Override
    public void periodic() {
        // This condition will reduce CPU utilization when the motor is not meant to run and save power because 
        // it will not actively deccelerate the wheel
        if (currentState == State.Shooter.CRESCENDO){
            this.leftSpeed = calculateBottomSpeed(RobotPosition.distanceToSpeaker());
            this.rightSpeed = calculateTopSpeed(RobotPosition.distanceToSpeaker());
            this.angle = calculateAngle(RobotPosition.distanceToSpeaker());
        }

        double leftShooterSpeed; // We will use this variable to keep track of our desired speed
        double rightShooterSpeed;
        double angleSpeed;
        rightShooterSpeed = rightFeedForward.calculate(this.leftSpeed); // adjust the setpoint to account for physical motor properties using feedforward
        leftShooterSpeed = leftFeedForward.calculate(this.rightSpeed);
        rightShooterSpeed = rightControllere.calculate(rightMotor.getVelocity().getValueAsDouble(), this.rightSpeed); // adjust for feedback error using proportional gain
        leftShooterSpeed = leftController.calculate(leftMotor.getVelocity().getValueAsDouble(), this.leftSpeed);
        set(leftShooterSpeed / Constants.ShooterConstants.MAX_RPM, rightShooterSpeed / Constants.ShooterConstants.MAX_RPM); // set the motor behavior using set() to interact with the controllers
        angleSpeed = angleController.calculate(getAngle(), this.angle) + angleFeedForward.calculate(angle, 0);
        setAngle(angleSpeed);
        setKicker(kickerSpeed);
        // We divide by MAX_RPM to scale to {-1, 1}
    }

    private void setKicker(double speed){
        kickerMotor.set(speed);
    }

    // Getter method to retrieve current State
    public State.Shooter getState() {
        return currentState;
    }

    public double getAngle() {
        return angleEncoder.getOutput();
    }

    // Every subsystem has a setState() method that configures local properties to match the desired state
    public void setState(State.Shooter state) {
        currentState = state; // update state
        rightSpeed = state.topSpeed; // update setpoint
        leftSpeed = state.bottomSpeed;
        angle = state.angle;
        rightControllere.setSetpoint(this.leftSpeed); // update pController
        leftController.setSetpoint(this.rightSpeed);
        angleController.setSetpoint(this.angle);
    }

    public boolean atSetpoint(){
        return leftController.atSetpoint() && rightControllere.atSetpoint() && angleController.atSetpoint();
    }

    // We do NOT use the preset methods for following and inverting motors in case of flash failure 
    // (Ask Caleb about that if you're curious)
    // Use a method to define motor control in relevant groups
    private void set(double topSpeed, double bottomSpeed) {
        leftMotor.set(bottomSpeed);
        rightMotor.set(-topSpeed); // invert speed on right side (assuming the motor is facing opposite the left)
    }
    private void setAngle(double speed){
        angleMotor.set(speed);
    }

    private double calculateAngle(double distance){
        return 0;
    }

    private double calculateTopSpeed(double distance){
        return 0;
    }

    private double calculateBottomSpeed(double distance){
        return 0;
    }
    
    // Using static instances to reference the flywheel object ensures that we only use ONE FLywheel throughout the code 
    // This makes it very easy to access the flywheel object
    private static Shooter instance;

    public static Shooter getInstance() {
        if (instance == null) instance = new Shooter(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
