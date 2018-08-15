
package main.java.com.goxr3plus.xr3player.xplayer.waveform;

import javafx.scene.paint.Color;
import main.java.com.goxr3plus.xr3player.xplayer.visualizer.geometry.ResizableCanvas;

/**
 * Swing panel paints the waveform of a track.
 *
 * @author GOXR3PLUS STUDIO
 */
public class WaveFormPane extends ResizableCanvas {
	
	private float[] waveData;
	private Color backgroundColor;
	private Color foregroundColor;
	private Color transparentForeground;
	private Color mouseXColor = Color.rgb(255, 255, 255, 0.7);
	int width;
	int height;
	private int timerXPosition;
	private int mouseXPosition = -1;
	private WaveVisualization waveVisualization;
	
	/**
	 * Constructor
	 * 
	 * @param width
	 * @param height
	 */
	public WaveFormPane(int width, int height) {
		this.width = width;
		this.height = height;
		this.setWidth(width);
		this.setHeight(height);
		
		backgroundColor = Color.web("#252525");
		setForeground(Color.ORANGE);
		
	}
	
	/**
	 * Set the WaveData
	 * 
	 * @param waveData
	 */
	public void setWaveData(float[] waveData) {
		this.waveData = waveData;
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
	
	public void setMouseXPosition(int mouseXPosition) {
		this.mouseXPosition = mouseXPosition;
	}
	
	/**
	 * Clear the waveform
	 */
	public void clear() {
		//Draw a Background Rectangle
		gc.setFill(backgroundColor);
		gc.fillRect(0, 0, width, height);
		
		//Paint a line
		gc.setStroke(foregroundColor);
		gc.strokeLine(0, height / 2, width, height / 2);
	}
	
	/**
	 * Paint the WaveForm
	 */
	public void paintWaveForm() {
		
		//Draw a Background Rectangle
		gc.setFill(backgroundColor);
		gc.fillRect(0, 0, width, height);
		
		//Draw the waveform
		gc.setStroke(foregroundColor);
		if (waveData != null)
			for (int i = 0; i < waveData.length; i++) {
				if (!waveVisualization.getAnimationService().isRunning()) {
					clear();
					break;
				}
				int value = (int) ( waveData[i] * height );
				int y1 = ( height - 2 * value ) / 2;
				int y2 = y1 + 2 * value;
				gc.strokeLine(i, y1, i, y2);
			}
		
		//Draw a semi transparent Rectangle
		gc.setFill(transparentForeground);
		gc.fillRect(0, 0, timerXPosition, height);
		
		//Draw an horizontal line
		gc.setFill(Color.WHITE);
		gc.fillOval(timerXPosition, 0, 1, height);
		
		//Draw an horizontal line
		if (mouseXPosition != -1) {
			gc.setFill(mouseXColor);
			gc.fillRect(mouseXPosition, 0, 3, height);
		}
	}
	
	public WaveVisualization getWaveVisualization() {
		return waveVisualization;
	}
	
	public void setWaveVisualization(WaveVisualization waveVisualization) {
		this.waveVisualization = waveVisualization;
	}
	
}
