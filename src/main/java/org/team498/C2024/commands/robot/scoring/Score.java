package org.team498.C2024.commands.robot.scoring;

import org.team498.C2024.State;
import org.team498.C2024.commands.SetHopperNextState;
import org.team498.C2024.commands.SetKickerNextState;
import org.team498.C2024.commands.SetShooterNextState;
import org.team498.C2024.commands.robot.SetState;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class Score extends SequentialCommandGroup {
    public Score(){
        super(

            //Starts in CRESCENDO, SUBWOOFER, PODIUM, or AMP

            //Sets Hopper, Shooter and Kicker to Next state
            new SetHopperNextState(),
            new SetShooterNextState(),
            new SetKickerNextState(),

            //Waits for Note to shoot
            new WaitCommand(1.5),

            // Sets Shooter, Hopper and Kicker to IDLE
            new SetState(State.IDLE),
            new SetShooterNextState(),
            new SetHopperNextState(),
            new SetKickerNextState()
        );
    }
    
}
