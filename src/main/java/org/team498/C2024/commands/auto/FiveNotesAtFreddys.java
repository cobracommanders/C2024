// package org.team498.C2024.commands.auto;

// import org.team498.C2024.PathLib;
// import org.team498.C2024.StateController;
// import org.team498.C2024.StateController.ScoringOption;
// import org.team498.C2024.commands.drivetrain.PathPlannerFollower;
// import org.team498.C2024.commands.robot.loading.LoadGround;
// import org.team498.C2024.commands.robot.scoring.FullScore;
// import org.team498.C2024.commands.robot.scoring.PrepareToScore;
// import org.team498.C2024.commands.robot.scoring.Score;
// import org.team498.lib.auto.Auto;

// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.InstantCommand;
// import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
// import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
// import edu.wpi.first.wpilibj2.command.WaitCommand;

// public class FiveNotesAtFreddys implements Auto{
//     @Override
//     public Command getCommand() {

//         return new SequentialCommandGroup(
//             /**
//              * Starts in Scoring Location 1
//              * Scores Pre Loaded Note
//              * Drives to First Note
//              * Intakes Note 1
//              * Scores Note 1
//              * Drives to second Note
//              * Intakes Note 2
//              * Scores Note 2
//              * Drives to Note 3
//              * Intakes Note 3
//              * Scores Note 3
//              * Drives to Middle Note 5
//              * Intakes Middle Note 5
//              * Drives to Note 3
//              * Scores Middle Note 5
//              */
//             new FullScore(),
//             new ParallelCommandGroup(
//                 new PathPlannerFollower(PathLib.SL1toNote1),
//                 new SequentialCommandGroup(
//                     new WaitCommand(1),
//                     new LoadGround(),
//                     new WaitCommand(1),
//                     new InstantCommand(() -> StateController.getInstance().setNextScoringOption(ScoringOption.CRESCENDO)),
//                     new PrepareToScore())),
//             new Score(),
//             new ParallelCommandGroup(
//                 new PathPlannerFollower(PathLib.Note1toNote2),
//                 new SequentialCommandGroup(
//                     new WaitCommand(1),
//                     new LoadGround(),
//                     new WaitCommand(1),
//                     new InstantCommand(() -> StateController.getInstance().setNextScoringOption(ScoringOption.CRESCENDO)),
//                     new PrepareToScore())),
//             new Score(),
//             new ParallelCommandGroup(
//                 new PathPlannerFollower(PathLib.Note2Note3),
//                 new SequentialCommandGroup(
//                     new WaitCommand(1),
//                     new LoadGround(),
//                     new WaitCommand(1),
//                     new InstantCommand(() -> StateController.getInstance().setNextScoringOption(ScoringOption.CRESCENDO)),
//                     new PrepareToScore())),
//             new Score(),
//             new ParallelCommandGroup(
//                 new SequentialCommandGroup(
//                     // new PathPlannerFollower(PathLib.Note3Mid5),
//                     // new PathPlannerFollower(PathLib.Mid5Note3)),
//                 ),
//                 new SequentialCommandGroup(
//                     new WaitCommand(1),
//                     new LoadGround(),
//                     new WaitCommand(1),
//                     new InstantCommand(() -> StateController.getInstance().setNextScoringOption(ScoringOption.CRESCENDO)),
//                     new PrepareToScore()),
//             new Score(),
//             new LoadGround(),
//             new WaitCommand(.5),
//             new FullScore()
//         ));
//     }

//     @Override
//     public Pose2d getInitialPose() {
//         return PathLib.SL1toNote1.getInitialTargetHolonomicPose();
//     }
    
// }

