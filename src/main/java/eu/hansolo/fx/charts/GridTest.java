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

import eu.hansolo.fx.charts.tools.Helper;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 02.08.17
 * Time: 17:33
 */
public class GridTest extends Application {
    private static final Double AXIS_WIDTH = 25d;
    private              Axis   xAxis;
    private              Axis   yAxis;
    private              Grid   grid;


    @Override public void init() {
        xAxis = createBottomXAxis(0, 50, true);
        xAxis.setMinorTickMarksVisible(false);
        xAxis.setMajorTickMarkColor(Color.RED);

        yAxis = createLeftYAxis(0, 20, true);
        yAxis.setMinorTickMarksVisible(false);
        yAxis.setMediumTickMarkColor(Color.MAGENTA);

        grid  = new Grid(xAxis, yAxis);
    }

    @Override public void start(Stage stage) {
        AnchorPane pane = new AnchorPane(xAxis, yAxis, grid);

        AnchorPane.setTopAnchor(yAxis, 0d);
        AnchorPane.setBottomAnchor(yAxis, 25d);
        AnchorPane.setLeftAnchor(yAxis, 0d);

        AnchorPane.setLeftAnchor(xAxis, 25d);
        AnchorPane.setRightAnchor(xAxis, 0d);
        AnchorPane.setBottomAnchor(xAxis, 0d);

        AnchorPane.setTopAnchor(grid, 0d);
        AnchorPane.setRightAnchor(grid, 0d);
        AnchorPane.setBottomAnchor(grid, 25d);
        AnchorPane.setLeftAnchor(grid, 25d);

        Scene scene = new Scene(pane);

        stage.setTitle("GridTest");
        stage.setScene(scene);
        stage.show();

        //Helper.saveAsPng(pane, "/Users/hansolo/Desktop/grid.png");
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
