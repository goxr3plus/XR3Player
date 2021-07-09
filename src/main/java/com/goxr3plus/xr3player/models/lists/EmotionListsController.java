package com.goxr3plus.xr3player.models.lists;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.goxr3plus.streamplayer.stream.ThreadFactoryWithNamePrefix;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.smartcontroller.SmartController;
import com.goxr3plus.xr3player.controllers.windows.EmotionsWindow.Emotion;
import com.goxr3plus.xr3player.enums.Genre;

import javafx.application.Platform;

public class EmotionListsController {

	public final DatabaseList hatedMediaList;
	public final DatabaseList dislikedMediaList;
	public final DatabaseList likedMediaList;
	public final DatabaseList lovedMediaList;

	public final SmartController hatedMediaListController;
	public final SmartController dislikedMediaListController;
	public final SmartController likedMediaListController;
	public final SmartController lovedMediaListController;

	/**
	 * This executor service is used in order the emotion update events to be
	 * executed in an order
	 */
	private final ExecutorService emotionsUpdaterExecutorService;

	/**
	 * Constructor
	 */
	public EmotionListsController() {

		// emotionsUpdaterExecutorService
		emotionsUpdaterExecutorService = Executors
				.newSingleThreadExecutor(new ThreadFactoryWithNamePrefix("EmotionsUpdaterService-"));

		// Lists
		hatedMediaList = DatabaseListFactory.hated();
		dislikedMediaList = DatabaseListFactory.disliked();
		likedMediaList = DatabaseListFactory.liked();
		lovedMediaList = DatabaseListFactory.loved();

		// SmartControllers
		hatedMediaListController = new SmartController(Genre.EMOTIONSMEDIA, "HatedMediaPlayList",
				hatedMediaList.getDatabaseTableName());
		dislikedMediaListController = new SmartController(Genre.EMOTIONSMEDIA, "DislikedMediaPlayList",
				dislikedMediaList.getDatabaseTableName());
		likedMediaListController = new SmartController(Genre.EMOTIONSMEDIA, "LikedMediaPlayList",
				likedMediaList.getDatabaseTableName());
		lovedMediaListController = new SmartController(Genre.EMOTIONSMEDIA, "LovedMediaPlayList",
				lovedMediaList.getDatabaseTableName());

		// Bidirectional binding with Instant Search
		hatedMediaListController.getInstantSearch().selectedProperty().bindBidirectional(
				Main.settingsWindow.getPlayListsSettingsController().getInstantSearch().selectedProperty());
		dislikedMediaListController.getInstantSearch().selectedProperty().bindBidirectional(
				Main.settingsWindow.getPlayListsSettingsController().getInstantSearch().selectedProperty());
		likedMediaListController.getInstantSearch().selectedProperty().bindBidirectional(
				Main.settingsWindow.getPlayListsSettingsController().getInstantSearch().selectedProperty());
		lovedMediaListController.getInstantSearch().selectedProperty().bindBidirectional(
				Main.settingsWindow.getPlayListsSettingsController().getInstantSearch().selectedProperty());
	}

	/**
	 * This method accepts a song path and based on the emotion the user expressed
	 * for it , the song will be added on one of the EmotionsList (for example
	 * LikedSongs) and will automatically determine if it needs to be removed from
	 * some other emotion list that it was .
	 * 
	 * <br>
	 * For example if it was on liked songs and the user disliked it will go on the
	 * disliked songs list etc.
	 * 
	 * @param songPath
	 * @param emotion
	 */
	public void makeEmotionDecisition(final String songPath, final Emotion emotion) {
		final boolean[] updateEmotion = new boolean[4];

		// Submit
		emotionsUpdaterExecutorService.submit(() -> {
			try {

				if (emotion == Emotion.HATE) {

					updateEmotion[0] = hatedMediaList.add(songPath, false);
					updateEmotion[1] = dislikedMediaList.remove(songPath, false);
					updateEmotion[2] = likedMediaList.remove(songPath, false);
					updateEmotion[3] = lovedMediaList.remove(songPath, false);

				} else if (emotion == Emotion.DISLIKE) {

					updateEmotion[0] = hatedMediaList.remove(songPath, false);
					updateEmotion[1] = dislikedMediaList.add(songPath, false);
					updateEmotion[2] = likedMediaList.remove(songPath, false);
					updateEmotion[3] = lovedMediaList.remove(songPath, false);

				} else if (emotion == Emotion.NEUTRAL) {

					updateEmotion[0] = hatedMediaList.remove(songPath, false);
					updateEmotion[1] = dislikedMediaList.remove(songPath, false);
					updateEmotion[2] = likedMediaList.remove(songPath, false);
					updateEmotion[3] = lovedMediaList.remove(songPath, false);

				} else if (emotion == Emotion.LIKE) {

					updateEmotion[0] = hatedMediaList.remove(songPath, false);
					updateEmotion[1] = dislikedMediaList.remove(songPath, false);
					updateEmotion[2] = likedMediaList.add(songPath, false);
					updateEmotion[3] = lovedMediaList.remove(songPath, false);

				} else if (emotion == Emotion.LOVE) {

					updateEmotion[0] = hatedMediaList.remove(songPath, false);
					updateEmotion[1] = dislikedMediaList.remove(songPath, false);
					updateEmotion[2] = likedMediaList.remove(songPath, false);
					updateEmotion[3] = lovedMediaList.add(songPath, false);

				}

				// Update in a very smart way
				Platform.runLater(() -> updateSelectedSmartController(updateEmotion));

				// Commit to the Database
				Main.dbManager.commit();

			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		});

	}

	/**
	 * Updates the Selected SmartController in a Smart Way
	 */
	public void updateSelectedSmartController(final boolean[] updateEmotion) {

		// Show ReloadVBox for every SmartController
		if (updateEmotion[0])
			hatedMediaListController.getReloadVBox().setVisible(true);
		if (updateEmotion[1])
			dislikedMediaListController.getReloadVBox().setVisible(true);
		if (updateEmotion[2])
			likedMediaListController.getReloadVBox().setVisible(true);
		if (updateEmotion[3])
			lovedMediaListController.getReloadVBox().setVisible(true);

		// If the emotions Tab Pane is Selected we need an instant refresh
		if (Main.playListModesTabPane.getEmotionListsTab().isSelected())
			((SmartController) Main.emotionsTabPane.getTabPane().getSelectionModel().getSelectedItem().getContent())
					.getLoadService().startService(false, false, true);

	}

	/**
	 * Checks if the Media is contained in any of the emotion lists , if not it's
	 * emotion is neutral by default
	 * 
	 * @param mediaPath
	 */
	public Emotion getEmotionForMedia(final String mediaPath) {
		if (hatedMediaList.containsFile(mediaPath))
			return Emotion.HATE;
		if (dislikedMediaList.containsFile(mediaPath))
			return Emotion.DISLIKE;
		else if (likedMediaList.containsFile(mediaPath))
			return Emotion.LIKE;
		else if (lovedMediaList.containsFile(mediaPath))
			return Emotion.LOVE;
		else
			return Emotion.NEUTRAL;
	}

}
