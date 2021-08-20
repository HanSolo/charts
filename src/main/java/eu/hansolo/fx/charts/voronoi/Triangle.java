/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.charts.voronoi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;


class Triangle extends ArraySet<VPoint> {
    private int    idNumber;                   // The id number
    private VPoint circumcenter = null;        // The triangle's circumcenter

    private static int     idGenerator = 0;     // Used to create id numbers
    public  static boolean moreInfo    = false; // True iff more info in toString


    public Triangle(final VPoint... vertices) {
        this(Arrays.asList(vertices));
    }
    public Triangle(Collection<? extends VPoint> collection) {
        super(collection);
        idNumber = idGenerator++;
        if (this.size() != 3) { throw new IllegalArgumentException("Triangle must have 3 vertices"); }
    }


    public VPoint getVertexButNot(final VPoint... badVertices) {
        Collection<VPoint> bad = Arrays.asList(badVertices);
        for (VPoint v : this) { if (!bad.contains(v)) { return v; } }
        throw new NoSuchElementException("No vertex found");
    }

    public boolean isNeighbor(final Triangle triangle) {
        int count = 0;
        for (VPoint vertex : this) { if (!triangle.contains(vertex)) { count++; } }
        return count == 1;
    }

    public ArraySet<VPoint> facetOpposite(final VPoint vertex) {
        ArraySet<VPoint> facet = new ArraySet<VPoint>(this);
        if (!facet.remove(vertex)) { throw new IllegalArgumentException("Vertex not in triangle"); }
        return facet;
    }

    public VPoint getCircumCenter() {
        if (circumcenter == null) { circumcenter = VPoint.circumcenter(this.toArray(new VPoint[0])); }
        return circumcenter;
    }


    @Override public boolean add(VPoint vertex) {
        throw new UnsupportedOperationException();
    }

    @Override public Iterator<VPoint> iterator() {
        return new Iterator<VPoint>() {
            private Iterator<VPoint> it = Triangle.super.iterator();
            public boolean hasNext() { return it.hasNext(); }
            public VPoint next() { return it.next(); }
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }


    @Override public int hashCode() {
        return (int) (idNumber ^ (idNumber >>> 32));
    }

    @Override public boolean equals(Object o) {
        return (this == o);
    }

    @Override public String toString() {
        if (!moreInfo) { return "Triangle" + idNumber; }
        return "Triangle" + idNumber + super.toString();
    }
}
