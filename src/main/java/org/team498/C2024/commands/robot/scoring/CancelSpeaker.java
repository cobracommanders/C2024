package org.team498.C2024.commands.robot.scoring;

import org.team498.C2024.State;
import org.team498.C2024.StateController;
import org.team498.C2024.commands.MoveHopper;
import org.team498.C2024.commands.SetKickerNextState;
import org.team498.C2024.commands.SetShooterNextState;
import org.team498.C2024.commands.robot.SetState;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class CancelSpeaker extends ConditionalCommand {
    public CancelSpeaker(){
        super(

            // does nothing if scoring
            new WaitCommand(0),

            //cancel speaker if you're not scoring
            new SequentialCommandGroup(
                // Sets Kicker to Forward and Shooter to Idle
                new SetState(State.CANCEL_SPEAKER),
                new SetShooterNextState(),
                new SetKickerNextState(),

                //Moves Note back into Hopper
                new MoveHopper(-1),

                //Sets Kicker and Shooter to Idle
                new SetState(State.IDLE),
                new SetShooterNextState(),
                new SetKickerNextState()
            ),

            //checks if the score command is scheduled
            StateController.getInstance()::isScoring
            
        );
    }
}