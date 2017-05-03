/*
 * 
 */
package aacode_to_be_used_in_future;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * The Class ClipPlayer.
 */
public class ClipPlayer implements Runnable, LineListener {
	
	/** The m data source. */
	Object				m_dataSource;
	
	/** The m audio input stream. */
	AudioInputStream	m_audioInputStream;
	
	/** The clip. */
	Clip				clip;
	
	/**
	 * Instantiates a new clip player.
	 */
	// Constructor
	public ClipPlayer() {
		
	}
	
	/**
	 * Load sound.
	 *
	 * @param object the object
	 * @return true, if successful
	 */
	public boolean loadSound(Object object) {
		
		try {
			if (object instanceof URL) {
				m_dataSource = AudioSystem.getAudioInputStream((URL) object);
				
			} else if (object instanceof File) {
				m_dataSource = AudioSystem.getAudioInputStream((File) object);
				
			} else if (object instanceof InputStream) {
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		if (m_dataSource instanceof AudioInputStream) {
			try {
				m_audioInputStream = (AudioInputStream) m_dataSource;
				AudioFormat sourceFormat = m_audioInputStream.getFormat();
				int nSampleSizeInBits = sourceFormat.getSampleSizeInBits();
				
				// AudioFormat tmp = new
				// AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				// sourceFormat.getSampleRate(),
				// sourceFormat.getSampleSizeInBits() * 2,
				// sourceFormat.getChannels(), sourceFormat.getFrameSize() * 2,
				// sourceFormat.getFrameRate(), true);
				
				nSampleSizeInBits = ( sourceFormat.getEncoding() == AudioFormat.Encoding.ULAW
						|| sourceFormat.getEncoding() == AudioFormat.Encoding.ALAW || nSampleSizeInBits <= 0
						|| nSampleSizeInBits != 8 ) ? 16 : nSampleSizeInBits;
				
				/**
				 * we can't yet open the device for ALAW/ULAW playback, convert
				 * ALAW/ULAW to PCM
				 */
				AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
						sourceFormat.getSampleRate(), nSampleSizeInBits, sourceFormat.getChannels(),
						sourceFormat.getChannels() * ( nSampleSizeInBits / 8 ), sourceFormat.getFrameRate(), true);
				
				// Create decoded Stream
				m_audioInputStream = AudioSystem.getAudioInputStream(targetFormat, m_audioInputStream);
				DataLine.Info info = new DataLine.Info(Clip.class, m_audioInputStream.getFormat(),
						( (int) m_audioInputStream.getFrameLength() * targetFormat.getFrameSize() ));
				
				// Create Clip
				clip = (Clip) AudioSystem.getLine(info);
				clip.addLineListener(this);
				clip.open(m_audioInputStream);
				m_dataSource = clip;
				// clip.
				// seekSlider.setMaximum((int) stream.getFrameLength());
			} catch (Exception ex) {
				ex.printStackTrace();
				m_dataSource = null;
				return false;
			}
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
	}
	
	/* (non-Javadoc)
	 * @see javax.sound.sampled.LineListener#update(javax.sound.sampled.LineEvent)
	 */
	@Override
	public void update(LineEvent event) {
		
	}
	
}
