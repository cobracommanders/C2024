package org.team498.C2024.subsystems;

import org.team498.C2024.Ports;
import org.team498.C2024.State;
import org.team498.C2024.Constants.IntakeConstants;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

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
public class Intake extends SubsystemBase {
    // all variable/object declaration goes at the top of the class. 
    // They can be instantiated (given values) later, but they must be declared here

    // Motors will almost always be private because they will only be controlled using public methods. There should be NO global use where motors 
    private final CANSparkMax motor; // Declaration for a NEO or NEO550 brushless motor
    private final RelativeEncoder encoder; //Declaration for a Built-in NEO/NEO550 encoder
    private final DutyCycle angleEncoder;

    private final PIDController pidController; //Declaration for a P Controller
    private final ArmFeedforward feedforward;

    // Variables will store the current properties of the subsystem
    private double setpoint;
    private State.Intake currentState;
    
    // Constructor: Configure Motor Controller settings and  
    // Instantiate all objects (assign values to every variable and object)
    public Intake() {
        // motor = new LazySparkMax(Ports.IntakePorts.LMOTOR, MotorType.kBrushless);
        motor = new CANSparkMax(Ports.IntakePorts.LMOTOR, MotorType.kBrushless);
        encoder = motor.getEncoder(); //this can be left or right motor, whichever is most convenient
        angleEncoder = new DutyCycle(new DigitalInput(Ports.IntakePorts.ANGLE_ENCODER));

        // Use the subsystems constants to instantiate PID and Feedforward
        pidController = new PIDController(IntakeConstants.P, IntakeConstants.I, IntakeConstants.D);
        feedforward = new ArmFeedforward(IntakeConstants.S, IntakeConstants.G, IntakeConstants.V);

        // reset motor defaults to ensure all settings are clear
        motor.restoreFactoryDefaults();

        // Instantiate variables to intitial values
        currentState = State.Intake.IDLE;
    }

    // This method will run every 10-20 milliseconds (about 50-100 times in one second)
    @Override
    public void periodic() {
        // This condition will reduce CPU utilization when the motor is not meant to run and save power because 
        // it will not actively deccelerate the wheel
        double speed; // We will use this variable to keep track of our desired speed
        speed = pidController.calculate(getAngle(), this.setpoint) + feedforward.calculate(getAngle(),0); // adjust for feedback error using proportional gain
        set(speed); // set the motor behavior using set() to interact with the controllers
        // We divide by MAX_RPM to scale to {-1, 1}
    }

    // Getter method to retrieve current State
    public State.Intake getState() {
        return currentState;
    }

    // Every subsystem has a setState() method that configures local properties to match the desired state
    public void setState(State.Intake state) {
        currentState = state;
        setpoint = state.speed; // update state
        pidController.setSetpoint(this.setpoint); // update pController
    }
    
    public double getAngle() {
        return angleEncoder.getOutput();
    }

    public boolean atSetpoint(){
        return pidController.atSetpoint();
    
    }

    // We do NOT use the preset methods for following and inverting motors in case of flash failure 
    // (Ask Caleb about that if you're curious)
    // Use a method to define motor control in relevant groups
    public void set(double speed) {
        motor.set(speed);
    }
    
    // Using static instances to reference the flywheel object ensures that we only use ONE FLywheel throughout the code 
    // This makes it very easy to access the flywheel object
    private static Intake instance;

    public static Intake getInstance() {
        if (instance == null) instance = new Intake(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
