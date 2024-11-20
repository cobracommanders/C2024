package org.team498.C2024.subsystems.kicker;


import org.team498.C2024.config.RobotConfig;
import org.team498.C2024.subsystems.Kicker;
import org.team498.lib.util.StateMachine;
import org.team498.lib.util.lifecycle.SubsystemPriority;

import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;

public class KickerSubsystem extends StateMachine<KickerStateEnum> {
  private final TalonFX mainMotor;

  public KickerSubsystem(TalonFX mainMotor) {
    super(SubsystemPriority.KICKER, KickerStateEnum.IDLE);

    this.mainMotor = mainMotor;
    mainMotor.getConfigurator().apply(RobotConfig.get().intake().mainMotorConfig());
  }

  public void setState(KickerStateEnum newState) {
    setStateFromRequest(newState);
  }

  public double getIntakeRotations() {
    return mainMotor.getRotorPosition().getValueAsDouble();
  }

  @Override
  protected KickerStateEnum getNextState(KickerStateEnum currentState) {
    return currentState;
  }

  @Override
  protected void afterTransition(KickerStateEnum newState) {
    switch (newState) {
      case IDLE -> {
        mainMotor.disable();
      }

      case FORWARD -> {
        mainMotor.setVoltage(12);
      }

      case REVERSE -> {
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

