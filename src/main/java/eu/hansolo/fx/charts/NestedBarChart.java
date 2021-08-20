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

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.event.SelectionEvent;
import eu.hansolo.fx.charts.event.SelectionEventListener;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.InfoPopup;
import eu.hansolo.fx.charts.tools.Order;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * User: hansolo
 * Date: 28.12.17
 * Time: 13:35
 */
@DefaultProperty("children")
public class NestedBarChart extends Region implements ChartArea {
    private static final double                                       PREFERRED_WIDTH  = 250;
    private static final double                                       PREFERRED_HEIGHT = 150;
    private static final double                                       MINIMUM_WIDTH    = 50;
    private static final double                                       MINIMUM_HEIGHT   = 50;
    private static final double                                       MAXIMUM_WIDTH    = 2048;
    private static final double                                       MAXIMUM_HEIGHT   = 2048;
    private              double                                       size;
    private              double                                       width;
    private              double                                       height;
    private              Paint                                        _chartBackground;
    private              ObjectProperty<Paint>                        chartBackground;
    private              Canvas                                       canvas;
    private              GraphicsContext                              ctx;
    private              Pane                                         pane;
    private              ObservableList<ChartItemSeries<ChartItem>>   series;
    private              Order                                        _order;
    private              ObjectProperty<Order>                        order;
    private              EventHandler<MouseEvent>                     clickHandler;
    private              CopyOnWriteArrayList<SelectionEventListener> listeners;
    private              InfoPopup                                    popup;
    private              double                                       spacer;
    private              boolean                                      _seriesTitleVisible;
    private              BooleanProperty                              seriesTitleVisible;
    private              Color                                        _seriesTitleColor;
    private              ObjectProperty<Color>                        seriesTitleColor;


    // ******************** Constructors **************************************
    public NestedBarChart() {
        this(new ArrayList<>(), Color.TRANSPARENT);
    }
    public NestedBarChart(final ChartItemSeries<ChartItem>... SERIES) {
        this(Arrays.asList(SERIES), Color.TRANSPARENT);
    }
    public NestedBarChart(final List<ChartItemSeries<ChartItem>> SERIES) {
        this(SERIES, Color.TRANSPARENT);
    }
    public NestedBarChart(final List<ChartItemSeries<ChartItem>> SERIES, final Paint BACKGROUND) {
        width               = PREFERRED_WIDTH;
        height              = PREFERRED_HEIGHT;
        size                = PREFERRED_HEIGHT;
        series              = FXCollections.observableArrayList(SERIES);
        _order              = Order.DESCENDING;
        _chartBackground    = BACKGROUND;
        spacer              = -1;
        _seriesTitleVisible = false;
        _seriesTitleColor   = null;
        clickHandler        = e -> checkForClick(e);
        listeners           = new CopyOnWriteArrayList<>();
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

        getStyleClass().add("nested-bar-chart");

        popup = new InfoPopup();

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        pane = new Pane(canvas);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, clickHandler);
        setOnSelectionEvent(e -> {
            popup.update(e);
            popup.animatedShow(getScene().getWindow());
        });
    }


    // ******************** Methods *******************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
    }

    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public void dispose() { canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, clickHandler);}

    public List<ChartItemSeries<ChartItem>> getSeries() { return series; }
    public void setSeries(final ChartItemSeries<ChartItem>... SERIES) {
        setSeries(Arrays.asList(SERIES));
    }
    public void setSeries(final List<ChartItemSeries<ChartItem>> SERIES) {
        series.clear();
        SERIES.forEach(item -> series.add(item));
        redraw();
    }
    public void addSeries(final ChartItemSeries<ChartItem> SERIES) {
        if (!series.contains(SERIES)) {
            series.add(SERIES);
            redraw();
        }
    }
    public void addSeries(final ChartItemSeries<ChartItem>... SERIES) {
        addSeries(Arrays.asList(SERIES));
    }
    public void addSeries(final List<ChartItemSeries<ChartItem>> SERIES) {
        SERIES.forEach(item -> addSeries(item));
    }
    public void removeSeries(final ChartItemSeries<ChartItem> SERIES) {
        if (series.contains(SERIES)) {
            series.remove(SERIES);
            redraw();
        }
    }
    public void removeSeries(final ChartItemSeries<ChartItem>... SERIES) {
        removeSeries(Arrays.asList(SERIES));
    }
    public void removeSeries(final List<ChartItemSeries<ChartItem>> SERIES) {
        SERIES.forEach(item -> removeSeries(item));
    }

    public Order getOrder() { return null == order ? _order : order.get(); }
    public void setOrder(final Order ORDER) {
        if (null == order) {
            _order = ORDER;
            redraw();
        } else {
            order.set(ORDER);
        }
    }
    public ObjectProperty<Order> orderProperty() {
        if (null == order) {
            order = new ObjectPropertyBase<Order>(_order) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return NestedBarChart.this; }
                @Override public String getName() { return "order"; }
            };
            _order = null;
        }
        return order;
    }

    public Paint getChartBackground() { return null == chartBackground ? _chartBackground : chartBackground.get(); }
    public void setChartBackground(final Paint PAINT) {
        if (null == chartBackground) {
            _chartBackground = PAINT;
            redraw();
        } else {
            chartBackground.set(PAINT);
        }
    }
    public ObjectProperty<Paint> chartBackgroundProperty() {
        if (null == chartBackground) {
            chartBackground = new ObjectPropertyBase<Paint>(_chartBackground) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return NestedBarChart.this; }
                @Override public String getName() { return "chartBackground"; }
            };
            _chartBackground = null;
        }
        return chartBackground;
    }

    public double getSpacer() { return spacer; }
    public void setSpacer(final double SPACER) {
        this.spacer = SPACER;
        redraw();
    }
    public void setDefaultSpacer() {
        this.spacer = -1;
        redraw();
    }

    public boolean isSeriesTitleVisible() { return null == seriesTitleVisible ? _seriesTitleVisible : seriesTitleVisible.get(); }
    public void setSeriesTitleVisible(final boolean VISIBLE) {
        if (null == seriesTitleVisible) {
            _seriesTitleVisible = VISIBLE;
            redraw();
        } else {
            seriesTitleVisible.set(VISIBLE);
        }
    }
    public BooleanProperty seriesTitleVisibleProperty() {
        if (null == seriesTitleVisible) {
            seriesTitleVisible = new BooleanPropertyBase(_seriesTitleVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return NestedBarChart.this; }
                @Override public String getName() { return "seriesTitleVisible"; }
            };
        }
        return seriesTitleVisible;
    }

    public Color getSeriesTitleColor() { return null == seriesTitleColor ? _seriesTitleColor : seriesTitleColor.get(); }
    public void setSeriesTitleColor(final Color COLOR) {
        if (null == seriesTitleColor) {
            _seriesTitleColor = COLOR;
            redraw();
        } else {
            seriesTitleColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> seriesTitleColor() {
        if (null == seriesTitleColor) {
            seriesTitleColor = new ObjectPropertyBase<>(_seriesTitleColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return NestedBarChart.this; }
                @Override public String getName() { return "seriesTitleColor"; }
            };
        }
        return seriesTitleColor;
    }

    public void checkForClick(final MouseEvent EVT) {
        final double X = EVT.getX();
        final double Y = EVT.getY();

        popup.setX(EVT.getScreenX());
        popup.setY(EVT.getScreenY() - popup.getHeight());

        long noOfBars        = series.size();
        double defaultSpacer = width * 0.05;
        double usedSpacer    = spacer == -1 ? defaultSpacer : spacer;
        double mainBarWidth  = (width - (usedSpacer * (noOfBars - 1))) / noOfBars;

        // Find series with sum of values
        double maxSum = -Double.MAX_VALUE;
        for (int i = 0 ; i < noOfBars  ;i++) {
            maxSum = Math.max(maxSum, series.get(i).getItems().stream().mapToDouble(ChartItem::getValue).sum());
        }
        double                     stepY          = height / maxSum;
        ChartItemSeries<ChartItem> selectedSeries = null;
        for (int i = 0 ; i < noOfBars ; i++) {
            ChartItemSeries<ChartItem> s             = series.get(i);
            int                        noOfItems     = s.getNoOfItems();
            double                     sumOfItems    = s.getItems().stream().mapToDouble(ChartItem::getValue).sum();
            double                     innerBarWidth = mainBarWidth / noOfItems;
            double                     mainBarHeight = sumOfItems * stepY;
            double                     minX          = i * mainBarWidth + i * usedSpacer;
            if (Helper.isInRectangle(X, Y, minX, height - mainBarHeight, minX + mainBarWidth, height)) {
                selectedSeries = s;
            }
            for (ChartItem item : s.getItems()) {
                double innerBarHeight = item.getValue() * stepY;
                if (Helper.isInRectangle(X, Y, minX, height - innerBarHeight, minX + innerBarWidth, height)) {
                    fireSelectionEvent(new SelectionEvent(selectedSeries, item));
                    return;
                }
                minX += innerBarWidth;
            }
        }
        if (null != selectedSeries) { fireSelectionEvent(new SelectionEvent(selectedSeries)); }
    }

    private void sortItems(final List<ChartItem> ITEMS, final Order ORDER) {
        if (Order.ASCENDING == ORDER) {
            Collections.sort(ITEMS, Comparator.comparingDouble(ChartItem::getValue));
        } else {
            Collections.sort(ITEMS, Comparator.comparingDouble(ChartItem::getValue).reversed());
        }
    }


    // ******************** Event Handling ************************************
    public void setOnSelectionEvent(final SelectionEventListener LISTENER) { addSelectionEventListener(LISTENER); }
    public void addSelectionEventListener(final SelectionEventListener LISTENER) { if (!listeners.contains(LISTENER)) listeners.add(LISTENER); }
    public void removeSelectionEventListener(final SelectionEventListener LISTENER) { if (listeners.contains(LISTENER)) listeners.remove(LISTENER); }
    public void removeAllSelectionEventListeners() { listeners.clear(); }

    public void fireSelectionEvent(final SelectionEvent EVENT) {
        for (SelectionEventListener listener : listeners) { listener.onSelectionEvent(EVENT); }
    }


    // ******************** Drawing *******************************************
    private void redraw() {
        drawChart();
    }

    private void drawChart() {
        ctx.clearRect(0, 0, width, height);
        ctx.setFill(getChartBackground());
        ctx.fillRect(0, 0, width, height);

        long   noOfBars      = series.size();
        double defaultSpacer = width * 0.025;
        double usedSpacer    = spacer == -1 ? defaultSpacer : spacer;
        double mainBarWidth  = (width - (usedSpacer * (noOfBars - 1))) / noOfBars;

        boolean seriesTitleVisible = isSeriesTitleVisible();

        // Find series with sum of values
        double maxSum = -Double.MAX_VALUE;
        for (int i = 0 ; i < noOfBars  ;i++) {
            maxSum = Math.max(maxSum, series.get(i).getItems().stream().mapToDouble(ChartItem::getValue).sum());
        }
        double stepY = height / maxSum;


        double fontSize = mainBarWidth * 0.125;
        Font   font     = Fonts.opensansRegular(fontSize);
        for (int i = 0 ; i < noOfBars ; i++) {
            ChartItemSeries<ChartItem> s             = series.get(i);
            int                        noOfItems     = s.getNoOfItems();
            double                     sumOfItems    = s.getSumOfAllItems();
            double                     innerBarWidth = mainBarWidth / noOfItems;
            double                     mainBarHeight = sumOfItems * stepY;
            double                     minX          = i * mainBarWidth + i * usedSpacer;
            // Draw main bar
            ctx.setFill(s.getFill());
            ctx.fillRect(minX, height - mainBarHeight, mainBarWidth, mainBarHeight);

            // Sort items in bar
            sortItems(s.getItems(), getOrder());

            double minMainBarX = minX;

            // Draw sub bars within main bar
            for (ChartItem item : s.getItems()) {
                double innerBarHeight = item.getValue() * stepY;
                ctx.setFill(s.getItems().size() == 1 ? s.getFill() : item.getFill());
                ctx.fillRect(minX, height - innerBarHeight, innerBarWidth, innerBarHeight);
                minX += innerBarWidth;
            }

            // Draw series title if enabled
            if (seriesTitleVisible) {
                ctx.save();
                ctx.setTextBaseline(VPos.CENTER);
                ctx.setTextAlign(TextAlignment.CENTER);
                ctx.setFill(null == getSeriesTitleColor() ? s.getTextFill() : getSeriesTitleColor());
                ctx.setFill(getSeriesTitleColor());
                ctx.setFont(font);
                if (mainBarHeight <= 2 * fontSize) {
                    ctx.fillText(s.getName(), minMainBarX + mainBarWidth * 0.5, height - mainBarHeight - fontSize - mainBarWidth * 0.01, mainBarWidth);
                } else {
                    ctx.fillText(s.getName(), minMainBarX + mainBarWidth * 0.5, height - mainBarHeight + fontSize + mainBarWidth * 0.01, mainBarWidth);
                }
                ctx.restore();
            }
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
            canvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            redraw();
        }
    }
}