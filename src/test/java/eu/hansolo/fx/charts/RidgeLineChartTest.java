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

import eu.hansolo.fx.charts.data.Item;
import eu.hansolo.fx.charts.data.XYChartItem;
import eu.hansolo.fx.charts.series.XYSeries;
import eu.hansolo.fx.charts.series.XYSeriesBuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RidgeLineChartTest extends Application {
    private VBox ridgeLineChartBox;

    @Override public void init() {
        double chartWidth  = 400;
        double chartHeight = 150;
        ridgeLineChartBox = new VBox(-chartHeight * 0.75);
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                                                     new Stop(0.00, Color.rgb(0, 0, 200, 0.5)),
                                                     new Stop(0.25, Color.rgb(122, 0, 183, 0.5)),
                                                     new Stop(0.50, Color.rgb(255, 0, 0, 0.5)),
                                                     new Stop(0.75, Color.rgb(255, 175, 0, 0.5)),
                                                     new Stop(1.00, Color.rgb(255, 255, 0, 0.5)));

        Map<Integer, List<XYChartItem>> items = createItems();

        for (int i = 0 ; i < items.keySet().size() ; i++) {
            XYSeries xySeries = XYSeriesBuilder.create()
                                               .items(items.get(i))
                                               .chartType(ChartType.RIDGE_LINE)
                                               .fill(gradient)
                                               .stroke(Color.BLACK)
                                               .strokeWidth(1)
                                               .symbolsVisible(false)
                                               .build();
            XYPane ridgeLineChart = new XYPane(xySeries);
            ridgeLineChart.setPrefSize(chartWidth, chartHeight);
            ridgeLineChart.setUpperBoundX(xySeries.getItems().size());
            ridgeLineChartBox.getChildren().add(ridgeLineChart);
        }
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(ridgeLineChartBox);
        pane.setPadding(new Insets(10));
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("RidgeLine Chart");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private Map<Integer, List<XYChartItem>> createItems() {
        Map<Integer, List<XYChartItem>> map = new HashMap<>();
        map.put(0, List.of(new XYChartItem(1, 0, "Jan"),
                           new XYChartItem(2, 10, "Feb"),
                           new XYChartItem(3, 40, "Mar"),
                           new XYChartItem(4, 5, "Apr"),
                           new XYChartItem(5, 0, "May"),
                           new XYChartItem(6, 0, "Jun"),
                           new XYChartItem(7, 0, "Jul"),
                           new XYChartItem(8, 0, "Aug"),
                           new XYChartItem(9, 0, "Sep"),
                           new XYChartItem(10, 0, "Oct"),
                           new XYChartItem(11, 0, "Nov"),
                           new XYChartItem(12, 0, "Dec")));

        map.put(1, List.of(new XYChartItem(1, 0, "Jan"),
                           new XYChartItem(2, 0, "Feb"),
                           new XYChartItem(3, 15, "Mar"),
                           new XYChartItem(4, 20, "Apr"),
                           new XYChartItem(5, 50, "May"),
                           new XYChartItem(6, 25, "Jun"),
                           new XYChartItem(7, 5, "Jul"),
                           new XYChartItem(8, 0, "Aug"),
                           new XYChartItem(9, 0, "Sep"),
                           new XYChartItem(10, 0, "Oct"),
                           new XYChartItem(11, 0, "Nov"),
                           new XYChartItem(12, 0, "Dec")));

        map.put(2, List.of(new XYChartItem(1, 0, "Jan"),
                           new XYChartItem(2, 0, "Feb"),
                           new XYChartItem(3, 0, "Mar"),
                           new XYChartItem(4, 5, "Apr"),
                           new XYChartItem(5, 20, "May"),
                           new XYChartItem(6, 35, "Jun"),
                           new XYChartItem(7, 5, "Jul"),
                           new XYChartItem(8, 0, "Aug"),
                           new XYChartItem(9, 0, "Sep"),
                           new XYChartItem(10, 0, "Oct"),
                           new XYChartItem(11, 0, "Nov"),
                           new XYChartItem(12, 0, "Dec")));

        map.put(3, List.of(new XYChartItem(1, 0, "Jan"),
                           new XYChartItem(2, 0, "Feb"),
                           new XYChartItem(3, 0, "Mar"),
                           new XYChartItem(4, 0, "Apr"),
                           new XYChartItem(5, 10, "May"),
                           new XYChartItem(6, 55, "Jun"),
                           new XYChartItem(7, 15, "Jul"),
                           new XYChartItem(8, 5, "Aug"),
                           new XYChartItem(9, 0, "Sep"),
                           new XYChartItem(10, 0, "Oct"),
                           new XYChartItem(11, 0, "Nov"),
                           new XYChartItem(12, 0, "Dec")));

        map.put(4, List.of(new XYChartItem(1, 0, "Jan"),
                           new XYChartItem(2, 0, "Feb"),
                           new XYChartItem(3, 0, "Mar"),
                           new XYChartItem(4, 0, "Apr"),
                           new XYChartItem(5, 0, "May"),
                           new XYChartItem(6, 10, "Jun"),
                           new XYChartItem(7, 15, "Jul"),
                           new XYChartItem(8, 45, "Aug"),
                           new XYChartItem(9, 5, "Sep"),
                           new XYChartItem(10, 0, "Oct"),
                           new XYChartItem(11, 0, "Nov"),
                           new XYChartItem(12, 0, "Dec")));

        map.put(5, List.of(new XYChartItem(1, 0, "Jan"),
                           new XYChartItem(2, 0, "Feb"),
                           new XYChartItem(3, 0, "Mar"),
                           new XYChartItem(4, 0, "Apr"),
                           new XYChartItem(5, 0, "May"),
                           new XYChartItem(6, 0, "Jun"),
                           new XYChartItem(7, 10, "Jul"),
                           new XYChartItem(8, 40, "Aug"),
                           new XYChartItem(9, 15, "Sep"),
                           new XYChartItem(10, 5, "Oct"),
                           new XYChartItem(11, 0, "Nov"),
                           new XYChartItem(12, 0, "Dec")));

        map.put(6, List.of(new XYChartItem(1, 0, "Jan"),
                           new XYChartItem(2, 0, "Feb"),
                           new XYChartItem(3, 0, "Mar"),
                           new XYChartItem(4, 0, "Apr"),
                           new XYChartItem(5, 0, "May"),
                           new XYChartItem(6, 5, "Jun"),
                           new XYChartItem(7, 15, "Jul"),
                           new XYChartItem(8, 50, "Aug"),
                           new XYChartItem(9, 5, "Sep"),
                           new XYChartItem(10, 0, "Oct"),
                           new XYChartItem(11, 0, "Nov"),
                           new XYChartItem(12, 0, "Dec")));

        map.put(7, List.of(new XYChartItem(1, 0, "Jan"),
                           new XYChartItem(2, 0, "Feb"),
                           new XYChartItem(3, 0, "Mar"),
                           new XYChartItem(4, 0, "Apr"),
                           new XYChartItem(5, 10, "May"),
                           new XYChartItem(6, 25, "Jun"),
                           new XYChartItem(7, 35, "Jul"),
                           new XYChartItem(8, 20, "Aug"),
                           new XYChartItem(9, 0, "Sep"),
                           new XYChartItem(10, 0, "Oct"),
                           new XYChartItem(11, 0, "Nov"),
                           new XYChartItem(12, 0, "Dec")));

        map.put(8, List.of(new XYChartItem(1, 0, "Jan"),
                           new XYChartItem(2, 0, "Feb"),
                           new XYChartItem(3, 0, "Mar"),
                           new XYChartItem(4, 5, "Apr"),
                           new XYChartItem(5, 20, "May"),
                           new XYChartItem(6, 45, "Jun"),
                           new XYChartItem(7, 15, "Jul"),
                           new XYChartItem(8, 0, "Aug"),
                           new XYChartItem(9, 0, "Sep"),
                           new XYChartItem(10, 0, "Oct"),
                           new XYChartItem(11, 0, "Nov"),
                           new XYChartItem(12, 0, "Dec")));

        map.put(9, List.of(new XYChartItem(1, 0, "Jan"),
                           new XYChartItem(2, 0, "Feb"),
                           new XYChartItem(3, 10, "Mar"),
                           new XYChartItem(4, 35, "Apr"),
                           new XYChartItem(5, 50, "May"),
                           new XYChartItem(6, 25, "Jun"),
                           new XYChartItem(7, 5, "Jul"),
                           new XYChartItem(8, 0, "Aug"),
                           new XYChartItem(9, 0, "Sep"),
                           new XYChartItem(10, 0, "Oct"),
                           new XYChartItem(11, 0, "Nov"),
                           new XYChartItem(12, 0, "Dec")));
        return map;
    }

    private Axis createLeftYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        Axis axis = new Axis(Orientation.VERTICAL, Position.LEFT);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefWidth(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setTopAnchor(axis, 0d);
        AnchorPane.setBottomAnchor(axis, AXIS_WIDTH);
        AnchorPane.setLeftAnchor(axis, 0d);

        return axis;
    }
    private Axis createCenterYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        Axis axis = new Axis(Orientation.VERTICAL, Position.CENTER);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefWidth(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setTopAnchor(axis, 0d);
        AnchorPane.setBottomAnchor(axis, AXIS_WIDTH);
        AnchorPane.setLeftAnchor(axis, axis.getZeroPosition());

        return axis;
    }
    private Axis createRightYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        Axis axis = new Axis(Orientation.VERTICAL, Position.RIGHT);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefWidth(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setRightAnchor(axis, 0d);
        AnchorPane.setTopAnchor(axis, 0d);
        AnchorPane.setBottomAnchor(axis, AXIS_WIDTH);

        return axis;
    }

    private Axis createBottomXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        Axis axis = new Axis(Orientation.HORIZONTAL, Position.BOTTOM);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefHeight(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setBottomAnchor(axis, 0d);
        AnchorPane.setLeftAnchor(axis, AXIS_WIDTH);
        AnchorPane.setRightAnchor(axis, AXIS_WIDTH);

        return axis;
    }
    private Axis createCenterXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        Axis axis = new Axis(Orientation.HORIZONTAL, Position.CENTER);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefHeight(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setBottomAnchor(axis, axis.getZeroPosition());
        AnchorPane.setLeftAnchor(axis, AXIS_WIDTH);
        AnchorPane.setRightAnchor(axis, AXIS_WIDTH);

        return axis;
    }
    private Axis createTopXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        Axis axis = new Axis(Orientation.HORIZONTAL, Position.TOP);
        axis.setMinValue(MIN);
        axis.setMaxValue(MAX);
        axis.setPrefHeight(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);

        AnchorPane.setTopAnchor(axis, AXIS_WIDTH);
        AnchorPane.setLeftAnchor(axis, AXIS_WIDTH);
        AnchorPane.setRightAnchor(axis, AXIS_WIDTH);

        return axis;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
