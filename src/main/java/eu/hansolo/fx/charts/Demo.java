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

import eu.hansolo.fx.charts.data.PlotItem;
import eu.hansolo.fx.charts.font.Fonts;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;


/**
 * User: hansolo
 * Date: 12.06.20
 * Time: 10:17
 */
public class Demo extends Application {
    private ArcChart arcChart;

    @Override public void init() {
        // Data taken from: https://github.com/evelinag/StarWars-social-network/tree/master/networks
        List<PlotItem> plotItems = List.of(
            new PlotItem("R2-D2", 33, Color.web("#bde0f6")),
            new PlotItem("QUI-GON", 61, Color.web("#4f4fb1")),
            new PlotItem("NUTE GUNRAY", 19, Color.web("#808080")),
            new PlotItem("PK-4", 3, Color.web("#808080")),
            new PlotItem("TC-14", 5, Color.web("#808080")),
            new PlotItem("OBI-WAN", 34, Color.web("#6946BC")),
            new PlotItem("DOFINE", 4, Color.web("#808080")),
            new PlotItem("RUNE", 11, Color.web("#808080")),
            new PlotItem("TEY HOW", 5, Color.web("#808080")),
            new PlotItem("EMPEROR", 14, Color.web("#191970")),
            new PlotItem("CAPTAIN PANAKA", 20, Color.web("#808080")),
            new PlotItem("SIO BIBBLE", 8, Color.web("#808080")),
            new PlotItem("JAR JAR", 36, Color.web("#FA9600")),
            new PlotItem("TARPALS", 4, Color.web("#808080")),
            new PlotItem("BOSS NASS", 5, Color.web("#808080")),
            new PlotItem("PADME", 31, Color.web("#BC25BA")),
            new PlotItem("RIC OLIE", 12, Color.web("#808080")),
            new PlotItem("WATTO", 8, Color.web("#808080")),
            new PlotItem("ANAKIN", 41, Color.web("#ce3b59")),
            new PlotItem("SEBULBA", 4, Color.web("#808080")),
            new PlotItem("JIRA", 4, Color.web("#808080")),
            new PlotItem("SHMI", 11, Color.web("#808080")),
            new PlotItem("C-3PO", 6, Color.web("#FFD700")),
            new PlotItem("DARTH MAUL", 6, Color.web("#808080")),
            new PlotItem("KITSTER", 5, Color.web("#808080")),
            new PlotItem("WALD", 4, Color.web("#808080")),
            new PlotItem("FODE/BEED", 12, Color.web("#808080")),
            new PlotItem("JABBA", 4, Color.web("#808080")),
            new PlotItem("GREEDO", 3, Color.web("#808080")),
            new PlotItem("VALORUM", 4, Color.web("#808080")),
            new PlotItem("MACE WINDU", 6, Color.web("#808080")),
            new PlotItem("KI-ADI-MUNDI", 5, Color.web("#808080")),
            new PlotItem("YODA", 7, Color.web("#9ACD32")),
            new PlotItem("RABE", 3, Color.web("#808080")),
            new PlotItem("BAIL ORGANA", 3, Color.web("#808080")),
            new PlotItem("GENERAL CEEL", 5, Color.web("#808080")),
            new PlotItem("BRAVO TWO", 6, Color.web("#808080")),
            new PlotItem("BRAVO THREE", 4, Color.web("#808080"))
        );
        System.out.println("no of items: " + plotItems.size());
        plotItems.get(15).addToOutgoing(plotItems.get(0), 11);
        plotItems.get(1).addToOutgoing(plotItems.get(0), 14);
        plotItems.get(18).addToOutgoing(plotItems.get(0), 16);
        plotItems.get(0).addToOutgoing(plotItems.get(17), 3);
        plotItems.get(22).addToOutgoing(plotItems.get(0), 2);
        plotItems.get(24).addToOutgoing(plotItems.get(0), 2);
        plotItems.get(2).addToOutgoing(plotItems.get(1), 1);
        plotItems.get(3).addToOutgoing(plotItems.get(4), 1);
        plotItems.get(5).addToOutgoing(plotItems.get(4), 1);
        plotItems.get(1).addToOutgoing(plotItems.get(4), 1);
        plotItems.get(5).addToOutgoing(plotItems.get(1), 26);
        plotItems.get(2).addToOutgoing(plotItems.get(4), 1);
        plotItems.get(6).addToOutgoing(plotItems.get(2), 1);
        plotItems.get(6).addToOutgoing(plotItems.get(4), 1);
        plotItems.get(2).addToOutgoing(plotItems.get(7), 8);
        plotItems.get(7).addToOutgoing(plotItems.get(8), 2);
        plotItems.get(2).addToOutgoing(plotItems.get(8), 1);
        plotItems.get(10).addToOutgoing(plotItems.get(9), 3);
        plotItems.get(9).addToOutgoing(plotItems.get(11), 1);
        plotItems.get(10).addToOutgoing(plotItems.get(11), 3);
        plotItems.get(12).addToOutgoing(plotItems.get(1), 22);
        plotItems.get(12).addToOutgoing(plotItems.get(5), 12);
        plotItems.get(12).addToOutgoing(plotItems.get(13), 2);
        plotItems.get(14).addToOutgoing(plotItems.get(1), 2);
        plotItems.get(14).addToOutgoing(plotItems.get(5), 2);
        plotItems.get(14).addToOutgoing(plotItems.get(12), 2);
        plotItems.get(9).addToOutgoing(plotItems.get(2), 5);
        plotItems.get(9).addToOutgoing(plotItems.get(7), 3);
        plotItems.get(2).addToOutgoing(plotItems.get(11), 2);
        plotItems.get(12).addToOutgoing(plotItems.get(11), 1);
        plotItems.get(10).addToOutgoing(plotItems.get(12), 7);
        plotItems.get(1).addToOutgoing(plotItems.get(11), 2);
        plotItems.get(10).addToOutgoing(plotItems.get(1), 9);
        plotItems.get(10).addToOutgoing(plotItems.get(15), 7);
        plotItems.get(15).addToOutgoing(plotItems.get(1), 16);
        plotItems.get(15).addToOutgoing(plotItems.get(11), 1);
        plotItems.get(10).addToOutgoing(plotItems.get(5), 7);
        plotItems.get(5).addToOutgoing(plotItems.get(16), 3);
        plotItems.get(12).addToOutgoing(plotItems.get(16), 1);
        plotItems.get(1).addToOutgoing(plotItems.get(16), 2);
        plotItems.get(10).addToOutgoing(plotItems.get(16), 2);
        plotItems.get(12).addToOutgoing(plotItems.get(15), 9);
        plotItems.get(1).addToOutgoing(plotItems.get(17), 6);
        plotItems.get(18).addToOutgoing(plotItems.get(17), 4);
        plotItems.get(15).addToOutgoing(plotItems.get(17), 3);
        plotItems.get(18).addToOutgoing(plotItems.get(1), 22);
        plotItems.get(18).addToOutgoing(plotItems.get(15), 16);
        plotItems.get(12).addToOutgoing(plotItems.get(19), 2);
        plotItems.get(18).addToOutgoing(plotItems.get(19), 2);
        plotItems.get(1).addToOutgoing(plotItems.get(19), 2);
        plotItems.get(15).addToOutgoing(plotItems.get(19), 2);
        plotItems.get(18).addToOutgoing(plotItems.get(12), 8);
        plotItems.get(18).addToOutgoing(plotItems.get(20), 2);
        plotItems.get(20).addToOutgoing(plotItems.get(1), 2);
        plotItems.get(20).addToOutgoing(plotItems.get(15), 1);
        plotItems.get(18).addToOutgoing(plotItems.get(21), 7);
        plotItems.get(12).addToOutgoing(plotItems.get(21), 3);
        plotItems.get(1).addToOutgoing(plotItems.get(21), 8);
        plotItems.get(15).addToOutgoing(plotItems.get(21), 5);
        plotItems.get(18).addToOutgoing(plotItems.get(22), 3);
        plotItems.get(22).addToOutgoing(plotItems.get(15), 2);
        plotItems.get(5).addToOutgoing(plotItems.get(11), 1);
        plotItems.get(23).addToOutgoing(plotItems.get(9), 3);
        plotItems.get(18).addToOutgoing(plotItems.get(24), 3);
        plotItems.get(18).addToOutgoing(plotItems.get(25), 2);
        plotItems.get(24).addToOutgoing(plotItems.get(25), 1);
        plotItems.get(12).addToOutgoing(plotItems.get(24), 1);
        plotItems.get(24).addToOutgoing(plotItems.get(1), 2);
        plotItems.get(12).addToOutgoing(plotItems.get(25), 1);
        plotItems.get(1).addToOutgoing(plotItems.get(25), 2);
        plotItems.get(18).addToOutgoing(plotItems.get(5), 5);
        plotItems.get(5).addToOutgoing(plotItems.get(21), 1);
        plotItems.get(22).addToOutgoing(plotItems.get(17), 1);
        plotItems.get(24).addToOutgoing(plotItems.get(17), 1);
        plotItems.get(22).addToOutgoing(plotItems.get(1), 1);
        plotItems.get(22).addToOutgoing(plotItems.get(24), 1);
        plotItems.get(24).addToOutgoing(plotItems.get(15), 1);
        plotItems.get(26).addToOutgoing(plotItems.get(27), 1);
        plotItems.get(27).addToOutgoing(plotItems.get(21), 1);
        plotItems.get(19).addToOutgoing(plotItems.get(21), 1);
        plotItems.get(18).addToOutgoing(plotItems.get(27), 1);
        plotItems.get(27).addToOutgoing(plotItems.get(12), 1);
        plotItems.get(27).addToOutgoing(plotItems.get(15), 1);
        plotItems.get(27).addToOutgoing(plotItems.get(19), 1);
        plotItems.get(27).addToOutgoing(plotItems.get(1), 1);
        plotItems.get(26).addToOutgoing(plotItems.get(12), 2);
        plotItems.get(26).addToOutgoing(plotItems.get(15), 1);
        plotItems.get(28).addToOutgoing(plotItems.get(1), 1);
        plotItems.get(18).addToOutgoing(plotItems.get(28), 1);
        plotItems.get(28).addToOutgoing(plotItems.get(25), 1);
        plotItems.get(24).addToOutgoing(plotItems.get(21), 1);
        plotItems.get(18).addToOutgoing(plotItems.get(10), 2);
        plotItems.get(18).addToOutgoing(plotItems.get(16), 4);
        plotItems.get(9).addToOutgoing(plotItems.get(29), 2);
        plotItems.get(9).addToOutgoing(plotItems.get(12), 2);
        plotItems.get(9).addToOutgoing(plotItems.get(1), 1);
        plotItems.get(12).addToOutgoing(plotItems.get(29), 1);
        plotItems.get(1).addToOutgoing(plotItems.get(29), 1);
        plotItems.get(30).addToOutgoing(plotItems.get(1), 2);
        plotItems.get(31).addToOutgoing(plotItems.get(1), 2);
        plotItems.get(1).addToOutgoing(plotItems.get(32), 2);
        plotItems.get(1).addToOutgoing(plotItems.get(33), 1);
        plotItems.get(31).addToOutgoing(plotItems.get(30), 3);
        plotItems.get(30).addToOutgoing(plotItems.get(32), 4);
        plotItems.get(18).addToOutgoing(plotItems.get(30), 3);
        plotItems.get(30).addToOutgoing(plotItems.get(33), 1);
        plotItems.get(31).addToOutgoing(plotItems.get(32), 3);
        plotItems.get(18).addToOutgoing(plotItems.get(31), 2);
        plotItems.get(31).addToOutgoing(plotItems.get(33), 1);
        plotItems.get(18).addToOutgoing(plotItems.get(32), 3);
        plotItems.get(33).addToOutgoing(plotItems.get(32), 1);
        plotItems.get(18).addToOutgoing(plotItems.get(33), 1);
        plotItems.get(34).addToOutgoing(plotItems.get(9), 1);
        plotItems.get(34).addToOutgoing(plotItems.get(29), 1);
        plotItems.get(5).addToOutgoing(plotItems.get(32), 3);
        plotItems.get(30).addToOutgoing(plotItems.get(5), 2);
        plotItems.get(31).addToOutgoing(plotItems.get(5), 1);
        plotItems.get(14).addToOutgoing(plotItems.get(15), 2);
        plotItems.get(23).addToOutgoing(plotItems.get(2), 3);
        plotItems.get(18).addToOutgoing(plotItems.get(14), 1);
        plotItems.get(14).addToOutgoing(plotItems.get(10), 1);
        plotItems.get(5).addToOutgoing(plotItems.get(15), 1);
        plotItems.get(9).addToOutgoing(plotItems.get(35), 1);
        plotItems.get(35).addToOutgoing(plotItems.get(2), 1);
        plotItems.get(23).addToOutgoing(plotItems.get(35), 1);
        plotItems.get(23).addToOutgoing(plotItems.get(7), 1);
        plotItems.get(36).addToOutgoing(plotItems.get(16), 4);
        plotItems.get(18).addToOutgoing(plotItems.get(36), 2);
        plotItems.get(35).addToOutgoing(plotItems.get(12), 1);
        plotItems.get(2).addToOutgoing(plotItems.get(15), 1);
        plotItems.get(6).addToOutgoing(plotItems.get(8), 1);
        plotItems.get(37).addToOutgoing(plotItems.get(36), 2);
        plotItems.get(37).addToOutgoing(plotItems.get(16), 2);
        plotItems.get(18).addToOutgoing(plotItems.get(37), 1);
        plotItems.get(9).addToOutgoing(plotItems.get(15), 1);

        arcChart = ArcChartBuilder.create()
                                  .prefSize(600, 600)
                                  .items(plotItems)
                                  .connectionOpacity(0.75)
                                  .decimals(0)
                                  .textColor(Color.rgb(90,90,90))
                                  .coloredConnections(false)
                                  .sortByCluster(false)
                                  .useFullCircle(true)
                                  .weightDots(false)
                                  .weightConnections(true)
                                  .connectionOpacity(0.2)
                                  .build();
        arcChart.setSelectedItem(plotItems.stream().filter(item -> item.getName().equals("PADME")).findFirst().get());
    }

    @Override public void start(Stage stage) {
        Label title = new Label("Star Wars Episode I - Interactions");
        title.setAlignment(Pos.CENTER);
        title.setFont(Fonts.opensansRegular(24));
        VBox vBox = new VBox(10, title, arcChart);
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.CENTER);
        StackPane pane = new StackPane(vBox);
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
