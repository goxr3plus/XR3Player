package com.goxr3plus.xr3player.controllers.general;

import java.util.Comparator;
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
import javafx.collections.transformation.SortedList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.librarymode.Library;
import com.goxr3plus.xr3player.controllers.librarymode.LibraryMode;
import com.goxr3plus.xr3player.controllers.loginmode.LoginMode;
import com.goxr3plus.xr3player.controllers.loginmode.User;
import com.goxr3plus.xr3player.controllers.smartcontroller.MediaViewer;
import com.goxr3plus.xr3player.controllers.smartcontroller.SmartController;

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
	private Duration duration;

	/** The interpolator. */
	private Interpolator interpolator;

	/** The Constant SPACING. */
	private double spacing = 120;

	/** The Constant LEFT_OFFSET. */
	private double leftOffSet = -110;

	/** The Constant RIGHT_OFFSET. */
	private double rightOffSet = 110;

	/** The Constant SCALE_SMALL. */
	private static final double SCALE_SMALL = 0.6;

	/** The items. */
	private final ObservableList<Node> itemsObservableList = FXCollections.observableArrayList();

	private SortedList<Node> sortedList;

	/**
	 * This class wraps an ObservableList
	 */
	private final SimpleListProperty<Node> itemsWrapperProperty = new SimpleListProperty<>(itemsObservableList);

	/**
	 * Holds the center item of TeamViewer
	 */
	private final ObjectProperty<Node> centerItemProperty = new SimpleObjectProperty<>(null);

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
	private ScrollBar scrollBar;

	/** The time line */
	private final Timeline timeline = new Timeline();

	private final Rectangle clip = new Rectangle();

	/** The pause transition. */
	private final PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1.5));
	private StringProperty searchWord = new SimpleStringProperty("");

	private LibraryMode libraryMode;
	private LoginMode loginMode;
	private SmartController smartController;

	public Viewer(LibraryMode libraryMode, ScrollBar scrollBar) {
		this.libraryMode = libraryMode;
		this.scrollBar = scrollBar;
		duration = Duration.millis(450);
		interpolator = Interpolator.EASE_BOTH;
		sortedList = new SortedList<>(itemsObservableList, (a, b) -> String.CASE_INSENSITIVE_ORDER
				.compare(((Library) a).getLibraryName(), ((Library) b).getLibraryName()));
		init();
	}

	public Viewer(LoginMode loginMode, ScrollBar scrollBar) {
		this.loginMode = loginMode;
		this.scrollBar = scrollBar;
		duration = Duration.millis(450);
		interpolator = Interpolator.EASE_BOTH;
		sortedList = new SortedList<>(itemsObservableList);
		init();
	}

	public Viewer(SmartController smartController) {
		this.smartController = smartController;
		duration = Duration.millis(250);
		interpolator = Interpolator.EASE_BOTH;
		init();
	}

	/**
	 * Constructor
	 *
	 */
	private void init() {

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
			else if (key.getCode() == KeyCode.A && key.isControlDown()) {
				if (libraryMode != null)
					if (getItemsObservableList().size() == 0)
						getItemsObservableList().forEach(library -> ((Library) library).setSelected(true));
					else {
						boolean select = !((Library) getItemsObservableList().get(0)).isSelected();
						getItemsObservableList().forEach(library -> ((Library) library).setSelected(select));
					}
			} else if ((key.getCode() == KeyCode.D && key.isControlDown()) || key.getCode() == KeyCode.DELETE) {
				if (libraryMode != null)
					libraryMode.deleteLibraries(null, null);
			}

			// Local Search
			if (!key.isControlDown() && (key.getCode().isDigitKey() || key.getCode().isKeypadKey()
					|| key.getCode().isLetterKey() || key.getCode() == KeyCode.SPACE)) {
				String keySmall = key.getText();
				searchWord.set(searchWord.get() + keySmall);
				pauseTransition.playFromStart();

				// Check if searchWord is empty
				if (!searchWord.get().isEmpty()) {
					boolean[] found = { false };
					if (libraryMode != null) // LibraryMode
						// Find the first matching item
						getItemsObservableList().forEach(library -> {
							if (((Library) library).getLibraryName().contains(searchWord.get()) && !found[0]) {
								setCenterItem(library);
								found[0] = true;
							}
						});
					else if (loginMode != null) // LoginMode
						// Find the first matching item
						getItemsObservableList().forEach(user -> {
							if (((User) user).getName().contains(searchWord.get()) && !found[0]) {
								this.setCenterItem(user);
								found[0] = true;
							}
						});
				}
			}
		});

		// PauseTransition
		pauseTransition.setOnFinished(f -> searchWord.set(""));

		// clip.set
		setClip(clip);
		if (smartController == null)
			setStyle("-fx-background-color: linear-gradient(to bottom,transparent 60,#141414 60.2%, #00E5BB 87%);");
		else
			setStyle("-fx-background-color: #151515");
		// setStyle("-fx-background-color: linear-gradient(to bottom,black 60,#141414
		// 60.2%, purple 87%);")

		// ScrollBar
		if (scrollBar != null) {
			scrollBar.visibleProperty().bind(itemsWrapperProperty.sizeProperty().greaterThan(2));
			scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
				int newVal = (int) Math.round(newValue.doubleValue());
				int oldVal = (int) Math.round(oldValue.doubleValue());
				// new!=old
				if (newVal != oldVal)
					setCenterIndex(newVal);

				// System.out.println(scrollBar.getValue())
			});
		}

		// create content
		centered.getChildren().addAll(leftGroup, rightGroup, centerGroup);

		getChildren().addAll(centered);

		// Now let's check if SmartController !=null
		if (smartController != null) {

		}

	}

	/**
	 * The Collection that holds all the Library Viewer Items
	 * 
	 * @return The Collection that holds all the Libraries
	 */
	public ObservableList<Node> getItemsObservableList() {
		return itemsObservableList;
	}

	/**
	 * This class wraps an ObservableList
	 *
	 * @return the itemsWrapperProperty
	 */
	public SimpleListProperty<Node> itemsWrapperProperty() {
		return itemsWrapperProperty;
	}

	/**
	 * @return the centerItem
	 */
	public ObjectProperty<Node> centerItemProperty() {
		return centerItemProperty;
	}

	/**
	 * @return the centerItem
	 */
	public Node getSelectedItem() {
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
		height = width;

		boolean doUpdate = false;

		// Libraries and Users
		if (smartController == null) {

			// Size
			double size = height / var;

			// AVOID DOING CALCULATIONS WHEN THE CLIP SIZE IS THE SAME
			if (previousHeight != (int) height) {

				// Update ImageView width and height
				spacing = height / (var + 0.5);
				leftOffSet = -(spacing - size / 2.0);
				rightOffSet = -leftOffSet;

				// For-Each
				if (libraryMode != null)
					itemsObservableList.forEach(library -> {
						// --
						Library libraryy = (Library) library;
						libraryy.getImageView().setFitWidth(size);
						libraryy.getImageView().setFitHeight(size);
						libraryy.setMaxSize(size, size);
					});
				else if (loginMode != null)
					itemsObservableList.forEach(user -> {
						// --
						User userr = (User) user;
						userr.getImageView().setFitWidth(size);
						userr.getImageView().setFitHeight(size);
						userr.setMaxSize(size, size);
					});

				// the current size of each
				double currentSize = width / var;
				doUpdate = Math.abs(currentSize - lastSize) > 2;
				lastSize = currentSize;
			}

			centered.setLayoutX((getWidth() - size) / 2);
			centered.setLayoutY((getHeight() - size) / 2);
		} else {

			// Size
			double size = height * var;

			// AVOID DOING CALCULATIONS WHEN THE CLIP SIZE IS THE SAME
			if (previousHeight != (int) height) {

				// Update ImageView width and height
				spacing = height / (var - 0.4);
				leftOffSet = -0;
				rightOffSet = -leftOffSet;

				// For-Each
				itemsObservableList.forEach(mediaViewerr -> {
					// --
					MediaViewer mediaViewer = (MediaViewer) mediaViewerr;
					mediaViewer.getImageView().setFitWidth(size);
					mediaViewer.getImageView().setFitHeight(size);
					mediaViewer.setMaxSize(size, size);
					mediaViewer.getNameLabel().setMaxSize(size, size);
				});

				// the current size of each
				double currentSize = width / var;
				doUpdate = Math.abs(currentSize - lastSize) > 2;
				lastSize = currentSize;

			}

			centered.setLayoutX((getWidth() - size) / 2);
			centered.setLayoutY((getHeight() - size) / 2);

		}

		// Update?
		if (doUpdate)
			update();

		previousWidth = (int) width;
		previousHeight = (int) height;

	}

	/**
	 * Set a custom comporator to the sortList
	 * 
	 * @param comparator
	 */
	public void sortByComparator(Comparator<Node> comparator) {
		sortedList.setComparator(null);
		sortedList.setComparator(comparator);
		update();
	}

	/**
	 * Add multiple libraries at once.
	 *
	 * @param list List full of Libraries
	 */
	public void addMultipleItems(List<Node> list) {

		// Check it first
		if (list == null || list.isEmpty())
			return;

		// Add all them
		list.forEach(l -> addItem(l, false));

		// update
		calculateCenterIndex();
	}

	/**
	 * Add the new library.
	 *
	 * @param node   the library
	 * @param update Do the update on the list?
	 */
	public void addItem(Node node, boolean update) {
		itemsObservableList.add(node);

		// --
		double size = height / var;

		if (libraryMode != null) {

			Library library = (Library) node;
			library.getImageView().setFitWidth(size);
			library.getImageView().setFitHeight(size);
			library.setMaxSize(size, size);

			// --
			node.setOnMouseClicked(m -> {

				if (m.getButton() == MouseButton.PRIMARY || m.getButton() == MouseButton.MIDDLE) {

					// Primary MouseButton - //Unselect all the libraries
					if (m.getButton() == MouseButton.PRIMARY)
						this.itemsObservableList.forEach(lib -> ((Library) lib).setSelected(false));
					else if (m.getButton() == MouseButton.MIDDLE) // Select the library
						library.setSelected(!library.isSelected());

					// If it isn't the same library again
					if (((Library) centerGroup.getChildren().get(0)) != library) {

						setCenterItem(library);
						// scrollBar.setValue(library.getPosition())

					}

				} else if (m.getButton() == MouseButton.SECONDARY) {

					// if isn't the same library again
					if (((Library) centerGroup.getChildren().get(0)) != library) {

						setCenterIndex(itemsObservableList.indexOf(library));
						// scrollBar.setValue(library.getPosition())

						timeline.setOnFinished(v -> {
							Bounds bounds = node.localToScreen(node.getBoundsInLocal());
							libraryMode.librariesContextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3,
									bounds.getMinY() + bounds.getHeight() / 4, library);
							timeline.setOnFinished(null);
						});

					} else { // if is the same library again
						libraryMode.librariesContextMenu.show(Main.window, m.getScreenX() - 5, m.getScreenY() - 15,
								library);
					}
				}

			});
		} else if (loginMode != null) {

			User user = (User) node;
			user.getImageView().setFitWidth(size);
			user.getImageView().setFitHeight(size);
			user.setMaxSize(size, size);

			// --
			user.setOnMouseClicked(m -> {

				if (m.getButton() == MouseButton.PRIMARY || m.getButton() == MouseButton.MIDDLE) {

					// If it isn't the same User again
					if (((User) centerGroup.getChildren().get(0)) != user) {

						setCenterIndex(itemsObservableList.indexOf(user));
						// scrollBar.setValue(library.getPosition())
					}

				} else if (m.getButton() == MouseButton.SECONDARY) {

					// if isn't the same User again
					if (((User) centerGroup.getChildren().get(0)) != user) {

						setCenterItem(user);
						// scrollBar.setValue(library.getPosition())

						timeline.setOnFinished(v -> {
							Bounds bounds = user.localToScreen(user.getBoundsInLocal());
							loginMode.userContextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3,
									bounds.getMinY() + bounds.getHeight() / 4, user);
							timeline.setOnFinished(null);
						});

					} else { // if is the same User again
						Bounds bounds = user.localToScreen(user.getBoundsInLocal());
						loginMode.userContextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3,
								bounds.getMinY() + bounds.getHeight() / 4, user);
					}
				}

			});
		} else if (smartController != null) {

			MediaViewer mediaViewer = (MediaViewer) node;
			mediaViewer.getImageView().setFitWidth(size);
			mediaViewer.getImageView().setFitHeight(size);
			mediaViewer.setMaxSize(size, size);
			mediaViewer.getNameLabel().setMaxSize(size, size);

			mediaViewer.setOnMouseClicked(m -> {

				if (m.getButton() == MouseButton.PRIMARY || m.getButton() == MouseButton.MIDDLE) {

					// If it isn't the same User again
					if (((MediaViewer) centerGroup.getChildren().get(0)) != mediaViewer) {

						// Update Center Index
						setCenterIndex(itemsObservableList.indexOf(mediaViewer));

						// Show Media Information
						// Main.mediaInformation.updateInformation(mediaViewer.getMedia())

						// Select the matching item on Playlist
						if (Main.settingsWindow.getPlayListsSettingsController().getSelectMatchingPlaylistItem()
								.isSelected())
							smartController.getItemsObservableList().stream()
									.filter(media -> media.getFilePath().equals(mediaViewer.getMedia().getFilePath()))
									.findFirst().ifPresent(media -> {

										// Check if selection is allowed
										if (Main.settingsWindow.getPlayListsSettingsController()
												.getSelectMatchingPlaylistItem().isSelected()) {

											// Select
											smartController.getNormalModeMediaTableViewer().getSelectionModel()
													.clearSelection();
											smartController.getNormalModeMediaTableViewer().getSelectionModel()
													.select(media);

										}

										// Check if scroll is allowed
										if (Main.settingsWindow.getPlayListsSettingsController()
												.getSelectMatchingPlaylistItem().isSelected()) {

											// ScrollTo
											smartController.getNormalModeMediaTableViewer().getTableView()
													.scrollTo(media);
										}
									});
					}
				} else if (m.getButton() == MouseButton.SECONDARY) {

					// If it isn't the same User again
					if (((MediaViewer) centerGroup.getChildren().get(0)) != mediaViewer) {

						// Update Center Index
						setCenterIndex(itemsObservableList.indexOf(mediaViewer));

						// Select the matching item on Playlist
						smartController.getItemsObservableList().stream()
								.filter(media -> media.getFilePath().equals(mediaViewer.getMedia().getFilePath()))
								.findFirst().ifPresent(media -> {
									smartController.getNormalModeMediaTableViewer().getSelectionModel()
											.clearSelection();
									smartController.getNormalModeMediaTableViewer().getSelectionModel().select(media);
								});

						// Set on timeline finished
						timeline.setOnFinished(finished -> {
							// Bounds
							Bounds bounds = mediaViewer.localToScreen(mediaViewer.getBoundsInLocal());

							// Show Context Menu
							Main.songsContextMenu.showContextMenu(mediaViewer.getMedia(), smartController.getGenre(),
									bounds.getMinX() + 25, bounds.getMinY() + bounds.getHeight() / 4, smartController,
									mediaViewer);
							timeline.setOnFinished(null);
						});
					} else {
						// Bounds
						Bounds bounds = mediaViewer.localToScreen(mediaViewer.getBoundsInLocal());

						// Show Context Menu
						Main.songsContextMenu.showContextMenu(mediaViewer.getMedia(), smartController.getGenre(),
								bounds.getMinX() + 25, bounds.getMinY() + bounds.getHeight() / 4, smartController,
								mediaViewer);
					}

				}
			});
		}

		// MAX
		if (scrollBar != null)
			scrollBar.setMax(itemsObservableList.size() - 1.00);

		// Update?
		if (update) {
			calculateCenterIndex();
			update();
		}

	}

	/**
	 * Deletes the specific Library from the list
	 * 
	 * @param item Item to be deleted
	 */
	public void deleteItem(Node item) {
		itemsObservableList.remove(item);

		// if (libraryMode != null)
		// for (int i = 0; i < itemsObservableList.size(); i++)
		// ( (Library) itemsObservableList.get(i) ).updatePosition(i);
		// else if (loginMode != null)
		// for (int i = 0; i < itemsObservableList.size(); i++)
		// ( (User) itemsObservableList.get(i) ).updatePosition(i);

		// Recalculate the center index after a delete occurs.
		calculateCenterIndex();
	}

	/**
	 * Delete all the items from that list that match items in the Viewer
	 * 
	 * @param items
	 */
	public void deleteItems(List<Node> items) {
		itemsObservableList.removeAll(items);

		// if (libraryMode != null)
		// for (int i = 0; i < itemsObservableList.size(); i++)
		// ( (Library) itemsObservableList.get(i) ).updatePosition(i);
		// else if (loginMode != null)
		// for (int i = 0; i < itemsObservableList.size(); i++)
		// ( (User) itemsObservableList.get(i) ).updatePosition(i);
		//
		// Recalculate the center index after a delete occurs.
		calculateCenterIndex();
	}

	/**
	 * Deletes all the items from the Viewer
	 */
	public void deleteAllItems(boolean update) {

		// Clear all the items
		itemsObservableList.clear();

		// Recalculate the center index after a delete occurs.
		if (update)
			calculateCenterIndex();
	}

	/**
	 * Recalculate the center index after a delete occurs.
	 */
	private void calculateCenterIndex() {

		// center index
		if (!centerGroup.getChildren().isEmpty())
			centerIndex = this.getNotNullList().indexOf(centerGroup.getChildren().get(0));

		else if (!leftGroup.getChildren().isEmpty())
			centerIndex = leftGroup.getChildren().size() - 1;
		else
			centerIndex = 0;

		// Max
		if (scrollBar != null)
			scrollBar.setMax(itemsObservableList.size() - 1.00);

		update();

	}

	/**
	 * Sets the center index.
	 *
	 * @param i the new center index
	 */
	public void setCenterIndex(int i) {
		if (centerIndex != i) {
			centerIndex = i;
			update();

			// Update the ScrollBar Value
			if (scrollBar != null)
				scrollBar.setValue(centerIndex);
		}
	}

	/**
	 * Set the center item
	 * 
	 * @param item
	 */
	public void setCenterItem(Node item) {
		if (item == null)
			return;
		setCenterIndex(getNotNullList().indexOf(item));
	}

	/**
	 * Checks if this item is the center item
	 * 
	 * @param item Node to be checked
	 * @return True if it is the center item.
	 */
	public boolean isCenterItem(Node item) {
		return !itemsObservableList.isEmpty() && getNotNullList().get(centerIndex).equals(item);
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
	 * Smart method to get the itemsObservableList when sortedList is null
	 * 
	 * @return
	 */
	private List<Node> getNotNullList() {
		return sortedList != null ? sortedList : itemsObservableList;
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
				centerGroup.getChildren().add(getNotNullList().get(0));
				centerIndex = 0;
			} else {

				// LEFT,
				for (int i = 0; i < centerIndex; i++)
					leftGroup.getChildren().add(getNotNullList().get(i));

				// CENTER,
				if (centerIndex == itemsObservableList.size()) {
					centerGroup.getChildren().add(leftGroup.getChildren().get(centerIndex - 1));
				} else
					centerGroup.getChildren().add(getNotNullList().get(centerIndex));

				// RIGHT
				for (int i = itemsObservableList.size() - 1; i > centerIndex; i--)
					rightGroup.getChildren().add(getNotNullList().get(i));

			}

			// duration = Duration.millis(150)

			// stop old time line
			if (timeline.getStatus() == Status.RUNNING)
				timeline.stop();

			// clear the old keyFrames
			timeline.getKeyFrames().clear();
			final ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();

			// LEFT KEYFRAMES
			for (int i = 0; i < leftGroup.getChildren().size(); i++) {

				final Node it = getNotNullList().get(i);

				double newX = -leftGroup.getChildren().size() *

						spacing + spacing * i + leftOffSet;

				keyFrames.add(new KeyFrame(duration,

						new KeyValue(it.translateXProperty(), newX, interpolator),

						new KeyValue(it.scaleXProperty(), SCALE_SMALL, interpolator),

						new KeyValue(it.scaleYProperty(), SCALE_SMALL, interpolator)));

				// new KeyValue(it.angle, 45.0, INTERPOLATOR)))

			}

			// CENTER ITEM KEYFRAME
			final Node centerItem;
			if (itemsObservableList.size() == 1)
				centerItem = getNotNullList().get(0);
			else
				centerItem = centerGroup.getChildren().get(0);

			// The Property Center Item
			this.centerItemProperty.set(centerItem);

			keyFrames.add(new KeyFrame(duration,

					new KeyValue(centerItem.translateXProperty(), 0, interpolator),

					new KeyValue(centerItem.scaleXProperty(), smartController != null ? SCALE_SMALL : 1.0,
							interpolator),

					new KeyValue(centerItem.scaleYProperty(), smartController != null ? SCALE_SMALL : 1.0,
							interpolator)));// ,

			// new KeyValue(centerItem.rotationTransform.angleProperty(),
			// 360)));

			// new KeyValue(centerItem.angle, 90, INTERPOLATOR)));

			// RIGHT KEYFRAMES
			for (int i = 0; i < rightGroup.getChildren().size(); i++) {

				final Node it = getNotNullList().get(itemsObservableList.size() - i - 1);

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
			// The Property Center Item
			this.centerItemProperty.set(null);

		if (libraryMode != null) {
			libraryMode.getNext().setDisable(rightGroup.getChildren().isEmpty());
			libraryMode.getPrevious().setDisable(leftGroup.getChildren().isEmpty());

		} else if (loginMode != null) {
			loginMode.getNext().setDisable(rightGroup.getChildren().isEmpty());
			loginMode.getPrevious().setDisable(leftGroup.getChildren().isEmpty());
		}

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
