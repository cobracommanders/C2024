package org.team498.C2024.subsystems.hopper;

import org.team498.C2024.config.RobotConfig;
import org.team498.C2024.subsystems.Hopper;
import org.team498.lib.util.StateMachine;
import org.team498.lib.util.lifecycle.SubsystemPriority;

import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;

public class HopperSubsystem extends StateMachine<HopperStateEnum> {
  private final TalonFX mainMotor;

  public HopperSubsystem(TalonFX mainMotor) {
    super(SubsystemPriority.HOPPER, HopperStateEnum.IDLE);

    this.mainMotor = mainMotor;
    mainMotor.getConfigurator().apply(RobotConfig.get().intake().mainMotorConfig());
  }

  public void setState(HopperStateEnum newState) {
    setStateFromRequest(newState);
  }

  public double getIntakeRotations() {
    return mainMotor.getRotorPosition().getValueAsDouble();
  }

  @Override
  protected HopperStateEnum getNextState(HopperStateEnum currentState) {
    return currentState;
  }

  @Override
  protected void afterTransition(HopperStateEnum newState) {
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
