/*
 * 
 */

package main.java.com.goxr3plus.xr3player.application.xplayer.dsp;

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
	 * @param leftChannel        Audio data for the left channel.
	 * @param rightChannel       Audio data for the right channel.
	 * @param stereoMerge        Merged Audio data from left and right channel
	 * @param frameRateRatioHint A float value representing the ratio of the current
	 *                           frame rate to the desired frame rate. It is used to
	 *                           keep DSP animation consistent if the frame rate
	 *                           drop below the desired frame rate.
	 */
	void process(float[] leftChannel, float[] rightChannel, float[] stereoMerge, float frameRateRatioHint);

}
