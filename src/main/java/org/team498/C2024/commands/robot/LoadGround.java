package org.team498.C2024.commands.robot;

import org.team498.C2024.State;
import org.team498.C2024.commands.SetIntakeNextState;
import org.team498.C2024.commands.SetIntakeRollersNextState;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class LoadGround extends SequentialCommandGroup{
    public LoadGround(){
        super(
            new SetState(State.INTAKE),
            new SetIntakeNextState(),
            new SetIntakeRollersNextState()
        );
    }
}
