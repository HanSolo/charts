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

import eu.hansolo.fx.charts.Cluster;
import eu.hansolo.fx.charts.Symbol;
import eu.hansolo.fx.charts.event.EventType;
import eu.hansolo.fx.charts.event.ItemEvent;
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


public class PlotItem implements Item, Comparable<PlotItem> {
    private final ItemEvent               ITEM_EVENT = new ItemEvent(PlotItem.this, EventType.UPDATE);
    private       String                  _name;
    private       StringProperty          name;
    private       double                  _value;
    private       DoubleProperty          value;
    private       String                  _description;
    private       StringProperty          description;
    private       Color                   _fill;
    private       ObjectProperty<Color>   fill;
    private       Color                   _stroke;
    private       ObjectProperty<Color>   stroke;
    private       Color                   _connectionFill;
    private       ObjectProperty<Color>   connectionFill;
    private       Color                   _textColor;
    private       ObjectProperty<Color>   textColor;
    private       Font                    _font;
    private       ObjectProperty<Font>    font;
    private       Symbol                  _symbol;
    private       ObjectProperty<Symbol>  symbol;
    private       boolean                 _isEmpty;
    private       BooleanProperty         isEmpty;
    private       Map<PlotItem, Double>   outgoing;
    private       Map<PlotItem, Double>   incoming;
    private       List<ItemEventListener> listeners;
    private       int                     level;
    private       Cluster                 cluster;


    // ******************** Constructors **************************************
    public PlotItem() {
            this("", 0,"", Color.RED, -1, false);
    }
    public PlotItem(final boolean IS_EMPTY) {
        this("", 0,"", Color.RED, -1, IS_EMPTY);
    }
    public PlotItem(final String NAME, final double VALUE) {
        this(NAME, VALUE, "", Color.RED, -1, false);
    }
    public PlotItem(final String NAME, final double VALUE, final boolean IS_EMPTY) {
        this(NAME, VALUE, "", Color.RED, -1, IS_EMPTY);
    }
    public PlotItem(final String NAME, final Color COLOR) {
        this(NAME, 0, "", COLOR, -1, false);
    }
    public PlotItem(final String NAME, final Color COLOR, final boolean IS_EMPTY) {
        this(NAME, 0, "", COLOR, -1, IS_EMPTY);
    }
    public PlotItem(final String NAME, final Color COLOR, final int LEVEL) {
        this(NAME, 0, NAME, COLOR, LEVEL, false);
    }
    public PlotItem(final String NAME, final Color COLOR, final int LEVEL, final boolean IS_EMPTY) {
        this(NAME, 0, NAME, COLOR, LEVEL, IS_EMPTY);
    }
    public PlotItem(final String NAME, final double VALUE, final Color COLOR) {
        this(NAME, VALUE, "", COLOR, -1, false);
    }
    public PlotItem(final String NAME, final double VALUE, final Color COLOR, final boolean IS_EMPTY) {
        this(NAME, VALUE, "", COLOR, -1, IS_EMPTY);
    }
    public PlotItem(final String NAME, final double VALUE, final Color COLOR, final int LEVEL) {
        this(NAME, VALUE, "", COLOR, LEVEL, false);
    }
    public PlotItem(final String NAME, final double VALUE, final Color COLOR, final int LEVEL, final boolean IS_EMPTY) {
        this(NAME, VALUE, "", COLOR, LEVEL, IS_EMPTY);
    }
    public PlotItem(final String NAME, final double VALUE, final String DESCRIPTION, final Color FILL) {
        this(NAME, VALUE, DESCRIPTION, FILL, -1, false);
    }
    public PlotItem(final String NAME, final double VALUE, final String DESCRIPTION, final Color FILL, final boolean IS_EMPTY) {
        this(NAME, VALUE, DESCRIPTION, FILL, -1, IS_EMPTY);
    }
    public PlotItem(final String NAME, final double VALUE, final String DESCRIPTION, final Color FILL, final int LEVEL) {
        this(NAME, VALUE, DESCRIPTION, FILL, LEVEL, false);
    }
    public PlotItem(final String NAME, final double VALUE, final String DESCRIPTION, final Color FILL, final int LEVEL, final boolean IS_EMPTY) {
        _name           = NAME;
        _value          = VALUE;
        _description    = DESCRIPTION;
        _fill           = FILL;
        _stroke         = Color.TRANSPARENT;
        _connectionFill = Color.TRANSPARENT;
        _textColor      = Color.TRANSPARENT;
        _font           = Fonts.opensansRegular(10);
        _symbol         = Symbol.NONE;
        _isEmpty        = IS_EMPTY;
        level           = LEVEL;
        cluster         = null;
        outgoing        = new LinkedHashMap<>();
        incoming        = new LinkedHashMap<>();
        listeners       = new CopyOnWriteArrayList<>();
    }


    // ******************** Methods *******************************************
    public String getName() { return null == name ? _name : name.get(); }
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
                @Override public Object getBean() { return PlotItem.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    public double getValue() { return null == value ? _value : value.get(); }
    public void setValue(final double VALUE) {
        if (null == value) {
            _value = VALUE;
            fireItemEvent(ITEM_EVENT);
        } else {
            value.set(VALUE);
        }
    }
    public DoubleProperty valueProperty() {
        if (null == value) {
            value = new DoublePropertyBase(_value) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return PlotItem.this; }
                @Override public String getName() { return "value"; }
            };
        }
        return value;
    }

    public String getDescription() { return null == description ? _description : description.get(); }
    public void setDescription(final String DESCRIPTION) {
        if (null == description) {
            _description = DESCRIPTION;
            fireItemEvent(ITEM_EVENT);
        } else {
            description.set(DESCRIPTION);
        }
    }
    public StringProperty descriptionProperty() {
        if (null == description) {
            description = new StringPropertyBase(_description) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return PlotItem.this; }
                @Override public String getName() { return "description"; }
            };
            _description = null;
        }
        return description;
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
                @Override public Object getBean() { return PlotItem.this; }
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
                @Override public Object getBean() { return PlotItem.this; }
                @Override public String getName() { return "stroke"; }
            };
            _stroke = null;
        }
        return stroke;
    }

    public Color getConnectionFill() { return null == connectionFill ? _connectionFill : connectionFill.get(); }
    private void setConnectionFill(final Color FILL) {
        if (null == connectionFill) {
            _connectionFill = FILL;
            fireItemEvent(ITEM_EVENT);
        } else {
            connectionFill.set(FILL);
        }
    }
    public ReadOnlyObjectProperty<Color> connectionFillProperty() {
        if (null == connectionFill) {
            connectionFill = new ObjectPropertyBase<>(_connectionFill) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return PlotItem.this; }
                @Override public String getName() { return "connectionFill"; }
            };
            _connectionFill = null;
        }
        return stroke;
    }

    public Color getTextColor() { return null == textColor ? _textColor : textColor.get(); }
    public void setTextColor(final Color TEXT_COLOR) {
        if (null == textColor) {
            _textColor = TEXT_COLOR;
            fireItemEvent(ITEM_EVENT);
        } else {
            textColor.set(TEXT_COLOR);
        }
    }
    public ObjectProperty<Color> textColorProperty() {
        if (null == textColor) {
            textColor = new ObjectPropertyBase<>(_textColor) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return PlotItem.this; }
                @Override public String getName() { return "textColor"; }
            };
            _textColor = null;
        }
        return textColor;
    }

    public Font getFont() { return null == font ? _font : font.get(); }
    public void setFont(final Font FONT) {
        if (null == font) {
            _font = FONT;
            fireItemEvent(ITEM_EVENT);
        } else {
            font.set(FONT);
        }
    }
    public ObjectProperty<Font> fontProperty() {
        if (null == font) {
            font = new ObjectPropertyBase<>(_font) {
                @Override protected void invalidated() { fireItemEvent(ITEM_EVENT); }
                @Override public Object getBean() { return PlotItem.this; }
                @Override public String getName() { return "font"; }
            };
            _font = null;
        }
        return font;
    }

    @Override public Symbol getSymbol() { return null == symbol ? _symbol : symbol.get(); }
    @Override public void setSymbol(final Symbol SYMBOL) {
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
                @Override public Object getBean() {  return PlotItem.this;  }
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
                @Override public Object getBean() { return PlotItem.this; }
                @Override public String getName() { return "isEmpty"; }
            };
        }
        return isEmpty;
    }

    public double getSumOfIncoming() { return incoming.values().stream().mapToDouble(Double::doubleValue).sum(); }
    public double getSumOfOutgoing() { return outgoing.values().stream().mapToDouble(Double::doubleValue).sum(); }
    public double getMaxSum() { return Math.max(getSumOfIncoming(), getSumOfOutgoing()); }

    public Map<PlotItem, Double> getOutgoing() { return outgoing; }
    public void setOutgoing(final Map<PlotItem, Double> OUTGOING) {
        outgoing.forEach((item, value) -> item.removeFromIncoming(PlotItem.this));
        outgoing.clear();
        outgoing.putAll(OUTGOING);
        establishConnections();
        fireItemEvent(ITEM_EVENT);
    }
    public void addToOutgoing(final PlotItem ITEM, final double VALUE) {
        if (!outgoing.containsKey(ITEM)) {
            outgoing.put(ITEM, Helper.clamp(0, Double.MAX_VALUE, VALUE));
            establishConnections();
            fireItemEvent(ITEM_EVENT);
        }
    }
    public void removeFromOutgoing(final PlotItem ITEM) {
        if (outgoing.containsKey(ITEM)) {
            ITEM.removeFromIncoming(PlotItem.this);
            outgoing.remove(ITEM);
            fireItemEvent(ITEM_EVENT);
        }
    }
    public void clearOutgoing() {
        outgoing.forEach((item, value) -> item.removeFromIncoming(PlotItem.this));
        outgoing.clear();
        fireItemEvent(ITEM_EVENT);
    }
    public boolean hasOutgoing() { return outgoing.size() > 0; }

    public Map<PlotItem, Double> getIncoming() { return incoming; }
    protected void setIncoming(final Map<PlotItem, Double> INCOMING) {
        incoming.clear();
        incoming.putAll(INCOMING);
        fireItemEvent(ITEM_EVENT);
    }
    protected void addToIncoming(final PlotItem ITEM, final double VALUE) {
        if (!incoming.containsKey(ITEM)) {
            incoming.put(ITEM, Helper.clamp(0, Double.MAX_VALUE, VALUE));
            fireItemEvent(ITEM_EVENT);
        }
    }
    protected void removeFromIncoming(final PlotItem ITEM) {
        if (incoming.containsKey(ITEM)) {
            incoming.remove(ITEM);
            fireItemEvent(ITEM_EVENT);
        }
    }
    protected void clearIncoming() {
        incoming.clear();
        fireItemEvent(ITEM_EVENT);
    }
    public boolean hasIncoming() { return incoming.size() > 0 ; }

    public double getIncomingValueFrom(final PlotItem INCOMING_ITEM) {
        if (getIncoming().containsKey(INCOMING_ITEM)) {
            return getIncoming().get(INCOMING_ITEM);
        } else {
            return 0;
        }
    }

    public double getOutgoingValueTo(final PlotItem OUTGOING_ITEM) {
        if (getOutgoing().containsKey(OUTGOING_ITEM)) {
            return getOutgoing().get(OUTGOING_ITEM);
        } else {
            return 0;
        }
    }

    public boolean isRoot() { return hasOutgoing() && !hasIncoming(); }

    public boolean isLeaf() { return hasIncoming() && !hasOutgoing(); }

    public int getLevel() { return level; }
    public void setLevel(final int LEVEL) {
        if (LEVEL < 0) { throw new IllegalArgumentException("Level cannot be smaller than 0"); }
        level = LEVEL;
    }

    public Cluster getCluster() { return cluster; }
    public void setCluster(final Cluster CLUSTER) { cluster = CLUSTER; }

    public void sortOutgoingByGivenList(final List<PlotItem> LIST_WITH_SORTED_ITEMS) {
        List<PlotItem> outgoingKeys = new ArrayList(getOutgoing().keySet());

        sortAndReverse(outgoingKeys, LIST_WITH_SORTED_ITEMS);

        Map<PlotItem, Double> sortedOutgoingItems = new LinkedHashMap<>(outgoingKeys.size());
        for (PlotItem plotItem : outgoingKeys) { sortedOutgoingItems.put(plotItem, getOutgoing().get(plotItem)); }
        outgoing.clear();
        outgoing.putAll(sortedOutgoingItems);
    }
    public void sortIncomingByGivenList(final List<PlotItem> LIST_WITH_SORTED_ITEMS) {
        List<PlotItem> incomingKeys = new ArrayList(getIncoming().keySet());
        Collections.reverse(incomingKeys);

        sortAndReverse(incomingKeys, LIST_WITH_SORTED_ITEMS);

        Map<PlotItem, Double> sortedIncomingItems = new LinkedHashMap<>(incomingKeys.size());
        for (PlotItem plotItem : incomingKeys) { sortedIncomingItems.put(plotItem, getIncoming().get(plotItem)); }
        incoming.clear();
        incoming.putAll(sortedIncomingItems);
    }

    private void sortAndReverse(final List<PlotItem> LIST_TO_SORT, final List<PlotItem> SORTED_LIST) {
        Collections.sort(LIST_TO_SORT, Comparator.comparing(item -> SORTED_LIST.indexOf(item)));
        Collections.reverse(LIST_TO_SORT);
    }

    private void establishConnections() {
        outgoing.forEach((item, value) -> item.addToIncoming(PlotItem.this, value));
    }

    @Override public int compareTo(final PlotItem ITEM) { return Double.compare(getValue(), ITEM.getValue()); }

    @Override public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlotItem item = (PlotItem) o;

        if (Double.compare(item.getValue(), getValue()) != 0) return false;
        if (getName() != null ? !getName().equals(item.getName()) : item.getName() != null) return false;
        return (getName() != null ? !getName().equals(item.getName()) : item.getName() != null);
//        if (getFill() != null ? !getFill().equals(item.getFill()) : item.getFill() != null) return false;
//        if (getStroke() != null ? !getStroke().equals(item.getStroke()) : item.getStroke() != null) return false;
//        return getConnectionFill() != null ? getConnectionFill().equals(item.getConnectionFill()) : item.getConnectionFill() == null;
    }

    @Override public int hashCode() {
        int  result;
        long temp;
        result = getName() != null ? getName().hashCode() : 0;
        temp = Double.doubleToLongBits(getValue());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
//        result = 31 * result + (getFill() != null ? getFill().hashCode() : 0);
//        result = 31 * result + (getStroke() != null ? getStroke().hashCode() : 0);
//        result = 31 * result + (getConnectionFill() != null ? getConnectionFill().hashCode() : 0);
        return result;
    }


    // ******************** Event Handling ************************************
    public void setOnItemEvent(final ItemEventListener LISTENER) { addItemEventListener(LISTENER); }
    public void addItemEventListener(final ItemEventListener LISTENER) { if (!listeners.contains(LISTENER)) { listeners.add(LISTENER); } }
    public void removeItemEventListener(final ItemEventListener LISTENER) { if (listeners.contains(LISTENER)) { listeners.remove(LISTENER); } }

    public void fireItemEvent(final ItemEvent EVENT) { listeners.forEach(listener -> listener.onItemEvent(EVENT)); }
}
