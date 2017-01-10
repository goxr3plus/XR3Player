/*
 * 
 */

package dsp;


/**
 * The Interface KJDigitalSignalProcessor.
 *
 * @author Kris Fudalewski
 * 
 *         Classes must implement this interface in order to be registered with
 *         the KJDigitalSignalProcessingAudioDataConsumer class.
 */
@FunctionalInterface
public interface KJDigitalSignalProcessor {
	
	/**
	 * Called by the KJDigitalSignalProcessingAudioDataConsumer.
	 * 
	 * @param pLeftChannel
	 *        Audio data for the left channel.
	 * @param pRightChannel
	 *        Audio data for the right channel.
	 * @param pFrameRateRatioHint
	 *        A float value representing the ratio of the current frame rate
	 *        to the desired frame rate. It is used to keep DSP animation
	 *        consistent if the frame rate drop below the desired frame
	 *        rate.
	 */
	void process(float[] pLeftChannel , float[] pRightChannel , float pFrameRateRatioHint);
	
}
