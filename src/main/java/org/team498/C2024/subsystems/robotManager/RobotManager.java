package org.team498.C2024.subsystems.robotManager;

import org.team498.C2024.State.Hopper;
import org.team498.C2024.subsystems.IntakeStateMachine.IntakeStateEnum;
import org.team498.C2024.subsystems.IntakeStateMachine.IntakeSubsystem;
import org.team498.C2024.subsystems.drivetrain.CommandSwerveDrivetrain;
import org.team498.C2024.subsystems.drivetrain.SwerveState;
import org.team498.C2024.subsystems.drivetrain.TunerConstants;
import org.team498.C2024.subsystems.hopper.HopperStateEnum;
import org.team498.C2024.subsystems.hopper.HopperSubsystem;
import org.team498.C2024.subsystems.intakeRollers.IntakeRollersStateEnum;
import org.team498.C2024.subsystems.intakeRollers.IntakeRollersSubsystem;
import org.team498.C2024.subsystems.shooter.ShooterStateEnum;
import org.team498.C2024.subsystems.shooter.ShooterSubsystem;
import org.team498.lib.util.StateMachine;
import org.team498.lib.util.lifecycle.SubsystemPriority;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public class RobotManager extends StateMachine<RobotState> {
  private static final double MINIMUM_SHOT_TIME = 0.5;
  public final ShooterSubsystem shooter;
  //public final VisionSubsystem vision;
  //public final ImuSubsystem imu;
  public final IntakeSubsystem intake;
  public final IntakeRollersSubsystem intakeRollers;
  public final CommandSwerveDrivetrain swerve;
  public final  HopperSubsystem hopper;
  private final Timer shotTimer = new Timer();

  private DistanceAngle fieldRelativeDistanceAngleToSpeaker = new DistanceAngle(0, 0, false);
  private DistanceAngle fieldRelativeDistanceAngleToFeedSpot = new DistanceAngle(0, 0, false);
  private boolean facingSpeakerAngle = false;
  private boolean facingFeedSpotAngle = false;

  private boolean confirmShotActive = false;

  public RobotManager(
      ShooterSubsystem shooter,
      //LocalizationSubsystem localization,
      //VisionSubsystem vision,
      //ImuSubsystem imu,
      IntakeSubsystem intake,
      IntakeRollersSubsystem intakeRollers,
      HopperSubsystem hopper,
      //QueuerSubsystem queuer,
      CommandSwerveDrivetrain swerve) {
    super(SubsystemPriority.ROBOT_MANAGER, RobotState.IDLE_NO_GP);
    this.shooter = shooter;
    //this.localization = localization;
    //this.vision = vision;
    //this.imu = imu;
    this.intake = intake;
    //this.queuer = queuer;
    this.swerve = swerve;

    this.hopper = hopper;
    this.intakeRollers = intakeRollers;
  }

  @Override
  protected void collectInputs() {
    // fieldRelativeDistanceAngleToSpeaker =
    //     localization.getFieldRelativeDistanceAngleToPose(FieldUtil.getSpeakerPose());
    // fieldRelativeDistanceAngleToFeedSpot =
    //     localization.getFieldRelativeDistanceAngleToPose(FieldUtil.getFeedSpotPose());

    var robotHeading = imu.getRobotHeading();

    facingSpeakerAngle =
        MathUtil.isNear(fieldRelativeDistanceAngleToSpeaker.targetAngle(), robotHeading, 3);
    facingFeedSpotAngle =
        MathUtil.isNear(fieldRelativeDistanceAngleToFeedSpot.targetAngle(), robotHeading, 3);
  }

  @Override
  protected RobotState getNextState(RobotState currentState) {
    return switch (currentState) {
      case IDLE_NO_GP,
              IDLE_WITH_GP,
              CLIMBING_1_LINEUP,
              CLIMBING_2_HANGING,
              OUTTAKING,
              AMP_SCORING,
              SPEAKER_WAITING,
              AMP_WAITING,
              SUBWOOFER_WAITING,
              FEEDING_WAITING,
              PODIUM_WAITING ->
          currentState;

      case FEEDING_SHOOTING, PASS_SHOOTING, SUBWOOFER_SCORING, PODIUM_SCORING ->
          hopper.hasNote() ? currentState : RobotState.IDLE_NO_GP;
      case SPEAKER_SCORING ->
          hopper.hasNote() || !shotTimer.hasElapsed(MINIMUM_SHOT_TIME)
              ? currentState
              : RobotState.IDLE_NO_GP;

      case SPEAKER_PREPARE_TO_SCORE ->
          (shooter.atGoal())
                  && (DriverStation.isAutonomous() || confirmShotActive)
                  && swerve.isSlowEnoughToShoot()
                  && ((DriverStation.isAutonomous()
                          && vision.getVisionState() == CameraStatus.OFFLINE)
                      || vision.getVisionState() == CameraStatus.SEES_TAGS)
                  && facingSpeakerAngle
              ? RobotState.SPEAKER_SCORING
              : currentState;

      case AMP_PREPARE_TO_SCORE ->
          shooter.atGoal() ? RobotState.AMP_SCORING : currentState;

      case FEEDING_PREPARE_TO_SHOOT ->
          shooter.atGoal() && facingFeedSpotAngle && swerve.isSlowEnoughToFeed()
              ? RobotState.FEEDING_SHOOTING
              : currentState;
      case PASS_PREPARE_TO_SHOOT ->
          shooter.atGoal() ? RobotState.PASS_SHOOTING : currentState;
      case SUBWOOFER_PREPARE_TO_SCORE ->
          shooter.atGoal()  ? RobotState.SUBWOOFER_SCORING : currentState;
      case PODIUM_PREPARE_TO_SCORE ->
          shooter.atGoal() ? RobotState.PODIUM_SCORING : currentState;

      case UNJAM -> currentState;
      case BEFORE_INTAKING -> shooter.atGoal() ? RobotState.INTAKING : currentState;
      case BEFORE_INTAKING_ASSIST -> shooter.atGoal() ? RobotState.INTAKE_ASSIST : currentState;

      case INTAKING, INTAKE_ASSIST -> {
        if (!hopper.hasNote()) {
          yield currentState;
        }

        if (confirmShotActive) {
          yield RobotState.SPEAKER_PREPARE_TO_SCORE;
        }

        yield RobotState.IDLE_WITH_GP;
      }
        // case INTAKING_BACK -> !queuer.hasNote() ? RobotState.INTAKING_FORWARD_PUSH :
        // currentState;
        // case INTAKING_FORWARD_PUSH -> {
        //   if (!queuer.atGoal()) {
        //     yield currentState;
        //   }

        //   if (confirmShotActive) {
        //     yield RobotState.SPEAKER_PREPARE_TO_SCORE;
        //   }

        //   yield RobotState.IDLE_WITH_GP;
        // }
    };
  }

  @Override
  protected void afterTransition(RobotState newState) {
    switch (newState) {
      case SUBWOOFER_PREPARE_TO_SCORE, SUBWOOFER_WAITING -> {
        intake.setState(IntakeStateEnum.INTAKE);
        shooter.setState(ShooterStateEnum.SUBWOOFER);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.IDLE);
        swerve.setSnapsEnabled(true);
        swerve.setSnapToAngle(SnapUtil.getSubwooferAngle());
      }
      case PODIUM_PREPARE_TO_SCORE, PODIUM_WAITING -> {
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        shooter.setState(ShooterStateEnum.PODIUM);
        intake.setState(IntakeStateEnum.IDLE);
        hopper.setState(HopperStateEnum.IDLE);
        swerve.setSnapsEnabled(true);
        swerve.setSnapToAngle(SnapUtil.getPodiumAngle());
      }
      case PODIUM_SCORING -> {
        shooter.setState(ShooterStateEnum.PODIUM);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.FORWARD);
        swerve.setSnapsEnabled(true);
        swerve.setSnapToAngle(SnapUtil.getPodiumAngle());
      }
      case SUBWOOFER_SCORING -> {
        shooter.setState(ShooterStateEnum.SUBWOOFER);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.FORWARD);
        swerve.setSnapsEnabled(true);
        swerve.setSnapToAngle(SnapUtil.getSubwooferAngle());
      }
      case SPEAKER_PREPARE_TO_SCORE -> {
        shooter.setState(ShooterStateEnum.CRESCENDO);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.IDLE);
        swerve.setSnapsEnabled(true);
        swerve.setSnapToAngle(fieldRelativeDistanceAngleToSpeaker.targetAngle());
        shotTimer.stop();
        shotTimer.reset();
      }
      case SPEAKER_WAITING -> {
        shooter.setState(ShooterStateEnum.CRESCENDO);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.IDLE);
        swerve.setSnapsEnabled(true);
        swerve.setSnapToAngle(fieldRelativeDistanceAngleToSpeaker.targetAngle());
      }
      case SPEAKER_SCORING -> {
        shooter.setState(ShooterStateEnum.CRESCENDO);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.FORWARD);
        swerve.setSnapsEnabled(true);
        swerve.setSnapToAngle(fieldRelativeDistanceAngleToSpeaker.targetAngle());
        shotTimer.start();
      }
      case AMP_PREPARE_TO_SCORE, AMP_WAITING -> {
        shooter.setState(ShooterStateEnum.IDLE);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.IDLE);
        swerve.setSnapsEnabled(true);
        swerve.setSnapToAngle(SnapUtil.getAmpAngle());
      }
      case AMP_SCORING -> {
        shooter.setState(ShooterStateEnum.IDLE);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.FORWARD);
        swerve.setSnapsEnabled(true);
        swerve.setSnapToAngle(SnapUtil.getAmpAngle());
      }
      case FEEDING_PREPARE_TO_SHOOT, FEEDING_WAITING -> {
        shooter.setState(ShooterStateEnum.SUBWOOFER);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.IDLE);
        swerve.setSnapsEnabled(true);
        swerve.setSnapToAngle(fieldRelativeDistanceAngleToFeedSpot.targetAngle());
      }
      case FEEDING_SHOOTING -> {
        shooter.setState(ShooterStateEnum.SUBWOOFER);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.FORWARD);
        swerve.setSnapsEnabled(true);
        swerve.setSnapToAngle(fieldRelativeDistanceAngleToFeedSpot.targetAngle());
      }
      case PASS_PREPARE_TO_SHOOT -> {
        shooter.setState(ShooterStateEnum.SUBWOOFER);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.IDLE);
        swerve.setSnapsEnabled(false);
        swerve.setSnapToAngle(0);
      }
      case PASS_SHOOTING -> {
        shooter.setState(ShooterStateEnum.SUBWOOFER);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.IDLE);
        swerve.setSnapsEnabled(false);
        swerve.setSnapToAngle(0);
      }
      case UNJAM -> {
        shooter.setState(ShooterStateEnum.SUBWOOFER);
        intake.setState(IntakeStateEnum.OUTTAKE);
        intakeRollers.setState(IntakeRollersStateEnum.OUTTAKE);
        hopper.setState(HopperStateEnum.FORWARD);
        swerve.setSnapsEnabled(false);
        swerve.setSnapToAngle(0);
      }
      case INTAKING -> {
        shooter.setState(ShooterStateEnum.IDLE);
        intake.setState(IntakeStateEnum.INTAKE);
        intakeRollers.setState(IntakeRollersStateEnum.INTAKE);
        hopper.setState(HopperStateEnum.IDLE);
        swerve.setSnapsEnabled(false);
        swerve.setSnapToAngle(0);
      }
      case BEFORE_INTAKING -> {
        shooter.setState(ShooterStateEnum.IDLE);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.IDLE);
        swerve.setSnapsEnabled(false);
        swerve.setSnapToAngle(0);
      }
      case BEFORE_INTAKING_ASSIST -> {
        shooter.setState(ShooterStateEnum.IDLE);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.IDLE);
        if (DriverStation.isTeleop()) {
          swerve.setState(SwerveState.INTAKE_ASSIST_TELEOP);
        } else {
          swerve.setState(SwerveState.INTAKE_ASSIST_AUTO);
        }
      }
        // case INTAKING_BACK -> {
        //   arm.setState(ArmState.IDLE);
        //   shooter.setState(ShooterState.IDLE_STOPPED);
        //   intake.setState(IntakeState.INTAKING_BACK);
        //   queuer.setState(QueuerState.INTAKING_BACK);
        //   swerve.setSnapsEnabled(false);
        //   swerve.setSnapToAngle(0);
        // }
        // case INTAKING_FORWARD_PUSH -> {
        //   arm.setState(ArmState.IDLE);
        //   shooter.setState(ShooterState.IDLE_STOPPED);
        //   intake.setState(IntakeState.INTAKING_FORWARD_PUSH);
        //   queuer.setState(QueuerState.INTAKING_FORWARD_PUSH);
        //   swerve.setSnapsEnabled(false);
        //   swerve.setSnapToAngle(0);
        // }
      case INTAKE_ASSIST -> {
        shooter.setState(ShooterStateEnum.IDLE);
        intake.setState(IntakeStateEnum.INTAKE);
        intakeRollers.setState(IntakeRollersStateEnum.INTAKE);
        hopper.setState(HopperStateEnum.FORWARD);
        // We don't use the setSnaps here, since intake assist is a separate state
        if (DriverStation.isTeleop()) {
          swerve.setState(SwerveState.INTAKE_ASSIST_TELEOP);
        } else {
          swerve.setState(SwerveState.INTAKE_ASSIST_AUTO);
        }
      }
      case OUTTAKING -> {
        shooter.setState(ShooterStateEnum.IDLE);
        intake.setState(IntakeStateEnum.OUTTAKE);
        intakeRollers.setState(IntakeRollersStateEnum.INTAKE);
        hopper.setState(HopperStateEnum.REVERSE);
        swerve.setSnapsEnabled(false);
        swerve.setSnapToAngle(0);
      }
     
     
      case IDLE_NO_GP -> {
        shooter.setState(ShooterStateEnum.IDLE);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.IDLE);
        swerve.setSnapsEnabled(false);
        swerve.setSnapToAngle(0);
      }
      case IDLE_WITH_GP -> {
        shooter.setState(ShooterStateEnum.IDLE);
        intake.setState(IntakeStateEnum.IDLE);
        intakeRollers.setState(IntakeRollersStateEnum.IDLE);
        hopper.setState(HopperStateEnum.IDLE);
        swerve.setSnapsEnabled(false);
        swerve.setSnapToAngle(0);
      }
    }
  }

  @Override
  public void robotPeriodic() {
    super.robotPeriodic();

    // Continuous state actions
    switch (getState()) {
      case SPEAKER_PREPARE_TO_SCORE, SPEAKER_SCORING, SPEAKER_WAITING -> {
        shooter.setDistanceToSpeaker(fieldRelativeDistanceAngleToSpeaker.distance());
        swerve.setSnapToAngle(fieldRelativeDistanceAngleToSpeaker.targetAngle());
      }
      case FEEDING_PREPARE_TO_SHOOT, FEEDING_SHOOTING, FEEDING_WAITING -> {
        shooter.setDistanceToFeedSpot(fieldRelativeDistanceAngleToFeedSpot.distance());
        swerve.setSnapToAngle(fieldRelativeDistanceAngleToFeedSpot.targetAngle())
      }
      case INTAKING, INTAKE_ASSIST -> {
        if (shooter.atGoal()) {
          intake.setState(IntakeStateEnum.INTAKE);
        } else {
          intake.setState(IntakeStateEnum.IDLE);
        }
      }
        // case INTAKING_BACK -> {
        //   if (arm.atGoal()) {
        //     intake.setState(IntakeState.INTAKING_BACK);
        //   } else {
        //     intake.setState(IntakeState.IDLE);
        //   }
        // }
        // case INTAKING_FORWARD_PUSH -> {
        //   if (arm.atGoal()) {
        //     intake.setState(IntakeState.INTAKING_FORWARD_PUSH);
        //   } else {
        //     intake.setState(IntakeState.IDLE);
        //   }
        // }
      default -> {}
    }
  }

  public void setConfirmShotActive(boolean newValue) {
    confirmShotActive = newValue;
  }

  public void confirmShotRequest() {
    switch (getState()) {
      case
          INTAKING,
          INTAKE_ASSIST,
          BEFORE_INTAKING,
          BEFORE_INTAKING_ASSIST
      // INTAKING_BACK,
      // INTAKING_FORWARD_PUSH
      -> {}

      case AMP_WAITING -> setStateFromRequest(RobotState.AMP_PREPARE_TO_SCORE);
      case SPEAKER_WAITING -> setStateFromRequest(RobotState.SPEAKER_PREPARE_TO_SCORE);
      case FEEDING_WAITING -> setStateFromRequest(RobotState.FEEDING_PREPARE_TO_SHOOT);
      case SUBWOOFER_WAITING -> setStateFromRequest(RobotState.SUBWOOFER_PREPARE_TO_SCORE);
      case PODIUM_WAITING -> setStateFromRequest(RobotState.PODIUM_PREPARE_TO_SCORE);
      default -> setStateFromRequest(RobotState.SPEAKER_PREPARE_TO_SCORE);
    }
  }

  public void waitAmpRequest() {
    switch (getState()) {
      case
          AMP_SCORING,
          // INTAKING_BACK,
          // INTAKING_FORWARD_PUSH,
          INTAKING,
          INTAKE_ASSIST,
          BEFORE_INTAKING,
          BEFORE_INTAKING_ASSIST -> {}
      default -> setStateFromRequest(RobotState.AMP_WAITING);
    }
  }

  public void waitSubwooferRequest() {
    switch (getState()) {
      case 
          SPEAKER_SCORING,
          // INTAKING_BACK,
          // INTAKING_FORWARD_PUSH,
          INTAKING,
          INTAKE_ASSIST,
          BEFORE_INTAKING,
          BEFORE_INTAKING_ASSIST -> {}
      default -> setStateFromRequest(RobotState.SUBWOOFER_WAITING);
    }
  }

  public void waitPodiumRequest() {
    switch (getState()) {
      case 
          SPEAKER_SCORING,
          // INTAKING_BACK,
          // INTAKING_FORWARD_PUSH,
          INTAKING,
          INTAKE_ASSIST,
          BEFORE_INTAKING,
          BEFORE_INTAKING_ASSIST -> {}
      default -> setStateFromRequest(RobotState.PODIUM_WAITING);
    }
  }

  public void waitSpeakerRequest() {
    switch (getState()) {
      case 
          // INTAKING_BACK,
          // INTAKING_FORWARD_PUSH,
          INTAKING,
          INTAKE_ASSIST,
          BEFORE_INTAKING,
          BEFORE_INTAKING_ASSIST -> {
        if (DriverStation.isAutonomous()) {
          // Bypass intake checks if we're in auto
          setStateFromRequest(RobotState.SPEAKER_WAITING);
        }
      }
      default -> setStateFromRequest(RobotState.SPEAKER_WAITING);
    }
  }

  public void unjamRequest() {
    switch (getState()) {
      case CLIMBING_1_LINEUP, CLIMBING_2_HANGING -> {}
      default -> setStateFromRequest(RobotState.UNJAM);
    }
  }

  public void intakeRequest() {
    if (!intake.hasNote()) {
      switch (getState()) {
        case CLIMBING_1_LINEUP, CLIMBING_2_HANGING -> {}
        default -> setStateFromRequest(RobotState.BEFORE_INTAKING);
      }
    }
  }

  public void intakeAssistRequest() {
    if (!intake.hasNote()) {
      switch (getState()) {
        case CLIMBING_1_LINEUP, CLIMBING_2_HANGING -> {}
        default -> setStateFromRequest(RobotState.BEFORE_INTAKING_ASSIST);
      }
    }
  }

  public void outtakeRequest() {
    switch (getState()) {
      case CLIMBING_1_LINEUP, CLIMBING_2_HANGING -> {}
      default -> setStateFromRequest(RobotState.OUTTAKING);
    }
  }

  public void idleWithGpRequest() {
    setStateFromRequest(RobotState.IDLE_WITH_GP);
  }

  public void stowRequest() {
    switch (getState()) {
        // TODO: Intaking and unjam should not be IDLE_WITH_GP
      case INTAKING,
              INTAKE_ASSIST,
              AMP_PREPARE_TO_SCORE,
              SPEAKER_PREPARE_TO_SCORE,
              FEEDING_PREPARE_TO_SHOOT,
              PASS_PREPARE_TO_SHOOT,
              AMP_WAITING,
              SPEAKER_WAITING,
              FEEDING_WAITING,
              AMP_SCORING,
              SPEAKER_SCORING,
              FEEDING_SHOOTING,
              PASS_SHOOTING,
              IDLE_WITH_GP,
              UNJAM
          // INTAKING_BACK,
          // INTAKING_FORWARD_PUSH
          ->
          setStateFromRequest(RobotState.IDLE_WITH_GP);
      default -> setStateFromRequest(RobotState.IDLE_NO_GP);
    }
  }

  public void preparePassRequest() {
    switch (getState()) {
      case CLIMBING_1_LINEUP,
          CLIMBING_2_HANGING,
          // INTAKING_BACK,
          // INTAKING_FORWARD_PUSH,
          INTAKING,
          INTAKE_ASSIST,
          BEFORE_INTAKING,
          BEFORE_INTAKING_ASSIST -> {}
      default -> setStateFromRequest(RobotState.PASS_PREPARE_TO_SHOOT);
    }
  }

  public void nextClimbStateRequest() {
    switch (getState()) {
      case CLIMBING_1_LINEUP -> setStateFromRequest(RobotState.CLIMBING_2_HANGING);
      case CLIMBING_2_HANGING -> {}
      default -> setStateFromRequest(RobotState.CLIMBING_1_LINEUP);
    }
  }

  public void prepareSpeakerRequest() {
    switch (getState()) {
      case         // INTAKING_BACK,
          // INTAKING_FORWARD_PUSH,
          INTAKING,
          INTAKE_ASSIST,
          BEFORE_INTAKING,
          BEFORE_INTAKING_ASSIST -> {
        if (DriverStation.isAutonomous()) {
          // Bypass intake checks if we're in auto
          setStateFromRequest(RobotState.SPEAKER_PREPARE_TO_SCORE);
        }
      }
      default -> setStateFromRequest(RobotState.SPEAKER_PREPARE_TO_SCORE);
    }
  }

  public void prepareAmpRequest() {
    switch (getState()) {
      case 
          // INTAKING_BACK,
          // INTAKING_FORWARD_PUSH,
          INTAKING,
          INTAKE_ASSIST,
          BEFORE_INTAKING,
          BEFORE_INTAKING_ASSIST -> {}
      default -> setStateFromRequest(RobotState.AMP_PREPARE_TO_SCORE);
    }
  }

  public void prepareFeedRequest() {
    switch (getState()) {
      case 
          // INTAKING_BACK,
          // INTAKING_FORWARD_PUSH,
          INTAKING,
          INTAKE_ASSIST,
          BEFORE_INTAKING,
          BEFORE_INTAKING_ASSIST -> {}
      default -> setStateFromRequest(RobotState.FEEDING_PREPARE_TO_SHOOT);
    }
  }

  public void waitFeedRequest() {
    switch (getState()) {
      case 
          // INTAKING_BACK,
          // INTAKING_FORWARD_PUSH,
          INTAKING,
          INTAKE_ASSIST,
          BEFORE_INTAKING,
          BEFORE_INTAKING_ASSIST -> {}
      default -> setStateFromRequest(RobotState.FEEDING_WAITING);
    }
  }

  public void stopShootingRequest() {
    // If we are actively taking a shot, ignore the request to avoid messing up shooting
    switch (getState()) {
      case SPEAKER_SCORING,
          SUBWOOFER_SCORING,
          AMP_SCORING,
          AMP_WAITING,
          FEEDING_SHOOTING,
          PASS_SHOOTING -> {}

      default -> setStateFromRequest(RobotState.IDLE_WITH_GP);
    }
  }

  public void prepareSubwooferRequest() {
    switch (getState()) {
      case 
          // INTAKING_BACK,
          // INTAKING_FORWARD_PUSH,
          INTAKING,
          INTAKE_ASSIST,
          BEFORE_INTAKING,
          BEFORE_INTAKING_ASSIST -> {}
      default -> setStateFromRequest(RobotState.SUBWOOFER_PREPARE_TO_SCORE);
    }
  }

  public void preparePodiumRequest() {
    switch (getState()) {
      case 
          // INTAKING_BACK,
          // INTAKING_FORWARD_PUSH,
          INTAKING,
          INTAKE_ASSIST,
          BEFORE_INTAKING,
          BEFORE_INTAKING_ASSIST -> {}
      default -> setStateFromRequest(RobotState.PODIUM_PREPARE_TO_SCORE);
    }
}}

