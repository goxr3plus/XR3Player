module XR3Player {


	opens com.goxr3plus.xr3player.application to javafx.graphics;
//	opens com.goxr3plus.xr3player.controllers.chromium to javafx.fxml;
	opens com.goxr3plus.xr3player.controllers.custom to javafx.fxml;
	opens com.goxr3plus.xr3player.controllers.djmode to javafx.fxml;
	opens com.goxr3plus.xr3player.controllers.dropbox to javafx.fxml;
	opens com.goxr3plus.xr3player.controllers.general to javafx.fxml;
	opens com.goxr3plus.xr3player.controllers.librarymode to javafx.fxml;
	opens com.goxr3plus.xr3player.controllers.loginmode to javafx.fxml;
	opens com.goxr3plus.xr3player.controllers.moviemode to javafx.fxml;
	opens com.goxr3plus.xr3player.controllers.settings to javafx.fxml;
	opens com.goxr3plus.xr3player.controllers.smartcontroller to javafx.fxml;
	opens com.goxr3plus.xr3player.controllers.systemtree to javafx.fxml;
	opens com.goxr3plus.xr3player.controllers.tagging to javafx.fxml;
	opens com.goxr3plus.xr3player.controllers.windows to javafx.fxml;
	opens com.goxr3plus.xr3player.controllers.xplayer to javafx.fxml;
	opens com.goxr3plus.xr3player.models.smartcontroller to javafx.base;
	opens com.goxr3plus.xr3player.xplayer.visualizer.presenter to javafx.fxml;

	requires FX.BorderlessScene;
	requires com.goxr3plus.streamplayer;
	requires com.jfoenix;
	requires commons.validator;
	requires dropbox.core.sdk;
	requires evo.inflector;
	requires image4j;
	requires jaudiotagger;
	requires java.base;
	requires java.flac.encoder;
	requires java.google.speech.api;
	requires java.logging;
	requires java.net.http;
	requires java.sql;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.media;
	requires javafx.swing;
	requires javasysmon2;
	requires jave.core;
	requires jnativehook;
//	requires jxbrowser;
	requires mp3agic;
	requires org.apache.commons.io;
	requires org.controlsfx.controls;
	requires org.jsoup;
	requires org.kordamp.ikonli.core;
	requires org.kordamp.ikonli.javafx;
	requires reactfx;
	requires zt.zip;
	requires org.fxmisc.richtext;
	requires org.fxmisc.flowless;
	requires org.json;
//    requires XR3Capture;
}
