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
import eu.hansolo.fx.charts.data.ChartItemBuilder;
import eu.hansolo.fx.charts.data.Metadata;
import eu.hansolo.fx.charts.tools.Order;
import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
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
    private VBox         textPane;
    private Metainfo     metainfo1;
    private Metainfo     metainfo2;
    private Metainfo     metainfo3;
    private Metainfo     metainfo4;
    private Metainfo     metainfo5;
    private Metainfo     metainfo6;


    @Override public void init() {
        metainfo1 = new Metainfo("Text 1");
        metainfo2 = new Metainfo("Text 2");
        metainfo3 = new Metainfo("Text 3");
        metainfo4 = new Metainfo("Text 4");
        metainfo5 = new Metainfo("Text 5");
        metainfo6 = new Metainfo("Text 6");


        List<ChartItem> items = List.of(
            ChartItemBuilder.create().name("Item 1").value(27).fill(Color.web("#96AA3B")).metadata(metainfo1).build(),
            ChartItemBuilder.create().name("Item 2").value(24).fill(Color.web("#29A783")).metadata(metainfo2).build(),
            ChartItemBuilder.create().name("Item 3").value(16).fill(Color.web("#098AA9")).metadata(metainfo3).build(),
            ChartItemBuilder.create().name("Item 4").value(15).fill(Color.web("#62386F")).metadata(metainfo4).build(),
            ChartItemBuilder.create().name("Item 5").value(13).fill(Color.web("#89447B")).metadata(metainfo5).build(),
            ChartItemBuilder.create().name("Item 6").value(5).fill(Color.web("#EF5780")).metadata(metainfo6).build()
            //new ChartItem("Item 1", 27, Color.web("#96AA3B")),
            //new ChartItem("Item 2", 24, Color.web("#29A783")),
            //new ChartItem("Item 3", 16, Color.web("#098AA9")),
            //new ChartItem("Item 4", 15, Color.web("#62386F")),
            //new ChartItem("Item 5", 13, Color.web("#89447B")),
            //new ChartItem("Item 6", 5, Color.web("#EF5780"))
            );

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
                                   .useChartItemTextFill(false)
                                   .equalSegmentAngles(true)
                                   .order(Order.ASCENDING)
                                   .onMousePressed(onPressedHandler)
                                   .onMouseMoved(onMoveHandler)
                                   .showPopup(false)
                                   .showItemName(true)
                                   .formatString("%.2f")
                                   .selectedItemFill(Color.MAGENTA)
                                   .build();

        Label row1 = new Label("Main title");
        Label row2 = new Label("Sub title 1");
        Label row3 = new Label("Sub title 2");
        textPane = new VBox(10, row1, row2, row3);
        textPane.setMouseTransparent(true);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(chart, textPane);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Coxcomb Chart");
        stage.setScene(scene);
        stage.show();

        chart.addItem(new ChartItem("New Item", 3, Color.RED));

        metainfo6.setText("Changed Text 6");
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }


    // ******************** Inner Classes *************************************
    public class Metainfo implements Metadata {
        private StringProperty text;

        public Metainfo(final String text) {
            this.text = new StringPropertyBase(text) {
                @Override protected void invalidated() { super.invalidated(); }
                @Override public Object getBean() { return Metainfo.this; }
                @Override public String getName() { return "text"; }
            };
        }

        public String getText() { return text.get(); }
        public void setText(final String text) { this.text.set(text); }
        public StringProperty textProperty() { return text; }

        @Override public String toString() { return text.get(); }
    }
}
