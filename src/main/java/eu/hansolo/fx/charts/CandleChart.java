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

import eu.hansolo.fx.charts.color.MaterialDesignColors;
import eu.hansolo.fx.charts.data.CandleChartItem;
import eu.hansolo.fx.charts.event.ChartEvt;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.TooltipPopup;
import eu.hansolo.fx.geometry.Rectangle;
import eu.hansolo.toolbox.evt.EvtObserver;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@DefaultProperty("children")
public class CandleChart extends Region {
    private static final double                              PREFERRED_WIDTH          = 600;
    private static final double                              PREFERRED_HEIGHT         = 400;
    private static final double                              MINIMUM_WIDTH            = 50;
    private static final double                              MINIMUM_HEIGHT           = 50;
    private static final double                              MAXIMUM_WIDTH            = 2048;
    private static final double                              MAXIMUM_HEIGHT           = 2048;
    private static final Color                               DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;
    private static final Color                               DEFAULT_BULLISH_COLOR    = MaterialDesignColors.GREEN_300.get();
    private static final Color                               DEFAULT_BEARISH_COLOR    = MaterialDesignColors.RED_300.get();
    private static final Color                               DEFAULT_STROKE           = Color.BLACK;
    private static final DateTimeFormatter                   DTF                      = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.US);
    private              String                              userAgentStyleSheet;
    private              double                              width;
    private              double                              height;
    private              Canvas                              canvas;
    private              GraphicsContext                     ctx;
    private              ObservableList<CandleChartItem>     items;
    private              EvtObserver<ChartEvt>               itemObserver;
    private              ListChangeListener<CandleChartItem> itemListListener;
    private              int                                 _decimals;
    private              IntegerProperty                     decimals;
    private              Locale                              _locale;
    private              ObjectProperty<Locale>              locale;
    private              String                              formatString;
    private              Map<Rectangle, CandleChartItem>     paths;
    private              TooltipPopup                        popup;
    private              double                              minValue;
    private              double                              maxValue;
    private              double                              calculatedRange;
    private              Color                               _backgroundColor;
    private              ObjectProperty<Color>               backgroundColor;
    private              Color                               _bullishColor;
    private              ObjectProperty<Color>               bullishColor;
    private              Color                               _bearishColor;
    private              ObjectProperty<Color>               bearishColor;
    private              Color                               _strokeColor;
    private              ObjectProperty<Color>               strokeColor;
    private              boolean                             _endLinesVisible;
    private              BooleanProperty                     endLinesVisible;
    private              boolean                             _useItemColorForStroke;
    private              BooleanProperty                     useItemColorForStroke;
    private              int                                 _minNumberOfItems;
    private              IntegerProperty                     minNumberOfItems;
    private              boolean                             _useMinNumberOfItems;
    private              BooleanProperty                     useMinNumberOfItems;
    private              Axis                                _yAxis;
    private              ObjectProperty<Axis>                yAxis;
    private              boolean                             sorted;


    // ******************** Constructors **************************************
    public CandleChart() {
        this(new ArrayList<>());
    }
    public CandleChart(final List<CandleChartItem> ITEMS) {
        items                  = FXCollections.observableArrayList();
        itemObserver           = e -> redraw();
        itemListListener       = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedItem -> addedItem.addChartEvtObserver(ChartEvt.ITEM_UPDATE, itemObserver));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedItem -> removedItem.removeChartEvtObserver(ChartEvt.ITEM_UPDATE, itemObserver));
                }
            }
            minValue        = items.stream().min(Comparator.comparing(CandleChartItem::getLow)).get().getLow();
            maxValue        = items.stream().max(Comparator.comparing(CandleChartItem::getHigh)).get().getHigh();
            calculatedRange = maxValue - minValue;
            sorted          = false;
        };
        _backgroundColor       = DEFAULT_BACKGROUND_COLOR;
        _bullishColor          = DEFAULT_BULLISH_COLOR;
        _bearishColor          = DEFAULT_BEARISH_COLOR;
        _strokeColor           = DEFAULT_STROKE;
        _endLinesVisible       = false;
        _useItemColorForStroke = false;
        _minNumberOfItems      = 10;
        _useMinNumberOfItems   = false;
        _yAxis                 = null;
        _decimals              = 0;
        _locale                = Locale.getDefault();
        paths                  = new ConcurrentHashMap<>();
        formatString           = "%." + _decimals + "f";
        popup                  = new TooltipPopup("", 3500, true);
        minValue               = 100;
        maxValue               = 0;
        calculatedRange        = 100;
        sorted                 = false;

        items.setAll(null == ITEMS ? new ArrayList<>() : ITEMS);

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

        getStyleClass().add("candle-chart");

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        getChildren().setAll(canvas);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        popup.setOnHiding(e -> popup.setText(""));
        items.addListener(itemListListener);
        canvas.setOnMousePressed(e -> {
            paths.forEach((path, chartItem) -> {
                double eventX = e.getX();
                double eventY = e.getY();
                if (path.contains(eventX, eventY)) {
                    boolean bullish = chartItem.getOpen() < chartItem.getClose();
                    String  tooltipText = new StringBuilder().append(chartItem.getName()).append("\n")
                                                             .append("timestamp: ").append(DTF.format(LocalDateTime.ofInstant(chartItem.getTimestamp(), ZoneId.systemDefault()))).append("\n")
                                                             .append("high     : ").append(String.format(getLocale(), formatString, chartItem.getHigh())).append("\n")
                                                             .append(bullish ? "close    : " : "open     : ").append(bullish ? String.format(getLocale(), formatString, chartItem.getClose()) : String.format(getLocale(), formatString, chartItem.getOpen())).append("\n")
                                                             .append(bullish ? "open     : " : "close    : ").append(bullish ? String.format(getLocale(), formatString, chartItem.getOpen())  : String.format(getLocale(), formatString, chartItem.getClose())).append("\n")
                                                             .append("low      : ").append(String.format(getLocale(), formatString, chartItem.getLow()))
                                                             .toString();
                    if (!tooltipText.isEmpty()) {
                        popup.setX(e.getScreenX() - popup.getWidth() * 0.5);
                        popup.setY(e.getScreenY() - 30);
                        popup.setText(tooltipText);
                        popup.animatedShow(getScene().getWindow());
                    }
                }
            });
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

    public void dispose() { items.removeListener(itemListListener); }

    public List<CandleChartItem> getItems() { return items; }
    public void setItems(final CandleChartItem... ITEMS) { setItems(Arrays.asList(ITEMS)); }
    public void setItems(final List<CandleChartItem> ITEMS) { items.setAll(ITEMS); }
    public void addItem(final CandleChartItem ITEM) {
        if (!items.contains(ITEM)) { items.add(ITEM); }
    }
    public void removeItem(final CandleChartItem ITEM) {
        if (items.contains(ITEM)) { items.remove(ITEM); }
    }

    public int getDecimals() { return null == decimals ? _decimals : decimals.get(); }
    public void setDecimals(final int DECIMALS) {
        if (null == decimals) {
            _decimals = Helper.clamp(0, 6, DECIMALS);
            formatString = new StringBuilder("%.").append(getDecimals()).append("f").toString();
            redraw();
        } else {
            decimals.set(DECIMALS);
        }
    }
    public IntegerProperty decimalsProperty() {
        if (null == decimals) {
            decimals = new IntegerPropertyBase(_decimals) {
                @Override protected void invalidated() {
                    set(Helper.clamp(0, 6, get()));
                    formatString = new StringBuilder("%.").append(get()).append("f").toString();
                    redraw();
                }
                @Override public Object getBean() { return CandleChart.this; }
                @Override public String getName() { return "decimals"; }
            };
        }
        return decimals;
    }

    public Locale getLocale() { return null == locale ? _locale : locale.get(); }
    public void setLocale(final Locale LOCALE) {
        if (null == locale) {
            _locale = LOCALE;
        } else {
            locale.set(LOCALE);
        }
    }
    public ObjectProperty<Locale> localeProperty() {
        if (null == locale) {
            locale = new ObjectPropertyBase<Locale>(_locale) {
                @Override protected void invalidated() {  }
                @Override public Object getBean() { return CandleChart.this; }
                @Override public String getName() { return "locale"; }
            };
        }
        _locale = null;
        return locale;
    }

    public Color getBackgroundColor() { return null == backgroundColor ? _backgroundColor : backgroundColor.get(); }
    public void setBackgroundColor(final Color backgroundColor) {
        if (null == this.backgroundColor) {
            _backgroundColor = backgroundColor;
            redraw();
        } else {
            this.backgroundColor.set(backgroundColor);
        }
    }
    public ObjectProperty<Color> backgroundColorProperty() {
        if (null == backgroundColor) {
            backgroundColor = new ObjectPropertyBase<>(_backgroundColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CandleChart.this; }
                @Override public String getName() { return "backgroundColor"; }
            };
            _backgroundColor = null;
        }
        return backgroundColor;
    }

    public Color getBullishColor() { return null == bullishColor ? _bullishColor : bullishColor.get(); }
    public void setBullishColor(final Color bullishColor) {
        if (null == this.bullishColor) {
            _bullishColor = bullishColor;
            redraw();
        } else {
            this.bullishColor.set(bullishColor);
        }
    }
    public ObjectProperty<Color> bullishColorProperty() {
        if (null == bullishColor) {
            bullishColor = new ObjectPropertyBase<>(_bullishColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CandleChart.this; }
                @Override public String getName() { return "bullishColor"; }
            };
            _bullishColor = null;
        }
        return bullishColor;
    }

    public Color getBearishColor() { return null == bearishColor ? _bearishColor : bearishColor.get(); }
    public void setBearishColor(final Color bearishColor) {
        if (null == this.bearishColor) {
            _bearishColor = bearishColor;
            redraw();
        } else {
            this.bearishColor.set(bearishColor);
        }
    }
    public ObjectProperty<Color> bearishColorProperty() {
        if (null == bearishColor) {
            bearishColor = new ObjectPropertyBase<>(_bearishColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CandleChart.this; }
                @Override public String getName() { return "bearishColor"; }
            };
            _bearishColor = null;
        }
        return bearishColor;
    }

    public Color getStrokeColor() { return null == strokeColor ? _strokeColor : strokeColor.get(); }
    public void setStrokeColor(final Color strokeColor) {
        if (null == this.strokeColor) {
            _strokeColor = strokeColor;
            redraw();
        } else {
            this.strokeColor.set(strokeColor);
        }
    }
    public ObjectProperty<Color> strokeColorProperty() {
        if (null == strokeColor) {
            strokeColor = new ObjectPropertyBase<>(_strokeColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CandleChart.this; }
                @Override public String getName() { return "strokeColor"; }
            };
            _strokeColor = null;
        }
        return strokeColor;
    }

    public boolean getEndLinesVisible() { return null == endLinesVisible ? _endLinesVisible : endLinesVisible.get(); }
    public void setEndLinesVisible(final boolean endLinesVisible) {
        if (null == this.endLinesVisible) {
            _endLinesVisible = endLinesVisible;
            redraw();
        } else {
            this.endLinesVisible.set(endLinesVisible);
        }
    }
    public BooleanProperty endLinesVisibleProperty() {
        if (null == endLinesVisible) {
            endLinesVisible = new BooleanPropertyBase(_endLinesVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CandleChart.this; }
                @Override public String getName() { return "endLinesVisible"; }
            };
        }
        return endLinesVisible;
    }

    public boolean getUseItemColorForStroke() { return null == useItemColorForStroke ? _useItemColorForStroke : useItemColorForStroke.get(); }
    public void setUseItemColorForStroke(final boolean useItemColorForStroke) {
        if (null == this.useItemColorForStroke) {
            _useItemColorForStroke = useItemColorForStroke;
            redraw();
        } else {
            this.useItemColorForStroke.set(useItemColorForStroke);
        }
    }
    public BooleanProperty useItemColorForStrokeProperty() {
        if (null == useItemColorForStroke) {
            useItemColorForStroke = new BooleanPropertyBase(_useItemColorForStroke) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CandleChart.this; }
                @Override public String getName() { return "useItemColorForStroke"; }
            };
        }
        return useItemColorForStroke;
    }

    public int getMinNumberOfItems() { return null == minNumberOfItems ? _minNumberOfItems : minNumberOfItems.get(); }
    public void setMinNumberOfItems(final int minNumberOfItems) {
        if (null == this.minNumberOfItems) {
            _minNumberOfItems = Helper.clamp(1, Integer.MAX_VALUE, minNumberOfItems);
            redraw();
        } else {
            this.minNumberOfItems.set(minNumberOfItems);
        }
    }
    public IntegerProperty minNumberOfItemsProperty() {
        if (null == minNumberOfItems) {
            minNumberOfItems = new IntegerPropertyBase(_minNumberOfItems) {
                @Override protected void invalidated() {
                    set(Helper.clamp(1, Integer.MAX_VALUE, get()));
                    redraw();
                }
                @Override public Object getBean() { return CandleChart.this; }
                @Override public String getName() { return "minNumberOfItems"; }
            };
        }
        return minNumberOfItems;
    }

    public boolean getUseMinNumberOfItems() { return null == useMinNumberOfItems ? _useMinNumberOfItems : useMinNumberOfItems.get(); }
    public void setUseMinNumberOfItems(final boolean useMinNumberOfItems) {
        if (null == this.useMinNumberOfItems) {
            _useMinNumberOfItems = useMinNumberOfItems;
            redraw();
        } else {
            this.useMinNumberOfItems.set(useMinNumberOfItems);
        }
    }
    public BooleanProperty useMinNumberOfItemsProperty() {
        if (null == useMinNumberOfItems) {
            useMinNumberOfItems = new BooleanPropertyBase(_useMinNumberOfItems) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CandleChart.this; }
                @Override public String getName() { return "useMinNumberOfItems"; }
            };
        }
        return useMinNumberOfItems;
    }

    public Axis getYAxis() { return null == yAxis ? _yAxis : yAxis.get(); }
    public void setYAxis(final Axis yAxis) {
        if (null == this.yAxis) {
            _yAxis = yAxis;
            redraw();
        } else {
            this.yAxis.set(yAxis);
        }
    }
    public ObjectProperty<Axis> yAxisProperty() {
        if (null == yAxis) {
            yAxis = new ObjectPropertyBase<>(_yAxis) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CandleChart.this; }
                @Override public String getName() { return "yAxis"; }
            };
        }
        return yAxis;
    }

    public void resetYAxis() {
        _yAxis = null;
        yAxis  = null;
        minValue = items.stream().min(Comparator.comparing(CandleChartItem::getLow)).get().getLow();
        maxValue = items.stream().max(Comparator.comparing(CandleChartItem::getHigh)).get().getHigh();
        redraw();
    }

    public void setPopupTimeout(final long milliseconds) { popup.setTimeout(milliseconds); }

    /**
     * Calling this method will render this chart/plot to a png given of the given width and height
     * @param filename The path and name of the file  /Users/hansolo/Desktop/plot.png
     * @param width The width of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @param height The height of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @return True if the procedure was successful, otherwise false
     */
    public boolean renderToImage(final String filename, final int width, final int height) {
        return Helper.renderToImage(CandleChart.this, width, height, filename);
    }

    /**
     * Calling this method will render this chart/plot to a png given of the given width and height
     * @param width The width of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @param height The height of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @return A BufferedImage of this chart in the given dimension
     */
    public BufferedImage renderToImage(final int width, final int height) {
        return Helper.renderToImage(CandleChart.this, width, height);
    }


    // ******************** Layout ********************************************
    @Override public String getUserAgentStylesheet() {
        if (null == userAgentStyleSheet) { userAgentStyleSheet = CandleChart.class.getResource("chart.css").toExternalForm(); }
        return userAgentStyleSheet;
    }


    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (width > 0 && height > 0) {
            canvas.setWidth(width);
            canvas.setHeight(height);
            canvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            ctx.setTextBaseline(VPos.CENTER);
        }
        redraw();
    }

    private void redraw() {
        paths.clear();
        double inset       = 5;
        double chartWidth  = this.width - 2 * inset;
        double chartHeight = this.height - 2 * inset;

        ctx.clearRect(0, 0, width, height);
        ctx.setFill(getBackgroundColor());
        ctx.fillRect(0, 0, width, height);

        if (items.isEmpty()) { return; }

        if (!sorted) {
            Collections.sort(items, Comparator.comparing(CandleChartItem::getTimestamp));
            sorted = true;
        }

        int    noOfItems              = items.size();
        double minNumberOfItems       = noOfItems > getMinNumberOfItems() ? noOfItems : getMinNumberOfItems();
        double itemWidth              = getUseMinNumberOfItems() ? chartWidth / (minNumberOfItems + (minNumberOfItems * 0.2)) : chartWidth / (noOfItems + (noOfItems * 0.2));
        double halfItemWidth          = itemWidth * 0.5;
        double quarterItemWidth       = halfItemWidth * 0.5;
        double itemSpacer             = (chartWidth - (noOfItems * itemWidth)) / (noOfItems - 1);
        Axis   yAxis                  = getYAxis();
        double rangeY;
        if (null == yAxis) {
            rangeY = calculatedRange;
        } else {
            rangeY   = yAxis.getMaxValue() - yAxis.getMinValue();
            minValue = yAxis.getMinValue();
            maxValue = yAxis.getMaxValue();
        }
        double calculatedScaleFactorY = chartHeight / rangeY;

        for (int i = 0 ; i < noOfItems ; i++) {
            CandleChartItem item      = items.get(i);
            double          high      = item.getHigh();
            double          low       = item.getLow();
            double          open      = item.getOpen();
            double          close     = item.getClose();
            Color           fill      = open < close ? getBullishColor() : getBearishColor();
            double          x         = inset + halfItemWidth + (i * itemWidth) + (i * itemSpacer);
            double          yLow      = (low - minValue) * calculatedScaleFactorY;
            double          yHigh     = (high - minValue) * calculatedScaleFactorY;
            double          yOpen     = (open - minValue) * calculatedScaleFactorY;
            double          yClose    = (close - minValue) * calculatedScaleFactorY;
            double          boxHeight = open < close ? (yClose - yOpen) : (yOpen - yClose);

            if (getUseItemColorForStroke()) {
                ctx.setStroke(fill.equals(Color.TRANSPARENT) ? getStrokeColor() : fill);
            } else {
                ctx.setStroke(getStrokeColor());
            }
            if (getEndLinesVisible()) {
                ctx.strokeLine(x - quarterItemWidth, height - inset - yHigh, x + quarterItemWidth, height - inset - yHigh);
                ctx.strokeLine(x - quarterItemWidth, height - inset - yLow, x + quarterItemWidth, height - inset- yLow);
            }
            ctx.strokeLine(x, height - inset - yHigh, x, height - inset - yLow);
            ctx.setFill(fill.equals(Color.TRANSPARENT) ? getBackgroundColor() : fill);
            ctx.setStroke(fill.equals(Color.TRANSPARENT) ? getStrokeColor() : fill);
            ctx.fillRect(x - halfItemWidth, height - inset - (open < close ? yClose : yOpen), itemWidth, boxHeight);
            ctx.strokeRect(x - halfItemWidth, height - inset - (open < close ? yClose : yOpen), itemWidth, boxHeight);
            paths.put(new Rectangle(x - halfItemWidth, height - inset - yHigh, itemWidth, (yHigh - yLow)), item);
        }
    }
}
