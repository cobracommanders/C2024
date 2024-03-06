package org.team498.C2024.commands.auto;
import org.team498.C2024.PathLib;
import org.team498.C2024.State;
import org.team498.C2024.commands.drivetrain.PathFollowerX;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.C2024.commands.hopper.SetHopperNextState;
import org.team498.C2024.commands.intake.SetIntakeNextState;
import org.team498.C2024.commands.intake.SetIntakeRollerState;
import org.team498.C2024.commands.intake.SetIntakeRollersNextState;
import org.team498.C2024.commands.kicker.SetKickerNextState;
import org.team498.C2024.commands.robot.ReturnToIdle;
import org.team498.C2024.commands.robot.SetState;
import org.team498.C2024.commands.robot.scoring.FullScore;
import org.team498.C2024.commands.robot.scoring.SubwooferScore;
import org.team498.C2024.commands.shooter.SetShooterNextState;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class Spit implements Auto{
    @Override
    public Command getCommand() {

        return new SequentialCommandGroup(
            new FullScore(3),
            new SetState(State.SPIT),
            new ParallelCommandGroup(
                new SetIntakeNextState(),
                new SetIntakeRollersNextState(),
                new SetHopperNextState(),
                new SetShooterNextState(),
                new SetKickerNextState()
            ),
            new PathPlannerFollower(PathLib.spit),
            new ReturnToIdle()
        );
            
}

    @Override
    public Pose2d getInitialPose() {
        return PathLib.spit.getInitialTargetHolonomicPose();
    }
    
}
