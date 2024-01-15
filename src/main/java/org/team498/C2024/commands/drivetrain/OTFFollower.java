package org.team498.C2024.commands.drivetrain;

import java.util.LinkedList;
import java.util.List;

import org.team498.C2024.Robot;
import org.team498.C2024.RobotPosition;
import org.team498.C2024.subsystems.Drivetrain;
import org.team498.lib.util.PoseUtil;
import org.team498.lib.util.Trajectories;

import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.path.PathPlannerTrajectory.State;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;

public class OTFFollower extends Command{

    private final Timer trajectoryTimer = new Timer();
    private PathPlannerTrajectory trajectory;

    private List <Pose2d> waypoints;
    public OTFFollower(List <Pose2d> poses){
        waypoints = poses;
    }
    @Override
    public void initialize() {
        waypoints.add(0, Drivetrain.getInstance().getPose());
        trajectory = Trajectories.getOTFTrajectory(waypoints);
        
        trajectoryTimer.reset();
        trajectoryTimer.start();

        // Display the trajectory on the driver station dashboard
        List<Pose2d> poses = new LinkedList<>();
        List<State> trajectoryStates = trajectory.getStates();
        for (int i = 0; i < trajectoryStates.size(); i += trajectoryStates.size() / 60) {
            if (Robot.alliance.get() == Alliance.Blue) {
                poses.add(PoseUtil.toPose2d(trajectoryStates.get(i).positionMeters));
            } else {
                poses.add(PoseUtil.flip(PoseUtil.toPose2d(trajectoryStates.get(i).positionMeters)));
            }
        }



    }
    
     @Override
    public void execute() {
            State state = trajectory.sample(trajectoryTimer.get());

            if (Robot.alliance.get() == Alliance.Blue) {
                Drivetrain.getInstance().setPositionGoal(new Pose2d(state.positionMeters.getX(), state.positionMeters.getY(), state.targetHolonomicRotation));
            } else {
                Drivetrain.getInstance().setPositionGoal(PoseUtil.flip(new Pose2d(state.positionMeters.getX(), state.positionMeters.getY(), state.targetHolonomicRotation)));
            }

            var speeds = Drivetrain.getInstance().calculatePositionSpeed();
            Drivetrain.getInstance().drive(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond, speeds.omegaRadiansPerSecond, true);
        // }
    }

    @Override
    public boolean isFinished() {
        if (Robot.controls.driver.isLeftStickActive() || Robot.controls.driver.isRightStickActive())
            return true;
        // return trajectoryTimer.get() > trajectory.getTotalTimeSeconds();
        if (Robot.alliance.get() == Alliance.Blue) {
            return RobotPosition.isNear(PoseUtil.toPose2d(trajectory.getEndState().positionMeters), 0.1);
        } else {
            return RobotPosition.isNear(PoseUtil.flip(PoseUtil.toPose2d(trajectory.getEndState().positionMeters)), 0.1);
        }
    }

    @Override
    public void end(boolean interrupted) {
        Drivetrain.getInstance().stop();
    }
}
