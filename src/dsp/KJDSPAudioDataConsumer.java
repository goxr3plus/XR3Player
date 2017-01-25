/*
 * 
 */

package dsp;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.SourceDataLine;

/**
 * The Class KJDSPAudioDataConsumer.
 *
 * @author Kris Fudalewski
 */
public class KJDSPAudioDataConsumer implements KJAudioDataConsumer {

    /** The Constant DEFAULT_SAMPLE_SIZE. */
    private static final int DEFAULT_SAMPLE_SIZE = 2048;

    /** The Constant DEFAULT_FPS. */
    private static final int DEFAULT_FPS = 70;

    /** The read write lock. */
    private Object readWriteLock = new Object();

    /** The source data line. */
    private SourceDataLine sourceDataLine;

    /** The sample size. */
    private int sampleSize;

    /** The fps as NS. */
    private long fpsAsNS;

    /** The desired fps as NS. */
    private long desiredFpsAsNS;

    /** The audio data buffer. */
    private byte[] audioDataBuffer;

    /** The left. */
    private float[] left;

    /** The right. */
    private float[] right;

    /** The position. */
    private int position;

    /** The offset. */
    private long offset;

    /** The signal processor. */
    private SignalProcessor signalProcessor;

    /** The dsps. */
    private ArrayList<KJDigitalSignalProcessor> dsps = new ArrayList<>();

    /** The sample type. */
    private SampleType sampleType;

    /** The channel mode. */
    private ChannelMode channelMode;

    /**
     * Indicates the Mode of the channel.
     *
     * @author GOXR3PLUS
     */
    public enum ChannelMode {

	/** The mono. */
	MONO,

	/** The stereo. */
	STEREO;
    }

    /**
     * Indicates the Type of The Sample.
     *
     * @author GOXR3PLUS
     */
    public enum SampleType {

	/** The eight bit. */
	EIGHT_BIT,

	/** The sixteen bit. */
	SIXTEEN_BIT;
    }

    /**
     * Default constructor creates a DSPAC with DEFAULT_SAMPLE_SIZE and
     * DEFAULT_FPS as parameters.
     */
    public KJDSPAudioDataConsumer() {
	this(DEFAULT_SAMPLE_SIZE, DEFAULT_FPS);
    }

    /**
     * Instantiates a new KJDSP audio data consumer.
     *
     * @param pSampleSize
     *            The sample size to extract from audio data sent to the
     *            SourceDataLine.
     * @param pFramesPerSecond
     *            The desired refresh rate per second of registered DSP's.
     */
    public KJDSPAudioDataConsumer(int pSampleSize, int pFramesPerSecond) {
	this(pSampleSize, pFramesPerSecond, SampleType.SIXTEEN_BIT, ChannelMode.STEREO);
    }

    /**
     * Instantiates a new KJDSP audio data consumer.
     *
     * @param pSampleSize
     *            The sample size to extract from audio data sent to the
     *            SourceDataLine.
     * @param pFramesPerSecond
     *            The desired refresh rate per second of registered DSP's.
     * @param pSampleType
     *            The sample type SAMPLE_TYPE_EIGHT_BIT or
     *            SAMPLE_TYPE_SIXTEEN_BIT.
     * @param pChannelMode
     *            The channel mode CHANNEL_MODE_MONO or CHANNEL_MODE_STEREO.
     */
    public KJDSPAudioDataConsumer(int pSampleSize, int pFramesPerSecond, SampleType pSampleType,
	    ChannelMode pChannelMode) {

	sampleSize = pSampleSize;
	desiredFpsAsNS = 1000000000L / (long) pFramesPerSecond;
	fpsAsNS = desiredFpsAsNS;

	setSampleType(pSampleType);
	setChannelMode(pChannelMode);

    }

    /**
     * Adds a DSP to the DSPAC and forwards any audio data to it at the specific
     * frame rate.
     * 
     * @param pSignalProcessor
     *            class implementing the KJDigitalSignalProcessor interface.
     */
    public void add(KJDigitalSignalProcessor pSignalProcessor) {
	dsps.add(pSignalProcessor);
    }

    /**
     * Removes the specified DSP from this DSPAC if it exists.
     * 
     * @param pSignalProcessor
     *            class implementing the KJDigitalSignalProcessor interface.
     */
    public void remove(KJDigitalSignalProcessor pSignalProcessor) {
	dsps.remove(pSignalProcessor);
    }

    /**
     * Set the Channel Mode.
     *
     * @param pChannelMode
     *            the new channel mode
     */
    public void setChannelMode(ChannelMode pChannelMode) {
	channelMode = pChannelMode;
    }

    /**
     * Set the sample Type.
     *
     * @param pSampleType
     *            the new sample type
     */
    public void setSampleType(SampleType pSampleType) {
	sampleType = pSampleType;
    }

    /**
     * Start monitoring the specified SourceDataLine.
     * 
     * @param pSdl
     *            A SourceDataLine.
     */
    public synchronized void start(SourceDataLine pSdl) {

	// -- Stop processing previous source data line.
	if (signalProcessor != null) {
	    stop();
	}

	if (signalProcessor == null) {

	    // System.out.println( "ADBS: " + pSdl.getBufferSize() )

	    sourceDataLine = pSdl;

	    // -- Allocate double the memory than the SDL to prevent
	    // buffer overlapping.
	    audioDataBuffer = new byte[pSdl.getBufferSize() << 1];

	    left = new float[sampleSize];
	    right = new float[sampleSize];

	    position = 0;
	    offset = 0;

	    signalProcessor = new SignalProcessor();

	    new Thread(signalProcessor).start();

	}

    }

    /**
     * Stop monitoring the currect SourceDataLine.
     */
    public synchronized void stop() {

	if (signalProcessor != null) {

	    signalProcessor.stop();
	    signalProcessor = null;

	    audioDataBuffer = null;
	    sourceDataLine = null;

	}

    }

    /**
     * Store audio data.
     *
     * @param pAudioData
     *            the audio data
     * @param pOffset
     *            the offset
     * @param pLength
     *            the length
     */
    private void storeAudioData(byte[] pAudioData, int pOffset, int pLength) {

	synchronized (readWriteLock) {

	    if (audioDataBuffer == null) {
		return;
	    }

	    int wOverrun = 0;

	    if (position + pLength > audioDataBuffer.length - 1) {

		wOverrun = (position + pLength) - audioDataBuffer.length;
		pLength = audioDataBuffer.length - position;

	    }

	    System.arraycopy(pAudioData, pOffset, audioDataBuffer, position, pLength);

	    if (wOverrun > 0) {

		System.arraycopy(pAudioData, pOffset + pLength, audioDataBuffer, 0, wOverrun);
		position = wOverrun;

	    } else {
		position += pLength;
	    }

	    // KJJukeBox.getDSPDialog().setDSPBufferInfo(
	    // position,
	    // pOffset,
	    // pLength,
	    // audioDataBuffer.length )

	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see dsp.KJAudioDataConsumer#writeAudioData(byte[])
     */
    @Override
    public void writeAudioData(byte[] pAudioData) {
	storeAudioData(pAudioData, 0, pAudioData.length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see dsp.KJAudioDataConsumer#writeAudioData(byte[], int, int)
     */
    @Override
    public void writeAudioData(byte[] pAudioData, int pOffset, int pLength) {
	storeAudioData(pAudioData, pOffset, pLength);
    }

    /**
     * The Class SignalProcessor.
     */
    private class SignalProcessor implements Runnable {

	/** The process. */
	boolean process = true;

	/** The lfp. */
	long lfp = 0;

	/** The frame size. */
	int frameSize;

	/**
	 * Instantiates a new signal processor.
	 */
	public SignalProcessor() {
	    frameSize = sourceDataLine.getFormat().getFrameSize();
	}

	/**
	 * Calculate sample position.
	 *
	 * @return the int
	 */
	private int calculateSamplePosition() {

	    synchronized (readWriteLock) {

		long wFp = sourceDataLine.getLongFramePosition();
		long wNfp = lfp;

		lfp = wFp;

		// int wSdp =
		return (int) ((wNfp * frameSize) - (audioDataBuffer.length * offset));

		// KJJukeBox.getDSPDialog().setOutputPositionInfo(
		// wFp,
		// wFp - wNfp,
		// wSdp )

	    }

	}

	/**
	 * This method is processing the samples.
	 *
	 * @param pPosition
	 *            the position
	 */
	private void processSamples(int pPosition) {

	    int c = pPosition;

	    if (channelMode == ChannelMode.MONO && sampleType == SampleType.EIGHT_BIT) {

		for (int a = 0; a < sampleSize; a++, c++) {

		    if (c >= audioDataBuffer.length) {
			offset++;
			c = c - audioDataBuffer.length;
		    }

		    left[a] = (int) audioDataBuffer[c] / 128.0f;
		    right[a] = left[a];

		}

	    } else if (channelMode == ChannelMode.STEREO && sampleType == SampleType.EIGHT_BIT) {

		for (int a = 0; a < sampleSize; a++, c += 2) {

		    if (c >= audioDataBuffer.length) {
			offset++;
			c = c - audioDataBuffer.length;
		    }

		    left[a] = (int) audioDataBuffer[c] / 128.0f;
		    right[a] = (int) audioDataBuffer[c + 1] / 128.0f;

		}

	    } else if (channelMode == ChannelMode.MONO && sampleType == SampleType.SIXTEEN_BIT) {

		for (int a = 0; a < sampleSize; a++, c += 2) {

		    if (c >= audioDataBuffer.length) {
			offset++;
			c = c - audioDataBuffer.length;
		    }

		    left[a] = (float) (((int) audioDataBuffer[c + 1] << 8) + (audioDataBuffer[c] & 0xff)) / 32767.0f;
		    right[a] = left[a];

		}

	    } else if (channelMode == ChannelMode.STEREO && sampleType == SampleType.SIXTEEN_BIT) {

		for (int a = 0; a < sampleSize; a++, c += 4) {

		    if (c >= audioDataBuffer.length) {
			offset++;
			c = c - audioDataBuffer.length;
		    }

		    left[a] = (float) (((int) audioDataBuffer[c + 1] << 8) + (audioDataBuffer[c] & 0xff)) / 32767.0f;
		    right[a] = (float) (((int) audioDataBuffer[c + 3] << 8) + (audioDataBuffer[c + 2] & 0xff))
			    / 32767.0f;

		}

	    }

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

	    while (process) {

		try {

		    long wStn = System.nanoTime();

		    int wSdp = calculateSamplePosition();

		    if (wSdp > 0) {
			processSamples(wSdp);
		    }

		    // -- Dispatch sample data to digital signal processors.
		    for (int a = 0; a < dsps.size(); a++) {

			// -- Calculate the frame rate ratio hint. This value
			// can be used by
			// animated DSP's to fast forward animation frames to
			// make up for
			// inconsistencies with the frame rate.
			float wFrr = (float) fpsAsNS / (float) desiredFpsAsNS;

			try {
			    dsps.get(a).process(left, right, wFrr);
			} catch (Exception ex) {
			    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "- DSP Exception: ", ex);
			}
		    }

		    // KJJukeBox.getDSPDialog().setDSPInformation(
		    // String.valueOf( 1000.0f / ( (float)( wEtn - wStn ) /
		    // 1000000.0f ) ) )

		    // System.out.println( 1000.0f / ( (float)( wEtn - wStn ) /
		    // 1000000.0f ) )

		    long wDelay = fpsAsNS - (System.nanoTime() - wStn);

		    // -- No DSP registered? Put the the DSP thread to sleep.
		    if (dsps.isEmpty()) {
			wDelay = 1000000000; // -- 1 second.
		    }

		    if (wDelay > 0) {

			try {
			    Thread.sleep(wDelay / 1000000, (int) wDelay % 1000000);
			} catch (Exception ex) {
			    Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
			}

			// -- Adjust FPS until we meet the "desired FPS".
			if (fpsAsNS > desiredFpsAsNS) {
			    fpsAsNS -= wDelay;
			} else {
			    fpsAsNS = desiredFpsAsNS;
			}

		    } else {

			// -- Reduce FPS because we cannot keep up with the
			// "desired FPS".
			fpsAsNS += -wDelay;

			// -- Keep thread from hogging CPU.
			try {
			    Thread.sleep(10);
			} catch (InterruptedException ex) {
			    Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
			}

		    }

		} catch (Exception ex) {
		    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "- DSP Exception: ", ex);
		}

	    }

	}

	/**
	 * Stop.
	 */
	public void stop() {
	    process = false;
	}

    }

}
