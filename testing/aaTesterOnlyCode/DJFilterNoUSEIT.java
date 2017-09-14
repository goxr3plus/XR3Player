/*
 * 
 */
package aaTesterOnlyCode;

import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * The Class DJFilter.
 */
public class DJFilterNoUSEIT extends Canvas {
	
	/** The arc color. */
	private Color arcColor = Color.FIREBRICK;
	
	/** The arc color. */
	private Color textColor = Color.WHITE;
	
	private Color backgroundColor = Color.web("#202020");
	
	/** The font. */
	private Font font = Font.font("Default", FontWeight.BOLD, 14);
	
	/** The value. */
	float value = 100;
	
	/** The mouse X. */
	private int mouseX = 0;
	
	/** The mouse Y. */
	private int mouseY = 0;
	
	/** The angle. */
	private int angle;
	
	/** The gc. */
	GraphicsContext gc = getGraphicsContext2D();
	
	
	/** Constructor
	 * @param width
	 * @param height
	 * @param textColor
	 * @param arcColor
	 * @param backgroundColor
	 */
	public DJFilterNoUSEIT(double width, double height, Color textColor, Color arcColor, Color backgroundColor) {
		
		setWidth(width);
		setHeight(height);
		this.textColor = textColor;
		this.arcColor = arcColor;
		this.backgroundColor = backgroundColor;
		setAngle(100, 200);
		setCursor(Cursor.HAND);
		
		// setOnMouseDragged(this::onMouseDragged)
		paintFilter();
	}
	
	/**
	 * Paint filter.
	 */
	private void paintFilter() {
		
		// --- Clear everything
		gc.clearRect(0, 0, getWidth(), getHeight());
		
		// --- Fill the background
		gc.setFill(backgroundColor);
		gc.fillArc(0, - ( getHeight() / 2 ) * 2, getWidth(), getHeight() * 2 - 1, 180, 180, ArcType.ROUND);
		
		// --- Draw the Arc
		gc.setFill(arcColor);
		gc.fillArc(1, - ( getHeight() / 2 ) * 2, getWidth() - 2, getHeight() * 2 - 1, 180, angle, ArcType.ROUND);
		
		// -- Draw the text
		gc.setFill(textColor);
		gc.setFont(font);
		value = getValue(200);
		gc.fillText(String.valueOf(value), getWidth() / 2 - 10, 20);
		
	}
	
	/**
	 * Calculates the angle of disc based on maximum value.
	 *
	 * @param maximum
	 *            the maximum
	 * @return angle from 0 - 360
	 */
	public float getValue(int maximum) {
		value = (float) ( ( maximum * angle ) / 180.00 );
		
		if (value == 0)
			return -1.0f;
		else if (value == 100)
			return 0.0f;
		else if (value == 200)
			return 1.0f;
		else if (value < 100)
			return - ( 1 - value / 100f );
		else if (value > 100)
			return value / 100f - 1;
		
		return 0;
	}
	
	/**
	 * Changes the Angle of the filter.
	 *
	 * @param value
	 *            the value
	 * @param maximum
	 *            the maximum
	 */
	public void setAngle(int value , int maximum) {
		
		// Min and Max values
		if (value == 0)
			angle = 0;
		else if (value == maximum)
			angle = 180;
		else// Calculation
			angle = ( ( value * 180 ) / maximum );
		
		paintFilter();
	}
	
	/**
	 * Changes the Angle of the filter.
	 *
	 * @param value
	 *            the value
	 * @param maximum
	 *            the maximum
	 */
	public void setAngle(float value , int maximum) {
		
		if (value == -1.0f)
			angle = 0;
		else if (value == 1.0f)
			angle = 180;
		else if (value == 0.0f)
			angle = 90;
		else if (value < 0.0f)
			angle = (int) - ( ( ( value * 100 ) * 180 ) / maximum );
		else if (value > 0.0f)
			angle = (int) ( ( ( value * 100 + 100 ) * 180 ) / maximum );
		
		paintFilter();
	}
	
	/**
	 * @return the textColor
	 */
	public Color getTextColor() {
		return textColor;
	}
	
	/**
	 * @param textColor
	 *            the textColor to set
	 */
	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}
	
	/**
	 * @return the arcColor
	 */
	public Color getArcColor() {
		return arcColor;
	}
	
	/**
	 * @param arcColor
	 *            the arcColor to set
	 */
	public void setArcColor(Color arcColor) {
		this.arcColor = arcColor;
	}
	
	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	/**
	 * @param backgroundColor
	 *            the backgroundColor to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	/**
	 * When the mouse is dragged.
	 *
	 * @param m
	 *            the m
	 */
	public void onMouseDragged(MouseEvent m) {
		mouseX = (int) m.getX();
		mouseY = (int) m.getY();
		if (mouseX > getWidth() / 2)
			angle = -(int) Math.toDegrees(Math.atan2(getWidth() / 2 - mouseX, 2 - mouseY));
		else {
			angle = (int) Math.toDegrees(Math.atan2(getWidth() / 2 - mouseX, 2 - mouseY));
			angle = 360 - angle; // So it calculates it correctly
		}
		
		angle = 270 - angle;
		if (mouseX < 0 && mouseY < 0)
			angle = 0;
		else if (mouseX > getWidth() / 2 && mouseY < 0)
			angle = 180;
		
		if (angle <= 180 && angle >= 0)
			paintFilter();
	}
	
}
