/*
 * Copyright (c) 2017 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.charts.heatmap;

public class HeatMapSpot {
    private double              x;
    private double              y;
    private double              radius;
    private OpacityDistribution opacityDistribution;


    // ******************** Constructors **************************************
    public HeatMapSpot(final double X, final double Y) {
        this(X, Y, 15.5, OpacityDistribution.CUSTOM);
    }
    public HeatMapSpot(final double X, final double Y, final double RADIUS) {
        this(X, Y, RADIUS, OpacityDistribution.CUSTOM);
    }
    public HeatMapSpot(final double X, final double Y, final double RADIUS, final OpacityDistribution OPACITY_GRADIENT) {
        x                   = X;
        y                   = Y;
        radius              = RADIUS;
        opacityDistribution = OPACITY_GRADIENT;
    }


    // ******************** Methods *******************************************
    public double getX() { return x; }
    public double getY() { return y; }

    public double getRadius() { return radius; }
    public void setRadius(final double RADIUS) { radius = RADIUS; }

    public OpacityDistribution getOpacityDistribution() { return opacityDistribution; }
    public void setOpacityDistribution(final OpacityDistribution OPACITY_GRADIENT) { opacityDistribution = OPACITY_GRADIENT; }
}

