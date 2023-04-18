/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2023 Gerrit Grunwald.
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

import eu.hansolo.fx.charts.world.World;
import eu.hansolo.fx.charts.world.World.Resolution;
import eu.hansolo.fx.charts.world.WorldBuilder;
import eu.hansolo.toolboxfx.geom.Point;
import eu.hansolo.fx.heatmap.ColorMapping;
import eu.hansolo.fx.heatmap.Mapping;
import eu.hansolo.fx.heatmap.OpacityDistribution;
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/**
 * User: hansolo
 * Date: 22.11.17
 * Time: 20:25
 */
public class WorldHeatMapTest extends Application {
    private StackPane   pane;
    private World       worldMap;
    private List<Point> cities;

    @Override public void init() {
        try { cities = readCitiesFromFile(); } catch (IOException e) { cities = new ArrayList<>(); } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Mapping customMapping = new Mapping() {
            private final Stop[]         stops = { new Stop(0.0, Color.LIME), new Stop(0.8, Color.YELLOW), new Stop(1.0, Color.CYAN) };
            private final LinearGradient gradient = new LinearGradient(0, 0, 100, 0, false, CycleMethod.NO_CYCLE, stops);

            @Override public Stop[] getStops() { return stops; }
            @Override public LinearGradient getGradient() { return gradient; }
        };

        worldMap = WorldBuilder.create()
                               .resolution(Resolution.HI_RES)
                               //.backgroundColor(Color.BLACK)
                               .fillColor(Color.BLACK)
                               .zoomEnabled(true)
                               .hoverEnabled(false)
                               .selectionEnabled(true)
                               .selectedColor(Color.LIGHTBLUE)
                               //.mousePressHandler(e -> {
                               //    //worldMap.setSelectedColor(worldMap.getSelectedCountry().getFill());
                               //    System.out.println(worldMap.getSelectedCountry());
                               //})
                               //.colorMapping(ColorMapping.BLUE_CYAN_GREEN_YELLOW_RED)
                               .colorMapping(ColorMapping.BLUE_GREEN_RED)
                               //.colorMapping(ChartsColorMapping.BLACK_WHITE)
                               //.colorMapping(customMapping)
                               .fadeColors(true)
                               .eventRadius(3)
                               .heatMapOpacity(0.75)
                               .opacityDistribution(OpacityDistribution.LINEAR)
                               .build();

        pane = new StackPane(worldMap);

        /* Add heatmap events by clicking on the map
        pane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            double x = event.getX();
            double y = event.getY();
            HeatMap heatMap = worldMap.getHeatMap();
            if (x < heatMap.getEventRadius()) x = heatMap.getEventRadius();
            if (x > pane.getWidth() - heatMap.getEventRadius()) x = pane.getWidth() - heatMap.getEventRadius();
            if (y < worldMap.getHeatMap().getEventRadius()) y = worldMap.getHeatMap().getEventRadius();
            if (y > pane.getHeight() - heatMap.getEventRadius()) y = pane.getHeight() - heatMap.getEventRadius();

            worldMap.getHeatMap().addEvent(x, y);
        });
        */
    }

    @Override public void start(Stage stage) {
        Scene scene = new Scene(pane);

        stage.setTitle("World Cities");
        stage.setScene(scene);
        stage.show();

        worldMap.getHeatMap().addSpots(cities);
    }

    private List<Point> readCitiesFromFile() throws IOException, URISyntaxException {
        List<Point>  cities     = new ArrayList<>(8092);
        URI citiesFile = (WorldHeatMapTest.class.getResource("cities.txt")).toURI();
        Stream<String> lines      = Files.lines(Paths.get(citiesFile));
        lines.forEach(line -> {
            String city[] = line.split(",");
            double[] xy = World.latLonToXY(Double.parseDouble(city[1]), Double.parseDouble(city[2]));
            cities.add(new Point(xy[0], xy[1]));
        });
        lines.close();
        return cities;
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}