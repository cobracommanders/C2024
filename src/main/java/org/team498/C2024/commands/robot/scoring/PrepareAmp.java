package org.team498.C2024.commands.robot.scoring;

import org.team498.C2024.State;
import org.team498.C2024.commands.MoveHopper;
import org.team498.C2024.commands.SetKickerNextState;
import org.team498.C2024.commands.SetKickerState;
import org.team498.C2024.commands.robot.SetState;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class PrepareAmp extends SequentialCommandGroup {
    public PrepareAmp(){
        super(
            //Starts in AMP state

            // Sets Kicker to forward
            new SetKickerState(State.Kicker.FORWARD),

            // Moves Note into to Kicker
            new MoveHopper(-4),

            // Sets Kicker to Idle
            new SetState(State.IDLE),
            new SetKickerNextState(),

            // Sets robot state back to AMP
            new SetState(State.AMP)
        );
    }
}