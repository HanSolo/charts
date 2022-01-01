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
import eu.hansolo.fx.charts.event.ChartEvt;
import eu.hansolo.fx.charts.event.SelectionEvt;
import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolboxfx.font.Fonts;
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
import javafx.collections.FXCollections;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


@DefaultProperty("children")
public class ConcentricRingChart extends Region {
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
    private              ObservableList<ChartItem>                    items;
    private              Color                                        _barBackgroundFill;
    private              ObjectProperty<Color>                        barBackgroundFill;
    private              boolean                                      _sorted;
    private              BooleanProperty                              sorted;
    private              Order                                        _order;
    private              ObjectProperty<Order>                        order;
    private              NumberFormat                                 _numberFormat;
    private              ObjectProperty<NumberFormat>                 numberFormat;
    private              Color                                        _itemLabelFill;
    private              ObjectProperty<Color>                        itemLabelFill;
    private              boolean                                      _shortenNumbers;
    private              BooleanProperty                              shortenNumbers;
    private              boolean                                      _valueVisible;
    private              BooleanProperty                              valueVisible;
    private              ListChangeListener<ChartItem>                chartItemListener;
    private              EvtObserver<ChartEvt>                        itemObserver;
    private              EventHandler<MouseEvent>                     mouseHandler;
    private              Map<EvtType, List<EvtObserver<ChartEvt>>>    observers;
    private              InfoPopup                                    popup;


    // ******************** Constructors **************************************
    public ConcentricRingChart() {
        this(new ArrayList<>());
    }
    public ConcentricRingChart(final ChartItem... ITEMS) {
        this(Arrays.asList(ITEMS));
    }
    public ConcentricRingChart(final List<ChartItem> ITEMS) {
        items = FXCollections.observableArrayList();
        items.setAll(ITEMS);
        _barBackgroundFill = Color.rgb(230, 230, 230);
        _sorted            = false;
        _order             = Order.ASCENDING;
        _numberFormat      = NumberFormat.NUMBER;
        _itemLabelFill     = Color.BLACK;
        _shortenNumbers    = false;
        _valueVisible      = true;
        observers          = new ConcurrentHashMap<>();
        popup              = new InfoPopup();
        itemObserver       = e -> {
            final EvtType<? extends Evt> type = e.getEvtType();
            if (type.equals(ChartEvt.ITEM_UPDATE) || type.equals(ChartEvt.FINISHED)) {
                drawChart();
            }
        };
        chartItemListener  = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedItem -> addedItem.addChartEvtObserver(ChartEvt.ANY, itemObserver));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedItem -> removedItem.removeChartEvtObserver(ChartEvt.ANY, itemObserver));
                }
            }
            drawChart();
        };
        mouseHandler       = e -> handleMouseEvents(e);
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

        items.forEach(chartitem -> chartitem.addChartEvtObserver(ChartEvt.ANY, itemObserver));
        items.addListener(chartItemListener);

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

    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public List<ChartItem> getItems() { return items; }
    public void setItems(final Series<ChartItem> SERIES) {
        List<ChartItem> seriesItems       = SERIES.getItems();
        boolean         animated          = SERIES.isAnimated();
        long            animationDuration = SERIES.getAnimationDuration();
        Paint           fill              = SERIES.getFill();
        boolean         isColor           = fill instanceof Color;
        Color           barColor          = isColor ? (Color) fill : null;
        Color           textFill          = SERIES.getTextFill();
        seriesItems.forEach(item -> {
            if (animated) { item.setAnimated(animated); }
            item.setAnimationDuration(animationDuration);
            if (isColor) { item.setFill(barColor); }
            item.setTextFill(textFill);
        });

        setItems(seriesItems);
    }
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
                @Override public Object getBean() { return ConcentricRingChart.this; }
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
                @Override public Object getBean() { return ConcentricRingChart.this; }
                @Override public String getName() { return "numberFormat"; }
            };
            _numberFormat = null;
        }
        return numberFormat;
    }

    public Color getItemLabelFill() { return null == itemLabelFill ? _itemLabelFill : itemLabelFill.get(); }
    public void setItemLabelFill(final Color FILL) {
        if (null == itemLabelFill) {
            _itemLabelFill = FILL;
            redraw();
        } else {
            itemLabelFill.set(FILL);
        }
    }
    public ObjectProperty<Color> itemLabelFillProperty() {
        if (null == itemLabelFill) {
            itemLabelFill = new ObjectPropertyBase<Color>(_itemLabelFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ConcentricRingChart.this; }
                @Override public String getName() { return "itemLabelFill"; }
            };
            _itemLabelFill = null;
        }
        return itemLabelFill;
    }

    public boolean getShortenNumbers() { return null == shortenNumbers ? _shortenNumbers : shortenNumbers.get(); }
    public void setShortenNumbers(final boolean SHORTEN) {
        if (null == shortenNumbers) {
            _shortenNumbers = SHORTEN;
            redraw();
        } else {
            shortenNumbers.set(SHORTEN);
        }
    }
    public BooleanProperty shortenNumbersProperty() {
        if (null == shortenNumbers) {
            shortenNumbers = new BooleanPropertyBase(_shortenNumbers) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ConcentricRingChart.this; }
                @Override public String getName() { return "shortenNumbers"; }
            };
        }
        return shortenNumbers;
    }

    public boolean getValueVisible() { return null == valueVisible ? _valueVisible : valueVisible.get(); }
    public void setValueVisible(final boolean VISIBLE) {
        if (null == valueVisible) {
            _valueVisible = VISIBLE;
            redraw();
        } else {
            valueVisible.set(VISIBLE);
        }
    }
    public BooleanProperty valueVisibleProperty() {
        if (null == valueVisible) {
            valueVisible = new BooleanPropertyBase(_valueVisible) {
                @Override public Object getBean() { return ConcentricRingChart.this; }
                @Override public String getName() { return "valueVisible"; }
            };
        }
        return valueVisible;
    }

    private void handleMouseEvents(final MouseEvent EVT) {
        double x           = EVT.getX();
        double y           = EVT.getY();
        double centerX     = size * 0.5;
        double centerY     = centerX;
        double radius      = size * 0.5;
        double innerSpacer = radius * 0.18;
        double barSpacer   = (radius - innerSpacer) * 0.005;
        int    noOfItems   = items.size();
        double barWidth    = (radius - innerSpacer - (noOfItems - 1) * barSpacer) / noOfItems;
        double startAngle  = 0;
        double maxValue    = noOfItems == 0 ? 0 : items.stream().max(Comparator.comparingDouble(ChartItem::getValue)).get().getValue();
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
        for (int i = 0 ; i < noOfItems ; i++) {
            ChartItem item    = sortedItems.get(i);
            double    value = Helper.clamp(0, Double.MAX_VALUE, item.getValue());
            double    barWH = size - barWidth - (2 * i * barWidth - barSpacer) - (2 * i * barSpacer);
            double    angle = value / maxValue * 270.0;

            boolean hit = Helper.isInRingSegment(x, y, centerX, centerY, (barWH + barWidth) * 0.5, (barWH - barWidth) * 0.5, startAngle, angle);
            if (hit) {
                popup.setX(EVT.getScreenX());
                popup.setY(EVT.getScreenY() - popup.getHeight());
                fireChartEvt(new SelectionEvt(item));
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
    private void drawChart() {
        double          centerX           = size * 0.5;
        double          centerY           = centerX;
        double          radius            = size * 0.5;
        double          innerSpacer       = radius * 0.18;
        double          barSpacer         = (radius - innerSpacer) * 0.005;
        int             noOfItems         = items.size();
        double          barWidth          = (radius - innerSpacer - (noOfItems - 1) * barSpacer) / noOfItems;
        double          maxValue          = noOfItems == 0 ? 0 : items.stream().max(Comparator.comparingDouble(ChartItem::getValue)).get().getValue();
        double          nameX             = radius * 0.975;
        double          nameWidth         = radius * 0.95;
        double          fontSize          = barWidth * 0.45;
        NumberFormat    numberFormat      = getNumberFormat();
        Color           barBackgroundFill = getBarBackgroundFill();
        Color           itemLabelFill     = getItemLabelFill();
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

        ctx.clearRect(0, 0, size, size);
        ctx.setLineCap(StrokeLineCap.BUTT);
        ctx.setTextAlign(TextAlignment.RIGHT);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setFont(Fonts.latoRegular(fontSize));

        // Draw bars
        for (int i = 0 ; i < noOfItems ; i++) {
            ChartItem item  = sortedItems.get(i);
            double    value = Helper.clamp(0, Double.MAX_VALUE, item.getValue());
            double    barXY = (barWidth * 0.5) + (i * barWidth) + (i * barSpacer) + 1;
            double    barWH = size - barWidth - (2 * i * barWidth - barSpacer) - (2 * i * barSpacer) - 2;
            double    angle = value / maxValue * 270.0;

            // BarBackground
            ctx.setLineWidth(barWidth);
            ctx.setStroke(barBackgroundFill);
            ctx.strokeArc(barXY, barXY, barWH, barWH, 90, -270, ArcType.OPEN);

            // Bar
            ctx.setStroke(item.getFill());
            ctx.strokeArc(barXY, barXY, barWH, barWH, 90, -angle, ArcType.OPEN);

            // Name
            ctx.setFill(itemLabelFill);
            ctx.setTextAlign(TextAlignment.RIGHT);
            ctx.fillText(item.getName(), nameX, barXY, nameWidth);

            // Value
            if (getValueVisible()) {
                ctx.setTextAlign(TextAlignment.LEFT);
                ctx.setFill(item.getTextFill());
                if (getShortenNumbers()) {
                    drawTextAlongArc(ctx, Helper.shortenNumber((long) value), fontSize, centerX, centerY, barWH * 0.5, angle);
                } else {
                    if (NumberFormat.PERCENTAGE == numberFormat || NumberFormat.PERCENTAGE_1_DECIMAL == numberFormat) {
                        drawTextAlongArc(ctx, String.format(Locale.US, numberFormat.formatString(), value / maxValue * 100), fontSize, centerX, centerY, barWH * 0.5, angle);
                    } else {
                        drawTextAlongArc(ctx, String.format(Locale.US, numberFormat.formatString(), value), fontSize, centerX, centerY, barWH * 0.5, angle);
                    }
                }
            }
        }
    }

    private void drawTextAlongArc(final GraphicsContext ctx, final String text, final double fontSize, final double centerX, final double centerY, final double radius, final double angle) {
        int    length     = text.length();
        double charSpacer = Helper.clamp(2, 0.75 * fontSize, (7 / radius) * size * 0.13);
        double textAngle  = (charSpacer * (length + 0.5));
        if (angle > textAngle) {
            ctx.save();
            ctx.translate(centerX, centerY);
            ctx.rotate(angle - (charSpacer * (length + 0.5)));
            for (int i = 0; i < length; i++) {
                ctx.save();
                ctx.translate(0, -1 * radius);
                char c = text.charAt(i);
                ctx.fillText(Character.toString(c), 0, 0);
                ctx.restore();
                ctx.rotate(charSpacer);
            }
            ctx.restore();
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
