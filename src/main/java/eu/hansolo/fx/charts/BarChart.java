/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2022 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.event.ChartEvt;
import eu.hansolo.fx.charts.event.SelectionEvt;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.series.ChartItemSeriesBuilder;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.InfoPopup;
import eu.hansolo.fx.charts.tools.NumberFormat;
import eu.hansolo.fx.charts.tools.Order;
import eu.hansolo.fx.geometry.Rectangle;
import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolboxfx.font.Fonts;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.LongProperty;
import javafx.beans.property.LongPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@DefaultProperty("children")
public class BarChart<T extends ChartItem> extends Region {
    private static final double                                    PREFERRED_WIDTH  = 250;
    private static final double                                    PREFERRED_HEIGHT = 250;
    private static final double                                    MINIMUM_WIDTH    = 50;
    private static final double                                    MINIMUM_HEIGHT   = 50;
    private static final double                                    MAXIMUM_WIDTH    = 4096;
    private static final double                                    MAXIMUM_HEIGHT   = 4096;
    private              double                                    size;
    private              double                                    width;
    private              double                                    height;
    private              Canvas                                    canvas;
    private              GraphicsContext                           ctx;
    private              Pane                                      pane;
    private              ChartItemSeries<T>                        series;
    private              Orientation                               _orientation;
    private              ObjectProperty<Orientation>               orientation;
    private              Paint                                     _backgroundFill;
    private              ObjectProperty<Paint>                     backgroundFill;
    private              Paint                                     _namesBackgroundFill;
    private              ObjectProperty<Paint>                     namesBackgroundFill;
    private              Color                                     _barBackgroundFill;
    private              ObjectProperty<Color>                     barBackgroundFill;
    private              Paint                                     _seriesFill;
    private              ObjectProperty<Paint>                     seriesFill;
    private              Color                                     _textFill;
    private              ObjectProperty<Color>                     textFill;
    private              Color                                     _namesTextFill;
    private              ObjectProperty<Color>                     namesTextFill;
    private              boolean                                   _barBackgroundVisible;
    private              BooleanProperty                           barBackgroundVisible;
    private              boolean                                   _shadowsVisible;
    private              BooleanProperty                           shadowsVisible;
    private              NumberFormat                              _numberFormat;
    private              ObjectProperty<NumberFormat>              numberFormat;
    private              boolean                                   _useItemFill;
    private              BooleanProperty                           useItemFill;
    private              boolean                                   _useItemTextFill;
    private              BooleanProperty                           useItemTextFill;
    private              boolean                                   _useNamesTextFill;
    private              BooleanProperty                           useNamesTextFill;
    private              boolean                                   _shortenNumbers;
    private              BooleanProperty                           shortenNumbers;
    private              boolean                                   _sorted;
    private              BooleanProperty                           sorted;
    private              Order                                     _order;
    private              ObjectProperty<Order>                     order;
    private              boolean                                   _animated;
    private              BooleanProperty                           animated;
    private              long                                      _animationDuration;
    private              LongProperty                              animationDuration;
    private              int                                       _minNumberOfBars;
    private              IntegerProperty                           minNumberOfBars;
    private              boolean                                   _useMinNumberOfBars;
    private              BooleanProperty                           useMinNumberOfBars;
    private              boolean                                   _useGivenColors;
    private              BooleanProperty                           useGivenColors;
    private              List<Color>                               colors;
    private              Map<Rectangle, ChartItem>                 rectangleItemMap;
    private              ListChangeListener<ChartItem>             chartItemListener;
    private              EvtObserver<ChartEvt>                     observer;
    private              EventHandler<MouseEvent>                  mouseHandler;
    private              Map<EvtType, List<EvtObserver<ChartEvt>>> observers;
    private              InfoPopup                                 popup;


    // ******************** Constructors **************************************
    public BarChart() {
        this(new ArrayList<>());
    }
    public BarChart(final List<T> items) {
        if (null == items) { throw new IllegalArgumentException("Items cannot be null or empty"); }
        _orientation            = Orientation.HORIZONTAL;
        _backgroundFill         = Color.TRANSPARENT;
        _barBackgroundFill      = Color.rgb(230, 230, 230);
        _namesBackgroundFill    = Color.TRANSPARENT;
        _seriesFill             = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(255, 105, 91)), new Stop(1, Color.rgb(217, 41, 76)));
        _textFill               = Color.WHITE;
        _namesTextFill          = Color.BLACK;
        _barBackgroundVisible   = false;
        _shadowsVisible         = false;
        _numberFormat           = NumberFormat.NUMBER;
        _useItemFill            = false;
        _useItemTextFill        = false;
        _useNamesTextFill       = false;
        _shortenNumbers         = false;
        _sorted                 = false;
        _order                  = Order.DESCENDING;
        _animated               = true;
        _animationDuration      = 1000;
        _minNumberOfBars        = 5;
        _useMinNumberOfBars     = false;
        _useGivenColors         = false;
        colors                  = new ArrayList<>(List.of(Color.rgb(253, 231, 37), Color.rgb(170, 220, 49), Color.rgb(94, 200, 99), Color.rgb(40, 173, 129), Color.rgb(33, 144, 140), Color.rgb(44, 114, 142), Color.rgb(59, 82, 139), Color.rgb(71, 45, 123), Color.rgb(68, 4, 84)));
        observers               = new ConcurrentHashMap<>();
        popup                   = new InfoPopup();
        rectangleItemMap        = new HashMap<>();
        observer                = evt -> {
            EvtType<? extends Evt> type = evt.getEvtType();
            if (type.equals(ChartEvt.ITEM_UPDATE) || type.equals(ChartEvt.FINISHED)) {
                if (getSorted()) { series.sort(getOrder()); }
                switch(getOrientation()) {
                    case HORIZONTAL -> drawHorizontalChart();
                    case VERTICAL   -> drawVerticalChart();
                }
            }
        };
        chartItemListener       = c -> {
            boolean animated          = series.isAnimated();
            long    animationDuration = series.getAnimationDuration();
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedItem -> {
                        addedItem.addChartEvtObserver(ChartEvt.ANY, observer);
                        if (animated) { addedItem.setAnimated(animated); }
                        addedItem.setAnimationDuration(animationDuration);
                    });
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedItem -> removedItem.removeChartEvtObserver(ChartEvt.ANY, observer));
                }
            }
            switch(getOrientation()) {
                case HORIZONTAL -> drawHorizontalChart();
                case VERTICAL   -> drawVerticalChart();
            }
        };
        mouseHandler            = e -> handleMouseEvents(e);

        this.series = ChartItemSeriesBuilder.create()
                                            .name("Series")
                                            .items(items)
                                            .fill(_seriesFill)
                                            .textFill(_textFill)
                                            .animated(true)
                                            .animationDuration(_animationDuration)
                                            .build();

        prepareSeries();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
            Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        getStyleClass().add("bar-chart");

        canvas = new Canvas(size * 0.9, 0.9);
        ctx    = canvas.getGraphicsContext2D();

        pane = new Pane(canvas);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());

        series.getItems().forEach(item -> item.addChartEvtObserver(ChartEvt.ANY, observer));
        series.getItems().addListener(chartItemListener);

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        addChartEvtObserver(SelectionEvt.ANY, e -> {
            popup.update((SelectionEvt) e);
            popup.animatedShow(getScene().getWindow());
        });
    }


    // ******************** Methods *******************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
    }

    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public List<T> getItems() { return series.getItems(); }
    public void setItems(final T... items) {
        setItems(Arrays.asList(items));
    }
    public void setItems(final List<T> items) {
        this.series.getItems().forEach(item -> item.removeChartEvtObserver(ChartEvt.ANY, observer));
        this.series.getItems().removeListener(chartItemListener);

        this.series.getItems().clear();
        this.series.setItems(items);
        prepareSeries();

        this.series.getItems().forEach(item -> item.addChartEvtObserver(ChartEvt.ANY, observer));
        this.series.getItems().addListener(chartItemListener);

        if (getSorted()) { this.series.sort(getOrder()); }

        redraw();
    }

    public Orientation getOrientation() { return null == orientation ? _orientation : orientation.get(); }
    public void setOrientation(final Orientation orientation) {
        if (null == this.orientation) {
            _orientation = orientation;
            redraw();
        } else {
            this.orientation.set(orientation);
        }
    }
    public ObjectProperty<Orientation> orientationProperty() {
        if (null == orientation) {
            orientation = new ObjectPropertyBase<>(_orientation) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "orientation"; }
            };
            _orientation = null;
        }
        return orientation;
    }

    public Paint getBackgroundFill() { return null == backgroundFill ? _backgroundFill : backgroundFill.get(); }
    public void setBackgroundFill(final Paint backgroundFill) {
        if (null == this.backgroundFill) {
            _backgroundFill = backgroundFill;
            redraw();
        } else {
            this.backgroundFill.set(backgroundFill);
        }
    }
    public ObjectProperty<Paint> backgroundFillProperty() {
        if (null == backgroundFill) {
            backgroundFill = new ObjectPropertyBase<>(_backgroundFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "backgroundFill"; }
            };
            _backgroundFill = null;
        }
        return backgroundFill;
    }

    public Paint getNamesBackgroundFill() { return null == namesBackgroundFill ? _namesBackgroundFill : namesBackgroundFill.get(); }
    public void setNamesBackgroundFill(final Paint namesBackgroundFill) {
        if (null == this.namesBackgroundFill) {
            _namesBackgroundFill = namesBackgroundFill;
            redraw();
        } else {
            this.namesBackgroundFill.set(namesBackgroundFill);
        }
    }
    public ObjectProperty<Paint> namesBackgroundFillProperty() {
        if (null == namesBackgroundFill) {
            namesBackgroundFill  = new ObjectPropertyBase<>(_namesBackgroundFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "namesBackgroundFill"; }
            };
            _namesBackgroundFill = null;
        }
        return namesBackgroundFill;
    }

    public Color getBarBackgroundFill() { return null == barBackgroundFill ? _barBackgroundFill : barBackgroundFill.get(); }
    public void setBarBackgroundFill(final Color barBackgroundFill) {
        if (null == this.barBackgroundFill) {
            _barBackgroundFill = barBackgroundFill;
            redraw();
        } else {
            this.barBackgroundFill.set(barBackgroundFill);
        }
    }
    public ObjectProperty<Color> barBackgroundFillProperty() {
        if (null == barBackgroundFill) {
            barBackgroundFill = new ObjectPropertyBase<>(_barBackgroundFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "barBackgroundFill"; }
            };
            _barBackgroundFill = null;
        }
        return barBackgroundFill;
    }

    public Paint getSeriesFill() { return null == seriesFill ? _seriesFill : seriesFill.get(); }
    public void setSeriesFill(final Paint seriesFill) {
        if (null == this.seriesFill) {
            _seriesFill = seriesFill;
            series.setFill(_seriesFill);
            redraw();
        } else {
            this.seriesFill.set(seriesFill);
        }
    }
    public ObjectProperty<Paint> seriesFillProperty() {
        if (null == seriesFill) {
            seriesFill = new ObjectPropertyBase<>(_seriesFill) {
                @Override protected void invalidated() {
                    series.setFill(get());
                    redraw();
                }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "seriesFill"; }
            };
            _seriesFill = null;
        }
        return seriesFill;
    }

    public Color getTextFill() { return null == textFill ? _textFill : textFill.get(); }
    public void setTextFill(final Color textFill) {
        if (null == this.textFill) {
            _textFill = textFill;
            redraw();
        } else {
            this.textFill.set(textFill);
        }
    }
    public ObjectProperty<Color> textFillProperty() {
        if (null == textFill) {
            textFill = new ObjectPropertyBase<>(_textFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "textFill"; }
            };
            _textFill = null;
        }
        return textFill;
    }

    public Color getNamesTextFill() { return null == namesTextFill ? _namesTextFill : namesTextFill.get(); }
    public void setNamesTextFill(final Color namesTextFill) {
        if (null == this.namesTextFill) {
            _namesTextFill = namesTextFill;
            redraw();
        } else {
            this.namesTextFill.set(namesTextFill);
        }
    }
    public ObjectProperty<Color> namesTextFillProperty() {
        if (null == namesTextFill) {
            namesTextFill  = new ObjectPropertyBase<>(_namesTextFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "namesTextFill"; }
            };
            _namesTextFill = null;
        }
        return namesTextFill;
    }

    public boolean getBarBackgroundVisible() { return null == barBackgroundVisible ? _barBackgroundVisible : barBackgroundVisible.get(); }
    public void setBarBackgroundVisible(final boolean barBackgroundVisible) {
        if (null == this.barBackgroundVisible) {
            _barBackgroundVisible = barBackgroundVisible;
            redraw();
        } else {
            this.barBackgroundVisible.set(barBackgroundVisible);
        }
    }
    public BooleanProperty barBackgroundVisibleProperty() {
        if (null == barBackgroundVisible) {
            barBackgroundVisible = new BooleanPropertyBase(_barBackgroundVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "barBackgroundVisible"; }
            };
        }
        return barBackgroundVisible;
    }

    public boolean getShadowsVisible() { return null == shadowsVisible ? _shadowsVisible : shadowsVisible.get(); }
    public void setShadowsVisible(final boolean shadowsVisible) {
        if (null == this.shadowsVisible) {
            _shadowsVisible = shadowsVisible;
            redraw();
        } else {
            this.shadowsVisible.set(shadowsVisible);
        }
    }
    public BooleanProperty shadowsVisibleProperty() {
        if (null == shadowsVisible) {
            shadowsVisible = new BooleanPropertyBase(_shadowsVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "shadowsVisible"; }
            };
        }
        return shadowsVisible;
    }

    public NumberFormat getNumberFormat() { return null == numberFormat ? _numberFormat : numberFormat.get(); }
    public void setNumberFormat(final NumberFormat format) {
        if (null == numberFormat) {
            _numberFormat = format;
            updatePopup();
            redraw();
        } else {
            numberFormat.set(format);
        }
    }
    public ObjectProperty<NumberFormat> numberFormatProperty() {
        if (null == numberFormat) {
            numberFormat = new ObjectPropertyBase<NumberFormat>(_numberFormat) {
                @Override protected void invalidated() {
                    updatePopup();
                    redraw();
                }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "numberFormat"; }
            };
            _numberFormat = null;
        }
        return numberFormat;
    }

    public boolean getUseItemFill() { return null == useItemFill ? _useItemFill : useItemFill.get(); }
    public void setUseItemFill(final boolean useItemFill) {
        if (null == this.useItemFill) {
            _useItemFill = useItemFill;
            redraw();
        } else {
            this.useItemFill.set(useItemFill);
        }
    }
    public BooleanProperty useItemFillProperty() {
        if (null == useItemFill) {
            useItemFill = new BooleanPropertyBase(_useItemFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "useItemFill"; }
            };
        }
        return useItemFill;
    }

    public boolean getUseItemTextFill() { return null == useItemTextFill ? _useItemTextFill : useItemTextFill.get(); }
    public void setUseItemTextFill(final boolean useItemTextFill) {
        if (null == this.useItemTextFill) {
            _useItemTextFill = useItemTextFill;
            redraw();
        } else {
            this.useItemTextFill.set(useItemTextFill);
        }
    }
    public BooleanProperty useItemTextFillProperty() {
        if (null == useItemTextFill) {
            useItemTextFill = new BooleanPropertyBase(_useItemTextFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "useItemTextFill"; }
            };
        }
        return useItemTextFill;
    }

    public boolean getUseNamesTextFill() { return null == useNamesTextFill ? _useNamesTextFill : useNamesTextFill.get(); }
    public void setUseNamesTextFill(final boolean useNamesTextFill) {
        if (null == this.useNamesTextFill) {
            _useNamesTextFill = useNamesTextFill;
            redraw();
        } else {
            this.useNamesTextFill.set(useNamesTextFill);
        }
    }
    public BooleanProperty useNamesTextFillProperty() {
        if (null == useNamesTextFill) {
            useNamesTextFill = new BooleanPropertyBase(_useNamesTextFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "useNamesTextFill"; }
            };
        }
        return useNamesTextFill;
    }

    public boolean getShortenNumbers() { return null == shortenNumbers ? _shortenNumbers : shortenNumbers.get(); }
    public void setShortenNumbers(final boolean shorten) {
        if (null == shortenNumbers) {
            _shortenNumbers = shorten;
            redraw();
        } else {
            shortenNumbers.set(shorten);
        }
    }
    public BooleanProperty shortenNumbersProperty() {
        if (null == shortenNumbers) {
            shortenNumbers = new BooleanPropertyBase(_shortenNumbers) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "shortenNumbers"; }
            };
        }
        return shortenNumbers;
    }

    public boolean getSorted() { return null == sorted ? _sorted : sorted.get(); }
    public void setSorted(final boolean sorted) {
        if (null == this.sorted) {
            _sorted = sorted;
            redraw();
        } else {
            this.sorted.set(sorted);
        }
    }
    public BooleanProperty sortedProperty() {
        if (null == sorted) {
            sorted = new BooleanPropertyBase() {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "sorted"; }
            };
        }
        return sorted;
    }

    public Order getOrder() { return null == order ? _order : order.get(); }
    public void setOrder(final Order order) {
        if (null == this.order) {
            _order = order;
            redraw();
        } else {
            this.order.set(order);
        }
    }
    public ObjectProperty<Order> orderProperty() {
        if (null == order) {
            order = new ObjectPropertyBase<>(_order) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "order"; }
            };
            _order = null;
        }
        return order;
    }

    public boolean isAnimated() { return null == animated ? _animated : animated.get(); }
    public void setAnimated(final boolean animated) {
        if (null == this.animated) {
            _animated = animated;
            series.setAnimated(_animated);
            prepareSeries();
        } else {
            this.animated.set(animated);
        }
    }
    public BooleanProperty animatedProperty() {
        if (null == animated) {
            animated = new BooleanPropertyBase(_animated) {
                @Override protected void invalidated() {
                    series.setAnimated(get());
                    prepareSeries();
                }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "animated"; }
            };
        }
        return animated;
    }

    public long getAnimationDuration() { return null == animationDuration ? _animationDuration : animationDuration.get(); }
    public void setAnimationDuration(final long animationDuration) {
        if (null == this.animationDuration) {
            _animationDuration = Helper.clamp(10, 10000, animationDuration);
            series.setAnimationDuration(_animationDuration);
            prepareSeries();
        } else {
            this.animationDuration.set(animationDuration);
        }
    }
    public LongProperty animationDurationProperty() {
        if (null == animationDuration) {
            animationDuration = new LongPropertyBase(_animationDuration) {
                @Override protected void invalidated() {
                    long ad = Helper.clamp(10, 10000, get());
                    series.setAnimationDuration(ad);
                    prepareSeries();
                    set(ad);
                }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "animationDuration"; }
            };
        }
        return animationDuration;
    }

    public int getMinNumberOfBars() { return null == minNumberOfBars ? _minNumberOfBars : minNumberOfBars.get(); }
    public void setMinNumberOfBars(final int minNumberOfBars) {
        if (null == this.minNumberOfBars) {
            _minNumberOfBars = Helper.clamp(1, Integer.MAX_VALUE, minNumberOfBars);
            redraw();
        } else {
            this.minNumberOfBars.set(minNumberOfBars);
        }
    }
    public IntegerProperty minNumberOfBarsProperty() {
        if (null == minNumberOfBars) {
            minNumberOfBars = new IntegerPropertyBase(_minNumberOfBars) {
                @Override protected void invalidated() {
                    set(Helper.clamp(1, Integer.MAX_VALUE, get()));
                    redraw();
                }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "minNumberOfBars"; }
            };
        }
        return minNumberOfBars;
    }

    public boolean getUseMinNumberOfBars() { return null == useMinNumberOfBars ? _useMinNumberOfBars : useMinNumberOfBars.get(); }
    public void setUseMinNumberOfBars(final boolean useMinNumberOfBars) {
        if (null == this.useMinNumberOfBars) {
            _useMinNumberOfBars = useMinNumberOfBars;
            redraw();
        } else {
            this.useMinNumberOfBars.set(useMinNumberOfBars);
        }
    }
    public BooleanProperty useMinNumberOfBarsProperty() {
        if (null == useMinNumberOfBars) {
            useMinNumberOfBars = new BooleanPropertyBase(_useMinNumberOfBars) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "useMinNumberOfBars"; }
            };
        }
        return useMinNumberOfBars;
    }

    public boolean useGivenColors() { return null == useGivenColors ? _useGivenColors : useGivenColors.get(); }
    public void setUseGivenColors(final boolean useGivenColors) {
        if (null == this.useGivenColors) {
            _useGivenColors = useGivenColors;
            redraw();
        } else {
            this.useGivenColors.set(useGivenColors);
        }
    }
    public BooleanProperty getUseGivenColorsProperty() {
        if (null == useGivenColors) {
            useGivenColors = new BooleanPropertyBase(_useGivenColors) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BarChart.this; }
                @Override public String getName() { return "useGivenColors"; }
            };
        }
        return useGivenColors;
    }

    public List<Color> getColors() { return colors; }
    public void setColors(final List<Color> colors) {
        if (colors.isEmpty()) { throw new IllegalArgumentException("colors cannot be empty"); }
        this.colors.clear();
        this.colors.addAll(colors);
        if (useGivenColors()) { redraw(); }
    }

    /**
     * Calling this method will render this chart/plot to a png given of the given width and height
     * @param filename The path and name of the file  /Users/hansolo/Desktop/plot.png
     * @param width The width of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @param height The height of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @return True if the procedure was successful, otherwise false
     */
    public boolean renderToImage(final String filename, final int width, final int height) {
        return Helper.renderToImage(BarChart.this, width, height, filename);
    }

    /**
     * Calling this method will render this chart/plot to a png given of the given width and height
     * @param width The width of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @param height The height of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @return A BufferedImage of this chart in the given dimension
     */
    public BufferedImage renderToImage(final int width, final int height) {
        return Helper.renderToImage(BarChart.this, width, height);
    }

    private void handleMouseEvents(final MouseEvent evt) {
        final double x = evt.getX();
        final double y = evt.getY();

        Optional<Entry<Rectangle, ChartItem>> opt = rectangleItemMap.entrySet().stream().filter(entry -> entry.getKey().contains(x, y)).findFirst();
        if (opt.isPresent()) {
            popup.setX(evt.getScreenX());
            popup.setY(evt.getScreenY() - popup.getHeight());
            ChartItem selectedItem = opt.get().getValue();
            fireChartEvt(new SelectionEvt(series, opt.get().getValue()));
        }
    }

    private void updatePopup() {
        switch(getNumberFormat()) {
            case NUMBER:
                popup.setDecimals(0);
                break;
            case FLOAT_1_DECIMAL:
                popup.setDecimals(1);
                break;
            case FLOAT_2_DECIMALS:
                popup.setDecimals(2);
                break;
            case FLOAT:
                popup.setDecimals(8);
                break;
            case PERCENTAGE          :
                popup.setDecimals(0);
                break;
            case PERCENTAGE_1_DECIMAL:
                popup.setDecimals(1);
                break;
        }
    }


    // ******************** Event Handling ************************************
    public void addChartEvtObserver(final EvtType type, final EvtObserver<ChartEvt> observer) {
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public void removeChartEvtObserver(final EvtType type, final EvtObserver<ChartEvt> observer) {
        if (observers.containsKey(type)) {
            if (observers.get(type).contains(observer)) {
                observers.get(type).remove(observer);
            }
        }
    }
    public void removeAllChartEvtObservers() { observers.clear(); }

    public void fireChartEvt(final ChartEvt evt) {
        final EvtType type = evt.getEvtType();
        observers.entrySet().stream().filter(entry -> entry.getKey().equals(ChartEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (observers.containsKey(type) && !type.equals(ChartEvt.ANY)) {
            observers.get(type).forEach(observer -> observer.handle(evt));
        }
    }


    // ******************** Drawing *******************************************
    private void prepareSeries() {
        boolean animated          = this.series.isAnimated();
        long    animationDuration = this.series.getAnimationDuration();
        this.series.getItems().forEach(item -> {
            item.setAnimated(animated);
            item.setAnimationDuration(animationDuration);
        });
    }

    private void drawHorizontalChart() {
        rectangleItemMap.clear();
        double          inset                = 5;
        double          chartWidth           = this.width - 2 * inset;
        double          chartHeight          = this.height - 2 * inset;
        List<T>         items                = series.getItems();
        double          noOfItems            = items.size();
        double          namesWidth           = chartWidth * 0.2;
        double          maxBarWidth          = chartWidth - namesWidth;
        double          minNumberOfBars      = noOfItems > getMinNumberOfBars() ? noOfItems : getMinNumberOfBars();
        double          barHeight            = getUseMinNumberOfBars() ? chartHeight / (minNumberOfBars + (minNumberOfBars * 0.4)) : chartHeight / (noOfItems + (noOfItems * 0.4));
        double          cornerRadius         = barHeight * 0.75;
        double          barSpacer            = getUseMinNumberOfBars() ? (chartHeight - (minNumberOfBars * barHeight)) / (minNumberOfBars - 1) : (chartHeight - (noOfItems * barHeight)) / (noOfItems - 1);
        double          maxValue             = series.getMaxValue();
        NumberFormat    numberFormat         = getNumberFormat();
        Color           valueTextFill        = getTextFill();
        Color           namesTextFill        = getNamesTextFill();
        boolean         useItemFill          = getUseItemFill();
        boolean         useItemTextFill      = getUseItemTextFill();
        boolean         useNamesTextFill     = getUseNamesTextFill();
        String          formatString         = numberFormat.formatString();
        Paint           barFill              = series.getFill();
        boolean         shortenNumbers       = getShortenNumbers();
        boolean         barBackgroundVisible = getBarBackgroundVisible();
        Color           barBackgroundFill    = getBarBackgroundFill();
        Paint           namesBackgroundFill  = getNamesBackgroundFill().equals(Color.TRANSPARENT) ? getBackgroundFill() : getNamesBackgroundFill();
        boolean         shadowsVisible       = getShadowsVisible();
        double          valueFontSize        = barHeight * 0.5;
        double          nameFontSize         = barHeight * 0.5;
        Font            valueFont            = Fonts.latoRegular(valueFontSize);
        Font            nameFont             = Fonts.latoRegular(nameFontSize);
        DropShadow      shadow               = new DropShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.15), barHeight * 0.1, 0.0, 1, barHeight * 0.1);
        double          barX                 = inset + namesWidth;
        int             givenColorCounter    = 0;

        ctx.clearRect(0, 0, width, height);
        ctx.setFill(getBackgroundFill());
        ctx.fillRect(0, 0, width, height);
        ctx.setLineCap(StrokeLineCap.BUTT);
        ctx.setTextAlign(TextAlignment.RIGHT);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setFont(valueFont);

        // Draw bars
        for (int i = 0 ; i < noOfItems ; i++) {
            ChartItem item      = items.get(i);
            double    itemValue = Helper.clamp(0, Double.MAX_VALUE, item.getValue());
            double    barWidth  = 0 == maxValue ? 0 : itemValue / maxValue * maxBarWidth;
            double    barY      = inset + (i * barHeight) + (i * barSpacer);

            // Bar
            if (barBackgroundVisible) {
                ctx.setFill(barBackgroundFill);
                ctx.beginPath();
                ctx.moveTo(barX, barY);
                ctx.lineTo(barX + maxBarWidth - cornerRadius, barY);
                ctx.bezierCurveTo(barX + maxBarWidth, barY, barX + maxBarWidth, barY + barHeight, barX + maxBarWidth - cornerRadius, barY + barHeight);
                ctx.lineTo(barX, barY + barHeight);
                ctx.lineTo(barX, barY);
                ctx.closePath();
                ctx.fill();
            }

            ctx.save();
            if (shadowsVisible) { ctx.setEffect(shadow); }

            if (useGivenColors()) {
                ctx.setFill(colors.get(givenColorCounter));
                givenColorCounter++;
                if (givenColorCounter > colors.size() - 1) { givenColorCounter = 0; }
            } else {
                ctx.setFill(useItemFill ? item.getFill() : barFill);
            }

            ctx.beginPath();
            ctx.moveTo(barX, barY);
            if (barWidth < cornerRadius) {
                ctx.bezierCurveTo(barX + cornerRadius, barY, barX + cornerRadius, barY + barHeight, barX, barY + barHeight);
            } else {
                ctx.lineTo(barX + barWidth - cornerRadius, barY);
                ctx.bezierCurveTo(barX + barWidth, barY, barX + barWidth, barY + barHeight, barX + barWidth - cornerRadius, barY + barHeight);
            }
            ctx.lineTo(barX, barY + barHeight);
            ctx.lineTo(barX, barY);
            ctx.closePath();
            ctx.fill();
            ctx.restore();
            rectangleItemMap.put(new Rectangle(barX, barY, barWidth, barHeight), item);

            // Bar Value
            if (valueFontSize > 6) {
                ctx.setFill(useItemTextFill ? item.getTextFill() : valueTextFill);
                String valueText;
                double valueX;
                double valueTextWidth;
                if (shortenNumbers) {
                    valueText      = Helper.shortenNumber((long) itemValue);
                    valueTextWidth = Helper.getTextDimension(valueText, valueFont).getWidth();
                    valueX         = barX + barWidth - barHeight * 0.5;
                    ctx.setTextAlign(TextAlignment.RIGHT);
                } else {
                    if (NumberFormat.PERCENTAGE == numberFormat || NumberFormat.PERCENTAGE_1_DECIMAL == numberFormat) {
                        valueText      = String.format(Locale.US, formatString, itemValue / maxValue * 100);
                        valueTextWidth = Helper.getTextDimension(valueText, valueFont).getWidth();
                        valueX         = barX + 5;
                        ctx.setTextAlign(TextAlignment.LEFT);
                    } else {
                        valueText      = String.format(Locale.US, formatString, itemValue);
                        valueTextWidth = Helper.getTextDimension(valueText, valueFont).getWidth();
                        valueX         = barX + barWidth - barHeight * 0.5;
                        ctx.setTextAlign(TextAlignment.RIGHT);
                    }
                }
                valueX = barWidth <= (valueTextWidth * 2) ? barX + valueTextWidth + 5 : valueX;
                ctx.fillText(valueText, valueX, barY + barHeight * 0.5);
            }
        }

        // Draw names
        ctx.setFill(namesBackgroundFill);
        ctx.fillRect(inset, inset, namesWidth, chartHeight);
        for (int i = 0 ; i < items.size() ; i++) {
            ChartItem item  = items.get(i);
            String    name  = item.getName();
            double    nameX = inset + namesWidth * 0.95;
            double    nameY = inset + (i * barHeight) + (i * barSpacer);

            ctx.setTextAlign(TextAlignment.RIGHT);
            ctx.setFill(useNamesTextFill ? namesTextFill : item.getTextFill());
            ctx.setFont(nameFont);
            ctx.fillText(name, nameX, nameY + barHeight * 0.5, namesWidth * 0.9);
        }

        if (shadowsVisible) {
            ctx.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.TRANSPARENT), new Stop(1.0, Color.rgb(0, 0, 0, 0.25))));
            ctx.fillRect(inset - 6, inset, 6, chartHeight);
            ctx.setFill(new LinearGradient(1, 0, 0, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.TRANSPARENT), new Stop(1.0, Color.rgb(0, 0, 0, 0.25))));
            ctx.fillRect(inset + namesWidth, inset, 6, chartHeight);
        }
    }

    private void drawVerticalChart() {
        rectangleItemMap.clear();
        double          inset                = 5;
        double          chartWidth           = this.width - 2 * inset;
        double          chartHeight          = this.height - 2 * inset;
        List<T>         items                = series.getItems();
        double          noOfItems            = items.size();
        double          namesHeight          = chartHeight * 0.1;
        double          maxBarHeight         = chartHeight - namesHeight;
        double          minNumberOfBars      = noOfItems > getMinNumberOfBars() ? noOfItems : getMinNumberOfBars();
        double          barWidth             = getUseMinNumberOfBars() ? chartWidth / (minNumberOfBars + (minNumberOfBars * 0.4)) : chartWidth / (noOfItems + (noOfItems * 0.4));
        double          cornerRadius         = barWidth * 0.75;
        double          barSpacer            = getUseMinNumberOfBars() ? (chartWidth - (minNumberOfBars * barWidth)) / (minNumberOfBars - 1) : (chartWidth - (noOfItems * barWidth)) / (noOfItems - 1);
        double          maxValue             = series.getMaxValue();
        NumberFormat    numberFormat         = getNumberFormat();
        Color           valueTextFill        = getTextFill();
        Color           namesTextFill        = getNamesTextFill();
        boolean         useItemFill          = getUseItemFill();
        boolean         useItemTextFill      = getUseItemTextFill();
        boolean         useNamesTextFill     = getUseNamesTextFill();
        String          formatString         = numberFormat.formatString();
        Paint           barFill              = series.getFill();
        boolean         shortenNumbers       = getShortenNumbers();
        boolean         barBackgroundVisible = getBarBackgroundVisible();
        Color           barBackgroundFill    = getBarBackgroundFill();
        Paint           namesBackgroundFill  = getNamesBackgroundFill().equals(Color.TRANSPARENT) ? getBackgroundFill() : getNamesBackgroundFill();
        boolean         shadowsVisible       = getShadowsVisible();
        double          valueFontSize        = barWidth * 0.25;
        double          nameFontSize         = barWidth * 0.25;
        Font            valueFont            = Fonts.latoRegular(valueFontSize);
        Font            nameFont             = Fonts.latoRegular(nameFontSize);
        DropShadow      shadow               = new DropShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.15), barWidth * 0.1, 0.0, barWidth * 0.1, 1);
        double          barY                 = chartHeight - inset - namesHeight;
        int             givenColorCounter    = 0;

        ctx.clearRect(0, 0, width, height);
        ctx.setFill(getBackgroundFill());
        ctx.fillRect(0, 0, width, height);
        ctx.setLineCap(StrokeLineCap.BUTT);
        ctx.setTextAlign(TextAlignment.RIGHT);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setFont(valueFont);

        // Draw bars
        for (int i = 0 ; i < noOfItems ; i++) {
            ChartItem item      = items.get(i);
            double    itemValue = Helper.clamp(0, Double.MAX_VALUE, item.getValue());
            double    barHeight = 0 == maxValue ? 0 : itemValue / maxValue * maxBarHeight;
            double    barX      = inset + (i * barWidth) + (i * barSpacer);

            // Bar
            if (barBackgroundVisible) {
                ctx.setFill(barBackgroundFill);
                ctx.beginPath();
                ctx.moveTo(barX, barY);
                ctx.lineTo(barX, barY - maxBarHeight + cornerRadius);
                ctx.bezierCurveTo(barX, barY - maxBarHeight, barX + barWidth, barY - maxBarHeight, barX + barWidth, barY - maxBarHeight + cornerRadius);
                ctx.lineTo(barX + barWidth, barY);
                ctx.lineTo(barX, barY);
                ctx.closePath();
                ctx.fill();
            }

            ctx.save();
            if (shadowsVisible) { ctx.setEffect(shadow); }

            if (useGivenColors()) {
                ctx.setFill(colors.get(givenColorCounter));
                givenColorCounter++;
                if (givenColorCounter > colors.size() - 1) { givenColorCounter = 0; }
            } else {
                ctx.setFill(useItemFill ? item.getFill() : barFill);
            }

            ctx.beginPath();
            ctx.moveTo(barX, barY);
            if (barHeight < cornerRadius) {
                ctx.bezierCurveTo(barX, barY - cornerRadius, barX + barWidth, barY - cornerRadius, barX + barWidth, barY);
            } else {
                ctx.lineTo(barX, barY - barHeight + cornerRadius);
                ctx.bezierCurveTo(barX, barY - barHeight, barX + barWidth, barY - barHeight, barX + barWidth, barY - barHeight + cornerRadius);
            }
            ctx.lineTo(barX + barWidth, barY);
            ctx.lineTo(barX, barY);
            ctx.closePath();
            ctx.fill();
            ctx.restore();
            rectangleItemMap.put(new Rectangle(barX, barY - maxBarHeight, barWidth, barHeight), item);

            // Bar Value
            if (valueFontSize > 6) {
                ctx.setFill(useItemTextFill ? item.getTextFill() : valueTextFill);
                ctx.setTextAlign(TextAlignment.CENTER);
                String valueText;
                double valueY;
                double valueTextHeight;
                if (shortenNumbers) {
                    valueText       = Helper.shortenNumber((long) itemValue);
                    valueTextHeight = Helper.getTextDimension(valueText, valueFont).getHeight();
                    valueY          = barY - barHeight + barWidth * 0.5;
                } else {
                    if (NumberFormat.PERCENTAGE == numberFormat || NumberFormat.PERCENTAGE_1_DECIMAL == numberFormat) {
                        valueText       = String.format(Locale.US, formatString, itemValue / maxValue * 100);
                        valueTextHeight = Helper.getTextDimension(valueText, valueFont).getHeight();
                        valueY          = barY - 5 - valueFontSize;
                    } else {
                        valueText       = String.format(Locale.US, formatString, itemValue);
                        valueTextHeight = Helper.getTextDimension(valueText, valueFont).getHeight();
                        valueY          = barY - barHeight + barWidth * 0.5;
                    }
                }
                valueY = barHeight <= (valueTextHeight * 2) ? barY - valueTextHeight - 5 : valueY;
                ctx.fillText(valueText, barX + (barWidth * 0.5), valueY);
            }
        }

        // Draw names
        ctx.setFill(namesBackgroundFill);
        ctx.fillRect(inset, chartHeight - inset - namesHeight, chartWidth, namesHeight);
        for (int i = 0 ; i < items.size() ; i++) {
            ChartItem item  = items.get(i);
            String    name  = item.getName();
            double    nameY = chartHeight - inset - namesHeight * 0.5;
            double    nameX = inset + (i * barWidth) + (i * barSpacer);

            ctx.setTextAlign(TextAlignment.CENTER);
            ctx.setFill(useNamesTextFill ? namesTextFill : item.getTextFill());
            ctx.setFont(nameFont);
            ctx.fillText(name, nameX + barWidth * 0.5, nameY, barWidth);
        }

        if (shadowsVisible) {
            ctx.setFill(new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.TRANSPARENT), new Stop(1.0, Color.rgb(0, 0, 0, 0.25))));
            ctx.fillRect(inset, chartHeight - namesHeight - inset - 6, chartWidth, 6);
            ctx.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(1.0, Color.rgb(0, 0, 0, 0.25)), new Stop(0.0, Color.TRANSPARENT)));
            ctx.fillRect(inset, chartHeight - inset, chartWidth, 6);
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            canvas.setWidth(width);
            canvas.setHeight(height);

            redraw();
        }
    }

    private void redraw() {
        if (getSorted()) { series.sort(getOrder()); }
        switch(getOrientation()) {
            case HORIZONTAL -> drawHorizontalChart();
            case VERTICAL   -> drawVerticalChart();
        }
    }
}
