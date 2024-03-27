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
import org.team498.C2024.commands.shooter.SetShooterNextState;
import org.team498.lib.auto.Auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public class FourPort implements Auto{
    @Override
    public Command getCommand() {

        return new SequentialCommandGroup(

            new SetState(State.CRESCENDO),
            // new ReturnToIdle(),
            // new SetShooterNextState(),

            new SetIntakeState(State.Intake.INTAKE),

            //new FullScore(1),

            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.four_port_1),
                // PathLib.FourPortOne,
                new SequentialCommandGroup(
                    //new WaitCommand(0.5),
                    new FullScore(1.25),                  
                    new LoadGround().withTimeout(4.5),
                    new SetIntakeState(State.Intake.IDLE),
                        //for testing paths without intake
                            //new WaitCommand(4.5),
                    new SetHopperState(State.Hopper.IDLE),
                    new SetIntakeRollerState(State.IntakeRollers.IDLE)

                    // new WaitCommand(3),
                    //new LoadGround()
                )
            ),
            new SetState(State.CRESCENDO),
            // new StoreNote().withTimeout(2),
            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.four_port_2),
                // PathLib.FourPortTwo,
                new SequentialCommandGroup(

                    //new SetIntakeRollerState(State.IntakeRollers.IDLE),

                    new FullScore(1.75),
                    new LoadGround().withTimeout(4),
                    new SetIntakeState(State.Intake.IDLE),
                        //for testing paths without intake
                            //new WaitCommand(4),
                    new SetHopperState(State.Hopper.IDLE),
                    new SetIntakeRollerState(State.IntakeRollers.IDLE)
                    // new WaitCommand(3),
                    //new LoadGround()
                )
            ),
            new SetState(State.CRESCENDO),
            // new StoreNote().withTimeout(2),

            new ParallelDeadlineGroup(
                new PathPlannerFollower(PathLib.four_port_3),
                // PathLib.FourPortThree,
                new SequentialCommandGroup(
                    //new SetIntakeRollerState(State.IntakeRollers.IDLE),
                    new FullScore(1.25),
                    new LoadGround().withTimeout(5),
                    new SetIntakeState(State.Intake.IDLE),
                        //for testing paths without intake
                            //new WaitCommand(5),
                    new SetHopperState(State.Hopper.IDLE),
                    new SetIntakeRollerState(State.IntakeRollers.IDLE)
                    // new WaitCommand(3),
                    //new LoadGround()
                )
            ),
            new SetState(State.CRESCENDO),
            // new StoreNote().withTimeout(2),
            new FullScore(0.75),
            new ReturnToIdle()
        );
}

    @Override
    public Pose2d getInitialPose() {
        return PathLib.four_port_1.getInitialTargetHolonomicPose();
    }
    
}
