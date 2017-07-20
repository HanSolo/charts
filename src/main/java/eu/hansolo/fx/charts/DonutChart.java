package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.YData;
import eu.hansolo.fx.charts.model.YChartModel;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
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
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Locale;


public class DonutChart<T extends YData> extends Region implements Chart {
    private static final double             PREFERRED_WIDTH  = 250;
    private static final double             PREFERRED_HEIGHT = 250;
    private static final double             MINIMUM_WIDTH    = 0;
    private static final double             MINIMUM_HEIGHT   = 0;
    private static final double             MAXIMUM_WIDTH    = 4096;
    private static final double             MAXIMUM_HEIGHT   = 4096;
    private static double          aspectRatio;
    private        boolean         keepAspect;
    private        double          size;
    private        double          width;
    private        double          height;
    private        Pane            pane;
    private        Paint           backgroundPaint;
    private        Paint           borderPaint;
    private        double          borderWidth;
    private        YChartModel<T>  model;
    private        Canvas          canvas;
    private        GraphicsContext ctx;


    // ******************** Constructors **************************************
    public DonutChart(final YChartModel<T> MODEL) {
        getStylesheets().add(XYChart.class.getResource("chart.css").toExternalForm());
        aspectRatio     = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        keepAspect      = false;
        backgroundPaint = Color.TRANSPARENT;
        borderPaint     = Color.TRANSPARENT;
        borderWidth     = 0d;
        model           = MODEL;

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

        getStyleClass().setAll("chart", "donut-chart");

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

    @Override public ChartType getChartType() { return ChartType.DONUT; }
    @Override public void setChartType(final ChartType TYPE) {}

    public YChartModel<T> getModel() { return model; }


    // ******************** Draw Chart ****************************************
    private void drawChart() {
        if (null == model) return;
        List<YData> items       = model.getItems();
        int         noOfItems   = items.size();
        double      center      = size * 0.5;
        double      innerRadius = size * 0.275;
        double      outerRadius = size * 0.4;
        double      barWidth    = size * 0.1;
        double      sum         = items.stream().mapToDouble(YData::getY).sum();
        double      stepSize    = 360.0 / sum;
        double      angle       = 0;
        double      startAngle  = 90;
        double      xy          = size * 0.1;
        double      wh          = size * 0.8;
        double      x;
        double      y;

        ctx.setFill(Color.WHITE);
        ctx.clearRect(0, 0, width, height);
        ctx.setLineCap(StrokeLineCap.BUTT);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);

        for (YData data : items) {
            double value = data.getY();
            startAngle -= angle;
            angle = value * stepSize;

            // Segment
            ctx.setLineWidth(barWidth);
            ctx.setStroke(data.getColor());
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

            canvas.setWidth(size);
            canvas.setHeight(size);
            canvas.relocate((width - size) * 0.5, (height - size) * 0.5);
            
            redraw();
        }
    }

    private void redraw() {
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth / PREFERRED_WIDTH * size))));

        drawChart();
    }
}
