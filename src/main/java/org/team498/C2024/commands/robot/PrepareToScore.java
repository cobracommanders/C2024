package org.team498.C2024.commands.robot;

import org.team498.C2024.FieldPositions;
import org.team498.C2024.commands.SetShooterNextState;
import org.team498.C2024.commands.drivetrain.TargetDrive;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class PrepareToScore extends SequentialCommandGroup {
    public PrepareToScore(){
        super(
            //new TargetDrive(FieldPositions.blueSpeaker.toPose2d()),
            new SetScoringState()
            //new SetShooterNextState()
        );
    }
}
