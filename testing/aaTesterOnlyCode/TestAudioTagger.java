/**
 * @author : Paul Taylor
 *
 *         Version @version:$Id$
 *
 *         Jaudiotagger Copyright (C)2004,2005
 *
 *         This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 *         published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 *
 *         This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 *         You should have received a copy of the GNU Lesser General Public License along with this library; if not, you can get a copy from
 *         http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *         Boston, MA 02110-1301 USA
 *
 *         Description:
 */

package aaTesterOnlyCode;

import java.io.File;

import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

/**
 * Simple class that will attempt to recursively read all files within a directory, flags errors that occur.
 */
public class TestAudioTagger {
	
	public static void main(final String[] args) {
		
		//for (int i = 0; i < 150; i++) {
		//	System.out.println("\n\nI: =" + i);
			
			File file = new File("iggy.mp3");
			try {
				MP3AudioHeader mp3AudioHeader = new MP3File(file).getMP3AudioHeader();
				
				System.out.println(mp3AudioHeader.getEmphasis());
				System.out.println(mp3AudioHeader.getSampleRate());
				System.out.println(mp3AudioHeader.getTrackLength());//getTrackLengthAsString());
				System.out.println(mp3AudioHeader.isVariableBitRate());
				System.out.println(mp3AudioHeader.getMpegVersion());
				System.out.println(mp3AudioHeader.getMpegLayer());
				System.out.println(mp3AudioHeader.getChannels());
				System.out.println(mp3AudioHeader.isOriginal());
				System.out.println(mp3AudioHeader.isCopyrighted());
				System.out.println(mp3AudioHeader.isPrivate());
				System.out.println(mp3AudioHeader.isProtected());
				System.out.println(mp3AudioHeader.getBitRate());
				System.out.println(mp3AudioHeader.getEncodingType());
				System.out.println(mp3AudioHeader.getEncoder());
				System.out.println(mp3AudioHeader.isVariableBitRate());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			//----------
			
			File file2 = new File("iggy.mp3");
			try {
				ID3v24Tag tag2 = new MP3File(file).getID3v2TagAsv24();
				//tag2.get
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		//}
		
	}
}
