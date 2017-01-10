/**
 * 
 */
package visualizer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

/**
 * The Class VisualizerDrawer.
 *
 * @author GOXR3PLUS
 */
public class VisualizerDrawer extends VisualizerModel {
	
	/** The random. */
	// ----------Variables
	Random random = new Random();
	
	/** The Constant TWO_PI. */
	static final float TWO_PI = (float) ( Math.PI * 2 );
	
	/** The pixel size. */
	// --------------- PolySpiral ---------------------
	float pixelSize;
	
	/** The r width. */
	float rWidth = 50.0F;
	
	/** The r height. */
	float rHeight = 50.0F;
	
	/** The center X. */
	int centerX;
	
	/** The center Y. */
	int centerY;
	
	// -------------- Rosette --------------------------
	
	/** The num of vertices. */
	int numOfVertices = 0;
	
	/** The radius. */
	float radius;
	
	/** The rosette angle. */
	float rosetteAngle;
	
	/** The delang. */
	float delang;
	
	/** The vertices. */
	Point2D[] vertices = null;
	
	/** The default angle. */
	float defaultAngle = 0.0F;
	
	/** The cd. */
	float cd;
	
	/** The iterator. */
	int iterator = 500;
	
	/** The distance. */
	float distance;
	
	/** The poly spiral angle. */
	float polySpiralAngle;
	
	/** The increment. */
	float increment;
	
	/** The cur X. */
	float curX;
	
	/** The cur Y. */
	float curY;
	
	/** The foreground image. */
	public Image foregroundImage = new Image(VisualizerModel.class.getResourceAsStream("foreground.png"));
	
	/** The background image. */
	public Image backgroundImage;// new
	                             // Image(VisualizerModel.class.getResourceAsStream("background.gif"));
	
	/** The cool effect. */
	public Image coolEffect = new Image(VisualizerModel.class.getResourceAsStream("anim1.gif"));
	
	/** The yellow light. */
	public Image yellowLight = new Image(VisualizerModel.class.getResourceAsStream("yellowLight.png"));
	
	/** The blue light. */
	public Image blueLight = new Image(VisualizerModel.class.getResourceAsStream("blueLight.png"));
	
	/** The grey light. */
	public Image greyLight = new Image(VisualizerModel.class.getResourceAsStream("greyLight.png"));
	
	/** The light blue light. */
	public Image lightBlueLight = new Image(VisualizerModel.class.getResourceAsStream("lightBlueLight.png"));
	
	/** The red light. */
	public Image redLight = new Image(VisualizerModel.class.getResourceAsStream("redLight.png"));
	
	/**
	 * Returns an array which has length<array length> and contains frequencies
	 * in every cell which has a value from 0.00 to 1.00.
	 *
	 * @param pSample the sample
	 * @param arrayLength the array length
	 * @return An array which has length<array length> and contains frequencies
	 *         in every cell which has a value from 0.00 to 1.00.
	 */
	private float[] returnBandsArray(float[] pSample , int arrayLength) {
		
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
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							      Scope
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	
	/**
	 * Draws a scope with the given values.
	 */
	public void drawScope() {
		float[] pSample = stereoMerge(left, right);
		
		// backgoundImage
		if (backgroundImage != null)
			gc.drawImage(backgroundImage, 0, 0, width, height);
		
		gc.setStroke(scopeColor);
		// System.out.println(pSample.length)
		
		int yLast = (int) ( pSample[0] * (float) height_2 ) + height_2;
		int samIncrement = 1;
		for (int a = samIncrement, c = 0; c < width; a += samIncrement, c++) {
			System.out.println(pSample[a]);
			int yNow = (int) ( pSample[a] * (float) height_2 ) + height_2;
			gc.strokeLine(c, yLast, c + 1.00, yNow);
			yLast = yNow;
		}
		
	}
	
	/*-----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							Rosette and Polyspiral
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	
	/**
	 * Draws a Rosette and a Polyspiral.
	 */
	public void drawPolySpiral() {
		
		// Calculations
		float[] pSample = stereoMerge(left, right);
		float[] array = returnBandsArray(pSample, 3);
		
		int length;
		int total;
		
		if (array[0] < 0.25) {
			length = 2;
			total = 65;
		} else if (array[0] < .5) {
			length = 3;
			total = 200;
		} else if (array[0] < .65) {
			length = 4;
			total = 300;
		} else if (array[0] < 0.75) {
			length = 5;
			total = 500;
		} else {
			length = 6;
			total = 1200;
		}
		
		// Set the background fill
		gc.setFill(Color.rgb(0, 0, 0, array[0]));
		gc.fillRect(0, 0, width, height);
		
		// Background image
		if (backgroundImage != null)
			gc.drawImage(backgroundImage, 0, 0, width, height);
		
		// Draw Random Ovals
		/* Exception in thread "JavaFX Application Thread"
		 * java.lang.IllegalArgumentException: bound must be positive
		 * at java.util.Random.nextInt(Random.java:388)
		 * at visualizer.model.VisualizerDrawer.drawPolySpiral(VisualizerDrawer.
		 * java:261)
		 * error(gc.fillOval(random.nextInt(width), random.nextInt(height),
		 * length + 2.00, length + 2.00);) */
		gc.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		for (int i = 0; i < total; i++) {
			gc.fillOval(random.nextInt(width), random.nextInt(height), length + 2.00, length + 2.00);
		}
		
		// Draw Lights
		if (width > greyLight.getWidth() && height > greyLight.getHeight())
			if (array[0] < 0.2) {
				gc.drawImage(greyLight, 0, 0);
				gc.drawImage(greyLight, 0, 0, greyLight.getWidth(), greyLight.getHeight(), width, 0,
				        -greyLight.getWidth(), greyLight.getHeight());
			} else if (array[0] < 0.3) {
				gc.drawImage(lightBlueLight, 0, 0);
				gc.drawImage(lightBlueLight, 0, 0, lightBlueLight.getWidth(), lightBlueLight.getHeight(), width, 0,
				        -lightBlueLight.getWidth(), lightBlueLight.getHeight());
			} else if (array[0] < 0.4) {
				gc.drawImage(blueLight, 0, 0);
				gc.drawImage(blueLight, 0, 0, blueLight.getWidth(), blueLight.getHeight(), width, 0,
				        -blueLight.getWidth(), blueLight.getHeight());
			} else if (array[0] < 0.6) {
				gc.drawImage(yellowLight, 0, 0);
				gc.drawImage(yellowLight, 0, 0, yellowLight.getWidth(), yellowLight.getHeight(), width, 0,
				        -yellowLight.getWidth(), yellowLight.getHeight());
			} else if (array[0] < 0.9) {
				gc.drawImage(redLight, 0, 0);
				gc.drawImage(redLight, 0, 0, redLight.getWidth(), redLight.getHeight(), width, 0, -redLight.getWidth(),
				        redLight.getHeight());
			}
		
		// Scope
		if (width > greyLight.getWidth() && height > greyLight.getHeight()) {
			gc.setStroke(scopeColor);
			double coolW = coolEffect.getWidth();
			double coolH = coolEffect.getHeight() < height ? coolEffect.getHeight() : height;
			
			// ------------------------Draw Scope----------------------------
			int zb = (int) ( 50 + 100 * array[0] );
			int yLast = (int) ( pSample[0] * (float) zb ) + zb;
			int angleIncrement = 1;
			for (int a = angleIncrement, c = (int) ( coolW / 2 - 50 ); c < ( width
			        - coolW / 2 ); a += angleIncrement, c++) {
				int yNow = (int) ( pSample[a] * (float) zb ) + zb;
				gc.strokeLine(c, yLast, c + 1.00, yNow);
				yLast = yNow;
			}
			
			gc.setStroke(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 1.0));
			int zb2 = (int) ( height - 50 - 100 * array[0] );
			int yLast2 = (int) ( pSample[0] * (float) zb2 ) + zb2;
			int angleIncrement2 = 1;
			for (int a = angleIncrement2, c = (int) ( coolW / 2 - 50 ); c < ( width
			        - coolW / 2 ); a += angleIncrement2, c++) {
				int yNow2 = (int) ( pSample[a] * (float) zb2 ) + zb2;
				gc.strokeLine(c, yLast2, c + 1.00, yNow2);
				yLast2 = yNow2;
			}
			
			// Cool Effect1
			gc.drawImage(coolEffect, ( coolW / 2 - 50 ) - coolW * array[0] / 2,
			        ( height / 2 - coolH / 2 ) - coolH * array[0] / 2, coolW * array[0], coolH * array[0]);
			gc.drawImage(coolEffect, ( width - coolW / 2 ) - coolW * array[0] / 2,
			        ( height / 2 - coolH / 2 ) - coolH * array[0] / 2, coolW * array[0], coolH * array[0]);
			
			gc.drawImage(coolEffect, ( coolW / 2 - 50 ) - coolW * array[0] / 2,
			        ( height - coolH / 2 ) - coolH * array[0] / 2, coolW * array[0], coolH * array[0]);
			gc.drawImage(coolEffect, ( width - coolW / 2 ) - coolW * array[0] / 2,
			        ( height - coolH / 2 ) - coolH * array[0] / 2, coolW * array[0], coolH * array[0]);
		}
		
		// ------------------------Draw Rosette----------------------------
		calculate();
		
		numOfVertices = (int) ( array[0] * 100 );
		vertices = new Point2D[numOfVertices];
		delang = TWO_PI / numOfVertices;
		
		// Calculate the angle of vertices
		for (int i = 0; i < numOfVertices; i++) {
			rosetteAngle = i * delang + defaultAngle;
			vertices[i] = new Point2D((float) ( radius * Math.cos(rosetteAngle) ),
			        (float) ( radius * Math.sin(rosetteAngle) ));
		}
		
		// Draw the vertices
		double opacity = array[0] < 0.28 ? 1.0 : 0;
		for (int i = 0; i < numOfVertices; i++)
			for (int j = numOfVertices - 1; j >= 0; j--)
				if (j > i) {
					gc.setStroke(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), opacity));
					gc.strokeLine(deviceX(vertices[i].getX()), deviceY(vertices[i].getY()), deviceX(vertices[j].getX()),
					        deviceY(vertices[j].getY()));
				} else
					break;
					
		// --------------------Draw PolySpiral
		// -----------------------------------
		
		// if array[0]==1 it has an ugly effect of drawing one horizontal line
		// so i don't need this
		if (array[0] < 0.99) {
			
			do {
				polySpiralAngle = (float) Math.random() * 360;
				distance = (float) Math.random();
				increment = (float) Math.random();
			} while (polySpiralAngle == 0 || distance == 0.0F || increment == 0.0F);
			
			distance = (float) Math.random() * array[0];//(float) 0.2;// //
			                       // array[0]*array[1]*100;
			increment = (float) 0.07; // (float) Math.random() * array[1] * 100
			                          // + (float) 0.07;
			polySpiralAngle = array[1] < 0.35 ? array[0] * 360 : array[0] * 360 * (float) Math.random() * 100;
			
			curX = 0.0F;
			curY = 0.0F;
			cd = 0.0F;
			
			gc.setStroke(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255),
			        array[0] < 0.28 ? 0.0 : 1.0));
			for (int i = 0; i < iterator; i++) {
				
				// draw Line
				float lineAngle = TWO_PI * cd / 360.0f;
				float newX = curX + distance * (float) Math.cos(lineAngle);
				float newY = curY + distance * (float) Math.sin(lineAngle);
				gc.strokeLine(deviceX(curX), deviceY(curY), deviceX(newX), deviceY(newY));
				curX = newX;
				curY = newY;
				
				cd -= polySpiralAngle;
				distance = distance + increment;
			}
		}
		
		// Draw the foreground images
		double imageW = foregroundImage.getWidth();
		double imageH = foregroundImage.getHeight();
		if (width < height)
			imageW = imageH = width / 1.5;
		else if (height < width)
			imageW = imageH = height / 1.5;
		
		else {
			
			imageW = getWidth() / 2;
			imageH = getHeight() / 2;
		}
		// System.out.println(imageW + ", h:" + imageH);
		gc.drawImage(foregroundImage, ( width / 2 - imageW / 2 ) - imageW * array[0] / 2,
		        ( height / 2 - imageH / 2 ) - imageH * array[0] / 2, imageW + imageW * array[0],
		        imageH + imageH * array[0]);
		
	}
	
	/**
	 * Calculate.
	 */
	void calculate() {
		int maxX = width - 1;
		int maxY = height - 1;
		pixelSize = Math.max(rWidth / maxX, rHeight / maxY);
		centerX = maxX / 2;
		centerY = maxY / 2;
		radius = Math.min(rWidth, rHeight) / 2.0F;
	}
	
	/**
	 * Logical X coordinates to Device X coordinates.
	 *
	 * @param logicalX the logical X
	 * @return the int
	 */
	private int deviceX(double logicalX) {
		return (int) ( centerX + logicalX / pixelSize );
	}
	
	/**
	 * Logical Y coordinates to Device Y coordinates.
	 *
	 * @param logicalY the logical Y
	 * @return the int
	 */
	private int deviceY(double logicalY) {
		return (int) ( centerY - logicalY / pixelSize );
	}
	
	/*-----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							Spectrum Analyzer
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	
	/**
	 * Draws a spectrum analyzer using rectangles.
	 */
	public void drawSpectrumAnalyser() {
		float[] pSample = stereoMerge(left, right);
		
		float barWidth = (float) width / (float) saBands;
		float[] array = returnBandsArray(pSample, saBands);
		float c = 0;
		// BackgroundImage
		if (backgroundImage != null)
			gc.drawImage(backgroundImage, 0, 0, width, height);
		
		for (int band = 0; band < saBands; band++) {
			drawSpectrumAnalyserBar((int) c, height, (int) barWidth - 1, (int) ( array[band] * height ), band);
			c += barWidth;
		}
	}
	
	/**
	 * Draw spectrum analyser bar.
	 *
	 * @param pX the p X
	 * @param pY the p Y
	 * @param pWidth the width
	 * @param pHeight the height
	 * @param band the band
	 */
	private void drawSpectrumAnalyserBar(int pX , int pY , int pWidth , int pHeight , int band) {
		float c = 0;
		
		// Draw the main Shape
		for (int a = pY; a >= pY - pHeight; a -= barOffset) {
			c += saColorScale;
			if (c < spectrumAnalyserColors.length)
				gc.setFill(spectrumAnalyserColors[(int) c]);
			
			gc.fillRect(pX, a, pWidth, 1);
		}
		
		// Draw The peaks
		// peakColor = (Color) gc.getFill();
		if (peakColor != null && peaksEnabled) {
			
			gc.setStroke(peakColor);
			if (pHeight > peaks[band]) {
				peaks[band] = pHeight;
				peaksDelay[band] = peakDelay;
			} else {
				peaksDelay[band] -= 2;
				if (peaksDelay[band] < 0)
					peaks[band] -= 2;
				if (peaks[band] < 0)
					peaks[band] = 0;
			}
			
			gc.strokeRect(pX, pY - peaks[band], pWidth, 1);
		}
		
	}
	
	/*-----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							VUMeter
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	
	/**
	 * Draws a VUMeter.
	 */
	public void drawVUMeter() {
		
		// BackgroundImage
		if (backgroundImage != null)
			gc.drawImage(backgroundImage, 0, 0, width, height);
		
		float wLeft = 0.0f;
		float wRight = 0.0f;
		float wSadfrr = vuDecay * frameRateRatioHint;
		
		for (int a = 0; a < left.length; a++) {
			wLeft += Math.abs(left[a]);
			wRight += Math.abs(right[a]);
		}
		
		wLeft = ( wLeft = ( wLeft * 2.0f ) / left.length ) > 1.0f ? 1.0f : wLeft;
		wRight = ( wRight = ( wRight * 2.0f ) / right.length ) > 1.0f ? 1.0f : wRight;
		
		/* vuAverage += ( ( wLeft + wRight ) / 2.0f ); vuSamples++;
		 * if ( vuSamples > 128 ) { vuSamples /= 2.0f; vuAverage /= 2.0f; } */
		
		if (wLeft >= ( oldLeft - wSadfrr ))
			oldLeft = wLeft;
		else {
			oldLeft -= wSadfrr;
			if (oldLeft < 0)
				oldLeft = 0;
			
		}
		
		if (wRight >= ( oldRight - wSadfrr ))
			oldRight = wRight;
		else {
			oldRight -= wSadfrr;
			if (oldRight < 0)
				oldRight = 0;
			
		}
		
		int wHeight = ( height >> 1 ) - 20;
		drawVolumeMeterBar(16, 16, (int) ( oldLeft * (float) ( width - 32 ) ), wHeight);
		drawVolumeMeterBar(16, wHeight + 32, (int) ( oldRight * (float) ( width - 32 ) ), wHeight);
		
	}
	
	/**
	 * Draw volume meter bar.
	 *
	 * @param x the x
	 * @param y the y
	 * @param pWidth the width
	 * @param pHeight the height
	 */
	private void drawVolumeMeterBar(int x , int y , int pWidth , int pHeight) {
		
		float c = 0;
		int max = x + pWidth;
		for (int a = x; a <= max; a += 2) {
			c += vuColorScale;
			if (c < 256.0f)
				gc.setStroke(spectrumAnalyserColors[(int) c]);
			
			gc.strokeRect(a, y, 2, pHeight);
		}
		
		gc.setStroke(Color.BLACK);
		for (int a = x; a <= max; a += 15) {
			gc.strokeRect(a, y, 1, pHeight);
		}
	}
	
	/*-----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							Cicular
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	
	/**
	 * Draws an Arc or whole Circle.
	 */
	public void drawCircular() {
		float[] pSample = stereoMerge(left, right);
		
		// backgoundImage
		if (backgroundImage != null)
			gc.drawImage(backgroundImage, 0, 0, width, height);
		float[] array = returnBandsArray(pSample, 1);
		int arcHeight = height / 2;
		
		gc.setFill(Color.WHITE);
		// gc.fillOval(iX(-w*2), iY(w*2), w, w);
		gc.fillArc(width / 2.00, height / 2.00, arcHeight, arcHeight, 0, 360 * array[0], ArcType.ROUND);
	}
	
	/*-----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 						    Sierpinski
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	
	/**
	 * Draws the Sierpinski Triangles
	 */
	public void drawSierpinski() {
		// Calculations
		float[] pSample = stereoMerge(left, right);
		float[] array = returnBandsArray(pSample, 3);
		sierpinskiSmallest = array[1] * 100 < 10 ? 10 : array[1] * 100;
		sierpinskiAcceleration = array[0] * 0.1;
		// System.out.println(sierpinskiAcceleration)
		
		// Background image
		if (backgroundImage != null)
			gc.drawImage(backgroundImage, 0, 0, width, height);
		
		calcTriangles();
		drawTriangles();
	}
	
	protected double sierpinskiRootHeight;
	private double sierpinskiSmallest = 25;
	private double sierpinskiAcceleration = 0.2;
	private List<Triangle> renderList = new ArrayList<>();
	
	private final double[] pointsX = new double[3];
	private final double[] pointsY = new double[3];
	
	/**
	 * Sierpinski Triangle
	 *
	 */
	private class Triangle {
		private final double topX;
		private final double topY;
		private final double height;
		
		public Triangle(double topX, double topY, double height) {
			this.topX = topX;
			this.topY = topY;
			this.height = height;
		}
		
		public final double getTopX() {
			return topX;
		}
		
		public final double getTopY() {
			return topY;
		}
		
		public final double getHeight() {
			return height;
		}
	}
	
	/**
	 * Calculate the position of the Triangles
	 */
	private final void calcTriangles() {
		renderList.clear();
		
		double acceleration = sierpinskiRootHeight * sierpinskiAcceleration;
		
		sierpinskiRootHeight += acceleration;
		
		if (sierpinskiRootHeight >= 2 * height) {
			sierpinskiRootHeight = height;
		}
		
		Triangle root = new Triangle(width / 2, 0, sierpinskiRootHeight);
		
		shrink(root);
	}
	
	/**
	 * @param Triangle
	 */
	private void shrink(Triangle triangle) {
		double topX = triangle.getTopX();
		double topY = triangle.getTopY();
		double triangleHeight = triangle.getHeight();
		
		if (topY >= height) {
			return;
		}
		
		if (triangleHeight < sierpinskiSmallest) {
			renderList.add(triangle);
		} else {
			Triangle top = new Triangle(topX, topY, triangleHeight / 2);
			Triangle left = new Triangle(topX - triangleHeight / 4, topY + triangleHeight / 2, triangleHeight / 2);
			Triangle right = new Triangle(topX + triangleHeight / 4, topY + triangleHeight / 2, triangleHeight / 2);
			
			shrink(top);
			shrink(left);
			shrink(right);
		}
	}
	
	/**
	 * Draw the triangles
	 */
	private final void drawTriangles() {
		gc.setFill(Color.WHITE);
		// gc.setFill(Color.rgb((int)(255*Math.random()),(int)(255*Math.random()),(int)(255*Math.random())))
		
		int triangleCount = renderList.size();
		
		for (int i = 0; i < triangleCount; i++) {
			Triangle tri = renderList.get(i);
			
			if (tri.getTopY() < height) {
				drawTriangle(tri);
			}
		}
	}
	
	/**
	 * Draw the triangle
	 * 
	 * @param triangle
	 */
	private final void drawTriangle(Triangle triangle) {
		double topX = triangle.getTopX();
		double topY = triangle.getTopY();
		double h = triangle.getHeight();
		
		pointsX[0] = topX;
		pointsY[0] = topY;
		
		pointsX[1] = topX + h / 2;
		pointsY[1] = topY + h;
		
		pointsX[2] = topX - h / 2;
		pointsY[2] = topY + h;
		
		gc.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		gc.fillPolygon(pointsX, pointsY, 3);
		
		// gc.strokePolygon(pointsX, pointsY, 3);
	}
	
}
