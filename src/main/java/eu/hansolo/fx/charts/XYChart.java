package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.XYData;
import eu.hansolo.fx.charts.model.XYChartModel;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


/**
 * Created by hansolo on 16.07.17.
 */
public class XYChart<T extends XYData> extends Region implements Chart {
    private static final double          PREFERRED_WIDTH  = 250;
    private static final double          PREFERRED_HEIGHT = 250;
    private static final double          MINIMUM_WIDTH    = 0;
    private static final double          MINIMUM_HEIGHT   = 0;
    private static final double          MAXIMUM_WIDTH    = 4096;
    private static final double          MAXIMUM_HEIGHT   = 4096;
    private static       double          aspectRatio;
    private              boolean         keepAspect;
    private              double          size;
    private              double          width;
    private              double          height;
    private              Pane            pane;
    private              Paint           backgroundPaint;
    private              Paint           borderPaint;
    private              double          borderWidth;
    private              XYChartModel<T> model;
    private              Canvas          canvas;
    private              GraphicsContext ctx;
    private              double          scaleX;
    private              double          scaleY;
    private              double          symbolSize;
    private              ChartType       chartType;
    private              Color           strokeColor;
    private              Color           fillColor;


    // ******************** Constructors **************************************
    public XYChart(final XYChartModel<T> MODEL) {
        this(MODEL, ChartType.SCATTER);
    }
    public XYChart(final XYChartModel<T> MODEL, final ChartType TYPE) {
        this(MODEL, TYPE, Color.BLACK, Color.TRANSPARENT);
    }
    public XYChart(final XYChartModel<T> MODEL, final ChartType TYPE, final Color STROKE_COLOR, final Color FILL_COLOR) {
        getStylesheets().add(XYChart.class.getResource("chart.css").toExternalForm());
        aspectRatio     = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        keepAspect      = false;
        backgroundPaint = Color.TRANSPARENT;
        borderPaint     = Color.TRANSPARENT;
        borderWidth     = 0d;
        model           = MODEL;
        scaleX          = 1;
        scaleY          = 1;
        symbolSize      = 2;
        chartType       = TYPE;
        strokeColor     = STROKE_COLOR;
        fillColor       = FILL_COLOR;

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
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth))));

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

    public XYChartModel<T> getModel() { return model; }


    // ******************** Draw Chart ****************************************
    private void drawChart() {
        if (null == model) return;
        double halfSymbolSize = symbolSize * 0.5;
        double oldX           = 0;
        double oldY           = height;

        ctx.setFill(Color.WHITE);
        ctx.clearRect(0, 0, width, height);
        if (ChartType.AREA == chartType) {
            ctx.beginPath();
            ctx.moveTo(0, height);
        }
        for (XYData item : model.getItems()) {
            double x = item.getX() * scaleX;
            double y = item.getY() * scaleY;
            switch(chartType) {
                case LINE:
                    ctx.setStroke(strokeColor);
                    ctx.setFill(Color.TRANSPARENT);
                    ctx.strokeLine(oldX, oldY, x, y);
                    drawSymbol(x, y, item.getColor(), item.getSymbol());
                    break;
                case AREA:
                    ctx.lineTo(x, y);
                    drawSymbol(x, y, item.getColor(), item.getSymbol());
                    break;
                case BAR:
                    break;
                case BUBBLE:
                    halfSymbolSize = item.getZ() * 0.5;
                    ctx.setStroke(Color.TRANSPARENT);
                    ctx.setFill(item.getColor());
                    ctx.fillOval(x - halfSymbolSize, height - y - halfSymbolSize, symbolSize, symbolSize);
                    break;
                case SCATTER:
                default     :
                    drawSymbol(x, y, item.getColor(), item.getSymbol());
                    break;
            }
            oldX = x;
            oldY = y;
        }
        if (ChartType.AREA == chartType) {
            ctx.lineTo(width, height);
            ctx.lineTo(0, height);
            ctx.closePath();
            ctx.setFill(fillColor);
            ctx.fill();
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
                ctx.fillRect(X - halfSymbolSize, height - Y - halfSymbolSize, symbolSize, symbolSize);
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
                ctx.fillOval(X - halfSymbolSize, height - Y - halfSymbolSize, symbolSize, symbolSize);
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

            scaleX = width / model.getItems().size();
            scaleY = height / model.getRangeY();

            redraw();
        }
    }

    private void redraw() {
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth / PREFERRED_WIDTH * size))));

        drawChart();
    }
}
