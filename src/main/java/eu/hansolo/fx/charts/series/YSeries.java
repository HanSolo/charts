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
import eu.hansolo.fx.charts.data.ValueItem;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Comparator;
import java.util.List;


public class YSeries<T extends ValueItem> extends Series {

    // ******************** Constructors **************************************
    public YSeries() {
        this(null, ChartType.DONUT, "", Color.TRANSPARENT, Color.BLACK, Symbol.CIRCLE);
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "", Color.TRANSPARENT, Color.BLACK, Symbol.CIRCLE);
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME) {
        this(ITEMS, TYPE, NAME, Color.TRANSPARENT, Color.BLACK, Symbol.CIRCLE);
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE, final Paint FILL, final Paint STROKE) {
        this(ITEMS, TYPE, "", FILL, STROKE, Symbol.CIRCLE);
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME, final Paint FILL, final Paint STROKE) {
        super(ITEMS, TYPE, NAME, FILL, STROKE, Symbol.CIRCLE);
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME, final Paint FILL, final Paint STROKE, final Symbol SYMBOL) {
        super(ITEMS, TYPE, NAME, FILL, STROKE, SYMBOL);
    }


    // ******************** Methods *******************************************
    @Override public ObservableList<T> getItems() { return items; }

    public double getMinY() { return getItems().stream().min(Comparator.comparingDouble(T::getValue)).get().getValue(); }
    public double getMaxY() { return getItems().stream().max(Comparator.comparingDouble(T::getValue)).get().getValue(); }

    public double getSumOfYValues() { return getItems().stream().mapToDouble(T::getValue).sum(); }
}
