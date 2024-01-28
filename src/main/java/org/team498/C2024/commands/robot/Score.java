package org.team498.C2024.commands.robot;

import org.team498.C2024.State;
import org.team498.C2024.commands.drivetrain.SlowDrive;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class Score extends SequentialCommandGroup {
    public Score(){
        super(
            new SlowDrive(true),
            //new SetHopperNextState(),
            new WaitCommand(2),
            new SetState(State.IDLE),
            //new SetShooterNextState(),
            //new SetHopperNextState(),
            new SlowDrive(false)
        );
    }
    
}
