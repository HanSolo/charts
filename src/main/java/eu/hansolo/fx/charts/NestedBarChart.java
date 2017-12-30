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
import eu.hansolo.fx.charts.series.Series;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.InfoPopup;
import eu.hansolo.fx.charts.tools.Order;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

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
public class NestedBarChart extends Region {
    private static final double                                       PREFERRED_WIDTH  = 250;
    private static final double                                       PREFERRED_HEIGHT = 150;
    private static final double                                       MINIMUM_WIDTH    = 50;
    private static final double                                       MINIMUM_HEIGHT   = 50;
    private static final double                                       MAXIMUM_WIDTH    = 1024;
    private static final double                                       MAXIMUM_HEIGHT   = 1024;
    private              double                                       size;
    private              double                                       width;
    private              double                                       height;
    private              Canvas                                       canvas;
    private              GraphicsContext                              ctx;
    private              Pane                                         pane;
    private              ObservableList<Series<ChartItem>>            series;
    private              Order                                        _order;
    private              ObjectProperty<Order>                        order;
    private              EventHandler<MouseEvent>                     clickHandler;
    private              CopyOnWriteArrayList<SelectionEventListener> listeners;
    private              InfoPopup                                    popup;


    // ******************** Constructors **************************************
    public NestedBarChart() {
        this(new ArrayList<>());
    }
    public NestedBarChart(final Series<ChartItem>... SERIES) {
        this(Arrays.asList(SERIES));
    }
    public NestedBarChart(final List<Series<ChartItem>> SERIES) {
        width        = PREFERRED_WIDTH;
        height       = PREFERRED_HEIGHT;
        size         = PREFERRED_HEIGHT;
        series       = FXCollections.observableArrayList(SERIES);
        _order       = Order.DESCENDING;
        clickHandler = e -> checkForClick(e);
        listeners    = new CopyOnWriteArrayList<>();
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

    public List<Series<ChartItem>> getSeries() { return series; }
    public void setSeries(final Series<ChartItem>... SERIES) {
        setSeries(Arrays.asList(SERIES));
    }
    public void setSeries(final List<Series<ChartItem>> SERIES) {
        series.clear();
        SERIES.forEach(item -> series.add(item));
        redraw();
    }
    public void addSeries(final Series<ChartItem> SERIES) {
        if (!series.contains(SERIES)) {
            series.add(SERIES);
            redraw();
        }
    }
    public void addSeries(final Series<ChartItem>... SERIES) {
        addSeries(Arrays.asList(SERIES));
    }
    public void addSeries(final List<Series<ChartItem>> SERIES) {
        SERIES.forEach(item -> addSeries(item));
    }
    public void removeSeries(final Series<ChartItem> SERIES) {
        if (series.contains(SERIES)) {
            series.remove(SERIES);
            redraw();
        }
    }
    public void removeSeries(final Series<ChartItem>... SERIES) {
        removeSeries(Arrays.asList(SERIES));
    }
    public void removeSeries(final List<Series<ChartItem>> SERIES) {
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

    public void checkForClick(final MouseEvent EVT) {
        final double X = EVT.getX();
        final double Y = EVT.getY();

        popup.setX(EVT.getScreenX());
        popup.setY(EVT.getScreenY() - popup.getHeight());

        long noOfBars       = series.size();
        double spacer       = width * 0.05;
        double mainBarWidth = (width - (spacer * (noOfBars - 1))) / noOfBars;

        // Find series with sum of values
        double maxSum = -Double.MAX_VALUE;
        for (int i = 0 ; i < noOfBars  ;i++) {
            maxSum = Math.max(maxSum, series.get(i).getItems().stream().mapToDouble(ChartItem::getValue).sum());
        }
        double            stepY          = height / maxSum;
        Series<ChartItem> selectedSeries = null;
        for (int i = 0 ; i < noOfBars ; i++) {
            Series<ChartItem> s             = series.get(i);
            int               noOfItems     = s.getNoOfItems();
            double            sumOfItems    = s.getItems().stream().mapToDouble(ChartItem::getValue).sum();
            double            innerBarWidth = mainBarWidth / noOfItems;
            double            mainBarHeight = sumOfItems * stepY;
            double            minX          = i * mainBarWidth + i * spacer;
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
        long noOfBars       = series.size();
        double spacer       = width * 0.05;
        double mainBarWidth = (width - (spacer * (noOfBars - 1))) / noOfBars;

        // Find series with sum of values
        double maxSum = -Double.MAX_VALUE;
        for (int i = 0 ; i < noOfBars  ;i++) {
            maxSum = Math.max(maxSum, series.get(i).getItems().stream().mapToDouble(ChartItem::getValue).sum());
        }
        double stepY = height / maxSum;

        for (int i = 0 ; i < noOfBars ; i++) {
            Series<ChartItem> s             = series.get(i);
            int               noOfItems     = s.getNoOfItems();
            double            sumOfItems    = s.getItems().stream().mapToDouble(ChartItem::getValue).sum();
            double            innerBarWidth = mainBarWidth / noOfItems;
            double            mainBarHeight = sumOfItems * stepY;
            double            minX          = i * mainBarWidth + i * spacer;
            // Draw main bar
            ctx.setFill(s.getFill());
            ctx.fillRect(minX, height - mainBarHeight, mainBarWidth, mainBarHeight);

            // Sort items in bar
            sortItems(s.getItems(), getOrder());

            // Draw sub bars within main bar
            for (ChartItem item : s.getItems()) {
                double innerBarHeight = item.getValue() * stepY;
                ctx.setFill(item.getFillColor());
                ctx.fillRect(minX, height - innerBarHeight, innerBarWidth, innerBarHeight);
                minX += innerBarWidth;
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