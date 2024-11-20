package org.team498.C2024.subsystems.IntakeStateMachine;

import org.team498.C2024.config.RobotConfig;
import org.team498.lib.util.StateMachine;
import org.team498.lib.util.lifecycle.SubsystemPriority;

import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;

public class IntakeSubsystem extends StateMachine<IntakeStateEnum> {
  private final TalonFX mainMotor;

  public IntakeSubsystem(TalonFX mainMotor, CANSparkMax centeringMotor) {
    super(SubsystemPriority.INTAKE, IntakeStateEnum.IDLE);

    this.mainMotor = mainMotor;
    mainMotor.getConfigurator().apply(RobotConfig.get().intake().mainMotorConfig());
  }

  public void setState(IntakeStateEnum newState) {
    setStateFromRequest(newState);
  }

  public double getIntakeRotations() {
    return mainMotor.getRotorPosition().getValueAsDouble();
  }

  @Override
  protected IntakeStateEnum getNextState(IntakeStateEnum currentState) {
    return currentState;
  }

  @Override
  protected void afterTransition(IntakeStateEnum newState) {
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
