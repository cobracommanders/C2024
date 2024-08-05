package org.team498.C2024.commands.drivetrain;

import org.team498.C2024.Robot;
import org.team498.C2024.RobotPosition;
import org.team498.C2024.Controls;
import org.team498.C2024.subsystems.CommandSwerveDrivetrain;
import org.team498.C2024.subsystems.Limelight;
import org.team498.C2024.subsystems.TunerConstants;
import org.team498.lib.LimelightHelpers;
import org.team498.lib.LimelightHelpers.LimelightResults;
import org.team498.lib.LimelightHelpers.LimelightTarget_Fiducial;
import org.team498.lib.drivers.Xbox;
import org.team498.lib.util.PoseUtil;

import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;

public class AutoAlign extends Command{
    double tx = 0;
    boolean end = true;
    private final CommandSwerveDrivetrain drivetrain = CommandSwerveDrivetrain.getInstance();
    private Xbox controller = Robot.controls.driver;
    public AutoAlign() {
            addRequirements(drivetrain);
        }
        
        /// add math code here
    @Override
    public void execute() {
        //end = true;
        int desiredTagID = Robot.alliance.get() == Alliance.Red ? 7 : 4;
        LimelightResults results = LimelightHelpers.getLatestResults("limelight");
        for (LimelightTarget_Fiducial tag : results.targetingResults.targets_Fiducials) {
            if (tag.fiducialID == desiredTagID) {
                tx = tag.tx;
                end = false;
            }
        }
        double speeds = 0;
        speeds = tx * (Math.PI / 180);
        // drivetrain.driveFieldRelative(new ChassisSpeeds(
        //     controller.leftY() * controller.leftY() * controller.leftY() * TunerConstants.kSpeedAt12VoltsMps,
        //     controller.leftX() * controller.leftX() * controller.leftX() * TunerConstants.kSpeedAt12VoltsMps,
        //     4 * speeds
        // ));
        drivetrain.driveFieldRelativeAngleLock(new ChassisSpeeds(
            controller.leftY() * controller.leftY() * controller.leftY() * TunerConstants.kSpeedAt12VoltsMps,
            controller.leftX() * controller.leftX() * controller.leftX() * TunerConstants.kSpeedAt12VoltsMps,
            0), 
            CommandSwerveDrivetrain.getInstance().getState().Pose.getRotation().getDegrees() - tx
        );


    }
    // @Override
    // public boolean isFinished() {
    //     //currentTime > trajectory.getTotalTimeSeconds();
    //     return (Math.abs(tx) <= 1.5 || end);
    // }
    // @Override
    // public void end(boolean interrupted) {
    //     drivetrain.driveFieldRelative(new ChassisSpeeds());
    // }
}
