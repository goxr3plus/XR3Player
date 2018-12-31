/*
 * 
 */
package main.java.com.goxr3plus.xr3player.controllers.chromium;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The Class AutoCompleteTextField.
 *
 * @author GOXR3PLUS
 */
/**
 * @author GOXR3PLUS
 *
 */
public final class AutoCompleteTextField {

	/** The existing auto complete entries. */
	private SortedSet<String> entries = new TreeSet<>();

	/** The pop up used to select an entry. */
	private final ContextMenu contextMenu = new ContextMenu();

	/** The maximum entries. */
	private int maximumEntries = 15;

	private final StringBuilder stringBuilder = new StringBuilder();

	/** The last length. */
	private int lastLength;

	private TextField textField;

	// -----------------------------------------------------------------------------------

	/**
	 * Text Listener
	 */
	private final InvalidationListener textListener = v -> {
		if (textField.getText().length() == 0 || entries.isEmpty())
			contextMenu.hide();
		else {
			populatePopup();
			if (!contextMenu.isShowing()) {
				contextMenu.show(textField, Side.BOTTOM, 0, 0);
				// Request focus on first item
				if (!contextMenu.getItems().isEmpty())
					contextMenu.getSkin().getNode().lookup(".menu-item:nth-child(1)").requestFocus();
			}
		}

	};

	/**
	 * Focus Listener
	 */
	private final InvalidationListener focusListener = v -> {
		lastLength = 0;
		stringBuilder.delete(0, stringBuilder.length());
		contextMenu.hide();
	};

	/**
	 * !!!!!!!!!!!!!!!!BUGGED !!!!!!!!!!!!!!!!! NEEDS FIXING KeyHandler
	 */
	private EventHandler<? super KeyEvent> keyHandler = key -> {
		KeyCode k = key.getCode();

		// This variable is used to bypass the auto complete process if the
		// length is the same. this occurs if user types fast, the length of
		// textfield will record after the user has typed after a certain
		// delay.
		if (lastLength != (textField.getLength() - textField.getSelectedText().length()))
			lastLength = textField.getLength() - textField.getSelectedText().length();

		boolean pass = true;
		System.out.println(k.getName());

		// Not causing problems by these buttons
		if (key.isControlDown() || k == KeyCode.BACK_SPACE || k == KeyCode.RIGHT || k == KeyCode.LEFT
				|| k == KeyCode.DELETE || k == KeyCode.HOME || k == KeyCode.END || k == KeyCode.TAB) {
			pass = false;
		}

		if (pass) {
			IndexRange indexRange = textField.getSelection();
			stringBuilder.delete(0, stringBuilder.length());
			stringBuilder.append(textField.getText());
			// remove selected string index until end so only unselected text
			// will be recorded
			try {
				stringBuilder.delete(indexRange.getStart(), stringBuilder.length());
			} catch (Exception e) {
				e.printStackTrace();
			}

			String originalLowered = textField.getText().toLowerCase();
			// Select the first Matching
			for (String entry : entries)
				if (entry.toLowerCase().startsWith(originalLowered)) {
					try {
						textField.setText(entry);
					} catch (Exception e) {
						textField.setText(stringBuilder.toString());
					}
					textField.positionCaret(stringBuilder.toString().length());
					textField.selectEnd();
					break;
				}

			System.out.println("Passed...");
		}
	};

	// --------------------------------------------------------------------------------------

	/**
	 * Add auto completion capabilities to the given textField
	 * 
	 * @param textField
	 * @param maximumEntries
	 * @param addKeyListener !NOT IMPLEMENTED!
	 * @param list           W
	 */
	public void bindAutoCompletion(TextField textField, int maximumEntries, boolean addKeyListener, List<String> list) {
		entries.addAll(list);
		bindAutoCompletion(textField, maximumEntries, addKeyListener);
	}

	/**
	 * <b> Warning with this method will lead to NullPointerException if the given
	 * SortedSet goes to null , i am using it when i have really big TreeSets on my
	 * Application</b> <br>
	 * 
	 * Add auto completion capabilities to the given textField
	 * 
	 * @param textField
	 * @param maximumEntries
	 * @param addKeyListener !NOT IMPLEMENTED!
	 * @param list
	 */
	public void bindAutoCompletion(TextField textField, int maximumEntries, boolean addKeyListener,
			SortedSet<String> sortedSet) {
		entries = sortedSet;
		bindAutoCompletion(textField, maximumEntries, addKeyListener);
	}

	/**
	 * Add auto completion capabilities to the given textField
	 * 
	 * @param textField
	 * @param maximumEntries
	 * @param addKeyListener !NOT IMPLEMENTED!
	 */
	public void bindAutoCompletion(TextField textField, int maximumEntries, boolean addKeyListener) {
		this.textField = textField;
		this.maximumEntries = maximumEntries <= 0 ? 10 : maximumEntries;

		// TextChanged Listener
		textField.textProperty().addListener(textListener);

		// FocusListener
		textField.focusedProperty().addListener(focusListener);

		// Add keyHandler?
		// if (addKeyListener)
		// textField.addEventHandler(KeyEvent.KEY_RELEASED, keyHandler);

		// if(addAutoLearner)
		// textField.addEventHandler(KeyEvent.KEY_RELEASED, keyHandler);

	}

	/**
	 * Remove autoCompletion listeners from the TextField
	 */
	public void removeAutoCompletion() {

		// TextChanged Listener
		textField.textProperty().removeListener(textListener);

		// FocusListener
		textField.focusedProperty().removeListener(focusListener);

		// Add keyHandler?
		textField.removeEventHandler(KeyEvent.KEY_RELEASED, keyHandler);
	}

	/**
	 * Get the existing set of auto complete entries.
	 * 
	 * @return The existing auto complete entries.
	 */
	public SortedSet<String> getEntries() {
		return entries;
	}

	/**
	 * @return the maximumEntries
	 */
	public int getMaximumEntries() {
		return maximumEntries;
	}

	/**
	 * @param maximumEntries the maximumEntries to set
	 */
	public void setMaximumEntries(int maximumEntries) {
		this.maximumEntries = maximumEntries;
	}

	/**
	 * Populate the entry set with the given search results.
	 */
	private void populatePopup() {
		contextMenu.getItems().clear();

		String text = textField.getText().toLowerCase();

		// Filter the first maximumEntries matching the text
		contextMenu.getItems()
				.addAll(entries.stream().filter(string -> string.toLowerCase().contains(text.toLowerCase()))
						.limit(maximumEntries).map(MenuItem::new).collect(Collectors.toList()));
		contextMenu.getItems().forEach(item -> item.setOnAction(a -> {
			textField.setText(item.getText());
			textField.positionCaret(textField.getLength());
		}));
	}
}
