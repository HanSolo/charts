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


public class Rectangle {
    public int x;
    public int y;
    public int width;
    public int height;


    public Rectangle() {
        this(0, 0, 0, 0);
    }
    public Rectangle(BaseBounds b) {
        setBounds(b);
    }
    public Rectangle(Rectangle r) {
        this(r.x, r.y, r.width, r.height);
    }
    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public Rectangle(int width, int height) {
        this(0, 0, width, height);
    }


    public void setBounds(Rectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }
    public void setBounds(int x, int y, int width, int height) {
        reshape(x, y, width, height);
    }
    public void setBounds(BaseBounds b) {
        x = (int) Math.floor(b.getMinX());
        y = (int) Math.floor(b.getMinY());
        int x2 = (int) Math.ceil(b.getMaxX());
        int y2 = (int) Math.ceil(b.getMaxY());
        width = x2 - x;
        height = y2 - y;
    }

    public boolean contains(int cx, int cy) {
        int tw = this.width;
        int th = this.height;
        if ((tw | th) < 0) {
            // At least one of the dimensions is negative...
            return false;
        }
        // Note: if either dimension is zero, tests below must return false...
        int tx = this.x;
        int ty = this.y;
        if (cx < tx || cy < ty) {
            return false;
        }
        tw += tx;
        th += ty;
        //    overflow || intersect
        return ((tw < tx || tw > cx) &&
                (th < ty || th > cy));
    }
    public boolean contains(Rectangle r) {
        return contains(r.x, r.y, r.width, r.height);
    }
    public boolean contains(int cx, int cy, int cw, int ch) {
        int tw = this.width;
        int th = this.height;
        if ((tw | th | cw | ch) < 0) {
            // At least one of the dimensions is negative...
            return false;
        }
        // Note: if any dimension is zero, tests below must return false...
        int tx = this.x;
        int ty = this.y;
        if (cx < tx || cy < ty) {
            return false;
        }
        tw += tx;
        cw += cx;
        if (cw <= cx) {
            // cx+cw overflowed or cw was zero, return false if...
            // either original tw or cw was zero or
            // tx+tw did not overflow or
            // the overflowed cx+cw is smaller than the overflowed tx+tw
            if (tw >= tx || cw > tw) return false;
        } else {
            // cx+cw did not overflow and cw was not zero, return false if...
            // original tw was zero or
            // tx+tw did not overflow and tx+tw is smaller than cx+cw
            if (tw >= tx && cw > tw) return false;
        }
        th += ty;
        ch += cy;
        if (ch <= cy) {
            if (th >= ty || ch > th) return false;
        } else {
            if (th >= ty && ch > th) return false;
        }
        return true;
    }

    public Rectangle intersection(Rectangle r) {
        Rectangle ret = new Rectangle(this);
        ret.intersectWith(r);
        return ret;
    }

    public void intersectWith(Rectangle r) {
        if (r == null) {
            return;
        }
        int  tx1 = this.x;
        int  ty1 = this.y;
        int  rx1 = r.x;
        int  ry1 = r.y;
        long tx2 = tx1; tx2 += this.width;
        long ty2 = ty1; ty2 += this.height;
        long rx2 = rx1; rx2 += r.width;
        long ry2 = ry1; ry2 += r.height;
        if (tx1 < rx1) { tx1 = rx1; }
        if (ty1 < ry1) { ty1 = ry1; }
        if (tx2 > rx2) { tx2 = rx2; }
        if (ty2 > ry2) { ty2 = ry2; }
        tx2 -= tx1;
        ty2 -= ty1;
        if (tx2 < Integer.MIN_VALUE) { tx2 = Integer.MIN_VALUE; }
        if (ty2 < Integer.MIN_VALUE) { ty2 = Integer.MIN_VALUE; }
        setBounds(tx1, ty1, (int) tx2, (int) ty2);
    }

    public void translate(int dx, int dy) {
        int oldv = this.x;
        int newv = oldv + dx;
        if (dx < 0) {
            if (newv > oldv) {
                if (width >= 0) { width += newv - Integer.MIN_VALUE; }
                newv = Integer.MIN_VALUE;
            }
        } else {
            if (newv < oldv) {
                if (width >= 0) {
                    width += newv - Integer.MAX_VALUE;
                    if (width < 0) { width = Integer.MAX_VALUE; }
                }
                newv = Integer.MAX_VALUE;
            }
        }
        this.x = newv;

        oldv = this.y;
        newv = oldv + dy;
        if (dy < 0) {
            if (newv > oldv) {
                if (height >= 0) { height += newv - Integer.MIN_VALUE; }
                newv = Integer.MIN_VALUE;
            }
        } else {
            if (newv < oldv) {
                if (height >= 0) {
                    height += newv - Integer.MAX_VALUE;
                    if (height < 0) { height = Integer.MAX_VALUE; }
                }
                newv = Integer.MAX_VALUE;
            }
        }
        this.y = newv;
    }

    public RectBounds toRectBounds() { return new RectBounds(x, y, x+width, y+height); }

    public void add(int newx, int newy) {
        if ((width | height) < 0) {
            this.x = newx;
            this.y = newy;
            this.width = this.height = 0;
            return;
        }
        int  x1 = this.x;
        int  y1 = this.y;
        long x2 = this.width;
        long y2 = this.height;
        x2 += x1;
        y2 += y1;
        if (x1 > newx) { x1 = newx; }
        if (y1 > newy) { y1 = newy; }
        if (x2 < newx) { x2 = newx; }
        if (y2 < newy) { y2 = newy; }
        x2 -= x1;
        y2 -= y1;
        if (x2 > Integer.MAX_VALUE) { x2 = Integer.MAX_VALUE; }
        if (y2 > Integer.MAX_VALUE) { y2 = Integer.MAX_VALUE; }
        reshape(x1, y1, (int) x2, (int) y2);
    }

    public void add(Rectangle r) {
        long tx2 = this.width;
        long ty2 = this.height;
        if ((tx2 | ty2) < 0) { reshape(r.x, r.y, r.width, r.height); }
        long rx2 = r.width;
        long ry2 = r.height;
        if ((rx2 | ry2) < 0) { return; }
        int tx1 = this.x;
        int ty1 = this.y;
        tx2 += tx1;
        ty2 += ty1;
        int rx1 = r.x;
        int ry1 = r.y;
        rx2 += rx1;
        ry2 += ry1;
        if (tx1 > rx1) { tx1 = rx1; }
        if (ty1 > ry1) { ty1 = ry1; }
        if (tx2 < rx2) { tx2 = rx2; }
        if (ty2 < ry2) { ty2 = ry2; }
        tx2 -= tx1;
        ty2 -= ty1;
        if (tx2 > Integer.MAX_VALUE) { tx2 = Integer.MAX_VALUE; }
        if (ty2 > Integer.MAX_VALUE) { ty2 = Integer.MAX_VALUE; }
        reshape(tx1, ty1, (int) tx2, (int) ty2);
    }

    public void grow(int h, int v) {
        long x0 = this.x;
        long y0 = this.y;
        long x1 = this.width;
        long y1 = this.height;
        x1 += x0;
        y1 += y0;

        x0 -= h;
        y0 -= v;
        x1 += h;
        y1 += v;

        if (x1 < x0) {
            x1 -= x0;
            if (x1 < Integer.MIN_VALUE) x1 = Integer.MIN_VALUE;
            if (x0 < Integer.MIN_VALUE) x0 = Integer.MIN_VALUE;
            else if (x0 > Integer.MAX_VALUE) x0 = Integer.MAX_VALUE;
        } else {
            if (x0 < Integer.MIN_VALUE) {
                x0 = Integer.MIN_VALUE;
            } else if (x0 > Integer.MAX_VALUE) {
                x0 = Integer.MAX_VALUE;
            }
            x1 -= x0;
            if (x1 < Integer.MIN_VALUE) {
                x1 = Integer.MIN_VALUE;
            } else if (x1 > Integer.MAX_VALUE) {
                x1 = Integer.MAX_VALUE;
            }
        }

        if (y1 < y0) {
            y1 -= y0;
            if (y1 < Integer.MIN_VALUE) { y1 = Integer.MIN_VALUE; }
            if (y0 < Integer.MIN_VALUE) {
                y0 = Integer.MIN_VALUE;
            } else if (y0 > Integer.MAX_VALUE) {
                y0 = Integer.MAX_VALUE;
            }
        } else {
            if (y0 < Integer.MIN_VALUE) {
                y0 = Integer.MIN_VALUE;
            } else if (y0 > Integer.MAX_VALUE) {
                y0 = Integer.MAX_VALUE;
            }
            y1 -= y0;
            if (y1 < Integer.MIN_VALUE) {
                y1 = Integer.MIN_VALUE;
            } else if (y1 > Integer.MAX_VALUE) {
                y1 = Integer.MAX_VALUE;
            }
        }
        reshape((int) x0, (int) y0, (int) x1, (int) y1);
    }

    private void reshape(int x, int y, int width, int height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }

    public boolean isEmpty() { return (width <= 0) || (height <= 0); }

    @Override public boolean equals(Object obj) {
        if (obj instanceof Rectangle) {
            Rectangle r = (Rectangle)obj;
            return ((x == r.x) && (y == r.y) && (width == r.width) && (height == r.height));
        }
        return super.equals(obj);
    }
}