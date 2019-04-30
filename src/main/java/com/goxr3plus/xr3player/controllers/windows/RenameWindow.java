/*
 *
 */
package com.goxr3plus.xr3player.controllers.windows;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.controlsfx.control.textfield.TextFields;

import com.jfoenix.controls.JFXButton;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.goxr3plus.xr3player.enums.FileCategory;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

/**
 * The Class RenameWindow.
 */
public class RenameWindow extends VBox {

    @FXML
    private Label titleLabel;

    @FXML
    private Label charsField;

    @FXML
    private JFXButton okButton;

    @FXML
    private JFXButton closeButton;

    // ----------------

    /**
     * The field inside the user writes the text
     */
    private final TextField inputField = TextFields.createClearableTextField();

    // Custom Event Handler
    private final EventHandler<ActionEvent> myHandler = e -> {

        // can pass?
        if (!inputField.getText().trim().isEmpty())
            close(true);
        else
            AlertTool.showNotification("Message", "You have to type something..", Duration.millis(1500),
                    NotificationType.WARNING);

    };

    /**
     * The window
     */
    private final Stage window = new Stage();

    /**
     * If it was accepted
     */
    private boolean accepted = false;

    /**
     * The not allow.
     */
    private Set<String> notAllow = Stream.of("/", "\\", ":", "*", "?", "\"", "<", ">", "|", "'", ".")
            .collect(Collectors.toSet());

    /**
     * The timeLine which controls the animations of the Window
     */
    private Timeline timeLine = new Timeline();

    /**
     * This variable defines the time in milliseconds that this windows requires to get closed
     */
    public int windowCloseTime = 150;

    /**
     * Constructor
     */
    public RenameWindow() {

        // Window
        window.setTitle("Rename Window");
        window.setMinHeight(100);
        window.setMinWidth(300);
        window.setWidth(440);
        window.setHeight(80);
        window.initStyle(StageStyle.TRANSPARENT);
        window.getIcons().add(InfoTool.getImageFromResourcesFolder("icon.png"));
        window.centerOnScreen();
        window.setOnCloseRequest(ev -> close(false));
        window.setAlwaysOnTop(true);

        // ----------------------------------FXMLLoader
        FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.WINDOW_FXMLS + "RenameWindow.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // ----------------------------------Scene
        window.setScene(new Scene(this, Color.TRANSPARENT));
        getScene().setOnKeyReleased(key -> {
            if (key.getCode() == KeyCode.ESCAPE)
                close(false);
        });
        window.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && window.isShowing())// && Main.starWindow.getTimeLine().getStatus() != Status.RUNNING &&
                // Main.emotionsWindow.getTimeLine().getStatus() != Status.RUNNING)
                close(false);
        });

    }

    /**
     * Called as soon as .fxml has been initialized
     */
    @FXML
    private void initialize() {

        // CharsField
        charsField.textProperty().bind(inputField.textProperty().length().asString());

        // inputField
        getChildren().add(inputField);
        inputField.setMinSize(420, 32);
        inputField.setPromptText("Type Here...");
        inputField.setTooltip(new Tooltip("Not allowed:(<) (>) (:) (\\\") (/) (\\\\) (|) (?) (*) (') (.)"));
        inputField.setStyle("-fx-font-weight:bold; -fx-font-size:14;");
        // inputField.setPrefColumnCount(200)
        // inputField.prefColumnCountProperty().bind(inputField.textProperty().length().add(1))
        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Check newValue
            if (newValue != null) {

                // Allow until 200 characters
                if (newValue.length() > 200)
                    inputField.setText(newValue.substring(0, 200));

                // Strict Mode
                for (String character : notAllow)
                    if (newValue.contains(character))
                        inputField.setText(newValue.replace(character, ""));
            }
        });
        // ---prefColumnCountProperty
        // inputField.prefColumnCountProperty().addListener((observable, oldValue,
        // newValue) -> {
        // if (inputField.getWidth() < 450)
        // window.setWidth(inputField.getWidth() + 50);
        // });
        inputField.setOnAction(myHandler);
        inputField.getStyleClass().add("dark-text-field");

        // okButton
        okButton.setOnAction(myHandler);

        // closeButton
        closeButton.setOnAction(action -> close(false));

        // window.show()
    }

    /**
     * get the input that connectedUser Typed.
     *
     * @return the user input
     */
    public String getUserInput() {
        return inputField.getText();
    }

    /**
     * Checks if it was cancelled
     *
     * @return True if it was cancelled , false if not
     */
    public boolean wasAccepted() {
        return accepted;
    }

    /**
     * Close the Window.
     *
     * @param accepted True if accepted , False if not
     */
    public void close(boolean accepted) {
        // System.out.println("Rename Window Close called with accepted := " + accepted)
        this.accepted = accepted;

        // ------------Animation------------------
        // Y axis
        double yIni = window.getY();
        double yEnd = window.getY() + 50;
        window.setY(yIni);

        // Create Double Property
        final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
        yProperty.addListener((ob, n, n1) -> window.setY(n1.doubleValue()));

        // Create Time Line
        timeLine.getKeyFrames()
                .setAll(new KeyFrame(Duration.millis(windowCloseTime), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
        timeLine.setOnFinished(f -> window.close());
        timeLine.playFromStart();
        // ------------ END of Animation------------------

    }

    /**
     * Show Window with the given parameters.
     *
     * @param text  the text
     * @param n     the node
     * @param title The text if the title Label
     */
    public void show(String text, Node n, String title, FileCategory fileCategory, boolean... exactPositioning) {

        // Stop the TimeLine
        timeLine.stop();
        window.close();

        // Auto Calculate the position
        Bounds bounds = n.localToScreen(n.getBoundsInLocal());
        // show(text, bounds.getMinX() + 5, bounds.getMaxY(), title)
        // System.out.println(bounds.getMinX() + " , " + getWidth() + " , " +
        // bounds.getWidth() / 2)
        show(text, exactPositioning.length == 0 ? bounds.getMinX() - 440 / 2 + bounds.getWidth() / 2
                : bounds.getMinX() + 60, bounds.getMaxY(), title);

        // System.out.println(bounds.getMinX() + " , " + getWidth() + " , " +
        // bounds.getWidth() / 2)
        if (!notAllow.contains(".") && fileCategory == FileCategory.DIRECTORY) {
            inputField.getTooltip().setText("Not allowed:(<) (>) (:) (\") (/) (\\) (|) (?) (*) (') (.)");
            notAllow.add(".");
            notAllow.add("'");
        } else if (fileCategory == FileCategory.FILE) {
            inputField.getTooltip().setText("Not allowed:(<) (>) (:) (\") (/) (\\) (|) (?) (*)");
            notAllow.remove(".");
            notAllow.remove("'");
        }

    }

    /**
     * Show Window with the given parameters.
     *
     * @param text  the text
     * @param x     the x
     * @param y     the y
     * @param title The text if the title Label
     */
    private void show(String text, double x, double y, String title) {

        titleLabel.setText(title);
        inputField.setText(text);
        accepted = true;

        // Set once
        window.setX(x);
        window.setY(y);

        window.show();

        // Set it again -- NEEDS FIXING
        if (x <= -1 && y <= -1)
            window.centerOnScreen();
        else {
            if (x + getWidth() > JavaFXTool.getScreenWidth())
                x = JavaFXTool.getScreenWidth() - getWidth();
            else if (x < 0)
                x = 0;

            if (y + getHeight() > JavaFXTool.getScreenHeight())
                y = JavaFXTool.getScreenHeight() - getHeight();
            else if (y < 0)
                y = 0;

            window.setX(x);
            window.setY(y);

            // ------------Animation------------------
            // Y axis
            double yIni = y + 50;
            double yEnd = y;
            window.setY(yIni);

            // Create Double Property
            final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
            yProperty.addListener((ob, n, n1) -> window.setY(n1.doubleValue()));

            // Create Time Line
            Timeline timeIn = new Timeline(
                    new KeyFrame(Duration.seconds(0.15), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
            timeIn.play();
            // ------------ END of Animation------------------
        }

        //
        inputField.requestFocus();
        inputField.end();
    }

    /**
     * @return Whether or not this {@code Stage} is showing (that is, open on the
     * user's system). The Stage might be "showing", yet the user might not
     * be able to see it due to the Stage being rendered behind another
     * window or due to the Stage being positioned off the monitor.
     * @defaultValue false
     */
    public ReadOnlyBooleanProperty showingProperty() {
        return window.showingProperty();
    }

    /**
     * @return Whether or not this {@code Stage} is showing (that is, open on the
     * user's system). The Stage might be "showing", yet the user might not
     * be able to see it due to the Stage being rendered behind another
     * window or due to the Stage being positioned off the monitor.
     */
    public boolean isShowing() {
        return showingProperty().get();
    }

    /**
     * @return the window
     */
    public Stage getWindow() {
        return window;
    }

    /**
     * @return the inputField
     */
    public TextField getInputField() {
        return inputField;
    }

    /**
     * @return the timeLine
     */
    public Timeline getTimeLine() {
        return timeLine;
    }

}
