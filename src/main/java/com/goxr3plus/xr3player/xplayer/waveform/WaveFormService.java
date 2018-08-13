package main.java.com.goxr3plus.xr3player.xplayer.waveform;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.xplayer.presenter.XPlayerController;
import ws.schild.jave.AudioAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaObject;

public class WaveFormService extends Service<Boolean> {
	
	private static final double WAVEFORM_HEIGHT_COEFFICIENT = 2.6; // This fits the waveform to the swing node height
	private static final CopyOption[] options = new CopyOption[]{ COPY_ATTRIBUTES , REPLACE_EXISTING };
	private float[] resultingWaveform;
	private int[] wavAmplitudes;
	private String fileAbsolutePath;
	private final XPlayerController xPlayerController;
	private final Random random = new Random();
	private File temp1;
	private File temp2;
	private Encoder encoder;
	
	/**
	 * Constructor.
	 */
	public WaveFormService(XPlayerController xPlayerController) {
		this.xPlayerController = xPlayerController;
		
		setOnSucceeded(s -> done());
		setOnFailed(f -> failure());
		setOnCancelled(c -> failure());
	}
	
	/**
	 * Start the external Service Thread.
	 *
	 */
	public void startService(String fileAbsolutePath) {
		this.fileAbsolutePath = fileAbsolutePath;
		this.resultingWaveform = null;
		this.wavAmplitudes = null;
		
		//Go
		restart();
	}
	
	/**
	 * Done.
	 */
	// Work done
	private void done() {
		xPlayerController.getWaveProgressLabel().setText("Wave Spectrum");
		xPlayerController.getWaveFormVisualization().setWaveData(resultingWaveform);
		xPlayerController.getWaveFormVisualization().startPainterService();
		deleteTemporaryFiles();
	}
	
	private void failure() {
		xPlayerController.getWaveProgressLabel().setText("Wave Spectrum");
		deleteTemporaryFiles();
	}
	
	/**
	 * Delete temporary files
	 */
	private void deleteTemporaryFiles() {
		temp1.delete();
		temp2.delete();
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			
			@Override
			protected Boolean call() throws Exception {
				boolean success = true;
				
				//Try to get the resultingWaveForm
				try {
					Platform.runLater(() -> xPlayerController.getWaveProgressLabel().setText("Generating Wave Spectrum..."));
					
					String fileFormat = "mp3";
					//		                if ("wav".equals(fileFormat))
					//		                    resultingWaveform = processFromWavFile();
					//		                else if ("mp3".equals(fileFormat) || "m4a".equals(fileFormat))
					resultingWaveform = processFromNoWavFile(fileFormat);
					
				} catch (Exception ex) {
					ex.printStackTrace();
					success = false;
				}
				
				return success;
				
			}
			
			/**
			 * Try to process a Non Wav File
			 * 
			 * @param fileFormat
			 * @return
			 * @throws IOException
			 * @throws UnsupportedAudioFileException
			 * @throws EncoderException
			 */
			private float[] processFromNoWavFile(String fileFormat) throws IOException , UnsupportedAudioFileException , EncoderException {
				int randomN = random.nextInt(99999);
				
				//Create temporary files
				File temporalDecodedFile = File.createTempFile("decoded_" + InfoTool.getFileTitle(fileAbsolutePath) + randomN, ".wav");
				File temporalCopiedFile = File.createTempFile("original_" + InfoTool.getFileTitle(fileAbsolutePath) + randomN, "." + fileFormat);
				temp1 = temporalDecodedFile;
				temp2 = temporalCopiedFile;
				
				//Delete temporary Files on exit
				temporalDecodedFile.deleteOnExit();
				temporalCopiedFile.deleteOnExit();
				
				//Create a temporary path
				Files.copy(new File(fileAbsolutePath).toPath(), temporalCopiedFile.toPath(), options);
				
				//Transcode to .wav
				transcodeToWav(temporalCopiedFile, temporalDecodedFile);
				
				//Avoid creating amplitudes again for the same file
				if (wavAmplitudes == null)
					wavAmplitudes = getWavAmplitudes(temporalDecodedFile);
				
				//Delete temporary files
				temporalDecodedFile.delete();
				temporalCopiedFile.delete();
				
				return processAmplitudes(wavAmplitudes);
			}
			
			/**
			 * Process the amplitudes
			 * 
			 * @param sourcePcmData
			 * @return An array with amplitudes
			 */
			private float[] processAmplitudes(int[] sourcePcmData) {
				int width = xPlayerController.getWaveFormVisualization().width;    // the width of the resulting waveform panel
				float[] waveData = new float[width];
				int samplesPerPixel = sourcePcmData.length / width;
				
				for (int w = 0; w < width; w++) {
					float nValue = 0.0f;
					
					for (int s = 0; s < samplesPerPixel; s++) {
						nValue += ( Math.abs(sourcePcmData[w * samplesPerPixel + s]) / 65536.0f );
					}
					nValue /= samplesPerPixel;
					waveData[w] = nValue;
				}
				return waveData;
			}
			
			/**
			 * Get Wav Amplitudes
			 * 
			 * @param file
			 * @return
			 * @throws UnsupportedAudioFileException
			 * @throws IOException
			 */
			private int[] getWavAmplitudes(File file) throws UnsupportedAudioFileException , IOException {
				int[] amplitudes;
				try (AudioInputStream input = AudioSystem.getAudioInputStream(file)) {
					AudioFormat baseFormat = input.getFormat();
					
					Encoding encoding = AudioFormat.Encoding.PCM_UNSIGNED;
					float sampleRate = baseFormat.getSampleRate();
					int numChannels = baseFormat.getChannels();
					
					AudioFormat decodedFormat = new AudioFormat(encoding, sampleRate, 16, numChannels, numChannels * 2, sampleRate, false);
					int available = input.available();
					amplitudes = new int[available];
					
					try (AudioInputStream pcmDecodedInput = AudioSystem.getAudioInputStream(decodedFormat, input)) {
						byte[] buffer = new byte[available];
						pcmDecodedInput.read(buffer, 0, available);
						for (int i = 0; i < available - 1; i += 2) {
							amplitudes[i] = ( ( buffer[i + 1] << 8 ) | buffer[i] & 0xff ) << 16;
							amplitudes[i] /= 32767;
							amplitudes[i] *= WAVEFORM_HEIGHT_COEFFICIENT;
						}
					}
				}
				return amplitudes;
			}
			
			/**
			 * Transcode to Wav
			 * 
			 * @param sourceFile
			 * @param destinationFile
			 * @throws EncoderException
			 */
			private void transcodeToWav(File sourceFile , File destinationFile) throws EncoderException {
				try {
					
					//Set Audio Attributes
					AudioAttributes audio = new AudioAttributes();
					audio.setCodec("pcm_s16le");
					audio.setChannels(2);
					audio.setSamplingRate(44100);
					
					//Set encoding attributes
					EncodingAttributes attributes = new EncodingAttributes();
					attributes.setFormat("wav");
					attributes.setAudioAttributes(audio);
					
					//Encode
					encoder = encoder != null ? encoder : new Encoder();
					encoder.encode(new MultimediaObject(sourceFile), destinationFile, attributes);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
			//TODO CREATE IN FUTURE
			//			private float[] processFromWavFile() throws IOException , UnsupportedAudioFileException {
			//				File trackFile = new File(trackToAnalyze.getFileFolder(), trackToAnalyze.getFileName());
			//				return processAmplitudes(getWavAmplitudes(trackFile));
			//			}
		};
	}
	
	public String getFileAbsolutePath() {
		return fileAbsolutePath;
	}
	
	public void setFileAbsolutePath(String fileAbsolutePath) {
		this.fileAbsolutePath = fileAbsolutePath;
	}
	
	public int[] getWavAmplitudes() {
		return wavAmplitudes;
	}
	
}
