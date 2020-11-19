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

package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.PlotItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class Cluster implements Comparable<Cluster> {
    private Set<PlotItem>         items = new CopyOnWriteArraySet<>();
    private String                _name;
    private StringProperty        name;
    private Color                 _fill;
    private ObjectProperty<Color> fill;
    private Color                 _stroke;
    private ObjectProperty<Color> stroke;

    
    public Cluster() {
        this("", Color.TRANSPARENT, Color.TRANSPARENT, new PlotItem[]{});
    }
    public Cluster(final String NAME) {
        this(NAME, Color.TRANSPARENT, Color.TRANSPARENT, new PlotItem[]{});
    }
    public Cluster(final String NAME, final List<PlotItem> ITEMS) {
        this(NAME, Color.TRANSPARENT, Color.TRANSPARENT, ITEMS.toArray(new PlotItem[0]));
    }
    public Cluster(final List<PlotItem> ITEMS) {
        this("", Color.TRANSPARENT, Color.TRANSPARENT, ITEMS.toArray(new PlotItem[0]));
    }
    public Cluster(final PlotItem... ITEMS) {
        this("", Color.TRANSPARENT, Color.TRANSPARENT, ITEMS);
    }
    public Cluster(final String NAME, final PlotItem... ITEMS) {
        this(NAME, Color.TRANSPARENT, Color.TRANSPARENT, ITEMS);
    }
    public Cluster(final String NAME, final Color FILL, final PlotItem... ITEMS) {
        this(NAME, FILL, Color.TRANSPARENT, ITEMS);
    }
    public Cluster(final String NAME, final Color FILL, final Color STROKE, final PlotItem... ITEMS) {
        _name   = NAME;
        _fill   = FILL;
        _stroke = STROKE;
        items.addAll(Arrays.asList(ITEMS.clone()));
        Arrays.stream(ITEMS).forEach(item -> item.setCluster(Cluster.this));
    }


    public Collection<PlotItem> getItems() { return items; }

    public Collection<PlotItem> getSortedItems() {
        List<PlotItem> sortedItems = new LinkedList<>(items);
        Collections.sort(sortedItems, Comparator.comparingDouble(PlotItem::getSumOfOutgoing).reversed());
        return sortedItems;
    }

    public void addItem(final PlotItem ITEM) {
        items.add(ITEM);
        ITEM.setCluster(Cluster.this);
    }
    public void removeItem(final PlotItem ITEM) {
        if (items.contains(ITEM)) {
            ITEM.setCluster(null);
            items.remove(ITEM);
        }
    }

    public void addItems(final List<PlotItem> ITEMS) {
        items.addAll(ITEMS);
        ITEMS.forEach(item -> item.setCluster(Cluster.this));
    }
    public void addItems(final PlotItem... ITEMS) {
        items.addAll(Arrays.asList(ITEMS.clone()));
        Arrays.stream(ITEMS).forEach(item -> item.setCluster(Cluster.this));
    }

    public PlotItem getItemWithMaxValue() {
        return items.stream().max(Comparator.comparingDouble(PlotItem::getSumOfOutgoing)).get();
    }

    public double getMaxValue() {
        return items.stream().max(Comparator.comparingDouble(PlotItem::getSumOfOutgoing)).get().getValue();
    }

    public void clear() {
        items.forEach(item -> item.setCluster(null));
        items.clear();
    }
    
    public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) { 
        if (null == name) {
            _name = NAME;
        } else {
            name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override public Object getBean() { return Cluster.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
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
            fill = new ObjectPropertyBase<>(_fill) {
                @Override public Object getBean() { return Cluster.this; }
                @Override public String getName() { return "fill"; }
            };
            _fill = null;
        }
        return fill;
    }

    public Color getStroke() { return null == stroke ? _stroke : stroke.get(); }
    public void setStroke(final Color STROKE) {
        if (null == stroke) {
            _stroke = STROKE;
        } else {
            stroke.set(STROKE);
        }
    }
    public ObjectProperty<Color> strokeProperty() {
        if (null == stroke) {
            stroke = new ObjectPropertyBase<>(_stroke) {
                @Override public Object getBean() { return Cluster.this; }
                @Override public String getName() { return "stroke"; }
            };
            _stroke = null;
        }
        return stroke;
    }

    @Override public int compareTo(final Cluster other) {
        if (getMaxValue() < other.getMaxValue()) {
            return 1;
        } else if (getMaxValue() > other.getMaxValue()) {
            return -1;
        } else {
            return 0;
        }
    }
}
