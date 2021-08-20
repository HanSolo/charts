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
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;


public class SectorChartTest extends Application {
    private static final Random RND = new Random();
    private SectorChart    chart;
    private long           lastTimerCall;
    private AnimationTimer timer;


    @Override public void init() {
        ChartItem porsche911      = ChartItemBuilder.create().name("911").value(120).fill(Color.rgb(50, 184, 241)).build();
        ChartItem porscheTaycan   = ChartItemBuilder.create().name("Taycan").value(45).fill(Color.rgb(50, 184, 241)).build();
        ChartItem porschePanamera = ChartItemBuilder.create().name("Panamera").value(80).fill(Color.rgb(50, 184, 241)).build();
        ChartItem porscheMacan    = ChartItemBuilder.create().name("Macan").value(20).fill(Color.rgb(50, 184, 241)).build();
        ChartItem porscheCayenne  = ChartItemBuilder.create().name("Cayenne").value(10).fill(Color.rgb(50, 184, 241)).build();
        ChartItemSeries<ChartItem> porsche = ChartItemSeriesBuilder.create().name("Porsche").fill(Color.rgb(178, 231, 250)).items(porsche911, porscheTaycan, porschePanamera, porscheMacan, porscheCayenne).build();

        ChartItem lamboAventador  = ChartItemBuilder.create().name("Aventador").value(31).fill(Color.rgb(250, 154, 0)).build();
        ChartItem lamboHuracan    = ChartItemBuilder.create().name("Huracan").value(40).fill(Color.rgb(250, 154, 0)).build();
        ChartItem lamboUrus       = ChartItemBuilder.create().name("Urus").value(25).fill(Color.rgb(250, 154, 0)).build();
        ChartItem lamboSian       = ChartItemBuilder.create().name("Sian").value(10).fill(Color.rgb(250, 154, 0)).build();
        ChartItemSeries<ChartItem> lamborghini = ChartItemSeriesBuilder.create().name("Lamborghini").fill(Color.rgb(253, 223, 177)).items(lamboAventador, lamboHuracan, lamboUrus, lamboSian).build();

        ChartItem ferrari812       = ChartItemBuilder.create().name("812").description("Ferrari 812").value(13).fill(Color.rgb(220, 59, 21)).build();
        ChartItem ferrari296       = ChartItemBuilder.create().name("296").value(21).fill(Color.rgb(220, 59, 21)).build();
        ChartItem ferrariSf90      = ChartItemBuilder.create().name("SF 90").value(32).fill(Color.rgb(220, 59, 21)).build();
        ChartItem ferrariF8        = ChartItemBuilder.create().name("F8").value(11).fill(Color.rgb(220, 59, 21)).build();
        ChartItem ferrariRoma      = ChartItemBuilder.create().name("Roma").value(29).fill(Color.rgb(220, 59, 21)).build();
        ChartItem ferrariPortofino = ChartItemBuilder.create().name("Portofino").value(38).fill(Color.rgb(220, 59, 21)).build();
        ChartItemSeries<ChartItem> ferrari = ChartItemSeriesBuilder.create().name("Ferrari").fill(Color.rgb(244, 194, 184)).items(ferrari812, ferrari296, ferrariSf90, ferrariF8, ferrariRoma, ferrariPortofino).build();

        List<ChartItemSeries<ChartItem>> allSeries = List.of(porsche, lamborghini, ferrari);

        chart = SectorChartBuilder.create()
                                  .prefSize(600, 600)
                                  .itemTextVisible(true)
                                  .seriesTextVisible(true)
                                  .seriesSumTextVisible(false)
                                  .gridColor(Color.WHITE)
                                  .threshold(20)
                                  .thresholdColor(Color.LIME)
                                  .thresholdVisible(true)
                                  .allSeries(allSeries)
                                  .build();

        lastTimerCall = System.nanoTime();
        timer         = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 2_000_000_000l) {
                    porsche911.setValue(RND.nextDouble() * 120);
                    lamboHuracan.setValue(RND.nextDouble() * 40);
                    ferrariF8.setValue(RND.nextDouble() * 40);
                    lastTimerCall = now;
                }
            }
        };

        registerListener();
    }

    private void registerListener() {

    }

    @Override public void start(Stage stage) {
        StackPane pane  = new StackPane(chart);
        Scene     scene = new Scene(pane);

        stage.setTitle("SectorChart");
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

