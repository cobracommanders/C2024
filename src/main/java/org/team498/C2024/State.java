package org.team498.C2024;

public enum State {
    IDLE(Shooter.IDLE, Hopper.IDLE, Intake.IDLE),
    SOURCE(Shooter.SOURCE, Hopper.IDLE, Intake.INTAKE),
    INTAKE(Shooter.IDLE, Hopper.REVERSE, Intake.INTAKE),
    OUTTAKE(Shooter.IDLE, Hopper.FORWARD, Intake.OUTTAKE),
    AMP(Shooter.AMP, Hopper.FORWARD, Intake.OUTTAKE),
    SUBWOOFER(Shooter.SUBWOOFER, Hopper.REVERSE, Intake.IDLE),
    PODIUM(Shooter.PODIUM, Hopper.REVERSE, Intake.IDLE),
    CRESCENDO(Shooter.CRESCENDO, Hopper.REVERSE, Intake.IDLE);

    public final Shooter shooter;
    public final Hopper hopper;
    public final Intake intake;


    State(Shooter shooter, Hopper hopper, Intake intake) {
        this.shooter = shooter;
        this.hopper = hopper;
        this.intake = intake;
    }

    public enum Shooter {
        IDLE(0, 0, 0),
        SUBWOOFER(0, 0, 0),
        AMP(0, 0, 0),
        PODIUM(0, 0, 0),
        CRESCENDO(0, 0, 0),
        SOURCE(0, 0, 0);

        public final double topSpeed;
        public final double bottomSpeed;
        public final double angle;

        Shooter(double topSpeed, double angle, double bottomSpeed) {
            this.bottomSpeed = bottomSpeed;
            this.topSpeed = topSpeed;
            this.angle = angle;
        }
    }

    public enum Hopper {
        IDLE(0),
        FORWARD(.5),
        REVERSE(-.5);

        public final double speed;

        Hopper(double speed) {
            this.speed = speed;
        }
    }

    public enum Intake {
        IDLE(0),
        INTAKE(0),
        OUTTAKE(0);

        public final double speed;
        
        Intake(double speed){
            this.speed = speed;
        }
        
    }

    public enum IntakeRollers {
        IDLE(0),
        INTAKE(0),
        OUTTAKE(0);

        public final double speed;

        IntakeRollers(double speed) {
            this.speed = speed;
        }

    }
}
