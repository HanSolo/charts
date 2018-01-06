/*
 * Copyright (c) 2018 by Gerrit Grunwald
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

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 05.01.18
 * Time: 21:20
 */
public class LegendTest extends Application {
    private Legend legend;

    @Override public void init() {
        LegendItem item1 = new LegendItem(Symbol.CIRCLE, "Item 1", Color.RED, Color.BLACK);
        LegendItem item2 = new LegendItem(Symbol.SQUARE, "Item 2", Color.GREEN, Color.BLACK);
        LegendItem item3 = new LegendItem(Symbol.TRIANGLE, "Item 3", Color.BLUE, Color.BLACK);

        legend = new Legend(item1, item2, item3);
        legend.setOrientation(Orientation.VERTICAL);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(legend);

        Scene scene = new Scene(pane);

        stage.setTitle("Legend");
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
