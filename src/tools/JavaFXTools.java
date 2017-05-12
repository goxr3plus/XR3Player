/**
 * 
 */
package tools;

import javafx.scene.control.ToggleGroup;

/**
 * This class has some functions that are not there by default in JavaFX 8
 * 
 * @author GOXR3PLUS
 *
 */
public class JavaFXTools {

    private JavaFXTools() {
    }

    /**
     * Returns the Index of the Selected Toggle inside the ToggleGroup (counting from 0)
     * 
     * @param g
     * @return The index of the Selected Toggle
     */
    public static int getIndexOfSelectedToggle(ToggleGroup g) {
	return g.getToggles().indexOf(g.getSelectedToggle());
    }

    /**
     * Selects the Toggle in position Index inside the toggle group (counting from 0 )
     * 
     * @param g
     * @param index
     */
    public static void selectToggleOnIndex(ToggleGroup g, int index) {
	g.selectToggle(g.getToggles().get(index));
    }

}
