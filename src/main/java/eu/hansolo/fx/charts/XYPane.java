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

import eu.hansolo.fx.charts.data.XYData;
import eu.hansolo.fx.charts.series.Series;
import eu.hansolo.fx.charts.series.XYSeries;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.Point;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static eu.hansolo.fx.charts.tools.Helper.clamp;


/**
 * Created by hansolo on 16.07.17.
 */
public class XYPane<T extends XYData> extends Region implements ChartArea {
    private static final double                PREFERRED_WIDTH  = 250;
    private static final double                PREFERRED_HEIGHT = 250;
    private static final double                MINIMUM_WIDTH    = 0;
    private static final double                MINIMUM_HEIGHT   = 0;
    private static final double                MAXIMUM_WIDTH    = 4096;
    private static final double                MAXIMUM_HEIGHT   = 4096;
    private static final double                MIN_SYMBOL_SIZE  = 2;
    private static final double                MAX_SYMBOL_SIZE  = 6;
    private static final int                   SUB_DIVISIONS    = 24;
    private static       double                aspectRatio;
    private              boolean               keepAspect;
    private              double                size;
    private              double                width;
    private              double                height;
    private              Pane                  pane;
    private              Paint                 _chartBackgroundPaint;
    private              ObjectProperty<Paint> chartBackgroundPaint;
    private              List<XYSeries<T>>     listOfSeries;
    private              Canvas                canvas;
    private              GraphicsContext       ctx;
    private              double                scaleX;
    private              double                scaleY;
    private              double                symbolSize;
    private              int                   noOfBands;
    private              double                _lowerBoundX;
    private              DoubleProperty        lowerBoundX;
    private              double                _upperBoundX;
    private              DoubleProperty        upperBoundX;
    private              double                _lowerBoundY;
    private              DoubleProperty        lowerBoundY;
    private              double                _upperBoundY;
    private              DoubleProperty        upperBoundY;
    private              boolean               referenceZero;


    // ******************** Constructors **************************************
    public XYPane(final XYSeries<T>... SERIES) {
        this(Color.WHITE, 1,  SERIES);
    }
    public XYPane(final int BANDS, final XYSeries<T>... SERIES) {
        this(Color.WHITE, BANDS, SERIES);
    }
    public XYPane(final Paint BACKGROUND, final int BANDS, final XYSeries<T>... SERIES) {
        getStylesheets().add(XYPane.class.getResource("chart.css").toExternalForm());
        aspectRatio           = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        keepAspect            = false;
        _chartBackgroundPaint = BACKGROUND;
        listOfSeries          = FXCollections.observableArrayList(SERIES);
        scaleX                = 1;
        scaleY                = 1;
        symbolSize            = 2;
        noOfBands = clamp(1, 5, BANDS);
        _lowerBoundX          = 0;
        _upperBoundX          = 100;
        _lowerBoundY          = 0;
        _upperBoundY          = 100;
        referenceZero         = true;

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

        pane = new Pane(canvas);

        getChildren().setAll(pane);
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
                @Override public Object getBean() { return XYPane.this; }
                @Override public String getName() { return "chartBackgroundPaint"; }
            };
            _chartBackgroundPaint = null;
        }
        return chartBackgroundPaint;
    }

    public int getNoOfBands() { return noOfBands; }
    public void setNoOfBands(final int BANDS) {
        noOfBands = clamp(1, 5, BANDS);
        redraw();
    }

    public double getLowerBoundX() { return null == lowerBoundX ? _lowerBoundX : lowerBoundX.get(); }
    public void setLowerBoundX(final double VALUE) {
        if (null == lowerBoundX) {
            _lowerBoundX = VALUE;
            redraw();
        } else {
            lowerBoundX.set(VALUE);
        }
    }
    public DoubleProperty lowerBoundXProperty() {
        if (null == lowerBoundX) {
            lowerBoundX = new DoublePropertyBase(_lowerBoundX) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return XYPane.this; }
                @Override public String getName() { return "lowerBoundX"; }
            };
        }
        return lowerBoundX;
    }

    public double getUpperBoundX() { return null == upperBoundX ? _upperBoundX : upperBoundX.get(); }
    public void setUpperBoundX(final double VALUE) {
        if (null == upperBoundX) {
            _upperBoundX = VALUE;
            redraw();
        } else {
            upperBoundX.set(VALUE);
        }
    }
    public DoubleProperty upperBoundXProperty() {
        if (null == upperBoundX) {
            upperBoundX = new DoublePropertyBase(_upperBoundX) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return XYPane.this; }
                @Override public String getName() { return "upperBoundX"; }
            };
        }
        return upperBoundX;
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
                @Override public Object getBean() { return XYPane.this; }
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
                @Override public Object getBean() { return XYPane.this; }
                @Override public String getName() { return "upperBoundY"; }
            };
        }
        return upperBoundY;
    }

    public boolean isReferenceZero() { return referenceZero; }
    public void setReferenceZero(final boolean IS_ZERO) {
        referenceZero = IS_ZERO;
        redraw();
    }

    public double getRangeX() {  return getUpperBoundX() - getLowerBoundX();  }
    public double getRangeY() { return getUpperBoundY() - getLowerBoundY(); }
    
    public List<XYSeries<T>> getListOfSeries() { return listOfSeries; }


    // ******************** Draw Chart ****************************************
    private void drawChart() {
        if (null == listOfSeries || listOfSeries.isEmpty()) return;

        ctx.clearRect(0, 0, width, height);
        ctx.setFill(getChartBackgroundPaint());
        ctx.fillRect(0, 0, width, height);

        if (listOfSeries.size() == 2) {
            boolean     deltaChart = false;
            ChartType[] chartTypes = new ChartType[2];
            int         count      = 0;
            for(Series series : listOfSeries) {
                chartTypes[count] = series.getChartType();
                deltaChart        = ChartType.LINE_DELTA == chartTypes[count] || ChartType.SMOOTH_LINE_DELTA == chartTypes[count];
                count++;
            }
            if (deltaChart && chartTypes[0] == chartTypes[1]) {
                switch(chartTypes[0]) {
                    case LINE_DELTA       : drawLineDelta(listOfSeries.get(0), listOfSeries.get(1)); return;
                    case SMOOTH_LINE_DELTA: drawSmoothLineDelta(listOfSeries.get(0), listOfSeries.get(1)); return;
                }
            }
        }


        listOfSeries.forEach(series -> {
            final ChartType TYPE        = series.getChartType();
            final boolean   SHOW_POINTS = series.isShowPoints();
            switch(TYPE) {
                case LINE             : drawLine(series, SHOW_POINTS); break;
                case SMOOTH_LINE      : drawSmoothLine(series, SHOW_POINTS); break;
                case AREA             : drawArea(series, SHOW_POINTS); break;
                case SMOOTH_AREA      : drawSmoothArea(series, SHOW_POINTS); break;
                case SCATTER          : drawScatter(series) ;break;
                case HORIZON          : drawHorizon(series, false); break;
                case SMOOTHED_HORIZON : drawHorizon(series, true); break;
            }
        });
    }

    private void drawLine(final XYSeries<T> SERIES, final boolean SHOW_POINTS) {
        final double LOWER_BOUND_X = getLowerBoundX();
        final double LOWER_BOUND_Y = getLowerBoundY();
        double oldX = 0;
        double oldY = height;
        ctx.setStroke(SERIES.getStroke());
        ctx.setFill(Color.TRANSPARENT);
        for (XYData item : SERIES.getItems()) {
            double x = (item.getX() - LOWER_BOUND_X) * scaleX;
            double y = height - (item.getY() - LOWER_BOUND_Y) * scaleY;
            ctx.strokeLine(oldX, oldY, x, y);
            oldX = x;
            oldY = y;
        }

        if (SHOW_POINTS) { drawSymbols(SERIES); }
    }

    private void drawArea(final XYSeries<T> SERIES, final boolean SHOW_POINTS) {
        final double LOWER_BOUND_X = getLowerBoundX();
        final double LOWER_BOUND_Y = getLowerBoundY();
        double oldX = 0;
        ctx.setStroke(SERIES.getStroke());
        ctx.setFill(SERIES.getFill());
        ctx.beginPath();
        ctx.moveTo(SERIES.getItems().get(0).getX() * scaleX, height);
        for (XYData item : SERIES.getItems()) {
            double x = (item.getX() - LOWER_BOUND_X) * scaleX;
            double y = height - (item.getY() - LOWER_BOUND_Y) * scaleY;
            ctx.lineTo(x, y);
            oldX = x;
        }
        ctx.lineTo(oldX, height);
        ctx.lineTo(0, height);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();

        if (SHOW_POINTS) { drawSymbols(SERIES); }
    }

    private void drawScatter(final XYSeries<T> SERIES) {
        final double LOWER_BOUND_X = getLowerBoundX();
        final double LOWER_BOUND_Y = getLowerBoundY();
        ctx.setStroke(Color.TRANSPARENT);
        ctx.setFill(Color.TRANSPARENT);
        for (XYData item : SERIES.getItems()) {
            double x = (item.getX() - LOWER_BOUND_X) * scaleX;
            double y = height - (item.getY() - LOWER_BOUND_Y) * scaleY;
            drawSymbol(x, y, item.getColor(), item.getSymbol());
        }
    }

    private void drawSmoothLine(final XYSeries<T> SERIES, final boolean SHOW_POINTS) {
        final double LOWER_BOUND_X = getLowerBoundX();
        final double LOWER_BOUND_Y = getLowerBoundY();
        ctx.setStroke(SERIES.getStroke());
        ctx.setFill(Color.TRANSPARENT);

        List<Point> points = new ArrayList<>(SERIES.getItems().size());
        SERIES.getItems().forEach(item -> points.add(new Point(item.getX(), item.getY())));

        Point[] interpolatedPoints = Helper.subdividePoints(points.toArray(new Point[0]), SUB_DIVISIONS);

        ctx.beginPath();
        for(Point p : interpolatedPoints) {
            ctx.lineTo((p.getX() - LOWER_BOUND_X) * scaleX, height - (p.getY() - LOWER_BOUND_Y) * scaleY);
        }
        ctx.stroke();

        if (SHOW_POINTS) { drawSymbols(SERIES); }
    }

    private void drawSmoothArea(final XYSeries<T> SERIES, final boolean SHOW_POINTS) {
        final double LOWER_BOUND_X = getLowerBoundX();
        final double LOWER_BOUND_Y = getLowerBoundY();
        ctx.setStroke(SERIES.getStroke());
        ctx.setFill(SERIES.getFill());
        double oldX = 0;

        List<Point> points = new ArrayList<>(SERIES.getItems().size());
        SERIES.getItems().forEach(item -> points.add(new Point(item.getX(), item.getY())));

        Point[] interpolatedPoints = Helper.subdividePoints(points.toArray(new Point[0]), SUB_DIVISIONS);

        ctx.beginPath();
        ctx.moveTo(SERIES.getItems().get(0).getX() * scaleX, height);
        for(Point p : interpolatedPoints) {
            double x = (p.getX() - LOWER_BOUND_X) * scaleX;
            ctx.lineTo(x, height - (p.getY() - LOWER_BOUND_Y) * scaleY);
            oldX = x;
        }

        ctx.lineTo(oldX, height);
        ctx.lineTo(0, height);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();

        if (SHOW_POINTS) { drawSymbols(SERIES); }
    }

    private void drawHorizon(final XYSeries<T> SERIES, final boolean SMOOTHED) {
        if (null == SERIES || SERIES.getItems().isEmpty()) { return; }

        Color positiveBaseColor;
        Color negativeBaseColor;
        if (SERIES.getFill() instanceof Color) {
            positiveBaseColor = (Color) SERIES.getFill();
            if (positiveBaseColor.equals(Color.BLACK) ||
                positiveBaseColor.equals(Color.WHITE) ||
                positiveBaseColor.equals(Color.TRANSPARENT)) {
                positiveBaseColor = Color.BLUE;
                negativeBaseColor = Color.RED;
            } else {
                negativeBaseColor = Helper.getComplementaryColor(positiveBaseColor);
            }
        } else {
            positiveBaseColor = Color.BLUE;
            negativeBaseColor = Color.RED;
        }

        // Create colors
        List<Color> aboveColors = Helper.createColorVariations(positiveBaseColor, noOfBands);
        List<Color> belowColors = Helper.createColorVariations(negativeBaseColor, noOfBands);

        int noOfItems = SERIES.getItems().size();

        // Create list of points
        List<Point> points = new ArrayList<>(noOfItems);
        for (int i = 0; i < noOfItems; i++) { points.add(new Point(i, SERIES.getItems().get(i).getY())); }

        double refValue  = isReferenceZero() ? 0 : (points.isEmpty() ? 0 : points.get(0).getY());
        double minY      = points.stream().mapToDouble(Point::getY).min().getAsDouble();
        double maxY      = points.stream().mapToDouble(Point::getY).max().getAsDouble();
        double bandWidth = (maxY - minY) / noOfBands;

        scaleX = width / (noOfItems - 1);
        scaleY = height / ((maxY - minY) / (getNoOfBands()));

        // Normalize y values to 0
        points.forEach(point -> point.setY(point.getY() - refValue));

        // Subdivide points
        Point[] subdividedPoints;
        if (SMOOTHED) {
            subdividedPoints = Helper.subdividePoints(points.toArray(new Point[0]), SUB_DIVISIONS);
        } else {
            subdividedPoints = Helper.subdividePointsLinear(points.toArray(new Point[0]), SUB_DIVISIONS);
        }

        // Split in points above and below 0
        List<Point>[] splittedPoints = splitIntoAboveAndBelow(Arrays.asList(subdividedPoints));
        List<Point>   aboveRefPoints = splittedPoints[0];
        List<Point>   belowRefPoints = splittedPoints[1];

        // Split points above and below 0 into noOfBands
        Map<Integer, List<Point>> aboveRefPointsSplitToBands = splitIntoBands(aboveRefPoints, bandWidth);
        Map<Integer, List<Point>> belowRefPointsSplitToBands = splitIntoBands(belowRefPoints, bandWidth);

        // Draw values above 0
        if (!aboveRefPoints.isEmpty()) { drawPath(aboveRefPointsSplitToBands, bandWidth, aboveColors); }

        // Draw values below 0
        if (!belowRefPoints.isEmpty()) { drawPath(belowRefPointsSplitToBands, bandWidth, belowColors); }
    }

    private void drawLineDelta(final XYSeries<T> SERIES_1, final XYSeries<T> SERIES_2) {
        if (SERIES_1.getItems().size() != SERIES_2.getItems().size()) { throw new IllegalArgumentException("Both series must have the same number of items!"); }
        final double LOWER_BOUND_X = getLowerBoundX();
        final double LOWER_BOUND_Y = getLowerBoundY();

        int          noOfItems         = SERIES_1.getItems().size();
        List<XYData> cachedItems       = new LinkedList<>();
        Point        lastPointForClose = new Point();

        XYData series1Item0  = SERIES_1.getItems().get(0);
        XYData series2Item0  = SERIES_2.getItems().get(0);
        int    currentSeries = series1Item0.getY() > series2Item0.getY() ? 1 : 2;

        Paint series1Stroke = SERIES_1.getStroke();
        Paint series1Fill   = SERIES_1.getFill();

        Paint series2Stroke = SERIES_2.getStroke();
        Paint series2Fill   = SERIES_2.getFill();

        // Start path
        ctx.setLineWidth(size * 0.0025);
        ctx.beginPath();
        switch(currentSeries) {
            case 1 :
                ctx.moveTo((series1Item0.getX() - LOWER_BOUND_X) * scaleX, height - (series1Item0.getY() - LOWER_BOUND_Y) * scaleY);
                lastPointForClose.set(series2Item0.getX(), series2Item0.getY());
                break;
            case 2 :
                ctx.moveTo((series2Item0.getX() - LOWER_BOUND_X) * scaleX, height - (series2Item0.getY() - LOWER_BOUND_Y) * scaleY);
                lastPointForClose.set(series1Item0.getX(), series1Item0.getY());
                break;
            default: ctx.moveTo((series1Item0.getX() - LOWER_BOUND_X) * scaleX, height - (series1Item0.getY() - LOWER_BOUND_Y) * scaleY);
                lastPointForClose.set(series2Item0.getX(), series2Item0.getY());
                break;
        }
        // Draw path
        List<XYData> items1 = SERIES_1.getItems();
        List<XYData> items2 = SERIES_2.getItems();
        for (int i = 1 ; i < noOfItems ; i++) {
            XYData lastXyData1 = items1.get(i - 1);
            XYData lastXyData2 = items2.get(i - 1);

            XYData xyData1 = items1.get(i);
            XYData xyData2 = items2.get(i);

            if (lastXyData1.getY() > lastXyData2.getY() && xyData1.getY() < xyData2.getY()) {
                // Lines crossed Line1 is now below lower Line2
                Point intersectionPoint = Helper.calcIntersectionOfTwoLines(lastXyData1.getX(), lastXyData1.getY(), xyData1.getX(), xyData1.getY(),
                                                                            lastXyData2.getX(), lastXyData2.getY(), xyData2.getX(), xyData2.getY());
                ctx.lineTo((intersectionPoint.getX() - LOWER_BOUND_X) * scaleX, height - (intersectionPoint.getY() - LOWER_BOUND_Y) * scaleY);

                Collections.reverse(cachedItems);
                for (XYData item : cachedItems) { ctx.lineTo((item.getX() - LOWER_BOUND_X) * scaleX, height - (item.getY() - LOWER_BOUND_Y) * scaleY); }
                ctx.lineTo((lastPointForClose.getX() - LOWER_BOUND_X) * scaleX, height - (lastPointForClose.getY() - LOWER_BOUND_Y) * scaleY);
                ctx.closePath();
                ctx.setFill(series1Fill);
                ctx.fill();
                cachedItems.clear();

                ctx.beginPath();
                ctx.moveTo((intersectionPoint.getX() - LOWER_BOUND_X) * scaleX, height - (intersectionPoint.getY() - LOWER_BOUND_Y) * scaleY);
                ctx.lineTo((xyData2.getX() - LOWER_BOUND_X) * scaleX, height - (xyData2.getY() - LOWER_BOUND_Y) * scaleY);
                currentSeries = 2;
                cachedItems.add(xyData1);
                lastPointForClose.set(intersectionPoint.getX(), intersectionPoint.getY());
            } else if (lastXyData1.getY() < lastXyData2.getY() && xyData1.getY() > xyData2.getY()) {
                // Lines crossed and Line1 is now above Line2
                Point intersectionPoint = Helper.calcIntersectionOfTwoLines(lastXyData1.getX(), lastXyData1.getY(), xyData1.getX(), xyData1.getY(),
                                                                            lastXyData2.getX(), lastXyData2.getY(), xyData2.getX(), xyData2.getY());
                ctx.lineTo((intersectionPoint.getX() - LOWER_BOUND_X) * scaleX, height - (intersectionPoint.getY() - LOWER_BOUND_Y) * scaleY);

                Collections.reverse(cachedItems);
                for (XYData item : cachedItems) { ctx.lineTo((item.getX() - LOWER_BOUND_X) * scaleX, height - (item.getY() - LOWER_BOUND_Y) * scaleY); }
                ctx.lineTo((lastPointForClose.getX() - LOWER_BOUND_X) * scaleX, height - (lastPointForClose.getY() - LOWER_BOUND_Y) * scaleY);
                ctx.closePath();
                ctx.setFill(series2Fill);
                ctx.fill();
                cachedItems.clear();

                ctx.beginPath();
                ctx.moveTo((intersectionPoint.getX() - LOWER_BOUND_X) * scaleX, height - (intersectionPoint.getY() - LOWER_BOUND_Y) * scaleY);
                ctx.lineTo((xyData1.getX() - LOWER_BOUND_X) * scaleX, height - (xyData1.getY() - LOWER_BOUND_Y) * scaleY);
                currentSeries = 1;
                cachedItems.add(xyData2);
                lastPointForClose.set(intersectionPoint.getX(), intersectionPoint.getY());
            } else {
                // Lines did not cross
                switch(currentSeries) {
                    case 1: ctx.lineTo((xyData1.getX() - LOWER_BOUND_X) * scaleX, height - (xyData1.getY() - LOWER_BOUND_Y) * scaleY); cachedItems.add(xyData2); break;
                    case 2: ctx.lineTo((xyData2.getX() - LOWER_BOUND_X) * scaleX, height - (xyData2.getY() - LOWER_BOUND_Y) * scaleY); cachedItems.add(xyData1); break;
                }
            }

            ctx.setStroke(series1Stroke);
            ctx.strokeLine((lastXyData1.getX() - LOWER_BOUND_X) * scaleX, height - (lastXyData1.getY() - LOWER_BOUND_Y) * scaleY, (xyData1.getX() - LOWER_BOUND_X) * scaleX, height - (xyData1.getY() - LOWER_BOUND_Y) * scaleY);

            ctx.setStroke(series2Stroke);
            ctx.strokeLine((lastXyData2.getX() - LOWER_BOUND_X) * scaleX, height - (lastXyData2.getY() - LOWER_BOUND_Y) * scaleY, (xyData2.getX() - LOWER_BOUND_X) * scaleX, height - (xyData2.getY() - LOWER_BOUND_Y) * scaleY);
        }
        Collections.reverse(cachedItems);
        for (XYData item : cachedItems) { ctx.lineTo((item.getX() - LOWER_BOUND_X) * scaleX, height - (item.getY() - LOWER_BOUND_Y) * scaleY); }
        ctx.lineTo((lastPointForClose.getX() - LOWER_BOUND_X) * scaleX, height - (lastPointForClose.getY() - LOWER_BOUND_Y) * scaleY);
        ctx.closePath();
        switch(currentSeries) {
            case 1: ctx.setFill(series1Fill); break;
            case 2: ctx.setFill(series2Fill); break;
        }
        ctx.fill();
        cachedItems.clear();


        if (SERIES_1.isShowPoints()) { drawSymbols(SERIES_1); }
        if (SERIES_2.isShowPoints()) { drawSymbols(SERIES_2); }
    }

    private void drawSmoothLineDelta(final XYSeries<T> SERIES_1, final XYSeries<T> SERIES_2) {
        if (SERIES_1.getItems().size() != SERIES_2.getItems().size()) { throw new IllegalArgumentException("Both series must have the same number of items!"); }
        final double LOWER_BOUND_X = getLowerBoundX();
        final double LOWER_BOUND_Y = getLowerBoundY();

        // Smooth series
        List<Point> points1 = new ArrayList<>(SERIES_1.getItems().size());
        SERIES_1.getItems().forEach(item -> points1.add(new Point(item.getX(), item.getY())));
        Point[] interpolatedPoints1 = Helper.subdividePoints(points1.toArray(new Point[0]), SUB_DIVISIONS);

        List<Point> points2 = new ArrayList<>(SERIES_2.getItems().size());
        SERIES_2.getItems().forEach(item -> points2.add(new Point(item.getX(), item.getY())));
        Point[] interpolatedPoints2 = Helper.subdividePoints(points2.toArray(new Point[0]), SUB_DIVISIONS);

        int         noOfItems         = interpolatedPoints1.length;
        List<Point> cachedItems       = new LinkedList<>();
        Point       lastPointForClose = new Point();

        XYData series1Item0  = SERIES_1.getItems().get(0);
        XYData series2Item0  = SERIES_2.getItems().get(0);
        int    currentSeries = series1Item0.getY() > series2Item0.getY() ? 1 : 2;

        Paint series1Stroke = SERIES_1.getStroke();
        Paint series1Fill   = SERIES_1.getFill();

        Paint series2Stroke = SERIES_2.getStroke();
        Paint series2Fill   = SERIES_2.getFill();

        // Start path
        ctx.setLineWidth(size * 0.0025);
        ctx.beginPath();
        switch(currentSeries) {
            case 1 :
                ctx.moveTo((interpolatedPoints1[0].getX() - LOWER_BOUND_X) * scaleX, height - (interpolatedPoints1[0].getY() - LOWER_BOUND_Y) * scaleY);
                lastPointForClose.set(interpolatedPoints2[0].getX(), interpolatedPoints2[0].getY());
                break;
            case 2 :
                ctx.moveTo((interpolatedPoints2[0].getX() - LOWER_BOUND_X) * scaleX, height - (interpolatedPoints2[0].getY() - LOWER_BOUND_Y) * scaleY);
                lastPointForClose.set(interpolatedPoints1[0].getX(), interpolatedPoints1[0].getY());
                break;
            default: ctx.moveTo((interpolatedPoints1[0].getX() - LOWER_BOUND_X) * scaleX, height - (interpolatedPoints1[0].getY() - LOWER_BOUND_Y) * scaleY);
                lastPointForClose.set(interpolatedPoints2[0].getX(), interpolatedPoints2[0].getY());
                break;
        }
        // Draw path
        for (int i = 1 ; i < noOfItems ; i++) {
            Point lastXyData1 = interpolatedPoints1[i - 1];
            Point lastXyData2 = interpolatedPoints2[i - 1];

            Point xyData1 = interpolatedPoints1[i];
            Point xyData2 = interpolatedPoints2[i];

            if (lastXyData1.getY() > lastXyData2.getY() && xyData1.getY() < xyData2.getY()) {
                // Lines crossed Line1 is now below lower Line2
                Point intersectionPoint = Helper.calcIntersectionOfTwoLines(lastXyData1.getX(), lastXyData1.getY(), xyData1.getX(), xyData1.getY(),
                                                                            lastXyData2.getX(), lastXyData2.getY(), xyData2.getX(), xyData2.getY());
                ctx.lineTo((intersectionPoint.getX() - LOWER_BOUND_X) * scaleX, height - (intersectionPoint.getY() - LOWER_BOUND_Y) * scaleY);

                Collections.reverse(cachedItems);
                for (Point item : cachedItems) { ctx.lineTo((item.getX() - LOWER_BOUND_X) * scaleX, height - (item.getY() - LOWER_BOUND_Y) * scaleY); }
                ctx.lineTo((lastPointForClose.getX() - LOWER_BOUND_X) * scaleX, height - (lastPointForClose.getY() - LOWER_BOUND_Y) * scaleY);
                ctx.closePath();
                ctx.setFill(series1Fill);
                ctx.fill();
                cachedItems.clear();

                ctx.beginPath();
                ctx.moveTo((intersectionPoint.getX() - LOWER_BOUND_X) * scaleX, height - (intersectionPoint.getY() - LOWER_BOUND_Y) * scaleY);
                ctx.lineTo((xyData2.getX() - LOWER_BOUND_X) * scaleX, height - (xyData2.getY() - LOWER_BOUND_Y) * scaleY);
                currentSeries = 2;
                cachedItems.add(xyData1);
                lastPointForClose.set(intersectionPoint.getX(), intersectionPoint.getY());
            } else if (lastXyData1.getY() < lastXyData2.getY() && xyData1.getY() > xyData2.getY()) {
                // Lines crossed and Line1 is now above Line2
                Point intersectionPoint = Helper.calcIntersectionOfTwoLines(lastXyData1.getX(), lastXyData1.getY(), xyData1.getX(), xyData1.getY(),
                                                                            lastXyData2.getX(), lastXyData2.getY(), xyData2.getX(), xyData2.getY());
                ctx.lineTo((intersectionPoint.getX() - LOWER_BOUND_X) * scaleX, height - (intersectionPoint.getY() - LOWER_BOUND_Y) * scaleY);

                Collections.reverse(cachedItems);
                for (Point item : cachedItems) { ctx.lineTo((item.getX() - LOWER_BOUND_X) * scaleX, height - (item.getY() - LOWER_BOUND_Y) * scaleY); }
                ctx.lineTo((lastPointForClose.getX() - LOWER_BOUND_X) * scaleX, height - (lastPointForClose.getY() - LOWER_BOUND_Y) * scaleY);
                ctx.closePath();
                ctx.setFill(series2Fill);
                ctx.fill();
                cachedItems.clear();

                ctx.beginPath();
                ctx.moveTo((intersectionPoint.getX() - LOWER_BOUND_X) * scaleX, height - (intersectionPoint.getY() - LOWER_BOUND_Y) * scaleY);
                ctx.lineTo((xyData1.getX() - LOWER_BOUND_X) * scaleX, height - (xyData1.getY() - LOWER_BOUND_Y) * scaleY);
                currentSeries = 1;
                cachedItems.add(xyData2);
                lastPointForClose.set(intersectionPoint.getX(), intersectionPoint.getY());
            } else {
                // Lines did not cross
                switch(currentSeries) {
                    case 1: ctx.lineTo((xyData1.getX() - LOWER_BOUND_X) * scaleX, height - (xyData1.getY() - LOWER_BOUND_Y) * scaleY); cachedItems.add(xyData2); break;
                    case 2: ctx.lineTo((xyData2.getX() - LOWER_BOUND_X) * scaleX, height - (xyData2.getY() - LOWER_BOUND_Y) * scaleY); cachedItems.add(xyData1); break;
                }
            }

            ctx.setStroke(series1Stroke);
            ctx.strokeLine((lastXyData1.getX() - LOWER_BOUND_X) * scaleX, height - (lastXyData1.getY() - LOWER_BOUND_Y) * scaleY, (xyData1.getX() - LOWER_BOUND_X) * scaleX, height - (xyData1.getY() - LOWER_BOUND_Y) * scaleY);

            ctx.setStroke(series2Stroke);
            ctx.strokeLine((lastXyData2.getX() - LOWER_BOUND_X) * scaleX, height - (lastXyData2.getY() - LOWER_BOUND_Y) * scaleY, (xyData2.getX() - LOWER_BOUND_X) * scaleX, height - (xyData2.getY() - LOWER_BOUND_Y) * scaleY);
        }
        Collections.reverse(cachedItems);
        for (Point item : cachedItems) { ctx.lineTo((item.getX() - LOWER_BOUND_X) * scaleX, height - (item.getY() - LOWER_BOUND_Y) * scaleY); }
        ctx.lineTo((lastPointForClose.getX() - LOWER_BOUND_X) * scaleX, height - (lastPointForClose.getY() - LOWER_BOUND_Y) * scaleY);
        ctx.closePath();
        switch(currentSeries) {
            case 1: ctx.setFill(series1Fill); break;
            case 2: ctx.setFill(series2Fill); break;
        }
        ctx.fill();
        cachedItems.clear();


        if (SERIES_1.isShowPoints()) { drawSymbols(SERIES_1); }
        if (SERIES_2.isShowPoints()) { drawSymbols(SERIES_2); }
    }

    private void drawPath(final Map<Integer, List<Point>> MAP_OF_BANDS, final double BAND_WIDTH, final List<Color> COLORS) {
        double oldX = 0;
        for (int band = 0 ; band < getNoOfBands() ; band++) {
            ctx.beginPath();
            for (Point p : MAP_OF_BANDS.get(band)) {
                double x = p.getX() * scaleX;
                double y = height - (p.getY() * scaleY);
                ctx.lineTo(x, y + (band * BAND_WIDTH) * scaleY);
                oldX = x;
            }
            ctx.lineTo(oldX, height);
            ctx.lineTo(0, height);
            ctx.closePath();
            ctx.setFill(COLORS.get(band));
            ctx.fill();
        }
    }

    private List<Point>[] splitIntoAboveAndBelow(final List<Point> POINTS) {
        ArrayList<Point> aboveReferencePoints = new ArrayList<>();
        ArrayList<Point> belowReferencePoints = new ArrayList<>();
        Point   last       = POINTS.get(0);
        boolean isAbove    = Double.compare(last.getY(), 0.0) >= 0;
        int     noOfPoints = POINTS.size();
        for (int i = 0 ; i < noOfPoints ; i++) {
            Point current = POINTS.get(i);
            Point next    = i < noOfPoints - 1 ? POINTS.get(i + 1) : POINTS.get(noOfPoints - 1);

            if (Double.compare(current.getY(), 0.0) >= 0) {
                if (!isAbove) {
                    Point p = Helper.calcIntersectionPoint(last, current, 0.0);
                    aboveReferencePoints.add(p);
                    belowReferencePoints.add(p);
                }
                aboveReferencePoints.add(current);
                isAbove = true;
            } else {
                if (isAbove) {
                    Point p = Helper.calcIntersectionPoint(current, next, 0.0);
                    aboveReferencePoints.add(p);
                    belowReferencePoints.add(p);
                }
                // Invert y values that are below the reference point
                belowReferencePoints.add(new Point(current.getX(), -current.getY()));
                isAbove = false;
            }
            last = current;
        }
        return new ArrayList[] { aboveReferencePoints, belowReferencePoints };
    }

    private Map<Integer, List<Point>> splitIntoBands(final List<Point> POINTS, final double BAND_WIDTH) {
        Map<Integer, List<Point>> mapOfBands = new HashMap<>(getNoOfBands());
        if (POINTS.isEmpty()) { return mapOfBands; }

        int    noOfPoints = POINTS.size();
        double currentBandMinY;
        double currentBandMaxY;
        double currentBandMinYScaled;
        double currentBandMaxYScaled;

        // Add first point to all noOfBands
        Point firstPoint = new Point(POINTS.get(0).getX(), POINTS.get(0).getY());
        for (int band = 0 ; band < getNoOfBands() ; band++) {
            List<Point> listOfPointsInBand = new ArrayList<>(noOfPoints);
            listOfPointsInBand.add(firstPoint);
            mapOfBands.put(band, listOfPointsInBand);
        }

        // Iterate over all points and check for each band
        for (int i = 1 ; i < noOfPoints - 1 ; i++) {
            Point  last     = POINTS.get(i - 1);
            double lastY    = height - (last.getY() * scaleY);
            Point  current  = POINTS.get(i);
            double currentY = height - (current.getY() * scaleY);
            Point  next     = POINTS.get(i + 1);
            double nextY    = height - (next.getY() * scaleY);

            for (int band = 0 ; band < getNoOfBands() ; band++) {
                currentBandMinY       = band * BAND_WIDTH;
                currentBandMaxY       = currentBandMinY + BAND_WIDTH;
                currentBandMinYScaled = height - currentBandMinY * scaleY;
                currentBandMaxYScaled = height - currentBandMaxY * scaleY;

                if (Double.compare(lastY, currentBandMinYScaled) >= 0) {             // last <= currentBandMinY
                    // Calculate intersection with currentBandMinY
                    mapOfBands.get(band).add(Helper.calcIntersectionPoint(last, current, currentBandMinY));
                } else if (Double.compare(currentY, currentBandMinYScaled) <= 0 &&
                           Double.compare(currentY, currentBandMaxYScaled) >= 0) {   // currentBandMinY < current < currentBandMaxY
                    mapOfBands.get(band).add(new Point(current.getX(), current.getY()));
                } else if (Double.compare(nextY, currentBandMaxYScaled) <= 0) {      // next >= currentBandMaxY
                    // Calculate intersection with currentBandMaxY
                    mapOfBands.get(band).add(Helper.calcIntersectionPoint(current, next, currentBandMaxY));
                }
            }
        }

        // Add last point to all bands
        Point lastPoint = new Point(POINTS.get(noOfPoints - 1).getX(), clamp(0, BAND_WIDTH, POINTS.get(noOfPoints - 1).getY()));
        mapOfBands.forEach((band, pointsInBand) -> {
            Point lastPointInBand = pointsInBand.get(pointsInBand.size() - 1);
            if(noOfPoints - lastPointInBand.getX() > 2) { pointsInBand.add(new Point(noOfPoints - 1, lastPointInBand.getY())); }
            pointsInBand.add(lastPoint);
        });

        return mapOfBands;
    }

    private void drawSymbols(final XYSeries<T> SERIES) {
        final double LOWER_BOUND_X = getLowerBoundX();
        final double LOWER_BOUND_Y = getLowerBoundY();
        for (XYData item : SERIES.getItems()) {
            double x = (item.getX() - LOWER_BOUND_X) * scaleX;
            double y = height - (item.getY() - LOWER_BOUND_Y) * scaleY;
            drawSymbol(x, y, item.getColor(), item.getSymbol());
        }
    }

    private void drawSymbol(final double X, final double Y, final Color COLOR, final Symbol SYMBOL) {
        double halfSymbolSize = symbolSize * 0.5;
        ctx.save();
        switch(SYMBOL) {
            case NONE:
                break;
            case SQUARE:
                ctx.setStroke(Color.TRANSPARENT);
                ctx.setFill(COLOR);
                ctx.fillRect(X - halfSymbolSize, Y - halfSymbolSize, symbolSize, symbolSize);
                break;
            case TRIANGLE:
                ctx.setStroke(COLOR);
                ctx.setFill(null);
                ctx.strokeLine(X, Y - halfSymbolSize, X + halfSymbolSize, Y + halfSymbolSize);
                ctx.strokeLine(X + halfSymbolSize, Y + halfSymbolSize, X - halfSymbolSize, Y + halfSymbolSize);
                ctx.strokeLine(X - halfSymbolSize, Y + halfSymbolSize, X, Y - halfSymbolSize);
                break;
            case STAR:
                ctx.setStroke(COLOR);
                ctx.setFill(null);
                ctx.strokeLine(X - halfSymbolSize, Y, X + halfSymbolSize, Y);
                ctx.strokeLine(X, Y - halfSymbolSize, X, Y + halfSymbolSize);
                ctx.strokeLine(X - halfSymbolSize, Y - halfSymbolSize, X + halfSymbolSize, Y + halfSymbolSize);
                ctx.strokeLine(X + halfSymbolSize, Y - halfSymbolSize, X - halfSymbolSize, Y + halfSymbolSize);
                break;
            case CROSS:
                ctx.setStroke(COLOR);
                ctx.setFill(null);
                ctx.strokeLine(X - halfSymbolSize, Y, X + halfSymbolSize, Y);
                ctx.strokeLine(X, Y - halfSymbolSize, X, Y + halfSymbolSize);
                break;
            case CIRCLE:
            default    :
                ctx.setStroke(Color.TRANSPARENT);
                ctx.setFill(COLOR);
                ctx.fillOval(X - halfSymbolSize, Y - halfSymbolSize, symbolSize, symbolSize);
                break;
        }
        ctx.restore();
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
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            canvas.setWidth(width);
            canvas.setHeight(height);

            symbolSize = clamp(MIN_SYMBOL_SIZE, MAX_SYMBOL_SIZE, size * 0.016);

            scaleX = width / getRangeX();
            scaleY = height / getRangeY();

            redraw();
        }
    }

    protected void redraw() {
        drawChart();
    }
}
