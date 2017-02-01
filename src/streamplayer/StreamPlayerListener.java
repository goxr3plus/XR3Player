/*
 *  This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

   Also(warning!):
 
  1)You are not allowed to sell this product to third party.
  2)You can't change license and made it like you are the owner,author etc.
  3)All redistributions of source code files must contain all copyright
     notices that are currently in this file, and this list of conditions without
     modification.
 */
package streamplayer;

import java.util.Map;

/**
 * Used to notify for events that are happening on StreamPlayer.
 *
 * @author GOXR3PLUS (www.goxr3plus.co.nf)
 */
public interface StreamPlayerListener {
	
	/**
	 * It is called when the StreamPlayer open(Object object) method is called.
	 *
	 * @param dataSource the data source
	 * @param properties the properties
	 */
	void opened(Object dataSource , Map<String,Object> properties);
	
	/**
	 * Is called several times per second when StreamPlayer run method is
	 * running.
	 *
	 * @param nEncodedBytes the n encoded bytes
	 * @param microsecondPosition the microsecond position
	 * @param pcmData the pcm data
	 * @param properties the properties
	 */
	void progress(int nEncodedBytes , long microsecondPosition , byte[] pcmData , Map<String,Object> properties);
	
	/**
	 * Is called every time the status of the StreamPlayer changes.
	 *
	 * @param event the event
	 */
    void statusUpdated(StreamPlayerEvent event);

}
