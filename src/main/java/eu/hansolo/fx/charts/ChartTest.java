package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.XYData;
import eu.hansolo.fx.charts.data.XYDataObject;
import eu.hansolo.fx.charts.data.XYZData;
import eu.hansolo.fx.charts.data.XYZDataObject;
import eu.hansolo.fx.charts.data.YData;
import eu.hansolo.fx.charts.data.YDataObject;
import eu.hansolo.fx.charts.model.XYZChartModel;
import eu.hansolo.fx.charts.model.YChartModel;
import eu.hansolo.fx.charts.model.XYChartModel;
import eu.hansolo.fx.charts.unit.Unit;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by hansolo on 16.07.17.
 */
public class ChartTest extends Application {
    private static final Color[]         COLORS = { Color.RED, Color.BLUE, Color.CYAN, Color.LIME };
    private static final Random          RND    = new Random();
    private XYChartModel<XYDataObject>   xyChartModel;
    private XYChart<XYDataObject>        scatterChart;
    private XYChart<XYDataObject>        lineChart;
    private XYChart<XYDataObject>        areaChart;
    private YChartModel<YDataObject>     yChartModel;
    private DonutChart<YDataObject>      donutChart;
    private XYZChartModel<XYZDataObject> xyzChartModel;
    private XYZChart<XYZDataObject>      bubbleChart;
    private Axis                         xAxisBottom;
    private Axis                         yAxisLeft;
    private Axis                         yAxisRight;

    private long                         lastTimerCall;
    private AnimationTimer               timer;


    @Override public void init() {
        List<XYDataObject> xyData   = new ArrayList<>(20);
        List<YDataObject>  yData    = new ArrayList<>(20);
        List<XYZDataObject> xyzData = new ArrayList<>(20);
        for (int i = 0 ; i < 20 ; i++) {
            xyData.add(new XYDataObject(i, RND.nextDouble() * 15, "P" + i, COLORS[RND.nextInt(3)]));
        }
        for (int i = 0 ; i < 20 ; i++) {
            yData.add(new YDataObject(RND.nextDouble() * 10, "P" + i, COLORS[RND.nextInt(3)]));
            xyzData.add(new XYZDataObject(RND.nextDouble() * 10, RND.nextDouble() * 10, RND.nextDouble() * 25,"P" + i, COLORS[RND.nextInt(3)]));
        }

        xyChartModel  = new XYChartModel(xyData);
        scatterChart  = new XYChart(xyChartModel, ChartType.SCATTER);
        lineChart     = new XYChart(xyChartModel, ChartType.SMOOTH_AREA);
        areaChart     = new XYChart(xyChartModel, ChartType.AREA, Color.BLACK, Color.rgb(255, 0, 0, 0.5));

        yChartModel   = new YChartModel(yData);
        donutChart    = new DonutChart(yChartModel);

        xyzChartModel = new XYZChartModel<>(xyzData);
        bubbleChart   = new XYZChart<>(xyzChartModel, ChartType.BUBBLE);

        xAxisBottom = new Axis(Orientation.HORIZONTAL, Pos.BOTTOM_CENTER);
        xAxisBottom.setMinValue(0);
        xAxisBottom.setMaxValue(20);
        xAxisBottom.prefWidthProperty().bind(lineChart.widthProperty());
        xAxisBottom.setPrefHeight(25);

        yAxisLeft = new Axis(Orientation.VERTICAL, Pos.CENTER_LEFT);
        yAxisLeft.setMinValue(0);
        yAxisLeft.setMaxValue(20);
        yAxisLeft.prefHeightProperty().bind(lineChart.heightProperty());
        yAxisLeft.setPrefWidth(25);

        Unit   tempUnit          = new Unit(Unit.Type.TEMPERATURE, Unit.Definition.CELSIUS); // Type Temperature with BaseUnit Celsius
        double tempFahrenheitMin = tempUnit.convert(0, Unit.Definition.FAHRENHEIT);
        double tempFahrenheitMax = tempUnit.convert(20, Unit.Definition.FAHRENHEIT);

        yAxisRight = new Axis(Orientation.VERTICAL, Pos.CENTER_RIGHT);
        yAxisRight.setMinValue(tempFahrenheitMin);
        yAxisRight.setMaxValue(tempFahrenheitMax);
        yAxisRight.setPrefWidth(25);
        yAxisRight.setAutoScale(false);


        lineChart.setRangeX(xAxisBottom.getRange());
        lineChart.setRangeY(yAxisLeft.getRange());
        lineChart.setFillPaint(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(255, 0, 0, 0.6)), new Stop(1.0, Color.TRANSPARENT)));
        lineChart.setStrokePaint(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(255, 0, 0, 1.0)), new Stop(1.0, Color.TRANSPARENT)));
        lineChart.setChartBackgroundPaint(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(50, 50, 50, 0.25)), new Stop(1.0, Color.rgb(25, 25, 25, 0.8))));

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 1_000_000_000l) {
                    List<XYData> xyItems = xyChartModel.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 15));

                    List<YData> yItems = yChartModel.getItems();
                    yItems.forEach(item -> item.setY(RND.nextDouble() * 20));

                    List<XYZData> xyzItems = xyzChartModel.getItems();
                    xyzItems.forEach(item -> item.setZ(RND.nextDouble() * 25));

                    xyChartModel.refresh();
                    yChartModel.refresh();
                    xyzChartModel.refresh();

                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        AnchorPane pane = new AnchorPane(yAxisLeft, yAxisRight, xAxisBottom, lineChart);

        AnchorPane.setTopAnchor(yAxisLeft, 0d);
        AnchorPane.setBottomAnchor(yAxisLeft, 25d);
        AnchorPane.setLeftAnchor(yAxisLeft, 0d);

        AnchorPane.setRightAnchor(yAxisRight, 0d);
        AnchorPane.setTopAnchor(yAxisRight, 0d);
        AnchorPane.setBottomAnchor(yAxisRight, 25d);

        AnchorPane.setBottomAnchor(xAxisBottom, 0d);
        AnchorPane.setLeftAnchor(xAxisBottom, 25d);
        AnchorPane.setRightAnchor(xAxisBottom, 25d);

        AnchorPane.setTopAnchor(lineChart, 0d);
        AnchorPane.setRightAnchor(lineChart, 25d);
        AnchorPane.setLeftAnchor(lineChart, 25d);
        AnchorPane.setBottomAnchor(lineChart, 25d);


        Scene scene = new Scene(new StackPane(pane));

        stage.setTitle("Charts");
        stage.setScene(scene);
        stage.show();

        timer.start();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}