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

import eu.hansolo.fx.charts.heatmap.HeatMap;
import eu.hansolo.fx.charts.heatmap.HeatMapBuilder;
import eu.hansolo.fx.charts.heatmap.OpacityDistribution;
import eu.hansolo.fx.charts.tools.ColorMapping;
import javafx.application.Application;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


public class HeatMapTest extends Application {
    private HeatMap heatMap;

    @Override public void init() {
        heatMap = HeatMapBuilder.create()
                                .prefSize(400, 400)
                                .colorMapping(ColorMapping.INFRARED_4)
                                .spotRadius(20)
                                .opacityDistribution(OpacityDistribution.CUSTOM)
                                .fadeColors(true)
                                .build();

        heatMap.setOnMouseMoved(e -> heatMap.addSpot(e.getX(), e.getY()));

    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(heatMap);

        // Setup a mouse event filter which adds spots to the heatmap as soon as the mouse will be moved across the pane
        pane.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            double x = event.getX();
            double y = event.getY();
            if (x < heatMap.getSpotRadius()) x = heatMap.getSpotRadius();
            if (x > pane.getWidth() - heatMap.getSpotRadius()) x = pane.getWidth() - heatMap.getSpotRadius();
            if (y < heatMap.getSpotRadius()) y = heatMap.getSpotRadius();
            if (y > pane.getHeight() - heatMap.getSpotRadius()) y = pane.getHeight() - heatMap.getSpotRadius();

            heatMap.addSpot(x, y);
        });
        pane.widthProperty().addListener((ov, oldWidth, newWidth) -> heatMap.setSize(newWidth.doubleValue(), pane.getHeight()));
        pane.heightProperty().addListener((ov, oldHeight, newHeight) -> heatMap.setSize(pane.getWidth(), newHeight.doubleValue()));

        Scene scene = new Scene(pane, 400, 400);

        stage.setTitle("HeatMap (move mouse over pane)");
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
