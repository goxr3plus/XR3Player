/**
 * 
 */
package application.users;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import tools.InfoTool;

/**
 * @author GOXR3PLUS
 *
 */
public class UserMode extends BorderPane {

    // ----------------------

    @FXML
    private PieChart pieChart;

    @FXML
    private Label userNameLabel;

    @FXML
    private ImageView userImageView;

    // ----------------------

    /** The logger. */
    private Logger logger = Logger.getLogger(getClass().getName());

    User user;

    /**
     * Constructor.
     */
    public UserMode() {

	// ------------------------------------FXMLLOADER ----------------------------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "UserMode.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    logger.log(Level.WARNING, "", ex);
	}
    }

    /**
     * Called as soon as .fxml is initialized
     */
    @FXML
    private void initialize() {

	pieChart.setData(FXCollections.observableArrayList(new PieChart.Data("Grapefruit", 13), new PieChart.Data("Oranges", 25),
		new PieChart.Data("Plums", 10), new PieChart.Data("Pears", 22), new PieChart.Data("Apples", 30)));
    }

    /**
     * This method should be called after fxml has been initialized for this controller
     * 
     * @param user
     */
    public void setUser(User user) {
	this.user = user;

	//-----UserNameLabel
	userNameLabel.textProperty().bind(Bindings.concat("Logged in as->[ ").concat(user.getNameField().textProperty()).concat(" ]"));
    }

}
