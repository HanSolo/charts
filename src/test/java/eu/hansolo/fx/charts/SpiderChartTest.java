/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2025 Gerrit Grunwald.
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
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.Order;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SpiderChartTest extends Application {
    private static final Random                RND             = new Random();
    private static final long                  UPDATE_INTERVAL = 2_000_000_000l;
    private              XYSeries<XYChartItem> xySeries1;

    private SpiderChart<XYChartItem> spiderChart;

    private long           lastTimerCall;
    private AnimationTimer timer;


    @Override public void init() {
        List<XYChartItem> xyItems1 = new ArrayList<>();
        xyItems1.add(new XYChartItem(0.0, 70.0, "Uphill"));
        xyItems1.add(new XYChartItem(72.0, 41.0, "Range"));
        xyItems1.add(new XYChartItem(144.0, 61.0, "Equipment"));
        xyItems1.add(new XYChartItem(216.0, 80.0, "Downhill"));
        xyItems1.add(new XYChartItem(288.0, 70.0, "Playfulness"));

        Helper.orderXYChartItemsByX(xyItems1, Order.ASCENDING);

        xySeries1 = new XYSeries(xyItems1, ChartType.SPIDER, Color.rgb(0, 0, 128, 0.25), Color.RED);
        xySeries1.setStroke(Color.TRANSPARENT);
        xySeries1.setSymbolStroke(Color.LIME);
        xySeries1.setSymbolFill(Color.GREEN);
        xySeries1.setSymbol(Symbol.SQUARE);
        xySeries1.setSymbolsVisible(false);

        XYPane spiderPane = new XYPane(xySeries1);
        spiderPane.setLowerBoundY(spiderPane.getDataMinY());
        spiderPane.setUpperBoundY(spiderPane.getDataMaxY());
        spiderPane.setPolarTickStep(PolarTickStep.SEVENTY_TWO);
        spiderPane.setCategories("Uphill", "Range", "Equipment", "Downhill", "Playfulness");
        spiderPane.setCategoryTextVisible(true);

        spiderChart = new SpiderChart<>(spiderPane);

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + UPDATE_INTERVAL) {
                    ObservableList<XYChartItem> xyItems = xySeries1.getItems();
                    xyItems.forEach(item -> {
                        item.setX(RND.nextDouble() * 360.0);
                        item.setY(RND.nextDouble() * 8 + RND.nextDouble() * 10);
                    });

                    // Can be used to update charts but if more than one series is in one xyPane
                    // it's easier to use the refresh() method of XYChart
                    //xySeries1.refresh();
                    //xySeries2.refresh();
                    //xySeries3.refresh();
                    //xySeries4.refresh();

                    // Useful to refresh the chart if it contains more than one series to avoid
                    // multiple redraws
                    spiderChart.refresh();

                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(spiderChart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(new StackPane(pane));

        stage.setTitle("Spider Chart");
        stage.setScene(scene);
        stage.show();

        //timer.start();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
