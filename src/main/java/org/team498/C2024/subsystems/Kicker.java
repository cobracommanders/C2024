package org.team498.C2024.subsystems;

import org.team498.C2024.Ports;
import org.team498.C2024.State;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Kicker extends SubsystemBase {

    private final CANSparkMax motor;

    private double setpoint;
    private State.Kicker currentState;
    
    // Constructor: Configure Motor Controller settings and  
    // Instantiate all objects (assign values to every variable and object)
    public Kicker() {
        motor = new CANSparkMax(Ports.KickerPorts.MOTOR, MotorType.kBrushless);

        // Instantiate variables to intitial values
        setpoint = 0;
        currentState = State.Kicker.IDLE;

        // reset motor defaults to ensure all settings are clear
        motor.restoreFactoryDefaults();
    }

    @Override
    public void periodic() {
        //updates setpoint
        double speed = setpoint;
        set(speed);
    }

    //sets motor speeds
    private void set(double speed) {
        motor.set(-speed);
    }

    /**
     * sets Kicker state
     * updates setpoint
     */
    public void setState(State.Kicker state) {
        currentState = state; // update state
        setpoint = state.speed; // update setpoint
    }

    // Getter method to retrieve current State
    public State.Kicker getState() {
        return currentState;
    }

    private static Kicker instance;

    public static Kicker getInstance() {
        if (instance == null) instance = new Kicker(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
