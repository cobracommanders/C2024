package org.team498.C2024.commands;

import org.team498.C2024.StateController;
import org.team498.C2024.subsystems.Hopper;
import org.team498.C2024.subsystems.IntakeRollers;

import edu.wpi.first.wpilibj2.command.Command;

public class SetIntakeRollersNextState extends Command {
    private final IntakeRollers intakeRollers = IntakeRollers.getInstance();

    public SetIntakeRollersNextState() {
        addRequirements(intakeRollers);
    }

    @Override
    public void initialize() {
        intakeRollers.setState(StateController.getInstance().getState().intakeRollers);
    }

    @Override
    public boolean isFinished() {
        return intakeRollers.atSetpoint();
    }
}

