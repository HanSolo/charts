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

package eu.hansolo.fx.charts.areaheatmap;

import eu.hansolo.fx.charts.data.DataPoint;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.ColorMapping;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


@DefaultProperty("children")
public class AreaHeatMap extends Region {
    public enum Quality {
        FINE(2),
        BETTER(4),
        NORMAL(8),
        POOR(16),
        RAW(32);

        private final int FACTOR;

        Quality(final int FACTOR) {
            this.FACTOR = FACTOR;
        }

        public int getFactor() { return FACTOR; }
    }
    private static final double                       PREFERRED_WIDTH  = 250;
    private static final double                       PREFERRED_HEIGHT = 250;
    private static final double                       MINIMUM_WIDTH    = 50;
    private static final double                       MINIMUM_HEIGHT   = 50;
    private static final double                       MAXIMUM_WIDTH    = 1024;
    private static final double                       MAXIMUM_HEIGHT   = 1024;
    private              double                       size;
    private              double                       width;
    private              double                       height;
    private              Canvas                       canvas;
    private              GraphicsContext              ctx;
    private              List<DataPoint>              points;
    private              List<DataPoint>              polygon;
    private              int                          _quality;
    private              IntegerProperty              quality;
    private              int                          _noOfCloserInfluentPoints;
    private              IntegerProperty              noOfCloserInfluentPoints;
    private              double                       _heatMapOpacity;
    private              DoubleProperty               heatMapOpacity;
    private              boolean                      _dataPointsVisible;
    private              BooleanProperty              dataPointsVisible;
    private              boolean                      _discreteColors;
    private              BooleanProperty              discreteColors;
    private              boolean                      _smoothedHull;
    private              BooleanProperty              smoothedHull;
    private              ColorMapping                 _mapping;
    private              ObjectProperty<ColorMapping> mapping;
    private              boolean                      _useColorMapping;
    private              BooleanProperty              useColorMapping;
    private              double                       minValue;
    private              double                       maxValue;
    private              double                       range;


    // ******************** Constructors **************************************
    public AreaHeatMap() {
        this(5, Quality.BETTER.getFactor());
    }
    public AreaHeatMap(final Quality QUALITY) {
        this(5, QUALITY.getFactor());
    }
    public AreaHeatMap(final int QUALITY) {
        this(5, QUALITY);
    }
    public AreaHeatMap(final int NO_OF_CLOSER_INFLUENT_POINTS, final int QUALITY) {
        points                    = new ArrayList<>();
        polygon                   = new ArrayList<>();
        _quality                  = QUALITY;
        _noOfCloserInfluentPoints = NO_OF_CLOSER_INFLUENT_POINTS;
        _heatMapOpacity           = 0.5;
        _dataPointsVisible        = false;
        _discreteColors           = false;
        _smoothedHull             = false;

        _mapping                  = ColorMapping.BLUE_CYAN_GREEN_YELLOW_RED;

        _useColorMapping          = true;
        minValue                  = Double.MAX_VALUE;
        maxValue                  = -Double.MAX_VALUE;
        range                     = maxValue - minValue;
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
        ctx    = canvas.getGraphicsContext2D();

        getChildren().setAll(canvas);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public int getQuality() { return null == quality ? _quality : quality.get(); }
    public void setQuality(final Quality QUALITY) { setQuality(QUALITY.getFactor()); }
    public void setQuality(final int QUALITY) {
        if (null == quality) {
            _quality = Helper.clamp(2, 32, QUALITY);
            redraw();
        } else {
            quality.set(QUALITY);
        }
    }
    public IntegerProperty qualityProperty() {
        if (null == quality) {
            quality = new IntegerPropertyBase(_quality) {
                @Override protected void invalidated() {
                    set(Helper.clamp(2, 32, get()));
                    redraw();
                }
                @Override public Object getBean() { return AreaHeatMap.this; }
                @Override public String getName() { return "quality"; }
            };
        }
        return quality;
    }

    public int getNoOfCloserInfluentPoints() { return null == noOfCloserInfluentPoints ? _noOfCloserInfluentPoints : noOfCloserInfluentPoints.get(); }
    public void setNoOfCloserInfluentPoints(final int NUMBER_OF_POINTS) {
        if (null == noOfCloserInfluentPoints) {
            _noOfCloserInfluentPoints = Helper.clamp(1, 10, NUMBER_OF_POINTS);
            redraw();
        } else {
            noOfCloserInfluentPoints.set(NUMBER_OF_POINTS);
        }
    }
    public IntegerProperty noOfCloserInfluentPointsProperty() {
        if (null == noOfCloserInfluentPoints) {
            noOfCloserInfluentPoints = new IntegerPropertyBase(_noOfCloserInfluentPoints) {
                @Override protected void invalidated() {
                    set(Helper.clamp(1, 10, get()));
                    redraw();
                }
                @Override public Object getBean() { return AreaHeatMap.this; }
                @Override public String getName() { return "noOfCloserInfluentPoints"; }
            };
        }
        return noOfCloserInfluentPoints;
    }

    public double getHeatMapOpacity() { return null == heatMapOpacity ? _heatMapOpacity : heatMapOpacity.get(); }
    public void setHeatMapOpacity(final double OPACITY) {
        if (null == heatMapOpacity) {
            _heatMapOpacity = Helper.clamp(0, 1, OPACITY);
            redraw();
        } else {
            heatMapOpacity.set(OPACITY);
        }
    }
    public DoubleProperty heatMapOpacityProperty() {
        if (null == heatMapOpacity) {
            heatMapOpacity = new DoublePropertyBase(_heatMapOpacity) {
                @Override protected void invalidated() {
                    set(Helper.clamp(0, 1, get()));
                    redraw();
                }
                @Override public Object getBean() { return AreaHeatMap.this; }
                @Override public String getName() { return "heatMapOpacity"; }
            };
        }
        return heatMapOpacity;
    }

    public boolean getShowDataPoints() { return null == dataPointsVisible ? _dataPointsVisible : dataPointsVisible.get(); }
    public void setDataPointsVisible(final boolean VISIBLE) {
        if (null == dataPointsVisible) {
            _dataPointsVisible = VISIBLE;
            redraw();
        } else {
            dataPointsVisible.set(VISIBLE);
        }
    }
    public BooleanProperty dataPointsVisibleProperty() {
        if (null == dataPointsVisible) {
            dataPointsVisible = new BooleanPropertyBase(_dataPointsVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return AreaHeatMap.this; }
                @Override public String getName() { return "dataPointsVisible"; }
            };
        }
        return dataPointsVisible;
    }

    public boolean isSmoothedHull() { return null == smoothedHull ? _smoothedHull : smoothedHull.get(); }
    public void setSmoothedHull(final boolean SMOOTHED) {
        if (null == smoothedHull) {
            _smoothedHull = SMOOTHED;
            createHullPolygon();
            redraw();
        } else {
            smoothedHull.set(SMOOTHED);
        }
    }
    public BooleanProperty smoothedHullProperty() {
        if (null == smoothedHull) {
            smoothedHull = new BooleanPropertyBase(_smoothedHull) {
                @Override protected void invalidated() {
                    createHullPolygon();
                    redraw();
                }
                @Override public Object getBean() { return AreaHeatMap.this; }
                @Override public String getName() { return "smoothedHull"; }
            };
        }
        return smoothedHull;
    }

    public boolean isDiscreteColors() { return null == discreteColors ? _discreteColors : discreteColors.get(); }
    public void setDiscreteColors(final boolean DISCRETE) {
        if (null == discreteColors) {
            _discreteColors = DISCRETE;
            redraw();
        } else {
            discreteColors.set(DISCRETE);
        }
    }
    public BooleanProperty discreteColorsProperty() {
        if (null == discreteColors) {
            discreteColors = new BooleanPropertyBase(_discreteColors) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return AreaHeatMap.this; }
                @Override public String getName() { return "discreteColors"; }
            };
        }
        return discreteColors;
    }

    public ColorMapping getMapping() { return null == mapping ? _mapping : mapping.get(); }
    public void setColorMapping(final ColorMapping MAPPING) {
        if (null == mapping) {
            _mapping = MAPPING;
            redraw();
        } else {
            mapping.set(MAPPING);
        }
    }
    public ObjectProperty<ColorMapping> mappingProperty() {
        if (null == mapping) {
            mapping = new ObjectPropertyBase<ColorMapping>(_mapping) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return AreaHeatMap.this; }
                @Override public String getName() { return "mapping"; }
            };
            _mapping = null;
        }
        return mapping;
    }

    public boolean getUseColorMapping() { return null == useColorMapping ? _useColorMapping : useColorMapping.get(); }
    public void setUseColorMapping(final boolean USE) {
        if (null == useColorMapping) {
            _useColorMapping = USE;
            redraw();
        } else {
            useColorMapping.set(USE);
        }
    }
    public BooleanProperty useColorMapping() {
        if (null == useColorMapping) {
            useColorMapping = new BooleanPropertyBase(_useColorMapping) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return AreaHeatMap.this; }
                @Override public String getName() { return "useColorMapping"; }
            };
        }
        return useColorMapping;
    }

    public void setDataPoints(final DataPoint... POINTS) {
        setDataPoints(Arrays.asList(POINTS));
    }
    public void setDataPoints(final List<DataPoint> POINTS) {
        minValue = POINTS.stream().mapToDouble(DataPoint::getValue).min().getAsDouble();
        maxValue = POINTS.stream().mapToDouble(DataPoint::getValue).max().getAsDouble();
        range    = maxValue - minValue;

        points.clear();
        points.addAll(POINTS);
        createHullPolygon();
        redraw();
    }

    private Color getColorForValue(final double VALUE, final boolean LEVELS) {
        double limit  = 0.55;
        double min    = -30;
        double max    = 50;
        double delta  = max - min;
        double levels = 25;
        double value  = Helper.clamp(min, max, VALUE);
        double tmp    = 1 - (1 - limit) - (((value - min) * limit) / delta);
        if (LEVELS) {
            tmp = Math.round(tmp * levels) / levels;
        }
        return Helper.hslToRGB(tmp, 1, 0.5);
    }
    private Color getColorForValue(final double VALUE) { return getColorForValue(VALUE, getHeatMapOpacity()); }
    private Color getColorForValue(final double VALUE, final double OPACITY) {
        return Helper.getColorWithOpacityAt(getMapping().getGradient(), ((VALUE - minValue) / range), OPACITY);
    }

    private void createHullPolygon() {
        polygon.clear();
        if (isSmoothedHull()) {
            List<DataPoint> p = Helper.createSmoothedHull(points, 16);
            polygon.addAll(p);
        } else {
            polygon.addAll(Helper.createHull(points));
        }
    }

    private double getValueAt(final int LIMIT, final double X , final double Y) {
        List<Number[]> arr = new ArrayList<>();
        double         t   = 0.0;
        double         b   = 0.0;
        if(Helper.isInPolygon(X, Y, polygon)) {
            for (int counter = 0 ; counter < points.size() ; counter++) {
                DataPoint point = points.get(counter);
                double distance = Helper.squareDistance(X, Y, point.getX(), point.getY());
                if (Double.compare(distance, 0) == 0) { return point.getValue(); }
                arr.add(counter, new Number[] { distance, counter });
            }
            arr.sort(Comparator.comparingInt(n -> n[0].intValue()));
            for (int counter = 0 ; counter < LIMIT ; counter++) {
                Number[] ptr = arr.get(counter);
                double inv = 1 / Math.pow(ptr[0].intValue(), 2);
                t = t + inv * points.get(ptr[1].intValue()).getValue();
                b = b + inv;
            }
            return t / b;
        } else {
            return -255;
        }
    }

    private void draw(final int LIMIT, final double RESOLUTION) {
        int limit        = LIMIT > points.size() ? points.size() : LIMIT + 1;
        double pixelSize = 2 * RESOLUTION;

        ctx.clearRect(0, 0, width, height);

        for (double y = 0 ; y < height ; y += RESOLUTION) {
            for (double x = 0 ; x < width ; x += RESOLUTION) {
                double value = getValueAt(limit, x, y);
                if (value != -255) {
                    Color          color    = getUseColorMapping() ? getColorForValue(value) : getColorForValue(value, isDiscreteColors());
                    RadialGradient gradient = new RadialGradient(0, 0, x, y, RESOLUTION,
                                                                 false, CycleMethod.NO_CYCLE,
                                                                 new Stop(0, Color.color(color.getRed(), color.getGreen(), color.getBlue(), getHeatMapOpacity())),
                                                                 new Stop(1, Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.0)));
                    ctx.setFill(gradient);
                    ctx.fillOval(x - RESOLUTION, y - RESOLUTION, pixelSize, pixelSize);
                }
            }
        }
    }

    private void drawDataPoints() {
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setFont(Fonts.opensansRegular(size * 0.0175));

        for (int i = 0 ; i < points.size() ; i++) {
            DataPoint point = points.get(i);

            ctx.setFill(Color.rgb(255, 255, 255, 0.5));
            ctx.fillOval(point.getX() - 8, point.getY() - 8, 16, 16);

            //ctx.setStroke(getUseColorMapping() ? getColorForValue(point.getValue(), 1) : getColorForValue(point.getValue(), isDiscreteColors()));
            ctx.setStroke(Color.BLACK);
            ctx.strokeOval(point.getX() - 8, point.getY() - 8, 16, 16);

            ctx.setFill(Color.BLACK);
            ctx.fillText(Long.toString(Math.round(point.getValue())), point.getX(), point.getY(), 16);
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            canvas.setWidth(width);
            canvas.setHeight(height);
            canvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            redraw();
        }
    }

    private void redraw() {
        draw(getNoOfCloserInfluentPoints(), getQuality());
        if (getShowDataPoints()) { drawDataPoints(); }
    }
}
