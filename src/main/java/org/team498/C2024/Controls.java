package org.team498.C2024;

import static org.team498.C2024.Constants.DrivetrainConstants.FULL_SPEED_SCALAR;

import org.team498.C2024.Constants.DrivetrainConstants;
import org.team498.C2024.Constants.OIConstants;
import org.team498.C2024.StateController.LoadingOption;
import org.team498.C2024.StateController.ScoringOption;
import org.team498.C2024.commands.drivetrain.HybridDrive;
import org.team498.C2024.commands.drivetrain.SlowDrive;
import org.team498.C2024.commands.drivetrain.TargetDrive;
import org.team498.C2024.commands.hopper.SetHopperState;
import org.team498.C2024.commands.intake.SetIntakeManual;
import org.team498.C2024.commands.intake.SetIntakeState;
import org.team498.C2024.commands.kicker.SetKickerNextState;
import org.team498.C2024.commands.robot.ReturnToIdle;
import org.team498.C2024.commands.robot.SetIntakeIdle;
import org.team498.C2024.commands.robot.SetScoringState;
import org.team498.C2024.commands.robot.SetState;
import org.team498.C2024.commands.robot.loading.CollectSource;
import org.team498.C2024.commands.robot.loading.LoadGround;
import org.team498.C2024.commands.robot.loading.LoadSource;
import org.team498.C2024.commands.robot.loading.Outtake;
import org.team498.C2024.commands.robot.scoring.CancelAmp;
import org.team498.C2024.commands.robot.scoring.CancelSpeaker;
import org.team498.C2024.commands.robot.scoring.PrepareAmp;
import org.team498.C2024.commands.robot.scoring.PrepareToScore;
import org.team498.C2024.commands.robot.scoring.Score;
import org.team498.C2024.commands.shooter.SetShooterManual;
import org.team498.C2024.commands.shooter.SetShooterNextState;
import org.team498.C2024.commands.shooter.SetShooterState;
import org.team498.C2024.subsystems.Drivetrain;
import org.team498.lib.drivers.Xbox;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class Controls {
    public final Xbox driver = new Xbox(OIConstants.DRIVER_CONTROLLER_ID);
    public final Xbox operator = new Xbox(OIConstants.OPERATOR_CONTROLLER_ID);

    public static final Command scoreCommand = new Score();

    public Controls() {
        driver.setDeadzone(0.15);
        driver.setTriggerThreshold(0.2);
        operator.setDeadzone(0.2);
        operator.setTriggerThreshold(0.2);
    }

    public void configureDefaultCommands() {
        Drivetrain.getInstance().setDefaultCommand(new HybridDrive(driver::leftYSquared, driver::leftXSquared, driver::rightX, driver::rawPOVAngle));
    }

    public void configureDriverCommands() {
        driver.rightBumper().onTrue(new SlowDrive(DrivetrainConstants.SLOW_SPEED_SCALAR))
            .onFalse(new SlowDrive(DrivetrainConstants.FULL_SPEED_SCALAR));
        driver.leftBumper().onTrue(new ConditionalCommand(
                runOnce(()->StateController.getInstance().setAngleOverride(-90)),
                runOnce(()->StateController.getInstance().setTargetDrive(FieldPositions.getSpeaker())).alongWith(new SlowDrive(DrivetrainConstants.TARGET_SPEED_SCALAR)), 
                // new ConditionalCommand(
                //     runOnce(()-> {}),
                //     runOnce(()->StateController.getInstance().setTargetDrive(FieldPositions.getSpeaker())).alongWith(new SlowDrive(DrivetrainConstants.TARGET_SPEED_SCALAR)), 
                //     ()-> StateController.getInstance().getNextScoringState() == State.SUBWOOFER  || StateController.getInstance().getNextScoringState() == State.PODIUM || StateController.getInstance().getNextScoringState() == State.AMP_SPEAKER
                // ),
                //runOnce(()->StateController.getInstance().setTargetDrive(FieldPositions.getSpeaker())),
                
                ()-> StateController.getInstance().getNextScoringState() == State.AMP))
            .onFalse(new TargetDrive(null).alongWith(runOnce(()->StateController.getInstance().setAngleOverride(-1))).alongWith(new SlowDrive(DrivetrainConstants.FULL_SPEED_SCALAR)));
        driver.A().onTrue(runOnce(() -> Drivetrain.getInstance().setYaw(0 + Robot.rotationOffset)));
        driver.B().onTrue(runOnce(() -> Drivetrain.getInstance().setPose(new Pose2d(15.18, 1.32, Rotation2d.fromDegrees(0 + Robot.rotationOffset)))));
        driver.Y().onTrue(runOnce(() -> Drivetrain.getInstance().setPose(new Pose2d(15.07, 5.55, Rotation2d.fromDegrees(0 + Robot.rotationOffset)))));
        driver.leftTrigger().onTrue(new LoadGround())
            .onFalse(new SetIntakeIdle());
        // driver.leftBumper().onTrue(new PrepareToScore())
        //     .onFalse(runOnce(()-> {
        //             if (!StateController.getInstance().isScoring()) {
        //                 CommandScheduler.getInstance().schedule(new ConditionalCommand(new CancelAmp(), new CancelSpeaker(), ()-> StateController.getInstance().getState() == State.AMP));
        //             }
        //         }   
        //     ));
        //driver.rightTrigger().onTrue(runOnce(()-> CommandScheduler.getInstance().schedule(scoreCommand)));//.onFalse(runOnce(()-> scoreCommand.cancel()));
        driver.rightTrigger().onTrue(new SetScoringState().andThen(runOnce(()-> CommandScheduler.getInstance().schedule(scoreCommand))));
    }

    public void configureOperatorCommands() {
        operator.start().toggleOnTrue(new SetShooterManual(true, operator::leftY, ()-> 0));
        // operator.back().toggleOnTrue(new SetIntakeManual(true, operator::rightY));
        operator.rightTrigger().onTrue(new SetHopperState(State.Hopper.REVERSE))
            .onFalse(new ReturnToIdle());
        operator.leftTrigger().onTrue(new Outtake())
            .onFalse(new ReturnToIdle());
        operator.leftBumper().onTrue(new SetShooterState(State.Shooter.PODIUM));
        operator.rightBumper().onTrue(new ReturnToIdle());
        // operator.back().toggleOnTrue(new SetShooterManual(true, operator::leftY, ()-> 0.5));
        // operator.A().toggleOnTrue(new SetShooterManual(true, operator::leftY, ()-> 1.0));
        // operator.B().toggleOnTrue(new SetShooterManual(true, operator::leftY, ()-> 0.0));
        // operator.Y().toggleOnTrue(new SetState(State.AMP).andThen(new SetKickerNextState()));
        // operator.X().toggleOnTrue(new SetState(State.SUBWOOFER).andThen(new SetKickerNextState()));
        // operator.rightBumper().toggleOnTrue(new SetState(State.IDLE).andThen(new SetKickerNextState()));
        operator.back().onTrue(new SetShooterState(State.Shooter.CLIMB_UP)).onFalse(new SetShooterState(State.Shooter.CLIMB_DOWN));



        operator.X().onTrue(runOnce(() -> StateController.getInstance().setNextScoringOption(ScoringOption.AMP_SPEAKER)));
        operator.X().toggleOnTrue(new PrepareToScore());

        operator.A().onTrue(runOnce(() -> StateController.getInstance().setNextScoringOption(ScoringOption.SUBWOOFER)));
        operator.A().toggleOnTrue(new PrepareToScore());

        // operator.B().onTrue(runOnce(() -> StateController.getInstance().setNextScoringOption(ScoringOption.PODIUM)));
        // operator.B().toggleOnTrue(new PrepareToScore());
        operator.B().onTrue(runOnce(() -> StateController.getInstance().setNextScoringOption(ScoringOption.PODIUM)));
        operator.B().toggleOnTrue(new PrepareToScore());

        operator.Y().onTrue(runOnce(() -> StateController.getInstance().setNextScoringOption(ScoringOption.AMP)));
        operator.Y().toggleOnTrue(new PrepareAmp());

        // operator.leftBumper().onTrue(runOnce(() -> StateController.getInstance().setNextLoadingOption(LoadingOption.GROUND)));
        // operator.leftTrigger().onTrue(new LoadSource())
        //     .onFalse(new ReturnToIdle());
        // operator.rightBumper().onTrue(runOnce(() -> StateController.getInstance().setNextLoadingOption(LoadingOption.SOURCE)));
        // operator.rightTrigger().onTrue(new CollectSource())
        //     .onFalse(new ReturnToIdle());
    }
}