/*
 * Copyright (c) 2018 by Gerrit Grunwald
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
import eu.hansolo.fx.charts.event.EventType;
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.Order;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


/**
 * User: hansolo
 * Date: 25.01.18
 * Time: 11:23
 */
@DefaultProperty("children")
public class ConcentricRingChart extends Region {
    private static final double                        PREFERRED_WIDTH  = 250;
    private static final double                        PREFERRED_HEIGHT = 250;
    private static final double                        MINIMUM_WIDTH    = 50;
    private static final double                        MINIMUM_HEIGHT   = 50;
    private static final double                        MAXIMUM_WIDTH    = 1024;
    private static final double                        MAXIMUM_HEIGHT   = 1024;
    private              double                        size;
    private              double                        width;
    private              double                        height;
    private              Canvas                        canvas;
    private              GraphicsContext               ctx;
    private              Pane                          pane;
    private              ObservableList<ChartItem>     items;
    private              Color                         _barBackgroundColor;
    private              ObjectProperty<Color>         barBackgroundColor;
    private              boolean                       _sorted;
    private              BooleanProperty               sorted;
    private              Order                         _order;
    private              ObjectProperty<Order>         order;
    private              ListChangeListener<ChartItem> chartItemListener;
    private              ItemEventListener             itemEventListener;


    // ******************** Constructors **************************************
    public ConcentricRingChart() {
        this(new ArrayList<ChartItem>());
    }
    public ConcentricRingChart(final ChartItem... ITEMS) {
        this(Arrays.asList(ITEMS));
    }
    public ConcentricRingChart(final List<ChartItem> ITEMS) {
        items = FXCollections.observableArrayList();
        items.setAll(ITEMS);
        _barBackgroundColor = Color.rgb(200, 200, 200);
        _sorted             = false;
        _order              = Order.ASCENDING;
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

        getStyleClass().add("concentric-ring-chart");

        canvas = new Canvas(size * 0.9, 0.9);
        ctx    = canvas.getGraphicsContext2D();

        pane = new Pane(canvas);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        itemEventListener = e -> {
            final EventType TYPE = e.getEventType();
            switch(TYPE) {
                case UPDATE  : drawChart(); break;
                case FINISHED: drawChart(); break;
            }
        };
        items.forEach(chartitem -> chartitem.addItemEventListener(itemEventListener));
        chartItemListener = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedItem -> addedItem.addItemEventListener(itemEventListener));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedItem -> removedItem.removeItemEventListener(itemEventListener));
                }
            }
            drawChart();
        };
        items.addListener(chartItemListener);
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

    public List<ChartItem> getItems() { return items; }
    public void setItems(final ChartItem... ITEMS) {
        setItems(Arrays.asList(ITEMS));
    }
    public void setItems(final List<ChartItem> ITEMS) { items.setAll(ITEMS); }
    public void addItem(final ChartItem ITEM) {
        if (!items.contains(ITEM)) {
            items.add(ITEM);
        }
    }
    public void addItems(final ChartItem... ITEMS) {
        addItems(Arrays.asList(ITEMS));
    }
    public void addItems(final List<ChartItem> ITEMS) {
        ITEMS.forEach(item -> addItem(item));
    }
    public void removeItem(final ChartItem ITEM) {
        if (items.contains(ITEM)) {
            items.remove(ITEM);
        }
    }
    public void removeItems(final ChartItem... ITEMS) {
        removeItems(Arrays.asList(ITEMS));
    }
    public void removeItems(final List<ChartItem> ITEMS) {
        ITEMS.forEach(item -> removeItem(item));
    }

    public Color getBarBackgroundColor() { return null == barBackgroundColor ? _barBackgroundColor : barBackgroundColor.get(); }
    public void setBarBackgroundColor(final Color COLOR) {
        if (null == barBackgroundColor) {
            _barBackgroundColor = COLOR;
            redraw();
        } else {
            barBackgroundColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> barBackgroundColorProperty() {
        if (null == barBackgroundColor) {
            barBackgroundColor = new ObjectPropertyBase<Color>(_barBackgroundColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ConcentricRingChart.this; }
                @Override public String getName() { return "barBackgroundColor"; }
            };
            _barBackgroundColor = null;
        }
        return barBackgroundColor;
    }

    public boolean isSorted() { return null == sorted ? _sorted : sorted.get(); }
    public void setSorted(final boolean SORTED) {
        if (null == sorted) {
            _sorted = SORTED;
            redraw();
        } else {
            sorted.set(SORTED);
        }
    }
    public BooleanProperty sortedProperty() {
        if (null == sorted) {
            sorted = new BooleanPropertyBase(_sorted) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ConcentricRingChart.this; }
                @Override public String getName() { return "sorted"; }
            };
        }
        return sorted;
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
                @Override public Object getBean() { return ConcentricRingChart.this; }
                @Override public String getName() { return "order"; }
            };
            _order = null;
        }
        return order;
    }

    private void drawChart() {
        double          canvasSize         = canvas.getWidth();
        double          radius             = canvasSize * 0.5;
        double          innerSpacer        = radius * 0.18;
        int             noOfItems          = items.size();
        double          barWidth           = (radius - innerSpacer) / noOfItems;
        List<ChartItem> sortedItems;
        if (isSorted()) {
            if (Order.ASCENDING == getOrder()) {
                sortedItems = items.stream().sorted(Comparator.comparingDouble(ChartItem::getValue)).collect(Collectors.toList());
            } else {
                sortedItems = items.stream().sorted(Comparator.comparingDouble(ChartItem::getValue).reversed()).collect(Collectors.toList());
            }
        } else {
            sortedItems = items;
        }
        //double          minValue           = noOfItems == 0 ? 0 : items.stream().min(Comparator.comparingDouble(ChartItem::getValue)).get().getValue();
        double          maxValue           = noOfItems == 0 ? 0 : items.stream().max(Comparator.comparingDouble(ChartItem::getValue)).get().getValue();

        double          nameX              = radius * 0.975;
        double          nameWidth          = radius * 0.95;
        double          valueY             = radius * 0.94;
        double          valueWidth         = barWidth * 0.9;
        Color           barBackgroundColor = getBarBackgroundColor();

        ctx.clearRect(0, 0, canvasSize, canvasSize);
        ctx.setLineCap(StrokeLineCap.BUTT);
        ctx.setTextAlign(TextAlignment.RIGHT);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setFont(Fonts.latoRegular(barWidth * 0.5));

        ctx.setStroke(barBackgroundColor);
        ctx.setLineWidth(1);
        ctx.strokeLine(radius, 0, radius, radius - 2 * barWidth * 0.875);
        ctx.strokeLine(0, radius, radius - 2 * barWidth * 0.875, radius);
        ctx.strokeArc(noOfItems * barWidth, noOfItems * barWidth, canvasSize - (2 * noOfItems * barWidth), canvasSize - (2 * noOfItems * barWidth), 90, -270, ArcType.OPEN);

        for (int i = 0 ; i < noOfItems ; i++) {
            ChartItem item  = sortedItems.get(i);
            double    value = Helper.clamp(0, Double.MAX_VALUE, item.getValue());
            double    bkgXY = i * barWidth;
            double    bkgWH = canvasSize - (2 * i * barWidth);
            double    barXY = barWidth * 0.5 + i * barWidth;
            double    barWH = canvasSize - barWidth - (2 * i * barWidth);
            double    angle = value / maxValue * 270.0;

            // Background
            ctx.setLineWidth(1);
            ctx.setStroke(barBackgroundColor);
            ctx.strokeArc(bkgXY, bkgXY, bkgWH, bkgWH, 90, -270, ArcType.OPEN);

            // DataBar
            ctx.setLineWidth(barWidth);
            ctx.setStroke(item.getFill());
            ctx.strokeArc(barXY, barXY, barWH, barWH, 90, -angle, ArcType.OPEN);

            // Name
            ctx.setTextAlign(TextAlignment.RIGHT);
            ctx.fillText(item.getName(), nameX, barXY, nameWidth);

            // Value
            ctx.setFill(item.getTextColor());
            ctx.setTextAlign(TextAlignment.CENTER);
            ctx.fillText(String.format(Locale.US, "%.0f", value), barXY, valueY, valueWidth);
        }
    }
    

    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            canvas.setWidth(size);
            canvas.setHeight(size);

            redraw();
        }
    }

    private void redraw() {
        drawChart();
    }
}
