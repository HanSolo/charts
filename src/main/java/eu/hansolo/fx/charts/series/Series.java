package eu.hansolo.fx.charts.series;

import eu.hansolo.fx.charts.ChartType;
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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by hansolo on 16.07.17.
 */
public abstract class Series<T> {
    public    final SeriesEvent REFRESH = new SeriesEvent(Series.this, SeriesEventType.REDRAW);
    protected String                                    _title;
    protected StringProperty                            title;
    protected String                                    _subTitle;
    protected StringProperty                            subTitle;
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
        this(null, ChartType.SCATTER, "", "", Color.BLACK, Color.TRANSPARENT);
    }
    public Series(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "", "", Color.BLACK, Color.TRANSPARENT);
    }
    public Series(final List<T> ITEMS, final ChartType TYPE, final String TITLE, final String SUB_TITLE) {
        this(ITEMS, TYPE, TITLE, SUB_TITLE, Color.BLACK, Color.TRANSPARENT);
    }
    public Series(final List<T> ITEMS, final ChartType TYPE, final String TITLE, final String SUB_TITLE, final Paint STROKE, final Paint FILL) {
        _title    = TITLE;
        _subTitle = SUB_TITLE;
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

    public String getTitle() { return null == title ? _title : title.get(); }
    public void setTitle(final String TITLE) {
        if (null == title) {
            _title = TITLE;
            fireSeriesEvent(REFRESH);
        } else {
            title.set(TITLE);
        }
    }
    public StringProperty titleProperty() {
        if (null == title) {
            title = new StringPropertyBase(_title) {
                @Override protected void invalidated() { fireSeriesEvent(REFRESH); }
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "title"; }
            };
            _title = null;
        }
        return title;
    }

    public String getSubTitle() { return null == subTitle ? _subTitle : subTitle.get(); }
    public void setSubTitle(final String SUB_TITLE) {
        if (null == subTitle) {
            _subTitle = SUB_TITLE;
            fireSeriesEvent(REFRESH);
        } else {
            subTitle.set(SUB_TITLE);
        }
    }
    public StringProperty subTitleProperty() {
        if (null == subTitle) {
            subTitle = new StringPropertyBase(_subTitle) {
                @Override protected void invalidated() { fireSeriesEvent(REFRESH); }
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "subTitle"; }
            };
            _subTitle = null;
        }
        return subTitle;
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

    public ChartType getChartType() { return chartType; }
    public void setChartType(final ChartType TYPE) {
        chartType = TYPE;
        refresh();
    }

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
