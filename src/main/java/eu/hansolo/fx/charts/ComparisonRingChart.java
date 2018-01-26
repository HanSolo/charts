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
import eu.hansolo.fx.charts.event.SelectionEvent;
import eu.hansolo.fx.charts.event.SelectionEventListener;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.series.Series;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.InfoPopup;
import eu.hansolo.fx.charts.tools.NumberFormat;
import eu.hansolo.fx.charts.tools.Order;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
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
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.TextAlignment;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


@DefaultProperty("children")
public class ComparisonRingChart extends Region {
    private static final double                                       PREFERRED_WIDTH  = 250;
    private static final double                                       PREFERRED_HEIGHT = 250;
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
    private              ChartItemSeries<ChartItem>                   series1;
    private              ChartItemSeries<ChartItem>                   series2;
    private              Color                                        _barBackgroundFill;
    private              ObjectProperty<Color>                        barBackgroundFill;
    private              boolean                                      _sorted;
    private              BooleanProperty                              sorted;
    private              Order                                        _order;
    private              ObjectProperty<Order>                        order;
    private              NumberFormat                                 _numberFormat;
    private              ObjectProperty<NumberFormat>                 numberFormat;
    private              ListChangeListener<ChartItem>                chartItemListener;
    private              ItemEventListener                            itemEventListener;
    private              EventHandler<MouseEvent>                     mouseHandler;
    private              CopyOnWriteArrayList<SelectionEventListener> listeners;
    private              InfoPopup                                    popup;


    // ******************** Constructors **************************************
    public ComparisonRingChart(final ChartItemSeries SERIES_1, final ChartItemSeries SERIES_2) {
        series1             = SERIES_1;
        series2             = SERIES_2;
        _barBackgroundFill = Color.rgb(230, 230, 230);
        _sorted             = true;
        _order              = Order.DESCENDING;
        _numberFormat       = NumberFormat.NUMBER;
        listeners           = new CopyOnWriteArrayList<>();
        popup               = new InfoPopup();
        itemEventListener   = e -> {
            final EventType TYPE = e.getEventType();
            switch(TYPE) {
                case UPDATE  : drawChart(); break;
                case FINISHED: drawChart(); break;
            }
        };
        chartItemListener   = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedItem -> addedItem.addItemEventListener(itemEventListener));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedItem -> removedItem.removeItemEventListener(itemEventListener));
                }
            }
            drawChart();
        };
        mouseHandler        = e -> handleMouseEvents(e);
        prepareSeries(series1);
        prepareSeries(series2);
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

        getStyleClass().add("comparison-ring-chart");

        canvas = new Canvas(size * 0.9, 0.9);
        ctx    = canvas.getGraphicsContext2D();

        pane = new Pane(canvas);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());

        series1.getItems().forEach(item -> item.addItemEventListener(itemEventListener));
        series2.getItems().forEach(item -> item.addItemEventListener(itemEventListener));

        series1.getItems().addListener(chartItemListener);
        series2.getItems().addListener(chartItemListener);

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
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

    public Color getBarBackgroundFill() { return null == barBackgroundFill ? _barBackgroundFill : barBackgroundFill.get(); }
    public void setBarBackgroundFill(final Color FILL) {
        if (null == barBackgroundFill) {
            _barBackgroundFill = FILL;
            redraw();
        } else {
            barBackgroundFill.set(FILL);
        }
    }
    public ObjectProperty<Color> barBackgroundFillProperty() {
        if (null == barBackgroundFill) {
            barBackgroundFill = new ObjectPropertyBase<Color>(_barBackgroundFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ComparisonRingChart.this; }
                @Override public String getName() { return "barBackgroundFill"; }
            };
            _barBackgroundFill = null;
        }
        return barBackgroundFill;
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
                @Override public Object getBean() { return ComparisonRingChart.this; }
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
                @Override public Object getBean() { return ComparisonRingChart.this; }
                @Override public String getName() { return "order"; }
            };
            _order = null;
        }
        return order;
    }

    public NumberFormat getNumberFormat() { return null == numberFormat ? _numberFormat : numberFormat.get(); }
    public void setNumberFormat(final NumberFormat FORMAT) {
        if (null == numberFormat) {
            _numberFormat = FORMAT;
            updatePopup();
            redraw();
        } else {
            numberFormat.set(FORMAT);
        }
    }
    public ObjectProperty<NumberFormat> numberFormatProperty() {
        if (null == numberFormat) {
            numberFormat = new ObjectPropertyBase<NumberFormat>(_numberFormat) {
                @Override protected void invalidated() {
                    updatePopup();
                    redraw();
                }
                @Override public Object getBean() { return ComparisonRingChart.this; }
                @Override public String getName() { return "numberFormat"; }
            };
            _numberFormat = null;
        }
        return numberFormat;
    }

    private void handleMouseEvents(final MouseEvent EVT) {
        double x           = EVT.getX();
        double y           = EVT.getY();
        double centerX     = size * 0.5;
        double centerY     = centerX;
        double radius      = size * 0.5;
        double innerSpacer = radius * 0.18;
        double barSpacer   = (radius - innerSpacer) * 0.005;
        int    noOfItems1  = series1.getItems().size();
        int    noOfItems2  = series2.getItems().size();
        double barWidth1   = (radius - innerSpacer - (noOfItems1 - 1) * barSpacer) / noOfItems1;
        double barWidth2   = (radius - innerSpacer - (noOfItems2 - 1) * barSpacer) / noOfItems2;
        double maxValue1   = noOfItems1 == 0 ? 0 : series1.getItems().stream().max(Comparator.comparingDouble(ChartItem::getValue)).get().getValue();
        double maxValue2   = noOfItems1 == 0 ? 0 : series2.getItems().stream().max(Comparator.comparingDouble(ChartItem::getValue)).get().getValue();

        List<ChartItem> sortedItems1;
        List<ChartItem> sortedItems2;
        if (isSorted()) {
            if (Order.ASCENDING == getOrder()) {
                sortedItems1 = series1.getItems().stream().sorted(Comparator.comparingDouble(ChartItem::getValue)).collect(Collectors.toList());
                sortedItems2 = series2.getItems().stream().sorted(Comparator.comparingDouble(ChartItem::getValue)).collect(Collectors.toList());
            } else {
                sortedItems1 = series1.getItems().stream().sorted(Comparator.comparingDouble(ChartItem::getValue).reversed()).collect(Collectors.toList());
                sortedItems2 = series2.getItems().stream().sorted(Comparator.comparingDouble(ChartItem::getValue).reversed()).collect(Collectors.toList());
            }
        } else {
            sortedItems1 = series1.getItems();
            sortedItems2 = series2.getItems();
        }
        // Check Series 1
        for (int i = 0 ; i < noOfItems1 ; i++) {
            ChartItem item  = sortedItems1.get(i);
            double    value = Helper.clamp(0, Double.MAX_VALUE, item.getValue());
            double    barWH = size - barWidth1 - (2 * i * barWidth1 - barSpacer) - (2 * i * barSpacer);
            double    angle = value / maxValue1 * 180.0;

            boolean hitLeft  = Helper.isInRingSegment(x, y, centerX, centerY, (barWH + barWidth1) * 0.5, (barWH - barWidth1) * 0.5, 270, angle);
            boolean hitRight = Helper.isInRingSegment(x, y, centerX, centerY, (barWH + barWidth1) * 0.5, (barWH - barWidth1) * 0.5, 0, angle);
            if (hitLeft || hitRight) {
                popup.setX(EVT.getScreenX());
                popup.setY(EVT.getScreenY() - popup.getHeight());
                fireSelectionEvent(new SelectionEvent(series1, item));
                break;
            }
        }

        // Check Series 2
        for (int i = 0 ; i < noOfItems2 ; i++) {
            ChartItem item  = sortedItems2.get(i);
            double    value = Helper.clamp(0, Double.MAX_VALUE, item.getValue());
            double    barWH = size - barWidth2 - (2 * i * barWidth2 - barSpacer) - (2 * i * barSpacer);
            double    angle = value / maxValue2 * 180.0;

            boolean hit = Helper.isInRingSegment(x, y, centerX, centerY, (barWH + barWidth2) * 0.5, (barWH - barWidth2) * 0.5, 90, angle);
            if (hit) {
                popup.setX(EVT.getScreenX());
                popup.setY(EVT.getScreenY() - popup.getHeight());
                fireSelectionEvent(new SelectionEvent(series2, item));
                break;
            }
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
    public void setOnSelectionEvent(final SelectionEventListener LISTENER) { addSelectionEventListener(LISTENER); }
    public void addSelectionEventListener(final SelectionEventListener LISTENER) { if (!listeners.contains(LISTENER)) listeners.add(LISTENER); }
    public void removeSelectionEventListener(final SelectionEventListener LISTENER) { if (listeners.contains(LISTENER)) listeners.remove(LISTENER); }
    public void removeAllSelectionEventListeners() { listeners.clear(); }

    public void fireSelectionEvent(final SelectionEvent EVENT) {
        for (SelectionEventListener listener : listeners) { listener.onSelectionEvent(EVENT); }
    }


    // ******************** Drawing *******************************************
    private void prepareSeries(final Series<ChartItem> SERIES) {
        boolean         animated          = SERIES.isAnimated();
        long            animationDuration = SERIES.getAnimationDuration();
        Paint           fill              = SERIES.getFill();
        boolean         isColor           = fill instanceof Color;
        Color           barColor          = isColor ? (Color) fill : null;
        Color           textFill          = SERIES.getTextFill();
        SERIES.getItems().forEach(item -> {
            if (animated) { item.setAnimated(animated); }
            item.setAnimationDuration(animationDuration);
            if (isColor) { item.setFill(barColor); }
            item.setTextFill(textFill);
        });
    }

    private void drawChart() {
        double          centerX           = size * 0.5;
        double          centerY           = centerX;
        double          radius            = size * 0.5;
        double          innerSpacer       = radius * 0.18;
        double          barSpacer         = (radius - innerSpacer) * 0.005;
        int             noOfItems1        = series1.getItems().size();
        int             noOfItems2        = series2.getItems().size();
        double          barWidth1         = (radius - innerSpacer - (noOfItems1 - 1) * barSpacer) / noOfItems1;
        double          barWidth2         = (radius - innerSpacer - (noOfItems2 - 1) * barSpacer) / noOfItems2;
        double          maxValue1         = noOfItems1 == 0 ? 0 : series1.getItems().stream().max(Comparator.comparingDouble(ChartItem::getValue)).get().getValue();
        double          maxValue2         = noOfItems1 == 0 ? 0 : series2.getItems().stream().max(Comparator.comparingDouble(ChartItem::getValue)).get().getValue();
        Color           barBackgroundFill = getBarBackgroundFill();
        NumberFormat    numberFormat      = getNumberFormat();
        List<ChartItem> sortedItems1;
        List<ChartItem> sortedItems2;
        if (isSorted()) {
            if (Order.ASCENDING == getOrder()) {
                sortedItems1 = series1.getItems().stream().sorted(Comparator.comparingDouble(ChartItem::getValue)).collect(Collectors.toList());
                sortedItems2 = series2.getItems().stream().sorted(Comparator.comparingDouble(ChartItem::getValue)).collect(Collectors.toList());
            } else {
                sortedItems1 = series1.getItems().stream().sorted(Comparator.comparingDouble(ChartItem::getValue).reversed()).collect(Collectors.toList());
                sortedItems2 = series2.getItems().stream().sorted(Comparator.comparingDouble(ChartItem::getValue).reversed()).collect(Collectors.toList());
            }
        } else {
            sortedItems1 = series1.getItems();
            sortedItems2 = series2.getItems();
        }

        ctx.clearRect(0, 0, size, size);
        ctx.setLineCap(StrokeLineCap.BUTT);
        ctx.setTextAlign(TextAlignment.RIGHT);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setFont(Fonts.latoRegular(barWidth1 * 0.5));

        // Draw bars 1
        for (int i = 0 ; i < noOfItems1 ; i++) {
            ChartItem item  = sortedItems1.get(i);
            double    value = Helper.clamp(0, Double.MAX_VALUE, item.getValue());
            double    barXY = (barWidth1 * 0.5) + (i * barWidth1) + (i * barSpacer) + 1;
            double    barWH = size - barWidth1 - (2 * i * barWidth1 - barSpacer) - (2 * i * barSpacer) - 2;
            double    angle = value / maxValue1 * 180.0;

            // BarBackground 1
            ctx.setLineWidth(barWidth1);
            ctx.setStroke(barBackgroundFill);
            ctx.strokeArc(barXY, barXY, barWH, barWH, 180, -180, ArcType.OPEN);

            // Bar 1
            ctx.setStroke(item.getFill());
            ctx.strokeArc(barXY, barXY, barWH, barWH, 180, -angle, ArcType.OPEN);

            // Value 1
            ctx.setTextAlign(TextAlignment.LEFT);
            ctx.setFill(item.getTextFill());
            if (NumberFormat.PERCENTAGE == numberFormat || NumberFormat.PERCENTAGE_1_DECIMAL == numberFormat) {
                drawTextAlongArc(true, ctx, String.format(Locale.US, numberFormat.formatString(), value / maxValue1 * 100), centerX, centerY, barWH * 0.5, angle - 90);
            } else {
                drawTextAlongArc(true, ctx, String.format(Locale.US, numberFormat.formatString(), value), centerX, centerY, barWH * 0.5, angle - 90);
            }
        }

        // Draw bars 2
        for (int i = 0 ; i < noOfItems2 ; i++) {
            ChartItem item  = sortedItems2.get(i);
            double    value = Helper.clamp(0, Double.MAX_VALUE, item.getValue());
            double    barXY = (barWidth2 * 0.5) + (i * barWidth2) + (i * barSpacer) + 1;
            double    barWH = size - barWidth2 - (2 * i * barWidth2 - barSpacer) - (2 * i * barSpacer) - 2;
            double    angle = value / maxValue2 * 180.0;

            // BarBackground 2
            ctx.setLineWidth(barWidth2);
            ctx.setStroke(barBackgroundFill);
            ctx.strokeArc(barXY, barXY, barWH, barWH, 0, -180, ArcType.OPEN);

            // Bar 2
            ctx.setStroke(item.getFill());
            ctx.strokeArc(barXY, barXY, barWH, barWH, 0, -angle, ArcType.OPEN);

            // Value 2
            ctx.setTextAlign(TextAlignment.LEFT);
            ctx.setFill(item.getTextFill());
            if (NumberFormat.PERCENTAGE == numberFormat || NumberFormat.PERCENTAGE_1_DECIMAL == numberFormat) {
                drawTextAlongArc(false, ctx, String.format(Locale.US, numberFormat.formatString(), value / maxValue2 * 100), centerX, centerY, barWH * 0.5, angle + 90);
            } else {
                drawTextAlongArc(false, ctx, String.format(Locale.US, numberFormat.formatString(), value), centerX, centerY, barWH * 0.5, angle + 90);
            }
        }
    }

    private void drawTextAlongArc(final boolean UPPER, final GraphicsContext CTX, final String TEXT, final double CENTER_X, final double CENTER_Y, final double RADIUS, final double ANGLE){
        int    length     = TEXT.length();
        double charSpacer = (7 / RADIUS) * size * 0.13;
        double textAngle  = (charSpacer * (length + 0.5));
        double offset     = UPPER ? 90 : -90;
        if (ANGLE + offset > textAngle) {
            CTX.save();
            CTX.translate(CENTER_X, CENTER_Y);
            CTX.rotate(ANGLE - (charSpacer * (length + 0.5)));
            for (int i = 0; i < length; i++) {
                CTX.save();
                CTX.translate(0, -1 * RADIUS);
                char c = TEXT.charAt(i);
                CTX.fillText(Character.toString(c), 0, 0);
                CTX.restore();
                CTX.rotate(charSpacer);
            }
            CTX.restore();
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
