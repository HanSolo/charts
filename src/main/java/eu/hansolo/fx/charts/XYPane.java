package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.XYData;
import eu.hansolo.fx.charts.series.XYSeries;
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
import java.util.List;

import static eu.hansolo.fx.charts.Helper.clamp;


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
    private              double                _rangeX;
    private              DoubleProperty        rangeX;
    private              double                _rangeY;
    private              DoubleProperty        rangeY;


    // ******************** Constructors **************************************
    public XYPane(final XYSeries<T>... SERIES) {
        this(Color.WHITE, SERIES);
    }
    public XYPane(final Paint BACKGROUND, final XYSeries<T>... SERIES) {
        getStylesheets().add(XYPane.class.getResource("chart.css").toExternalForm());
        aspectRatio           = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        keepAspect            = false;
        _chartBackgroundPaint = BACKGROUND;
        listOfSeries          = FXCollections.observableArrayList(SERIES);
        scaleX                = 1;
        scaleY                = 1;
        symbolSize            = 2;
        _rangeX               = 100;
        _rangeY               = 100;

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

    public double getRangeX() {  return null == rangeX ? _rangeX : rangeX.get();  }
    public void setRangeX(final double VALUE) {
        if (null == rangeX) {
            _rangeX = VALUE;
            resize();
        } else {
            rangeX.set(VALUE);
        }
    }
    public DoubleProperty rangeXProperty() {
        if (null == rangeX) {
            rangeX = new DoublePropertyBase(_rangeX) {
                @Override protected void invalidated() { resize(); }
                @Override public Object getBean() {  return XYPane.this;  }
                @Override public String getName() {  return "rangeX"; }
            };
        }
        return rangeX;
    }

    public double getRangeY() { return null == rangeY ? _rangeY : rangeY.get(); }
    public void setRangeY(final double VALUE) {
        if (null == rangeY) {
            _rangeY = VALUE;
            resize();
        } else {
            rangeY.set(VALUE);
        }
    }
    public DoubleProperty rangeYProperty() {
        if (null == rangeY) {
            rangeY = new DoublePropertyBase(_rangeY) {
                @Override protected void invalidated() { resize(); }
                @Override public Object getBean() { return XYPane.this; }
                @Override public String getName() { return "rangeY"; }
            };
        }
        return rangeY;
    }

    public List<XYSeries<T>> getListOfSeries() { return listOfSeries; }


    // ******************** Draw Chart ****************************************
    private void drawChart() {
        if (null == listOfSeries || listOfSeries.isEmpty()) return;

        ctx.clearRect(0, 0, width, height);
        ctx.setFill(getChartBackgroundPaint());
        ctx.fillRect(0, 0, width, height);

        listOfSeries.forEach(series -> {
            final ChartType TYPE        = series.getChartType();
            final boolean   SHOW_POINTS = series.isShowPoints();
            switch(TYPE) {
                case LINE       : drawLine(series, SHOW_POINTS);break;
                case SMOOTH_LINE: drawSmoothLine(series, 0.5, 16, SHOW_POINTS);break;
                case AREA       : drawArea(series, SHOW_POINTS);break;
                case SMOOTH_AREA: drawSmoothArea(series, 0.5, 16, SHOW_POINTS);break;
                case SCATTER    : drawScatter(series);break;
            }
        });
    }

    private void drawLine(final XYSeries<T> SERIES, final boolean SHOW_POINTS) {
        double oldX = 0;
        double oldY = height;
        ctx.setStroke(SERIES.getStroke());
        ctx.setFill(Color.TRANSPARENT);
        for (XYData item : SERIES.getItems()) {
            double x = item.getX() * scaleX;
            double y = height - item.getY() * scaleY;
            ctx.strokeLine(oldX, oldY, x, y);
            oldX = x;
            oldY = y;
        }

        if (SHOW_POINTS) { drawSymbols(SERIES); }
    }

    private void drawArea(final XYSeries<T> SERIES, final boolean SHOW_POINTS) {
        double oldX = 0;
        ctx.setStroke(SERIES.getStroke());
        ctx.setFill(SERIES.getFill());
        ctx.beginPath();
        ctx.moveTo(SERIES.getItems().get(0).getX() * scaleX, height);
        for (XYData item : SERIES.getItems()) {
            double x = item.getX() * scaleX;
            double y = height - item.getY() * scaleY;
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
        ctx.setStroke(Color.TRANSPARENT);
        ctx.setFill(Color.TRANSPARENT);
        for (XYData item : SERIES.getItems()) {
            double x = item.getX() * scaleX;
            double y = item.getY() * scaleY;
            drawSymbol(x, height - y, item.getColor(), item.getSymbol());
        }
    }

    private void drawSmoothLine(final XYSeries<T> SERIES, final double TENSION, final int SUB_DEVISIONS, final boolean SHOW_POINTS) {
        ctx.setStroke(SERIES.getStroke());
        ctx.setFill(Color.TRANSPARENT);

        List<Double> pointList = new ArrayList<>(SERIES.getItems().size() * 2);
        SERIES.getItems().forEach(item -> {
            pointList.add(item.getX());
            pointList.add(item.getY());
        });
        Double[] points = new Double[pointList.size()];
        points = pointList.toArray(points);

        List<Double> curvePoints = getCurvePoints(points, TENSION, SUB_DEVISIONS);

        ctx.beginPath();
        //ctx.moveTo(POINTS.get(0), POINTS.get(1));
        for(int i = 2 ; i < curvePoints.size() - 1 ; i += 2) {
            ctx.lineTo(curvePoints.get(i) * scaleX, height - curvePoints.get(i + 1) * scaleY);
        }
        ctx.stroke();

        if (SHOW_POINTS) { drawSymbols(SERIES); }
    }

    private void drawSmoothArea(final XYSeries<T> SERIES, final double TENSION, final int SUB_DEVISIONS, final boolean SHOW_POINTS) {
        ctx.setStroke(SERIES.getStroke());
        ctx.setFill(SERIES.getFill());
        double oldX = 0;
        List<Double> pointList = new ArrayList<>(SERIES.getItems().size() * 2);
        SERIES.getItems().forEach(item -> {
            pointList.add(item.getX());
            pointList.add(item.getY());
        });
        Double[] points = new Double[pointList.size()];
        points = pointList.toArray(points);

        List<Double> curvePoints = getCurvePoints(points, TENSION, SUB_DEVISIONS);

        ctx.beginPath();
        ctx.moveTo(SERIES.getItems().get(0).getX() * scaleX, height);
        //ctx.moveTo(POINTS.get(0), POINTS.get(1));
        for(int i = 2 ; i < curvePoints.size() - 1 ; i += 2) {
            ctx.lineTo(curvePoints.get(i) * scaleX, height - curvePoints.get(i + 1) * scaleY);
            oldX = curvePoints.get(i) * scaleX;
        }
        ctx.lineTo(oldX, height);
        ctx.lineTo(0, height);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();

        if (SHOW_POINTS) { drawSymbols(SERIES); }
    }

    private List<Double> getCurvePoints(final Double[] POINTS, final double TENSION, final int NUMBER_OF_SEGMENTS) {
        List<Double> _pts = new ArrayList<>();
        List<Double> res  = new ArrayList<>();

        // clone array so we don't change the original
        for (double p : POINTS) { _pts.add(p); }

        // The algorithm require a previous and next point to the actual point array.
        // Check if we will draw closed or open curve.
        // If closed, copy end points to beginning and first points to end
        // If open, duplicate first points to beginning, end points to end
        /*
        if (IS_CLOSED) {
            _pts.add(0, POINTS[POINTS.length - 1]);
            _pts.add(0, POINTS[POINTS.length - 2]);
            _pts.add(0, POINTS[POINTS.length - 1]);
            _pts.add(0, POINTS[POINTS.length - 2]);
            _pts.add(POINTS[0]);
            _pts.add(POINTS[1]);
        } else {
            _pts.add(0, POINTS[1]);	        //copy 1. point and insert at beginning
            _pts.add(0, POINTS[0]);
            _pts.add(POINTS[POINTS.length - 2]);	//copy last point and append
            _pts.add(POINTS[POINTS.length - 1]);
        }
        */
        if (POINTS.length > 0) {
            _pts.add(0, POINTS[1]);            //copy 1. point and insert at beginning
            _pts.add(0, POINTS[0]);
            _pts.add(POINTS[POINTS.length - 2]);    //copy last point and append
            _pts.add(POINTS[POINTS.length - 1]);
        }
        // 1. loop goes through point array
        // 2. loop goes through each segment between the 2 POINTS + 1e point before and after
        for (int i = 2 ; i < (_pts.size() - 4) ; i += 2) {
            for (double t = 0 ; t <= NUMBER_OF_SEGMENTS ; t++) {

                // calc TENSION vectors
                double t1x = (_pts.get(i + 2) - _pts.get(i - 2)) * TENSION;
                double t2x = (_pts.get(i + 4) - _pts.get(i)) * TENSION;

                double t1y = (_pts.get(i + 3) - _pts.get(i - 1)) * TENSION;
                double t2y = (_pts.get(i + 5) - _pts.get(i + 1)) * TENSION;

                // calc step
                double st = t / NUMBER_OF_SEGMENTS;

                // calc cardinals
                double c1 =   2 * Math.pow(st, 3) 	- 3 * Math.pow(st, 2) + 1;
                double c2 = -(2 * Math.pow(st, 3)) + 3 * Math.pow(st, 2);
                double c3 = 	   Math.pow(st, 3)	- 2 * Math.pow(st, 2) + st;
                double c4 = 	   Math.pow(st, 3)	- 	  Math.pow(st, 2);

                // calc x and y cords with common control vectors
                double x = c1 * _pts.get(i)     + c2 * _pts.get(i + 2) + c3 * t1x + c4 * t2x;
                double y = c1 * _pts.get(i + 1) + c2 * _pts.get(i + 3) + c3 * t1y + c4 * t2y;

                //store points in array
                res.add(x);
                res.add(y);
            }
        }
        return res;
    }

    private void drawSymbols(final XYSeries<T> SERIES) {
        for (XYData item : SERIES.getItems()) {
            double x = item.getX() * scaleX;
            double y = item.getY() * scaleY;
            drawSymbol(x, height - y, item.getColor(), item.getSymbol());
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
