package main.java.com.goxr3plus.xr3player.utils.io;

import main.java.com.goxr3plus.xr3player.application.enums.FileLinkType;

/**
 * Holds FileType and AbsolutePath
 * 
 * @author GOXR3PLUSSTUDIO
 *
 */
public class FileTypeAndAbsolutePath {
	private FileLinkType fileType;
	private String fileAbsolutePath;

	public FileTypeAndAbsolutePath(FileLinkType fileType, String fileAbsolutePath) {
		this.fileType = fileType;
		this.fileAbsolutePath = fileAbsolutePath;
	}

	/**
	 * @return the fileAbsolutePath
	 */
	public String getFileAbsolutePath() {
		return fileAbsolutePath;
	}

	/**
	 * 
	 * 
	 * /**
	 * 
	 * @return the fileType
	 */
	public FileLinkType getFileType() {
		return fileType;
	}

}