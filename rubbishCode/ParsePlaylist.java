package aaTesterOnlyCode;

/*
 * Copyright 2014 William Seemann Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.SAXException;

import wseemann.media.jplaylistparser.exception.JPlaylistParserException;
import wseemann.media.jplaylistparser.parser.AutoDetectParser;
import wseemann.media.jplaylistparser.playlist.Playlist;
import wseemann.media.jplaylistparser.playlist.PlaylistEntry;

public class ParsePlaylist {
	
	public static void main(String[] args) throws SAXException , JPlaylistParserException {
		URL url;
		HttpURLConnection conn = null;
		InputStream is = null;
		
		try {
			url = new URL("Enter playlist URL here");
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(6000);
			conn.setReadTimeout(6000);
			conn.setRequestMethod("GET");
			
			String contentType = conn.getContentType();
			is = conn.getInputStream();
			
			AutoDetectParser parser = new AutoDetectParser(); // Should auto-detect!
			Playlist playlist = new Playlist();
			parser.parse(url.toString(), contentType, is, playlist);
			
			for (int i = 0; i < playlist.getPlaylistEntries().size(); i++) {
				PlaylistEntry entry = playlist.getPlaylistEntries().get(i);
				System.out.println(entry.get(PlaylistEntry.URI));
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
