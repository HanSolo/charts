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

import eu.hansolo.fx.charts.data.ChartData;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 28.12.17
 * Time: 07:24
 */
public class CoxcombChartTest extends Application {
    private CoxcombChart chart;

    @Override public void init() {
        ChartData[] items = {
            new ChartData("Item 1", 27, Color.web("#96AA3B")),
            new ChartData("Item 2", 24, Color.web("#29A783")),
            new ChartData("Item 3", 16, Color.web("#098AA9")),
            new ChartData("Item 4", 15, Color.web("#62386F")),
            new ChartData("Item 5", 13, Color.web("#89447B")),
            new ChartData("Item 6", 5, Color.web("#EF5780"))
        };
        chart = new CoxcombChart(items);
        chart.setTextColor(Color.WHITE);
        chart.setAutoTextColor(false);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(chart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Coxcomb Chart");
        stage.setScene(scene);
        stage.show();

        chart.addItem(new ChartData("New Item", 3, Color.RED));
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
