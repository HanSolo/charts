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

package eu.hansolo.fx.charts.series;

import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.data.ChartData;
import eu.hansolo.fx.charts.event.SeriesEvent;
import eu.hansolo.fx.charts.event.SeriesEventListener;
import eu.hansolo.fx.charts.event.SeriesEventType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by hansolo on 16.07.17.
 */
public abstract class Series<T extends ChartData> {
    public    final SeriesEvent REFRESH = new SeriesEvent(Series.this, SeriesEventType.REDRAW);
    protected String                                    _name;
    protected StringProperty                            name;
    protected Paint                                     _stroke;
    protected ObjectProperty<Paint>                     stroke;
    protected Paint                                     _fill;
    protected ObjectProperty<Paint>                     fill;
    protected ChartType                                 chartType;
    protected ObservableList<T>                         items;
    private   CopyOnWriteArrayList<SeriesEventListener> listeners;
    private   ListChangeListener<T>                     itemListener;



    // ******************** Constructors **************************************
    public Series() {
        this(null, ChartType.SCATTER, "", Color.BLACK, Color.TRANSPARENT);
    }
    public Series(final T... ITEMS) {
        this(Arrays.asList(ITEMS), ChartType.SCATTER, "", Color.BLACK, Color.TRANSPARENT);
    }
    public Series(final ChartType TYPE, final T... ITEMS) {
        this(Arrays.asList(ITEMS), TYPE, "", Color.BLACK, Color.TRANSPARENT);
    }
    public Series(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "", Color.BLACK, Color.TRANSPARENT);
    }
    public Series(final ChartType TYPE, final String NAME, final T... ITEMS) {
        this(Arrays.asList(ITEMS), TYPE, NAME, Color.BLACK, Color.TRANSPARENT);
    }
    public Series(final List<T> ITEMS, final ChartType TYPE, final String NAME) {
        this(ITEMS, TYPE, NAME, Color.BLACK, Color.TRANSPARENT);
    }
    public Series(final ChartType TYPE, final String NAME, final Paint STROKE, final Paint FILL, final T... ITEMS) {
        this(Arrays.asList(ITEMS), TYPE, NAME, STROKE, FILL);
    }
    public Series(final List<T> ITEMS, final ChartType TYPE, final String NAME, final Paint STROKE, final Paint FILL) {
        _name     = NAME;
        _stroke   = STROKE;
        _fill     = FILL;
        chartType = TYPE;
        items     = FXCollections.observableArrayList();
        listeners = new CopyOnWriteArrayList<>();

        if (null != ITEMS) { items.setAll(ITEMS); }
    }


    // ******************** Initialization ************************************
    private void init() {
        itemListener = change -> fireSeriesEvent(REFRESH);
    }

    private void registerListeners() {
        items.addListener(itemListener);
    }


    // ******************** Methods *******************************************
    public ObservableList<T> getItems() { return items; }
    public void setItems(final List<T> ITEMS) { items.setAll(ITEMS); }

    public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) {
        if (null == name) {
            _name = NAME;
            fireSeriesEvent(REFRESH);
        } else {
            name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override protected void invalidated() { fireSeriesEvent(REFRESH); }
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    public Paint getStroke() { return null == stroke ? _stroke : stroke.get(); }
    public void setStroke(final Paint PAINT) {
        if (null == stroke) {
            _stroke = PAINT;
            refresh();
        } else {
            stroke.set(PAINT);
        }
    }
    public ObjectProperty<Paint> strokeProperty() {
        if (null == stroke) {
            stroke = new ObjectPropertyBase<Paint>(_stroke) {
                @Override protected void invalidated() { refresh(); }
                @Override public Object getBean() {  return Series.this; }
                @Override public String getName() { return "stroke"; }
            };
            _stroke = null;
        }
        return stroke;
    }

    public Paint getFill() { return null == fill ? _fill : fill.get(); }
    public void setFill(final Paint PAINT) {
        if (null == fill) {
            _fill = PAINT;
            refresh();
        } else {
            fill.set(PAINT);
        }
    }
    public ObjectProperty<Paint> fillProperty() {
        if (null == fill) {
            fill = new ObjectPropertyBase<Paint>(_fill) {
                @Override protected void invalidated() { refresh(); }
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "fill"; }
            };
            _fill = null;
        }
        return fill;
    }

    public void setPointColor(final Color COLOR) {}

    public ChartType getChartType() { return chartType; }
    public void setChartType(final ChartType TYPE) {
        chartType = TYPE;
        refresh();
    }

    public int getNoOfItems() { return items.size(); }

    public double getSumOfAllItems() { return items.stream().mapToDouble(T::getValue).sum(); }

    public void dispose() { items.remove(itemListener); }

    public void refresh() { fireSeriesEvent(REFRESH); }


    // ******************** Event handling ************************************
    public void setOnSeriesEvent(final SeriesEventListener LISTENER) { addSeriesEventListener(LISTENER); }
    public void addSeriesEventListener(final SeriesEventListener LISTENER) { if (!listeners.contains(LISTENER)) listeners.add(LISTENER); }
    public void removeSeriesEventListener(final SeriesEventListener LISTENER) { if (listeners.contains(LISTENER)) listeners.remove(LISTENER); }

    public void fireSeriesEvent(final SeriesEvent EVENT) {
        for (SeriesEventListener listener : listeners) { listener.onModelEvent(EVENT); }
    }
}
