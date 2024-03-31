package org.team498.C2024.commands.auto;
import org.team498.C2024.PathLib;
import org.team498.C2024.State;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.C2024.commands.intake.SetIntakeRollerState;
import org.team498.C2024.commands.robot.ReturnToIdle;
import org.team498.C2024.commands.robot.loading.LoadGround;
import org.team498.C2024.commands.robot.scoring.FullScore;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class FourNoteWing implements Auto{
    @Override
    public Command getCommand() {

        return new SequentialCommandGroup(
 
            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.four_wing_1),
                new SequentialCommandGroup(
                    new FullScore(0.75),
                    new LoadGround(),
                    new WaitCommand(3),
                    new FullScore(0.5),
                    new LoadGround()
                )
            ),
            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.four_wing_2),
                new SequentialCommandGroup(
                    // new SetIntakeRollerState(State.IntakeRollers.IDLE),
                    new FullScore(0.5),
                    new LoadGround(),
                    new WaitCommand(3),
                    new FullScore(0.5),
                    new LoadGround()
                )
            ),

            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.four_wing_3),
                new SequentialCommandGroup(
                    // new SetIntakeRollerState(State.IntakeRollers.IDLE),
                    new FullScore(0.5),
                    new LoadGround(),
                    new WaitCommand(3),
                    new FullScore(0.5),
                    new LoadGround()
                )
            ),
            new FullScore(1),
            new ReturnToIdle()
        );
}

    @Override
    public Pose2d getInitialPose() {
        return PathLib.four_wing_1.getInitialTargetHolonomicPose();
    }
    
}
