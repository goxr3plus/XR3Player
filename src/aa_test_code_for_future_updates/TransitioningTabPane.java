package aa_test_code_for_future_updates;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TransitioningTabPane extends Application {

    @Override
    public void start(Stage primaryStage) {
	TabPane tabPane = new TabPane();
	Tab tab1 = new Tab("Tab 1");
	Tab tab2 = new Tab("Tab 2");
	Tab tab3 = new Tab("Tab 1");
	Tab tab4 = new Tab("Tab 2");
	Tab tab5 = new Tab("Tab 1");
	Tab tab6 = new Tab("Tab 2");
	tabPane.getTabs().addAll(tab1, tab2, tab3, tab4, tab5, tab6);

	Map<Tab, Node> tabContent = new HashMap<>();
	tabContent.put(tab1, createTab1Content());
	tabContent.put(tab2, createTab2Content());
	tabContent.put(tab3, createTab1Content());
	tabContent.put(tab4, createTab2Content());
	tabContent.put(tab5, createTab1Content());
	tabContent.put(tab6, createTab2Content());

	// Initial state:

	tab1.setContent(tabContent.get(tab1));
	tab2.setContent(tabContent.get(tab2));
	tab3.setContent(tabContent.get(tab3));
	tab4.setContent(tabContent.get(tab4));
	tab5.setContent(tabContent.get(tab5));
	tab6.setContent(tabContent.get(tab6));
	tabPane.getSelectionModel().select(tab1);

	// State change manager:

	tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
	   // oldTab.setContent(null);
	    Node oldContent = tabContent.get(oldTab);
	    Node newContent = tabContent.get(newTab);

	    newTab.setContent(oldContent);
	    ScaleTransition fadeOut = new ScaleTransition(Duration.seconds(0.1), oldContent);
	    fadeOut.setFromX(1);
	    fadeOut.setFromY(1);
	    fadeOut.setToX(0);
	    fadeOut.setToY(0);

	    ScaleTransition fadeIn = new ScaleTransition(Duration.seconds(0.2), newContent);
	    fadeIn.setFromX(0);
	    fadeIn.setFromY(0);
	    fadeIn.setToX(1);
	    fadeIn.setToY(1);

	    fadeOut.setOnFinished(event -> newTab.setContent(newContent));

	    SequentialTransition crossFade = new SequentialTransition(fadeOut, fadeIn);
	    crossFade.play();
	});

	BorderPane root = new BorderPane(tabPane);
	Scene scene = new Scene(root, 600, 600);
	primaryStage.setScene(scene);
	primaryStage.show();

    }

    private Node createTab1Content() {
	Pane pane = new Pane();
	Rectangle rect1 = new Rectangle(50, 50, 250, 250);
	rect1.setFill(Color.SALMON);
	Rectangle rect2 = new Rectangle(150, 150, 250, 250);
	rect2.setFill(Color.CORNFLOWERBLUE.deriveColor(0, 1, 1, 0.5));
	Text text = new Text(225, 225, "This is tab 1");
	pane.getChildren().addAll(rect1, rect2, text);
	pane.setMinSize(500, 500);
	return pane;
    }

    private Node createTab2Content() {
	Pane pane = new Pane();
	Rectangle rect1 = new Rectangle(250, 50, 250, 250);
	rect1.setFill(Color.CORNFLOWERBLUE);
	Rectangle rect2 = new Rectangle(50, 150, 250, 250);
	rect2.setFill(Color.SALMON.deriveColor(0, 1, 1, 0.5));
	Text text = new Text(225, 225, "This is tab 2");
	pane.getChildren().addAll(rect1, rect2, text);
	pane.setMinSize(500, 500);
	return pane;
    }

    public static void main(String[] args) {
	launch(args);
    }
}