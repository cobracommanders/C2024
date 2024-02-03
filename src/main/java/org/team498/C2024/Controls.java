package org.team498.C2024;

import org.team498.C2024.Constants.OIConstants;
import org.team498.C2024.StateController.LoadingOption;
import org.team498.C2024.StateController.ScoringOption;
import org.team498.C2024.commands.drivetrain.HybridDrive;
import org.team498.C2024.commands.drivetrain.SlowDrive;
import org.team498.C2024.commands.drivetrain.TargetDrive;
import org.team498.C2024.commands.robot.CancelAmp;
import org.team498.C2024.commands.robot.CancelSpeaker;
import org.team498.C2024.commands.robot.CollectSource;
import org.team498.C2024.commands.robot.LoadGround;
import org.team498.C2024.commands.robot.LoadSource;
import org.team498.C2024.commands.robot.PrepareToScore;
import org.team498.C2024.commands.robot.ReturnToIdle;
import org.team498.C2024.commands.robot.Score;
import org.team498.C2024.subsystems.Drivetrain;
import org.team498.lib.drivers.Xbox;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class Controls {
    public final Xbox driver = new Xbox(OIConstants.DRIVER_CONTROLLER_ID);
    public final Xbox operator = new Xbox(OIConstants.OPERATOR_CONTROLLER_ID);

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
        driver.rightBumper().onTrue(new SlowDrive(true))
            .onFalse(new SlowDrive(false));
        driver.leftBumper().onTrue(new ConditionalCommand(
                runOnce(()->Drivetrain.getInstance().setAngleGoal(90)),
                runOnce(()->StateController.getInstance().setTargetDrive(FieldPositions.getSpeaker())), 
                ()-> StateController.getInstance().getState() == State.AMP))
            .onFalse(new TargetDrive(null));
        driver.A().onTrue(runOnce(() -> Drivetrain.getInstance().setYaw(0 + Robot.rotationOffset)));
        driver.B().onTrue(runOnce(() -> Drivetrain.getInstance().setPose(new Pose2d(15.18, 1.32, Rotation2d.fromDegrees(0 + Robot.rotationOffset)))));
        driver.Y().onTrue(runOnce(() -> Drivetrain.getInstance().setPose(new Pose2d(15.07, 5.55, Rotation2d.fromDegrees(0 + Robot.rotationOffset)))));
        // driver.B().onTrue(new LoadGround())
        //     .onFalse(new ReturnToIdle());
        // driver.leftBumper().onTrue(new PrepareToScore())
        //     .onFalse( new ConditionalCommand(new CancelAmp(), new CancelSpeaker(), ()-> StateController.getInstance().getState() == State.AMP));
        // driver.rightTrigger().onTrue(new Score());


    }

    public void configureOperatorCommands() {
        // operator.X().onTrue(runOnce(() -> StateController.getInstance().setNextScoringOption(ScoringOption.CRESCENDO)));
        // operator.A().onTrue(runOnce(() -> StateController.getInstance().setNextScoringOption(ScoringOption.SUBWOOFER)));
        // operator.B().onTrue(runOnce(() -> StateController.getInstance().setNextScoringOption(ScoringOption.PODIUM)));
        // operator.leftBumper().onTrue(runOnce(() -> StateController.getInstance().setNextLoadingOption(LoadingOption.GROUND)));
        // operator.leftTrigger().onTrue(new LoadSource())
        //     .onFalse(new ReturnToIdle());
        // operator.rightBumper().onTrue(runOnce(() -> StateController.getInstance().setNextLoadingOption(LoadingOption.SOURCE)));
        // operator.rightTrigger().onTrue(new CollectSource())
        //     .onFalse(new ReturnToIdle());
    }
}