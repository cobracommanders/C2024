package org.team498.C2024.config;

import org.team498.C2024.config.RobotConfig.IntakeConfig;
import org.team498.C2024.config.RobotConfig.ShooterConfig;

import com.ctre.phoenix6.configs.ClosedLoopRampsConfigs;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.VoltageConfigs;
import com.ctre.phoenix6.mechanisms.swerve.utility.PhoenixPIDController;
import com.ctre.phoenix6.signals.GravityTypeValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.math.util.Units;

class CompConfig {
  private static final String CANIVORE_NAME = "498CANivore";
  private static final String RIO_CAN_NAME = "rio";

  private static final ClosedLoopRampsConfigs CLOSED_LOOP_RAMP =
      new ClosedLoopRampsConfigs()
          .withDutyCycleClosedLoopRampPeriod(0.04)
          .withTorqueClosedLoopRampPeriod(0.04)
          .withVoltageClosedLoopRampPeriod(0.04);
  private static final OpenLoopRampsConfigs OPEN_LOOP_RAMP =
      new OpenLoopRampsConfigs()
          .withDutyCycleOpenLoopRampPeriod(0.04)
          .withTorqueOpenLoopRampPeriod(0.04)
          .withVoltageOpenLoopRampPeriod(0.04);

  public static final RobotConfig competitionBot =
      new RobotConfig(
          "competition",
          
          new IntakeConfig(
              19,
              CANIVORE_NAME,
              new TalonFXConfiguration()
                  .withClosedLoopRamps(CLOSED_LOOP_RAMP)
                  .withOpenLoopRamps(OPEN_LOOP_RAMP)
                  .withCurrentLimits(
                      new CurrentLimitsConfigs()
                          .withStatorCurrentLimit(20)
                          .withSupplyCurrentLimit(25))
                  .withSlot0(
                      new Slot0Configs().withKV(0).withKP(1.0).withKI(0).withKD(0).withKG(0))
             ),
             new ShooterConfig(
              1,
              2,
              3,
              4,
              CANIVORE_NAME,
              CANIVORE_NAME,
              CANIVORE_NAME,
              CANIVORE_NAME,
              new TalonFXConfiguration()
                  .withClosedLoopRamps(CLOSED_LOOP_RAMP)
                  .withOpenLoopRamps(OPEN_LOOP_RAMP)
                  .withCurrentLimits(
                      new CurrentLimitsConfigs()
                          .withStatorCurrentLimit(20)
                          .withSupplyCurrentLimit(25))
                  .withSlot0(
                      new Slot0Configs().withKV(0).withKP(1.0).withKI(0).withKD(0).withKG(0)),
               new TalonFXConfiguration()
                  .withClosedLoopRamps(CLOSED_LOOP_RAMP)
                  .withOpenLoopRamps(OPEN_LOOP_RAMP)
                  .withCurrentLimits(
                      new CurrentLimitsConfigs()
                          .withStatorCurrentLimit(20)
                          .withSupplyCurrentLimit(25))
                  .withSlot0(
                      new Slot0Configs().withKV(0).withKP(1.0).withKI(0).withKD(0).withKG(0)),
                new TalonFXConfiguration()
                  .withClosedLoopRamps(CLOSED_LOOP_RAMP)
                  .withOpenLoopRamps(OPEN_LOOP_RAMP)
                  .withCurrentLimits(
                      new CurrentLimitsConfigs()
                          .withStatorCurrentLimit(20)
                          .withSupplyCurrentLimit(25))
                  .withSlot0(
                      new Slot0Configs().withKV(0).withKP(1.0).withKI(0).withKD(0).withKG(0)),
                new TalonFXConfiguration()
                  .withClosedLoopRamps(CLOSED_LOOP_RAMP)
                  .withOpenLoopRamps(OPEN_LOOP_RAMP)
                  .withCurrentLimits(
                      new CurrentLimitsConfigs()
                          .withStatorCurrentLimit(20)
                          .withSupplyCurrentLimit(25))
                  .withSlot0(
                      new Slot0Configs().withKV(0).withKP(1.0).withKI(0).withKD(0).withKG(0))
             ));

  private CompConfig() {}
}
