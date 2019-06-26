package com.goxr3plus.xr3player.application;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.goxr3plus.xr3player.database.DatabaseTool;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

import javafx.application.Platform;
import javafx.scene.image.Image;

public class MainTools {
    /**
     * The user has the ability to change the Library Image
     */
    public static void changeBackgroundImage() {

        // Check the response
        JavaFXTool.selectAndSaveImage("background", DatabaseTool.getAbsoluteDatabasePathPlain(), Main.specialChooser, Main.window)
                .ifPresent(imageFile -> Main.loginMode.getBackgroundImageView().setImage(new Image(imageFile.toURI() + "")));

    }

    /**
     * Determines the background image of the application based on if a custom image
     * exists inside the database .If not then the default image is being added :)
     */
    public static void determineBackgroundImage() {

        // Set the background image to the ImageView
        Optional.ofNullable(JavaFXTool.findAnyImageWithTitle("background", DatabaseTool.getAbsoluteDatabasePathPlain()))
                .
                // If the image exists
                        ifPresentOrElse(image -> Main.loginMode.getBackgroundImageView().setImage(image),
                        // If it doesn't set the default
                        () -> Main.loginMode.getBackgroundImageView()
                                .setImage(InfoTool.getImageFromResourcesFolder("application_background.jpg")));
    }

    /**
     * Resets the application background image to the default one
     */
    public static void resetBackgroundImage() {

        // Delete the background image
        JavaFXTool.deleteAnyImageWithTitle("background", DatabaseTool.getAbsoluteDatabasePathPlain());

        // Set the default one
        determineBackgroundImage();
    }

    /**
     * Count application downloads from Github and SourceForge
     */
    public static void countDownloads() {
        // ---- Update Downloads Labels
        new Thread(() -> {
            try {

                // ---------------------- COUNT TOTAL GITHUB DOWNLOADS ----------------------
                final String text2 = "GitHub: [ " + Arrays
                        .stream(IOUtils
                                .toString(new URL("https://api.github.com/repos/goxr3plus/XR3Player/releases"), "UTF-8")
                                .split("\"download_count\":"))
                        .skip(1).mapToInt(l -> Integer.parseInt(l.split(",")[0])).sum() + " ]";
                Platform.runLater(() -> Main.loginMode.getGitHubDownloadsLabel().setText(text2));

            } catch (final Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    Main.loginMode.getGitHubDownloadsLabel().setText("GitHub: [ ? ]");
                    Main.loginMode.getDownloadsVBox().setManaged(false);
                    Main.loginMode.getDownloadsVBox().setVisible(false);
                });

            }

            try {
                // ---------------------- COUNT TOTAL SOURCEFORGE DOWNLOADS  ----------------------

                //Create http  client
                HttpClient client = HttpClient.newHttpClient();

                //Create http request
                HttpRequest request = HttpRequest.newBuilder(URI.create("https://sourceforge.net/projects/xr3player/files/stats/json?start_date=2015-01-30&end_date=2050-01-30")).GET().build();

                //Send request and await for answer
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {

                        // Parse JSON
                        final JSONArray oses = new JSONObject(response.body()).getJSONArray("oses");

                        // Count total downloads
                        final int[] counter = {0};
                        oses.forEach(os -> counter[0] += Integer.parseInt(((JSONArray) os).get(1).toString()));

                        /* Run on JavaFX Thread */
                        Platform.runLater(
                            () -> Main.loginMode.getSourceForgeDownloadsLabel().setText("SourceForge: [ " + counter[0] + " ]"));

                    }).get();

            } catch (final Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    Main.loginMode.getSourceForgeDownloadsLabel().setText("SourceForge: [ ? ]");
                    Main.loginMode.getDownloadsVBox().setManaged(false);
                    Main.loginMode.getDownloadsVBox().setVisible(false);
                });

            }
        }).start();

    }
}
