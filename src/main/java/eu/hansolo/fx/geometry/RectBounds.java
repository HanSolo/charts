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

import eu.hansolo.fx.geometry.tools.Point;

public class RectBounds extends BaseBounds {
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;


    public RectBounds() {
        minX = minY = 0.0;
        maxX = maxY = -1.0;
    }

    public BaseBounds copy() {
        return new RectBounds(minX, minY, maxX, maxY);
    }

    public RectBounds(double minX, double minY, double maxX, double maxY) {
        setBounds(minX, minY, maxX, maxY);
    }
    public RectBounds(RectBounds other) {
        setBounds(other);
    }
    public RectBounds(Rectangle other) {
        setBounds(other.x, other.y,
                  other.x + other.width, other.y + other.height);
    }

    public double getWidth() { return maxX - minX; }
    public double getHeight() { return maxY - minY; }

    public double getDepth() { return 0.0; }

    public double getMinX() { return minX; }
    public void setMinX(double minX) { this.minX = minX; }

    public double getMinY() { return minY; }
    public void setMinY(double minY) { this.minY = minY; }

    public double getMinZ() { return 0.0; }

    public double getMaxX() { return maxX; }
    public void setMaxX(double maxX) { this.maxX = maxX; }

    public double getMaxY() { return maxY; }
    public void setMaxY(double maxY) { this.maxY = maxY; }

    public Point getMin(Point min) {
        if (min == null) { min = new Point(); }
        min.x = minX;
        min.y = minY;
        return min;
    }
    public Point getMax(Point max) {
        if (max == null) { max = new Point(); }
        max.x = maxX;
        max.y = maxY;
        return max;
    }

    public BaseBounds deriveWithUnion(BaseBounds other) {
        RectBounds rb = (RectBounds) other;
        unionWith(rb);
        return this;
    }

    public BaseBounds deriveWithNewBounds(Rectangle other) {
        if (other.width < 0 || other.height < 0) return makeEmpty();
        setBounds(other.x, other.y, other.x + other.width, other.y + other.height);
        return this;
    }
    public BaseBounds deriveWithNewBounds(BaseBounds other) {
        if (other.isEmpty()) return makeEmpty();
        RectBounds rb = (RectBounds) other;
        minX = rb.getMinX();
        minY = rb.getMinY();
        maxX = rb.getMaxX();
        maxY = rb.getMaxY();
        return this;
    }
    public BaseBounds deriveWithNewBounds(double minX, double minY, double maxX, double maxY) {
        if ((maxX < minX) || (maxY < minY)) return makeEmpty();
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        return this;
    }
    public BaseBounds deriveWithNewBoundsAndSort(double minX, double minY, double maxX, double maxY) {
        setBoundsAndSort(minX, minY,maxX, maxY);
        return this;
    }

    public final void setBounds(RectBounds other) {
        minX = other.getMinX();
        minY = other.getMinY();
        maxX = other.getMaxX();
        maxY = other.getMaxY();
    }
    public final void setBounds(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public void setBoundsAndSort(double minX, double minY, double maxX, double maxY) {
        setBounds(minX, minY, maxX, maxY);
        sortMinMax();
    }
    public void setBoundsAndSort(double minX, double minY,  double minZ, double maxX, double maxY, double maxZ) {
        if (minZ != 0 || maxZ != 0) {
            throw new UnsupportedOperationException("Unknown BoundsType");
        }
        setBounds(minX, minY, maxX, maxY);
        sortMinMax();
    }
    public void setBoundsAndSort(Point p1, Point p2) {
        setBoundsAndSort(p1.x, p1.y, p2.x, p2.y);
    }

    public void unionWith(RectBounds other) {
        // Short circuit union if either bounds is empty.
        if (other.isEmpty()) return;
        if (this.isEmpty()) {
            setBounds(other);
            return;
        }

        minX = Math.min(minX, other.getMinX());
        minY = Math.min(minY, other.getMinY());
        maxX = Math.max(maxX, other.getMaxX());
        maxY = Math.max(maxY, other.getMaxY());
    }
    public void unionWith(double minX, double minY, double maxX, double maxY) {
        // Short circuit union if either bounds is empty.
        if ((maxX < minX) || (maxY < minY)) return;
        if (this.isEmpty()) {
            setBounds(minX, minY, maxX, maxY);
            return;
        }

        this.minX = Math.min(this.minX, minX);
        this.minY = Math.min(this.minY, minY);
        this.maxX = Math.max(this.maxX, maxX);
        this.maxY = Math.max(this.maxY, maxY);
    }

    public void add(double x, double y, double z) {
        if (z != 0) { throw new UnsupportedOperationException("Unknown BoundsType"); }
        unionWith(x, y, x, y);
    }
    public void add(double x, double y) { unionWith(x, y, x, y); }
    public void add(Point p) { add(p.x, p.y); }

    public boolean intersects(double x, double y, double width, double height) {
        if (isEmpty()) return false;
        return (x + width >= minX &&
                y + height >= minY &&
                x <= maxX &&
                y <= maxY);
    }
    public boolean intersects(BaseBounds other) {
        if ((other == null) || other.isEmpty() || isEmpty()) { return false; }
        return (other.getMaxX() >= minX &&
                other.getMaxY() >= minY &&
                other.getMinX() <= maxX &&
                other.getMinY() <= maxY);
    }

    public void intersectWith(BaseBounds other) {
        // Short circuit intersect if either bounds is empty.
        if (this.isEmpty()) return;
        if (other.isEmpty()) {
            makeEmpty();
            return;
        }

        minX = Math.max(minX, other.getMinX());
        minY = Math.max(minY, other.getMinY());
        maxX = Math.min(maxX, other.getMaxX());
        maxY = Math.min(maxY, other.getMaxY());
    }
    public void intersectWith(Rectangle other) {
        double x = other.x;
        double y = other.y;
        intersectWith(x, y, x + other.width, y + other.height);
    }
    public void intersectWith(double minX, double minY, double maxX, double maxY) {
        // Short circuit intersect if either bounds is empty.
        if (this.isEmpty()) return;
        if ((maxX < minX) || (maxY < minY)) {
            makeEmpty();
            return;
        }

        this.minX = Math.max(this.minX, minX);
        this.minY = Math.max(this.minY, minY);
        this.maxX = Math.min(this.maxX, maxX);
        this.maxY = Math.min(this.maxY, maxY);
    }
    public void intersectWith(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        // Short circuit intersect if either bounds is empty.
        if (this.isEmpty()) return;
        if ((maxX < minX) || (maxY < minY) || (maxZ < minZ)) {
            makeEmpty();
            return;
        }

        this.minX = Math.max(this.minX, minX);
        this.minY = Math.max(this.minY, minY);
        this.maxX = Math.min(this.maxX, maxX);
        this.maxY = Math.min(this.maxY, maxY);
    }

    public boolean contains(Point p) {
        if ((p == null) || isEmpty()) return false;
        return (p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY);
    }
    public boolean contains(double x, double y) {
        if (isEmpty()) return false;
        return (x >= minX && x <= maxX && y >= minY && y <= maxY);
    }
    public boolean contains(double x, double y, double width, double height) {
        if (isEmpty()) return false;
        return contains(x, y) && contains(x+width, y+height);
    }

    public boolean disjoint(double x, double y, double width, double height) {
        if (isEmpty()) return true;
        return (x + width < minX ||
                y + height < minY ||
                x > maxX ||
                y > maxY);
    }
    public boolean disjoint(RectBounds other) {
        if ((other == null) || other.isEmpty() || isEmpty()) {
            return true;
        }
        return (other.getMaxX() < minX ||
                other.getMaxY() < minY ||
                other.getMinX() > maxX ||
                other.getMinY() > maxY);
    }

    public boolean isEmpty() { return !(maxX >= minX && maxY >= minY);}

    public void roundOut() {
        minX = Math.floor(minX);
        minY = Math.floor(minY);
        maxX = Math.ceil(maxX);
        maxY = Math.ceil(maxY);
    }

    public void grow(double h, double v) {
        minX -= h;
        maxX += h;
        minY -= v;
        maxY += v;
    }

    public BaseBounds deriveWithPadding(double horizontal, double vertical) {
        grow(horizontal, vertical);
        return this;
    }

    public RectBounds makeEmpty() {
        minX = minY = 0.0;
        maxX = maxY = -1.0;
        return this;
    }

    protected void sortMinMax() {
        if (minX > maxX) {
            double tmp = maxX;
            maxX = minX;
            minX = tmp;
        }
        if (minY > maxY) {
            double tmp = maxY;
            maxY = minY;
            minY = tmp;
        }
    }

    @Override public void translate(double x, double y, double z) {
        setMinX(getMinX() + x);
        setMinY(getMinY() + y);
        setMaxX(getMaxX() + x);
        setMaxY(getMaxY() + y);
    }

    @Override public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        final RectBounds other = (RectBounds) obj;
        if (minX != other.getMinX()) return false;
        if (minY != other.getMinY()) return false;
        if (maxX != other.getMaxX()) return false;
        if (maxY != other.getMaxY()) return false;
        return true;
    }

    @Override public String toString() {
        return "RectBounds { minX:" + minX + ", minY:" + minY + ", maxX:" + maxX + ", maxY:" + maxY + "} (w:" + (maxX-minX) + ", h:" + (maxY-minY) +")";
    }
}