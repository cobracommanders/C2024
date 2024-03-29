package org.team498.C2024.commands.auto;
import org.team498.C2024.PathLib;
import org.team498.C2024.State;
import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
import org.team498.C2024.commands.hopper.SetHopperState;
import org.team498.C2024.commands.intake.SetIntakeRollerState;
import org.team498.C2024.commands.intake.SetIntakeState;
import org.team498.C2024.commands.robot.ReturnToIdle;
import org.team498.C2024.commands.robot.SetIntakeIdle;
import org.team498.C2024.commands.robot.SetState;
import org.team498.C2024.commands.robot.StoreNote;
import org.team498.C2024.commands.robot.loading.LoadGround;
import org.team498.C2024.commands.robot.scoring.FullScore;
import org.team498.C2024.commands.robot.scoring.SubwooferScore;
import org.team498.C2024.commands.shooter.SetShooterNextState;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public class OuterWing implements Auto{
    @Override
    public Command getCommand() {

        return new SequentialCommandGroup(

            new SubwooferScore(1),

            new SetIntakeState(State.Intake.INTAKE),

            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.OuterWing1),
                new SequentialCommandGroup(
                    new FullScore(1),                  
                    new LoadGround().withTimeout(1.5),
                    new SetIntakeState(State.Intake.IDLE),
                    new SetHopperState(State.Hopper.IDLE),
                    new SetIntakeRollerState(State.IntakeRollers.IDLE)
                )
            ),
            new SetState(State.CRESCENDO),
            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.OuterWing2),
                new SequentialCommandGroup(

                    new FullScore(1),
                    new LoadGround().withTimeout(1.5),
                    new SetIntakeState(State.Intake.IDLE),
                    new SetHopperState(State.Hopper.IDLE),
                    new SetIntakeRollerState(State.IntakeRollers.IDLE)
                )
            ),
            new SetState(State.CRESCENDO),

            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.OuterWing3),
                new SequentialCommandGroup(
                    new FullScore(1),
                    new LoadGround().withTimeout(1),
                    new SetIntakeState(State.Intake.IDLE),
                    new SetHopperState(State.Hopper.IDLE),
                    new SetIntakeRollerState(State.IntakeRollers.IDLE)
                )
            ),
            new SetState(State.CRESCENDO),
            new FullScore(0.75),
            new ReturnToIdle()
        );
}

    @Override
    public Pose2d getInitialPose() {
        return PathLib.OuterWing1.getInitialTargetHolonomicPose();
    }
    
}
