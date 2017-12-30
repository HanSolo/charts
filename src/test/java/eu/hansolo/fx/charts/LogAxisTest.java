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

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 08.08.17
 * Time: 14:16
 */
public class LogAxisTest extends Application {
    private Axis xAxisBottom;
    private Axis yAxisLeft;
    private Axis logAxisX;


    @Override public void init() {
        xAxisBottom = new Axis(0, 1000, Orientation.HORIZONTAL, AxisType.LOGARITHMIC, Position.BOTTOM);
        xAxisBottom.setPrefHeight(20);
        AnchorPane.setLeftAnchor(xAxisBottom, 20d);
        AnchorPane.setRightAnchor(xAxisBottom, 20d);
        AnchorPane.setBottomAnchor(xAxisBottom, 0d);

        yAxisLeft = new Axis(0, 1000, Orientation.VERTICAL, AxisType.LOGARITHMIC, Position.LEFT);
        yAxisLeft.setPrefWidth(20);
        AnchorPane.setLeftAnchor(yAxisLeft, 0d);
        AnchorPane.setTopAnchor(yAxisLeft, 20d);
        AnchorPane.setBottomAnchor(yAxisLeft, 20d);
    }

    @Override public void start(Stage stage) {
        AnchorPane pane = new AnchorPane(xAxisBottom, yAxisLeft);
        pane.setPadding(new Insets(10));
        pane.setPrefSize(400, 400);

        Scene scene = new Scene(pane);

        stage.setTitle("Title");
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
