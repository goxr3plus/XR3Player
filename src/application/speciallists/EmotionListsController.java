package application.speciallists;

import application.Main;
import application.windows.EmotionsWindow.Emotion;
import javafx.application.Platform;
import smartcontroller.Genre;
import smartcontroller.SmartController;

public class EmotionListsController {
	
	public final HatedSongsList hatedMediaList;
	public final DislikedSongsList dislikedMediaList;
	public final LikedSongsList likedMediaList;
	public final LovedSongsList lovedMediaList;
	
	public final SmartController hatedMediaListController;
	public final SmartController dislikedMediaListController;
	public final SmartController likedMediaListController;
	public final SmartController lovedMediaListController;
	
	/**
	 * Constructor
	 */
	public EmotionListsController() {
		
		//Lists
		hatedMediaList = new HatedSongsList();
		dislikedMediaList = new DislikedSongsList();
		likedMediaList = new LikedSongsList();
		lovedMediaList = new LovedSongsList();
		
		//SmartControllers
		hatedMediaListController = new SmartController(Genre.EMOTIONSMEDIA, "HatedMediaPlayList", hatedMediaList.getDatabaseTableName());
		dislikedMediaListController = new SmartController(Genre.EMOTIONSMEDIA, "DislikedMediaPlayList", dislikedMediaList.getDatabaseTableName());
		likedMediaListController = new SmartController(Genre.EMOTIONSMEDIA, "LikedMediaPlayList", likedMediaList.getDatabaseTableName());
		lovedMediaListController = new SmartController(Genre.EMOTIONSMEDIA, "LovedMediaPlayList", lovedMediaList.getDatabaseTableName());
	}
	
	/**
	 * This method accepts a song path and based on the emotion the user expressed for it , the song will be added on one of the EmotionsList (for
	 * example LikedSongs) and will automatically determine if it needs to be removed from some other emotion list that it was .
	 * 
	 * <br>
	 * For example if it was on liked songs and the user disliked it will go on the disliked songs list etc.
	 * 
	 * @param songPath
	 * @param emotion
	 */
	public void makeEmotionDecisition(String songPath , Emotion emotion) {
		boolean[] updateEmotion = new boolean[4];
		
		if (emotion == Emotion.HATE) {
			
			updateEmotion[0] = hatedMediaList.addIfNotExists(songPath, false);
			updateEmotion[1] = dislikedMediaList.remove(songPath, false);
			updateEmotion[2] = likedMediaList.remove(songPath, false);
			updateEmotion[3] = lovedMediaList.remove(songPath, false);
			
		} else if (emotion == Emotion.DISLIKE) {
			
			updateEmotion[0] = hatedMediaList.remove(songPath, false);
			updateEmotion[1] = dislikedMediaList.addIfNotExists(songPath, false);
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
			updateEmotion[2] = likedMediaList.addIfNotExists(songPath, false);
			updateEmotion[3] = lovedMediaList.remove(songPath, false);
			
		} else if (emotion == Emotion.LOVE) {
			
			updateEmotion[0] = hatedMediaList.remove(songPath, false);
			updateEmotion[1] = dislikedMediaList.remove(songPath, false);
			updateEmotion[2] = likedMediaList.remove(songPath, false);
			updateEmotion[3] = lovedMediaList.addIfNotExists(songPath, false);
			
		}
		
		//Update all the SmartControllers
		Platform.runLater(() -> updateEmotionSmartControllers(updateEmotion[0], updateEmotion[1], updateEmotion[2], updateEmotion[3]));
		
		//Commit to the Database
		Main.dbManager.commit();
	}
	
	/**
	 * Update the Emotion SmartControllers based on the boolean given for each Emotion SmartController
	 * 
	 * @param updateHated
	 * @param updateDisliked
	 * @param updateLiked
	 * @param updateLoved
	 */
	public void updateEmotionSmartControllers(boolean updateHated , boolean updateDisliked , boolean updateLiked , boolean updateLoved) {
		
		if (updateHated)
			hatedMediaListController.getLoadService().startService(false, false, true);
		if (updateDisliked)
			dislikedMediaListController.getLoadService().startService(false, false, true);
		if (updateLiked)
			likedMediaListController.getLoadService().startService(false, false, true);
		if (updateLoved)
			lovedMediaListController.getLoadService().startService(false, false, true);
	}
	
	/**
	 * Checks if the Media is contained in any of the emotion lists , if not it's emotion is neutral by default
	 * 
	 * @param mediaPath
	 */
	public Emotion getEmotionForMedia(String mediaPath) {
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
