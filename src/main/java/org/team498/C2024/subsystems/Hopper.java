package org.team498.C2024.subsystems;

import org.team498.C2024.Ports;
import org.team498.C2024.State;
import org.team498.C2024.Constants.HopperConstants;
import org.team498.C2024.Ports.HopperPorts;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
/*
 * This is an example Flywheel subsystem that can be used for reference while writing your own subsystems :)
 * The mechanism uses two NEO brushless motors controlled by Spark Maxes
 * They are mounted opposite each other and power a single-wheel flywheel with velocity control
 * While this code is for a Flywheel, the ideas here can be used for any velocity-based subsystem
 */
public class Hopper extends SubsystemBase {
    // all variable/object declaration goes at the top of the class. 
    // They can be instantiated (given values) later, but they must be declared here

    // Motors will almost always be private because they will only be controlled using public methods. There should be NO global use where motors 
    private final CANSparkMax topMotor; // Declaration for a NEO or NEO550 brushless motor
    private final CANSparkMax bottomMotor;
    private final RelativeEncoder topEncoder; //Declaration for a Built-in NEO/NEO550 encoder
    private final RelativeEncoder bottomEncoder;
    private final PIDController pidController;
    private final DigitalInput beamBreak;
    private boolean beamBreakEnabled;
    private boolean pidEnabled;

    // Variables will store the current properties of the subsystem
    private double setpoint;
    private State.Hopper currentState;
    
    // Constructor: Configure Motor Controller settings and  
    // Instantiate all objects (assign values to every variable and object)
    public Hopper() {
        topMotor = new CANSparkMax(Ports.HopperPorts.TOP_MOTOR, MotorType.kBrushless);
        bottomMotor = new CANSparkMax(Ports.HopperPorts.BOTTOM_MOTOR, MotorType.kBrushless);
        topEncoder = topMotor.getEncoder(); //this can be left or right motor, whichever is most convenient
        bottomEncoder = bottomMotor.getEncoder();
        beamBreak = new DigitalInput(HopperPorts.BEAM_BREAk);
        pidController = new PIDController(HopperConstants.P, HopperConstants.I, HopperConstants.D);
        pidController.setTolerance(0.1);


        // Instantiate variables to intitial values
        setpoint = 0;
        currentState = State.Hopper.IDLE;
        pidEnabled = false;
        beamBreakEnabled = true;

        topMotor.restoreFactoryDefaults();
        bottomMotor.restoreFactoryDefaults();
    }

    // This method will run every 10-20 milliseconds (about 50-100 times in one second)
    @Override
    public void periodic() {
        if(pidController.atSetpoint()) pidEnabled = false;
        if(pidEnabled){
            set(pidController.calculate(bottomEncoder.getPosition()));
        }
        else{
            double speed = setpoint;
            set(speed);
        }
        // This condition will reduce CPU utilization when the motor is not meant to run and save power because 
        // it will not actively deccelerate the wheel 
    }

    private void set(double speed) {
        topMotor.set(speed);
        bottomMotor.set(speed);
    }

     public void setState(State.Hopper state) {
        currentState = state; // update state
        setpoint = state.speed; // update setpoint
    }

    // Getter method to retrieve current State
    public State.Hopper getState() {
        return currentState;
    }

    public boolean atSetpoint(){
        return pidController.atSetpoint();
    }

    public void setPosition(double position){
        bottomEncoder.setPosition(0);
        pidEnabled = true;
        pidController.setSetpoint(position);
    }

    public boolean getBeamBreak(){
        return beamBreak.get();
    }
    public boolean isBeamBreakEnabled(){
        return beamBreakEnabled;
    }

    public void setBeamBreakEnabled(boolean isEnabled){
        beamBreakEnabled = isEnabled;
    }
    
    // Using static instances to reference the flywheel object ensures that we only use ONE FLywheel throughout the code 
    // This makes it very easy to access the flywheel object
    private static Hopper instance;

    public static Hopper getInstance() {
        if (instance == null) instance = new Hopper(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
