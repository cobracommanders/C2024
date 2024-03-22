package org.team498.C2024.commands.auto;
import org.team498.C2024.Constants.DrivetrainConstants;
import org.team498.C2024.PathLib;
import org.team498.C2024.State;
import org.team498.C2024.commands.drivetrain.PathFollowerX;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.C2024.commands.drivetrain.SlowDrive;
import org.team498.C2024.commands.drivetrain.TargetDrive;
import org.team498.C2024.commands.drivetrain.TargetSpeaker;
import org.team498.C2024.commands.robot.ReturnToIdle;
import org.team498.C2024.commands.robot.SetIntakeIdle;
import org.team498.C2024.commands.robot.SetState;
import org.team498.C2024.commands.robot.loading.LoadGround;
import org.team498.C2024.commands.robot.scoring.AutoScore;
import org.team498.C2024.commands.robot.scoring.FullScore;
import org.team498.C2024.commands.robot.scoring.PrepareToScore;
import org.team498.C2024.commands.robot.scoring.Score;
import org.team498.C2024.commands.robot.scoring.SubwooferScore;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class LongTaxi implements Auto{
    @Override
    public Command getCommand() {

        return new SequentialCommandGroup(
            new SubwooferScore(2),
            new ParallelDeadlineGroup( 
                new PathPlannerFollower(PathLib.long_taxi),
                new SequentialCommandGroup(
                    new WaitCommand(2),
                    new LoadGround().withTimeout(2),
                    new SetIntakeIdle()
                    // new WaitCommand(1),
                    // new PrepareToScore(),
                    // new AutoScore()
                )
            )
        );
            
            //     new SlowDrive(DrivetrainConstants.AUTO_SPEED_SCALAR),
            //     new WaitCommand(0.5),
            //     new TargetSpeaker(),
            //     new SubwooferScore(1),
            //     new TargetDrive(null),
            //     new SlowDrive(DrivetrainConstants.FULL_SPEED_SCALAR)
            // );
            
}

    @Override
    public Pose2d getInitialPose() {
        return PathLib.long_taxi.getInitialTargetHolonomicPose();
    }
    
}
