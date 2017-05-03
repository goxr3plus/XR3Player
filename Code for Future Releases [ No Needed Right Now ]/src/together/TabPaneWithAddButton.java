package together;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TabPaneWithAddButton extends Application {

  @Override
  public void start(Stage primaryStage) {
    final AnchorPane root = new AnchorPane();
    final TabPane tabPane = new TabPane();
    tabPane.setId("magicTabPane");
    final Button addButton = new Button("+");

    AnchorPane.setTopAnchor(tabPane, 5.0);
    AnchorPane.setLeftAnchor(tabPane, 5.0);
    AnchorPane.setRightAnchor(tabPane, 5.0);
    AnchorPane.setTopAnchor(addButton, 6.0);
    AnchorPane.setLeftAnchor(addButton, 6.0);

    addButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        final Tab tab = new Tab("Tab " + (tabPane.getTabs().size() + 1));
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
      }
    });

    root.getChildren().addAll(tabPane, addButton);

    final Scene scene = new Scene(root, 600, 400);
    scene.getStylesheets().add(getClass().getResource("tab.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();

  }

  public static void main(String[] args) {
    launch(args);
  }
}