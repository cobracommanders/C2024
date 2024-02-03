package org.team498.C2024.commands.robot;

import org.team498.C2024.FieldPositions;
import org.team498.C2024.commands.MoveHopper;
import org.team498.C2024.commands.SetKickerNextState;
import org.team498.C2024.commands.SetShooterNextState;
import org.team498.C2024.commands.drivetrain.TargetDrive;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class PrepareSpeaker extends SequentialCommandGroup {
    public PrepareSpeaker(){
        super(
            new SetShooterNextState(),
            new SetKickerNextState(),
            new MoveHopper(1)
        );
    }
}