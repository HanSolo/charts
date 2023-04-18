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

import eu.hansolo.fx.charts.Axis;
import eu.hansolo.fx.charts.BoxPlots;
import eu.hansolo.fx.charts.BoxPlotsBuilder;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.series.ChartItemSeriesBuilder;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class BoxPlotsTest extends Application {
    private static final double   INSET = 20;
    private              BoxPlots boxPlots;
    private              Axis     yAxisLeft;


    @Override public void init() {
        yAxisLeft = Helper.createLeftAxis(0, 100, true, INSET);
        yAxisLeft.setDecimals(0);
        yAxisLeft.setAxisColor(Color.WHITE);
        yAxisLeft.setTickMarkColor(Color.WHITE);
        yAxisLeft.setTickLabelColor(Color.WHITE);

        AnchorPane.setBottomAnchor(yAxisLeft, 0d);

        boxPlots = BoxPlotsBuilder.create()
                                  .seriesList(createSeriesList())
                                  .backgroundColor(Color.rgb(48, 48, 48))
                                  .iqrStrokeColor(Color.WHITE)
                                  .iqrFillColor(Color.PURPLE)
                                  .whiskerStrokeColor(Color.WHITE)
                                  .nameVisible(true)
                                  .textFillColor(Color.WHITE)
                                  .yAxis(yAxisLeft)
                                  .build();

        AnchorPane.setTopAnchor(boxPlots, 0d);
        AnchorPane.setRightAnchor(boxPlots, 0d);
        AnchorPane.setBottomAnchor(boxPlots, 0d);
        AnchorPane.setLeftAnchor(boxPlots, INSET);
    }

    @Override public void start(Stage stage) {
        AnchorPane pane = new AnchorPane();
        pane.getChildren().addAll(boxPlots, yAxisLeft);


        pane.setBackground(new Background(new BackgroundFill(Color.rgb(48, 48, 48), CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Box Plots");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private List<ChartItemSeries> createSeriesList() {
        List<Double> store1 = List.of(350.0, 460.0, 20.0, 160.0, 580.0, 250.0, 210.0, 120.0, 200.0, 510.0, 290.0, 380.0);
        List<Double> store2 = List.of(520.0, 180.0, 260.0, 380.0, 80.0, 500.0, 630.0, 420.0, 210.0, 70.0, 440.0, 140.0);
        List<Double> store3 = List.of(500.0, 120.0, 250.0, 320.0, 50.0, 520.0, 600.0, 380.0, 200.0, 90.0, 440.0, 120.0);

        List<ChartItem> items1 = new ArrayList<>();
        List<ChartItem> items2 = new ArrayList<>();
        List<ChartItem> items3 = new ArrayList<>();

        store1.forEach(v -> items1.add(new ChartItem(v)));
        store2.forEach(v -> items2.add(new ChartItem(v)));
        store3.forEach(v -> items3.add(new ChartItem(v)));

        ChartItemSeries series1 = ChartItemSeriesBuilder.create().items(items1).name("Store 1").build();
        ChartItemSeries series2 = ChartItemSeriesBuilder.create().items(items2).name("Store 2").build();
        ChartItemSeries series3 = ChartItemSeriesBuilder.create().items(items3).name("Store 3").build();

        return List.of(series1, series2, series3);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
