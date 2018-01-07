/*
 * Copyright (c) 2018 by Gerrit Grunwald
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
import eu.hansolo.fx.charts.series.XYSeriesBuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 06.01.18
 * Time: 11:23
 */
public class LineChartTest extends Application {
    private static final Double  AXIS_WIDTH     = 50d;
    private XYChart<XYChartItem> lineChart;
    private XYSeries             xySeries1;
    private Axis                 xAxisBottom;
    private Axis                 yAxisLeft;

    @Override public void init() {
        xySeries1 = XYSeriesBuilder.create()
                                   .items(new XYChartItem(1, 280, "Jan"),
                                          new XYChartItem(2, 190, "Feb"),
                                          new XYChartItem(3, 280, "Mar"),
                                          new XYChartItem(4, 300, "Apr"),
                                          new XYChartItem(5, 205, "May"),
                                          new XYChartItem(6, 430, "Jun"),
                                          new XYChartItem(7, 380, "Jul"),
                                          new XYChartItem(8, 180, "Aug"),
                                          new XYChartItem(9, 300, "Sep"),
                                          new XYChartItem(10, 440, "Oct"),
                                          new XYChartItem(11, 300, "Nov"),
                                          new XYChartItem(12, 390, "Dec"))
                                   .chartType(ChartType.SMOOTH_AREA)
                                   .fill(Color.web("#4EE29B20"))
                                   .stroke(Color.web("#4EE29B"))
                                   .symbolFill(Color.web("#4EE29B"))
                                   .symbolStroke(Color.web("#293C47"))
                                   .symbolSize(10)
                                   .strokeWidth(3)
                                   .symbolsVisible(true)
                                   .build();

        xAxisBottom = AxisBuilder.create(Orientation.HORIZONTAL, Position.BOTTOM)
                                 .type(AxisType.LINEAR)
                                 .prefHeight(AXIS_WIDTH)
                                 .minValue(1)
                                 .maxValue(13)
                                 .autoScale(true)
                                 .axisColor(Color.TRANSPARENT)
                                 .tickLabelColor(Color.web("#85949B"))
                                 .tickMarkColor(Color.web("#85949B"))
                                 .tickMarksVisible(false)
                                 .build();
        AnchorPane.setBottomAnchor(xAxisBottom, 0d);
        AnchorPane.setLeftAnchor(xAxisBottom, AXIS_WIDTH);
        AnchorPane.setRightAnchor(xAxisBottom, AXIS_WIDTH);

        yAxisLeft = AxisBuilder.create(Orientation.VERTICAL, Position.LEFT)
                               .type(AxisType.LINEAR)
                               .prefWidth(AXIS_WIDTH)
                               .minValue(0)
                               .maxValue(1000)
                               .autoScale(true)
                               .axisColor(Color.TRANSPARENT)
                               .tickLabelColor(Color.web("#85949B"))
                               .tickMarkColor(Color.web("#85949B"))
                               .tickMarksVisible(false)
                               .build();
        AnchorPane.setTopAnchor(yAxisLeft, 0d);
        AnchorPane.setBottomAnchor(yAxisLeft, AXIS_WIDTH);
        AnchorPane.setLeftAnchor(yAxisLeft, 0d);

        Grid grid = GridBuilder.create(xAxisBottom, yAxisLeft)
                               .gridLinePaint(Color.web("#384C57"))
                               .minorHGridLinesVisible(false)
                               .mediumHGridLinesVisible(false)
                               .minorVGridLinesVisible(false)
                               .mediumVGridLinesVisible(false)
                               .gridLineDashes(4, 4)
                               .build();

        XYPane lineChartPane = new XYPane(xySeries1);

        lineChart = new XYChart<>(lineChartPane, yAxisLeft, xAxisBottom);
        lineChart.setGrid(grid);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(lineChart);
        pane.setPadding(new Insets(10));
        pane.setBackground(new Background(new BackgroundFill(Color.web("#293C47"), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("Line Chart");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private Axis createLeftYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        Axis axis = new Axis(Orientation.VERTICAL, Position.LEFT);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefWidth(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setTopAnchor(axis, 0d);
        AnchorPane.setBottomAnchor(axis, AXIS_WIDTH);
        AnchorPane.setLeftAnchor(axis, 0d);

        return axis;
    }
    private Axis createCenterYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        Axis axis = new Axis(Orientation.VERTICAL, Position.CENTER);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefWidth(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setTopAnchor(axis, 0d);
        AnchorPane.setBottomAnchor(axis, AXIS_WIDTH);
        AnchorPane.setLeftAnchor(axis, axis.getZeroPosition());

        return axis;
    }
    private Axis createRightYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        Axis axis = new Axis(Orientation.VERTICAL, Position.RIGHT);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefWidth(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setRightAnchor(axis, 0d);
        AnchorPane.setTopAnchor(axis, 0d);
        AnchorPane.setBottomAnchor(axis, AXIS_WIDTH);

        return axis;
    }

    private Axis createBottomXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        Axis axis = new Axis(Orientation.HORIZONTAL, Position.BOTTOM);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefHeight(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setBottomAnchor(axis, 0d);
        AnchorPane.setLeftAnchor(axis, AXIS_WIDTH);
        AnchorPane.setRightAnchor(axis, AXIS_WIDTH);

        return axis;
    }
    private Axis createCenterXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        Axis axis = new Axis(Orientation.HORIZONTAL, Position.CENTER);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefHeight(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setBottomAnchor(axis, axis.getZeroPosition());
        AnchorPane.setLeftAnchor(axis, AXIS_WIDTH);
        AnchorPane.setRightAnchor(axis, AXIS_WIDTH);

        return axis;
    }
    private Axis createTopXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        Axis axis = new Axis(Orientation.HORIZONTAL, Position.TOP);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefHeight(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setTopAnchor(axis, AXIS_WIDTH);
        AnchorPane.setLeftAnchor(axis, AXIS_WIDTH);
        AnchorPane.setRightAnchor(axis, AXIS_WIDTH);

        return axis;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
