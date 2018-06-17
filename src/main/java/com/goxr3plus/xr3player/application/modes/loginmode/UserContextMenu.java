package main.java.com.goxr3plus.xr3player.application.modes.loginmode;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.presenter.TitleMenuItem;

/**
 * This is the Context Menu for every Library in the LibraryMode
 * 
 * @author GOXR3PLUS
 *
 */
public class UserContextMenu extends ContextMenu {
	
	/** The Login. */
	MenuItem login = new MenuItem("Login");
	
	/** The rename. */
	MenuItem rename = new MenuItem("Rename(CTRL + R)");
	
	/** The delete. */
	MenuItem delete = new MenuItem("Delete(CTRL + D)");
	
	/** The image. */
	Menu image = new Menu("Image");
	
	/** The set image. */
	Menu setImage = new Menu("change...");
	
	/** The local image. */
	MenuItem localImage = new MenuItem("local");
	
	/** The internet image. */
	MenuItem internetImage = new MenuItem("internet");
	
	/** The export image. */
	MenuItem exportImage = new MenuItem("export...(CTRL + E)");
	
	/** The reset image. */
	MenuItem resetImage = new MenuItem("default");
	
	/** The library. */
	private User user;
	
	/**
	 * Instantiates a new library context menu.
	 * 
	 * @param loginMode
	 */
	// Constructor
	public UserContextMenu(LoginMode loginMode) {
		login.setOnAction(a -> Main.startAppWithUser(user));
		
		rename.setOnAction(ac -> user.renameUser(user));
		
		localImage.setOnAction(ac -> user.changeUserImage());
		
		resetImage.setOnAction(ac -> user.setDefaultImage());
		
		exportImage.setOnAction(a -> user.exportImage());
		
		delete.setOnAction(ac -> loginMode.teamViewer.getSelectedItem().deleteUser(user));
		
		internetImage.setDisable(true);
		// exportImage.setDisable(true)
		
		setImage.getItems().addAll(localImage, internetImage);
		image.getItems().addAll(setImage, exportImage, resetImage);
		
		getItems().addAll(new TitleMenuItem("Common"), login, rename, new TitleMenuItem("Other"), image, delete);
		
	}
	
	/**
	 * Shows the LibraryContextMenu.
	 *
	 * @param window
	 *            the window
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param user
	 *            the user
	 */
	public void show(Window window , double x , double y , User user) {
		this.user = user;
		
		// customize the menu accordingly
		exportImage.setDisable(user.getImageView().getImage() == null);
		resetImage.setDisable(exportImage.isDisable());
		
		//Fix first time show problem
		if (super.getWidth() == 0) {
			show(Main.window);
			hide();
		}
		
		// Show it
		show(window, x - 15 - super.getWidth() + super.getWidth() * 14 / 100, y - 1);
		
		//Y axis
		double yIni = y - 50;
		double yEnd = super.getY();
		super.setY(yIni);
		final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
		yProperty.addListener((ob , n , n1) -> super.setY(n1.doubleValue()));
		
		//X axis
		//	double xIni = screenX - super.getWidth() + super.getWidth() * 14 / 100 + 30;
		//	double xEnd = screenX - super.getWidth() + super.getWidth() * 14 / 100;
		//	super.setX(xIni);
		//	final DoubleProperty xProperty = new SimpleDoubleProperty(xIni);
		//	xProperty.addListener((ob, n, n1) -> super.setY(n1.doubleValue()));
		
		//Timeline
		Timeline timeIn = new Timeline();
		timeIn.getKeyFrames().addAll(new KeyFrame(Duration.seconds(0.35), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
		//new KeyFrame(Duration.seconds(0.5), new KeyValue(xProperty, xEnd, Interpolator.EASE_BOTH)))
		timeIn.play();
		
	}
	
}
