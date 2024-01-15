package org.team498.C2024.commands.robot;

import org.team498.C2024.commands.SetShooterNextState;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class PrepareToScore extends SequentialCommandGroup {
    public PrepareToScore(){
        super(
            new SetScoringState(),
            new SetShooterNextState()
        );
    }
}
