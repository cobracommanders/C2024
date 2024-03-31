package org.team498.C2024.commands.auto;
import org.team498.C2024.PathLib;
import org.team498.C2024.Constants.DrivetrainConstants;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.C2024.commands.drivetrain.SlowDrive;
import org.team498.C2024.subsystems.Drivetrain;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class TestPathing implements Auto{
    @Override
    public Command getCommand() {

        return new SequentialCommandGroup(
            new SlowDrive(DrivetrainConstants.SLOW_SPEED_SCALAR),
            new PathPlannerFollower(PathLib.outer_wing_2)
        );
            
}

    @Override
    public Pose2d getInitialPose() {
        return PathLib.outer_wing_2.getInitialTargetHolonomicPose();
    }
    
}
