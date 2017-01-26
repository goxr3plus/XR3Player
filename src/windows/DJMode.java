/*
 * 
 */
package windows;

import java.io.IOException;

import application.Main;
import customnodes.DigitalClock;
import disc.Balancer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import tools.InfoTool;
import xplayer.presenter.XPlayerController;

/**
 * The DJMode.
 *
 * @author GOXR3PLUS
 */
public class DJMode extends GridPane {
	
	/** The split pane. */
	@FXML
	private SplitPane splitPane;
	
	/** The dj tabs. */
	//public DJTabs djTabs;
	
	/** The balancer. */
	public Balancer balancer;
	
	/** The digital clock. */
	public DigitalClock digitalClock;
	
	/** The divider. */
	// Variables
	private double[] divider = { 0.18 , 0.83 , 0.2 };
	
	/**
	 * Constructor.
	 */
	public DJMode() {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "DJMode.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	public void initialize() {
		Main.xPlayersList.addXPlayerUI(new XPlayerController(400, 0, 1));
		Main.xPlayersList.getXPlayerUI(1).makeTheDisc(136, 136, Color.rgb(53, 144, 255), 45, Side.RIGHT);
		Main.xPlayersList.getXPlayerUI(1).makeTheVisualizer(Side.LEFT);
		add(Main.xPlayersList.getXPlayerUI(1), 0, 0);
		
		Main.xPlayersList.addXPlayerUI(new XPlayerController(400, 0, 2));
		Main.xPlayersList.getXPlayerUI(2).makeTheDisc(136, 136, Color.RED, 45, Side.LEFT);
		Main.xPlayersList.getXPlayerUI(2).makeTheVisualizer(Side.RIGHT);
		add(Main.xPlayersList.getXPlayerUI(2), 1, 0);
		
		// splitPane
		splitPane.setStyle("-fx-background-color:transparent");
		
		// makeCpuUsageMeter()
		makeDigitalClock();
		makeDJSoundTeamTabs();
		makeBalancer();
		
		addKeyListeners();
	}
	
	/**
	 * Returns the splitPane of the DJMode.
	 *
	 * @return the split pane
	 */
	public SplitPane getSplitPane() {
		return splitPane;
	}
	
	/**
	 * Create CpuMeter.
	 */
	void makeCpuUsageMeter() {
		// cpu = new CPUsage(0, InfoTool.screenHeight - 420, 151, 15)
	}
	
	/**
	 * Κατασκευάζει το ψηφιακό ρολόι.
	 */
	void makeDigitalClock() {
		digitalClock = new DigitalClock(170, InfoTool.getScreenHeight() - 422, 80, 16);
	}
	
	/**
	 * Make DJ sound team tabs.
	 */
	void makeDJSoundTeamTabs() {
		//djTabs = new DJTabs(InfoTool.getScreenWidth() - 305, 400, 300, InfoTool.getScreenHeight() - 400);
	}
	
	/**
	 * Make balancer.
	 */
	void makeBalancer() {
		
		balancer = new Balancer(InfoTool.getScreenWidth() / 2 - 100, 260, 208, 20, 100, 200);
		balancer.setOnMouseDragged(drag -> {
			balancer.onMouseDragged(drag);
			Main.xPlayersList.getXPlayerUI(1).controlVolume();
		});
		
		balancer.setOnScroll(scroll -> {
			balancer.onScroll(scroll);
			Main.xPlayersList.getXPlayerUI(1).controlVolume();
		});
		
	}
	
	/**
	 * Adds the appropriate key listeners to the Parent.
	 */
	private final void addKeyListeners() {
		
		// TODO keyPressed
		setOnKeyPressed(key -> {
			
			KeyCode keyCode = key.getCode();
			
			// -->Xplayer_1||XPlayer_2 Volume++
			if (keyCode == KeyCode.W) {
				
				if (key.isShiftDown()) {
					if (Main.xPlayersList.getXPlayerUI(1).getVolume() < 101) {
						Main.xPlayersList.getXPlayerUI(1).setVolume(Main.xPlayersList.getXPlayerUI(1).getVolume() + 1);
					}
				} else if (key.isControlDown()) {
					if (Main.xPlayersList.getXPlayerUI(2).getVolume() < 101) {
						Main.xPlayersList.getXPlayerUI(2).setVolume(Main.xPlayersList.getXPlayerUI(2).getVolume() + 1);
					}
				}
				
				// -->Xplayer_1||XPlayer_2 Volume--
			} else if (keyCode == KeyCode.Q) {
				
				if (key.isShiftDown()) {
					if (Main.xPlayersList.getXPlayerUI(1).getVolume() > -1) {
						Main.xPlayersList.getXPlayerUI(1).setVolume(Main.xPlayersList.getXPlayerUI(1).getVolume() - 1);
					}
				} else if (key.isControlDown()) {
					if (Main.xPlayersList.getXPlayerUI(2).getVolume() > -1) {
						Main.xPlayersList.getXPlayerUI(2).setVolume(Main.xPlayersList.getXPlayerUI(2).getVolume() - 1);
					}
				}
			}
			
		});
		
		setOnKeyReleased(key -> {
			KeyCode keyCode = key.getCode();
			
			// Xplayer_1||Xplayer2.Resume
			if (keyCode == KeyCode.DIGIT1) {
				
				if (key.isShiftDown())
					Main.xPlayersList.getXPlayer(1).resume();
				else if (key.isControlDown())
					Main.xPlayersList.getXPlayer(2).resume();
				
				// Xplayer_1||Xplayer_2.Pause
			} else if (keyCode == KeyCode.DIGIT2) {
				
				if (key.isShiftDown())
					Main.xPlayersList.getXPlayer(1).pause();
				else if (key.isControlDown())
					Main.xPlayersList.getXPlayer(2).pause();
				
				// Xplayer_1||Xplayer_2.Stop
			} else if (keyCode == KeyCode.DIGIT3) {
				
				if (key.isShiftDown()) {
					if (Main.xPlayersList.getXPlayer(1).isPausedOrPlaying())
						Main.xPlayersList.getXPlayer(1).stop();
				} else if (key.isControlDown())
					if (Main.xPlayersList.getXPlayer(2).isPausedOrPlaying())
						Main.xPlayersList.getXPlayer(2).stop();
					
				// DJBeats
			}
//			 else if (keyCode == KeyCode.DIGIT4) {
//				
//				for (Node n : djTabs.djBeats.getChildren())
//					if ( ( (DJSoundTeamButton) n ).getRadioButton().isSelected()) {
//						
//						( (DJSoundTeamButton) n ).controllPlayer();
//						
//						break;
//					}
//				
//				// DJScratch
//			} else if (keyCode == KeyCode.DIGIT5) {
//				
//				for (Node n : djTabs.djScratches.getChildren())
//					if ( ( (DJSoundTeamButton) n ).getRadioButton().isSelected()) {
//						
//						( (DJSoundTeamButton) n ).controllPlayer();
//						
//						break;
//					}
//				
//			}
			
		});
		
	}
	
	/**
	 * Sets the position of the divider.
	 */
	public void setDividerPositions() {
		splitPane.setDividerPositions(divider);
	}
	
	/**
	 * Updates the array holding the divider positions.
	 */
	public void updateDividerArray() {
		divider = splitPane.getDividerPositions();
	}
	
}
