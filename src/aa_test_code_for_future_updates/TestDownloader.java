/**
 * 
 */
package aa_test_code_for_future_updates;

import java.io.File;

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
	new DownloadService().startDownload(
		"https://github.com/goxr3plus/XR3Player/releases/download/V3.45/XR3Player.Update.45.zip",
		System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "XR3Player.Update.zip");

    }

}
