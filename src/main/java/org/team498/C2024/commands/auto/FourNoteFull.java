package org.team498.C2024.commands.auto;
import org.team498.C2024.FieldPositions;
import org.team498.C2024.PathLib;
import org.team498.C2024.StateController;
import org.team498.C2024.Constants.DrivetrainConstants;
import org.team498.C2024.StateController.ScoringOption;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.C2024.commands.drivetrain.SetNoteTarget;
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

public class FourNoteFull implements Auto{
    @Override
    public Command getCommand() {

        return new ParallelCommandGroup(
            new PathPlannerFollower(PathLib.FourNoteFull),
            new SequentialCommandGroup(

                    new InstantCommand(() -> StateController.getInstance().setNextScoringOption(ScoringOption.CRESCENDO)),
                    new TargetSpeaker(),
                    new SlowDrive(DrivetrainConstants.AUTO_SPEED_SCALAR),
                    new FullScore(),
                    new TargetDrive(null),
                    new SlowDrive(DrivetrainConstants.FULL_SPEED_SCALAR),

                    new WaitCommand(1),
                    new LoadGround(),
                    new WaitCommand(1),
                    new TargetSpeaker(),
                    new SlowDrive(DrivetrainConstants.AUTO_SPEED_SCALAR),
                    new FullScore(),
                    new TargetDrive(null),
                    new SlowDrive(DrivetrainConstants.FULL_SPEED_SCALAR),

                    new WaitCommand(1),
                    new LoadGround(),
                    new WaitCommand(1),
                    new TargetSpeaker(),
                    new SlowDrive(DrivetrainConstants.AUTO_SPEED_SCALAR),
                    new FullScore(),
                    new TargetDrive(null),
                    new SlowDrive(DrivetrainConstants.FULL_SPEED_SCALAR),

                    new WaitCommand(1),
                    new LoadGround(),
                    new WaitCommand(1),
                    new TargetSpeaker(),
                    new SlowDrive(DrivetrainConstants.AUTO_SPEED_SCALAR),
                    new FullScore(),
                    new TargetDrive(null),
                    new SlowDrive(DrivetrainConstants.FULL_SPEED_SCALAR)));
    }

    @Override
    public Pose2d getInitialPose() {
        return PathLib.FourNoteFull.getInitialTargetHolonomicPose();
    }
    
}
