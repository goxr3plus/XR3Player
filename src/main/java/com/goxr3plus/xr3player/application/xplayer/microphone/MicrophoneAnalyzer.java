package main.java.com.goxr3plus.xr3player.application.xplayer.microphone;

import javax.sound.sampled.AudioFileFormat;

import com.darkprograms.speech.util.Complex;
import com.darkprograms.speech.util.FFT;

/********************************************************************************************
 * Microphone Analyzer class, detects pitch and volume while extending the microphone class. Implemented as a precursor to a Voice Activity Detection
 * (VAD) algorithm. Currently can be used for audio data analysis. Dependencies: FFT.java and Complex.java. Both found in the utility package.
 * 
 * @author Aaron Gokaslan
 ********************************************************************************************/

public class MicrophoneAnalyzer extends Microphone {
	
	/**
	 * Constructor
	 * 
	 * @param fileType
	 *            The file type you want to save in. FLAC recommended.
	 */
	public MicrophoneAnalyzer(AudioFileFormat.Type fileType) {
		super(fileType);
	}
	
	/**
	 * Gets the volume of the microphone input Interval is 100ms so allow 100ms for this method to run in your code or specify smaller interval.
	 * 
	 * @return The volume of the microphone input or -1 if data-line is not available
	 */
	public int getAudioVolume() {
		return getAudioVolume(100);
	}
	
	/**
	 * Gets the volume of the microphone input
	 * 
	 * @param interval:
	 *            The length of time you would like to calculate the volume over in milliseconds.
	 * @return The volume of the microphone input or -1 if data-line is not available.
	 */
	public int getAudioVolume(int interval) {
		return calculateAudioVolume(this.getNumOfBytes(interval / 1000d));
	}
	
	/**
	 * Gets the volume of microphone input
	 * 
	 * @param numOfBytes
	 *            The number of bytes you want for volume interpretation
	 * @return The volume over the specified number of bytes or -1 if data-line is unavailable.
	 */
	private int calculateAudioVolume(int numOfBytes) {
		byte[] data = getBytes(numOfBytes);
		if (data == null)
			return -1;
		return calculateRMSLevel(data);
	}
	
	/**
	 * Calculates the volume of AudioData which may be buffered data from a data-line.
	 * 
	 * @param audioData
	 *            The byte[] you want to determine the volume of
	 * @return the calculated volume of audioData
	 */
	public static int calculateRMSLevel(byte[] audioData) {
		long lSum = 0;
		for (int i = 0; i < audioData.length; i++)
			lSum = lSum + audioData[i];
		
		double dAvg = lSum / audioData.length;
		
		double sumMeanSquare = 0d;
		for (int j = 0; j < audioData.length; j++)
			sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);
		
		double averageMeanSquare = sumMeanSquare / audioData.length;
		return (int) ( Math.pow(averageMeanSquare, 0.5d) + 0.5 );
	}
	
	/**
	 * Returns the number of bytes over interval for useful when figuring out how long to record.
	 * 
	 * @param seconds
	 *            The length in seconds
	 * @return the number of bytes the microphone will save.
	 */
	public int getNumOfBytes(int seconds) {
		return getNumOfBytes((double) seconds);
	}
	
	/**
	 * Returns the number of bytes over interval for useful when figuring out how long to record.
	 * 
	 * @param seconds
	 *            The length in seconds
	 * @return the number of bytes the microphone will output over the specified time.
	 */
	public int getNumOfBytes(double seconds) {
		return (int) ( seconds * getAudioFormat().getSampleRate() * getAudioFormat().getFrameSize() + .5 );
	}
	
	/**
	 * Returns the a byte[] containing the specified number of bytes
	 * 
	 * @param numOfBytes
	 *            The length of the returned array.
	 * @return The specified array or null if it cannot.
	 */
	private byte[] getBytes(int numOfBytes) {
		if (getTargetDataLine() != null) {
			byte[] data = new byte[numOfBytes];
			this.getTargetDataLine().read(data, 0, numOfBytes);
			return data;
		}
		return null;//If data cannot be read, returns a null array.
	}
	
	/**
	 * Calculates the fundamental frequency. In other words, it calculates pitch, except pitch is far more subjective and subtle. Also note, that
	 * readings may occasionally, be in error due to the complex nature of sound. This feature is in Beta
	 * 
	 * @return The frequency of the sound in Hertz.
	 */
	public int getFrequency() {
		try {
			return getFrequency(4096);
		} catch (Exception e) {
			//This will never happen. Ever...
			return -666;
		}
	}
	
	/**
	 * Calculates the frequency based off of the number of bytes. CAVEAT: THE NUMBER OF BYTES MUST BE A MULTIPLE OF 2!!!
	 * 
	 * @param numOfBytes
	 *            The number of bytes which must be a multiple of 2!!!
	 * @return The calculated frequency in Hertz.
	 */
	public int getFrequency(int numOfBytes) throws Exception {
		if (getTargetDataLine() == null) {
			return -1;
		}
		byte[] data = new byte[numOfBytes + 1];//One byte is lost during conversion
		this.getTargetDataLine().read(data, 0, numOfBytes);
		return getFrequency(data);
	}
	
	/**
	 * Calculates the frequency based off of the byte array,
	 * 
	 * @param bytes
	 *            The audioData you want to analyze
	 * @return The calculated frequency in Hertz.
	 */
	public int getFrequency(byte[] bytes) {
		double[] audioData = this.bytesToDoubleArray(bytes);
		audioData = applyHanningWindow(audioData);
		Complex[] complex = new Complex[audioData.length];
		for (int i = 0; i < complex.length; i++) {
			complex[i] = new Complex(audioData[i], 0);
		}
		Complex[] fftTransformed = FFT.fft(complex);
		return this.calculateFundamentalFrequency(fftTransformed, 4);
	}
	
	/**
	 * Applies a Hanning Window to the data set. Hanning Windows are used to increase the accuracy of the FFT. One should always apply a window to a
	 * dataset before applying an FFT
	 * 
	 * @param The
	 *            data you want to apply the window to
	 * @return The windowed data set
	 */
	private double[] applyHanningWindow(double[] data) {
		return applyHanningWindow(data, 0, data.length);
	}
	
	/**
	 * Applies a Hanning Window to the data set. Hanning Windows are used to increase the accuracy of the FFT. One should always apply a window to a
	 * dataset before applying an FFT
	 * 
	 * @param The
	 *            data you want to apply the window to
	 * @param The
	 *            starting index you want to apply a window from
	 * @param The
	 *            size of the window
	 * @return The windowed data set
	 */
	private double[] applyHanningWindow(double[] signal_in , int pos , int size) {
		for (int i = pos; i < pos + size; i++) {
			int j = i - pos; // j = index into Hann window function
			signal_in[i] = ( signal_in[i] * 0.5 * ( 1.0 - Math.cos(2.0 * Math.PI * j / size) ) );
		}
		return signal_in;
	}
	
	/**
	 * This method calculates the fundamental frequency using Harmonic Product Specturm It down samples the FFTData four times and multiplies the
	 * arrays together to determine the fundamental frequency. This is slightly more computationally expensive, but much more accurate. In simpler
	 * terms, the function will remove the harmonic frequencies which occur at every N value by finding the lowest common divisor among them.
	 * 
	 * @param fftData
	 *            The array returned by the FFT
	 * @param N
	 *            the number of times you wish to downsample. WARNING: The more times you downsample, the lower the maximum detectable frequency is.
	 * @return The fundamental frequency in Hertz
	 */
	private int calculateFundamentalFrequency(Complex[] fftData , int N) {
		if (N <= 0 || fftData == null) {
			return -1;
		} //error case
		
		final int LENGTH = fftData.length;//Used to calculate bin size
		fftData = removeNegativeFrequencies(fftData);
		Complex[][] data = new Complex[N][fftData.length / N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < data[0].length; j++) {
				data[i][j] = fftData[j * ( i + 1 )];
			}
		}
		Complex[] result = new Complex[fftData.length / N];//Combines the arrays
		for (int i = 0; i < result.length; i++) {
			Complex tmp = new Complex(1, 0);
			for (int j = 0; j < N; j++) {
				tmp = tmp.times(data[j][i]);
			}
			result[i] = tmp;
		}
		int index = this.findMaxMagnitude(result);
		return index * getFFTBinSize(LENGTH);
	}
	
	/**
	 * Removes useless data from transform since sound doesn't use complex numbers.
	 * 
	 * @param The
	 *            data you want to remove the complex transforms from
	 * @return The cleaned data
	 */
	private Complex[] removeNegativeFrequencies(Complex[] c) {
		Complex[] out = new Complex[c.length / 2];
		for (int i = 0; i < out.length; i++) {
			out[i] = c[i];
		}
		return out;
	}
	
	/**
	 * Calculates the FFTbin size based off the length of the the array Each FFTBin size represents the range of frequencies treated as one. For
	 * example, if the bin size is 5 then the algorithm is precise to within 5hz. Precondition: length cannot be 0.
	 * 
	 * @param fftDataLength
	 *            The length of the array used to feed the FFT algorithm
	 * @return FFTBin size
	 */
	private int getFFTBinSize(int fftDataLength) {
		return (int) ( getAudioFormat().getSampleRate() / fftDataLength + .5 );
	}
	
	/**
	 * Calculates index of the maximum magnitude in a complex array.
	 * 
	 * @param The
	 *            Complex[] you want to get max magnitude from.
	 * @return The index of the max magnitude
	 */
	private int findMaxMagnitude(Complex[] input) {
		//Calculates Maximum Magnitude of the array
		double max = Double.MIN_VALUE;
		int index = -1;
		for (int i = 0; i < input.length; i++) {
			Complex c = input[i];
			double tmp = c.getMagnitude();
			if (tmp > max) {
				max = tmp;
				;
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * Converts bytes from a TargetDataLine into a double[] allowing the information to be read. NOTE: One byte is lost in the conversion so don't
	 * expect the arrays to be the same length!
	 * 
	 * @param bufferData
	 *            The buffer read in from the target data line
	 * @return The double[] that the buffer has been converted into.
	 */
	private double[] bytesToDoubleArray(byte[] bufferData) {
		final int bytesRecorded = bufferData.length;
		final int bytesPerSample = getAudioFormat().getSampleSizeInBits() / 8;
		final double amplification = 100.0; // choose a number as you like
		double[] micBufferData = new double[bytesRecorded - bytesPerSample + 1];
		for (int index = 0, floatIndex = 0; index < bytesRecorded - bytesPerSample + 1; index += bytesPerSample, floatIndex++) {
			double sample = 0;
			for (int b = 0; b < bytesPerSample; b++) {
				int v = bufferData[index + b];
				if (b < bytesPerSample - 1 || bytesPerSample == 1) {
					v &= 0xFF;
				}
				sample += v << ( b * 8 );
			}
			double sample32 = amplification * ( sample / 32768.0 );
			micBufferData[floatIndex] = sample32;
			
		}
		return micBufferData;
	}
	
}
