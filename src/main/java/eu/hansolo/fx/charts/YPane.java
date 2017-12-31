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

import eu.hansolo.fx.charts.data.YItem;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.series.YSeries;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.Point;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static eu.hansolo.fx.charts.tools.Helper.clamp;


public class YPane<T extends YItem> extends Region implements ChartArea {
    private static final double                PREFERRED_WIDTH  = 250;
    private static final double                PREFERRED_HEIGHT = 250;
    private static final double                MINIMUM_WIDTH    = 0;
    private static final double                MINIMUM_HEIGHT   = 0;
    private static final double                MAXIMUM_WIDTH    = 4096;
    private static final double                MAXIMUM_HEIGHT   = 4096;
    private static       double                aspectRatio;
    private              boolean               keepAspect;
    private              double                size;
    private              double                width;
    private              double                height;
    private              Paint                 _chartBackgroundPaint;
    private              ObjectProperty<Paint> chartBackgroundPaint;
    private              List<YSeries<T>>      listOfSeries;
    private              Canvas                canvas;
    private              GraphicsContext       ctx;
    private              double                _thresholdY;
    private              DoubleProperty        thresholdY;
    private              boolean               _thresholdYVisible;
    private              BooleanProperty       thresholdYVisible;
    private              Color                 _thresholdYColor;
    private              ObjectProperty<Color> thresholdYColor;
    private              boolean               valid;
    private              double                _lowerBoundY;
    private              DoubleProperty        lowerBoundY;
    private              double                _upperBoundY;
    private              DoubleProperty        upperBoundY;


    // ******************** Constructors **************************************
    public YPane(final YSeries<T>... SERIES) {
        this(Color.WHITE, SERIES);
    }
    public YPane(final Paint BACKGROUND, final YSeries<T>... SERIES) {
        getStylesheets().add(YPane.class.getResource("chart.css").toExternalForm());
        aspectRatio           = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        keepAspect            = false;
        _chartBackgroundPaint = BACKGROUND;
        listOfSeries          = FXCollections.observableArrayList(SERIES);
        _thresholdY           = 100;
        _thresholdYVisible    = false;
        _thresholdYColor      = Color.RED;
        _lowerBoundY          = 0;
        _upperBoundY          = 100;
        valid                 = isChartTypeValid();
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

        getStyleClass().setAll("chart", "xy-chart");

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        getChildren().setAll(canvas);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());

        listOfSeries.forEach(series -> series.setOnSeriesEvent(seriesEvent -> redraw()));
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT)  { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH)  { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT)  { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH)  { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public Paint getChartBackgroundPaint() { return null == chartBackgroundPaint ? _chartBackgroundPaint : chartBackgroundPaint.get(); }
    public void setChartBackgroundPaint(final Paint PAINT) {
        if (null == chartBackgroundPaint) {
            _chartBackgroundPaint = PAINT;
            redraw();
        } else {
            chartBackgroundPaint.set(PAINT);
        }
    }
    public ObjectProperty<Paint> chartBackgroundPaintProperty() {
        if (null == chartBackgroundPaint) {
            chartBackgroundPaint = new ObjectPropertyBase<Paint>(_chartBackgroundPaint) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return YPane.this; }
                @Override public String getName() { return "chartBackgroundPaint"; }
            };
            _chartBackgroundPaint = null;
        }
        return chartBackgroundPaint;
    }

    public double getThresholdY() { return null == thresholdY ? _thresholdY : thresholdY.get(); }
    public void setThresholdY(final double THRESHOLD) {
        if (null == thresholdY) {
            _thresholdY = THRESHOLD;
            redraw();
        } else {
            thresholdY.set(THRESHOLD);
        }
    }
    public DoubleProperty thresholdYProperty() {
        if (null == thresholdY) {
            thresholdY = new DoublePropertyBase(_thresholdY) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return YPane.this; }
                @Override public String getName() { return "thresholdY"; }
            };
        }
        return thresholdY;
    }

    public boolean isThresholdYVisible() { return null == thresholdYVisible ? _thresholdYVisible : thresholdYVisible.get(); }
    public void setThresholdYVisible(final boolean VISIBLE) {
        if (null == thresholdYVisible) {
            _thresholdYVisible = VISIBLE;
            redraw();
        } else {
            thresholdYVisible.set(VISIBLE);
        }
    }
    public BooleanProperty thresholdYVisibleProperty() {
        if (null == thresholdYVisible) {
            thresholdYVisible = new BooleanPropertyBase(_thresholdYVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return YPane.this; }
                @Override public String getName() { return "thresholdYVisible"; }
            };
        }
        return thresholdYVisible;
    }

    public Color getThresholdYColor() { return null == thresholdYColor ? _thresholdYColor : thresholdYColor.get(); }
    public void setThresholdYColor(final Color COLOR) {
        if (null == thresholdYColor) {
            _thresholdYColor = COLOR;
            redraw();
        } else {
            thresholdYColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> thresholdYColorProperty() {
        if (null == thresholdYColor) {
            thresholdYColor = new ObjectPropertyBase<Color>(_thresholdYColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return YPane.this; }
                @Override public String getName() { return "thresholdYColor"; }
            };
            _thresholdYColor = null;
        }
        return thresholdYColor;
    }

    public double getLowerBoundY() { return null == lowerBoundY ? _lowerBoundY : lowerBoundY.get(); }
    public void setLowerBoundY(final double VALUE) {
        if (null == lowerBoundY) {
            _lowerBoundY = VALUE;
            redraw();
        } else {
            lowerBoundY.set(VALUE);
        }
    }
    public DoubleProperty lowerBoundYProperty() {
        if (null == lowerBoundY) {
            lowerBoundY = new DoublePropertyBase(_lowerBoundY) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return YPane.this; }
                @Override public String getName() { return "lowerBoundY"; }
            };
        }
        return lowerBoundY;
    }

    public double getUpperBoundY() { return null == upperBoundY ? _upperBoundY : upperBoundY.get(); }
    public void setUpperBoundY(final double VALUE) {
        if (null == upperBoundY) {
            _upperBoundY = VALUE;
            redraw();
        } else {
            upperBoundY.set(VALUE);
        }
    }
    public DoubleProperty upperBoundYProperty() {
        if (null == upperBoundY) {
            upperBoundY = new DoublePropertyBase(_upperBoundY) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return YPane.this; }
                @Override public String getName() { return "upperBoundY"; }
            };
        }
        return upperBoundY;
    }

    public double getRangeY() { return getUpperBoundY() - getLowerBoundY(); }

    public List<YSeries<T>> getListOfSeries() { return listOfSeries; }


    // ******************** Draw Chart ****************************************
    private void drawChart() {
        if (null == listOfSeries || listOfSeries.isEmpty()) return;

        ctx.clearRect(0, 0, width, height);
        ctx.setFill(getChartBackgroundPaint());
        ctx.fillRect(0, 0, width, height);

        //double    minValue = listOfSeries.stream().mapToDouble(YSeries::getMinY).min().getAsDouble();
        ChartType type     = listOfSeries.get(0).getChartType();

        listOfSeries.forEach(series -> {
            final ChartType TYPE = series.getChartType();
            switch(TYPE) {
                case DONUT               : drawDonut(series); break;
                case RADAR_POLYGON       :
                case SMOOTH_RADAR_POLYGON:
                case RADAR_SECTOR        : drawRadar(series); break;
            }
        });
        boolean containsRadarChart = false;
        for(YSeries<T> series : listOfSeries) {
            final ChartType TYPE = series.getChartType();
            if (ChartType.RADAR_SECTOR         == TYPE ||
                ChartType.RADAR_POLYGON        == TYPE ||
                ChartType.SMOOTH_RADAR_POLYGON == TYPE) {
                containsRadarChart = true;
                break;
            }
        }
        if (containsRadarChart) {
            drawRadarOverlay(listOfSeries.get(0).getItems().size(), listOfSeries.get(0).getChartType());
        }
    }

    private void drawDonut(final YSeries<T> SERIES) {
        if (null == SERIES) return;
        List<YItem> items       = SERIES.getItems();
        int         noOfItems   = items.size();
        double      center      = size * 0.5;
        double      innerRadius = size * 0.275;
        double      outerRadius = size * 0.4;
        double      barWidth    = size * 0.1;
        double      sum         = items.stream().mapToDouble(YItem::getY).sum();
        double      stepSize    = 360.0 / sum;
        double      angle       = 0;
        double      startAngle  = 90;
        double      xy          = size * 0.1;
        double      wh          = size * 0.8;
        double      x;
        double      y;
        
        ctx.setLineCap(StrokeLineCap.BUTT);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);

        for (YItem item : items) {
            double value = item.getY();
            startAngle -= angle;
            angle = value * stepSize;

            // Segment
            ctx.setLineWidth(barWidth);
            ctx.setStroke(item.getFillColor());
            ctx.strokeArc(xy, xy, wh, wh, startAngle, -angle, ArcType.OPEN);

            // Percentage
            //x = innerRadius * Math.cos(Math.toRadians(startAngle - (angle * 0.5)));
            //y = -innerRadius * Math.sin(Math.toRadians(startAngle - (angle * 0.5)));
            //ctx.setFill(Color.BLACK);
            //ctx.fillText(String.format(Locale.US, "%.0f%%", (value / sum * 100.0)), center + x, center + y, barWidth);

            // Value
            x = outerRadius * Math.cos(Math.toRadians(startAngle - (angle * 0.5)));
            y = -outerRadius * Math.sin(Math.toRadians(startAngle - (angle * 0.5)));
            ctx.setFill(Color.WHITE);
            ctx.fillText(String.format(Locale.US, "%.0f", value), center + x, center + y, barWidth);
        }
    }

    private void drawRadar(final YSeries<T> SERIES) {
        final double CENTER_X      = 0.5 * size;
        final double CENTER_Y      = CENTER_X;
        final double CIRCLE_SIZE   = 0.9 * size;
        final double LOWER_BOUND_Y = getLowerBoundY();
        final double DATA_RANGE    = getRangeY();
        final double RANGE         = 0.35714 * CIRCLE_SIZE;
        final double OFFSET        = 0.14286 * CIRCLE_SIZE;
        final int    NO_OF_SECTORS = SERIES.getItems().size();
        final double angleStep     = 360.0 / NO_OF_SECTORS;

        // draw the chart data
        ctx.save();
        if (SERIES.getFill() instanceof RadialGradient) {
            ctx.setFill(new RadialGradient(0, 0, size  * 0.5, size * 0.5, size * 0.45, false, CycleMethod.NO_CYCLE, ((RadialGradient) SERIES.getFill()).getStops()));
        } else {
            ctx.setFill(SERIES.getFill());
        }
        ctx.setStroke(SERIES.getStroke());

        switch(SERIES.getChartType()) {
            case RADAR_POLYGON:
                ctx.save();
                ctx.beginPath();
                ctx.moveTo(CENTER_X, 0.36239 * size);
                SERIES.getItems().forEach(item -> {
                    double r1 = (item.getY() - LOWER_BOUND_Y) / DATA_RANGE;
                    ctx.lineTo(CENTER_X, CENTER_Y - OFFSET - r1 * RANGE);
                    Helper.rotateCtx(ctx, CENTER_X, CENTER_Y, angleStep);
                });
                double r2 = ((SERIES.getItems().get(NO_OF_SECTORS - 1).getY() - LOWER_BOUND_Y) / DATA_RANGE);
                ctx.lineTo(CENTER_X, CENTER_Y - OFFSET - r2 * RANGE);
                ctx.closePath();
                ctx.fill();
                ctx.stroke();
                ctx.restore();
                break;
            case SMOOTH_RADAR_POLYGON:
                double      radAngle     = Math.toRadians(180);
                double      radAngleStep = Math.toRadians(angleStep);
                List<Point> points       = new ArrayList<>();

                double x = CENTER_X + (-Math.sin(radAngle) * (CENTER_Y - (0.36239 * size)));
                double y = CENTER_Y + (+Math.cos(radAngle) * (CENTER_Y - (0.36239 * size)));
                points.add(new Point(x, y));

                for (YItem item : SERIES.getItems()) {
                    double r1 = (CENTER_Y - (CENTER_Y - OFFSET - ((item.getY() - LOWER_BOUND_Y) / DATA_RANGE) * RANGE));
                    x = CENTER_X + (-Math.sin(radAngle) * r1);
                    y = CENTER_Y + (+Math.cos(radAngle) * r1);
                    points.add(new Point(x, y));
                    radAngle += radAngleStep;
                }
                double r3 = (CENTER_Y - (CENTER_Y - OFFSET - ((SERIES.getItems().get(NO_OF_SECTORS - 1).getY() - LOWER_BOUND_Y) / DATA_RANGE) * RANGE));
                x = CENTER_X + (-Math.sin(radAngle) * r3);
                y = CENTER_Y + (+Math.cos(radAngle) * r3);
                points.add(new Point(x, y));

                Point[] interpolatedPoints = Helper.subdividePoints(points.toArray(new Point[0]), 8);

                ctx.beginPath();
                ctx.moveTo(interpolatedPoints[0].getX(), interpolatedPoints[0].getY());
                for (int i = 0 ; i < interpolatedPoints.length - 1 ; i++) {
                    Point point = interpolatedPoints[i];
                    ctx.lineTo(point.getX(), point.getY());
                }
                ctx.lineTo(interpolatedPoints[interpolatedPoints.length - 1].getX(), interpolatedPoints[interpolatedPoints.length - 1].getY());
                ctx.closePath();

                ctx.fill();
                ctx.stroke();
                break;
            case RADAR_SECTOR:
                Helper.rotateCtx(ctx, CENTER_X, CENTER_Y, -90);
                SERIES.getItems().forEach(item -> {
                    double r1 = (item.getY() - LOWER_BOUND_Y) / DATA_RANGE;
                    ctx.beginPath();
                    ctx.moveTo(CENTER_X, CENTER_Y);
                    ctx.arc(CENTER_X, CENTER_Y, r1 * RANGE + OFFSET, r1 * RANGE + OFFSET, 0, -angleStep);
                    ctx.closePath();
                    ctx.fill();
                    ctx.stroke();

                    Helper.rotateCtx(ctx, CENTER_X, CENTER_Y, angleStep);
                });
                break;
        }
        ctx.restore();

        //drawRadarOverlay(NO_OF_SECTORS, TYPE);
    }

    private void drawRadarOverlay(final int NO_OF_SECTORS, final ChartType TYPE) {
        final double CENTER_X    = 0.5 * size;
        final double CENTER_Y    = CENTER_X;
        final double CIRCLE_SIZE = 0.90 * size;
        final double DATA_RANGE  = getRangeY();
        final double MIN_VALUE   = listOfSeries.stream().mapToDouble(YSeries::getMinY).min().getAsDouble();
        final double RANGE       = 0.35714 * CIRCLE_SIZE;
        final double OFFSET      = 0.14286 * CIRCLE_SIZE;
        final double angleStep   = 360.0 / NO_OF_SECTORS;

        // draw concentric rings
        ctx.setLineWidth(1);
        ctx.setStroke(Color.GRAY);
        double ringStepSize = size / 20.0;
        double pos          = 0.5 * (size - CIRCLE_SIZE);
        double ringSize     = CIRCLE_SIZE;
        for (int i = 0 ; i < 11 ; i++) {
            ctx.strokeOval(pos, pos, ringSize, ringSize);
            pos      += ringStepSize;
            ringSize -= 2 * ringStepSize;
        }

        // draw star lines
        ctx.save();
        for (int i = 0 ; i < NO_OF_SECTORS ; i++) {
            ctx.strokeLine(CENTER_X, 0.05 * size, CENTER_X, 0.5 * size);
            Helper.rotateCtx(ctx, CENTER_X, CENTER_Y, angleStep);
        }
        ctx.restore();

        // draw threshold line
        if (isThresholdYVisible()) {
            double r = ((getThresholdY() - MIN_VALUE) / DATA_RANGE);
            ctx.setLineWidth(clamp(1d, 3d, size * 0.005));
            ctx.setStroke(getThresholdYColor());
            ctx.strokeOval(0.5 * size - OFFSET - r * RANGE, 0.5 * size - OFFSET - r * RANGE,
                                  2 * (r * RANGE + OFFSET), 2 * (r * RANGE + OFFSET));
        }

        // prerotate if sectormode
        ctx.save();

        if (ChartType.RADAR_SECTOR == TYPE) {
            Helper.rotateCtx(ctx, CENTER_X, CENTER_Y, angleStep * 0.5);
        }

        // draw text
        ctx.save();
        ctx.setFont(Fonts.latoRegular(0.04 * size));
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setFill(Color.BLACK);
        for (int i = 0 ; i < NO_OF_SECTORS ; i++) {
            //ctx.fillText(data.get(i).ID, CENTER_X, size * 0.02);
            Helper.rotateCtx(ctx, CENTER_X, CENTER_Y, angleStep);
        }
        ctx.restore();

        ctx.restore();
    }

    private boolean isChartTypeValid() {
        boolean containsDonut              = false;
        boolean containsRadarSector        = false;
        boolean containsRadarPolygon       = false;
        boolean containsSmoothRadarPolygon = false;
        for(YSeries<T> series : getListOfSeries()) {
            final ChartType TYPE = series.getChartType();
            containsDonut              = ChartType.DONUT                == TYPE && !containsDonut;
            containsRadarSector        = ChartType.RADAR_SECTOR         == TYPE && !containsRadarSector;
            containsRadarPolygon       = ChartType.RADAR_POLYGON        == TYPE && !containsRadarPolygon;
            containsSmoothRadarPolygon = ChartType.SMOOTH_RADAR_POLYGON == TYPE && !containsSmoothRadarPolygon;
        }
        boolean valid = false;
        if (containsDonut && !containsRadarSector && !containsRadarPolygon && !containsSmoothRadarPolygon) {
            valid = true;
        } else if (containsRadarSector && !containsDonut && !containsRadarPolygon && !containsSmoothRadarPolygon) {
            valid = true;
        } else if (containsRadarPolygon | containsSmoothRadarPolygon && !containsDonut && !containsRadarSector) {
            valid = true;
        }
        return valid;
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (keepAspect) {
            if (aspectRatio * width > height) {
                width = 1 / (aspectRatio / height);
            } else if (1 / (aspectRatio / height) > width) {
                height = aspectRatio * width;
            }
        }

        if (width > 0 && height > 0) {
            canvas.setWidth(size);
            canvas.setHeight(size);
            //canvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);
            canvas.relocate((width - size) * 0.5, (height - size) * 0.5);
            
            redraw();
        }
    }

    protected void redraw() {
        drawChart();
    }
}
