/**
 * 
 */
package visualizer.model;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

/**
 * The Class VisualizerDrawer.
 *
 * @author GOXR3PLUS
 */
public class VisualizerDrawer extends VisualizerModel {

    private Oscilloscope oscilloscope = new Oscilloscope(this);
    Polyspiral polySpiral = new Polyspiral(this);
    protected Sierpinski sierpinski = new Sierpinski(this);
    private JuliaSet juliaSet = new JuliaSet(this);

    // -----------------------------Images---------------------------------

    /** The foreground image. */
    public Image foregroundImage = new Image(VisualizerModel.class.getResourceAsStream("foreground.png"));

    /** The background image. */
    public Image backgroundImage;// new
				 // Image(VisualizerModel.class.getResourceAsStream("background.gif"))

    /** The cool effect. */
    final static Image coolEffect = new Image(VisualizerModel.class.getResourceAsStream("anim1.gif"));

    /** The yellow light. */
    final static Image yellowLight = new Image(VisualizerModel.class.getResourceAsStream("yellowLight.png"));

    /** The blue light. */
    final static Image blueLight = new Image(VisualizerModel.class.getResourceAsStream("blueLight.png"));

    /** The grey light. */
    final static Image greyLight = new Image(VisualizerModel.class.getResourceAsStream("greyLight.png"));

    /** The light blue light. */
    final static Image lightBlueLight = new Image(VisualizerModel.class.getResourceAsStream("lightBlueLight.png"));

    /** The red light. */
    final static Image redLight = new Image(VisualizerModel.class.getResourceAsStream("redLight.png"));

    // ---------------------------------------------------------------------

    /*-----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 			        Oscilloscope
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Draws an Oscilloscope
     * 
     * @param stereo
     *            The Oscilloscope with have 2 lines->stereo or 1 line->merge left and right audio
     */
    public void drawOscilloscope(boolean stereo) {
	oscilloscope.drawOscilloscope(stereo);
    }

    double inc = 0;
    /** The color index. */
    private int colorIndex = 0;

    /**
     * Draws an Oscilloscope with up and down Lines
     */
    public void drawOscilloScopeLines() {
	float[] pSample = stereoMerge(pLeftChannel, pRightChannel);
	float[] array = returnBandsArray(pSample, 4);

	//oscilloscope.drawOscilloScopeLines()
	inc = (inc + 0.3 + Math.abs(array[0])) % 360;
	double len = 6;
	double angleIncrement = Math.toRadians(inc);
	double x1 = getWidth() / 2.00;
	double y1 = getHeight() / 2.00;
	double angle = angleIncrement;

	//gc.setStroke(Color.RED);
	gc.setLineWidth(2);
	gc.strokeArc(5, 5, getWidth() - 10, getHeight() - 10, 90, 360 * Math.abs(array[0]), ArcType.OPEN);
	gc.setStroke(Color.CYAN);
	gc.strokeArc(15, 15, getWidth() - 30, getHeight() - 30, 180, 360 * Math.abs(array[1]), ArcType.OPEN);
	gc.setStroke(Color.FIREBRICK);
	gc.strokeArc(25, 25, getWidth() - 50, getHeight() - 50, 270, 360 * Math.abs(array[2]), ArcType.OPEN);
	gc.setStroke(Color.CHARTREUSE);
	gc.strokeArc(35, 35, getWidth() - 70, getHeight() - 70, 360, 360 * Math.abs(array[3]), ArcType.OPEN);
	gc.setLineWidth(1);

	int until = (int) (x1 + y1) / 2; //(int) (getWidth()/2 * Math.abs(array[0]));
	for (int i = 0; i < until; i++) {

	    colorIndex = (colorIndex == 360 - 1) ? 0 : colorIndex + 1;
	    gc.setStroke(Color.hsb(colorIndex, 1.0f, 1.0f));

	    double x2 = x1 + Math.cos(angle) * len;
	    double y2 = y1 - Math.sin(angle) * len;
	    gc.strokeLine((int) x1, (int) y1, (int) x2, (int) y2);
	    x1 = x2;
	    y1 = y2;

	    len += 3 * Math.abs(array[1]);

	    angle = (angle + angleIncrement) % (Math.PI * 2);
	}
    }

    /*-----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 				Rosette and Polyspiral
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Draws a Rosette and a Polyspiral.
     */
    public void drawPolySpiral() {
	polySpiral.drawPolySpiral();
    }

    /*-----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 			Spectrum Analyzer
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Draws a spectrum analyzer using rectangles.
     */
    public void drawSpectrumBars() {
	float[] pSample = stereoMerge(pLeftChannel, pRightChannel);

	float barWidth = (float) canvasWidth / (float) saBands;
	float[] array = returnBandsArray(pSample, saBands);
	float c = 0;
	// BackgroundImage
	if (backgroundImage != null)
	    gc.drawImage(backgroundImage, 0, 0, canvasWidth, canvasHeight);

	for (int band = 0; band < saBands; band++) {
	    drawSpectrumBar((int) c, canvasHeight, (int) barWidth - 1, (int) (array[band] * canvasHeight), band);
	    c += barWidth;
	}
    }

    /**
     * Draw spectrum analyser bar.
     *
     * @param pX
     *            the p X
     * @param pY
     *            the p Y
     * @param pWidth
     *            the width
     * @param pHeight
     *            the height
     * @param band
     *            the band
     */
    private void drawSpectrumBar(int pX, int pY, int pWidth, int pHeight, int band) {
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
	    gc.drawImage(backgroundImage, 0, 0, canvasWidth, canvasHeight);

	float wLeft = 0.0f;
	float wRight = 0.0f;
	float wSadfrr = vuDecay * frameRateRatioHint;

	for (int a = 0; a < pLeftChannel.length; a++) {
	    wLeft += Math.abs(pLeftChannel[a]);
	    wRight += Math.abs(pRightChannel[a]);
	}

	wLeft = (wLeft = (wLeft * 2.0f) / pLeftChannel.length) > 1.0f ? 1.0f : wLeft;
	wRight = (wRight = (wRight * 2.0f) / pRightChannel.length) > 1.0f ? 1.0f : wRight;

	/*
	 * vuAverage += ( ( wLeft + wRight ) / 2.0f ); vuSamples++; if (
	 * vuSamples > 128 ) { vuSamples /= 2.0f; vuAverage /= 2.0f; }
	 */

	if (wLeft >= (oldLeft - wSadfrr))
	    oldLeft = wLeft;
	else {
	    oldLeft -= wSadfrr;
	    if (oldLeft < 0)
		oldLeft = 0;

	}

	if (wRight >= (oldRight - wSadfrr))
	    oldRight = wRight;
	else {
	    oldRight -= wSadfrr;
	    if (oldRight < 0)
		oldRight = 0;

	}

	int wHeight = (canvasHeight >> 1) - 20;
	drawVolumeMeterBar(16, 16, (int) (oldLeft * (float) (canvasWidth - 32)), wHeight);
	drawVolumeMeterBar(16, wHeight + 32, (int) (oldRight * (float) (canvasWidth - 32)), wHeight);

    }

    /**
     * Draw volume meter bar.
     *
     * @param x
     *            the x
     * @param y
     *            the y
     * @param pWidth
     *            the width
     * @param pHeight
     *            the height
     */
    private void drawVolumeMeterBar(int x, int y, int pWidth, int pHeight) {

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
    @Deprecated
    public void drawCircular() {
	float[] pSample = stereoMerge(pLeftChannel, pRightChannel);

	// backgoundImage
	if (backgroundImage != null)
	    gc.drawImage(backgroundImage, 0, 0, canvasWidth, canvasHeight);
	float[] array = returnBandsArray(pSample, 1);
	int arcHeight = canvasHeight / 2;

	gc.setFill(Color.WHITE);
	// gc.fillOval(iX(-w*2), iY(w*2), w, w);
	gc.fillArc(canvasWidth / 2.00, canvasHeight / 2.00, arcHeight, arcHeight, 0, 360 * array[0], ArcType.ROUND);
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
	sierpinski.drawSierpinski();
    }

    /*-----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 						    Julia Fractals
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Draws the Julia Set
     */
    public void drawJuliaSet() {
	juliaSet.drawJuliaSet();
    }

}
