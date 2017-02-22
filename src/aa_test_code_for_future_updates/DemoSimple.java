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
import eu.hansolo.enzo.radialmenu.RadialMenuItemBuilder;
import eu.hansolo.enzo.radialmenu.RadialMenuOptionsBuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA. User: hansolo Date: 27.09.13 Time: 07:40 To change this template use File | Settings | File Templates.
 */
public class DemoSimple extends Application {

    @Override
    public void start(Stage stage) {
	final RadialMenu radialMenu = RadialMenuBuilder.create().options(RadialMenuOptionsBuilder.create().degrees(360)
		.offset(-90).radius(50).buttonSize(72).buttonHideOnSelect(true).buttonHideOnClose(false)
		.buttonAlpha(1.0).simpleMode(true).strokeVisible(false).build()).items(//RadialMenuItemBuilder.create().thumbnailImageName(getClass().getResource("star.png").toExternalForm()).size(48).build(),
			RadialMenuItemBuilder.create().symbol(SymbolType.LOCATION).tooltip("Location").size(48).build(),
			RadialMenuItemBuilder.create().selectable(true).symbol(SymbolType.MUSIC).tooltip("Music")
				.size(48).build(),
			RadialMenuItemBuilder.create().symbol(SymbolType.SPEECH_BUBBLE).tooltip("Chat").size(48)
				.build(),
			RadialMenuItemBuilder.create().symbol(SymbolType.BLUE_TOOTH).tooltip("Bluetooth").size(48)
				.build(),
			RadialMenuItemBuilder.create().symbol(SymbolType.BULB).tooltip("Ideas").size(48).build(),
			RadialMenuItemBuilder.create().symbol(SymbolType.HEAD_PHONES).tooltip("Sound").size(48).build(),
			RadialMenuItemBuilder.create().symbol(SymbolType.TWITTER).tooltip("Twitter").size(48).build(),
			RadialMenuItemBuilder.create().symbol(SymbolType.TAGS).tooltip("Tags").size(48).build(),
			RadialMenuItemBuilder.create().symbol(SymbolType.CART).tooltip("Shop").size(48).build(),
			RadialMenuItemBuilder.create().symbol(SymbolType.ALARM).tooltip("Alarm").size(48).build(),
			RadialMenuItemBuilder.create().symbol(SymbolType.CLOCK).tooltip("Clock").size(48).build())
		.build();
	radialMenu.setPrefSize(500, 500);
	radialMenu.setOnItemSelected(
		selectionEvent -> System.out.println("item " + selectionEvent.item.getTooltip() + " selected"));
	radialMenu.setOnItemClicked(
		clickEvent -> System.out.println("item " + clickEvent.item.getTooltip() + " clicked"));
	radialMenu.setOnMenuOpenStarted(menuEvent -> System.out.println("Menu starts to open"));
	radialMenu.setOnMenuOpenFinished(menuEvent -> System.out.println("Menu finished to open"));
	radialMenu.setOnMenuCloseStarted(menuEvent -> System.out.println("Menu starts to close"));
	radialMenu.setOnMenuCloseFinished(menuEvent -> System.out.println("Menu finished to close"));

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
	pane.setBackground(
		new Background(new BackgroundFill(Color.rgb(150, 150, 150), CornerRadii.EMPTY, Insets.EMPTY)));

	Scene scene = new Scene(pane);

	stage.setScene(scene);
	stage.show();
    }

    public static void main(String[] args) {
	launch(args);
    }
}
