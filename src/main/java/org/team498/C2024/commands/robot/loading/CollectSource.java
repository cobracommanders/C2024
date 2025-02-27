package org.team498.C2024.commands.robot.loading;

import java.util.List;

import org.team498.C2024.Robot;
import org.team498.C2024.commands.drivetrain.OTFFollower;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class CollectSource extends ParallelCommandGroup{
    private static List<Pose2d> waypoints = List.of(new Pose2d(15.18, 1.32, Rotation2d.fromDegrees(-50.00)), new Pose2d(15.46, 0.91, Rotation2d.fromDegrees(-50.00)));
    public CollectSource(){
        super(
            new OTFFollower(waypoints, Robot.controls.driver.rightBumper())
            //new LoadSource()
        );
    }
}
