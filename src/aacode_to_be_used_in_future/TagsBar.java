/*
 * 
 */
package aacode_to_be_used_in_future;

import java.util.SortedSet;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import tools.InfoTool;

// TODO: Auto-generated Javadoc
/**
 * The Class TagsBar.
 */
public class TagsBar extends HBox {

	/** The h box. */
	private HBox hBox = new HBox();
	
	/** The scroll pane. */
	private ScrollPane scrollPane = new ScrollPane(hBox);
	
	/** The field. */
	private AutoCompleteTextField field = new AutoCompleteTextField();

	/**
	 * Instantiates a new tags bar.
	 */
	// Constructor
	public TagsBar() {

		getStyleClass().setAll("tags-bar");

		// hBox
		hBox.setStyle(" -fx-spacing:3px;");

		// scrollPane
		scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);

		// field
		field.setPromptText("tag...");
		field.setMinSize(120, 30);
		field.setBackground(null);
		field.setOnAction(evt -> {
			String text = field.getText();
			// No Duplicates allowed
			if (!text.isEmpty() && getEntries().contains(text) && !hBox.getChildren().stream()
					.anyMatch(s -> ((Tag) s).getTag().toLowerCase().equals(text.toLowerCase())))
				hBox.getChildren().add(new Tag(text));
			field.clear();
		});

		getChildren().addAll(scrollPane, field);
	}

	/**
	 * Returns all the tags of TagsBar.
	 *
	 * @return the tags
	 */
	public ObservableList<Node> getTags() {
		return hBox.getChildren();
	}

	/**
	 * Get the existing set of auto complete entries.
	 * 
	 * @return The existing auto complete entries.
	 */
	public SortedSet<String> getEntries() {
		return field.getEntries();
	}

	/**
	 * Clears all the tags.
	 */
	public void clearAllTags() {
		hBox.getChildren().clear();
	}

	/**
	 * Add this tag if it doesn't exist.
	 *
	 * @param tag the tag
	 */
	public void addTag(String tag) {
		if (!hBox.getChildren().stream().anyMatch(s -> ((Tag) s).getTag().toLowerCase().equals(tag.toLowerCase())))
			hBox.getChildren().add(new Tag(tag));
	}

	/**
	 * The Class Tag.
	 *
	 * @author SuperGoliath TagClass
	 */
	public class Tag extends HBox {

		/** The text label. */
		private Label textLabel = new Label();
		
		/** The icon label. */
		private Label iconLabel = new Label(null, InfoTool.getImageViewFromDocuments("x.png"));

		/**
		 * Instantiates a new tag.
		 *
		 * @param tag the tag
		 */
		// Constructor
		public Tag(String tag) {
			getStyleClass().add("tag");

			// drag detected
			setOnDragDetected(event -> {

				/* allow copy transfer mode */
				Dragboard db = startDragAndDrop(TransferMode.MOVE);

				/* put a string on dragboard */
				ClipboardContent content = new ClipboardContent();
				content.putString("#c" + getTag());

				db.setDragView(snapshot(null, new WritableImage((int) getWidth(), (int) getHeight())), getWidth() / 2,
						0);

				db.setContent(content);

				event.consume();
			});

			// drag over
			setOnDragOver((event) -> {
				/*
				 * data is dragged over the target accept it only if it is not
				 * dragged from the same imageView and if it has a string data
				 */
				if (event.getGestureSource() != this && event.getDragboard().hasString())
					event.acceptTransferModes(TransferMode.MOVE);

				event.consume();
			});

			// drag dropped
			setOnDragDropped(event -> {

				boolean sucess = false;
				if (event.getDragboard().hasString() && event.getDragboard().getString().startsWith("#c")) {
					String currentTag = getTag();
					setTag(event.getDragboard().getString().replace("#c", ""));
					((Tag) event.getGestureSource()).setTag(currentTag);
					sucess = true;
				}

				event.setDropCompleted(sucess);
				event.consume();
			});

			// drag done
			setOnDragDone(event -> {
				if (event.getTransferMode() == TransferMode.MOVE) {
					// System.out.println("Source"+event.getGestureSource() + "
					// /Target:" + event.getGestureTarget());
				}

				event.consume();
			});

			// textLabel
			textLabel.getStyleClass().add("label");
			textLabel.setText(tag);
			// textLabel.setMinWidth(getTag().length() * 6);

			// iconLabel
			iconLabel.setOnMouseReleased(r -> {
				hBox.getChildren().remove(this);
			});

			getChildren().addAll(textLabel, iconLabel);
		}

		/**
		 * Gets the tag.
		 *
		 * @return the tag
		 */
		public String getTag() {
			return textLabel.getText();
		}

		/**
		 * Sets the tag.
		 *
		 * @param text the new tag
		 */
		public void setTag(String text) {
			textLabel.setText(text);
		}

	}

}