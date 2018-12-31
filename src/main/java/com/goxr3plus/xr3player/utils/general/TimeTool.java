package main.java.com.goxr3plus.xr3player.utils.general;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.jaudiotagger.audio.mp3.MP3File;

import main.java.com.goxr3plus.xr3player.application.enums.AudioType;
import main.java.com.goxr3plus.xr3player.application.enums.FileType;
import main.java.com.goxr3plus.xr3player.utils.io.IOInfo;

public final class TimeTool {

	private TimeTool() {
	}

	/**
	 * Returns the time in format %02d:%02d.
	 *
	 * @param seconds the seconds
	 * @return the time edited on hours
	 */
	public static String getTimeEditedOnHours(final int seconds) {

		return String.format("%02d:%02d", seconds / 60, seconds % 60);

	}

	/**
	 * Returns the time in format <b> %02d:%02d:%02d if( minutes >60 )</b> or
	 * %02dsec if (seconds<60) %02d:%02d.
	 * 
	 * @param seconds the seconds
	 * @return the time edited in format <b> %02d:%02d:%02d if( minutes >60 )</b> or
	 *         %02d:%02d. [[SuppressWarningsSpartan]]
	 */
	public static String getTimeEdited(final int seconds) {
		if (seconds < 60) // duration < 1 minute
			return String.format("%02ds", seconds % 60);
		else if ((seconds / 60) / 60 <= 0) // duration < 1 hour
			return String.format("%02dm:%02d", (seconds / 60) % 60, seconds % 60);
		else
			return String.format("%02dh:%02dm:%02d", (seconds / 60) / 60, (seconds / 60) % 60, seconds % 60);
	}

	/**
	 * /** Returns the time in format <b> %02d:%02d:%02d if( minutes >60 )</b> or
	 * %02d:%02d.
	 *
	 * @param ms The milliseconds
	 * @return The Time edited in format <b> %02d:%02d:%02d if( minutes >60 )</b> or
	 *         %02d:%02d.
	 * 
	 */
	public static String millisecondsToTime(final long ms) {
		final int millis = (int) ((ms % 1000) / 100);
		// int seconds = (int) ((ms / 1000) % 60);
		// int minutes = (int) ((ms / (1000 * 60)) % 60);
		// int hours = (int) ((ms / (1000 * 60 * 60)) % 24);

		// if (minutes > 60)
		// return String.format("%02d:%02d:%02d.%d", hours, minutes, seconds, millis);
		// else
		// return String.format("%02d:%02d.%d", minutes, seconds, millis);

		return String.format(".%d", millis);

	}

	/**
	 * Returns the time of Audio to seconds
	 *
	 * @param name the name
	 * @param type <br>
	 *             1->URL <br>
	 *             2->FILE <br>
	 *             3->INPUTSTREAM
	 * @return time in milliseconds
	 */
	public static int durationInSeconds(final String name, final AudioType type) {

		final long time = TimeTool.durationInMilliseconds(name, type);

		return (int) ((time == 0 || time == -1) ? time : time / 1000);

		// Long microseconds = (Long)AudioSystem.getAudioFileFormat(new
		// File(audio)).properties().get("duration") int mili = (int)(microseconds /
		// 1000L);
		// int sec = milli / 1000 % 60;
		// int min = milli / 1000 / 60;

	}

	/**
	 * This method determines the duration of given data.
	 *
	 * @param input     The name of the input
	 * @param audioType URL, FILE, INPUTSTREAM, UNKOWN;
	 * @return Returns the duration of URL/FILE/INPUTSTREAM in milliseconds
	 */
	public static long durationInMilliseconds(final String input, final AudioType audioType) {
		return audioType == AudioType.FILE ? durationInMilliseconds_Part2(new File(input))
				: (audioType == AudioType.URL || audioType == AudioType.INPUTSTREAM || audioType == AudioType.UNKNOWN)
						? -1
						: -1;
	}

	/**
	 * Used by method durationInMilliseconds() to get file duration.
	 *
	 * @param file the file
	 * @return the int
	 */
	private static long durationInMilliseconds_Part2(final File file) {
		long milliseconds = -1;

		// exists?
		if (file.exists() && file.length() != 0) {

			// extension?
			final String extension = IOInfo.getFileExtension(file.getName());

			// MP3?
			if ("mp3".equals(extension)) {
				try {
					milliseconds = new MP3File(file).getMP3AudioHeader().getTrackLength() * 1000;

					// milliseconds = (int) ( (Long)
					// AudioSystem.getAudioFileFormat(file).properties().get("duration") / 1000 );

					// Get the result of mp3agic if the duration is bigger than 6 minutes
					// if (milliseconds / 1000 > 60 * 9) {
					// System.out.println("Entered..");
					// milliseconds = tryWithMp3Agic(file);
					// }

				} catch (final Exception ex) {
					System.err.println("Problem getting the time of-> " + file.getAbsolutePath());
				}
				// }
			}
			// WAVE || OGG?
			else if ("ogg".equals(extension) || "wav".equals(extension)) {
				try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file)) {
					final AudioFormat format = audioInputStream.getFormat();
					milliseconds = (int) (file.length() / (format.getFrameSize() * (int) format.getFrameRate())) * 1000;
				} catch (IOException | UnsupportedAudioFileException ex) {
					System.err.println("Problem getting the time of-> " + file.getAbsolutePath());
				}
			}
		}

		// System.out.println("Passed with error")
		return milliseconds < 0 ? -1 : milliseconds;
	}

	/**
	 * Creates the given File or Folder if not exists and returns the result
	 * 
	 * @param absoluteFilePath The absolute path of the File|Folder
	 * @param fileType         Create DIRECTORY OR FILE ?
	 * @return True if exists or have been successfully created , otherwise false
	 */
	public static boolean createFileOrFolder(final File file, final FileType fileType) {
		// Already exists?
		if (file.exists())
			return true;
		// Directory?
		if (fileType == FileType.DIRECTORY)
			return file.mkdir();
		// File?
		try {
			return file.createNewFile();
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Creates the given File or Folder if not exists and returns the result
	 * 
	 * @param absoluteFilePath The absolute path of the File|Folder
	 * @param fileType         Create DIRECTORY OR FILE ?
	 * @return True if exists or have been successfully created , otherwise false
	 */
	public static boolean createFileOrFolder(final String absoluteFilePath, final FileType fileType) {
		return createFileOrFolder(new File(absoluteFilePath), fileType);
	}

}
