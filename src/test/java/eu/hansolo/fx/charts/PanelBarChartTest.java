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

import eu.hansolo.fx.charts.data.Categories;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.ChartItemBuilder;
import eu.hansolo.fx.charts.data.MonthCategory;
import eu.hansolo.fx.charts.panelbarchart.PanelBarChart;
import eu.hansolo.fx.charts.panelbarchart.PanelBarChartBuilder;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

import static eu.hansolo.fx.charts.color.MaterialDesignColors.CYAN_900;


public class PanelBarChartTest extends Application {
    private PanelBarChart chart;

    @Override public void init() {
        List<MonthCategory> categories = (List.of(Categories.JANUARY, Categories.FEBRUARY, Categories.MARCH, Categories.APRIL, Categories.MAY, Categories.JUNE, Categories.JULY, Categories.AUGUST, Categories.SEPTEMBER, Categories.OCTOBER, Categories.NOVEMBER, Categories.DECEMBER));

        // data 7
        ChartItem data7Jan = ChartItemBuilder.create().name("data 7").category(Categories.JANUARY).value(10).fill(Color.LIGHTGRAY).build();
        ChartItem data7Feb = ChartItemBuilder.create().name("data 7").category(Categories.FEBRUARY).value(20).fill(Color.LIGHTGRAY).build();
        ChartItem data7Mar = ChartItemBuilder.create().name("data 7").category(Categories.MARCH).value(5).fill(Color.LIGHTGRAY).build();
        ChartItem data7Apr = ChartItemBuilder.create().name("data 7").category(Categories.APRIL).value(13).fill(Color.LIGHTGRAY).build();
        ChartItem data7May = ChartItemBuilder.create().name("data 7").category(Categories.MAY).value(11).fill(Color.LIGHTGRAY).build();
        ChartItem data7Jun = ChartItemBuilder.create().name("data 7").category(Categories.JUNE).value(5).fill(Color.LIGHTGRAY).build();
        ChartItem data7Jul = ChartItemBuilder.create().name("data 7").category(Categories.JULY).value(1).fill(Color.LIGHTGRAY).build();
        ChartItem data7Aug = ChartItemBuilder.create().name("data 7").category(Categories.AUGUST).value(8).fill(Color.LIGHTGRAY).build();
        ChartItem data7Sep = ChartItemBuilder.create().name("data 7").category(Categories.SEPTEMBER).value(17).fill(Color.LIGHTGRAY).build();
        ChartItem data7Oct = ChartItemBuilder.create().name("data 7").category(Categories.OCTOBER).value(20).fill(Color.LIGHTGRAY).build();
        ChartItem data7Nov = ChartItemBuilder.create().name("data 7").category(Categories.NOVEMBER).value(22).fill(Color.LIGHTGRAY).build();
        ChartItem data7Dec = ChartItemBuilder.create().name("data 7").category(Categories.DECEMBER).value(21).fill(Color.LIGHTGRAY).build();

        // data 8
        ChartItem data8Jan = ChartItemBuilder.create().name("data 8").category(Categories.JANUARY).value(0).fill(Color.CYAN).build();
        ChartItem data8Feb = ChartItemBuilder.create().name("data 8").category(Categories.FEBRUARY).value(2).fill(Color.CYAN).build();
        ChartItem data8Mar = ChartItemBuilder.create().name("data 8").category(Categories.MARCH).value(5).fill(Color.CYAN).build();
        ChartItem data8Apr = ChartItemBuilder.create().name("data 8").category(Categories.APRIL).value(11).fill(Color.CYAN).build();
        ChartItem data8May = ChartItemBuilder.create().name("data 8").category(Categories.MAY).value(15).fill(Color.CYAN).build();
        ChartItem data8Jun = ChartItemBuilder.create().name("data 8").category(Categories.JUNE).value(18).fill(Color.CYAN).build();
        ChartItem data8Jul = ChartItemBuilder.create().name("data 8").category(Categories.JULY).value(22).fill(Color.CYAN).build();
        ChartItem data8Aug = ChartItemBuilder.create().name("data 8").category(Categories.AUGUST).value(23).fill(Color.CYAN).build();
        ChartItem data8Sep = ChartItemBuilder.create().name("data 8").category(Categories.SEPTEMBER).value(25).fill(Color.CYAN).build();
        ChartItem data8Oct = ChartItemBuilder.create().name("data 8").category(Categories.OCTOBER).value(27).fill(Color.CYAN).build();
        ChartItem data8Nov = ChartItemBuilder.create().name("data 8").category(Categories.NOVEMBER).value(30).fill(Color.CYAN).build();
        ChartItem data8Dec = ChartItemBuilder.create().name("data 8").category(Categories.DECEMBER).value(32).fill(Color.CYAN).build();

        // data 9
        ChartItem data9Jan = ChartItemBuilder.create().name("data 9").category(Categories.JANUARY).value(5).fill(Color.LIGHTGRAY).build();
        ChartItem data9Feb = ChartItemBuilder.create().name("data 9").category(Categories.FEBRUARY).value(2).fill(Color.LIGHTGRAY).build();
        ChartItem data9Mar = ChartItemBuilder.create().name("data 9").category(Categories.MARCH).value(8).fill(Color.LIGHTGRAY).build();
        ChartItem data9Apr = ChartItemBuilder.create().name("data 9").category(Categories.APRIL).value(13).fill(Color.LIGHTGRAY).build();
        ChartItem data9May = ChartItemBuilder.create().name("data 9").category(Categories.MAY).value(10).fill(Color.LIGHTGRAY).build();
        ChartItem data9Jun = ChartItemBuilder.create().name("data 9").category(Categories.JUNE).value(8).fill(Color.LIGHTGRAY).build();
        ChartItem data9Jul = ChartItemBuilder.create().name("data 9").category(Categories.JULY).value(2).fill(Color.LIGHTGRAY).build();
        ChartItem data9Aug = ChartItemBuilder.create().name("data 9").category(Categories.AUGUST).value(3).fill(Color.LIGHTGRAY).build();
        ChartItem data9Sep = ChartItemBuilder.create().name("data 9").category(Categories.SEPTEMBER).value(5).fill(Color.LIGHTGRAY).build();
        ChartItem data9Oct = ChartItemBuilder.create().name("data 9").category(Categories.OCTOBER).value(7).fill(Color.LIGHTGRAY).build();
        ChartItem data9Nov = ChartItemBuilder.create().name("data 9").category(Categories.NOVEMBER).value(10).fill(Color.LIGHTGRAY).build();
        ChartItem data9Dec = ChartItemBuilder.create().name("data 9").category(Categories.DECEMBER).value(12).fill(Color.LIGHTGRAY).build();


        ChartItemSeries<ChartItem> data7 = new ChartItemSeries<>(ChartType.PANEL_BAR_CHART, "data 7", CYAN_900.get(), Color.TRANSPARENT, data7Jan, data7Feb, data7Mar, data7Apr, data7May, data7Jun, data7Jul, data7Aug, data7Sep, data7Oct, data7Nov, data7Dec);
        ChartItemSeries<ChartItem> data8 = new ChartItemSeries<>(ChartType.PANEL_BAR_CHART, "data 8", CYAN_900.get(), Color.TRANSPARENT, data8Jan, data8Feb, data8Mar, data8Apr, data8May, data8Jun, data8Jul, data8Aug, data8Sep, data8Oct, data8Nov, data8Dec);
        ChartItemSeries<ChartItem> data9 = new ChartItemSeries<>(ChartType.PANEL_BAR_CHART, "data 9", CYAN_900.get(), Color.TRANSPARENT, data9Jan, data9Feb, data9Mar, data9Apr, data9May, data9Jun, data9Jul, data9Aug, data9Sep, data9Oct, data9Nov, data9Dec);

        chart = PanelBarChartBuilder.create(categories)
                                    .series(data7, data8, data9)
                                    .build();
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(chart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Panel Bar Chart");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
