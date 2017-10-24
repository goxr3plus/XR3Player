/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package main.java.com.goxr3plus.xr3player.xplayer.visualizer.geometry;

import javafx.scene.paint.Color;

/**
 * 
 * @author Jörn Hameister
 * 
 * http://www.hameister.org
 * 
 */
public class MandelbrotBean {

    public enum ColorSchema {

        GREEN, RED, YELLOW, BLUE, CYAN, MAGENTA
    }
    // Paint and calulation sizes
    private double reMin;
    private double reMax;
    private double imMin;
    private double imMax;
    // z + zi for Julia set
    private double z;
    private double zi;
    private int convergenceSteps;
    private Color convergenceColor = Color.WHITE;
    private ColorSchema colorSchema = ColorSchema.GREEN;

    public boolean isIsMandelbrot() {
        // if z is 0 then it is a Mandelbrot set
        return (getZ() == 0 && getZi() == 0) ? true : false;
    }

    public MandelbrotBean(int convergenceSteps, double reMin, double reMax, double imMin, double imMax, double z, double zi) {
        this.convergenceSteps = convergenceSteps;
        this.reMin = reMin;
        this.reMax = reMax;
        this.imMin = imMin;
        this.imMax = imMax;
        this.z = z;
        this.zi = zi;
    }

    public int getConvergenceSteps() {
        return convergenceSteps;
    }

    public double getReMin() {
        return reMin;
    }

    public double getReMax() {
        return reMax;
    }

    public double getImMin() {
        return imMin;
    }

    public double getImMax() {
        return imMax;
    }

    public double getZ() {
        return z;
    }

    public double getZi() {
        return zi;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setZi(double zi) {
        this.zi = zi;
    }

    public Color getConvergenceColor() {
        return convergenceColor;
    }

    public void setConvergenceColor(Color convergenceColor) {
        this.convergenceColor = convergenceColor;
    }

    public ColorSchema getColorSchema() {
        return colorSchema;
    }

    public void setColorSchema(ColorSchema colorSchema) {
        this.colorSchema = colorSchema;
    }
}
