package org.team498.C2024.commands.robot;

import org.team498.C2024.State;
import org.team498.C2024.commands.MoveHopper;
import org.team498.C2024.commands.SetKickerNextState;
import org.team498.C2024.commands.SetShooterNextState;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class CancelSpeaker extends SequentialCommandGroup {
    public CancelSpeaker(){
        super(
            new SetState(State.CANCEL_SPEAKER),
            new SetShooterNextState(),
            new SetKickerNextState(),
            new MoveHopper(-1),
            new SetState(State.IDLE),
            new SetShooterNextState(),
            new SetKickerNextState()
        );
    }
}