/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
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


import eu.hansolo.fx.charts.data.BubbleGridChartItem;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.FontMetrix;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.InfoPopup;
import eu.hansolo.fx.charts.tools.Order;
import eu.hansolo.fx.charts.tools.Topic;
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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@DefaultProperty("children")
public class BubbleGridChart extends Region {
    private static final double                                  PREFERRED_WIDTH  = 600;
    private static final double                                  PREFERRED_HEIGHT = 400;
    private static final double                                  MINIMUM_WIDTH    = 50;
    private static final double                                  MINIMUM_HEIGHT   = 50;
    private static final double                                  MAXIMUM_WIDTH    = 4096;
    private static final double                                  MAXIMUM_HEIGHT   = 4096;
    private              double                                  width;
    private              double                                  height;
    private              Canvas                                  canvas;
    private              GraphicsContext                         ctx;
    private              ObservableList<BubbleGridChartItem>     items;
    private              List<ChartItem>                         xCategoryItems;
    private              List<ChartItem>                         yCategoryItems;
    private              Map<ChartItem, Double>                  sumsOfXCategoryItems;
    private              Map<ChartItem, Double>                  sumsOfYCategoryItems;
    private              double                                  sumOfValues;
    private              double                                  minValue;
    private              double                                  maxValue;
    private              ItemEventListener                       itemListener;
    private              ListChangeListener<BubbleGridChartItem> itemListListener;
    private              InfoPopup                               popup;
    private              Paint                                   _chartBackground;
    private              ObjectProperty<Paint>                   chartBackground;
    private              Color                                   _gridColor;
    private              ObjectProperty<Color>                   gridColor;
    private              boolean                                 _showGrid;
    private              BooleanProperty                         showGrid;
    private              Color                                   _textColor;
    private              ObjectProperty<Color>                   textColor;
    private              boolean                                 _autoBubbleTextColor;
    private              BooleanProperty                         autoBubbleTextColor;
    private              boolean                                 useXCategoryFill;
    private              boolean                                 _showValues;
    private              BooleanProperty                         showValues;
    private              boolean                                 _showPercentage;
    private              BooleanProperty                         showPercentage;
    private              boolean                                 _useGradientFill;
    private              BooleanProperty                         useGradientFill;
    private              Color                                   _minColor;
    private              ObjectProperty<Color>                   minColor;
    private              Color                                   _maxColor;
    private              ObjectProperty<Color>                   maxColor;
    private              LinearGradient                          gradient;
    private              Topic                                   sortTopicX;
    private              Topic                                   sortTopicY;
    private              Order                                   sortOrderX;
    private              Order                                   sortOrderY;
    private List<Bubble> bubbles;


    // ******************** Constructors **************************************
    public BubbleGridChart() {
        items                  = FXCollections.observableArrayList();
        xCategoryItems         = new ArrayList<>();
        yCategoryItems         = new ArrayList<>();
        sumsOfXCategoryItems   = new HashMap<>();
        sumsOfYCategoryItems   = new HashMap<>();
        sumOfValues            = 0;
        minValue               = 0;
        maxValue               = 0;
        useXCategoryFill       = true;
        _textColor             = Color.BLACK;
        _autoBubbleTextColor   = false;
        _chartBackground       = Color.TRANSPARENT;
        _gridColor             = Color.rgb(0, 0, 0, 0.1);
        _showGrid              = true;
        _showValues            = true;
        _showPercentage        = false;
        _useGradientFill       = false;
        _minColor              = Color.web("#2C67D5");
        _maxColor              = Color.web("#F23C5A");
        gradient               = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, _minColor), new Stop(1, _maxColor));
        bubbles                = new ArrayList<>();
        sortTopicX             = Topic.INDEX;
        sortTopicY             = Topic.INDEX;
        sortOrderX             = Order.ASCENDING;
        sortOrderY             = Order.ASCENDING;
        itemListener           = e -> sort();
        itemListListener       = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedItem -> addedItem.setOnItemEvent(itemListener));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedItem -> removedItem.removeItemEventListener(itemListener));
                }
            }

            xCategoryItems = items.stream().map(bgci -> bgci.getCategoryX()).distinct().collect(Collectors.toList());
            yCategoryItems = items.stream().map(bgci -> bgci.getCategoryY()).distinct().collect(Collectors.toList());

            xCategoryItems.forEach(xCategoryItem -> sumsOfXCategoryItems.put(xCategoryItem, items.stream()
                                                                                                 .filter(bgci -> bgci.getCategoryX().equals(xCategoryItem))
                                                                                                 .mapToDouble(bgci -> bgci.getValue())
                                                                                                 .sum()));
            yCategoryItems.forEach(yCategoryItem -> sumsOfYCategoryItems.put(yCategoryItem, items.stream()
                                                                                                 .filter(bgci -> bgci.getCategoryY().equals(yCategoryItem))
                                                                                                 .mapToDouble(bgci -> bgci.getValue())
                                                                                                 .sum()));

            minValue    = items.parallelStream().min(Comparator.comparingDouble(BubbleGridChartItem::getValue)).map(bgci -> bgci.getValue()).orElse(0d);
            maxValue    = items.parallelStream().max(Comparator.comparingDouble(BubbleGridChartItem::getValue)).map(bgci -> bgci.getValue()).orElse(0d);
            sumOfValues = items.parallelStream().mapToDouble(bgci -> bgci.getValue()).sum();

            sort();
        };

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

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx = canvas.getGraphicsContext2D();

        ctx.setLineCap(StrokeLineCap.BUTT);

        popup = new InfoPopup();

        getChildren().setAll(canvas);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        items.addListener(itemListListener);
        canvas.setOnMouseClicked(e -> bubbles.forEach(bubble -> {
                if (Helper.isInCircle(e.getX(), e.getY(), bubble.x, bubble.y, bubble.r)) {
                    popup.setX(e.getScreenX());
                    popup.setY(e.getScreenY() - popup.getHeight());
                    popup.update(bubble.item, sumOfValues);
                    popup.animatedShow(getScene().getWindow());
                }
            }));
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
                @Override public Object getBean() { return BubbleGridChart.this; }
                @Override public String getName() { return "chartBackground"; }
            };
            _chartBackground = null;
        }
        return chartBackground;
    }

    public Color getGridColor() { return null == gridColor ? _gridColor : gridColor.get(); }
    public void setGridColor(final Color COLOR) {
        if (null == gridColor) {
            _gridColor = COLOR;
            redraw();
        } else {
            gridColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> gridColorProperty() {
        if (null == gridColor) {
            gridColor = new ObjectPropertyBase<>(_gridColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BubbleGridChart.this; }
                @Override public String getName() { return "gridColor"; }
            };
            _gridColor = null;
        }
        return gridColor;
    }

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
            textColor = new ObjectPropertyBase<>(_textColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BubbleGridChart.this; }
                @Override public String getName() { return "textColor"; }
            };
            _textColor = null;
        }
        return textColor;
    }

    public boolean isAutoBubbleTextColor() { return null == autoBubbleTextColor ? _autoBubbleTextColor : autoBubbleTextColor.get(); }
    public void setAutoBubbleTextColor(final boolean AUTO) {
        if (null == autoBubbleTextColor) {
            _autoBubbleTextColor = AUTO;
            redraw();
        } else {
            autoBubbleTextColor.set(AUTO);
        }
    }
    public BooleanProperty autoBubbleTextColorProperty() {
        if (null == autoBubbleTextColor) {
            autoBubbleTextColor = new BooleanPropertyBase(_autoBubbleTextColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BubbleGridChart.this; }
                @Override public String getName() { return "autoBubbleTextColor"; }
            };
        }
        return autoBubbleTextColor;
    }

    public boolean getShowGrid() { return null == showGrid ? _showGrid : showGrid.get(); }
    public void setShowGrid(final boolean SHOW) {
        if (null == showGrid) {
            _showGrid = SHOW;
            redraw();
        } else {
            showGrid.set(SHOW);
        }
    }
    public BooleanProperty showGridProperty() {
        if (null == showGrid) {
            showGrid = new BooleanPropertyBase(_showGrid) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BubbleGridChart.this; }
                @Override public String getName() { return "gridVisible"; }
            };
        }
        return showGrid;
    }

    public boolean getShowValues() { return null == showValues ? _showValues : showValues.get(); }
    public void setShowValues(final boolean SHOW) {
        if (null == showValues) {
            _showValues = SHOW;
            redraw();
        } else {
            showValues.set(SHOW);
        }
    }
    public BooleanProperty showValuesProperty() {
        if (null == showValues) {
            showValues = new BooleanPropertyBase() {
                @Override protected void  invalidated() { redraw(); }
                @Override public Object getBean() { return BubbleGridChart.this; }
                @Override public String getName() { return "showValues"; }
            };
        }
        return showValues;
    }

    public boolean getShowPercentage() { return null == showPercentage ? _showPercentage : showPercentage.get(); }
    public void setShowPercentage(final boolean SHOW) {
        if (null == showPercentage) {
            _showPercentage = SHOW;
            redraw();
        } else {
            showPercentage.set(SHOW);
        }
    }
    public BooleanProperty showPercentageProperty() {
        if (null == showPercentage) {
            showPercentage = new BooleanPropertyBase(_showPercentage) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BubbleGridChart.this; }
                @Override public String getName() { return "showPercentage"; }
            };
        }
        return showPercentage;
    }

    public List<BubbleGridChartItem> getItems() { return items; }
    public void setItems(final BubbleGridChartItem... ITEMS) { setItems(Arrays.asList(ITEMS)); }
    public void setItems(final List<BubbleGridChartItem> ITEMS) {
        items.setAll(ITEMS);
    }
    public void addItem(final BubbleGridChartItem ITEM) {
        if (!items.contains(ITEM)) { items.add(ITEM); }
    }
    public void removeItem(final BubbleGridChartItem ITEM) { if (items.contains(ITEM)) { items.remove(ITEM); } }

    public void useXCategoryFill() {
        useXCategoryFill = true;
    }
    public void useYCategoryFill() {
        useXCategoryFill = false;
    }

    public boolean getUseGradientFill() { return null == useGradientFill ? _useGradientFill : useGradientFill.get(); }
    public void setUseGradientFill(final boolean USE) {
        if (null == useGradientFill) {
            _useGradientFill = USE;
            redraw();
        } else {
            useGradientFill.set(USE);
        }
    }
    public BooleanProperty useGradientFillProperty() {
        if (null == useGradientFill) {
            useGradientFill = new BooleanPropertyBase(_useGradientFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BubbleGridChart.this; }
                @Override public String getName() { return "useGradientFill"; }
            };
        }
        return useGradientFill;
    }

    public Color getMinColor() { return null == minColor ? _minColor : minColor.get(); }
    public void setMinColor(final Color MIN_COLOR) {
        if (null == minColor) {
            _minColor = MIN_COLOR;
            gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, _minColor), new Stop(1, getMaxColor()));
            redraw();
        } else {
            minColor.set(MIN_COLOR);
        }
    }
    public ObjectProperty<Color> minColorProperty() {
        if (null == minColor) {
            minColor = new ObjectPropertyBase<>(_minColor) {
                @Override protected void invalidated() {
                    gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, get()), new Stop(1, getMaxColor()));
                    redraw();
                }
                @Override public Object getBean() { return BubbleGridChart.this; }
                @Override public String getName() { return "minColor"; }
            };
            _minColor = null;
        }
        return minColor;
    }

    public Color getMaxColor() { return null == maxColor ? _maxColor : maxColor.get(); }
    public void setMaxColor(final Color MAX_COLOR) {
        if (null == maxColor) {
            _maxColor = MAX_COLOR;
            gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, getMinColor()), new Stop(1, _minColor));
            redraw();
        } else {
            maxColor.set(MAX_COLOR);
        }
    }
    public ObjectProperty<Color> maxColorProperty() {
        if (null == maxColor) {
            maxColor = new ObjectPropertyBase<>(_maxColor) {
                @Override protected void invalidated() {
                    gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, getMinColor()), new Stop(1, get()));
                    redraw();
                }
                @Override public Object getBean() { return BubbleGridChart.this; }
                @Override public String getName() { return "maxColor"; }
            };
            _maxColor = null;
        }
        return maxColor;
    }

    public void setGradient(final LinearGradient gradient) {
        this.gradient = gradient;
        redraw();
    }

    public Topic getSortTopicX() { return sortTopicX; }
    public void setSortTopicX(final Topic TOPIC) { sortCategoryX(TOPIC, getSortOrderX()); }

    public Topic getSortTopicY() { return sortTopicY; }
    public void setSortTopicY(final Topic TOPIC) { sortCategoryY(TOPIC, getSortOrderY()); }

    public Order getSortOrderX() { return sortOrderX; }
    public void setSortOrderX(final Order ORDER) { sortCategoryX(getSortTopicX(), ORDER); }

    public Order getSortOrderY() { return sortOrderY; }
    public void setSortOrderY(final Order ORDER) { sortCategoryY(getSortTopicY(), ORDER); }

    public void sortCategoryX(final Topic TOPIC, final Order ORDER) {
        sortTopicX = TOPIC;
        sortOrderX = ORDER;
        switch(TOPIC) {
            case INDEX:
                switch (ORDER) {
                    case ASCENDING : Collections.sort(xCategoryItems, Comparator.comparing(ChartItem::getIndex)); break;
                    case DESCENDING: Collections.sort(xCategoryItems, Comparator.comparing(ChartItem::getIndex).reversed()); break;
                }
                break;
            case VALUE:
                switch (ORDER) {
                    case ASCENDING:
                        final Map<ChartItem, Double> sortedByValueAscending = sumsOfXCategoryItems.entrySet()
                                                                                                  .stream()
                                                                                                  .sorted(Map.Entry.comparingByValue())
                                                                                                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) ->e1, LinkedHashMap::new));
                        xCategoryItems.clear();
                        xCategoryItems.addAll(sortedByValueAscending.keySet());
                        break;
                    case DESCENDING:
                        final Map<ChartItem, Double> sortedByValueDescending = sumsOfXCategoryItems.entrySet()
                                                                                                   .stream()
                                                                                                   .sorted(Map.Entry.<ChartItem,Double>comparingByValue().reversed())
                                                                                                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) ->e1, LinkedHashMap::new));
                        xCategoryItems.clear();
                        xCategoryItems.addAll(sortedByValueDescending.keySet());
                        break;
                }
                break;
            case NAME:
                switch (ORDER) {
                    case ASCENDING:
                        List<ChartItem> sortedAscending = xCategoryItems.stream().sorted(Comparator.comparing(ChartItem::getName)).collect(Collectors.toList());
                        xCategoryItems.clear();
                        xCategoryItems.addAll(sortedAscending);
                        break;
                    case DESCENDING:
                        List<ChartItem> sortedDescending = xCategoryItems.stream().sorted(Comparator.comparing(ChartItem::getName).reversed()).collect(Collectors.toList());
                        System.out.println(sortedDescending);
                        xCategoryItems.clear();
                        xCategoryItems.addAll(sortedDescending);
                        break;
                }
                break;
        }
        redraw();
    }
    public void sortCategoryY(final Topic TOPIC, final Order ORDER) {
        sortTopicY = TOPIC;
        sortOrderY = ORDER;
        switch(TOPIC) {
            case INDEX:
                switch (ORDER) {
                    case ASCENDING : Collections.sort(yCategoryItems, Comparator.comparing(ChartItem::getIndex).reversed()); break;
                    case DESCENDING: Collections.sort(yCategoryItems, Comparator.comparing(ChartItem::getIndex)); break;
                }
                break;
            case VALUE:
                switch (ORDER) {
                    case ASCENDING:
                        final Map<ChartItem, Double> sortedByValueAscending = sumsOfYCategoryItems.entrySet()
                                                                                                  .stream()
                                                                                                  .sorted(Map.Entry.<ChartItem,Double>comparingByValue().reversed())
                                                                                                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                        yCategoryItems.clear();
                        yCategoryItems.addAll(sortedByValueAscending.keySet());
                        break;
                    case DESCENDING:
                        final Map<ChartItem, Double> sortedByValueDescending = sumsOfYCategoryItems.entrySet()
                                                                                                   .stream()
                                                                                                   .sorted(Map.Entry.comparingByValue())
                                                                                                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                        yCategoryItems.clear();
                        yCategoryItems.addAll(sortedByValueDescending.keySet());
                        break;
                }
                break;
            case NAME:
                switch (ORDER) {
                    case ASCENDING:
                        List<ChartItem> sortedAscending = yCategoryItems.stream().sorted(Comparator.comparing(ChartItem::getName)).collect(Collectors.toList());
                        yCategoryItems.clear();
                        yCategoryItems.addAll(sortedAscending);
                        break;
                    case DESCENDING:
                        List<ChartItem> sortedDescending = yCategoryItems.stream().sorted(Comparator.comparing(ChartItem::getName).reversed()).collect(Collectors.toList());
                        yCategoryItems.clear();
                        yCategoryItems.addAll(sortedDescending);
                        break;
                }
                break;
        }
        redraw();
    }

    private void sort() {
        sortCategoryX(getSortTopicX(), getSortOrderX());
        sortCategoryY(getSortTopicY(), getSortOrderY());
    }

    private static <T> Predicate<T> distinctByName(Function<? super T, ?> nameExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(nameExtractor.apply(t));
    }

    /**
     * Overrideable drawChart() method
     */
    protected void drawChart() {
        bubbles.clear();

        ctx.clearRect(0, 0, width, height);

        double noOfXCategoryItems = xCategoryItems.size();
        double noOfYCategoryItems = yCategoryItems.size();

        double yCategoryWidth    = width * 0.14;
        double xCategoryHeight   = height * 0.08333333;
        double maxYCategoryWidth = width * 0.12;
        double dataWidth         = width - yCategoryWidth;
        double dataHeight        = height - xCategoryHeight;
        double stepX             = 0 == noOfXCategoryItems ? dataWidth  : dataWidth  / noOfXCategoryItems;
        double stepY             = 0 == noOfYCategoryItems ? dataHeight : dataHeight / noOfYCategoryItems;
        double fontsize          = height * 0.024;
        double dataFontSize      = height * 0.021;
        double valueFontSize     = height * 0.019;
        double maxBubbleDiameter = (stepX < stepY ? stepX : stepY) * 0.95;
        double maxBubbleRadius   = maxBubbleDiameter * 0.5;
        double maxBubbleArea     = Math.PI * maxBubbleRadius * maxBubbleRadius;
        double factor            = maxBubbleArea / maxValue;
        Font   chartFont         = Fonts.latoLight(fontsize);
        Font   dataFont          = Fonts.latoRegular(dataFontSize);
        Font   valueFont         = Fonts.latoLight(valueFontSize);
        Font   sumFont           = Fonts.latoRegular(fontsize);

        ctx.setFill(getChartBackground());
        ctx.fillRect(0, 0, width, height);

        ctx.setTextBaseline(VPos.CENTER);

        // Grid
        if (getShowGrid()) {
            for (int x = 0 ; x < noOfXCategoryItems ; x++) {
                double cellCenterX = yCategoryWidth + x * stepX + stepX * 0.5;
                for (int y = 0 ; y < noOfYCategoryItems ; y++) {
                    double cellCenterY = height - xCategoryHeight - y * stepY - stepY * 0.5;
                    ctx.setStroke(getGridColor());
                    ctx.setLineDashes(4, 2);
                    ctx.strokeLine(yCategoryWidth, cellCenterY, width, cellCenterY);
                    ctx.strokeLine(cellCenterX, height - xCategoryHeight, cellCenterX, 0);
                }
            }
        }

        // Chart
        for (int x = 0 ; x < noOfXCategoryItems ; x++) {
            double cellCenterX = yCategoryWidth + x * stepX + stepX * 0.5;

            // x category text
            ChartItem xItem = xCategoryItems.get(x);
            ctx.setTextAlign(TextAlignment.CENTER);
            ctx.setFill(getTextColor());
            if (getShowValues() | getShowPercentage()) {
                ctx.setFont(chartFont);
                ctx.fillText(xItem.getName(), cellCenterX, height - xCategoryHeight * 0.68, stepX);
                ctx.setFont(valueFont);
                if (getShowValues() && !getShowPercentage()) {
                    ctx.fillText("(" + String.format(Locale.US, "%.0f", sumsOfXCategoryItems.get(xItem)) + ")", cellCenterX, height - xCategoryHeight * 0.3, stepX);
                } else if (!getShowValues() && getShowPercentage()) {
                    ctx.fillText("(" + String.format(Locale.US, "%.0f%%", sumsOfXCategoryItems.get(xItem) / sumOfValues * 100) + ")", cellCenterX, height - xCategoryHeight * 0.3, stepX);
                } else {
                    ctx.fillText("(" + String.join("/", String.format(Locale.US, "%.0f", sumsOfXCategoryItems.get(xItem)), String.format(Locale.US, "%.0f%%", sumsOfXCategoryItems.get(xItem) / sumOfValues * 100)) + ")", cellCenterX, height - xCategoryHeight * 0.3, stepX);
                }
            } else {
                ctx.setFont(chartFont);
                ctx.fillText(xItem.getName(), cellCenterX, height - xCategoryHeight * 0.5, stepX);
            }

            for (int y = 0 ; y < noOfYCategoryItems ; y++) {
                ctx.setFont(chartFont);
                double cellCenterY = height - xCategoryHeight - y * stepY - stepY * 0.5;

                // y category text
                ChartItem yItem = yCategoryItems.get(y);
                if (x == 0) {
                    ctx.setTextAlign(TextAlignment.CENTER);
                    ctx.setFill(getTextColor());
                    if (getShowValues() | getShowPercentage()) {
                        ctx.setFont(chartFont);
                        ctx.fillText(yItem.getName(), yCategoryWidth * 0.5, cellCenterY - stepY * 0.18, maxYCategoryWidth);
                        ctx.setFont(valueFont);
                        if (getShowValues() && !getShowPercentage()) {
                            ctx.fillText("(" + String.format(Locale.US, "%.0f", sumsOfYCategoryItems.get(yItem)) + ")", yCategoryWidth * 0.5, cellCenterY + stepY * 0.18, maxYCategoryWidth);
                        } else if (!getShowValues() && getShowPercentage()) {
                            ctx.fillText("(" + String.format(Locale.US, "%.0f%%", sumsOfYCategoryItems.get(yItem) / sumOfValues * 100) + ")", yCategoryWidth * 0.5, cellCenterY + stepY * 0.18, maxYCategoryWidth);
                        } else {
                            ctx.fillText("(" + String.join("/", String.format(Locale.US, "%.0f", sumsOfYCategoryItems.get(yItem)), String.format(Locale.US, "%.0f%%", sumsOfYCategoryItems.get(yItem) / sumOfValues * 100)) + ")", yCategoryWidth * 0.5, cellCenterY + stepY * 0.18, maxYCategoryWidth);
                        }
                    } else {
                        ctx.fillText(yItem.getName(), yCategoryWidth * 0.5, cellCenterY, maxYCategoryWidth);
                    }
                }

                Optional<BubbleGridChartItem> item = items.stream()
                                                          .filter(ci -> ci.getCategoryY().equals(yItem))
                                                          .filter(ci -> ci.getCategoryX().equals(xItem))
                                                          .findFirst();
                if (item.isPresent()) {
                    final BubbleGridChartItem bgci           = item.get();
                    final double              bubbleArea     = bgci.getValue() * factor;
                    final double              radius         = Math.sqrt(bubbleArea / Math.PI);
                    final double              diameter       = radius * 2.0;
                    Color                     fill           = useXCategoryFill ? xItem.getFill() : yItem.getFill();
                    if (getUseGradientFill()) {
                        fill = Helper.getColorAt(gradient, bgci.getValue() / (maxValue - minValue));
                    }

                    bubbles.add(new Bubble(cellCenterX, cellCenterY, radius, bgci));
                    ctx.setFill(fill);
                    ctx.fillOval(cellCenterX - radius, cellCenterY - radius, diameter, diameter);

                    if (diameter > dataFontSize * 1.5 && dataFontSize > 7 && getShowValues()) {
                        ctx.setFont(dataFont);
                        ctx.setTextAlign(TextAlignment.CENTER);
                        if (isAutoBubbleTextColor()) {
                            ctx.setFill(Helper.isDark(fill) ? Color.WHITE : Color.BLACK);
                        } else {
                            ctx.setFill(getTextColor());
                        }
                        String     bubbleText = String.format(Locale.US, "%.0f", bgci.getValue());
                        FontMetrix metrix     = new FontMetrix(dataFont);
                        metrix.computeStringWidth(bubbleText);
                        if (metrix.computeStringWidth(bubbleText) < (radius * 2)) {
                            ctx.fillText(bubbleText, cellCenterX, cellCenterY, maxBubbleDiameter);
                        }
                    }
                }
            }
        }

        if (getShowValues()) {
            ctx.setFill(getTextColor());
            ctx.setFont(sumFont);
            ctx.fillText("Sum\n" + String.format(Locale.US, "%.0f", sumOfValues), yCategoryWidth * 0.5, height - xCategoryHeight * 0.5, yCategoryWidth);
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (width > 0 && height > 0) {
            canvas.setWidth(width);
            canvas.setHeight(height);
            canvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            redraw();
        }
    }

    /**
     * Overrideable redraw()
     */
    public void redraw() {
        drawChart();
    }


    // ******************** Internal Classes **********************************
    private static final class Bubble {
        private final double x;
        private final double              y;
        private final double              r;
        private final BubbleGridChartItem item;


        private Bubble(double x, double y, double r, BubbleGridChartItem item) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.item = item;
        }


        public double x() { return x; }

        public double y() { return y; }

        public double r() { return r; }

        public BubbleGridChartItem item() { return item; }

        @Override public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Bubble) obj;
            return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(that.x) && Double.doubleToLongBits(this.y) == Double.doubleToLongBits(that.y) &&
                   Double.doubleToLongBits(this.r) == Double.doubleToLongBits(that.r) && Objects.equals(this.item, that.item);
        }

        @Override public int hashCode() {
            return Objects.hash(x, y, r, item);
        }

        @Override public String toString() {
            return "Bubble[" + "x=" + x + ", " + "y=" + y + ", " + "r=" + r + ", " + "item=" + item + ']';
        }
    }
}