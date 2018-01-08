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
import eu.hansolo.fx.charts.series.XYSeries;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * User: hansolo
 * Date: 01.12.17
 * Time: 12:40
 */
public class PlayfairTest extends Application {
    private static final Double  AXIS_WIDTH     = 25d;
    private static final Color[] COLORS         = {Color.rgb(200, 0, 0, 0.75), Color.rgb(0, 0, 200, 0.75), Color.rgb(0, 200, 200, 0.75), Color.rgb(0, 200, 0, 0.75) };
    private static final Random  RND            = new Random();
    private static final int     NO_OF_X_VALUES = 5;
    private XYSeries<XYChartItem> xySeries1;
    private XYSeries<XYChartItem> xySeries2;

    private XYChart<XYChartItem> playfairChart;
    private Axis                 xAxisBottom;
    private Axis                 yAxisLeft;

    private Thread                   modificationThread;

    private long                     lastTimerCall;
    private AnimationTimer           timer;


    @Override public void init() {
        List<XYChartItem> xyData1 = new ArrayList<>(5);
        List<XYChartItem> xyData2 = new ArrayList<>(5);

        Color item1Color = Color.rgb(0, 200, 0);
        Color item2Color = Color.rgb(200, 0, 0);

        xyData1.add(new XYChartItem(0, 12, "P0"));
        xyData1.add(new XYChartItem(1, 7, "P1"));
        xyData1.add(new XYChartItem(2, 9, "P2"));
        xyData1.add(new XYChartItem(3, 3, "P3"));
        xyData1.add(new XYChartItem(4, 5, "P4"));
        xyData1.add(new XYChartItem(5, 4, "P5"));

        xyData2.add(new XYChartItem(0, 5, "P0"));
        xyData2.add(new XYChartItem(1, 9, "P1"));
        xyData2.add(new XYChartItem(2, 5, "P2"));
        xyData2.add(new XYChartItem(3, 4, "P3"));
        xyData2.add(new XYChartItem(4, 7, "P4"));
        xyData2.add(new XYChartItem(5, 9, "P5"));

        xySeries1 = new XYSeries<>(xyData1, ChartType.SMOOTH_LINE_DELTA, true);
        xySeries2 = new XYSeries<>(xyData2, ChartType.SMOOTH_LINE_DELTA, true);

        xySeries1.setSymbolFill(item1Color);
        xySeries1.setSymbolStroke(item1Color.darker());

        xySeries2.setSymbolFill(item2Color);
        xySeries2.setSymbolStroke(item2Color.darker());

        xySeries1.setStroke(item1Color);
        //xySeries1.setFill(Color.colorToRGB(0, 200, 0, 0.35));
        xySeries1.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                             new Stop(0, Color.rgb(0, 200, 0, 0.75)),
                                             new Stop(1, Color.rgb(0, 200, 0, 0.25))));

        xySeries2.setStroke(item2Color);
        //xySeries2.setFill(Color.colorToRGB(200, 0, 0, 0.35));
        xySeries2.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                             new Stop(0, Color.rgb(200, 0, 0, 0.75)),
                                             new Stop(1, Color.rgb(200, 0, 0, 0.25))));

        // SmoothLineChart
        xAxisBottom   = createBottomXAxis(0, NO_OF_X_VALUES, true);
        xAxisBottom.setDecimals(1);
        yAxisLeft     = createLeftYAxis(0, 15, true);
        playfairChart = new XYChart<>(new XYPane(xySeries1, xySeries2), yAxisLeft, xAxisBottom);


        Grid grid = new Grid(xAxisBottom, yAxisLeft);
        playfairChart.setGrid(grid);

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 1_000_000_000l) {
                    ObservableList<XYChartItem> xyItems = xySeries1.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 20));

                    xyItems = xySeries2.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 15));

                    xySeries1.refresh();
                    xySeries2.refresh();

                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(playfairChart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(new StackPane(pane));

        stage.setTitle("Playfair Chart Test");
        stage.setScene(scene);
        stage.show();

        //timer.start();
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
    private Axis createCenterYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(Orientation.VERTICAL, Position.CENTER);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefWidth(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setTopAnchor(axis, 0d);
        AnchorPane.setBottomAnchor(axis, 25d);
        AnchorPane.setLeftAnchor(axis, axis.getZeroPosition());

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
    private Axis createCenterXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(Orientation.HORIZONTAL, Position.CENTER);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefHeight(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setBottomAnchor(axis, axis.getZeroPosition());
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