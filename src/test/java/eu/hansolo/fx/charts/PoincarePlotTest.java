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

import eu.hansolo.fx.charts.data.XYChartItem;
import eu.hansolo.fx.charts.data.XYChartItemBuilder;
import eu.hansolo.fx.charts.series.XYSeries;
import eu.hansolo.fx.charts.series.XYSeriesBuilder;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.toolboxfx.GradientLookup;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class PoincarePlotTest extends Application {
    private static final Double                AXIS_WIDTH = 25d;
    private              XYSeries<XYChartItem> xySeries;

    private XYChart<XYChartItem> chart;
    private Axis                 xAxisBottom;
    private Axis                 yAxisLeft;

    private record SGVDto(String _id, int sgv, long date, String dateString, int trend, String direction, String device, String type, int utcOffset, String sysTime) {};
    private record SGV(long date, double sgv) {}


    @Override public void init() {
        List<SGV> data = getData();
        GradientLookup gradientLookup = new GradientLookup(new Stop(0.0, Color.RED), new Stop(0.1375, Color.RED), new Stop(0.175, Color.ORANGE), new Stop(0.176, Color.LIME), new Stop(0.35, Color.LIME), new Stop(0.45, Color.YELLOW), new Stop(0.625, Color.ORANGE), new Stop(1.0, Color.RED));
        List<XYChartItem> xyItems = new ArrayList<>(20);
        for (int i = 0 ; i < data.size() ; i++) {
            final SGV   sgv   = data.get(i);
            final Color color = gradientLookup.getColorAt(sgv.sgv / 400);
            xyItems.add(XYChartItemBuilder.create().x(sgv.date).y(sgv.sgv).symbol(Symbol.CIRCLE).fill(color).build());
        }

        xySeries = XYSeriesBuilder.create()
                                  .items(xyItems)
                                  .chartType(ChartType.POINCARE)
                                  .fill(Color.TRANSPARENT)
                                  .stroke(Color.MAGENTA)
                                  .symbolFill(Color.RED)
                                  .symbolStroke(Color.TRANSPARENT)
                                  .symbolsVisible(true)
                                  .build();


        // Poincare Plot
        yAxisLeft   = Helper.createAxis(0, 400, true, AXIS_WIDTH, Orientation.VERTICAL, Position.LEFT);
        xAxisBottom = Helper.createAxis(0, 400, true, AXIS_WIDTH, Orientation.HORIZONTAL, Position.BOTTOM);

        yAxisLeft.setForegroundColor(Color.WHITE);
        xAxisBottom.setForegroundColor(Color.WHITE);

        chart       = new XYChart<>(new XYPane(xySeries), yAxisLeft, xAxisBottom);
        chart.getXYPane().setCrossHairVisible(true);
        chart.setTitle("Blood Glucose");
        chart.setSubTitle("April 17th 2023");
        chart.setTitleColor(Color.WHITE);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(chart);
        pane.setPadding(new Insets(10));
        pane.setPrefSize(600, 600);
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(7, 36, 56), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("Poincare Plot");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }


    public static final List<SGV> getData() {
        List<SGV> sgvData = new ArrayList<>();

        String   filename = PoincarePlotTest.class.getResource("data4.csv").toExternalForm().replaceAll("file:", "");
        String   data     = Helper.readTextFile(filename);
        String[] lines    = data.split(System.getProperty("line.separator"));

        for (String line : lines) {
            String[] parts = line.split(",");
            SGV sgv = new SGV(Long.parseLong(parts[1]), Double.parseDouble(parts[0]));
            sgvData.add(sgv);
        }
        return sgvData;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
