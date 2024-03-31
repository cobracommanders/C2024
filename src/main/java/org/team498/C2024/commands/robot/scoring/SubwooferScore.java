package org.team498.C2024.commands.robot.scoring;

import org.team498.C2024.State;
import org.team498.C2024.StateController;
import org.team498.C2024.StateController.ScoringOption;
import org.team498.C2024.Constants.DrivetrainConstants;
import org.team498.C2024.commands.drivetrain.SlowDrive;
import org.team498.C2024.commands.drivetrain.TargetDrive;
import org.team498.C2024.commands.drivetrain.TargetSpeaker;
import org.team498.C2024.commands.robot.ReturnToIdle;
import org.team498.C2024.commands.robot.SetScoringState;
import org.team498.C2024.commands.robot.SetState;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class SubwooferScore extends SequentialCommandGroup{
    public SubwooferScore(double wait){
        super(
            new SetState(State.SUBWOOFER),
            //new InstantCommand(StateController.getInstance().setNextScoringOption(ScoringOption.SUBWOOFER))),
            new ParallelDeadlineGroup(
                new WaitCommand(wait),
                new PrepareToScore()
                //new WaitCommand(wait),
            ),
            new AutoScore()
            // new WaitCommand(1),
            // new ReturnToIdle()
        );
    }
}
