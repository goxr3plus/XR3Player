package com.goxr3plus.xr3player.application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.goxr3plus.xr3player.controllers.loginmode.User;
import com.goxr3plus.xr3player.controllers.settings.ApplicationSettingsLoader;
import com.goxr3plus.xr3player.database.DatabaseTool;
import com.goxr3plus.xr3player.enums.FileType;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class MainLoadUser {

    /**
     * This part is actually loading the application users
     */
    public static void loadTheUsers() {

        // Set Update Screen Visible
        Main.updateScreen.setVisible(true);

        // Create Chromium Folder
        if (!IOAction.createFileOrFolder(DatabaseTool.getAbsoluteDatabaseParentFolderPathWithSeparator() + "Chromium",
                FileType.DIRECTORY)) {
            System.err.println("Failed to create chromium folder");
            MainExit.terminateXR3Player(-1);
        }

        // Create Database folder if not exists
        if (!IOAction.createFileOrFolder(DatabaseTool.getAbsoluteDatabasePathPlain(), FileType.DIRECTORY)) {
            System.err.println(
                    "Failed to create database folder[lack of permissions],please change installation directory");
            MainExit.terminateXR3Player(-1);
        } else {

            // Create the List with the Available Users
            final AtomicInteger counter = new AtomicInteger();
            try (Stream<Path> stream = Files.walk(Paths.get(DatabaseTool.getAbsoluteDatabasePathPlain()), 1)) {

                // Append all available users
                Main.loginMode.viewer.addMultipleItems(stream
                        .filter(path -> path.toFile().isDirectory()
                                && !(path + "").equals(DatabaseTool.getAbsoluteDatabasePathPlain()))
                        .map(path -> new User(path.getFileName() + "", counter.getAndAdd(1), Main.loginMode))
                        .collect(Collectors.toList()));

            } catch (final IOException e) {
                e.printStackTrace();
            }

            // avoid error
            if (!Main.loginMode.viewer.getItemsObservableList().isEmpty())
                Main.loginMode.viewer.setCenterIndex(Main.loginMode.viewer.getItemsObservableList().size() / 2);

        }

        // Create Original xr3database signature file
        IOAction.createFileOrFolder(DatabaseTool.getDatabaseSignatureFile().getAbsolutePath(), FileType.FILE);

    }

    /**
     * Starts the application for this specific user
     *
     * @param selectedUser The user selected to be logged in the application
     */
    public static void startAppWithUser(final User selectedUser) {

        // Close the LoginMode
        Main.loginMode.userSearchBox.getSearchBoxWindow().close();
        Main.loginMode.setVisible(false);
        Main.updateScreen.getProgressBar().setProgress(-1);
        Main.updateScreen.getLabel().setText("Launching...");
        Main.updateScreen.setVisible(true);

        // Prepare the BackgroundImageView
        Main.loginMode.getChildren().remove(Main.loginMode.getBackgroundImageView());
        Main.applicationStackPane.getChildren().add(1, Main.loginMode.getBackgroundImageView());

        // SideBar
        Main.sideBar.prepareForLoginMode(false);

        // Set root visible
        Main.root.setVisible(true);

        // Do a pause so the login mode disappears
        final PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(f -> {

            // Create this in a Thread
            final Thread s = new Thread(() -> Main.dbManager.initialize(selectedUser.getName()));
            s.start();

            // Do the below until the database is initialized
            Main.userInfoMode.displayForUser(selectedUser);

            try {
                s.join();
            } catch (final InterruptedException ex) {
                ex.printStackTrace();
            }

            // --------- Create the Menu Items of available users for Settings Window
            if (Main.loginMode.viewer.getItemsObservableList().size() == 1)
                Main.settingsWindow.getCopySettingsMenuButton().setDisable(true);
            else
                Main.loginMode.viewer.getItemsObservableList().stream()
                        .filter(userr -> !((User) userr).getName().equals(selectedUser.getName())).forEach(userr -> {

                    // Create the MenuItem
                    final MenuItem menuItem = new MenuItem(
                            InfoTool.getMinString(((User) userr).getName(), 50, "..."));

                    // Set Image
                    final ImageView imageView = new ImageView(((User) userr).getImageView().getImage());
                    imageView.setFitWidth(24);
                    imageView.setFitHeight(24);
                    menuItem.setGraphic(imageView);

                    // Set Action
                    menuItem.setOnAction(a -> {

                        // Ask the user
                        if (AlertTool.doQuestion("Override Settings",
                                "Soore you want to override your current user settings with the one that you selected from the menu ?",
                                Main.settingsWindow.getCopySettingsMenuButton(), Main.window))

                            // Don't block the application due to IO Operations
                            new Thread(() -> {

                                // Delete the current settings from the User
                                IOAction.deleteFile(new File(DatabaseTool.getAbsoluteDatabasePathWithSeparator()
                                        + selectedUser.getName() + File.separator + "settings" + File.separator
                                        + DatabaseTool.USER_SETTINGS_FILE_NAME));

                                // Transfer the settings from the other user
                                IOAction.copy(
                                        DatabaseTool.getAbsoluteDatabasePathWithSeparator()
                                                + ((User) userr).getName() + File.separator + "settings"
                                                + File.separator + DatabaseTool.USER_SETTINGS_FILE_NAME,
                                        DatabaseTool.getAbsoluteDatabasePathWithSeparator()
                                                + selectedUser.getName() + File.separator + "settings"
                                                + File.separator + DatabaseTool.USER_SETTINGS_FILE_NAME);

                                // Reload the application settings now...
                                Platform.runLater(ApplicationSettingsLoader::loadApplicationSettings);
                            }).start();

                    });

                    // Disable if user has no settings defined
                    if (!new File(DatabaseTool.getAbsoluteDatabasePathWithSeparator() + ((User) userr).getName()
                            + File.separator + "settings" + File.separator
                            + DatabaseTool.USER_SETTINGS_FILE_NAME).exists())
                        menuItem.setDisable(true);

                    // Finally add the Menu Item
                    Main.settingsWindow.getCopySettingsMenuButton().getItems().add(menuItem);
                });

            // ----Update the UserInformation properties file when the total libraries
            // change
            Main.libraryMode.viewer.itemsWrapperProperty().sizeProperty()
                    .addListener((observable, oldValue, newValue) -> selectedUser.getUserInformationDb()
                            .updateProperty("Total-Libraries", String.valueOf(newValue.intValue())));

            // ----Bind Label to User Name
            Main.sideBar.getNameLabel().setText(Main.userInfoMode.getUserName().getText());

            // ---Store this user as last logged in user
            Main.applicationProperties.updateProperty("Last-LoggedIn-User", selectedUser.getName());

            // ---------------END:Important
            // Work-----------------------------------------------------------

            // ================Load the DataBase - After the DBManager has been initialized
            // of course ;)============================
            Main.dbManager.loadApplicationDataBase();

        });
        pause.playFromStart();
    }
}
