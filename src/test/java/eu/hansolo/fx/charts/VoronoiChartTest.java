/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
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

import eu.hansolo.fx.charts.voronoi.VPoint;
import eu.hansolo.fx.charts.voronoi.VoronoiChart;
import eu.hansolo.fx.charts.voronoi.VoronoiChart.Type;
import eu.hansolo.fx.charts.voronoi.VoronoiChartBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class VoronoiChartTest extends Application {
    private static final Random       RND    = new Random();
    private static final double       WIDTH  = 600;
    private static final double       HEIGHT = 600;
    private              VoronoiChart voronoiChart;


    @Override public void init() {
        List<VPoint> vPoints = new ArrayList<>();
        for (int i = 0 ; i < 25 ; i++) {
            vPoints.add(new VPoint(RND.nextDouble() * WIDTH, RND.nextDouble() * HEIGHT));
        }

        voronoiChart = VoronoiChartBuilder.create()
                                          .prefSize(WIDTH, HEIGHT)
                                          .type(Type.VORONOI)                 // Type of diagram (VORONOI, DELAUNY)
                                          .borderColor(Color.BLACK)           // Color of line between regions
                                          .multiColor(true)                  // Randomly created fill colors for regions
                                          .pointsVisible(true)                // Points visible
                                          .pointColor(Color.BLACK)            // Color of points
                                          .fillRegions(true)                    // Fill regions, otherwise only the borders will be visible
                                          .interactive(true)                  // When true new points can be added by clicking in the diagram
                                          .voronoiColor(Color.ORANGERED)      // Fill color for voronoi regions if multicolor == false
                                          .delaunayColor(Color.YELLOWGREEN)   // Fill color for delauny regions if multicolor == false
                                          .points(vPoints)
                                          .build();

        registerListener();
    }

    private void registerListener() {

    }

    @Override public void start(Stage stage) {
        StackPane pane  = new StackPane(voronoiChart);
        Scene     scene = new Scene(pane);

        stage.setTitle("VoronoiChart");
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
