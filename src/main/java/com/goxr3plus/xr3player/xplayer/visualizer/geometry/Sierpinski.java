/**
 * 
 */
package com.goxr3plus.xr3player.xplayer.visualizer.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;
import com.goxr3plus.xr3player.xplayer.visualizer.core.VisualizerDrawer;

/**
 * -----------------------------------------------------------------------
 * 
 * -----------------------------------------------------------------------
 * 
 * 
 * Sierpinski
 * 
 * -----------------------------------------------------------------------
 * 
 * -----------------------------------------------------------------------.
 *
 * @author GOXR3PLUS
 */
public class Sierpinski {

	Random random = new Random();

	/** The sierpinski root height. */
	public double sierpinskiRootHeight;

	/** The sierpinski smallest. */
	private double sierpinskiSmallest = 25;

	/** The sierpinski acceleration. */
	private double sierpinskiAcceleration = 0.2;

	/** The render list. */
	private List<Triangle> renderList = new ArrayList<>();

	/** The points X. */
	private final double[] pointsX = new double[3];

	/** The points Y. */
	private final double[] pointsY = new double[3];

	/** VisualizerDrawer instance. */
	private VisualizerDrawer visualizerDrawer;

	/**
	 * Constructor.
	 *
	 * @param visualizerDrawer the visualizer drawer
	 */
	public Sierpinski(VisualizerDrawer visualizerDrawer) {
		this.visualizerDrawer = visualizerDrawer;
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
	 * Draws the Sierpinski Triangles.
	 */
	public void drawSierpinski() {
		// Calculations
		float[] array = visualizerDrawer.returnBandsArray(visualizerDrawer.stereoMerge, 3);
		sierpinskiSmallest = array[1] * 100 < 10 ? 10 : array[1] * 100;
		sierpinskiAcceleration = array[0] * 0.1;
		// System.out.println(sierpinskiAcceleration)

		// Background
		visualizerDrawer.drawBackgroundImage();

		calcTriangles();
		drawTriangles();
	}

	/**
	 * Calculate the position of the Triangles.
	 */
	private void calcTriangles() {
		renderList.clear();

		double acceleration = sierpinskiRootHeight * sierpinskiAcceleration;

		sierpinskiRootHeight += acceleration;

		if (sierpinskiRootHeight >= 2 * visualizerDrawer.canvasHeight) {
			sierpinskiRootHeight = visualizerDrawer.canvasHeight;
		}

		Triangle root = new Triangle(visualizerDrawer.canvasWidth / 2, 0, sierpinskiRootHeight);

		shrink(root);
	}

	/**
	 * Shrink.
	 *
	 * @param triangle the triangle
	 */
	private void shrink(Triangle triangle) {
		double topX = triangle.getTopX();
		double topY = triangle.getTopY();
		double triangleHeight = triangle.getHeight();

		if (topY >= visualizerDrawer.canvasHeight) {
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
	 * Draw the triangles.
	 */
	private void drawTriangles() {
		visualizerDrawer.gc.setFill(Color.WHITE);
		// gc.setFill(Color.rgb((int)(255*Math.random()),(int)(255*Math.random()),(int)(255*Math.random())))

		int triangleCount = renderList.size();

        for (Triangle triangle : renderList) {
            if (triangle.getTopY() < visualizerDrawer.canvasHeight) {
                drawTriangle(triangle);
            }
        }
	}

	/**
	 * Draw the triangle.
	 *
	 * @param triangle the triangle
	 */
	private void drawTriangle(Triangle triangle) {
		double topX = triangle.getTopX();
		double topY = triangle.getTopY();
		double h = triangle.getHeight();

		pointsX[0] = topX;
		pointsY[0] = topY;

		pointsX[1] = topX + h / 2;
		pointsY[1] = topY + h;

		pointsX[2] = topX - h / 2;
		pointsY[2] = topY + h;

		visualizerDrawer.gc.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		visualizerDrawer.gc.fillPolygon(pointsX, pointsY, 3);

		// gc.strokePolygon(pointsX, pointsY, 3)
	}

	/**
	 * --------------------------Triangle class which contains x,y of a Triangle and
	 * height.
	 */
	private class Triangle {

		/** The top X. */
		private final double topX;

		/** The top Y. */
		private final double topY;

		/** The height. */
		private final double height;

		/**
		 * Constructor.
		 *
		 * @param topX   The topLeftX corner of the Triangle
		 * @param topY   The topLeftY corner of the Triangle
		 * @param height The height of the Triangle
		 */
		public Triangle(double topX, double topY, double height) {
			this.topX = topX;
			this.topY = topY;
			this.height = height;
		}

		/**
		 * Gets the top X.
		 *
		 * @return the top X
		 */
		public final double getTopX() {
			return topX;
		}

		/**
		 * Gets the top Y.
		 *
		 * @return the top Y
		 */
		public final double getTopY() {
			return topY;
		}

		/**
		 * Gets the height.
		 *
		 * @return the height
		 */
		public final double getHeight() {
			return height;
		}
	}

}
