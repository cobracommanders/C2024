package org.team498.C2024.commands.robot.scoring;

import org.team498.C2024.FieldPositions;
import org.team498.C2024.State;
import org.team498.C2024.commands.MoveHopper;
import org.team498.C2024.commands.SetKickerNextState;
import org.team498.C2024.commands.SetShooterNextState;
import org.team498.C2024.commands.drivetrain.TargetDrive;
import org.team498.C2024.commands.robot.SetScoringState;
import org.team498.C2024.commands.robot.SetState;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class PrepareSpeaker extends SequentialCommandGroup {
    public PrepareSpeaker(){
        super(
            // Starts in PODIUM, SUBWOOFER, or CRESCENDO

            // Sets Shooter to Speed and sets kicker to REVERSE
            new SetShooterNextState(),
            new SetKickerNextState(),

            // Moves Note into Shooter
            new MoveHopper(1),

            //Sets Kicker to IDLE
            new SetState(State.IDLE),
            new SetKickerNextState(),

            //Sets state Back to Speaker
            new SetScoringState()
        );
    }
}