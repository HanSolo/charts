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

package eu.hansolo.fx.charts.tools;

public class CtxDimension {
    private double minX;
    private double oldMinX;
    private double minY;
    private double oldMinY;
    private double maxX;
    private double oldMaxX;
    private double maxY;
    private double oldMaxY;
    private double width;
    private double oldWidth;
    private double height;
    private double oldHeight;


    // ******************** Constructors **************************************
    public CtxDimension() {
        this(0, 0, 0, 0);
    }
    public CtxDimension(final double WIDTH, final double HEIGHT) {
        this(0, 0, WIDTH, HEIGHT);
    }
    public CtxDimension(final double MIN_X, final double MIN_Y, final double MAX_X, final double MAX_Y) {
        minX      = MIN_X;
        oldMinX   = MIN_X;
        minY      = MIN_Y;
        oldMinY   = MIN_Y;
        maxX      = MAX_X;
        oldMaxX   = MAX_X;
        maxY      = MAX_Y;
        oldMaxY   = MAX_Y;
        width     = MAX_X - MIN_X;
        oldWidth  = width;
        height    = MAX_Y - MIN_Y;
        oldHeight = height;
    }


    // ******************** Methods *******************************************
    public double getOldMinX() { return oldMinX; }
    public double getMinX() { return minX; }
    public void setMinX(final double X) {
        oldWidth = width;
        oldMinX  = minX;
        minX     = X;
        width    = maxX - minX;
    }

    public double getOldMaxX() { return oldMaxX; }
    public double getMaxX() { return maxX; }
    public void setMaxX(final double X) {
        oldWidth = width;
        oldMaxX  = maxX;
        maxX     = X;
        width    = maxX - minX;
    }

    public double getOldMinY() { return oldMinY; }
    public double getMinY() { return minY; }
    public void setMinY(final double Y) {
        oldHeight = height;
        oldMinY   = minY;
        minY      = Y;
        height    = maxY - minY;
    }

    public double getOldMaxY() { return oldMaxY; }
    public double getMaxY() { return maxY; }
    public void setMaxY(final double Y) {
        oldHeight = height;
        oldMaxY   = maxY;
        maxY      = Y;
        height    = maxY - minY;
    }

    public void shiftX(final double SHIFT_X) {
        setMinX(minX + SHIFT_X);
        setMaxX(maxX + SHIFT_X);
    }
    public void shiftY(final double SHIFT_Y) {
        setMinY(minY + SHIFT_Y);
        setMaxY(maxY + SHIFT_Y);
    }

    public double getOldWidth() { return oldWidth; }
    public double getWidth() { return width; }

    public double getOldHeight() { return oldHeight; }
    public double getHeight() { return height; }

    public double getCenterX() { return minX + getWidth() * 0.5; }
    public double getCenterY() { return minY + getHeight() * 0.5; }

    public void set(final CtxDimension DIM) {
        setMinX(DIM.getMinX());
        setMinY(DIM.getMinY());
        setMaxX(DIM.getMaxX());
        setMaxY(DIM.getMaxY());
    }
    public void set(final double MIN_X, final double MIN_Y, final double MAX_X, final double MAX_Y) {
        setMinX(MIN_X);
        setMinY(MIN_Y);
        setMaxX(MAX_X);
        setMaxY(MAX_Y);
    }

    public boolean contains(final double X, final double Y) {
        return (Double.compare(X, getMinX()) >= 0 &&
                Double.compare(X, getMaxX()) <= 0 &&
                Double.compare(Y, getMinY()) >= 0 &&
                Double.compare(Y, getMaxY()) <= 0);
    }

    @Override public String toString() {
        return new StringBuilder().append("{\n")
                                  .append("  \"minX\"  : ").append(getMinX()).append(",\n")
                                  .append("  \"minY\"  : ").append(getMinY()).append(",\n")
                                  .append("  \"maxX\"  : ").append(getMaxX()).append(",\n")
                                  .append("  \"maxY\"  : ").append(getMaxY()).append(",\n")
                                  .append("  \"width\" : ").append(getWidth()).append(",\n")
                                  .append("  \"height\": ").append(getHeight()).append("\n")
                                  .append("}").toString();
    }
}
