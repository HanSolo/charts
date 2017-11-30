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

import java.util.Arrays;


public class BezierCurve extends Shape {
    private static final int BELOW     = -2;
    private static final int LOW_EDGE  = -1;
    private static final int INSIDE    = 0;
    private static final int HIGH_EDGE = 1;
    private static final int ABOVE     = 2;

    public double x1;
    public double y1;

    public double ctrlx1;
    public double ctrly1;
    public double ctrlx2;
    public double ctrly2;

    public double x2;
    public double y2;


    public BezierCurve() { }
    public BezierCurve(final double X1, final double Y1, final double CTRL_X1, final double CTRL_Y1, final double CTRL_X2, final double CTRL_Y2, final double X2, final double Y2) {
        setCurve(X1, Y1, CTRL_X1, CTRL_Y1, CTRL_X2, CTRL_Y2, X2, Y2);
    }


    public static int solveCubic(final double EQN[]) { return solveCubic(EQN, EQN); }
    public static int solveCubic(double eqn[], final double RES[]) {
        double d = eqn[3];
        if (Double.compare(d, 0) == 0) { return QuadCurve.solveQuadratic(eqn, RES); }
        double a     = eqn[2] / d;
        double b     = eqn[1] / d;
        double c     = eqn[0] / d;
        int    roots = 0;
        double Q     = (a * a - 3.0 * b) / 9.0;
        double R     = (2.0 * a * a * a - 9.0 * a * b + 27.0 * c) / 54.0;
        double R2    = R * R;
        double Q3    = Q * Q * Q;
        a = a / 3.0;
        if (R2 < Q3) {
            double theta =  Math.acos(R / Math.sqrt(Q3));
            Q =  (-2.0 * Math.sqrt(Q));
            if (RES == eqn) {
                eqn = new double[4];
                System.arraycopy(RES, 0, eqn, 0, 4);
            }
            RES[roots++] = (Q * Math.cos(theta / 3.0) - a);
            RES[roots++] = (Q * Math.cos((theta + Math.PI * 2.0) / 3.0) - a);
            RES[roots++] = (Q * Math.cos((theta - Math.PI * 2.0) / 3.0) - a);
            fixRoots(RES, eqn);
        } else {
            boolean neg = (R < 0.0);
            double    S = Math.sqrt(R2 - Q3);
            if (neg) { R = -R; }
            double A = Math.pow(R + S, 1.0 / 3.0);
            if (!neg) { A = -A; }
            double B = (Double.compare(A, 0) == 0) ? 0.0 : (Q / A);
            RES[roots++] = (A + B) - a;
        }
        return roots;
    }

    private static void fixRoots(final double RES[], final double EQN[]) {
        final double EPSILON =  1E-5; // eek, Rich may have botched this
        for (int i = 0; i < 3; i++) {
            double t = RES[i];
            if (Math.abs(t) < EPSILON) {
                RES[i] = findZero(t, 0, EQN);
            } else if (Math.abs(t - 1) < EPSILON) {
                RES[i] = findZero(t, 1, EQN);
            }
        }
    }

    private static double solveEqn(final double EQN[], int order, final double T) {
        double v = EQN[order];
        while (--order >= 0) { v = v * T + EQN[order]; }
        return v;
    }

    private static double findZero(double t, final double TARGET, final double EQN[]) {
        double slopeqn[] = { EQN[1], 2 * EQN[2], 3 * EQN[3] };
        double slope;
        double origdelta = 0;
        double origt     = t;
        while (true) {
            slope = solveEqn(slopeqn, 2, t);
            if (Double.compare(slope, 0) == 0) { return t; }
            double y = solveEqn(EQN, 3, t);
            if (Double.compare(y, 0) == 0) { return t; }
            double delta = -(y / slope);
            if (Double.compare(origdelta, 0) == 0) { origdelta = delta; }
            if (t < TARGET) {
                if (delta < 0) { return t; }
            } else if (t > TARGET) {
                if (delta > 0) { return t; }
            } else {
                return (delta > 0 ? (TARGET + java.lang.Float.MIN_VALUE) : (TARGET - java.lang.Float.MIN_VALUE));
            }
            double newt = t + delta;
            if (t == newt) { return t; }
            if (delta * origdelta < 0) {
                int tag = (origt < t ? getTag(TARGET, origt, t) : getTag(TARGET, t, origt));
                if (tag != INSIDE) { return (origt + t) / 2; }
                t = TARGET;
            } else {
                t = newt;
            }
        }
    }

    private static void fillEqn(final double EQN[], final double VAL, final double C1, final double CP1, final double CP2, final double C2) {
        EQN[0] = C1 - VAL;
        EQN[1] = (CP1 - C1) * 3.0;
        EQN[2] = (CP2 - CP1 - CP1 + C1) * 3.0;
        EQN[3] = C2 + (CP1 - CP2) * 3.0 - C1;
    }

    private static int evalCubic(double vals[], int num, boolean include0, boolean include1, double inflect[], double c1, double cp1, double cp2, double c2) {
        int j = 0;
        for (int i = 0; i < num; i++) {
            double t = vals[i];
            if ((include0 ? t >= 0 : t > 0) && (include1 ? t <= 1 : t < 1) && (inflect == null || inflect[1] + (2 * inflect[2] + 3 * inflect[3] * t) * t != 0)) {
                double u = 1 - t;
                vals[j++] = c1 * u * u * u + 3 * cp1 * t * u * u + 3 * cp2 * t * t * u + c2 * t * t * t;
            }
        }
        return j;
    }

    private static int getTag(final double COORD, final double LOW, final double HIGH) {
        if (COORD <= LOW)  { return (COORD < LOW ? BELOW : LOW_EDGE); }
        if (COORD >= HIGH) { return (COORD > HIGH ? ABOVE : HIGH_EDGE); }
        return INSIDE;
    }

    private static boolean inwards(int pttag, int opt1tag, int opt2tag) {
        switch (pttag) {
            case BELOW    :
            case ABOVE    :
            default       : return false;
            case LOW_EDGE : return (opt1tag >= INSIDE || opt2tag >= INSIDE);
            case INSIDE   : return true;
            case HIGH_EDGE: return (opt1tag <= INSIDE || opt2tag <= INSIDE);
        }
    }

    public RectBounds getBounds() {
        double left   = Math.min(Math.min(x1, x2), Math.min(ctrlx1, ctrlx2));
        double top    = Math.min(Math.min(y1, y2), Math.min(ctrly1, ctrly2));
        double right  = Math.max(Math.max(x1, x2), Math.max(ctrlx1, ctrlx2));
        double bottom = Math.max(Math.max(y1, y2), Math.max(ctrly1, ctrly2));
        return new RectBounds(left, top, right, bottom);
    }

    public Point eval(final double T) {
        Point result = new Point();
        eval(T, result);
        return result;
    }
    public void eval(final double TD, final Point RESULT) { RESULT.set(calcX(TD), calcY(TD)); }

    public Point evalDt(final double T) {
        Point result = new Point();
        evalDt(T, result);
        return result;
    }
    public void evalDt(double td, final Point RESULT) {
        double t = td;
        double u = 1 - t;
        double x = 3 * ((ctrlx1 - x1) * u * u + 2 * (ctrlx2 - ctrlx1) * u * t + (x2 - ctrlx2) * t * t);
        double y = 3 * ((ctrly1 - y1) * u * u + 2 * (ctrly2 - ctrly1) * u * t + (y2 - ctrly2) * t * t);
        RESULT.set(x, y);
    }

    public void setCurve(final double[] COORDS, final int OFFSET) {
        setCurve(COORDS[OFFSET + 0], COORDS[OFFSET + 1], COORDS[OFFSET + 2], COORDS[OFFSET + 3], COORDS[OFFSET + 4], COORDS[OFFSET + 5], COORDS[OFFSET + 6], COORDS[OFFSET + 7]);
    }
    public void setCurve(final Point P1, final Point CP1, final Point CP2, final Point P2) {
        setCurve(P1.x, P1.y, CP1.x, CP1.y, CP2.x, CP2.y, P2.x, P2.y);
    }
    public void setCurve(final Point[] POINTS, final int OFFSET) {
        setCurve(POINTS[OFFSET + 0].x, POINTS[OFFSET + 0].y, POINTS[OFFSET + 1].x, POINTS[OFFSET + 1].y, POINTS[OFFSET + 2].x, POINTS[OFFSET + 2].y, POINTS[OFFSET + 3].x, POINTS[OFFSET + 3].y);
    }
    public void setCurve(final BezierCurve BEZIER_CURVE) { setCurve(BEZIER_CURVE.x1, BEZIER_CURVE.y1, BEZIER_CURVE.ctrlx1, BEZIER_CURVE.ctrly1, BEZIER_CURVE.ctrlx2, BEZIER_CURVE.ctrly2, BEZIER_CURVE.x2, BEZIER_CURVE.y2); }
    public void setCurve(final double X1, final double Y1, final double CTRL_X1, final double CTRL_Y1, final double CTRL_X2, final double CTRL_Y2, final double X2, final double Y2) {
        x1     = X1;
        y1     = Y1;
        ctrlx1 = CTRL_X1;
        ctrly1 = CTRL_Y1;
        ctrlx2 = CTRL_X2;
        ctrly2 = CTRL_Y2;
        x2     = X2;
        y2     = Y2;
    }

    public static double getFlatnessSq(final double COORDS[], final int OFFSET) {
        return getFlatnessSq(COORDS[OFFSET + 0], COORDS[OFFSET + 1], COORDS[OFFSET + 2], COORDS[OFFSET + 3], COORDS[OFFSET + 4], COORDS[OFFSET + 5], COORDS[OFFSET + 6], COORDS[OFFSET + 7]);
    }
    public double getFlatnessSq() {
        return getFlatnessSq(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
    }
    public static double getFlatnessSq(double x1, double y1, double ctrlx1, double ctrly1, double ctrlx2, double ctrly2, double x2, double y2) {
        return Math.max(Line.ptSegDistSq(x1, y1, x2, y2, ctrlx1, ctrly1), Line.ptSegDistSq(x1, y1, x2, y2, ctrlx2, ctrly2));
    }

    public static double getFlatness(final double COORDS[], final int OFFSET) {
        return getFlatness(COORDS[OFFSET + 0], COORDS[OFFSET + 1], COORDS[OFFSET + 2], COORDS[OFFSET + 3], COORDS[OFFSET + 4], COORDS[OFFSET + 5], COORDS[OFFSET + 6], COORDS[OFFSET + 7]);
    }
    public double getFlatness() { return getFlatness(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2); }
    public static double getFlatness(double x1, double y1, double ctrlx1, double ctrly1, double ctrlx2, double ctrly2, double x2, double y2) {
        return  Math.sqrt(getFlatnessSq(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2));
    }

    public void subdivide(final double T, final BezierCurve LEFT, final BezierCurve RIGHT) {
        if ((LEFT == null) && (RIGHT == null)) return;

        double npx = calcX(T);
        double npy = calcY(T);

        double x1  = this.x1;
        double y1  = this.y1;
        double c1x = this.ctrlx1;
        double c1y = this.ctrly1;
        double c2x = this.ctrlx2;
        double c2y = this.ctrly2;
        double x2  = this.x2;
        double y2  = this.y2;
        double u   = 1 - T;
        double hx  = u * c1x + T * c2x;
        double hy  = u * c1y + T * c2y;

        if (LEFT != null) {
            double lx1  = x1;
            double ly1  = y1;
            double lc1x = u * x1 + T * c1x;
            double lc1y = u * y1 + T * c1y;
            double lc2x = u * lc1x + T * hx;
            double lc2y = u * lc1y + T * hy;
            double lx2  = npx;
            double ly2  = npy;
            LEFT.setCurve(lx1, ly1, lc1x, lc1y, lc2x, lc2y, lx2, ly2);
        }

        if (RIGHT != null) {
            double rx1  = npx;
            double ry1  = npy;
            double rc2x = u * c2x + T * x2;
            double rc2y = u * c2y + T * y2;
            double rc1x = u * hx + T * rc2x;
            double rc1y = u * hy + T * rc2y;
            double rx2  = x2;
            double ry2  = y2;
            RIGHT.setCurve(rx1, ry1, rc1x, rc1y, rc2x, rc2y, rx2, ry2);
        }
    }
    public void subdivide(final BezierCurve LEFT, final BezierCurve RIGHT) { subdivide(this, LEFT, RIGHT); }
    public static void subdivide(final BezierCurve SOURCE, final BezierCurve LEFT, final BezierCurve RIGHT) {
        double x1      = SOURCE.x1;
        double y1      = SOURCE.y1;
        double ctrlx1  = SOURCE.ctrlx1;
        double ctrly1  = SOURCE.ctrly1;
        double ctrlx2  = SOURCE.ctrlx2;
        double ctrly2  = SOURCE.ctrly2;
        double x2      = SOURCE.x2;
        double y2      = SOURCE.y2;
        double centerx = (ctrlx1 + ctrlx2) / 2.0;
        double centery = (ctrly1 + ctrly2) / 2.0;
        ctrlx1 = (x1 + ctrlx1) / 2.0;
        ctrly1 = (y1 + ctrly1) / 2.0;
        ctrlx2 = (x2 + ctrlx2) / 2.0;
        ctrly2 = (y2 + ctrly2) / 2.0;
        double ctrlx12 = (ctrlx1 + centerx) / 2.0;
        double ctrly12 = (ctrly1 + centery) / 2.0;
        double ctrlx21 = (ctrlx2 + centerx) / 2.0;
        double ctrly21 = (ctrly2 + centery) / 2.0;
        centerx = (ctrlx12 + ctrlx21) / 2.0;
        centery = (ctrly12 + ctrly21) / 2.0;
        if (LEFT != null) { LEFT.setCurve(x1, y1, ctrlx1, ctrly1, ctrlx12, ctrly12, centerx, centery); }
        if (RIGHT != null) { RIGHT.setCurve(centerx, centery, ctrlx21, ctrly21, ctrlx2, ctrly2, x2, y2); }
    }
    public static void subdivide(double src[], int srcoff, double left[], int leftoff, double right[], int rightoff) {
        double x1     = src[srcoff + 0];
        double y1     = src[srcoff + 1];
        double ctrlx1 = src[srcoff + 2];
        double ctrly1 = src[srcoff + 3];
        double ctrlx2 = src[srcoff + 4];
        double ctrly2 = src[srcoff + 5];
        double x2     = src[srcoff + 6];
        double y2     = src[srcoff + 7];
        if (left != null) {
            left[leftoff + 0] = x1;
            left[leftoff + 1] = y1;
        }
        if (right != null) {
            right[rightoff + 6] = x2;
            right[rightoff + 7] = y2;
        }
        x1 = (x1 + ctrlx1) / 2.0;
        y1 = (y1 + ctrly1) / 2.0;
        x2 = (x2 + ctrlx2) / 2.0;
        y2 = (y2 + ctrly2) / 2.0;
        double centerx = (ctrlx1 + ctrlx2) / 2.0;
        double centery = (ctrly1 + ctrly2) / 2.0;
        ctrlx1  = (x1 + centerx) / 2.0;
        ctrly1  = (y1 + centery) / 2.0;
        ctrlx2  = (x2 + centerx) / 2.0;
        ctrly2  = (y2 + centery) / 2.0;
        centerx = (ctrlx1 + ctrlx2) / 2.0;
        centery = (ctrly1 + ctrly2) / 2.0;
        if (left != null) {
            left[leftoff + 2] = x1;
            left[leftoff + 3] = y1;
            left[leftoff + 4] = ctrlx1;
            left[leftoff + 5] = ctrly1;
            left[leftoff + 6] = centerx;
            left[leftoff + 7] = centery;
        }
        if (right != null) {
            right[rightoff + 0] = centerx;
            right[rightoff + 1] = centery;
            right[rightoff + 2] = ctrlx2;
            right[rightoff + 3] = ctrly2;
            right[rightoff + 4] = x2;
            right[rightoff + 5] = y2;
        }
    }

    public boolean intersects(double X, double Y, double WIDTH, double HEIGHT) {
        if (WIDTH <= 0 || HEIGHT <= 0) { return false; }

        double x1    = this.x1;
        double y1    = this.y1;
        int    x1tag = getTag(x1, X, X + WIDTH);
        int    y1tag = getTag(y1, Y, Y + HEIGHT);
        if (x1tag == INSIDE && y1tag == INSIDE) {
            return true;
        }
        double x2    = this.x2;
        double y2    = this.y2;
        int    x2tag = getTag(x2, X, X + WIDTH);
        int    y2tag = getTag(y2, Y, Y + HEIGHT);
        if (x2tag == INSIDE && y2tag == INSIDE) {
            return true;
        }

        double ctrlx1    = this.ctrlx1;
        double ctrly1    = this.ctrly1;
        double ctrlx2    = this.ctrlx2;
        double ctrly2    = this.ctrly2;
        int    ctrlx1tag = getTag(ctrlx1, X, X + WIDTH);
        int    ctrly1tag = getTag(ctrly1, Y, Y + HEIGHT);
        int    ctrlx2tag = getTag(ctrlx2, X, X + WIDTH);
        int    ctrly2tag = getTag(ctrly2, Y, Y + HEIGHT);

        if (x1tag < INSIDE && x2tag < INSIDE && ctrlx1tag < INSIDE && ctrlx2tag < INSIDE) { return false; }
        if (y1tag < INSIDE && y2tag < INSIDE && ctrly1tag < INSIDE && ctrly2tag < INSIDE) { return false; }
        if (x1tag > INSIDE && x2tag > INSIDE && ctrlx1tag > INSIDE && ctrlx2tag > INSIDE) { return false; }
        if (y1tag > INSIDE && y2tag > INSIDE && ctrly1tag > INSIDE && ctrly2tag > INSIDE) { return false; }

        if (inwards(x1tag, x2tag, ctrlx1tag) && inwards(y1tag, y2tag, ctrly1tag)) { return true; }
        if (inwards(x2tag, x1tag, ctrlx2tag) && inwards(y2tag, y1tag, ctrly2tag)) { return true; }

        boolean xoverlap = (x1tag * x2tag <= 0);
        boolean yoverlap = (y1tag * y2tag <= 0);
        if (x1tag == INSIDE && x2tag == INSIDE && yoverlap) { return true; }
        if (y1tag == INSIDE && y2tag == INSIDE && xoverlap) { return true; }

        double[] eqn = new double[4];
        double[] res = new double[4];
        if (!yoverlap) {
            fillEqn(eqn, (y1tag < INSIDE ? Y : Y + HEIGHT), y1, ctrly1, ctrly2, y2);
            int num = solveCubic(eqn, res);
            num = evalCubic(res, num, true, true, null, x1, ctrlx1, ctrlx2, x2);
            return (num == 2 && getTag(res[0], X, X + WIDTH) * getTag(res[1], X, X + WIDTH) <= 0);
        }

        if (!xoverlap) {
            fillEqn(eqn, (x1tag < INSIDE ? X : X + WIDTH), x1, ctrlx1, ctrlx2, x2);
            int num = solveCubic(eqn, res);
            num = evalCubic(res, num, true, true, null, y1, ctrly1, ctrly2, y2);
            return (num == 2 && getTag(res[0], Y, Y + HEIGHT) * getTag(res[1], Y, Y + HEIGHT) <= 0);
        }

        double dx = x2 - x1;
        double dy = y2 - y1;
        double k  = y2 * x1 - x2 * y1;
        int   c1tag, c2tag;

        c1tag = y1tag == INSIDE ? x1tag : getTag((k + dx * (y1tag < INSIDE ? Y : Y + HEIGHT)) / dy, X, X + WIDTH);
        c2tag = y2tag == INSIDE ? x2tag : getTag((k + dx * (y2tag < INSIDE ? Y : Y + HEIGHT)) / dy, X, X + WIDTH);

        if (c1tag * c2tag <= 0) { return true; }
        c1tag = ((c1tag * x1tag <= 0) ? y1tag : y2tag);

        fillEqn(eqn, (c2tag < INSIDE ? X : X + WIDTH), x1, ctrlx1, ctrlx2, x2);
        int num = solveCubic(eqn, res);
        num = evalCubic(res, num, true, true, null, y1, ctrly1, ctrly2, y2);

        int tags[] = new int[num + 1];
        for (int i = 0; i < num; i++) { tags[i] = getTag(res[i], Y, Y + HEIGHT); }
        tags[num] = c1tag;
        Arrays.sort(tags);
        return ((num >= 1 && tags[0] * tags[1] <= 0) || (num >= 3 && tags[2] * tags[3] <= 0));
    }

    public boolean contains(Point POINT) { return contains(POINT.x, POINT.y); }
    public boolean contains(double X, double Y) {
        if (!(Double.compare((X * 0.0 + Y * 0.0), 0) == 0)) { return false; }
        int crossings = (Shape.pointCrossingsForLine(X, Y, x1, y1, x2, y2) + Shape.pointCrossingsForCubic(X, Y, x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2, 0));
        return ((crossings & 1) == 1);
    }
    public boolean contains(double X, double Y, double WIDTH, double HEIGHT) {
        if (WIDTH <= 0 || HEIGHT <= 0) { return false; }

        if (!(contains(X, Y) && contains(X + WIDTH, Y) && contains(X + WIDTH, Y + HEIGHT) && contains(X, Y + HEIGHT))) { return false; }
        return !Shape.intersectsLine(X, Y, WIDTH, HEIGHT, x1, y1, x2, y2);
    }

    public PathIterator getPathIterator(BaseTransform TRANSFORM) { return new BezierCurveIterator(this, TRANSFORM); }
    public PathIterator getPathIterator(BaseTransform TRANSFORM, double FLATNESS) {
        return new FlatteningPathIterator(getPathIterator(TRANSFORM), FLATNESS);
    }

    private double calcX(final double T) {
        final double u = 1 - T;
        return (u * u * u * x1 + 3 * (T * u * u * ctrlx1 + T * T * u * ctrlx2) + T * T * T * x2);
    }
    private double calcY(final double T) {
        final double u = 1 - T;
        return (u * u * u * y1 + 3 * (T * u * u * ctrly1 + T * T * u * ctrly2) + T * T * T * y2);
    }

    @Override public BezierCurve copy() { return new BezierCurve(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2); }

    @Override public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj instanceof BezierCurve) {
            BezierCurve curve = (BezierCurve) obj;
            return ((x1 == curve.x1) &&
                    (y1 == curve.y1) &&
                    (x2 == curve.x2) &&
                    (y2 == curve.y2) &&
                    (ctrlx1 == curve.ctrlx1) &&
                    (ctrly1 == curve.ctrly1) &&
                    (ctrlx2 == curve.ctrlx2) &&
                    (ctrly2 == curve.ctrly2));
        }
        return false;
    }
}
