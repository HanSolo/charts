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
import javafx.geometry.Orientation;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 08.08.17
 * Time: 18:25
 */
public class LogGridTest extends Application {
    private static final Double AXIS_WIDTH = 25d;

    private              Axis   xAxisLog;
    private              Axis   yAxisLog;
    private              Grid   gridLog;


    @Override public void init() {
        xAxisLog = createBottomXAxis(0, 1000, false);
        xAxisLog.setMajorTickMarkColor(Color.RED);
        xAxisLog.setType(AxisType.LOGARITHMIC);

        yAxisLog = createLeftYAxis(0, 1000, false);
        yAxisLog.setMinorTickMarkColor(Color.MAGENTA);
        yAxisLog.setType(AxisType.LOGARITHMIC);


        gridLog  = new Grid(xAxisLog, yAxisLog);
    }

    @Override public void start(Stage stage) {
        AnchorPane pane = new AnchorPane(xAxisLog, yAxisLog, gridLog);

        AnchorPane.setTopAnchor(yAxisLog, 0d);
        AnchorPane.setBottomAnchor(yAxisLog, 25d);
        AnchorPane.setLeftAnchor(yAxisLog, 0d);

        AnchorPane.setLeftAnchor(xAxisLog, 25d);
        AnchorPane.setRightAnchor(xAxisLog, 0d);
        AnchorPane.setBottomAnchor(xAxisLog, 0d);

        AnchorPane.setTopAnchor(gridLog, 0d);
        AnchorPane.setRightAnchor(gridLog, 0d);
        AnchorPane.setBottomAnchor(gridLog, 25d);
        AnchorPane.setLeftAnchor(gridLog, 25d);

        Scene scene = new Scene(pane);

        stage.setTitle("LogGridTest");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private Axis createLeftYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(Orientation.VERTICAL, Position.LEFT);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefWidth(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setTopAnchor(axis, 0d);
        AnchorPane.setBottomAnchor(axis, 25d);
        AnchorPane.setLeftAnchor(axis, 0d);

        return axis;
    }
    private Axis createRightYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(Orientation.VERTICAL, Position.RIGHT);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefWidth(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setRightAnchor(axis, 0d);
        AnchorPane.setTopAnchor(axis, 0d);
        AnchorPane.setBottomAnchor(axis, 25d);

        return axis;
    }

    private Axis createBottomXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(Orientation.HORIZONTAL, Position.BOTTOM);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefHeight(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setBottomAnchor(axis, 0d);
        AnchorPane.setLeftAnchor(axis, 25d);
        AnchorPane.setRightAnchor(axis, 25d);

        return axis;
    }
    private Axis createTopXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE) {
        Axis axis = new Axis(Orientation.HORIZONTAL, Position.TOP);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefHeight(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setTopAnchor(axis, 25d);
        AnchorPane.setLeftAnchor(axis, 25d);
        AnchorPane.setRightAnchor(axis, 25d);

        return axis;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
