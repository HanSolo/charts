/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2022 Gerrit Grunwald.
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

package eu.hansolo.fx.charts.data;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;

import java.time.Instant;
import java.time.ZonedDateTime;


public class CandleChartItem extends ChartItem {
    private double                  _high;
    private DoubleProperty          high;
    private double                  _low;
    private DoubleProperty          low;
    private double                  _open;
    private DoubleProperty          open;
    private double                  _close;
    private DoubleProperty          close;
    private Instant                 _openTimestamp;
    private ObjectProperty<Instant> openTimestamp;
    private Instant                 _closeTimestamp;
    private ObjectProperty<Instant> closeTimestamp;


    public CandleChartItem() {
        this("", "", "", 0, 0, 0, 0, Instant.now());
    }
    public CandleChartItem(final String name, final String unit, final String description, final double low, final double open, final double close, final double high, final ZonedDateTime timestamp) {
        super();
        setName(name);
        setUnit(unit);
        setDescription(description);
        setTimestamp(timestamp);
        this._high  = high;
        this._low   = low;
        this._open  = open;
        this._close = close;
    }
    public CandleChartItem(final String name, final String unit, final String description, final double low, final double open, final double close, final double high, final Instant timestamp) {
        this(name, unit, description, low, open, close, high, timestamp, timestamp, timestamp);
    }
    public CandleChartItem(final String name, final String unit, final String description, final double low, final double open, final double close, final double high, final Instant timestamp, final Instant openTimestamp, final Instant closeTimestamp) {
        super();
        setName(name);
        setUnit(unit);
        setDescription(description);
        setTimestamp(timestamp);
        this._high           = high;
        this._low            = low;
        this._open           = open;
        this._close          = close;
        this._openTimestamp  = openTimestamp;
        this._closeTimestamp = closeTimestamp;
    }


    public double getHigh() { return null == high ? _high : high.get(); }
    public void setHigh(final double high) {
        if (null == this.high) {
            _high = high;
            fireChartEvt(UPDATE_EVENT);
        } else {
            this.high.set(high);
        }
    }
    public DoubleProperty highProperty() {
        if (null == high) {
            high = new DoublePropertyBase(_high) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() { return CandleChartItem.this; }
                @Override public String getName() { return "high"; }
            };
        }
        return high;
    }

    public double getLow() { return null == low ? _low : low.get(); }
    public void setLow(final double low) {
        if (null == this.low) {
            _low = low;
            fireChartEvt(UPDATE_EVENT);
        } else {
            this.low.set(low);
        }
    }
    public DoubleProperty lowProperty() {
        if (null == low) {
            low = new DoublePropertyBase(_low) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() { return CandleChartItem.this; }
                @Override public String getName() { return "low"; }
            };
        }
        return low;
    }

    public double getOpen() { return null == open ? _open : open.get(); }
    public void setOpen(final double open) {
        if (null == this.open) {
            _open = open;
            fireChartEvt(UPDATE_EVENT);
        } else {
            this.open.set(open);
        }
    }
    public DoubleProperty openProperty() {
        if (null == open) {
            open = new DoublePropertyBase(_open) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() { return CandleChartItem.this; }
                @Override public String getName() { return "open"; }
            };
        }
        return open;
    }

    public double getClose() { return null == close ? _close : close.get(); }
    public void setClose(final double close) {
        if (null == this.close) {
            _close = close;
            fireChartEvt(UPDATE_EVENT);
        } else {
            this.close.set(close);
        }
    }
    public DoubleProperty closeProperty() {
        if (null == close) {
            close = new DoublePropertyBase(_close) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() { return CandleChartItem.this; }
                @Override public String getName() { return "close"; }
            };
        }
        return close;
    }

    public Instant getOpenTimestamp() { return null == openTimestamp ? _openTimestamp : openTimestamp.get(); }
    public void setOpenTimestamp(final long openTimestampEpochSecond) { setOpenTimestamp(Instant.ofEpochSecond(openTimestampEpochSecond)); }
    public void setOpenTimestamp(final Instant openTimestamp) {
        if (null == this.openTimestamp) {
            _openTimestamp = openTimestamp;
        } else {
            this.openTimestamp.set(openTimestamp);
        }
    }
    public ObjectProperty<Instant> openTimestampProperty() {
        if (null == openTimestamp) {
            openTimestamp = new ObjectPropertyBase<>(_openTimestamp) {
                @Override public Object getBean() { return CandleChartItem.this; }
                @Override public String getName() { return "openTimestamp"; }
            };
            _openTimestamp = null;
        }
        return openTimestamp;
    }

    public Instant getCloseTimestamp() { return null == closeTimestamp ? _closeTimestamp : closeTimestamp.get(); }
    public void setCloseTimestamp(final long closeTimestampEpochSecond) { setCloseTimestamp(Instant.ofEpochSecond(closeTimestampEpochSecond));}
    public void setCloseTimestamp(final Instant closeTimestamp) {
        if (null == this.closeTimestamp) {
            _closeTimestamp = closeTimestamp;
        } else {
            this.closeTimestamp.set(closeTimestamp);
        }
    }
    public ObjectProperty<Instant> closeTimestampProperty() {
        if (null == closeTimestamp) {
            closeTimestamp = new ObjectPropertyBase<>(_closeTimestamp) {
                @Override public Object getBean() { return CandleChartItem.this; }
                @Override public String getName() { return "closeTimestamp"; }
            };
            _closeTimestamp = null;
        }
        return closeTimestamp;
    }
    
    public void validate() {
        if (Double.compare(getHigh(), getLow()) != 0 && getHigh() < getLow()) { throw new IllegalArgumentException("High cannot be smaller than low"); }
    }

    @Override public String toString() {
        return new StringBuilder().append("{\n")
                                  .append("  \"name\":").append(getName()).append(",\n")
                                  .append("  \"unit\":").append(getUnit()).append(",\n")
                                  .append("  \"description\":").append(getDescription()).append(",\n")
                                  .append("  \"high\":").append(getHigh()).append(",\n")
                                  .append("  \"low\":").append(getLow()).append(",\n")
                                  .append("  \"open\":").append(getOpen()).append(",\n")
                                  .append("  \"close\":").append(getClose()).append(",\n")
                                  .append("  \"open_timestamp\":").append(getOpenTimestamp().getEpochSecond()).append(",\n")
                                  .append("  \"close_timestamp\":").append(getCloseTimestamp().getEpochSecond()).append(",\n")
                                  .append("  \"timestamp\":").append(getTimestamp().getEpochSecond()).append("\n")
                                  .append("}")
                                  .toString();
    }
}
