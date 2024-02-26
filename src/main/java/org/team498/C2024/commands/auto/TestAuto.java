package org.team498.C2024.commands.auto;
import org.team498.C2024.PathLib;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class TestAuto implements Auto{
    

    @Override
    public Command getCommand() {

        return new SequentialCommandGroup(
            // new PathPlannerFollower(PathLib.SixSource1),
            // new PathPlannerFollower(PathLib.SixSource2),
            // new PathPlannerFollower(PathLib.SixSource3)
            new PathPlannerFollower(PathLib.SixAmp1)
            // new PathPlannerFollower(PathLib.SixAmp2),
            // new PathPlannerFollower(PathLib.SixAmp3)
        );
    }

    @Override
    public Pose2d getInitialPose() {
        return PathLib.SixAmp1.getInitialTargetHolonomicPose();//PathLib.MidSubwooferToFirstNote.getInitialTargetHolonomicPose();
    }
    
}
