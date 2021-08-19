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

public class VPoint {
    private double[] coordinates;


    public VPoint(double... coords) {
        // Copying is done here to ensure that Point's coords cannot be altered.
        // This is necessary because the double... notation actually creates a
        // constructor with double[] as its argument.
        coordinates = new double[coords.length];
        System.arraycopy(coords, 0, coordinates, 0, coords.length);
    }


    public double getCoordinates(final int i) {
        return this.coordinates[i];
    }

    public int getDimension() {
        return coordinates.length;
    }

    public int checkDimension(final VPoint p) {
        int len = this.coordinates.length;
        if (len != p.coordinates.length) { throw new IllegalArgumentException("Dimension mismatch"); }
        return len;
    }

    public VPoint extend(final double... coords) {
        double[] result = new double[coordinates.length + coords.length];
        System.arraycopy(coordinates, 0, result, 0, coordinates.length);
        System.arraycopy(coords, 0, result, coordinates.length, coords.length);
        return new VPoint(result);
    }

    public double dotProduct(final VPoint p) {
        int    len = checkDimension(p);
        double sum = 0;
        for (int i = 0; i < len; i++) { sum += this.coordinates[i] * p.coordinates[i]; }
        return sum;
    }

    public double magnitude() {
        return Math.sqrt(this.dotProduct(this));
    }

    public VPoint subtract(final VPoint p) {
        int      len    = checkDimension(p);
        double[] coords = new double[len];
        for (int i = 0; i < len; i++) { coords[i] = this.coordinates[i] - p.coordinates[i]; }
        return new VPoint(coords);
    }

    public VPoint add(final VPoint p) {
        int      len    = checkDimension(p);
        double[] coords = new double[len];
        for (int i = 0; i < len; i++) { coords[i] = this.coordinates[i] + p.coordinates[i]; }
        return new VPoint(coords);
    }

    public double angle(final VPoint p) {
        return Math.acos(this.dotProduct(p) / (this.magnitude() * p.magnitude()));
    }

    public VPoint bisector(final VPoint point) {
        checkDimension(point);
        VPoint diff = this.subtract(point);
        VPoint sum  = this.add(point);
        double dot  = diff.dotProduct(sum);
        return diff.extend(-dot / 2);
    }


    public static String toString(final VPoint[] matrix) {
        StringBuilder buf = new StringBuilder("{");
        for (VPoint row : matrix) { buf.append(" " + row); }
        buf.append(" }");
        return buf.toString();
    }

    public static double determinant(final VPoint[] matrix) {
        if (matrix.length != matrix[0].getDimension()) { throw new IllegalArgumentException("Matrix is not square"); }
        boolean[] columns = new boolean[matrix.length];
        for (int i = 0; i < matrix.length; i++) { columns[i] = true; }
        try { return determinant(matrix, 0, columns); } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Matrix is wrong shape");
        }
    }

    private static double determinant(final VPoint[] matrix, final int row, final boolean[] cols) {
        if (row == matrix.length) { return 1; }
        double sum  = 0;
        int    sign = 1;
        for (int col = 0; col < cols.length; col++) {
            if (!cols[col]) { continue; }
            cols[col] = false;
            sum += sign * matrix[row].coordinates[col] * determinant(matrix, row + 1, cols);
            cols[col] = true;
            sign = -sign;
        }
        return sum;
    }

    public static VPoint cross(final VPoint[] matrix) {
        int len = matrix.length + 1;
        if (len != matrix[0].getDimension()) { throw new IllegalArgumentException("Dimension mismatch"); }
        boolean[] columns = new boolean[len];
        for (int i = 0; i < len; i++) { columns[i] = true; }
        double[] result = new double[len];
        int      sign   = 1;
        try {
            for (int i = 0; i < len; i++) {
                columns[i] = false;
                result[i] = sign * determinant(matrix, 0, columns);
                columns[i] = true;
                sign = -sign;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Matrix is wrong shape");
        }
        return new VPoint(result);
    }


    public static double content(final VPoint[] simplex) {
        VPoint[] matrix = new VPoint[simplex.length];
        for (int i = 0; i < matrix.length; i++) { matrix[i] = simplex[i].extend(1); }
        int fact = 1;
        for (int i = 1; i < matrix.length; i++) { fact = fact * i; }
        return determinant(matrix) / fact;
    }

    public int[] relation(final VPoint[] simplex) {
        /* In 2D, we compute the cross of this matrix:
         *    1   1   1   1
         *    p0  a0  b0  c0
         *    p1  a1  b1  c1
         * where (a, b, c) is the simplex and p is this Point. The result is a
         * vector in which the first coordinate is the signed area (all signed
         * areas are off by the same constant factor) of the simplex and the
         * remaining coordinates are the *negated* signed areas for the
         * simplices in which p is substituted for each of the vertices.
         * Analogous results occur in higher dimensions.
         */
        int dim = simplex.length - 1;
        if (this.getDimension() != dim) { throw new IllegalArgumentException("Dimension mismatch"); }

        /* Create and load the matrix */
        VPoint[] matrix = new VPoint[dim + 1];
        /* First row */
        double[] coords = new double[dim + 2];
        for (int j = 0; j < coords.length; j++) { coords[j] = 1; }
        matrix[0] = new VPoint(coords);
        /* Other rows */
        for (int i = 0; i < dim; i++) {
            coords[0] = this.coordinates[i];
            for (int j = 0; j < simplex.length; j++) {
                coords[j + 1] = simplex[j].coordinates[i];
            }
            matrix[i + 1] = new VPoint(coords);
        }

        /* Compute and analyze the vector of areas/volumes/contents */
        VPoint vector  = cross(matrix);
        double content = vector.coordinates[0];
        int[]  result  = new int[dim + 1];
        for (int i = 0; i < result.length; i++) {
            double value = vector.coordinates[i + 1];
            if (Math.abs(value) <= 1.0e-6 * Math.abs(content)) {
                result[i] = 0;
            } else if (value < 0) {
                result[i] = -1;
            } else {
                result[i] = 1;
            }
        }
        if (content < 0) {
            for (int i = 0; i < result.length; i++) { result[i] = -result[i]; }
        }
        if (content == 0) {
            for (int i = 0; i < result.length; i++) { result[i] = Math.abs(result[i]); }
        }
        return result;
    }

    public VPoint isOutside(final VPoint[] simplex) {
        int[] result = this.relation(simplex);
        for (int i = 0; i < result.length; i++) {
            if (result[i] > 0) { return simplex[i]; }
        }
        return null;
    }

    public VPoint isOn(final VPoint[] simplex) {
        int[]  result  = this.relation(simplex);
        VPoint witness = null;
        for (int i = 0; i < result.length; i++) {
            if (result[i] == 0) { witness = simplex[i]; } else if (result[i] > 0) { return null; }
        }
        return witness;
    }

    public boolean isInside(final VPoint[] simplex) {
        int[] result = this.relation(simplex);
        for (int r : result) { if (r >= 0) { return false; } }
        return true;
    }

    public int vsCircumCircle(final VPoint[] simplex) {
        VPoint[] matrix = new VPoint[simplex.length + 1];
        for (int i = 0; i < simplex.length; i++) { matrix[i] = simplex[i].extend(1, simplex[i].dotProduct(simplex[i])); }
        matrix[simplex.length] = this.extend(1, this.dotProduct(this));
        double d      = determinant(matrix);
        int    result = (d < 0) ? -1 : ((d > 0) ? +1 : 0);
        if (content(simplex) < 0) { result = -result; }
        return result;
    }

    public static VPoint circumcenter(final VPoint[] simplex) {
        int dim = simplex[0].getDimension();
        if (simplex.length - 1 != dim) { throw new IllegalArgumentException("Dimension mismatch"); }
        VPoint[] matrix = new VPoint[dim];
        for (int i = 0; i < dim; i++) { matrix[i] = simplex[i].bisector(simplex[i + 1]); }
        VPoint   hCenter = cross(matrix);      // Center in homogeneous coordinates
        double   last    = hCenter.coordinates[dim];
        double[] result  = new double[dim];
        for (int i = 0; i < dim; i++) { result[i] = hCenter.coordinates[i] / last; }
        return new VPoint(result);
    }

    @Override public boolean equals(final Object other) {
        if (!(other instanceof VPoint)) { return false; }
        VPoint p = (VPoint) other;
        if (this.coordinates.length != p.coordinates.length) { return false; }
        for (int i = 0; i < this.coordinates.length; i++) { if (this.coordinates[i] != p.coordinates[i]) { return false; } }
        return true;
    }

    @Override public int hashCode() {
        int hash = 0;
        for (double c : this.coordinates) {
            long bits = Double.doubleToLongBits(c);
            hash = (31 * hash) ^ (int) (bits ^ (bits >> 32));
        }
        return hash;
    }

    @Override public String toString() {
        if (coordinates.length == 0) { return "Point()"; }
        String result = "Point(" + coordinates[0];
        for (int i = 1; i < coordinates.length; i++) { result = result + "," + coordinates[i]; }
        result = result + ")";
        return result;
    }
}
