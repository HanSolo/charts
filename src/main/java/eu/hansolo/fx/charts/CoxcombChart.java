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
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.event.SelectionEvent;
import eu.hansolo.fx.charts.event.SelectionEventListener;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.InfoPopup;
import eu.hansolo.fx.charts.tools.Order;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
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
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * User: hansolo
 * Date: 26.12.17
 * Time: 12:11
 */
@DefaultProperty("children")
public class CoxcombChart extends Region {
    private static final double                                       PREFERRED_WIDTH       = 250;
    private static final double                                       PREFERRED_HEIGHT      = 250;
    private static final double                                       MINIMUM_WIDTH         = 50;
    private static final double                                       MINIMUM_HEIGHT        = 50;
    private static final double                                       MAXIMUM_WIDTH         = 1024;
    private static final double                                       MAXIMUM_HEIGHT        = 1024;
    private static final String                                       DEFAULT_FORMAT_STRING = "%.0f%%";
    private              double                                       size;
    private              double                                       width;
    private              double                                       height;
    private              Canvas                                       canvas;
    private              GraphicsContext                              ctx;
    private              Pane                                         pane;
    private              ObservableList<ChartItem>                    items;
    private              Color                                        _textColor;
    private              ObjectProperty<Color>                        textColor;
    private              boolean                                      _autoTextColor;
    private              BooleanProperty                              autoTextColor;
    private              boolean                                      _useChartItemTextFill;
    private              BooleanProperty                              useChartItemTextFill;
    private              Order                                        _order;
    private              ObjectProperty<Order>                        order;
    private              boolean                                      _equalSegmentAngles;
    private              BooleanProperty                              equalSegmentAngles;
    private              boolean                                      _showPopup;
    private              BooleanProperty                              showPopup;
    private              String                                       _formatString;
    private              StringProperty                               formatString;
    private              boolean                                      _showItemName;
    private              BooleanProperty                              showItemName;
    private              Color                                        _selectedItemFill;
    private              ObjectProperty<Color>                        selectedItemFill;
    private              ItemEventListener                            itemListener;
    private              ListChangeListener<ChartItem>                itemListListener;
    private              EventHandler<MouseEvent>                     mouseHandler;
    private              Map<EventType, EventHandler<MouseEvent>>     mouseHandlers;
    private              CopyOnWriteArrayList<SelectionEventListener> listeners;
    private              InfoPopup                                    popup;


    // ******************** Constructors **************************************
    public CoxcombChart() {
        this(new ArrayList<>());
    }
    public CoxcombChart(final ChartItem... ITEMS) {
        this(Arrays.asList(ITEMS));
    }
    public CoxcombChart(final List<ChartItem> ITEMS) {
        width                 = PREFERRED_WIDTH;
        height                = PREFERRED_HEIGHT;
        size                  = PREFERRED_WIDTH;
        items                 = FXCollections.observableArrayList(ITEMS);
        _textColor            = Color.WHITE;
        _autoTextColor        = true;
        _useChartItemTextFill = false;
        _order                = Order.DESCENDING;
        _equalSegmentAngles   = false;
        _showPopup            = false;
        _formatString         = DEFAULT_FORMAT_STRING;
        _showItemName         = false;
        _selectedItemFill     = Color.RED;
        itemListener          = e -> reorder(getOrder());
        itemListListener      = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedItem -> addedItem.setOnItemEvent(itemListener));
                    reorder(getOrder());
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedItem -> removedItem.removeItemEventListener(itemListener));
                    reorder(getOrder());
                }
            }
            redraw();
        };
        mouseHandler        = e -> handleMouseEvent(e);
        mouseHandlers       = new ConcurrentHashMap<>();
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

        getStyleClass().add("coxcomb-chart");

        popup = new InfoPopup();

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        ctx.setLineCap(StrokeLineCap.BUTT);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setTextAlign(TextAlignment.CENTER);

        pane = new Pane(canvas);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        items.forEach(item -> item.setOnItemEvent(itemListener));
        items.addListener(itemListListener);
        setOnSelectionEvent(e -> {
            if (getShowPopup()) {
                popup.update(e);
                popup.animatedShow(getScene().getWindow());
            }
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

    public void dispose() {
        items.forEach(item -> item.removeItemEventListener(itemListener));
        items.removeListener(itemListListener);
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
    }

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

    public void sortItemsAscending() {
        Collections.sort(items, Comparator.comparingDouble(ChartItem::getValue));
    }
    public void sortItemsDescending() {
        Collections.sort(items, Comparator.comparingDouble(ChartItem::getValue).reversed());
    }

    public double sumOfAllItems() { return items.stream().mapToDouble(ChartItem::getValue).sum(); }

    public double getMinValue() { return items.isEmpty() ? 0 : items.stream().mapToDouble(ChartItem::getValue).min().getAsDouble(); }
    public double getMaxValue() { return items.isEmpty() ? 100 : items.stream().mapToDouble(ChartItem::getValue).max().getAsDouble(); }

    public Color getTextColor() { return null == textColor ? _textColor : textColor.get(); }
    public void setTextColor(final Color COLOR) {
        if (null == textColor) {
            _textColor = COLOR;
            redraw();
        } else {
            textColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> textColorProperty() {
        if (null == textColor) {
            textColor = new ObjectPropertyBase<Color>(_textColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CoxcombChart.this; }
                @Override public String getName() { return "textColor"; }
            };
            _textColor = null;
        }
        return textColor;
    }

    public Order getOrder() { return null == order ? _order : order.get(); }
    public void setOrder(final Order ORDER) {
        if (null == order) {
            _order = ORDER;
            reorder(_order);
        } else {
            order.set(ORDER);
        }
    }
    public ObjectProperty<Order> orderProperty() {
        if (null == order) {
            order = new ObjectPropertyBase<Order>(_order) {
                @Override protected void invalidated() { reorder(get()); }
                @Override public Object getBean() { return CoxcombChart.this; }
                @Override public String getName() { return "order"; }
            };
            _order = null;
        }
        return order;
    }

    public boolean isAutoTextColor() { return null == autoTextColor ? _autoTextColor : autoTextColor.get(); }
    public void setAutoTextColor(final boolean AUTO) {
        if (null == autoTextColor) {
            _autoTextColor = AUTO;
            redraw();
        } else {
            autoTextColor.set(AUTO);
        }
    }
    public BooleanProperty autoTextColorProperty() {
        if (null == autoTextColor) {
            autoTextColor = new BooleanPropertyBase(_autoTextColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CoxcombChart.this; }
                @Override public String getName() { return "autoTextColor"; }
            };
        }
        return autoTextColor;
    }

    public boolean getUseChartItemTextFill() { return null == useChartItemTextFill ? _useChartItemTextFill : useChartItemTextFill.get(); }
    public void setUseChartItemTextFill(final boolean USE) {
        if (null == useChartItemTextFill) {
            _useChartItemTextFill = USE;
            redraw();
        } else {
            useChartItemTextFill.set(USE);
        }
    }
    public BooleanProperty useChartItemTextFillProperty() {
        if (null == useChartItemTextFill) {
            useChartItemTextFill = new BooleanPropertyBase(_useChartItemTextFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CoxcombChart.this; }
                @Override public String getName() { return "useChartItemTextFill"; }
            };
        }
        return useChartItemTextFill;
    }

    public boolean getEqualSegmentAngles() { return null == equalSegmentAngles ? _equalSegmentAngles : equalSegmentAngles.get(); }
    public void setEqualSegmentAngles(final boolean SET) {
        if (null == equalSegmentAngles) {
            _equalSegmentAngles = SET;
            redraw();
        } else {
            equalSegmentAngles.set(SET);
        }
    }
    public BooleanProperty equalSegmentAnglesProperty() {
        if (null == equalSegmentAngles) {
            equalSegmentAngles = new BooleanPropertyBase(_equalSegmentAngles) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CoxcombChart.this; }
                @Override public String getName() { return "equalSegmentAngles"; }
            };
        }
        return equalSegmentAngles;
    }

    public boolean getShowPopup() { return null == showPopup ? _showPopup : showPopup.get(); }
    public void setShowPopup(final boolean SHOW) {
        if (null == showPopup) {
            _showPopup = SHOW;
            if (SHOW) {
                canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
            } else {
                canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
            }
        } else {
            showPopup.set(SHOW);
        }
    }
    public BooleanProperty showPopupProperty() {
        if (null == showPopup) {
            showPopup = new BooleanPropertyBase(_showPopup) {
                @Override protected void invalidated() {
                    if (get()) {
                        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
                    } else {
                        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
                    }
                }
                @Override public Object getBean() { return CoxcombChart.this; }
                @Override public String getName() { return "showPopup"; }
            };
        }
        return showPopup;
    }

    public String getFormatString() { return null == formatString ? _formatString : formatString.get(); }
    public void setFormatString(final String FORMAT_STRING) {
        if (null == formatString) {
            _formatString = null == FORMAT_STRING || FORMAT_STRING.isEmpty() ? DEFAULT_FORMAT_STRING : FORMAT_STRING;
            redraw();
        } else {
            formatString.set(FORMAT_STRING);
        }
    }
    public StringProperty formatStringProperty() {
        if (null == formatString) {
            formatString = new StringPropertyBase(_formatString) {
                @Override protected void invalidated() {
                    final String fs = get();
                    if (null == fs || fs.isEmpty()) { set(DEFAULT_FORMAT_STRING); }
                    redraw();
                }
                @Override public Object getBean() { return CoxcombChart.this; }
                @Override public String getName() { return "formatString"; }
            };
        }
        return formatString;
    }

    public boolean getShowItemName() { return null == showItemName ? _showItemName : showItemName.get(); }
    public void setShowItemName(final boolean SHOW) {
        if (null == showItemName) {
            _showItemName = SHOW;
            redraw();
        } else {
            showItemName.set(SHOW);
        }
    }
    public BooleanProperty showItemNameProperty() {
        if (null == showItemName) {
            showItemName = new BooleanPropertyBase(_showItemName) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CoxcombChart.this; }
                @Override public String getName() { return "showItemName"; }
            };
        }
        return showItemName;
    }

    public Color getSelectedItemFill() { return null == selectedItemFill ? _selectedItemFill : selectedItemFill.get(); }
    public void setSelectedItemFill(final Color SELECTED_ITEM_FILL) {
        if (null == selectedItemFill) {
            _selectedItemFill = SELECTED_ITEM_FILL;
            redraw();
        } else {
            selectedItemFill.set(SELECTED_ITEM_FILL);
        }
    }
    public ObjectProperty<Color> selectedItemFillProperty() {
        if (null == selectedItemFill) {
            selectedItemFill = new ObjectPropertyBase<>(_selectedItemFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CoxcombChart.this; }
                @Override public String getName() { return "selectedItemFill"; }
            };
        }
        return selectedItemFill;
    }

    public Optional<ChartItem> getSelectedItem(final MouseEvent EVT) {
        final double X = EVT.getX();
        final double Y = EVT.getY();

        popup.setX(EVT.getScreenX());
        popup.setY(EVT.getScreenY() - popup.getHeight());

        int     noOfChartItems = items.size();
        boolean equalsAngles   = getEqualSegmentAngles();
        double  barWidth       = size * 0.04;
        double  minValue       = getMinValue();
        double  maxValue       = getMaxValue();
        double  valueRange     = maxValue - minValue;
        double  sum            = sumOfAllItems();
        double  stepSize       = equalsAngles ? (360.0 / noOfChartItems) : (360.0 / sum);
        double  angle          = equalsAngles ? stepSize : 0;
        double  startAngle     = 0;
        double  baseXY         = size * 0.345;
        double  baseWH         = size * 0.31;
        double  xy             = size * 0.32;
        double  minWH          = size * 0.36;
        double  maxWH          = size * 0.64;
        double  whRange        = maxWH - minWH;
        double  wh             = minWH;
        double  whStep         = equalsAngles ? (whRange / valueRange) : (whRange / noOfChartItems);

        for (int i = 0 ; i < noOfChartItems ; i++) {
            ChartItem item = items.get(i);

            if (equalsAngles) {
                barWidth    = item.getValue() * whStep;
                xy          = baseXY - barWidth * 0.5;
                wh          = baseWH + barWidth;
                startAngle += angle;
            } else {
                angle       = item.getValue() * stepSize;
                startAngle += angle;
                xy         -= (whStep / 2.0);
                wh         += whStep;
                barWidth   += whStep;
            }

            // Check if x,y are in segment
            if (Helper.isInRingSegment(X, Y, xy, xy, wh, wh, Math.abs(360 - startAngle), angle, barWidth)) {
                fireSelectionEvent(new SelectionEvent(item));
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public void handleMouseEvent(final MouseEvent EVT) {
        final double X = EVT.getX();
        final double Y = EVT.getY();

        popup.setX(EVT.getScreenX());
        popup.setY(EVT.getScreenY() - popup.getHeight());

        int     noOfChartItems = items.size();
        boolean equalsAngles   = getEqualSegmentAngles();
        double  barWidth       = size * 0.04;
        double  minValue       = getMinValue();
        double  maxValue       = getMaxValue();
        double  valueRange     = maxValue - minValue;
        double  sum            = sumOfAllItems();
        double  stepSize       = equalsAngles ? (360.0 / noOfChartItems) : (360.0 / sum);
        double  angle          = equalsAngles ? stepSize : 0;
        double  startAngle     = 0;
        double  baseXY         = size * 0.345;
        double  baseWH         = size * 0.31;
        double  xy             = size * 0.32;
        double  minWH          = size * 0.36;
        double  maxWH          = size * 0.64;
        double  whRange        = maxWH - minWH;
        double  wh             = minWH;
        double  whStep         = equalsAngles ? (whRange / valueRange) : (whRange / noOfChartItems);

        for (int i = 0 ; i < noOfChartItems ; i++) {
            ChartItem item = items.get(i);

            if (equalsAngles) {
                barWidth    = item.getValue() * whStep;
                xy          = baseXY - barWidth * 0.5;
                wh          = baseWH + barWidth;
                startAngle += angle;
            } else {
                angle       = item.getValue() * stepSize;
                startAngle += angle;
                xy         -= (whStep / 2.0);
                wh         += whStep;
                barWidth   += whStep;
            }

            // Check if x,y are in segment
            if (Helper.isInRingSegment(X, Y, xy, xy, wh, wh, Math.abs(360 - startAngle), angle, barWidth)) {
                fireSelectionEvent(new SelectionEvent(item));
                break;
            }
        }
    }

    // ******************** Mouse Event Handling ******************************
    public void onMousePressed(final EventHandler<MouseEvent> handler) {
        if (mouseHandlers.containsKey(MouseEvent.MOUSE_PRESSED) && mouseHandlers.get(MouseEvent.MOUSE_PRESSED).equals(handler)) { return; }
        mouseHandlers.put(MouseEvent.MOUSE_PRESSED, handler);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, handler);
    }
    public void removeOnMousePressed(final EventHandler<MouseEvent> handler) {
        if (mouseHandlers.containsKey(MouseEvent.MOUSE_PRESSED) && mouseHandlers.get(MouseEvent.MOUSE_PRESSED).equals(handler)) {
            mouseHandlers.remove(MouseEvent.MOUSE_PRESSED);
            canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, handler);
        }
    }

    public void onMouseReleased(final EventHandler<MouseEvent> handler) {
        if (mouseHandlers.containsKey(MouseEvent.MOUSE_RELEASED) && mouseHandlers.get(MouseEvent.MOUSE_RELEASED).equals(handler)) { return; }
        mouseHandlers.put(MouseEvent.MOUSE_RELEASED, handler);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, handler);
    }
    public void removeOnMouseReleased(final EventHandler<MouseEvent> handler) {
        if (mouseHandlers.containsKey(MouseEvent.MOUSE_RELEASED) && mouseHandlers.get(MouseEvent.MOUSE_RELEASED).equals(handler)) {
            mouseHandlers.remove(MouseEvent.MOUSE_RELEASED);
            canvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, handler);
        }
    }

    public void onMouseMoved(final EventHandler<MouseEvent> handler) {
        if (mouseHandlers.containsKey(MouseEvent.MOUSE_MOVED) && mouseHandlers.get(MouseEvent.MOUSE_MOVED).equals(handler)) { return; }
        mouseHandlers.put(MouseEvent.MOUSE_MOVED, handler);
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, handler);
    }
    public void removeOnMouseMoved(final EventHandler<MouseEvent> handler) {
        if (mouseHandlers.containsKey(MouseEvent.MOUSE_MOVED) && mouseHandlers.get(MouseEvent.MOUSE_MOVED).equals(handler)) {
            mouseHandlers.remove(MouseEvent.MOUSE_MOVED);
            canvas.removeEventHandler(MouseEvent.MOUSE_MOVED, handler);
        }
    }


    private void reorder(final Order ORDER) {
        if (ORDER == Order.ASCENDING) {
            sortItemsAscending();
        } else {
            sortItemsDescending();
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
    private void drawChart() {
        Order        order           = getOrder();
        int          noOfChartItems  = items.size();
        boolean      equalAngles     = getEqualSegmentAngles();
        double       center          = size * 0.5;
        double       barWidth        = size * 0.04;
        double       minValue        = getMinValue();
        double       maxValue        = getMaxValue();
        double       valueRange      = maxValue - minValue;
        double       sum             = sumOfAllItems();
        double       stepSize        = equalAngles ? (360.0 / noOfChartItems) : (360.0 / sum);
        double       angle           = 0;
        double       startAngle      = 90;
        double       baseXY          = size * 0.345;
        double       baseWH          = size * 0.31;
        double       xy              = size * 0.32;
        double       minWH           = size * 0.36;
        double       maxWH           = size * 0.64;
        double       whRange         = maxWH - minWH;
        double       wh              = minWH;
        double       whStep          = equalAngles ? (whRange / valueRange) : (whRange / noOfChartItems);
        Color        textColor       = getTextColor();
        boolean      isAutoColor     = isAutoTextColor();
        DropShadow   shadow          = new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.75), size * 0.02, 0, 0, 0);
        double       spread          = size * 0.005;
        double       fontSize        = size * 0.03;
        double       itemNameOffset  = fontSize * 0.75;
        double       x, y;
        double       tx, ty;
        double       endAngle;
        double       radius;
        double       clippingRadius;

        ctx.clearRect(0, 0, size, size);
        ctx.setFont(Fonts.opensansRegular(fontSize));
        final String formatString = getFormatString();
        for (int i = 0 ; i < noOfChartItems ; i++) {
            ChartItem item       = items.get(i);
            double    value      = item.getValue();

            startAngle += angle;
            if (equalAngles) {
                barWidth = value * whStep;
                xy       = baseXY - barWidth * 0.5;
                wh       = baseWH + barWidth;
                angle    = stepSize;
            } else {
                xy         -= (whStep / 2.0);
                wh         += whStep;
                barWidth   += whStep;
                angle       = value * stepSize;
            }
            endAngle       = startAngle + angle;
            radius         = wh * 0.5;
            clippingRadius = radius + barWidth * 0.5;

            // Segment
            ctx.save();
            // Draw segment
            ctx.setLineWidth(barWidth);
            ctx.setStroke(item.isSelected() ? getSelectedItemFill() : item.getFill());
            ctx.strokeArc(xy, xy, wh, wh, startAngle, angle, ArcType.OPEN);

            // Set Segment Clipping
            ctx.save();
            ctx.beginPath();
            if (equalAngles && Order.DESCENDING == order && i < noOfChartItems - 1) {
                ChartItem nextItem     = items.get(i + 1);
                double    nextBarWidth = nextItem.getValue() * whStep;
                double    nextWH       = baseWH + nextBarWidth;
                double    nextRadius   = nextWH * 0.5;
                clippingRadius = nextRadius + nextBarWidth * 0.5;
            }
            ctx.arc(center, center, clippingRadius, clippingRadius, 0, 360);
            ctx.clip();

            // Add shadow effect to segment
            if (i != (noOfChartItems - 1) && angle > 2) {
                x = Math.cos(Math.toRadians(endAngle - 5));
                y = -Math.sin(Math.toRadians(endAngle - 5));
                shadow.setOffsetX(x * spread);
                shadow.setOffsetY(y * spread);
                if (equalAngles && Order.DESCENDING == order && i < noOfChartItems - 1) {
                    ChartItem nextItem     = items.get(i + 1);
                    double    nextBarWidth = nextItem.getValue() * whStep;
                    double    nextXY       = baseXY - nextBarWidth * 0.5;
                    double    nextWH       = baseWH + nextBarWidth;
                    ctx.save();
                    ctx.setLineWidth(nextBarWidth);
                    ctx.setEffect(shadow);
                    ctx.strokeArc(nextXY, nextXY, nextWH, nextWH, endAngle, 2, ArcType.OPEN);
                    ctx.restore();
                    if (i == 0) {
                        x = Math.cos(Math.toRadians(startAngle + 5));
                        y = -Math.sin(Math.toRadians(startAngle + 5));
                        shadow.setOffsetX(x * spread);
                        shadow.setOffsetY(y * spread);
                        ctx.setEffect(shadow);
                        nextBarWidth = minValue * whStep;
                        nextXY       = baseXY - nextBarWidth * 0.5;
                        nextWH       = baseWH + nextBarWidth;
                        ctx.setLineWidth(nextBarWidth);
                        ctx.strokeArc(nextXY, nextXY, nextWH, nextWH, startAngle, -2, ArcType.OPEN);
                    }
                } else {
                    ctx.save();
                    ctx.setEffect(shadow);
                    ctx.strokeArc(xy, xy, wh, wh, endAngle, 2, ArcType.OPEN);
                    ctx.restore();
                    if (i == 0) {
                        x = Math.cos(Math.toRadians(startAngle + 5));
                        y = -Math.sin(Math.toRadians(startAngle + 5));
                        shadow.setOffsetX(x * spread);
                        shadow.setOffsetY(y * spread);
                        ctx.setEffect(shadow);
                        ctx.strokeArc(xy, xy, wh, wh, startAngle, -2, ArcType.OPEN);
                    }
                }
            }
            // Remove Segment Clipping
            ctx.restore();

            ctx.restore();

            // Percentage
            if (angle > 12 && barWidth > 10) {
                tx = center + radius * Math.cos(Math.toRadians(endAngle - angle * 0.5));
                ty = center - radius * Math.sin(Math.toRadians(endAngle - angle * 0.5));
                if (getUseChartItemTextFill()) {
                    ctx.setFill(item.getTextFill());
                } else if (isAutoColor) {
                    ctx.setFill(Helper.isDark(item.getFill()) ? Color.WHITE : Color.BLACK);
                } else {
                    ctx.setFill(textColor);
                }
                if (getShowItemName()) {
                    ctx.fillText(item.getName(), tx, ty - itemNameOffset, barWidth);
                }
                ctx.fillText(String.format(Locale.US, formatString, (value / sum * 100.0)), tx, getShowItemName() ? ty + itemNameOffset : ty, barWidth);
            }
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size = width < height ? width : height;

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
