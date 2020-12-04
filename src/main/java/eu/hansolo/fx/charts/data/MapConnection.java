/*
 * Copyright (c) 2020 by Gerrit Grunwald
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

import eu.hansolo.fx.charts.event.MapConnectionEvent;
import eu.hansolo.fx.charts.event.MapConnectionEventListener;
import eu.hansolo.fx.charts.event.EventType;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.MapPoint;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class MapConnection {
    private final MapConnectionEvent               SELECTED_EVENT = new MapConnectionEvent(MapConnection.this, EventType.SELECTED);
    private final MapConnectionEvent               UPDATED_EVENT  = new MapConnectionEvent(MapConnection.this, EventType.UPDATE);
    private       List<MapConnectionEventListener> listeners;
    private       MapPoint                         _incomingItem;
    private       ObjectProperty<MapPoint>         incomingItem;
    private       MapPoint                         _outgoingItem;
    private       ObjectProperty<MapPoint>         outgoingItem;
    private       Color                            _stroke;
    private       ObjectProperty<Color>            stroke;
    private       Color                            _startColor;
    private       ObjectProperty<Color>            startColor;
    private       Color                            _endColor;
    private       ObjectProperty<Color>            endColor;
    private       boolean                          _gradientFill;
    private       BooleanProperty                  gradientFill;
    private       double                           _value;
    private       DoubleProperty                   value;
    private       double                           _lineWidth;
    private       DoubleProperty                   lineWidth;
    private       String                           _tooltipText;
    private       StringProperty                   tooltipText;


    public MapConnection(final MapPoint OUTGOING_ITEM, final MapPoint INCOMING_ITEM) {
        this(OUTGOING_ITEM, INCOMING_ITEM, 0, Color.BLACK, Color.BLUE, Color.RED, false, 1, "");
    }
    public MapConnection(final MapPoint OUTGOING_ITEM, final MapPoint INCOMING_ITEM, final Color STROKE) {
        this(OUTGOING_ITEM, INCOMING_ITEM, 0, STROKE, Color.BLUE, Color.RED, false, 1, "");
    }
    public MapConnection(final MapPoint OUTGOING_ITEM, final MapPoint INCOMING_ITEM, final double VALUE, final Color STROKE) {
        this(OUTGOING_ITEM, INCOMING_ITEM, VALUE, STROKE, Color.BLUE, Color.RED, false, 1, "");
    }
    public MapConnection(final MapPoint OUTGOING_ITEM, final MapPoint INCOMING_ITEM, final double VALUE, final Color START_COLOR, final Color END_COLOR, final boolean GRADIENT_FILL) {
        this(OUTGOING_ITEM, INCOMING_ITEM, VALUE, Color.BLACK, START_COLOR, END_COLOR, GRADIENT_FILL, 1, "");
    }
    public MapConnection(final MapPoint OUTGOING_ITEM, final MapPoint INCOMING_ITEM, final double VALUE, final Color START_COLOR, final Color END_COLOR, final boolean GRADIENT_FILL, final String TOOLTIP_TEXT) {
        this(OUTGOING_ITEM, INCOMING_ITEM, VALUE, Color.BLACK, START_COLOR, END_COLOR, GRADIENT_FILL, 1, TOOLTIP_TEXT);
    }
    public MapConnection(final MapPoint OUTGOING_ITEM, final MapPoint INCOMING_ITEM, final double VALUE, final Color STROKE, final String TOOLTIP_TEXT) {
        this(OUTGOING_ITEM, INCOMING_ITEM, VALUE, STROKE, Color.BLUE, Color.RED, false, 1, TOOLTIP_TEXT);
    }
    public MapConnection(final MapPoint OUTGOING_ITEM, final MapPoint INCOMING_ITEM, final double VALUE, final Color STROKE, final Color START_COLOR, final Color END_COLOR, final boolean GRADIENT_FILL, final double LINE_WIDTH, final String TOOLTIP_TEXT) {
        listeners     = new CopyOnWriteArrayList<>();
        _outgoingItem = OUTGOING_ITEM;
        _incomingItem = INCOMING_ITEM;
        _value        = VALUE;
        _stroke       = STROKE;
        _tooltipText  = TOOLTIP_TEXT;
        _startColor   = START_COLOR;
        _endColor     = END_COLOR;
        _gradientFill = GRADIENT_FILL;
        _lineWidth    = Helper.clamp(0.5, 10, LINE_WIDTH);
    }


    public MapPoint getIncomingItem() { return null == incomingItem ? _incomingItem : incomingItem.get(); }
    public void setIncomingItem(final MapPoint ITEM1) {
        if (null == incomingItem) {
            _incomingItem = ITEM1;
            fireMapConnectionEvent(UPDATED_EVENT);
        } else {
            incomingItem.set(ITEM1);
        }
    }
    public ObjectProperty<MapPoint> incomingItemProperty() {
        if (null == incomingItem) {
            incomingItem = new ObjectPropertyBase<MapPoint>(_incomingItem) {
                @Override protected void invalidated() { fireMapConnectionEvent(UPDATED_EVENT); }
                @Override public Object getBean() { return MapConnection.this; }
                @Override public String getName() { return "item1"; }
            };
            _incomingItem = null;
        }
        return incomingItem;
    }

    public MapPoint getOutgoingItem() { return null == outgoingItem ? _outgoingItem : outgoingItem.get(); }
    public void setOutgoingItem(final MapPoint ITEM2) {
        if (null == outgoingItem) {
            _outgoingItem = ITEM2;
            fireMapConnectionEvent(UPDATED_EVENT);
        } else {
            outgoingItem.set(ITEM2);
        }

    }
    public ObjectProperty<MapPoint> outgoingItemProperty() {
        if (null == outgoingItem) {
            outgoingItem = new ObjectPropertyBase<MapPoint>(_outgoingItem) {
                @Override protected void invalidated() { fireMapConnectionEvent(UPDATED_EVENT); }
                @Override public Object getBean() { return MapConnection.this; }
                @Override public String getName() { return "item2"; }
            };
            _outgoingItem = null;
        }
        return outgoingItem;
    }

    public double getValue() { return null == value ? _value : value.get(); }
    public ReadOnlyDoubleProperty valueProperty() {
        if (null == value) {
            value = new DoublePropertyBase(_value) {
                @Override public Object getBean() { return MapConnection.this; }
                @Override public String getName() { return "value"; }
            };
        }
        return value;
    }

    public Color getStroke() { return null == stroke ? _stroke : stroke.get(); }
    public void setStroke(final Color FILL) {
        if (null == stroke) {
            _stroke = FILL;
            fireMapConnectionEvent(UPDATED_EVENT);
        } else {
            stroke.set(FILL);
        }
    }
    public ObjectProperty<Color> strokeProperty() {
        if (null == stroke) {
            stroke = new ObjectPropertyBase<>(_stroke) {
                @Override protected void invalidated() { fireMapConnectionEvent(UPDATED_EVENT); }
                @Override public Object getBean() { return MapConnection.this; }
                @Override public String getName() { return "fill"; }
            };
            _stroke = null;
        }
        return stroke;
    }

    public Color getStartColor() { return null == startColor ? _startColor : startColor.get(); }
    public void setStartColor(final Color START_COLOR) {
        if (null == startColor) {
            _startColor = START_COLOR;
            fireMapConnectionEvent(UPDATED_EVENT);
        } else {
            startColor.set(START_COLOR);
        }
    }
    public ObjectProperty<Color> startColorProperty() {
        if (null == startColor) {
            startColor = new ObjectPropertyBase<>(_startColor) {
                @Override protected void invalidated() { fireMapConnectionEvent(UPDATED_EVENT); }
                @Override public Object getBean() { return MapConnection.this; }
                @Override public String getName() { return "startColor"; }
            };
            _startColor = null;
        }
        return startColor;
    }

    public Color getEndColor() { return null == endColor ? _endColor : endColor.get(); }
    public void setEndColor(final Color END_COLOR) {
        if (null == endColor) {
            _endColor = END_COLOR;
            fireMapConnectionEvent(UPDATED_EVENT);
        } else {
            endColor.set(END_COLOR);
        }
    }
    public ObjectProperty<Color> endColorProperty() {
        if (null == endColor) {
            endColor = new ObjectPropertyBase<>(_endColor) {
                @Override protected void invalidated() { fireMapConnectionEvent(UPDATED_EVENT); }
                @Override public Object getBean() { return MapConnection.this; }
                @Override public String getName() { return "endColor"; }
            };
            _endColor = null;
        }
        return endColor;
    }

    public boolean getGradientFill() { return null == gradientFill ? _gradientFill : gradientFill.get(); }
    public void setGradientFill(final boolean GRADIENT_FILL) {
        if (null == gradientFill) {
            _gradientFill = GRADIENT_FILL;
            fireMapConnectionEvent(UPDATED_EVENT);
        } else {
            gradientFill.set(GRADIENT_FILL);
        }
    }
    public BooleanProperty gradientFillProperty() {
        if (null == gradientFill) {
            gradientFill = new BooleanPropertyBase(_gradientFill) {
                @Override protected void invalidated() { fireMapConnectionEvent(UPDATED_EVENT); }
                @Override public Object getBean() { return MapConnection.this; }
                @Override public String getName() { return "gradientFill"; }
            };
        }
        return gradientFill;
    }

    public double getLineWidth() { return null == lineWidth ? _lineWidth : lineWidth.get(); }
    public void setLineWidth(final double LINE_WIDTH) {
        if (null == lineWidth) {
            _lineWidth = Helper.clamp(0.5, 10, LINE_WIDTH);
            fireMapConnectionEvent(UPDATED_EVENT);
        } else {
            lineWidth.set(LINE_WIDTH);
        }
    }
    public DoubleProperty lineWidthProperty() {
        if (null == lineWidth) {
            lineWidth = new DoublePropertyBase(_lineWidth) {
                @Override protected void invalidated() {
                    set(Helper.clamp(0.5, 10, get()));
                    fireMapConnectionEvent(UPDATED_EVENT);
                }
                @Override public Object getBean() { return MapConnection.this; }
                @Override public String getName() { return "lineWidth"; }
            };
        }
        return lineWidth;
    }

    public String getTooltipText() { return null == tooltipText ? _tooltipText : tooltipText.get(); }
    public void setTooltipText(final String TOOLTIP_TEXT) {
        if (null == tooltipText) {
            _tooltipText = TOOLTIP_TEXT;
        } else {
            tooltipText.set(TOOLTIP_TEXT);
        }
    }
    public StringProperty tooltipTextProperty() {
        if (null == tooltipText) {
            tooltipText = new StringPropertyBase(_tooltipText) {
                @Override public Object getBean() { return MapConnection.this; }
                @Override public String getName() { return "tooltipText"; }
            };
        }
        return tooltipText;
    }


    // ******************** Event Handling ************************************
    public void setOnMapConnectionEvent(final MapConnectionEventListener LISTENER) { addMapConnectionEventListener(LISTENER); }
    public void addMapConnectionEventListener(final MapConnectionEventListener LISTENER) { if (!listeners.contains(LISTENER)) { listeners.add(LISTENER); } }
    public void removeMapConnectionEventListener(final MapConnectionEventListener LISTENER) { if (listeners.contains(LISTENER)) { listeners.remove(LISTENER); } }

    public void fireMapConnectionEvent(final MapConnectionEvent EVENT) { listeners.forEach(listener -> listener.onMapConnectionEvent(EVENT)); }
}
