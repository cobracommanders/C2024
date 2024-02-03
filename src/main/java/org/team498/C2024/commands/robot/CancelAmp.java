package org.team498.C2024.commands.robot;

import org.team498.C2024.State;
import org.team498.C2024.commands.MoveHopper;
import org.team498.C2024.commands.SetKickerNextState;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class CancelAmp extends SequentialCommandGroup {
    public CancelAmp(){
        super(
            new SetState(State.CANCEL_AMP),
            new SetKickerNextState(),
            new MoveHopper(-1),
            new SetState(State.IDLE),
            new SetKickerNextState()
        );
    }
}