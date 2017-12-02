package main.java.com.goxr3plus.xr3player.application.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

public class IOTool {
	
	/**
	 * Returns the real Path of the given File , either it is symbolic link or hard link or Windows Shortcut File
	 * 
	 * @param absoluteFilePath
	 */
	public static FileTypeAndAbsolutePath getRealPathFromFile(String absoluteFilePath) {
		Path path = Paths.get(absoluteFilePath);
		File file = path.toFile();
		
		//Check if file exists
		//if (!file.exists())
		//	return new FileTypeAndAbsolutePath(FileType.ORIGINAL_FILE, absoluteFilePath);
		
		//---------------Check if it is symbolic link		
		if (Files.isSymbolicLink(path))
			try {
				//If yes return the real file name
				absoluteFilePath = Files.readSymbolicLink(path).toFile().getAbsolutePath();
				return new FileTypeAndAbsolutePath(FileType.SYMBOLIC_LINK, absoluteFilePath);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		
		//------------Check if it Windows ShortCut File
		else
			try {
				//If yes returns the real file name
				if (WindowsShortcut.isPotentialValidLink(file))
					absoluteFilePath = new WindowsShortcut(file).getRealFilename();
				return new FileTypeAndAbsolutePath(FileType.SHORTCUT, absoluteFilePath);
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
		
		return new FileTypeAndAbsolutePath(FileType.ORIGINAL_FILE, absoluteFilePath);
	}
	
}
