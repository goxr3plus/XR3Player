package application.loginmode.services;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import application.Main;
import application.loginmode.LoginMode;
import application.tools.ActionTool;
import application.tools.InfoTool;
import application.tools.ActionTool.FileType;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.chart.PieChart;

/**
 * @author GOXR3PLUS
 *
 */
public class UsersInfoLoader extends Service<Boolean> {
	
	/**
	 * Constructor
	 */
	public UsersInfoLoader() {
		
		this.setOnSucceeded(s -> done());
		this.setOnFailed(f -> done());
		this.setOnCancelled(c -> done());
	}
	
	@Override
	public void start() {
		if (isRunning())
			return;
		
		//Bindings
		Main.updateScreen.setVisible(true);
		Main.updateScreen.getProgressBar().progressProperty().bind(progressProperty());
		Main.updateScreen.getLabel().setText("---Loadings Users Information---");
		
		//Start
		super.start();
	}
	
	/**
	 * Called when Service is done
	 */
	private void done() {
		//Bindings
		Main.updateScreen.getProgressBar().progressProperty().unbind();
		Main.updateScreen.setVisible(false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				//Variables
				int[] counter = { 0 };
				int totalUsers = Main.loginMode.teamViewer.getItemsObservableList().size();
				
				try {
					
					// -- For every user
					Main.loginMode.teamViewer.getItemsObservableList().forEach(user -> {
						
						//Check if the UserInformation Properties File exist
						if (new File(user.getUserInformationDb().getFileAbsolutePath()).exists()) {
							//System.out.println("UsersInformationDb Exists"); //debugging
							
							//--------------------Now continue normally----------------------------------------------
							Properties userInformationSettings = user.getUserInformationDb().loadProperties(); //Load the properties from the File
							
							//--Total Libraries
							Optional.ofNullable(userInformationSettings.getProperty("Total-Libraries")).ifPresent(s -> {
								int totalLibraries = Integer.parseInt(s);
								
								// Refresh the text
								Platform.runLater(() -> {
									//Add Pie Chart Data
									if (totalLibraries > 0)
										Main.loginMode.getLibrariesPieChartData().add(new PieChart.Data(InfoTool.getMinString(user.getUserName(), 4), totalLibraries));
									
									//Update User Label
									user.getTotalLibrariesLabel().setText(Integer.toString(totalLibraries));
								});
							});
							
							//--User Description
							Optional.ofNullable(userInformationSettings.getProperty("User-Description"))
									.ifPresent(description -> Platform.runLater(() -> user.getDescriptionLabel().setText(description)));
							
						} //If the UserInformation Properties File doesn't exit try to take Total-Libraries information from the actual sqlite.db file
							//this process is slow as fu.ck that's why i am keeping information inside a properties file generally...
						else {
							//System.out.println("UsersInformationDb doesn't Exists"); //debugging
							
							//Very well create the UsersInformationDb because it doesn't exist so on the next load it will exist
							ActionTool.createFileOrFolder(new File(InfoTool.getAbsoluteDatabasePathWithSeparator() + user.getUserName() + File.separator + "settings"),
									FileType.DIRECTORY);
							ActionTool.createFileOrFolder(new File(user.getUserInformationDb().getFileAbsolutePath()), FileType.FILE);
							
							//Check if the database of this user exists
							String dbFileAbsolutePath = InfoTool.getAbsoluteDatabasePathWithSeparator() + user.getUserName() + File.separator + "dbFile.db";
							if (new File(dbFileAbsolutePath).exists()) {
								
								// --Create connection and load user information
								try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFileAbsolutePath);
										ResultSet dbCounter = connection.createStatement().executeQuery("SELECT COUNT(*) FROM LIBRARIES;");) {
									
									int[] totalLibraries = { 0 };
									totalLibraries[0] += dbCounter.getInt(1);
									Thread.sleep(500);
									
									// Refresh the text
									Platform.runLater(() -> {
										
										//Add Pie Chart Data
										if (totalLibraries[0] > 0)
											Main.loginMode.getLibrariesPieChartData().add(new PieChart.Data(InfoTool.getMinString(user.getUserName(), 4), totalLibraries[0]));
										
										//Update User Label
										user.getTotalLibrariesLabel().setText(Integer.toString(totalLibraries[0]));
										
									});
									
									//Update the UserInformationDb so it is ready for the next time
									user.getUserInformationDb().updateProperty("Total-Libraries", String.valueOf(totalLibraries[0]));
									
									//System.out.println("User:" + user.getUserName() + " contains : " + totalLibraries + " Libraries"); //debugging
									
									updateProgress(++counter[0], totalUsers);
								} catch (Exception ex) {
									Main.logger.log(Level.SEVERE, "", ex);
								}
								
							} else {
								// Refresh the text
								Platform.runLater(() -> user.getTotalLibrariesLabel().setText("0"));
								
								//Update the UserInformationDb so it is ready for the next time
								user.getUserInformationDb().updateProperty("Total-Libraries", String.valueOf(0));
							}
							
						}
						
						//----------------SUPPORT FOR XR3Player Version 72 and below---------------------------------------------
						//----------------THIS CODE WILL BE REMOVED SOMEDAY IN THE FUTURE----------------------------------------
						//-----------------IT PARSES THE settings.json file from the previous updates which was holding information
						//-----------------about open libraries and and the last opened library , now that information are stored
						//-----------------on the userInformation.properties file ;)
						String jsonFilePath = InfoTool.getAbsoluteDatabasePathWithSeparator() + user.getUserName() + File.separator + "settings.json";
						
						//Check if the file exists -- make the simple magic
						if (new File(jsonFilePath).exists()) {
							//Read the JSON File
							try (FileReader fileReader = new FileReader(jsonFilePath)) {
								
								//JSON Array [ROOT]
								JsonObject json = (JsonObject) Jsoner.deserialize(fileReader);
								
								//Opened Libraries Array
								JsonArray openedLibraries = (JsonArray) ( (JsonObject) json.get("librariesSystem") ).get("openedLibraries");
								
								//For each Library
								//System.out.println("\n\nUser Name:" + user.getUserName());
								if (!openedLibraries.isEmpty()) {
									String openedLibs = openedLibraries.stream().map(libraryObject -> ( (JsonObject) libraryObject ).get("name").toString())
											.collect(Collectors.joining("<|>:<|>"));
									//System.out.println("Opened Libraries:\n-> " + openedLibs);
									user.getUserInformationDb().updateProperty("Opened-Libraries", openedLibs);
								}
								
								//Last selected library 
								JsonObject lastSelectedLibrary = (JsonObject) ( (JsonObject) json.get("librariesSystem") ).get("lastSelectedLibrary");
								
								//If not Last selected library is empty...
								if (!lastSelectedLibrary.isEmpty()) {
									String lastOpenedLibrary = lastSelectedLibrary.get("name").toString();
									//	System.out.println("Last Opened Library: " + lastOpenedLibrary);
									user.getUserInformationDb().updateProperty("Last-Opened-Library", lastOpenedLibrary);
								}
								
							} catch (IOException | DeserializationException e) {
								e.printStackTrace();
								//  logger.severe("SettingsWindowController - exception: " + e); //$NON-NLS-1$
							}
						}
						
						//Now delete that fucking JSON File
						new File(jsonFilePath).delete();
						
					});
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				return true;
			}
		};
	}
	
}
