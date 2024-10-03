package org.team498.C2024.commands.drivetrain;

import org.team498.C2024.Robot;
import org.team498.C2024.subsystems.CommandSwerveDrivetrain;
import org.team498.C2024.subsystems.TunerConstants;
import org.team498.lib.LimelightHelpers;
import org.team498.lib.LimelightHelpers.LimelightResults;
import org.team498.lib.LimelightHelpers.LimelightTarget_Fiducial;
import org.team498.lib.drivers.Xbox;
import org.team498.C2024.RobotPosition;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;

public class AutoLock extends Command {
    private final double degrees;
    private final CommandSwerveDrivetrain drivetrain = CommandSwerveDrivetrain.getInstance();
    private Xbox controller = Robot.controls.driver;
    public AutoLock() {
        addRequirements(drivetrain);
        this.degrees = RobotPosition.calculateDegreesToSpeaker();
    }

    @Override
    public void execute() {
        drivetrain.driveFieldRelativeAngleLock(new ChassisSpeeds(
            -controller.leftY() * controller.leftY() * controller.leftY() * TunerConstants.kSpeedAt12VoltsMps,
            -controller.leftX() * controller.leftX() * controller.leftX() * TunerConstants.kSpeedAt12VoltsMps,
            0), 
            this.degrees);
            isFinished();
    }

    @Override
    public boolean isFinished() {
        //currentTime > trajectory.getTotalTimeSeconds();
        return ((Math.abs(RobotPosition.calculateDegreesToSpeaker() - Math.abs(CommandSwerveDrivetrain.getInstance().getHeading(degrees))) ) <= 3);
    }
}
