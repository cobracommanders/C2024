package org.team498.C2024.subsystems.intakeRollers;


import org.team498.C2024.config.RobotConfig;
import org.team498.lib.util.StateMachine;
import org.team498.lib.util.lifecycle.SubsystemPriority;

import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;

public class IntakeRollersSubsystem extends StateMachine<IntakeRollersStateEnum> {
  private final TalonFX mainMotor;

  public IntakeRollersSubsystem(TalonFX mainMotor) {
    super(SubsystemPriority.INTAKEROLLERS, IntakeRollersStateEnum.IDLE);

    this.mainMotor = mainMotor;
    mainMotor.getConfigurator().apply(RobotConfig.get().intake().mainMotorConfig());
  }

  public void setState(IntakeRollersStateEnum newState) {
    setStateFromRequest(newState);
  }

  public double getIntakeRollersRotations() {
    return mainMotor.getRotorPosition().getValueAsDouble();
  }

  @Override
  protected IntakeRollersStateEnum getNextState(IntakeRollersStateEnum currentState) {
    return currentState;
  }

  @Override
  protected void afterTransition(IntakeRollersStateEnum newState) {
    switch (newState) {
      case IDLE -> {
        mainMotor.disable();
      }

      case INTAKE -> {
        mainMotor.setVoltage(12);
      }

      case OUTTAKE -> {
        mainMotor.setVoltage(-6);
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

