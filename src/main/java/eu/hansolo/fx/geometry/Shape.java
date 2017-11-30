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
import eu.hansolo.fx.geometry.tools.IllegalPathStateException;
import eu.hansolo.fx.geometry.tools.Point;
import javafx.scene.canvas.GraphicsContext;


public abstract class Shape {
    public static final int RECT_INTERSECTS = 0x80000000;
    public static final int OUT_LEFT        = 1;
    public static final int OUT_TOP         = 2;
    public static final int OUT_RIGHT       = 4;
    public static final int OUT_BOTTOM      = 8;

    public abstract RectBounds getBounds();

    public boolean contains(final Point POINT) { return contains(POINT.x, POINT.y); }
    public abstract boolean contains(final double X, final double Y);
    public boolean contains(final RectBounds BOUNDS) {
        double x = BOUNDS.getMinX();
        double y = BOUNDS.getMinY();
        double w = BOUNDS.getMaxX() - x;
        double h = BOUNDS.getMaxY() - y;
        return contains(x, y, w, h);
    }
    public abstract boolean contains(final double X, final double Y, final double WIDTH, final double HEIGHT);

    public abstract boolean intersects(final double X, final double Y, final double WIDTH, final double HEIGHT);
    public boolean intersects(final RectBounds BOUNDS) {
        double x = BOUNDS.getMinX();
        double y = BOUNDS.getMinY();
        double w = BOUNDS.getMaxX() - x;
        double h = BOUNDS.getMaxY() - y;
        return intersects(x, y, w, h);
    }

    public abstract PathIterator getPathIterator(final BaseTransform TRANSFORM);
    public abstract PathIterator getPathIterator(final BaseTransform TRANSFORM, final double FLATNESS);

    public abstract Shape copy();

    public static int pointCrossingsForPath(final PathIterator PATH_ITERATOR, final double POINT_X, final double POINT_Y) {
        if (PATH_ITERATOR.isDone()) { return 0; }
        double coords[] = new double[6];
        if (PATH_ITERATOR.currentSegment(coords) != PathIterator.MOVE_TO) {
            throw new IllegalPathStateException("missing initial moveto in path definition");
        }
        PATH_ITERATOR.next();
        double movx = coords[0];
        double movy = coords[1];
        double curx = movx;
        double cury = movy;
        double endx;
        double endy;
        int crossings = 0;
        while (!PATH_ITERATOR.isDone()) {
            switch (PATH_ITERATOR.currentSegment(coords)) {
                case PathIterator.MOVE_TO:
                    if (cury != movy) { crossings += pointCrossingsForLine(POINT_X, POINT_Y, curx, cury, movx, movy); }
                    movx = curx = coords[0];
                    movy = cury = coords[1];
                    break;
                case PathIterator.LINE_TO:
                    endx = coords[0];
                    endy = coords[1];
                    crossings += pointCrossingsForLine(POINT_X, POINT_Y, curx, cury, endx, endy);
                    curx = endx;
                    cury = endy;
                    break;
                case PathIterator.QUAD_TO:
                    endx = coords[2];
                    endy = coords[3];
                    crossings += pointCrossingsForQuad(POINT_X, POINT_Y, curx, cury, coords[0], coords[1], endx, endy, 0);
                    curx = endx;
                    cury = endy;
                    break;
                case PathIterator.BEZIER_TO:
                    endx = coords[4];
                    endy = coords[5];
                    crossings += pointCrossingsForCubic(POINT_X, POINT_Y, curx, cury, coords[0], coords[1], coords[2], coords[3], endx, endy, 0);
                    curx = endx;
                    cury = endy;
                    break;
                case PathIterator.CLOSE:
                    if (cury != movy) { crossings += pointCrossingsForLine(POINT_X, POINT_Y, curx, cury, movx, movy); }
                    curx = movx;
                    cury = movy;
                    break;
            }
            PATH_ITERATOR.next();
        }
        if (cury != movy) { crossings += pointCrossingsForLine(POINT_X, POINT_Y, curx, cury, movx, movy); }
        return crossings;
    }

    public static int pointCrossingsForLine(final double POINT_X, final double POINT_Y,
                                            final double X0, final double Y0,
                                            final double X1, final double Y1) {
        if (POINT_Y <  Y0 && POINT_Y <  Y1) { return 0; }
        if (POINT_Y >= Y0 && POINT_Y >= Y1) { return 0; }
        if (POINT_X >= X0 && POINT_X >= X1) { return 0; }
        if (POINT_X <  X0 && POINT_X <  X1) { return (Y0 < Y1) ? 1 : -1; }
        double xIntercept = X0 + (POINT_Y - Y0) * (X1 - X0) / (Y1 - Y0);
        if (POINT_X >= xIntercept) { return 0; }
        return (Y0 < Y1) ? 1 : -1;
    }

    public static int pointCrossingsForQuad(final double POINT_X, final double POINT_Y,
                                            final double X0, final double Y0,
                                            double xc, double yc,
                                            final double X1, final double Y1, final int LEVEL) {
        if (POINT_Y <  Y0 && POINT_Y <  yc && POINT_Y <  Y1) { return 0; }
        if (POINT_Y >= Y0 && POINT_Y >= yc && POINT_Y >= Y1) { return 0; }
        if (POINT_X >= X0 && POINT_X >= xc && POINT_X >= X1) { return 0; }
        if (POINT_X <  X0 && POINT_X <  xc && POINT_X <  X1) {
            if (POINT_Y >= Y0) {
                if (POINT_Y < Y1) { return 1; }
            } else {
                if (POINT_Y >= Y1) { return -1; }
            }
            return 0;
        }
        if (LEVEL > 52) return pointCrossingsForLine(POINT_X, POINT_Y, X0, Y0, X1, Y1);
        double x0c = (X0 + xc) / 2;
        double y0c = (Y0 + yc) / 2;
        double xc1 = (xc + X1) / 2;
        double yc1 = (yc + Y1) / 2;
        xc = (x0c + xc1) / 2;
        yc = (y0c + yc1) / 2;
        if (Double.isNaN(xc) || Double.isNaN(yc)) { return 0; }
        return (pointCrossingsForQuad(POINT_X, POINT_Y, X0, Y0, x0c, y0c, xc, yc, LEVEL+1) +
                pointCrossingsForQuad(POINT_X, POINT_Y, xc, yc, xc1, yc1, X1, Y1, LEVEL+1));
    }

    public static int pointCrossingsForCubic(final double POINT_X, final double POINT_Y,
                                             final double X0, final double Y0,
                                             double xc0, double yc0,
                                             double xc1, double yc1,
                                             final double X1, final double Y1, final int LEVEL)
    {
        if (POINT_Y <  Y0 && POINT_Y <  yc0 && POINT_Y <  yc1 && POINT_Y <  Y1) { return 0; }
        if (POINT_Y >= Y0 && POINT_Y >= yc0 && POINT_Y >= yc1 && POINT_Y >= Y1) { return 0; }
        if (POINT_X >= X0 && POINT_X >= xc0 && POINT_X >= xc1 && POINT_X >= X1) { return 0; }
        if (POINT_X <  X0 && POINT_X <  xc0 && POINT_X <  xc1 && POINT_X <  X1) {
            if (POINT_Y >= Y0) {
                if (POINT_Y < Y1) { return 1; }
            } else {
                if (POINT_Y >= Y1) { return -1; }
            }
            return 0;
        }
        if (LEVEL > 52) { return pointCrossingsForLine(POINT_X, POINT_Y, X0, Y0, X1, Y1); }
        double xmid = (xc0 + xc1) / 2;
        double ymid = (yc0 + yc1) / 2;
        xc0 = (X0 + xc0) / 2;
        yc0 = (Y0 + yc0) / 2;
        xc1 = (xc1 + X1) / 2;
        yc1 = (yc1 + Y1) / 2;
        double xc0m = (xc0 + xmid) / 2;
        double yc0m = (yc0 + ymid) / 2;
        double xmc1 = (xmid + xc1) / 2;
        double ymc1 = (ymid + yc1) / 2;
        xmid = (xc0m + xmc1) / 2;
        ymid = (yc0m + ymc1) / 2;
        if (Double.isNaN(xmid) || Double.isNaN(ymid)) { return 0; }
        return (pointCrossingsForCubic(POINT_X, POINT_Y, X0, Y0, xc0, yc0, xc0m, yc0m, xmid, ymid, LEVEL+1) +
                pointCrossingsForCubic(POINT_X, POINT_Y, xmid, ymid, xmc1, ymc1, xc1, yc1, X1, Y1, LEVEL+1));
    }


    public static int rectCrossingsForPath(final PathIterator PATH_ITERATOR,
                                           final double RECT_X_MIN, final double RECT_Y_MIN,
                                           final double RECT_X_MAX, final double RECT_Y_MAX) {
        if (RECT_X_MAX <= RECT_X_MIN || RECT_Y_MAX <= RECT_Y_MIN) { return 0; }
        if (PATH_ITERATOR.isDone()) { return 0; }
        double coords[] = new double[6];
        if (PATH_ITERATOR.currentSegment(coords) != PathIterator.MOVE_TO) {
            throw new IllegalPathStateException("missing initial moveto in path definition");
        }
        PATH_ITERATOR.next();
        double curx, cury, movx, movy, endx, endy;
        curx = movx = coords[0];
        cury = movy = coords[1];
        int crossings = 0;
        while (crossings != RECT_INTERSECTS && !PATH_ITERATOR.isDone()) {
            switch (PATH_ITERATOR.currentSegment(coords)) {
                case PathIterator.MOVE_TO:
                    if (curx != movx || cury != movy) {
                        crossings = rectCrossingsForLine(crossings,
                                                         RECT_X_MIN, RECT_Y_MIN,
                                                         RECT_X_MAX, RECT_Y_MAX,
                                                         curx, cury,
                                                         movx, movy);
                    }
                    movx = curx = coords[0];
                    movy = cury = coords[1];
                    break;
                case PathIterator.LINE_TO:
                    endx = coords[0];
                    endy = coords[1];
                    crossings = rectCrossingsForLine(crossings,
                                                     RECT_X_MIN, RECT_Y_MIN,
                                                     RECT_X_MAX, RECT_Y_MAX,
                                                     curx, cury,
                                                     endx, endy);
                    curx = endx;
                    cury = endy;
                    break;
                case PathIterator.QUAD_TO:
                    endx = coords[2];
                    endy = coords[3];
                    crossings = rectCrossingsForQuad(crossings,
                                                     RECT_X_MIN, RECT_Y_MIN,
                                                     RECT_X_MAX, RECT_Y_MAX,
                                                     curx, cury,
                                                     coords[0], coords[1],
                                                     endx, endy, 0);
                    curx = endx;
                    cury = endy;
                    break;
                case PathIterator.BEZIER_TO:
                    endx = coords[4];
                    endy = coords[5];
                    crossings = rectCrossingsForCubic(crossings,
                                                      RECT_X_MIN, RECT_Y_MIN,
                                                      RECT_X_MAX, RECT_Y_MAX,
                                                      curx, cury,
                                                      coords[0], coords[1],
                                                      coords[2], coords[3],
                                                      endx, endy, 0);
                    curx = endx;
                    cury = endy;
                    break;
                case PathIterator.CLOSE:
                    if (curx != movx || cury != movy) {
                        crossings = rectCrossingsForLine(crossings,
                                                         RECT_X_MIN, RECT_Y_MIN,
                                                         RECT_X_MAX, RECT_Y_MAX,
                                                         curx, cury,
                                                         movx, movy);
                    }
                    curx = movx;
                    cury = movy;
                    // Count should always be a multiple of 2 here.
                    // assert((crossings & 1) != 0);
                    break;
            }
            PATH_ITERATOR.next();
        }
        if (crossings != RECT_INTERSECTS && (curx != movx || cury != movy)) {
            crossings = rectCrossingsForLine(crossings,
                                             RECT_X_MIN, RECT_Y_MIN,
                                             RECT_X_MAX, RECT_Y_MAX,
                                             curx, cury,
                                             movx, movy);
        }
        // Count should always be a multiple of 2 here.
        // assert((crossings & 1) != 0);
        return crossings;
    }


    public static int rectCrossingsForLine(int crossings,
                                           final double RECT_X_MIN, final double RECT_Y_MIN,
                                           final double RECT_X_MAX, final double RECT_Y_MAX,
                                           final double X0, final double Y0,
                                           final double X1, final double Y1) {
        if (Y0 >= RECT_Y_MAX && Y1 >= RECT_Y_MAX) { return crossings; }
        if (Y0 <= RECT_Y_MIN && Y1 <= RECT_Y_MIN) { return crossings; }
        if (X0 <= RECT_X_MIN && X1 <= RECT_X_MIN) { return crossings; }
        if (X0 >= RECT_X_MAX && X1 >= RECT_X_MAX) {
            if (Y0 < Y1) {
                if (Y0 <= RECT_Y_MIN) { crossings++; }
                if (Y1 >= RECT_Y_MAX) { crossings++; }
            } else if (Y1 < Y0) {
                if (Y1 <= RECT_Y_MIN) { crossings--; }
                if (Y0 >= RECT_Y_MAX) { crossings--; }
            }
            return crossings;
        }
        // Remaining case:
        // Both x and y ranges overlap by a non-empty amount
        // First do trivial INTERSECTS rejection of the cases
        // where one of the endpoints is inside the rectangle.
        if ((X0 > RECT_X_MIN && X0 < RECT_X_MAX && Y0 > RECT_Y_MIN && Y0 < RECT_Y_MAX) ||
            (X1 > RECT_X_MIN && X1 < RECT_X_MAX && Y1 > RECT_Y_MIN && Y1 < RECT_Y_MAX))
        {
            return RECT_INTERSECTS;
        }
        // Otherwise calculate the y intercepts and see where
        // they fall with respect to the rectangle
        double xi0 = X0;
        if (Y0 < RECT_Y_MIN) {
            xi0 += ((RECT_Y_MIN - Y0) * (X1 - X0) / (Y1 - Y0));
        } else if (Y0 > RECT_Y_MAX) {
            xi0 += ((RECT_Y_MAX - Y0) * (X1 - X0) / (Y1 - Y0));
        }
        double xi1 = X1;
        if (Y1 < RECT_Y_MIN) {
            xi1 += ((RECT_Y_MIN - Y1) * (X0 - X1) / (Y0 - Y1));
        } else if (Y1 > RECT_Y_MAX) {
            xi1 += ((RECT_Y_MAX - Y1) * (X0 - X1) / (Y0 - Y1));
        }
        if (xi0 <= RECT_X_MIN && xi1 <= RECT_X_MIN) return crossings;
        if (xi0 >= RECT_X_MAX && xi1 >= RECT_X_MAX) {
            if (Y0 < Y1) {
                // y-increasing line segment...
                // We know that Y0 < RECT_Y_MAX and Y1 > RECT_Y_MIN
                if (Y0 <= RECT_Y_MIN) crossings++;
                if (Y1 >= RECT_Y_MAX) crossings++;
            } else if (Y1 < Y0) {
                // y-decreasing line segment...
                // We know that Y1 < RECT_Y_MAX and Y0 > RECT_Y_MIN
                if (Y1 <= RECT_Y_MIN) crossings--;
                if (Y0 >= RECT_Y_MAX) crossings--;
            }
            return crossings;
        }
        return RECT_INTERSECTS;
    }


    public static int rectCrossingsForQuad(int crossings,
                                           double rxmin, double rymin,
                                           double rxmax, double rymax,
                                           double x0, double y0,
                                           double xc, double yc,
                                           double x1, double y1,
                                           int level)
    {
        if (y0 >= rymax && yc >= rymax && y1 >= rymax) { return crossings; }
        if (y0 <= rymin && yc <= rymin && y1 <= rymin) { return crossings; }
        if (x0 <= rxmin && xc <= rxmin && x1 <= rxmin) { return crossings; }
        if (x0 >= rxmax && xc >= rxmax && x1 >= rxmax) {
            if (y0 < y1) {
                if (y0 <= rymin && y1 >  rymin) { crossings++; }
                if (y0 <  rymax && y1 >= rymax) { crossings++; }
            } else if (y1 < y0) {
                if (y1 <= rymin && y0 >  rymin) { crossings--; }
                if (y1 <  rymax && y0 >= rymax) { crossings--; }
            }
            return crossings;
        }
        if ((x0 < rxmax && x0 > rxmin && y0 < rymax && y0 > rymin) || (x1 < rxmax && x1 > rxmin && y1 < rymax && y1 > rymin)) {
            return RECT_INTERSECTS;
        }
        if (level > 52) { return rectCrossingsForLine(crossings, rxmin, rymin, rxmax, rymax, x0, y0, x1, y1); }
        double x0c = (x0 + xc) / 2;
        double y0c = (y0 + yc) / 2;
        double xc1 = (xc + x1) / 2;
        double yc1 = (yc + y1) / 2;
        xc = (x0c + xc1) / 2;
        yc = (y0c + yc1) / 2;
        if (Double.isNaN(xc) || Double.isNaN(yc)) { return 0; }
        crossings = rectCrossingsForQuad(crossings, rxmin, rymin, rxmax, rymax, x0, y0, x0c, y0c, xc, yc, level+1);
        if (crossings != RECT_INTERSECTS) {
            crossings = rectCrossingsForQuad(crossings, rxmin, rymin, rxmax, rymax, xc, yc, xc1, yc1, x1, y1, level+1);
        }
        return crossings;
    }


    public static int rectCrossingsForCubic(int crossings,
                                            double rxmin, double rymin,
                                            double rxmax, double rymax,
                                            double x0,  double y0,
                                            double xc0, double yc0,
                                            double xc1, double yc1,
                                            double x1,  double y1,
                                            int level)
    {
        if (y0 >= rymax && yc0 >= rymax && yc1 >= rymax && y1 >= rymax) {
            return crossings;
        }
        if (y0 <= rymin && yc0 <= rymin && yc1 <= rymin && y1 <= rymin) {
            return crossings;
        }
        if (x0 <= rxmin && xc0 <= rxmin && xc1 <= rxmin && x1 <= rxmin) {
            return crossings;
        }
        if (x0 >= rxmax && xc0 >= rxmax && xc1 >= rxmax && x1 >= rxmax) {
            if (y0 < y1) {
                if (y0 <= rymin && y1 >  rymin) crossings++;
                if (y0 <  rymax && y1 >= rymax) crossings++;
            } else if (y1 < y0) {
                if (y1 <= rymin && y0 >  rymin) crossings--;
                if (y1 <  rymax && y0 >= rymax) crossings--;
            }
            return crossings;
        }
        if ((x0 > rxmin && x0 < rxmax && y0 > rymin && y0 < rymax) || (x1 > rxmin && x1 < rxmax && y1 > rymin && y1 < rymax)) {
            return RECT_INTERSECTS;
        }
        if (level > 52) { return rectCrossingsForLine(crossings, rxmin, rymin, rxmax, rymax, x0, y0, x1, y1); }
        double xmid = (xc0 + xc1) / 2;
        double ymid = (yc0 + yc1) / 2;
        xc0 = (x0 + xc0) / 2;
        yc0 = (y0 + yc0) / 2;
        xc1 = (xc1 + x1) / 2;
        yc1 = (yc1 + y1) / 2;
        double xc0m = (xc0 + xmid) / 2;
        double yc0m = (yc0 + ymid) / 2;
        double xmc1 = (xmid + xc1) / 2;
        double ymc1 = (ymid + yc1) / 2;
        xmid = (xc0m + xmc1) / 2;
        ymid = (yc0m + ymc1) / 2;
        if (Double.isNaN(xmid) || Double.isNaN(ymid)) { return 0; }
        crossings = rectCrossingsForCubic(crossings, rxmin, rymin, rxmax, rymax, x0, y0, xc0, yc0, xc0m, yc0m, xmid, ymid, level+1);
        if (crossings != RECT_INTERSECTS) {
            crossings = rectCrossingsForCubic(crossings, rxmin, rymin, rxmax, rymax, xmid, ymid, xmc1, ymc1, xc1, yc1, x1, y1, level+1);
        }
        return crossings;
    }

    static boolean intersectsLine(double rx1, double ry1, double rwidth, double rheight, double x1, double y1, double x2, double y2) {
        int out1, out2;
        if ((out2 = outcode(rx1, ry1, rwidth, rheight, x2, y2)) == 0) { return true; }
        while ((out1 = outcode(rx1, ry1, rwidth, rheight, x1, y1)) != 0) {
            if ((out1 & out2) != 0) { return false; }
            if ((out1 & (OUT_LEFT | OUT_RIGHT)) != 0) {
                if ((out1 & OUT_RIGHT) != 0) { rx1 += rwidth; }
                y1 = y1 + (rx1 - x1) * (y2 - y1) / (x2 - x1);
                x1 = rx1;
            } else {
                if ((out1 & OUT_BOTTOM) != 0) { ry1 += rheight; }
                x1 = x1 + (ry1 - y1) * (x2 - x1) / (y2 - y1);
                y1 = ry1;
            }
        }
        return true;
    }

    static int outcode(final double RECT_X, final double RECT_Y, final double RECT_WIDTH, final double RECT_HEIGHT, final double X, final double Y) {
        int out = 0;
        if (RECT_WIDTH <= 0) {
            out |= OUT_LEFT | OUT_RIGHT;
        } else if (X < RECT_X) {
            out |= OUT_LEFT;
        } else if (X > RECT_X + RECT_WIDTH) {
            out |= OUT_RIGHT;
        }
        if (RECT_HEIGHT <= 0) {
            out |= OUT_TOP | OUT_BOTTOM;
        } else if (Y < RECT_Y) {
            out |= OUT_TOP;
        } else if (Y > RECT_Y + RECT_HEIGHT) {
            out |= OUT_BOTTOM;
        }
        return out;
    }

    public static void accumulate(double bbox[], Shape s, BaseTransform tx) {
        PathIterator pi = s.getPathIterator(tx);
        double coords[] = new double[6];
        double mx = 0.0, my = 0.0, x0 = 0.0, y0 = 0.0, x1, y1;
        while (!pi.isDone()) {
            switch (pi.currentSegment(coords)) {
                case PathIterator.MOVE_TO:
                    mx = coords[0];
                    my = coords[1];
                case PathIterator.LINE_TO:
                    x0 = coords[0];
                    y0 = coords[1];
                    if (bbox[0] > x0) bbox[0] = x0;
                    if (bbox[1] > y0) bbox[1] = y0;
                    if (bbox[2] < x0) bbox[2] = x0;
                    if (bbox[3] < y0) bbox[3] = y0;
                    break;
                case PathIterator.QUAD_TO:
                    x1 = coords[2];
                    y1 = coords[3];
                    if (bbox[0] > x1) bbox[0] = x1;
                    if (bbox[1] > y1) bbox[1] = y1;
                    if (bbox[2] < x1) bbox[2] = x1;
                    if (bbox[3] < y1) bbox[3] = y1;
                    if (bbox[0] > coords[0] || bbox[2] < coords[0]) {
                        accumulateQuad(bbox, 0, x0, coords[0], x1);
                    }
                    if (bbox[1] > coords[1] || bbox[3] < coords[1]) {
                        accumulateQuad(bbox, 1, y0, coords[1], y1);
                    }
                    x0 = x1;
                    y0 = y1;
                    break;
                case PathIterator.BEZIER_TO:
                    x1 = coords[4];
                    y1 = coords[5];
                    if (bbox[0] > x1) bbox[0] = x1;
                    if (bbox[1] > y1) bbox[1] = y1;
                    if (bbox[2] < x1) bbox[2] = x1;
                    if (bbox[3] < y1) bbox[3] = y1;
                    if (bbox[0] > coords[0] || bbox[2] < coords[0] || bbox[0] > coords[2] || bbox[2] < coords[2]) {
                        accumulateCubic(bbox, 0, x0, coords[0], coords[2], x1);
                    }
                    if (bbox[1] > coords[1] || bbox[3] < coords[1] || bbox[1] > coords[3] || bbox[3] < coords[3]) {
                        accumulateCubic(bbox, 1, y0, coords[1], coords[3], y1);
                    }
                    x0 = x1;
                    y0 = y1;
                    break;
                case PathIterator.CLOSE:
                    x0 = mx;
                    y0 = my;
                    break;
            }
            pi.next();
        }
    }

    public static void accumulateQuad(double bbox[], int off, double v0, double vc, double v1) {
        double num = v0 - vc;
        double den = v1 - vc + num;
        if (den != 0.0) {
            double t = num / den;
            if (t > 0 && t < 1) {
                double u = 1.0 - t;
                double v = v0 * u * u + 2 * vc * t * u + v1 * t * t;
                if (bbox[off] > v)     { bbox[off] = v;   }
                if (bbox[off + 2] < v) { bbox[off+2] = v; }
            }
        }
    }

    public static void accumulateCubic(double bbox[], int off, double v0, double vc0, double vc1, double v1) {
        double c = vc0 - v0;
        double b = 2.0 * ((vc1 - vc0) - c);
        double a = (v1 - vc1) - b - c;
        if (Double.compare(a, 0) == 0) {
            if (Double.compare(b, 0) == 0) { return; }
            accumulateCubic(bbox, off, -c/b, v0, vc0, vc1, v1);
        } else {
            double d = b * b - 4.0 * a * c;
            if (d < 0.0) { return; }
            d = Math.sqrt(d);
            if (b < 0.0) { d = -d; }
            double q = (b + d) / -2.0;
            accumulateCubic(bbox, off, q/a, v0, vc0, vc1, v1);
            if (q != 0.0) { accumulateCubic(bbox, off, c/q, v0, vc0, vc1, v1); }
        }
    }

    public static void accumulateCubic(double bbox[], int off, double t, double v0, double vc0, double vc1, double v1) {
        if (t > 0 && t < 1) {
            double u = 1.0 - t;
            double v = v0 * u * u * u + 3 * vc0 * t * u * u + 3 * vc1 * t * t * u + v1 * t * t * t;
            if (bbox[off] > v)     { bbox[off] = v;   }
            if (bbox[off + 2] < v) { bbox[off+2] = v; }
        }
    }

    // Method to draw the shape on the GraphicsContext
    //public abstract void draw(final GraphicsContext CTX);
}