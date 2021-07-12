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


import eu.hansolo.fx.charts.data.BubbleGridChartItem;
import eu.hansolo.fx.charts.data.BubbleGridChartItemBuilder;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.ChartItemBuilder;
import eu.hansolo.fx.charts.tools.Order;
import eu.hansolo.fx.charts.tools.Topic;
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

import java.util.List;
import java.util.Random;


public class BubbleGridChartTest extends Application {
    private static final Random RND = new Random();

    private BubbleGridChart bubbleGridChart;
    private BubbleGridChartItem peaches1;
    private BubbleGridChartItem peaches2;
    private BubbleGridChartItem peaches3;
    private BubbleGridChartItem peaches4;
    private BubbleGridChartItem peaches5;
    private BubbleGridChartItem peaches6;
    private BubbleGridChartItem peaches7;
    private BubbleGridChartItem peaches8;
    private long                lastTimerCall;
    private AnimationTimer      timer;



    @Override public void init() {
        // Setup Data

        // Y categories
        ChartItem ripe                = ChartItemBuilder.create().name("Ripe").index(0).fill(Color.BLUE).build();
        ChartItem unripe              = ChartItemBuilder.create().name("Unripe").index(1).fill(Color.BLUE).build();
        ChartItem eatenByBirds        = ChartItemBuilder.create().name("Eaten by birds").index(2).fill(Color.ORANGE).build();
        ChartItem eatenByCaterpillars = ChartItemBuilder.create().name("Eaten by caterpillars").index(3).fill(Color.LIGHTBLUE).build();
        ChartItem hailDamaged         = ChartItemBuilder.create().name("Hail damaged").index(4).fill(Color.LIGHTBLUE).build();
        ChartItem notEnoughWater      = ChartItemBuilder.create().name("Not enough water").index(5).fill(Color.LIGHTBLUE).build();
        ChartItem mouldy              = ChartItemBuilder.create().name("Mouldy").index(6).fill(Color.LIGHTBLUE).build();
        ChartItem rotten              = ChartItemBuilder.create().name("Rotten").index(7).fill(Color.LIGHTBLUE).build();

        // X categories
        ChartItem peaches  = ChartItemBuilder.create().name("Peaches").index(0).fill(Color.ORANGERED).build();
        ChartItem apples   = ChartItemBuilder.create().name("Apples").index(1).fill(Color.LIMEGREEN).build();
        ChartItem pears    = ChartItemBuilder.create().name("Pears").index(2).fill(Color.ORANGE).build();
        ChartItem plums    = ChartItemBuilder.create().name("Plums").index(3).fill(Color.PURPLE).build();
        ChartItem apricots = ChartItemBuilder.create().name("Apricots").index(4).fill(Color.DARKORANGE).build();

        // Dataset
        peaches1  = BubbleGridChartItemBuilder.create().categoryXItem(peaches).categoryYItem(ripe).value(60).fill(Color.BLUE).build();
        peaches2  = BubbleGridChartItemBuilder.create().categoryXItem(peaches).categoryYItem(unripe).value(5).fill(Color.BLUE).build();
        peaches3  = BubbleGridChartItemBuilder.create().categoryXItem(peaches).categoryYItem(eatenByBirds).value(10).fill(Color.BLUE).build();
        peaches4  = BubbleGridChartItemBuilder.create().categoryXItem(peaches).categoryYItem(eatenByCaterpillars).value(0).fill(Color.BLUE).build();
        peaches5  = BubbleGridChartItemBuilder.create().categoryXItem(peaches).categoryYItem(hailDamaged).value(10).fill(Color.BLUE).build();
        peaches6  = BubbleGridChartItemBuilder.create().categoryXItem(peaches).categoryYItem(notEnoughWater).value(0).fill(Color.BLUE).build();
        peaches7  = BubbleGridChartItemBuilder.create().categoryXItem(peaches).categoryYItem(mouldy).value(5).fill(Color.BLUE).build();
        peaches8  = BubbleGridChartItemBuilder.create().categoryXItem(peaches).categoryYItem(rotten).value(10).fill(Color.BLUE).build();

        BubbleGridChartItem apples1  = BubbleGridChartItemBuilder.create().categoryXItem(apples).categoryYItem(ripe).value(90).fill(Color.BLUE).build();
        BubbleGridChartItem apples2  = BubbleGridChartItemBuilder.create().categoryXItem(apples).categoryYItem(unripe).value(0).fill(Color.BLUE).build();
        BubbleGridChartItem apples3  = BubbleGridChartItemBuilder.create().categoryXItem(apples).categoryYItem(eatenByBirds).value(0).fill(Color.BLUE).build();
        BubbleGridChartItem apples4  = BubbleGridChartItemBuilder.create().categoryXItem(apples).categoryYItem(eatenByCaterpillars).value(3).fill(Color.BLUE).build();
        BubbleGridChartItem apples5  = BubbleGridChartItemBuilder.create().categoryXItem(apples).categoryYItem(hailDamaged).value(2).fill(Color.BLUE).build();
        BubbleGridChartItem apples6  = BubbleGridChartItemBuilder.create().categoryXItem(apples).categoryYItem(notEnoughWater).value(0).fill(Color.BLUE).build();
        BubbleGridChartItem apples7  = BubbleGridChartItemBuilder.create().categoryXItem(apples).categoryYItem(mouldy).value(0).fill(Color.BLUE).build();
        BubbleGridChartItem apples8  = BubbleGridChartItemBuilder.create().categoryXItem(apples).categoryYItem(rotten).value(5).fill(Color.BLUE).build();

        BubbleGridChartItem pears1  = BubbleGridChartItemBuilder.create().categoryXItem(pears).categoryYItem(ripe).value(30).fill(Color.BLUE).build();
        BubbleGridChartItem pears2  = BubbleGridChartItemBuilder.create().categoryXItem(pears).categoryYItem(unripe).value(40).fill(Color.BLUE).build();
        BubbleGridChartItem pears3  = BubbleGridChartItemBuilder.create().categoryXItem(pears).categoryYItem(eatenByBirds).value(5).fill(Color.BLUE).build();
        BubbleGridChartItem pears4  = BubbleGridChartItemBuilder.create().categoryXItem(pears).categoryYItem(eatenByCaterpillars).value(10).fill(Color.BLUE).build();
        BubbleGridChartItem pears5  = BubbleGridChartItemBuilder.create().categoryXItem(pears).categoryYItem(hailDamaged).value(5).fill(Color.BLUE).build();
        BubbleGridChartItem pears6  = BubbleGridChartItemBuilder.create().categoryXItem(pears).categoryYItem(notEnoughWater).value(0).fill(Color.BLUE).build();
        BubbleGridChartItem pears7  = BubbleGridChartItemBuilder.create().categoryXItem(pears).categoryYItem(mouldy).value(0).fill(Color.BLUE).build();
        BubbleGridChartItem pears8  = BubbleGridChartItemBuilder.create().categoryXItem(pears).categoryYItem(rotten).value(10).fill(Color.BLUE).build();

        BubbleGridChartItem plums1  = BubbleGridChartItemBuilder.create().categoryXItem(plums).categoryYItem(ripe).value(15).fill(Color.BLUE).build();
        BubbleGridChartItem plums2  = BubbleGridChartItemBuilder.create().categoryXItem(plums).categoryYItem(unripe).value(5).fill(Color.BLUE).build();
        BubbleGridChartItem plums3  = BubbleGridChartItemBuilder.create().categoryXItem(plums).categoryYItem(eatenByBirds).value(30).fill(Color.BLUE).build();
        BubbleGridChartItem plums4  = BubbleGridChartItemBuilder.create().categoryXItem(plums).categoryYItem(eatenByCaterpillars).value(0).fill(Color.BLUE).build();
        BubbleGridChartItem plums5  = BubbleGridChartItemBuilder.create().categoryXItem(plums).categoryYItem(hailDamaged).value(2).fill(Color.BLUE).build();
        BubbleGridChartItem plums6  = BubbleGridChartItemBuilder.create().categoryXItem(plums).categoryYItem(notEnoughWater).value(0).fill(Color.BLUE).build();
        BubbleGridChartItem plums7  = BubbleGridChartItemBuilder.create().categoryXItem(plums).categoryYItem(mouldy).value(5).fill(Color.BLUE).build();
        BubbleGridChartItem plums8  = BubbleGridChartItemBuilder.create().categoryXItem(plums).categoryYItem(rotten).value(43).fill(Color.BLUE).build();

        BubbleGridChartItem apricots1  = BubbleGridChartItemBuilder.create().categoryXItem(apricots).categoryYItem(ripe).value(20).fill(Color.BLUE).build();
        BubbleGridChartItem apricots2  = BubbleGridChartItemBuilder.create().categoryXItem(apricots).categoryYItem(unripe).value(40).fill(Color.BLUE).build();
        BubbleGridChartItem apricots3  = BubbleGridChartItemBuilder.create().categoryXItem(apricots).categoryYItem(eatenByBirds).value(5).fill(Color.BLUE).build();
        BubbleGridChartItem apricots4  = BubbleGridChartItemBuilder.create().categoryXItem(apricots).categoryYItem(eatenByCaterpillars).value(0).fill(Color.BLUE).build();
        BubbleGridChartItem apricots5  = BubbleGridChartItemBuilder.create().categoryXItem(apricots).categoryYItem(hailDamaged).value(15).fill(Color.BLUE).build();
        BubbleGridChartItem apricots6  = BubbleGridChartItemBuilder.create().categoryXItem(apricots).categoryYItem(notEnoughWater).value(1).fill(Color.BLUE).build();
        BubbleGridChartItem apricots7  = BubbleGridChartItemBuilder.create().categoryXItem(apricots).categoryYItem(mouldy).value(14).fill(Color.BLUE).build();
        BubbleGridChartItem apricots8  = BubbleGridChartItemBuilder.create().categoryXItem(apricots).categoryYItem(rotten).value(5).fill(Color.BLUE).build();
        
        List<BubbleGridChartItem> chartItems = List.of(peaches1, peaches2, peaches3, peaches4, peaches5, peaches6, peaches7, peaches8,
                                                       apples1, apples2, apples3, apples4, apples5, apples6, apples7, apples8,
                                                       pears1, pears2, pears3, pears4, pears5, pears6, pears7, pears8,
                                                       plums1, plums2, plums3, plums4, plums5, plums6, plums7, plums8,
                                                       apricots1, apricots2, apricots3, apricots4, apricots5, apricots6, apricots7, apricots8);

        // Setup Chart
        bubbleGridChart = BubbleGridChartBuilder.create()
                                                .chartBackground(Color.WHITE)
                                                .textColor(Color.BLACK)
                                                .gridColor(Color.rgb(0, 0, 0, 0.1))
                                                .showGrid(true)
                                                .showValues(true)
                                                .showPercentage(true)
                                                .items(chartItems)
                                                .sortCategoryX(Topic.NAME, Order.ASCENDING)
                                                .sortCategoryY(Topic.VALUE, Order.DESCENDING)
                                                .useXCategoryFill()
                                                .autoBubbleTextColor(true)
                                                .useGradientFill(false)
                                                .gradient(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                                                                             new Stop(0.00, Color.web("#2C67D5")),
                                                                             new Stop(0.25, Color.web("#00BF6C")),
                                                                             new Stop(0.50, Color.web("#FFD338")),
                                                                             new Stop(0.75, Color.web("#FF8235")),
                                                                             new Stop(1.00, Color.web("#F23C5A"))))
                                                .build();

        lastTimerCall = System.nanoTime();
        timer         = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 2_000_000_000l) {
                    peaches1.setValue(RND.nextInt(60));
                    peaches2.setValue(RND.nextInt(60));
                    peaches3.setValue(RND.nextInt(60));
                    peaches4.setValue(RND.nextInt(60));
                    peaches5.setValue(RND.nextInt(60));
                    peaches6.setValue(RND.nextInt(60));
                    peaches7.setValue(RND.nextInt(60));
                    peaches8.setValue(RND.nextInt(60));
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(bubbleGridChart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane, 1240, 1000);

        stage.setTitle("Bubble Grid Chart");
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

