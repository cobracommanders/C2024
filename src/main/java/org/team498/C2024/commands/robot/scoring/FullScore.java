package org.team498.C2024.commands.robot.scoring;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class FullScore extends SequentialCommandGroup{
    public FullScore(){
        super(
            new PrepareToScore(),
            new WaitCommand(1),
            new Score()
        );
    }
}
