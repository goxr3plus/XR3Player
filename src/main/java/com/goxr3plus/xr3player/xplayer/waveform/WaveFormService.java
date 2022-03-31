package com.goxr3plus.xr3player.xplayer.waveform;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.goxr3plus.xr3player.controllers.xplayer.XPlayerController;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import ws.schild.jave.Encoder;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.progress.EncoderProgressListener;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.EncoderException;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class WaveFormService extends Service<Boolean> {

	private static final double WAVEFORM_HEIGHT_COEFFICIENT = 1.3; // This fits the waveform to the swing node height
	private static final CopyOption[] options = new CopyOption[] { COPY_ATTRIBUTES, REPLACE_EXISTING };
	private float[] resultingWaveform;
	private int[] wavAmplitudes;
	private String fileAbsolutePath;
	private final XPlayerController xPlayerController;
	private final Random random = new Random();
	private File temp1;
	private File temp2;
	private Encoder encoder;
	private ConvertProgressListener listener = new ConvertProgressListener();
	private WaveFormJob waveFormJob;
	private SimpleDoubleProperty converterProgress = new SimpleDoubleProperty();

	/**
	 * Wave Service type of Job ( not boob job ... )
	 * 
	 * @author GOXR3PLUSSTUDIO
	 *
	 */
	public enum WaveFormJob {
		AMPLITUDES_AND_WAVEFORM, WAVEFORM;
	}

	/**
	 * Constructor.
	 */
	public WaveFormService(XPlayerController xPlayerController) {
		this.xPlayerController = xPlayerController;
		xPlayerController.getWaveProgressBar().progressProperty().bind(converterProgress);

		setOnSucceeded(s -> done());
		setOnFailed(f -> failure());
		setOnCancelled(c -> failure());
	}

	/**
	 * Start the external Service Thread.
	 *
	 */
	public void startService(String fileAbsolutePath, WaveFormJob waveFormJob) {

		// Security Check
		if (waveFormJob == WaveFormJob.AMPLITUDES_AND_WAVEFORM && fileAbsolutePath.equals(this.fileAbsolutePath) && wavAmplitudes != null) // If it is the same file
			return;

		// Check
		if (waveFormJob == WaveFormJob.WAVEFORM)
			cancel();

		// Stop the Serivce
		xPlayerController.getWaveFormVisualization().stopPainterService();

		// Abort Encoding
		if (encoder != null)
			encoder.abortEncoding();

		// Check if boob job
		this.waveFormJob = waveFormJob;

		// Variables
		this.fileAbsolutePath = fileAbsolutePath;
		// this.resultingWaveform = null
		if (waveFormJob != WaveFormJob.WAVEFORM)
			this.wavAmplitudes = null;
		converterProgress.set(-1);

		// Go
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
		// Abort Encoding
		if (encoder != null)
			encoder.abortEncoding();

		xPlayerController.getWaveProgressLabel().setText("Wave Spectrum");
		deleteTemporaryFiles();
	}

	/**
	 * Delete temporary files
	 */
	private void deleteTemporaryFiles() {
		if (temp1 == null || temp2 == null)
			return;
		temp1.deleteOnExit();
		temp2.deleteOnExit();
	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<>() {

			@Override
			protected Boolean call() throws Exception {
				boolean success = true;

				try {

					// Run on JavaFX Thread
					Platform.runLater(
							() -> xPlayerController.getWaveProgressLabel().setText("Generating Wave Spectrum..."));

					// Calculate
					if (waveFormJob == WaveFormJob.AMPLITUDES_AND_WAVEFORM) { // AMPLITUDES_AND_AMPLITUDES
						System.out.println("AMPLITUDES_AND_AMPLITUDES");
						String fileFormat = "mp3";
						resultingWaveform = processFromNoWavFile(fileFormat);

					} else if (waveFormJob == WaveFormJob.WAVEFORM) { // WAVEFORM
						resultingWaveform = processAmplitudes(wavAmplitudes);
					}
				} catch (Exception ex) {
					ex.printStackTrace();

					// Show not enough disk space error
					if (ex.getMessage().contains("There is not enough space on the disk"))
						AlertTool.showNotification("Error",
								"There is not enough space on the disk \n to create Wave Form Visualization",
								Duration.seconds(3), NotificationType.ERROR);

					return false;
				}

				return true;

			}

			/**
			 * Try to process a Non Wav File
			 *
			 * @param fileFormat The extension of the file
			 * @return
			 * @throws IOException
			 * @throws EncoderException
			 */
			private float[] processFromNoWavFile(String fileFormat)
					throws IOException, EncoderException {
				int randomN = random.nextInt(99999);

				// Create temporary files
				String title = IOInfo.getFileTitle(fileAbsolutePath);
				File temporalDecodedFile = File.createTempFile("decoded_" + title + randomN, ".wav");
				File temporalCopiedFile = File.createTempFile("original_" + title + randomN, "." + fileFormat);
				temp1 = temporalDecodedFile;
				temp2 = temporalCopiedFile;

				// Delete temporary Files on exit
				temporalDecodedFile.deleteOnExit();
				temporalCopiedFile.deleteOnExit();

				// Create a temporary path
				Files.copy(new File(fileAbsolutePath).toPath(), temporalCopiedFile.toPath(), options);

				// Transcode to .wav
				if (!transcodeToWav(temporalCopiedFile, temporalDecodedFile))
					return null;

				// Avoid creating amplitudes again for the same file
				if (wavAmplitudes == null)
					wavAmplitudes = getWavAmplitudes(temporalDecodedFile);

				// Delete temporary files
				temporalDecodedFile.delete();
				temporalCopiedFile.delete();

				return processAmplitudes(wavAmplitudes);
			}

			/**
			 * Get Wav Amplitudes
			 *
			 * @param file
			 * @return
			 */
			private int[] getWavAmplitudes(File file) {

				// Get Audio input stream
				try (AudioInputStream input = AudioSystem.getAudioInputStream(file)) {
					AudioFormat baseFormat = input.getFormat();

					// Encoding
					Encoding encoding = AudioFormat.Encoding.PCM_UNSIGNED;
					float sampleRate = baseFormat.getSampleRate();
					int numChannels = baseFormat.getChannels();

					AudioFormat decodedFormat = new AudioFormat(encoding, sampleRate, 16, numChannels, numChannels * 2,
							sampleRate, false);
					int available = input.available();

					// Get the PCM Decoded Audio Input Stream
					try (AudioInputStream pcmDecodedInput = AudioSystem.getAudioInputStream(decodedFormat, input)) {
						final int BUFFER_SIZE = 4096; // this is actually bytes

						// Create a buffer
						byte[] buffer = new byte[BUFFER_SIZE];

						// Now get the average to a smaller array
						int maximumArrayLength = 100000;
						int[] finalAmplitudes = new int[maximumArrayLength];
						int samplesPerPixel = available / maximumArrayLength;

						// Variables to calculate finalAmplitudes array
						int currentSampleCounter = 0;
						int arrayCellPosition = 0;
						float currentCellValue = 0.0f;

						// Variables for the loop
						int arrayCellValue;

						// Read all the available data on chunks
						while (pcmDecodedInput.readNBytes(buffer, 0, BUFFER_SIZE) > 0)
							for (int i = 0; i < buffer.length - 1; i += 2) {

								// Calculate the value
								arrayCellValue = (int) (((((buffer[i + 1] << 8) | buffer[i] & 0xff) << 16) / 32767)
										* WAVEFORM_HEIGHT_COEFFICIENT);

								// Every time you him [currentSampleCounter=samplesPerPixel]
								if (currentSampleCounter != samplesPerPixel) {
									++currentSampleCounter;
									currentCellValue += Math.abs(arrayCellValue);
								} else {
									// Avoid ArrayIndexOutOfBoundsException
									if (arrayCellPosition != maximumArrayLength)
										finalAmplitudes[arrayCellPosition] = finalAmplitudes[arrayCellPosition
												+ 1] = (int) currentCellValue / samplesPerPixel;

									// Fix the variables
									currentSampleCounter = 0;
									currentCellValue = 0;
									arrayCellPosition += 2;
								}
							}

						return finalAmplitudes;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} catch (Exception ex) {
					ex.printStackTrace();

				}

				// You don't want this to reach here...
				return new int[1];
			}

			/**
			 * Process the amplitudes
			 *
			 * @param sourcePcmData
			 * @return An array with amplitudes
			 */
			private float[] processAmplitudes(int[] sourcePcmData) {

				// The width of the resulting waveform panel
				int width = xPlayerController.getWaveFormVisualization().width;
				float[] waveData = new float[width];
				// Check if null
				if (sourcePcmData == null)
					return waveData;
				int samplesPerPixel = sourcePcmData.length / width;

				// Calculate
				float nValue;
				for (int w = 0; w < width; w++) {

					// For performance keep it here
					int c = w * samplesPerPixel;
					nValue = 0.0f;

					// Keep going
					for (int s = 0; s < samplesPerPixel; s++) {
						nValue += (Math.abs(sourcePcmData[c + s]) / 65536.0f);
					}

					// Set WaveData
					waveData[w] = nValue / samplesPerPixel;
				}

				return waveData;
			}

			/**
			 * Transcode to Wav
			 *
			 * @param sourceFile
			 * @param destinationFile
			 * @throws EncoderException
			 */
			private boolean transcodeToWav(File sourceFile, File destinationFile) throws EncoderException {
				try {

					// Set Audio Attributes
					AudioAttributes audio = new AudioAttributes();
					audio.setCodec("pcm_s16le");
					audio.setChannels(2);
					audio.setSamplingRate(44100);

					// Set encoding attributes
					EncodingAttributes attributes = new EncodingAttributes();
					attributes.setOutputFormat("wav");
					attributes.setAudioAttributes(audio);

					// Abort Encoding
					if (encoder != null)
						encoder.abortEncoding();

					// Encode
					encoder = encoder != null ? encoder : new Encoder();
					encoder.encode(new MultimediaObject(sourceFile), destinationFile, attributes, listener);
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}

				return true;
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

	public float[] getResultingWaveform() {
		return resultingWaveform;
	}

	public void setResultingWaveform(float[] resultingWaveform) {
		this.resultingWaveform = resultingWaveform;
	}

	public class ConvertProgressListener implements EncoderProgressListener {

		public ConvertProgressListener() {
		}

		public void message(String m) {
		}

		public void progress(int p) {

			double progress = p / 1000.00;
			Platform.runLater(() -> converterProgress.set(progress));
			System.out.println(progress);

		}

		public void sourceInfo(MultimediaInfo m) {
		}
	}

}
