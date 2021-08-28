/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.converter.Converter;
import eu.hansolo.fx.charts.data.ValueChartItem;
import eu.hansolo.fx.charts.data.XYChartItem;
import eu.hansolo.fx.charts.data.XYZChartItem;
import eu.hansolo.fx.charts.series.XYSeries;
import eu.hansolo.fx.charts.series.XYSeriesBuilder;
import eu.hansolo.fx.charts.series.XYZSeries;
import eu.hansolo.fx.charts.series.YSeries;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
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

import static eu.hansolo.fx.charts.converter.Converter.Category.TEMPERATURE;
import static eu.hansolo.fx.charts.converter.Converter.UnitDefinition.CELSIUS;
import static eu.hansolo.fx.charts.converter.Converter.UnitDefinition.FAHRENHEIT;


public class EmptyItemTest extends Application {
    private static final Double      AXIS_WIDTH     = 25d;
    private static final Color[]     COLORS         = { Color.rgb(200, 0, 0, 0.75), Color.rgb(0, 0, 200, 0.75), Color.rgb(0, 200, 200, 0.75), Color.rgb(0, 200, 0, 0.75) };
    private static final Random      RND            = new Random();
    private static final int         NO_OF_X_VALUES = 10;
    private XYSeries<XYChartItem> xySeries1;
    private XYSeries<XYChartItem> xySeries2;
    private XYSeries<XYChartItem> xySeries3;
    private XYSeries<XYChartItem> xySeries4;
    private XYSeries<XYChartItem> xySeries5;

    private XYChart<XYChartItem> lineChart;
    private Axis                 lineChartXAxisBottom;
    private Axis                 lineChartYAxisLeft;
    private Axis                 lineChartYAxisRight;

    private XYChart<XYChartItem> areaChart;
    private Axis                 areaChartXAxisBottom;
    private Axis                 areaChartYAxisLeft;

    private XYChart<XYChartItem> smoothLineChart;
    private Axis                 smoothLineChartXAxisBottom;
    private Axis                 smoothLineChartYAxisLeft;

    private XYChart<XYChartItem> smoothAreaChart;
    private Axis                 smoothAreaChartXAxisBottom;
    private Axis                 smoothAreaChartYAxisLeft;


    @Override public void init() {
        List<XYChartItem>  xyItems1 = new ArrayList<>(20);
        List<XYChartItem>  xyItems2 = new ArrayList<>(20);
        List<XYChartItem>  xyItems3 = new ArrayList<>(20);
        List<XYChartItem>    xyItems4 = new ArrayList<>(40);
        List<ValueChartItem> yItem    = new ArrayList<>(20);
        List<XYZChartItem>   xyzItem  = new ArrayList<>(20);
        for (int i = 0 ; i < NO_OF_X_VALUES ; i++) {
            if (i == 4) {
                xyItems1.add(new XYChartItem(i, RND.nextDouble() * 15, "P" + i, COLORS[RND.nextInt(3)], "P" + i, true));
                xyItems2.add(new XYChartItem(i, RND.nextDouble() * 15, "P" + i, COLORS[RND.nextInt(3)], "P" + i, true));
                xyItems3.add(new XYChartItem(i, RND.nextDouble() * 15, "P" + i, COLORS[RND.nextInt(3)], "P" + i, true));
            } else {
                xyItems1.add(new XYChartItem(i, RND.nextDouble() * 15, "P" + i, COLORS[RND.nextInt(3)], "P" + i));
                xyItems2.add(new XYChartItem(i, RND.nextDouble() * 15, "P" + i, COLORS[RND.nextInt(3)], "P" + i));
                xyItems3.add(new XYChartItem(i, RND.nextDouble() * 15, "P" + i, COLORS[RND.nextInt(3)], "P" + i));
            }
        }
        for (int i = 0 ; i < 20 ; i++) {
            yItem.add(new ValueChartItem(RND.nextDouble() * 10, "P" + i, COLORS[RND.nextInt(3)]));
            xyzItem.add(new XYZChartItem(RND.nextDouble() * 10, RND.nextDouble() * 10, RND.nextDouble() * 25, "P" + i, COLORS[RND.nextInt(3)]));
        }
        for (int i = -20 ; i < 20 ; i++) {
            xyItems4.add(new XYChartItem(i, RND.nextDouble() * 40 - 20, "P" + i, COLORS[RND.nextInt(3)]));
        }

        xySeries1 = XYSeriesBuilder.create()
                                   .items(xyItems1)
                                   .chartType(ChartType.LINE)
                                   .fill(Color.TRANSPARENT)
                                   .stroke(Color.MAGENTA)
                                   .symbolFill(Color.RED)
                                   .symbolStroke(Color.TRANSPARENT)
                                   .symbolsVisible(true)
                                   .symbolSize(10)
                                   .build();

        xySeries2 = XYSeriesBuilder.create()
                                   .items(xyItems2)
                                   .chartType(ChartType.AREA)
                                   .fill(Color.TRANSPARENT)
                                   .stroke(Color.BLUE)
                                   .symbolFill(Color.BLUE)
                                   .symbolStroke(Color.TRANSPARENT)
                                   .symbolsVisible(true)
                                   .symbolSize(10)
                                   .build();

        xySeries3 = new XYSeries<>(xyItems3, ChartType.SMOOTH_LINE);
        xySeries4 = new XYSeries<>(xyItems1, ChartType.SMOOTH_AREA);

        xySeries3.setSymbolFill(Color.LIME);
        xySeries4.setSymbolFill(Color.MAGENTA);

        xySeries3.setSymbolStroke(Color.TRANSPARENT);
        xySeries4.setSymbolStroke(Color.TRANSPARENT);

        xySeries5 = XYSeriesBuilder.create()
                                   .items(xyItems4)
                                   .chartType(ChartType.SCATTER)
                                   .fill(Color.TRANSPARENT)
                                   .stroke(Color.MAGENTA)
                                   .symbolFill(Color.RED)
                                   .symbolStroke(Color.TRANSPARENT)
                                   .symbolsVisible(true)
                                   .build();

        // LineChart
        Converter tempConverter     = new Converter(TEMPERATURE, CELSIUS); // Type Temperature with BaseUnit Celsius
        double    tempFahrenheitMin = tempConverter.convert(-10, FAHRENHEIT);
        double    tempFahrenheitMax = tempConverter.convert(20, FAHRENHEIT);

        lineChartXAxisBottom = Helper.createBottomAxis(-10, NO_OF_X_VALUES, true, AXIS_WIDTH);
        lineChartYAxisLeft   = Helper.createLeftAxis(-10, 20, true, AXIS_WIDTH);
        lineChartYAxisRight  = Helper.createRightAxis(tempFahrenheitMin, tempFahrenheitMax, false, AXIS_WIDTH);

        lineChartXAxisBottom.setZeroColor(Color.BLACK);
        lineChartYAxisLeft.setZeroColor(Color.BLACK);

        lineChart = new XYChart<>(new XYPane(xySeries2, xySeries1), lineChartYAxisLeft, lineChartYAxisRight, lineChartXAxisBottom);

        Grid grid1 = new Grid(lineChartXAxisBottom, lineChartYAxisLeft);
        lineChart.setGrid(grid1);


        // AreaChart
        areaChartXAxisBottom = Helper.createBottomAxis(0, NO_OF_X_VALUES, true, AXIS_WIDTH);
        areaChartYAxisLeft   = Helper.createLeftAxis(0, 20, true, AXIS_WIDTH);
        areaChart            = new XYChart<>(new XYPane(xySeries2), areaChartXAxisBottom, areaChartYAxisLeft);

        xySeries2.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(0, 0, 255, 0.75)), new Stop(1.0, Color.rgb(0, 255, 255, 0.25))));
        xySeries2.setStroke(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(0, 0, 255, 1.0)), new Stop(1.0, Color.rgb(0, 255, 255, 1.0))));
        areaChart.getXYPane().setChartBackground(Color.rgb(50, 50, 50, 0.5));

        // SmoothLineChart
        smoothLineChartXAxisBottom = Helper.createBottomAxis(0, NO_OF_X_VALUES, true, AXIS_WIDTH);
        smoothLineChartYAxisLeft   = Helper.createLeftAxis(0, 20, true, AXIS_WIDTH);
        smoothLineChart            = new XYChart<>(new XYPane(xySeries3), smoothLineChartYAxisLeft, smoothLineChartXAxisBottom);

        Grid grid2 = new Grid(smoothLineChartXAxisBottom, smoothLineChartYAxisLeft);
        smoothLineChart.setGrid(grid2);

        // SmoothAreaChart
        smoothAreaChartXAxisBottom = Helper.createBottomAxis(0, NO_OF_X_VALUES, true, AXIS_WIDTH);
        smoothAreaChartYAxisLeft   = Helper.createLeftAxis(0, 20, true, AXIS_WIDTH);
        smoothAreaChart            = new XYChart<>(new XYPane(xySeries4), smoothAreaChartYAxisLeft, smoothAreaChartXAxisBottom);

        xySeries4.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(255, 255, 255, 0.6)), new Stop(1.0, Color.TRANSPARENT)));
        xySeries4.setStroke(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(255, 255, 255, 1.0)), new Stop(1.0, Color.TRANSPARENT)));
        smoothAreaChart.getXYPane().setChartBackground(Color.rgb(25, 25, 25, 0.8));

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

        Scene scene = new Scene(new StackPane(gridPane));

        stage.setTitle("Charts");
        stage.setScene(scene);
        stage.show();

        //timer.start();

        //modificationThread.start();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
