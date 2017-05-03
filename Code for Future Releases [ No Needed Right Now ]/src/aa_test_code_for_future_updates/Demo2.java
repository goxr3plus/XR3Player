package aa_test_code_for_future_updates;

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

import eu.hansolo.enzo.common.SymbolType;
import eu.hansolo.enzo.radialmenu.RadialMenu;
import eu.hansolo.enzo.radialmenu.RadialMenuBuilder;
import eu.hansolo.enzo.radialmenu.RadialMenuItem;
import eu.hansolo.enzo.radialmenu.RadialMenuItemBuilder;
import eu.hansolo.enzo.radialmenu.RadialMenuOptionsBuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA. User: hansolo Date: 21.09.12 Time: 16:18 To change this template use File | Settings | File Templates.
 */
public class Demo2 extends Application {

    RadialMenuItem play = RadialMenuItemBuilder.create().symbol(SymbolType.REWIND).tooltip("Play").size(40)
	    .build();

    @Override
    public void start(Stage stage) {
	final RadialMenu radialMenu = RadialMenuBuilder.create()
		.options(RadialMenuOptionsBuilder.create().degrees(180).offset(-135).radius(60).buttonSize(40)
			.buttonFillColor(Color.BLUE).buttonForegroundColor(Color.BLUE).buttonStrokeColor(Color.BLUE)
			.tooltipsEnabled(true).buttonHideOnSelect(false).buttonHideOnClose(false).buttonAlpha(1.0)
			.build())
		.items(
			//RadialMenuItemBuilder.create().thumbnailImageName(getClass().getResource("star.png").toExternalForm()).size(40).build(),
			RadialMenuItemBuilder.create().symbol(SymbolType.STOP).tooltip("Stop").size(40).build(),
			play,
			RadialMenuItemBuilder.create().symbol(SymbolType.FORWARD).tooltip("Forward").size(40).build(),
			RadialMenuItemBuilder.create().symbol(SymbolType.REFRESH).tooltip("Replay").size(40).build(),
			RadialMenuItemBuilder.create().symbol(SymbolType.SEARCH).tooltip("Search").size(40).build(),
			RadialMenuItemBuilder.create().selectable(true).selected(true).symbol(SymbolType.VOLUME)
				.tooltip("Mute").size(40).build()
		//RadialMenuItemBuilder.create().thumbnailImageName(getClass().getResource(InfoTool.images+"backward.png").toExternalForm()).tooltip("BackWard").size(40).build()
		).build();

	//radialMenu.hide();
	play.setRotate(-90);
	//playOrPause.setDisable(true)
	radialMenu.setStyle("-fx-background-color:cyan");
	radialMenu.setPrefSize(300, 300);
	radialMenu.setCursor(Cursor.HAND);

	radialMenu.setOnItemSelected(
		selectionEvent -> System.out.println("item " + selectionEvent.item.getTooltip() + " selected"));
	radialMenu.setOnItemDeselected(
		selectionEvent -> System.out.println("item " + selectionEvent.item.getTooltip() + " deselected"));
	radialMenu.setOnItemClicked(
		clickEvent -> System.out.println("item " + clickEvent.item.getTooltip() + " clicked"));
	radialMenu.setOnMenuOpenStarted(menuEvent -> System.out.println("Menu starts to open"));
	radialMenu.setOnMenuOpenFinished(menuEvent -> System.out.println("Menu finished to open"));
	radialMenu.setOnMenuCloseStarted(menuEvent -> System.out.println("Menu starts to close"));
	radialMenu.setOnMenuCloseFinished(menuEvent -> System.out.println("Menu finished to close"));

	radialMenu.getItems()
		.add(RadialMenuItemBuilder.create().symbol(SymbolType.PAUSE).tooltip("Pause").size(40).build());

	HBox buttons = new HBox();
	buttons.setSpacing(10);
	buttons.setPadding(new Insets(10, 10, 10, 10));
	Button buttonShow = new Button("Show menu");
	buttonShow.setOnAction(actionEvent -> radialMenu.show());
	buttons.getChildren().add(buttonShow);

	Button buttonHide = new Button("Hide menu");
	buttonHide.setOnAction(actionEvent -> radialMenu.hide());
	buttons.getChildren().add(buttonHide);

	VBox pane = new VBox();
	pane.getChildren().add(radialMenu);
	pane.getChildren().add(buttons);
	pane.setBackground(new Background(new BackgroundFill(Color.BISQUE, CornerRadii.EMPTY, Insets.EMPTY)));

	Scene scene = new Scene(pane);

	stage.setScene(scene);
	stage.show();
    }

    public static void main(String[] args) {
	launch(args);
    }
}
