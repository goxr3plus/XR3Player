package main.java.com.goxr3plus.xr3player.xplayer.waveform;

import javafx.scene.paint.Color;
import main.java.com.goxr3plus.xr3player.xplayer.visualizer.geometry.ResizableCanvas;

/**
 * Swing panel paints the waveform of a track.
 *
 * @author GOXR3PLUS STUDIO
 */
public class WaveFormPane extends ResizableCanvas {
	
	//private final float[] defaultWave;
	private float[] waveData;
	private Color backgroundColor;
	private Color foregroundColor;
	private Color transparentForeground;
	int width;
	int height;
	private int timerXPosition = 0;
	
	/**
	 * Constructor
	 * 
	 * @param width
	 * @param height
	 */
	public WaveFormPane(int width, int height) {
		//defaultWave = new float[width];
		this.width = width;
		this.height = height;
		setWidth(width);
		setHeight(height);
		
		//Create the default Wave
		//		for (int i = 0; i < width; i++)
		//			defaultWave[i] = 0.28802148f;
		//		waveData = defaultWave;
		
		setBackgroundColor(Color.web("#252525"));
		setForeground(Color.ORANGERED);
		
	}
	
	/**
	 * Set the WaveData
	 * 
	 * @param waveData
	 */
	public void setWaveData(float[] waveData) {
		this.waveData = waveData;
	}
	
	public float[] getWaveData() {
		return waveData;
	}
	
	public void setForeground(Color color) {
		this.foregroundColor = color;
		transparentForeground = Color.rgb((int) ( foregroundColor.getRed() * 255 ), (int) ( foregroundColor.getGreen() * 255 ), (int) ( foregroundColor.getBlue() * 255 ), 0.3);
	}
	
	public void setBackgroundColor(Color color) {
		this.backgroundColor = color;
	}
	
	public int getTimerXPosition() {
		return timerXPosition;
	}
	
	public void setTimerXPosition(int timerXPosition) {
		this.timerXPosition = timerXPosition;
	}
	
	/**
	 * Clear the waveform
	 */
	public void clear() {
		gc.clearRect(0, 0, width + 5, height + 5);
	}
	
	/**
	 * Paint the WaveForm
	 */
	public void drawWaveForm() {
		
		//Draw a Background Rectangle
		gc.setFill(backgroundColor);
		gc.fillRect(0, 0, width + 5, height + 5);
		
		//Draw the waveform
		if (waveData != null) {
			gc.setStroke(foregroundColor);
			for (int i = 0; i < waveData.length; i++) {
				int value = (int) ( waveData[i] * height );
				int y1 = ( height - 2 * value ) / 2;
				int y2 = y1 + 2 * value;
				gc.strokeLine(i, y1, i, y2);
			}
		}
		
		//Draw a semi transparent Rectangle
		gc.setFill(transparentForeground);
		gc.fillRect(0, 0, timerXPosition, height);
		
		//Draw an horizontal line
		gc.setFill(Color.WHITE);
		gc.fillOval(timerXPosition, 0, 1, height);
	}
	
}
