package org.team498.C2024;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class StateController extends SubsystemBase {
    private State currentState = State.IDLE;
    private ScoringOption nextScoringOption = ScoringOption.CRESCENDO;
    private LoadingOption nextLoadingOption = LoadingOption.GROUND;

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

    private static StateController instance;

    public static StateController getInstance() {
        if (instance == null) instance = new StateController(); // Make sure there is an instance (this will only run once)
        return instance;
    }
}
