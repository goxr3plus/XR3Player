/*
 * 
 */
package aacode_to_be_used_in_future;

import static application.Main.djMode;
import java.net.URISyntaxException;
import java.net.URL;

import disc.VolumeSlider;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import smartcontroller.Genre.TYPE;
import streamplayer.StreamPlayer;
import streamplayer.StreamPlayerException;
import tools.InfoTool;

// TODO: Auto-generated Javadoc
/**
 * The Class DJSoundTeam.
 */
public class DJSoundTeam extends TilePane {

	/** The border. */
	BorderPane border = new BorderPane();

	/** The group. */
	ToggleGroup group = new ToggleGroup();
	
	/** The player. */
	StreamPlayer player = new StreamPlayer();
	
	/** The url. */
	URL url;
	
	/** The slider. */
	VolumeSlider slider = new VolumeSlider(300, 20, Orientation.HORIZONTAL, 15, 100);

	/**
	 * Constructor.
	 *
	 * @param url the url
	 */
	public DJSoundTeam(URL url) {
		this.url = url;

		// This
		setPrefColumns(1);
		setHgap(10);
		setVgap(10);

		// Slider
		slider.setOnMouseDragged(this::onMouseDragged);
		slider.setOnScroll(this::onScroll);

		// Border
		border.setStyle("-fx-background-color:black");
		border.setTop(slider);
		border.setCenter(new ScrollPane(this));

	}

	// onScroll

	/**
	 * On scroll.
	 *
	 * @param scroll the scroll
	 */
	public void onScroll(ScrollEvent scroll) {
		if (scroll.getSource() == slider) {
			slider.onScroll(scroll);
			if (player.isPausedOrPlaying())
				player.setGain((double) (slider.getVolume() / 100.00));

		}
	}

	// onMouseDragged

	/**
	 * On mouse dragged.
	 *
	 * @param m the m
	 */
	public void onMouseDragged(MouseEvent m) {
		if (m.getSource() == slider) {
			slider.onMouseDragged(m);
			if (player.isPausedOrPlaying())
				player.setGain((double) (slider.getVolume() / 100.00));
		}

	}

	/**
	 * The Class DJSoundTeamButton.
	 */
	// TODO DJSoundTeamButtons
	public class DJSoundTeamButton extends StackPane {

		/** The radio button. */
		private RadioButton radioButton = new RadioButton();
		
		/** The play. */
		private Button play = new Button();
		
		/** The region. */
		private Region region = new Region();
		
		/** The progress bar. */
		private ProgressBar progressBar = new ProgressBar();

		/** The url. */
		private URL url;
		
		/** The category. */
		private int category;
		
		/** The duration. */
		private int duration = 0;
		
		/** The time service. */
		private TimeService timeService = new TimeService();

		/**
		 * Instantiates a new DJ sound team button.
		 *
		 * @param url2 url
		 * @param name όνομα κουμπιού
		 * @param Category the category
		 */
		public DJSoundTeamButton(URL url2, String name, int Category) {
			BorderPane borderPane = new BorderPane();

			// Αρχικοποίση μεταβλητών
			url = url2;
			category = Category;
			try {
				duration = InfoTool.durationInMilliseconds(url.toURI().getPath(), TYPE.FILE);
				// new Alert(AlertType.INFORMATION,""+duration).showAndWait();
			} catch (URISyntaxException e) {
				new Alert(AlertType.INFORMATION, e.getMessage()).showAndWait();
				e.printStackTrace();
			}

			// radioButton
			// radioButton.setPadding(new Insets(5,10,5,10));
			borderPane.setLeft(radioButton);

			// play
			play.setText(name);
			play.setPrefWidth(170);
			// play.setMaxWidth(Double.MAX_VALUE);
			play.setTooltip(new Tooltip((category == 1) ? "Key_4 or Key_9" : "Key_3 or Key_8"));
			borderPane.setCenter(play);

			// progressBar
			progressBar.setStyle("-fx-accent:orange;");
			progressBar.progressProperty().bind(timeService.progressProperty());
			progressBar.visibleProperty().bind(timeService.runningProperty());
			region.setStyle("-fx-background-color:rgb(0,0,0,0.7); -fx-background-radius:15px;");
			region.visibleProperty().bind(timeService.runningProperty());

			getChildren().addAll(borderPane, region, progressBar);

			play.setOnMouseReleased(this::onMouseReleased);
			play.setOnMousePressed(this::onMousePressed);
			radioButton.setOnMouseReleased(this::onMouseReleased);
		}

		/**
		 * Επιστρέφει το radioButton.
		 *
		 * @return the radio button
		 */
		public RadioButton getRadioButton() {
			return radioButton;
		}

		/**
		 * Gets the url.
		 *
		 * @return the url
		 */
		public URL getURL() {
			return url;
		}

		/**
		 * Start player.
		 */
		public void startPlayer() {
			System.out.println("Entered startPlayer djButton line 87");
			if (djMode.djTabs.getTeam(category).player.isStopped() || djMode.djTabs.getTeam(category).player.isOpened()
					|| djMode.djTabs.getTeam(category).player.isUnknown()) {

				try {
					timeService.startService();
					djMode.djTabs.getTeam(category).player.open(url);
					djMode.djTabs.getTeam(category).player.play();
					djMode.djTabs.getTeam(category).player
							.setGain((double) (djMode.djTabs.getTeam(category).slider.getVolume() / 100.00));

				} catch (StreamPlayerException e1) {
					e1.printStackTrace();
				}

			}
		}

		/**
		 * Stop player.
		 */
		public void stopPlayer() {

			if (djMode.djTabs.getTeam(category).player.isPlaying()) {
				timeService.stopService();
				djMode.djTabs.getTeam(category).player.stop();
			}

		}

		/**
		 * Controll player.
		 */
		public void controllPlayer() {
			if (djMode.djTabs.getTeam(category).player.isPlaying())
				stopPlayer();
			else
				startPlayer();
		}

		// TODO mouseHandlers

		/**
		 * On mouse pressed.
		 *
		 * @param m the m
		 */
		// Pressed
		public void onMousePressed(MouseEvent m) {
			if (m.getSource() == play && m.getButton() == MouseButton.PRIMARY) {
				startPlayer();
			}
		}

		/**
		 * On mouse released.
		 *
		 * @param m the m
		 */
		// Released
		public void onMouseReleased(MouseEvent m) {
			if (m.getSource() == play && m.getButton() == MouseButton.PRIMARY)
				stopPlayer();
			else if (m.getSource() == radioButton)
				djMode.djTabs.getTeam(category).url = url;

		}

		/**
		 * The Class TimeService.
		 *
		 * @author SuperGoliath
		 */
		public class TimeService extends Service<Void> {

			/**
			 * Starts the Service.
			 */
			public void startService() {
				if (!isRunning()) {
					reset();
					start();
				}
			}

			/**
			 * Stops the service.
			 */
			public void stopService() {
				if (isRunning())
					cancel();
			}

			/* (non-Javadoc)
			 * @see javafx.concurrent.Service#createTask()
			 */
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						try {
							for (int i = 1; i <= duration; i++) {
								updateProgress((double) i / duration, 1);
								if (i % 100 == 0)
									Thread.sleep(104);
							}
						} catch (InterruptedException c) {
							c.printStackTrace();
						}
						return null;
					}
				};

			}

		}

	}

}
