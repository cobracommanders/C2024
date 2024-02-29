package org.team498.C2024.commands.auto;
import org.team498.C2024.PathLib;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.C2024.commands.robot.scoring.FullScore;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class OneTaxi implements Auto{
    @Override
    public Command getCommand() {

        return new SequentialCommandGroup(
            new FullScore(0.75),
            new PathPlannerFollower(PathLib.pre_taxi_1)
        );
            
}

    @Override
    public Pose2d getInitialPose() {
        return PathLib.pre_taxi_1.getInitialTargetHolonomicPose();
    }
    
}
