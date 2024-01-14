package org.team498.C2024.commands.auto;

import org.team498.C2024.PathLib;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FiveNoteAuto implements Auto{
    @Override
    public Command getCommand() {

        return new SequentialCommandGroup(
            new PathPlannerFollower(PathLib.SL1toNote1),
            new PathPlannerFollower(PathLib.Note1toNote2),
            new PathPlannerFollower(PathLib.Note2Note3),
            new PathPlannerFollower(PathLib.Note3Mid5),
            new PathPlannerFollower(PathLib.Mid5Note3)
            
        );
    }

    @Override
    public Pose2d getInitialPose() {
        return PathLib.GavinsPath.getInitialTargetHolonomicPose();
    }
    
}

