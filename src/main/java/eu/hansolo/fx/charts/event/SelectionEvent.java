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


public class SelectionEvent<T extends ChartItem> {
    private final ChartItemSeries<T> SERIES;
    private final T                  ITEM;
    private final EventType          TYPE;


    // ******************** Constructors **************************************
    public SelectionEvent(final T ITEM) {
        this(null, ITEM, EventType.SELECTED);
    }
    public SelectionEvent(final ChartItemSeries<T> SERIES) {
        this(SERIES, null, EventType.SELECTED);
    }
    public SelectionEvent(final ChartItemSeries<T> SERIES, final T ITEM) {
        this(SERIES, ITEM, EventType.SELECTED);
    }
    public SelectionEvent(final ChartItemSeries<T> SERIES, final T ITEM, final EventType TYPE) {
        this.SERIES = SERIES;
        this.ITEM   = ITEM;
        this.TYPE   = TYPE;
    }


    // ******************** Methods *******************************************
    public ChartItemSeries<T> getSeries() { return SERIES; }

    public T getItem() { return ITEM; }

    public EventType getEventType() { return TYPE; }

    @Override public String toString() {
        String ret;
        if (null == SERIES) {
            if (null == ITEM) { ret = "{}"; }
            // Only Item
            ret = new StringBuilder().append("{\n")
                                     .append("  \"item\"  :\"").append(ITEM.getName()).append("\",\n")
                                     .append("  \"value\" :").append(ITEM.getValue()).append("\n")
                                     .append("}").toString();
        } else {
            if (null == ITEM) {
                // Only Series
                ret = new StringBuilder().append("{\n")
                                         .append("  \"series\":\"").append(SERIES.getName()).append("\",\n")
                                         .append("  \"sum\"   :").append(SERIES.getSumOfAllItems()).append("\n")
                                         .append("}").toString();
            } else {
                // Series and Item
                ret = new StringBuilder().append("{\n")
                                         .append("  \"series\":\"").append(SERIES.getName()).append("\",\n")
                                         .append("  \"sum\"   :").append(SERIES.getSumOfAllItems()).append(",\n")
                                         .append("  \"item\"  :\"").append(ITEM.getName()).append("\",\n")
                                         .append("  \"value\" :").append(ITEM.getValue()).append("\n")
                                         .append("}").toString();
            }
        }
        return ret;
    }
}
