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

public class Intake extends SubsystemBase {

    private final CANSparkMax motor;
    private final RelativeEncoder encoder;
    private final DutyCycle angleEncoder;

    private final PIDController pidController;
    private final ArmFeedforward feedforward;
    private boolean isManual;
    private double manualSpeed;
    
    private State.Intake currentState;
    private double setpoint;
    
    // Constructor: Configure Motor Controller settings and  
    // Instantiate all objects (assign values to every variable and object)
    public Intake() {
        // motor = new LazySparkMax(Ports.IntakePorts.LMOTOR, MotorType.kBrushless);
        motor = new CANSparkMax(Ports.IntakePorts.LMOTOR, MotorType.kBrushless);
        encoder = motor.getEncoder(); //this can be left or right motor, whichever is most convenient
        angleEncoder = new DutyCycle(new DigitalInput(Ports.IntakePorts.ANGLE_ENCODER));

        pidController = new PIDController(IntakeConstants.P, IntakeConstants.I, IntakeConstants.D);
        feedforward = new ArmFeedforward(IntakeConstants.S, IntakeConstants.G, IntakeConstants.V);

        // Instantiate variables to intitial values
        currentState = State.Intake.IDLE;

        pidController.setTolerance(0.01);

        // reset motor defaults to ensure all settings are clear
        motor.restoreFactoryDefaults();
    }

    @Override
    public void periodic() {
        // We will use this variable to keep track of our desired speed
        double speed;
        speed = pidController.calculate(getAngle(), this.setpoint) + feedforward.calculate(getAngle(),0); // adjust for feedback error using proportional gain
        if (isManual) speed = manualSpeed;
        set(speed);
    }

    /**
     * sets Motor Speed
     */
    public void set(double speed) {
        motor.set(speed);
    }

    /**
     * sets state for speed and sets PID controller to setpoint
     */
    public void setState(State.Intake state) {
        currentState = state;
        setpoint = state.speed; // update state
        pidController.setSetpoint(this.setpoint); // update pController
    }

    public void setAngleManual(boolean isManual, double speed){
        this.isManual = isManual;
        this.manualSpeed = speed;
    }

    // Getter method to retrieve current State
    public State.Intake getState() {
        return currentState;
    }

    /**
     *returns PID controller when it reaches setpoint
     */
    public boolean atSetpoint(){
        return pidController.atSetpoint();
    }

    /**
     * returns encoder angle
     */
    public double getAngle() {
        return angleEncoder.getOutput();
    }
    
    // Using static instances to reference the flywheel object ensures that we only use ONE FLywheel throughout the code 
    // This makes it very easy to access the flywheel object
    private static Intake instance;

    public static Intake getInstance() {
        if (instance == null) instance = new Intake(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
