package tarsosWaveForm;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import be.tarsos.transcoder.Attributes;
import be.tarsos.transcoder.DefaultAttributes;
import be.tarsos.transcoder.Transcoder;
import be.tarsos.transcoder.ffmpeg.EncoderException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class WaveFormService extends Service<Boolean> {
	
	private static final double WAVEFORM_HEIGHT_COEFFICIENT = 2.6; // This fits the waveform to the swing node height
	private static final CopyOption[] options = new CopyOption[]{ COPY_ATTRIBUTES , REPLACE_EXISTING };
	private float[] resultingWaveform;
	private int[] wavAmplitudes;
	private String fileAbsolutePath;
	private final WaveVisualization waveVisualization;
	
	/**
	 * Constructor.
	 */
	public WaveFormService(WaveVisualization waveVisualization) {
		this.waveVisualization = waveVisualization;
		
		setOnSucceeded(s -> done());
		setOnFailed(f -> done());
		setOnCancelled(c -> done());
	}
	
	/**
	 * Start the external Service Thread.
	 *
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
	public void done() {
		waveVisualization.startPainterService();
	//	waveVisualization.setWaveData(resultingWaveform);
	//	waveVisualization.paintWaveForm();
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			
			@Override
			protected Boolean call() throws Exception {
				
				//Try to get the resultingWaveForm
				try {
					
					String fileFormat = "mp3";
					//		                if ("wav".equals(fileFormat))
					//		                    resultingWaveform = processFromWavFile();
					//		                else if ("mp3".equals(fileFormat) || "m4a".equals(fileFormat))
					resultingWaveform = processFromNoWavFile(fileFormat);
					
					System.out.println("Service done successfully");
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
				
				return true;
				
			}
			
			//			private float[] processFromWavFile() throws IOException , UnsupportedAudioFileException {
			//				File trackFile = new File(trackToAnalyze.getFileFolder(), trackToAnalyze.getFileName());
			//				return processAmplitudes(getWavAmplitudes(trackFile));
			//			}
			
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
				File temporalDecodedFile = File.createTempFile("decoded_" + "trackID", ".wav");
				File temporalCopiedFile = File.createTempFile("original_" + "trackID", "." + fileFormat);
				
				//Create a temporary path
				Files.copy(new File(fileAbsolutePath).toPath(), temporalCopiedFile.toPath(), options);
				
				//Transcode to wav
				transcodeToWav(temporalCopiedFile, temporalDecodedFile);
				
				//Avoid creating amplitudes again for the same file
				if (wavAmplitudes == null)
					wavAmplitudes = getWavAmplitudes(temporalDecodedFile);
				return processAmplitudes(wavAmplitudes);
			}
			
			/**
			 * Process the amplitudes
			 * 
			 * @param sourcePcmData
			 * @return An array with amplitudes
			 */
			private float[] processAmplitudes(int[] sourcePcmData) {
				int width = waveVisualization.width;    // the width of the resulting waveform panel
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
				Attributes attributes = DefaultAttributes.WAV_PCM_S16LE_STEREO_44KHZ.getAttributes();
				try {
					Transcoder.transcode(sourceFile.toString(), destinationFile.toString(), attributes);
				} catch (EncoderException exception) {
					if (exception.getMessage().startsWith("Source and target should")) {
						// even with this error message the library does the conversion, who knows why
					} else {
						throw exception;
					}
				}
			}
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
