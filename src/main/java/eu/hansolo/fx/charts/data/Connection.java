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

import eu.hansolo.fx.charts.event.ConnectionEvent;
import eu.hansolo.fx.charts.event.ConnectionEventListener;
import eu.hansolo.fx.charts.event.EventType;
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


public class Connection {
//    private final ConnectionEvent         SELECTED_EVENT = new ConnectionEvent(Connection.this, EventType.SELECTED);
    private List<ConnectionEventListener> listeners;
    private PlotItem                      _incomingItem;
    private ObjectProperty<PlotItem>      incomingItem;
    private PlotItem                      _outgoingItem;
    private ObjectProperty<PlotItem>      outgoingItem;
    private Color                         _fill;
    private ObjectProperty<Color>         fill;
    private double                        _value;
    private DoubleProperty                value;
    private String                        _tooltipText;
    private StringProperty                tooltipText;


    public Connection(final PlotItem INCOMING_ITEM, final PlotItem OUTGOING_ITEM, final double VALUE, final Color FILL) {
        this(INCOMING_ITEM, OUTGOING_ITEM, VALUE, FILL, "");
    }
    public Connection(final PlotItem INCOMING_ITEM, final PlotItem OUTGOING_ITEM, final double VALUE, final Color FILL, final String TOOLTIP_TEXT) {
        listeners     = new CopyOnWriteArrayList<>();
        _incomingItem = INCOMING_ITEM;
        _outgoingItem = OUTGOING_ITEM;
        _value        = VALUE;
        _fill         = FILL;
        _tooltipText  = TOOLTIP_TEXT;
    }


    public PlotItem getIncomingItem() { return null == incomingItem ? _incomingItem : incomingItem.get(); }
    public void setIncomingItem(final PlotItem ITEM1) {
        if (null == incomingItem) {
            _incomingItem = ITEM1;
        } else {
            incomingItem.set(ITEM1);
        }
    }
    public ObjectProperty<PlotItem> incomingItemProperty() {
        if (null == incomingItem) {
            incomingItem = new ObjectPropertyBase<PlotItem>(_incomingItem) {
                @Override public Object getBean() { return Connection.this; }
                @Override public String getName() { return "item1"; }
            };
            _incomingItem = null;
        }
        return incomingItem;
    }

    public PlotItem getOutgoingItem() { return null == outgoingItem ? _outgoingItem : outgoingItem.get(); }
    public void setOutgoingItem(final PlotItem ITEM2) {
        if (null == outgoingItem) {
            _outgoingItem = ITEM2;
        } else {
            outgoingItem.set(ITEM2);
        }

    }
    public ObjectProperty<PlotItem> outgoingItemProperty() {
        if (null == outgoingItem) {
            outgoingItem = new ObjectPropertyBase<PlotItem>(_outgoingItem) {
                @Override public Object getBean() { return Connection.this; }
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
                @Override public Object getBean() { return Connection.this; }
                @Override public String getName() { return "value"; }
            };
        }
        return value;
    }

    public Color getFill() { return null == fill ? _fill : fill.get(); }
    public void setFill(final Color FILL) {
        if (null == fill) {
            _fill = FILL;
        } else {
            fill.set(FILL);
        }
    }
    public ObjectProperty<Color> fillProperty() {
        if (null == fill) {
            fill = new ObjectPropertyBase<Color>(_fill) {
                @Override public Object getBean() { return Connection.this; }
                @Override public String getName() { return "fill"; }
            };
            _fill = null;
        }
        return fill;
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
                @Override public Object getBean() { return Connection.this; }
                @Override public String getName() { return "tooltipText"; }
            };
        }
        return tooltipText;
    }


    // ******************** Event Handling ************************************
    public void setOnConnectionEvent(final ConnectionEventListener LISTENER) { addConnectionEventListener(LISTENER); }
    public void addConnectionEventListener(final ConnectionEventListener LISTENER) { if (!listeners.contains(LISTENER)) { listeners.add(LISTENER); } }
    public void removeConnectionEventListener(final ConnectionEventListener LISTENER) { if (listeners.contains(LISTENER)) { listeners.remove(LISTENER); } }

    public void fireConnectionEvent(final ConnectionEvent EVENT) { listeners.forEach(listener -> listener.onConnectionEvent(EVENT)); }
}
