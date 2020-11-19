/*
 * Copyright (c) 2020 by Gerrit Grunwald
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

import eu.hansolo.fx.charts.data.Connection;
import eu.hansolo.fx.charts.data.PlotItem;
import eu.hansolo.fx.charts.event.ConnectionEventListener;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;


public class ArcChartTest extends Application {
    private ArcChart arcChart;

    @Override public void init() {
        // Setup Data
        // Wahlberechtigte 61_500_000
        PlotItem germany   = new PlotItem("GERMANY", 1_250_000, Color.rgb(255, 51, 51));
        PlotItem france    = new PlotItem("FRANCE", 1_000_000, Color.rgb(0, 0, 180));
        PlotItem spain     = new PlotItem("SPAIN", 300_000, Color.rgb(180, 0, 0));
        PlotItem italy     = new PlotItem("ITALY", 350_000, Color.rgb(0, 180, 0));
        PlotItem india     = new PlotItem("INDIA", 750_000, Color.rgb(255, 153, 51));
        PlotItem china     = new PlotItem("CHINA", 920_000, Color.rgb(255, 255, 51));
        PlotItem japan     = new PlotItem("JAPAN", 1_060_000, Color.rgb(153, 255, 51));
        PlotItem thailand  = new PlotItem("THAILAND", 720_000, Color.rgb(51, 255, 51));
        PlotItem singapore = new PlotItem("SINGAPORE", 800_000, Color.rgb(51, 255, 153));

        Cluster asia   = new Cluster("asia", Color.rgb(220, 50, 50), china, japan, india, thailand, singapore);
        Cluster europe = new Cluster("europe", Color.rgb(50, 50, 220), germany, france, italy, spain);

        // Connections
        germany.addToOutgoing(india, 150_000);
        germany.addToOutgoing(china, 90_000);
        germany.addToOutgoing(japan, 180_000);
        germany.addToOutgoing(thailand, 15_000);
        germany.addToOutgoing(singapore, 10_000);

        spain.addToOutgoing(italy, 100_000);
        spain.addToOutgoing(japan, 20_000);
        spain.addToOutgoing(thailand, 80_000);
        System.out.println("Spain sum of outgoing -> " + spain.getSumOfOutgoing());

        italy.addToOutgoing(germany, 20_000);
        italy.addToOutgoing(spain, 10_000);
        italy.addToOutgoing(singapore, 5_000);
        System.out.println("Italy sum of outgoing -> " + italy.getSumOfOutgoing());

        france.addToOutgoing(germany, 40_000);
        france.addToOutgoing(china, 20_000);
        france.addToOutgoing(singapore, 10_000);
        france.addToOutgoing(japan, 5_000);
        System.out.println("France sum of outgoing -> " + france.getSumOfOutgoing());

        japan.addToOutgoing(germany, 70_000);

        //india.addToOutgoing(australia, 35_000);
        //india.addToOutgoing(china, 10_000);
        india.addToOutgoing(japan, 40_000);
        india.addToOutgoing(thailand, 25_000);
        india.addToOutgoing(singapore, 8_000);

        //china.addToOutgoing(australia, 10_000);
        //china.addToOutgoing(india, 7_000);
        //china.addToOutgoing(japan, 40_000);
        //china.addToOutgoing(thailand, 5_000);
        china.addToOutgoing(singapore, 4_000);

        //japan.addToOutgoing(australia, 7_000);
        //japan.addToOutgoing(india, 8_000);
        //japan.addToOutgoing(china, 175_000);
        japan.addToOutgoing(thailand, 11_000);
        japan.addToOutgoing(singapore, 18_000);

        thailand.addToOutgoing(germany, 70_000);
        thailand.addToOutgoing(india, 30_000);
        thailand.addToOutgoing(china, 22_000);
        thailand.addToOutgoing(japan, 120_000);
        thailand.addToOutgoing(singapore, 40_000);

        singapore.addToOutgoing(germany, 60_000);
        singapore.addToOutgoing(india, 90_000);
        singapore.addToOutgoing(china, 110_000);
        singapore.addToOutgoing(japan, 14_000);
        singapore.addToOutgoing(thailand, 30_000);


        List<PlotItem> items = List.of(germany, france, italy, spain, india, china, japan, thailand, singapore);

        // Register listeners to click on connections and items
        items.forEach(item -> {
            item.addItemEventListener(e -> {
                switch (e.getEventType()) {
                    case SELECTED: System.out.println("Selected: " + e.getItem().getName()); break;
                }
            });
        });

        // Setup Chart
        arcChart = ArcChartBuilder.create()
                                  .prefSize(600, 600)
                                  .items(items)
                                  .connectionOpacity(0.75)
                                  .decimals(0)
                                  .coloredConnections(false)
                                  .sortByCluster(true)
                                  .useFullCircle(true)
                                  .weightDots(true)
                                  .weightConnections(true)
                                  .build();

        ConnectionEventListener connectionListener = e -> System.out.println("From: " + e.getConnection().getOutgoingItem().getName() + " -> to: " + e.getConnection().getIncomingItem().getName() + " -> Value: " + e.getConnection().getValue());
        arcChart.getConnections().forEach(connection -> connection.addConnectionEventListener(connectionListener));

        /* Custom connection colors
        if (null != arcChart.getConnection(australia, japan)) {
            arcChart.getConnection(australia, japan).setFill(Color.BLUE);
        }
        if (null != arcChart.getConnection(australia, india)) {
            arcChart.getConnection(australia, india).setFill(Color.CHOCOLATE);
        }
        if (null != arcChart.getConnection(japan, australia)) {
            arcChart.getConnection(japan, australia).setFill(Color.POWDERBLUE);
        }
        */

        /*
        arcChart.getConnections().forEach(connection -> {
            //connection.setFill(Color.CRIMSON);
            System.out.println(connection.getOutgoingItem().getName() + " -> " + connection.getIncomingItem().getName() + " -> Value: " + connection.getValue() + " -> Color: " + connection.getFill());
        });
        */

        //Connection connection = arcChart.getConnection(thailand, china);
        //System.out.println(null == connection ? "Connection is null!!!" : "Connection from Thailand -> China: " + connection.getValue());
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(arcChart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Arc Chart");
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
