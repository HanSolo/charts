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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;
import javafx.scene.paint.Color;

import java.util.concurrent.CopyOnWriteArrayList;


public class MatrixChartItem implements MatrixItem {
    private final ItemEvent                         ITEM_EVENT = new ItemEvent(MatrixChartItem.this);
    private CopyOnWriteArrayList<ItemEventListener> listeners;
    private int                                     _x;
    private IntegerProperty                         x;
    private int                                     _y;
    private IntegerProperty                         y;
    private double                                  _z;
    private DoubleProperty                          z;
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


    // ******************** Constructors **********************************
    public MatrixChartItem() {
        this(0, 0, 0, "", Color.RED, false);
    }
    public MatrixChartItem(final boolean IS_EMPTY) {
        this(0, 0, 0, "", Color.RED, IS_EMPTY);
    }
    public MatrixChartItem(final int X, final int Y, final double Z) {
        this(X, Y, Z, "", Color.RED, false);
    }
    public MatrixChartItem(final int X, final int Y, final double Z, final boolean IS_EMPTY) {
        this(X, Y, Z, "", Color.RED, IS_EMPTY);
    }
    public MatrixChartItem(final int X, final int Y, final double Z, final String NAME) {
        this(X, Y, Z, NAME, Color.RED, false);
    }
    public MatrixChartItem(final int X, final int Y, final double Z, final String NAME, final boolean IS_EMPTY) {
        this(X, Y, Z, NAME, Color.RED, IS_EMPTY);
    }
    public MatrixChartItem(final int X, final int Y, final double Z, final String NAME, final Color FILL) {
        this(X, Y, Z, NAME, FILL, false);
    }
    public MatrixChartItem(final int X, final int Y, final double Z, final String NAME, final Color FILL, final boolean IS_EMPTY) {
        _x        = X;
        _y        = Y;
        _z        = Z;
        _name     = NAME;
        _fill     = FILL;
        _stroke   = Color.TRANSPARENT;
        _symbol   = Symbol.NONE;
        _isEmpty  = IS_EMPTY;
        listeners = new CopyOnWriteArrayList<>();
    }


    // ******************** Methods ***************************************
    @Override public int getX() { return null == x ? _x : x.get(); }
    @Override public void setX(final int X) {
        if (null == x) {
            _x = X;
            fireItemEvent(ITEM_EVENT);
        } else {
            x.set(X);
        }
    }
    public IntegerProperty xProperty() {
        if (null == x) {
            x = new IntegerPropertyBase(_x) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return MatrixChartItem.this; }
                @Override public String getName() { return "x"; }
            };
        }
        return x;
    }

    @Override public int getY() { return null == y ? _y : y.get(); }
    @Override public void setY(final int Y) {
        if (null == y) {
            _y = Y;
            fireItemEvent(ITEM_EVENT);
        } else {
            y.set(Y);
        }
    }
    @Override public IntegerProperty yProperty() {
        if (null == y) {
            y = new IntegerPropertyBase(_y) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return MatrixChartItem.this; }
                @Override public String getName() { return "y"; }
            };
        }
        return y;
    }

    @Override public double getZ() { return null == z ? _z : z.get(); }
    @Override public void setZ(final double Z) {
        if (null == z) {
            _z = Z;
            fireItemEvent(ITEM_EVENT);
        } else {
            z.set(Z);
        }
    }
    @Override public DoubleProperty zProperty() {
        if (null == z) {
            z = new DoublePropertyBase(_z) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return MatrixChartItem.this; }
                @Override public String getName() { return "z"; }
            };
        }
        return z;
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
                @Override public Object getBean() { return MatrixChartItem.this; }
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
                @Override public Object getBean() { return MatrixChartItem.this; }
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
                @Override public Object getBean() { return MatrixChartItem.this; }
                @Override public String getName() { return "stroke"; }
            };
            _stroke = null;
        }
        return stroke;
    }

    @Override public Symbol getSymbol() { return Symbol.NONE; }
    public void setSymbol(final Symbol SYMBOL) {}
    public ObjectProperty<Symbol> symbolProperty() {
        if (null == symbol) {
            symbol = new ObjectPropertyBase<Symbol>(_symbol) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() {  return MatrixChartItem.this;  }
                @Override public String getName() {  return "symbol";  }
            };
            _symbol = null;
        }
        return symbol;
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
                @Override public Object getBean() { return MatrixChartItem.this; }
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
                                  .append("  \"z\":").append(getZ()).append(",\n")
                                  .append("  \"symbol\":\"").append(getSymbol().name()).append("\"\n")
                                  .append("}")
                                  .toString();
    }
}
