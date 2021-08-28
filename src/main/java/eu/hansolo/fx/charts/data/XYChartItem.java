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


/**
 * Created by hansolo on 16.07.17.
 */
public class XYChartItem implements XYItem, Comparable<XYChartItem> {
    private final ItemEvent                         ITEM_EVENT = new ItemEvent(XYChartItem.this);
    private CopyOnWriteArrayList<ItemEventListener> listeners;
    private double                                  _x;
    private DoubleProperty                          x;
    private double                                  _y;
    private DoubleProperty                          y;
    private String                                  _name;
    private StringProperty                          name;
    private Color                                   _fill;
    private ObjectProperty<Color>                   fill;
    private Color                                   _stroke;
    private ObjectProperty<Color>                   stroke;
    private Symbol                                  _symbol;
    private ObjectProperty<Symbol>                  symbol;
    private boolean                                 _isEmpty;
    private BooleanProperty                         isEmpty;
    private String                                  _tooltipText;
    private StringProperty                          tooltipText;


    // ******************** Constructors **********************************
    public XYChartItem() {
        this(0, 0, "", Color.RED, Color.TRANSPARENT, Symbol.NONE, "", false);
    }
    public XYChartItem(final boolean IS_EMPTY) {
        this(0, 0, "", Color.RED, Color.TRANSPARENT, Symbol.NONE, "", IS_EMPTY);
    }
    public XYChartItem(final double X, final double Y) {
        this(X, Y, "", Color.RED, Color.TRANSPARENT, Symbol.NONE, "", false);
    }
    public XYChartItem(final double X, final double Y, final boolean IS_EMPTY) {
        this(X, Y, "", Color.RED, Color.TRANSPARENT, Symbol.NONE, "", IS_EMPTY);
    }
    public XYChartItem(final double X, final double Y, final Color FILL) {
        this(X, Y, "", FILL, Color.TRANSPARENT, Symbol.NONE, "", false);
    }
    public XYChartItem(final double X, final double Y, final Color FILL, final String TOOLTIP) {
        this(X, Y, "", FILL, Color.TRANSPARENT, Symbol.NONE, TOOLTIP, false);
    }
    public XYChartItem(final double X, final double Y, final Color FILL, final boolean IS_EMPTY) {
        this(X, Y, "", FILL, Color.TRANSPARENT, Symbol.NONE, "", IS_EMPTY);
    }
    public XYChartItem(final double X, final double Y, final Color FILL, final String TOOLTIP, final boolean IS_EMPTY) {
        this(X, Y, "", FILL, Color.TRANSPARENT, Symbol.NONE, TOOLTIP, IS_EMPTY);
    }
    public XYChartItem(final double X, final double Y, final String NAME) {
        this(X, Y, NAME, Color.RED, Color.TRANSPARENT, Symbol.NONE, "", false);
    }
    public XYChartItem(final double X, final double Y, final String NAME, final String TOOLTIP) {
        this(X, Y, NAME, Color.RED, Color.TRANSPARENT, Symbol.NONE, TOOLTIP, false);
    }
    public XYChartItem(final double X, final double Y, final String NAME, final boolean IS_EMPTY) {
        this(X, Y, NAME, Color.RED, Color.TRANSPARENT, Symbol.NONE, "", IS_EMPTY);
    }
    public XYChartItem(final double X, final double Y, final String NAME, final String TOOLTIP, final boolean IS_EMPTY) {
        this(X, Y, NAME, Color.RED, Color.TRANSPARENT, Symbol.NONE, TOOLTIP, IS_EMPTY);
    }
    public XYChartItem(final double X, final double Y, final String NAME, final Color FILL) {
        this(X, Y, NAME, FILL, Color.TRANSPARENT, Symbol.NONE, "", false);
    }
    public XYChartItem(final double X, final double Y, final String NAME, final Color FILL, final String TOOLTIP) {
        this(X, Y, NAME, FILL, Color.TRANSPARENT, Symbol.NONE, TOOLTIP, false);
    }
    public XYChartItem(final double X, final double Y, final String NAME, final Color FILL, final boolean IS_EMPTY) {
        this(X, Y, NAME, FILL, Color.TRANSPARENT, Symbol.NONE, "", IS_EMPTY);
    }
    public XYChartItem(final double X, final double Y, final String NAME, final Color FILL, final String TOOLTIP, final boolean IS_EMPTY) {
        this(X, Y, NAME, FILL, Color.TRANSPARENT, Symbol.NONE, TOOLTIP, IS_EMPTY);
    }
    public XYChartItem(final double X, final double Y, final String NAME, final Color FILL, final Color STROKE, final Symbol SYMBOL) {
        this(X, Y, NAME, FILL, STROKE, SYMBOL, false);
    }
    public XYChartItem(final double X, final double Y, final String NAME, final Color FILL, final Color STROKE, final Symbol SYMBOL, final boolean IS_EMPTY) {
        this(X, Y, NAME, FILL, STROKE, SYMBOL, "", IS_EMPTY);
    }
    public XYChartItem(final double X, final double Y, final String NAME, final Color FILL, final Color STROKE, final Symbol SYMBOL, final String TOOLTIP, final boolean IS_EMPTY) {
        _x           = X;
        _y           = Y;
        _name        = NAME;
        _fill        = FILL;
        _stroke      = STROKE;
        _symbol      = SYMBOL;
        _isEmpty     = IS_EMPTY;
        _tooltipText = TOOLTIP;
        listeners    = new CopyOnWriteArrayList<>();
    }


    // ******************** Methods ***************************************
    @Override public double getX() { return null == x ? _x : x.get(); }
    @Override public void setX(final double X) {
        if (null == x) {
            _x = X;
            fireItemEvent(ITEM_EVENT);
        } else {
            x.set(X);
        }
    }
    @Override public DoubleProperty xProperty() {
        if (null == x) {
            x = new DoublePropertyBase(_x) {
                @Override protected void invalidated() {
                    fireItemEvent(ITEM_EVENT);
                }
                @Override public Object getBean() { return XYChartItem.this; }
                @Override public String getName() { return "x"; }
            };
        }
        return x;
    }

    @Override public double getY() { return null == y ? _y : y.get(); }
    @Override public void setY(final double Y) {
        if (null == y) {
            _y = Y;
            fireItemEvent(ITEM_EVENT);
        } else {
            y.set(Y);
        }
    }
    @Override public DoubleProperty yProperty() {
        if (null == y) {
            y = new DoublePropertyBase(_y) {
                @Override protected void invalidated() {
                    fireItemEvent(ITEM_EVENT);
                }
                @Override public Object getBean() { return XYChartItem.this; }
                @Override public String getName() { return "y"; }
            };
        }
        return y;
    }

    @Override public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) {
        if (null == name) {
            _name = NAME;
            fireItemEvent(ITEM_EVENT);
        } else {
            name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return XYChartItem.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    @Override public Color getFill() { return null == fill ? _fill : fill.get(); }
    public void setFill(final Color FILL) {
        if (null == fill) {
            _fill = FILL;
            fireItemEvent(ITEM_EVENT);
        } else {
            fill.set(FILL);
        }
    }
    public ObjectProperty<Color> fillProperty() {
        if (null == fill) {
            fill = new ObjectPropertyBase<Color>(_fill) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return XYChartItem.this; }
                @Override public String getName() { return "fill"; }
            };
            _fill = null;
        }
        return fill;
    }

    @Override public Color getStroke() { return null == stroke ? _stroke : stroke.get(); }
    public void setStroke(final Color STROKE) {
        if (null == stroke) {
            _stroke = STROKE;
            fireItemEvent(ITEM_EVENT);
        } else {
            stroke.set(STROKE);
        }
    }
    public ObjectProperty<Color> strokeProperty() {
        if (null == stroke) {
            stroke = new ObjectPropertyBase<Color>(_stroke) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return XYChartItem.this; }
                @Override public String getName() { return "stroke"; }
            };
            _stroke = null;
        }
        return stroke;
    }

    @Override public Symbol getSymbol() { return null == symbol ? _symbol : symbol.get(); }
    public void setSymbol(final Symbol SYMBOL) {
        if (null == symbol) {
            _symbol = SYMBOL;
            fireItemEvent(ITEM_EVENT);
        } else {
            symbol.set(SYMBOL);
        }
    }
    public ObjectProperty<Symbol> symbolProperty() {
        if (null == symbol) {
            symbol = new ObjectPropertyBase<Symbol>(_symbol) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() {  return XYChartItem.this;  }
                @Override public String getName() {  return "symbol";  }
            };
            _symbol = null;
        }
        return symbol;
    }

    @Override public String getTooltipText() { return null == tooltipText ? _tooltipText : tooltipText.get(); }
    @Override public void setTooltipText(final String TOOLTIP) {
        if (null == tooltipText) {
            _tooltipText = TOOLTIP;
            fireItemEvent(ITEM_EVENT);
        } else {
            tooltipText.set(TOOLTIP);
        }
    }
    @Override public StringProperty tooltipTextProperty() {
        if (null == tooltipText) {
            tooltipText = new StringPropertyBase(_tooltipText) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return XYChartItem.this; }
                @Override public String getName() { return "tooltip"; }
            };
            _tooltipText = null;
        }
        return tooltipText;
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
                @Override public Object getBean() { return XYChartItem.this; }
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
                                  .append("  \"x\":").append(getX()).append(",\n")
                                  .append("  \"y\":").append(getY()).append(",\n")
                                  .append("  \"symbol\":\"").append(getSymbol().name()).append("\"\n")
                                  .append("}")
                                  .toString();
    }

    @Override public int compareTo(final XYChartItem ITEM) { return Double.compare(getX(), ITEM.getX()); }
}
