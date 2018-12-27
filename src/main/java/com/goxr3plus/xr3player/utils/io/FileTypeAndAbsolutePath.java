package main.java.com.goxr3plus.xr3player.utils.io;
/**
	 * Holds FileType and AbsolutePath
	 * 
	 * @author GOXR3PLUSSTUDIO
	 *
	 */
	public class FileTypeAndAbsolutePath {
		private FileType fileType;
		private String fileAbsolutePath;
		
		public FileTypeAndAbsolutePath(FileType fileType, String fileAbsolutePath) {
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
		public FileType getFileType() {
			return fileType;
		}
		
	}