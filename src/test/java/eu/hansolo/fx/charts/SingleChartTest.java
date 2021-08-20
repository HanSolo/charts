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

import eu.hansolo.fx.charts.converter.Converter;
import eu.hansolo.fx.charts.data.XYChartItem;
import eu.hansolo.fx.charts.data.ValueChartItem;
import eu.hansolo.fx.charts.series.XYSeries;
import eu.hansolo.fx.charts.series.YSeries;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
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

import static eu.hansolo.fx.charts.converter.Converter.Category.TEMPERATURE;
import static eu.hansolo.fx.charts.converter.Converter.UnitDefinition.CELSIUS;
import static eu.hansolo.fx.charts.converter.Converter.UnitDefinition.FAHRENHEIT;


public class SingleChartTest extends Application {
    private static final Double    AXIS_WIDTH      = 25d;
    private static final Color[]   COLORS          = { Color.RED, Color.BLUE, Color.CYAN, Color.LIME };
    private static final Random    RND             = new Random();
    private static final int       NO_OF_X_VALUES  = 1000;
    private static final long      UPDATE_INTERVAL = 1_000_000_000l;
    private XYSeries<XYChartItem> xySeries1;
    private XYSeries<XYChartItem> xySeries2;
    private XYSeries<XYChartItem> xySeries3;
    private XYSeries<XYChartItem> xySeries4;

    private XYChart<XYChartItem> xyChart;
    private Axis                 lineChartXAxisBottom;
    private Axis                 lineChartYAxisLeft;
    private Axis                 lineChartYAxisRight;

    private YSeries<ValueChartItem> ySeries1;
    private YSeries<ValueChartItem> ySeries2;
    private YSeries<ValueChartItem> ySeries3;
    private YChart<ValueChartItem>  yChart;

    private long                   lastTimerCall;
    private AnimationTimer         timer;


    @Override public void init() {
        List<XYChartItem> xyItem1 = new ArrayList<>(NO_OF_X_VALUES);
        List<XYChartItem> xyItem2 = new ArrayList<>(NO_OF_X_VALUES);
        List<XYChartItem> xyItem3 = new ArrayList<>(NO_OF_X_VALUES);
        List<XYChartItem> xyItem4 = new ArrayList<>(NO_OF_X_VALUES);

        for (int i = 0 ; i < NO_OF_X_VALUES ; i++) {
            xyItem1.add(new XYChartItem(i, RND.nextDouble() * 12 + RND.nextDouble() * 6, "P" + i, COLORS[RND.nextInt(3)]));
            xyItem2.add(new XYChartItem(i, RND.nextDouble() * 7 + RND.nextDouble() * 3, "P" + i, COLORS[RND.nextInt(3)]));
            xyItem3.add(new XYChartItem(i, RND.nextDouble() * 3 + RND.nextDouble() * 4, "P" + i, COLORS[RND.nextInt(3)]));
            xyItem4.add(new XYChartItem(i, RND.nextDouble() * 4, "P" + i, COLORS[RND.nextInt(3)]));
        }

        xySeries1 = new XYSeries(xyItem1, ChartType.LINE, Color.rgb(255, 0, 0, 0.5), Color.RED);
        xySeries2 = new XYSeries(xyItem2, ChartType.LINE, Color.rgb(0, 255, 0, 0.5), Color.LIME);
        xySeries3 = new XYSeries(xyItem3, ChartType.LINE, Color.rgb(0, 0, 255, 0.5), Color.BLUE);
        xySeries4 = new XYSeries(xyItem4, ChartType.LINE, Color.rgb(255, 0, 255, 0.5), Color.MAGENTA);

        xySeries1.setSymbolsVisible(false);
        xySeries2.setSymbolsVisible(false);
        xySeries3.setSymbolsVisible(false);
        xySeries4.setSymbolsVisible(false);

        // XYChart
        Converter tempConverter     = new Converter(TEMPERATURE, CELSIUS); // Type Temperature with BaseUnit Celsius
        double    tempFahrenheitMin = tempConverter.convert(0, FAHRENHEIT);
        double    tempFahrenheitMax = tempConverter.convert(20, FAHRENHEIT);

        lineChartXAxisBottom = createBottomXAxis(0, NO_OF_X_VALUES, true);
        lineChartYAxisLeft   = createLeftYAxis(0, 20, true);
        lineChartYAxisRight  = createRightYAxis(tempFahrenheitMin, tempFahrenheitMax, false);
        xyChart = new XYChart<>(new XYPane(xySeries1, xySeries2, xySeries3, xySeries4),
                                lineChartYAxisLeft, lineChartYAxisRight, lineChartXAxisBottom);

        // YChart
        List<ValueChartItem> yItem1 = new ArrayList<>(20);
        List<ValueChartItem> yItem2 = new ArrayList<>(20);
        List<ValueChartItem> yItem3 = new ArrayList<>(20);
        for (int i = 0 ; i < 20 ; i++) {
            yItem1.add(new ValueChartItem(RND.nextDouble() * 100, "P" + i, COLORS[RND.nextInt(3)]));
            yItem2.add(new ValueChartItem(RND.nextDouble() * 100, "P" + i, COLORS[RND.nextInt(3)]));
            yItem3.add(new ValueChartItem(RND.nextDouble() * 100, "P" + i, COLORS[RND.nextInt(3)]));
        }

        ySeries1 = new YSeries(yItem1, ChartType.RADAR_SECTOR, new RadialGradient(0, 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(255, 0, 0, 0.5)), new Stop(0.5, Color.rgb(255, 255, 0, 0.5)), new Stop(1.0, Color.rgb(0, 200, 0, 0.8))), Color.TRANSPARENT);
        ySeries2 = new YSeries(yItem2, ChartType.SMOOTH_RADAR_POLYGON, new RadialGradient(0, 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(0, 255, 255, 0.5)), new Stop(1.0, Color.rgb(0, 0, 255, 0.5))), Color.TRANSPARENT);
        ySeries3 = new YSeries(yItem3, ChartType.SMOOTH_RADAR_POLYGON, new RadialGradient(0, 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(255, 255, 0, 0.5)), new Stop(1.0, Color.rgb(255, 0, 255, 0.5))), Color.TRANSPARENT);
        yChart   = new YChart(new YPane(ySeries1, ySeries2, ySeries3));

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + UPDATE_INTERVAL) {
                    ObservableList<XYChartItem> xyItems = xySeries1.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 8 + RND.nextDouble() * 10));

                    xyItems = xySeries2.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 4 + RND.nextDouble() * 10));

                    xyItems = xySeries3.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 3 + RND.nextDouble() * 4));

                    xyItems = xySeries4.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 4));

                    ObservableList<ValueChartItem> yItems = ySeries1.getItems();
                    yItems.forEach(item -> item.setValue(RND.nextDouble() * 100));

                    yItems = ySeries2.getItems();
                    yItems.forEach(item -> item.setValue(RND.nextDouble() * 100));

                    yItems = ySeries3.getItems();
                    yItems.forEach(item -> item.setValue(RND.nextDouble() * 100));

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
