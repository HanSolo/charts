package eu.hansolo.fx.charts.model;

import eu.hansolo.fx.charts.event.ChartModelEvent;
import eu.hansolo.fx.charts.event.ChartModelEventListener;
import eu.hansolo.fx.charts.event.ChartModelEventType;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by hansolo on 16.07.17.
 */
public abstract class ChartModel<T> {
    public    final ChartModelEvent REFRESH = new ChartModelEvent(ChartModel.this, ChartModelEventType.REDRAW);
    protected       String                                        _title;
    protected       StringProperty                                title;
    protected       String                                        _subTitle;
    protected       StringProperty                                subTitle;
    protected       ObservableList<T>                             items;
    private         CopyOnWriteArrayList<ChartModelEventListener> listeners;
    private         ListChangeListener<T>                         itemListener;



    // ******************** Constructors **************************************
    public ChartModel() {
        this(null, "", "");
    }
    public ChartModel(final List<T> ITEMS) {
        this(ITEMS, "", "");
    }
    public ChartModel(final List<T> ITEMS, final String TITLE, final String SUB_TITLE) {
        _title    = TITLE;
        _subTitle = SUB_TITLE;
        items     = FXCollections.observableArrayList();
        listeners = new CopyOnWriteArrayList<>();

        if (null != ITEMS) { items.setAll(ITEMS); }
    }


    // ******************** Initialization ************************************
    private void init() {
        itemListener = change -> fireModelEvent(REFRESH);
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
            fireModelEvent(REFRESH);
        } else {
            title.set(TITLE);
        }
    }
    public StringProperty titleProperty() {
        if (null == title) {
            title = new StringPropertyBase(_title) {
                @Override protected void invalidated() { fireModelEvent(REFRESH); }
                @Override public Object getBean() { return ChartModel.this; }
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
            fireModelEvent(REFRESH);
        } else {
            subTitle.set(SUB_TITLE);
        }
    }
    public StringProperty subTitleProperty() {
        if (null == subTitle) {
            subTitle = new StringPropertyBase(_subTitle) {
                @Override protected void invalidated() { fireModelEvent(REFRESH); }
                @Override public Object getBean() { return ChartModel.this; }
                @Override public String getName() { return "subTitle"; }
            };
            _subTitle = null;
        }
        return subTitle;
    }

    public void dispose() { items.remove(itemListener); }

    public void refresh() { fireModelEvent(REFRESH); }


    // ******************** Event handling ************************************
    public void setOnModelEvent(final ChartModelEventListener LISTENER) { addModelEventListener(LISTENER); }
    public void addModelEventListener(final ChartModelEventListener LISTENER) { if (!listeners.contains(LISTENER)) listeners.add(LISTENER); }
    public void removeModelEventListener(final ChartModelEventListener LISTENER) { if (listeners.contains(LISTENER)) listeners.remove(LISTENER); }

    public void fireModelEvent(final ChartModelEvent EVENT) {
        for (ChartModelEventListener listener : listeners) { listener.onModelEvent(EVENT); }
    }
}
