/*
 * 
 */
package streamplayer;

import java.util.Map;

/**
 * Used to notify for events that are happening on StreamPlayer.
 *
 * @author GOXR3PLUS
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
