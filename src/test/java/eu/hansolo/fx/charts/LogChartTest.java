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

import eu.hansolo.fx.charts.data.XYChartItem;
import eu.hansolo.fx.charts.data.XYZChartItem;
import eu.hansolo.fx.charts.data.ValueChartItem;
import eu.hansolo.fx.charts.series.XYSeries;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * User: hansolo
 * Date: 09.08.17
 * Time: 13:30
 */
public class LogChartTest extends Application {
    private static final Double   AXIS_WIDTH     = 25d;
    private static final Color[]  COLORS         = {Color.RED, Color.BLUE, Color.CYAN, Color.LIME };
    private static final Random   RND            = new Random();
    private static final int      NO_OF_X_VALUES = 100;
    private XYSeries<XYChartItem> xySeries1;

    private XYChart<XYChartItem> smoothLineChart;
    private Axis                 smoothLineChartXAxisBottom;
    private Axis                 smoothLineChartYAxisLeft;

    private long                 lastTimerCall;
    private AnimationTimer       timer;


    @Override public void init() {
        List<XYChartItem>    xyData1 = new ArrayList<>(20);
        List<ValueChartItem> yData   = new ArrayList<>(20);
        List<XYZChartItem>   xyzData = new ArrayList<>(20);
        for (int i = 0 ; i < NO_OF_X_VALUES ; i++) {
            xyData1.add(new XYChartItem(i, RND.nextDouble() * 15, "P" + i, COLORS[RND.nextInt(3)]));
        }
        for (int i = 0 ; i < 20 ; i++) {
            yData.add(new ValueChartItem(RND.nextDouble() * 10, "P" + i, COLORS[RND.nextInt(3)]));
            xyzData.add(new XYZChartItem(RND.nextDouble() * 10, RND.nextDouble() * 10, RND.nextDouble() * 25, "P" + i, COLORS[RND.nextInt(3)]));
        }

        xySeries1 = new XYSeries<>(xyData1, ChartType.LINE, Color.rgb(255, 0, 255, 0.5));
        xySeries1.setSymbolsVisible(false);

        // SmoothLineChart
        smoothLineChartXAxisBottom = createBottomXAxis(0, NO_OF_X_VALUES, true);
        smoothLineChartYAxisLeft   = createLeftYAxis(0, 1000, true);
        smoothLineChart            = new XYChart<>(new XYPane(xySeries1),
                                                   smoothLineChartYAxisLeft, smoothLineChartXAxisBottom);
        Grid grid = new Grid(smoothLineChartXAxisBottom, smoothLineChartYAxisLeft);
        smoothLineChart.setGrid(grid);


        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 1_000_000_000l) {
                    ObservableList<XYChartItem> xyItems = xySeries1.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 1000));

                    xySeries1.refresh();

                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(smoothLineChart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Charts");
        stage.setScene(scene);
        stage.show();

        timer.start();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private Axis createLeftYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(MIN, MAX, Orientation.VERTICAL, AxisType.LOGARITHMIC, Position.LEFT);
        axis.setPrefWidth(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setTopAnchor(axis, 0d);
        AnchorPane.setBottomAnchor(axis, 25d);
        AnchorPane.setLeftAnchor(axis, 0d);

        return axis;
    }
    private Axis createRightYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(MIN, MAX, Orientation.VERTICAL, AxisType.LOGARITHMIC, Position.RIGHT);
        axis.setPrefWidth(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setRightAnchor(axis, 0d);
        AnchorPane.setTopAnchor(axis, 0d);
        AnchorPane.setBottomAnchor(axis, 25d);

        return axis;
    }

    private Axis createBottomXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(MIN, MAX, Orientation.HORIZONTAL, AxisType.LOGARITHMIC, Position.BOTTOM);
        axis.setPrefHeight(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setBottomAnchor(axis, 0d);
        AnchorPane.setLeftAnchor(axis, 25d);
        AnchorPane.setRightAnchor(axis, 25d);

        return axis;
    }
    private Axis createTopXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(MIN, MAX, Orientation.HORIZONTAL, AxisType.LOGARITHMIC, Position.TOP);
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
