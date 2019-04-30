package com.goxr3plus.xr3player.utils.general;

import java.io.ByteArrayInputStream;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import javafx.scene.image.Image;
import com.goxr3plus.xr3player.utils.io.IOInfo;

public final class AudioImageTool {

	private AudioImageTool() {
	}

	/**
	 * Return the imageView of mp3File in requested Width and Height.
	 *
	 * @param absolutePath The File absolute path
	 * @param width        the width
	 * @param height       the height
	 * @return an Image
	 */
	public static Image getAudioAlbumImage(final String absolutePath, final int width, final int height) {
		final ByteArrayInputStream arrayInputStream = AudioImageTool.getAudioAlbumImageRaw(absolutePath, width, height);

		// Does it contain an image
		if (arrayInputStream != null)
			return (width == -1 && height == -1) ? new Image(arrayInputStream)
					: new Image(arrayInputStream, width, height, false, true);

		return null;
	}

	/**
	 * Return the imageView of mp3File in requested Width and Height.
	 *
	 * @param absolutePath The File absolute path
	 * @param width        the width
	 * @param height       the height
	 * @return ByteArrayInputStream containing the image as binary data
	 */
	public static ByteArrayInputStream getAudioAlbumImageRaw(final String absolutePath, final int width,
			final int height) {
		// Is it mp3?
		if ("mp3".equals(IOInfo.getFileExtension(absolutePath)))
			try {
				final Mp3File song = new Mp3File(absolutePath);

				if (song.hasId3v2Tag()) { // has id3v2 tag?

					final ID3v2 id3v2Tag = song.getId3v2Tag();

					if (id3v2Tag.getAlbumImage() != null) // image?
						return new ByteArrayInputStream(id3v2Tag.getAlbumImage());
				}
			} catch (final Exception ex) {
				// logger.log(Level.WARNING, "Can't get Album Image", ex);
			}

		return null;// fatal error here
	}

}
