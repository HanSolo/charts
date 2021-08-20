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


public class PolarChartTest extends Application {
    private static final Random  RND             = new Random();
    private static final long    UPDATE_INTERVAL = 2_000_000_000l;
    private XYSeries<XYChartItem> xySeries1;

    private PolarChart<XYChartItem> polarChart;

    private long           lastTimerCall;
    private AnimationTimer timer;


    @Override public void init() {
        List<XYChartItem> xyItems1 = new ArrayList<>();
        xyItems1.add(new XYChartItem(0.0, 45.0));
        xyItems1.add(new XYChartItem(145.0, 120.0));
        xyItems1.add(new XYChartItem(90.0, 150.0));
        xyItems1.add(new XYChartItem(225, 60));

        Helper.orderXYChartItemsByX(xyItems1, Order.ASCENDING);

        xySeries1 = new XYSeries(xyItems1, ChartType.POLAR, Color.rgb(255, 0, 0, 0.5), Color.RED);
        //xySeries1.setShowPoints(false);
        xySeries1.setStroke(Color.rgb(90, 90, 90));
        xySeries1.setSymbolStroke(Color.LIME);
        xySeries1.setSymbolFill(Color.GREEN);
        xySeries1.setSymbol(Symbol.SQUARE);

        XYPane polarPane = new XYPane(xySeries1);
        polarPane.setLowerBoundY(polarPane.getDataMinY());
        polarPane.setUpperBoundY(polarPane.getDataMaxY());

        polarChart = new PolarChart<>(polarPane);


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
                    polarChart.refresh();

                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(polarChart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(new StackPane(pane));

        stage.setTitle("Polar Chart");
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
