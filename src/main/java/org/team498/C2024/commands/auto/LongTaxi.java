package org.team498.C2024.commands.auto;
import org.team498.C2024.PathLib;
import org.team498.C2024.commands.drivetrain.PathFollowerX;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.C2024.commands.robot.ReturnToIdle;
import org.team498.C2024.commands.robot.loading.LoadGround;
import org.team498.C2024.commands.robot.scoring.FullScore;
import org.team498.C2024.commands.robot.scoring.SubwooferScore;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class LongTaxi implements Auto{
    @Override
    public Command getCommand() {

        return new SequentialCommandGroup(
            new  WaitCommand(0.5),
            new SubwooferScore(3),
            //new ParallelDeadlineGroup( 
                new PathPlannerFollower(PathLib.long_taxi), //new LoadGround()),
            new ReturnToIdle()
        );
            
}

    @Override
    public Pose2d getInitialPose() {
        return PathLib.long_taxi.getInitialTargetHolonomicPose();
    }
    
}
