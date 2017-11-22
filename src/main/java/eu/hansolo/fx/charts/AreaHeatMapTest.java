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

import eu.hansolo.fx.charts.areaheatmap.AreaHeatMap;
import eu.hansolo.fx.charts.areaheatmap.AreaHeatMap.Quality;
import eu.hansolo.fx.charts.areaheatmap.AreaHeatMapBuilder;
import eu.hansolo.fx.charts.data.DataPoint;
import eu.hansolo.fx.charts.tools.ColorMapping;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * User: hansolo
 * Date: 22.11.17
 * Time: 19:55
 */
public class AreaHeatMapTest extends Application {
    private static final Random RND = new Random();
    private AreaHeatMap areaHeatMap;

    @Override public void init() {
        List<DataPoint> randomPoints = new ArrayList<>(29);
        //randomPoints.add(new DataPoint(0, 0, 0));
        //randomPoints.add(new DataPoint(400, 0, 0));
        //randomPoints.add(new DataPoint(400, 400, 0));
        //randomPoints.add(new DataPoint(0, 400, 0));
        for (int counter = 0 ; counter < 25 ; counter++) {
            double x = RND.nextDouble() * 400;
            double y = RND.nextDouble() * 400;
            double v = RND.nextDouble() * 100 - 50;
            randomPoints.add(new DataPoint(x, y, v));
        }

        areaHeatMap = AreaHeatMapBuilder.create()
                                        .prefSize(400, 400)
                                        .colorMapping(ColorMapping.BLUE_CYAN_GREEN_YELLOW_RED)
                                        .quality(Quality.FINE)
                                        .heatMapOpacity(0.5)
                                        .useColorMapping(true)
                                        .dataPointsVisible(true)
                                        .noOfCloserInfluentPoints(5)
                                        .dataPoints(randomPoints)
                                        .build();
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(areaHeatMap);

        Scene scene = new Scene(pane);

        stage.setTitle("Area HeatMap");
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
