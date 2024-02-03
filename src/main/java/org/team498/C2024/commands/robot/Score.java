package org.team498.C2024.commands.robot;

import org.team498.C2024.State;
import org.team498.C2024.commands.SetHopperNextState;
import org.team498.C2024.commands.SetKickerNextState;
import org.team498.C2024.commands.SetShooterNextState;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class Score extends SequentialCommandGroup {
    public Score(){
        super(
            new SetHopperNextState(),
            new SetShooterNextState(),
            new SetKickerNextState(),
            new WaitCommand(.5),
            new SetState(State.IDLE),
            new SetShooterNextState(),
            new SetHopperNextState(),
            new SetKickerNextState()
        );
    }
    
}
