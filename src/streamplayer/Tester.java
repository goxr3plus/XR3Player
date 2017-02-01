/**
 * 
 */
package streamplayer;

import java.io.File;
import java.util.Map;

/**
 * @author GOXR3PLUS
 *
 */
public class Tester extends StreamPlayer implements StreamPlayerListener {

    /**
     * Constructor
     */
    public Tester() {

	try {

	    // Register to the Listeners
	    super.addStreamPlayerListener(this);

	    // Open a File
	    super.open(new File("FILEPATH HERE"));
	    // Play it
	    super.play();

	} catch (StreamPlayerException ex) {
	    ex.printStackTrace();
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see streamplayer.StreamPlayerListener#opened(java.lang.Object,
     * java.util.Map)
     */
    @Override
    public void opened(Object dataSource, Map<String, Object> properties) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see streamplayer.StreamPlayerListener#progress(int, long, byte[],
     * java.util.Map)
     */
    @Override
    public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {

	System.out.println(nEncodedBytes);
    }

    /*
     * (non-Javadoc)
     * 
     * @see streamplayer.StreamPlayerListener#statusUpdated(streamplayer.
     * StreamPlayerEvent)
     */
    @Override
    public void statusUpdated(StreamPlayerEvent event) {
	System.out.println(event.getPlayerStatus());
    }

    /**
     * Main method
     * 
     * @param args
     */
    public static void main(String[] args) {
	new Tester();
    }

}
