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


public class QuadCurve extends Shape {
    public double x1;
    public double y1;
    public double ctrlx;
    public double ctrly;
    public double x2;
    public double y2;

    private static final int BELOW     = -2;
    private static final int LOW_EDGE  = -1;
    private static final int INSIDE    = 0;
    private static final int HIGH_EDGE = 1;
    private static final int ABOVE     = 2;


    public QuadCurve() { }
    public QuadCurve(final double X1, final double Y1, final double CTRL_X, final double CTRL_Y, final double X2, final double Y2) {
        setCurve(X1, Y1, CTRL_X, CTRL_Y, X2, Y2);
    }


    public void setCurve(final double X1, final double Y1, final double CTRL_X, final double CTRL_Y, final double X2, final double Y2) {
        x1    = X1;
        y1    = Y1;
        ctrlx = CTRL_X;
        ctrly = CTRL_Y;
        x2    = X2;
        y2    = Y2;
    }

    public RectBounds getBounds() {
        double left   = Math.min(Math.min(x1, x2), ctrlx);
        double top    = Math.min(Math.min(y1, y2), ctrly);
        double right  = Math.max(Math.max(x1, x2), ctrlx);
        double bottom = Math.max(Math.max(y1, y2), ctrly);
        return new RectBounds(left, top, right, bottom);
    }

    public BezierCurve toCubic() {
        return new BezierCurve(x1, y1, (x1 + 2 * ctrlx) / 3, (y1 + 2 * ctrly) / 3, (2 * ctrlx + x2) / 3, (2 * ctrly + y2) / 3, x2, y2);
    }

    public void setCurve(final double[] COORDS, final int OFFSET) {
        setCurve(COORDS[OFFSET + 0], COORDS[OFFSET + 1], COORDS[OFFSET + 2], COORDS[OFFSET + 3], COORDS[OFFSET + 4], COORDS[OFFSET + 5]);
    }
    public void setCurve(final Point P1, final Point CP, final Point P2) { setCurve(P1.x, P1.y, CP.x, CP.y, P2.x, P2.y); }
    public void setCurve(final Point[] POINTS, final int OFFSET) {
        setCurve(POINTS[OFFSET + 0].x, POINTS[OFFSET + 0].y, POINTS[OFFSET + 1].x, POINTS[OFFSET + 1].y, POINTS[OFFSET + 2].x, POINTS[OFFSET + 2].y);
    }
    public void setCurve(final QuadCurve QUAD_CURVE) { setCurve(QUAD_CURVE.x1, QUAD_CURVE.y1, QUAD_CURVE.ctrlx, QUAD_CURVE.ctrly, QUAD_CURVE.x2, QUAD_CURVE.y2); }

    public double getFlatnessSq() { return Line.ptSegDistSq(x1, y1, x2, y2, ctrlx, ctrly); }
    public static double getFlatnessSq(final double COORDS[], final int OFFSET) {
        return Line.ptSegDistSq(COORDS[OFFSET + 0], COORDS[OFFSET + 1], COORDS[OFFSET + 4], COORDS[OFFSET + 5], COORDS[OFFSET + 2], COORDS[OFFSET + 3]);
    }
    public static double getFlatnessSq(double x1, double y1, double ctrlx, double ctrly, double x2, double y2) {
        return Line.ptSegDistSq(x1, y1, x2, y2, ctrlx, ctrly);
    }

    public double getFlatness() { return Line.ptSegDist(x1, y1, x2, y2, ctrlx, ctrly); }
    public static double getFlatness(final double COORDS[], final int OFFSET) {
        return Line.ptSegDist(COORDS[OFFSET + 0], COORDS[OFFSET + 1], COORDS[OFFSET + 4], COORDS[OFFSET + 5], COORDS[OFFSET + 2], COORDS[OFFSET + 3]);
    }
    public static double getFlatness(double x1, double y1, double ctrlx, double ctrly, double x2, double y2) {
        return Line.ptSegDist(x1, y1, x2, y2, ctrlx, ctrly);
    }

    public void subdivide(final QuadCurve LEFT, final QuadCurve RIGHT) { subdivide(this, LEFT, RIGHT); }
    public static void subdivide(final QuadCurve SOURCE, final QuadCurve LEFT, final QuadCurve RIGHT) {
        double x1     = SOURCE.x1;
        double y1     = SOURCE.y1;
        double ctrlx  = SOURCE.ctrlx;
        double ctrly  = SOURCE.ctrly;
        double x2     = SOURCE.x2;
        double y2     = SOURCE.y2;
        double ctrlx1 = (x1 + ctrlx) / 2.0;
        double ctrly1 = (y1 + ctrly) / 2.0;
        double ctrlx2 = (x2 + ctrlx) / 2.0;
        double ctrly2 = (y2 + ctrly) / 2.0;
        ctrlx = (ctrlx1 + ctrlx2) / 2.0;
        ctrly = (ctrly1 + ctrly2) / 2.0;
        if (LEFT != null)  { LEFT.setCurve(x1, y1, ctrlx1, ctrly1, ctrlx, ctrly); }
        if (RIGHT != null) { RIGHT.setCurve(ctrlx, ctrly, ctrlx2, ctrly2, x2, y2); }
    }
    public static void subdivide(final double SOURCE[], final int SOURCE_OFFSET, final double LEFT[], final int LEFT_OFFSET, final double RIGHT[], final int RIGHT_OFFSET) {
        double x1    = SOURCE[SOURCE_OFFSET + 0];
        double y1    = SOURCE[SOURCE_OFFSET + 1];
        double ctrlx = SOURCE[SOURCE_OFFSET + 2];
        double ctrly = SOURCE[SOURCE_OFFSET + 3];
        double x2    = SOURCE[SOURCE_OFFSET + 4];
        double y2    = SOURCE[SOURCE_OFFSET + 5];
        if (LEFT != null) {
            LEFT[LEFT_OFFSET + 0] = x1;
            LEFT[LEFT_OFFSET + 1] = y1;
        }
        if (RIGHT != null) {
            RIGHT[RIGHT_OFFSET + 4] = x2;
            RIGHT[RIGHT_OFFSET + 5] = y2;
        }
        x1    = (x1 + ctrlx) / 2.0;
        y1    = (y1 + ctrly) / 2.0;
        x2    = (x2 + ctrlx) / 2.0;
        y2    = (y2 + ctrly) / 2.0;
        ctrlx = (x1 + x2) / 2.0;
        ctrly = (y1 + y2) / 2.0;
        if (LEFT != null) {
            LEFT[LEFT_OFFSET + 2] = x1;
            LEFT[LEFT_OFFSET + 3] = y1;
            LEFT[LEFT_OFFSET + 4] = ctrlx;
            LEFT[LEFT_OFFSET + 5] = ctrly;
        }
        if (RIGHT != null) {
            RIGHT[RIGHT_OFFSET + 0] = ctrlx;
            RIGHT[RIGHT_OFFSET + 1] = ctrly;
            RIGHT[RIGHT_OFFSET + 2] = x2;
            RIGHT[RIGHT_OFFSET + 3] = y2;
        }
    }

    public static int solveQuadratic(final double EQN[]) { return solveQuadratic(EQN, EQN); }
    public static int solveQuadratic(final double EQN[], final double RES[]) {
        double a = EQN[2];
        double b = EQN[1];
        double c = EQN[0];
        int roots = 0;
        if (Double.compare(a, 0) == 0) {
            if (Double.compare(b, 0) == 0) { return -1; }
            RES[roots++] = -c / b;
        } else {
            double d = b * b - 4.0 * a * c;
            if (d < 0) { return 0; }
            d =  Math.sqrt(d);
            if (b < 0) { d = -d; }
            double q = (b + d) / -2.0;
            RES[roots++] = q / a;
            if (q != 0) { RES[roots++] = c / q; }
        }
        return roots;
    }

    public boolean contains(Point POINT) { return contains(POINT.x, POINT.y); }
    public boolean contains(double X, double Y) {
        double x1 = this.x1;
        double y1 = this.y1;
        double xc = this.ctrlx;
        double yc = this.ctrly;
        double x2 = this.x2;
        double y2 = this.y2;

        double kx  = x1 - 2 * xc + x2;
        double ky  = y1 - 2 * yc + y2;
        double dx  = X - x1;
        double dy  = Y - y1;
        double dxl = x2 - x1;
        double dyl = y2 - y1;

        double t0 = (dx * ky - dy * kx) / (dxl * ky - dyl * kx);
        if (t0 < 0 || t0 > 1 || t0 != t0) { return false; }

        double xb = kx * t0 * t0 + 2 * (xc - x1) * t0 + x1;
        double yb = ky * t0 * t0 + 2 * (yc - y1) * t0 + y1;
        double xl = dxl * t0 + x1;
        double yl = dyl * t0 + y1;

        return (X >= xb && X < xl) ||
               (X >= xl && X < xb) ||
               (Y >= yb && Y < yl) ||
               (Y >= yl && Y < yb);
    }

    private static void fillEqn(final double EQN[], final double VAL, final double C1, final double CP, final double C2) {
        EQN[0] = C1 - VAL;
        EQN[1] = CP + CP - C1 - C1;
        EQN[2] = C1 - CP - CP + C2;
    }

    private static int evalQuadratic(final double VALUES[], final int NUM, final boolean INCLUDE_0, final boolean INCLUDE_1, final double INFLECT[], final double C1, final double CTRL, final double C2) {
        int j = 0;
        for (int i = 0; i < NUM; i++) {
            double t = VALUES[i];
            if ((INCLUDE_0 ? t >= 0 : t > 0) && (INCLUDE_1 ? t <= 1 : t < 1) && (INFLECT == null || INFLECT[1] + 2*INFLECT[2]*t != 0)) {
                double u  = 1 - t;
                VALUES[j++] = C1*u*u + 2*CTRL*t*u + C2*t*t;
            }
        }
        return j;
    }

    private static int getTag(final double COORD, final double LOW, final double HIGH) {
        if (COORD <= LOW)  { return (COORD < LOW ? BELOW : LOW_EDGE); }
        if (COORD >= HIGH) { return (COORD > HIGH ? ABOVE : HIGH_EDGE); }
        return INSIDE;
    }

    private static boolean inwards(final int PT_TAG, final int OPT_1_TAG, final int OPT_2_TAG) {
        switch (PT_TAG) {
            case BELOW    :
            case ABOVE    :
            default       : return false;
            case LOW_EDGE : return (OPT_1_TAG >= INSIDE || OPT_2_TAG >= INSIDE);
            case INSIDE   : return true;
            case HIGH_EDGE: return (OPT_1_TAG <= INSIDE || OPT_2_TAG <= INSIDE);
        }
    }

    public boolean intersects(double X, double Y, double WIDTH, double HEIGHT) {
        if (WIDTH <= 0 || HEIGHT <= 0) { return false; }

        double x1 = this.x1;
        double y1 = this.y1;
        int x1tag = getTag(x1, X, X + WIDTH);
        int y1tag = getTag(y1, Y, Y + HEIGHT);
        if (x1tag == INSIDE && y1tag == INSIDE) { return true; }
        double x2 = this.x2;
        double y2 = this.y2;
        int x2tag = getTag(x2, X, X + WIDTH);
        int y2tag = getTag(y2, Y, Y + HEIGHT);
        if (x2tag == INSIDE && y2tag == INSIDE) { return true; }
        double ctrlx = this.ctrlx;
        double ctrly = this.ctrly;
        int ctrlxtag = getTag(ctrlx, X, X + WIDTH);
        int ctrlytag = getTag(ctrly, Y, Y + HEIGHT);

        if (x1tag < INSIDE && x2tag < INSIDE && ctrlxtag < INSIDE) { return false; }
        if (y1tag < INSIDE && y2tag < INSIDE && ctrlytag < INSIDE) { return false; }
        if (x1tag > INSIDE && x2tag > INSIDE && ctrlxtag > INSIDE) { return false; }
        if (y1tag > INSIDE && y2tag > INSIDE && ctrlytag > INSIDE) { return false; }

        if (inwards(x1tag, x2tag, ctrlxtag) && inwards(y1tag, y2tag, ctrlytag)) { return true; }
        if (inwards(x2tag, x1tag, ctrlxtag) && inwards(y2tag, y1tag, ctrlytag)) { return true; }

        boolean xoverlap = (x1tag * x2tag <= 0);
        boolean yoverlap = (y1tag * y2tag <= 0);
        if (x1tag == INSIDE && x2tag == INSIDE && yoverlap) { return true; }
        if (y1tag == INSIDE && y2tag == INSIDE && xoverlap) { return true; }

        double[] eqn = new double[3];
        double[] res = new double[3];
        if (!yoverlap) {
            fillEqn(eqn, (y1tag < INSIDE ? Y : Y + HEIGHT), y1, ctrly, y2);
            return (solveQuadratic(eqn, res) == 2 &&
                    evalQuadratic(res, 2, true, true, null, x1, ctrlx, x2) == 2 &&
                    getTag(res[0], X, X + WIDTH) * getTag(res[1], X, X + WIDTH) <= 0);
        }

        if (!xoverlap) {
            fillEqn(eqn, (x1tag < INSIDE ? X : X + WIDTH), x1, ctrlx, x2);
            return (solveQuadratic(eqn, res) == 2 &&
                    evalQuadratic(res, 2, true, true, null, y1, ctrly, y2) == 2 &&
                    getTag(res[0], Y, Y + HEIGHT) * getTag(res[1], Y, Y + HEIGHT) <= 0);
        }

        double dx = x2 - x1;
        double dy = y2 - y1;
        double k = y2 * x1 - x2 * y1;
        int c1tag, c2tag;
        if (y1tag == INSIDE) {
            c1tag = x1tag;
        } else {
            c1tag = getTag((k + dx * (y1tag < INSIDE ? Y : Y + HEIGHT)) / dy, X, X + WIDTH);
        }
        if (y2tag == INSIDE) {
            c2tag = x2tag;
        } else {
            c2tag = getTag((k + dx * (y2tag < INSIDE ? Y : Y + HEIGHT)) / dy, X, X + WIDTH);
        }
        if (c1tag * c2tag <= 0) { return true; }

        c1tag = ((c1tag * x1tag <= 0) ? y1tag : y2tag);

        fillEqn(eqn, (c2tag < INSIDE ? X : X + WIDTH), x1, ctrlx, x2);
        int num = solveQuadratic(eqn, res);

        evalQuadratic(res, num, true, true, null, y1, ctrly, y2);

        c2tag = getTag(res[0], Y, Y + HEIGHT);

        return (c1tag * c2tag <= 0);
    }

    public boolean contains(double X, double Y, double WIDTH, double HEIGHT) {
        if (WIDTH <= 0 || HEIGHT <= 0) { return false; }
        return (contains(X, Y) && contains(X + WIDTH, Y) && contains(X + WIDTH, Y + HEIGHT) && contains(X, Y + HEIGHT));
    }

    public PathIterator getPathIterator(BaseTransform TRANSFORM) { return new QuadIterator(this, TRANSFORM); }
    public PathIterator getPathIterator(BaseTransform TRANSFORM, double FLATNESS) { return new FlatteningPathIterator(getPathIterator(TRANSFORM), FLATNESS); }

    @Override public QuadCurve copy() { return new QuadCurve(x1, y1, ctrlx, ctrly, x2, y2); }

    @Override public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj instanceof QuadCurve) {
            QuadCurve curve = (QuadCurve) obj;
            return ((x1 == curve.x1) && (y1 == curve.y1) && (x2 == curve.x2) && (y2 == curve.y2) && (ctrlx == curve.ctrlx) && (ctrly == curve.ctrly));
        }
        return false;
    }
}