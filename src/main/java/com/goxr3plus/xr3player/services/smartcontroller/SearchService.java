package main.java.com.goxr3plus.xr3player.services.smartcontroller;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Node;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.enums.Genre;
import main.java.com.goxr3plus.xr3player.controllers.librarymode.Library;
import main.java.com.goxr3plus.xr3player.controllers.smartcontroller.SmartController;
import main.java.com.goxr3plus.xr3player.controllers.smartcontroller.SmartController.WorkOnProgress;
import main.java.com.goxr3plus.xr3player.models.smartcontroller.Audio;
import main.java.com.goxr3plus.xr3player.models.smartcontroller.Media;

/**
 * The Class SearchService.
 */
public class SearchService extends Service<Void> {
	
	/** The word. */
	private String word;
	
	/** The counter. */
	private int counter = 0;
	
	/** The page before search. */
	private int pageBeforeSearch = 0;
	
	private final SmartController smartController;
	
	/**
	 * Constructor.
	 */
	public SearchService(SmartController smartController) {
		this.smartController = smartController;
		
		setOnSucceeded(s -> done());
		setOnFailed(f -> {
			smartController.getNavigationHBox().setDisable(false);
			done();
		});
		
	}
	
	/**
	 * You can start the search Service by calling this method.
	 */
	public void search(String text) {
		if (!smartController.isFree(true))
			return;
		
		word = text;
		smartController.getIndicatorVBox().visibleProperty().bind(runningProperty());
		smartController.getIndicator().progressProperty().bind(progressProperty());
		smartController.getDescriptionLabel().setText("Searching...");
		smartController.getDescriptionArea().setText("\n Searching ....");
		smartController.getNavigationHBox().setDisable(true);
		// Security Value
		smartController.workOnProgress = WorkOnProgress.SEARCHING_FILES;
		
		//Clear the list
		smartController.getItemsObservableList().clear();
		
		reset();
		start();
	}
	
	/**
	 * When the Service is done.
	 */
	private void done() {
		// Security Value
		smartController.workOnProgress = WorkOnProgress.NONE;
		smartController.updateList();
		smartController.unbind();
	}
	
	/**
	 * Returns the page that the SmartController was before the Search.
	 *
	 * @return the pane number before search
	 */
	public int getPaneNumberBeforeSearch() {
		return pageBeforeSearch;
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			/**
			 * [[SuppressWarningsSpartan]]
			 */
			@Override
			protected Void call() throws Exception {
				
				// Counter
				counter = 0;
				
				// if (word.isEmpty())
				//	word = "";
				
				// Given Work
				System.out.println("Searching for word:[" + word + "]");
				String query = "";
				
				//--------------SEARCH WINDOW SPECIAL SEARCH----------------------------
				
				if (smartController.getGenre() == Genre.SEARCHWINDOW) {
					
					//Let's create the UNION
					ArrayList<String> queryArray = new ArrayList<>();
					ObservableList<Node> observableList = Main.libraryMode.viewer.getItemsObservableList();
					smartController.setTotalInDataBase(observableList.stream().mapToInt(library -> ( (Library) library ).getTotalEntries()).sum());
					
					//Check if any PlayLists exist
					if (observableList.isEmpty()) {
						return null;
					}
					
					queryArray.add("SELECT * FROM (");
					
					//For Each
					Main.libraryMode.viewer.getItemsObservableList().forEach(lib -> {
						if (observableList.indexOf(lib) != observableList.size() - 1)
							queryArray.add(" SELECT * FROM '" + ( (Library) lib ).getDataBaseTableName() + "' UNION ALL ");
						else
							queryArray.add(" SELECT * FROM '" + ( (Library) lib ).getDataBaseTableName() + "' ");
					});
					
					//Choose the correct query based on the settings of the user
					if (Main.settingsWindow.getPlayListsSettingsController().getFileSearchGroup().getToggles().get(0).isSelected())
						queryArray.add(" ) WHERE PATH LIKE '%" + word + "%' GROUP BY PATH LIMIT " + smartController.getMaximumPerPage() + " ");
					else
						queryArray.add(" ) WHERE replace(path, rtrim(path, replace(path,'" + File.separator + "','')),'') LIKE '%" + word + "%' GROUP BY PATH LIMIT "
								+ smartController.getMaximumPerPage() + " ");
					
					query = String.join("", queryArray);
					//System.out.println(query)
					
				}
				
				//--------------NORMAL PLAYLISTS SEARCH----------------------------
				
				else {
					query = "SELECT * FROM '" + smartController.getDataBaseTableName() + "' ";
					
					String wordQuery = smartController.getAlphabetBar().isLetterPressed() ? word + "%" : ( "%" + word + "%" );
					
					//Choose the correct query based on the settings of the user
					if (Main.settingsWindow.getPlayListsSettingsController().getFileSearchGroup().getToggles().get(0).isSelected())
						query = query + " WHERE PATH LIKE '" + wordQuery + "' LIMIT " + smartController.getMaximumPerPage();
					else
						query = query + " WHERE replace(path, rtrim(path, replace(path, '" + File.separator + "', '')), '') LIKE '" + wordQuery + "' LIMIT "
								+ smartController.getMaximumPerPage();
				}
				
				//System.out.println(query);
				
				//Continue
				try (ResultSet resultSet = Main.dbManager.getConnection().createStatement().executeQuery(query)) {
					//try (ResultSet resultSet = Main.dbManager.connection1.createStatement().executeQuery(query)) {
					
					//Fetch the items from the database
					Platform.runLater(() -> smartController.getCancelButton().setText("Adding data..."));
					List<Media> arrayList = new ArrayList<>();
					while (resultSet.next()) {
						//Add the Audio to the ArrayList
						arrayList.add(new Audio(resultSet.getString("PATH"), resultSet.getDouble("STARS"), resultSet.getInt("TIMESPLAYED"), resultSet.getString("DATE"),
								resultSet.getString("HOUR"), smartController.getGenre(), arrayList.size() + 1));
						
						updateProgress(++counter, smartController.getMaximumPerPage());
					}
					
					//Show 100%
					updateProgress(smartController.getMaximumPerPage(), smartController.getMaximumPerPage());
					
					//Add the the items to the observable list
					CountDownLatch countDown = new CountDownLatch(1);
					Platform.runLater(() -> {
						smartController.getItemsObservableList().addAll(arrayList);
						smartController.getNormalModeMediaTableViewer().getAllDetailsService().restartService(smartController.getNormalModeMediaTableViewer());
						countDown.countDown();
					});
					countDown.await();
					
				} catch (Exception ex) {
					Main.logger.log(Level.WARNING, "", ex);
				}
				
				return null;
			}
			
		};
	}
	
	/**
	 * @return the pageBeforeSearch
	 */
	public int getPageBeforeSearch() {
		return pageBeforeSearch;
	}
	
	/**
	 * @param pageBeforeSearch
	 *            the pageBeforeSearch to set
	 */
	public void setPageBeforeSearch(int pageBeforeSearch) {
		this.pageBeforeSearch = pageBeforeSearch;
	}
	
}
