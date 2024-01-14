package org.team498.C2024;

public enum State {
IDLE(Shooter.IDLE);

    public final Shooter shooter;


    State(Shooter shooter) {
        this.shooter = shooter;
    }

    public enum Shooter {
        IDLE(0, 0, 0),
        SUBWOOFER(0, 0, 0),
        AMP(0, 0, 0),
        PODIUM(0, 0, 0),
        SPEAKER(0, 0, 0);

        public final double topSpeed;
        public final double bottomSpeed;
        public final double angle;

        Shooter(double topSpeed, double angle, double bottomSpeed) {
            this.bottomSpeed = bottomSpeed;
            this.topSpeed = topSpeed;
            this.angle = angle;
        }
    }
}
