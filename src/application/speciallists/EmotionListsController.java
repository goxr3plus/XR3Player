package application.speciallists;

import application.windows.EmotionsWindow.Emotion;

public class EmotionListsController {
	
	private final HatedSongsList hatedSongsList;
	private final DislikedSongsList dislikedSongsList;
	private final LikedSongsList likedSongsList;
	private final LovedSongsList lovedSongsList;
	
	/**
	 * Constructor
	 */
	public EmotionListsController() {
		hatedSongsList = new HatedSongsList();
		dislikedSongsList = new DislikedSongsList();
		likedSongsList = new LikedSongsList();
		lovedSongsList = new LovedSongsList();
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
		if (emotion == Emotion.HATE) {
			
			hatedSongsList.addIfNotExists(songPath);
			dislikedSongsList.remove(songPath);
			likedSongsList.remove(songPath);
			lovedSongsList.remove(songPath);
			
		} else if (emotion == Emotion.DISLIKE) {
			
			hatedSongsList.remove(songPath);
			dislikedSongsList.addIfNotExists(songPath);
			likedSongsList.remove(songPath);
			lovedSongsList.remove(songPath);
			
		} else if (emotion == Emotion.NEUTRAL) {
			
			hatedSongsList.remove(songPath);
			dislikedSongsList.remove(songPath);
			likedSongsList.remove(songPath);
			lovedSongsList.remove(songPath);
			
		} else if (emotion == Emotion.LIKE) {
			
			hatedSongsList.remove(songPath);
			dislikedSongsList.remove(songPath);
			likedSongsList.addIfNotExists(songPath);
			lovedSongsList.remove(songPath);
			
		} else if (emotion == Emotion.LOVE) {
			
			hatedSongsList.remove(songPath);
			dislikedSongsList.remove(songPath);
			likedSongsList.remove(songPath);
			lovedSongsList.addIfNotExists(songPath);
			
		}
	}
	
	/**
	 * Checks if the Media is contained in any of the emotion lists , if not it's emotion is neutral by default
	 * 
	 * @param mediaPath
	 */
	public Emotion getEmotionForMedia(String mediaPath) {
		if (hatedSongsList.containsFile(mediaPath))
			return Emotion.HATE;
		if (dislikedSongsList.containsFile(mediaPath))
			return Emotion.DISLIKE;
		else if (likedSongsList.containsFile(mediaPath))
			return Emotion.LIKE;
		else if (lovedSongsList.containsFile(mediaPath))
			return Emotion.LOVE;
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
	
	/**
	 * @return the hatedSongsList
	 */
	public HatedSongsList getHatedSongsList() {
		return hatedSongsList;
	}
	
	/**
	 * @return the lovedSongsList
	 */
	public LovedSongsList getLovedSongsList() {
		return lovedSongsList;
	}
	
}
