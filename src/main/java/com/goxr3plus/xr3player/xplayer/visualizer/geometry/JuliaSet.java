/**
 * 
 */
package main.java.com.goxr3plus.xr3player.xplayer.visualizer.geometry;

import javafx.scene.paint.Color;
import main.java.com.goxr3plus.xr3player.xplayer.visualizer.core.VisualizerDrawer;
import main.java.com.goxr3plus.xr3player.xplayer.visualizer.geometry.MandelbrotBean.ColorSchema;

/*-----------------------------------------------------------------------
 * 
 * -----------------------------------------------------------------------
 * 
 * 
 * 						    Julia Set
 * 
 * -----------------------------------------------------------------------
 * 
 * -----------------------------------------------------------------------
 */
public class JuliaSet {

	/**
	 * Size of the coordinate system for the Julia set
	 */
	private static final double JULIA_RE_MIN = -1.5;

	/**
	 * The julia re max.
	 */
	private static final double JULIA_RE_MAX = 1.5;

	/**
	 * The julia im min.
	 */
	private static final double JULIA_IM_MIN = -1.5;

	/**
	 * The julia im max.
	 */
	private static final double JULIA_IM_MAX = 1.5;

	/** The bean. */
	private final MandelbrotBean bean = new MandelbrotBean(50, JULIA_RE_MIN, JULIA_RE_MAX, JULIA_IM_MIN, JULIA_IM_MAX,
			0.3, -0.5);

	/** VisualizerDrawer instance. */
	private final VisualizerDrawer visualizerDrawer;

	/**
	 * Constructor.
	 *
	 * @param visualizerDrawer the visualizer drawer
	 */
	public JuliaSet(VisualizerDrawer visualizerDrawer) {
		this.visualizerDrawer = visualizerDrawer;
	}

	/**
	 * Draws the Julia Set.
	 */
	public void drawJuliaSet() {

		// int X_OFFSET = 50
		// int Y_OFFSET = 50

		// Move canvans to the middlepoint
		visualizerDrawer.setLayoutX(visualizerDrawer.canvasWidth / 2.00);
		visualizerDrawer.setLayoutY(visualizerDrawer.canvasHeight / 2.00);
		// setLayoutX(canvasWidth / ( bean.getReMax() - bean.getReMin() ) / 2 +
		// X_OFFSET / 2)
		// setLayoutY(canvasHeight / ( bean.getImMax() - bean.getImMin() ) / 2 -
		// Y_OFFSET * 2)

		bean.setColorSchema(ColorSchema.GREEN);
		bean.setConvergenceColor(Color.BLUEVIOLET);

		// Calculations
		float[] array = visualizerDrawer.returnBandsArray(visualizerDrawer.stereoMerge, 2);

		// System.out.println(array[0] + " , " + array[1])
		bean.setZ(-array[0] + array[0] < 0.5 ? -0.4 : -0.1);// bean.setZ(0.3)
		bean.setZi(array[1] + 0.4);// bean.setZi(-0.5)

		// Paint it
		double precision = Math.max((bean.getReMax() - bean.getReMin()) / visualizerDrawer.canvasWidth,
				(bean.getImMax() - bean.getImMin()) / visualizerDrawer.canvasHeight); // 0.004

		double convergenceValue;
		for (double c = bean.getReMin(), xR = 0; xR < visualizerDrawer.canvasWidth; c += precision, xR++) {
			for (double ci = bean.getImMin(), yR = 0; yR < visualizerDrawer.canvasHeight; ci += precision, yR++) {

				// Calculate convergenceValue
				convergenceValue = bean.isIsMandelbrot() ? checkConvergence(ci, c, 0, 0, bean.getConvergenceSteps())
						: checkConvergence(bean.getZi(), bean.getZ(), ci, c, bean.getConvergenceSteps());

				double t1 = convergenceValue / bean.getConvergenceSteps(); // (50.0..)
				double c1 = Math.min(255 * 2 * t1, 255);
				double c2 = Math.max(255 * (2 * t1 - 1), 0);

				// Set Fill
				visualizerDrawer.gc.setFill(convergenceValue != bean.getConvergenceSteps() ? getColorSchema(c1, c2)
						: bean.getConvergenceColor());

				visualizerDrawer.gc.fillRect(xR, yR, 1, 1);
			}
		}
	}

	/**
	 * Checks the convergence of a coordinate (c, ci) The convergence factor
	 * determines the color of the point.
	 *
	 * @param ci               the ci
	 * @param c                the c
	 * @param z                the z
	 * @param zi               the zi
	 * @param convergenceSteps the convergence steps
	 * @return the int
	 */
	private int checkConvergence(double ci, double c, double z, double zi, int convergenceSteps) {
		for (int i = 0; i < convergenceSteps; i++) {
			double ziT = 2 * (z * zi);
			double zT = z * z - (zi * zi);
			z = zT + c;
			zi = ziT + ci;

			if (z * z + zi * zi >= 4.0) {
				return i;
			}
		}
		return convergenceSteps;
	}

	/**
	 * Gets the color schema.
	 *
	 * @param c1 the c 1
	 * @param c2 the c 2
	 * @return the color schema
	 */
	private Color getColorSchema(double c1, double c2) {
		MandelbrotBean.ColorSchema colorSchema = bean.getColorSchema();
		switch (colorSchema) {
		case RED:
			return Color.color(c1 / 255.0, c2 / 255.0, c2 / 255.0);
		case YELLOW:
			return Color.color(c1 / 255.0, c1 / 255.0, c2 / 255.0);
		case MAGENTA:
			return Color.color(c1 / 255.0, c2 / 255.0, c1 / 255.0);
		case BLUE:
			return Color.color(c2 / 255.0, c2 / 255.0, c1 / 255.0);
		case GREEN:
			return Color.color(c2 / 255.0, c1 / 255.0, c2 / 255.0);
		case CYAN:
			return Color.color(c2 / 255.0, c1 / 255.0, c1 / 255.0);
		default:
			return Color.color(c2 / 255.0, c1 / 255.0, c2 / 255.0);
		}
	}

}
