package org.team498.C2024.commands.robot;

import org.team498.C2024.FieldPositions;
import org.team498.C2024.Robot;
import org.team498.C2024.State;
import org.team498.C2024.StateController;
import org.team498.C2024.commands.drivetrain.TargetDrive;
import org.team498.C2024.subsystems.Drivetrain;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class PrepareToScore extends SequentialCommandGroup {
    public PrepareToScore(){
        super(
            new ConditionalCommand(new InstantCommand(()-> Drivetrain.getInstance().setAngleGoal(90 + Robot.rotationOffset)), new TargetDrive(FieldPositions.blueSpeaker.toPose2d()), ()-> StateController.getInstance().getState() == State.AMP),
            new ConditionalCommand(null, null, null),
            new SetScoringState()
            //new SetShooterNextState()
        );
    }
}
