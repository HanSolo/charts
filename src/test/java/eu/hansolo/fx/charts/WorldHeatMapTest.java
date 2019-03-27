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

import eu.hansolo.fx.charts.heatmap.OpacityDistribution;
import eu.hansolo.fx.charts.tools.ColorMapping;
import eu.hansolo.fx.charts.tools.Point;
import eu.hansolo.fx.charts.world.World;
import eu.hansolo.fx.charts.world.World.Resolution;
import eu.hansolo.fx.charts.world.WorldBuilder;
import javafx.application.Application;
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

        worldMap = WorldBuilder.create()
                               .resolution(Resolution.HI_RES)
                               .zoomEnabled(false)
                               .hoverEnabled(false)
                               .selectionEnabled(false)
                               .colorMapping(ColorMapping.BLUE_YELLOW_RED)
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