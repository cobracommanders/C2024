package org.team498.C2024.commands.robot;

import org.team498.C2024.commands.SetHopperNextState;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class Score extends SequentialCommandGroup {
    public Score(){
        super(
            new SetHopperNextState(),
            new WaitCommand(.5),
            new ReturnToIdle()
        );
    }
}
