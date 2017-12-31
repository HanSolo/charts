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

package eu.hansolo.fx.charts.event;

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.series.Series;


public class SelectionEvent<T extends ChartItem> {
    private ChartItemSeries<T> series;
    private T                  item;


    // ******************** Constructors **************************************
    public SelectionEvent(final T ITEM) {
        this(null, ITEM);
    }
    public SelectionEvent(final ChartItemSeries<T> SERIES) {
        this(SERIES, null);
    }
    public SelectionEvent(final ChartItemSeries<T> SERIES, final T ITEM) {
        series = SERIES;
        item   = ITEM;
    }


    // ******************** Methods *******************************************
    public ChartItemSeries<T> getSeries() { return series; }

    public T getItem() { return item; }

    @Override public String toString() {
        String ret;
        if (null == series) {
            if (null == item) { ret = "{}"; }
            // Only Item
            ret = new StringBuilder().append("{\n")
                                     .append("  \"item\"  :\"").append(item.getName()).append("\",\n")
                                     .append("  \"value\" :").append(item.getValue()).append("\n")
                                     .append("}").toString();
        } else {
            if (null == item) {
                // Only Series
                ret = new StringBuilder().append("{\n")
                                         .append("  \"series\":\"").append(series.getName()).append("\",\n")
                                         .append("  \"sum\"   :").append(series.getSumOfAllItems()).append("\n")
                                         .append("}").toString();
            } else {
                // Series and Item
                ret = new StringBuilder().append("{\n")
                                         .append("  \"series\":\"").append(series.getName()).append("\",\n")
                                         .append("  \"sum\"   :").append(series.getSumOfAllItems()).append(",\n")
                                         .append("  \"item\"  :\"").append(item.getName()).append("\",\n")
                                         .append("  \"value\" :").append(item.getValue()).append("\n")
                                         .append("}").toString();
            }
        }
        return ret;
    }
}
