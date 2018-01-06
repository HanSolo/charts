/*
 * Copyright (c) 2017 by Gerrit Grunwald
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

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static eu.hansolo.fx.charts.color.MaterialDesignColors.*;


public class NestedBarChartTest extends Application {
    private NestedBarChart chart;

    @Override public void init() {
        ChartItem p1Q1 = new ChartItem("Product 1", 16, CYAN_700.get());
        ChartItem p2Q1 = new ChartItem("Product 2", 8, CYAN_500.get());
        ChartItem p3Q1 = new ChartItem("Product 3", 4, CYAN_300.get());
        ChartItem p4Q1 = new ChartItem("Product 4", 2, CYAN_100.get());

        ChartItem p1Q2 = new ChartItem("Product 1", 12, PURPLE_700.get());
        ChartItem p2Q2 = new ChartItem("Product 2", 5, PURPLE_500.get());
        ChartItem p3Q2 = new ChartItem("Product 3", 3, PURPLE_300.get());
        ChartItem p4Q2 = new ChartItem("Product 4", 1, PURPLE_100.get());

        ChartItem p1Q3 = new ChartItem("Product 1", 14, PINK_700.get());
        ChartItem p2Q3 = new ChartItem("Product 2", 7, PINK_500.get());
        ChartItem p3Q3 = new ChartItem("Product 3", 3.5, PINK_300.get());
        ChartItem p4Q3 = new ChartItem("Product 4", 1.75, PINK_100.get());

        ChartItem p1Q4 = new ChartItem("Product 1", 18, AMBER_700.get());
        ChartItem p2Q4 = new ChartItem("Product 2", 9, AMBER_500.get());
        ChartItem p3Q4 = new ChartItem("Product 3", 4.5, AMBER_300.get());
        ChartItem p4Q4 = new ChartItem("Product 4", 2.25, AMBER_100.get());

        ChartItemSeries<ChartItem> q1 = new ChartItemSeries<>(ChartType.NESTED_BAR, "1st Quarter", CYAN_900.get(), Color.TRANSPARENT, p1Q1, p2Q1, p3Q1, p4Q1);
        ChartItemSeries<ChartItem> q2 = new ChartItemSeries<>(ChartType.NESTED_BAR, "2nd Quarter", PURPLE_900.get(), Color.TRANSPARENT, p1Q2, p2Q2, p3Q2, p4Q2);
        ChartItemSeries<ChartItem> q3 = new ChartItemSeries<>(ChartType.NESTED_BAR, "3rd Quarter", PINK_900.get(), Color.TRANSPARENT, p1Q3, p2Q3, p3Q3, p4Q3);
        ChartItemSeries<ChartItem> q4 = new ChartItemSeries<>(ChartType.NESTED_BAR, "4th Quarter", AMBER_900.get(), Color.TRANSPARENT, p1Q4, p2Q4, p3Q4, p4Q4);


        chart = new NestedBarChart(q1, q2, q3, q4);

        chart.setOnSelectionEvent(e -> System.out.println(e));
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(chart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Nested Bar Chart");
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
