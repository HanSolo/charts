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

import eu.hansolo.fx.charts.SankeyPlot.StreamFillMode;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class SankeyPlotTest extends Application {
    private SankeyPlot sankeyPlot;
    private enum Colors {
        LIGHT_BLUE(Color.web("#a6cee3")),
        ORANGE(Color.web("#fdbf6f")),
        LIGHT_RED(Color.web("#fb9a99")),
        LIGHT_GREEN(Color.web("#b2df8a")),
        YELLOW(Color.web("#ffff99")),
        PURPLE(Color.web("#cab2d6")),
        BLUE(Color.web("#1f78b4")),
        GREEN(Color.web("#33a02c"));

        private Color color;
        private Color translucentColor;

        Colors(final Color COLOR) {
            color = COLOR;
            translucentColor = Helper.getColorWithOpacity(color, 0.75);
        }

        public Color get() { return color; }
        public Color getTranslucent() { return translucentColor; }
    }


    @Override public void init() {
        // Setup chart items
        PlotItem brazil      = new PlotItem("Brazil", Colors.LIGHT_BLUE.get());
        PlotItem mexico      = new PlotItem("Mexico", Colors.ORANGE.get());
        PlotItem usa         = new PlotItem("USA", Colors.ORANGE.get());
        PlotItem canada      = new PlotItem("Canada", Colors.LIGHT_RED.get());

        PlotItem germany     = new PlotItem("Germany", Color.web("#FF48C6"));

        PlotItem portugal    = new PlotItem("Portugal", Colors.LIGHT_BLUE.get());
        PlotItem spain       = new PlotItem("Spain", Colors.LIGHT_GREEN.get());
        PlotItem england     = new PlotItem("England", Colors.LIGHT_RED.get());
        PlotItem france      = new PlotItem("France", Colors.LIGHT_GREEN.get());

        PlotItem southAfrica = new PlotItem("South Africa", Colors.YELLOW.get());
        PlotItem angola      = new PlotItem("Angola", Colors.PURPLE.get());
        PlotItem morocco     = new PlotItem("Morocco", Colors.YELLOW.get());
        PlotItem senegal     = new PlotItem("Senegal", Colors.PURPLE.get());
        PlotItem mali        = new PlotItem("Mali", Colors.BLUE.get());

        PlotItem china       = new PlotItem("China", Colors.BLUE.get());
        PlotItem japan       = new PlotItem("Japan", Colors.GREEN.get());
        PlotItem india       = new PlotItem("India", Colors.GREEN.get());

        // Setup flows
        brazil.addToOutgoing(portugal, 5);
        brazil.addToOutgoing(france, 1);
        brazil.addToOutgoing(spain, 1);
        brazil.addToOutgoing(england, 1);
        canada.addToOutgoing(portugal, 1);
        canada.addToOutgoing(france, 5);
        canada.addToOutgoing(england, 1);
        mexico.addToOutgoing(portugal, 1);
        mexico.addToOutgoing(france, 1);
        mexico.addToOutgoing(spain, 5);
        mexico.addToOutgoing(england, 1);
        usa.addToOutgoing(portugal, 1);
        usa.addToOutgoing(france, 1);
        usa.addToOutgoing(spain, 1);
        usa.addToOutgoing(england, 5);

        germany.addToOutgoing(southAfrica, 5);

        portugal.addToOutgoing(angola, 2);
        portugal.addToOutgoing(senegal, 1);
        portugal.addToOutgoing(morocco, 1);
        portugal.addToOutgoing(southAfrica, 3);
        france.addToOutgoing(angola, 1);
        france.addToOutgoing(senegal, 3);
        france.addToOutgoing(mali, 3);
        france.addToOutgoing(morocco, 3);
        france.addToOutgoing(southAfrica, 1);
        spain.addToOutgoing(senegal, 1);
        spain.addToOutgoing(morocco, 3);
        spain.addToOutgoing(southAfrica, 1);
        england.addToOutgoing(angola, 1);
        england.addToOutgoing(senegal, 1);
        england.addToOutgoing(morocco, 2);
        england.addToOutgoing(southAfrica, 7);
        southAfrica.addToOutgoing(china, 5);
        southAfrica.addToOutgoing(india, 1);
        southAfrica.addToOutgoing(japan, 3);
        angola.addToOutgoing(china, 5);
        angola.addToOutgoing(india, 1);
        angola.addToOutgoing(japan, 3);
        senegal.addToOutgoing(china, 5);
        senegal.addToOutgoing(india, 1);
        senegal.addToOutgoing(japan, 3);
        mali.addToOutgoing(china, 5);
        mali.addToOutgoing(india, 1);
        mali.addToOutgoing(japan, 3);
        morocco.addToOutgoing(china, 5);
        morocco.addToOutgoing(india, 1);
        morocco.addToOutgoing(japan, 3);

        sankeyPlot = SankeyPlotBuilder.create()
                                      .prefSize(600, 400)
                                      .items(brazil, mexico, usa, canada,
                                             germany,
                                             portugal, spain, england, france,
                                             southAfrica, angola, morocco, senegal, mali,
                                             china, japan, india)
                                      //.useItemColor(false)
                                      //.itemColor(Color.RED)
                                      .streamFillMode(StreamFillMode.GRADIENT)
                                      //.streamColor(Color.rgb(200, 0, 0, 0.25))
                                      //.textColor(Color.RED)
                                      //.autoItemWidth(false)
                                      //.itemWidth(5)
                                      //.autoItemGap(false)
                                      //.itemGap(10)
                                      //.showFlowDirection(true)
                                      .build();
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(sankeyPlot);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Sankey Plot");
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
