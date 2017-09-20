package applicationmodes.librarymode;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;

import com.jfoenix.controls.JFXButton;

import application.Main;
import application.presenter.SearchBox;
import application.presenter.SearchBox.SearchBoxType;
import application.tools.ActionTool;
import application.tools.InfoTool;
import application.tools.NotificationType;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import xplayer.presenter.XPlayerController;

/**
 * This class contains everything needed going on LibraryMode.
 *
 * @author SuperGoliath
 */
public class LibraryMode extends BorderPane {
	
	// ------------------------------------------------
	
	@FXML
	private SplitPane topSplitPane;
	
	@FXML
	private BorderPane borderPane;
	
	@FXML
	private StackPane librariesStackView;
	
	@FXML
	private ScrollBar horizontalScrollBar;
	
	@FXML
	private Button newLibrary;
	
	@FXML
	private Label quickSearchTextField;
	
	@FXML
	private HBox libraryToolBar;
	
	@FXML
	private Button deleteLibrary;
	
	@FXML
	private Button renameLibrary;
	
	@FXML
	private Button openOrCloseLibrary;
	
	@FXML
	private JFXButton previous;
	
	@FXML
	private JFXButton createLibrary;
	
	@FXML
	private JFXButton next;
	
	@FXML
	private Button openLibraryContextMenu;
	
	@FXML
	private Label librariesInfoLabel;
	
	@FXML
	private HBox botttomHBox;
	
	@FXML
	private SplitPane bottomSplitPane;
	
	// ------------------------------------------------
	
	// protected boolean dragDetected
	
	/**
	 * The mechanism which allows you to transport items between libraries and more.
	 */
	public final SearchBox librariesSearcher = new SearchBox(SearchBoxType.LIBRARYSEARCHBOX);
	
	/**
	 * The mechanism which allows you to view the libraries as components with image etc.
	 */
	public TeamViewer teamViewer;
	
	/** The mechanism behind of opening multiple libraries. */
	public final MultipleLibraries multipleLibs = new MultipleLibraries();
	
	//--------Images ------------------------------
	
	/**
	 * Default image of a library(which has not a costume one selected by the user.
	 */
	public static Image defaultImage;//= InfoTool.getImageFromDocuments("visualizer.jpg");
	/**
	 * A classic warning image to inform the user about something
	 * 
	 */
	public static final Image warningImage = InfoTool.getImageFromResourcesFolder("warning.png");
	
	private boolean openLibraryAfterCreation;
	
	//----- Library Specific ------------------
	
	/** A PopUp window showing information about the selected library */
	public LibraryInformation libraryInformation = new LibraryInformation();
	
	/** The context menu. */
	public LibraryContextMenu librariesContextMenu = new LibraryContextMenu();
	
	/**
	 * This binding contains a number which shows how many libraries are currently opened
	 */
	public SimpleIntegerProperty openedLibraries = new SimpleIntegerProperty();
	
	/**
	 * This binding contains a number which shows how many libraries have currently no items at all
	 */
	public SimpleIntegerProperty emptyLibraries = new SimpleIntegerProperty();
	
	//----- Invalidation Listeners ------------------
	
	/** This variable is used during the creation of a new library. */
	private final InvalidationListener creationInvalidator = new InvalidationListener() {
		@Override
		public void invalidated(Observable observable) {
			
			// Remove the Listener
			Main.renameWindow.showingProperty().removeListener(this);
			
			// !Showing && !XPressed
			if (!Main.renameWindow.isShowing() && Main.renameWindow.wasAccepted()) {
				
				Main.window.requestFocus();
				
				// Check if this name already exists
				String name = Main.renameWindow.getUserInput();
				
				// if can pass
				if (!teamViewer.getViewer().getItemsObservableList().stream().anyMatch(lib -> lib.getLibraryName().equals(name))) {
					String tableName;
					boolean validName;
					
					// Until the randomName doesn't already exists
					do {
						tableName = ActionTool.returnRandomTableName();
						validName = !Main.dbManager.doesTableExist(tableName);
					} while (!validName);
					final String dataBaseTableName = tableName; //add it to a final variable
					
					//Ok Now Go
					try (PreparedStatement insertNewLibrary = Main.dbManager.getConnection()
							.prepareStatement("INSERT INTO LIBRARIES (NAME,TABLENAME,STARS,DATECREATED,TIMECREATED,DESCRIPTION,SAVEMODE,POSITION,LIBRARYIMAGE,OPENED) "
									+ "VALUES (?,?,?,?,?,?,?,?,?,?)");
							Statement statement = Main.dbManager.getConnection().createStatement()) {
						
						// Create the dataBase table
						statement.executeUpdate("CREATE TABLE '" + dataBaseTableName + "' (PATH       TEXT    PRIMARY KEY   NOT NULL ," + "STARS       DOUBLE     NOT NULL,"
								+ "TIMESPLAYED  INT     NOT NULL," + "DATE        TEXT   	NOT NULL," + "HOUR        TEXT    NOT NULL)");
						
						// Create the Library
						Library currentLib = new Library(name, dataBaseTableName, 0, null, null, null, 1, teamViewer.getViewer().getItemsObservableList().size(), null, false);
						
						// Add the library
						//currentLib.goOnSelectionMode(selectionModeToggle.isSelected());
						teamViewer.getViewer().addItem(currentLib, true);
						
						// Add a row on libraries table
						insertNewLibrary.setString(1, name);
						insertNewLibrary.setString(2, dataBaseTableName);
						insertNewLibrary.setDouble(3, currentLib.starsProperty().get());
						insertNewLibrary.setString(4, currentLib.getDateCreated());
						insertNewLibrary.setString(5, currentLib.getTimeCreated());
						insertNewLibrary.setString(6, currentLib.getDescription());
						insertNewLibrary.setInt(7, 1);
						insertNewLibrary.setInt(8, currentLib.getPosition());
						insertNewLibrary.setString(9, null);
						insertNewLibrary.setBoolean(10, false);
						
						insertNewLibrary.executeUpdate();
						
						// Commit
						Main.dbManager.commit();
						
						//Recalculate Some Bindings
						calculateEmptyLibraries();
						
						//Check if the user wants to immediately open library after it's creation
						if (openLibraryAfterCreation) {
							currentLib.openLibrary(true, false);
							Main.libraryMode.multipleLibs.selectTab(currentLib.getLibraryName());
						}
						
						//Bidirectional binding with Instant Search
						currentLib.getSmartController().getInstantSearch().selectedProperty()
								.bindBidirectional(Main.settingsWindow.getPlayListsSettingsController().getInstantSearch().selectedProperty());
						
					} catch (Exception ex) {
						Main.logger.log(Level.WARNING, "", ex);
						ActionTool.showNotification("Error Creating a Library", "Library can't be created cause of:" + ex.getMessage(), Duration.seconds(2),
								NotificationType.WARNING);
					}
				} else {
					ActionTool.showNotification("Dublicate Name", "A Library or PlayList with this name already exists!", Duration.seconds(2), NotificationType.INFORMATION);
				}
			}
			
			//Disable the openLibrary when the user creates a new Library
			if (!Main.renameWindow.isShowing())
				openLibraryAfterCreation = false;
			
		}
	};
	
	/**
	 * Constructor.
	 */
	public LibraryMode() {
		
		// FXMLLOADER
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "LibraryMode.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Return the library with the given name.
	 *
	 * @param name
	 *            the name
	 * @return the library with name
	 */
	public Optional<Library> getLibraryWithName(String name) {
		
		// Find that
		for (Library library : teamViewer.getViewer().getItemsObservableList())
			if (library.getLibraryName().equals(name))
				return Optional.of(library);
			
		return Optional.ofNullable(null);
	}
	
	/**
	 * Update Settings Total Library only if this Library exists and it is on settings mode
	 * 
	 * @param name
	 */
	//    public void updateLibraryTotalLabel(String name) {
	//	Library lib = getLibraryWithName(name);
	//	if (lib != null)
	//	    lib.updateSettingsTotalLabel();
	//    }
	
	/**
	 * Called as soon as FXML file has been loaded
	 */
	@FXML
	public void initialize() {
		
		//Initialise
		teamViewer = new TeamViewer(this);
		quickSearchTextField.visibleProperty().bind(teamViewer.getViewer().searchWordProperty().isEmpty().not());
		quickSearchTextField.textProperty().bind(Bindings.concat("Search :> ").concat(teamViewer.getViewer().searchWordProperty()));
		
		// createLibrary
		createLibrary.setOnAction(a -> createNewLibrary(createLibrary, false));
		
		// newLibrary
		newLibrary.setOnAction(a -> createNewLibrary(newLibrary, true));
		newLibrary.visibleProperty().bind(Bindings.size(teamViewer.getViewer().getItemsObservableList()).isEqualTo(0));
		
		// selectionModeToggle
		//selectionModeToggle.selectedProperty().addListener((observable , oldValue , newValue) -> teamViewer.getViewer().goOnSelectionMode(newValue));
		
		// searchLibrary
		botttomHBox.getChildren().add(librariesSearcher);
		
		// previous
		previous.setOnAction(a -> teamViewer.getViewer().previous());
		
		// next
		next.setOnAction(a -> teamViewer.getViewer().next());
		
		//showSettings
		//showSettings.setOnAction(a -> Main.settingsWindow.showWindow(SettingsTab.LIBRARIES));
		
		// StackPane
		librariesStackView.getChildren().addAll(teamViewer.getViewer(), librariesSearcher.region, librariesSearcher.searchProgress);
		teamViewer.getViewer().toBack();
		
		// XPlayer - 0
		Main.xPlayersList.addXPlayerController(new XPlayerController(0));
		Main.xPlayersList.getXPlayerController(0).makeTheDisc(Color.rgb(255, 95, 0), 45, Side.LEFT);
		Main.xPlayersList.getXPlayerController(0).makeTheVisualizer(Side.RIGHT);
		
		// -- openLibrariesContextMenu
		openLibraryContextMenu.setOnAction(a -> {
			Library library = teamViewer.getViewer().getSelectedItem();
			Bounds bounds = library.localToScreen(library.getBoundsInLocal());
			librariesContextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3, bounds.getMinY() + bounds.getHeight() / 4, library);
		});
		
		// -- libraryToolBar
		libraryToolBar.disableProperty().bind(teamViewer.getViewer().centerItemProperty().isNull());
		
		// -- renameLibrary
		renameLibrary.setOnAction(a -> teamViewer.getViewer().centerItemProperty().get().renameLibrary(renameLibrary));
		
		// -- deleteLibrary
		deleteLibrary.setOnAction(a -> teamViewer.getViewer().centerItemProperty().get().deleteLibrary(deleteLibrary));
		
		// -- openOrCloseLibrary 
		teamViewer.getViewer().centerItemProperty().addListener((observable , oldValue , newValue) -> {
			if (newValue != null)
				openOrCloseLibrary.textProperty().bind(Bindings.when(teamViewer.getViewer().centerItemProperty().get().openedProperty()).then("CLOSE").otherwise("OPEN"));
			else {
				openOrCloseLibrary.textProperty().unbind();
				openOrCloseLibrary.setText("...");
			}
		});
		
		// -- openOrCloseLibrary
		openOrCloseLibrary.disableProperty().bind(libraryToolBar.disabledProperty());
		openOrCloseLibrary.setOnAction(a -> teamViewer.getViewer().centerItemProperty().get().openLibrary(!teamViewer.getViewer().centerItemProperty().get().isOpened(), false));
		
		// -- settingsOfLibrary
		//openLibraryInformation.setOnAction(a -> libraryInformation.showWindow(teamViewer.getViewer().centerItemProperty().get()));
		
		// -- goToLibraryPlayList
		//		goToLibraryPlayList.setOnAction(a -> Optional.ofNullable(teamViewer.getViewer().centerItemProperty().get()).ifPresent(library -> {
		//			if (library.isOpened())
		//				multipleLibs.selectTab(library.getLibraryName());
		//		}));
		
		//----librariesInfoLabel
		librariesInfoLabel.textProperty().bind(Bindings.concat("Totally -> [ ", teamViewer.getViewer().itemsWrapperProperty().sizeProperty(), " ] Libraries", " , [ ",
				openedLibraries, " ] Opened", " , [ ", emptyLibraries, " ] Empty"));
		
	}
	
	/**
	 * Recalculates the opened libraries
	 */
	public void calculateOpenedLibraries() {
		openedLibraries.set((int) teamViewer.getViewer().getItemsObservableList().stream().filter(Library::isOpened).count());
	}
	
	/**
	 * Recalculates the empty libraries
	 */
	public void calculateEmptyLibraries() {
		emptyLibraries.set((int) teamViewer.getViewer().getItemsObservableList().stream().filter(Library::isEmpty).count());
	}
	
	/**
	 * Used to create a new Library
	 * 
	 * @param owner
	 */
	public void createNewLibrary(Node owner , boolean openLibraryAfterCreation) {
		if (Main.renameWindow.isShowing())
			return;
		
		this.openLibraryAfterCreation = openLibraryAfterCreation;
		
		// Open rename window
		Main.renameWindow.show("", owner, "Creating " + ( !openLibraryAfterCreation ? "" : "+ Open " ) + "new Library");
		
		// Add the showing listener
		Main.renameWindow.showingProperty().addListener(creationInvalidator);
	}
	
	/**
	 * Gets the previous.
	 *
	 * @return the previous
	 */
	public Button getPrevious() {
		return previous;
	}
	
	/**
	 * Gets the next.
	 *
	 * @return the next
	 */
	public Button getNext() {
		return next;
	}
	
	/**
	 * @return the horizontalScrollBar
	 */
	public ScrollBar getHorizontalScrollBar() {
		return horizontalScrollBar;
	}
	
	// Variables
	private double[] topSplitPaneDivider = { 0.45 , 0.55 };
	
	// Variables
	private double[] bottomSplitPaneDivider = { 0.6 , 0.4 };
	
	//	/**
	//	 * Updates the values of array that holds DividerPositions of splitPane
	//	 */
	//	public void updateTopSplitPaneDividerArray(double[] array) {
	//		topSplitPaneDivider[0] = array[0];
	//		topSplitPaneDivider[1] = array[1];
	//	}
	//	
	//	/**
	//	 * Updates the values of array that holds DividerPositions of splitPane
	//	 */
	//	public void updateBottomSplitPaneDividerArray(double[] array) {
	//		bottomSplitPaneDivider[0] = array[0];
	//		bottomSplitPaneDivider[1] = array[1];
	//	}
	
	//----------------------------
	
	/**
	 * Updates the SplitPane DividerPositions based on the saved array
	 */
	public void updateTopSplitPaneDivider() {
		topSplitPane.setDividerPositions(topSplitPaneDivider);
	}
	
	/**
	 * Updates the SplitPane DividerPositions based on the saved array
	 */
	public void updateBottomSplitPaneDivider() {
		bottomSplitPane.setDividerPositions(bottomSplitPaneDivider);
	}
	
	//----------------------------	
	
	/**
	 * Saves current divider positions of SplitPane into an array
	 */
	public void saveTopSplitPaneDivider() {
		topSplitPaneDivider = topSplitPane.getDividerPositions();
	}
	
	/**
	 * Saves current divider positions of SplitPane into an array
	 */
	public void saveBottomSplitPaneDivider() {
		bottomSplitPaneDivider = bottomSplitPane.getDividerPositions();
	}
	
	/**
	 * Turns the Library Mode Upside Down or opposite
	 * 
	 * @param turnDown
	 */
	public void turnUpsideDownSplitPane(boolean turnDown) {
		
		//Check if it can enter based on the library border pane position
		if ( ( turnDown && !topSplitPane.getItems().get(0).equals(Main.playListModesSplitPane) )
				|| ( !turnDown && topSplitPane.getItems().get(0).equals(Main.playListModesSplitPane) ))
			return;
		
		//this.saveTopSplitPaneDivider();
		double temp = topSplitPaneDivider[0];
		topSplitPaneDivider[0] = topSplitPaneDivider[1];
		topSplitPaneDivider[1] = temp;
		
		boolean libraryIsOnTop = topSplitPane.getItems().get(0).equals(Main.playListModesSplitPane);
		topSplitPane.getItems().clear();
		if (libraryIsOnTop) {
			//System.out.println("Entered first if!");
			topSplitPane.getItems().addAll(bottomSplitPane, Main.playListModesSplitPane);
		} else {
			//System.out.println("Entered second if!");
			topSplitPane.getItems().addAll(Main.playListModesSplitPane, bottomSplitPane);
		}
		
		this.updateTopSplitPaneDivider();
		
	}
	
	//----------------------------
	
	/**
	 * @return the topSplitPane
	 */
	public SplitPane getTopSplitPane() {
		return topSplitPane;
	}
	
	/**
	 * @return the bottomSplitPane
	 */
	public SplitPane getBottomSplitPane() {
		return bottomSplitPane;
	}
	
	/**
	 * @return the borderPane
	 */
	public BorderPane getBorderPane() {
		return borderPane;
	}
	
}
