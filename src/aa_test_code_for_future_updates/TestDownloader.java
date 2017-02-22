/**
 * 
 */
package aa_test_code_for_future_updates;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * @author GOXR3PLUS
 *
 */
public class TestDownloader extends Application {

    /**
     * @param args
     */
    public static void main(String[] args) {
	launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
	//Block From exiting
	Platform.setImplicitExit(false);

	//"https://github.com/goxr3plus/XR3Player/releases/download/V3.45/XR3Player.Update.45.zip"

	//Try to download the File
	DownloadService downloadService = new DownloadService();
	downloadService.setRemoteResourceLocation(
		new URL("https://github.com/goxr3plus/XR3Player/releases/download/V3.45/XR3Player.Update.45.zip"));
	downloadService.setPathToLocalResource(Paths.get(System.getProperty("user.home") + File.separator + "Desktop"
		+ File.separator + "XR3Player.Update.zip"));
	downloadService.startDownload();
    }

}
