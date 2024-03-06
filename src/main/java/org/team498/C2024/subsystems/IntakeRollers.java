package org.team498.C2024.subsystems;

import org.team498.C2024.Ports;
import org.team498.C2024.State;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeRollers extends SubsystemBase {

    private final CANSparkMax motor;
   // private final RelativeEncoder encoder;

    private double setpoint;
    private State.IntakeRollers currentState;
    
    // Constructor: Configure Motor Controller settings and  
    // Instantiate all objects (assign values to every variable and object)
    public IntakeRollers() {
        motor = new CANSparkMax(Ports.IntakeRollersPorts.MOTOR, MotorType.kBrushless);
        //encoder = motor.getEncoder(); //this can be left or right motor, whichever is most convenient

        // Instantiate variables to intitial values
        setpoint = 0;
        currentState = State.IntakeRollers.IDLE;

        // reset motor defaults to ensure all settings are clear
       
        // motor.setSmartCurrentLimit(50);
    }
    public void configMotors() {
        motor.restoreFactoryDefaults();
    }

    @Override
    public void periodic() {
        double speed = setpoint;
        set(speed);
    }

    //sets Motor Speed
    public void set(double speed) {
        motor.set(-speed);
    }


    /**
     * sets state of IntakeRollers
     * sets Setpoint to Speed
     */
    public void setState(State.IntakeRollers state) {
        currentState = state; // update state
        setpoint = state.speed; // update setpoint
    }

    // Getter method to retrieve current State
    public State.IntakeRollers getState() {
        return currentState;
    }
    
    private static IntakeRollers instance;

    public static IntakeRollers getInstance() {
        if (instance == null) instance = new IntakeRollers(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
