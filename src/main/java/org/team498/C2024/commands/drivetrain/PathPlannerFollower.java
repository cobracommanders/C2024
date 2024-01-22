package org.team498.C2024.commands.drivetrain;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;

import java.util.Optional;

import org.team498.C2024.Robot;
import org.team498.C2024.RobotPosition;
import org.team498.C2024.subsystems.Drivetrain;
import org.team498.lib.util.PoseUtil;
import org.team498.lib.wpilib.ChassisSpeeds;

import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.path.PathPlannerTrajectory.State;

public class PathPlannerFollower extends Command {
    private final Drivetrain drivetrain;
    private final PathPlannerTrajectory trajectory;
    private final Timer trajectoryTimer = new Timer();
    private double currentTime;
    private double dt;
    private boolean slowDrive;
    public static Optional<Pose2d> targetPose;

    public PathPlannerFollower(PathPlannerTrajectory trajectory) {
        this.drivetrain = Drivetrain.getInstance();
        this.trajectory = trajectory;
        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        trajectoryTimer.reset();
        trajectoryTimer.start();
        currentTime = 0;
        dt = 0;
        targetPose = Optional.empty();
        slowDrive = false;

        // Display the trajectory on the driver station dashboard
        // List<Pose2d> poses = new LinkedList<>();
        // List<State> trajectoryStates = trajectory.getStates();
        // for (int i = 0; i < trajectoryStates.size(); i += trajectoryStates.size() / 60) {
        //     if (Robot.alliance.get() == Alliance.Blue) {
        //         poses.add(PoseUtil.toPose2d(trajectoryStates.get(i).positionMeters));
        //     } else {
        //         poses.add(PoseUtil.flip(PoseUtil.toPose2d(trajectoryStates.get(i).positionMeters)));
        //     }
        // }


    }

    // boolean hasReset = false;

    @Override
    public void execute() {
        slowDrive = Robot.slowDrive;
        dt = trajectoryTimer.get();
        trajectoryTimer.restart();
        currentTime += slowDrive ? dt / 2 : dt;
        State state = trajectory.sample(currentTime);

        if (Robot.alliance.get() == Alliance.Blue) {
            drivetrain.setPositionGoal(new Pose2d(state.positionMeters.getX(), state.positionMeters.getY(), state.targetHolonomicRotation));
            if (targetPose.isPresent()) drivetrain.setAngleGoal(RobotPosition.calculateDegreesToTarget(targetPose.get()));
        } else {
            drivetrain.setPositionGoal(PoseUtil.flip(new Pose2d(state.positionMeters.getX(), state.positionMeters.getY(), state.targetHolonomicRotation)));
            if (targetPose.isPresent()) drivetrain.setAngleGoal(RobotPosition.calculateDegreesToTarget(PoseUtil.flip(targetPose.get())));
        }
        ChassisSpeeds speeds = drivetrain.calculatePositionSpeed();
        drivetrain.drive(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond, speeds.omegaRadiansPerSecond, true);
    }

    @Override
    public boolean isFinished() {
        // return trajectoryTimer.get() > trajectory.getTotalTimeSeconds();
        if (Robot.alliance.get() == Alliance.Blue) {
            return RobotPosition.isNear(PoseUtil.toPose2d(trajectory.getEndState().positionMeters), 0.1);
        } else {
            return RobotPosition.isNear(PoseUtil.flip(PoseUtil.toPose2d(trajectory.getEndState().positionMeters)), 0.1);
        }
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.stop();
    }
}
