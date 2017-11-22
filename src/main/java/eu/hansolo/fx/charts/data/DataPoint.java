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

package eu.hansolo.fx.charts.data;

import eu.hansolo.fx.charts.tools.Point;


public class DataPoint extends Point {
    private double value;


    // ******************** Constructors **************************************
    public DataPoint() {
        this(0, 0, 0);
    }
    public DataPoint(final double X, final double Y) {
        this(X, Y, 0);
    }
    public DataPoint(final double X, final double Y, final double VALUE) {
        super(X, Y);
        value = VALUE;
    }


    // ******************** Methods *******************************************
    public double getValue() { return value; }
    public void setValue(final double VALUE) { value = VALUE; }

    @Override public String toString() {
        return new StringBuilder().append("x: ").append(getX()).append(", y: ").append(getY()).append(", value: ").append(value).toString();
    }
}
