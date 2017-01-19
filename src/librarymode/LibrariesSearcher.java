/*
 * 
 */
package librarymode;

import java.io.IOException;
import java.util.logging.Level;

import org.controlsfx.control.textfield.TextFields;

import application.Main;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import tools.InfoTool;

/**
 * Represents the Libraries Search.
 *
 * @author GOXR3PLUS
 */
public class LibrariesSearcher extends HBox {

    /** Libraries Search Window Controller */
    private LibrariesSearchWindow libSearchWinController = new LibrariesSearchWindow();

    /** The Search Service */
    private final SearchService service = new SearchService();

    /** The search field. */
    /** The SearchField */
    private final TextField searchField = TextFields.createClearableTextField();

    /** The region. */
    // Progress
    public final Region region = new Region();

    /** The search progress. */
    public final ProgressIndicator searchProgress = new ProgressIndicator();

    /**
     * Constructor.
     */
    public LibrariesSearcher() {

        getStyleClass().add("libraries-search-box");
        this.setMaxWidth(250);

        // Region
        region.setStyle("-fx-background-color:rgb(0,0,0,0.7)");

        // SearchProgress
        searchProgress.visibleProperty()
            .bind(service.runningProperty());
        searchProgress.progressProperty()
            .bind(service.progressProperty());
        region.visibleProperty()
            .bind(service.runningProperty());

        // SearchField
        searchField.setPrefWidth(300);
        searchField.setPromptText("Search...");
        searchField.textProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (!searchField.getText()
                    .isEmpty())
                    service.startService();
            });
        searchField.setOnAction(a -> service.startService());
        getChildren().add(searchField);

        // ------------------------LibrariesSearchWindow

        // Load the LibrariesSearchWindow FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "LibrariesSearchWindow.fxml"));
        loader.setController(libSearchWinController);
        BorderPane root = null;
        try {
            root = (BorderPane) loader.load();
        } catch (IOException ex) {
            Main.logger.log(Level.WARNING, "", ex);
        }

        // Initialize the Controller
        libSearchWinController.setTheScene(new Scene(root, Color.TRANSPARENT));

    }

    /**
     * This method registers some listeners to the main window so when main
     * windows changes his size or position then the Search Window recalculates
     * it's position.
     */
    public void registerListeners() {
        // Care so the Search Window is recalculating it's position
        Main.window.xProperty()
            .addListener((observable, oldValue, newValue) -> recalculateWindowPosition());
        Main.window.yProperty()
            .addListener((observable, oldValue, newValue) -> recalculateWindowPosition());
        Main.window.widthProperty()
            .addListener((observable, oldValue, newValue) -> recalculateWindowPosition());
        Main.window.heightProperty()
            .addListener((observable, oldValue, newValue) -> recalculateWindowPosition());
    }

    /**
     * Recalculate window position.
     */
    private void recalculateWindowPosition() {
        if (libSearchWinController.stage.isShowing()) {
            Bounds bounds = searchField.localToScreen(searchField.getBoundsInLocal());
            libSearchWinController.stage.setX(bounds.getMinX() - 10);
            libSearchWinController.stage.setY(bounds.getMaxY() + 10);
        }
    }

    /**
     * This button is added as a choice item into the LibrariesSearchWindow.
     *
     * @author GOXR3PLUS
     */
    class ResultButton extends Button {

        /**
         * Constructor.
         *
         * @param text the text
         * @param position the position
         */
        public ResultButton(String text, int position) {
            getStyleClass().add("library-search-box-item");
            setPrefSize(235, 30);
            setText(text);
            setOnAction(ac -> {
                Main.libraryMode.libraryViewer.setCenterIndex(position);
                Main.libraryMode.libraryViewer.scrollBar.setValue(position);
            });
        }

    }

    /**
     * This service is searching for Libraries with the given name.
     *
     * @author GOXR3PLUS
     */
    private class SearchService extends Service<Void> {

        /** The word. */
        String word;

        /** The found. */
        int found;

        /**
         * Constructor.
         */
        public SearchService() {
            setOnSucceeded(s -> done());
            setOnFailed(s -> done());
            setOnCancelled(c -> done());
        }

        /**
         * Start the Service.
         */
        public void startService() {
            if (!isRunning()) {
                libSearchWinController.clearContainer();
                word = searchField.getText();
                reset();
                start();
            }
        }

        /**
         * When the SearchService is done.
         */
        private void done() {
            // Change Label text
            libSearchWinController.setLabelText("Found " + found + " Libraries");

            // Show the Window
            libSearchWinController.stage.show();
            recalculateWindowPosition();
            Main.window.requestFocus();
            searchField.requestFocus();
        }

        /* (non-Javadoc)
         * @see javafx.concurrent.Service#createTask() */
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Variables
                    found = 0;
                    word = word.toLowerCase();

                    // matcher
                    Main.libraryMode.libraryViewer.items.stream()
                        .forEach(lib -> {
                            // match?
                            if (lib.getLibraryName()
                                .toLowerCase()
                                .contains(word)) {
                                Platform.runLater(() -> libSearchWinController.addChildren(new ResultButton(lib.getLibraryName(), lib.getPosition())));
                                ++found;
                            }
                        });
                    return null;
                }

            };
        }

    }
}
