package org.team498.C2024;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.team498.C2024.commands.robot.scoring.Score;
import org.team498.lib.field.Point;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class StateController extends SubsystemBase {
    private State currentState = State.IDLE;

    private Point currentNote = FieldPositions.midNotes[0];
    private ScoringOption nextScoringOption = ScoringOption.CRESCENDO;
    private LoadingOption nextLoadingOption = LoadingOption.GROUND;

    private Supplier<Pose2d> targetDrive = null;
    private BooleanSupplier slowDrive = ()-> false;

    public enum ScoringOption{AMP, PODIUM, SUBWOOFER, CRESCENDO}
    public enum LoadingOption{GROUND, SOURCE}

    public void setState(State state){
        currentState = state;
    }

    public State getState(){
        return currentState;
    }

    public State getNextScoringState() {
        State state;

        state = switch (nextScoringOption) {
            case AMP -> State.AMP;
            case PODIUM -> State.PODIUM;
            case SUBWOOFER -> State.SUBWOOFER;
            case CRESCENDO -> State.CRESCENDO;
        };

        return state;
    }

    public State getNextLoadingState(){
        State state;

        state = switch (nextLoadingOption) {
            case SOURCE -> State.SOURCE;
            case GROUND -> State.INTAKE;
        };
        return state;
    }
    public void setNextScoringOption(ScoringOption scoringOption){
        nextScoringOption = scoringOption;
    }
    public ScoringOption getNextScoringOption(){
        return nextScoringOption;
    }

    public void setNextLoadingOption(LoadingOption loadingOption) {
        nextLoadingOption = loadingOption;
    }
    public LoadingOption getNextLoadingOption(){
        return nextLoadingOption;
    }
    public void setNote(Point note) {
        currentNote = note;
    }
    public Point getNote() {
        return currentNote;
    }
    // target drive
    public void setTargetDrive(Pose2d target) {
        if (target == null) targetDrive = null;
        else targetDrive = ()-> target;
    }
    public Pose2d getTargetDrive() {
        if (targetDrive == null) return null;
        return targetDrive.get();
    }
    public boolean getTargetDriveActive() {
        return targetDrive != null;
    }
    //slow drive
    public void setSlowDrive(boolean isSlow) {
        slowDrive = ()-> isSlow;
    }
    public boolean getSlowDrive() {
        return slowDrive.getAsBoolean();
    }

    public boolean isScoring(){
        return CommandScheduler.getInstance().isScheduled(new Score());
    }

    private static StateController instance;

    public static StateController getInstance() {
        if (instance == null) instance = new StateController(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
