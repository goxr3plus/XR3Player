/*
 * This file is part of Musicott software. Musicott software is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Musicott library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public
 * License along with Musicott. If not, see <http://www.gnu.org/licenses/>. Copyright (C) 2015 - 2017 Octavio Calleya
 */

package main.java.com.goxr3plus.xr3player.xplayer.waveform;

import javafx.scene.paint.Color;

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
		//waveData = defaultWave;
		
		//Draw a Background Rectangle
		gc.setFill(backgroundColor);
		gc.fillRect(0, 0, width, height);
	}
	
	/**
	 * Paint the WaveForm
	 */
	public void paintWaveForm() {
		
		//Draw a Background Rectangle
		gc.setFill(backgroundColor);
		gc.fillRect(0, 0, width, height);
		
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
