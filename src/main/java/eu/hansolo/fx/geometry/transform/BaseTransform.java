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

package eu.hansolo.fx.geometry.transform;

import eu.hansolo.fx.geometry.BaseBounds;
import eu.hansolo.fx.geometry.Rectangle;
import eu.hansolo.fx.geometry.Shape;
import eu.hansolo.fx.geometry.tools.NonInvertibleTransformException;
import eu.hansolo.fx.geometry.tools.Point;


public abstract class BaseTransform {
    public static final BaseTransform IDENTITY_TRANSFORM = new Identity();

    public enum Degree {
        IDENTITY, TRANSLATE, AFFINE
    }

    protected static final int TYPE_UNKNOWN        = -1;

    public static final int TYPE_IDENTITY          = 0;
    public static final int TYPE_TRANSLATION       = 1;
    public static final int TYPE_UNIFORM_SCALE     = 2;
    public static final int TYPE_GENERAL_SCALE     = 4;
    public static final int TYPE_MASK_SCALE        = (TYPE_UNIFORM_SCALE | TYPE_GENERAL_SCALE);
    public static final int TYPE_FLIP              = 64;
    public static final int TYPE_QUADRANT_ROTATION = 8;
    public static final int TYPE_GENERAL_ROTATION  = 16;
    public static final int TYPE_MASK_ROTATION     = (TYPE_QUADRANT_ROTATION | TYPE_GENERAL_ROTATION);
    public static final int TYPE_GENERAL_TRANSFORM = 32;
    public static final int TYPE_AFFINE_3D         = 128;
    public static final int TYPE_AFFINE2D_MASK     =
        (TYPE_TRANSLATION |
         TYPE_UNIFORM_SCALE |
         TYPE_GENERAL_SCALE |
         TYPE_QUADRANT_ROTATION |
         TYPE_GENERAL_ROTATION |
         TYPE_GENERAL_TRANSFORM |
         TYPE_FLIP);


    static void degreeError(Degree maxSupported) {
        throw new InternalError("does not support higher than " + maxSupported+" operations");
    }

    public static BaseTransform getInstance(BaseTransform tx) {
        if (tx.isIdentity()) {
            return IDENTITY_TRANSFORM;
        } else if (tx.isTranslateOrIdentity()) {
            return new Translate(tx);
        } else {
            return new Affine(tx);
        }
    }


    public static BaseTransform getInstance(double mxx, double myx, double mxy, double myy, double mxt, double myt) {
        if (mxx == 1.0 && myx == 0.0 && mxy == 0.0 && myy == 1.0) {
            return getTranslateInstance(mxt, myt);
        } else {
            return new Affine(mxx, myx, mxy, myy, mxt, myt);
        }
    }

    public static BaseTransform getTranslateInstance(double mxt, double myt) {
        if (mxt == 0.0 && myt == 0.0) {
            return IDENTITY_TRANSFORM;
        } else {
            return new Translate(mxt, myt);
        }
    }

    public static BaseTransform getScaleInstance(double mxx, double myy) {
        return getInstance(mxx, 0, 0, myy, 0, 0);
    }

    public static BaseTransform getRotateInstance(double theta, double x, double y) {
        Affine a = new Affine();
        a.setToRotation(theta, x, y);
        return a;
    }

    public abstract Degree getDegree();

    public abstract int getType();

    public abstract boolean isIdentity();
    public abstract boolean isTranslateOrIdentity();

    public abstract double getDeterminant();

    public double getMxx() { return 1.0; }
    public double getMxy() { return 0.0; }
    public double getMxz() { return 0.0; }
    public double getMxt() { return 0.0; }

    public double getMyx() { return 0.0; }
    public double getMyy() { return 1.0; }
    public double getMyz() { return 0.0; }
    public double getMyt() { return 0.0; }

    public double getMzx() { return 0.0; }
    public double getMzy() { return 0.0; }
    public double getMzz() { return 1.0; }
    public double getMzt() { return 0.0; }

    public abstract Point transform(Point src, Point dst);

    public abstract Point inverseTransform(Point src, Point dst) throws NonInvertibleTransformException;

    public abstract void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts);

    public abstract void deltaTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts);

    public abstract void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) throws NonInvertibleTransformException;

    public abstract void inverseDeltaTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) throws NonInvertibleTransformException;

    public abstract BaseBounds transform(BaseBounds bounds, BaseBounds result);

    public abstract void transform(Rectangle rect, Rectangle result);

    public abstract BaseBounds inverseTransform(BaseBounds bounds, BaseBounds result) throws NonInvertibleTransformException;

    public abstract void inverseTransform(Rectangle rect, Rectangle result) throws NonInvertibleTransformException;

    public abstract Shape createTransformedShape(Shape s);

    public abstract void setToIdentity();

    public abstract void setTransform(BaseTransform xform);

    public abstract void invert() throws NonInvertibleTransformException;

    public abstract void restoreTransform(double mxx, double myx, double mxy, double myy, double mxt, double myt);

    public abstract void restoreTransform(double mxx, double mxy, double mxz, double mxt,
                                          double myx, double myy, double myz, double myt,
                                          double mzx, double mzy, double mzz, double mzt);

    public abstract BaseTransform deriveWithTranslation(double mxt, double myt);

    public abstract BaseTransform deriveWithPreTranslation(double mxt, double myt);

    public abstract BaseTransform deriveWithConcatenation(double mxx, double myx,
                                                          double mxy, double myy,
                                                          double mxt, double myt);

    public abstract BaseTransform deriveWithPreConcatenation(BaseTransform transform);

    public abstract BaseTransform deriveWithConcatenation(BaseTransform tx);

    public abstract BaseTransform deriveWithNewTransform(BaseTransform tx);

    public abstract BaseTransform createInverse() throws NonInvertibleTransformException;

    public abstract BaseTransform copy();

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof BaseTransform)) {
            return false;
        }

        BaseTransform a = (BaseTransform) obj;

        return (getMxx() == a.getMxx() &&
                getMxy() == a.getMxy() &&
                getMxz() == a.getMxz() &&
                getMxt() == a.getMxt() &&
                getMyx() == a.getMyx() &&
                getMyy() == a.getMyy() &&
                getMyz() == a.getMyz() &&
                getMyt() == a.getMyt() &&
                getMzx() == a.getMzx() &&
                getMzy() == a.getMzy() &&
                getMzz() == a.getMzz() &&
                getMzt() == a.getMzt());
    }

    static Point makePoint(Point src, Point dst) {
        if (dst == null) {
            dst = new Point();
        }
        return dst;
    }
}