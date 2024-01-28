package org.team498.C2024;

public enum State {
    IDLE(Shooter.IDLE, Hopper.IDLE, Intake.IDLE, IntakeRollers.IDLE, Kicker.IDLE),
    SOURCE(Shooter.SOURCE, Hopper.IDLE, Intake.INTAKE, IntakeRollers.IDLE, Kicker.IDLE),
    INTAKE(Shooter.IDLE, Hopper.REVERSE, Intake.INTAKE, IntakeRollers.INTAKE, Kicker.IDLE),
    OUTTAKE(Shooter.IDLE, Hopper.FORWARD, Intake.OUTTAKE, IntakeRollers.OUTTAKE, Kicker.IDLE),
    AMP(Shooter.AMP, Hopper.FORWARD, Intake.OUTTAKE, IntakeRollers.IDLE, Kicker.IDLE),
    SUBWOOFER(Shooter.SUBWOOFER, Hopper.REVERSE, Intake.IDLE, IntakeRollers.IDLE, Kicker.IDLE),
    PODIUM(Shooter.PODIUM, Hopper.REVERSE, Intake.IDLE, IntakeRollers.IDLE, Kicker.IDLE),
    CRESCENDO(Shooter.CRESCENDO, Hopper.REVERSE, Intake.IDLE, IntakeRollers.IDLE, Kicker.IDLE),
    CANCEL_AMP(Shooter.CANCEL_AMP, Hopper.IDLE, Intake.IDLE, IntakeRollers.IDLE, Kicker.IDLE);

    public final Shooter shooter;
    public final Hopper hopper;
    public final Intake intake;
    public final IntakeRollers intakeRollers;
    public final Kicker kicker;


    State(Shooter shooter, Hopper hopper, Intake intake, IntakeRollers intakeRollers, Kicker kicker) {
        this.shooter = shooter;
        this.hopper = hopper;
        this.intake = intake;
        this.intakeRollers = intakeRollers;
        this.kicker = kicker;
    }

    public enum Shooter {
        IDLE(0, 0, 0),
        SUBWOOFER(0, 0, 0),
        AMP(0, 0, 0),
        PODIUM(0, 0, 0),
        CRESCENDO(0, 0, 0),
        SOURCE(0, 0, 0),
        CANCEL_AMP(0, 0, 0);

        public final double topSpeed;
        public final double bottomSpeed;
        public final double angle;

        Shooter(double topSpeed, double angle, double bottomSpeed) {
            this.bottomSpeed = bottomSpeed;
            this.topSpeed = topSpeed;
            this.angle = angle;
        }
    }

    public enum Kicker {
    IDLE(0);


    public final double speed;
        Kicker(double speed){
            this.speed = speed;
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
