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

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.event.SelectionEvent;
import eu.hansolo.fx.charts.event.SelectionEventListener;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.InfoPopup;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static eu.hansolo.fx.charts.tools.Helper.clamp;


public class SectorChart extends Region {
    private static final int                                            MIN_NO_OF_SECTORS = 4;
    private static final int                                            MAX_NO_OF_SECTORS = 128;
    private static final double                                         PREFERRED_WIDTH   = 250;
    private static final double                                         PREFERRED_HEIGHT  = 250;
    private static final double                                         MINIMUM_WIDTH     = 10;
    private static final double                                         MINIMUM_HEIGHT    = 10;
    private static final double                                         MAXIMUM_WIDTH     = 1024;
    private static final double                                         MAXIMUM_HEIGHT    = 1024;
    private              double                                         size;
    private              double                                         centerX;
    private              double                                         centerY;
    private              Pane                                           pane;
    private              Canvas                                         canvas;
    private              GraphicsContext                                ctx;
    private              int                                            _decimals;
    private              IntegerProperty                                decimals;
    private              String                                         formatString;
    private              double                                         angleStep;
    private              ObservableList<ChartItemSeries<ChartItem>>     allSeries;
    private              Map<Sector, ChartItem>                         sectorMap;
    private              double                                         originalThreshold;
    private              double                                         _threshold;
    private              DoubleProperty                                 threshold;
    private              Color                                          _thresholdColor;
    private              ObjectProperty<Color>                          thresholdColor;
    private              boolean                                        _thresholdVisible;
    private              BooleanProperty                                thresholdVisible;
    private              boolean                                        _itemTextVisible;
    private              BooleanProperty                                itemTextVisible;
    private              boolean                                        _seriesTextVisible;
    private              BooleanProperty                                seriesTextVisible;
    private              boolean                                        _seriesSumTextVisible;
    private              BooleanProperty                                seriesSumTextVisible;
    private              Color                                          _gridColor;
    private              ObjectProperty<Color>                          gridColor;
    private              CopyOnWriteArrayList<SelectionEventListener>   listeners;
    private              InfoPopup                                      popup;
    private              InvalidationListener                           resizeListener;
    private              ListChangeListener<ChartItemSeries<ChartItem>> seriesListener;
    private              ListChangeListener<ChartItem>                  itemListListener;
    private              ItemEventListener                              itemEventListener;
    private              EventHandler<MouseEvent>                       mouseHandler;


    // ******************** Constructors **************************************
    public SectorChart() { this(null); }
    public SectorChart(final List<ChartItemSeries<ChartItem>> ALL_SERIES) {
        centerX               = PREFERRED_WIDTH * 0.5;
        centerY               = PREFERRED_HEIGHT * 0.5;
        originalThreshold     = 100;
        _threshold            = 100;
        _thresholdVisible     = false;
        _itemTextVisible      = true;
        _seriesTextVisible    = true;
        _seriesSumTextVisible = true;
        _decimals             = 0;
        formatString          = new StringBuilder("%.").append(_decimals).append("f").toString();
        allSeries             = null == ALL_SERIES ? FXCollections.observableArrayList() : FXCollections.observableArrayList(ALL_SERIES);
        sectorMap             = new HashMap<>();
        _gridColor            = Color.WHITE;
        _thresholdColor       = Color.RED;
        listeners             = new CopyOnWriteArrayList<>();
        resizeListener        = o -> resize();
        seriesListener        = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(series -> {
                        series.getItems().forEach(item -> item.addItemEventListener(itemEventListener));
                        series.getItems().addListener(itemListListener);
                    });
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(series -> {
                        series.getItems().forEach(item -> item.removeItemEventListener(itemEventListener));
                        series.getItems().removeListener(itemListListener);
                    });
                }
            }
            angleStep = 360.0 / getNoOfSectors();
            redraw();
        };
        itemListListener      = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(item -> item.addItemEventListener(itemEventListener));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(item -> item.removeItemEventListener(itemEventListener));
                }
            }
        };
        itemEventListener     = e -> redraw();
        mouseHandler          = e -> {
            Optional<Entry<Sector, ChartItem>> optionalSector = sectorMap.entrySet()
                                                                         .parallelStream()
                                                                         .filter(entry -> Helper.isInSector(e.getX(), e.getY(), centerX,centerY, entry.getKey().radius, entry.getKey().startAngle, entry.getKey().segmentAngle))
                                                                         .findFirst();
            if (optionalSector.isPresent()) {
                popup.setX(e.getScreenX());
                popup.setY(e.getScreenY() - popup.getHeight());
                popup.update(optionalSector.get().getValue());
                popup.animatedShow(getScene().getWindow());
            }
        };

        if (null == ALL_SERIES || ALL_SERIES.isEmpty()) {
            int noOfSectorsPerSeries = MAX_NO_OF_SECTORS / 4;
            for (int i = 0; i < 4 ; i++) {
                ChartItemSeries<ChartItem> series = new ChartItemSeries<>();
                for (int j = 0 ; j < noOfSectorsPerSeries ; j++) {
                    series.getItems().add(new ChartItem(0d));
                }
                addSeries(series);
            }
        } else {
            allSeries.forEach(series -> {
                series.getItems().forEach(item -> item.addItemEventListener(itemEventListener));
                series.getItems().addListener(itemListListener);
            });
        }
        angleStep             = 360.0 / getNoOfSectors();

        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initAllSeries(final List<ChartItemSeries<ChartItem>> ALL_SERIES) {
        if (null == ALL_SERIES || ALL_SERIES.isEmpty()) {
            int noOfSectorsPerSeries = MAX_NO_OF_SECTORS / 4;
            for (int i = 0; i < 4 ; i++) {
                ChartItemSeries<ChartItem> series = new ChartItemSeries<>();
                for (int j = 0 ; j < noOfSectorsPerSeries ; j++) {
                    series.getItems().add(new ChartItem(0d));
                }
                addSeries(series);
            }
        } else {
            allSeries.setAll(ALL_SERIES);
        }
    }

    private void init() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if (Double.compare(getMinWidth(), 0.0) <= 0 || Double.compare(getMinHeight(), 0.0) <= 0) {
            setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if (Double.compare(getMaxWidth(), 0.0) <= 0 || Double.compare(getMaxHeight(), 0.0) <= 0) {
            setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics() {
        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx = canvas.getGraphicsContext2D();
        
        popup = new InfoPopup();

        // Add all nodes
        pane = new Pane(canvas);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(resizeListener);
        heightProperty().addListener(resizeListener);
        allSeries.addListener(seriesListener);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
    }

    public void dispose() {
        widthProperty().removeListener(resizeListener);
        heightProperty().removeListener(resizeListener);
        allSeries.removeListener(seriesListener);
        allSeries.forEach(series -> {
            series.getItems().forEach(item -> item.removeItemEventListener(itemEventListener));
            series.getItems().removeListener(itemListListener);
        });
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
    }


    // ******************** Methods *******************************************
    public double getMinValue() {
        return allSeries.isEmpty() ? 0 : allSeries.stream().min(Comparator.comparingDouble(ChartItemSeries::getMinValue)).map(ChartItemSeries::getMinValue).orElse(0d);
    }

    public double getMaxValue() {
        return allSeries.isEmpty() ? 100 : allSeries.stream().max(Comparator.comparingDouble(ChartItemSeries::getMaxValue)).map(ChartItemSeries::getMaxValue).orElse(100d);
    }

    public double getRange() {
        return getMaxValue() - getMinValue();
    }

    public int getDecimals() { return null == decimals ? _decimals : decimals.get(); }
    public void setDecimals(final int DECIMALS) {
        if (null == decimals) {
            _decimals    = DECIMALS;
            formatString = new StringBuilder("%.").append(_decimals).append("f").toString();
            redraw();
        } else {
            decimals.set(DECIMALS);
        }
    }
    public IntegerProperty decimalsProperty() {
        if (null == decimals) {
            decimals = new IntegerPropertyBase(_decimals) {
                @Override protected void invalidated() {
                    formatString = new StringBuilder("%.").append(get()).append("f").toString();
                    redraw();
                }
                @Override public Object getBean() { return SectorChart.this; }
                @Override public String getName() { return "decimals"; }
            };
        }
        return decimals;
    }

    public double getThreshold() { return null == threshold ? _threshold : threshold.get(); }
    public void setThreshold(final double VALUE) {
        originalThreshold = VALUE;
        if (null == threshold) {
            if (allSeries.isEmpty()) {
                _threshold = VALUE;
            } else {
                _threshold = clamp(getMinValue(), getMaxValue(), VALUE);
            }
            drawChart();
        } else {
            threshold.set(VALUE);
        }
    }
    public DoubleProperty thresholdProperty() {
        if (null == threshold) {
            threshold = new DoublePropertyBase(_threshold) {
                @Override protected void invalidated() {
                    if (!allSeries.isEmpty()) {
                        originalThreshold = get();
                        set(clamp(getMinValue(), getMaxValue(), get()));
                    }
                    drawChart();
                }
                @Override public Object getBean() { return SectorChart.this; }
                @Override public String getName() { return "threshold"; }
            };
        }
        return threshold;
    }

    public int getNoOfSectors() { return allSeries.isEmpty() ? 0 : allSeries.stream().mapToInt(l -> l.getItems().size()).sum(); }

    public boolean isThresholdVisible() { return null == thresholdVisible ? _thresholdVisible : thresholdVisible.get(); }
    public void setThresholdVisible(final boolean VISIBLE) {
        if (null == thresholdVisible) {
            _thresholdVisible = VISIBLE;
            redraw();
        } else {
            thresholdVisible.set(VISIBLE);
        }
    }
    public BooleanProperty thresholdVisibleProperty() {
        if (null == thresholdVisible) {
            thresholdVisible = new BooleanPropertyBase(_thresholdVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return SectorChart.this; }
                @Override public String getName() { return "thresholdVisible"; }
            };
        }
        return thresholdVisible;
    }

    public boolean getItemTextVisible() { return null == itemTextVisible ? _itemTextVisible : itemTextVisible.get(); }
    public void setItemTextVisible(final boolean VISIBLE) {
        if (null == itemTextVisible) {
            _itemTextVisible = VISIBLE;
            redraw();
        } else {
            itemTextVisible.set(VISIBLE);
        }
    }
    public BooleanProperty itemTextVisibleProperty() {
        if (null == itemTextVisible) {
            itemTextVisible = new BooleanPropertyBase(_itemTextVisible) {
                @Override protected void invalidated() { redraw();}
                @Override public Object getBean() { return SectorChart.this; }
                @Override public String getName() { return "itemTextVisible"; }
            };
        }
        return itemTextVisible;
    }

    public boolean getSeriesTextVisible() { return null == seriesTextVisible ? _seriesTextVisible : seriesTextVisible.get(); }
    public void setSeriesTextVisible(final boolean VISIBLE) {
        if (null == seriesTextVisible) {
            _seriesTextVisible = VISIBLE;
            redraw();
        } else {
            seriesTextVisible.set(VISIBLE);
        }
    }
    public BooleanProperty seriesTextVisibleProperty() {
        if (null == seriesTextVisible) {
            seriesTextVisible = new BooleanPropertyBase(_seriesTextVisible) {
                @Override protected void invalidated() { redraw();}
                @Override public Object getBean() { return SectorChart.this; }
                @Override public String getName() { return "seriesTextVisible"; }
            };
        }
        return seriesTextVisible;
    }

    public boolean getSeriesSumTextVisible() { return null == seriesSumTextVisible ? _seriesSumTextVisible : seriesSumTextVisible.get(); }
    public void setSeriesSumTextVisible(final boolean VISIBLE) {
        if (null == seriesSumTextVisible) {
            _seriesSumTextVisible = VISIBLE;
            redraw();
        } else {
            seriesSumTextVisible.set(VISIBLE);
        }
    }
    public BooleanProperty seriesSumTextVisibleProperty() {
        if (null == seriesSumTextVisible) {
            seriesTextVisible = new BooleanPropertyBase(_seriesSumTextVisible) {
                @Override protected void invalidated() { redraw();}
                @Override public Object getBean() { return SectorChart.this; }
                @Override public String getName() { return "seriesSumTextVisible"; }
            };
        }
        return seriesSumTextVisible;
    }

    public ObservableList<ChartItemSeries<ChartItem>> getAllSeries() { return allSeries; }
    public void setAllSeries(final List<ChartItemSeries<ChartItem>> ALL_SERIES) {
        int noOfSectors = allSeries.stream().mapToInt(l -> l.getItems().size()).sum();
        if (noOfSectors < MIN_NO_OF_SECTORS) throw new IllegalArgumentException("Not enough sectors (min. " + MIN_NO_OF_SECTORS + "needed)");
        if (noOfSectors > MAX_NO_OF_SECTORS) throw new IllegalArgumentException("Too many sectors (max. " + MAX_NO_OF_SECTORS + " sectors allowed)");
        allSeries.setAll(ALL_SERIES);
        setThreshold(originalThreshold);
    }
    public void addSeries(final ChartItemSeries<ChartItem> SERIES) {
        int noOfSectors = allSeries.stream().mapToInt(l -> l.getItems().size()).sum();
        if (noOfSectors + SERIES.getItems().size() > MAX_NO_OF_SECTORS) throw new IllegalArgumentException("Too many sectors (max. " + getNoOfSectors() + " sectors allowed)");
        allSeries.add(SERIES);
        setThreshold(originalThreshold);
    }

    public void reset() {
        allSeries.clear();
        initAllSeries(allSeries);
    }

    public Color getGridColor() { return null == gridColor ? _gridColor : gridColor.getValue(); }
    public void setGridColor(Color COLOR) {
        if (null == gridColor) {
            _gridColor = COLOR;
            redraw();
        } else {
            gridColor.setValue(COLOR);
        }
    }
    public ObjectProperty<Color> gridColorProperty() {
        if (null == gridColor) {
            gridColor  = new ObjectPropertyBase<Color>(_gridColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return SectorChart.this; }
                @Override public String getName() { return "gridColor"; }
            };
            _gridColor = null;
        }
        return gridColor;
    }

    public Color getThresholdColor() { return null == thresholdColor ? _thresholdColor : thresholdColor.getValue(); }
    public void setThresholdColor(final Color COLOR) {
        if (null == thresholdColor) {
            _thresholdColor = COLOR;
            redraw();
        } else {
            thresholdColor.setValue(COLOR);
        }
    }
    public ObjectProperty<Color> thresholdColorProperty() {
        if (null == thresholdColor) {
            thresholdColor  = new ObjectPropertyBase<Color>(_thresholdColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return SectorChart.this; }
                @Override public String getName() { return "thresholdColor"; }
            };
            _thresholdColor = null;
        }
        return thresholdColor;
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() {
        return SectorChart.class.getResource("chart.css").toExternalForm();
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
    private void resize() {
        double width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size          = width < height ? width : height;

        if (size > 0) {
            centerX = getInsets().getLeft() + size * 0.5;
            centerY = getInsets().getTop() + size * 0.5;

            pane.setMaxSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            canvas.setWidth(size);
            canvas.setHeight(size);

            redraw();
        }
    }

    public void redraw() {
        canvas.setCache(false);
        drawChart();
        canvas.setCache(true);
        canvas.setCacheHint(CacheHint.QUALITY);
    }

    private void drawChart() {
        if (null == ctx) { return; }
        final double CENTER_X      = 0.5 * size;
        final double CENTER_Y      = CENTER_X;
        final double CIRCLE_SIZE   = 0.95 * size;
        final double CIRCLE_RADIUS = 0.475 * CIRCLE_SIZE;
        final double MIN_VALUE     = getMinValue();
        final double MAX_VALUE     = getMaxValue();
        final double DATA_RANGE    = MAX_VALUE - MIN_VALUE;
        final int    NO_OF_SECTORS = getNoOfSectors();

        // clear the canvas
        ctx.clearRect(0, 0, size, size);

        // draw the chart data
        ctx.save();

        ctx.translate(CENTER_X, CENTER_Y);
        ctx.rotate(-90);
        ctx.translate(-CENTER_X, -CENTER_Y);

        double radiusFactor;
        double radius;

        // draw series sectors
        ctx.save();
        for (int i = 0 ; i < allSeries.size() ; i++) {
            ChartItemSeries<ChartItem> series = allSeries.get(i);
            ctx.beginPath();
            ctx.moveTo(CENTER_X, CENTER_Y);
            ctx.arc(CENTER_X, CENTER_Y, CIRCLE_RADIUS, CIRCLE_RADIUS, 0, -angleStep * series.getItems().size());
            ctx.closePath();
            ctx.setFill(series.getFill());
            ctx.fill();
            ctx.translate(CENTER_X, CENTER_Y);
            ctx.rotate(angleStep * series.getItems().size());
            ctx.translate(-CENTER_X, -CENTER_Y);
        }
        ctx.restore();

        // draw item sectors
        double currentAngle = 0;
        for (int i = 0 ; i < allSeries.size() ; i++) {
            ChartItemSeries<ChartItem> series = allSeries.get(i);
            for (int j = 0 ; j < series.getItems().size() ; j++) {
                ChartItem item = series.getItems().get(j);
                radiusFactor = clamp(MIN_VALUE, MAX_VALUE, (item.getValue() - MIN_VALUE)) / DATA_RANGE;
                radius = clamp(0, CIRCLE_RADIUS, radiusFactor * CIRCLE_RADIUS);
                ctx.beginPath();
                ctx.moveTo(CENTER_X, CENTER_Y);
                ctx.arc(CENTER_X, CENTER_Y, radius, radius, 0, -angleStep);
                ctx.closePath();
                ctx.setFill(item.getFill());
                ctx.fill();
                ctx.translate(CENTER_X, CENTER_Y);
                ctx.rotate(angleStep);
                ctx.translate(-CENTER_X, -CENTER_Y);
                sectorMap.put(new Sector(centerX, centerY, radius, currentAngle, angleStep), item);
                currentAngle += angleStep;
            }
        }
        ctx.restore();
        
        ctx.setLineWidth(0.75);
        ctx.setStroke(getGridColor());

        // draw star lines
        ctx.save();
        for (int i = 0 ; i < NO_OF_SECTORS ; i++) {
            ctx.strokeLine(CENTER_X, CENTER_Y, CENTER_X, CENTER_Y - CIRCLE_RADIUS);
            ctx.translate(CENTER_X, CENTER_Y);
            ctx.rotate(angleStep);
            ctx.translate(-CENTER_X, -CENTER_Y);
        }
        ctx.restore();

        // draw threshold line
        if (isThresholdVisible()) {
            ctx.save();
            radiusFactor = (clamp(MIN_VALUE, MAX_VALUE, (getThreshold() - MIN_VALUE)) / DATA_RANGE);
            radius       = clamp(0, CIRCLE_RADIUS, radiusFactor * CIRCLE_RADIUS);
            ctx.setLineWidth(clamp(0.75d, 1d, size * 0.005));
            ctx.setLineDashes(new double[] {6, 3});
            ctx.setStroke(getThresholdColor());
            ctx.strokeOval(0.5 * size - radius, 0.5 * size - radius, 2 * radius, 2 * radius);
            ctx.restore();
        }

        // prerotate
        ctx.save();

        ctx.translate(CENTER_X, CENTER_Y);
        ctx.rotate(angleStep * 0.5);
        ctx.translate(-CENTER_X, -CENTER_Y);

        // draw item text
        if (getItemTextVisible()) {
            ctx.save();
            ctx.setFont(Fonts.latoRegular(0.015 * size));
            ctx.setTextAlign(TextAlignment.CENTER);
            ctx.setTextBaseline(VPos.CENTER);

            currentAngle = 0;
            for (int i = 0; i < allSeries.size(); i++) {
                ChartItemSeries<ChartItem> series = allSeries.get(i);
                for (int j = 0; j < series.getItems().size(); j++) {
                    ChartItem item = series.getItems().get(j);
                    ctx.setFill(item.getTextFill());

                    ctx.save();
                    ctx.setTextAlign(currentAngle < 180 ? TextAlignment.RIGHT : TextAlignment.LEFT);
                    ctx.translate(CENTER_X, size * 0.06);
                    ctx.rotate(currentAngle < 180 ? 270 : 90);
                    ctx.translate(-CENTER_X, -size * 0.06);
                    ctx.fillText(item.getName(), CENTER_X, size * 0.06);
                    ctx.restore();

                    ctx.translate(CENTER_X, CENTER_Y);
                    ctx.rotate(angleStep);
                    ctx.translate(-CENTER_X, -CENTER_Y);
                    currentAngle += angleStep;
                }
            }
            ctx.restore();
        }

        // draw series text
        if (getSeriesTextVisible()) {
            ctx.save();
            ctx.setFont(Fonts.latoRegular(0.018 * size));
            ctx.setTextAlign(TextAlignment.CENTER);
            ctx.setTextBaseline(VPos.CENTER);

            boolean sumVisible = getSeriesSumTextVisible();

            currentAngle = 0;
            for (int i = 0; i < allSeries.size(); i++) {
                ChartItemSeries<ChartItem> series = allSeries.get(i);
                ctx.translate(CENTER_X, CENTER_Y);
                ctx.rotate(angleStep * series.getItems().size() * 0.5 - angleStep * 0.5);
                ctx.translate(-CENTER_X, -CENTER_Y);
                currentAngle += angleStep * series.getItems().size() * 0.5 - angleStep * 0.5;

                ctx.save();
                ctx.translate(CENTER_X, size * 0.035);
                ctx.rotate(currentAngle > 135 && currentAngle < 225 ? 180 : 0);
                ctx.translate(-CENTER_X, -size * 0.035);
                ctx.setFill(series.getTextFill());
                if (sumVisible) {
                    ctx.fillText(series.getName() + " (" + String.format(Locale.US, formatString, series.getSumOfAllItems()) + ")", CENTER_X, size * 0.035);
                } else {
                    ctx.fillText(series.getName(), CENTER_X, size * 0.035);
                }
                ctx.restore();

                ctx.translate(CENTER_X, CENTER_Y);
                ctx.rotate(angleStep * series.getItems().size() * 0.5 + angleStep * 0.5);
                ctx.translate(-CENTER_X, -CENTER_Y);

                currentAngle += angleStep * series.getItems().size() * 0.5 + angleStep * 0.5;
            }

            ctx.restore();
        }

        ctx.restore();
    }


    // ******************** Internal Classes **********************************
    private static final class Sector {
        private final double centerX;
        private final double centerY;
        private final double radius;
        private final double startAngle;
        private final double segmentAngle;


        private Sector(final double centerX, final double centerY, final double radius, final double startAngle, final double segmentAngle) {
            this.centerX      = centerX;
            this.centerY      = centerY;
            this.radius       = radius;
            this.startAngle   = startAngle;
            this.segmentAngle = segmentAngle;
        }


        public double centerX() { return centerX; }

        public double centerY() { return centerY; }

        public double radius() { return radius; }

        public double startAngle() { return startAngle; }

        public double segmentAngle() { return segmentAngle; }

        @Override public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Sector) obj;
            return Double.doubleToLongBits(this.centerX) == Double.doubleToLongBits(that.centerX) &&
                   Double.doubleToLongBits(this.centerY) == Double.doubleToLongBits(that.centerY) && Double.doubleToLongBits(this.radius) == Double.doubleToLongBits(that.radius) &&
                   Double.doubleToLongBits(this.startAngle) == Double.doubleToLongBits(that.startAngle) &&
                   Double.doubleToLongBits(this.segmentAngle) == Double.doubleToLongBits(that.segmentAngle);
        }

        @Override public int hashCode() {
            return Objects.hash(centerX, centerY, radius, startAngle, segmentAngle);
        }

        @Override public String toString() {
            return "Sector[" + "centerX=" + centerX + ", " + "centerY=" + centerY + ", " + "radius=" + radius + ", " + "startAngle=" + startAngle + ", " + "segmentAngle=" +
                   segmentAngle + ']';
        }
    }
}
