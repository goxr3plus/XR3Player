/**
 * 
 */
package com.goxr3plus.xr3player.controllers.windows;

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

import com.goxr3plus.xr3player.application.MainExit;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.general.NetworkingTool;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

/**
 * @author GOXR3PLUS
 *
 */
public class UpdateWindow extends StackPane {

	// --------------------------------------------------------------

	@FXML
	private JFXButton closeWindow;

	@FXML
	private JFXButton viewOnGithub;

	@FXML
	private JFXButton automaticUpdate;

	@FXML
	private JFXTabPane tabPane;

	@FXML
	private Tab whatsNewTab;

	@FXML
	private BorderPane whatsNewContainer;

	@FXML
	private Tab upcomingFeaturesTab;

	@FXML
	private BorderPane upcomingFeaturesContainer;

	@FXML
	private Tab knowBugsTab;

	@FXML
	private BorderPane knownBugsContainer;

	@FXML
	private Tab releasesHistoryTab;

	@FXML
	private Accordion gitHubAccordion;

	@FXML
	private Label topLabel;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	/** Window **/
	private final Stage window = new Stage();

	private final InlineCssTextArea whatsNewTextArea = new InlineCssTextArea();
	private final InlineCssTextArea upcomingFeaturesTextArea = new InlineCssTextArea();
	private final InlineCssTextArea knownBugsTextArea = new InlineCssTextArea();
	private final VirtualizedScrollPane<InlineCssTextArea> whatsNewVirtualPane = new VirtualizedScrollPane<>(
			whatsNewTextArea);
	private final VirtualizedScrollPane<InlineCssTextArea> upcomingFeaturesVirtualPane = new VirtualizedScrollPane<>(
			upcomingFeaturesTextArea);
	private final VirtualizedScrollPane<InlineCssTextArea> knownBugsVirtualPane = new VirtualizedScrollPane<>(
			knownBugsTextArea);

	private final String style = "-fx-font-weight:bold; -fx-font-size:14; -fx-fill:white;  -rtfx-background-color:transparent;";

	/**
	 * The Thread which is responsible for the update check
	 */
	private static Thread updaterThread;

	/**
	 * Constructor.
	 */
	public UpdateWindow() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		final FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.WINDOW_FXMLS + "UpdateWindow.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (final IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}

		window.setTitle("Update Window");
		window.initStyle(StageStyle.UTILITY);
		window.setScene(new Scene(this));
		window.getScene().getStylesheets()
				.add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());
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

		// -- whatsNewVirtualPane , upcomingFeaturesVirtualPane , knownBugsVirtualPane
		whatsNewVirtualPane.setFocusTraversable(false);
		upcomingFeaturesVirtualPane.setFocusTraversable(false);
		knownBugsVirtualPane.setFocusTraversable(false);

		// -- whatsNewTextArea
		whatsNewTextArea.setEditable(false);
		whatsNewTextArea.setFocusTraversable(false);
		whatsNewTextArea.getStyleClass().add("inline-css-text-area");

		// -- upcomingFeaturesTextArea
		upcomingFeaturesTextArea.setEditable(false);
		upcomingFeaturesTextArea.setFocusTraversable(false);
		upcomingFeaturesTextArea.setWrapText(true);
		upcomingFeaturesTextArea.getStyleClass().add("inline-css-text-area");

		// -- knownBugsTextArea
		knownBugsTextArea.setEditable(false);
		knownBugsTextArea.setFocusTraversable(false);
		knownBugsTextArea.setWrapText(true);
		knownBugsTextArea.getStyleClass().add("inline-css-text-area");

		// -- whatsNewContainer + upcomingFeaturesContainer + knownBugsContainer
		whatsNewContainer.setCenter(whatsNewVirtualPane);
		upcomingFeaturesContainer.setCenter(upcomingFeaturesVirtualPane);
		knownBugsContainer.setCenter(knownBugsVirtualPane);

		// -- GitHub
		viewOnGithub.setOnAction(a -> NetworkingTool.openWebSite(InfoTool.GITHUB_URL));

		// -- automaticUpdate
		automaticUpdate
				.setOnAction(a -> NetworkingTool.openWebSite("https://github.com/goxr3plus/XR3Player/releases/latest"));

		// -- closeWindow
		closeWindow.setOnAction(a -> window.close());

	}

	/**
	 * This method is fetching data from github to check if the is a new update for
	 * XR3Player
	 * 
	 * @param showTheWindow If not update is available then don't show the window
	 */
	public synchronized void searchForUpdates(final boolean showTheWindow) {

		// Not already running
		if (updaterThread != null && updaterThread.isAlive())
			return;

		updaterThread = new Thread(() -> {
			if (showTheWindow)
				Platform.runLater(() -> AlertTool.showNotification("Searching for Updates",
						"Fetching informations from server...", Duration.millis(1000), NotificationType.INFORMATION));

			// Check if we have internet connection
			if (NetworkingTool.isReachableByPing("www.google.com"))
				searchForUpdatesPart2(showTheWindow);
			else
				Platform.runLater(() -> AlertTool.showNotification("Can't Connect",
						"Can't connect to the update site :\n1) Maybe there is not internet connection\n2)GitHub is down for maintenance",
						Duration.millis(2500), NotificationType.ERROR));

		}, "Application Update Thread");

		updaterThread.setDaemon(true);
		updaterThread.start();

	}

	private boolean searchForUpdatesPart2(final boolean showTheWindow) {
		try {

			final Document doc = Jsoup
					.connect("https://raw.githubusercontent.com/goxr3plus/XR3Player/master/XR3PlayerUpdatePage.html")
					.get();

			// Document doc = Jsoup.parse(new File("XR3PlayerUpdatePage.html"), "UTF-8",
			// "http://example.com/")

			final int currentVersion = Main.APPLICATION_VERSION;
			final int lastArticleID = doc.getElementsByTag("article").stream()
					.mapToInt(element -> Integer.parseInt(element.id())).max().getAsInt();

			// Check the latest tag on github
			if (getLatestReleaseTag() <= Main.APPLICATION_VERSION && !showTheWindow)
				return false;

			// Update is available or not?
			Platform.runLater(() -> {

				// --TopLabel
				if (lastArticleID <= currentVersion) {
					window.setTitle("You have the latest update!");
					topLabel.setText("You have the latest update ->( " + currentVersion + " )<-");
				} else {
					window.setTitle("New update is available!");
					topLabel.setText("New Update ->( " + lastArticleID
							+ " )<- is available !!!! | You currently have : ->( " + currentVersion + " )<-");
					tabPane.getSelectionModel().select(0);
				}

				// Clear the textAreas
				whatsNewTextArea.clear();
				upcomingFeaturesTextArea.clear();
				knownBugsTextArea.clear();

				// --------------------------------------- whatsNewTextArea
				// -----------------------------------
				doc.getElementsByTag("article").stream()
						.sorted((o1, o2) -> Integer.compare(Integer.parseInt(o1.id()), Integer.parseInt(o2.id())))
						.collect(Collectors.toCollection(ArrayDeque::new)).descendingIterator()
						.forEachRemaining(element -> analyzeUpdate(whatsNewTextArea, element));

				whatsNewTextArea.moveTo(0);
				whatsNewTextArea.requestFollowCaret();

				// --------------------------------------- upcomingTextArea
				// -----------------------------------
				doc.getElementsByTag("section").stream().filter(section -> "Upcoming Features".equals(section.id()))
						.forEach(section -> {

							// Append the text to the textArea
							upcomingFeaturesTextArea.appendText("\n");

							// Most Important
							upcomingFeaturesTextArea.appendText("  Most Important:\n\n");
							upcomingFeaturesTextArea.setStyle(upcomingFeaturesTextArea.getLength() - 17,
									upcomingFeaturesTextArea.getLength() - 1, style.replace("white", "#3DFF53"));
							final AtomicInteger counter2 = new AtomicInteger(-1);
							Arrays.asList(section.getElementById("most important").text().split("\\*")).forEach(el -> {
								if (counter2.addAndGet(1) >= 1) {
									final String s = "\t" + counter2 + " ";
									upcomingFeaturesTextArea.appendText(s);
									upcomingFeaturesTextArea.setStyle(upcomingFeaturesTextArea.getLength() - s.length(),
											upcomingFeaturesTextArea.getLength() - 1, style2);
									upcomingFeaturesTextArea.appendText(el + "\n");
									upcomingFeaturesTextArea.setStyle(
											upcomingFeaturesTextArea.getLength() - el.length(),
											upcomingFeaturesTextArea.getLength() - 1, style3);
								}
							});

							// Less Important
							upcomingFeaturesTextArea.appendText("\n\n  Less Important:\n\n");
							upcomingFeaturesTextArea.setStyle(upcomingFeaturesTextArea.getLength() - 17,
									upcomingFeaturesTextArea.getLength() - 1, style.replace("white", "#3DFF53"));
							final AtomicInteger counter = new AtomicInteger(-1);
							Arrays.asList(section.getElementById("info").text().split("\\*")).forEach(el -> {
								if (counter.addAndGet(1) >= 1) {
									final String s = "\t" + counter + " ";
									upcomingFeaturesTextArea.appendText(s);
									upcomingFeaturesTextArea.setStyle(upcomingFeaturesTextArea.getLength() - s.length(),
											upcomingFeaturesTextArea.getLength() - 1, style2);
									upcomingFeaturesTextArea.appendText(el + "\n");
									upcomingFeaturesTextArea.setStyle(
											upcomingFeaturesTextArea.getLength() - el.length(),
											upcomingFeaturesTextArea.getLength() - 1, style3);
								}
							});

							// Last Updated
							upcomingFeaturesTextArea.appendText("\n  Last Updated: ");
							upcomingFeaturesTextArea.setStyle(upcomingFeaturesTextArea.getLength() - 14,
									upcomingFeaturesTextArea.getLength() - 1, style.replace("white", "#FFEC00"));
							upcomingFeaturesTextArea.appendText(section.getElementById("lastUpdated").text());
							upcomingFeaturesTextArea.setStyle(
									upcomingFeaturesTextArea.getLength()
											- section.getElementById("lastUpdated").text().length(),
									upcomingFeaturesTextArea.getLength(), style3);

						});

				// upcomingFeaturesTextArea.moveTo(upcomingFeaturesTextArea.getLength())
				// upcomingFeaturesTextArea.requestFollowCaret()

				// --------------------------------------- knownBugsTextArea
				// -----------------------------------
				doc.getElementsByTag("section").stream().filter(section -> "Bugs".equals(section.id()))
						.forEach(section -> {

							// Append the text to the textArea
							knownBugsTextArea.appendText("\n");

							// Information
							knownBugsTextArea.appendText("  Bugs:\n\n");
							knownBugsTextArea.setStyle(knownBugsTextArea.getLength() - 7,
									knownBugsTextArea.getLength() - 1, style.replace("white", "#FF130F"));
							final AtomicInteger counter = new AtomicInteger(-1);
							Arrays.asList(section.getElementById("info").text().split("\\*")).forEach(el -> {
								if (counter.addAndGet(1) >= 1) {
									final String s = "\t" + counter + " ";
									knownBugsTextArea.appendText(s);
									knownBugsTextArea.setStyle(knownBugsTextArea.getLength() - s.length(),
											knownBugsTextArea.getLength() - 1, style2);
									knownBugsTextArea.appendText(el + "\n");
									knownBugsTextArea.setStyle(knownBugsTextArea.getLength() - el.length(),
											knownBugsTextArea.getLength() - 1, style3);
								}
							});

							// Last Updated
							knownBugsTextArea.appendText("\n  Last Updated: ");
							knownBugsTextArea.setStyle(knownBugsTextArea.getLength() - 14,
									knownBugsTextArea.getLength() - 1, style.replace("white", "#FFEC00"));
							knownBugsTextArea.appendText(section.getElementById("lastUpdated").text());
							knownBugsTextArea.setStyle(
									knownBugsTextArea.getLength()
											- section.getElementById("lastUpdated").text().length(),
									knownBugsTextArea.getLength(), style3);

						});

				// knownBugsTextArea.moveTo(knownBugsTextArea.getLength())
				// knownBugsTextArea.requestFollowCaret()
			});

			// show?
			final int latestUpdate = lastArticleID;
			if (showTheWindow || latestUpdate > currentVersion) {
				automaticUpdate.setDisable(latestUpdate <= currentVersion);
				show();
			}

		} catch (final IOException ex) {
			// Show message to the user
			Platform.runLater(
					() -> AlertTool.showNotification("Error", "Trying to fetch update information a problem occured",
							Duration.millis(2500), NotificationType.ERROR));
			ex.printStackTrace();
			return false;
		}

		return true;
	}

	private final String style2 = style.replace("white", "#329CFF");
	private final String style3 = style.replace("bold", "400");

	// For [ New , Improved , Bug Fixes ] counters
	private final String sectionNewCounters = style.replace("white", "#00D993");
	private final String sectionImrpovedCounters = style.replace("white", "#00BBEF");
	private final String sectionBugsFixedCounters = style.replace("white", "#F0004C");

	// Other styles
	private final String updateStyle = style.replace("transparent", "#000000");
	private final String releaseDateStyle = style.replace("white", "#3DFF53");
	private final String minimumJREStyle = style.replace("white", "#FF8800");
	private final String changeLogStyle = style.replace("white", "#FFEC00");
	private final String newStyle = style.replace("transparent", "#00D993");
	private final String improvedStyle = style.replace("transparent", "#00BBEF");
	private final String bugFixesStyle = style.replace("transparent", "#F0004C");

	/**
	 * Streams the given update and appends it to the InlineCssTextArea in a
	 * specific format
	 * 
	 * @param textArea
	 * @param element
	 */
	private void analyzeUpdate(final InlineCssTextArea textArea, final Element element) {
		textArea.appendText("\n\n");

		// Update Version
		final int id = Integer.parseInt(element.id());

		// Update
		String text = "\t\t\t\t\t\t\t\t\t\t Update  ~  " + id + " \n";
		textArea.appendText(text);
		textArea.setStyle(textArea.getLength() - text.length() + 11, textArea.getLength() - 1, updateStyle);

		// Release Date
		text = "\t\t\t\t\t\t  Released: ";
		textArea.appendText(text);
		textArea.setStyle(textArea.getLength() - text.length() + 7, textArea.getLength() - 1, releaseDateStyle);
		textArea.appendText(element.getElementsByClass("releasedate").text() + " ");
		textArea.setStyle(textArea.getLength() - element.getElementsByClass("releasedate").text().length() - 1,
				textArea.getLength() - 1, style3);

		// Minimum JRE
		text = "  Requires Java: ";
		textArea.appendText(text);
		textArea.setStyle(textArea.getLength() - text.length() - 1, textArea.getLength() - 1, minimumJREStyle);
		textArea.appendText(element.getElementsByClass("minJavaVersion").text() + "\n");
		textArea.setStyle(textArea.getLength() - element.getElementsByClass("minJavaVersion").text().length() - 1,
				textArea.getLength() - 1, style3);

		// ChangeLog
		if (id < 91) { // After Update 91 change log contains more sections
			text = "  ChangeLog:\n";
			textArea.appendText(text);
			textArea.setStyle(textArea.getLength() - text.length() - 1, textArea.getLength() - 1, changeLogStyle);

			final AtomicInteger counter = new AtomicInteger(-1);
			Arrays.asList(element.getElementsByClass("changelog").text().split("\\*")).forEach(improvement -> {
				if (counter.addAndGet(1) >= 1) {
					final String s = "\t" + counter + " ";
					textArea.appendText(s);
					textArea.setStyle(textArea.getLength() - s.length(), textArea.getLength() - 1, style2);
					textArea.appendText(improvement + "\n");
					textArea.setStyle(textArea.getLength() - improvement.length() - 1, textArea.getLength() - 1,
							style3);
				}
			});
		} else {

			// Show or not?
			if (element.getElementsByClass("new").text().split("\\*").length > 0) {

				// new
				text = "      New/Added \n";
				textArea.appendText(text);
				textArea.setStyle(textArea.getLength() - text.length() + 5, textArea.getLength() - 1, newStyle);
				final AtomicInteger counter = new AtomicInteger(-1);
				Arrays.asList(element.getElementsByClass("new").text().split("\\*")).forEach(improvement -> {
					if (counter.addAndGet(1) >= 1) {
						final String s = "\t\t" + counter + " ";
						textArea.appendText(s);
						textArea.setStyle(textArea.getLength() - s.length(), textArea.getLength() - 1,
								sectionNewCounters);
						textArea.appendText(improvement + "\n");
						textArea.setStyle(textArea.getLength() - improvement.length() - 1, textArea.getLength() - 1,
								style3);
					}
				});
			}

			// Show or not?
			if (element.getElementsByClass("improved").text().split("\\*").length > 0) {

				// improved
				text = "      Improved \n";
				textArea.appendText(text);
				textArea.setStyle(textArea.getLength() - text.length() + 5, textArea.getLength() - 1, improvedStyle);
				final AtomicInteger counter2 = new AtomicInteger(-1);
				Arrays.asList(element.getElementsByClass("improved").text().split("\\*")).forEach(improvement -> {
					if (counter2.addAndGet(1) >= 1) {
						final String s = "\t\t" + counter2 + " ";
						textArea.appendText(s);
						textArea.setStyle(textArea.getLength() - s.length(), textArea.getLength() - 1,
								sectionImrpovedCounters);
						textArea.appendText(improvement + "\n");
						textArea.setStyle(textArea.getLength() - improvement.length() - 1, textArea.getLength() - 1,
								style3);
					}
				});
			}

			// Show or not?
			if (element.getElementsByClass("fixed").text().split("\\*").length > 0) {

				// fixed
				text = "      Bug Fixes \n";
				textArea.appendText(text);
				textArea.setStyle(textArea.getLength() - text.length() + 5, textArea.getLength() - 1, bugFixesStyle);
				final AtomicInteger counter3 = new AtomicInteger(-1);
				Arrays.asList(element.getElementsByClass("fixed").text().split("\\*")).forEach(improvement -> {
					if (counter3.addAndGet(1) >= 1) {
						final String s = "\t\t" + counter3 + " ";
						textArea.appendText(s);
						textArea.setStyle(textArea.getLength() - s.length(), textArea.getLength() - 1,
								sectionBugsFixedCounters);
						textArea.appendText(improvement + "\n");
						textArea.setStyle(textArea.getLength() - improvement.length() - 1, textArea.getLength() - 1,
								style3);
					}
				});
			}
		}
	}

	/**
	 * Checks to see if the latest release tag matches the UpdatePage.html
	 */
	public int getLatestReleaseTag() {
		try {

			// Check if really that release exists....
			final HttpURLConnection httpcon = (HttpURLConnection) new URL(
					"https://api.github.com/repos/goxr3plus/XR3Player/releases/latest").openConnection();
			httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
			final BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));

			// Read line by line
			final String responseSB = in.lines().collect(Collectors.joining());
			in.close();

			// Parse JSon
			final String latestVersion = new JSONObject(responseSB).getString("tag_name");

			// Return the latest release tag
			return Integer.parseInt(latestVersion.replaceAll("V3.", ""));
		} catch (final Exception ex) {
			ex.printStackTrace();
			return -1;
		}

	}

	/**
	 * Calling this method to start the main Application which is XR3Player
	 * 
	 */
	@Deprecated
	private void startXR3PlayerUpdater(final int update) {
		final String applicationName = "XR3PlayerUpdater";

		// Start XR3Player Updater
		new Thread(() -> {
			final String path = IOInfo.getBasePathForClass(Main.class);
			final String[] applicationPath = { new File(path + applicationName + ".jar").getAbsolutePath() };

			// Show message that application is restarting
			Platform.runLater(() -> AlertTool.showNotification("Starting " + applicationName, "Application Path:[ "
					+ applicationPath[0]
					+ " ]\n\tIf this takes more than 10 seconds either the computer is slow or it has failed....",
					Duration.seconds(25), NotificationType.INFORMATION));

			try {

				// ----Auto Update Button
				Platform.runLater(() -> automaticUpdate.setDisable(true));

				// ------------Export XR3PlayerUpdater
				IOAction.copy(UpdateWindow.class.getResourceAsStream("/updater/" + applicationName + ".jar"),
						applicationPath[0]);

				// ------------Wait until XR3Player is created
				final File XR3Player = new File(applicationPath[0]);
				while (!XR3Player.exists()) {
					Thread.sleep(50);
					System.out.println("Waiting " + applicationName + " Jar to be created...");
				}

				System.out.println(applicationName + " Path is : " + applicationPath[0]);

				// Create a process builder
				final ProcessBuilder builder = new ProcessBuilder("java", "-jar", applicationPath[0], String.valueOf(update));
				builder.redirectErrorStream(true);
				final Process process = builder.start();
				final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				// Wait n seconds
				final PauseTransition pause = new PauseTransition(Duration.seconds(10));
				pause.setOnFinished(f -> Platform
						.runLater(() -> AlertTool.showNotification("Starting " + applicationName + " failed",
								"\nApplication Path: [ " + applicationPath[0] + " ]\n\tTry to do it manually...",
								Duration.seconds(10), NotificationType.ERROR)));
				pause.play();

				// Continuously Read Output to check if the main application started
				String line;
				while (process.isAlive())
					while ((line = bufferedReader.readLine()) != null) {
						System.out.println(line);
						if (line.isEmpty())
							break;
						if (line.contains(applicationName + " Application Started"))
							MainExit.terminateXR3Player(0);
					}

			} catch (IOException | InterruptedException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.INFO, null, ex);

				// Show failed message
				Platform.runLater(() -> Platform
						.runLater(() -> AlertTool.showNotification("Starting " + applicationName + " failed",
								"\nApplication Path: [ " + applicationPath[0] + " ]\n\tTry to do it manually...",
								Duration.seconds(10), NotificationType.ERROR)));

			}

			// ----Auto Update Button
			Platform.runLater(() -> automaticUpdate.setDisable(false));

		}, "Start XR3Application Thread").start();
	}

	/**
	 * Show the Window
	 */
	public void show() {
		Platform.runLater(() -> {
			if (!window.isShowing()) {
				window.show();
				whatsNewTextArea.scrollYBy(20);
			} else
				window.requestFocus();
		});
	}

	/**
	 * Close the Window
	 */
	public void close() {
		window.close();
	}

	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}

}
