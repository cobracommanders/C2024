package org.team498.C2024.subsystems;

import org.team498.C2024.Ports;
import org.team498.C2024.State;
import org.team498.lib.drivers.LazySparkMax;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.CAN;
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
    private final CANSparkMax motor; // Declaration for a NEO or NEO550 brushless motor
    private final RelativeEncoder encoder; //Declaration for a Built-in NEO/NEO550 encoder

    // Variables will store the current properties of the subsystem
    private double setpoint;
    private State.Hopper currentState;
    
    // Constructor: Configure Motor Controller settings and  
    // Instantiate all objects (assign values to every variable and object)
    public Hopper() {
        motor = new CANSparkMax(Ports.HopperPorts.MOTOR, MotorType.kBrushless);
        encoder = motor.getEncoder(); //this can be left or right motor, whichever is most convenient
        
        // reset motor defaults to ensure all settings are clear
        motor.restoreFactoryDefaults();

        // Instantiate variables to intitial values
        setpoint = 0;
        currentState = State.Hopper.IDLE;
    }

    // This method will run every 10-20 milliseconds (about 50-100 times in one second)
    @Override
    public void periodic() {
        // This condition will reduce CPU utilization when the motor is not meant to run and save power because 
        // it will not actively deccelerate the wheel
        double speed = setpoint;
        set(speed);
    }

    // Getter method to retrieve current State
    public State.Hopper getState() {
        return currentState;
    }

    // Every subsystem has a setState() method that configures local properties to match the desired state
    public void setState(State.Hopper state) {
        currentState = state; // update state
        setpoint = state.speed; // update setpoint
    }

    public boolean atSetpoint(){
        double currentSpeed = encoder.getVelocity();
        double threshold = setpoint;
        return Math.abs(currentSpeed - setpoint) <=threshold;
    }

    // We do NOT use the preset methods for following and inverting motors in case of flash failure 
    // (Ask Caleb about that if you're curious)
    // Use a method to define motor control in relevant groups
    private void set(double speed) {
        motor.set(speed);
    }
    
    // Using static instances to reference the flywheel object ensures that we only use ONE FLywheel throughout the code 
    // This makes it very easy to access the flywheel object
    private static Hopper instance;

    public static Hopper getInstance() {
        if (instance == null) instance = new Hopper(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
