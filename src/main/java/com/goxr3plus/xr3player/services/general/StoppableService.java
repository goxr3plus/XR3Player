package main.java.com.goxr3plus.xr3player.services.general;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.utils.general.InfoTool;

public class StoppableService extends Service<Boolean> {
	
	public enum StoppableServiceCategory {
		INTERNET_CHECKER, TIMER_CHECKER;
	}
	
	private final StoppableServiceCategory category;
	
	/**
	 * Using this variable in order to avoid extra CPU or GPU usage by Internet connection checker thread
	 */
	private boolean internetPreviousStatus;
	
	/**
	 * Parameter needed for TIMER_CHECKER
	 */
	private int minutes = 0;
	
	//Just to update the image for the firstTime
	boolean firstHack = true;
	
	/**
	 * Constructor
	 */
	public StoppableService(StoppableServiceCategory category) {
		this.category = category;
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				
				//Title
				updateTitle(category + " Thread");
				
				try {
					
					//Check the category
					if (category == StoppableServiceCategory.INTERNET_CHECKER) { //INTERNET_CHECKER
						boolean[] newStatus = { false };
						
						//Run until Service is cancelled
						while (!isCancelled()) {
							//System.out.println(category + " Thread" + " Running...")
							
							newStatus[0] = InfoTool.isReachableByPing("www.google.com");
							
							//Try to connect
							if (newStatus[0] != internetPreviousStatus || firstHack)
								Platform.runLater(() -> {
									Main.bottomBar.getInternetConnectionLabel().setDisable(!newStatus[0]);
									Main.bottomBar.getInternetConnectionDescriptionLabel().setText(newStatus[0] ? "Connected" : "Disconnected");
								});
							
							internetPreviousStatus = newStatus[0];
							firstHack = false;
							
							//Sleep sometime [ Don't lag the CPU]
							try {
								Thread.sleep(2000);
							} catch (InterruptedException ex) {
								//ex.printStackTrace()
							}
							
						}
					} else if (category == StoppableServiceCategory.TIMER_CHECKER) { //TIMER_CHECKER
						String[] localTime = { "" };
						
						//Run until Service is cancelled
						while (!isCancelled()) {
							//System.out.println(category + " Thread" + " Running...")
							
							//Find local time
							localTime[0] = LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));
							
							//Run on JavaFX Thread
							Platform.runLater(() -> {
								Main.bottomBar.getCurrentTimeLabel().setText(localTime[0]);
								Main.bottomBar.getRunningTimeLabel().setText(minutes + ( minutes == 1 ? " minute" : " minutes" ));
							});
							
							//Sleep sometime [ Don't lag the CPU]
							try {
								Thread.sleep(60000);
								++minutes;
							} catch (InterruptedException exx) {
								//e.printStackTrace()
							}
							
						}
						
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return true;
			}
			
		};
	}
	
}
