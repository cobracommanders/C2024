package org.team498.C2024.commands.robot.loading;

import org.team498.C2024.State;
import org.team498.C2024.commands.hopper.SetHopperNextState;
import org.team498.C2024.commands.intake.SetIntakeNextState;
import org.team498.C2024.commands.intake.SetIntakeRollersNextState;
import org.team498.C2024.commands.robot.SetState;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class Outtake extends SequentialCommandGroup{
    public Outtake(){
        super(
            // Set Intake and intakeRollers to state INTAKE
            new SetState(State.OUTTAKE),
            //Intake Wrist comes out at the same time that the wait command starts
            new ParallelCommandGroup(
                //Intake Rollers wait .1 seconds to start soinning
                new SequentialCommandGroup(
                    new WaitCommand(.1),
                    new SetIntakeRollersNextState()
                ),
                // new SetKickerNextState(),
                new SetIntakeNextState()
            ),
            // If the BeamBreak is enabled then it stores the Note and if it's not enabled then it Sets the hopper to the next state
            new SetHopperNextState()
        );
    }
}
