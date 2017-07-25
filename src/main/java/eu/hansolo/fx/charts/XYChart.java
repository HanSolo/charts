package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.XYData;
import eu.hansolo.fx.charts.model.XYChartModel;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hansolo on 16.07.17.
 */
public class XYChart<T extends XYData> extends Region implements Chart {
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
    private              Pane                  pane;
    private              Color                 _chartBackgroundColor;
    private              ObjectProperty<Color> chartBackgroundColor;
    private              XYChartModel<T>       model;
    private              Canvas                canvas;
    private              GraphicsContext       ctx;
    private              double                scaleX;
    private              double                scaleY;
    private              double                symbolSize;
    private              ChartType             chartType;
    private              Color                 strokeColor;
    private              Color                 fillColor;
    private              double                _rangeX;
    private              DoubleProperty        rangeX;
    private              double                _rangeY;
    private              DoubleProperty        rangeY;


    // ******************** Constructors **************************************
    public XYChart(final XYChartModel<T> MODEL) {
        this(MODEL, ChartType.SCATTER);
    }
    public XYChart(final XYChartModel<T> MODEL, final ChartType TYPE) {
        this(MODEL, TYPE, Color.BLACK, Color.TRANSPARENT);
    }
    public XYChart(final XYChartModel<T> MODEL, final ChartType TYPE, final Color STROKE_COLOR, final Color FILL_COLOR) {
        getStylesheets().add(XYChart.class.getResource("chart.css").toExternalForm());
        aspectRatio           = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        keepAspect            = false;
        _chartBackgroundColor = Color.WHITE;
        model                 = MODEL;
        scaleX                = 1;
        scaleY                = 1;
        symbolSize            = 2;
        chartType             = TYPE;
        strokeColor           = STROKE_COLOR;
        fillColor             = FILL_COLOR;
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

        model.setOnModelEvent(modelEvent -> redraw());
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT)  { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH)  { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT)  { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH)  { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    @Override public ChartType getChartType() { return chartType; }
    @Override public void setChartType(final ChartType TYPE) {
        chartType = TYPE;
        redraw();
    }

    public Color getChartBackgroundColor() { return null == chartBackgroundColor ? _chartBackgroundColor : chartBackgroundColor.get(); }
    public void setChartBackgroundColor(final Color COLOR) {
        if (null == chartBackgroundColor) {
            _chartBackgroundColor = COLOR;
            redraw();
        } else {
            chartBackgroundColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> chartBackgroundProperty() {
        if (null == chartBackgroundColor) {
            chartBackgroundColor = new ObjectPropertyBase<Color>(_chartBackgroundColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return XYChart.this; }
                @Override public String getName() { return "chartBackgroundColor"; }
            };
            _chartBackgroundColor = null;
        }
        return chartBackgroundColor;
    }

    public Color getStrokeColor() { return strokeColor; }
    public void setStrokeColor(final Color COLOR) {
        strokeColor = COLOR;
        redraw();
    }

    public Color getFillColor() { return fillColor; }
    public void setFillColor(final Color COLOR) {
        fillColor = COLOR;
        redraw();
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
                @Override public Object getBean() {  return XYChart.this;  }
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
                @Override public Object getBean() { return XYChart.this; }
                @Override public String getName() { return "rangeY"; }
            };
        }
        return rangeY;
    }

    public XYChartModel<T> getModel() { return model; }


    // ******************** Draw Chart ****************************************
    private void drawChart() {
        if (null == model) return;

        ctx.setFill(getChartBackgroundColor());
        ctx.clearRect(0, 0, width, height);

        ctx.setStroke(strokeColor);
        ctx.setFill(Color.TRANSPARENT);

        if (ChartType.SMOOTH_LINE == chartType) {
            drawSmoothLine(0.5, false , 32, true);
        } else if (ChartType.LINE == chartType) {
            drawLine();
            drawSymbols();
        } else if (ChartType.AREA == chartType) {
            drawArea();
            drawSymbols();
        } else if (ChartType.SCATTER == chartType) {
            drawScatter();
        }
    }

    private void drawLine() {
        double oldX = 0;
        double oldY = height;
        for (XYData item : model.getItems()) {
            double x = item.getX() * scaleX;
            double y = height - item.getY() * scaleY;
            ctx.strokeLine(oldX, oldY, x, y);
            oldX = x;
            oldY = y;
        }
    }

    private void drawArea() {
        double oldX = 0;
        ctx.beginPath();
        ctx.moveTo(model.getItems().get(0).getX() * scaleX, height);
        for (XYData item : model.getItems()) {
            double x = item.getX() * scaleX;
            double y = height - item.getY() * scaleY;
            ctx.lineTo(x, y);
            oldX = x;
        }
        ctx.lineTo(oldX, height);
        ctx.lineTo(0, height);
        ctx.closePath();
        ctx.setFill(fillColor);
        ctx.fill();
        ctx.stroke();
    }

    private void drawScatter() {
        for (XYData item : model.getItems()) {
            double x = item.getX() * scaleX;
            double y = item.getY() * scaleY;
            drawSymbol(x, height - y, item.getColor(), item.getSymbol());
        }
    }

    private void drawSmoothLine(final double TENSION, final boolean IS_CLOSED, final int NUMBER_OF_SEGMENTS, final boolean SHOW_POINTS) {
        List<Double> pointList = new ArrayList<>(model.getItems().size() * 2);
        model.getItems().forEach(item -> {
            pointList.add(item.getX());
            pointList.add(item.getY());
        });
        Double[] points = new Double[pointList.size()];
        points = pointList.toArray(points);

        List<Double> curvePoints = getCurvePoints(points, TENSION, IS_CLOSED, NUMBER_OF_SEGMENTS);

        ctx.beginPath();
        //ctx.moveTo(POINTS.get(0), POINTS.get(1));
        for(int i = 2 ; i < curvePoints.size() - 1 ; i += 2) {
            ctx.lineTo(curvePoints.get(i) * scaleX, curvePoints.get(i + 1) * scaleY);
        }
        ctx.stroke();

        if (SHOW_POINTS) {
            model.getItems().forEach(item -> drawSymbol(item.getX() * scaleX, item.getY() * scaleY, item.getColor(), item.getSymbol()));
        }
    }

    private List<Double> getCurvePoints(final Double[] POINTS, final double TENSION, final boolean IS_CLOSED, final int NUMBER_OF_SEGMENTS) {
        List<Double> _pts = new ArrayList<>();
        List<Double> res  = new ArrayList<>();

        // clone array so we don't change the original
        for (double p : POINTS) { _pts.add(p); }

        // The algorithm require a previous and next point to the actual point array.
        // Check if we will draw closed or open curve.
        // If closed, copy end points to beginning and first points to end
        // If open, duplicate first points to beginning, end points to end
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

    private void drawSymbols() {
        for (XYData item : model.getItems()) {
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

            symbolSize = size * 0.016;

            scaleX = width / getRangeX();
            scaleY = height / getRangeY();

            redraw();
        }
    }

    private void redraw() {
        drawChart();
    }
}
