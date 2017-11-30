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


public class Ellipse extends RectangularShape {
    public double x;
    public double y;
    public double width;
    public double height;


    public Ellipse() { }
    public Ellipse(final double X, final double Y, final double WIDTH, final double HEIGHT) {
        set(X, Y, WIDTH, HEIGHT);
    }
    /*public Ellipse(double centerX, double centerY, double radiusX, double radiusY) {
        setFrame(centerX - radiusX, centerY - radiusY, radiusX * 2, radiusY * 2);
    }*/


    @Override public double getX() { return x; }
    @Override public double getY() { return y; }

    @Override public double getWidth() { return width; }
    @Override public double getHeight() { return height; }

    public double getCenterX() { return (x + width) / 2; }
    public double getCenterY() { return (y + height) / 2; }

    public double getRadiusX() { return width / 2; }
    public double getRadiusY() { return height / 2; }

    @Override public boolean isEmpty() { return (Double.compare(width, 0) <= 0 || Double.compare(height, 0) <= 0); }

    public void set(final double X, final double Y, final double WIDTH, final double HEIGHT) {
        x      = X;
        y      = Y;
        width  = WIDTH;
        height = HEIGHT;
    }

    public RectBounds getBounds() { return new RectBounds(x, y, x + width, y + height); }

    public boolean intersects(double X, double Y, double WIDTH, double HEIGHT) {
        if (WIDTH <= 0 || HEIGHT <= 0) { return false; }

        double ellw = this.width;

        if (ellw <= 0) { return false; }

        double normx0 = (X - this.x) / ellw - 0.5;
        double normx1 = normx0 + WIDTH / ellw;
        double ellh   = this.height;

        if (ellh <= 0) { return false; }

        double normy0 = (Y - this.y) / ellh - 0.5;
        double normy1 = normy0 + HEIGHT / ellh;
        double nearx, neary;
        if (normx0 > 0) {
            nearx = normx0;
        } else if (normx1 < 0) {
            nearx = normx1;
        } else {
            nearx = 0;
        }
        if (normy0 > 0) {
            neary = normy0;
        } else if (normy1 < 0) {
            neary = normy1;
        } else {
            neary = 0;
        }
        return (nearx * nearx + neary * neary) < 0.25;
    }

    public boolean contains(double X, double Y) {
        double ellw = this.width;

        if (ellw <= 0) { return false; }

        double normx = (X - this.x) / ellw - 0.5;
        double ellh = this.height;

        if (ellh <= 0) { return false; }

        double normy = (Y - this.y) / ellh - 0.5;

        return (normx * normx + normy * normy) < 0.25;
    }
    public boolean contains(double X, double Y, double WIDTH, double HEIGHT) {
        return (contains(X, Y) &&
                contains(X + WIDTH, Y) &&
                contains(X, Y + HEIGHT) &&
                contains(X + WIDTH, Y + HEIGHT));
    }

    public PathIterator getPathIterator(BaseTransform TRANSFORM) { return new EllipseIterator(this, TRANSFORM); }

    @Override public Ellipse copy() { return new Ellipse(x, y, width, height); }

    @Override public boolean equals(final Object OBJECT) {
        if (OBJECT == this) { return true; }
        if (OBJECT instanceof Ellipse) {
            Ellipse e2d = (Ellipse) OBJECT;
            return ((x == e2d.x) && (y == e2d.y) && (width == e2d.width) && (height == e2d.height));
        }
        return false;
    }
}