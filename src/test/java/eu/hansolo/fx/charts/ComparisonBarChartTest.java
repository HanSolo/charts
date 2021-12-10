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

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.ChartItemBuilder;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.series.ChartItemSeriesBuilder;
import eu.hansolo.fx.charts.tools.NumberFormat;
import eu.hansolo.fx.charts.tools.Order;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.util.Random;


public class ComparisonBarChartTest extends Application {
    private static final Random              RND = new Random();
    private              ChartItem           chart1Data1;
    private              ChartItem           chart1Data2;
    private              ChartItem           chart1Data3;
    private              ChartItem           chart1Data4;
    private              ChartItem           chart1Data5;
    private              ChartItem           chart1Data6;
    private              ChartItem           chart1Data7;
    private              ChartItem           chart1Data8;
    private              ChartItem           chart2Data1;
    private              ChartItem           chart2Data2;
    private              ChartItem           chart2Data3;
    private              ChartItem           chart2Data4;
    private              ChartItem           chart2Data5;
    private              ChartItem           chart2Data6;
    private              ChartItem           chart2Data7;
    private              ChartItem           chart2Data8;
    private              ComparisonBarChart  chart;
    private              long                lastTimerCall;
    private              AnimationTimer      timer;


    @Override public void init() {
        Category option1  = new Category("Option 1");
        Category option2 = new Category("Option 2");
        Category option3 = new Category("Option 3");
        Category option4 = new Category("Option 4");
        Category option5 = new Category("Option 5");
        Category option6 = new Category("Option 6");
        Category option7 = new Category("Option 7");
        Category option8 = new Category("Option 8");

        chart1Data1 = ChartItemBuilder.create().name("Item 1").category(option1).build();
        chart1Data2 = ChartItemBuilder.create().name("Item 2").category(option2).build();
        chart1Data3 = ChartItemBuilder.create().name("Item 3").category(option3).build();
        chart1Data4 = ChartItemBuilder.create().name("Item 4").category(option4).build();
        chart1Data5 = ChartItemBuilder.create().name("Item 5").category(option5).build();
        chart1Data6 = ChartItemBuilder.create().name("Item 6").category(option6).build();
        chart1Data7 = ChartItemBuilder.create().name("Item 7").category(option7).build();
        chart1Data8 = ChartItemBuilder.create().name("Item 8").category(option8).build();

        ChartItemSeries<ChartItem> series1 = ChartItemSeriesBuilder.create()
                                                                   .name("Series 1")
                                                                   .items(chart1Data1, chart1Data2, chart1Data3, chart1Data4,
                                                                          chart1Data5, chart1Data6, chart1Data7, chart1Data8)
                                                                   .fill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(0, 212, 244)), new Stop(1, Color.rgb(0, 150, 235))))
                                                                   .textFill(Color.WHITE)
                                                                   .animated(true)
                                                                   .animationDuration(1000)
                                                                   .build();

        chart2Data1 = ChartItemBuilder.create().name("Item 1").category(option1).build();
        chart2Data2 = ChartItemBuilder.create().name("Item 2").category(option2).build();
        chart2Data3 = ChartItemBuilder.create().name("Item 3").category(option3).build();
        chart2Data4 = ChartItemBuilder.create().name("Item 4").category(option4).build();
        chart2Data5 = ChartItemBuilder.create().name("Item 5").category(option5).build();
        chart2Data6 = ChartItemBuilder.create().name("Item 6").category(option6).build();
        chart2Data7 = ChartItemBuilder.create().name("Item 7").category(option7).build();
        chart2Data8 = ChartItemBuilder.create().name("Item 8").category(option8).build();

        ChartItemSeries<ChartItem> series2 = ChartItemSeriesBuilder.create()
                                                                   .name("Series 2")
                                                                   .items(chart2Data1, chart2Data2, chart2Data3, chart2Data4,
                                                                          chart2Data5, chart2Data6, chart2Data7, chart2Data8)
                                                                   .fill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(0, 150, 235)), new Stop(1, Color.rgb(0, 212, 244))))
                                                                   .textFill(Color.WHITE)
                                                                   .animated(true)
                                                                   .animationDuration(1000)
                                                                   .build();

        chart = ComparisonBarChartBuilder.create(series1, series2)
                                         .prefSize(600, 600)
                                         .doCompare(true)
                                         .backgroundColor(Color.rgb(51, 51, 51))
                                         .textFill(Color.WHITE)
                                         .categoryTextFill(Color.WHITE)
                                         .shortenNumbers(true)
                                         .sorted(true)
                                         .build();

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 3_000_000_000l) {
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
        StackPane pane = new StackPane(chart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("ComparisonBarChart");
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
