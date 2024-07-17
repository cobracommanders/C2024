// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Vision;

import java.util.function.Supplier;

import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Util.RectanglePoseArea;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.TunerConstants;

public class Limelight extends SubsystemBase {
  CommandSwerveDrivetrain drivetrain;
  Alliance alliance;
  private String ll = "limelight";
  private double tx = 0;
  private Boolean enable = false;
  private Boolean trust = false;
  private Supplier<SwerveRequest> request;

  private double MaxSpeed = TunerConstants.kSpeedAt12VoltsMps; // Initial max is true top speed
  private final double TurtleSpeed = 0.1; // Reduction in speed from Max Speed, 0.1 = 10%
  private final double MaxAngularRate = Math.PI * 1.5; // .75 rotation per second max angular velocity.  Adjust for max turning rate speed.
  private final double TurtleAngularRate = Math.PI * 0.5; // .75 rotation per second max angular velocity.  Adjust for max turning rate speed.
  private double AngularRate = MaxAngularRate; // This will be updated when turtle and reset to MaxAngularRate
  
  SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
    .withDriveRequestType(DriveRequestType.OpenLoopVoltage)
    .withDeadband(MaxSpeed * 0.1) // Deadband is handled on input
    .withRotationalDeadband(AngularRate * 0.1);
  // private int fieldError = 0;
  // private int distanceError = 0;
  // private Pose2d botpose;
  // private static final RectanglePoseArea field =
  //       new RectanglePoseArea(new Translation2d(0.0, 0.0), new Translation2d(16.54, 8.02));

  /** Creates a new Limelight. */
  public Limelight(CommandSwerveDrivetrain drivetrain) {
    this.drivetrain = drivetrain;
    SmartDashboard.putNumber("tx", tx);
    // SmartDashboard.putNumber("Field Error", fieldError);
    // SmartDashboard.putNumber("Limelight Error", distanceError);
  }

  @Override
  public void periodic() {
    // tx = LimelightHelpers.getTX(ll);
    // double kP = 0.1;
    // while (Math.abs(tx) < 1) {
    //   request = () -> {
    //     return drive.withVelocityX(0) // left x * gas
    //         .withVelocityY(0) // Angle of left stick Y * gas pedal
    //         .withRotationalRate(kP * tx); // Drive counterclockwise with negative X (left)
    //   }
    }



    // if (enable) {
    //   Double targetDistance = LimelightHelpers.getTargetPose3d_CameraSpace(ll).getTranslation().getDistance(new Translation3d());
    //   Double confidence = 1 - ((targetDistance - 1) / 6);
    //   LimelightHelpers.Results result =
    //       LimelightHelpers.getLatestResults(ll).targetingResults;
    //   if (result.valid) {
    //     botpose = LimelightHelpers.getBotPose2d_wpiBlue(ll);
    //     if (field.isPoseWithinArea(botpose)) {
    //       if (drivetrain.getState().Pose.getTranslation().getDistance(botpose.getTranslation()) < 0.5
    //           || trust
    //           || result.targets_Fiducials.length > 1) {
    //         drivetrain.addVisionMeasurement(
    //             botpose,
    //             Timer.getFPGATimestamp()
    //                 - (result.latency_capture / 1000.0)
    //                 - (result.latency_pipeline / 1000.0),
    //             VecBuilder.fill(confidence, confidence, .01));
    //       } else {
    //         distanceError++;
    //         SmartDashboard.putNumber("Limelight Error", distanceError);
    //       }
    //     } else {
    //       fieldError++;
    //       SmartDashboard.putNumber("Field Error", fieldError);
    //     }
    //   }
    // }
  // public Command alignToTag() {
  //   // tx = LimelightHelpers.getTX(ll);
  //   // double kp = 0.1;
  //   // double speed = kp * tx;
  // return drivetrain.applyRequest(request);
  // }

public Command alignToTag(){
  tx = LimelightHelpers.getTX(ll);
  double kp = 0.3;
  double speed = kp * tx;
    return drivetrain.applyRequest(request);
  }

  public void setAlliance(Alliance alliance) {
    this.alliance = alliance;
  }

  public void useLimelight(boolean enable) {
    this.enable = enable;
  }

  public void trustLL(boolean trust) {
    this.trust = trust;
  }
}