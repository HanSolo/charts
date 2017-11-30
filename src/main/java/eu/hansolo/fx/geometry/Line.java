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


public class Line extends Shape {
    public double x1;
    public double y1;
    public double x2;
    public double y2;


    public Line() { }
    public Line(final double X1, final double Y1, final double X2, final double Y2) {
        setLine(X1, Y1, X2, Y2);
    }
    public Line(final Point P1, final Point P2) {
        setLine(P1, P2);
    }


    public void setLine(final double X1, final double Y1, final double X2, final double Y2) {
        x1 = X1;
        y1 = Y1;
        x2 = X2;
        y2 = Y2;
    }
    public void setLine(final Point P1, final Point P2) { setLine(P1.x, P1.y, P2.x, P2.y); }
    public void setLine(final Line LINE) { setLine(LINE.x1, LINE.y1, LINE.x2, LINE.y2); }

    public RectBounds getBounds() {
        RectBounds b = new RectBounds();
        b.setBoundsAndSort(x1, y1, x2, y2);
        return b;
    }

    @Override public boolean contains(double X, double Y) { return false; }
    @Override public boolean contains(double X, double Y, double WIDTH, double HEIGHT) { return false; }
    @Override public boolean contains(Point POINT) { return false; }

    @Override public boolean intersects(final double X, final double Y, final double WIDTH, final double HEIGHT) {
        int out1, out2;
        if ((out2 = outcode(X, Y, WIDTH, HEIGHT, x2, y2)) == 0) {
            return true;
        }
        double px = x1;
        double py = y1;
        while ((out1 = outcode(X, Y, WIDTH, HEIGHT, px, py)) != 0) {
            if ((out1 & out2) != 0) {
                return false;
            }
            if ((out1 & (OUT_LEFT | OUT_RIGHT)) != 0) {
                px = X;
                if ((out1 & OUT_RIGHT) != 0) {
                    px += WIDTH;
                }
                py = y1 + (px - x1) * (y2 - y1) / (x2 - x1);
            } else {
                py = Y;
                if ((out1 & OUT_BOTTOM) != 0) {
                    py += HEIGHT;
                }
                px = x1 + (py - y1) * (x2 - x1) / (y2 - y1);
            }
        }
        return true;
    }

    public static int relativeCCW(double x1, double y1, double x2, double y2, double px, double py) {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double ccw = px * y2 - py * x2;
        if (Double.compare(ccw, 0) == 0.0) {
            ccw = px * x2 + py * y2;
            if (ccw > 0.0) {
                px -= x2;
                py -= y2;
                ccw = px * x2 + py * y2;
                if (ccw < 0.0) {
                    ccw = 0.0;
                }
            }
        }
        return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
    }

    public int relativeCCW(final double PX, final double PY) { return relativeCCW(x1, y1, x2, y2, PX, PY); }
    public int relativeCCW(final Point POINT) { return relativeCCW(x1, y1, x2, y2, POINT.x, POINT.y); }

    public static boolean linesIntersect(final double X1, final double Y1, final double X2, final double Y2,
                                         final double X3, final double Y3, final double X4, final double Y4) {
        return ((relativeCCW(X1, Y1, X2, Y2, X3, Y3) * relativeCCW(X1, Y1, X2, Y2, X4, Y4) <= 0) &&
                (relativeCCW(X3, Y3, X4, Y4, X1, Y1) * relativeCCW(X3, Y3, X4, Y4, X2, Y2) <= 0));
    }

    public boolean intersectsLine(final double X1, final double Y1, final double X2, final double Y2) { return linesIntersect(X1, Y1, X2, Y2, this.x1, this.y1, this.x2, this.y2); }
    public boolean intersectsLine(final Line LINE) { return linesIntersect(LINE.x1, LINE.y1, LINE.x2, LINE.y2, this.x1, this.y1, this.x2, this.y2); }

    public static double ptSegDistSq(double x1, double y1, double x2, double y2, double px, double py) {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double dotprod = px * x2 + py * y2;
        double projlenSq;
        if (dotprod <= 0) {
            projlenSq = 0.0;
        } else {
            px = x2 - px;
            py = y2 - py;
            dotprod = px * x2 + py * y2;
            if (Double.compare(dotprod, 0) <= 0) {
                projlenSq = 0.0;
            } else {
                projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
            }
        }
        double lenSq = px * px + py * py - projlenSq;
        if (lenSq < 0) { lenSq = 0; }
        return lenSq;
    }

    public static double ptSegDist(final double X1, final double Y1, final double X2, final double Y2, final double PX, final double PY) { return  Math.sqrt(ptSegDistSq(X1, Y1, X2, Y2, PX, PY)); }

    public double ptSegDistSq(final double PX, final double PY) { return ptSegDistSq(x1, y1, x2, y2, PX, PY); }
    public double ptSegDistSq(final Point POINT) { return ptSegDistSq(x1, y1, x2, y2, POINT.x, POINT.y); }

    public double ptSegDist(final double PX, final double PY) { return ptSegDist(x1, y1, x2, y2, PX, PY); }
    public double ptSegDist(final Point POINT) { return ptSegDist(x1, y1, x2, y2, POINT.x, POINT.y); }

    public static double ptLineDistSq(double x1, double y1, double x2, double y2, double px, double py) {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double dotprod   = px * x2 + py * y2;
        double projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
        double lenSq     = px * px + py * py - projlenSq;
        if (lenSq < 0) { lenSq = 0.0; }
        return lenSq;
    }

    public static double ptLineDist(final double X1, final double Y1, final double X2, final double Y2, final double PX, final double PY) {
        return  Math.sqrt(ptLineDistSq(X1, Y1, X2, Y2, PX, PY));
    }

    public double ptLineDistSq(final double PX, final double PY) { return ptLineDistSq(x1, y1, x2, y2, PX, PY); }
    public double ptLineDistSq(final Point POINT) { return ptLineDistSq(x1, y1, x2, y2, POINT.x, POINT.y); }

    public double ptLineDist(final double PX, final double PY) { return ptLineDist(x1, y1, x2, y2, PX, PY); }
    public double ptLineDist(final Point POINT) { return ptLineDist(x1, y1, x2, y2, POINT.x, POINT.y); }

    public PathIterator getPathIterator(BaseTransform TRANSFORM) { return new LineIterator(this, TRANSFORM); }
    public PathIterator getPathIterator(BaseTransform TRANSFORM, double FLATNESS) { return new LineIterator(this, TRANSFORM); }

    @Override public Line copy() { return new Line(x1, y1, x2, y2); }

    @Override public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj instanceof Line) {
            Line line = (Line) obj;
            return ((x1 == line.x1) && (y1 == line.y1) && (x2 == line.x2) && (y2 == line.y2));
        }
        return false;
    }
}
