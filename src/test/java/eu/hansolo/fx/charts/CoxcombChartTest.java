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
import eu.hansolo.fx.charts.tools.Order;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import java.util.List;
import java.util.Optional;


/**
 * User: hansolo
 * Date: 28.12.17
 * Time: 07:24
 */
public class CoxcombChartTest extends Application {
    private CoxcombChart chart;

    @Override public void init() {
        List<ChartItem> items = List.of(
            new ChartItem("Item 1", 27, Color.web("#96AA3B")),
            new ChartItem("Item 2", 24, Color.web("#29A783")),
            new ChartItem("Item 3", 16, Color.web("#098AA9")),
            new ChartItem("Item 4", 15, Color.web("#62386F")),
            new ChartItem("Item 5", 13, Color.web("#89447B")),
            new ChartItem("Item 6", 5, Color.web("#EF5780")));

        EventHandler<MouseEvent> onPressedHandler = e -> {
            Optional<ChartItem> opt = chart.getSelectedItem(e);
            if (opt.isEmpty()) { return; }
            ChartItem selectedItem = opt.get();
            //System.out.println(selectedItem);
            if (selectedItem.isSelected()) {
                selectedItem.setSelected(false);
            } else {
                items.forEach(item -> item.setSelected(false));
                selectedItem.setSelected(true);
            }
        };

        EventHandler<MouseEvent> onMoveHandler = e -> {
            Optional<ChartItem> opt = chart.getSelectedItem(e);
            if (opt.isEmpty()) { return; }
            System.out.println(opt.get());
        };


        chart = CoxcombChartBuilder.create()
                                   .items(items)
                                   .textColor(Color.WHITE)
                                   .autoTextColor(false)
                                   .equalSegmentAngles(true)
                                   .order(Order.ASCENDING)
                                   .onMousePressed(onPressedHandler)
                                   .onMouseMoved(onMoveHandler)
                                   .showPopup(false)
                                   .formatString("%.2f")
                                   .selectedItemFill(Color.MAGENTA)
                                   .build();
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(chart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Coxcomb Chart");
        stage.setScene(scene);
        stage.show();

        chart.addItem(new ChartItem("New Item", 3, Color.RED));
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
