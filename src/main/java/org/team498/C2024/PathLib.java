package org.team498.C2024;
import org.team498.lib.util.Trajectories;
import com.pathplanner.lib.path.PathPlannerTrajectory;

public class PathLib {
    public static final PathPlannerTrajectory choreoTest = Trajectories.getChoreoTrajectory("NewPath");
    public static final PathPlannerTrajectory FifthNoteToSecondNoteUnderStage = Trajectories.getChoreoTrajectory("Fifth Note To Second Note Under Stage");
    public static final PathPlannerTrajectory FirstNoteToFifthNote = Trajectories.getChoreoTrajectory("First Note To Fifth Note");
    public static final PathPlannerTrajectory MidSubwooferToFirstNote = Trajectories.getChoreoTrajectory("Mid Subwoofer To First Note");
}