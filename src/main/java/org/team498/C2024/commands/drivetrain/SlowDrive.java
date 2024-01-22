package org.team498.C2024.commands.drivetrain;
import org.team498.C2024.Robot;

import edu.wpi.first.wpilibj2.command.InstantCommand;

public class SlowDrive extends InstantCommand{
    
    private final Boolean slowDrive;

    public SlowDrive(boolean slowDrive){
        this.slowDrive = slowDrive;
    }

    @Override
    public void execute() {
        Robot.slowDrive = this.slowDrive;
    }
}
