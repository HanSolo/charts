package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.XYData;
import eu.hansolo.fx.charts.data.XYDataObject;
import eu.hansolo.fx.charts.data.YData;
import eu.hansolo.fx.charts.data.YDataObject;
import eu.hansolo.fx.charts.series.XYSeries;
import eu.hansolo.fx.charts.series.YSeries;
import eu.hansolo.fx.charts.unit.Unit;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SingleChartTest extends Application {
    private static final Double    AXIS_WIDTH      = 25d;
    private static final Color[]   COLORS          = { Color.RED, Color.BLUE, Color.CYAN, Color.LIME };
    private static final Random    RND             = new Random();
    private static final int       NO_OF_X_VALUES  = 1000;
    private static final long      UPDATE_INTERVAL = 1_000_000_000l;
    private XYSeries<XYDataObject> xySeries1;
    private XYSeries<XYDataObject> xySeries2;
    private XYSeries<XYDataObject> xySeries3;
    private XYSeries<XYDataObject> xySeries4;

    private XYChart<XYDataObject>  xyChart;
    private Axis                   lineChartXAxisBottom;
    private Axis                   lineChartYAxisLeft;
    private Axis                   lineChartYAxisRight;

    private YSeries<YDataObject>   ySeries1;
    private YSeries<YDataObject>   ySeries2;
    private YChart<YDataObject>    yChart;

    private long                   lastTimerCall;
    private AnimationTimer         timer;


    @Override public void init() {
        List<XYDataObject> xyData1 = new ArrayList<>(NO_OF_X_VALUES);
        List<XYDataObject> xyData2 = new ArrayList<>(NO_OF_X_VALUES);
        List<XYDataObject> xyData3 = new ArrayList<>(NO_OF_X_VALUES);
        List<XYDataObject> xyData4 = new ArrayList<>(NO_OF_X_VALUES);

        for (int i = 0 ; i < NO_OF_X_VALUES ; i++) {
            xyData1.add(new XYDataObject(i, RND.nextDouble() * 12 + RND.nextDouble() * 6, "P" + i, COLORS[RND.nextInt(3)]));
            xyData2.add(new XYDataObject(i, RND.nextDouble() * 7 + RND.nextDouble() * 3, "P" + i, COLORS[RND.nextInt(3)]));
            xyData3.add(new XYDataObject(i, RND.nextDouble() * 3 + RND.nextDouble() * 4, "P" + i, COLORS[RND.nextInt(3)]));
            xyData4.add(new XYDataObject(i, RND.nextDouble() * 4, "P" + i, COLORS[RND.nextInt(3)]));
        }

        xySeries1 = new XYSeries(xyData1, ChartType.LINE, Color.RED, Color.rgb(255, 0, 0, 0.5));
        xySeries2 = new XYSeries(xyData2, ChartType.LINE, Color.LIME, Color.rgb(0, 255, 0, 0.5));
        xySeries3 = new XYSeries(xyData3, ChartType.LINE, Color.BLUE, Color.rgb(0, 0, 255, 0.5));
        xySeries4 = new XYSeries(xyData4, ChartType.LINE, Color.MAGENTA, Color.rgb(255, 0, 255, 0.5));

        xySeries1.setShowPoints(false);
        xySeries2.setShowPoints(false);
        xySeries3.setShowPoints(false);
        xySeries4.setShowPoints(false);

        // XYChart
        Unit   tempUnit          = new Unit(Unit.Type.TEMPERATURE, Unit.Definition.CELSIUS); // Type Temperature with BaseUnit Celsius
        double tempFahrenheitMin = tempUnit.convert(0, Unit.Definition.FAHRENHEIT);
        double tempFahrenheitMax = tempUnit.convert(20, Unit.Definition.FAHRENHEIT);

        lineChartXAxisBottom = createBottomXAxis(0, NO_OF_X_VALUES, true);
        lineChartYAxisLeft   = createLeftYAxis(0, 20, true);
        lineChartYAxisRight  = createRightYAxis(tempFahrenheitMin, tempFahrenheitMax, false);
        xyChart = new XYChart<>(new XYPane(xySeries1, xySeries2, xySeries3, xySeries4),
                                lineChartYAxisLeft, lineChartYAxisRight, lineChartXAxisBottom);

        // YChart
        List<YDataObject> yData1 = new ArrayList<>(20);
        List<YDataObject> yData2 = new ArrayList<>(20);
        for (int i = 0 ; i < 20 ; i++) {
            yData1.add(new YDataObject(RND.nextDouble() * 100, "P" + i, COLORS[RND.nextInt(3)]));
            yData2.add(new YDataObject(RND.nextDouble() * 100, "P" + i, COLORS[RND.nextInt(3)]));
        }

        ySeries1 = new YSeries(yData1, ChartType.RADAR_POLYGON, Color.TRANSPARENT, new RadialGradient(0, 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(255, 0, 0, 0.8)), new Stop(0.5, Color.rgb(255, 255, 0, 0.8)), new Stop(1.0, Color.rgb(0, 200, 0, 0.8))));
        ySeries2 = new YSeries(yData2, ChartType.RADAR_POLYGON, Color.TRANSPARENT, new RadialGradient(0, 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(0, 0, 255, 0.5)), new Stop(1.0, Color.rgb(0, 0, 255, 0.8))));
        yChart   = new YChart(new YPane(ySeries1));


        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + UPDATE_INTERVAL) {
                    List<XYData> xyItems = xySeries1.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 8 + RND.nextDouble() * 10));

                    xyItems = xySeries2.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 4 + RND.nextDouble() * 10));

                    xyItems = xySeries3.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 3 + RND.nextDouble() * 4));

                    xyItems = xySeries4.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 4));

                    List<YData> yItems = ySeries1.getItems();
                    yItems.forEach(item -> item.setY(RND.nextDouble() * 100));

                    yItems = ySeries2.getItems();
                    yItems.forEach(item -> item.setY(RND.nextDouble() * 100));

                    // Can be used to update charts but if more than one series is in one xyPane
                    // it's easier to use the refresh() method of XYChart
                    //xySeries1.refresh();
                    //xySeries2.refresh();
                    //xySeries3.refresh();
                    //xySeries4.refresh();

                    // Useful to refresh the chart if it contains more than one series to avoid
                    // multiple redraws
                    xyChart.refresh();

                    //ySeries1.refresh();
                    //ySeries2.refresh();

                    yChart.refresh();

                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(yChart);
        //StackPane pane = new StackPane(xyChart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(new StackPane(pane));

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
