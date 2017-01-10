/*
 * 
 */

package dsp;

/**
 * The Interface KJAudioDataConsumer.
 * 
 * @author Kris Fudalewski
 */
public interface KJAudioDataConsumer {
	
	/**
	 * Write audio data.
	 *
	 * @param pAudioData the audio data
	 */
	void writeAudioData(byte[] pAudioData);
	
	/**
	 * Write audio data.
	 *
	 * @param pAudioData the audio data
	 * @param pOffset the offset
	 * @param pLength the length
	 */
	void writeAudioData(byte[] pAudioData , int pOffset , int pLength);
	
}
