/*
 * Copyright (c) 2015 Chris Newland.
 * Licensed under https://github.com/chriswhocodes/demofx/blob/master/LICENSE-BSD
 */
package com.goxr3plus.xr3player.xplayer.visualizer.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javafx.scene.image.Image;
import com.goxr3plus.xr3player.xplayer.visualizer.core.VisualizerDrawer;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * The Class Sprite3D.
 */
public class Sprites3D {

	/**
	 * The Class Point3D.
	 */
	// Point 3D
	class Point3D {

		/** The x. */
		private double x;

		/** The y. */
		private double y;

		/** The z. */
		private double z;

		/**
		 * Instantiates a new point 3 D.
		 *
		 * @param x the x
		 * @param y the y
		 * @param z the z
		 */
		public Point3D(double x, double y, double z) {
			super();
			this.x = x;
			this.y = y;
			this.z = z;
		}

		/**
		 * Gets the x.
		 *
		 * @return the x
		 */
		public double getX() {
			return x;
		}

		/**
		 * Gets the y.
		 *
		 * @return the y
		 */
		public double getY() {
			return y;
		}

		/**
		 * Gets the z.
		 *
		 * @return the z
		 */
		public double getZ() {
			return z;
		}
	}

	/**
	 * The Enum Shape3D.
	 */
	public enum Shape3D {

		/** The ring. */
		RING,
		/** The tube. */
		TUBE,
		/** The cube. */
		CUBE,
		/** The sphere. */
		SPHERE
	}

	/** The render list. */
	private List<Point3D> renderList;

	/** The points. */
	private List<Point3D> points;

	/** The roll. */
	private double roll;

	/** The pitch. */
	private double pitch;

	/** The yaw. */
	private double yaw;

	/** The roll inc. */
	private double rollInc;

	/** The pitch inc. */
	private double pitchInc;

	/** The yaw inc. */
	private double yawInc;

	/** The x offset. */
	private double xOffset;

	/** The y offset. */
	private double yOffset;

	/** The z offset. */
	private double zOffset;

	/** The zoom. */
	private double zoom;

	/** The background. */
	// private Image background;

	/** The image ball. */
	private Image imageBall;

	/** The z comparator. */
	private Comparator<Point3D> zComparator = (p1, p2) -> Double.compare(p2.z, p1.z);

	private VisualizerDrawer visualizerDrawer;

	Image[] images = { InfoTool.getImageFromResourcesFolder("star1.png"),
			InfoTool.getImageFromResourcesFolder("star2.png"), InfoTool.getImageFromResourcesFolder("star3.png"),
			InfoTool.getImageFromResourcesFolder("star4.png"), InfoTool.getImageFromResourcesFolder("star5.png") };

	/**
	 * Instantiates a new sprite 3 D.
	 *
	 * @param visualizerDrawer
	 * @param shape
	 */
	public Sprites3D(VisualizerDrawer visualizerDrawer, Shape3D shape) {
		// super(gc);
		this.visualizerDrawer = visualizerDrawer;

		// background = new Image(getClass().getResourceAsStream(InfoTool.images +
		// "trapNation.jpg"));
		customInitialise(shape, -1, -1);
	}

	// @Override
	// protected void initialise() {
	// imageBall = new
	// Image(getClass().getResourceAsStream("/resources/earth.png"));
	// }

	/**
	 * Custom initialise.
	 *
	 * @param shape       the shape
	 * @param startMillis the start millis
	 * @param stopMillis  the stop millis
	 */
	private void customInitialise(Shape3D shape, long startMillis, long stopMillis) {
		// this.effectStartMillis = startMillis;
		// this.effectStopMillis = stopMillis;

		switch (shape) {
		case CUBE:
			points = makeCube(12, 3.0);
			break;
		case RING:
			points = makeRing(48, 2.0);
			break;
		case SPHERE:
			points = makeSphere(5.0, 50);
			break;
		case TUBE:
			points = makeTube(48, 1.0, 16, 3.0);
			break;
		default:
			break;

		}

		// itemCount = points.size();
		renderList = new ArrayList<>(points.size());
	}

	/**
	 * Make cube.
	 *
	 * @param balls the balls
	 * @param side  the side
	 * @return the list
	 */
	private List<Point3D> makeCube(int balls, double side) {
		zoom = 250;
		rollInc = 1.0;
		pitchInc = 0.5;
		yawInc = 1.5;
		zOffset = 4;

		List<Point3D> result = new ArrayList<>();

		double gap = 1.0 / (balls / 2 - 1);

		double halfSide = side / 2.0;

		for (double x = -halfSide; x <= halfSide; x += gap) {
			for (double y = -halfSide; y <= halfSide; y += gap) {
				for (double z = -halfSide; z <= halfSide; z += gap) {
					result.add(new Point3D(x, y, z));
				}
			}
		}

		return result;
	}

	/**
	 * Make ring.
	 *
	 * @param balls  the balls
	 * @param radius the radius
	 * @return the list
	 */
	private List<Point3D> makeRing(double balls, double radius) {
		zoom = 100;
		rollInc = 1.0;
		pitchInc = 0.5;
		yawInc = 1.5;
		zOffset = 2.5;

		List<Point3D> result = new ArrayList<>();

		for (double a = 0; a < 360; a += 360.0 / balls) {

			double x = radius * Math.sin(Math.toRadians(a));// precalc.sin(a);
			double y = radius * Math.cos(Math.toRadians(a));// precalc.cos(a);

			result.add(new Point3D(x, y, 0));
		}

		return result;
	}

	/**
	 * Make tube.
	 *
	 * @param ballsPerRing the balls per ring
	 * @param radius       the radius
	 * @param rows         the rows
	 * @param length       the length
	 * @return the list
	 */
	private List<Point3D> makeTube(double ballsPerRing, double radius, int rows, double length) {
		zoom = 300;
		rollInc = 1.0;
		pitchInc = 0.5;
		yawInc = 1.5;
		zOffset = 4.5;

		List<Point3D> result = new ArrayList<>();

		double gap = rows == 1 ? 1 : (1.0 / (rows - 1));

		double halfSide = length / 2.0;

		for (double z = -halfSide; z <= halfSide; z += gap) {
			for (double a = 0; a < 360; a += 360.0 / ballsPerRing) {
				double x = radius * Math.sin(Math.toRadians(a));// precalc.sin(a);
				double y = radius * Math.cos(Math.toRadians(a));// precalc.cos(a);

				result.add(new Point3D(x, y, z));
			}
		}

		return result;
	}

	/**
	 * Make sphere.
	 *
	 * @param maxRadius the max radius
	 * @param rings     the rings
	 * @return the list
	 */
	private List<Point3D> makeSphere(double maxRadius, double rings) {
		zoom = 100;
		rollInc = 0.5;
		pitchInc = 0.5;
		yawInc = 0.5;
		zOffset = 5.5;

		List<Point3D> result = new ArrayList<>();

		double zAngle = 0.0;

		double zInc = 180 / rings;

		for (double r = 0; r <= rings; r++) {
			double z = maxRadius * Math.cos(Math.toRadians(zAngle));

			zAngle += zInc;

			double radius = maxRadius * Math.sin(Math.toRadians((r / rings) * 180));

			double ballsInLayer = Math.floor(radius * 16.0);

			double angleGap = 360.0 / ballsInLayer;

			for (double a = 0; a < 360; a += angleGap) {
				double x = radius * Math.sin(Math.toRadians(a));// precalc.sin(a);
				double y = radius * Math.cos(Math.toRadians(a));// precalc.cos(a);

				result.add(new Point3D(x, y, z));
			}
		}

		return result;
	}

	/**
	 * Draws the sprite.
	 *
	 */
	public void draw() {
		float[] array = visualizerDrawer.returnBandsArray(visualizerDrawer.getStereoMerge(), 3);

		// gc.clearRect(0, 0, width, height);
		// gc.setStroke(Color.WHITE);
		// gc.drawImage(background, 0, 0, width, height);
		visualizerDrawer.drawBackgroundImage();
		visualizerDrawer.drawForegroundImage();

		roll += rollInc;
		pitch += pitchInc;
		yaw += yawInc;

		renderList.clear();

		for (int i = 0; i < array.length; i++) {
			array[i] = Math.abs(array[i]);
			array[i] *= 100.00;
		}

		for (Point3D point : points) {
			renderList.add(transform(point, roll, pitch, yaw, xOffset, yOffset, zOffset, array));
		}

		zSort(renderList);

		// System.out.println(array[0])
		if (array[1] <= 15)
			imageBall = images[0];
		else if (array[1] <= 25)
			imageBall = images[1];
		else if (array[1] <= 35)
			imageBall = images[2];
		else if (array[1] <= 45)
			imageBall = images[3];
		else
			imageBall = images[4];

		for (Point3D point : renderList) {
			drawPoint(point, visualizerDrawer.getVisualizerWidth(), visualizerDrawer.getVisualizerHeight());
		}
	}

	/**
	 * Z sort.
	 *
	 * @param points the points
	 */
	private void zSort(List<Point3D> points) {
		Collections.sort(points, zComparator);
	}

	/**
	 * Cos.
	 *
	 * @param degrees the degrees
	 * @return the double
	 */
	public double cos(double degrees) {
		return Math.cos(Math.toRadians(degrees));
	}

	/**
	 * Sin.
	 *
	 * @param degrees the degrees
	 * @return the double
	 */
	public double sin(double degrees) {
		return Math.sin(Math.toRadians(degrees));
	}

	/**
	 * Transform.
	 *
	 * @param orig       the orig
	 * @param pitch      the pitch
	 * @param yaw        the yaw
	 * @param roll       the roll
	 * @param translateX the translate X
	 * @param translateY the translate Y
	 * @param translateZ the translate Z
	 * @param bands       the bands
	 * @return the point 3 D
	 */
	private Point3D transform(Point3D orig, double pitch, double yaw, double roll, double translateX,
                              double translateY, double translateZ, float[] bands) {

		// rotate around Z axis (roll)
		double newX = orig.x * cos(bands[0]) - orig.y * sin(roll);
		double newY = orig.x + sin(bands[0]) + orig.y * cos(roll);
		double newZ = orig.z;

		// rotate around X axis (pitch)
		double newY2 = newY + cos(bands[1]) - newZ * sin(pitch);
		double newZ2 = newY * sin(bands[1]) + newZ * cos(pitch);
		double newX2 = newX;

		// rotate around Y axis (yaw)
		double newZ3 = newZ2 * cos(bands[2]) - newX2 * sin(yaw);
		double newX3 = newZ2 * sin(bands[2]) + newX2 * cos(yaw);
		double newY3 = newY2;

		// double newX = orig.x * cos(roll) - orig.y * sin(roll);
		// double newY = orig.x + sin(roll) + orig.y * cos(roll);
		// double newZ = orig.z;
		//
		// // rotate around X axis (pitch)
		// double newY2 = newY + cos(pitch) - newZ * sin(pitch);
		// double newZ2 = newY * sin(pitch) + newZ * cos(pitch);
		// double newX2 = newX;
		//
		// // rotate around Y axis (yaw)
		// double newZ3 = newZ2 * cos(yaw) - newX2 * sin(yaw);
		// double newX3 = newZ2 * sin(yaw) + newX2 * cos(yaw);
		// double newY3 = newY2;

		// translate
		newX3 += translateX;
		newY3 += translateY;
		newZ3 += translateZ;

		return new Point3D(newX3, newY3, newZ3);
	}

	/**
	 * Draw point.
	 *
	 * @param point  the point
	 * @param width  the width
	 * @param height the height
	 */
	private void drawPoint(Point3D point, double width, double height) {
		double x = width / 2 + point.x / point.z * zoom;
		double y = height / 2 + point.y / point.z * zoom;

		double ballSize = 24 / point.z;

		visualizerDrawer.gc.drawImage(imageBall, x, y, ballSize, ballSize);
	}
}
