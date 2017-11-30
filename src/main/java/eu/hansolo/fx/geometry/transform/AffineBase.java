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
import eu.hansolo.fx.geometry.RectBounds;
import eu.hansolo.fx.geometry.Rectangle;
import eu.hansolo.fx.geometry.Shape;
import eu.hansolo.fx.geometry.tools.NonInvertibleTransformException;
import eu.hansolo.fx.geometry.tools.Point;


public abstract class AffineBase extends BaseTransform {
    protected static final int APPLY_IDENTITY  = 0;
    protected static final int APPLY_TRANSLATE = 1;
    protected static final int APPLY_SCALE     = 2;
    protected static final int APPLY_SHEAR     = 4;
    protected static final int APPLY_2D_MASK = (APPLY_TRANSLATE | APPLY_SCALE | APPLY_SHEAR);
    protected static final int APPLY_2D_DELTA_MASK = (APPLY_SCALE | APPLY_SHEAR);
    protected static final int HI_SHIFT = 4;
    protected static final int HI_IDENTITY = APPLY_IDENTITY << HI_SHIFT;
    protected static final int HI_TRANSLATE = APPLY_TRANSLATE << HI_SHIFT;
    protected static final int HI_SCALE = APPLY_SCALE << HI_SHIFT;
    protected static final int HI_SHEAR = APPLY_SHEAR << HI_SHIFT;
    protected double mxx;
    protected double myx;
    protected double mxy;
    protected double myy;
    protected double mxt;
    protected double myt;
    protected transient int state;
    protected transient int type;
    private static final int rot90conversion[] = {
        /* IDENTITY => */        APPLY_SHEAR,
        /* TRANSLATE (TR) => */  APPLY_SHEAR | APPLY_TRANSLATE,
        /* SCALE (SC) => */      APPLY_SHEAR,
        /* SC | TR => */         APPLY_SHEAR | APPLY_TRANSLATE,
        /* SHEAR (SH) => */      APPLY_SCALE,
        /* SH | TR => */         APPLY_SCALE | APPLY_TRANSLATE,
        /* SH | SC => */         APPLY_SHEAR | APPLY_SCALE,
        /* SH | SC | TR => */    APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE,
        };


    protected static void stateError() { throw new InternalError("missing case in transform state switch"); }

    protected void updateState() {
        if (mxy == 0.0 && myx == 0.0) {
            if (mxx == 1.0 && myy == 1.0) {
                if (mxt == 0.0 && myt == 0.0) {
                    state = APPLY_IDENTITY;
                    type = TYPE_IDENTITY;
                } else {
                    state = APPLY_TRANSLATE;
                    type = TYPE_TRANSLATION;
                }
            } else {
                if (mxt == 0.0 && myt == 0.0) {
                    state = APPLY_SCALE;
                } else {
                    state = (APPLY_SCALE | APPLY_TRANSLATE);
                }
                type = TYPE_UNKNOWN;
            }
        } else {
            if (mxx == 0.0 && myy == 0.0) {
                if (mxt == 0.0 && myt == 0.0) {
                    state = APPLY_SHEAR;
                } else {
                    state = (APPLY_SHEAR | APPLY_TRANSLATE);
                }
            } else {
                if (mxt == 0.0 && myt == 0.0) {
                    state = (APPLY_SHEAR | APPLY_SCALE);
                } else {
                    state = (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE);
                }
            }
            type = TYPE_UNKNOWN;
        }
    }
    
    public int getType() {
        if (type == TYPE_UNKNOWN) {
            updateState();
            if (type == TYPE_UNKNOWN) { type = calculateType(); }
        }
        return type;
    }

    protected int calculateType() {
        int ret = TYPE_IDENTITY;
        boolean sgn0, sgn1;
        switch (state & APPLY_2D_MASK) {
            default:
                stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                ret |= TYPE_TRANSLATION;
            case (APPLY_SHEAR | APPLY_SCALE):
                if (mxx * mxy + myx * myy != 0) {
                    ret |= TYPE_GENERAL_TRANSFORM;
                    break;
                }
                sgn0 = (mxx >= 0.0);
                sgn1 = (myy >= 0.0);
                if (sgn0 == sgn1) {
                    if (mxx != myy || mxy != -myx) {
                        ret |= (TYPE_GENERAL_ROTATION | TYPE_GENERAL_SCALE);
                    } else if (mxx * myy - mxy * myx != 1.0) {
                        ret |= (TYPE_GENERAL_ROTATION | TYPE_UNIFORM_SCALE);
                    } else {
                        ret |= TYPE_GENERAL_ROTATION;
                    }
                } else {
                    if (mxx != -myy || mxy != myx) {
                        ret |= (TYPE_GENERAL_ROTATION |
                                TYPE_FLIP |
                                TYPE_GENERAL_SCALE);
                    } else if (mxx * myy - mxy * myx != 1.0) {
                        ret |= (TYPE_GENERAL_ROTATION |
                                TYPE_FLIP |
                                TYPE_UNIFORM_SCALE);
                    } else {
                        ret |= (TYPE_GENERAL_ROTATION | TYPE_FLIP);
                    }
                }
                break;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                ret |= TYPE_TRANSLATION;
            case (APPLY_SHEAR):
                sgn0 = (mxy >= 0.0);
                sgn1 = (myx >= 0.0);
                if (sgn0 != sgn1) {
                    if (mxy != -myx) {
                        ret |= (TYPE_QUADRANT_ROTATION | TYPE_GENERAL_SCALE);
                    } else if (mxy != 1.0 && mxy != -1.0) {
                        ret |= (TYPE_QUADRANT_ROTATION | TYPE_UNIFORM_SCALE);
                    } else {
                        ret |= TYPE_QUADRANT_ROTATION;
                    }
                } else {
                    if (mxy == myx) {
                        ret |= (TYPE_QUADRANT_ROTATION | TYPE_FLIP | TYPE_UNIFORM_SCALE);
                    } else {
                        ret |= (TYPE_QUADRANT_ROTATION | TYPE_FLIP | TYPE_GENERAL_SCALE);
                    }
                }
                break;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                ret |= TYPE_TRANSLATION;
            case (APPLY_SCALE):
                sgn0 = (mxx >= 0.0);
                sgn1 = (myy >= 0.0);
                if (sgn0 == sgn1) {
                    if (sgn0) {
                        if (mxx == myy) {
                            ret |= TYPE_UNIFORM_SCALE;
                        } else {
                            ret |= TYPE_GENERAL_SCALE;
                        }
                    } else {
                        if (mxx != myy) {
                            ret |= (TYPE_QUADRANT_ROTATION | TYPE_GENERAL_SCALE);
                        } else if (mxx != -1.0) {
                            ret |= (TYPE_QUADRANT_ROTATION | TYPE_UNIFORM_SCALE);
                        } else {
                            ret |= TYPE_QUADRANT_ROTATION;
                        }
                    }
                } else {
                    if (mxx == -myy) {
                        if (mxx == 1.0 || mxx == -1.0) {
                            ret |= TYPE_FLIP;
                        } else {
                            ret |= (TYPE_FLIP | TYPE_UNIFORM_SCALE);
                        }
                    } else {
                        ret |= (TYPE_FLIP | TYPE_GENERAL_SCALE);
                    }
                }
                break;
            case (APPLY_TRANSLATE):
                ret |= TYPE_TRANSLATION;
                break;
            case (APPLY_IDENTITY):
                break;
        }
        return ret;
    }

    @Override public double getMxx() { return mxx; }
    @Override public double getMyy() { return myy; }
    @Override public double getMxy() { return mxy; }
    @Override public double getMyx() { return myx; }
    @Override public double getMxt() { return mxt; }
    @Override public double getMyt() { return myt; }

    public boolean isIdentity() {
        return (state == APPLY_IDENTITY || (getType() == TYPE_IDENTITY));
    }

    @Override public boolean isTranslateOrIdentity() {
        return (state <= APPLY_TRANSLATE || (getType() <= TYPE_TRANSLATION));
    }

    public double getDeterminant() {
        switch (state) {
            default                                           : stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
            case (APPLY_SHEAR | APPLY_SCALE)                  : return mxx * myy - mxy * myx;
            case (APPLY_SHEAR | APPLY_TRANSLATE)              :
            case (APPLY_SHEAR)                                : return -(mxy * myx);
            case (APPLY_SCALE | APPLY_TRANSLATE)              :
            case (APPLY_SCALE)                                : return mxx * myy;
            case (APPLY_TRANSLATE)                            :
            case (APPLY_IDENTITY)                             : return 1.0;
        }
    }
    
    public void setToIdentity() {
        mxx = myy = 1.0;
        myx = mxy = mxt = myt = 0.0;
        state = APPLY_IDENTITY;
        type = TYPE_IDENTITY;
    }

    public void setTransform(double mxx, double myx, double mxy, double myy, double mxt, double myt) {
        this.mxx = mxx;
        this.myx = myx;
        this.mxy = mxy;
        this.myy = myy;
        this.mxt = mxt;
        this.myt = myt;
        updateState();
    }

    public void setToShear(double shx, double shy) {
        mxx = 1.0;
        mxy = shx;
        myx = shy;
        myy = 1.0;
        mxt = 0.0;
        myt = 0.0;
        if (shx != 0.0 || shy != 0.0) {
            state = (APPLY_SHEAR | APPLY_SCALE);
            type = TYPE_UNKNOWN;
        } else {
            state = APPLY_IDENTITY;
            type = TYPE_IDENTITY;
        }
    }

    public Point transform(Point pt) { return transform(pt, pt); }
    public Point transform(Point ptSrc, Point ptDst) {
        if (ptDst == null) { ptDst = new Point(); }
        double x = ptSrc.x;
        double y = ptSrc.y;
        switch (state & APPLY_2D_MASK) {
            default:
                stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                ptDst.set((x * mxx + y * mxy + mxt), (x * myx + y * myy + myt));
                return ptDst;
            case (APPLY_SHEAR | APPLY_SCALE):
                ptDst.set((x * mxx + y * mxy), (x * myx + y * myy));
                return ptDst;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                ptDst.set((y * mxy + mxt), (x * myx + myt));
                return ptDst;
            case (APPLY_SHEAR):
                ptDst.set((y * mxy), (x * myx));
                return ptDst;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                ptDst.set((x * mxx + mxt), (y * myy + myt));
                return ptDst;
            case (APPLY_SCALE):
                ptDst.set((x * mxx), (y * myy));
                return ptDst;
            case (APPLY_TRANSLATE):
                ptDst.set((x + mxt), (y + myt));
                return ptDst;
            case (APPLY_IDENTITY):
                ptDst.set(x, y);
                return ptDst;
        }
    }
    public BaseBounds transform(BaseBounds src, BaseBounds dst) {
        return transform2DBounds((RectBounds)src, (RectBounds)dst);
    }
    public void transform(Rectangle src, Rectangle dst) {
        switch (state & APPLY_2D_MASK) {
            default:
                stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
            case (APPLY_SHEAR | APPLY_SCALE):
            case (APPLY_SHEAR | APPLY_TRANSLATE):
            case (APPLY_SHEAR):
            case (APPLY_SCALE | APPLY_TRANSLATE):
            case (APPLY_SCALE):
                RectBounds b = new RectBounds(src);
                b = (RectBounds) transform(b, b);
                dst.setBounds(b);
                return;
            case (APPLY_TRANSLATE):
                Translate.transform(src, dst, mxt, myt);
                return;
            case (APPLY_IDENTITY):
                if (dst != src) { dst.setBounds(src); }
                return;
        }
    }
    public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        double Mxx, Mxy, Mxt, Myx, Myy, Myt;    // For caching
        switch (state & APPLY_2D_MASK) {
            default:
                stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                Mxx = mxx; Mxy = mxy; Mxt = mxt;
                Myx = myx; Myy = myy; Myt = myt;
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++];
                    double y = srcPts[srcOff++];
                    dstPts[dstOff++] = (Mxx * x + Mxy * y + Mxt);
                    dstPts[dstOff++] = (Myx * x + Myy * y + Myt);
                }
                return;
            case (APPLY_SHEAR | APPLY_SCALE):
                Mxx = mxx; Mxy = mxy;
                Myx = myx; Myy = myy;
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++];
                    double y = srcPts[srcOff++];
                    dstPts[dstOff++] = (Mxx * x + Mxy * y);
                    dstPts[dstOff++] = (Myx * x + Myy * y);
                }
                return;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                Mxy = mxy; Mxt = mxt;
                Myx = myx; Myt = myt;
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++];
                    dstPts[dstOff++] = (Mxy * srcPts[srcOff++] + Mxt);
                    dstPts[dstOff++] = (Myx * x + Myt);
                }
                return;
            case (APPLY_SHEAR):
                Mxy = mxy; Myx = myx;
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++];
                    dstPts[dstOff++] = (Mxy * srcPts[srcOff++]);
                    dstPts[dstOff++] = (Myx * x);
                }
                return;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                Mxx = mxx; Mxt = mxt;
                Myy = myy; Myt = myt;
                while (--numPts >= 0) {
                    dstPts[dstOff++] = (Mxx * srcPts[srcOff++] + Mxt);
                    dstPts[dstOff++] = (Myy * srcPts[srcOff++] + Myt);
                }
                return;
            case (APPLY_SCALE):
                Mxx = mxx; Myy = myy;
                while (--numPts >= 0) {
                    dstPts[dstOff++] = (Mxx * srcPts[srcOff++]);
                    dstPts[dstOff++] = (Myy * srcPts[srcOff++]);
                }
                return;
            case (APPLY_TRANSLATE):
                Mxt = mxt; Myt = myt;
                while (--numPts >= 0) {
                    dstPts[dstOff++] = (srcPts[srcOff++] + Mxt);
                    dstPts[dstOff++] = (srcPts[srcOff++] + Myt);
                }
                return;
            case (APPLY_IDENTITY):
                while (--numPts >= 0) {
                    dstPts[dstOff++] = (srcPts[srcOff++]);
                    dstPts[dstOff++] = (srcPts[srcOff++]);
                }
                return;
        }
    }

    private BaseBounds transform2DBounds(RectBounds src, RectBounds dst) {
        switch (state & APPLY_2D_MASK) {
            default:
                stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
            case (APPLY_SHEAR | APPLY_SCALE):
                double x1 = src.getMinX();
                double y1 = src.getMinY();
                double x2 = src.getMaxX();
                double y2 = src.getMaxY();
                dst.setBoundsAndSort((x1 * mxx + y1 * mxy), (x1 * myx + y1 * myy), (x2 * mxx + y2 * mxy), (x2 * myx + y2 * myy));
                dst.add((x1 * mxx + y2 * mxy), (x1 * myx + y2 * myy));
                dst.add((x2 * mxx + y1 * mxy), (x2 * myx + y1 * myy));
                dst.setBounds((dst.getMinX() + mxt), (dst.getMinY() + myt), (dst.getMaxX() + mxt), (dst.getMaxY() + myt));
                break;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                dst.setBoundsAndSort((src.getMinY() * mxy + mxt), (src.getMinX() * myx + myt), (src.getMaxY() * mxy + mxt), (src.getMaxX() * myx + myt));
                break;
            case (APPLY_SHEAR):
                dst.setBoundsAndSort((src.getMinY() * mxy), (src.getMinX() * myx), (src.getMaxY() * mxy), (src.getMaxX() * myx));
                break;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                dst.setBoundsAndSort((src.getMinX() * mxx + mxt), (src.getMinY() * myy + myt), (src.getMaxX() * mxx + mxt), (src.getMaxY() * myy + myt));
                break;
            case (APPLY_SCALE):
                dst.setBoundsAndSort((src.getMinX() * mxx), (src.getMinY() * myy), (src.getMaxX() * mxx), (src.getMaxY() * myy));
                break;
            case (APPLY_TRANSLATE):
                dst.setBounds((src.getMinX() + mxt), (src.getMinY() + myt), (src.getMaxX() + mxt), (src.getMaxY() + myt));
                break;
            case (APPLY_IDENTITY):
                if (src != dst) { dst.setBounds(src); }
                break;
        }
        return dst;
    }

    public void deltaTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        doTransform(srcPts, srcOff, dstPts, dstOff, numPts, (this.state & APPLY_2D_DELTA_MASK));
    }

    private void doTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts, int thestate) {
        double Mxx, Mxy, Mxt, Myx, Myy, Myt;    // For caching
        if (dstPts == srcPts && dstOff > srcOff && dstOff < srcOff + numPts * 2) {
            System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2);
            srcOff = dstOff;
        }
        switch (thestate) {
            default:
                stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                Mxx = mxx; Mxy = mxy; Mxt = mxt;
                Myx = myx; Myy = myy; Myt = myt;
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++];
                    double y = srcPts[srcOff++];
                    dstPts[dstOff++] = (Mxx * x + Mxy * y + Mxt);
                    dstPts[dstOff++] = (Myx * x + Myy * y + Myt);
                }
                return;
            case (APPLY_SHEAR | APPLY_SCALE):
                Mxx = mxx; Mxy = mxy;
                Myx = myx; Myy = myy;
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++];
                    double y = srcPts[srcOff++];
                    dstPts[dstOff++] = (Mxx * x + Mxy * y);
                    dstPts[dstOff++] = (Myx * x + Myy * y);
                }
                return;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                Mxy = mxy; Mxt = mxt;
                Myx = myx; Myt = myt;
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++];
                    dstPts[dstOff++] = (Mxy * srcPts[srcOff++] + Mxt);
                    dstPts[dstOff++] = (Myx * x + Myt);
                }
                return;
            case (APPLY_SHEAR):
                Mxy = mxy; Myx = myx;
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++];
                    dstPts[dstOff++] = (Mxy * srcPts[srcOff++]);
                    dstPts[dstOff++] = (Myx * x);
                }
                return;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                Mxx = mxx; Mxt = mxt;
                Myy = myy; Myt = myt;
                while (--numPts >= 0) {
                    dstPts[dstOff++] = (Mxx * srcPts[srcOff++] + Mxt);
                    dstPts[dstOff++] = (Myy * srcPts[srcOff++] + Myt);
                }
                return;
            case (APPLY_SCALE):
                Mxx = mxx; Myy = myy;
                while (--numPts >= 0) {
                    dstPts[dstOff++] = (Mxx * srcPts[srcOff++]);
                    dstPts[dstOff++] = (Myy * srcPts[srcOff++]);
                }
                return;
            case (APPLY_TRANSLATE):
                Mxt = mxt; Myt = myt;
                while (--numPts >= 0) {
                    dstPts[dstOff++] = (srcPts[srcOff++] + Mxt);
                    dstPts[dstOff++] = (srcPts[srcOff++] + Myt);
                }
                return;
            case (APPLY_IDENTITY):
                if (srcPts != dstPts || srcOff != dstOff) {
                    System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2);
                }
                return;
        }
    }

    public Point inverseTransform(Point ptSrc, Point ptDst) throws NonInvertibleTransformException {
        if (ptDst == null) { ptDst = new Point(); }
        double x = ptSrc.x;
        double y = ptSrc.y;
        switch (state) {
            default:
                stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                x -= mxt;
                y -= myt;
            case (APPLY_SHEAR | APPLY_SCALE):
                double det = mxx * myy - mxy * myx;
                if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) { throw new NonInvertibleTransformException("Determinant is " + det); }
                ptDst.set(((x * myy - y * mxy) / det), ((y * mxx - x * myx) / det));
                return ptDst;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                x -= mxt;
                y -= myt;
            case (APPLY_SHEAR):
                if (mxy == 0.0 || myx == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                ptDst.set((y / myx), (x / mxy));
                return ptDst;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                x -= mxt;
                y -= myt;
            case (APPLY_SCALE):
                if (mxx == 0.0 || myy == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                ptDst.set((x / mxx), (y / myy));
                return ptDst;
            case (APPLY_TRANSLATE):
                ptDst.set((x - mxt), (y - myt));
                return ptDst;
            case (APPLY_IDENTITY):
                ptDst.set(x, y);
                return ptDst;
        }
    }

    private BaseBounds inversTransform2DBounds(RectBounds src, RectBounds dst) throws NonInvertibleTransformException {
        switch (state) {
            default:
                stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
            case (APPLY_SHEAR | APPLY_SCALE):
                double det = mxx * myy - mxy * myx;
                if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) { throw new NonInvertibleTransformException("Determinant is " + det); }
                double x1 = src.getMinX() - mxt;
                double y1 = src.getMinY() - myt;
                double x2 = src.getMaxX() - mxt;
                double y2 = src.getMaxY() - myt;
                dst.setBoundsAndSort(((x1 * myy - y1 * mxy) / det),
                                     ((y1 * mxx - x1 * myx) / det),
                                     ((x2 * myy - y2 * mxy) / det),
                                     ((y2 * mxx - x2 * myx) / det));
                dst.add(((x2 * myy - y1 * mxy) / det),
                        ((y1 * mxx - x2 * myx) / det));
                dst.add(((x1 * myy - y2 * mxy) / det),
                        ((y2 * mxx - x1 * myx) / det));
                return dst;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                if (mxy == 0.0 || myx == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                dst.setBoundsAndSort(((src.getMinY() - myt) / myx),
                                     ((src.getMinX() - mxt) / mxy),
                                     ((src.getMaxY() - myt) / myx),
                                     ((src.getMaxX() - mxt) / mxy));
                break;
            case (APPLY_SHEAR):
                if (mxy == 0.0 || myx == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                dst.setBoundsAndSort((src.getMinY() / myx),
                                     (src.getMinX() / mxy),
                                     (src.getMaxY() / myx),
                                     (src.getMaxX() / mxy));
                break;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                if (mxx == 0.0 || myy == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                dst.setBoundsAndSort(((src.getMinX() - mxt) / mxx),
                                     ((src.getMinY() - myt) / myy),
                                     ((src.getMaxX() - mxt) / mxx),
                                     ((src.getMaxY() - myt) / myy));
                break;
            case (APPLY_SCALE):
                if (mxx == 0.0 || myy == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                dst.setBoundsAndSort((src.getMinX() / mxx),
                                     (src.getMinY() / myy),
                                     (src.getMaxX() / mxx),
                                     (src.getMaxY() / myy));
                break;
            case (APPLY_TRANSLATE):
                dst.setBounds((src.getMinX() - mxt),
                              (src.getMinY() - myt),
                              (src.getMaxX() - mxt),
                              (src.getMaxY() - myt));
                break;
            case (APPLY_IDENTITY):
                if (dst != src) { dst.setBounds((RectBounds) src); }
                break;
        }
        return dst;
    }

    public BaseBounds inverseTransform(BaseBounds src, BaseBounds dst) throws NonInvertibleTransformException {
        return inversTransform2DBounds((RectBounds)src, (RectBounds)dst);
    }
    public void inverseTransform(Rectangle src, Rectangle dst) throws NonInvertibleTransformException {
        switch (state) {
            default:
                stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
            case (APPLY_SHEAR | APPLY_SCALE):
            case (APPLY_SHEAR | APPLY_TRANSLATE):
            case (APPLY_SHEAR):
            case (APPLY_SCALE | APPLY_TRANSLATE):
            case (APPLY_SCALE):
                RectBounds b = new RectBounds(src);
                b = (RectBounds) inverseTransform(b, b);
                dst.setBounds(b);
                return;
            case (APPLY_TRANSLATE):
                Translate.transform(src, dst, -mxt, -myt);
                return;
            case (APPLY_IDENTITY):
                if (dst != src) { dst.setBounds(src); }
                return;
        }
    }
    public void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) throws NonInvertibleTransformException {
        double Mxx, Mxy, Mxt, Myx, Myy, Myt;    // For caching
        double det;
        if (dstPts == srcPts && dstOff > srcOff && dstOff < srcOff + numPts * 2) {
            System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2);
            srcOff = dstOff;
        }
        switch (state) {
            default:
                stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                Mxx = mxx; Mxy = mxy; Mxt = mxt;
                Myx = myx; Myy = myy; Myt = myt;
                det = Mxx * Myy - Mxy * Myx;
                if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) { throw new NonInvertibleTransformException("Determinant is " + det);  }
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++] - Mxt;
                    double y = srcPts[srcOff++] - Myt;
                    dstPts[dstOff++] = (x * Myy - y * Mxy) / det;
                    dstPts[dstOff++] = (y * Mxx - x * Myx) / det;
                }
                return;
            case (APPLY_SHEAR | APPLY_SCALE):
                Mxx = mxx; Mxy = mxy;
                Myx = myx; Myy = myy;
                det = Mxx * Myy - Mxy * Myx;
                if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) { throw new NonInvertibleTransformException("Determinant is " + det); }
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++];
                    double y = srcPts[srcOff++];
                    dstPts[dstOff++] = (x * Myy - y * Mxy) / det;
                    dstPts[dstOff++] = (y * Mxx - x * Myx) / det;
                }
                return;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                Mxy = mxy; Mxt = mxt;
                Myx = myx; Myt = myt;
                if (Mxy == 0.0 || Myx == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++] - Mxt;
                    dstPts[dstOff++] = (srcPts[srcOff++] - Myt) / Myx;
                    dstPts[dstOff++] = x / Mxy;
                }
                return;
            case (APPLY_SHEAR):
                Mxy = mxy; Myx = myx;
                if (Mxy == 0.0 || Myx == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++];
                    dstPts[dstOff++] = srcPts[srcOff++] / Myx;
                    dstPts[dstOff++] = x / Mxy;
                }
                return;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                Mxx = mxx; Mxt = mxt;
                Myy = myy; Myt = myt;
                if (Mxx == 0.0 || Myy == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                while (--numPts >= 0) {
                    dstPts[dstOff++] = (srcPts[srcOff++] - Mxt) / Mxx;
                    dstPts[dstOff++] = (srcPts[srcOff++] - Myt) / Myy;
                }
                return;
            case (APPLY_SCALE):
                Mxx = mxx; Myy = myy;
                if (Mxx == 0.0 || Myy == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                while (--numPts >= 0) {
                    dstPts[dstOff++] = srcPts[srcOff++] / Mxx;
                    dstPts[dstOff++] = srcPts[srcOff++] / Myy;
                }
                return;
            case (APPLY_TRANSLATE):
                Mxt = mxt; Myt = myt;
                while (--numPts >= 0) {
                    dstPts[dstOff++] = srcPts[srcOff++] - Mxt;
                    dstPts[dstOff++] = srcPts[srcOff++] - Myt;
                }
                return;
            case (APPLY_IDENTITY):
                if (srcPts != dstPts || srcOff != dstOff) {
                    System.arraycopy(srcPts, srcOff, dstPts, dstOff,numPts * 2);
                }
                return;
        }
    }

    public void inverseDeltaTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) throws NonInvertibleTransformException {
        doInverseTransform(srcPts, srcOff, dstPts, dstOff, numPts, state & ~APPLY_TRANSLATE);
    }
    
    private void doInverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts, int thestate) throws NonInvertibleTransformException {
        double Mxx, Mxy, Mxt, Myx, Myy, Myt;    // For caching
        double det;
        if (dstPts == srcPts && dstOff > srcOff && dstOff < srcOff + numPts * 2) {
            System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2);
            srcOff = dstOff;
        }
        switch (thestate) {
            default:
                stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                Mxx = mxx; Mxy = mxy; Mxt = mxt;
                Myx = myx; Myy = myy; Myt = myt;
                det = Mxx * Myy - Mxy * Myx;
                if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) { throw new NonInvertibleTransformException("Determinant is " + det); }
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++] - Mxt;
                    double y = srcPts[srcOff++] - Myt;
                    dstPts[dstOff++] = ((x * Myy - y * Mxy) / det);
                    dstPts[dstOff++] = ((y * Mxx - x * Myx) / det);
                }
                return;
            case (APPLY_SHEAR | APPLY_SCALE):
                Mxx = mxx; Mxy = mxy;
                Myx = myx; Myy = myy;
                det = Mxx * Myy - Mxy * Myx;
                if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) { throw new NonInvertibleTransformException("Determinant is " + det); }
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++];
                    double y = srcPts[srcOff++];
                    dstPts[dstOff++] = ((x * Myy - y * Mxy) / det);
                    dstPts[dstOff++] = ((y * Mxx - x * Myx) / det);
                }
                return;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                Mxy = mxy; Mxt = mxt;
                Myx = myx; Myt = myt;
                if (Mxy == 0.0 || Myx == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++] - Mxt;
                    dstPts[dstOff++] = ((srcPts[srcOff++] - Myt) / Myx);
                    dstPts[dstOff++] = (x / Mxy);
                }
                return;
            case (APPLY_SHEAR):
                Mxy = mxy; Myx = myx;
                if (Mxy == 0.0 || Myx == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                while (--numPts >= 0) {
                    double x = srcPts[srcOff++];
                    dstPts[dstOff++] = (srcPts[srcOff++] / Myx);
                    dstPts[dstOff++] = (x / Mxy);
                }
                return;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                Mxx = mxx; Mxt = mxt;
                Myy = myy; Myt = myt;
                if (Mxx == 0.0 || Myy == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                while (--numPts >= 0) {
                    dstPts[dstOff++] = ((srcPts[srcOff++] - Mxt) / Mxx);
                    dstPts[dstOff++] = ((srcPts[srcOff++] - Myt) / Myy);
                }
                return;
            case (APPLY_SCALE):
                Mxx = mxx; Myy = myy;
                if (Mxx == 0.0 || Myy == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                while (--numPts >= 0) {
                    dstPts[dstOff++] = (srcPts[srcOff++] / Mxx);
                    dstPts[dstOff++] = (srcPts[srcOff++] / Myy);
                }
                return;
            case (APPLY_TRANSLATE):
                Mxt = mxt; Myt = myt;
                while (--numPts >= 0) {
                    dstPts[dstOff++] = (srcPts[srcOff++] - Mxt);
                    dstPts[dstOff++] = (srcPts[srcOff++] - Myt);
                }
                return;
            case (APPLY_IDENTITY):
                if (srcPts != dstPts || srcOff != dstOff) {
                    System.arraycopy(srcPts, srcOff, dstPts, dstOff,numPts * 2);
                }
                return;
        }
    }

    public Shape createTransformedShape(Shape s) {
        if (s == null) { return null; }
        return new Path(s, this);
    }

    public void translate(double tx, double ty) {
        switch (state) {
            default:
                stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                mxt = tx * mxx + ty * mxy + mxt;
                myt = tx * myx + ty * myy + myt;
                if (mxt == 0.0 && myt == 0.0) {
                    state = APPLY_SHEAR | APPLY_SCALE;
                    if (type != TYPE_UNKNOWN) { type &= ~TYPE_TRANSLATION; }
                }
                return;
            case (APPLY_SHEAR | APPLY_SCALE):
                mxt = tx * mxx + ty * mxy;
                myt = tx * myx + ty * myy;
                if (mxt != 0.0 || myt != 0.0) {
                    state = APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE;
                    type |= TYPE_TRANSLATION;
                }
                return;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                mxt = ty * mxy + mxt;
                myt = tx * myx + myt;
                if (mxt == 0.0 && myt == 0.0) {
                    state = APPLY_SHEAR;
                    if (type != TYPE_UNKNOWN) { type &= ~TYPE_TRANSLATION;} }
                return;
            case (APPLY_SHEAR):
                mxt = ty * mxy;
                myt = tx * myx;
                if (mxt != 0.0 || myt != 0.0) {
                    state = APPLY_SHEAR | APPLY_TRANSLATE;
                    type |= TYPE_TRANSLATION;
                }
                return;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                mxt = tx * mxx + mxt;
                myt = ty * myy + myt;
                if (mxt == 0.0 && myt == 0.0) {
                    state = APPLY_SCALE;
                    if (type != TYPE_UNKNOWN) { type &= ~TYPE_TRANSLATION; }
                }
                return;
            case (APPLY_SCALE):
                mxt = tx * mxx;
                myt = ty * myy;
                if (mxt != 0.0 || myt != 0.0) {
                    state = APPLY_SCALE | APPLY_TRANSLATE;
                    type |= TYPE_TRANSLATION;
                }
                return;
            case (APPLY_TRANSLATE):
                mxt = tx + mxt;
                myt = ty + myt;
                if (mxt == 0.0 && myt == 0.0) {
                    state = APPLY_IDENTITY;
                    type = TYPE_IDENTITY;
                }
                return;
            case (APPLY_IDENTITY):
                mxt = tx;
                myt = ty;
                if (tx != 0.0 || ty != 0.0) {
                    state = APPLY_TRANSLATE;
                    type = TYPE_TRANSLATION;
                }
                return;
        }
    }

    protected final void rotate90() {
        double M0 = mxx;
        mxx = mxy;
        mxy = -M0;
        M0 = myx;
        myx = myy;
        myy = -M0;
        int newstate = rot90conversion[this.state];
        if ((newstate & (APPLY_SHEAR | APPLY_SCALE)) == APPLY_SCALE &&
            mxx == 1.0 && myy == 1.0)
        {
            newstate -= APPLY_SCALE;
        }
        this.state = newstate;
        type = TYPE_UNKNOWN;
    }
    protected final void rotate180() {
        mxx = -mxx;
        myy = -myy;
        int oldstate = this.state;
        if ((oldstate & (APPLY_SHEAR)) != 0) {
            // If there was a shear, then this rotation has no
            // effect on the state.
            mxy = -mxy;
            myx = -myx;
        } else {
            // No shear means the SCALE state may toggle when
            // m00 and m11 are negated.
            if (mxx == 1.0 && myy == 1.0) {
                this.state = oldstate & ~APPLY_SCALE;
            } else {
                this.state = oldstate | APPLY_SCALE;
            }
        }
        type = TYPE_UNKNOWN;
    }
    protected final void rotate270() {
        double M0 = mxx;
        mxx = -mxy;
        mxy = M0;
        M0 = myx;
        myx = -myy;
        myy = M0;
        int newstate = rot90conversion[this.state];
        if ((newstate & (APPLY_SHEAR | APPLY_SCALE)) == APPLY_SCALE &&
            mxx == 1.0 && myy == 1.0)
        {
            newstate -= APPLY_SCALE;
        }
        this.state = newstate;
        type = TYPE_UNKNOWN;
    }

    public void rotate(double theta) {
        // assert(APPLY_3D was dealt with at a higher level)
        double sin = Math.sin(theta);
        if (sin == 1.0) {
            rotate90();
        } else if (sin == -1.0) {
            rotate270();
        } else {
            double cos = Math.cos(theta);
            if (cos == -1.0) {
                rotate180();
            } else if (cos != 1.0) {
                double M0, M1;
                M0 = mxx;
                M1 = mxy;
                mxx =  cos * M0 + sin * M1;
                mxy = -sin * M0 + cos * M1;
                M0 = myx;
                M1 = myy;
                myx =  cos * M0 + sin * M1;
                myy = -sin * M0 + cos * M1;
                updateState();
            }
        }
    }

    public void scale(double sx, double sy) {
        int mystate = this.state;
        // assert(APPLY_3D was dealt with at a higher level)
        switch (mystate) {
            default:
                stateError();
            /* NOTREACHED */
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
            case (APPLY_SHEAR | APPLY_SCALE):
                mxx *= sx;
                myy *= sy;
            /* NOBREAK */
            case (APPLY_SHEAR | APPLY_TRANSLATE):
            case (APPLY_SHEAR):
                mxy *= sy;
                myx *= sx;
                if (mxy == 0 && myx == 0) {
                    mystate &= APPLY_TRANSLATE;
                    if (mxx == 1.0 && myy == 1.0) {
                        this.type = (mystate == APPLY_IDENTITY
                                     ? TYPE_IDENTITY
                                     : TYPE_TRANSLATION);
                    } else {
                        mystate |= APPLY_SCALE;
                        this.type = TYPE_UNKNOWN;
                    }
                    this.state = mystate;
                }
                return;
            case (APPLY_SCALE | APPLY_TRANSLATE):
            case (APPLY_SCALE):
                mxx *= sx;
                myy *= sy;
                if (mxx == 1.0 && myy == 1.0) {
                    this.state = (mystate &= APPLY_TRANSLATE);
                    this.type = (mystate == APPLY_IDENTITY
                                 ? TYPE_IDENTITY
                                 : TYPE_TRANSLATION);
                } else {
                    this.type = TYPE_UNKNOWN;
                }
                return;
            case (APPLY_TRANSLATE):
            case (APPLY_IDENTITY):
                mxx = sx;
                myy = sy;
                if (sx != 1.0 || sy != 1.0) {
                    this.state = mystate | APPLY_SCALE;
                    this.type = TYPE_UNKNOWN;
                }
                return;
        }
    }

    public void shear(double shx, double shy) {
        int mystate = this.state;
        // assert(APPLY_3D was dealt with at a higher level)
        switch (mystate) {
            default:
                stateError();
            /* NOTREACHED */
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
            case (APPLY_SHEAR | APPLY_SCALE):
                double M0, M1;
                M0 = mxx;
                M1 = mxy;
                mxx = M0 + M1 * shy;
                mxy = M0 * shx + M1;

                M0 = myx;
                M1 = myy;
                myx = M0 + M1 * shy;
                myy = M0 * shx + M1;
                updateState();
                return;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
            case (APPLY_SHEAR):
                mxx = mxy * shy;
                myy = myx * shx;
                if (mxx != 0.0 || myy != 0.0) {
                    this.state = mystate | APPLY_SCALE;
                }
                this.type = TYPE_UNKNOWN;
                return;
            case (APPLY_SCALE | APPLY_TRANSLATE):
            case (APPLY_SCALE):
                mxy = mxx * shx;
                myx = myy * shy;
                if (mxy != 0.0 || myx != 0.0) {
                    this.state = mystate | APPLY_SHEAR;
                }
                this.type = TYPE_UNKNOWN;
                return;
            case (APPLY_TRANSLATE):
            case (APPLY_IDENTITY):
                mxy = shx;
                myx = shy;
                if (mxy != 0.0 || myx != 0.0) {
                    this.state = mystate | APPLY_SCALE | APPLY_SHEAR;
                    this.type = TYPE_UNKNOWN;
                }
                return;
        }
    }

    public void concatenate(BaseTransform Tx) {
        switch (Tx.getDegree()) {
            case IDENTITY:
                return;
            case TRANSLATE:
                translate(Tx.getMxt(), Tx.getMyt());
                return;
            case AFFINE:
                break;
            default:
                degreeError(Degree.AFFINE);
                if (!(Tx instanceof AffineBase)) { Tx = new Affine(Tx); }
                break;
        }
        double M0, M1;
        double Txx, Txy, Tyx, Tyy;
        double Txt, Tyt;
        int mystate = state;
        AffineBase at = (AffineBase) Tx;
        int txstate = at.state;
        switch ((txstate << HI_SHIFT) | mystate) {

            /* ---------- Tx == IDENTITY cases ---------- */
            case (HI_IDENTITY | APPLY_IDENTITY):
            case (HI_IDENTITY | APPLY_TRANSLATE):
            case (HI_IDENTITY | APPLY_SCALE):
            case (HI_IDENTITY | APPLY_SCALE | APPLY_TRANSLATE):
            case (HI_IDENTITY | APPLY_SHEAR):
            case (HI_IDENTITY | APPLY_SHEAR | APPLY_TRANSLATE):
            case (HI_IDENTITY | APPLY_SHEAR | APPLY_SCALE):
            case (HI_IDENTITY | APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                return;

            /* ---------- this == IDENTITY cases ---------- */
            case (HI_SHEAR | HI_SCALE | HI_TRANSLATE | APPLY_IDENTITY):
                mxy = at.mxy;
                myx = at.myx;
            /* NOBREAK */
            case (HI_SCALE | HI_TRANSLATE | APPLY_IDENTITY):
                mxx = at.mxx;
                myy = at.myy;
            /* NOBREAK */
            case (HI_TRANSLATE | APPLY_IDENTITY):
                mxt = at.mxt;
                myt = at.myt;
                state = txstate;
                type = at.type;
                return;
            case (HI_SHEAR | HI_SCALE | APPLY_IDENTITY):
                mxy = at.mxy;
                myx = at.myx;
            /* NOBREAK */
            case (HI_SCALE | APPLY_IDENTITY):
                mxx = at.mxx;
                myy = at.myy;
                state = txstate;
                type = at.type;
                return;
            case (HI_SHEAR | HI_TRANSLATE | APPLY_IDENTITY):
                mxt = at.mxt;
                myt = at.myt;
            /* NOBREAK */
            case (HI_SHEAR | APPLY_IDENTITY):
                mxy = at.mxy;
                myx = at.myx;
                mxx = myy = 0.0;
                state = txstate;
                type = at.type;
                return;

            /* ---------- Tx == TRANSLATE cases ---------- */
            case (HI_TRANSLATE | APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
            case (HI_TRANSLATE | APPLY_SHEAR | APPLY_SCALE):
            case (HI_TRANSLATE | APPLY_SHEAR | APPLY_TRANSLATE):
            case (HI_TRANSLATE | APPLY_SHEAR):
            case (HI_TRANSLATE | APPLY_SCALE | APPLY_TRANSLATE):
            case (HI_TRANSLATE | APPLY_SCALE):
            case (HI_TRANSLATE | APPLY_TRANSLATE):
                translate(at.mxt, at.myt);
                return;

            /* ---------- Tx == SCALE cases ---------- */
            case (HI_SCALE | APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
            case (HI_SCALE | APPLY_SHEAR | APPLY_SCALE):
            case (HI_SCALE | APPLY_SHEAR | APPLY_TRANSLATE):
            case (HI_SCALE | APPLY_SHEAR):
            case (HI_SCALE | APPLY_SCALE | APPLY_TRANSLATE):
            case (HI_SCALE | APPLY_SCALE):
            case (HI_SCALE | APPLY_TRANSLATE):
                scale(at.mxx, at.myy);
                return;

            /* ---------- Tx == SHEAR cases ---------- */
            case (HI_SHEAR | APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
            case (HI_SHEAR | APPLY_SHEAR | APPLY_SCALE):
                Txy = at.mxy; Tyx = at.myx;
                M0 = mxx;
                mxx = mxy * Tyx;
                mxy = M0 * Txy;
                M0 = myx;
                myx = myy * Tyx;
                myy = M0 * Txy;
                type = TYPE_UNKNOWN;
                return;
            case (HI_SHEAR | APPLY_SHEAR | APPLY_TRANSLATE):
            case (HI_SHEAR | APPLY_SHEAR):
                mxx = mxy * at.myx;
                mxy = 0.0;
                myy = myx * at.mxy;
                myx = 0.0;
                state = mystate ^ (APPLY_SHEAR | APPLY_SCALE);
                type = TYPE_UNKNOWN;
                return;
            case (HI_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
            case (HI_SHEAR | APPLY_SCALE):
                mxy = mxx * at.mxy;
                mxx = 0.0;
                myx = myy * at.myx;
                myy = 0.0;
                state = mystate ^ (APPLY_SHEAR | APPLY_SCALE);
                type = TYPE_UNKNOWN;
                return;
            case (HI_SHEAR | APPLY_TRANSLATE):
                mxx = 0.0;
                mxy = at.mxy;
                myx = at.myx;
                myy = 0.0;
                state = APPLY_TRANSLATE | APPLY_SHEAR;
                type = TYPE_UNKNOWN;
                return;
        }
        // If Tx has more than one attribute, pathIterator is not worth optimizing
        // all of those cases...
        Txx = at.mxx; Txy = at.mxy; Txt = at.mxt;
        Tyx = at.myx; Tyy = at.myy; Tyt = at.myt;
        switch (mystate) {
            default:
                stateError();
            /* NOTREACHED */
            case (APPLY_SHEAR | APPLY_SCALE):
                state = mystate | txstate;
            /* NOBREAK */
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                M0 = mxx;
                M1 = mxy;
                mxx  = Txx * M0 + Tyx * M1;
                mxy  = Txy * M0 + Tyy * M1;
                mxt += Txt * M0 + Tyt * M1;

                M0 = myx;
                M1 = myy;
                myx  = Txx * M0 + Tyx * M1;
                myy  = Txy * M0 + Tyy * M1;
                myt += Txt * M0 + Tyt * M1;
                type = TYPE_UNKNOWN;
                return;

            case (APPLY_SHEAR | APPLY_TRANSLATE):
            case (APPLY_SHEAR):
                M0 = mxy;
                mxx  = Tyx * M0;
                mxy  = Tyy * M0;
                mxt += Tyt * M0;

                M0 = myx;
                myx  = Txx * M0;
                myy  = Txy * M0;
                myt += Txt * M0;
                break;

            case (APPLY_SCALE | APPLY_TRANSLATE):
            case (APPLY_SCALE):
                M0 = mxx;
                mxx  = Txx * M0;
                mxy  = Txy * M0;
                mxt += Txt * M0;

                M0 = myy;
                myx  = Tyx * M0;
                myy  = Tyy * M0;
                myt += Tyt * M0;
                break;

            case (APPLY_TRANSLATE):
                mxx  = Txx;
                mxy  = Txy;
                mxt += Txt;

                myx  = Tyx;
                myy  = Tyy;
                myt += Tyt;
                state = txstate | APPLY_TRANSLATE;
                type = TYPE_UNKNOWN;
                return;
        }
        updateState();
    }

    public void invert() throws NonInvertibleTransformException {
        double Mxx, Mxy, Mxt;
        double Myx, Myy, Myt;
        double det;
        switch (state) {
            default:
                stateError();
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                Mxx = mxx; Mxy = mxy; Mxt = mxt;
                Myx = myx; Myy = myy; Myt = myt;
                det = Mxx * Myy - Mxy * Myx;
                if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) { throw new NonInvertibleTransformException("Determinant is " + det); }
                mxx =  Myy / det;
                myx = -Myx / det;
                mxy = -Mxy / det;
                myy =  Mxx / det;
                mxt = (Mxy * Myt - Myy * Mxt) / det;
                myt = (Myx * Mxt - Mxx * Myt) / det;
                break;
            case (APPLY_SHEAR | APPLY_SCALE):
                Mxx = mxx; Mxy = mxy;
                Myx = myx; Myy = myy;
                det = Mxx * Myy - Mxy * Myx;
                if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) { throw new NonInvertibleTransformException("Determinant is " + det); }
                mxx =  Myy / det;
                myx = -Myx / det;
                mxy = -Mxy / det;
                myy =  Mxx / det;
                break;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                Mxy = mxy; Mxt = mxt;
                Myx = myx; Myt = myt;
                if (Mxy == 0.0 || Myx == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                myx = 1.0 / Mxy;
                mxy = 1.0 / Myx;
                mxt = -Myt / Myx;
                myt = -Mxt / Mxy;
                break;
            case (APPLY_SHEAR):
                Mxy = mxy;
                Myx = myx;
                if (Mxy == 0.0 || Myx == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                myx = 1.0 / Mxy;
                mxy = 1.0 / Myx;
                break;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                Mxx = mxx; Mxt = mxt;
                Myy = myy; Myt = myt;
                if (Mxx == 0.0 || Myy == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                mxx = 1.0 / Mxx;
                myy = 1.0 / Myy;
                mxt = -Mxt / Mxx;
                myt = -Myt / Myy;
                break;
            case (APPLY_SCALE):
                Mxx = mxx;
                Myy = myy;
                if (Mxx == 0.0 || Myy == 0.0) { throw new NonInvertibleTransformException("Determinant is 0"); }
                mxx = 1.0 / Mxx;
                myy = 1.0 / Myy;
                break;
            case (APPLY_TRANSLATE):
                mxt = -mxt;
                myt = -myt;
                break;
            case (APPLY_IDENTITY):
                break;
        }
    }

}