/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
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

import eu.hansolo.fx.charts.Symbol;
import eu.hansolo.fx.charts.event.ItemEvent;
import eu.hansolo.fx.charts.event.ItemEventListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;

import java.util.concurrent.CopyOnWriteArrayList;


public class BubbleGridChartItem implements BubbleGridItem {
    private final ItemEvent                               ITEM_EVENT = new ItemEvent(BubbleGridChartItem.this);
    private       CopyOnWriteArrayList<ItemEventListener> listeners;
    private       String                                  _name;
    private       StringProperty                          name;
    private       Color                                   _fill;
    private       ObjectProperty<Color>                   fill;
    private       Color                                   _stroke;
    private       ObjectProperty<Color>                   stroke;
    private       Symbol                                  _symbol;
    private       ObjectProperty<Symbol>                  symbol;
    private       ChartItem                               _categoryX;
    private       ObjectProperty<ChartItem>               categoryX;
    private       ChartItem                               _categoryY;
    private       ObjectProperty<ChartItem>               categoryY;
    private       double                                  _value;
    private       DoubleProperty                          value;
    private       boolean                                 _isEmpty;
    private       BooleanProperty                         isEmpty;


    BubbleGridChartItem() {
        this("", Color.BLACK, Color.BLACK, Symbol.NONE, new ChartItem(), new ChartItem(), 0, false);
    }
    BubbleGridChartItem(final boolean isEmpty) {
        this("", Color.BLACK, Color.BLACK, Symbol.NONE, new ChartItem(), new ChartItem(), 0, isEmpty);
    }
    BubbleGridChartItem(final String name, final Color fill, final Color stroke, final Symbol symbol, final ChartItem categoryX, final ChartItem categoryY, final double value) {
        this(name, fill, stroke, symbol, categoryX, categoryY, value, false);
    }
    BubbleGridChartItem(final String name, final Color fill, final Color stroke, final Symbol symbol, final ChartItem categoryX, final ChartItem categoryY, final double value, final boolean isEmpty) {
        _name      = name;
        _fill      = fill;
        _stroke    = stroke;
        _symbol    = symbol;
        _categoryX = categoryX;
        _categoryY = categoryY;
        _value     = value;
        _isEmpty   = isEmpty;
        listeners  = new CopyOnWriteArrayList<>();
    }

    @Override public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String name) {
        if (null == this.name) {
            _name = name;
            fireItemEvent(ITEM_EVENT);
        } else {
            this.name.set(name);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return BubbleGridChartItem.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    @Override public Color getFill() { return null == fill ? _fill : fill.get(); }
    public void setFill(final Color fill) {
        if (null == this.fill) {
            _fill = fill;
            fireItemEvent(ITEM_EVENT);
        } else {
            this.fill.set(fill);
        }
    }
    public ObjectProperty<Color> fillProperty() {
        if (null == fill) {
            fill = new ObjectPropertyBase<>(_fill) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return BubbleGridChartItem.this; }
                @Override public String getName() { return "fill"; }
            };
            _fill = null;
        }
        return fill;
    }

    @Override public Color getStroke() { return null == stroke ? _stroke : stroke.get(); }
    public void setStroke(final Color stroke) {
        if (null == this.stroke) {
            _stroke = stroke;
            fireItemEvent(ITEM_EVENT);
        } else {
            this.stroke.set(stroke);
        }
    }
    public ObjectProperty<Color> strokeProperty() {
        if (null == stroke) {
            stroke = new ObjectPropertyBase<>(_stroke) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return BubbleGridChartItem.this; }
                @Override public String getName() { return "stroke"; }
            };
            _stroke = null;
        }
        return stroke;
    }

    @Override public Symbol getSymbol() { return null == symbol ? _symbol : symbol.get(); }
    @Override public void setSymbol(final Symbol symbol) {
        if (null == this.symbol) {
            _symbol = symbol;
            fireItemEvent(ITEM_EVENT);
        } else {
            this.symbol.set(symbol);
        }
    }
    public ObjectProperty<Symbol> symbolProperty() {
        if (null == symbol) {
            symbol = new ObjectPropertyBase<>(_symbol) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return BubbleGridChartItem.this; }
                @Override public String getName() { return "symbol"; }
            };
            _symbol = null;
        }
        return symbol;
    }

    @Override public ChartItem getCategoryX() { return null == categoryX ? _categoryX : categoryX.get(); }
    public void setCategoryX(final ChartItem categoryX) {
        if (null == this.categoryX) {
            _categoryX = categoryX;
            fireItemEvent(ITEM_EVENT);
        } else {
            this.categoryX.set(categoryX);
        }
    }
    @Override public ObjectProperty<ChartItem> categoryXProperty() {
        if (null == categoryX) {
            categoryX = new ObjectPropertyBase<>(_categoryX) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return BubbleGridChartItem.this; }
                @Override public String getName() { return "categoryX"; }
            };
            _categoryX = null;
        }
        return categoryX;
    }

    @Override public ChartItem getCategoryY() { return null == categoryY ? _categoryY : categoryY.get(); }
    public void setCategoryY(final ChartItem categoryY) {
        if (null == this.categoryY) {
            _categoryY = categoryY;
            fireItemEvent(ITEM_EVENT);
        } else {
            this.categoryY.set(categoryY);
        }
    }
    @Override public ObjectProperty<ChartItem> categoryYProperty() {
        if (null == categoryY) {
            categoryY = new ObjectPropertyBase<>(_categoryY) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return BubbleGridChartItem.this; }
                @Override public String getName() { return "categoryY"; }
            };
            _categoryY = null;
        }
        return categoryY;
    }

    @Override public double getValue() { return null == value ? _value : value.get(); }
    @Override public void setValue(final double value) {
        if (null == this.value) {
            _value = value;
            fireItemEvent(ITEM_EVENT);
        } else {
            this.value.set(value);
        }
    }
    @Override public DoubleProperty valueProperty() {
        if (null == value) {
            value = new DoublePropertyBase(_value) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return BubbleGridChartItem.this; }
                @Override public String getName() { return "value"; }
            };
        }
        return value;
    }

    @Override public boolean isEmptyItem() { return null == isEmpty ? _isEmpty : isEmpty.get(); }
    public void setIsEmpty(final boolean isEmpty) {
        if (null == this.isEmpty) {
            _isEmpty = isEmpty;
            fireItemEvent(ITEM_EVENT);
        } else {
            this.isEmpty.set(isEmpty);
        }
    }
    public BooleanProperty isEmptyProperty() {
        if (null == isEmpty) {
            isEmpty = new BooleanPropertyBase(_isEmpty) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return BubbleGridChartItem.this; }
                @Override public String getName() { return "isEmpty"; }
            };
        }
        return isEmpty;
    }


    // ******************** Event handling ************************************
    public void setOnItemEvent(final ItemEventListener LISTENER) { addItemEventListener(LISTENER); }
    public void addItemEventListener(final ItemEventListener LISTENER) { if (!listeners.contains(LISTENER)) listeners.add(LISTENER); }
    public void removeItemEventListener(final ItemEventListener LISTENER) { if (listeners.contains(LISTENER)) listeners.remove(LISTENER); }

    public void fireItemEvent(final ItemEvent EVENT) {
        for (ItemEventListener listener : listeners) { listener.onItemEvent(EVENT); }
    }


    @Override public String toString() {
        return new StringBuilder().append("{\n")
                                  .append("  \"name\":\"").append(getName()).append("\",\n")
                                  .append("  \"categoryX\":").append(getCategoryX()).append(",\n")
                                  .append("  \"categoryY\":").append(getCategoryY()).append(",\n")
                                  .append("  \"value\":").append(getValue()).append(",\n")
                                  .append("  \"symbol\":\"").append(getSymbol().name()).append("\"\n")
                                  .append("}")
                                  .toString();
    }
}
