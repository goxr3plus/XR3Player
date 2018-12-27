package main.java.com.goxr3plus.xr3player.models.lists;

import main.java.com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * Just to keep instances of Media for PlayedMediaList
 * 
 * @author GOXR3PLUSSTUDIO
 *
 */
public class FakeMedia {
	
	private String path;
	private String fileName;
	private double stars;
	private int timesPlayed;
	
	public FakeMedia(String path, double stars, int timesPlayed) {
		setPath(path);
		setFileName(InfoTool.getFileName(path));
		setStars(stars);
		setTimesPlayed(timesPlayed);
	}
	
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * @return the stars
	 */
	public double getStars() {
		return stars;
	}
	
	/**
	 * @param stars
	 *            the stars to set
	 */
	public void setStars(double stars) {
		this.stars = stars;
	}
	
	/**
	 * @return the timesPlayed
	 */
	public int getTimesPlayed() {
		return timesPlayed;
	}
	
	/**
	 * @param timesPlayed
	 *            the timesPlayed to set
	 */
	public void setTimesPlayed(int timesPlayed) {
		this.timesPlayed = timesPlayed;
	}
	
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
