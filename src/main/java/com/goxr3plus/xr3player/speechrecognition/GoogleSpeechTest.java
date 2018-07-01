package main.java.com.goxr3plus.xr3player.speechrecognition;

import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class GoogleSpeechTest {
	
	public GoogleSpeechTest() {
		
		//Target data line
		TargetDataLine microphone;
		AudioInputStream audio = null;
		
		//Check if Microphone is Supported
		checkMicrophoneAvailability();
		
		//Capture Microphone Audio Data
		try {
			
			// Signed PCM AudioFormat with 16kHz, 16 bit sample size, mono
			AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			
			//Check if Microphone is Supported
			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Microphone is not available");
				System.exit(0);
			}
			
			//Get the target data line
			microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(format);
			microphone.start();
			
			//Audio Input Stream
			audio = new AudioInputStream(microphone);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//Send audio from Microphone to Google Servers and return Text

		
	}
	
	/**
	 * Checks if the Microphone is available
	 */
	public static void checkMicrophoneAvailability() {
		enumerateMicrophones().forEach((string , info) -> {
			System.out.println("Name :" + string);
		});
	}
	
	/**
	 * Generates a hashmap to simplify the microphone selection process.
	 * The keyset is the name of the audio device's Mixer
	 * The value is the first lineInfo from that Mixer.
	 * @author Aaron Gokaslan (Skylion)
	 * @return The generated hashmap
	 */
	public static HashMap<String, Line.Info> enumerateMicrophones(){
		HashMap<String, Line.Info> out = new HashMap<String, Line.Info>();
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info info: mixerInfos){
			Mixer m = AudioSystem.getMixer(info);
			Line.Info[] lineInfos = m.getTargetLineInfo();
			if(lineInfos.length>=1 && lineInfos[0].getLineClass().equals(TargetDataLine.class))//Only adds to hashmap if it is audio input device
				out.put(info.getName(), lineInfos[0]);//Please enjoy my pun
		}
		return out;
	}
	
	public void printAvailableMixers() {
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mixerInfos) {
			Mixer m = AudioSystem.getMixer(info);
			Line.Info[] lineInfos = m.getSourceLineInfo();
			for (Line.Info lineInfo : lineInfos) {
				System.out.println(info.getName() + "---" + lineInfo);
				Line line = null;
				try {
					line = m.getLine(lineInfo);
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("\t-----" + line);
			}
			lineInfos = m.getTargetLineInfo();
			for (Line.Info lineInfo : lineInfos) {
				System.out.println(m + "---" + lineInfo);
				Line line = null;
				try {
					line = m.getLine(lineInfo);
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("\t-----" + line);
				
			}
			
		}
	}
	
	public static void main(String[] args) {
		new GoogleSpeechTest();
	}
	
}
