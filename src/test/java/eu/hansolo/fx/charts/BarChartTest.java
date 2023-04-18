/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2023 Gerrit Grunwald.
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

import eu.hansolo.fx.charts.BarChart;
import eu.hansolo.fx.charts.BarChartBuilder;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.ChartItemBuilder;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.series.ChartItemSeriesBuilder;
import eu.hansolo.fx.charts.tools.NumberFormat;
import eu.hansolo.fx.charts.tools.Order;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BarChartTest extends Application {
    private static final Random          RND = new Random();
    private              BarChart        chart;
    private              List<ChartItem> items;
    private              long            lastTimerCall;
    private              AnimationTimer  timer;


    @Override public void init() {
        items = new ArrayList<>();
        items.add(ChartItemBuilder.create().name("Item 1").value(0).fill(Color.rgb(221, 78, 77)).build());
        items.add(ChartItemBuilder.create().name("Item 2").value(0).fill(Color.rgb(215, 131, 79)).build());
        items.add(ChartItemBuilder.create().name("Item 3").value(0).fill(Color.rgb(236, 165, 57)).build());
        items.add(ChartItemBuilder.create().name("Item 4").value(0).fill(Color.rgb(135, 170, 102)).build());
        items.add(ChartItemBuilder.create().name("Item 5").value(0).fill(Color.rgb(136, 171, 173)).build());
        items.add(ChartItemBuilder.create().name("Item 6").value(0).fill(Color.rgb(76, 179, 210)).build());
        items.add(ChartItemBuilder.create().name("Item 7").value(0).fill(Color.rgb(106, 198, 255)).build());

        chart = BarChartBuilder.create()
                               .prefSize(600, 300)
                               .items(items)
                               .orientation(Orientation.HORIZONTAL)
                               .backgroundFill(Color.rgb(48, 48, 48))
                               .namesBackgroundFill(Color.rgb(48, 48, 48))
                               .barBackgroundFill(Color.rgb(62, 62, 62))
                               .barBackgroundVisible(true)
                               .seriesFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(91, 105, 255)), new Stop(1, Color.rgb(76, 41, 217))))
                               .shadowsVisible(false)
                               .textFill(Color.WHITE)
                               .namesTextFill(Color.rgb(190, 190, 190))
                               .useNamesTextFill(true)
                               .useItemTextFill(false)
                               .useItemFill(false)
                               .shortenNumbers(false)
                               .sorted(true)
                               .order(Order.DESCENDING)
                               //.order(Order.ASCENDING)
                               //.numberFormat(NumberFormat.PERCENTAGE)
                               .numberFormat(NumberFormat.NUMBER)
                               //.animated(false)
                               .minNumberOfBars(10)
                               .useMinNumberOfBars(false)
                               .useGivenColors(true)
                               .colors(List.of(Color.RED, Color.BLUE, Color.GREEN))
                               .build();

        AnchorPane.setTopAnchor(chart, 10d);
        AnchorPane.setRightAnchor(chart, 10d);
        AnchorPane.setBottomAnchor(chart, 10d);
        AnchorPane.setLeftAnchor(chart, 10d);

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 3_000_000_000l) {
                    items.forEach(item -> item.setValue(RND.nextDouble() * 75 + 25));
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        AnchorPane pane = new AnchorPane(chart);
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(48, 48, 48), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("BarChart");
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
