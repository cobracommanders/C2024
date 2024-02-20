package org.team498.C2024;

public enum State {
    IDLE(Shooter.IDLE, Hopper.IDLE, Intake.IDLE, IntakeRollers.IDLE, Kicker.IDLE),
    SOURCE(Shooter.SOURCE, Hopper.IDLE, Intake.INTAKE, IntakeRollers.IDLE, Kicker.IDLE),
    INTAKE(Shooter.IDLE, Hopper.REVERSE, Intake.INTAKE, IntakeRollers.INTAKE, Kicker.IDLE),
    OUTTAKE(Shooter.IDLE, Hopper.FORWARD, Intake.OUTTAKE, IntakeRollers.OUTTAKE, Kicker.IDLE),
    AMP(Shooter.AMP, Hopper.AMP, Intake.IDLE, IntakeRollers.IDLE, Kicker.FORWARD),
    SUBWOOFER(Shooter.SUBWOOFER, Hopper.REVERSE, Intake.IDLE, IntakeRollers.IDLE, Kicker.REVERSE),
    PODIUM(Shooter.PODIUM, Hopper.REVERSE, Intake.IDLE, IntakeRollers.IDLE, Kicker.REVERSE),
    CRESCENDO(Shooter.CRESCENDO, Hopper.REVERSE, Intake.IDLE, IntakeRollers.IDLE, Kicker.REVERSE),
    CANCEL_AMP(Shooter.IDLE, Hopper.IDLE, Intake.IDLE, IntakeRollers.IDLE, Kicker.REVERSE),
    CANCEL_SPEAKER(Shooter.IDLE, Hopper.IDLE, Intake.IDLE, IntakeRollers.IDLE, Kicker.FORWARD),
    TRACK_NOTE(Shooter.VISION, Hopper.REVERSE, Intake.INTAKE, IntakeRollers.INTAKE, Kicker.IDLE);

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

    //Sets Speed and Angle for Shooter
    public enum Shooter {
        IDLE(0, 0, 35),
        SUBWOOFER(2000, 1000, 49),
        AMP(0, 0, 35),
        PODIUM(2000, 1000, 37),
        CRESCENDO(2000, 1000, 40),
        SOURCE(0, 0, 50),
        CANCEL_AMP(0, 0, 35),
        VISION(0, 0, 40);

        public final double speed;
        public final double feedSpeed;
        public final double angle;

        Shooter(double speed, double feedSpeed, double angle) {
            this.speed = speed;
            this.feedSpeed = feedSpeed;
            this.angle = angle;
        }
    }

    //Sets Speeds for Kicker
    public enum Kicker {
        IDLE(0),
        //REVERSE is for PODIUM, SUBWOOFER, and CRESCENDO
        REVERSE(-1),
        //FORWARD is for AMP
        FORWARD(1);


    public final double speed;
        Kicker(double speed){
            this.speed = speed;
        }
    }

    //Sets Speeds for Hopper
    public enum Hopper {
        IDLE(0),
        FORWARD(0.5),
        REVERSE(-0.4),
        AMP(-0.75);

        public final double speed;

        Hopper(double speed) {
            this.speed = speed;
        }
    }

    //Sets Positions for Intake
    public enum Intake {
        IDLE(1.92), //0.304
        INTAKE(0.01),
        OUTTAKE(0.01);

        public final double speed;
        
        Intake(double speed){
            this.speed = speed;
        }
        
    }

    //Sets Speeds for IntakeRollers
    public enum IntakeRollers {
        IDLE(0),
        INTAKE(1),
        OUTTAKE(-1);

        public final double speed;

        IntakeRollers(double speed) {
            this.speed = speed;
        }

    }
}
