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

import eu.hansolo.fx.charts.SectorChart;
import eu.hansolo.fx.charts.SectorChartBuilder;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.ChartItemBuilder;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.series.ChartItemSeriesBuilder;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;


public class RadialBarChartTest extends Application {
    private static final Random      RND = new Random();
    private              SectorChart chart;
    private              long        lastTimerCall;
    private              AnimationTimer timer;


    @Override public void init() {
        ChartItem                  porsche911      = ChartItemBuilder.create().name("911").textFill(Color.WHITE).value(120).fill(Color.rgb(19, 126, 140)).build();
        ChartItem                  porscheTaycan   = ChartItemBuilder.create().name("Taycan").textFill(Color.WHITE).value(45).fill(Color.rgb(19, 126, 140)).build();
        ChartItem                  porschePanamera = ChartItemBuilder.create().name("Panamera").textFill(Color.WHITE).value(80).fill(Color.rgb(19, 126, 140)).build();
        ChartItem porscheMacan    = ChartItemBuilder.create().name("Macan").textFill(Color.WHITE).value(20).fill(Color.rgb(19, 126, 140)).build();
        ChartItem porscheCayenne  = ChartItemBuilder.create().name("Cayenne").textFill(Color.WHITE).value(10).fill(Color.rgb(19, 126, 140)).build();
        ChartItem porscheMacan2   = ChartItemBuilder.create().name("Macan").textFill(Color.WHITE).value(20).fill(Color.rgb(19, 126, 140)).build();
        ChartItem                  porscheCayenne2 = ChartItemBuilder.create().name("Cayenne").textFill(Color.WHITE).value(10).fill(Color.rgb(19, 126, 140)).build();
        ChartItemSeries<ChartItem> porsche         = ChartItemSeriesBuilder.create().name("Porsche").textFill(Color.WHITE).fill(Color.rgb(79, 186, 200)).items(porsche911, porscheTaycan, porschePanamera, porscheMacan, porscheCayenne, porscheMacan2, porscheCayenne2).build();

        ChartItem lamboAventador  = ChartItemBuilder.create().name("Aventador").textFill(Color.WHITE).value(31).fill(Color.rgb(236, 237, 150)).build();
        ChartItem lamboHuracan    = ChartItemBuilder.create().name("Huracan").textFill(Color.WHITE).value(40).fill(Color.rgb(236, 237, 150)).build();
        ChartItem lamboUrus       = ChartItemBuilder.create().name("Urus").textFill(Color.WHITE).value(25).fill(Color.rgb(236, 237, 150)).build();
        ChartItem lamboSian       = ChartItemBuilder.create().name("Sian").textFill(Color.WHITE).value(10).fill(Color.rgb(236, 237, 150)).build();
        ChartItem lamboUrus2      = ChartItemBuilder.create().name("Urus").textFill(Color.WHITE).value(25).fill(Color.rgb(236, 237, 150)).build();
        ChartItem lamboSian2      = ChartItemBuilder.create().name("Sian").textFill(Color.WHITE).value(10).fill(Color.rgb(236, 237, 150)).build();
        ChartItemSeries<ChartItem> lamborghini = ChartItemSeriesBuilder.create().name("Lamborghini").textFill(Color.WHITE).fill(Color.rgb(253, 223, 177)).items(lamboAventador, lamboHuracan, lamboUrus, lamboSian, lamboUrus2, lamboSian2).build();

        ChartItem ferrari812        = ChartItemBuilder.create().name("812").textFill(Color.WHITE).description("Ferrari 812").value(13).fill(Color.rgb(247, 73, 74)).build();
        ChartItem ferrari296        = ChartItemBuilder.create().name("296").textFill(Color.WHITE).value(21).fill(Color.rgb(247, 73, 74)).build();
        ChartItem ferrariSf90       = ChartItemBuilder.create().name("SF 90").textFill(Color.WHITE).value(32).fill(Color.rgb(247, 73, 74)).build();
        ChartItem ferrariF8         = ChartItemBuilder.create().name("F8").textFill(Color.WHITE).value(11).fill(Color.rgb(247, 73, 74)).build();
        ChartItem ferrariRoma       = ChartItemBuilder.create().name("Roma").textFill(Color.WHITE).value(29).fill(Color.rgb(247, 73, 74)).build();
        ChartItem ferrariPortofino  = ChartItemBuilder.create().name("Portofino").textFill(Color.WHITE).value(38).fill(Color.rgb(247, 73, 74)).build();
        ChartItem ferrariRoma2      = ChartItemBuilder.create().name("Roma").textFill(Color.WHITE).value(29).fill(Color.rgb(247, 73, 74)).build();
        ChartItem ferrariPortofino2 = ChartItemBuilder.create().name("Portofino").textFill(Color.WHITE).value(38).fill(Color.rgb(247, 73, 74)).build();
        ChartItemSeries<ChartItem> ferrari = ChartItemSeriesBuilder.create().name("Ferrari").textFill(Color.WHITE).fill(Color.rgb(244, 194, 184)).items(ferrari812, ferrari296, ferrariSf90, ferrariF8, ferrariRoma, ferrariPortofino, ferrariRoma2, ferrariPortofino2).build();

        ChartItem golf1               = ChartItemBuilder.create().name("812").textFill(Color.WHITE).description("Ferrari 812").value(13).fill(Color.rgb(42, 144, 89)).build();
        ChartItem passat1             = ChartItemBuilder.create().name("296").textFill(Color.WHITE).value(21).fill(Color.rgb(42, 144, 89)).build();
        ChartItem polo1               = ChartItemBuilder.create().name("SF 90").textFill(Color.WHITE).value(32).fill(Color.rgb(42, 144, 89)).build();
        ChartItem bulli1              = ChartItemBuilder.create().name("F8").textFill(Color.WHITE).value(11).fill(Color.rgb(42, 144, 89)).build();
        ChartItem phaeton             = ChartItemBuilder.create().name("Roma").textFill(Color.WHITE).value(29).fill(Color.rgb(42, 144, 89)).build();
        ChartItem tuareg              = ChartItemBuilder.create().name("Portofino").textFill(Color.WHITE).value(38).fill(Color.rgb(42, 144, 89)).build();
        ChartItem tuareg2             = ChartItemBuilder.create().name("Roma").textFill(Color.WHITE).value(29).fill(Color.rgb(42, 144, 89)).build();
        ChartItem beetle              = ChartItemBuilder.create().name("Portofino").textFill(Color.WHITE).value(38).fill(Color.rgb(42, 144, 89)).build();
        ChartItemSeries<ChartItem> vw = ChartItemSeriesBuilder.create().name("VW").textFill(Color.WHITE).fill(Color.rgb(102, 204, 159)).items(golf1, passat1, polo1, bulli1, phaeton, tuareg, tuareg2, beetle).build();

        List<ChartItemSeries<ChartItem>> allSeries = List.of(porsche, lamborghini, ferrari, vw);

        chart = SectorChartBuilder.create()
                                  .prefSize(600, 600)
                                  .radialBarChartMode(true)
                                  .itemTextVisible(true)
                                  .seriesTextVisible(true)
                                  .seriesSumTextVisible(false)
                                  .gridColor(Color.DARKGRAY)
                                  .threshold(50)
                                  .thresholdColor(Color.LIME)
                                  .thresholdVisible(true)
                                  .seriesBackgroundVisible(false)
                                  .allSeries(allSeries)
                                  .build();

        lastTimerCall = System.nanoTime();
        timer         = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 200_000_000l) {
                    porsche.getItems().forEach(item -> item.setValue(RND.nextDouble() * 100));
                    ferrari.getItems().forEach(item -> item.setValue(RND.nextDouble() * 100));
                    lamborghini.getItems().forEach(item -> item.setValue(RND.nextDouble() * 100));
                    vw.getItems().forEach(item -> item.setValue(RND.nextDouble() * 100));
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
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(7, 36, 56), CornerRadii.EMPTY, Insets.EMPTY)));
        Scene     scene = new Scene(pane);

        stage.setTitle("RadialBarChart");
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

