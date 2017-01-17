package librarymode;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXToggleButton;

import application.Main;
import database.LocalDBManager;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import tools.ActionTool;
import tools.InfoTool;
import xplayer.presenter.XPlayerController;

/**
 * This class contains everything needed going on LibraryMode.
 *
 * @author SuperGoliath
 */
public class LibraryMode extends GridPane {
	
	@FXML
	private GridPane root;
	
	@FXML
	private BorderPane borderPane;
	
	@FXML
	private StackPane librariesStackView;
	
	@FXML
	private Button previous;
	
	@FXML
	private Button next;
	
	@FXML
	private Button newLibrary;
	
	@FXML
	private GridPane topGrid;
	
	@FXML
	private Button createLibrary;
	
	@FXML
	private JFXToggleButton selectionModeToggle;
	
	// ------------------------------------------------
	
	protected boolean dragDetected;
	
	/** The mechanism behind of opening multiple libraries. */
	public final MultipleLibraries multipleLibs = new MultipleLibraries();
	
	/**
	 * The mechanism which allows you to view the libraries as components with
	 * image etc.
	 */
	public final LibrariesViewer libraryViewer = new LibrariesViewer();
	
	/**
	 * The mechanism which allows you to transport items between libraries and
	 * more.
	 */
	public final LibrariesSearcher librariesSearcher = new LibrariesSearcher();
	
	/** The insert new library. */
	PreparedStatement insertNewLibrary;
	
	/**
	 * Default image of a library(which has not a costume one selected by the
	 * user.
	 */
	public static final Image defaultImage = InfoTool.getImageFromDocuments("library.png");
	
	/** This variable is used during the creation of a new library. */
	private final InvalidationListener creationInvalidator = new InvalidationListener() {
		@Override
		public void invalidated(Observable observable) {
			
			// Remove the Listener
			Main.renameWindow.showingProperty().removeListener(this);
			
			// !Showing && !XPressed
			if (!Main.renameWindow.isShowing() && !Main.renameWindow.isXPressed()) {
				
				Main.window.requestFocus();
				
				// Check if this name already exists
				String name = Main.renameWindow.getUserInput();
				
				// if can pass
				if (!libraryViewer.items.stream().anyMatch(lib -> lib.getLibraryName().equals(name))) {
					String dataBaseTableName;
					boolean validName;
					
					// Until the randomName doesn't already exists
					do {
						dataBaseTableName = ActionTool.returnRandomTableName();
						validName = !LocalDBManager.tableExists(dataBaseTableName);
					} while (!validName);
					
					try {
						
						// Create the dataBase table
						Main.dbManager.connection1.createStatement()
						        .executeUpdate("CREATE TABLE '" + dataBaseTableName + "'"
						                + "(PATH       TEXT    PRIMARY KEY   NOT NULL ,"
						                + "STARS       DOUBLE     NOT NULL," + "TIMESPLAYED  INT     NOT NULL,"
						                + "DATE        TEXT   	NOT NULL," + "HOUR        TEXT    NOT NULL)");
						
						// Create the Library
						Library currentLib = new Library(name, dataBaseTableName, 0, null, null, null, 1,
						        libraryViewer.items.size(), null, false);
						
						// Add the library
						currentLib.goOnSelectionMode(selectionModeToggle.isSelected());
						libraryViewer.addLibrary(currentLib);
						libraryViewer.update();
						
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
					} catch (Exception ex) {
						Main.logger.log(Level.WARNING, "", ex);
					}
					
					// update the positions
					updateLibrariesPosition();
				} else {
					Notifications.create().title("Dublicate Name")
					        .text("A Library or PlayList with this name already exists!").darkStyle().showConfirm();
				}
			}
		}
	};
	
	/**
	 * Constructor.
	 */
	public LibraryMode() {
		
		// FXMLLOADER
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "LibraryMode.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			//Main.logger.log(Level.WARNING, "", ex);
			ex.printStackTrace();
		}
		
		// Prepared Statement
		try {
			insertNewLibrary = Main.dbManager.connection1.prepareStatement(
			        "INSERT INTO LIBRARIES (NAME,TABLENAME,STARS,DATECREATED,TIMECREATED,DESCRIPTION,SAVEMODE,POSITION,LIBRARYIMAGE,OPENED) "
			                + "VALUES (?,?,?,?,?,?,?,?,?,?)");
		} catch (SQLException ex) {
			Main.logger.log(Level.WARNING, "", ex);
		}
	}
	
	/**
	 * Return the library with the given name.
	 *
	 * @param name the name
	 * @return the library with name
	 */
	public Library getLibraryWithName(String name) {
		
		// Find that
		for (Library library : libraryViewer.items)
			if (library.getLibraryName().equals(name))
				return library;
			
		return null;
	}
	
	/**
	 * Updates the positions of the libraries in the database.
	 */
	public void updateLibrariesPosition() {
		
	}
	
	/**
	 * Called as soon as FXML file has been loaded
	 */
	@FXML
	public void initialize() {
		
		// createLibrary
		createLibrary.setOnAction(a -> {
			if (!Main.renameWindow.isShowing()) {
				
				// Open rename window
				Main.renameWindow.show("", createLibrary);
				
				// Add the showing listener
				Main.renameWindow.showingProperty().addListener(creationInvalidator);
			}
		});
		
		// newLibrary
		newLibrary.setOnAction(a -> {
			if (!Main.renameWindow.isShowing()) {
				
				// Open rename window
				Main.renameWindow.show("", newLibrary);
				
				// Add the showing listener
				Main.renameWindow.showingProperty().addListener(creationInvalidator);
			}
		});
		newLibrary.visibleProperty().bind(Bindings.size(libraryViewer.items).isEqualTo(0));
		
		// selectionModeToggle
		selectionModeToggle.selectedProperty()
		        .addListener((observable , oldValue , newValue) -> libraryViewer.goOnSelectionMode(newValue));
		
		// searchLibrary
		topGrid.add(librariesSearcher, 2, 0);
		
		// previous
		previous.setOnAction(a -> libraryViewer.previous());
		
		// next
		next.setOnAction(a -> libraryViewer.next());
		
		// StackPane
		librariesStackView.getChildren().addAll(libraryViewer, librariesSearcher.region,
		        librariesSearcher.searchProgress);
		librariesStackView.setStyle("-fx-border-color:white; -fx-border-style:segments(4.0);");
		libraryViewer.toBack();
		
		// XPlayer - 0
		Main.xPlayersList.addXPlayerUI(new XPlayerController(0, 0, 0));
		Main.xPlayersList.getXPlayerUI(0).makeTheDisc(136, 136, Color.ORANGE, 35, Side.LEFT);
		Main.xPlayersList.getXPlayerUI(0).makeTheVisualizer(Side.RIGHT);
		// Main.xPlayersList.getXPlayerUI(0).resizeUI(100, 100)
		add(Main.xPlayersList.getXPlayerUI(0), 1, 1);
		
	}
	
	/**
	 * Gets the previous.
	 *
	 * @return the previous
	 */
	protected Button getPrevious() {
		return previous;
	}
	
	/**
	 * Gets the next.
	 *
	 * @return the next
	 */
	protected Button getNext() {
		return next;
	}
	
	/*-----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 						    Libraries Viewer
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	/**
	 * This class allows you to view the libraries.
	 *
	 * @author SuperGoliath
	 */
	public class LibrariesViewer extends Region {
		
		/** The context menu. */
		public LibraryContextMenu contextMenu = new LibraryContextMenu();
		
		/** The settings. */
		public LibrarySettings settings = new LibrarySettings();
		
		/** The Constant WIDTH. */
		static final double WIDTH = 120;
		
		/** The Constant HEIGHT. */
		static final double HEIGHT = WIDTH + ( WIDTH * 0.4 );
		
		/** The duration. */
		private final Duration duration = Duration.millis(500);
		
		/** The interpolator. */
		private final Interpolator interpolator = Interpolator.EASE_BOTH;
		
		/** The Constant SPACING. */
		private static final double SPACING = 120;
		
		/** The Constant LEFT_OFFSET. */
		private static final double LEFT_OFFSET = -110;
		
		/** The Constant RIGHT_OFFSET. */
		private static final double RIGHT_OFFSET = 110;
		
		/** The Constant SCALE_SMALL. */
		private static final double SCALE_SMALL = 0.6;
		
		/** The items. */
		ObservableList<Library> items = FXCollections.observableArrayList();
		
		/** The centered. */
		private Group centered = new Group();
		
		/** The left group. */
		private Group leftGroup = new Group();
		
		/** The center group. */
		private Group centerGroup = new Group();
		
		/** The right group. */
		private Group rightGroup = new Group();
		
		/** The center index. */
		int centerIndex = 0;
		
		/** The scroll bar. */
		protected ScrollBar scrollBar = new ScrollBar();
		
		/** The time line */
		private Timeline timeline = new Timeline();
		
		Rectangle clip = new Rectangle();
		
		/**
		 * Instantiates a new libraries viewer.
		 */
		// Constructor
		public LibrariesViewer() {
			
			this.setOnMouseMoved(m -> {
				
				if (dragDetected) {
					System.out.println("Mouse Moving... with drag detected");
					
					try {
						Robot robot = new Robot();
						robot.mouseMove((int) m.getScreenX(),
						        (int) this.localToScreen(this.getBoundsInLocal()).getMinY() + 2);
					} catch (AWTException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}
			});
			
			//clip.set
			clip.setSmooth(true);
			setClip(clip);
			// setStyle(
			// "-fx-background-color: linear-gradient(to bottom,black 60,
			// #141414 60.2%, orange 87%); -fx-border-color:white;
			// -fx-border-style:dotted;");
			
			// setup containerScroller bar
			
			scrollBar.setMax(items.size() - 1);
			scrollBar.setVisibleAmount(1);
			scrollBar.setUnitIncrement(1);
			scrollBar.setBlockIncrement(1);
			scrollBar.valueProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable ov) {
					// if (timeline.getStatus() != Status.RUNNING)
					// setCenterIndex((int) scrollBar.getValue());
				}
			});
			
			// setFocusTraversable(true);
			// setOnKeyReleased(key -> {
			// if (key.getCode() == KeyCode.LEFT) {
			// if (timeline.getStatus() != Status.RUNNING)
			// previous();
			// } else if (key.getCode() == KeyCode.RIGHT) {
			// if (timeline.getStatus() != Status.RUNNING)
			// next();
			// }
			//
			// });
			
			// create content
			centered.getChildren().addAll(leftGroup, rightGroup, centerGroup);
			
			getChildren().addAll(centered);
			
		}
		
		public ObservableList<Library> getItems() {
			return items;
		}
		
	
		/* (non-Javadoc)
		 * @see javafx.scene.Parent#layoutChildren()
		 */
		@Override
		protected void layoutChildren() {	
			
			// update clip to our size
			clip.setWidth(getWidth());
			clip.setHeight(getHeight());
			
			// keep centered centered
			centered.setLayoutY( ( getHeight() - HEIGHT ) / 2);
			centered.setLayoutX( ( getWidth() - WIDTH ) / 2);
			
			// position containerScroller bar at bottom
			scrollBar.setLayoutX(10);
			scrollBar.setLayoutY(getHeight() - 25);
			scrollBar.resize(getWidth() - 20, 15);
			
		}
		
		/**
		 * Go on selection mode.
		 *
		 * @param way the way
		 */
		public void goOnSelectionMode(boolean way) {
			for (Library library : items)
				library.goOnSelectionMode(way);
		}
		
		/**
		 * Add multiple libraries at once.
		 *
		 * @param libraries the libraries
		 */
		public void addMultipleLibraries(Library[] libraries) {
			for (int i = 0; i < libraries.length; i++)
				addLibrary(libraries[i]);
			
			// update
			update();
		}
		
		/**
		 * Add the new library.
		 *
		 * @param library the library
		 */
		public void addLibrary(Library library) {
			items.add(library);
			library.setOnMouseClicked(m -> {
				
				if (m.getButton() == MouseButton.PRIMARY || m.getButton() == MouseButton.MIDDLE) {
					
					// If it isn't the same library again
					if ( ( (Library) centerGroup.getChildren().get(0) ).getPosition() != library.getPosition()) {
						
						centerIndex = library.getPosition();
						scrollBar.setValue(centerIndex);
						update();
					}
					
				} else if (m.getButton() == MouseButton.SECONDARY) {
					
					// if isn't the same library again
					if ( ( (Library) centerGroup.getChildren().get(0) ).getPosition() != library.getPosition()) {
						
						centerIndex = library.getPosition();
						scrollBar.setValue(centerIndex);
						update();
						
						timeline.setOnFinished(v -> {
							Bounds bounds = library.localToScreen(library.getBoundsInLocal());
							contextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3,
							        bounds.getMinY() + bounds.getHeight() / 4, library);
							timeline.setOnFinished(null);
						});
						
					} else { // if is the same library again
						Bounds bounds = library.localToScreen(library.getBoundsInLocal());
						contextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3,
						        bounds.getMinY() + bounds.getHeight() / 4, library);
					}
				}
				
			});
			
			// MAX
			scrollBar.setMax(items.size() + 1);
		}
		
		/**
		 * Recalculate the position of all the libraries.
		 *
		 * @param commit the commit
		 */
		public void updateLibrariesPositions(boolean commit) {
			
			for (int i = 0; i < items.size(); i++)
				items.get(i).updatePosition(i);
			
			if (commit)
				Main.dbManager.commit();
		}
		
		/**
		 * Recalculate the center index after a delete occurs.
		 */
		public void calculateCenterAfterDelete() {
			
			// center index
			if (!leftGroup.getChildren().isEmpty())
				centerIndex = leftGroup.getChildren().size() - 1;
			else
				// if (!rightGroup.getChildren().isEmpty())
				// centerIndex = 0;
				// else
				centerIndex = 0;
			
			// Max
			scrollBar.setMax(items.size() - 1);
			
			update();
			
		}
		
		/**
		 * Sets the center index.
		 *
		 * @param i the new center index
		 */
		public void setCenterIndex(int i) {
			centerIndex = i;
			update();
		}
		
		/**
		 * Goes to next Item (RIGHT).
		 */
		public void next() {
			if (centerIndex + 1 < items.size()) {
				++centerIndex;
				update();
			}
			
		}
		
		/**
		 * Goes to previous item(LEFT).
		 */
		public void previous() {
			if (centerIndex > 0) {
				--centerIndex;
				update();
			}
		}
		
		/**
		 * Update the library viewer so it shows the center index correctly.
		 */
		public void update() {
			
			// Reconstruct Groups
			leftGroup.getChildren().clear();
			centerGroup.getChildren().clear();
			rightGroup.getChildren().clear();
			
			if (!items.isEmpty()) {
				
				// If only on item exists
				if (items.size() == 1) {
					centerGroup.getChildren().add(items.get(0));
					centerIndex = 0;
				} else {
					
					// LEFT,
					for (int i = 0; i < centerIndex; i++)
						leftGroup.getChildren().add(items.get(i));
					
					// CENTER,
					if (centerIndex == items.size()) {
						centerGroup.getChildren().add(leftGroup.getChildren().get(centerIndex - 1));
					} else
						centerGroup.getChildren().add(items.get(centerIndex));
					
					// RIGHT
					for (int i = items.size() - 1; i > centerIndex; i--)
						rightGroup.getChildren().add(items.get(i));
					
				}
				
				// stop old time line
				if (timeline.getStatus() == Status.RUNNING)
					timeline.stop();
				
				// clear the old keyFrames
				timeline.getKeyFrames().clear();
				final ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();
				
				// LEFT KEYFRAMES
				for (int i = 0; i < leftGroup.getChildren().size(); i++) {
					
					final Library it = items.get(i);
					
					double newX = -leftGroup.getChildren().size() *
					        
					        SPACING + SPACING * i + LEFT_OFFSET;
					
					keyFrames.add(new KeyFrame(duration,
					        
					        new KeyValue(it.translateXProperty(), newX, interpolator),
					        
					        new KeyValue(it.scaleXProperty(), SCALE_SMALL, interpolator),
					        
					        new KeyValue(it.scaleYProperty(), SCALE_SMALL, interpolator)));
					
					// new KeyValue(it.angle, 45.0, INTERPOLATOR)));
					
				}
				
				// CENTER ITEM KEYFRAME
				final Library centerItem;
				if (items.size() == 1)
					centerItem = items.get(0);
				else
					centerItem = (Library) centerGroup.getChildren().get(0);
				
				keyFrames.add(new KeyFrame(duration,
				        
				        new KeyValue(centerItem.translateXProperty(), 0, interpolator),
				        
				        new KeyValue(centerItem.scaleXProperty(), 1.0, interpolator),
				        
				        new KeyValue(centerItem.scaleYProperty(), 1.0, interpolator)));// ,
				
				// new KeyValue(centerItem.rotationTransform.angleProperty(),
				// 360)));
				
				// new KeyValue(centerItem.angle, 90, INTERPOLATOR)));
				
				// RIGHT KEYFRAMES
				for (int i = 0; i < rightGroup.getChildren().size(); i++) {
					
					final Library it = items.get(items.size() - i - 1);
					
					final double newX = rightGroup.getChildren().size() *
					        
					        SPACING - SPACING * i + RIGHT_OFFSET;
					
					keyFrames.add(new KeyFrame(duration,
					        
					        new KeyValue(it.translateXProperty(), newX, interpolator),
					        
					        new KeyValue(it.scaleXProperty(), SCALE_SMALL, interpolator),
					        
					        // new
					        // KeyValue(it.rotationTransform.angleProperty(),
					        // -360)));
					        
					        new KeyValue(it.scaleYProperty(), SCALE_SMALL, interpolator)));
					
					// new KeyValue(it.angle, 135.0, INTERPOLATOR)));
					
				}
				
				// play animation
				timeline.setAutoReverse(true);
				timeline.play();
			}
			
			// Previous and Next Visibility
			if (rightGroup.getChildren().isEmpty())
				getNext().setVisible(false);
			else
				getNext().setVisible(true);
			
			if (leftGroup.getChildren().isEmpty())
				getPrevious().setVisible(false);
			else
				getPrevious().setVisible(true);
			
		}
		
	}
	
}
