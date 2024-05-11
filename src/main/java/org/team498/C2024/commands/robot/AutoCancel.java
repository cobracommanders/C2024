package org.team498.C2024.commands.robot;

import org.team498.C2024.subsystems.Drivetrain;
import org.team498.C2024.subsystems.Hopper;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoCancel extends ConditionalCommand{
    public AutoCancel(){
        super(new InstantCommand(()->CommandScheduler.getInstance().cancelAll()).andThen(new ReturnToIdle().alongWith(new InstantCommand(()-> Drivetrain.getInstance().stop()))),
                        new WaitCommand(0), 
                        Hopper.getInstance()::getBackBeamBreak
                        );
    }
}
