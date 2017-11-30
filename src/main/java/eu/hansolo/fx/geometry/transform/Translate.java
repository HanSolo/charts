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
import eu.hansolo.fx.geometry.Path;
import eu.hansolo.fx.geometry.Rectangle;
import eu.hansolo.fx.geometry.Shape;
import eu.hansolo.fx.geometry.tools.Point;


public class Translate extends BaseTransform {
    private double mxt;
    private double myt;

    public static BaseTransform getInstance(double mxt, double myt) {
        if (mxt == 0.0 && myt == 0.0) {
            return IDENTITY_TRANSFORM;
        } else {
            return new Translate(mxt, myt);
        }
    }

    public Translate(double tx, double ty) {
        this.mxt = tx;
        this.myt = ty;
    }
    public Translate(BaseTransform tx) {
        if (!tx.isTranslateOrIdentity()) {
            degreeError(Degree.TRANSLATE);
        }
        this.mxt = tx.getMxt();
        this.myt = tx.getMyt();
    }

    @Override public Degree getDegree() {
        return Degree.TRANSLATE;
    }

    @Override public double getDeterminant() {
        return 1.0;
    }

    @Override public double getMxt() {
        return mxt;
    }

    @Override public double getMyt() {
        return myt;
    }

    @Override public int getType() {
        return (mxt == 0.0 && myt == 0.0) ? TYPE_IDENTITY : TYPE_TRANSLATION;
    }

    @Override public boolean isIdentity() {
        return (mxt == 0.0 && myt == 0.0);
    }

    @Override public boolean isTranslateOrIdentity() {
        return true;
    }

    @Override public Point transform(Point src, Point dst) {
        if (dst == null) dst = makePoint(src, dst);
        dst.set(
            (src.x + mxt),
            (src.y + myt));
        return dst;
    }

    @Override public Point inverseTransform(Point src, Point dst) {
        if (dst == null) dst = makePoint(src, dst);
        dst.set(
            (src.x - mxt),
            (src.y - myt));
        return dst;
    }

    @Override public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        double tx = this.mxt;
        double ty = this.myt;
        if (dstPts == srcPts) {
            if (dstOff > srcOff && dstOff < srcOff + numPts * 2) {
                System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2);
                srcOff = dstOff;
            }
            if (Double.compare(dstOff, srcOff) == 0.0 && Double.compare(tx, 0) == 0.0 && Double.compare(ty, 0) == 0.0) { return; }
        }
        for (int i = 0; i < numPts; i++) {
            dstPts[dstOff++] = srcPts[srcOff++] + tx;
            dstPts[dstOff++] = srcPts[srcOff++] + ty;
        }
    }

    @Override public void deltaTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        if (srcPts != dstPts || srcOff != dstOff) { System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2); }
    }

    @Override public void inverseDeltaTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        if (srcPts != dstPts || srcOff != dstOff) { System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2); }
    }

    @Override public void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        double tx = this.mxt;
        double ty = this.myt;
        if (dstPts == srcPts) {
            if (dstOff > srcOff && dstOff < srcOff + numPts * 2) {
                System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2);
                srcOff = dstOff;
            }
            if (Double.compare(dstOff, srcOff) == 0.0 && Double.compare(tx, 0) == 0.0 && Double.compare(ty, 0) == 0.0) {
                return;
            }
        }
        for (int i = 0; i < numPts; i++) {
            dstPts[dstOff++] = srcPts[srcOff++] - tx;
            dstPts[dstOff++] = srcPts[srcOff++] - ty;
        }
    }

    @Override public BaseBounds transform(BaseBounds bounds, BaseBounds result) {
        double minX = (bounds.getMinX() + mxt);
        double minY = (bounds.getMinY() + myt);
        double maxX = (bounds.getMaxX() + mxt);
        double maxY = (bounds.getMaxY() + myt);
        return result.deriveWithNewBounds(minX, minY, maxX, maxY);
    }
    @Override public void transform(Rectangle rect, Rectangle result) { transform(rect, result, mxt, myt); }

    @Override public BaseBounds inverseTransform(BaseBounds bounds, BaseBounds result) {
        double minX = (bounds.getMinX() - mxt);
        double minY = (bounds.getMinY() - myt);
        double maxX = (bounds.getMaxX() - mxt);
        double maxY = (bounds.getMaxY() - myt);
        return result.deriveWithNewBounds(minX, minY, maxX, maxY);
    }

    @Override public void inverseTransform(Rectangle rect, Rectangle result) {
        transform(rect, result, -mxt, -myt);
    }

    static void transform(Rectangle rect, Rectangle result, double mxt, double myt) {
        int imxt = (int) mxt;
        int imyt = (int) myt;
        if (imxt == mxt && imyt == myt) {
            result.setBounds(rect);
            result.translate(imxt, imyt);
        } else {
            double x1 = rect.x + mxt;
            double y1 = rect.y + myt;
            double x2 = Math.ceil(x1 + rect.width);
            double y2 = Math.ceil(y1 + rect.height);
            x1 = Math.floor(x1);
            y1 = Math.floor(y1);
            result.setBounds((int) x1, (int) y1, (int) (x2 - x1), (int) (y2 - y1));
        }
    }

    @Override public Shape createTransformedShape(Shape s) { return new Path(s, this); }

    @Override public void setToIdentity() { this.mxt = this.myt = 0.0; }

    @Override public void setTransform(BaseTransform xform) {
        if (!xform.isTranslateOrIdentity()) { degreeError(Degree.TRANSLATE); }
        this.mxt = xform.getMxt();
        this.myt = xform.getMyt();
    }

    @Override public void invert() {
        this.mxt = -this.mxt;
        this.myt = -this.myt;
    }

    @Override public void restoreTransform(double mxx, double myx, double mxy, double myy, double mxt, double myt) {
        if (mxx != 1.0 || myx != 0.0 || mxy != 0.0 || myy != 1.0) { degreeError(Degree.TRANSLATE); }
        this.mxt = mxt;
        this.myt = myt;
    }

    @Override public void restoreTransform(double mxx, double mxy, double mxz, double mxt,
                                 double myx, double myy, double myz, double myt,
                                 double mzx, double mzy, double mzz, double mzt) {
        if (mxx != 1.0 || mxy != 0.0 || mxz != 0.0 ||
            myx != 0.0 || myy != 1.0 || myz != 0.0 ||
            mzx != 0.0 || mzy != 0.0 || mzz != 1.0 || mzt != 0.0) {
            degreeError(Degree.TRANSLATE);
        }
        this.mxt = mxt;
        this.myt = myt;
    }

    @Override public BaseTransform deriveWithTranslation(double mxt, double myt) {
        this.mxt += mxt;
        this.myt += myt;
        return this;
    }

    @Override public BaseTransform deriveWithPreTranslation(double mxt, double myt) {
        this.mxt += mxt;
        this.myt += myt;
        return this;
    }

    @Override public BaseTransform deriveWithConcatenation(double mxx, double myx, double mxy, double myy, double mxt, double myt) {
        if (mxx == 1.0 && myx == 0.0 && mxy == 0.0 && myy == 1.0) {
            this.mxt += mxt;
            this.myt += myt;
            return this;
        } else {
            return new Affine(mxx, myx, mxy, myy, this.mxt + mxt, this.myt + myt);
        }
    }
    @Override public BaseTransform deriveWithConcatenation(BaseTransform tx) {
        if (tx.isTranslateOrIdentity()) {
            this.mxt += tx.getMxt();
            this.myt += tx.getMyt();
            return this;
        } else {
            return getInstance(tx.getMxx(), tx.getMyx(), tx.getMxy(), tx.getMyy(),
                               this.mxt + tx.getMxt(), this.myt + tx.getMyt());
        }
    }

    @Override public BaseTransform deriveWithPreConcatenation(BaseTransform tx) {
        if (tx.isTranslateOrIdentity()) {
            this.mxt += tx.getMxt();
            this.myt += tx.getMyt();
            return this;
        } else {
            Affine t2d = new Affine(tx);
            t2d.translate(this.mxt, this.myt);
            return t2d;
        }
    }

    @Override public BaseTransform deriveWithNewTransform(BaseTransform tx) {
        if (tx.isTranslateOrIdentity()) {
            this.mxt = tx.getMxt();
            this.myt = tx.getMyt();
            return this;
        } else {
            return getInstance(tx);
        }
    }

    @Override public BaseTransform createInverse() { return isIdentity() ? IDENTITY_TRANSFORM : new Translate(-this.mxt, -this.myt); }

    private static double _matRound(double value) { return Math.rint(value * 1E15) / 1E15; }

    @Override public BaseTransform copy() { return new Translate(this.mxt, this.myt); }

    @Override public boolean equals(Object obj) {
        if (obj instanceof BaseTransform) {
            BaseTransform tx = (BaseTransform) obj;
            return (tx.isTranslateOrIdentity() &&
                    tx.getMxt() == this.mxt &&
                    tx.getMyt() == this.myt);
        }
        return false;
    }
}