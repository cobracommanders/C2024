package org.team498.C2024.config;


import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.mechanisms.swerve.utility.PhoenixPIDController;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import java.util.function.Consumer;

import org.team498.C2024.State.Shooter;

public record RobotConfig(
    String robotName,
    IntakeConfig intake,
    ShooterConfig shooter){


  public record IntakeConfig(
      int mainMotorID,
      String mainMotorCanBusName,
      TalonFXConfiguration mainMotorConfig) {}

  public record ShooterConfig(
    int leftMotorID,
    int rightMotorID,
    int feedMotorID,
    int angleMotorID,
    String leftMotorCanBusName,
    String rightMotorCanBusName,
    String feedMotorCanBusName,
    String angleMotorCanBusNamem,
    TalonFXConfiguration leftMotorConfig,
    TalonFXConfiguration rightMotorConfig,
    TalonFXConfiguration feedMotorConfig,
    TalonFXConfiguration angleMotorConfig){}


  public record LightsConfig(String canBusName, int deviceID) {}

  // TODO: Change this to false during events
  public static final boolean IS_DEVELOPMENT = true;
  public static final String SERIAL_NUMBER = System.getenv("serialnum");

  public static RobotConfig get() {
   return CompConfig.competitionBot;
  }
}