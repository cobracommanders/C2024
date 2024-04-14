package org.team498.C2024.commands.auto;
import org.team498.C2024.Constants.DrivetrainConstants;
import org.team498.C2024.PathLib;
import org.team498.C2024.State;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.C2024.commands.drivetrain.SlowDrive;
import org.team498.C2024.commands.hopper.SetHopperState;
import org.team498.C2024.commands.intake.SetIntakeRollerState;
import org.team498.C2024.commands.intake.SetIntakeState;
import org.team498.C2024.commands.robot.ReturnToIdle;
import org.team498.C2024.commands.robot.SetState;
import org.team498.C2024.commands.robot.loading.LoadGround;
import org.team498.C2024.commands.robot.scoring.FullScore;
import org.team498.C2024.commands.robot.scoring.SlowScore;
import org.team498.C2024.commands.robot.scoring.SubwooferScore;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class OuterWing implements Auto{
    @Override
    public Command getCommand() {

        return new SequentialCommandGroup(

            new SubwooferScore(0.8), //0.75

            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.outer_wing_2),
                new SlowDrive(DrivetrainConstants.AUTO_SPEED_SCALAR),
                new SequentialCommandGroup(
                    new WaitCommand(1.5),
                    new LoadGround().withTimeout(1.75),//2
                    new SetIntakeState(State.Intake.IDLE),
                    new SetHopperState(State.Hopper.IDLE),
                    new SetIntakeRollerState(State.IntakeRollers.IDLE)
                )
            ),
            new SetState(State.CRESCENDO),
            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.outer_wing_3).andThen(new WaitCommand(0.4)), //1
                new SequentialCommandGroup(
                    new SlowScore(2.5),
                    new SlowDrive(DrivetrainConstants.AUTO_SPEED_SCALAR),
                    new SetState(State.CRESCENDO),
                    new WaitCommand(0.5),
                    new LoadGround().withTimeout(2.2),//2.6
                    new SetIntakeState(State.Intake.IDLE),
                    new SetHopperState(State.Hopper.IDLE),
                    new SetIntakeRollerState(State.IntakeRollers.IDLE)
                )
            ),
            
            new ParallelCommandGroup(
                new PathPlannerFollower(PathLib.outer_wing_1),
                new SequentialCommandGroup(
                    new WaitCommand(0.3),
                    new SlowScore(1.6),
                    new SlowDrive(DrivetrainConstants.AUTO_SPEED_SCALAR)
                    //new LoadGround().withTimeout(3), // or 5
                    // new SetIntakeState(State.Intake.IDLE),
                    // new SetHopperState(State.Hopper.IDLE),
                    // new SetIntakeRollerState(State.IntakeRollers.IDLE)
                )
            )

                // new SequentialCommandGroup(
                // new SetState(State.CRESCENDO),
                // new SlowScore(1),
                // new ReturnToIdle())
        );
}

    @Override
    public Pose2d getInitialPose() {
        return PathLib.outer_wing_2.getInitialTargetHolonomicPose();
    }
    
}
