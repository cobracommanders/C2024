package org.team498.C2024;


import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import org.team498.C2024.commands.auto.PathPlannerTest;
import org.team498.C2024.commands.auto.PracticeAuto;
import org.team498.C2024.commands.auto.TestAuto;
import org.team498.C2024.subsystems.Drivetrain;
import org.team498.C2024.subsystems.Limelight;
import org.team498.lib.auto.Auto;
import org.team498.lib.drivers.Blinkin;
import org.team498.lib.drivers.Gyro;
import org.team498.lib.util.PoseUtil;
import java.util.List;
import java.util.Optional;


public class Robot extends TimedRobot{
    public static final double DEFAULT_PERIOD = 0.02;
    public static int coordinateFlip = 1;
    public static int rotationOffset = 0;

    public static Optional<Alliance> alliance = Optional.empty();
    public static final Controls controls = new Controls();

    private final Drivetrain drivetrain = Drivetrain.getInstance();
    private final Gyro gyro = Gyro.getInstance();
    private final Blinkin blinkin = Blinkin.getInstance();
    //private final RobotState robotState = RobotState.getInstance();

    private final SendableChooser<Auto> autoChooser = new SendableChooser<Auto>();
    private Auto autoToRun;

    private boolean matchStarted = false;

    private final List<Auto> autoOptions = List.of(
           new TestAuto(),
           new PathPlannerTest(),
           new PracticeAuto()
                                                  );

    @Override
    public void robotInit() {
        //new PowerDistribution(1, PowerDistribution.ModuleType.kRev).close(); // Enables power distribution logging
        drivetrain.setYaw(0);
       // FieldPositions.displayAll();
        autoChooser.setDefaultOption("Score", new TestAuto());
        autoOptions.forEach(auto -> autoChooser.addOption(auto.getName(), auto));
        controls.configureDefaultCommands();
        controls.configureDriverCommands();
        controls.configureOperatorCommands();
        PathLib.SL1Note1.getClass();
        drivetrain.enableBrakeMode(false);
        Limelight.getInstance();
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
        SmartDashboard.putData(autoChooser);

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


        if (!matchStarted) {
            autoToRun = autoChooser.getSelected();
            if (autoToRun != null) {
               // robotState.setState(autoToRun.getInitialState());
            }
        }

        Drivetrain.getInstance().stop();
    }

    @Override
    public void teleopInit() {
        matchStarted = true;
        drivetrain.enableBrakeMode(true);
    }

    @Override
    public void teleopPeriodic() {
    }


    @Override
    public void teleopExit() {
        controls.driver.rumble(0);
    }

    @Override
    public void disabledInit() {
        drivetrain.enableBrakeMode(false);
    }
    @Override
    public void autonomousInit() {
        drivetrain.enableBrakeMode(true);
        matchStarted = true;

        if (autoToRun == null)
            autoToRun = new TestAuto();

        //autoToRun = new HighHighCone();

        if (alliance.get() == Alliance.Blue) {
            Drivetrain.getInstance().setYaw(autoToRun.getInitialPose().getRotation().getDegrees());
            Drivetrain.getInstance().setPose(autoToRun.getInitialPose());
        } else {
            Drivetrain.getInstance().setYaw(PoseUtil.flip(autoToRun.getInitialPose()).getRotation().getDegrees());
            Drivetrain.getInstance().setPose(PoseUtil.flip(autoToRun.getInitialPose()));
        }
        //SmartDashboard.putData((Sendable) autoToRun.getInitialPose());

        // autoToRun.getCommand().schedule();

        CommandScheduler.getInstance().run();

        if (alliance.get() == Alliance.Blue) {
            Drivetrain.getInstance().setYaw(autoToRun.getInitialPose().getRotation().getDegrees());
            Drivetrain.getInstance().setPose(autoToRun.getInitialPose());
        } else {
            Drivetrain.getInstance().setYaw(PoseUtil.flip(autoToRun.getInitialPose()).getRotation().getDegrees());
            Drivetrain.getInstance().setPose(PoseUtil.flip(autoToRun.getInitialPose()));
        }
    }

    @Override
    public void testInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    public static void main(String... args) {
        RobotBase.startRobot(Robot::new);
    }
}