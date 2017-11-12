/**
 * 
 */
package main.java.com.goxr3plus.xr3player.application.modes.librarymode;

import java.util.List;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;

/**
 * @author GOXR3PLUS
 *
 */
public class TeamViewer {
	
	private final Viewer viewer;
	private final LibraryMode mode;
	
	/**
	 * Constructor
	 * 
	 * @param mode
	 */
	public TeamViewer(LibraryMode mode) {
		this.mode = mode;
		viewer = new Viewer(mode.getHorizontalScrollBar());
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
	 * 		                   TEAM VIEWER
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
	 * @return the viewer
	 */
	public Viewer getViewer() {
		return viewer;
	}
	
	/**
	 * This class allows you to view the libraries.
	 *
	 * @author GOXR3PLUS STUDIO
	 */
	public class Viewer extends Region {
		
		/** The Constant WIDTH. */
		private double width = 120;
		
		/** The Constant HEIGHT. */
		private double height = width + 0.4 * width;
		
		/** The duration. */
		private final Duration duration = Duration.millis(450);
		
		/** The interpolator. */
		private final Interpolator interpolator = Interpolator.EASE_BOTH;
		
		/** The Constant SPACING. */
		private double spacing = 120;
		
		/** The Constant LEFT_OFFSET. */
		private double leftOffSet = -110;
		
		/** The Constant RIGHT_OFFSET. */
		private double rightOffSet = 110;
		
		/** The Constant SCALE_SMALL. */
		private static final double SCALE_SMALL = 0.6;
		
		/** The items. */
		private final ObservableList<Library> itemsObservableList = FXCollections.observableArrayList();
		/**
		 * This class wraps an ObservableList
		 */
		private final SimpleListProperty<Library> itemsWrapperProperty = new SimpleListProperty<>(itemsObservableList);
		
		/**
		 * Holds the center item of TeamViewer
		 */
		private final ObjectProperty<Library> centerItemProperty = new SimpleObjectProperty<>(null);
		
		/** The centered. */
		private final Group centered = new Group();
		
		/** The left group. */
		private final Group leftGroup = new Group();
		
		/** The center group. */
		private final Group centerGroup = new Group();
		
		/** The right group. */
		private final Group rightGroup = new Group();
		
		/** The center index. */
		private int centerIndex;
		
		/** The scroll bar. */
		private final ScrollBar scrollBar;
		
		/** The time line */
		private final Timeline timeline = new Timeline();
		
		private final Rectangle clip = new Rectangle();
		
		/** The pause transition. */
		private final PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1.5));
		private StringProperty searchWord = new SimpleStringProperty("");
		
		/**
		 * Constructor
		 * 
		 * @param scrollBar
		 */
		public Viewer(ScrollBar scrollBar) {
			this.scrollBar = scrollBar;
			
			// -- Scroll Listener
			setOnScroll(scroll -> {
				if (scroll.getDeltaX() < 0)
					next();
				else if (scroll.getDeltaX() > 0)
					previous();
			});
			
			// --- Mouse Listeners
			setOnMouseEntered(m -> {
				if (!isFocused())
					requestFocus();
			});
			
			// -- KeyListeners
			setOnKeyPressed(key -> {
				if (key.getCode() == KeyCode.RIGHT)
					next();
				else if (key.getCode() == KeyCode.LEFT)
					previous();
				else if (key.getCode() == KeyCode.BACK_SPACE)
					searchWord.set("");
				
				//Local Search 
				if (!key.isControlDown() && ( key.getCode().isDigitKey() || key.getCode().isKeypadKey() || key.getCode().isLetterKey() || key.getCode() == KeyCode.SPACE )) {
					String keySmall = key.getText();
					searchWord.set(searchWord.get() + keySmall);
					pauseTransition.playFromStart();
					
					//Check if searchWord is empty
					if (!searchWord.get().isEmpty()) {
						boolean[] found = { false };
						//Find the first matching item
						getItemsObservableList().forEach(library -> {
							if (library.getLibraryName().contains(searchWord.get()) && !found[0]) {
								this.setCenterIndex(library.getPosition());
								found[0] = true;
							}
						});
					}
				}
			});
			
			// PauseTransition
			pauseTransition.setOnFinished(f -> searchWord.set(""));
			
			// this.setOnMouseMoved(m -> {
			//
			// if (dragDetected) {
			// System.out.println("Mouse Moving... with drag detected");
			//
			// try {
			// Robot robot = new Robot();
			// robot.mouseMove((int) m.getScreenX(),
			// (int) this.localToScreen(this.getBoundsInLocal()).getMinY() + 2);
			// } catch (AWTException ex) {
			// ex.printStackTrace();
			// }
			// }
			// })
			
			// clip.set
			setClip(clip);
			setStyle("-fx-background-color: linear-gradient(to bottom,transparent 60,#141414 60.2%, #00E5BB 87%);");
			//setStyle("-fx-background-color: linear-gradient(to bottom,black 60,#141414 60.2%, purple 87%);")
			
			// ScrollBar
			scrollBar.visibleProperty().bind(itemsWrapperProperty.sizeProperty().greaterThan(2));
			scrollBar.valueProperty().addListener((observable , oldValue , newValue) -> {
				int newVal = (int) Math.round(newValue.doubleValue());
				int oldVal = (int) Math.round(oldValue.doubleValue());
				// new!=old
				if (newVal != oldVal)
					setCenterIndex(newVal);
				
				// System.out.println(scrollBar.getValue())
			});
			
			// create content
			centered.getChildren().addAll(leftGroup, rightGroup, centerGroup);
			
			getChildren().addAll(centered);
		}
		
		/**
		 * The Collection that holds all the Library Viewer Items
		 * 
		 * @return The Collection that holds all the Libraries
		 */
		public ObservableList<Library> getItemsObservableList() {
			return itemsObservableList;
		}
		
		/**
		 * This class wraps an ObservableList
		 *
		 * @return the itemsWrapperProperty
		 */
		public SimpleListProperty<Library> itemsWrapperProperty() {
			return itemsWrapperProperty;
		}
		
		/**
		 * @return the centerItem
		 */
		public ObjectProperty<Library> centerItemProperty() {
			return centerItemProperty;
		}
		
		/**
		 * @return the centerItem
		 */
		public Library getSelectedItem() {
			return centerItemProperty.get();
		}
		
		/**
		 * Returns the Index of the List center Item
		 * 
		 * @return Returns the Index of the List center Item
		 */
		public int getCenterIndex() {
			return centerIndex;
		}
		
		// ----About the last size of each Library
		double lastSize;
		
		// ----About the width and height of LibraryMode Clip
		int previousWidth;
		int previousHeight;
		
		int counter;
		double var = 1.5;
		
		@Override
		protected void layoutChildren() {
			
			// update clip to our size
			clip.setWidth(getWidth());
			clip.setHeight(getHeight());
			
			// keep centered centered
			
			width = getHeight();
			height = width;// + (WIDTH * 0.4)
			
			double variable = width / var;
			centered.setLayoutX( ( getWidth() - variable ) / 2); //WIDTH/var) / 2)
			centered.setLayoutY( ( getHeight() - variable ) / 2); //HEIGHT / var) / 2)
			
			// centered.setLayoutX((getWidth() - WIDTH) / 2)
			// centered.setLayoutY((getHeight() - HEIGHT) / 2)
			
			//-----jfSlider.setLayoutX(getWidth() / 2 - 150);
			
			//jfSlider.setLayoutX(0);
			//jfSlider.setLayoutY(double g snoopy dogg);
			//jfSlider.resize(getWidth(), 15);
			
			//--- jfSlider.resize(300, 15);
			//--- jfSlider.setLayoutY(getHeight() - jfSlider.getHeight());
			
			// AVOID DOING CALCULATIONS WHEN THE CLIP SIZE IS THE SAME
			// if (previousWidth != (int) WIDTH ||
			if (previousHeight != (int) height) {
				// System.out.println("Updating Library Size")
				
				// Update ImageView width and height
				spacing = height / ( var + 0.5 );
				leftOffSet = - ( spacing - 10 );
				rightOffSet = -leftOffSet;
				// For-Each
				itemsObservableList.forEach(library -> {
					double size = height / var;
					
					// --
					library.getImageView().setFitWidth(size);
					library.getImageView().setFitHeight(size);
					library.setMaxWidth(size);
					library.setMaxHeight(size);
				});
				
				// Dont Fuck the CPU
				double currentSize = width / var; // the current size of each
				// library
				boolean doUpdate = Math.abs(currentSize - lastSize) > 2;
				// System.out.println("Do update?:" + doUpdate + " , " +
				// Math.abs(currentSize - lastSize) + "SSD.U2\n")
				lastSize = currentSize;
				if (doUpdate)
					update();
			}
			
			previousWidth = (int) width;
			previousHeight = (int) height;
			// System.out.println("Counter:" + (++counter) + " , " + getWidth() + "," + getHeight())
			
		}
		
		/**
		 * Go on selection mode.
		 *
		 * @param way
		 *            the way
		 */
		public void goOnSelectionMode(boolean way) {
			for (Library library : itemsObservableList)
				library.goOnSelectionMode(way);
		}
		
		/**
		 * Add multiple libraries at once.
		 *
		 * @param library
		 *            List full of Libraries
		 */
		public void addMultipleItems(List<Library> library) {
			
			//Check it first
			if (library == null || library.isEmpty())
				return;
			
			//Add all them
			library.forEach(l -> addItem(l, false));
			
			// update
			update();
		}
		
		/**
		 * Add the new library.
		 *
		 * @param library
		 *            the library
		 * @param update
		 *            Do the update on the list?
		 */
		public void addItem(Library library , boolean update) {
			itemsObservableList.add(library);
			
			// --
			double size = height / var;
			
			library.getImageView().setFitWidth(size);
			library.getImageView().setFitHeight(size);
			library.setMaxWidth(size);
			library.setMaxHeight(size);
			
			// --
			library.setOnMouseClicked(m -> {
				
				if (m.getButton() == MouseButton.PRIMARY || m.getButton() == MouseButton.MIDDLE) {
					
					// If it isn't the same library again
					if ( ( (Library) centerGroup.getChildren().get(0) ).getPosition() != library.getPosition()) {
						
						setCenterIndex(library.getPosition());
						// scrollBar.setValue(library.getPosition())
					}
					
				} else if (m.getButton() == MouseButton.SECONDARY) {
					
					// if isn't the same library again
					if ( ( (Library) centerGroup.getChildren().get(0) ).getPosition() != library.getPosition()) {
						
						setCenterIndex(library.getPosition());
						// scrollBar.setValue(library.getPosition())
						
						timeline.setOnFinished(v -> {
							Bounds bounds = library.localToScreen(library.getBoundsInLocal());
							mode.librariesContextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3, bounds.getMinY() + bounds.getHeight() / 4, library);
							// mode.contextMenu.show(Main.window, m.getScreenX(), m.getScreenY(), library);
							timeline.setOnFinished(null);
						});
						
					} else { // if is the same library again
						//			Bounds bounds = library.localToScreen(library.getBoundsInLocal());
						//			contextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3,
						//				bounds.getMinY() + bounds.getHeight() / 4, library);
						mode.librariesContextMenu.show(Main.window, m.getScreenX() - 5, m.getScreenY() - 15, library);
					}
				}
				
			});
			
			// MAX
			scrollBar.setMax(itemsObservableList.size() - 1.00);
			
			//Update?
			if (update)
				update();
		}
		
		/**
		 * Deletes the specific Library from the list
		 * 
		 * @param library
		 *            Library to be deleted
		 */
		public void deleteItem(Library library) {
			itemsObservableList.remove(library);
			
			for (int i = 0; i < itemsObservableList.size(); i++)
				itemsObservableList.get(i).updatePosition(i);
			
			calculateCenterAfterDelete();
		}
		
		/**
		 * Recalculate the center index after a delete occurs.
		 */
		private void calculateCenterAfterDelete() {
			
			// center index
			if (!leftGroup.getChildren().isEmpty())
				centerIndex = leftGroup.getChildren().size() - 1;
			else
				// if (!rightGroup.getChildren().isEmpty())	
				// centerIndex = 0	
				// else
				centerIndex = 0;
			
			// Max
			scrollBar.setMax(itemsObservableList.size() - 1.00);
			
			update();
			
		}
		
		/**
		 * Sets the center index.
		 *
		 * @param i
		 *            the new center index
		 */
		public void setCenterIndex(int i) {
			if (centerIndex != i) {
				centerIndex = i;
				update();
				
				// Update the ScrollBar Value
				scrollBar.setValue(centerIndex);
			}
		}
		
		/**
		 * Checks if this item is the center item
		 * 
		 * @param library
		 * @return True if it is
		 */
		public boolean isCenterItem(Library library) {
			return !itemsObservableList.isEmpty() && itemsObservableList.get(centerIndex).equals(library);
		}
		
		/**
		 * Goes to next Item (RIGHT).
		 */
		public void next() {
			if (centerIndex + 1 < itemsObservableList.size())
				setCenterIndex(centerIndex + 1);
		}
		
		/**
		 * Goes to previous item(LEFT).
		 */
		public void previous() {
			if (centerIndex > 0)
				setCenterIndex(centerIndex - 1);
		}
		
		/**
		 * Update the library viewer so it shows the center index correctly.
		 */
		public void update() {
			
			// Reconstruct Groups
			leftGroup.getChildren().clear();
			centerGroup.getChildren().clear();
			rightGroup.getChildren().clear();
			
			if (!itemsObservableList.isEmpty()) {
				
				// If only on item exists
				if (itemsObservableList.size() == 1) {
					centerGroup.getChildren().add(itemsObservableList.get(0));
					centerIndex = 0;
				} else {
					
					// LEFT,
					for (int i = 0; i < centerIndex; i++)
						leftGroup.getChildren().add(itemsObservableList.get(i));
					
					// CENTER,
					if (centerIndex == itemsObservableList.size()) {
						centerGroup.getChildren().add(leftGroup.getChildren().get(centerIndex - 1));
					} else
						centerGroup.getChildren().add(itemsObservableList.get(centerIndex));
					
					// RIGHT
					for (int i = itemsObservableList.size() - 1; i > centerIndex; i--)
						rightGroup.getChildren().add(itemsObservableList.get(i));
					
				}
				
				// stop old time line
				if (timeline.getStatus() == Status.RUNNING)
					timeline.stop();
				
				// clear the old keyFrames
				timeline.getKeyFrames().clear();
				final ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();
				
				// LEFT KEYFRAMES
				for (int i = 0; i < leftGroup.getChildren().size(); i++) {
					
					final Library it = itemsObservableList.get(i);
					
					double newX = -leftGroup.getChildren().size() *
							
							spacing + spacing * i + leftOffSet;
					
					keyFrames.add(new KeyFrame(duration,
							
							new KeyValue(it.translateXProperty(), newX, interpolator),
							
							new KeyValue(it.scaleXProperty(), SCALE_SMALL, interpolator),
							
							new KeyValue(it.scaleYProperty(), SCALE_SMALL, interpolator)));
					
					// new KeyValue(it.angle, 45.0, INTERPOLATOR)))
					
				}
				
				// CENTER ITEM KEYFRAME
				final Library centerItem;
				if (itemsObservableList.size() == 1)
					centerItem = itemsObservableList.get(0);
				else
					centerItem = (Library) centerGroup.getChildren().get(0);
				
				//The Property Center Item
				this.centerItemProperty.set(centerItem);
				
				keyFrames.add(new KeyFrame(duration,
						
						new KeyValue(centerItem.translateXProperty(), 0, interpolator),
						
						new KeyValue(centerItem.scaleXProperty(), 1.0, interpolator),
						
						new KeyValue(centerItem.scaleYProperty(), 1.0, interpolator)));// ,
				
				// new KeyValue(centerItem.rotationTransform.angleProperty(),
				// 360)));
				
				// new KeyValue(centerItem.angle, 90, INTERPOLATOR)));
				
				// RIGHT KEYFRAMES
				for (int i = 0; i < rightGroup.getChildren().size(); i++) {
					
					final Library it = itemsObservableList.get(itemsObservableList.size() - i - 1);
					
					final double newX = rightGroup.getChildren().size() *
							
							spacing - spacing * i + rightOffSet;
					
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
			} else
				//The Property Center Item
				this.centerItemProperty.set(null);
			
			mode.getNext().setDisable(rightGroup.getChildren().isEmpty());
			mode.getPrevious().setDisable(leftGroup.getChildren().isEmpty());
			
		}
		
		/**
		 * @return the timeline
		 */
		public Timeline getTimeline() {
			return timeline;
		}
		
		/**
		 * @return the searchWord
		 */
		public StringProperty searchWordProperty() {
			return searchWord;
		}
		
	}
	
}
