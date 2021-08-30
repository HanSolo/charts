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
import eu.hansolo.fx.charts.Symbol;
import eu.hansolo.fx.charts.data.Item;
import eu.hansolo.fx.charts.data.XYChartItem;
import eu.hansolo.fx.charts.data.XYItem;
import eu.hansolo.fx.charts.event.EventType;
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.event.SeriesEvent;
import eu.hansolo.fx.charts.event.SeriesEventListener;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.LongProperty;
import javafx.beans.property.LongPropertyBase;
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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by hansolo on 16.07.17.
 */
public abstract class Series<T extends Item> {
    public    final SeriesEvent                               UPDATE_EVENT = new SeriesEvent(Series.this, EventType.UPDATE);
    protected       String                                    _name;
    protected       StringProperty                            name;
    protected       Paint                                     _fill;
    protected       ObjectProperty<Paint>                     fill;
    protected       Paint                                     _stroke;
    protected       ObjectProperty<Paint>                     stroke;
    protected       Color                                     _textFill;
    protected       ObjectProperty<Color>                     textFill;
    protected       Color                                     _symbolFill;
    protected       ObjectProperty<Color>                     symbolFill;
    protected       Color                                     _symbolStroke;
    protected       ObjectProperty<Color>                     symbolStroke;
    protected       Symbol                                    _symbol;
    protected       ObjectProperty<Symbol>                    symbol;
    protected       boolean                                   _symbolsVisible;
    protected       BooleanProperty                           symbolsVisible;
    protected       double                                    _symbolSize;
    protected       DoubleProperty                            symbolSize;
    protected       double                                    _strokeWidth;
    protected       DoubleProperty                            strokeWidth;
    protected       boolean                                   _animated;
    protected       BooleanProperty                           animated;
    protected       long                                      _animationDuration;
    protected       LongProperty                              animationDuration;
    //ADDED property to see if wrapping should be used or not by default false. Keeps the previous functionality the same.
    protected       boolean                                   _withWrapping;
    protected       BooleanProperty                           withWrapping;
    protected       ChartType                                 chartType;
    protected       ObservableList<T>                         items;
    private         CopyOnWriteArrayList<SeriesEventListener> listeners;
    private         ListChangeListener<T>                     itemListener;
    private         ItemEventListener                         itemEventListener;


    // ******************** Constructors **************************************
    public Series() {
        this(null, ChartType.SCATTER, "", Color.TRANSPARENT, Color.BLACK, Color.BLACK, Color.BLACK, Symbol.CIRCLE);
    }
    public Series(final T... ITEMS) {
        this(Arrays.asList(ITEMS), ChartType.SCATTER, "", Color.TRANSPARENT, Color.BLACK, Color.BLACK, Color.BLACK, Symbol.CIRCLE);
    }
    public Series(final ChartType TYPE, final T... ITEMS) {
        this(Arrays.asList(ITEMS), TYPE, "", Color.TRANSPARENT, Color.BLACK, Color.BLACK, Color.BLACK, Symbol.CIRCLE);
    }
    public Series(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "", Color.TRANSPARENT, Color.BLACK, Color.BLACK, Color.BLACK, Symbol.CIRCLE);
    }
    public Series(final ChartType TYPE, final String NAME, final T... ITEMS) {
        this(Arrays.asList(ITEMS), TYPE, NAME, Color.TRANSPARENT, Color.BLACK, Color.BLACK, Color.BLACK, Symbol.CIRCLE);
    }
    public Series(final List<T> ITEMS, final ChartType TYPE, final String NAME) {
        this(ITEMS, TYPE, NAME, Color.TRANSPARENT, Color.BLACK, Color.BLACK, Color.BLACK, Symbol.CIRCLE);
    }
    public Series(final List<T> ITEMS, final ChartType TYPE, final String NAME, final Symbol SYMBOL) {
        this(ITEMS, TYPE, NAME, Color.TRANSPARENT, Color.BLACK, Color.BLACK, Color.BLACK, SYMBOL);
    }
    public Series(final ChartType TYPE, final String NAME, final Paint FILL, final Paint STROKE, final Symbol SYMBOL, final T... ITEMS) {
        this(Arrays.asList(ITEMS), TYPE, NAME, FILL, STROKE, Color.BLACK, Color.BLACK, SYMBOL);
    }
    public Series(final List<T> ITEMS, final ChartType TYPE, final String NAME, final Paint FILL, final Paint STROKE, final Symbol SYMBOL) {
        this(ITEMS, TYPE, NAME, FILL, STROKE, Color.BLACK, Color.BLACK, SYMBOL);
    }
    public Series(final List<T> ITEMS, final ChartType TYPE, final String NAME, final Paint FILL, final Paint STROKE, final Color SYMBOL_FILL, final Color SYMBOL_STROKE, final Symbol SYMBOL) {
        _name              = NAME;
        _fill              = FILL;
        _stroke            = STROKE;
        _textFill          = Color.BLACK;
        _symbolFill        = SYMBOL_FILL;
        _symbolStroke      = SYMBOL_STROKE;
        _symbol            = SYMBOL;
        _symbolsVisible    = true;
        _symbolSize        = -1;
        _strokeWidth       = -1;
        _animated          = false;
        _animationDuration = 800;
        _withWrapping      = false;
        chartType          = TYPE;
        items              = FXCollections.observableArrayList();
        itemListener       = change -> fireSeriesEvent(UPDATE_EVENT);
        itemEventListener  = e -> fireSeriesEvent(UPDATE_EVENT);
        listeners          = new CopyOnWriteArrayList<>();

        if (null != ITEMS) { items.setAll(ITEMS); }

        registerListeners();
    }


    // ******************** Initialization ************************************
    private void registerListeners() {
        items.addListener(new ListChangeListener<T>() {
            @Override public void onChanged(final Change<? extends T> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        c.getAddedSubList().forEach(item -> {
                            if (item instanceof XYChartItem) {
                                XYChartItem xyChartItem = (XYChartItem) item;
                                xyChartItem.addItemEventListener(itemEventListener);
                            }
                        });
                    } else if (c.wasRemoved()) {
                        c.getRemoved().forEach(item -> {
                            if (item instanceof XYChartItem) {
                                XYChartItem xyChartItem = (XYChartItem) item;
                                xyChartItem.removeItemEventListener(itemEventListener);
                            }
                        });
                    }
                }
            }
        });
        items.addListener(itemListener);
    }


    // ******************** Methods *******************************************
    public ObservableList<T> getItems() { return items; }
    public void setItems(final Collection<T> ITEMS) { items.setAll(ITEMS); }
    public void setItems(final T... ITEMS) { setItems(Arrays.asList(ITEMS)); }
    public void setItems(final List<T> ITEMS) { items.setAll(ITEMS); }

    public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) {
        if (null == name) {
            _name = NAME;
            fireSeriesEvent(UPDATE_EVENT);
        } else {
            name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override protected void invalidated() { fireSeriesEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
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

    public Color getTextFill() { return null == textFill ? _textFill : textFill.get(); }
    public void setTextFill(final Color COLOR) {
        if (null == textFill) {
            _textFill = COLOR;
            refresh();
        } else {
            textFill.set(COLOR);
        }
    }
    public ObjectProperty<Color> textFillProperty() {
        if (null == textFill) {
            textFill = new ObjectPropertyBase<Color>(_textFill) {
                @Override protected void invalidated() { refresh(); }
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "textFill"; }
            };
            _textFill = null;
        }
        return textFill;
    }
    
    public Color getSymbolFill() { return null == symbolFill ? _symbolFill : symbolFill.get(); }
    public void setSymbolFill(final Color COLOR) {
        if (null == symbolFill) {
            _symbolFill = COLOR;
            refresh();
        } else {
            symbolFill.set(COLOR);
        }
    }
    public ObjectProperty<Color> symbolFillProperty() {
        if (null == symbolFill) {
            symbolFill = new ObjectPropertyBase<Color>(_symbolFill) {
                @Override protected void invalidated() { refresh(); }
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "symbolFill"; }
            };
            _symbolFill = null;
        }
        return symbolFill;
    }

    public Color getSymbolStroke() { return null == symbolStroke ? _symbolStroke : symbolStroke.get(); }
    public void setSymbolStroke(final Color COLOR) {
        if (null == symbolStroke) {
            _symbolStroke = COLOR;
            refresh();
        } else {
            symbolStroke.set(COLOR);
        }
    }
    public ObjectProperty<Color> symbolStrokeProperty() {
        if (null == symbolStroke) {
            symbolStroke = new ObjectPropertyBase<Color>(_symbolStroke) {
                @Override protected void invalidated() { refresh(); }
                @Override public Object getBean() {  return Series.this; }
                @Override public String getName() { return "symbolStroke"; }
            };
            _symbolStroke = null;
        }
        return symbolStroke;
    }
    
    public Symbol getSymbol() { return null == symbol ? _symbol : symbol.get(); }
    public void setSymbol(final Symbol SYMBOL) {
        if (null == symbol) {
            _symbol = SYMBOL;
            fireSeriesEvent(UPDATE_EVENT);
        } else {
            symbol.set(SYMBOL);
        }
    }
    public ObjectProperty<Symbol> symbolProperty() {
        if (null == symbol) {
            symbol = new ObjectPropertyBase<Symbol>(_symbol) {
                @Override protected void invalidated() { fireSeriesEvent(UPDATE_EVENT); }
                @Override public Object getBean() {  return Series.this;  }
                @Override public String getName() {  return "symbol";  }
            };
            _symbol = null;
        }
        return symbol;
    }

    public boolean getSymbolsVisible() { return null == symbolsVisible ? _symbolsVisible : symbolsVisible.get(); }
    public void setSymbolsVisible(final boolean VISIBLE) {
        if (null == symbolsVisible) {
            _symbolsVisible = VISIBLE;
            fireSeriesEvent(UPDATE_EVENT);
        } else {
            symbolsVisible.set(VISIBLE);
        }
    }
    public BooleanProperty symbolsVisibleProperty() {
        if (null == symbolsVisible) {
            symbolsVisible = new BooleanPropertyBase(_symbolsVisible) {
                @Override protected void invalidated() { fireSeriesEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "symbolsVisible"; }
            };
        }
        return symbolsVisible;
    }

    public ChartType getChartType() { return chartType; }
    public void setChartType(final ChartType TYPE) {
        chartType = TYPE;
        refresh();
    }

    public double getSymbolSize() { return null == symbolSize ? _symbolSize : symbolSize.get(); }
    public void setSymbolSize(final double SIZE) {
        if (null == symbolSize) {
            _symbolSize = Helper.clamp(1, 24, SIZE);
            fireSeriesEvent(UPDATE_EVENT);
        } else {
            symbolSize.set(SIZE);
        }
    }
    public DoubleProperty symbolSizeProperty() {
        if (null == symbolSize) {
            symbolSize = new DoublePropertyBase(_symbolSize) {
                @Override protected void invalidated() {
                    set(Helper.clamp(1, 24, get()));
                    fireSeriesEvent(UPDATE_EVENT);
                }
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "symbolSize"; }
            };
        }
        return symbolSize;
    }

    public double getStrokeWidth() { return null == strokeWidth ? _strokeWidth : strokeWidth.get(); }
    public void setStrokeWidth(final double WIDTH) {
        if (null == strokeWidth) {
            _strokeWidth = Helper.clamp(1, 24, WIDTH);
            fireSeriesEvent(UPDATE_EVENT);
        } else {
            strokeWidth.set(WIDTH);
        }
    }
    public DoubleProperty strokeWidthProperty() {
        if (null == strokeWidth) {
            strokeWidth = new DoublePropertyBase(_strokeWidth) {
                @Override protected void invalidated() {
                    set(Helper.clamp(1, 24, get()));
                    fireSeriesEvent(UPDATE_EVENT);
                }
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "strokeWidth"; }
            };
        }
        return strokeWidth;
    }

    public boolean isAnimated() { return null == animated ? _animated : animated.get(); }
    public void setAnimated(final boolean ANIMATED) {
        if (null == animated) {
            _animated = ANIMATED;
        }  else {
            animated.set(ANIMATED);
        }
    }
    public BooleanProperty animatedProperty() {
        if (null == animated) {
            animated = new BooleanPropertyBase(_animated) {
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "animated"; }
            };
        }
        return animated;
    }

    public long getAnimationDuration() { return null == animationDuration ? _animationDuration : animationDuration.get(); }
    public void setAnimationDuration(final long DURATION) {
        if (null == animationDuration) {
            _animationDuration = Helper.clamp(10, 10000, DURATION);
        } else {
            animationDuration.set(Helper.clamp(10, 10000, DURATION));
        }
    }
    public LongProperty animationDurationProperty() {
        if (null == animationDuration) {
            animationDuration = new LongPropertyBase(_animationDuration) {
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "animationDuration"; }
            };
        }
        return animationDuration;
    }

    // ADDED accessors for the withWrapping boolean value and associated property.

    public boolean isWithWrapping() { return null == withWrapping ? _withWrapping : withWrapping.get(); }
    public void setWithWrapping(final boolean WITH_WRAPPING) {
        if (null == withWrapping) {
            _withWrapping = WITH_WRAPPING;
            fireSeriesEvent(UPDATE_EVENT);
        } else {
            withWrapping.set(WITH_WRAPPING);
        }
    }
    public BooleanProperty withWrappingProperty() {
        if (null == withWrapping) {
            withWrapping = new BooleanPropertyBase(_withWrapping) {
                @Override protected void invalidated() { fireSeriesEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "withWrapping"; }
            };
        }
        return symbolsVisible;
    }

    public int getNoOfItems() { return items.size(); }

    public void dispose() { items.remove(itemListener); }

    public void refresh() { fireSeriesEvent(UPDATE_EVENT); }


    // ******************** Event handling ************************************
    public void setOnSeriesEvent(final SeriesEventListener LISTENER) { addSeriesEventListener(LISTENER); }
    public void addSeriesEventListener(final SeriesEventListener LISTENER) { if (!listeners.contains(LISTENER)) listeners.add(LISTENER); }
    public void removeSeriesEventListener(final SeriesEventListener LISTENER) { if (listeners.contains(LISTENER)) listeners.remove(LISTENER); }

    public void fireSeriesEvent(final SeriesEvent EVENT) {
        for (SeriesEventListener listener : listeners) { listener.onModelEvent(EVENT); }
    }
}
