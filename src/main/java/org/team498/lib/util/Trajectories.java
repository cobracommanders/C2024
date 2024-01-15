package org.team498.lib.util;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.Filesystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.team498.C2024.Constants.DrivetrainConstants;
import org.team498.C2024.Constants.DrivetrainConstants.AngleConstants;

import com.pathplanner.lib.path.GoalEndState;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;

public class Trajectories {
    public static Trajectory getTrajectory(String name) {
        String filepath = "pathplanner/generatedJSON/" + name + ".wpilib.json";

        Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(filepath);
        try {
            return TrajectoryUtil.fromPathweaverJson(trajectoryPath);
        } catch (IOException e) {
            e.printStackTrace();
            return new Trajectory();
        }
    }
    public static PathPlannerTrajectory getChoreoTrajectory(String name){
        PathPlannerPath path = PathPlannerPath.fromChoreoTrajectory(name);
        ChassisSpeeds speeds = new ChassisSpeeds();
        Rotation2d rotation = path.getAllPathPoints().get(0).rotationTarget.getTarget();
        return path.getTrajectory(speeds, rotation);
    }   
    public static PathPlannerTrajectory getPathPlannerTrajectory(String name) {
        PathPlannerPath path = PathPlannerPath.fromPathFile(name);
        
        ChassisSpeeds speeds = new ChassisSpeeds();
        Rotation2d rotation = path.getAllPathPoints().get(0).rotationTarget.getTarget();
        return path.getTrajectory(speeds, rotation);
    }

    public static PathPlannerTrajectory getOTFTrajectory(List<Pose2d> poses) {
        PathConstraints constraints = new PathConstraints(DrivetrainConstants.MAX_VELOCITY_METERS_PER_SECOND, DrivetrainConstants.MAX_ACCELERATION_METERS_PER_SECOND_SQUARED, AngleConstants.MAX_ANGULAR_SPEED_DEGREES_PER_SECOND / 360.0, AngleConstants.MAX_ANGULAR_SPEED_DEGREES_PER_SECOND_SQUARED / 360.0);
        Rotation2d endRotation = poses.get(poses.size() - 1).getRotation();
        double endVelocity = 0;
        GoalEndState endState = new GoalEndState(endVelocity, endRotation);
        PathPlannerPath path = new PathPlannerPath(PathPlannerPath.bezierFromPoses(poses), constraints, endState, false);
        ChassisSpeeds speeds = new ChassisSpeeds();
        Rotation2d rotation = path.getAllPathPoints().get(0).rotationTarget.getTarget();
        return path.getTrajectory(speeds, rotation);
    }

    public static PathPlannerTrajectory getOTFTrajectory(List<Pose2d> poses, double maxVelocityMPS, double maxAccelMPSSq) {
        PathConstraints constraints = new PathConstraints(maxVelocityMPS, maxAccelMPSSq, AngleConstants.MAX_ANGULAR_SPEED_DEGREES_PER_SECOND / 360.0, AngleConstants.MAX_ANGULAR_SPEED_DEGREES_PER_SECOND_SQUARED / 360.0);
        Rotation2d endRotation = poses.get(poses.size() - 1).getRotation();
        double endVelocity = 0;
        GoalEndState endState = new GoalEndState(endVelocity, endRotation);
        PathPlannerPath path = new PathPlannerPath(PathPlannerPath.bezierFromPoses(poses), constraints, endState, false);
        ChassisSpeeds speeds = new ChassisSpeeds();
        Rotation2d rotation = path.getAllPathPoints().get(0).rotationTarget.getTarget();
        return path.getTrajectory(speeds, rotation);
    }
    
    //Get trajectory doesnt require starting speeds/rotation cause of Choreo
//    //public static PathPlannerTrajectory getPathPlannerTrajectory(String name) {
//         // return PathPlanner.loadPath(name, 3, 2.4);
//         return PathPlannerPath.loadPath(name, 3, 2);
//         PathPlannerAuto.getPathGroupFromAutoFile().
//     }
}
