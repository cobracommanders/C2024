package org.team498.C2024;
import org.team498.lib.util.Trajectories;
import com.pathplanner.lib.path.PathPlannerTrajectory;

public class PathLib {
    public static final PathPlannerTrajectory choreoTest = Trajectories.getChoreoTrajectory("NewPath");
    public static final PathPlannerTrajectory FifthNoteToSecondNoteUnderStage = Trajectories.getChoreoTrajectory("Fifth Note To Second Note Under Stage");
    public static final PathPlannerTrajectory FirstNoteToFifthNote = Trajectories.getChoreoTrajectory("First Note To Fifth Note");
    public static final PathPlannerTrajectory MidSubwooferToFirstNote = Trajectories.getChoreoTrajectory("Mid Subwoofer To First Note");
    public static final PathPlannerTrajectory GavinsPath = Trajectories.getChoreoTrajectory("Gavins Path");
    public static final PathPlannerTrajectory SL1toNote1 = Trajectories.getChoreoTrajectory("SL1toNote1");
    public static final PathPlannerTrajectory Note1toNote2 = Trajectories.getChoreoTrajectory("Note1toNote2");
    public static final PathPlannerTrajectory Note2Note3 = Trajectories.getChoreoTrajectory("Note2Note3");
    public static final PathPlannerTrajectory Note3Mid5 = Trajectories.getChoreoTrajectory("Note3Mid5");
    public static final PathPlannerTrajectory Mid5Note3 = Trajectories.getChoreoTrajectory("Mid5Note3");
}