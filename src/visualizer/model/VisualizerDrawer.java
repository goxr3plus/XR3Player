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
     *            The Oscilloscope with have 2 lines->stereo or 1 line->merge
     *            left and right audio
     */
    public void drawOscilloscope(boolean stereo) {
	oscilloscope.drawOscilloscope(stereo);
    }

    /**
     * Draws an Oscilloscope with up and down Lines
     */
    public void drawOscilloScopeLines() {
	oscilloscope.drawOscilloScopeLines();
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

    /**
     * Draws a Polyspiral and 4 arcs
     */
    public void drawPolySpiral2() {
	polySpiral.drawPolyspiral2();
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
