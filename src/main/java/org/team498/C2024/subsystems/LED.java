package org.team498.C2024.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static edu.wpi.first.wpilibj2.command.Commands.*;


public class LED extends SubsystemBase {
    private AddressableLED ledController = new AddressableLED(1);
    private AddressableLEDBuffer buffer = new AddressableLEDBuffer(65); // 46
    private int rainbowFirstPixelHue = 0;  // Initial hue for rainbow effect

    private State currentState = State.COLUMBIA_BLUE;

    public enum State {
        OFF(0, 0, 0),
        // Off when robot is off
        COLUMBIA_BLUE(101, 1, 92),
        // Idle color, no note on the robot
        YELLOW(29, 255, 255),
        // New intake success
        GREEN(59, 255, 255),
        // AMP, Subwoofer, & Shooter ready to shoot
        WHITE(0, 0, 100),
        // Secured note (A note has hit the 2nd beam breaks & is completely in the robot)
        RED(0, 255, 255),
        // Red Alliance (Auto)
        BLUE(112, 255, 255);
        // Blue Alliance (Auto)

        // Rainbow color path when disabled
        public final int h;
        public final int s;
        public final int v;

        State(int h, int s, int v) {
            this.h = h;
            this.s = s;
            this.v = v;
        }
    }

    public LED() {
        ledController.setLength(65); // 46
        setState(currentState);
        ledController.start();
    }

    @Override
    public void periodic() {
        if (currentState == State.OFF) {
            setRainbow();
        }
        ledController.setData(buffer);
    }

    // Rainbow path
    private void setRainbow() {
        for (var i = 0; i < buffer.getLength(); i++) {
            final int hue = (rainbowFirstPixelHue + (i * 180 / buffer.getLength())) % 180;
            buffer.setHSV(i, hue, 255, 128);
        }
        rainbowFirstPixelHue += 3;
        rainbowFirstPixelHue %= 180; // Keep hue value in range [0, 180]
    }

    public void setState(State state) {
        currentState = state;
        for (var i = 0; i < buffer.getLength(); i++) {
            buffer.setHSV(i, state.h, state.s, state.v);
        }
        ledController.setData(buffer);
    }

    public Command setStateCommand(State state) {
        return runOnce(()-> {
            currentState = state;
            for (var i = 0; i < buffer.getLength(); i++) {
                buffer.setHSV(i, state.h, state.s, state.v);
            }
            ledController.setData(buffer);
        });
    }

    private static LED instance;

    public static LED getInstance() {
        if (instance == null) {
            instance = new LED();  // Ensure there's only one instance (singleton pattern)
        }
        return instance;
    }
}