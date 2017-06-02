/**
 * 
 */
package application.versionupdate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import application.Main;
import application.tools.ActionTool;
import application.tools.InfoTool;
import application.tools.NotificationType;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * @author GOXR3PLUS
 *
 */
public class UpdateWindow extends StackPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private Label topLabel;
	
	@FXML
	private Button download;
	
	@FXML
	private Button gitHubButton;
	
	@FXML
	private Button automaticUpdate;
	
	@FXML
	private Button closeWindow;
	
	@FXML
	private Tab gitHubReleases;
	
	@FXML
	private Accordion gitHubAccordion;
	
	@FXML
	private Tab releasesTab;
	
	@FXML
	private BorderPane releasesInfoContainer;
	
	@FXML
	private Tab upcomingFeaturesTab;
	
	@FXML
	private BorderPane upcomingFeaturesContainer;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/** Window **/
	private Stage window = new Stage();
	
	private final InlineCssTextArea gitHubTextArea = new InlineCssTextArea();
	private final InlineCssTextArea upcomingTextArea = new InlineCssTextArea();
	private final VirtualizedScrollPane<InlineCssTextArea> vsPane1 = new VirtualizedScrollPane<>(gitHubTextArea);
	private final VirtualizedScrollPane<InlineCssTextArea> vsPane2 = new VirtualizedScrollPane<>(upcomingTextArea);
	
	private int update;
	
	private final String style = "-fx-font-weight:bold; -fx-font-size:14; -fx-fill:black;";
	
	/**
	 * The Thread which is responsible for the update check
	 */
	private static Thread updaterThread;
	
	/**
	 * Constructor.
	 */
	public UpdateWindow() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "UpdateWindow.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
		window.setTitle("Update Window");
		window.initStyle(StageStyle.UTILITY);
		window.setScene(new Scene(this));
		window.getScene().setOnKeyReleased(k -> {
			if (k.getCode() == KeyCode.ESCAPE)
				window.close();
		});
	}
	
	/**
	 * Called as soon as .fxml is initialized
	 */
	@FXML
	private void initialize() {
		
		// --
		gitHubTextArea.setEditable(false);
		gitHubTextArea.setFocusTraversable(false);
		
		//--
		upcomingTextArea.setEditable(false);
		upcomingTextArea.setFocusTraversable(false);
		upcomingTextArea.setWrapText(true);
		
		//--
		releasesInfoContainer.setCenter(vsPane1);
		upcomingFeaturesContainer.setCenter(vsPane2);
		
		// -- automaticUpdate
		automaticUpdate.setOnAction(a -> startXR3PlayerUpdater(update));
		
		// -- download
		download.setOnAction(a -> ActionTool.openWebSite("https://sourceforge.net/projects/xr3player/"));
		
		// -- GitHub
		gitHubButton.setOnAction(a -> ActionTool.openWebSite("https://github.com/goxr3plus/XR3Player"));
		
		// -- closeWindow
		closeWindow.setOnAction(a -> window.close());
		
	}
	
	/**
	 * This method is fetching data from github to check if the is a new update for XR3Player
	 * 
	 * @param showTheWindow
	 *            If not update is available then don't show the window
	 */
	public synchronized void searchForUpdates(boolean showTheWindow) {
		
		// Not already running
		if (updaterThread != null && updaterThread.isAlive())
			return;
		
		updaterThread = new Thread(() -> {
			if (showTheWindow)
				Platform.runLater(
						() -> ActionTool.showNotification("Searching for Updates", "Fetching informations from server...", Duration.millis(1000), NotificationType.INFORMATION));
			
			//Check if we have internet connection
			if (InfoTool.isReachableByPing("www.google.com"))
				searchForUpdatesPart2(showTheWindow);
			else
				Platform.runLater(() -> ActionTool.showNotification("Can't Connect",
						"Can't connect to the update site :\n1) Maybe there is not internet connection\n2)GitHub is down for maintenance", Duration.millis(2500),
						NotificationType.ERROR));
			
		}, "Application Update Thread");
		
		updaterThread.setDaemon(true);
		updaterThread.start();
		
	}
	
	private void searchForUpdatesPart2(boolean showTheWindow) {
		try {
			
			Document doc = Jsoup.connect("https://raw.githubusercontent.com/goxr3plus/XR3Player/master/XR3PlayerUpdatePage.html").get();
			
			//Document doc = Jsoup.parse(new File("XR3PlayerUpdatePage.html"), "UTF-8", "http://example.com/");
			
			Element lastArticle = doc.getElementsByTag("article").last();
			
			// Not disturb the user every time the application starts if there is not new update
			int currentVersion = (int) Main.applicationProperties.get("Version");
			if (Integer.valueOf(lastArticle.id()) <= currentVersion && !showTheWindow)
				return;
			
			//GitHub Releases
			HttpURLConnection httpcon = (HttpURLConnection) new URL("https://api.github.com/repos/goxr3plus/XR3Player/releases").openConnection();
			httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
			
			//Read line by line
			String responseSB = in.lines().collect(Collectors.joining());
			in.close();
			
			// Update is available or not?
			Platform.runLater(() -> {
				
				//--TopLabel
				if (Integer.valueOf(lastArticle.id()) <= currentVersion) {
					window.setTitle("You have the latest update!");
					topLabel.setText("You have the latest update ->( " + currentVersion + " )<-");
				} else {
					window.setTitle("New update is available!");
					topLabel.setText("New Update ->( " + lastArticle.id() + " )<- is available !!! |  Current : ->( " + currentVersion + " )<-");
				}
				
				//Read the JSON response
				JsonArray jsonRoot;
				try {
					jsonRoot = (JsonArray) Jsoner.deserialize(responseSB);
					//Avoid recreating again panes if no new releases have come
					boolean create = jsonRoot.stream().count() != gitHubAccordion.getPanes().size();
					
					//For Each
					int[] counter = { 0 };
					jsonRoot.forEach(item -> {
						//--
						String prerelease = ( (JsonObject) item ).get("prerelease").toString();
						
						//--
						String tagName = ( (JsonObject) item ).get("tag_name").toString();
						
						//--
						String[] downloads = { "" };
						( (JsonArray) ( (JsonObject) item ).get("assets") ).forEach(item2 -> downloads[0] = ( (JsonObject) item2 ).get("download_count").toString());
						downloads[0] = ( downloads[0].isEmpty() ? "-" : downloads[0] );
						
						//--
						String[] size = { "" };
						( (JsonArray) ( (JsonObject) item ).get("assets") ).forEach(item2 -> size[0] = ( (JsonObject) item2 ).get("size").toString());
						size[0] = ( size[0].isEmpty() ? "-" : InfoTool.getFileSizeEdited(Long.parseLong(size[0])) );
						
						//--
						String[] createdAt = { ( (JsonObject) item ).get("created_at").toString() };
						
						//--
						String[] publishedAt = { ( (JsonObject) item ).get("published_at").toString() };
						
						//Create or Reuse the existing Panes of Accordion
						GitHubRelease release;
						if (!create)
							release = (GitHubRelease) gitHubAccordion.getPanes().get(counter[0]++);
						else {
							release = new GitHubRelease();
							gitHubAccordion.getPanes().add(release);
						}
						
						//Update the GitHubRelease Pane
						release.setText("Update -> ( " + tagName.toLowerCase().replace("v3.", "") + " ) Downloads ( " + downloads[0] + " )");
						release.updateLabels(Boolean.toString(!Boolean.parseBoolean(prerelease)), downloads[0], size[0], publishedAt[0].substring(0, 10),
								createdAt[0].substring(0, 10));
						
					});
					gitHubAccordion.setExpandedPane(gitHubAccordion.getPanes().get(0));
				} catch (DeserializationException e) {
					e.printStackTrace();
					ActionTool.showNotification("Message", "Failed to connect update server :(\n Try again in 5 seconds", Duration.seconds(3), NotificationType.ERROR);
				}
				
				//Clear the textAreas
				gitHubTextArea.clear();
				upcomingTextArea.clear();
				
				// -- gitHubTextArea 			
				doc.getElementsByTag("article").stream().collect(Collectors.toCollection(ArrayDeque::new)).descendingIterator()
						.forEachRemaining(element -> analyzeUpdate(gitHubTextArea, element));
				
				//textArea.moveTo(gitHubTextArea.getLength());
				//textArea.requestFollowCaret();
				
				// -- upcomingTextArea
				doc.getElementsByTag("section").forEach(section -> {
					
					// Append the text to the textArea
					upcomingTextArea.appendText("\n\n-------------Upcoming Features for XR3Player-------------\n\n");
					
					// Information
					upcomingTextArea.appendText("->Coming:\n");
					upcomingTextArea.setStyle(upcomingTextArea.getLength() - 8, upcomingTextArea.getLength() - 1, style.replace("black", "green"));
					final AtomicInteger counter = new AtomicInteger(-1);
					Arrays.asList(section.getElementById("info").text().split("\\*")).forEach(el -> {
						if (counter.addAndGet(1) >= 1) {
							String s = "\t" + counter + ")";
							upcomingTextArea.appendText(s);
							upcomingTextArea.setStyle(upcomingTextArea.getLength() - s.length(), upcomingTextArea.getLength() - 1, style);
							upcomingTextArea.appendText(el + "\n");
						}
					});
					
					//Last Updated
					upcomingTextArea.appendText("->Last Updated: ");
					upcomingTextArea.setStyle(upcomingTextArea.getLength() - 14, upcomingTextArea.getLength() - 1, style.replace("black", "firebrick"));
					upcomingTextArea.appendText(section.getElementById("lastUpdated").text());
				});
				
				upcomingTextArea.moveTo(upcomingTextArea.getLength());
				upcomingTextArea.requestFollowCaret();
			});
			
			//show?
			if (showTheWindow || Integer.valueOf(lastArticle.id()) > currentVersion) {
				download.setDisable(Integer.valueOf(lastArticle.id()) <= currentVersion);
				automaticUpdate.setDisable(download.isDisable());
				update = Integer.valueOf(lastArticle.id());
				show();
			}
			
		} catch (IOException ex) {
			Platform.runLater(() -> ActionTool.showNotification("Error", "Trying to fetch update information a problem occured", Duration.millis(2500), NotificationType.ERROR));
			logger.log(Level.WARNING, "", ex);
		}
	}
	
	/**
	 * Streams the given update and appends it to the InlineCssTextArea in a specific format
	 * 
	 * @param textArea
	 * @param Element
	 */
	private void analyzeUpdate(InlineCssTextArea textArea , Element element) {
		
		// Append the text to the textArea
		textArea.appendText("\n\n-------------Start of Update (" + element.id() + ")-------------\n");
		
		// Information
		textArea.appendText("->Information: ");
		textArea.setStyle(textArea.getLength() - 13, textArea.getLength() - 1, style.replace("black", "#202020"));
		textArea.appendText(element.getElementsByClass("about").text() + "\n");
		
		// Release Date
		textArea.appendText("->Release Date: ");
		textArea.setStyle(textArea.getLength() - 14, textArea.getLength() - 1, style.replace("black", "firebrick"));
		textArea.appendText(element.getElementsByClass("releasedate").text() + "\n");
		
		// Minimum JRE
		textArea.appendText("->Minimum Java Version: ");
		textArea.setStyle(textArea.getLength() - 22, textArea.getLength() - 1, style.replace("black", "orange"));
		textArea.appendText(element.getElementsByClass("minJavaVersion").text() + "\n");
		
		// ChangeLog
		textArea.appendText("->ChangeLog:\n");
		textArea.setStyle(textArea.getLength() - 11, textArea.getLength() - 1, style.replace("black", "green"));
		final AtomicInteger counter = new AtomicInteger(-1);
		Arrays.asList(element.getElementsByClass("changelog").text().split("\\*")).forEach(el -> {
			if (counter.addAndGet(1) >= 1) {
				String s = "\t" + counter + ")";
				textArea.appendText(s);
				textArea.setStyle(textArea.getLength() - s.length(), textArea.getLength() - 1, style);
				textArea.appendText(el + "\n");
			}
		});
	}
	
	/**
	 * Calling this method to start the main Application which is XR3Player
	 * 
	 */
	public void startXR3PlayerUpdater(int update) {
		String applicationName = "XR3PlayerUpdater";
		
		// Start XR3Player Updater
		new Thread(() -> {
			String path = InfoTool.getBasePathForClass(Main.class);
			String[] applicationPath = { new File(path + applicationName + ".jar").getAbsolutePath() };
			
			//Show message that application is restarting
			Platform.runLater(() -> ActionTool.showNotification("Starting " + applicationName,
					"Application Path:[ " + applicationPath[0] + " ]\n\tIf this takes more than 10 seconds either the computer is slow or it has failed....", Duration.seconds(25),
					NotificationType.INFORMATION));
			
			try {
				
				//----Auto Update Button
				Platform.runLater(() -> automaticUpdate.setDisable(true));
				
				//------------Export XR3PlayerUpdater
				ActionTool.copy(UpdateWindow.class.getResourceAsStream("/updater/" + applicationName + ".jar"), applicationPath[0]);
				
				//------------Wait until XR3Player is created
				File XR3Player = new File(applicationPath[0]);
				while (!XR3Player.exists()) {
					Thread.sleep(50);
					System.out.println("Waiting " + applicationName + " Jar to be created...");
				}
				
				System.out.println(applicationName + " Path is : " + applicationPath[0]);
				
				//Create a process builder
				ProcessBuilder builder = new ProcessBuilder("java", "-jar", applicationPath[0], String.valueOf(update));
				builder.redirectErrorStream(true);
				Process process = builder.start();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				
				// Wait n seconds
				PauseTransition pause = new PauseTransition(Duration.seconds(10));
				pause.setOnFinished(f -> Platform.runLater(() -> ActionTool.showNotification("Starting " + applicationName + " failed",
						"\nApplication Path: [ " + applicationPath[0] + " ]\n\tTry to do it manually...", Duration.seconds(10), NotificationType.ERROR)));
				pause.play();
				
				// Continuously Read Output to check if the main application started
				String line;
				while (process.isAlive())
					while ( ( line = bufferedReader.readLine() ) != null) {
						System.out.println(line);
						if (line.isEmpty())
							break;
						if (line.contains(applicationName + " Application Started"))
							System.exit(0);
					}
				
			} catch (IOException | InterruptedException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.INFO, null, ex);
				
				// Show failed message
				Platform.runLater(() -> Platform.runLater(() -> ActionTool.showNotification("Starting " + applicationName + " failed",
						"\nApplication Path: [ " + applicationPath[0] + " ]\n\tTry to do it manually...", Duration.seconds(10), NotificationType.ERROR)));
				
			}
			
			//----Auto Update Button
			Platform.runLater(() -> automaticUpdate.setDisable(false));
			
		}, "Start XR3Application Thread").start();
	}
	
	/**
	 * Show the Window
	 */
	public void show() {
		Platform.runLater(() -> {
			if (!window.isShowing())
				window.show();
			else
				window.requestFocus();
		});
	}
	
	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}
	
}
