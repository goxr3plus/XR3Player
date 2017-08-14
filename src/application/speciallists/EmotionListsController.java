package application.speciallists;

import application.windows.EmotionsWindow.Emotion;

public class EmotionListsController {
	
	private final DislikedSongsList dislikedSongsList;
	private final LikedSongsList likedSongsList;
	
	/**
	 * Constructor
	 */
	public EmotionListsController() {
		dislikedSongsList = new DislikedSongsList();
		likedSongsList = new LikedSongsList();
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
		if (emotion == Emotion.DISLIKE) {
			
			dislikedSongsList.addIfNotExists(songPath);
			likedSongsList.remove(songPath);
			
		} else if (emotion == Emotion.NEUTRAL) {
			
			dislikedSongsList.remove(songPath);
			likedSongsList.remove(songPath);
			
		} else if (emotion == Emotion.LIKE) {
			
			likedSongsList.addIfNotExists(songPath);
			dislikedSongsList.remove(songPath);
			
		}
	}
	
	/**
	 * Checks if the Media is contained in any of the emotion lists , if not it's emotion is neutral by default
	 * 
	 * @param mediaPath
	 */
	public Emotion getEmotionForMedia(String mediaPath) {
		if (dislikedSongsList.containsFile(mediaPath))
			return Emotion.DISLIKE;
		else if (likedSongsList.containsFile(mediaPath))
			return Emotion.LIKE;
		else
			return Emotion.NEUTRAL;
	}
	
	/**
	 * @return the dislikedSongsList
	 */
	public DislikedSongsList getDislikedSongsList() {
		return dislikedSongsList;
	}
	
	/**
	 * @return the likedSongsList
	 */
	public LikedSongsList getLikedSongsList() {
		return likedSongsList;
	}
	
}
