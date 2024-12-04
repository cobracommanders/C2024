package org.team498.C2024.commands.intake;

import org.team498.C2024.State.Intake;
import org.team498.C2024.subsystems.IntakeStateMachine.IntakeStateEnum;
import org.team498.C2024.subsystems.IntakeStateMachine.IntakeSubsystem;
import org.team498.C2024.StateController;

import edu.wpi.first.wpilibj2.command.Command;

// public class SetIntakeNextState extends Command {
//     private final IntakeSubsystem intake = IntakeSubsystem.getState();

//     public SetIntakeNextState() {
//         addRequirements(intake);
//     }

//     @Override
//     public void initialize() {
//         intake.setState(StateController.getInstance().getState().intake);
//     }

//     @Override
//     public boolean isFinished() {
//         return true;//intake.atSetpoint();
//     }
// }

