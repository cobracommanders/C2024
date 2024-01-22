package org.team498.C2024;
import org.team498.lib.util.Trajectories;
import com.pathplanner.lib.path.PathPlannerTrajectory;

import edu.wpi.first.math.geometry.Rotation2d;

public class PathLib {
    //public static final PathPlannerTrajectory choreoTest = Trajectories.getChoreoTrajectory("NewPath");
    public static final PathPlannerTrajectory FifthNoteToSecondNoteUnderStage = Trajectories.getChoreoTrajectory("FifthNoteToSecondNoteUnderStage", Rotation2d.fromRadians(0));
    public static final PathPlannerTrajectory FirstNoteToFifthNote = Trajectories.getChoreoTrajectory("FirstNoteToFifthNote", Rotation2d.fromRadians(1.056));
    public static final PathPlannerTrajectory MidSubwooferToFirstNote = Trajectories.getChoreoTrajectory("MidSubwooferToFirstNote", Rotation2d.fromRadians(0));
    public static final PathPlannerTrajectory GavinsPath = Trajectories.getPathPlannerTrajectory("GavinsPath", Rotation2d.fromDegrees(0));
    public static final PathPlannerTrajectory SL1toNote1 = Trajectories.getChoreoTrajectory("SL1toNote1", Rotation2d.fromRadians(-3.14));
    public static final PathPlannerTrajectory Note1toNote2 = Trajectories.getChoreoTrajectory("Note1toNote2", Rotation2d.fromRadians(-3.14));
    public static final PathPlannerTrajectory Note2Note3 = Trajectories.getChoreoTrajectory("Note2Note3", Rotation2d.fromRadians(-1.695));
    public static final PathPlannerTrajectory SL1Note1 = Trajectories.getChoreoTrajectory("SL1Note1", Rotation2d.fromDegrees(-90));
    // public static final PathPlannerTrajectory Note3Mid5 = Trajectories.getChoreoTrajectory("Note3Mid5");
    // public static final PathPlannerTrajectory Mid5Note3 = Trajectories.getChoreoTrajectory("Mid5Note3");
}