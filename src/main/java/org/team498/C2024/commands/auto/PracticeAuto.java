package org.team498.C2024.commands.auto;

import org.team498.C2024.PathLib;
import org.team498.C2024.Constants.DrivetrainConstants;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.C2024.commands.drivetrain.SlowDrive;
import org.team498.C2024.commands.robot.scoring.FullScore;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class PracticeAuto implements Auto{
    @Override
    public Command getCommand() {
        return new SequentialCommandGroup(
            new ParallelCommandGroup(
                new PathPlannerFollower(PathLib.SL1Note1),
                new SequentialCommandGroup(
                    new SlowDrive(DrivetrainConstants.AUTO_SPEED_SCALAR),
                    new FullScore(),
                    new SlowDrive(DrivetrainConstants.FULL_SPEED_SCALAR)
                )
            )
            
        );
    }

    @Override
    public Pose2d getInitialPose() {
        return PathLib.SL1Note1.getInitialTargetHolonomicPose();
    }
}
