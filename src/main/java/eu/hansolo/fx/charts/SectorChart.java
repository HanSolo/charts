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
import eu.hansolo.fx.charts.event.ChartEvt;
import eu.hansolo.fx.charts.event.SelectionEvt;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolboxfx.font.Fonts;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.InfoPopup;
import javafx.beans.DefaultProperty;
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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
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
    private              boolean                                        _seriesBackgroundVisible;
    private              BooleanProperty                                seriesBackgroundVisible;
    private              boolean                                        _radialBarChartMode;
    private              BooleanProperty                                radialBarChartMode;
    private              Color                                          _gridColor;
    private              ObjectProperty<Color>                          gridColor;
    private              Map<EvtType, List<EvtObserver<ChartEvt>>>      observers;
    private              InfoPopup                                      popup;
    private              InvalidationListener                           resizeListener;
    private              ListChangeListener<ChartItemSeries<ChartItem>> seriesListener;
    private              ListChangeListener<ChartItem>                  itemListListener;
    private              EvtObserver<ChartEvt>                          itemObserver;
    private              EventHandler<MouseEvent>                       mouseHandler;

    private record Sector(double centerX, double centerY, double radius, double startAngle, double segmentAngle) {}


    // ******************** Constructors **************************************
    public SectorChart() { this(null); }
    public SectorChart(final List<ChartItemSeries<ChartItem>> ALL_SERIES) {
        centerX                  = PREFERRED_WIDTH * 0.5;
        centerY                  = PREFERRED_HEIGHT * 0.5;
        originalThreshold        = 100;
        _threshold               = 100;
        _thresholdVisible        = false;
        _itemTextVisible         = true;
        _seriesTextVisible       = true;
        _seriesSumTextVisible    = true;
        _seriesBackgroundVisible = true;
        _radialBarChartMode      = false;
        _decimals                = 0;
        formatString             = new StringBuilder("%.").append(_decimals).append("f").toString();
        allSeries                = null == ALL_SERIES ? FXCollections.observableArrayList() : FXCollections.observableArrayList(ALL_SERIES);
        sectorMap                = new HashMap<>();
        _gridColor               = Color.WHITE;
        _thresholdColor          = Color.RED;
        observers                = new ConcurrentHashMap<>();
        resizeListener           = o -> resize();
        seriesListener           = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(series -> {
                        series.getItems().forEach(item -> item.addChartEvtObserver(ChartEvt.ANY, itemObserver));
                        series.getItems().addListener(itemListListener);
                    });
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(series -> {
                        series.getItems().forEach(item -> item.removeChartEvtObserver(ChartEvt.ANY, itemObserver));
                        series.getItems().removeListener(itemListListener);
                    });
                }
            }
            angleStep = 360.0 / getNoOfSectors();
            redraw();
        };
        itemListListener = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(item -> item.addChartEvtObserver(ChartEvt.ANY, itemObserver));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(item -> item.removeChartEvtObserver(ChartEvt.ANY, itemObserver));
                }
            }
        };
        itemObserver     = e -> redraw();
        mouseHandler     = e -> {
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
                series.getItems().forEach(item -> item.addChartEvtObserver(ChartEvt.ANY, itemObserver));
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
            series.getItems().forEach(item -> item.removeChartEvtObserver(ChartEvt.ANY, itemObserver));
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

    public boolean getSeriesBackgroundVisible() { return null == seriesBackgroundVisible ? _seriesBackgroundVisible : seriesBackgroundVisible.get(); }
    public void setSeriesBackgroundVisible(final boolean VISIBLE) {
        if (null == seriesBackgroundVisible) {
            _seriesBackgroundVisible = VISIBLE;
            redraw();
        } else {
            this.seriesBackgroundVisible.set(VISIBLE);
        }
    }
    public BooleanProperty seriesBackgroundVisibleProperty() {
        if (null == seriesBackgroundVisible) {
            seriesBackgroundVisible = new BooleanPropertyBase(_seriesBackgroundVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return SectorChart.this; }
                @Override public String getName() { return "seriesBackgroundVisible"; }
            };
        }
        return seriesBackgroundVisible;
    }

    public boolean getRadialBarChartMode() { return null == radialBarChartMode ? _radialBarChartMode : radialBarChartMode.get(); }
    public void setRadialBarChartMode(final boolean BAR_CHART_MODE) {
        if (null == radialBarChartMode) {
            _radialBarChartMode = BAR_CHART_MODE;
            redraw();
        } else {
            this.radialBarChartMode.set(BAR_CHART_MODE);
        }
    }
    public BooleanProperty radialBarChartModeProperty() {
        if (null == radialBarChartMode) {
            radialBarChartMode = new BooleanPropertyBase(_radialBarChartMode) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return SectorChart.this; }
                @Override public String getName() { return "radialBarChartMode"; }
            };
        }
        return radialBarChartMode;
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

    /**
     * Calling this method will render this chart/plot to a png given of the given width and height
     * @param filename The path and name of the file  /Users/hansolo/Desktop/plot.png
     * @param width The width of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @param height The height of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @return True if the procedure was successful, otherwise false
     */
    public boolean renderToImage(final String filename, final int width, final int height) {
        return Helper.renderToImage(SectorChart.this, width, height, filename);
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() {
        return SectorChart.class.getResource("chart.css").toExternalForm();
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
        final double  centerX            = 0.5 * size;
        final double  centerY            = centerX;
        final double  circleSize         = 0.95 * size;
        final double  circleOuterRadius  = 0.475 * circleSize;
        final double  circleInnerRadius  = 0.1 * size;
        final double  deltaRadius        = circleOuterRadius - circleInnerRadius;
        final double  range              = getRange();
        final int     noOfSectors        = getNoOfSectors();
        final double  halfAngleStepRad   = Math.toRadians(angleStep * 0.5);
        final double  itemWidthFactor    = 0.75;
        final boolean radialBarChartMode = getRadialBarChartMode();


        // clear the canvas
        ctx.clearRect(0, 0, size, size);

        // draw the chart data
        ctx.save();

        double radiusFactor;
        double radius;

        // Pre-Rotate for background etc.
        ctx.translate(centerX, centerY);
        ctx.rotate(-90);
        ctx.translate(-centerX, -centerY);

        // draw series sectors
        if (getSeriesBackgroundVisible()) {
            ctx.save();
            for (int i = 0; i < allSeries.size(); i++) {
                ChartItemSeries<ChartItem> series = allSeries.get(i);
                ctx.beginPath();
                ctx.moveTo(centerX, centerY);
                ctx.arc(centerX, centerY, circleOuterRadius, circleOuterRadius, 0, -angleStep * series.getItems().size());
                ctx.closePath();
                ctx.setFill(series.getFill());
                ctx.fill();
                ctx.translate(centerX, centerY);
                ctx.rotate(angleStep * series.getItems().size());
                ctx.translate(-centerX, -centerY);
            }
            ctx.restore();
        }


        // Pre-Rotate for actual item sectors
        ctx.translate(centerX, centerY);
        //ctx.rotate(90 + angleStep * 0.5);
        ctx.rotate(radialBarChartMode ? 90 + angleStep * 0.5 : 0);
        ctx.translate(-centerX, -centerY);

        // draw item sectors
        double dxi = Math.tan(halfAngleStepRad) * circleInnerRadius * itemWidthFactor / Math.PI * 2;
        double dxiQuarter = dxi / 4;
        double currentAngle = 0;
        for (int i = 0 ; i < allSeries.size() ; i++) {
            ChartItemSeries<ChartItem> series = allSeries.get(i);
            for (int j = 0 ; j < series.getItems().size() ; j++) {
                ChartItem item = series.getItems().get(j);
                radiusFactor = clamp(0.0, 1.0, (item.getValue() / range));
                radius       = clamp(circleInnerRadius, circleOuterRadius, circleInnerRadius + (radiusFactor * deltaRadius));

                if (radialBarChartMode) {
                    double dxo = Math.tan(halfAngleStepRad) * radius * itemWidthFactor * 0.75;

                    ctx.beginPath();
                    ctx.moveTo(centerX - dxi, centerY - circleInnerRadius);
                    ctx.bezierCurveTo(centerX - dxi, centerY - circleInnerRadius - dxiQuarter, centerX + dxi, centerY - circleInnerRadius - dxiQuarter, centerX + dxi, centerY - circleInnerRadius);
                    ctx.lineTo(centerX + dxo, centerY - radius + dxo);
                    ctx.bezierCurveTo(centerX + dxo, centerY - radius, centerX - dxo, centerY - radius, centerX - dxo, centerY - radius + dxo);
                    ctx.closePath();
                } else {
                    ctx.beginPath();
                    ctx.moveTo(centerX, centerY);
                    ctx.arc(centerX, centerY, radius, radius, 0, -angleStep);
                    ctx.closePath();
                }
                ctx.setFill(item.getFill());
                ctx.fill();
                ctx.translate(centerX, centerY);
                ctx.rotate(angleStep);
                ctx.translate(-centerX, -centerY);
                sectorMap.put(new Sector(this.centerX, this.centerY, radius, currentAngle, angleStep), item);
                currentAngle += angleStep;
            }
        }
        ctx.restore();
        
        ctx.setLineWidth(0.75);
        ctx.setStroke(getGridColor());

        // draw star lines
        ctx.save();
        for (int i = 0 ; i < noOfSectors ; i++) {
            ctx.strokeLine(centerX, centerY, centerX, centerY - circleOuterRadius);
            ctx.translate(centerX, centerY);
            ctx.rotate(angleStep);
            ctx.translate(-centerX, -centerY);
        }
        ctx.restore();

        // draw threshold line
        if (isThresholdVisible()) {
            ctx.save();
            radiusFactor = (clamp(0.0, 1.0, (getThreshold() / range)));
            radius       = clamp(circleInnerRadius, circleOuterRadius, radiusFactor * deltaRadius);
            ctx.setLineWidth(clamp(0.75d, 1d, size * 0.005));
            ctx.setLineDashes(new double[] {6, 3});
            ctx.setStroke(getThresholdColor());
            ctx.strokeOval(0.5 * size - radius, 0.5 * size - radius, 2 * radius, 2 * radius);
            ctx.restore();
        }

        // prerotate
        ctx.save();

        ctx.translate(centerX, centerY);
        ctx.rotate(angleStep * 0.5);
        ctx.translate(-centerX, -centerY);

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
                    ctx.translate(centerX, size * 0.06);
                    ctx.rotate(currentAngle < 180 ? 270 : 90);
                    ctx.translate(-centerX, -size * 0.06);
                    ctx.fillText(item.getName(), centerX, size * 0.06);
                    ctx.restore();

                    ctx.translate(centerX, centerY);
                    ctx.rotate(angleStep);
                    ctx.translate(-centerX, -centerY);
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
                ctx.translate(centerX, centerY);
                ctx.rotate(angleStep * series.getItems().size() * 0.5 - angleStep * 0.5);
                ctx.translate(-centerX, -centerY);
                currentAngle += angleStep * series.getItems().size() * 0.5 - angleStep * 0.5;

                ctx.save();
                ctx.translate(centerX, size * 0.035);
                ctx.rotate(currentAngle > 135 && currentAngle < 225 ? 180 : 0);
                ctx.translate(-centerX, -size * 0.035);
                ctx.setFill(series.getTextFill());
                if (sumVisible) {
                    ctx.fillText(series.getName() + " (" + String.format(Locale.US, formatString, series.getSumOfAllItems()) + ")", centerX, size * 0.035);
                } else {
                    ctx.fillText(series.getName(), centerX, size * 0.035);
                }
                ctx.restore();

                ctx.translate(centerX, centerY);
                ctx.rotate(angleStep * series.getItems().size() * 0.5 + angleStep * 0.5);
                ctx.translate(-centerX, -centerY);

                currentAngle += angleStep * series.getItems().size() * 0.5 + angleStep * 0.5;
            }

            ctx.restore();
        }

        ctx.restore();
    }
}
