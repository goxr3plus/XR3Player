package com.goxr3plus.xr3player.services.loginmode;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.database.DatabaseTool;
import com.goxr3plus.xr3player.enums.FileType;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.controllers.loginmode.User;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

/**
 * @author GOXR3PLUS
 *
 */
public class UsersLoaderService extends Service<Boolean> {

	/**
	 * Constructor
	 */
	public UsersLoaderService() {

		this.setOnSucceeded(s -> done());
		this.setOnFailed(f -> done());
		this.setOnCancelled(c -> done());
	}

	@Override
	public void start() {
		if (isRunning())
			return;

		// Bindings
		Main.updateScreen.setVisible(true);
		Main.updateScreen.getProgressBar().progressProperty().bind(progressProperty());
		Main.updateScreen.getLabel().setText("Loading....");

		// Start
		super.start();
	}

	/**
	 * Called when Service is done
	 */
	private void done() {

		// Show Notification
		AlertTool.showNotification("Welcome :)", null, Duration.seconds(4), NotificationType.SUCCESS);

		// Bindings
		Main.updateScreen.getProgressBar().progressProperty().unbind();
		Main.updateScreen.setVisible(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<Boolean> createTask() {
		return new Task<>() {
			@Override
			protected Boolean call() throws Exception {
				// Variables
				final int[] counter = {0};
				final int totalUsers = Main.loginMode.viewer.getItemsObservableList().size();

				try {

					// -- For every user
					Main.loginMode.viewer.getItemsObservableList().forEach(userr -> {
						final User user = (User) userr;

						// Check if the UserInformation Properties File exist
						if (new File(user.getUserInformationDb().getFileAbsolutePath()).exists()) {
							// System.out.println("UsersInformationDb Exists"); //debugging

							// --------------------Now continue
							// normally----------------------------------------------
							final Properties userInformationSettings = user.getUserInformationDb().loadProperties(); // Load
							// the
							// properties
							// from
							// the
							// File

							// --Total Libraries
							Optional.ofNullable(userInformationSettings.getProperty("Total-Libraries")).ifPresent(s -> {
								final int totalLibraries = Integer.parseInt(s);

								// Refresh the text
								Platform.runLater(() -> {
									// Add Pie Chart Data
									// if (totalLibraries > 0)
									// Main.loginMode.getSeries().getData().add(new
									// XYChart.Data<String,Number>(user.getUserName(), totalLibraries));

									// Update User Label
									user.getTotalLibrariesLabel().setText(Integer.toString(totalLibraries));
								});
							});

							// --User Description
							Optional.ofNullable(userInformationSettings.getProperty("User-Description"))
									.ifPresent(description -> Platform
											.runLater(() -> user.getDescriptionProperty().set(description)));

							// --Drop-Box-Accounts
							Optional.ofNullable(userInformationSettings.getProperty("DropBox-Access-Tokens"))
									.ifPresent(accessTokens -> {

										if (accessTokens.contains("<>:<>")) // Check if we have multiple access tokens
											Platform.runLater(() -> user.getDropBoxLabel().setText(Integer
													.toString(accessTokens.split(Pattern.quote("<>:<>")).length)));
										else if (!accessTokens.isEmpty()) // Check if we have one access token
											Platform.runLater(
													() -> user.getDropBoxLabel().setText(Integer.toString(1)));

									});

						} // If the UserInformation Properties File doesn't exit try to take
						// Total-Libraries information from the actual sqlite.db file
						// this process is slow as fu.ck that's why i am keeping information inside a
						// properties file generally...
						else {
							// System.out.println("UsersInformationDb doesn't Exists"); //debugging

							// Very well create the UsersInformationDb because it doesn't exist so on the
							// next load it will exist
							IOAction.createFileOrFolder(new File(DatabaseTool.getAbsoluteDatabasePathWithSeparator()
									+ user.getName() + File.separator + "settings"), FileType.DIRECTORY);
							IOAction.createFileOrFolder(new File(user.getUserInformationDb().getFileAbsolutePath()),
									FileType.FILE);

							// Check if the database of this user exists
							final String dbFileAbsolutePath = DatabaseTool.getAbsoluteDatabasePathWithSeparator() + user.getName()
									+ File.separator + "dbFile.db";
							if (new File(dbFileAbsolutePath).exists()) {

								// --Create connection and load user information
								try (Connection connection = DriverManager
										.getConnection("jdbc:sqlite:" + dbFileAbsolutePath);
									 ResultSet dbCounter = connection.createStatement()
											 .executeQuery("SELECT COUNT(NAME) FROM LIBRARIES;");) {

									final int[] totalLibraries = {0};
									totalLibraries[0] += dbCounter.getInt(1);
									Thread.sleep(500);

									// Refresh the text
									Platform.runLater(() -> {

										// Add Pie Chart Data
										// if (totalLibraries[0] > 0)
										// Main.loginMode.getSeries().getData().add(new
										// XYChart.Data<String,Number>(user.getUserName(), totalLibraries[0]));

										// Update User Label
										user.getTotalLibrariesLabel().setText(Integer.toString(totalLibraries[0]));

									});

									// Update the UserInformationDb so it is ready for the next time
									user.getUserInformationDb().updateProperty("Total-Libraries",
											String.valueOf(totalLibraries[0]));

									// System.out.println("User:" + user.getUserName() + " contains : " +
									// totalLibraries + " Libraries"); //debugging

									updateProgress(++counter[0], totalUsers);
								} catch (final Exception ex) {
									Main.logger.log(Level.SEVERE, "", ex);
								}

							} else {
								// Refresh the text
								Platform.runLater(() -> user.getTotalLibrariesLabel().setText("0"));

								// Update the UserInformationDb so it is ready for the next time
								user.getUserInformationDb().updateProperty("Total-Libraries", String.valueOf(0));
							}

						}

					});

				} catch (final Exception ex) {
					ex.printStackTrace();
				}

				return true;
			}
		};
	}

}
