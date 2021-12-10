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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class ComparisonBarChartTest extends Application {
    private static final Random                   RND = new Random();
    private              List<Category>           categories;
    private              Map<Category, ChartItem> items1;
    private              Map<Category, ChartItem> items2;
    private              ComparisonBarChart       chart;
    private              long                     lastTimerCall;
    private              AnimationTimer           timer;


    @Override public void init() {
        categories = new ArrayList<>();
        items1     = new HashMap<>();
        items2     = new HashMap<>();

        for (int i = 0 ; i < 7 ; i++) {
            Category category = new Category("Option " + i);
            categories.add(category);
            items1.put(category, ChartItemBuilder.create().name("Item " + i).category(category).build());
            items2.put(category, ChartItemBuilder.create().name("Item " + i).category(category).build());
        }

        ChartItemSeries<ChartItem> series1 = ChartItemSeriesBuilder.create()
                                                                   .name("Series 1")
                                                                   .items(new ArrayList<>(items1.values()))
                                                                   .fill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(0, 212, 244)), new Stop(1, Color.rgb(0, 150, 235))))
                                                                   .textFill(Color.WHITE)
                                                                   .animated(true)
                                                                   .animationDuration(1000)
                                                                   .build();

        ChartItemSeries<ChartItem> series2 = ChartItemSeriesBuilder.create()
                                                                   .name("Series 2")
                                                                   .items(new ArrayList<>(items2.values()))
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
                    categories.forEach(category -> {
                        items1.get(category).setValue(RND.nextDouble() * 20);
                        items2.get(category).setValue(RND.nextDouble() * 20);
                    });

                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        AnchorPane pane = new AnchorPane(chart);
        AnchorPane.setTopAnchor(chart, 30d);
        AnchorPane.setRightAnchor(chart, 10d);
        AnchorPane.setBottomAnchor(chart, 10d);
        AnchorPane.setLeftAnchor(chart, 10d);
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
