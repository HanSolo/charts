package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.XYData;
import eu.hansolo.fx.charts.data.XYDataObject;
import eu.hansolo.fx.charts.data.XYZData;
import eu.hansolo.fx.charts.data.YData;
import eu.hansolo.fx.charts.series.XYSeries;
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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SingleChartTest extends Application {
    private enum Alignment { TOP, RIGHT, BOTTOM, LEFT, CENTER }
    private static final Double    AXIS_WIDTH     = 25d;
    private static final Color[]   COLORS         = { Color.RED, Color.BLUE, Color.CYAN, Color.LIME };
    private static final Random    RND            = new Random();
    private static final int       NO_OF_X_VALUES = 20;
    private XYSeries<XYDataObject> xySeries1;
    private XYSeries<XYDataObject> xySeries2;
    private XYSeries<XYDataObject> xySeries3;
    private XYSeries<XYDataObject> xySeries4;

    private XYChart<XYDataObject>  xyChart;
    private Axis                   lineChartXAxisBottom;
    private Axis                   lineChartYAxisLeft;
    private Axis                   lineChartYAxisRight;

    private Thread                 thread1;
    private Thread                 thread2;

    private long                   lastTimerCall;
    private AnimationTimer         timer;


    @Override public void init() {
        List<XYDataObject> xyData1 = new ArrayList<>(20);
        List<XYDataObject> xyData2 = new ArrayList<>(20);
        List<XYDataObject> xyData3 = new ArrayList<>(20);
        List<XYDataObject> xyData4 = new ArrayList<>(20);

        for (int i = 0 ; i < NO_OF_X_VALUES ; i++) {
            xyData1.add(new XYDataObject(i, RND.nextDouble() * 12 + RND.nextDouble() * 6, "P" + i, COLORS[RND.nextInt(3)]));
            xyData2.add(new XYDataObject(i, RND.nextDouble() * 7 + RND.nextDouble() * 3, "P" + i, COLORS[RND.nextInt(3)]));
            xyData3.add(new XYDataObject(i, RND.nextDouble() * 3 + RND.nextDouble() * 4, "P" + i, COLORS[RND.nextInt(3)]));
            xyData4.add(new XYDataObject(i, RND.nextDouble() * 4, "P" + i, COLORS[RND.nextInt(3)]));
        }

        xySeries1 = new XYSeries(xyData1, ChartType.AREA, Color.RED, Color.rgb(255, 0, 0, 0.5));
        xySeries2 = new XYSeries(xyData2, ChartType.AREA, Color.LIME, Color.rgb(0, 255, 0, 0.5));
        xySeries3 = new XYSeries(xyData3, ChartType.AREA, Color.BLUE, Color.rgb(0, 0, 255, 0.5));
        xySeries4 = new XYSeries(xyData4, ChartType.AREA, Color.MAGENTA, Color.rgb(255, 0, 255, 0.5));


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


        thread1 = new Thread(() -> {
            while(true) {
                List<XYData> xyItems = xySeries3.getItems();
                xyItems.forEach(item -> item.setY(RND.nextDouble() * 3 + RND.nextDouble() * 4));
                xySeries3.refresh();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread2 = new Thread(() -> {
            while(true) {
                List<XYData> xyItems = xySeries4.getItems();
                xyItems.forEach(item -> item.setY(RND.nextDouble() * 4));
                //xySeries4.refresh();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 1_000_000_000l) {
                    List<XYData> xyItems = xySeries1.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 8 + RND.nextDouble() * 10));

                    xyItems = xySeries2.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 4 + RND.nextDouble() * 10));

                    xySeries1.refresh();
                    xySeries2.refresh();

                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(xyChart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(new StackPane(pane));

        stage.setTitle("Charts");
        stage.setScene(scene);
        stage.show();

        timer.start();

        thread1.start();

        thread2.start();
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
