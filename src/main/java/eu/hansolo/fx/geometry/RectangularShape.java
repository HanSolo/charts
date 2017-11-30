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

package eu.hansolo.fx.geometry;

import eu.hansolo.fx.geometry.transform.BaseTransform;
import eu.hansolo.fx.geometry.tools.Point;


public abstract class RectangularShape extends Shape {

    protected RectangularShape() { }

    public abstract double getX();
    public abstract double getY();

    public abstract double getWidth();
    public abstract double getHeight();

    public double getMinX() {
        return getX();
    }
    public double getMinY() {
        return getY();
    }

    public double getMaxX() {
        return getX() + getWidth();
    }
    public double getMaxY() {
        return getY() + getHeight();
    }

    public double getCenterX() {
        return getX() + getWidth() / 2;
    }
    public double getCenterY() {
        return getY() + getHeight() / 2;
    }

    public abstract boolean isEmpty();

    public abstract void set(double x, double y, double w, double h);

    public void set(final Point POINT, final double WIDTH, final double HEIGHT) {
        set(POINT.getX(), POINT.getY(), WIDTH, HEIGHT);
    }

    public void setFromDiagonal(Point p1, Point p2) {
        setFromDiagonal(p1.x, p1.y, p2.x, p2.y);
    }
    public void setFromDiagonal(double x1, double y1, double x2, double y2) {
        if (x2 < x1) {
            double t = x1;
            x1 = x2;
            x2 = t;
        }
        if (y2 < y1) {
            double t = y1;
            y1 = y2;
            y2 = t;
        }
        set(x1, y1, x2 - x1, y2 - y1);
    }

    public void setFromCenter(final Point CENTER, final Point CORNER) {
        setFromCenter(CENTER.x, CENTER.y, CORNER.x, CORNER.y);
    }
    public void setFromCenter(final double CENTER_X, final double CENTER_Y, final double CORNER_X, final double CORNER_Y) {
        double halfW = Math.abs(CORNER_X - CENTER_X);
        double halfH = Math.abs(CORNER_Y - CENTER_Y);
        set(CENTER_X - halfW, CENTER_Y - halfH, halfW * 2.0, halfH * 2.0);
    }

    public boolean contains(Point POINT) { return contains(POINT.x, POINT.y); }

    public RectBounds getBounds() {
        double width  = getWidth();
        double height = getHeight();
        if (width < 0 || height < 0) { return new RectBounds(); }
        double x  = getX();
        double y  = getY();
        double x1 = Math.floor(x);
        double y1 = Math.floor(y);
        double x2 = Math.ceil(x + width);
        double y2 = Math.ceil(y + height);
        return new RectBounds(x1, y1, x2, y2);
    }

    public PathIterator getPathIterator(BaseTransform TRANSFORM, double FLATNESS) {
        return new FlatteningPathIterator(getPathIterator(TRANSFORM), FLATNESS);
    }
}