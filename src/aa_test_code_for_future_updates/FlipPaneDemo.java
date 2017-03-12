/*
 * Copyright (c) 2015 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package aa_test_code_for_future_updates;

import eu.hansolo.enzo.flippanel.FlipEvent;
import eu.hansolo.enzo.flippanel.FlipPanel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.PerspectiveCamera;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 30.04.14
 * Time: 07:55
 */
public class FlipPaneDemo extends Application {
    private FlipPanel flipPanel;
    private StackPane frontPanel;


    @Override public void init() {
        frontPanel = new StackPane();
        frontPanel.setBackground(new Background(new BackgroundFill(Color.AQUAMARINE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        flipPanel = new FlipPanel(Orientation.VERTICAL);
        flipPanel.getFront().getChildren().add(initFront(flipPanel, frontPanel));
        flipPanel.getBack().getChildren().add(initBack(flipPanel, frontPanel));

        flipPanel.addEventHandler(FlipEvent.FLIP_TO_FRONT_FINISHED, event -> System.out.println("Flip to front finished"));
        flipPanel.addEventHandler(FlipEvent.FLIP_TO_BACK_FINISHED, event -> System.out.println("Flip to back finished"));
    }

    private Pane initFront(final FlipPanel FLIP_PANEL, final StackPane FRONT_PANEL) {                
        Region settingsButton = new Region();
        settingsButton.getStyleClass().add("settings-button");
        settingsButton.addEventHandler(MouseEvent.MOUSE_CLICKED, EVENT -> FLIP_PANEL.flipToBack());

        VBox componentsFront = new VBox(settingsButton, FRONT_PANEL);
        componentsFront.setSpacing(10);
        VBox.setVgrow(FRONT_PANEL, Priority.ALWAYS);

        StackPane front = new StackPane();
        front.setPadding(new Insets(20, 20, 20, 20));
        front.getStyleClass().add("panel");
        front.getChildren().addAll(componentsFront);
        return front;
    }
    private Pane initBack(final FlipPanel flipPanel, final StackPane FRONT_PANEL) {
        Region backButton = new Region();
        backButton.getStyleClass().add("back-button");
        backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, EVENT -> flipPanel.flipToFront());

        ToggleGroup group = new ToggleGroup();

        final RadioButton standardGreen = new RadioButton("Green");
        standardGreen.setToggleGroup(group);
        standardGreen.setSelected(true);
        standardGreen.setOnAction(event -> FRONT_PANEL.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY))));

        final RadioButton amber         = new RadioButton("Red");
        amber.setToggleGroup(group);
        amber.setOnAction(event -> FRONT_PANEL.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY))));

        final RadioButton blueDarkBlue  = new RadioButton("Blue");
        blueDarkBlue.setToggleGroup(group);
        blueDarkBlue.setOnAction(event -> FRONT_PANEL.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY))));

        VBox componentsBack = new VBox(backButton, standardGreen, amber, blueDarkBlue);
        componentsBack.setSpacing(10);

        StackPane back = new StackPane();
        back.setPadding(new Insets(20, 20, 20, 20));
        back.getStyleClass().add("panel");
        back.getChildren().addAll(componentsBack);
        return back;
    }

    @Override public void start(Stage stage) {        
        BorderPane pane = new BorderPane(flipPanel);
        pane.setPrefSize(400, 250);
        pane.setPadding(new Insets(50, 50, 50, 50));
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(68, 68, 68), CornerRadii.EMPTY, Insets.EMPTY)));

        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.setFieldOfView(20);

        Scene scene = new Scene(pane);
        scene.setCamera(camera);
        //scene.getStylesheets().add(Demo.class.getResource("styles.css").toExternalForm());

        stage.setTitle("FlipPanel Demo");
        stage.setScene(scene);
        stage.show();
    }
    
    @Override public void stop() {}

    public static void main(String[] args) {
        launch(args);
    }
}
