/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2022 Gerrit Grunwald.
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


public class BoxPlotTest extends Application {
    private BoxPlot         boxPlot;
    private List<ChartItem> items;


    @Override public void init() {
        items = new ArrayList<>();

        // Prepare data for BoxPlot
        prepareData();

        boxPlot = BoxPlotBuilder.create()
                                .items(items)
                                .whiskerStrokeColor(Color.WHITE)
                                .iqrStrokeColor(Color.WHITE)
                                .outlierFillColor(Color.WHITE)
                                .textFillColor(Color.WHITE)
                                .name("Test")
                                .decimals(2)
                                .build();

        AnchorPane.setTopAnchor(boxPlot, 0d);
        AnchorPane.setRightAnchor(boxPlot, 0d);
        AnchorPane.setBottomAnchor(boxPlot, 0d);
        AnchorPane.setLeftAnchor(boxPlot, 0d);

    }

    @Override public void start(Stage stage) {
        AnchorPane pane = new AnchorPane();
        pane.getChildren().addAll(boxPlot);


        pane.setBackground(new Background(new BackgroundFill(Color.rgb(48, 48, 48), CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Box Plot");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private void prepareData() {
        //List<Double> data = List.of(52.0, 57.0, 57.0, 58.0, 63.0, 66.0, 66.0, 67.0, 67.0, 68.0, 69.0, 70.0, 70.0, 70.0, 70.0, 72.0, 73.0, 75.0, 75.0, 76.0, 76.0, 78.0, 79.0, 89.0);
        //List<Double> data = List.of(57.0, 57.0, 57.0, 58.0, 63.0, 66.0, 66.0, 67.0, 67.0, 68.0, 69.0, 70.0, 70.0, 70.0, 70.0, 72.0, 73.0, 75.0, 75.0, 76.0, 76.0, 78.0, 79.0, 81.0);
        List<Double> data = List.of(91.00,  95.00,  54.00,  69.00,  80.00,  85.00,  88.00,  73.00,  71.00,  70.00,  66.00,  90.00,  86.00,  84.00,  73.00);
        data.forEach(v -> items.add(new ChartItem(v)));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
