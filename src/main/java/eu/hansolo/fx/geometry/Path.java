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

import eu.hansolo.fx.geometry.transform.Affine;
import eu.hansolo.fx.geometry.transform.BaseTransform;
import eu.hansolo.fx.geometry.tools.IllegalPathStateException;
import eu.hansolo.fx.geometry.tools.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;


public class Path extends Shape {

    static final int curvecoords[] = { 2, 2, 4, 6, 0 };

    public enum CornerPrefix {
        CORNER_ONLY,
        MOVE_THEN_CORNER,
        LINE_THEN_CORNER
    }
    public enum WindingRule { WIND_EVEN_ODD, WIND_NON_ZERO }

    private static final byte SEG_MOVETO  = (byte) PathIterator.MOVE_TO;
    private static final byte SEG_LINETO  = (byte) PathIterator.LINE_TO;
    private static final byte SEG_QUADTO  = (byte) PathIterator.QUAD_TO;
    private static final byte SEG_CUBICTO = (byte) PathIterator.BEZIER_TO;
    private static final byte SEG_CLOSE   = (byte) PathIterator.CLOSE;

    static final int INIT_SIZE  = 20;
    static final int EXPAND_MAX = 500;

    byte[]      pointTypes;
    int         numTypes;
    int         numCoords;
    WindingRule windingRule;

    double      doubleCoords[];
    double      moveX, moveY;
    double      prevX, prevY;
    double      currentX, currentY;

    private Paint fill   = Color.BLACK;
    private Paint stroke = Color.BLACK;

    public Path() {
        this(WindingRule.WIND_NON_ZERO, INIT_SIZE);
    }
    public Path(final WindingRule RULE) {
        this(RULE, INIT_SIZE);
    }
    public Path(final WindingRule RULE, final int INITIAL_CAPACITY) {
        setWindingRule(RULE);
        this.pointTypes = new byte[INITIAL_CAPACITY];
        doubleCoords = new double[INITIAL_CAPACITY * 2];
    }
    public Path(final Shape SHAPE) {
        this(SHAPE, null);
    }
    public Path(final Shape SHAPE, final BaseTransform TRANSFORM) {
        if (SHAPE instanceof Path) {
            Path p2d = (Path) SHAPE;
            setWindingRule(p2d.windingRule);
            this.numTypes = p2d.numTypes;
            //this.pointTypes = Arrays.copyOf(p2d.pointTypes,
            //                                p2d.pointTypes.length); // jk16 dependency
            this.pointTypes = copyOf(p2d.pointTypes,
                                     p2d.pointTypes.length);
            this.numCoords = p2d.numCoords;
            if (TRANSFORM == null || TRANSFORM.isIdentity()) {
                this.doubleCoords = copyOf(p2d.doubleCoords, numCoords);
                this.moveX = p2d.moveX;
                this.moveY = p2d.moveY;
                this.prevX = p2d.prevX;
                this.prevY = p2d.prevY;
                this.currentX = p2d.currentX;
                this.currentY = p2d.currentY;
            } else {
                this.doubleCoords = new double[numCoords + 6];
                TRANSFORM.transform(p2d.doubleCoords, 0, this.doubleCoords, 0, numCoords / 2);
                doubleCoords[numCoords + 0] = moveX;
                doubleCoords[numCoords + 1] = moveY;
                doubleCoords[numCoords + 2] = prevX;
                doubleCoords[numCoords + 3] = prevY;
                doubleCoords[numCoords + 4] = currentX;
                doubleCoords[numCoords + 5] = currentY;
                TRANSFORM.transform(this.doubleCoords, numCoords, this.doubleCoords, numCoords, 3);
                moveX = doubleCoords[numCoords + 0];
                moveY = doubleCoords[numCoords + 1];
                prevX = doubleCoords[numCoords + 2];
                prevY = doubleCoords[numCoords + 3];
                currentX = doubleCoords[numCoords + 4];
                currentY = doubleCoords[numCoords + 5];
            }
        } else {
            PathIterator pi = SHAPE.getPathIterator(TRANSFORM);
            setWindingRule(pi.getWindingRule());
            this.pointTypes = new byte[INIT_SIZE];
            this.doubleCoords = new double[INIT_SIZE * 2];
            append(pi, false);
        }
    }
    public Path(final WindingRule RULE, final byte[] POINT_TYPES, final int NUM_TYPES, final double[] POINT_COORDS, final int NUM_COORDS) {
        windingRule  = RULE;
        pointTypes   = POINT_TYPES;
        numTypes     = NUM_TYPES;
        doubleCoords = POINT_COORDS;
        numCoords    = NUM_COORDS;
    }

    Point getPoint(final int INDEX) { return new Point(doubleCoords[INDEX], doubleCoords[INDEX+1]); }

    void needRoom(final boolean NEED_MOVE, final int NEW_COORDS) {
        if (NEED_MOVE && numTypes == 0) { throw new IllegalPathStateException("missing initial moveto in path definition"); }
        int size = pointTypes.length;
        if (size == 0) {
            pointTypes = new byte[2];
        } else if (numTypes >= size) {
            int grow = size;
            if (grow > EXPAND_MAX) {
                grow = EXPAND_MAX;
            }
            pointTypes = copyOf(pointTypes, size+grow);
        }
        size = doubleCoords.length;
        if (numCoords + NEW_COORDS > size) {
            int grow = size;
            if (grow > EXPAND_MAX * 2) {
                grow = EXPAND_MAX * 2;
            }
            if (grow < NEW_COORDS) {
                grow = NEW_COORDS;
            }
            doubleCoords = copyOf(doubleCoords, size+grow);
        }
    }

    public final void moveTo(final Point P) { moveTo(P.getX(), P.getY()); }
    public final void moveTo(final double X, final double Y) {
        if (numTypes > 0 && pointTypes[numTypes - 1] == SEG_MOVETO) {
            doubleCoords[numCoords-2] = moveX = prevX = currentX = X;
            doubleCoords[numCoords-1] = moveY = prevY = currentY = Y;
        } else {
            needRoom(false, 2);
            pointTypes[numTypes++] = SEG_MOVETO;
            doubleCoords[numCoords++] = moveX = prevX = currentX = X;
            doubleCoords[numCoords++] = moveY = prevY = currentY = Y;
        }
    }

    public final void moveToRel(final Point P) {
        moveToRel(P.getX(), P.getY());
    }
    public final void moveToRel(final double X_REL, final double Y_REL) {
        if (numTypes > 0 && pointTypes[numTypes - 1] == SEG_MOVETO) {
            doubleCoords[numCoords-2] = moveX = prevX = (currentX += X_REL);
            doubleCoords[numCoords-1] = moveY = prevY = (currentY += Y_REL);
        } else {
            needRoom(true, 2);
            pointTypes[numTypes++] = SEG_MOVETO;
            doubleCoords[numCoords++] = moveX = prevX = (currentX += X_REL);
            doubleCoords[numCoords++] = moveY = prevY = (currentY += Y_REL);
        }
    }

    public final void lineTo(final Point P) {
        lineTo(P.getX(), P.getY());
    }
    public final void lineTo(final double X, final double Y) {
        needRoom(true, 2);
        pointTypes[numTypes++] = SEG_LINETO;
        doubleCoords[numCoords++] = prevX = currentX = X;
        doubleCoords[numCoords++] = prevY = currentY = Y;
    }

    public final void lineToRel(final Point P) {
        lineToRel(P.getX(), P.getY());
    }
    public final void lineToRel(final double X_REL, final double Y_REL) {
        needRoom(true, 2);
        pointTypes[numTypes++] = SEG_LINETO;
        doubleCoords[numCoords++] = prevX = (currentX += X_REL);
        doubleCoords[numCoords++] = prevY = (currentY += Y_REL);
    }

    public final void quadraticCurveTo(final Point P1, final Point P2) {
        quadraticCurveTo(P1.getX(), P1.getY(), P2.getX(), P2.getY());
    }
    public final void quadraticCurveTo(final double X1, final double Y1, final double X2, final double Y2) {
        needRoom(true, 4);
        pointTypes[numTypes++]    = SEG_QUADTO;
        doubleCoords[numCoords++] = prevX = X1;
        doubleCoords[numCoords++] = prevY = Y1;
        doubleCoords[numCoords++] = currentX = X2;
        doubleCoords[numCoords++] = currentY = Y2;
    }

    public final void quadraticCurveToRel(final Point P1, final Point P2) {
        quadraticCurveToRel(P1.getX(), P1.getY(), P2.getX(), P2.getY());
    }
    public final void quadraticCurveToRel(final double X1_REL, final double Y1_REL, final double X2_REL, final double Y2_REL) {
        needRoom(true, 4);
        pointTypes[numTypes++]    = SEG_QUADTO;
        doubleCoords[numCoords++] = prevX = currentX + X1_REL;
        doubleCoords[numCoords++] = prevY = currentY + Y1_REL;
        doubleCoords[numCoords++] = (currentX += X2_REL);
        doubleCoords[numCoords++] = (currentY += Y2_REL);
    }

    public final void quadraticCurveToSmooth(final Point P) {
        quadraticCurveToSmooth(P.getX(), P.getY());
    }
    public final void quadraticCurveToSmooth(final double X2, final double Y2) {
        needRoom(true, 4);
        pointTypes[numTypes++]    = SEG_QUADTO;
        doubleCoords[numCoords++] = prevX = (currentX * 2.0 - prevX);
        doubleCoords[numCoords++] = prevY = (currentY * 2.0 - prevY);
        doubleCoords[numCoords++] = currentX = X2;
        doubleCoords[numCoords++] = currentY = Y2;
    }

    public final void quadraticCurveToSmoothRel(final Point P2) {
        quadraticCurveToSmoothRel(P2.getX(), P2.getY());
    }
    public final void quadraticCurveToSmoothRel(final double X2_REL, final double Y2_REL) {
        needRoom(true, 4);
        pointTypes[numTypes++]    = SEG_QUADTO;
        doubleCoords[numCoords++] = prevX = (currentX * 2.0 - prevX);
        doubleCoords[numCoords++] = prevY = (currentY * 2.0 - prevY);
        doubleCoords[numCoords++] = (currentX += X2_REL);
        doubleCoords[numCoords++] = (currentY += Y2_REL);
    }

    public final void bezierCurveTo(final Point P1, final Point P2, final Point P_END) {
        bezierCurveTo(P1.getX(), P1.getY(), P2.getX(), P2.getY(), P_END.getX(), P_END.getY());
    }
    public final void bezierCurveTo(final double X1, final double Y1, final double X2, final double Y2, final double X_END, final double Y_END) {
        needRoom(true, 6);
        pointTypes[numTypes++]    = SEG_CUBICTO;
        doubleCoords[numCoords++] = X1;
        doubleCoords[numCoords++] = Y1;
        doubleCoords[numCoords++] = prevX = X2;
        doubleCoords[numCoords++] = prevY = Y2;
        doubleCoords[numCoords++] = currentX = X_END;
        doubleCoords[numCoords++] = currentY = Y_END;
    }

    public final void bezierCurveToRel(final Point P1, final Point P2, final Point P_END) {
        bezierCurveToRel(P1.getX(), P1.getY(), P2.getX(), P2.getY(), P_END.getX(), P_END.getY());
    }
    public final void bezierCurveToRel(final double X1_REL, final double Y1_REL, final double X2_REL, final double Y2_REL, final double X_END_REL, final double Y_END_REL) {
        needRoom(true, 6);
        pointTypes[numTypes++]    = SEG_CUBICTO;
        doubleCoords[numCoords++] = currentX + X1_REL;
        doubleCoords[numCoords++] = currentY + Y1_REL;
        doubleCoords[numCoords++] = prevX = currentX + X2_REL;
        doubleCoords[numCoords++] = prevY = currentY + Y2_REL;
        doubleCoords[numCoords++] = (currentX += X_END_REL);
        doubleCoords[numCoords++] = (currentY += Y_END_REL);
    }

    public final void bezierCurveToSmooth(final Point P2, final Point P_END) {
        bezierCurveToSmooth(P2.getX(), P2.getY(), P_END.getX(), P_END.getY());
    }
    public final void bezierCurveToSmooth(final double X2, final double Y2, final double X_END, final double Y_END) {
        needRoom(true, 6);
        pointTypes[numTypes++]    = SEG_CUBICTO;
        doubleCoords[numCoords++] = currentX * 2.0 - prevX;
        doubleCoords[numCoords++] = currentY * 2.0 - prevY;
        doubleCoords[numCoords++] = prevX = X2;
        doubleCoords[numCoords++] = prevY = Y2;
        doubleCoords[numCoords++] = currentX = X_END;
        doubleCoords[numCoords++] = currentY = Y_END;
    }

    public final void bezierCurveToSmoothRel(final Point P2, final Point P_END) {
        bezierCurveToSmoothRel(P2.getX(), P2.getY(), P_END.getX(), P_END.getY());
    }
    public final void bezierCurveToSmoothRel(final double X2_REL, final double Y2_REL, final double X_END_REL, final double Y_END_REL) {
        needRoom(true, 6);
        pointTypes[numTypes++]    = SEG_CUBICTO;
        doubleCoords[numCoords++] = currentX * 2.0 - prevX;
        doubleCoords[numCoords++] = currentY * 2.0 - prevY;
        doubleCoords[numCoords++] = prevX = currentX + X2_REL;
        doubleCoords[numCoords++] = prevY = currentY + Y2_REL;
        doubleCoords[numCoords++] = (currentX += X_END_REL);
        doubleCoords[numCoords++] = (currentY += Y_END_REL);
    }

    public final void ovalQuadrantTo(final double CX, final double CY, final double EX, final double EY, final double T_FROM, final double T_TO) {
        if (numTypes < 1) { throw new IllegalPathStateException("missing initial moveto in path definition"); }
        appendOvalQuadrant(currentX, currentY, CX, CY, EX, EY, T_FROM, T_TO, CornerPrefix.CORNER_ONLY);
    }

    public void arcTo(double radiusx, double radiusy, double xAxisRotation, boolean largeArcFlag, boolean sweepFlag, double x, double y) {
        if (numTypes < 1) { throw new IllegalPathStateException("missing initial moveto in path definition"); }

        double rx = Math.abs(radiusx);
        double ry = Math.abs(radiusy);
        if (rx == 0 || ry == 0) {
            lineTo(x, y);
            return;
        }
        double x1 = currentX;
        double y1 = currentY;
        double x2 = x;
        double y2 = y;
        if (x1 == x2 && y1 == y2) { return; }
        double cosPhi;
        double sinPhi;
        if (xAxisRotation == 0.0) {
            cosPhi = 1.0;
            sinPhi = 0.0;
        } else {
            cosPhi = Math.cos(xAxisRotation);
            sinPhi = Math.sin(xAxisRotation);
        }
        double mx    = (x1 + x2) / 2.0;
        double my    = (y1 + y2) / 2.0;
        double relx1 = x1 - mx;
        double rely1 = y1 - my;
        double x1p   = (cosPhi * relx1 + sinPhi * rely1) / rx;
        double y1p   = (cosPhi * rely1 - sinPhi * relx1) / ry;

        double lenpsq = x1p * x1p + y1p * y1p;
        if (lenpsq >= 1.0) {
            double xqpr = y1p * rx;
            double yqpr = x1p * ry;
            if (sweepFlag) { xqpr = -xqpr; } else { yqpr = -yqpr; }
            double relxq = cosPhi * xqpr - sinPhi * yqpr;
            double relyq = cosPhi * yqpr + sinPhi * xqpr;
            double xq = mx + relxq;
            double yq = my + relyq;
            double xc = x1 + relxq;
            double yc = y1 + relyq;
            appendOvalQuadrant(x1, y1, xc, yc, xq, yq, 0.0, 1.0, CornerPrefix.CORNER_ONLY);
            xc = x2 + relxq;
            yc = y2 + relyq;
            appendOvalQuadrant(xq, yq, xc, yc, x2, y2, 0.0, 1.0, CornerPrefix.CORNER_ONLY);
            return;
        }

        double scalef = Math.sqrt((1.0 - lenpsq) / lenpsq);
        double cxp    = scalef * y1p;
        double cyp    = scalef * x1p;
        if (largeArcFlag == sweepFlag) { cxp = -cxp; } else { cyp = -cyp; }
        double  ux       = x1p - cxp;
        double  uy       = y1p - cyp;
        double  vx       = -(x1p + cxp);
        double  vy       = -(y1p + cyp);
        boolean done     = false;
        double  quadlen  = 1.0;
        boolean wasclose = false;

        mx += (cosPhi * cxp * rx - sinPhi * cyp * ry);
        my += (cosPhi * cyp * ry + sinPhi * cxp * rx);

        do {
            double xqp = uy;
            double yqp = ux;
            if (sweepFlag) { xqp = -xqp; } else { yqp = -yqp; }
            if (xqp * vx + yqp * vy > 0) {
                double dot = ux * vx + uy * vy;
                if (dot >= 0) {
                    quadlen = (Math.acos(dot) / (Math.PI / 2.0));
                    done    = true;
                }
                wasclose = true;
            } else if (wasclose) {
                break;
            }
            double relxq = (cosPhi * xqp * rx - sinPhi * yqp * ry);
            double relyq = (cosPhi * yqp * ry + sinPhi * xqp * rx);
            double xq    = mx + relxq;
            double yq    = my + relyq;
            double xc    = x1 + relxq;
            double yc    = y1 + relyq;
            appendOvalQuadrant(x1, y1, xc, yc, xq, yq, 0.0, quadlen, CornerPrefix.CORNER_ONLY);
            x1 = xq;
            y1 = yq;
            ux = xqp;
            uy = yqp;
        } while (!done);
    }
    public void arcToRel(double radiusx, double radiusy, double xAxisRotation, boolean largeArcFlag, boolean sweepFlag, double relx, double rely) {
        arcTo(radiusx, radiusy, xAxisRotation, largeArcFlag, sweepFlag, currentX + relx, currentY + rely);
    }

    int piontCrossings(final Point P) {
        return pointCrossings(P.getX(), P.getY());
    }
    int pointCrossings(final double POINT_X, final double POINT_Y) {
        double movx, movy, curx, cury, endx, endy;
        double coords[] = doubleCoords;
        curx = movx = coords[0];
        cury = movy = coords[1];
        int crossings = 0;
        int ci        = 2;
        for (int i = 1; i < numTypes; i++) {
            switch (pointTypes[i]) {
                case PathIterator.MOVE_TO:
                    if (cury != movy) {
                        crossings += Shape.pointCrossingsForLine(POINT_X, POINT_Y,
                                                                 curx, cury,
                                                                 movx, movy);
                    }
                    movx = curx = coords[ci++];
                    movy = cury = coords[ci++];
                    break;
                case PathIterator.LINE_TO:
                    crossings += Shape.pointCrossingsForLine(POINT_X, POINT_Y,
                                                             curx, cury,
                                                             endx = coords[ci++],
                                                             endy = coords[ci++]);
                    curx = endx;
                    cury = endy;
                    break;
                case PathIterator.QUAD_TO:
                    crossings += Shape.pointCrossingsForQuad(POINT_X, POINT_Y,
                                                             curx, cury,
                                                             coords[ci++],
                                                             coords[ci++],
                                                             endx = coords[ci++],
                                                             endy = coords[ci++],
                                                             0);
                    curx = endx;
                    cury = endy;
                    break;
                case PathIterator.BEZIER_TO:
                    crossings += Shape.pointCrossingsForCubic(POINT_X, POINT_Y,
                                                              curx, cury,
                                                              coords[ci++],
                                                              coords[ci++],
                                                              coords[ci++],
                                                              coords[ci++],
                                                              endx = coords[ci++],
                                                              endy = coords[ci++],
                                                              0);
                    curx = endx;
                    cury = endy;
                    break;
                case PathIterator.CLOSE:
                    if (cury != movy) {
                        crossings += Shape.pointCrossingsForLine(POINT_X, POINT_Y,
                                                                 curx, cury,
                                                                 movx, movy);
                    }
                    curx = movx;
                    cury = movy;
                    break;
            }
        }
        if (cury != movy) {
            crossings += Shape.pointCrossingsForLine(POINT_X, POINT_Y,
                                                     curx, cury,
                                                     movx, movy);
        }
        return crossings;
    }

    int rectCrossings(final double RX_MIN, final double RY_MIN, final double RX_MAX, final double RY_MAX) {
        double coords[] = doubleCoords;
        double curx, cury, movx, movy, endx, endy;
        curx = movx = coords[0];
        cury = movy = coords[1];
        int crossings = 0;
        int ci        = 2;
        for (int i = 1; crossings != Shape.RECT_INTERSECTS && i < numTypes; i++) {
            switch (pointTypes[i]) {
                case PathIterator.MOVE_TO:
                    if (curx != movx || cury != movy) {
                        crossings = Shape.rectCrossingsForLine(crossings,
                                                               RX_MIN, RY_MIN,
                                                               RX_MAX, RY_MAX,
                                                               curx, cury,
                                                               movx, movy);
                    }
                    movx = curx = coords[ci++];
                    movy = cury = coords[ci++];
                    break;
                case PathIterator.LINE_TO:
                    crossings = Shape.rectCrossingsForLine(crossings,
                                                           RX_MIN, RY_MIN,
                                                           RX_MAX, RY_MAX,
                                                           curx, cury,
                                                           endx = coords[ci++],
                                                           endy = coords[ci++]);
                    curx = endx;
                    cury = endy;
                    break;
                case PathIterator.QUAD_TO:
                    crossings = Shape.rectCrossingsForQuad(crossings,
                                                           RX_MIN, RY_MIN,
                                                           RX_MAX, RY_MAX,
                                                           curx, cury,
                                                           coords[ci++],
                                                           coords[ci++],
                                                           endx = coords[ci++],
                                                           endy = coords[ci++],
                                                           0);
                    curx = endx;
                    cury = endy;
                    break;
                case PathIterator.BEZIER_TO:
                    crossings = Shape.rectCrossingsForCubic(crossings,
                                                            RX_MIN, RY_MIN,
                                                            RX_MAX, RY_MAX,
                                                            curx, cury,
                                                            coords[ci++],
                                                            coords[ci++],
                                                            coords[ci++],
                                                            coords[ci++],
                                                            endx = coords[ci++],
                                                            endy = coords[ci++],
                                                            0);
                    curx = endx;
                    cury = endy;
                    break;
                case PathIterator.CLOSE:
                    if (curx != movx || cury != movy) {
                        crossings = Shape.rectCrossingsForLine(crossings,
                                                               RX_MIN, RY_MIN,
                                                               RX_MAX, RY_MAX,
                                                               curx, cury,
                                                               movx, movy);
                    }
                    curx = movx;
                    cury = movy;
                    break;
            }
        }
        if (crossings != Shape.RECT_INTERSECTS && (curx != movx || cury != movy)) {
            crossings = Shape.rectCrossingsForLine(crossings,
                                                   RX_MIN, RY_MIN,
                                                   RX_MAX, RY_MAX,
                                                   curx, cury,
                                                   movx, movy);
        }
        return crossings;
    }

    public final void transform(final BaseTransform TRANSFORM) {
        if (numCoords == 0) return;
        needRoom(false, 6);
        doubleCoords[numCoords + 0] = moveX;
        doubleCoords[numCoords + 1] = moveY;
        doubleCoords[numCoords + 2] = prevX;
        doubleCoords[numCoords + 3] = prevY;
        doubleCoords[numCoords + 4] = currentX;
        doubleCoords[numCoords + 5] = currentY;
        TRANSFORM.transform(doubleCoords, 0, doubleCoords, 0, numCoords / 2 + 3);
        moveX = doubleCoords[numCoords + 0];
        moveY = doubleCoords[numCoords + 1];
        prevX = doubleCoords[numCoords + 2];
        prevY = doubleCoords[numCoords + 3];
        currentX = doubleCoords[numCoords + 4];
        currentY = doubleCoords[numCoords + 5];
    }

    public final RectBounds getBounds() {
        double x1, y1, x2, y2;
        int i = numCoords;
        if (i > 0) {
            y1 = y2 = doubleCoords[--i];
            x1 = x2 = doubleCoords[--i];
            while (i > 0) {
                double y = doubleCoords[--i];
                double x = doubleCoords[--i];
                if (x < x1) x1 = x;
                if (y < y1) y1 = y;
                if (x > x2) x2 = x;
                if (y > y2) y2 = y;
            }
        } else {
            x1 = y1 = x2 = y2 = 0.0;
        }
        return new RectBounds(x1, y1, x2, y2);
    }

    public final int getNumCommands() { return numTypes; }

    public final byte[] getCommandsNoClone() { return pointTypes; }

    public final double[] getDoubleCoordsNoClone() { return doubleCoords; }

    public PathIterator getPathIterator(final BaseTransform TRANSFORM) {
        return null == TRANSFORM ? new CopyIterator(this) : new TxIterator(this, TRANSFORM);
    }

    public final void closePath() {
        if (numTypes == 0 || pointTypes[numTypes - 1] != SEG_CLOSE) {
            needRoom(true, 0);
            pointTypes[numTypes++] = SEG_CLOSE;
            prevX = currentX = moveX;
            prevY = currentY = moveY;
        }
    }

    public void pathDone() {
    }

    public final void append(final PathIterator PATH_ITERATOR, boolean connect) {
        double coords[] = new double[6];
        while (!PATH_ITERATOR.isDone()) {
            switch (PATH_ITERATOR.currentSegment(coords)) {
                case SEG_MOVETO:
                    if (!connect || numTypes < 1 || numCoords < 1) {
                        moveTo(coords[0], coords[1]);
                        break;
                    }
                    if (pointTypes[numTypes - 1] != SEG_CLOSE && doubleCoords[numCoords-2] == coords[0] && doubleCoords[numCoords-1] == coords[1]) {
                        break;
                    }
                    // NO BREAK;
                case SEG_LINETO:
                    lineTo(coords[0], coords[1]);
                    break;
                case SEG_QUADTO:
                    quadraticCurveTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
                case SEG_CUBICTO:
                    bezierCurveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                case SEG_CLOSE:
                    closePath();
                    break;
            }
            PATH_ITERATOR.next();
            connect = false;
        }
    }
    public final void append(final Shape SHAPE, final boolean CONNECT) { append(SHAPE.getPathIterator(null), CONNECT); }

    public final void appendOvalQuadrant(double sx, double sy, double cx, double cy, double ex, double ey, double tfrom, double tto, CornerPrefix prefix) {
        if (!(Double.compare(tfrom, 0) >= 0 && Double.compare(tfrom, tto) <= 0 && Double.compare(tto, 1.0) <= 0.0)) { throw new IllegalArgumentException("0 <= tfrom <= tto <= 1 required"); }
        double cx0 = (sx + (cx - sx) * EllipseIterator.CtrlVal);
        double cy0 = (sy + (cy - sy) * EllipseIterator.CtrlVal);
        double cx1 = (ex + (cx - ex) * EllipseIterator.CtrlVal);
        double cy1 = (ey + (cy - ey) * EllipseIterator.CtrlVal);
        if (tto < 1.0) {
            double t = 1.0 - tto;
            ex  += (cx1 - ex)  * t;
            ey  += (cy1 - ey)  * t;
            cx1 += (cx0 - cx1) * t;
            cy1 += (cy0 - cy1) * t;
            cx0 += (sx  - cx0) * t;
            cy0 += (sy  - cy0) * t;
            ex  += (cx1 - ex)  * t;
            ey  += (cy1 - ey)  * t;
            cx1 += (cx0 - cx1) * t;
            cy1 += (cy0 - cy1) * t;
            ex  += (cx1 - ex)  * t;
            ey  += (cy1 - ey)  * t;
        }
        if (tfrom > 0.0) {
            if (tto < 1.0) { tfrom = tfrom / tto; }
            sx  += (cx0 - sx)  * tfrom;
            sy  += (cy0 - sy)  * tfrom;
            cx0 += (cx1 - cx0) * tfrom;
            cy0 += (cy1 - cy0) * tfrom;
            cx1 += (ex  - cx1) * tfrom;
            cy1 += (ey  - cy1) * tfrom;
            sx  += (cx0 - sx)  * tfrom;
            sy  += (cy0 - sy)  * tfrom;
            cx0 += (cx1 - cx0) * tfrom;
            cy0 += (cy1 - cy0) * tfrom;
            sx  += (cx0 - sx)  * tfrom;
            sy  += (cy0 - sy)  * tfrom;
        }
        if (prefix == CornerPrefix.MOVE_THEN_CORNER) {
            moveTo(sx, sy);
        } else if (prefix == CornerPrefix.LINE_THEN_CORNER) {
            if (numTypes == 1 || sx != currentX || sy != currentY) { lineTo(sx, sy); }
        }
        if (tfrom == tto || (sx == cx0 && cx0 == cx1 && cx1 == ex && sy == cy0 && cy0 == cy1 && cy1 == ey)) {
            if (prefix != CornerPrefix.LINE_THEN_CORNER) { lineTo(ex, ey); }
        } else {
            bezierCurveTo(cx0, cy0, cx1, cy1, ex, ey);
        }
    }

    public final void appendSVGPath(final String SVG_PATH) {
        SVGParser p = new SVGParser(SVG_PATH);
        p.allowComma = false;
        while (!p.isDone()) {
            p.allowComma = false;
            char cmd = p.getChar();
            switch (cmd) {
                case 'M':
                    moveTo(p.f(), p.f());
                    while (p.nextIsNumber()) { lineTo(p.f(), p.f()); }
                    break;
                case 'm':
                    if (numTypes > 0) {
                        moveToRel(p.f(), p.f());
                    } else {
                        moveTo(p.f(), p.f());
                    }
                    while (p.nextIsNumber()) { lineToRel(p.f(), p.f()); }
                    break;
                case 'L':
                    do {
                        lineTo(p.f(), p.f());
                    } while (p.nextIsNumber());
                    break;
                case 'l':
                    do {
                        lineToRel(p.f(), p.f());
                    } while (p.nextIsNumber());
                    break;
                case 'H':
                    do {
                        lineTo(p.f(), currentY);
                    } while (p.nextIsNumber());
                    break;
                case 'h':
                    do {
                        lineToRel(p.f(), 0);
                    } while (p.nextIsNumber());
                    break;
                case 'V':
                    do {
                        lineTo(currentX, p.f());
                    } while (p.nextIsNumber());
                    break;
                case 'v':
                    do {
                        lineToRel(0, p.f());
                    } while (p.nextIsNumber());
                    break;
                case 'Q':
                    do {
                        quadraticCurveTo(p.f(), p.f(), p.f(), p.f());
                    } while (p.nextIsNumber());
                    break;
                case 'q':
                    do {
                        quadraticCurveToRel(p.f(), p.f(), p.f(), p.f());
                    } while (p.nextIsNumber());
                    break;
                case 'T':
                    do {
                        quadraticCurveToSmooth(p.f(), p.f());
                    } while (p.nextIsNumber());
                    break;
                case 't':
                    do {
                        quadraticCurveToSmoothRel(p.f(), p.f());
                    } while (p.nextIsNumber());
                    break;
                case 'C':
                    do {
                        bezierCurveTo(p.f(), p.f(), p.f(), p.f(), p.f(), p.f());
                    } while (p.nextIsNumber());
                    break;
                case 'c':
                    do {
                        bezierCurveToRel(p.f(), p.f(), p.f(), p.f(), p.f(), p.f());
                    } while (p.nextIsNumber());
                    break;
                case 'S':
                    do {
                        bezierCurveToSmooth(p.f(), p.f(), p.f(), p.f());
                    } while (p.nextIsNumber());
                    break;
                case 's':
                    do {
                        bezierCurveToSmoothRel(p.f(), p.f(), p.f(), p.f());
                    } while (p.nextIsNumber());
                    break;
                case 'A':
                    do {
                        arcTo(p.f(), p.f(), p.a(), p.b(), p.b(), p.f(), p.f());
                    } while (p.nextIsNumber());
                    break;
                case 'a':
                    do {
                        arcToRel(p.f(), p.f(), p.a(), p.b(), p.b(), p.f(), p.f());
                    } while (p.nextIsNumber());
                    break;
                case 'Z': case 'z': closePath(); break;
                default:
                    throw new IllegalArgumentException("invalid command (" + cmd + ") in SVG path at pos=" + p.pos);
            }
            p.allowComma = false;
        }
    }

    public final WindingRule getWindingRule() { return windingRule; }
    public final void setWindingRule(final WindingRule RULE) {
        if (RULE != WindingRule.WIND_EVEN_ODD && RULE != WindingRule.WIND_NON_ZERO) {
            throw new IllegalArgumentException("winding rule must be WIND_EVEN_ODD or WIND_NON_ZERO");
        }
        windingRule = RULE;
    }

    public final double getCurrentX() {
        if (numTypes < 1) { throw new IllegalPathStateException("no current point in empty path"); }
        return currentX;
    }
    public final double getCurrentY() {
        if (numTypes < 1) { throw new IllegalPathStateException("no current point in empty path"); }
        return currentY;
    }
    public final Point getCurrentPoint() {
        if (numTypes < 1) { return null; }
        return new Point(currentX, currentY);
    }

    public final void reset() {
        numTypes = numCoords = 0;
        moveX    = moveY = prevX = prevY = currentX = currentY = 0;
    }

    public final Shape createTransformedShape(final BaseTransform TRANSFORM) { return new Path(this, TRANSFORM); }

    @Override public Path copy() { return new Path(this); }

    @Override public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj instanceof Path) {
            Path p = (Path)obj;
            if (p.numTypes == this.numTypes && p.numCoords == this.numCoords && p.windingRule == this.windingRule) {
                for (int i = 0; i < numTypes; i++) {
                    if (p.pointTypes[i] != this.pointTypes[i]) { return false; }
                }
                for (int i = 0; i < numCoords; i++) {
                    if (p.doubleCoords[i] != this.doubleCoords[i]) { return false; }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean contains(final PathIterator PATH_ITERATOR, final double X, final double Y) {
        if (X * 0 + Y * 0 == 0) {
            int mask  = (PATH_ITERATOR.getWindingRule() == WindingRule.WIND_NON_ZERO ? -1 : 1);
            int cross = Shape.pointCrossingsForPath(PATH_ITERATOR, X, Y);
            return ((cross & mask) != 0);
        } else {
            return false;
        }
    }
    public static boolean contains(final PathIterator PATH_ITERATOR, final Point POINT) { return contains(PATH_ITERATOR, POINT.x, POINT.y); }
    public final boolean contains(final double X, final double Y) {
        if (X * 0 + Y * 0 == 0) {
            if (numTypes < 2) { return false; }
            int mask = (windingRule == WindingRule.WIND_NON_ZERO ? -1 : 1);
            return ((pointCrossings(X, Y) & mask) != 0);
        } else {
            return false;
        }
    }
    @Override public final boolean contains(final Point POINT) { return contains(POINT.x, POINT.y); }
    public static boolean contains(final PathIterator PATH_ITERATOR, final double X, final double Y, final double WIDTH, final double HEIGHT) {
        if (Double.isNaN(X + WIDTH) || Double.isNaN(Y + HEIGHT)) { return false; }
        if (WIDTH <= 0 || HEIGHT <= 0) { return false; }

        int mask      = (PATH_ITERATOR.getWindingRule() == WindingRule.WIND_NON_ZERO ? -1 : 2);
        int crossings = Shape.rectCrossingsForPath(PATH_ITERATOR, X, Y, X + WIDTH, Y + HEIGHT);
        return (crossings != Shape.RECT_INTERSECTS && (crossings & mask) != 0);
    }
    public final boolean contains(final double X, final double Y, final double WIDTH, final double HEIGHT) {
        if (Double.isNaN(X + WIDTH) || Double.isNaN(Y + HEIGHT)) { return false; }
        if (WIDTH <= 0 || HEIGHT <= 0) { return false; }

        int mask      = (windingRule == WindingRule.WIND_NON_ZERO ? -1 : 2);
        int crossings = rectCrossings(X, Y, X + WIDTH, Y + HEIGHT);
        return (crossings != Shape.RECT_INTERSECTS && (crossings & mask) != 0);
    }

    public static boolean intersects(final PathIterator PATH_ITERATOR, final double X, final double Y, final double WIDTH, final double HEIGHT) {
        if (Double.isNaN(X + WIDTH) || Double.isNaN(Y + HEIGHT)) { return false; }
        if (WIDTH <= 0 || HEIGHT <= 0) { return false; }

        int mask      = (PATH_ITERATOR.getWindingRule() == WindingRule.WIND_NON_ZERO ? -1 : 2);
        int crossings = Shape.rectCrossingsForPath(PATH_ITERATOR, X, Y, X+WIDTH, Y+HEIGHT);
        return (crossings == Shape.RECT_INTERSECTS || (crossings & mask) != 0);
    }
    public final boolean intersects(final double X, final double Y, final double WIDTH, final double HEIGHT) {
        if (Double.isNaN(X + WIDTH) || Double.isNaN(Y + HEIGHT)) { return false; }
        if (WIDTH <= 0 || HEIGHT <= 0) { return false; }

        int mask      = (windingRule == WindingRule.WIND_NON_ZERO ? -1 : 2);
        int crossings = rectCrossings(X, Y, X+WIDTH, Y+HEIGHT);
        return (crossings == Shape.RECT_INTERSECTS || (crossings & mask) != 0);
    }

    public PathIterator getPathIterator(final BaseTransform TRANSFORM, final double FLATNESS) {
        return new FlatteningPathIterator(getPathIterator(TRANSFORM), FLATNESS);
    }

    static byte[] copyOf(final byte[] ORIGINAL, final int NEW_LENGTH) {
        byte[] copy = new byte[NEW_LENGTH];
        System.arraycopy(ORIGINAL, 0, copy, 0, Math.min(ORIGINAL.length, NEW_LENGTH));
        return copy;
    }
    static double[] copyOf(final double[] ORIGINAL, final int NEW_LENGTH) {
        double[] copy = new double[NEW_LENGTH];
        System.arraycopy(ORIGINAL, 0, copy, 0, Math.min(ORIGINAL.length, NEW_LENGTH));
        return copy;
    }

    public void setTo(final Path OTHER_PATH) {
        numTypes  = OTHER_PATH.numTypes;
        numCoords = OTHER_PATH.numCoords;

        if (numTypes > pointTypes.length) { pointTypes = new byte[numTypes]; }

        System.arraycopy(OTHER_PATH.pointTypes, 0, pointTypes, 0, numTypes);

        if (numCoords > doubleCoords.length) { doubleCoords = new double[numCoords]; }

        System.arraycopy(OTHER_PATH.doubleCoords, 0, doubleCoords, 0, numCoords);

        windingRule = OTHER_PATH.windingRule;
        moveX       = OTHER_PATH.moveX;
        moveY       = OTHER_PATH.moveY;
        prevX       = OTHER_PATH.prevX;
        prevY       = OTHER_PATH.prevY;
        currentX    = OTHER_PATH.currentX;
        currentY    = OTHER_PATH.currentY;
    }

    static class CopyIterator extends Path.Iterator {
        double doubleCoords[];

        CopyIterator(final Path PATH) {
            super(PATH);
            doubleCoords = PATH.doubleCoords;
        }

        public int currentSegment(final double[] COORDINATES) {
            int type      = path.pointTypes[typeIdx];
            int numCoords = curvecoords[type];
            if (numCoords > 0) { System.arraycopy(doubleCoords, pointIdx, COORDINATES, 0, numCoords); }
            return type;
        }
    }

    static class TxIterator extends Path.Iterator {
        double        doubleCoords[];
        BaseTransform transform;

        TxIterator(final Path PATH, final BaseTransform TRANSFORM) {
            super(PATH);
            doubleCoords = PATH.doubleCoords;
            transform    = TRANSFORM;
        }

        public int currentSegment(final double[] COORDINATES) {
            int type = path.pointTypes[typeIdx];
            int numCoords = curvecoords[type];
            if (numCoords > 0) {
                transform.transform(doubleCoords, pointIdx, COORDINATES, 0, numCoords / 2);
            }
            return type;
        }
    }

    static abstract class Iterator implements PathIterator {
        int  typeIdx;
        int  pointIdx;
        Path path;


        Iterator(final Path PATH) {
            path = PATH;
        }

        public boolean isDone() { return (typeIdx >= path.numTypes); }

        public void next() {
            int type = path.pointTypes[typeIdx++];
            pointIdx += curvecoords[type];
        }

        public WindingRule getWindingRule() { return path.getWindingRule(); }
    }

    static class SVGParser {
        final String svgpath;
        final int    length;
        int          pos;
        boolean      allowComma;


        public SVGParser(final String SVG_PATH) {
            svgpath = SVG_PATH;
            length  = SVG_PATH.length();
        }


        public boolean isDone() { return (toNextNonWsp() >= length); }

        public char getChar() { return svgpath.charAt(pos++); }

        public boolean nextIsNumber() {
            if (toNextNonWsp() < length) {
                switch (svgpath.charAt(pos)) {
                    case '-':
                    case '+':
                    case '0': case '1': case '2': case '3': case '4':
                    case '5': case '6': case '7': case '8': case '9':
                    case '.': return true;
                }
            }
            return false;
        }

        public double f() { return getDouble(); }

        public double a() { return Math.toRadians(getDouble()); }

        public double getDouble() {
            int start  = toNextNonWsp();
            int end    = toNumberEnd();
            allowComma = true;

            if (start < end) {
                String flstr = svgpath.substring(start, end);
                try {
                    return Double.parseDouble(flstr);
                } catch (NumberFormatException e) { }
                throw new IllegalArgumentException("invalid double (" + flstr + ") in path at pos=" + start);
            }
            throw new IllegalArgumentException("end of path looking for double");
        }

        public boolean b() {
            toNextNonWsp();
            allowComma = true;
            if (pos < length) {
                char flag = svgpath.charAt(pos);
                switch (flag) {
                    case '0': pos++; return false;
                    case '1': pos++; return true;
                }
                throw new IllegalArgumentException("invalid boolean flag (" + flag + ") in path at pos=" + pos);
            }
            throw new IllegalArgumentException("end of path looking for boolean");
        }

        private int toNextNonWsp() {
            boolean canBeComma = allowComma;
            while (pos < length) {
                switch (svgpath.charAt(pos)) {
                    case ',':
                        if (!canBeComma) { return pos; }
                        canBeComma = false;
                        break;
                    case ' ':
                    case '\t':
                    case '\r':
                    case '\n':
                        break;
                    default:
                        return pos;
                }
                pos++;
            }
            return pos;
        }

        private int toNumberEnd() {
            boolean allowSign  = true;
            boolean hasExp     = false;
            boolean hasDecimal = false;
            while (pos < length) {
                switch (svgpath.charAt(pos)) {
                    case '-':
                    case '+':
                        if (!allowSign) return pos;
                        allowSign = false;
                        break;
                    case '0': case '1': case '2': case '3': case '4':
                    case '5': case '6': case '7': case '8': case '9':
                        allowSign = false;
                        break;
                    case 'E': case 'e':
                        if (hasExp) return pos;
                        hasExp = allowSign = true;
                        break;
                    case '.':
                        if (hasExp || hasDecimal) return pos;
                        hasDecimal = true;
                        allowSign  = false;
                        break;
                    default:
                        return pos;
                }
                pos++;
            }
            return pos;
        }
    }

    public Paint getFill() { return fill; }
    public void setFill(final Paint FILL) { fill = FILL;}

    public Paint getStroke() { return stroke; }
    public void setStroke(final Paint STROKE) { stroke = STROKE; }

    public void draw(final GraphicsContext CTX) {
        draw(CTX, false, false);
    }
    public void draw(final GraphicsContext CTX, final boolean FILL, final boolean STROKE) {
        draw(CTX, FILL, fill, STROKE, stroke);
    }
    public void draw(final GraphicsContext CTX, final boolean FILL, final Paint FILL_PAINT, final boolean STROKE, final Paint STROKE_PAINT) {
        PathIterator pi = getPathIterator(new Affine());

        CTX.setFillRule(WindingRule.WIND_EVEN_ODD == pi.getWindingRule() ? FillRule.EVEN_ODD : FillRule.NON_ZERO);
        CTX.beginPath();

        double[] seg = new double[6];
        int      segType;

        while(!pi.isDone()) {
            segType = pi.currentSegment(seg);
            switch (segType) {
                case PathIterator.MOVE_TO  : CTX.moveTo(seg[0], seg[1]); break;
                case PathIterator.LINE_TO  : CTX.lineTo(seg[0], seg[1]); break;
                case PathIterator.QUAD_TO  : CTX.quadraticCurveTo(seg[0], seg[1], seg[2], seg[3]);break;
                case PathIterator.BEZIER_TO: CTX.bezierCurveTo(seg[0], seg[1], seg[2], seg[3], seg[4], seg[5]);break;
                case PathIterator.CLOSE    : CTX.closePath();break;
                default                    : break;
            }
            pi.next();
        }

        if (FILL)   { CTX.setFill(FILL_PAINT); CTX.fill(); }
        if (STROKE) { CTX.setStroke(STROKE_PAINT); CTX.stroke(); }
    }
}