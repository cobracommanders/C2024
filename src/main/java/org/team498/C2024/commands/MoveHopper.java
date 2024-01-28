package org.team498.C2024.commands;

import org.team498.C2024.subsystems.Hopper;

public class MoveHopper {
    private final Hopper hopper = Hopper.getInstance();

    public void MoveHoppertoPosition(double position){
        MoveHoppertoPosition(position);
    }

    public boolean isFinished() {
        return hopper.atSetpoint();
    }
}