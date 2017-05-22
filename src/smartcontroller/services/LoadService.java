package smartcontroller.services;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import application.Main;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import smartcontroller.Genre;
import smartcontroller.SmartController;
import smartcontroller.media.Audio;
import smartcontroller.media.Media;

public class LoadService extends Service<Void> {
		
		/** The commit. */
		private boolean commit;
		
		/** The request focus. */
		private boolean requestFocus;
		
		private final SmartController smartController;
		
		/**
		 * Constructor.
		 */
		public LoadService(SmartController smartController) {
			this.smartController = smartController;
			
			setOnSucceeded(s -> done());
			setOnFailed(f -> done());
			setOnCancelled(c -> done());
		}
		
		/**
		 * Stars the reload Service.
		 *
		 * @param commit1
		 *        the commit
		 * @param requestFocus1
		 *        the request focus
		 */
		public void startService(boolean commit1 , boolean requestFocus1) {
			if (isRunning() || !smartController.isFree(false))
				return;
			
			// Variables
			this.requestFocus = requestFocus1;
			this.commit = commit1;
			
			// Hide ContextMenu
			Main.songsContextMenu.hide();
			
			// Start
			try {
				
				// Search + Trick for genre.SearchWindow
				if (smartController.getSearchService().isActive() || smartController.getGenre() == Genre.SEARCHWINDOW)
					smartController.getSearchService().getService().search();
				// Reload
				else {
					smartController.updateWorking = true;
					smartController.getRegion().visibleProperty().bind(runningProperty());
					smartController.getIndicator().progressProperty().bind(progressProperty());
					smartController.getCancelButton().setText("Updating...");
					smartController.getInformationTextArea().setText("\n Updating the playlist....");
					smartController.getItemsObservableList().clear();
					super.reset();
					super.start();
				}
				
			} catch (Exception ex) {
				Main.logger.log(Level.WARNING, "", ex);
			}
		}
		
		/**
		 * Stars the reload Service.
		 *
		 * @param commit1
		 *        the commit
		 * @param requestFocus1
		 *        the request focus
		 * @param rememberScrollPosition
		 *        Remembers the scroll position of the playlist
		 */
		public void startService(boolean commit1 , boolean requestFocus1 , boolean rememberScrollPosition) {
			
			//Remember the scroll position
			if (rememberScrollPosition) {
				// Search is activated?
				if (smartController.getSearchService().isActive() || smartController.getGenre() == Genre.SEARCHWINDOW) {
					// setVerticalScrollValueWithSearch(getVerticalScrollBar().getValue());
				} else
					smartController.getVerticalScrollBar().ifPresent(scrollBar -> smartController.setVerticalScrollValueWithoutSearch(scrollBar.getValue()));
			}
			
			startService(commit1, requestFocus1);
		}
		
		/**
		 * Done.
		 */
		// Work done
		public void done() {
			commit = false;
			smartController.updateList();
			smartController.unbind();
			smartController.updateWorking = false;
			if (requestFocus)
				smartController.getCenterStackPane().requestFocus();
			
			try {
				//Fix the vertical scroll bar position
				if (smartController.getSearchService().isActive() || smartController.getGenre() == Genre.SEARCHWINDOW) {
					System.out.println("Search is active");
					smartController.getVerticalScrollBar().ifPresent(scrollBar -> scrollBar.setValue(smartController.getVerticalScrollValueWithSearch()));
					smartController.setVerticalScrollValueWithSearch(0.0);
				} else {
					smartController.getVerticalScrollBar().ifPresent(scrollBar -> scrollBar.setValue(smartController.getVerticalScrollValueWithoutSearch()));
					smartController.setVerticalScrollValueWithoutSearch(0.0);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
		
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				
				@Override
				protected Void call() throws Exception {
					
					// counter
					int counter = 0;
					
					// // calculate the total entries
					smartController.calculateTotalEntries();
					
					// when the final list is deleted then the controller
					// has to go to the previous list automatically
					if (smartController.getTotalInDataBase() != 0 && smartController.currentPageProperty().get() > smartController.getMaximumList())
						smartController.currentPageProperty().set(smartController.currentPageProperty().get() - 1);
					
					// Select the available Media Files
					String query = "SELECT* FROM '" + smartController.getDataBaseTableName() + "' LIMIT " + smartController.getMaximumPerPage() + " OFFSET " + smartController.currentPageProperty().get() * smartController.getMaximumPerPage();
					try (ResultSet resultSet = Main.dbManager.getConnection().createStatement().executeQuery(query);
							ResultSet dbCounter = Main.dbManager.getConnection().createStatement().executeQuery(query)) {
						
						// Count how many items the result returned...
						int currentMaximumPerList = 0;
						while (dbCounter.next())
							++currentMaximumPerList;
						
						// Fetch the items from the database
						List<Media> array = new ArrayList<>();
						for (Audio song = null; resultSet.next();) {
							song = new Audio(resultSet.getString("PATH"), resultSet.getDouble("STARS"), resultSet.getInt("TIMESPLAYED"), resultSet.getString("DATE"),
									resultSet.getString("HOUR"), smartController.getGenre());
							array.add(song);
							//Update the progress
							updateProgress(++counter, currentMaximumPerList);
						}
						
						// Add the the items to the observable list
						CountDownLatch countDown = new CountDownLatch(1);
						Platform.runLater(() -> {
							smartController.getItemsObservableList().addAll(array);
							countDown.countDown();
						});
						countDown.await();
						//}
						
						// commit?
						if (commit)
							Main.dbManager.commit();
					} catch (Exception ex) {
						Main.logger.log(Level.WARNING, "", ex);
					}
					
					return null;
					
				}
			};
		}
	}