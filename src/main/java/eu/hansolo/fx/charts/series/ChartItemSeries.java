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
import eu.hansolo.fx.charts.data.ChartItem;
import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;

import java.util.Arrays;
import java.util.List;


public class ChartItemSeries<T extends ChartItem> extends Series<T> {

    // ******************** Constructors **************************************
    public ChartItemSeries() {
        super();
    }
    public ChartItemSeries(final ChartType TYPE, final String NAME, final T... ITEMS) {
        super(Arrays.asList(ITEMS), TYPE, NAME);
    }
    public ChartItemSeries(final ChartType TYPE, final String NAME, final Paint FILL, final Paint STROKE, final T... ITEMS) {
        super(Arrays.asList(ITEMS), TYPE, NAME, FILL, STROKE, Symbol.NONE);
    }
    public ChartItemSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME, final Paint FILL, final Paint STROKE) {
       super(ITEMS, TYPE, NAME, FILL, STROKE, Symbol.NONE);
    }
    public ChartItemSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME, final Paint FILL, final Paint STROKE, final Symbol SYMBOL) {
        super(ITEMS, TYPE, NAME, FILL, STROKE, SYMBOL);
    }


    // ******************** Methods *******************************************
    @Override public ObservableList<T> getItems() { return items; }

    public double getMinValue() { return items.stream().mapToDouble(T::getValue).min().orElse(0d); }
    public double getMaxValue() { return items.stream().mapToDouble(T::getValue).max().orElse(100d); }

    public double getSumOfAllItems() { return items.stream().mapToDouble(T::getValue).sum(); }
}
