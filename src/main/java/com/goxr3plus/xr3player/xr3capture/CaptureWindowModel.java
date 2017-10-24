/**
 * 
 */
package main.java.com.goxr3plus.xr3player.xr3capture;

import java.util.Random;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;

/**
 * @author GOXR3PLUS
 *
 */
public class CaptureWindowModel {
	
	/** The random. */
	Random random = new Random();
	
	/** The x pressed. */
	int mouseXPressed = 0;
	
	/** The y pressed. */
	int mouseYPressed = 0;
	
	/** The x now. */
	int mouseXNow = 0;
	
	/** The y now. */
	int mouseYNow = 0;
	
	/** The upper left X. */
	int rectUpperLeftX = 0;
	
	/** The upper left Y. */
	int rectUpperLeftY = 0;
	
	/** The rectangle width. */
	int rectWidth;
	
	/** The rectangle height. */
	int rectHeight;
	
	// ----------------
	
	/** The background. */
	Color background = Color.rgb(0, 0, 0, 0.3);
	
	/** The font. */
	Font font = Font.font("", FontWeight.BOLD, 14);
	
	// ---------------
	
	/** The shift pressed. */
	BooleanProperty shiftPressed = new SimpleBooleanProperty();
	
	/** The up pressed. */
	BooleanProperty upPressed = new SimpleBooleanProperty();
	
	/** The right pressed. */
	BooleanProperty rightPressed = new SimpleBooleanProperty();
	
	/** The down pressed. */
	BooleanProperty downPressed = new SimpleBooleanProperty();
	
	/** The left pressed. */
	BooleanProperty leftPressed = new SimpleBooleanProperty();
	
	/** The any pressed. */
	BooleanBinding anyPressed = upPressed.or(downPressed).or(leftPressed).or(rightPressed);
	
	/** The hide extra features. */
	BooleanProperty hideExtraFeatures = new SimpleBooleanProperty();
	
	// ------------
	
	/** The screen width. */
	int screenWidth = (int) Screen.getPrimary().getBounds().getWidth();
	
	/** The screen height. */
	int screenHeight = (int) Screen.getPrimary().getBounds().getHeight();
	
}
