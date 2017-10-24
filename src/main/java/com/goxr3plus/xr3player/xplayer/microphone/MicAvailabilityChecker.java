package main.java.com.goxr3plus.xr3player.xplayer.microphone;

import com.darkprograms.speech.microphone.Microphone;

import net.sourceforge.javaflacencoder.FLACFileWriter;

public class MicAvailabilityChecker {
	
	public static void validateMicAvailability() throws MicUnaccessibleException {
		Microphone mic = new Microphone(FLACFileWriter.FLAC);
		try {
			if (mic.getState() != Microphone.CaptureState.CLOSED) {
				throw new MicUnaccessibleException("Mic didn't successfully initialized");
			}
			
			mic.captureAudioToFile("damn.mp3");
			if (mic.getState() != Microphone.CaptureState.PROCESSING_AUDIO || mic.getState() != Microphone.CaptureState.STARTING_CAPTURE) {
				throw new MicUnaccessibleException("Mic is in use and can't be accessed");
			}
			mic.close();
		} finally {
			mic.close();
			
			mic = null;
		}
	}
	
	public static void main(String[] args) {
		try {
			
			MicAvailabilityChecker.validateMicAvailability();
		} catch (MicUnaccessibleException e) {
			e.printStackTrace();
		}
	}
	
}
