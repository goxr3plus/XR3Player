package main.java.com.goxr3plus.xr3player.utils.general;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.java.com.goxr3plus.xr3player.utils.io.IOInfo;

public final class ExtensionTool {

	// Java 7 Way and back
	static final Set<String> ACCEPTED_AUDIO_EXTENSIONS = new HashSet<>(Arrays.asList("mp3"));
	static final Set<String> ACCEPTED_VIDEO_EXTENSIONS = new HashSet<>(Arrays.asList("mp4"));
	static final Set<String> ACCEPTED_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList("png", "jpg", "jpeg", "gif"));
	public static final List<String> POPULAR_AUDIO_EXTENSIONS_LIST = Arrays.asList("mp3", "wav", "ogg", "opus", "aac",
			"flac", "aiff", "au", "speex", "webm", "wma", "amr", "ape", "awb", "dct", "dss", "dvf", "aa", "aax", "act",
			"m4a", "m4b", "m4p", "mpc", "msv", "oga", "mogg", "raw", "tta", "aifc", "ac3", "spx");
	public static final List<String> POPULAR_VIDEO_EXTENSIONS_LIST = Arrays.asList("mp4", "flv", "avi", "wmv", "mov",
			"3gp", "webm", "mkv", "vob", "yuv", "m4v", "svi", "3g2", "f4v", "f4p", "f4a", "f4b", "swf");
	public static final Set<String> POPULAR_AUDIO_EXTENSIONS = new HashSet<>(POPULAR_AUDIO_EXTENSIONS_LIST);
	static final Set<String> POPULAR_VIDEO_EXTENSIONS = new HashSet<>(POPULAR_VIDEO_EXTENSIONS_LIST);
	static final Set<String> POPULAR_IMAGE_EXTENSIONS = new HashSet<>(
			Arrays.asList("png", "jpg", "jpeg", "gif", "bmp", "exif", "tiff", "webp", "heif", "bat", "bpg", "svg"));
	static final Set<String> POPULAR_ZIP_EXTENSIONS = new HashSet<>(
			Arrays.asList("zip", "7z", "rar", "zipx", "bz2", "gz"));

	private ExtensionTool() {
	}

	// ------------------------------------------------------------------------------------------------------

	/**
	 * 1)Checks if this file is <b>audio</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param fileName The File Name
	 * @return True if the type is supported or else False
	 */
	public static boolean isAudioSupported(final String fileName) {
		final String extension = IOInfo.getFileExtension(fileName);
		return extension != null && ACCEPTED_AUDIO_EXTENSIONS.contains(extension);
	}

	/**
	 * 1)Checks if this file is <b>video</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param fileName The File Name
	 * @return True if the type is supported or else False
	 */
	public static boolean isVideoSupported(final String fileName) {
		final String extension = IOInfo.getFileExtension(fileName);
		return extension != null && ACCEPTED_VIDEO_EXTENSIONS.contains(extension);
	}

	/**
	 * 1)Checks if this file is <b>image</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param fileName The File Name
	 * @return True if the type is supported or else False
	 */
	public static boolean isImageSupported(final String fileName) {
		final String extension = IOInfo.getFileExtension(fileName);
		return extension != null && ExtensionTool.ACCEPTED_IMAGE_EXTENSIONS.contains(extension);
	}

	// ------------------------------------------------------------------------------------------------------

	/**
	 * 1)Checks if this file is <b>Audio</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param fileName The File Name
	 * @return True if the file is an Audio else false
	 */
	public static boolean isAudio(final String fileName) {
		final String extension = IOInfo.getFileExtension(fileName);
		return extension != null && POPULAR_AUDIO_EXTENSIONS.contains(extension);
	}

	/**
	 * 1)Checks if this file is <b>Audio</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param extension File extension
	 * @return True if the file is an Audio else false
	 */
	public static boolean isAudioCheckExtension(final String extension) {
		return extension != null && POPULAR_AUDIO_EXTENSIONS.contains(extension);
	}

	/**
	 * 1)Checks if this file is <b>Video</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param fileName The File Name
	 * @return True if the file is an Video else false
	 */
	public static boolean isVideo(final String fileName) {
		final String extension = IOInfo.getFileExtension(fileName);
		return extension != null && POPULAR_VIDEO_EXTENSIONS.contains(extension);
	}

	/**
	 * 1)Checks if this file is <b>Video</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param extension File extension
	 * @return True if the file is an Video else false
	 */
	public static boolean isVideoCheckExtension(final String extension) {
		return extension != null && POPULAR_VIDEO_EXTENSIONS.contains(extension);
	}

	/**
	 * 1)Checks if this file is <b>Image</b><br>
	 * 
	 * @param fileName The File Name
	 * @return True if the file is an Image else false
	 */
	public static boolean isImage(final String fileName) {
		final String extension = IOInfo.getFileExtension(fileName);
		return extension != null && POPULAR_IMAGE_EXTENSIONS.contains(extension);
	}

	/**
	 * 1)Checks if this file is <b>Image</b><br>
	 * 
	 * @param extension File extension
	 * @return True if the file is an Image else false
	 */
	public static boolean isImageCheckExtension(final String extension) {
		return extension != null && POPULAR_IMAGE_EXTENSIONS.contains(extension);
	}

	/**
	 * 1)Checks if this file is <b>PDF</b><br>
	 * 
	 * @param fileName The File Name
	 * @return True if the file is an PDF else false
	 */
	public static boolean isPdf(final String fileName) {
		final String extension = IOInfo.getFileExtension(fileName);
		return extension != null && "pdf".equals(extension);
	}

	/**
	 * 1)Checks if this file is <b>PDF</b><br>
	 * 
	 * @param extension File extension
	 * @return True if the file is an PDF else false
	 */
	public static boolean isPdfCheckExtension(final String extension) {
		return extension != null && "pdf".equals(extension);
	}

	/**
	 * 1)Checks if this file is <b>ZIP</b><br>
	 * 
	 * @param fileName The File Name
	 * @return True if the file is an ZIP else false
	 */
	public static boolean isZip(final String fileName) {
		final String extension = IOInfo.getFileExtension(fileName);
		return extension != null && POPULAR_ZIP_EXTENSIONS.contains(extension);
	}

	/**
	 * 1)Checks if this file is <b>ZIP</b><br>
	 * 
	 * @param extension File extension
	 * @return True if the file is an ZIP else false
	 */
	public static boolean isZipCheckExtension(final String extension) {
		return extension != null && POPULAR_ZIP_EXTENSIONS.contains(extension);
	}

	// ------------------------------------------------------------------------------------------------------

}
