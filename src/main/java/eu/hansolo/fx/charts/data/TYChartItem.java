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

import eu.hansolo.fx.charts.Symbol;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.paint.Color;

import java.time.LocalDateTime;


public class TYChartItem extends XYChartItem {
    private LocalDateTime                 _t;
    private ObjectProperty<LocalDateTime> t;


    // ******************** Constructors **********************************
    public TYChartItem() {
        this(LocalDateTime.now(), 0, "", Color.RED, Symbol.NONE);
    }
    public TYChartItem(final LocalDateTime T, final double Y, final String NAME) {
        this(T, Y, NAME, Color.RED, Symbol.NONE);
    }
    public TYChartItem(final LocalDateTime T, final double Y, final String NAME, final Color COLOR) {
        this(T, Y, NAME, COLOR, Symbol.NONE);
    }
    public TYChartItem(final LocalDateTime T, final double Y, final String NAME, final Color FILL, final Symbol SYMBOL) {
        super(T.toEpochSecond(Helper.getZoneOffset()), Y, NAME, FILL, Color.TRANSPARENT, SYMBOL);
        _t = T;
    }


    // ******************** Methods ***************************************
    public LocalDateTime getT() { return null == t ? _t : t.get(); }
    public void setT(final LocalDateTime T) {
        if (null == t) {
            _t = T;
            super.setX(_t.toEpochSecond(Helper.getZoneOffset()));
        } else {
            t.set(T);
        }
    }
    public ObjectProperty<LocalDateTime> tProperty() {
        if (null == t) {
            t = new ObjectPropertyBase<LocalDateTime>(_t) {
                @Override protected void invalidated() { TYChartItem.super.setX(get().toEpochSecond(Helper.getZoneOffset())); }
                @Override public Object getBean() { return TYChartItem.this; }
                @Override public String getName() { return "t"; }
            };
            _t = null;
        }
        return t;
    }


    @Override public String toString() {
        return new StringBuilder().append("{\n")
                                  .append("  \"name\":\"").append(getName()).append("\",\n")
                                  .append("  \"t\":").append(getT()).append(",\n")
                                  .append("  \"y\":").append(getY()).append(",\n")
                                  .append("  \"symbol\":\"").append(getSymbol().name()).append("\"\n")
                                  .append("}")
                                  .toString();
    }
}
