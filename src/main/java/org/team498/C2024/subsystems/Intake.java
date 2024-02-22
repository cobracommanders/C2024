package org.team498.C2024.subsystems;

import org.team498.C2024.Constants;
import org.team498.C2024.Ports;
import org.team498.C2024.State;
import org.team498.C2024.Constants.IntakeConstants;
import org.team498.lib.wpilib.ChassisSpeeds;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.SparkAbsoluteEncoder.Type;
import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkAbsoluteEncoder;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {

    private final CANSparkMax motor;
    private final SparkAbsoluteEncoder angleEncoder;

    private final PIDController pidController;
    private final ArmFeedforward gravityFeedforward;
    private final SimpleMotorFeedforward driveFeedforward;
    private final SimpleMotorFeedforward rotationFeedforward;

    private boolean isManual;
    private double manualSpeed;
    
    private State.Intake currentState;
    private double setpoint;

    private boolean isActivated = false;
    
    // Constructor: Configure Motor Controller settings and  
    // Instantiate all objects (assign values to every variable and object)
    public Intake() {
        // motor = new LazySparkMax(Ports.IntakePorts.LMOTOR, MotorType.kBrushless);
        motor = new CANSparkMax(Ports.IntakePorts.MOTOR, MotorType.kBrushless);
        angleEncoder = motor.getAbsoluteEncoder(Type.kDutyCycle);

        pidController = new PIDController(IntakeConstants.P, IntakeConstants.I, IntakeConstants.D);
        gravityFeedforward = new ArmFeedforward(IntakeConstants.S, IntakeConstants.G, IntakeConstants.V);
        driveFeedforward = new SimpleMotorFeedforward(0, IntakeConstants.dV, 0);
        rotationFeedforward = new SimpleMotorFeedforward(0, IntakeConstants.rV, 0);

        // Instantiate variables to intitial values
        currentState = State.Intake.IDLE;
        setpoint = currentState.speed;

        pidController.setTolerance(0.15);
        pidController.reset();
        pidController.setSetpoint(setpoint);

        // reset motor defaults to ensure all settings are clear
        motor.restoreFactoryDefaults();
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Intake Position", getPosition());
        SmartDashboard.putBoolean("Intake atSetpoint", pidController.atSetpoint());
        SmartDashboard.putNumber("Manual Intake Speed", manualSpeed);
        // We will use this variable to keep track of our desired speed
        double speed = 0;
        if (isActivated) {
            ChassisSpeeds driveSpeeds = Drivetrain.getInstance().getCurrentSpeeds();

            double initialPID = pidController.calculate(getPosition(), this.setpoint);
            double gravityOffset = gravityFeedforward.calculate(getPosition(), 0);
            double driveOffset = driveFeedforward.calculate(driveSpeeds.vyMetersPerSecond);
            double rotationOffset = rotationFeedforward.calculate(Math.abs(driveSpeeds.omegaRadiansPerSecond));

            speed = initialPID + gravityOffset + driveOffset + rotationOffset; // adjust for feedback error using proportional gain
        } 
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
        isActivated = true;
        currentState = state;
        setpoint = state.speed; // update state
        pidController.setSetpoint(this.setpoint); // update pController
    }

    public void setPositionManual(boolean isManual, double speed){
        this.isManual = isManual;
        this.manualSpeed = speed / 5;
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
    public double getPosition() {
        return angleEncoder.getPosition() * Math.PI * 2 - Constants.IntakeConstants.ENCODER_OFFSET;
    }
    
    // Using static instances to reference the flywheel object ensures that we only use ONE FLywheel throughout the code 
    // This makes it very easy to access the flywheel object
    private static Intake instance;

    public static Intake getInstance() {
        if (instance == null) instance = new Intake(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
