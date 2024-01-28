package org.team498.C2024.commands.robot;

import org.team498.C2024.State;
import org.team498.C2024.commands.SetHopperNextState;
import org.team498.C2024.subsystems.Hopper;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public class StoreNote extends Command{
    private final Hopper hopper = Hopper.getInstance();
    public StoreNote(){
        new SequentialCommandGroup(
            new SetState(State.INTAKE),
            new SetHopperNextState(),
            new WaitUntilCommand(() -> hopper.getBeamBreak()),
            new SetState(State.IDLE),
            new SetHopperNextState()
        );
    }
}
