package main.java.com.goxr3plus.xr3player.application.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.FilenameUtils;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.AudioType;

/**
 * Provides useful methods for retrieving informations.
 *
 * @author GOXR3PLUS
 */
public final class InfoTool {
	
	/** The random. */
	public static final Random RANDOM = new Random();
	
	/** Logger */
	public static final Logger logger = Logger.getLogger(InfoTool.class.getName());
	
	/** WebSite url */
	public static final String WEBSITE_URL = "http://goxr3plus.co.nf";
	
	/** WebSite url */
	public static final String GITHUB_URL = "https://github.com/goxr3plus/XR3Player";
	
	/** XR3Player Tutorials */
	public static final String TUTORIALS = "https://www.youtube.com/playlist?list=PL-xqaiRUr_iRKDkpFWPfSRFmJvHSr1VJI";
	
	/** The Constant images. */
	public static final String IMAGES = "/image/";
	
	/** The Constant videos. */
	public static final String VIDEOS = "/video/";
	
	public static final String SOUNDS = "/sound/";
	
	/** The Constant styLes. */
	public static final String STYLES = "/style/";
	
	/** The Constant applicationCss. */
	public static final String APPLICATIONCSS = "application.css";
	
	/** The Constant sounds. */
	//public static final String sounds = "/sound/"
	
	/** The Constant fxmls. */
	public static final String FXMLS = "/fxml/";
	
	/** The Constant fxmls. */
	public static final String PLAYERS_FXMLS = "/fxml/players/";
	
	public static final String VISUALIZERS_FXMLS = "/fxml/visualizer/";
	
	public static final String BROWSER_FXMLS = "/fxml/browser/";
	
	public static final String SETTINGS_FXMLS = "/fxml/settings/";
	
	public static final String SMARTCONTROLLER_FXMLS = "/fxml/smartcontroller/";
	
	public static final String DROPBOX_FXMLS = "/fxml/dropbox/";
	
	public static final String LIBRARIES_FXMLS = "/fxml/library/";
	
	public static final String USER_FXMLS = "/fxml/user/";
	
	public static final String XR3CAPTURE_FXMLS = "/fxml/xr3capture/";
	
	/** The Constant radioStationsTable. */
	public static final String RADIO_STATIONS_DATABASE_TABLE_NAME = "RADIOSTATIONS";
	
	/** Database folder name <b>with out</b> separator [example:XR3DataBase] */
	private static final String DATABASE_FOLDER_NAME = "XR3DataBase";
	
	/** The name of the application user [example:Alexander] */
	private static String USERNAME;
	
	public static final String USER_SETTINGS_FILE_NAME = "config.properties";
	
	public static final String USER_INFORMATION_FILE_NAME = "userInformation.properties";
	
	//-----------Lists of accepted extensions
	//Java 8 Way
	//    private static final Set<String> ACCEPTED_AUDIO_EXTENSIONS = Stream.of("mp3", "wav", "ogg")
	//	    .collect(Collectors.toCollection(HashSet::new))
	//    private static final Set<String> ACCEPTED_VIDEO_EXTENSIONS = Stream.of("mp4", "flv")
	//	    .collect(Collectors.toCollection(HashSet::new))
	//    private static final Set<String> ACCEPTED_IMAGE_EXTENSIONS = Stream.of("png", "jpg", "jpeg")
	//	    .collect(Collectors.toCollection(HashSet::new))
	//Java 7 Way and back
	private static final Set<String> ACCEPTED_AUDIO_EXTENSIONS = new HashSet<>(Arrays.asList("mp3", "wav"));
	private static final Set<String> ACCEPTED_VIDEO_EXTENSIONS = new HashSet<>(Arrays.asList("mp4", "flv"));
	private static final Set<String> ACCEPTED_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList("png", "jpg", "jpeg", "gif"));
	
	public static final List<String> POPULAR_AUDIO_EXTENSIONS_LIST = Arrays.asList("mp3", "wav", "ogg", "opus", "aac", "flac", "aiff", "au", "speex", "webm", "wma", "amr", "ape",
			"awb", "dct", "dss", "dvf", "aa", "aax", "act", "m4a", "m4b", "m4p", "mpc", "msv", "oga", "mogg", "raw", "tta", "aifc", "ac3", "spx");
	
	public static final Set<String> POPULAR_AUDIO_EXTENSIONS = new HashSet<>(POPULAR_AUDIO_EXTENSIONS_LIST);
	private static final Set<String> POPULAR_VIDEO_EXTENSIONS = new HashSet<>(Arrays.asList("mp4", "flv", "avi", "wmv", "mov", "3gp"));
	private static final Set<String> POPULAR_IMAGE_EXTENSIONS = new HashSet<>(
			Arrays.asList("png", "jpg", "jpeg", "gif", "bmp", "exif", "tiff", "webp", "heif", "bat", "bpg", "svg"));
	private static final Set<String> POPULAR_ZIP_EXTENSIONS = new HashSet<>(Arrays.asList("zip", "7z", "rar", "zipx", "bz2", "gz"));
	// ------------------------------------Important-------------------------------------------------------------------
	
	/** The name of the application user [example:Alexander] */
	public static String getUserName() {
		return USERNAME;
	}
	
	/** The name of the application user [example:Alexander] */
	public static void setUserName(String uSERNAME) {
		USERNAME = uSERNAME;
	}
	
	//----
	
	public static String getImagesFolderAbsolutePathPlain() {
		return getUserFolderAbsolutePathWithSeparator() + "Images";
	}
	
	public static String getImagesFolderAbsolutePathWithSeparator() {
		return getImagesFolderAbsolutePathPlain() + File.separator;
	}
	
	//----
	
	public static String getXPlayersImageFolderAbsolutePathPlain() {
		return getImagesFolderAbsolutePathWithSeparator() + "XPlayersImages";
	}
	
	public static String getXPlayersImageFolderAbsolutePathWithSeparator() {
		return getImagesFolderAbsolutePathPlain() + File.separator;
	}
	
	//----
	
	/**
	 * @return Database folder name <b>with out</b> separator [example:XR3DataBase]
	 */
	public static String getDatabaseFolderName() {
		return DATABASE_FOLDER_NAME;
	}
	
	/** @return Database folder name with separator [example:XR3DataBase/] */
	public static String getDatabaseFolderNameWithSeparator() {
		return getDatabaseFolderName() + File.separator;
	}
	
	//----
	
	/**
	 * @return The current absolute path to the database <b>PARENT</b> folder with separator[example:C:/Users/]
	 */
	public static String getAbsoluteDatabaseParentFolderPathWithSeparator() {
		return InfoTool.getBasePathForClass(InfoTool.class);
	}
	
	/**
	 * @return The current absolute path to the database <b>PARENT</b> folder without separator[example:C:/Users]
	 */
	public static String getAbsoluteDatabaseParentFolderPathPlain() {
		String parentName = getAbsoluteDatabaseParentFolderPathWithSeparator();
		return parentName.substring(0, parentName.length() - 1);
	}
	
	//----
	
	/**
	 * @return The absolute path to the database folder<b>with out</b> separator [example:C:/Users/XR3DataBase]
	 */
	public static String getAbsoluteDatabasePathPlain() {
		return getAbsoluteDatabaseParentFolderPathWithSeparator() + getDatabaseFolderName();
	}
	
	/**
	 * @return The absolute database path with separator [example:C:/Users/XR3DataBase/]
	 */
	public static String getAbsoluteDatabasePathWithSeparator() {
		return getAbsoluteDatabasePathPlain() + File.separator;
	}
	
	//----
	
	public static String getUserFolderAbsolutePathPlain() {
		return getAbsoluteDatabasePathWithSeparator() + getUserName();
	}
	
	public static String getUserFolderAbsolutePathWithSeparator() {
		return getUserFolderAbsolutePathPlain() + File.separator;
	}
	
	//----
	
	/**
	 * @return XR3Database signature File , i am using this so the user can use any name for the exported xr3database zip and has not too worry
	 */
	public static File getDatabaseSignatureFile() {
		return new File(getAbsoluteDatabasePathWithSeparator() + "xr3Original.sig");
	}
	
	// --------------------------------------------------------------------------------------------------------------
	
	/**
	 * Private Constructor , we don't want instances of this class
	 */
	private InfoTool() {
	}
	
	/**
	 * Gets the screen width.
	 *
	 * @return The screen <b>Width</b> based on the <b> bounds </b> of the Screen.
	 */
	public static double getScreenWidth() {
		return Screen.getPrimary().getBounds().getWidth();
	}
	
	/**
	 * Gets the screen height.
	 *
	 * @return The screen <b>Height</b> based on the <b> bounds </b> of the Screen.
	 */
	public static double getScreenHeight() {
		return Screen.getPrimary().getBounds().getHeight();
	}
	
	/**
	 * Gets the visual screen width.
	 *
	 * @return The screen <b>Width</b> based on the <b>visual bounds</b> of the Screen.These bounds account for objects in the native windowing system
	 *         such as task bars and menu bars. These bounds are contained by Screen.bounds.
	 */
	public static double getVisualScreenWidth() {
		return Screen.getPrimary().getVisualBounds().getWidth();
	}
	
	/**
	 * Gets the visual screen height.
	 *
	 * @return The screen <b>Height</b> based on the <b>visual bounds</b> of the Screen.These bounds account for objects in the native windowing system
	 *         such as task bars and menu bars. These bounds are contained by Screen.bounds.
	 */
	public static double getVisualScreenHeight() {
		return Screen.getPrimary().getVisualBounds().getHeight();
	}
	
	/**
	 * Returns the absolute path of the current directory in which the given class file is.
	 * 
	 * @param classs
	 *            * @return The absolute path of the current directory in which the class file is. <b>[it ends with File.Separator!!]</b>
	 * @author GOXR3PLUS[StackOverFlow user] + bachden [StackOverFlow user]
	 */
	public static final String getBasePathForClass(Class<?> classs) {
		
		// Local variables
		File file;
		String basePath = "";
		boolean failed = false;
		
		// Let's give a first try
		try {
			file = new File(classs.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			
			basePath = ( file.isFile() || file.getPath().endsWith(".jar") || file.getPath().endsWith(".zip") ) ? file.getParent() : file.getPath();
		} catch (URISyntaxException ex) {
			failed = true;
			Logger.getLogger(classs.getName()).log(Level.WARNING, "Cannot firgue out base path for class with way (1): ", ex);
		}
		
		// The above failed?
		if (failed)
			try {
				file = new File(classs.getClassLoader().getResource("").toURI().getPath());
				basePath = file.getAbsolutePath();
				
				// the below is for testing purposes...
				// starts with File.separator?
				// String l = local.replaceFirst("[" + File.separator +
				// "/\\\\]", "")
			} catch (URISyntaxException ex) {
				Logger.getLogger(classs.getName()).log(Level.WARNING, "Cannot firgue out base path for class with way (2): ", ex);
			}
		
		// fix to run inside Eclipse
		if (basePath.endsWith(File.separator + "lib") || basePath.endsWith(File.separator + "bin") || basePath.endsWith("bin" + File.separator)
				|| basePath.endsWith("lib" + File.separator)) {
			basePath = basePath.substring(0, basePath.length() - 4);
		}
		// fix to run inside NetBeans
		if (basePath.endsWith(File.separator + "build" + File.separator + "classes")) {
			basePath = basePath.substring(0, basePath.length() - 14);
		}
		// end fix
		if (!basePath.endsWith(File.separator))
			basePath += File.separator;
		
		return basePath;
	}
	
	/**
	 * Checks if a web site is reachable using ping command.
	 *
	 * @param host
	 *            the host
	 * @return <b> true </b> if Connected on Internet,<b> false </b> if not.
	 */
	public static boolean isReachableByPing(String host) {
		try {
			
			// Start a new Process
			Process process = Runtime.getRuntime().exec("ping -" + ( System.getProperty("os.name").toLowerCase().startsWith("windows") ? "n" : "c" ) + " 1 " + host);
			
			//Wait for it to finish
			process.waitFor();
			
			//Check the return value
			return process.exitValue() == 0;
			
		} catch (Exception ex) {
			Logger.getLogger(Main.class.getName()).log(Level.INFO, null, ex);
			return false;
		}
	}
	
	/**
	 * Checks for Application Internet connection using Socket and InetSocketAddress I combine this method with reachableByPing to check if the Operating
	 * System is connected to the Internet and this method to check if the application can be connected to Internet,
	 * 
	 * Because the admin my have blocked internet connection for this Java application.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @return <b> true </b> if Connected on Internet,<b> false </b> if not.
	 */
	@Deprecated
	public static boolean isReachableUsingSocket(String host , int port) {
		InetSocketAddress addr = new InetSocketAddress(host, port);
		
		//Check if it can be connected
		try (Socket sock = new Socket()) {
			sock.connect(addr, 2000);
			return true;
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.INFO, null, ex);
			return false;
		}
	}
	
	//	public Image getDragViewImage(Image image,int width,int height) {
	//		if(image!=null) {
	//			return image;
	//		}else {
	//		
	//				WritableImage image = new WritableImage(100, 100);
	//				Canvas canvas = new Canvas();
	//				canvas.setWidth(100);
	//				canvas.setHeight(100);
	//				ActionTool.paintCanvas(canvas.getGraphicsContext2D(), getFileName(), 100, 100);
	//				SnapshotParameters params = new SnapshotParameters();
	//				params.setFill(Color.TRANSPARENT);
	//				return 
	//				canvas.snapshot(params, image), 50, 0);
	//			
	//		}
	//	}
	
	/**
	 * Return the imageView of mp3File in requested Width and Height.
	 *
	 * @param absolutePath
	 *            The File absolute path
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return an Image
	 */
	public static Image getAudioAlbumImage(String absolutePath , int width , int height) {
		ByteArrayInputStream arrayInputStream = getAudioAlbumImageRaw(absolutePath, width, height);
		
		//Does it contain an image
		if (arrayInputStream != null)
			return ( width == -1 && height == -1 ) ? new Image(arrayInputStream) : new Image(arrayInputStream, width, height, false, true);
		
		return null;
	}
	
	/**
	 * Return the imageView of mp3File in requested Width and Height.
	 *
	 * @param absolutePath
	 *            The File absolute path
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return ByteArrayInputStream containing the image as binary data
	 */
	public static ByteArrayInputStream getAudioAlbumImageRaw(String absolutePath , int width , int height) {
		//Is it mp3?
		if ("mp3".equals(getFileExtension(absolutePath)))
			try {
				Mp3File song = new Mp3File(absolutePath);
				
				if (song.hasId3v2Tag()) { // has id3v2 tag?
					
					ID3v2 id3v2Tag = song.getId3v2Tag();
					
					if (id3v2Tag.getAlbumImage() != null) // image?
						return new ByteArrayInputStream(id3v2Tag.getAlbumImage());
				}
			} catch (Exception ex) {
				//logger.log(Level.WARNING, "Can't get Album Image", ex);
			}
		
		return null;// fatal error here
	}
	
	/**
	 * Returns the creation time. The creation time is the time that the file was created.
	 *
	 * <p>
	 * If the file system implementation does not support a time stamp to indicate the time when the file was created then this method returns an
	 * implementation specific default value, typically the {@link #lastModifiedTime() last-modified-time} or a {@code FileTime} representing the epoch
	 * (1970-01-01T00:00:00Z).
	 *
	 * @param absolutePath
	 *            The File absolute path
	 * @return The File Creation Date in String Format
	 */
	public static String getFileCreationDate(String absolutePath) {
		File file = new File(absolutePath);
		//exists?
		if (!file.exists())
			return "file missing";
		
		BasicFileAttributes attr;
		try {
			attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
		} catch (IOException ex) {
			logger.log(Level.WARNING, ex.getMessage(), ex);
			return "error";
		}
		return ( attr.creationTime() + "" ).replaceAll("T|Z", " ");
	}
	
	/**
	 * Returns the time of last modification.
	 *
	 * <p>
	 * If the file system implementation does not support a time stamp to indicate the time of last modification then this method returns an
	 * implementation specific default value, typically a {@code FileTime} representing the epoch (1970-01-01T00:00:00Z).
	 *
	 * @param absolutePath
	 *            The File absolute path
	 * @return The File Creation Date in String Format
	 */
	public static String getFileLastModifiedDate(String absolutePath) {
		File file = new File(absolutePath);
		//exists?
		if (!file.exists())
			return "file missing";
		
		BasicFileAttributes attr;
		try {
			attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
		} catch (IOException ex) {
			logger.log(Level.WARNING, ex.getMessage(), ex);
			return "error";
		}
		return ( attr.lastModifiedTime() + "" ).replaceAll("T|Z", " ");
	}
	
	/**
	 * Returns the title of the file for example if file name is <b>(club.mp3)</b> it returns <b>(club)</b>
	 *
	 * @param absolutePath
	 *            The File absolute path
	 * @return the File title
	 */
	public static String getFileTitle(String absolutePath) {
		return FilenameUtils.getBaseName(absolutePath);
	}
	
	/**
	 * Returns the name of the file for example if file path is <b>(C:/Give me more/no no/media.ogg)</b> it returns <b>(media.ogg)</b>
	 *
	 * @param absolutePath
	 *            the path
	 * @return the File title+extension
	 */
	public static String getFileName(String absolutePath) {
		return FilenameUtils.getName(absolutePath);
		
	}
	
	/**
	 * Returns the extension of file(without (.)) for example <b>(ai.mp3)->(mp3)</b> and to lowercase (Mp3 -> mp3)
	 *
	 * @param absolutePath
	 *            The File absolute path
	 * @return the File extension
	 */
	public static String getFileExtension(String absolutePath) {
		return FilenameUtils.getExtension(absolutePath).toLowerCase();
		
		// int i = path.lastIndexOf('.'); // characters contained before (.)
		//
		// if the name is not empty
		// if (i > 0 && i < path.length() - 1) 
		// return path.substring(i + 1).toLowerCase()
		//
		// return null
	}
	
	//------------------------------------------------------------------------------------------------------
	
	/**
	 * 1)Checks if this file is <b>audio</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param fileName
	 *            The File Name
	 * @return True if the type is supported or else False
	 */
	public static boolean isAudioSupported(String fileName) {
		String extension = getFileExtension(fileName);
		return extension != null && ACCEPTED_AUDIO_EXTENSIONS.contains(extension);
	}
	
	/**
	 * 1)Checks if this file is <b>video</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param fileName
	 *            The File Name
	 * @return True if the type is supported or else False
	 */
	public static boolean isVideoSupported(String fileName) {
		String extension = getFileExtension(fileName);
		return extension != null && ACCEPTED_VIDEO_EXTENSIONS.contains(extension);
	}
	
	/**
	 * 1)Checks if this file is <b>image</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param fileName
	 *            The File Name
	 * @return True if the type is supported or else False
	 */
	public static boolean isImageSupported(String fileName) {
		String extension = getFileExtension(fileName);
		return extension != null && ACCEPTED_IMAGE_EXTENSIONS.contains(extension);
	}
	
	//------------------------------------------------------------------------------------------------------
	
	/**
	 * 1)Checks if this file is <b>Audio</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param fileName
	 *            The File Name
	 * @return True if the file is an Audio else false
	 */
	public static boolean isAudio(String fileName) {
		String extension = getFileExtension(fileName);
		return extension != null && POPULAR_AUDIO_EXTENSIONS.contains(extension);
	}
	
	/**
	 * 1)Checks if this file is <b>Audio</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param extension
	 *            File extension
	 * @return True if the file is an Audio else false
	 */
	public static boolean isAudioCheckExtension(String extension) {
		return extension != null && POPULAR_AUDIO_EXTENSIONS.contains(extension);
	}
	
	/**
	 * 1)Checks if this file is <b>Video</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param fileName
	 *            The File Name
	 * @return True if the file is an Video else false
	 */
	public static boolean isVideo(String fileName) {
		String extension = getFileExtension(fileName);
		return extension != null && POPULAR_VIDEO_EXTENSIONS.contains(extension);
	}
	
	/**
	 * 1)Checks if this file is <b>Video</b><br>
	 * 2)If is supported by the application.
	 * 
	 * @param extension
	 *            File extension
	 * @return True if the file is an Video else false
	 */
	public static boolean isVideoCheckExtension(String extension) {
		return extension != null && POPULAR_VIDEO_EXTENSIONS.contains(extension);
	}
	
	/**
	 * 1)Checks if this file is <b>Image</b><br>
	 * 
	 * @param fileName
	 *            The File Name
	 * @return True if the file is an Image else false
	 */
	public static boolean isImage(String fileName) {
		String extension = getFileExtension(fileName);
		return extension != null && POPULAR_IMAGE_EXTENSIONS.contains(extension);
	}
	
	/**
	 * 1)Checks if this file is <b>Image</b><br>
	 * 
	 * @param extension
	 *            File extension
	 * @return True if the file is an Image else false
	 */
	public static boolean isImageCheckExtension(String extension) {
		return extension != null && POPULAR_IMAGE_EXTENSIONS.contains(extension);
	}
	
	/**
	 * 1)Checks if this file is <b>PDF</b><br>
	 * 
	 * @param fileName
	 *            The File Name
	 * @return True if the file is an PDF else false
	 */
	public static boolean isPdf(String fileName) {
		String extension = getFileExtension(fileName);
		return extension != null && "pdf".equals(extension);
	}
	
	/**
	 * 1)Checks if this file is <b>PDF</b><br>
	 * 
	 * @param extension
	 *            File extension
	 * @return True if the file is an PDF else false
	 */
	public static boolean isPdfCheckExtension(String extension) {
		return extension != null && "pdf".equals(extension);
	}
	
	/**
	 * 1)Checks if this file is <b>ZIP</b><br>
	 * 
	 * @param fileName
	 *            The File Name
	 * @return True if the file is an ZIP else false
	 */
	public static boolean isZip(String fileName) {
		String extension = getFileExtension(fileName);
		return extension != null && POPULAR_ZIP_EXTENSIONS.contains(extension);
	}
	
	/**
	 * 1)Checks if this file is <b>ZIP</b><br>
	 * 
	 * @param extension
	 *            File extension
	 * @return True if the file is an ZIP else false
	 */
	public static boolean isZipCheckExtension(String extension) {
		return extension != null && POPULAR_ZIP_EXTENSIONS.contains(extension);
	}
	
	//------------------------------------------------------------------------------------------------------
	
	/**
	 * Use this method to retrieve an image from the resources of the application.
	 *
	 * @param imageName
	 *            the image name
	 * @return Returns an image which is already into the resources folder of the application
	 */
	public static Image getImageFromResourcesFolder(String imageName) {
		return new Image(InfoTool.class.getResourceAsStream(IMAGES + imageName));
	}
	
	/**
	 * Use this method to retrieve an image from the resources of the application.
	 *
	 * @param imageName
	 *            the image name
	 * @return Returns an image which is already into the resources folder of the application
	 */
	//	public static Image getImageFromCurrentFolder(String folderName , String imageName) {
	//		return new Image(InfoTool.class.getResourceAsStream("/" + folderName + "/" + imageName));
	//	}
	
	/**
	 * Use this method to retrieve an ImageView from the resources of the application.
	 *
	 * @param imageName
	 *            the image name
	 * @return Returns an ImageView using method getImageFromResourcesFolder(String imageName);
	 */
	public static ImageView getImageViewFromResourcesFolder(String imageName) {
		return new ImageView(getImageFromResourcesFolder(imageName));
	}
	
	/**
	 * Returns the current hour in format h:mm a
	 *
	 * @return the Returns the current hour in format h:mm a
	 */
	public static String getLocalTime() {
		return LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));
	}
	
	/**
	 * Returns the Local Date in format dd/MM/yyyy
	 *
	 * @return the local date in format dd/MM/yyyy
	 */
	public static String getCurrentDate() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		
	}
	
	/**
	 * Returns a String with a fixed number of letters.
	 *
	 * @param s
	 *            the string
	 * @param letters
	 *            the letters
	 * @return A substring(or the current given string) based on the letters that have to be cut plus "..."
	 */
	public static String getMinString(String s , int letters) {
		return s.length() < letters ? s : s.substring(0, letters) + "...";
	}
	
	/**
	 * Returns a String with a fixed number of letters.
	 *
	 * @param s
	 *            the string
	 * @param letters
	 *            the letters
	 * @return A substring(or the current given string) based on the letters that have to be cut without adding "..." to the end of string
	 */
	public static String getMinString2(String s , int letters) {
		return s.length() < letters ? s : s.substring(0, letters);
	}
	
	/**
	 * This method determines the duration of given data.
	 *
	 * @param input
	 *            The name of the input
	 * @param audioType
	 *            URL, FILE, INPUTSTREAM, UNKOWN;
	 * @return Returns the duration of URL/FILE/INPUTSTREAM in milliseconds
	 */
	public static long durationInMilliseconds(String input , AudioType audioType) {
		return audioType == AudioType.FILE ? durationInMilliseconds_Part2(new File(input))
				: ( audioType == AudioType.URL || audioType == AudioType.INPUTSTREAM || audioType == AudioType.UNKNOWN ) ? -1 : -1;
	}
	
	/**
	 * Used by method durationInMilliseconds() to get file duration.
	 *
	 * @param file
	 *            the file
	 * @return the int
	 */
	private static long durationInMilliseconds_Part2(File file) {
		long milliseconds = -1;
		
		// exists?
		if (file.exists() && file.length() != 0) {
			
			// extension?
			String extension = InfoTool.getFileExtension(file.getName());
			
			// MP3?
			if ("mp3".equals(extension)) {
				try {
					milliseconds = new MP3File(file).getMP3AudioHeader().getTrackLength() * 1000;
					
					//milliseconds = (int) ( (Long) AudioSystem.getAudioFileFormat(file).properties().get("duration") / 1000 );
					
					//Get the result of mp3agic if the duration is bigger than 6 minutes
					//		    if (milliseconds / 1000 > 60 * 9) {
					//			System.out.println("Entered..");
					//			milliseconds = tryWithMp3Agic(file);
					//		    }
					
				} catch (IOException | InvalidAudioFrameException | TagException | ReadOnlyFileException ex) {
					System.out.println("Problem getting the time of->" + file.getAbsolutePath());
					//logger.log(Level.WARNING, ex.getMessage(), ex);
				} catch (Exception ex) {
					System.out.println("Problem getting the time of->" + file.getAbsolutePath());
				}
				//}
			}
			// WAVE || OGG?
			else if ("ogg".equals(extension) || "wav".equals(extension)) {
				try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file)) {
					AudioFormat format = audioInputStream.getFormat();
					milliseconds = (int) ( file.length() / ( format.getFrameSize() * (int) format.getFrameRate() ) ) * 1000;
				} catch (IOException | UnsupportedAudioFileException ex) {
					System.out.println("Problem getting the time of->" + file.getAbsolutePath());
					//logger.log(Level.WARNING, ex.getMessage(), ex);
				}
			}
		}
		
		// System.out.println("Passed with error")
		return milliseconds < 0 ? -1 : milliseconds;
	}
	
	/**
	 * Returns the time of Audio to seconds
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            <br>
	 *            1->URL <br>
	 *            2->FILE <br>
	 *            3->INPUTSTREAM
	 * @return time in milliseconds
	 */
	public static int durationInSeconds(String name , AudioType type) {
		
		long time = durationInMilliseconds(name, type);
		
		return (int) ( ( time == 0 || time == -1 ) ? time : time / 1000 );
		
		//	 Long microseconds = (Long)AudioSystem.getAudioFileFormat(new File(audio)).properties().get("duration") int mili = (int)(microseconds / 1000L);
		//	 int sec = milli / 1000 % 60; 
		//	 int min = milli / 1000 / 60; 
		
	}
	
	/**
	 * /** Returns the time in format <b> %02d:%02d:%02d if( minutes >60 )</b> or %02d:%02d.
	 *
	 * @param ms
	 *            The milliseconds
	 * @return The Time edited in format <b> %02d:%02d:%02d if( minutes >60 )</b> or %02d:%02d.
	 * 
	 */
	public static String millisecondsToTime(long ms) {
		int millis = (int) ( ( ms % 1000 ) / 100 );
		//	int seconds = (int) ((ms / 1000) % 60);
		//	int minutes = (int) ((ms / (1000 * 60)) % 60);
		//	int hours = (int) ((ms / (1000 * 60 * 60)) % 24);
		
		//	if (minutes > 60)
		//	    return String.format("%02d:%02d:%02d.%d", hours, minutes, seconds, millis);
		//	else
		//	    return String.format("%02d:%02d.%d", minutes, seconds, millis);
		
		return String.format(".%d", millis);
		
	}
	
	/**
	 * Returns the time in format <b> %02d:%02d:%02d if( minutes >60 )</b> or %02dsec if (seconds<60) %02d:%02d.
	 * 
	 * @param seconds
	 *            the seconds
	 * @return the time edited in format <b> %02d:%02d:%02d if( minutes >60 )</b> or %02d:%02d. [[SuppressWarningsSpartan]]
	 */
	public static String getTimeEdited(int seconds) {
		if (seconds < 60) // duration < 1 minute
			return String.format("%02ds", seconds % 60);
		else if ( ( seconds / 60 ) / 60 <= 0) // duration < 1 hour
			return String.format("%02dm:%02d", ( seconds / 60 ) % 60, seconds % 60);
		else
			return String.format("%02dh:%02dm:%02d", ( seconds / 60 ) / 60, ( seconds / 60 ) % 60, seconds % 60);
	}
	
	/**
	 * Returns the time in format %02d:%02d.
	 *
	 * @param seconds
	 *            the seconds
	 * @return the time edited on hours
	 */
	public static String getTimeEditedOnHours(int seconds) {
		
		return String.format("%02d:%02d", seconds / 60, seconds % 60);
		
	}
	
	/**
	 * Returns the Date the File Created in Format `dd/mm/yyyy`
	 * 
	 * @param file
	 *            The File to be given
	 * @return the Date the File Created in Format `dd/mm/yyyy`
	 */
	public static String getFileCreationDate(File file) {
		Path path = Paths.get(file.getAbsolutePath());
		BasicFileAttributes attr;
		try {
			attr = Files.readAttributes(path, BasicFileAttributes.class);
			
			return new SimpleDateFormat("dd/MM/yyyy").format(attr.creationTime().toMillis());
			
		} catch (IOException e) {
			e.printStackTrace();
			return "oops error! ";
		}
	}
	
	/**
	 * Returns the Time the File Created in Format `h:mm a`
	 * 
	 * @param file
	 *            The File to be given
	 * @return the Time the File Created in Format `HH:mm:ss`
	 */
	public static String getFileCreationTime(File file) {
		Path path = Paths.get(file.getAbsolutePath());
		BasicFileAttributes attr;
		try {
			attr = Files.readAttributes(path, BasicFileAttributes.class);
			
			return new SimpleDateFormat("h:mm a").format(attr.creationTime().toMillis());
			
		} catch (IOException e) {
			e.printStackTrace();
			return "oops error! ";
		}
	}
	
	/**
	 * Gets the file size edited in format "x MiB , y KiB"
	 *
	 * @param file
	 *            the file
	 * @return <b> a String representing the file size in MB and kB </b>
	 */
	public static String getFileSizeEdited(File file) {
		return !file.exists() ? "file missing" : getFileSizeEdited(file.length());
	}
	
	/**
	 * Gets the file size edited in format "x MiB , y KiB"
	 *
	 * @param bytes
	 *            File size in bytes
	 * @return <b> a String representing the file size in MB and kB </b>
	 */
	public static String getFileSizeEdited(long bytes) {
		
		//Find it	
		int kilobytes = (int) ( bytes / 1024 ) , megabytes = kilobytes / 1024;
		if (kilobytes < 1024)
			return kilobytes + " KiB";
		else if (kilobytes > 1024)
			return megabytes + "." + ( kilobytes - ( megabytes * 1024 ) ) + " MiB";
		
		return "error";
		
	}
	
	/**
	 * Returns a number with more than 3 digits [ Example 1000 as 1.000] with dots every 3 digits
	 * 
	 * @param number
	 * @return A number with more than 3 digits [ Example 1000 as 1.000] with dots every 3 digits
	 */
	public static String getNumberWithDots(int number) {
		return String.format(Locale.US, "%,d", number).replace(",", ".");
	}
	
}
