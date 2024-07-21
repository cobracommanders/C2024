package org.team498.C2024.commands.auto;
import org.team498.C2024.Constants.DrivetrainConstants;
import org.team498.C2024.PathLib;
import org.team498.C2024.StateController;
import org.team498.C2024.StateController.ScoringOption;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.C2024.commands.drivetrain.SlowDrive;
import org.team498.C2024.commands.drivetrain.TargetDrive;
import org.team498.C2024.commands.drivetrain.TargetSpeaker;
import org.team498.C2024.commands.robot.SetIntakeIdle;
import org.team498.C2024.commands.robot.loading.LoadGround;
import org.team498.C2024.commands.robot.scoring.FullScore;
import org.team498.C2024.subsystems.Drivetrain;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class SixNoteAmp implements Auto{
    @Override
    public Command getCommand() {

        return new SequentialCommandGroup(

            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.SixAmp1),
                new SequentialCommandGroup(
                    new FullScore(0.1),
                    // new WaitCommand(0.5),
                    new LoadGround(),
                    // new WaitCommand(1.25),
                    new SetIntakeIdle(),
                    new FullScore(0.1),
                    new LoadGround()
                )
            ),
            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.SixAmp2),
                new SequentialCommandGroup(
                    new FullScore(0.1),
                    // new WaitCommand(0.5),
                    new LoadGround(),
                    // new WaitCommand(1.25),
                    new SetIntakeIdle(),
                    new FullScore(0.1),
                    new LoadGround()
                )
            ),

            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.SixAmp3),
                new SequentialCommandGroup(
                    new FullScore(0.1),
                    // new WaitCommand(0.5),
                    new LoadGround(),
                    // new WaitCommand(1.25),
                    new SetIntakeIdle(),
                    new FullScore(0.1),
                    new LoadGround()
                )
            )
        );
}

    @Override
    public Pose2d getInitialPose() {
        return PathLib.SixAmp1.getInitialTargetHolonomicPose();
    }
    
}
