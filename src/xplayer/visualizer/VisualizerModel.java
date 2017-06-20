/*
 * 
 */
package xplayer.visualizer;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.SourceDataLine;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import xplayer.dsp.KJDSPAudioDataConsumer;
import xplayer.dsp.KJDigitalSignalProcessor;
import xplayer.dsp.KJFFT;

/**
 * This SuperClass represents the model of the Visualizer.
 *
 * @author GOXR3PLUS
 */
public class VisualizerModel extends ResizableCanvas implements KJDigitalSignalProcessor {
	
	/** The Constant log. */
	private static final Logger logger = Logger.getLogger(VisualizerModel.class.getName());
	
	/**
	 * The width of the canvas
	 */
	public int canvasWidth = 0;
	/**
	 * The height of the canvas
	 */
	public int canvasHeight = 0;
	/**
	 * Half the height of the canvas
	 */
	public int halfCanvasHeight = 0;
	
	/** The left. */
	protected float[] pLeftChannel = new float[1024];
	
	/** The right. */
	protected float[] pRightChannel = new float[1024];
	
	/** The frame rate ratio hint. */
	protected float frameRateRatioHint;
	
	/** The w sadfrr. */
	private float wSadfrr;
	
	/** The w FFT. */
	private float[] wFFT;
	
	/** The w fs. */
	private float wFs;
	
	/**
	 * The maximum that the display mode can reach
	 */
	public final static int DISPLAYMODE_MAXIMUM = DisplayMode.values().length - 2; //-1 cause i count from 0
	
	/** The display mode. */
	public final SimpleIntegerProperty displayMode = new SimpleIntegerProperty(Integer.parseInt(DisplayMode.CIRCLE_WITH_LINES.toString()));
	
	/** The Constant DEFAULT_FPS. */
	private static final int DEFAULT_FPS = 60;
	
	/** The Constant DEFAULT_SPECTRUM_ANALYSER_FFT_SAMPLE_SIZE. */
	private static final int DEFAULT_SPECTRUM_ANALYSER_FFT_SAMPLE_SIZE = 512;
	
	/** The Constant DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT. */
	private static final int DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT = 50;
	
	/** The Constant DEFAULT_SPECTRUM_ANALYSER_DECAY. */
	private static final float DEFAULT_SPECTRUM_ANALYSER_DECAY = 0.05f;
	
	/** The Constant DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY. */
	private static final int DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY = 20;
	
	/** The Constant DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY_FPS_RATIO. */
	private static final float DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY_FPS_RATIO = 0.4f;
	
	/** The Constant DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY_FPS_RATIO_RANGE. */
	private static final float DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY_FPS_RATIO_RANGE = 0.1f;
	
	/** The Constant MIN_SPECTRUM_ANALYSER_DECAY. */
	private static final float MIN_SPECTRUM_ANALYSER_DECAY = 0.02f;
	
	/** The Constant MAX_SPECTRUM_ANALYSER_DECAY. */
	private static final float MAX_SPECTRUM_ANALYSER_DECAY = 1.0f;
	
	/** The Constant DEFAULT_VU_METER_DECAY. */
	private static final float DEFAULT_VU_METER_DECAY = 0.02f;
	
	/** The scope color. */
	protected Color scopeColor;
	
	/** The spectrum analyser colors. */
	static Color[] spectrumAnalyserColors = getDefaultSpectrumAnalyserColors();
	
	/** The dsp. */
	private KJDSPAudioDataConsumer dsp = null;
	
	/** The dsp has started. */
	private boolean dspHasStarted = false;
	
	/** The peak color. */
	protected Color peakColor = null;
	
	/** The peaks. */
	protected int[] peaks = new int[DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT];
	
	/** The peaks delay. */
	protected int[] peaksDelay = new int[DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT];
	
	/** The peak delay. */
	protected int peakDelay = DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY;
	
	/** The peaks enabled. */
	protected boolean peaksEnabled = true;
	
	/** The bar offset. */
	protected int barOffset = 1;
	
	// -- Spectrum analyzer variables.
	/** The fft. */
	protected KJFFT fft;
	
	/** The old FFT. */
	protected float[] oldFFT;
	
	/** The sa FFT sample size. */
	private int saFFTSampleSize;
	
	/** The sa bands. */
	protected int saBands;
	
	/** The sa color scale. */
	protected float saColorScale;
	
	/** The sa multiplier. */
	protected float saMultiplier;
	
	/** The sa decay. */
	protected float saDecay = DEFAULT_SPECTRUM_ANALYSER_DECAY;
	
	/** The source data line. */
	protected SourceDataLine sourceDataLine = null;
	
	/** The old left. */
	// -- VU Meter
	protected float oldLeft;
	
	/** The old right. */
	protected float oldRight;
	
	/** The vu decay. */
	protected float vuDecay = DEFAULT_VU_METER_DECAY;
	
	/** The vu color scale. */
	protected float vuColorScale;
	
	/** The frames per second. */
	// -- FPS calculations.
	protected int framesPerSecond = 0;
	
	/** The fps. */
	public int fps = DEFAULT_FPS;
	
	/** The show FPS. */
	public boolean showFPS = false;
	
	/**
	 * Default Constructor.
	 */
	public VisualizerModel() {
		
		// ----------------------
		setFramesPerSecond(DEFAULT_FPS);
		setPeakDelay((int) ( DEFAULT_FPS * DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY_FPS_RATIO ));
		
		setSpectrumAnalyserFFTSampleSize(DEFAULT_SPECTRUM_ANALYSER_FFT_SAMPLE_SIZE);
		setSpectrumAnalyserDecay(DEFAULT_SPECTRUM_ANALYSER_DECAY);
		setSpectrumAnalyserBandCount(DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT);
		
		setPeakColor(Color.WHITE);
	}
	
	/*-----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							Methods
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	
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
	@Override
	public synchronized void process(float[] pLeftChannel , float[] pRightChannel , float pFrameRateRatioHint) {
		
		this.pLeftChannel = pLeftChannel;
		this.pRightChannel = pRightChannel;
		this.frameRateRatioHint = pFrameRateRatioHint;
	}
	
	/**
	 * Setup DSP.
	 *
	 * @param line
	 *        the new up DSP
	 */
	public void setupDSP(SourceDataLine line) {
		if (dsp != null) {
			// Number of Channels
			dsp.setChannelMode(
					line.getFormat().getChannels() == 1 ? KJDSPAudioDataConsumer.ChannelMode.MONO : KJDSPAudioDataConsumer.ChannelMode.STEREO);
			
			// SampleSizeInBits
			dsp.setSampleType(line.getFormat().getSampleSizeInBits() == 8 ? KJDSPAudioDataConsumer.SampleType.EIGHT_BIT
					: KJDSPAudioDataConsumer.SampleType.SIXTEEN_BIT);
		}
	}
	
	/**
	 * Starts DSP.
	 *
	 * @param line
	 *        the line
	 */
	public void startDSP(SourceDataLine line) {
		if (line != null)
			sourceDataLine = line;
		
		// dsp null?
		if (dsp == null) {
			dsp = new KJDSPAudioDataConsumer(2048, fps);
			dsp.add(this);
		}
		
		if (sourceDataLine != null) {
			if (dspHasStarted)
				stopDSP();
			
			dsp.start(sourceDataLine);
			dspHasStarted = true;
			logger.info("DSP started");
		}
	}
	
	/**
	 * Stop DSP.
	 */
	public void stopDSP() {
		if (dsp != null) {
			dsp.stop();
			dspHasStarted = false;
			logger.setLevel(Level.INFO);
			logger.info("DSP stopped");
		}
	}
	
	/**
	 * Close DSP.
	 */
	public void closeDSP() {
		if (dsp != null) {
			stopDSP();
			dsp = null;
			logger.info("DSP CLOSSED");
		}
	}
	
	/**
	 * Write PCM data to DSP.
	 *
	 * @param pcmdata
	 *        the pcmdata
	 */
	public void writeDSP(byte[] pcmdata) {
		if (dsp != null)
			dsp.writeAudioData(pcmdata);
	}
	
	/**
	 * Clears the Canvas from the Previous Painting.
	 */
	public void clear() {
		gc.clearRect(0, 0, getWidth(), getHeight());
	}
	
	/*-----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							GETTERS
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	
	/**
	 * Return DSP.
	 * 
	 * @return KJDSPAudioDataConsumer
	 */
	public KJDSPAudioDataConsumer getDSP() {
		return dsp;
	}
	
	/**
	 * Checks if is peaks enabled.
	 *
	 * @return true, if is peaks enabled
	 */
	public boolean isPeaksEnabled() {
		return peaksEnabled;
	}
	
	/**
	 * Gets the frames per second.
	 *
	 * @return the frames per second
	 */
	public int getFramesPerSecond() {
		return fps;
	}
	
	/**
	 * Return peak fall off delay.
	 *
	 * @return peak fall off delay
	 */
	public int getPeakDelay() {
		return peakDelay;
	}
	
	/**
	 * Gets the visualizer width.
	 *
	 * @return the visualizer width
	 */
	public int getVisualizerWidth() {
		return canvasWidth;
	}
	
	/**
	 * Gets the visualizer height.
	 *
	 * @return the visualizer height
	 */
	public int getVisualizerHeight() {
		return canvasHeight;
	}
	
	/**
	 * Gets the default spectrum analyzer colors. Colors are starting from green
	 * and ending to red.
	 *
	 * @return the default spectrum analyzer colors
	 */
	public static Color[] getDefaultSpectrumAnalyserColors() {
		Color[] wColors = new Color[256];
		
		for (int a = 0; a < 128; a++)
			wColors[a] = Color.rgb(0, ( a >> 1 ) + 192, 0);
		
		for (int a = 0; a < 64; a++)
			wColors[a + 128] = Color.rgb(a << 2, 255, 0);
		
		for (int a = 0; a < 64; a++)
			wColors[a + 192] = Color.rgb(255, 255 - ( a << 2 ), 0);
		
		return wColors;
	}
	
	/**
	 * Gets the display mode.
	 *
	 * @return Returns the current display mode, DISPLAY_MODE_SCOPE or
	 *         DISPLAY_MODE_SPECTRUM_ANALYSER or DISPLAY_MODE_VUMETER.
	 */
	public synchronized int getDisplayMode() {
		return displayMode.get();
	}
	
	/**
	 * Gets the spectrum analyser band count.
	 *
	 * @return Returns the current number of bands displayed by the spectrum
	 *         analyser.
	 */
	public synchronized int getSpectrumAnalyserBandCount() {
		return saBands;
	}
	
	/**
	 * Gets the spectrum analyser decay.
	 *
	 * @return Returns the decay rate of the spectrum analyser's bands.
	 */
	public synchronized float getSpectrumAnalyserDecay() {
		return saDecay;
	}
	
	/**
	 * Gets the scope color.
	 *
	 * @return Returns the color the scope is rendered in.
	 */
	public synchronized Color getScopeColor() {
		return scopeColor;
	}
	
	/**
	 * Gets the spectrum analyser colors.
	 *
	 * @return Returns the color scale used to render the spectrum analyser
	 *         bars.
	 */
	public synchronized Color[] getSpectrumAnalyserColors() {
		return spectrumAnalyserColors;
	}
	
	/**
	 * Checks if is showing FPS.
	 *
	 * @return Returns 'true' if "Frames Per Second" are being calculated and
	 *         displayed.
	 */
	public boolean isShowingFPS() {
		return showFPS;
	}
	
	/**
	 * Compute color scale.
	 */
	public void computeColorScale() {
		saColorScale = ( (float) spectrumAnalyserColors.length / canvasHeight ) * barOffset * 1.0f;
		vuColorScale = ( (float) spectrumAnalyserColors.length / ( canvasWidth - 32 ) ) * 2.0f;
	}
	
	/**
	 * Compute SA multiplier.
	 */
	private void computeSAMultiplier() {
		saMultiplier = (float) ( ( saFFTSampleSize / 2.00 ) / saBands );
	}
	
	/*-----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							SETTERS
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	
	/**
	 * Sets the peaks enabled.
	 *
	 * @param peaksEnabled
	 *        the new peaks enabled
	 */
	public void setPeaksEnabled(boolean peaksEnabled) {
		this.peaksEnabled = peaksEnabled;
	}
	
	/**
	 * Set visual peak color.
	 *
	 * @param c
	 *        the new peak color
	 */
	public void setPeakColor(Color c) {
		peakColor = c;
	}
	
	/**
	 * Set peak fall off delay.
	 *
	 * @param waitFPS
	 *        the new peak delay
	 */
	public void setPeakDelay(int waitFPS) {
		int min = Math.round( ( DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY_FPS_RATIO - DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY_FPS_RATIO_RANGE ) * fps);
		int max = Math.round( ( DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY_FPS_RATIO + DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY_FPS_RATIO_RANGE ) * fps);
		if ( ( waitFPS >= min ) && ( waitFPS <= max )) {
			peakDelay = waitFPS;
		} else {
			peakDelay = Math.round(DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY_FPS_RATIO * fps);
		}
	}
	
	/**
	 * Sets the frames per second.
	 *
	 * @param fps
	 *        the new frames per second
	 */
	public void setFramesPerSecond(int fps) {
		this.fps = fps;
	}
	
	/**
	 * Sets the current display mode.
	 *
	 * @param pMode
	 *        the new display mode
	 */
	public synchronized void setDisplayMode(int pMode) {
		displayMode.set(pMode);
	}
	
	/**
	 * Sets the color of the scope.
	 *
	 * @param pColor
	 *        the new scope color
	 */
	public synchronized void setScopeColor(Color pColor) {
		scopeColor = pColor;
	}
	
	/**
	 * When 'true' is passed as a parameter, will overlay the "Frames Per
	 * Seconds" achieved by the component.
	 *
	 * @param pState
	 *        the new show FPS
	 */
	public synchronized void setShowFPS(boolean pState) {
		showFPS = pState;
	}
	
	/**
	 * Sets the numbers of bands rendered by the spectrum analyser.
	 *
	 * @param pCount
	 *        Cannot be more than half the "FFT sample size".
	 */
	public synchronized void setSpectrumAnalyserBandCount(int pCount) {
		
		saBands = pCount;
		peaks = new int[saBands];
		peaksDelay = new int[saBands];
		computeSAMultiplier();
	}
	
	/**
	 * Sets the spectrum analyzer band decay rate.
	 *
	 * @param pDecay
	 *        Must be a number between 0.0 and 1.0 exclusive.
	 */
	public synchronized void setSpectrumAnalyserDecay(float pDecay) {
		if ( ( pDecay >= MIN_SPECTRUM_ANALYSER_DECAY ) && ( pDecay <= MAX_SPECTRUM_ANALYSER_DECAY )) {
			saDecay = pDecay;
		} else
			saDecay = DEFAULT_SPECTRUM_ANALYSER_DECAY;
	}
	
	/**
	 * Sets the spectrum analyzer color scale.
	 *
	 * @param pColors
	 *        Any amount of colors may be used. Must not be null.
	 */
	public synchronized void setSpectrumAnalyserColors(Color[] pColors) {
		spectrumAnalyserColors = pColors;
		computeColorScale();
	}
	
	/**
	 * Sets the FFT sample size to be just for calculating the spectrum analyzer
	 * values. The default is 512.
	 *
	 * @param pSize
	 *        Cannot be more than the size of the sample provided by the
	 *        DSP.
	 */
	public synchronized void setSpectrumAnalyserFFTSampleSize(int pSize) {
		saFFTSampleSize = pSize;
		fft = new KJFFT(saFFTSampleSize);
		oldFFT = new float[saFFTSampleSize];
		computeSAMultiplier();
	}
	
	/**
	 * Stereo merge.
	 *
	 * @param pLeft
	 *        the left
	 * @param pRight
	 *        the right
	 * @return A float[] array from merging left and right speakers
	 */
	public float[] stereoMerge(float[] pLeft , float[] pRight) {
		for (int a = 0; a < pLeft.length; a++)
			pLeft[a] = ( pLeft[a] + pRight[a] ) / 2.0f;
		
		return pLeft;
	}
	
	/**
	 * Returns an array which has length<array length> and contains frequencies
	 * in every cell which has a value from 0.00 to 1.00.
	 *
	 * @param pSample
	 *        the sample
	 * @param arrayLength
	 *        the array length
	 * @return An array which has length<array length> and contains frequencies
	 *         in every cell which has a value from 0.00 to 1.00.
	 */
	public float[] returnBandsArray(float[] pSample , int arrayLength) {
		
		wFFT = fft.calculate(pSample);
		wSadfrr = saDecay * frameRateRatioHint;
		wFs = 0;
		float[] array = new float[arrayLength];
		for (int a = 0, band = 0; band < array.length; a += saMultiplier, band++) {
			wFs = 0;
			
			// -- Average out nearest bands.
			for (int b = 0; b < saMultiplier; b++)
				wFs += wFFT[a + b];
			
			// -- Log filter.
			wFs = ( wFs = wFs * (float) Math.log(band + 2.00) ) > 1.0f ? 1.0f : wFs;
			// wFs = (wFs > 1.0f) ? 1.0f : wFs
			
			// -- Compute SA decay...
			if (wFs >= ( oldFFT[a] - wSadfrr ))
				oldFFT[a] = wFs;
			else {
				oldFFT[a] -= wSadfrr;
				if (oldFFT[a] < 0)
					oldFFT[a] = 0;
				
				wFs = oldFFT[a];
			}
			
			array[band] = wFs;
		}
		
		return array;
	}
	
	/*-----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							GETTERS
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	
	/**
	 * Visualizer Display Mode.
	 *
	 * @author GOXR3PLUS
	 */
	public enum DisplayMode {
		
		/** OSCILLOSCOPE */
		OSCILLOSCOPE {
			@Override
			public String toString() {
				return "0";
			}
		},
		
		/** OSCILLOSCOPE */
		STEREO_OSCILLOSCOPE {
			@Override
			public String toString() {
				return "1";
			}
		},
		
		/** OSCILLOSCOPE */
		OSCILLOSCOPE_LINES {
			@Override
			public String toString() {
				return "2";
			}
		},
		
		/** The display spectrum bars. */
		SPECTRUM_BARS {
			@Override
			public String toString() {
				return "3";
			}
		},
		
		/** Display a VOLUME_METER */
		VOLUME_METER {
			@Override
			public String toString() {
				return "4";
			}
		},
		/** The display rosette with polyspiral. */
		ROSETTE {
			@Override
			public String toString() {
				return "5";
			}
		},
		/** Display A Circle With Lines on it's circumference */
		CIRCLE_WITH_LINES {
			@Override
			public String toString() {
				return "6";
			}
		},
		/** Display Sierpinski Triangles */
		SIERPINSKI {
			@Override
			public String toString() {
				return "7";
			}
		},
		/** Display SPRITE3D */
		SPRITE3D {
			@Override
			public String toString() {
				return "8";
			}
		},
		/** Display Julia Fractals */
		JULIAFRACTALS {
			@Override
			public String toString() {
				return "9";
			}
		}
	}
	
}
