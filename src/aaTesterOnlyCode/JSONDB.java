/**
 * 
 */
package aaTesterOnlyCode;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import application.Main;
import application.database.DbManager;
import application.tools.ActionTool;
import application.tools.InfoTool;
import application.tools.NotificationType;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.util.Duration;
import smartcontroller.SmartController;

/**
 * This class is managing a key-value database
 * 
 * @author GOXR3PLUS
 *
 */
@Deprecated
public class JSONDB {
	
	/** This executor does the commit job. */
	private static final ExecutorService jSONUpdateExecutor = Executors.newSingleThreadExecutor();
	
	private DbManager localDBManager;
	
	/**
	 * Constructor
	 * 
	 * @param localDBManager
	 */
	public JSONDB(DbManager localDBManager) {
		this.localDBManager = localDBManager;
	}
	
	/**
	 * Creates the JSONDatabase if it doesn't exitst
	 * 
	 * @return True if succedeed or False if not
	 */
	public boolean recreateJSonDataBase() {
		String jsonFilePath = InfoTool.getUserFolderAbsolutePathWithSeparator() + "settings.json";
		
		//File already exists?
		if (new File(jsonFilePath).exists())
			return true;
		
		//JSON Array [ROOT]
		JsonObject json = new JsonObject();
		
		//-----------Libraries Array------------------
		JsonObject librariesSystem = new JsonObject();
		
		//Latest Library that was selected + Opened
		JsonObject lastSelectedLibrary = new JsonObject();
		
		//Libraries that where opened
		JsonArray openedLibraries = new JsonArray();
		//	for (int i = 0; i < 2; i++) {
		//	    JsonObject object = new JsonObject();
		//	    object.put("name", "library->" + i);
		//	    openedLibraries.add(object);
		//	}
		
		librariesSystem.put("openedLibraries", openedLibraries);
		librariesSystem.put("lastSelectedLibrary", lastSelectedLibrary);
		
		json.put("librariesSystem", librariesSystem);
		
		//Write to File	
		try (FileWriter file = new FileWriter(jsonFilePath)) {
			file.write(Jsoner.prettyPrint(json.toJson()));
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
			//logger.severe("SettingsWindowController - exception: " + e); //$NON-NLS-1$
			return false;
		}
		
		return true;
	}
	
	/**
	 * Loads the Libraries information into the application
	 * 
	 * @return True if succedeed or False if not
	 */
	public boolean loadOpenedLibraries() {
		
		String jsonFilePath = InfoTool.getUserFolderAbsolutePathWithSeparator() + "settings.json";
		
		//Check if the file exists
		if (!new File(jsonFilePath).exists())
			return false;
		
		//Read the JSON File
		try (FileReader fileReader = new FileReader(jsonFilePath)) {
			
			//JSON Array [ROOT]
			JsonObject json = (JsonObject) Jsoner.deserialize(fileReader);
			
			//Opened Libraries Array
			JsonArray openedLibraries = (JsonArray) ( (JsonObject) json.get("librariesSystem") ).get("openedLibraries");
			
			//For each Library
			openedLibraries.forEach(libraryObject -> Platform.runLater(() ->
			
			//Get the Library and Open it!
			Main.libraryMode.getLibraryWithName( ( (JsonObject) libraryObject ).get("name").toString()).get().libraryOpenClose(true, true)
			
			//Print its name
			//System.out.println(((JsonObject) libraryObject).get("name"))
			));
			
			//Last selected library Array
			JsonObject lastSelectedLibrary = (JsonObject) ( (JsonObject) json.get("librariesSystem") ).get("lastSelectedLibrary");
			
			//Add the Listener to multipleLibs
			Platform.runLater(() -> {
				Main.libraryMode.multipleLibs.getTabPane().getSelectionModel().selectedItemProperty().addListener((observable , oldTab , newTab) -> {
					
					// Give a refresh to the newly selected ,!! ONLY IF IT HAS NO ITEMS !! 
					if (!Main.libraryMode.multipleLibs.getTabPane().getTabs().isEmpty() && ( (SmartController) newTab.getContent() ).isFree(false)
							&& ( (SmartController) newTab.getContent() ).getItemsObservableList().isEmpty()) {
						
						( (SmartController) newTab.getContent() ).getLoadService().startService(false, true,false);
						
						updateLibrariesInformation(Main.libraryMode.multipleLibs.getTabPane().getTabs(), false);
						
					}
					
					//			    //Do an animation
					//			    if (oldTab != null && newTab != null) {
					//				Node oldContent = oldTab.getContent(); //tabContent.get(oldTab)
					//				Node newContent = newTab.getContent(); //tabContent.get(newTab)
					//
					//				newTab.setContent(oldContent);
					//				ScaleTransition fadeOut = new ScaleTransition(Duration.millis(50), oldContent);
					//				fadeOut.setFromX(1);
					//				fadeOut.setFromY(1);
					//				fadeOut.setToX(0);
					//				fadeOut.setToY(0);
					//
					//				ScaleTransition fadeIn = new ScaleTransition(Duration.millis(50), newContent);
					//				fadeIn.setFromX(0);
					//				fadeIn.setFromY(0);
					//				fadeIn.setToX(1);
					//				fadeIn.setToY(1);
					//
					//				fadeOut.setOnFinished(event -> newTab.setContent(newContent));
					//
					//				SequentialTransition crossFade = new SequentialTransition(fadeOut, fadeIn);
					//				crossFade.play();
					//			    }
				});
			});
			
			//If not empty...
			if (!lastSelectedLibrary.isEmpty()) {
				Platform.runLater(() -> {
					
					//Select the correct library inside the TabPane
					Main.libraryMode.multipleLibs.getTabPane().getSelectionModel()
							.select(Main.libraryMode.multipleLibs.getTab(lastSelectedLibrary.get("name").toString()));
					
					//This will change in future update when user can change the default position of Libraries
					Main.libraryMode.teamViewer.getViewer().setCenterIndex(Main.libraryMode.multipleLibs.getSelectedLibrary().get().getPosition());
					
					//System.out.println("Entered !lastSelectedLibrary.isEmpty()")
				});
			}
			
			//Do an Update on the selected Library SmartController
			Platform.runLater(() -> {
				//Check if empty and if not update the selected library
				if (!Main.libraryMode.multipleLibs.getTabs().isEmpty()
						&& Main.libraryMode.multipleLibs.getSelectedLibrary().get().getSmartController().isFree(false))
					Main.libraryMode.multipleLibs.getSelectedLibrary().get().getSmartController().getLoadService().startService(false, true,false);
			});
			
		} catch (IOException | DeserializationException e) {
			e.printStackTrace();
			//  logger.severe("SettingsWindowController - exception: " + e); //$NON-NLS-1$
			return false;
		}
		
		return true;
	}
	
	/**
	 * Stores the informations about the opened libraries , in the order they
	 * are opened
	 * 
	 * @param observableList
	 * @param updateOpenedLibraries
	 * 
	 * @return True if succedeed or False if not
	 */
	public boolean updateLibrariesInformation(ObservableList<Tab> observableList , boolean updateOpenedLibraries) {
		
		String jsonFilePath = InfoTool.getUserFolderAbsolutePathWithSeparator() + "settings.json";
		
		if (!new File(jsonFilePath).exists())
			return false;
		
		//Update the JSON File on an external Thread
		jSONUpdateExecutor.execute(() -> {
			try (FileReader fileReader = new FileReader(jsonFilePath)) {
				Object obj = Jsoner.deserialize(fileReader);
				
				//JSON Array [ROOT]
				JsonObject json = (JsonObject) obj;
				
				//Last selected library Array
				JsonObject lastSelectedLibrary = (JsonObject) ( (JsonObject) json.get("librariesSystem") ).get("lastSelectedLibrary");
				
				if (observableList.isEmpty())
					lastSelectedLibrary.clear();
				else
					observableList.forEach(tab -> {
						if (tab.isSelected())
							lastSelectedLibrary.put("name", tab.getTooltip().getText());
					});
				
				//Update the opened libraries?
				if (updateOpenedLibraries) {
					
					//Opened Libraries Array
					JsonArray openedLibraries = (JsonArray) ( (JsonObject) json.get("librariesSystem") ).get("openedLibraries");
					openedLibraries.clear();
					
					//Add the Libraries to the Libraries Array
					//System.out.println()
					observableList.forEach(tab -> {
						
						//Add it to opened libraries
						JsonObject object = new JsonObject();
						object.put("name", tab.getTooltip().getText());
						openedLibraries.add(object);
						
						//System.out.println(tab.getTooltip().getText())
					});
					
				}
				
				//Write to File
				try (FileWriter file = new FileWriter(jsonFilePath)) {
					file.write(Jsoner.prettyPrint(json.toJson()));
					file.flush();
				} catch (IOException e) {
					e.printStackTrace();
					//logger.severe("SettingsWindowController - exception: " + e); //$NON-NLS-1$
					//return false
				}
				
			} catch (IOException | DeserializationException e) {
				e.printStackTrace();
				//  logger.severe("SettingsWindowController - exception: " + e); //$NON-NLS-1$
				// return false
			} finally {
				if (localDBManager.isShowNotifications())
					ActionTool.showNotification("JSON Updated", "JSON File Updated...", Duration.millis(150), NotificationType.INFORMATION);
			}
		});
		
		//Returns always true needs to be fixed!!!
		return true;
	}
	
}
