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

package eu.hansolo.fx.charts.series;

import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.Symbol;
import eu.hansolo.fx.charts.data.XYZItem;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.List;


public class XYZSeries<T extends XYZItem> extends Series {

    // ******************** Constructors **************************************
    public XYZSeries() {
        this(null, ChartType.BUBBLE, "");
    }
    public XYZSeries(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "");
    }
    public XYZSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME) {
        super(ITEMS, TYPE, NAME);
    }
    public XYZSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME, final Symbol SYMBOL) {
        super(ITEMS, TYPE, NAME, SYMBOL);
    }


    // ******************** Methods *******************************************
    @Override public ObservableList<T> getItems() { return items; }

    public double getMinX() { return getItems().stream().min(Comparator.comparingDouble(T::getX)).get().getX(); }
    public double getMaxX() { return getItems().stream().max(Comparator.comparingDouble(T::getX)).get().getX(); }

    public double getMinY() { return getItems().stream().min(Comparator.comparingDouble(T::getY)).get().getY(); }
    public double getMaxY() { return getItems().stream().max(Comparator.comparingDouble(T::getY)).get().getY(); }

    public double getMinZ() { return getItems().stream().min(Comparator.comparingDouble(T::getZ)).get().getZ(); }
    public double getMaxZ() { return getItems().stream().max(Comparator.comparingDouble(T::getZ)).get().getZ(); }

    public double getRangeX() { return getMaxX() - getMinX(); }
    public double getRangeY() { return getMaxY() - getMinY(); }
    public double getRangeZ() { return getMaxZ() - getMinZ(); }

    public double getSumOfXValues() { return getItems().stream().mapToDouble(T::getX).sum(); }
    public double getSumOfYValues() { return getItems().stream().mapToDouble(T::getY).sum(); }
    public double getSumOfZValues() { return getItems().stream().mapToDouble(T::getZ).sum(); }
}

