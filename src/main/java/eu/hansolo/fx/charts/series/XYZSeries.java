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
import eu.hansolo.fx.charts.data.XYZData;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.List;


public class XYZSeries<T extends XYZData> extends Series {


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


    // ******************** Methods *******************************************
    @Override public ObservableList<XYZData> getItems() { return items; }

    public double getMinX() { return ((XYZData) items.stream().min(Comparator.comparingDouble(XYZData::getX)).get()).getX(); }
    public double getMaxX() { return ((XYZData) items.stream().max(Comparator.comparingDouble(XYZData::getX)).get()).getX(); }

    public double getMinY() { return ((XYZData) items.stream().min(Comparator.comparingDouble(XYZData::getY)).get()).getY(); }
    public double getMaxY() { return ((XYZData) items.stream().max(Comparator.comparingDouble(XYZData::getY)).get()).getY(); }

    public double getMinZ() { return ((XYZData) items.stream().min(Comparator.comparingDouble(XYZData::getZ)).get()).getZ(); }
    public double getMaxZ() { return ((XYZData) items.stream().max(Comparator.comparingDouble(XYZData::getZ)).get()).getZ(); }

    public double getRangeX() { return getMaxX() - getMinX(); }
    public double getRangeY() { return getMaxY() - getMinY(); }
    public double getRangeZ() { return getMaxZ() - getMinZ(); }
}

