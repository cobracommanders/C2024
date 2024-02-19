package org.team498.C2024.commands.auto;
import org.team498.C2024.PathLib;
import org.team498.C2024.StateController;
import org.team498.C2024.StateController.ScoringOption;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.C2024.commands.drivetrain.SlowDrive;
import org.team498.C2024.commands.drivetrain.TargetDrive;
import org.team498.C2024.commands.drivetrain.TargetSpeaker;
import org.team498.C2024.commands.robot.loading.LoadGround;
import org.team498.C2024.commands.robot.scoring.FullScore;
import org.team498.C2024.commands.robot.scoring.PrepareToScore;
import org.team498.C2024.commands.robot.scoring.Score;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class FourNote implements Auto{
    @Override
    public Command getCommand() {

        return new SequentialCommandGroup(
                new InstantCommand(() -> StateController.getInstance().setNextScoringOption(ScoringOption.CRESCENDO)),
                new FullScore(),

            new ParallelCommandGroup(
                new PathPlannerFollower(PathLib.SL1Note1),
                new SequentialCommandGroup(
                    new WaitCommand(1),
                    new LoadGround(),
                    new WaitCommand(1),
                    new TargetSpeaker(),
                    new SlowDrive(true),
                    new FullScore(),
                    new TargetDrive(null),
                    new SlowDrive(false)
                )
            ),

            new ParallelCommandGroup(
                new PathPlannerFollower(PathLib.Note1Note2),
                new SequentialCommandGroup(
                    new WaitCommand(1),
                    new LoadGround(),
                    new WaitCommand(1),
                    new TargetSpeaker(),
                    new SlowDrive(true),
                    new FullScore(),
                    new TargetDrive(null),
                    new SlowDrive(false)
                )
            ),

            new ParallelCommandGroup(
                new PathPlannerFollower(PathLib.Note2Note3),
                new SequentialCommandGroup(
                    new WaitCommand(1),
                    new LoadGround(),
                    new WaitCommand(1)
                )
            ),

            new ParallelCommandGroup(
                new PathPlannerFollower(PathLib.ThirdNoteSL2),
                new SequentialCommandGroup(
                    new TargetSpeaker(),
                    new SlowDrive(true),
                    new FullScore(),
                    new TargetDrive(null),
                    new SlowDrive(false)
                )
            )
        );
}

    @Override
    public Pose2d getInitialPose() {
        return PathLib.SL1Note1.getInitialTargetHolonomicPose();
    }
    
}
