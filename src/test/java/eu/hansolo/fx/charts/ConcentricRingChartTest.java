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
    private ChartItem           chartData1;
    private ChartItem           chartData2;
    private ChartItem           chartData3;
    private ChartItem           chartData4;
    private ChartItem           chartData5;
    private ChartItem           chartData6;
    private ChartItem           chartData7;
    private ChartItem           chartData8;
    private ConcentricRingChart chart;
    private ComparisonRingChart chart1;
    private long                lastTimerCall;
    private AnimationTimer      timer;


    @Override public void init() {
        chartData1 = new ChartItem("Item 1");
        chartData2 = new ChartItem("Item 2");
        chartData3 = new ChartItem("Item 3");
        chartData4 = new ChartItem("Item 4");
        chartData5 = new ChartItem("Item 5");
        chartData6 = new ChartItem("Item 6");
        chartData7 = new ChartItem("Item 7");
        chartData8 = new ChartItem("Item 8");

        Series<ChartItem> series = ChartItemSeriesBuilder.create()
                                                         .items(chartData1, chartData2, chartData3, chartData4,
                                                                chartData5, chartData6, chartData7, chartData8)
                                                         .fill(Color.web("#2EDDAE"))
                                                         .textFill(Color.WHITE)
                                                         .animated(true)
                                                         .animationDuration(1000)
                                                         .build();

        chart = ConcentricRingChartBuilder.create()
                                          .series(series)
                                          .sorted(false)
                                          .order(Order.DESCENDING)
                                          //.barBackgroundColor(Color.BLACK)
                                          .build();

        chart1 = new ComparisonRingChart(series, series);


        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 2_000_000_000l) {
                    chartData1.setValue(RND.nextDouble() * 20);
                    chartData2.setValue(RND.nextDouble() * 20);
                    chartData3.setValue(RND.nextDouble() * 20);
                    chartData4.setValue(RND.nextDouble() * 20);
                    chartData5.setValue(RND.nextDouble() * 20);
                    chartData6.setValue(RND.nextDouble() * 20);
                    chartData7.setValue(RND.nextDouble() * 20);
                    chartData8.setValue(RND.nextDouble() * 20);

                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(chart);
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
