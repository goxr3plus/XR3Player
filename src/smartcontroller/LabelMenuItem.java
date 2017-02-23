/**
 * 
 */
package smartcontroller;

import javafx.scene.control.MenuItem;

/**
 * @author GOXR3PLUS
 *
 */
public class LabelMenuItem extends MenuItem {

    /**
     * Constructor
     * 
     * @param text
     *            The Text of The Menu Item
     * 
     */
    public LabelMenuItem(String text) {
	setText(text);
	setDisable(true);
	getStyleClass().clear();
	getStyleClass().add("label-menu-item");
    }

}
