package org.team498.C2024.commands.intake;

import org.team498.C2024.StateController;
import org.team498.C2024.subsystems.IntakeRollers;
import org.team498.C2024.subsystems.intakeRollers.IntakeRollersSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;

public class SetIntakeRollersNextState extends InstantCommand {
    private final IntakeRollers intakeRollers = IntakeRollersSubsystem.getInstance();

    public SetIntakeRollersNextState() {
        addRequirements(intakeRollers);
    }

    @Override
    public void initialize() {
        intakeRollers.setState(StateController.getInstance().getState().intakeRollers);
    }
}

