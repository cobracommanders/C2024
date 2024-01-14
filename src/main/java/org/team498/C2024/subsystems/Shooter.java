package org.team498.C2024.subsystems;

import org.team498.C2024.Constants;
import org.team498.C2024.Ports;
import org.team498.C2024.State;
import org.team498.lib.drivers.LazySparkMax;
import com.revrobotics.RelativeEncoder;
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
    private final LazySparkMax bottomMotor; // Declaration for a NEO or NEO550 brushless motor
    private final LazySparkMax topMotor; // We use left / right descriptors to make identification easier in testing and communication
    private final LazySparkMax angleMotor;
    private final RelativeEncoder encoder; //Declaration for a Built-in NEO/NEO550 encoder

    private final PIDController bottomController; //Declaration for a P Controller
    private final PIDController topController; 
    private final SimpleMotorFeedforward bottomFeedForward;
    private final SimpleMotorFeedforward topFeedForward;
    private final PIDController angleController;
    private final ArmFeedforward angleFeedForward;
    private final DutyCycle angleEncoder;

    // Variables will store the current properties of the subsystem
    private double topSpeed;
    private double bottomSpeed;
    private double angle;
    private State.Shooter currentState;
    
    // Constructor: Configure Motor Controller settings and  
    // Instantiate all objects (assign values to every variable and object)
    public Shooter() {
        bottomMotor = new LazySparkMax(Ports.ShooterPorts.BOTTOM_MOTOR, MotorType.kBrushless);
        topMotor = new LazySparkMax(Ports.ShooterPorts.TOP_MOTOR, MotorType.kBrushless);
        angleMotor = new LazySparkMax(Ports.ShooterPorts.ANGLE_MOTOR, MotorType.kBrushless);
        encoder = bottomMotor.getEncoder(); //this can be left or right motor, whichever is most convenient

        // Use the subsystems constants to instantiate PID and Feedforward
        bottomController = new PIDController(Constants.ShooterConstants.P,Constants.ShooterConstants.I, Constants.ShooterConstants.D);
        topController = new PIDController(Constants.ShooterConstants.P,Constants.ShooterConstants.I, Constants.ShooterConstants.D);
        bottomFeedForward = new SimpleMotorFeedforward(Constants.ShooterConstants.S, Constants.ShooterConstants.V, Constants.ShooterConstants.A);
        topFeedForward = new SimpleMotorFeedforward(Constants.ShooterConstants.S, Constants.ShooterConstants.V, Constants.ShooterConstants.A);
        angleController = new PIDController(Constants.ShooterConstants.AngleConstants.P, Constants.ShooterConstants.AngleConstants.I, Constants.ShooterConstants.AngleConstants.D);
        angleFeedForward = new ArmFeedforward(Constants.ShooterConstants.AngleConstants.S, Constants.ShooterConstants.AngleConstants.G, Constants.ShooterConstants.AngleConstants.V);
        angleEncoder = new DutyCycle(new DigitalInput(Ports.ShooterPorts.ANGLE_ENCODER));

        // reset motor defaults to ensure all settings are clear
        bottomMotor.restoreFactoryDefaults();
        topMotor.restoreFactoryDefaults();
        angleMotor.restoreFactoryDefaults();

        // Instantiate variables to intitial values
        topSpeed = State.Shooter.IDLE.topSpeed;
        bottomSpeed = State.Shooter.IDLE.bottomSpeed;
        angle = State.Shooter.IDLE.angle;
        currentState = State.Shooter.IDLE;
    }

    // This method will run every 10-20 milliseconds (about 50-100 times in one second)
    @Override
    public void periodic() {
        // This condition will reduce CPU utilization when the motor is not meant to run and save power because 
        // it will not actively deccelerate the wheel

        double topSpeed; // We will use this variable to keep track of our desired speed
        double bottomSpeed;
        double angleSpeed;
        bottomSpeed = bottomFeedForward.calculate(this.bottomSpeed); // adjust the setpoint to account for physical motor properties using feedforward
        topSpeed = topFeedForward.calculate(this.topSpeed);
        bottomSpeed = bottomController.calculate(encoder.getVelocity(), bottomSpeed); // adjust for feedback error using proportional gain
        topSpeed = topController.calculate(encoder.getVelocity(), topSpeed);
        set(topSpeed / Constants.ShooterConstants.MAX_RPM, bottomSpeed / Constants.ShooterConstants.MAX_RPM); // set the motor behavior using set() to interact with the controllers
        angleSpeed = angleFeedForward.calculate(angle, 0);
        angleSpeed = angleController.calculate(getAngle(), angleSpeed);
        setAngle(angleSpeed);
        // We divide by MAX_RPM to scale to {-1, 1}
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
        topSpeed = state.topSpeed; // update setpoint
        bottomSpeed = state.bottomSpeed;
        bottomController.setSetpoint(this.bottomSpeed); // update pController
        topController.setSetpoint(this.topSpeed);
    }

    // We do NOT use the preset methods for following and inverting motors in case of flash failure 
    // (Ask Caleb about that if you're curious)
    // Use a method to define motor control in relevant groups
    public void set(double topSpeed, double bottomSpeed) {
        bottomMotor.set(bottomSpeed);
        topMotor.set(-topSpeed); // invert speed on right side (assuming the motor is facing opposite the left)
    }
    public void setAngle(double speed){
        angleMotor.set(speed);
    }
    
    // Using static instances to reference the flywheel object ensures that we only use ONE FLywheel throughout the code 
    // This makes it very easy to access the flywheel object
    private static Shooter instance;

    public static Shooter getInstance() {
        if (instance == null) instance = new Shooter(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
