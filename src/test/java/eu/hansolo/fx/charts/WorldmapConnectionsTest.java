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

import eu.hansolo.fx.charts.data.MapConnection;
import eu.hansolo.fx.charts.data.WeightedMapPoints;
import eu.hansolo.fx.charts.heatmap.OpacityDistribution;
import eu.hansolo.fx.charts.tools.ColorMapping;
import eu.hansolo.fx.charts.tools.MapPoint;
import eu.hansolo.fx.charts.world.World;
import eu.hansolo.fx.charts.world.World.Resolution;
import eu.hansolo.fx.charts.world.WorldBuilder;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * User: hansolo
 * Date: 30.11.20
 * Time: 13:21
 */
public class WorldmapConnectionsTest extends Application {
    private static final  Random RND = new Random();
    private World         worldMap;
    private MapConnection animatedConnection;

    @Override public void init() {
        worldMap = WorldBuilder.create()
                               .resolution(Resolution.HI_RES)
                               .zoomEnabled(true)
                               .hoverEnabled(false)
                               .selectionEnabled(false)
                               .backgroundColor(Color.WHITE)
                               .fillColor(Color.LIGHTGRAY)
                               .connectionWidth(1)
                               .weightedMapPoints(WeightedMapPoints.NONE)
                               .weightedMapConnections(false)
                               .arrowsVisible(false)
                               .drawImagePath(true)
                               .mapPointTextVisible(true)
                               .textColor(Color.BLACK)
                               .build();

        MapPoint calgary           = new MapPoint("Calgary", Color.RED,51.08299176,-114.0799982);
        MapPoint san_francisco     = new MapPoint("San Francisco", Color.BLUE, 37.74000775,-122.4599777);
        MapPoint new_york          = new MapPoint("New York", Color.BLUE,40.74997906,-73.98001693);
        MapPoint chicago           = new MapPoint("Chicago", Color.BLUE,41.82999066,-87.75005497);
        MapPoint denver            = new MapPoint("Denver",Color.BLUE, 39.73918805,-104.984016);

        MapPoint mexico_city       = new MapPoint("Mexico City",Color.GREEN, 19.44244244,-99.1309882);
        MapPoint buenos_aires      = new MapPoint("Buenos Aires", Color.LIGHTBLUE, -34.60250161,-58.39753137);
        MapPoint santiago_de_chile = new MapPoint("Santiago de Chile", Color.BLUE,-33.45001382,-70.66704085);
        MapPoint sao_paulo         = new MapPoint("Sao Paulo", Color.GREEN, -23.55867959,-46.62501998);

        MapPoint berlin            = new MapPoint("Berlin", Color.DARKORANGE, 52.52181866, 13.40154862);
        MapPoint paris             = new MapPoint("Paris", Color.DARKBLUE, 48.86669293,2.333335326);
        MapPoint madrid            = new MapPoint("Madrid", Color.YELLOW,40.40002626,-3.683351686);

        MapPoint johannesburg      = new MapPoint("Johannesburg", Color.BROWN,-26.17004474,28.03000972);
        MapPoint casablanca        = new MapPoint("Casablanca", Color.SADDLEBROWN,33.59997622,-7.616367433);
        MapPoint tunis             = new MapPoint("Tunis", Color.DARKGREEN,36.80277814,10.1796781);
        MapPoint alexandria        = new MapPoint("Alexandria", Color.BLACK,31.20001935,29.94999589);
        MapPoint nairobi           = new MapPoint("Nairobi", Color.LIGHTBLUE,-1.283346742,36.81665686);
        MapPoint abidjan           = new MapPoint("Abidjan", Color.IVORY,5.319996967,-4.04004826);

        MapPoint moscow            = new MapPoint("Moscow", Color.RED,55.75216412,37.61552283);
        MapPoint novosibirsk       = new MapPoint("Novosibirsk", Color.RED,55.02996014,82.96004187);
        MapPoint magadan           = new MapPoint("Magadan", Color.RED,59.57497988,150.8100089);

        MapPoint abu_dabi          = new MapPoint("Abu Dhabi", Color.GOLD, 24.46668357,54.36659338);
        MapPoint mumbai            = new MapPoint("Mumbai", Color.GOLD, 19.01699038,72.8569893);
        MapPoint hyderabad         = new MapPoint("Hyderabad", Color.GOLD,17.39998313,78.47995357);

        MapPoint beijing           = new MapPoint("Beijing", Color.DARKRED,39.92889223,116.3882857);
        MapPoint chongqing          = new MapPoint("Chongqing", Color.DARKRED,29.56497703,106.5949816);
        MapPoint hong_kong         = new MapPoint("Hong Kong", Color.DARKRED,22.3049809,114.1850093);
        MapPoint singapore         = new MapPoint("Singapore", Color.CRIMSON, 1.293033466,103.8558207);
        MapPoint tokio             = new MapPoint("Tokio",Color.RED, 35.652832,139.839478);

        MapPoint sydney            = new MapPoint("Sydney", Color.BLUE, -33.865143, 151.209900);
        MapPoint perth             = new MapPoint("Perth", Color.BLUE, -31.95501463,115.8399987);
        MapPoint christchurch      = new MapPoint("Christchurch", Color.BLUE, -43.53503131,172.6300207);


        List<MapPoint> northAmerica = List.of(calgary, san_francisco, chicago, new_york, denver);
        List<MapPoint> southAmerica = List.of(mexico_city, buenos_aires, santiago_de_chile, sao_paulo);
        List<MapPoint> europe       = List.of(madrid, paris, berlin);
        List<MapPoint> afrika       = List.of(johannesburg, casablanca, tunis, alexandria, nairobi, abidjan);
        List<MapPoint> russia       = List.of(moscow, novosibirsk, magadan);
        List<MapPoint> india        = List.of(abu_dabi, mumbai, hyderabad);
        List<MapPoint> asia         = List.of(beijing, hong_kong, singapore, tokio, chongqing);
        List<MapPoint> australia    = List.of(sydney, perth, christchurch);

        worldMap.addMapPoints(berlin, paris, san_francisco, abu_dabi, new_york, chicago, denver, sao_paulo, madrid, calgary,
                              mexico_city, buenos_aires, santiago_de_chile, johannesburg, moscow, novosibirsk, magadan,
                              mumbai, beijing, hong_kong, sydney, christchurch, tokio, singapore, casablanca, tunis, alexandria, nairobi,
                              abidjan, hyderabad, chongqing, perth);

        /*
        northAmerica.forEach(mapPoint -> {
            worldMap.addMapConnections(new MapConnection(berlin, mapPoint, RND.nextInt(130) + 10, berlin.getFill(), Color.ORANGERED, true));
        });
        asia.forEach(mapPoint -> {
            worldMap.addMapConnections(new MapConnection(berlin, mapPoint, RND.nextInt(130) + 10, berlin.getFill(), Color.ORANGERED, true));
        });
        australia.forEach(mapPoint -> {
            worldMap.addMapConnections(new MapConnection(beijing, mapPoint, RND.nextInt(130) + 10, beijing.getFill(), Color.PURPLE, true));
            worldMap.addMapConnections(new MapConnection(hong_kong, mapPoint, RND.nextInt(130) + 10, beijing.getFill(), Color.PURPLE, true));
        });
        europe.forEach(mapPoint -> {
            worldMap.addMapConnections(new MapConnection(johannesburg, mapPoint, RND.nextInt(130) + 10, johannesburg.getFill(), Color.ORANGE, true));
        });
        southAmerica.forEach(mapPoint -> {
            worldMap.addMapConnections(new MapConnection(johannesburg, mapPoint, RND.nextInt(130) + 10, johannesburg.getFill(), Color.ORANGE, true));
        });
        */

        MapConnection sanfrancisco_mumbai     = new MapConnection(san_francisco, mumbai, 90, Color.ORANGERED, Color.BLUE, true);
        MapConnection sanfrancisco_newyork    = new MapConnection(san_francisco, new_york, 100, Color.ORANGERED, Color.BLUE, true);
        MapConnection sanfrancisco_abudabi    = new MapConnection(san_francisco, abu_dabi, 60, Color.ORANGERED, Color.BLUE, true);
        MapConnection sanfrancisco_mexicocity = new MapConnection(san_francisco, mexico_city, 30, Color.ORANGERED, Color.BLUE, true);
        MapConnection sanfrancisco_santiago   = new MapConnection(san_francisco, santiago_de_chile, 70, Color.ORANGERED, Color.BLUE, true);


        animatedConnection = new MapConnection(berlin, christchurch, 1, Color.CRIMSON);
        animatedConnection.setLineWidth(5);

        //worldMap.addMapConnections(sanfrancisco_mumbai, sanfrancisco_abudabi, sanfrancisco_newyork, sanfrancisco_mexicocity, sanfrancisco_santiago);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(worldMap);

        Scene scene = new Scene(pane);

        stage.setTitle("Worldmap Connections");
        stage.setScene(scene);
        stage.show();

        Image plane = new Image(WorldmapConnectionsTest.class.getResourceAsStream("plane.png"));
        pane.setOnMousePressed(e -> worldMap.animateImageAlongConnection(plane, animatedConnection));
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
