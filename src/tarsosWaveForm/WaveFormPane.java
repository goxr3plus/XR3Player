/*
 * This file is part of Musicott software. Musicott software is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Musicott library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public
 * License along with Musicott. If not, see <http://www.gnu.org/licenses/>. Copyright (C) 2015 - 2017 Octavio Calleya
 */

package tarsosWaveForm;

import javafx.scene.paint.Color;

/**
 * Swing panel paints the waveform of a track.
 *
 * @author GOXR3PLUS STUDIO
 */
public class WaveFormPane extends ResizableCanvas {
	
	private final float[] defaultWave;
	private float[] waveData;
	private Color backgroundColor;
	private Color foregroundColor;
	int width;
	int height;
	int timerXPosition = 100;
	
	/**
	 * Constructor
	 * 
	 * @param width
	 * @param height
	 */
	public WaveFormPane(int width, int height) {
		defaultWave = new float[width];
		this.width = width;
		this.height = height;
		this.setWidth(width);
		this.setHeight(height);
		//this.maxWidth(width);
		//this.maxHeight(height);
		
		//Create the default Wave
		for (int i = 0; i < width; i++)
			defaultWave[i] = 0.28802148f;
		waveData = defaultWave;
		
		backgroundColor = Color.web("#202020");
		foregroundColor = Color.ORANGERED;
		
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
	}
	
	public void setBackgroundColor(Color color) {
		this.backgroundColor = color;
	}
	
	public void setTimerPosition(int x) {
		this.timerXPosition = x;
	}
	
	/**
	 * Clear the waveform
	 */
	public void clear() {
		waveData = defaultWave;
		
		//Draw a Background Rectangle
		gc.setFill(backgroundColor);
		gc.fillRect(0, 0, width, height);
	}
	
	/**
	 * Paint the WaveForm
	 */
	public void paintWaveForm() {
		//width = (int) getWidth();
		//height = (int) getHeight();
		
		//Draw a Background Rectangle
		gc.setFill(backgroundColor);
		gc.fillRect(0, 0, width, height);
		
		System.out.println(width);
		
		//Draw the waveform
		gc.setStroke(foregroundColor);
		for (int i = 0; i < waveData.length; i++) {
			int value = (int) ( waveData[i] * height );
			int y1 = ( height - 2 * value ) / 2;
			int y2 = y1 + 2 * value;
			gc.strokeLine(i, y1, i, y2);
		}
		
		//Draw a semi transparent Rectangle
		gc.setFill(Color.rgb(255,255,255, 0.1));
		gc.fillRect(0, 0, timerXPosition, height);
		
		//Draw an horizontal line
		gc.setStroke(Color.WHITE);
		gc.strokeLine(timerXPosition, 0, timerXPosition, height);
	}
	
}
