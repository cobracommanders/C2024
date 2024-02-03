package org.team498.C2024.subsystems;

import org.team498.C2024.Ports;
import org.team498.C2024.State;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
/*
 * This is an example Flywheel subsystem that can be used for reference while writing your own subsystems :)
 * The mechanism uses two NEO brushless motors controlled by Spark Maxes
 * They are mounted opposite each other and power a single-wheel flywheel with velocity control
 * While this code is for a Flywheel, the ideas here can be used for any velocity-based subsystem
 */
public class Kicker extends SubsystemBase {
    // all variable/object declaration goes at the top of the class. 
    // They can be instantiated (given values) later, but they must be declared here

    // Motors will almost always be private because they will only be controlled using public methods. There should be NO global use where motors 
    private final CANSparkMax motor; // Declaration for a NEO or NEO550 brushless motor

    // Variables will store the current properties of the subsystem
    private double setpoint;
    private State.Kicker currentState;
    
    // Constructor: Configure Motor Controller settings and  
    // Instantiate all objects (assign values to every variable and object)
    public Kicker() {
        motor = new CANSparkMax(Ports.HopperPorts.TOP_MOTOR, MotorType.kBrushless);

        // Instantiate variables to intitial values
        setpoint = 0;
        currentState = State.Kicker.IDLE;

        // reset motor defaults to ensure all settings are clear
        motor.restoreFactoryDefaults();
    }

    // This method will run every 10-20 milliseconds (about 50-100 times in one second)
    @Override
    public void periodic() {
            double speed = setpoint;
            set(speed);
        }


    private void set(double speed) {
        motor.set(speed);
    }

    public void setState(State.Kicker state) {
        currentState = state; // update state
        setpoint = state.speed; // update setpoint
    }

    // Getter method to retrieve current State
    public State.Kicker getState() {
        return currentState;
    }

    // Using static instances to reference the flywheel object ensures that we only use ONE FLywheel throughout the code 
    // This makes it very easy to access the flywheel object
    private static Kicker instance;

    public static Kicker getInstance() {
        if (instance == null) instance = new Kicker(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
