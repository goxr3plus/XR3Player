package com.goxr3plus.xr3player.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.goxr3plus.xr3player.database.DatabaseTool;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.services.database.VacuumProgressService;
import com.goxr3plus.xr3player.utils.general.OSTool;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * This class contains methods for exiting and restarting the application
 */
public class MainExit {
    /**
     * Terminate the application.
     *
     * @param vacuum the vacuum
     */
    private static void terminate(final boolean vacuum) {

        // I need to check it in case no user is logged in
        if (Main.dbManager == null)
            terminateXR3Player(0);
        else if (Main.libraryMode.openedLibrariesViewer.isFree(true)) {
            if (!vacuum)
                terminateXR3Player(0);
            else {
                final VacuumProgressService vService = new VacuumProgressService();
                Main.updateScreen.getLabel().textProperty().bind(vService.messageProperty());
                Main.updateScreen.getProgressBar().setProgress(-1);
                Main.updateScreen.getProgressBar().progressProperty().bind(vService.progressProperty());
                Main.updateScreen.setVisible(true);
                vService.start(new File(DatabaseTool.getUserFolderAbsolutePathWithSeparator() + "dbFile.db"),
                        new File(DatabaseTool.getUserFolderAbsolutePathWithSeparator() + "dbFile.db-journal"));
                Main.dbManager.commitAndVacuum();
            }
        }

    }

    /**
     * This method is used to exit the application
     */
    public static void confirmApplicationExit() {
        final Alert alert = JavaFXTool.createAlert("Exit XR3Player?",
                "Vacuum is clearing junks from database\n(In future updates it will be automatical)",
                "Pros:\nThe database file may be shrinked \n\nCons:\nIt may take some seconds to be done\n",
                Alert.AlertType.CONFIRMATION, StageStyle.UTILITY, Main.window, null);

        // Create Custom Buttons
        final ButtonType exit = new ButtonType("Exit", ButtonBar.ButtonData.OK_DONE);
        final ButtonType vacuum = new ButtonType("Vacuum + Exit", ButtonBar.ButtonData.OK_DONE);
        final ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setDefaultButton(true);
        alert.getButtonTypes().setAll(exit, vacuum, cancel);

        // Pick the answer
        alert.showAndWait().ifPresent(answer -> {
            if (answer == exit)
                terminate(false);
            else if (answer == vacuum)
                terminate(true);

        });
    }

    /**
     * Use this code to terminate XR3Player
     *
     * @param exitCode The exit code of System.exit();
     */
    public static void terminateXR3Player(final int exitCode) {

        System.out.println("Terminating XR3Player OS->" + OSTool.getOS());
        switch (OSTool.getOS()) {
            case WINDOWS:
                new Thread(() -> {
                    // Disposing all Browsers...
                    Main.webBrowser.disposeAllBrowsers();
                    System.exit(exitCode);
                }).start();
                break;
            case LINUX:
            case MAC:
                Platform.runLater(() -> {
                    // Disposing all Browsers...
                    Main.webBrowser.disposeAllBrowsers();
                    System.exit(exitCode);
                });
                break;
            default:
                System.out.println("Can't dispose browser instance!!!");
                break;
        }

    }

    /**
     * Calling this method restarts the application
     *
     * @param askUser Ask the User if he/she wants to restart the application
     */
    public static void restartTheApplication(final boolean askUser) {

        // Restart XR3Player
        new Thread(() -> {
            final String path = IOInfo.getBasePathForClass(Main.class);
            final String[] applicationPath = {new File(path + "XR3Player.exe").getAbsolutePath()};

            // Check if the file exists
            if (!new File(applicationPath[0]).exists()) {
                // Show message that application is restarting
                Platform.runLater(() -> AlertTool.showNotification("Application File can't be found",
                        "XR3Player can't be restarted due to unexpected problem ", Duration.seconds(2),
                        NotificationType.ERROR));

                if (!askUser)
                    terminate(false);
                else
                    return;
            }

            try {
                System.out.println("XR3PlayerPath is : " + applicationPath[0]);

                // ProcessBuilder builder = new ProcessBuilder("java", "-jar",
                // applicationPath[0])
                // builder.redirectErrorStream(true)
                // Process process = builder.start()
                final Process process = Runtime.getRuntime().exec("cmd.exe /c \"" + applicationPath[0] + "\"");
                final BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                // Show message that application is restarting
                Platform.runLater(
                        () -> AlertTool.showNotification("Restarting Application",
                                "If restart takes a lot of time exit application and restart it manually.\n[ "
                                        + applicationPath[0] + " ]",
                                Duration.seconds(20), NotificationType.INFORMATION));

                // startExitPauseTransition
                startExitPauseTransition(20, askUser);

                // Continuously Read Output
                String line;
                while (process.isAlive())
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.isEmpty())
                            break;
                        if (line.contains("Outside of Application Start Method"))
                            terminate(false);
                    }

            } catch (final Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.INFO, null, ex);
                Platform.runLater(() -> {
                    Main.updateScreen.setVisible(false);

                    // Show failed message
                    Platform.runLater(() -> AlertTool.showNotification("Restart seems to failed",
                            "Wait some more seconds before trying to restart/exit XR3Player manually",
                            Duration.seconds(20), NotificationType.ERROR));

                    // startExitPauseTransition
                    startExitPauseTransition(0, askUser);
                });
            }
        }, "Restart Application Thread").start();
    }

    private static void startExitPauseTransition(final int seconds, final boolean askUser) {
        // Wait 20 seconds
        final PauseTransition pause = new PauseTransition(Duration.seconds(seconds));
        pause.setOnFinished(f -> {
            Main.updateScreen.setVisible(false);

            // Show failed message
            if (seconds != 0 && askUser)
                Platform.runLater(() -> AlertTool.showNotification("Restart seems to failed",
                        "Wait some more seconds before trying to restart/exit XR3Player manually", Duration.seconds(20),
                        NotificationType.ERROR));

            // Ask the user
            if (askUser)
                Platform.runLater(() -> {
                    if (AlertTool.doQuestion(null, "Restart failed.... force shutdown?", null, Main.window))
                        terminate(false);
                });
            else {
                // Terminate after showing the message for a while
                final PauseTransition forceTerminate = new PauseTransition(Duration.seconds(2));
                forceTerminate.setOnFinished(fn -> terminate(false));
                forceTerminate.play();
            }

        });
        pause.play();
    }
}
