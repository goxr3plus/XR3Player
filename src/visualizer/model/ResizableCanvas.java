package visualizer.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class ResizableCanvas extends Canvas {
	
	public final GraphicsContext gc = getGraphicsContext2D();
	public int canvasWidth = 0;
	public int canvasHeight = 0;
	public int halfCanvasHeight = 0;
	
	/**
	 * Redraw the Canvas
	 */
	@SuppressWarnings("unused")
	private void draw() {
		
		System.out.println(" Real Canvas Width is:" + getWidth() + " , Real Canvas Height is:" + getHeight() + "\n");
		
		gc.clearRect(0, 0, canvasWidth, canvasHeight);
		
		gc.setStroke(Color.RED);
		gc.strokeLine(0, 0, canvasWidth, canvasHeight);
		gc.strokeLine(0, canvasHeight, canvasWidth, 0);
	}
	
	@Override
	public double minHeight(double width) {
		return 1;
	}
	
	@Override
	public double maxHeight(double width) {
		return Double.MAX_VALUE;
	}
	
	@Override
	public double prefHeight(double width) {
		return minHeight(width);
	}
	
	@Override
	public double minWidth(double height) {
		return 1;
	}
	
	@Override
	public double maxWidth(double height) {
		return Double.MAX_VALUE;
	}
	
	@Override
	public boolean isResizable() {
		return true;
	}
	
	@Override
	public void resize(double width , double height) {
		super.setWidth(width);
		super.setHeight(height);
		
		// This is for testing...
		// draw()
		
		// System.out.println("Resize method called...")
	}
}
