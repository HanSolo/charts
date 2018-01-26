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

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.ChartItemBuilder;
import eu.hansolo.fx.charts.series.ChartItemSeriesBuilder;
import eu.hansolo.fx.charts.series.Series;
import eu.hansolo.fx.charts.tools.Order;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import java.util.Random;


/**
 * User: hansolo
 * Date: 25.01.18
 * Time: 11:43
 */
public class ConcentricRingChartTest extends Application {
    private static final Random RND = new Random();
    private ChartItem           chart1Data1;
    private ChartItem           chart1Data2;
    private ChartItem           chart1Data3;
    private ChartItem           chart1Data4;
    private ChartItem           chart1Data5;
    private ChartItem           chart1Data6;
    private ChartItem           chart1Data7;
    private ChartItem           chart1Data8;
    private ChartItem           chart2Data1;
    private ChartItem           chart2Data2;
    private ChartItem           chart2Data3;
    private ChartItem           chart2Data4;
    private ChartItem           chart2Data5;
    private ChartItem           chart2Data6;
    private ChartItem           chart2Data7;
    private ChartItem           chart2Data8;
    private ConcentricRingChart chart;
    private ComparisonRingChart chart1;
    private long                lastTimerCall;
    private AnimationTimer      timer;


    @Override public void init() {
        chart1Data1 = new ChartItem("Item 1");
        chart1Data2 = new ChartItem("Item 2");
        chart1Data3 = new ChartItem("Item 3");
        chart1Data4 = new ChartItem("Item 4");
        chart1Data5 = new ChartItem("Item 5");
        chart1Data6 = new ChartItem("Item 6");
        chart1Data7 = new ChartItem("Item 7");
        chart1Data8 = new ChartItem("Item 8");

        Series<ChartItem> series1 = ChartItemSeriesBuilder.create()
                                                          .name("Series 1")
                                                          .items(chart1Data1, chart1Data2, chart1Data3, chart1Data4,
                                                                 chart1Data5, chart1Data6, chart1Data7, chart1Data8)
                                                          .fill(Color.web("#2EDDAE"))
                                                          .textFill(Color.WHITE)
                                                          .animated(true)
                                                          .animationDuration(1000)
                                                          .build();

        chart2Data1 = new ChartItem("Item 1");
        chart2Data2 = new ChartItem("Item 2");
        chart2Data3 = new ChartItem("Item 3");
        chart2Data4 = new ChartItem("Item 4");
        chart2Data5 = new ChartItem("Item 5");
        chart2Data6 = new ChartItem("Item 6");
        chart2Data7 = new ChartItem("Item 7");
        chart2Data8 = new ChartItem("Item 8");

        Series<ChartItem> series2 = ChartItemSeriesBuilder.create()
                                                          .name("Series 2")
                                                          .items(chart2Data1, chart2Data2, chart2Data3, chart2Data4,
                                                                 chart2Data5, chart2Data6, chart2Data7, chart2Data8)
                                                          .fill(Color.web("#49D762"))
                                                          .textFill(Color.WHITE)
                                                          .animated(true)
                                                          .animationDuration(1000)
                                                          .build();

        chart = ConcentricRingChartBuilder.create()
                                          .series(series1)
                                          .sorted(false)
                                          .order(Order.DESCENDING)
                                          //.barBackgroundColor(Color.BLACK)
                                          .build();

        chart1 = new ComparisonRingChart(series1, series2);
        chart1.setSorted(true);
        chart1.setOrder(Order.DESCENDING);


        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 2_000_000_000l) {
                    chart1Data1.setValue(RND.nextDouble() * 20);
                    chart1Data2.setValue(RND.nextDouble() * 20);
                    chart1Data3.setValue(RND.nextDouble() * 20);
                    chart1Data4.setValue(RND.nextDouble() * 20);
                    chart1Data5.setValue(RND.nextDouble() * 20);
                    chart1Data6.setValue(RND.nextDouble() * 20);
                    chart1Data7.setValue(RND.nextDouble() * 20);
                    chart1Data8.setValue(RND.nextDouble() * 20);

                    chart2Data1.setValue(RND.nextDouble() * 20);
                    chart2Data2.setValue(RND.nextDouble() * 20);
                    chart2Data3.setValue(RND.nextDouble() * 20);
                    chart2Data4.setValue(RND.nextDouble() * 20);
                    chart2Data5.setValue(RND.nextDouble() * 20);
                    chart2Data6.setValue(RND.nextDouble() * 20);
                    chart2Data7.setValue(RND.nextDouble() * 20);
                    chart2Data8.setValue(RND.nextDouble() * 20);

                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(chart1);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("ConcentricRingChart");
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
