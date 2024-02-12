package org.team498.C2024.commands.robot.scoring;

import org.team498.C2024.State;
import org.team498.C2024.StateController;
import org.team498.C2024.commands.MoveHopper;
import org.team498.C2024.commands.SetKickerNextState;
import org.team498.C2024.commands.SetKickerState;
import org.team498.C2024.commands.robot.SetState;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class CancelAmp extends ConditionalCommand{
    public CancelAmp(){
        super(

            new WaitCommand(0),

            new SequentialCommandGroup(
                //Set Kicker to Reverse
                new SetState(State.CANCEL_AMP),
                new SetKickerState(State.Kicker.REVERSE),

                //Moves Note back to hopper
                new MoveHopper(4),

                //Set kicker to Idle
                new SetState(State.IDLE),
                new SetKickerNextState()
            ),

            //checks if the score command is scheduled
            StateController.getInstance()::isScoring

        );
    }
}