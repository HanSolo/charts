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
import eu.hansolo.toolboxfx.font.Fonts;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.heatmap.ColorMapping;
import eu.hansolo.fx.heatmap.Mapping;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.text.TextAlignment;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


@DefaultProperty("children")
public class AreaHeatMap extends Region {
    public enum Quality {
        EXCELLENT(2, 4),
        REFINED(3, 6),
        GODD(4, 8),
        STANDARD(5, 10),
        BASIC(8, 16),
        POOR(16, 32),
        RAW(32, 64);

        private final int    factor;
        private final double pixelSize;

        Quality(final int factor, final double pixelSize) {
            this.factor    = factor;
            this.pixelSize = pixelSize;
        }

        public int getFactor() { return factor; }

        public double getPixelSize() { return pixelSize; }
    }

    private static final double                  PREFERRED_WIDTH  = 250;
    private static final double                  PREFERRED_HEIGHT = 250;
    private static final double                  MINIMUM_WIDTH    = 50;
    private static final double                  MINIMUM_HEIGHT   = 50;
    private static final double                  MAXIMUM_WIDTH    = 1024;
    private static final double                  MAXIMUM_HEIGHT   = 1024;
    private static final Color                   HALF_WHITE       = Color.rgb(255, 255, 255, 0.5);
    private              double                  size;
    private              double                  width;
    private              double                  height;
    private              Canvas                  canvas;
    private              GraphicsContext         ctx;
    private              List<DataPoint>         points;
    private              List<DataPoint>         polygon;
    private              Quality                 _quality;
    private              ObjectProperty<Quality> quality;
    private              int                     _noOfCloserInfluentPoints;
    private              IntegerProperty         noOfCloserInfluentPoints;
    private              double                  _heatMapOpacity;
    private              DoubleProperty          heatMapOpacity;
    private              boolean                 _dataPointsVisible;
    private              BooleanProperty         dataPointsVisible;
    private              boolean                 _discreteColors;
    private              BooleanProperty         discreteColors;
    private              boolean                 _smoothedHull;
    private              BooleanProperty         smoothedHull;
    private              Mapping                 _mapping;
    private              ObjectProperty<Mapping> mapping;
    private              boolean                 _useColorMapping;
    private              BooleanProperty         useColorMapping;
    private              double                  minValue;
    private              double                  maxValue;
    private              double                  range;


    // ******************** Constructors **************************************
    public AreaHeatMap() {
        this(5, Quality.STANDARD);
    }
    public AreaHeatMap(final Quality quality) {
        this(5, quality);
    }
    public AreaHeatMap(final int noOfCloserInfluentPoints, final Quality quality) {
        points                    = new ArrayList<>();
        polygon                   = new ArrayList<>();
        _quality                  = quality;
        _noOfCloserInfluentPoints = noOfCloserInfluentPoints;
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
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public Quality getQuality() { return null == quality ? _quality : quality.get(); }
    public void setQuality(final Quality quality) {
        if (null == this.quality) {
            this._quality = quality;
            redraw();
        } else {
            this.quality.set(quality);
        }
    }
    public ObjectProperty<Quality> qualityProperty() {
        if (null == this.quality) {
            this.quality = new ObjectPropertyBase<>(_quality) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return AreaHeatMap.this; }
                @Override public String getName() { return "quality"; }
            };
            this._quality = null;
        }

        return quality;
    }

    public int getNoOfCloserInfluentPoints() { return null == noOfCloserInfluentPoints ? _noOfCloserInfluentPoints : noOfCloserInfluentPoints.get(); }
    public void setNoOfCloserInfluentialPoints(final int numberOfPoints) {
        if (null == noOfCloserInfluentPoints) {
            _noOfCloserInfluentPoints = Helper.clamp(1, 10, numberOfPoints);
            redraw();
        } else {
            noOfCloserInfluentPoints.set(numberOfPoints);
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
    public void setHeatMapOpacity(final double opacity) {
        if (null == heatMapOpacity) {
            _heatMapOpacity = Helper.clamp(0, 1, opacity);
            redraw();
        } else {
            heatMapOpacity.set(opacity);
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
    public void setDataPointsVisible(final boolean visible) {
        if (null == dataPointsVisible) {
            _dataPointsVisible = visible;
            redraw();
        } else {
            dataPointsVisible.set(visible);
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
    public void setSmoothedHull(final boolean smoothed) {
        if (null == smoothedHull) {
            _smoothedHull = smoothed;
            createHullPolygon();
            redraw();
        } else {
            smoothedHull.set(smoothed);
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
    public void setDiscreteColors(final boolean discrete) {
        if (null == discreteColors) {
            _discreteColors = discrete;
            redraw();
        } else {
            discreteColors.set(discrete);
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

    public Mapping getMapping() { return null == mapping ? _mapping : mapping.get(); }
    public void setColorMapping(final Mapping mapping) {
        if (null == this.mapping) {
            _mapping = mapping;
            redraw();
        } else {
            this.mapping.set(mapping);
        }
    }
    public ObjectProperty<Mapping> mappingProperty() {
        if (null == mapping) {
            mapping = new ObjectPropertyBase<Mapping>(_mapping) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return AreaHeatMap.this; }
                @Override public String getName() { return "mapping"; }
            };
            _mapping = null;
        }
        return mapping;
    }

    public boolean getUseColorMapping() { return null == useColorMapping ? _useColorMapping : useColorMapping.get(); }
    public void setUseColorMapping(final boolean use) {
        if (null == useColorMapping) {
            _useColorMapping = use;
            redraw();
        } else {
            useColorMapping.set(use);
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

    /**
     * Calling this method will render this chart/plot to a png given of the given width and height
     * @param filename The path and name of the file  /Users/hansolo/Desktop/plot.png
     * @param width The width of the final image in pixels (if &lt; 0 then 400 and if &gt; 4096 then 4096)
     * @param height The height of the final image in pixels (if &lt; 0 then 400 and if &gt; 4096 then 4096)
     * @return True if the procedure was successful, otherwise false
     */
    public boolean renderToImage(final String filename, final int width, final int height) {
        return Helper.renderToImage(AreaHeatMap.this, width, height, filename);
    }

    /**
     * Calling this method will render this chart/plot to a png given of the given width and height
     * @param width The width of the final image in pixels (if &lt; 0 then 400 and if &gt; 4096 then 4096)
     * @param height The height of the final image in pixels (if &lt; 0 then 400 and if &gt; 4096 then 4096)
     * @return A BufferedImage of this chart in the given dimension
     */
    public BufferedImage renderToImage(final int width, final int height) {
        return Helper.renderToImage(AreaHeatMap.this, width, height);
    }

    private Color getColorForValue(final double value, final boolean levels) {
        double limit     = 0.55;
        double min       = -30;
        double max       = 50;
        double delta     = max - min;
        double newLevels = 25;
        double newValue  = Helper.clamp(min, max, value);
        double tmp       = 1 - (1 - limit) - (((newValue - min) * limit) / delta);
        if (levels) {
            tmp = Math.round(tmp * newLevels) / newLevels;
        }
        return Helper.hslToRGB(tmp, 1, 0.5);
    }
    private Color getColorForValue(final double value) { return getColorForValue(value, getHeatMapOpacity()); }
    private Color getColorForValue(final double value, final double opacity) {
        return Helper.getColorWithOpacityAt(getMapping().getGradient(), ((value - minValue) / range), opacity);
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

    private double getValueAt(final int limit, final double x , final double y) {
        List<Number[]> arr = new ArrayList<>();
        double         t   = 0.0;
        double         b   = 0.0;
        if(Helper.isInPolygon(x, y, polygon)) {
            for (int counter = 0 ; counter < points.size() ; counter++) {
                DataPoint point = points.get(counter);
                double distance = Helper.squareDistance(x, y, point.getX(), point.getY());
                if (Double.compare(distance, 0) == 0) { return point.getValue(); }
                arr.add(counter, new Number[] { distance, counter });
            }
            arr.sort(Comparator.comparingInt(n -> n[0].intValue()));
            for (int counter = 0 ; counter < limit ; counter++) {
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

    private void draw(final int limit, final double resolution, final double pixelSize) {
        final int    newLimit         = limit > points.size() ? points.size() : limit + 1;
        final double heatMapOpacity   = getHeatMapOpacity();
        final boolean useColorMapping = getUseColorMapping();
        ctx.clearRect(0, 0, width, height);
        for (double y = 0 ; y < height ; y += resolution) {
            for (double x = 0 ; x < width ; x += resolution) {
                double value = getValueAt(newLimit, x, y);
                if (value != -255) {
                    final Color          color    = useColorMapping ? getColorForValue(value) : getColorForValue(value, isDiscreteColors());
                    final double         red      = color.getRed();
                    final double         green    = color.getGreen();
                    final double         blue     = color.getBlue();

                    final RadialGradient gradient = new RadialGradient(0, 0, x, y, resolution,
                                                                       false, CycleMethod.NO_CYCLE,
                                                                       new Stop(0, Color.color(red, green, blue, heatMapOpacity)),
                                                                       new Stop(1, Color.color(red, green, blue, 0.0)));
                    ctx.setFill(gradient);

                    ctx.fillOval(x - resolution, y - resolution, pixelSize, pixelSize);
                }
            }
        }
    }

    private void drawDataPoints() {
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setFont(Fonts.opensansRegular(size * 0.0175));
        ctx.setStroke(Color.BLACK);

        points.stream().forEach(point -> {
            final double centerX = point.getX() - 8;
            final double centerY = point.getY() - 8;

            ctx.setFill(HALF_WHITE);
            ctx.fillOval(centerX, centerY, 16, 16);

            ctx.setStroke(Color.BLACK);
            ctx.strokeOval(centerX, centerY, 16, 16);

            ctx.setFill(Color.BLACK);
            ctx.fillText(Long.toString(Math.round(point.getValue())), point.getX(), point.getY(), 16);
        });
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
        draw(getNoOfCloserInfluentPoints(), getQuality().getFactor(), getQuality().getPixelSize());
        if (getShowDataPoints()) { drawDataPoints(); }
    }
}
