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
import eu.hansolo.fx.charts.data.YItem;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Comparator;
import java.util.List;


public class YSeries<T extends YItem> extends Series {

    // ******************** Constructors **************************************
    public YSeries() {
        this(null, ChartType.DONUT, "", Color.BLACK, Color.TRANSPARENT);
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "", Color.BLACK, Color.TRANSPARENT);
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME) {
        this(ITEMS, TYPE, NAME, Color.BLACK, Color.TRANSPARENT);
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE, final Paint STROKE, final Paint FILL) {
        this(ITEMS, TYPE, "", STROKE, FILL);
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME, final Paint STROKE, final Paint FILL) {
        super(ITEMS, TYPE, NAME, STROKE, FILL);
    }


    // ******************** Methods *******************************************
    @Override public ObservableList<T> getItems() { return items; }

    public double getMinY() { return getItems().stream().min(Comparator.comparingDouble(T::getY)).get().getY(); }
    public double getMaxY() { return getItems().stream().max(Comparator.comparingDouble(T::getY)).get().getY(); }

    public double getSumOfYValues() { return getItems().stream().mapToDouble(T::getY).sum(); }
}
