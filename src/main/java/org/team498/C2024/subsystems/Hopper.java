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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Hopper extends SubsystemBase {

    private final CANSparkMax topMotor;
    private final CANSparkMax bottomMotor;
    private final RelativeEncoder topEncoder;
    private final RelativeEncoder bottomEncoder;
    private final PIDController pidController;
    private final DigitalInput beamBreak;
    private boolean beamBreakEnabled;
    private boolean pidEnabled;

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
    }

    public void configMotors() {
        topMotor.restoreFactoryDefaults();
        bottomMotor.restoreFactoryDefaults();
    }

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
        SmartDashboard.putBoolean("Beambreak",getBeamBreak());
    }

    public void set(double speed) {
        topMotor.set(speed * 1.25);
        bottomMotor.set(speed);
    }

     public void setState(State.Hopper state) {
        // update state
        currentState = state;
        // update setpoint
        setpoint = state.speed;
    }

    // Getter method to retrieve current State
    public State.Hopper getState() {
        return currentState;
    }

    public boolean isPidEnabled(){
        return pidEnabled;
    }

    /**
     * returns true if pidController is at setpoint
     */
    public boolean atSetpoint(){
        return pidController.atSetpoint();
    }

    /**
     * resets encoder, enables PID and sets PID setpoint
     */
    public void setPosition(double position){
        bottomEncoder.setPosition(0);
        pidEnabled = true;
        pidController.setSetpoint(position);
    }

    /**
     * return true if beambreak sensor is active
     */
    public boolean getBeamBreak(){
        return !beamBreak.get();
    }

    /**
     * returns true if the beambreak is enabled
     */
    public boolean isBeamBreakEnabled(){
        return beamBreakEnabled;
    }

    /**
     * enables or disables beambreak
     * @param isEnabled true if enabling
     */
    public void setBeamBreakEnabled(boolean isEnabled){
        beamBreakEnabled = isEnabled;
    }
    private static Hopper instance;

    public static Hopper getInstance() {
        if (instance == null) instance = new Hopper(); 
        return instance;
    }
}
