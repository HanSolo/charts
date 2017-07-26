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
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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
    private enum Alignment { TOP, RIGHT, BOTTOM, LEFT, CENTER }
    private static final Double          AXIS_WIDTH = 25d;
    private static final Color[]         COLORS     = { Color.RED, Color.BLUE, Color.CYAN, Color.LIME };
    private static final Random          RND        = new Random();
    private XYChartModel<XYDataObject>   xyChartModel;

    private XYChart<XYDataObject>        lineChart;
    private Axis                         lineChartXAxisBottom;
    private Axis                         lineChartYAxisLeft;
    private Axis                         lineChartYAxisRight;

    private XYChart<XYDataObject>        areaChart;
    private Axis                         areaChartXAxisBottom;
    private Axis                         areaChartYAxisLeft;

    private XYChart<XYDataObject>        smoothLineChart;
    private Axis                         smoothLineChartXAxisBottom;
    private Axis                         smoothLineChartYAxisLeft;

    private XYChart<XYDataObject>        smoothAreaChart;
    private Axis                         smoothAreaChartXAxisBottom;
    private Axis                         smoothAreaChartYAxisLeft;

    private XYChart<XYDataObject>        scatterChart;
    private Axis                         scatterChartXAxisBottom;
    private Axis                         scatterChartYAxisLeft;

    private YChartModel<YDataObject>     yChartModel;
    private DonutChart<YDataObject>      donutChart;

    private XYZChartModel<XYZDataObject> xyzChartModel;
    private XYZPane<XYZDataObject>       bubbleChart;


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

        yChartModel   = new YChartModel(yData);
        donutChart    = new DonutChart(yChartModel);

        xyzChartModel = new XYZChartModel<>(xyzData);
        bubbleChart   = new XYZPane<>(xyzChartModel, ChartType.BUBBLE);

        // LineChart
        Unit   tempUnit          = new Unit(Unit.Type.TEMPERATURE, Unit.Definition.CELSIUS); // Type Temperature with BaseUnit Celsius
        double tempFahrenheitMin = tempUnit.convert(0, Unit.Definition.FAHRENHEIT);
        double tempFahrenheitMax = tempUnit.convert(20, Unit.Definition.FAHRENHEIT);

        lineChartXAxisBottom = createBottomXAxis(0, 20, true);
        lineChartYAxisLeft   = createLeftYAxis(0, 20, true);
        lineChartYAxisRight  = createRightYAxis(tempFahrenheitMin, tempFahrenheitMax, false);
        lineChart = new XYChart<>(new XYPane(xyChartModel, ChartType.LINE),
                                  lineChartYAxisLeft, lineChartYAxisRight, lineChartXAxisBottom);


        // AreaChart
        areaChartXAxisBottom = createBottomXAxis(0, 20, true);
        areaChartYAxisLeft   = createLeftYAxis(0, 20, true);
        areaChart            = new XYChart<>(new XYPane(xyChartModel, ChartType.AREA, Color.BLACK, Color.rgb(255, 0, 0, 0.5)),
                                             areaChartXAxisBottom, areaChartYAxisLeft);

        areaChart.getXYPane().setFillPaint(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(255, 0, 0, 0.6)), new Stop(1.0, Color.TRANSPARENT)));
        areaChart.getXYPane().setStrokePaint(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(255, 0, 0, 1.0)), new Stop(1.0, Color.TRANSPARENT)));
        areaChart.getXYPane().setChartBackgroundPaint(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(50, 50, 50, 0.25)), new Stop(1.0, Color.rgb(25, 25, 25, 0.8))));

        // SmoothLineChart
        smoothLineChartXAxisBottom = createBottomXAxis(0, 20, true);
        smoothLineChartYAxisLeft   = createLeftYAxis(0, 20, true);
        smoothLineChart            = new XYChart<>(new XYPane(xyChartModel, ChartType.SMOOTH_LINE),
                                                   smoothLineChartYAxisLeft, smoothLineChartXAxisBottom);

        // SmoothAreaChart
        smoothAreaChartXAxisBottom = createBottomXAxis(0, 20, true);
        smoothAreaChartYAxisLeft   = createLeftYAxis(0, 20, true);
        smoothAreaChart            = new XYChart<>(new XYPane(xyChartModel, ChartType.SMOOTH_AREA),
                                                   smoothAreaChartYAxisLeft, smoothAreaChartXAxisBottom);

        smoothAreaChart.getXYPane().setFillPaint(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(255, 255, 255, 0.6)), new Stop(1.0, Color.TRANSPARENT)));
        smoothAreaChart.getXYPane().setStrokePaint(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(255, 255, 255, 1.0)), new Stop(1.0, Color.TRANSPARENT)));
        smoothAreaChart.getXYPane().setChartBackgroundPaint(Color.rgb(25, 25, 25, 0.8));


        // ScatterChart
        scatterChartXAxisBottom = createBottomXAxis(0, 20, true);
        scatterChartYAxisLeft   = createLeftYAxis(0, 20, true);
        scatterChart            = new XYChart<>(new XYPane(xyChartModel, ChartType.SCATTER),
                                                scatterChartXAxisBottom, scatterChartYAxisLeft);



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
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(lineChart, 0, 0);
        gridPane.add(areaChart, 1, 0);
        gridPane.add(smoothLineChart, 0, 1);
        gridPane.add(smoothAreaChart, 1, 1);
        gridPane.add(scatterChart, 0, 2);

        Scene scene = new Scene(new StackPane(gridPane));

        stage.setTitle("Charts");
        stage.setScene(scene);
        stage.show();

        timer.start();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private Axis createLeftYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(Orientation.VERTICAL, Position.LEFT);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefWidth(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setTopAnchor(axis, 0d);
        AnchorPane.setBottomAnchor(axis, 25d);
        AnchorPane.setLeftAnchor(axis, 0d);

        return axis;
    }
    private Axis createRightYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(Orientation.VERTICAL, Position.RIGHT);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefWidth(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setRightAnchor(axis, 0d);
        AnchorPane.setTopAnchor(axis, 0d);
        AnchorPane.setBottomAnchor(axis, 25d);

        return axis;
    }

    private Axis createBottomXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(Orientation.HORIZONTAL, Position.BOTTOM);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefHeight(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setBottomAnchor(axis, 0d);
        AnchorPane.setLeftAnchor(axis, 25d);
        AnchorPane.setRightAnchor(axis, 25d);

        return axis;
    }
    private Axis createTopXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(Orientation.HORIZONTAL, Position.TOP);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefHeight(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setTopAnchor(axis, 25d);
        AnchorPane.setLeftAnchor(axis, 25d);
        AnchorPane.setRightAnchor(axis, 25d);

        return axis;
    }


    public static void main(String[] args) {
        launch(args);
    }
}