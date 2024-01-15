package org.team498.C2024.commands.robot;

import org.team498.C2024.State;
import org.team498.C2024.commands.SetHopperNextState;
import org.team498.C2024.commands.SetIntakeNextState;
import org.team498.C2024.commands.SetIntakeRollersNextState;
import org.team498.C2024.commands.SetShooterNextState;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ReturnToIdle extends SequentialCommandGroup {
    public ReturnToIdle(){
        super(
            new SetState(State.IDLE),

            new ParallelCommandGroup(
                new SetHopperNextState(),
                new SetIntakeNextState(),
                new SetIntakeRollersNextState(),
                new SetShooterNextState()
            )
        );
    }
}
