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
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.series.ChartItemSeriesBuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class PanelBarChartTest extends Application {
    private static final Random RND = new Random();
    private PanelBarChart       chart1;
    private PanelBarChart       chart2;


    @Override public void init() {
        List<MonthCategory>              categories   = List.of(Categories.JANUARY, Categories.FEBRUARY, Categories.MARCH, Categories.APRIL, Categories.MAY, Categories.JUNE, Categories.JULY, Categories.AUGUST, Categories.SEPTEMBER, Categories.OCTOBER, Categories.NOVEMBER, Categories.DECEMBER);
        List<Integer>                    ltsReleases  = List.of(8, 11, 17);
        List<ChartItemSeries<ChartItem>> listOfSeries = new ArrayList<>();
        for (int v = 6 ; v < 19 ; v++) {
            int featureVersion = v;
            ChartItemSeries series = ChartItemSeriesBuilder.create().name("JDK " + featureVersion).build();
            categories.forEach(category -> {
                ChartItem item = ChartItemBuilder.create().name(series.getName() + " " + category.getName(TextStyle.SHORT, Locale.US)).description("Zulu 2019").category(category).value(RND.nextDouble() * 100).fill(ltsReleases.contains(featureVersion) ? Color.LIGHTBLUE : Color.LIGHTGRAY).build();
                series.getItems().add(item);
            });
            listOfSeries.add(series);
        }
        chart1 = PanelBarChartBuilder.create(categories)
                                     .listOfSeries(listOfSeries)
                                     .name("Zulu")
                                     .colorByCategory(true)
                                     .build();

        // Chart with comparison
        List<ChartItemSeries<ChartItem>> comparisonListOfSeries = new ArrayList<>();
        for (int v = 6 ; v < 19 ; v++) {
            int featureVersion = v;
            ChartItemSeries series = ChartItemSeriesBuilder.create().name("JDK " + featureVersion).build();
            categories.forEach(category -> {
                ChartItem item = ChartItemBuilder.create().name(series.getName() + " " + category.getName(TextStyle.SHORT, Locale.US)).description("Zulu 2020").category(category).value(RND.nextDouble() * 100).fill(ltsReleases.contains(featureVersion) ? Color.BLUE : Color.GRAY).build();
                series.getItems().add(item);
            });
            comparisonListOfSeries.add(series);
        }


        chart2 = PanelBarChartBuilder.create(categories)
                                     .name("Zulu 2019")
                                     .nameColor(Color.LIGHTBLUE)
                                     .listOfSeries(listOfSeries)
                                     .comparisonEnabled(true)
                                     .comparisonName("Zulu 2020")
                                     .comparisonNameColor(Color.BLUE)
                                     .comparisonListOfSeries(comparisonListOfSeries)
                                     .build();
    }

    @Override public void start(Stage stage) {
        VBox pane = new VBox(10, chart1, chart2);
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
