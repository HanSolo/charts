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

package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.event.PlotItemEvent;
import eu.hansolo.fx.charts.event.PlotItemEventListener;
import eu.hansolo.fx.charts.event.PlotItemEventType;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


public class PlotItem {
    private final PlotItemEvent UPDATED_EVENT = new PlotItemEvent(PlotItem.this, PlotItemEventType.UPDATED);
    private       String                      _name;
    private       StringProperty              name;
    private       double                      _value;
    private       DoubleProperty              value;
    private       String                      _description;
    private       StringProperty              description;
    private       Color                       _color;
    private       ObjectProperty<Color>       color;
    private       Map<PlotItem, Double>       outgoing;
    private       Map<PlotItem, Double>       incoming;
    private       List<PlotItemEventListener> listeners;
    private       int                         level;


    // ******************** Constructors **************************************
    public PlotItem() {
            this("", 0,"", Color.RED);
    }
    public PlotItem(final String NAME, final double VALUE) {
        this(NAME, VALUE, "", Color.RED);
    }
    public PlotItem(final String NAME, final Color COLOR) {
        this(NAME, 0, COLOR);
    }
    public PlotItem(final String NAME, final double VALUE, final Color COLOR) {
        this(NAME, VALUE, "", COLOR);
    }
    public PlotItem(final String NAME, final double VALUE, final String DESCRIPTION, final Color COLOR) {
        _name        = NAME;
        _value       = VALUE;
        _description = DESCRIPTION;
        _color       = COLOR;
        level        = -1;
        outgoing     = new LinkedHashMap<>();
        incoming     = new LinkedHashMap<>();
        listeners    = new CopyOnWriteArrayList<>();
    }


    // ******************** Methods *******************************************
    public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) {
        if (null == name) {
            _name = NAME;
            fireChartItemEvent(UPDATED_EVENT);
        } else {
            name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override protected void invalidated() { fireChartItemEvent(UPDATED_EVENT); }
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
            fireChartItemEvent(UPDATED_EVENT);
        } else {
            value.set(VALUE);
        }
    }
    public DoubleProperty valueProperty() {
        if (null == value) {
            value = new DoublePropertyBase(_value) {
                @Override protected void invalidated() { fireChartItemEvent(UPDATED_EVENT); }
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
            fireChartItemEvent(UPDATED_EVENT);
        } else {
            description.set(DESCRIPTION);
        }
    }
    public StringProperty descriptionProperty() {
        if (null == description) {
            description = new StringPropertyBase(_description) {
                @Override protected void invalidated() { fireChartItemEvent(UPDATED_EVENT); }
                @Override public Object getBean() { return PlotItem.this; }
                @Override public String getName() { return "description"; }
            };
            _description = null;
        }
        return description;
    }

    public Color getColor() { return null == color ? _color : color.get(); }
    public void setColor(final Color COLOR) {
        if (null == color) {
            _color = COLOR;
            fireChartItemEvent(UPDATED_EVENT);
        } else {
            color.set(COLOR);
        }
    }
    public ObjectProperty<Color> colorProperty() {
        if (null == color) {
            color = new ObjectPropertyBase<Color>(_color) {
                @Override protected void invalidated() { fireChartItemEvent(UPDATED_EVENT); }
                @Override public Object getBean() { return PlotItem.this; }
                @Override public String getName() { return "color"; }
            };
            _color = null;
        }
        return color;
    }

    public double getSumOfIncoming() { return getIncoming().values().stream().mapToDouble(Double::doubleValue).sum(); }
    public double getSumOfOutgoing() { return getOutgoing().values().stream().mapToDouble(Double::doubleValue).sum(); }
    public double getMaxSum() { return Math.max(getSumOfIncoming(), getSumOfOutgoing()); }

    public Map<PlotItem, Double> getOutgoing() { return outgoing; }
    public void setOutgoing(final Map<PlotItem, Double> OUTGOING) {
        outgoing.forEach((item, value) -> item.removeFromIncoming(PlotItem.this));
        outgoing.clear();
        outgoing.putAll(OUTGOING);
        establishConnections();
        fireChartItemEvent(UPDATED_EVENT);
    }
    public void addToOutgoing(final PlotItem ITEM, final double VALUE) {
        if (!outgoing.containsKey(ITEM)) {
            outgoing.put(ITEM, Helper.clamp(0, Double.MAX_VALUE, VALUE));
            establishConnections();
            fireChartItemEvent(UPDATED_EVENT);
        }
    }
    public void removeFromOutgoing(final PlotItem ITEM) {
        if (outgoing.containsKey(ITEM)) {
            ITEM.removeFromIncoming(PlotItem.this);
            outgoing.remove(ITEM);
            fireChartItemEvent(UPDATED_EVENT);
        }
    }
    public void clearOutgoing() {
        outgoing.forEach((item, value) -> item.removeFromIncoming(PlotItem.this));
        outgoing.clear();
        fireChartItemEvent(UPDATED_EVENT);
    }
    public boolean hasOutgoing() { return outgoing.size() > 0; }

    public Map<PlotItem, Double> getIncoming() { return incoming; }
    protected void setIncoming(final Map<PlotItem, Double> INCOMING) {
        incoming.clear();
        incoming.putAll(INCOMING);
        fireChartItemEvent(UPDATED_EVENT);
    }
    protected void addToIncoming(final PlotItem ITEM, final double VALUE) {
        if (!incoming.containsKey(ITEM)) {
            incoming.put(ITEM, Helper.clamp(0, Double.MAX_VALUE, VALUE));
            fireChartItemEvent(UPDATED_EVENT);
        }
    }
    protected void removeFromIncoming(final PlotItem ITEM) {
        if (incoming.containsKey(ITEM)) {
            incoming.remove(ITEM);
            fireChartItemEvent(UPDATED_EVENT);
        }
    }
    protected void clearIncoming() {
        incoming.clear();
        fireChartItemEvent(UPDATED_EVENT);
    }
    public boolean hasIncoming() { return incoming.size() > 0 ; }

    public boolean isRoot() { return hasOutgoing() && !hasIncoming(); }

    public int getLevel() {
        if (level == -1) {
            if (isRoot()) {
                level = 0;
            } else {
                for (PlotItem item : getIncoming().keySet()) {
                    level = getLevel(item, 0);
                }
            }
        }
        return level;
    }
    private int getLevel(final PlotItem ITEM, int level) {
        level++;
        if (ITEM.isRoot()) { return level; }
        level = getLevel(ITEM.getIncoming().keySet().iterator().next(), level);
        return level;
    }

    protected void sortOutgoingByGivenList(final List<PlotItem> LIST_WITH_SORTED_ITEMS) {
        List<PlotItem> outgoingKeys = new ArrayList(getOutgoing().keySet());

        sortAndReverse(outgoingKeys, LIST_WITH_SORTED_ITEMS);

        Map<PlotItem, Double> sortedOutgoingItems = new LinkedHashMap<>(outgoingKeys.size());
        for (PlotItem plotItem : outgoingKeys) { sortedOutgoingItems.put(plotItem, getOutgoing().get(plotItem)); }
        outgoing.clear();
        outgoing.putAll(sortedOutgoingItems);
    }
    protected void sortIncomingByGivenList(final List<PlotItem> LIST_WITH_SORTED_ITEMS) {
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


    // ******************** Event Handling ************************************
    public void setOnChartItemEvent(final PlotItemEventListener LISTENER) { addChartItemEventListener(LISTENER); }
    public void addChartItemEventListener(final PlotItemEventListener LISTENER) { if (!listeners.contains(LISTENER)) { listeners.add(LISTENER); } }
    public void removeChartItemEventListener(final PlotItemEventListener LISTENER) { if (listeners.contains(LISTENER)) { listeners.remove(LISTENER); } }

    public void fireChartItemEvent(final PlotItemEvent EVENT) { listeners.forEach(listener -> listener.onChartItemEvent(EVENT)); }
}
