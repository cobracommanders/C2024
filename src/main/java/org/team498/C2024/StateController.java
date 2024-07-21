package org.team498.C2024;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import org.team498.lib.field.Point;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class StateController extends SubsystemBase {
    private State currentState = State.IDLE;

    private Point currentNote = FieldPositions.midNotes[0];
    private ScoringOption nextScoringOption = ScoringOption.CRESCENDO;
    private LoadingOption nextLoadingOption = LoadingOption.GROUND;

    private Supplier<Pose2d> targetDrive = null;
    private DoubleSupplier slowDrive = ()-> 1;
    private DoubleSupplier angleOverride = null;

    public enum ScoringOption{AMP, PODIUM, SUBWOOFER, CRESCENDO}
    public enum LoadingOption{GROUND, SOURCE}

    public void setState(State state){
        //Updates the current state
        currentState = state;
    }

    //gets the current state of the Robot
    public State getState(){
        return currentState;
    }

    public State getNextScoringState() {
        State state;

        //Sets State to the next Scoring Option
        state = switch (nextScoringOption) {

            //if nextScoringOption is AMP, it sets the State to AMP
            case AMP -> State.AMP;

            //if nextScoringOption is PODIUM, it sets the State to PODIUM
            case PODIUM -> State.PODIUM;

            //if nextScoringOption is SUBWOOFER, it sets the State to SUBWOOFER
            case SUBWOOFER -> State.SUBWOOFER;

            //if nextScoringOption is CRESCENDO, it sets the State to CRESCENDO
            case CRESCENDO -> State.CRESCENDO;
        };

        return state;
    }

    public State getNextLoadingState(){
        State state;

        //Sets state to nextLoadingOption
        state = switch (nextLoadingOption) {

            //if nextLoading option is SOURCE, then it sets state to SOURCE
            case SOURCE -> State.SOURCE;

            //if nextLoadingOption is INTAKE, then it sets state to INTAKE
            case GROUND -> State.INTAKE;
        };

        return state;
    }

    //Updates nextScoringOption
    public void setNextScoringOption(ScoringOption scoringOption){
        nextScoringOption = scoringOption;
    }

    //returns nextScoringOption
    public ScoringOption getNextScoringOption(){
        return nextScoringOption;
    }

    //Updates nextLoadingOption
    public void setNextLoadingOption(LoadingOption loadingOption) {
        nextLoadingOption = loadingOption;
    }

    //returns nextLoadingOption
    public LoadingOption getNextLoadingOption(){
        return nextLoadingOption;
    }

    //Updates the current note / position of note
    public void setNote(Point note) {
        currentNote = note;
    }

    //returs current not / position of note
    public Point getNote() {
        return currentNote;
    }

    // target drive - aims at speaker while driving
    public void setTargetDrive(Pose2d target) {
        if (target == null) targetDrive = null;
        else targetDrive = ()-> target;
    }

    public Pose2d getTargetDrive() {
        if (targetDrive == null) return null;
        return targetDrive.get();
    }

    //returns target drive if it is active
    public boolean getTargetDriveActive() {
        return targetDrive != null;
    }
    public void setAngleOverride(double target) {
        if (target == -1) angleOverride = null;
        else angleOverride = ()-> target;
    }

    public double getAngleOverride() {
        if (angleOverride == null) return Double.NaN;
        return angleOverride.getAsDouble();
    }

    //returns target drive if it is active
    public boolean getAngleOverrideActive() {
        return angleOverride != null;
    }

    //slow drive
    public void setSlowDrive(double isSlow) {
        slowDrive = ()-> isSlow;
    }

    public double getSlowDrive() {
        return slowDrive.getAsDouble();
    }

    public boolean isScoring(){
        return CommandScheduler.getInstance().isScheduled(Controls.scoreCommand);
    }

    private static StateController instance;

    public static StateController getInstance() {
        if (instance == null) instance = new StateController(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
