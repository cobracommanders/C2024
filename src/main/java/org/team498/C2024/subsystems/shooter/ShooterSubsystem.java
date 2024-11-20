package org.team498.C2024.subsystems.shooter;

import org.team498.C2024.config.RobotConfig;
import org.team498.lib.util.StateMachine;
import org.team498.lib.util.lifecycle.SubsystemPriority;

import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;

public class ShooterSubsystem extends StateMachine<ShooterStateEnum> {
  private final TalonFX leftMotor;
  private final TalonFX rightMotor;
  private final TalonFX feedMotor;
  private final TalonFX angleMotor;

  public ShooterSubsystem(TalonFX leftMotor, TalonFX rightMotor, TalonFX feedMotor, TalonFX angleMotor) {
    super(SubsystemPriority.INTAKE, ShooterStateEnum.IDLE);

    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
    this.feedMotor = feedMotor;
    this.angleMotor = angleMotor;
    leftMotor.getConfigurator().apply(RobotConfig.get().shooter().leftMotorConfig());
    rightMotor.getConfigurator().apply(RobotConfig.get().shooter().rightMotorConfig());
    feedMotor.getConfigurator().apply(RobotConfig.get().shooter().feedMotorConfig());
    angleMotor.getConfigurator().apply(RobotConfig.get().shooter().angleMotorConfig());
  }

  public void setState(ShooterStateEnum newState) {
    setStateFromRequest(newState);
  }

  public double getShooterAngle() {
    return leftMotor.getRotorPosition().getValueAsDouble();
  }

  @Override
  protected ShooterStateEnum getNextState(ShooterStateEnum currentState) {
    return currentState;
  }

  @Override
  protected void afterTransition(ShooterStateEnum newState) {
    switch (newState) {
      case IDLE -> {
        leftMotor.disable();
      }

      case SUBWOOFER -> {
        leftMotor.setVoltage(12);
      }

      case CRESCENDO -> {
        leftMotor.setVoltage(-6);
      }
    }
  }

  @Override
  public void robotPeriodic() {
    super.robotPeriodic();
    // DogLog.log("Intake/StatorCurrent", mainMotor.getStatorCurrent().getValueAsDouble());
    // DogLog.log("Intake/SupplyCurrent", mainMotor.getSupplyCurrent().getValueAsDouble());
    // DogLog.log("Intake/AppliedVoltage", mainMotor.getMotorVoltage().getValueAsDouble());
  }
}

