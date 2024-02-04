package org.team498.C2024.commands;

import java.util.function.DoubleSupplier;

import org.team498.C2024.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.Command;

public class SetShooterManual extends Command{
    private final Shooter shooter = Shooter.getInstance();
    private final DoubleSupplier speed;
    private final boolean isManual;

    public SetShooterManual(boolean isManual, DoubleSupplier speed){
        this.isManual = isManual;
        this.speed = speed;
    }

    @Override
    public void execute() {
        shooter.setAngleManual(isManual, speed.getAsDouble());
    }

    @Override
    public void end(boolean interrupted) {
        shooter.setAngleManual(false, 0);
    }
}
