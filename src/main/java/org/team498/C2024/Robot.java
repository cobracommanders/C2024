package org.team498.C2024;


import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import org.team498.C2024.commands.auto.FourNote;
import org.team498.C2024.commands.auto.FourNoteFull;
import org.team498.C2024.commands.auto.FourNoteWing;
import org.team498.C2024.commands.auto.FourPort;
import org.team498.C2024.commands.auto.LongTaxi;
import org.team498.C2024.commands.auto.OneTaxi;
import org.team498.C2024.commands.auto.SixNoteAmp;
import org.team498.C2024.commands.auto.Spit;
import org.team498.C2024.commands.auto.TestAuto;
import org.team498.C2024.commands.robot.ReturnToIdle;
import org.team498.C2024.commands.robot.scoring.FullScore;
import org.team498.C2024.subsystems.Drivetrain;
import org.team498.C2024.subsystems.Hopper;
import org.team498.C2024.subsystems.Intake;
import org.team498.C2024.subsystems.IntakeRollers;
import org.team498.C2024.subsystems.Kicker;
import org.team498.C2024.subsystems.Limelight;
import org.team498.C2024.subsystems.PhotonVision;
import org.team498.C2024.subsystems.Shooter;
import org.team498.lib.auto.Auto;
import org.team498.lib.drivers.Blinkin;
import org.team498.lib.drivers.Gyro;
import org.team498.lib.drivers.Blinkin.BlinkinColor;
import org.team498.lib.util.PoseUtil;
import java.util.List;
import java.util.Optional;


public class Robot extends TimedRobot{
    public static final double DEFAULT_PERIOD = 0.02;
    public final Timer setupTimer = new Timer();
    public double setupTime  = 0;
    public static int coordinateFlip = 1;
    public static int rotationOffset = 0;

    public static Optional<Alliance> alliance = Optional.empty();
    public static final Controls controls = new Controls();

    private final Drivetrain drivetrain = Drivetrain.getInstance();
    private final Gyro gyro = Gyro.getInstance();
    private final Blinkin blinkin = Blinkin.getInstance();
    //private final RobotState robotState = RobotState.getInstance();

    private final SendableChooser<Auto> autoChooser = new SendableChooser<Auto>();
    private Auto defaultAuto = new FourPort();
    private Auto autoToRun = defaultAuto;

    // private boolean matchStarted = false;

    private final List<Auto> autoOptions = List.of(
        new FourNote(),
        new FourNoteFull(),
        new SixNoteAmp(),
        new TestAuto(),
        new FourPort(),
        new FourNoteWing(),
        new OneTaxi(),
        new LongTaxi(),
        new Spit()
        //    new PracticeAuto()
                                                  );

    @Override
    public void robotInit() {
        //new PowerDistribution(1, PowerDistribution.ModuleType.kRev).close(); // Enables power distribution logging
        drivetrain.setYaw(0);
       // FieldPositions.displayAll();
        autoChooser.setDefaultOption(defaultAuto.getName(), defaultAuto);
        autoOptions.forEach(auto -> autoChooser.addOption(auto.getName(), auto));
        controls.configureDefaultCommands();
        controls.configureDriverCommands();
        controls.configureOperatorCommands();
        // PathLib.SL1Note1.getClass();
        drivetrain.enableBrakeMode(false);
        // Register Subsystems
        Drivetrain.getInstance();
        Shooter.getInstance();
        Hopper.getInstance();
        Kicker.getInstance();
        Intake.getInstance();
        IntakeRollers.getInstance();
        PhotonVision.getInstance();

        Shooter.getInstance().configMotors();
        Hopper.getInstance().configMotors();
        Kicker.getInstance().configMotors();
        Intake.getInstance().configMotors();
        IntakeRollers.getInstance().configMotors();

        // Limelight.getInstance();

    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
        SmartDashboard.putData(autoChooser);
        SmartDashboard.putBoolean("Note Ready To AMP", Kicker.getInstance().getKickerBeamBreak());
        // SmartDashboard.putNumber("bl cancoder", kDefaultPeriod)
        // SmartDashboard.putBoolean("is Scoring", StateController.getInstance().isScoring());

        if (alliance.isEmpty()) {
            alliance = DriverStation.getAlliance();
            // This reverses the coordinates/direction of the drive commands on the red alliance
            coordinateFlip = alliance.get() == Alliance.Blue
                             ? 1
                             : -1;
            // Add 180 degrees to all teleop rotation setpoints while on the red alliance
            rotationOffset = alliance.get() == Alliance.Blue
                             ? 0
                             : 180;
        }
        // blinkin.setColor(BlinkinColor.BREATH_RED);
        SmartDashboard.putBoolean("Shooter Aligned", Shooter.getInstance().atSetpoint() && Shooter.getInstance().shooterState() && !Hopper.getInstance().isPidEnabled());
        // if (RobotState.isEnabled()) {
        //     if(Shooter.getInstance().atSetpoint() && Shooter.getInstance().shooterState() && !Hopper.getInstance().isPidEnabled()) {
        //         blinkin.setColor(BlinkinColor.SOLID_DARK_GREEN);
        //         SmartDashboard.putBoolean("Shooter Aligned", true);
        //     }
        //     else {
        //         blinkin.setColor(BlinkinColor.SOLID_BLUE);
        //         SmartDashboard.putBoolean("Shooter Aligned", false);
        //     }
        // }
        // else { 
        //     if (RobotPosition.isNear(autoToRun.getInitialPose(), 1))
        //         blinkin.setColor(BlinkinColor.SOLID_DARK_GREEN);
        //     else blinkin.setColor(BlinkinColor.SOLID_HOT_PINK);

        // }
        if (RobotState.isEnabled()) {
            if(Hopper.getInstance().getBackBeamBreak()) {
                blinkin.setColor(BlinkinColor.RAINBOW_WITH_GLITTER);
                SmartDashboard.putBoolean("Note in Robot", true);
            }
            else {
                blinkin.setColor(BlinkinColor.SOLID_BLUE);
                SmartDashboard.putBoolean("Note Not In Robot", false);
            }
        }
    }

    @Override
    public void disabledPeriodic() {
        alliance = DriverStation.getAlliance();
        // This reverses the coordinates/direction of the drive commands on the red alliance
        coordinateFlip = alliance.get() == Alliance.Blue
                         ? 1
                         : -1;
        // Add 180 degrees to all teleop rotation setpoints while on the red alliance
        rotationOffset = alliance.get() == Alliance.Blue
                         ? 0
                         : 180;
        if (setupTime < 5) {
            setupTime = setupTimer.get();
            drivetrain.updateIntegratedEncoders();
        } else {
            setupTimer.stop();
            setupTime = setupTimer.get() + 10;
        }

        autoToRun = autoChooser.getSelected();
        // if (autoToRun != null) {
        //     // robotState.setState(autoToRun.getInitialState());
        // }
    }

    @Override
    public void teleopInit() {
        Shooter.getInstance().setFeed(0);
        Shooter.getInstance().setAngle(0);
        Hopper.getInstance().set(0);
        Intake.getInstance().set(0);
        IntakeRollers.getInstance().set(0);
        Kicker.getInstance().set(0);
        CommandScheduler.getInstance().schedule(new ReturnToIdle());
        // matchStarted = true;
        // drivetrain.enableBrakeMode(true);
    }

    @Override
    public void teleopPeriodic() {
        if (Hopper.getInstance().getBackBeamBreak()){
            controls.driver.rumble(0.05);
            controls.operator.rumble(0.05);
        }
        else {
            controls.driver.rumble(0);
            controls.operator.rumble(0);
        }

    }


    @Override
    public void teleopExit() {
        controls.driver.rumble(0);
    }

    @Override
    public void disabledInit() {
        setupTimer.restart();
        // drivetrain.enableBrakeMode(false);
    }
    @Override
    public void autonomousInit() {
        // new FullScore().schedule();
        // drivetrain.enableBrakeMode(true);
        // matchStarted = true;
        Shooter.getInstance().setFeed(0);
        Shooter.getInstance().setAngle(0);
        Hopper.getInstance().set(0);
        Intake.getInstance().set(0);
        IntakeRollers.getInstance().set(0);
        Kicker.getInstance().set(0);

        if (autoToRun == null)
            autoToRun = defaultAuto;

        //autoToRun = new HighHighCone();

        if (alliance.get() == Alliance.Blue) {
            Drivetrain.getInstance().setYaw(120);
            Drivetrain.getInstance().setPose(autoToRun.getInitialPose());
        } else {
            Drivetrain.getInstance().setYaw(60);
            Drivetrain.getInstance().setPose(PoseUtil.flip(autoToRun.getInitialPose()));
        }
        //SmartDashboard.putData((Sendable) autoToRun.getInitialPose());

        autoToRun.getCommand().schedule();

        // CommandScheduler.getInstance().run();

        // if (alliance.get() == Alliance.Blue) {
        //     Drivetrain.getInstance().setYaw(autoToRun.getInitialPose().getRotation().getDegrees());
        //     Drivetrain.getInstance().setPose(autoToRun.getInitialPose());
        // } else {
        //     Drivetrain.getInstance().setYaw(PoseUtil.flip(autoToRun.getInitialPose()).getRotation().getDegrees());
        //     Drivetrain.getInstance().setPose(PoseUtil.flip(autoToRun.getInitialPose()));
        // }
    }

    @Override
    public void testInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    public static void main(String... args) {
        RobotBase.startRobot(Robot::new);
    }
}