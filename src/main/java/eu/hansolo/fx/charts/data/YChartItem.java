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
import eu.hansolo.fx.charts.event.ItemEvent;
import eu.hansolo.fx.charts.event.ItemEventListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;

import java.util.concurrent.CopyOnWriteArrayList;


public class YChartItem implements YItem {
    private final ItemEvent DATA_EVENT = new ItemEvent(YChartItem.this);
    private CopyOnWriteArrayList<ItemEventListener> listeners;
    private double                                  _y;
    private DoubleProperty                          y;
    private String                                  _name;
    private StringProperty                          name;
    private Color                                   _color;
    private ObjectProperty<Color>                   color;
    private Symbol                                  _symbol;
    private ObjectProperty<Symbol>                  symbol;


    // ******************** Constructors **********************************
    public YChartItem() {
        this(0, "", Color.RED, Symbol.CIRCLE);
    }
    public YChartItem(final double Y, final String NAME) {
        this(Y, NAME, Color.RED, Symbol.CIRCLE);
    }
    public YChartItem(final double Y, final String NAME, final Color COLOR) {
        this(Y, NAME, COLOR, Symbol.CIRCLE);
    }
    public YChartItem(final double Y, final String NAME, final Color COLOR, final Symbol SYMBOL) {
        _y        = Y;
        _name     = NAME;
        _color    = COLOR;
        _symbol   = SYMBOL;
        listeners = new CopyOnWriteArrayList<>();
    }


    // ******************** Methods ***************************************
    @Override public double getY() { return null == y ? _y : y.get(); }
    @Override public void setY(final double Y) {
        if (null == y) {
            _y = Y;
            fireItemEvent(DATA_EVENT);
        } else {
            y.set(Y);
        }
    }
    @Override public DoubleProperty yProperty() {
        if (null == y) {
            y = new DoublePropertyBase(_y) {
                @Override protected void invalidated() { fireItemEvent(DATA_EVENT); }
                @Override public Object getBean() { return YChartItem.this; }
                @Override public String getName() { return "y"; }
            };
        }
        return y;
    }

    @Override public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) {
        if (null == name) {
            _name = NAME;
            fireItemEvent(DATA_EVENT);
        } else {
            name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override protected void invalidated() { fireItemEvent(DATA_EVENT); }
                @Override public Object getBean() { return YChartItem.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    @Override public Color getColor() { return null == color ? _color : color.get(); }
    public void setColor(final Color COLOR) {
        if (null == color) {
            _color = COLOR;
            fireItemEvent(DATA_EVENT);
        } else {
            color.set(COLOR);
        }
    }
    public ObjectProperty<Color> colorProperty() {
        if (null == color) {
            color = new ObjectPropertyBase<Color>(_color) {
                @Override protected void invalidated() { fireItemEvent(DATA_EVENT); }
                @Override public Object getBean() { return YChartItem.this; }
                @Override public String getName() { return "color"; }
            };
            _color = null;
        }
        return color;
    }

    @Override public Symbol getSymbol() { return null == symbol ? _symbol : symbol.get(); }
    public void setSymbol(final Symbol SYMBOL) {
        if (null == symbol) {
            _symbol = SYMBOL;
            fireItemEvent(DATA_EVENT);
        } else {
            symbol.set(SYMBOL);
        }
    }
    public ObjectProperty<Symbol> symbolProperty() {
        if (null == symbol) {
            symbol = new ObjectPropertyBase<Symbol>(_symbol) {
                @Override protected void invalidated() { fireItemEvent(DATA_EVENT); }
                @Override public Object getBean() {  return YChartItem.this;  }
                @Override public String getName() {  return "symbol";  }
            };
            _symbol = null;
        }
        return symbol;
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
                                  .append("  \"y\":").append(getY()).append(",\n")
                                  .append("  \"color\":\"").append(getColor().toString().replace("0x", "#")).append("\",\n")
                                  .append("  \"symbol\":\"").append(getSymbol().name()).append("\"\n")
                                  .append("}")
                                  .toString();
    }
}
