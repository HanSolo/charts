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

import eu.hansolo.fx.charts.data.XYChartItem;
import eu.hansolo.fx.charts.series.XYSeries;
import eu.hansolo.fx.charts.series.XYSeriesBuilder;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class MultiTimeSeriesTest extends Application {
    private static final Double AXIS_WIDTH = 25d;

    private XYChart<XYChartItem> multiTimeSeriesChart;
    private Axis xAxis;
    private Axis yAxis;


    @Override public void init() {
        // Data Series 1
        List<XYSeries<XYChartItem>>    listOfSeries1  = new ArrayList<>();
        String                         filename1      = MultiTimeSeriesTest.class.getResource("data1.csv").toExternalForm().replaceAll("file:", "");
        String                         data1          = Helper.readTextFile(filename1);
        String[]                       lines1         = data1.split(System.getProperty("line.separator"));
        String                         firstLine1     = lines1[0];
        String[]                       names1         = firstLine1.split(",");
        List<Double>                   xAxisValues1   = new ArrayList<>();
        double                         yAxisMinValue1 = Double.MAX_VALUE;
        double                         yAxisMaxValue1 = Double.MIN_VALUE;
        Map<String, List<XYChartItem>> seriesDataMap1 = new HashMap<>();
        for (int i = 1 ; i < lines1.length ; i++) {
            String line = lines1[i];
            List<XYChartItem> xyItems = new ArrayList<>();
            String[] dataPoints = line.split(",");
            double   timePoint  = Double.parseDouble(dataPoints[0]);
            xAxisValues1.add(timePoint);
            for (int j = 1 ; j < dataPoints.length ; j++) {
                double value = Double.parseDouble(dataPoints[j]);
                yAxisMinValue1 = Math.min(yAxisMinValue1, value);
                yAxisMaxValue1 = Math.max(yAxisMaxValue1, value);

                if (seriesDataMap1.containsKey(names1[j])) {
                    seriesDataMap1.get(names1[j]).add(new XYChartItem(timePoint, value, names1[j], Color.MAGENTA));
                } else {
                    seriesDataMap1.put(names1[j], new LinkedList<>());
                    seriesDataMap1.get(names1[j]).add(new XYChartItem(timePoint, value, names1[j], Color.MAGENTA));
                }
            }
        }

        seriesDataMap1.entrySet().forEach(entry -> {
            XYSeries<XYChartItem> xySeries = XYSeriesBuilder.create()
                                                            .items(entry.getValue().toArray(new XYChartItem[0]))
                                                            .chartType(ChartType.MULTI_TIME_SERIES)
                                                            .fill(Color.TRANSPARENT)
                                                            .stroke(Color.MAGENTA)
                                                            .symbolFill(Color.RED)
                                                            .symbolStroke(Color.TRANSPARENT)
                                                            .symbolsVisible(false)
                                                            .symbolSize(5)
                                                            .strokeWidth(0.5)
                                                            .build();
            listOfSeries1.add(xySeries);
        });

        // Data Series 2
        List<XYSeries<XYChartItem>>    listOfSeries2  = new ArrayList<>();
        String                         filename2      = MultiTimeSeriesTest.class.getResource("data2.csv").toExternalForm().replaceAll("file:", "");
        String                         data2          = Helper.readTextFile(filename2);
        String[]                       lines2         = data2.split(System.getProperty("line.separator"));
        String                         firstLine2     = lines2[0];
        String[]                       names2         = firstLine2.split(",");
        List<Double>                   xAxisValues2   = new ArrayList<>();
        double                         yAxisMinValue2 = Double.MAX_VALUE;
        double                         yAxisMaxValue2 = Double.MIN_VALUE;
        Map<String, List<XYChartItem>> seriesDataMap2 = new HashMap<>();
        for (int i = 1 ; i < lines2.length ; i++) {
            String line = lines2[i];
            List<XYChartItem> xyItems = new ArrayList<>();
            String[] dataPoints = line.split(",");
            double   timePoint  = Double.parseDouble(dataPoints[0]);
            xAxisValues2.add(timePoint);
            for (int j = 1 ; j < dataPoints.length ; j++) {
                double value = Double.parseDouble(dataPoints[j]);
                yAxisMinValue2 = Math.min(yAxisMinValue2, value);
                yAxisMaxValue2 = Math.max(yAxisMaxValue2, value);

                if (seriesDataMap2.containsKey(names2[j])) {
                    seriesDataMap2.get(names2[j]).add(new XYChartItem(timePoint, value, names2[j], Color.MAGENTA));
                } else {
                    seriesDataMap2.put(names2[j], new LinkedList<>());
                    seriesDataMap2.get(names2[j]).add(new XYChartItem(timePoint, value, names2[j], Color.MAGENTA));
                }
            }
        }

        seriesDataMap2.entrySet().forEach(entry -> {
            XYSeries<XYChartItem> xySeries = XYSeriesBuilder.create()
                                                            .items(entry.getValue().toArray(new XYChartItem[0]))
                                                            .chartType(ChartType.MULTI_TIME_SERIES)
                                                            .fill(Color.TRANSPARENT)
                                                            .stroke(Color.MAGENTA)
                                                            .symbolFill(Color.RED)
                                                            .symbolStroke(Color.TRANSPARENT)
                                                            .symbolsVisible(false)
                                                            .symbolSize(5)
                                                            .strokeWidth(0.5)
                                                            .build();
            listOfSeries2.add(xySeries);
        });


        // Data Series 3
        List<XYSeries<XYChartItem>>    listOfSeries3  = new ArrayList<>();
        String                         filename3      = MultiTimeSeriesTest.class.getResource("data3.csv").toExternalForm().replaceAll("file:", "");
        String                         data3          = Helper.readTextFile(filename3);
        String[]                       lines3         = data3.split(System.getProperty("line.separator"));
        String                         firstLine3     = lines3[0];
        String[]                       names3         = firstLine3.split(",");
        List<Double>                   xAxisValues3   = new ArrayList<>();
        double                         yAxisMinValue3 = Double.MAX_VALUE;
        double                         yAxisMaxValue3 = Double.MIN_VALUE;
        Map<String, List<XYChartItem>> seriesDataMap3 = new HashMap<>();
        for (int i = 1 ; i < lines3.length ; i++) {
            String line = lines3[i];
            List<XYChartItem> xyItems = new ArrayList<>();
            String[] dataPoints = line.split(",");
            double   timePoint  = Double.parseDouble(dataPoints[0]);
            xAxisValues3.add(timePoint);
            for (int j = 1 ; j < dataPoints.length ; j++) {
                double value = Double.parseDouble(dataPoints[j]);
                yAxisMinValue3 = Math.min(yAxisMinValue3, value);
                yAxisMaxValue3 = Math.max(yAxisMaxValue3, value);

                if (seriesDataMap3.containsKey(names3[j])) {
                    seriesDataMap3.get(names3[j]).add(new XYChartItem(timePoint, value, names3[j], Color.MAGENTA));
                } else {
                    seriesDataMap3.put(names3[j], new LinkedList<>());
                    seriesDataMap3.get(names3[j]).add(new XYChartItem(timePoint, value, names3[j], Color.MAGENTA));
                }
            }
        }

        seriesDataMap3.entrySet().forEach(entry -> {
            XYSeries<XYChartItem> xySeries = XYSeriesBuilder.create()
                                                            .items(entry.getValue().toArray(new XYChartItem[0]))
                                                            .chartType(ChartType.MULTI_TIME_SERIES)
                                                            .fill(Color.TRANSPARENT)
                                                            .stroke(Color.MAGENTA)
                                                            .symbolFill(Color.RED)
                                                            .symbolStroke(Color.TRANSPARENT)
                                                            .symbolsVisible(false)
                                                            .symbolSize(5)
                                                            .strokeWidth(0.5)
                                                            .build();
            listOfSeries3.add(xySeries);
        });


        double yAxisMinValue = Math.min(Math.min(yAxisMinValue1, yAxisMinValue2), yAxisMinValue3);
        double yAxisMaxValue = Math.max(Math.max(yAxisMaxValue1, yAxisMaxValue2), yAxisMaxValue3);


        // MultiTimeSeriesChart
        double start = xAxisValues1.stream().min(Comparator.comparingDouble(Double::doubleValue)).get();
        double end   = xAxisValues1.stream().max(Comparator.comparingDouble(Double::doubleValue)).get();
        xAxis = Helper.createBottomAxis(start, end, "Time [s]", true, AXIS_WIDTH);
        xAxis.setDecimals(1);

        yAxis = Helper.createLeftAxis(yAxisMinValue, yAxisMaxValue, "Ratio", true, AXIS_WIDTH);
        yAxis.setDecimals(2);

        xAxis.setZeroColor(Color.BLACK);
        yAxis.setZeroColor(Color.BLACK);

        XYPane xyPane1 = new XYPane(listOfSeries1);
        xyPane1.setAverageStroke(Color.rgb(247, 118, 109));
        xyPane1.setAverageStrokeWidth(3);
        xyPane1.setStdDeviationFill(Color.rgb(247, 118, 109, 0.2));
        xyPane1.setStdDeviationStroke(Color.rgb(120, 120, 120));
        xyPane1.setEnvelopeVisible(true);
        xyPane1.setEnvelopeFill(Color.TRANSPARENT);
        xyPane1.setEnvelopeStroke(Color.rgb(247, 118, 109));

        XYPane xyPane2 = new XYPane(listOfSeries2);
        xyPane2.setAverageStroke(Color.rgb(42, 186, 56));
        xyPane2.setAverageStrokeWidth(3);
        xyPane2.setStdDeviationFill(Color.rgb(42, 186, 56, 0.2));
        xyPane2.setStdDeviationStroke(Color.rgb(120, 120, 120));
        xyPane2.setEnvelopeVisible(true);
        xyPane2.setEnvelopeFill(Color.TRANSPARENT);
        xyPane2.setEnvelopeStroke(Color.rgb(42, 186, 56));

        XYPane xyPane3 = new XYPane(listOfSeries3);
        xyPane3.setAverageStroke(Color.rgb(97, 155, 255));
        xyPane3.setAverageStrokeWidth(3);
        xyPane3.setStdDeviationFill(Color.rgb(97, 155, 255, 0.2));
        xyPane3.setStdDeviationStroke(Color.rgb(120, 120, 120));
        xyPane3.setEnvelopeVisible(true);
        xyPane3.setEnvelopeFill(Color.TRANSPARENT);
        xyPane3.setEnvelopeStroke(Color.rgb(97, 155, 255));

        List<XYPane> xyPanes = new ArrayList<>();
        xyPanes.add(xyPane1);
        xyPanes.add(xyPane2);
        xyPanes.add(xyPane3);

        multiTimeSeriesChart = new XYChart(xyPanes, yAxis, xAxis);

        Grid grid1 = new Grid(xAxis, yAxis);
        multiTimeSeriesChart.setGrid(grid1);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(multiTimeSeriesChart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(new StackPane(pane), 800, 600);

        stage.setTitle("MultiTimeSeriesCharts");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
