package org.team498.C2024.commands.robot.scoring;

import org.team498.C2024.FieldPositions;
import org.team498.C2024.Robot;
import org.team498.C2024.State;
import org.team498.C2024.StateController;
import org.team498.C2024.commands.drivetrain.TargetDrive;
import org.team498.C2024.commands.robot.SetScoringState;
import org.team498.C2024.subsystems.CommandSwerveDrivetrain;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class PrepareToScore extends SequentialCommandGroup {
    public PrepareToScore(){
        super(

            //sets state
            new SetScoringState(),

            //If the state is AMP it runs PrepareAmp and if the state isn't AMP it runs PrepareSpeaker
            new ConditionalCommand(new PrepareAmp(), new PrepareSpeaker(), ()-> StateController.getInstance().getState() == State.AMP)
        );
    }
}
