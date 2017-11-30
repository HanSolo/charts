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


public final class Identity extends BaseTransform {
    @Override public Degree getDegree() {
        return Degree.IDENTITY;
    }

    @Override public int getType() {
        return TYPE_IDENTITY;
    }

    @Override public boolean isIdentity() {
        return true;
    }

    @Override public boolean isTranslateOrIdentity() {
        return true;
    }

    @Override public double getDeterminant() {
        return 1.0;
    }

    @Override public Point transform(Point src, Point dst) {
        if (dst == null) dst = makePoint(src, dst);
        dst.set(src);
        return dst;
    }

    @Override public Point inverseTransform(Point src, Point dst) {
        if (dst == null) dst = makePoint(src, dst);
        dst.set(src);
        return dst;
    }

    public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        if (srcPts != dstPts || srcOff != dstOff) { System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2); }
    }

    @Override public void deltaTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        if (srcPts != dstPts || srcOff != dstOff) { System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2); }
    }

    public void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        if (srcPts != dstPts || srcOff != dstOff) { System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2); }
    }

    public void inverseDeltaTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        if (srcPts != dstPts || srcOff != dstOff) { System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2); }
    }

    @Override public BaseBounds transform(BaseBounds bounds, BaseBounds result) {
        if (result != bounds) { result = result.deriveWithNewBounds(bounds); }
        return result;
    }

    @Override public void transform(Rectangle rect, Rectangle result) {
        if (result != rect) { result.setBounds(rect); }
    }

    @Override public BaseBounds inverseTransform(BaseBounds bounds, BaseBounds result) {
        if (result != bounds) { result = result.deriveWithNewBounds(bounds); }
        return result;
    }

    @Override public void inverseTransform(Rectangle rect, Rectangle result) {
        if (result != rect) { result.setBounds(rect); }
    }

    @Override public Shape createTransformedShape(Shape s) { return new Path(s); }

    @Override public void setToIdentity() { }

    @Override public void setTransform(BaseTransform xform) { if (!xform.isIdentity()) { degreeError(Degree.IDENTITY); } }

    @Override public void invert() {
    }

    @Override public void restoreTransform(double mxx, double myx,
                                           double mxy, double myy,
                                           double mxt, double myt) {
        if (mxx != 1.0 || myx != 0.0 ||
            mxy != 0.0 || myy != 1.0 ||
            mxt != 0.0 || myt != 0.0)
        {
            degreeError(Degree.IDENTITY);
        }
    }

    @Override public void restoreTransform(double mxx, double mxy, double mxz, double mxt,
                                           double myx, double myy, double myz, double myt,
                                           double mzx, double mzy, double mzz, double mzt) {
        if (mxx != 1.0 || mxy != 0.0 || mxz != 0.0 || mxt != 0.0 ||
            myx != 0.0 || myy != 1.0 || myz != 0.0 || myt != 0.0 ||
            mzx != 0.0 || mzy != 0.0 || mzz != 1.0 || mzt != 0.0)
        {
            degreeError(Degree.IDENTITY);
        }
    }

    @Override public BaseTransform deriveWithTranslation(double mxt, double myt) { return Translate.getInstance(mxt, myt); }

    @Override public BaseTransform deriveWithPreTranslation(double mxt, double myt) { return Translate.getInstance(mxt, myt); }

    @Override public BaseTransform deriveWithConcatenation(double mxx, double myx, double mxy, double myy, double mxt, double myt) {
        return getInstance(mxx, myx, mxy, myy, mxt, myt);
    }

    @Override public BaseTransform deriveWithConcatenation(BaseTransform tx) { return getInstance(tx); }

    @Override public BaseTransform deriveWithPreConcatenation(BaseTransform tx) { return getInstance(tx); }

    @Override public BaseTransform deriveWithNewTransform(BaseTransform tx) { return getInstance(tx); }

    @Override public BaseTransform createInverse() { return this; }

    @Override public BaseTransform copy() { return this; }

    @Override public boolean equals(Object obj) {
        return (obj instanceof BaseTransform &&
                ((BaseTransform) obj).isIdentity());
    }
}