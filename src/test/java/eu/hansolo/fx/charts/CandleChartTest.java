/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2022 Gerrit Grunwald.
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


import eu.hansolo.fx.charts.data.CandleChartItem;
import eu.hansolo.fx.charts.data.CandleChartItemBuilder;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.TYChartItem;
import eu.hansolo.fx.charts.data.XYChartItem;
import eu.hansolo.fx.charts.event.ChartEvt;
import eu.hansolo.fx.charts.series.XYSeries;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class CandleChartTest extends Application {
    private static final double                     INSET = 20;
    private              CandleChart                candleChart;
    private              List<CandleChartItem>      items;
    private              Axis                       xAxisBottom;
    private              Axis                       yAxisLeft;
    private              Grid                       grid;
    private              Map<ZonedDateTime, Double> ma5;
    private              Map<ZonedDateTime, Double> ma10;
    private              Map<ZonedDateTime, Double> ma20;
    private              Map<ZonedDateTime, Double> ma60;
    private              List<TYChartItem>          ma5Items;
    private              List<TYChartItem>          ma10Items;
    private              List<TYChartItem>          ma20Items;
    private              List<TYChartItem>          ma60Items;
    private              XYSeries<TYChartItem>      ma5Series;
    private              XYSeries<TYChartItem>      ma10Series;
    private              XYSeries<TYChartItem>      ma20Series;
    private              XYSeries<TYChartItem>      ma60Series;
    private              XYChart<TYChartItem>       ma5Chart;
    private              XYChart<TYChartItem>       ma10Chart;
    private              XYChart<TYChartItem>       ma20Chart;
    private              XYChart<TYChartItem>       ma60Chart;


    @Override public void init() {
        items = new ArrayList<>();
        ma5   = new HashMap<>();
        ma10  = new HashMap<>();
        ma20  = new HashMap<>();
        ma60  = new HashMap<>();

        // Prepare data for CandleChart
        prepareData();

        CandleChartItem minItem      = items.stream().min(Comparator.comparing(CandleChartItem::getLow)).get();
        CandleChartItem maxItem      = items.stream().max(Comparator.comparing(CandleChartItem::getHigh)).get();
        double          minValue     = minItem.getLow();
        double          maxValue     = maxItem.getHigh();

        Instant         minTimestamp = minItem.getTimestamp();
        Instant         maxTimestamp = maxItem.getTimestamp();
        long            days         = Duration.between(maxTimestamp, minTimestamp).toDays();

        Instant       startInstant = items.stream().min(Comparator.comparing(CandleChartItem::getTimestamp)).get().getTimestamp();
        Instant       endInstant   = items.stream().max(Comparator.comparing(CandleChartItem::getTimestamp)).get().getTimestamp();
        LocalDateTime start        = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault());
        LocalDateTime end          = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault());


        xAxisBottom = Helper.createBottomTimeAxis(start, end, "MM:yyyy", true, INSET, INSET, 0d);
        xAxisBottom.setAxisColor(Color.WHITE);
        xAxisBottom.setTickMarkColor(Color.WHITE);
        xAxisBottom.setTickLabelColor(Color.WHITE);

        yAxisLeft   = Helper.createLeftAxis(minValue, maxValue, true, INSET);
        yAxisLeft.setDecimals(1);
        yAxisLeft.setAxisColor(Color.WHITE);
        yAxisLeft.setTickMarkColor(Color.WHITE);
        yAxisLeft.setTickLabelColor(Color.WHITE);

        grid = new Grid(xAxisBottom, yAxisLeft);
        AnchorPane.setTopAnchor(grid, 0d);
        AnchorPane.setRightAnchor(grid, 0d);
        AnchorPane.setBottomAnchor(grid, INSET);
        AnchorPane.setLeftAnchor(grid, INSET);

        for (ChartItem item : items) {
            item.addChartEvtObserver(ChartEvt.ANY, e -> {
                ChartItem chartItem = (ChartItem) e.getSource();
                System.out.println(chartItem.getName() + ": " + chartItem.getValue());
            });
        }

        candleChart = CandleChartBuilder.create()
                                        .items(items)
                                        .backgroundColor(Color.TRANSPARENT)
                                        .endLinesVisible(false)
                                        //.strokeColor(MaterialDesignColors.BLUE_700.get())
                                        .bullishColor(Color.rgb(2, 245, 245))
                                        .bearishColor(Color.rgb(246, 22, 31))
                                        .useMinNumberOfItems(false)
                                        .useItemColorForStroke(true)
                                        .popupTimeout(5000)
                                        .yAxis(yAxisLeft)
                                        .decimals(2)
                                        .build();


        AnchorPane.setTopAnchor(candleChart, 0d);
        AnchorPane.setRightAnchor(candleChart, 0d);
        AnchorPane.setBottomAnchor(candleChart, INSET);
        AnchorPane.setLeftAnchor(candleChart, INSET);


        // MA5
        ma5Items = new ArrayList<>();
        ma5.entrySet().forEach(entry -> ma5Items.add(new TYChartItem(entry.getKey().toLocalDateTime(), entry.getValue())));

        Collections.sort(ma5Items, Comparator.comparing(TYChartItem::getT));
        ma5Series = new XYSeries(ma5Items, ChartType.SMOOTH_LINE, Color.YELLOWGREEN, Color.YELLOWGREEN);
        ma5Series.setSymbolsVisible(false);

        ma5Chart = new XYChart<>(new XYPane(ma5Series), yAxisLeft, xAxisBottom);
        ma5Chart.setMouseTransparent(true);

        AnchorPane.setTopAnchor(ma5Chart, 0d);
        AnchorPane.setRightAnchor(ma5Chart, 0d);
        AnchorPane.setBottomAnchor(ma5Chart, INSET);
        AnchorPane.setLeftAnchor(ma5Chart, INSET);

        // MA10
        ma10Items = new ArrayList<>();
        ma10.entrySet().forEach(entry -> ma10Items.add(new TYChartItem(entry.getKey().toLocalDateTime(), entry.getValue())));
        Collections.sort(ma10Items, Comparator.comparing(TYChartItem::getT));
        ma10Series = new XYSeries(ma10Items, ChartType.SMOOTH_LINE, Color.MAGENTA, Color.MAGENTA);
        ma10Series.setSymbolsVisible(false);

        ma10Chart = new XYChart<>(new XYPane(ma10Series), yAxisLeft, xAxisBottom);
        ma10Chart.setMouseTransparent(true);

        AnchorPane.setTopAnchor(ma10Chart, 0d);
        AnchorPane.setRightAnchor(ma10Chart, 0d);
        AnchorPane.setBottomAnchor(ma10Chart, INSET);
        AnchorPane.setLeftAnchor(ma10Chart, INSET);

        // MA20
        ma20Items = new ArrayList<>();
        ma20.entrySet().forEach(entry -> ma20Items.add(new TYChartItem(entry.getKey().toLocalDateTime(), entry.getValue())));
        Collections.sort(ma20Items, Comparator.comparing(TYChartItem::getT));
        ma20Series = new XYSeries(ma20Items, ChartType.SMOOTH_LINE, Color.LIME, Color.LIME);
        ma20Series.setSymbolsVisible(false);

        ma20Chart = new XYChart<>(new XYPane(ma20Series), yAxisLeft, xAxisBottom);
        ma20Chart.setMouseTransparent(true);

        AnchorPane.setTopAnchor(ma20Chart, 0d);
        AnchorPane.setRightAnchor(ma20Chart, 0d);
        AnchorPane.setBottomAnchor(ma20Chart, INSET);
        AnchorPane.setLeftAnchor(ma20Chart, INSET);

        // MA60
        ma60Items = new ArrayList<>();
        ma60.entrySet().forEach(entry -> ma60Items.add(new TYChartItem(entry.getKey().toLocalDateTime(), entry.getValue())));
        Collections.sort(ma60Items, Comparator.comparing(TYChartItem::getT));
        ma60Series = new XYSeries(ma60Items, ChartType.SMOOTH_LINE, Color.LIGHTBLUE, Color.LIGHTBLUE);
        ma60Series.setSymbolsVisible(false);

        ma60Chart = new XYChart<>(new XYPane(ma60Series), yAxisLeft, xAxisBottom);
        ma60Chart.setMouseTransparent(true);

        AnchorPane.setTopAnchor(ma60Chart, 0d);
        AnchorPane.setRightAnchor(ma60Chart, 0d);
        AnchorPane.setBottomAnchor(ma60Chart, INSET);
        AnchorPane.setLeftAnchor(ma60Chart, INSET);
    }

    @Override public void start(Stage stage) {
        AnchorPane pane = new AnchorPane();
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(48, 48, 48), CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setPadding(new Insets(10));
        pane.getChildren().addAll(grid, candleChart, ma5Chart, ma10Chart, ma20Chart, ma60Chart, xAxisBottom, yAxisLeft);

        Scene scene = new Scene(pane);

        stage.setTitle("Candle Chart");
        stage.setScene(scene);
        stage.show();

        //candleChart.resetYAxis();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private CandleChartItem createChartItem(final String DATE, final double LOW, final double OPEN, final double CLOSE, final double HIGH, final Double ma5, final Double ma10, final Double ma20, final Double ma60) {
        String[] dateParts = DATE.split("-");
        final int           MONTH           = Integer.parseInt(dateParts[1]);
        final int           DAY             = Integer.parseInt(dateParts[2]);
        final int           YEAR            = Integer.parseInt(dateParts[0]);
        final ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.of(YEAR, MONTH, DAY, 0, 00, 00, 00, ZoneId.systemDefault());

        if (null != ma5)  { this.ma5.put(ZONED_DATE_TIME,  ma5);  }
        if (null != ma10) { this.ma10.put(ZONED_DATE_TIME, ma10); }
        if (null != ma20) { this.ma20.put(ZONED_DATE_TIME, ma20); }
        if (null != ma60) { this.ma60.put(ZONED_DATE_TIME, ma60); }

        return CandleChartItemBuilder.create()
                                     .name("Data")
                                     .timestamp(ZONED_DATE_TIME)
                                     .low(LOW)
                                     .open(OPEN)
                                     .close(CLOSE)
                                     .high(HIGH)
                                     .build();
    }

    private void prepareData() {
        items.add(createChartItem("2022-08-10",2.200,2.245,2.209,2.266,2.224,2.239,2.274,2.202));
        items.add(createChartItem("2022-08-09",2.219,2.228,2.253,2.271,2.221,2.248,2.278,2.196));
        items.add(createChartItem("2022-08-08",2.193,2.208,2.230,2.231,2.224,2.25,2.279,2.188));
        items.add(createChartItem("2022-08-05",2.177,2.210,2.222,2.234,2.242,2.252,2.283,2.181));
        items.add(createChartItem("2022-08-04",2.170,2.204,2.208,2.232,2.244,2.258,2.293,2.173));
        items.add(createChartItem("2022-08-03",2.181,2.269,2.193,2.307,2.254,2.265,2.306,2.166));
        items.add(createChartItem("2022-08-02",2.243,2.280,2.267,2.306,2.274,2.278,2.318,2.157));
        items.add(createChartItem("2022-08-01",2.213,2.228,2.318,2.319,2.276,2.283,2.326,2.147));
        items.add(createChartItem("2022-07-29",2.226,2.266,2.235,2.275,2.262,2.287,2.331,2.136));
        items.add(createChartItem("2022-07-28",2.252,2.307,2.256,2.318,2.271,2.298,2.338,2.126));
        items.add(createChartItem("2022-07-27",2.244,2.270,2.293,2.300,2.275,2.309,2.343,2.117));
        items.add(createChartItem("2022-07-26",2.243,2.248,2.276,2.305,2.281,2.309,2.344,2.105));
        items.add(createChartItem("2022-07-25",2.241,2.260,2.251,2.289,2.291,2.307,2.353,2.094));
        items.add(createChartItem("2022-07-22",2.250,2.297,2.278,2.313,2.311,2.314,2.361,2.081));
        items.add(createChartItem("2022-07-21",2.271,2.312,2.278,2.332,2.325,2.328,2.366,2.068));
        items.add(createChartItem("2022-07-20",2.313,2.331,2.324,2.358,2.343,2.348,2.371,2.057));
        items.add(createChartItem("2022-07-19",2.311,2.350,2.322,2.371,2.337,2.358,2.368,2.045));
        items.add(createChartItem("2022-07-18",2.289,2.346,2.354,2.378,2.324,2.369,2.366,2.035));
        items.add(createChartItem("2022-07-15",2.342,2.368,2.345,2.418,2.316,2.376,2.363,2.025));
        items.add(createChartItem("2022-07-14",2.267,2.277,2.368,2.383,2.331,2.378,2.359,2.015));
        items.add(createChartItem("2022-07-13",2.205,2.254,2.298,2.319,2.354,2.377,2.35,2.004));
        items.add(createChartItem("2022-07-12",2.234,2.307,2.254,2.331,2.378,2.38,2.343,1.995));
        items.add(createChartItem("2022-07-11",2.285,2.400,2.315,2.400,2.414,2.398,2.34,1.987));
        items.add(createChartItem("2022-07-08",2.412,2.481,2.420,2.501,2.436,2.408,2.334,1.978));
        items.add(createChartItem("2022-07-07",2.402,2.418,2.481,2.486,2.425,2.405,2.32,1.967));
        items.add(createChartItem("2022-07-06",2.384,2.423,2.418,2.453,2.401,2.393,2.298,1.957));
        items.add(createChartItem("2022-07-05",2.387,2.446,2.434,2.476,2.381,2.379,2.283,1.949));
        items.add(createChartItem("2022-07-04",2.340,2.353,2.425,2.429,2.383,2.362,2.265,1.94));
        items.add(createChartItem("2022-07-01",2.327,2.341,2.369,2.396,2.38,2.35,2.248,1.932));
        items.add(createChartItem("2022-06-30",2.318,2.320,2.357,2.378,2.384,2.34,2.229,1.925));
        items.add(createChartItem("2022-06-29",2.320,2.423,2.322,2.425,2.385,2.322,2.207,1.92));
        items.add(createChartItem("2022-06-28",2.382,2.407,2.441,2.445,2.376,2.307,2.186,1.913));
        items.add(createChartItem("2022-06-27",2.388,2.401,2.411,2.446,2.342,2.281,2.158,1.904));
        items.add(createChartItem("2022-06-24",2.336,2.372,2.391,2.398,2.32,2.259,2.13,1.896));
        items.add(createChartItem("2022-06-23",2.260,2.281,2.361,2.362,2.296,2.234,2.103,1.89));
        items.add(createChartItem("2022-06-22",2.268,2.284,2.274,2.320,2.259,2.204,2.077,1.884));
        items.add(createChartItem("2022-06-21",2.237,2.302,2.273,2.305,2.238,2.187,2.055,1.88));
        items.add(createChartItem("2022-06-20",2.280,2.293,2.303,2.343,2.22,2.167,2.037,1.876));
        items.add(createChartItem("2022-06-17",2.145,2.153,2.271,2.278,2.198,2.146,2.017,1.872));
        items.add(createChartItem("2022-06-16",2.150,2.161,2.176,2.202,2.173,2.117,1.998,1.868));
        items.add(createChartItem("2022-06-15",2.147,2.188,2.167,2.223,2.149,2.093,1.982,1.865));
        items.add(createChartItem("2022-06-14",2.106,2.158,2.182,2.194,2.136,2.066,1.967,1.86));
        items.add(createChartItem("2022-06-13",2.120,2.129,2.194,2.214,2.115,2.036,1.947,1.855));
        items.add(createChartItem("2022-06-10",2.043,2.054,2.144,2.146,2.094,2.001,1.926,1.851));
        items.add(createChartItem("2022-06-09",2.043,2.094,2.056,2.105,2.061,1.971,1.907,1.848));
        items.add(createChartItem("2022-06-08",2.042,2.084,2.106,2.120,2.037,1.949,1.893,1.845));
        items.add(createChartItem("2022-06-07",2.050,2.091,2.074,2.104,1.995,1.922,1.872,1.842));
        items.add(createChartItem("2022-06-06",1.981,1.981,2.091,2.106,1.957,1.907,1.849,1.84));
        items.add(createChartItem("2022-06-02",1.908,1.923,1.978,1.991,1.908,1.888,1.827,1.839));
        items.add(createChartItem("2022-06-01",1.875,1.896,1.934,1.945,1.88,1.879,1.812,1.841));
        items.add(createChartItem("2022-05-31",1.852,1.887,1.897,1.905,1.861,1.872,1.8,1.845));
        items.add(createChartItem("2022-05-30",1.847,1.860,1.884,1.885,1.85,1.868,1.785,1.85));
        items.add(createChartItem("2022-05-27",1.832,1.855,1.845,1.899,1.857,1.857,1.772,1.856));
        items.add(createChartItem("2022-05-26",1.802,1.842,1.840,1.866,1.869,1.851,1.753,1.862));
        items.add(createChartItem("2022-05-25",1.804,1.834,1.841,1.854,1.878,1.844,1.736,1.867));
        items.add(createChartItem("2022-05-24",1.835,1.910,1.839,1.910,1.882,1.837,1.724,1.873));
        items.add(createChartItem("2022-05-23",1.883,1.905,1.918,1.927,1.885,1.821,1.714,1.878));
        items.add(createChartItem("2022-05-20",1.870,1.901,1.905,1.926,1.858,1.792,1.702,1.881));
        items.add(createChartItem("2022-05-19",1.820,1.830,1.889,1.890,1.834,1.767,1.694,1.885));
        items.add(createChartItem("2022-05-18",1.840,1.860,1.861,1.881,1.809,1.745,1.687,1.889));
        items.add(createChartItem("2022-05-17",1.770,1.772,1.854,1.865,1.792,1.728,1.681,1.893));
        items.add(createChartItem("2022-05-16",1.775,1.795,1.780,1.833,1.757,1.703,1.676,1.897));
        items.add(createChartItem("2022-05-13",1.755,1.770,1.787,1.799,1.726,1.686,1.675,1.901));
        items.add(createChartItem("2022-05-12",1.739,1.750,1.763,1.781,1.699,1.654,1.675,1.904));
        items.add(createChartItem("2022-05-11",1.677,1.677,1.774,1.819,1.681,1.628,1.675,1.908));
        items.add(createChartItem("2022-05-10",1.593,1.600,1.679,1.687,1.664,1.611,1.68,1.913));
        items.add(createChartItem("2022-05-09",1.616,1.630,1.629,1.652,1.649,1.606,1.691,1.919));
        items.add(createChartItem("2022-05-06",1.622,1.628,1.649,1.674,1.645,1.612,1.706,1.927));
        items.add(createChartItem("2022-05-05",1.636,1.660,1.674,1.702,1.61,1.622,1.722,1.935));
        items.add(createChartItem("2022-04-29",1.595,1.619,1.687,1.695,1.575,1.63,1.736,1.942));
        items.add(createChartItem("2022-04-28",1.581,1.596,1.607,1.643,1.558,1.634,1.752,1.951));
        items.add(createChartItem("2022-04-27",1.442,1.450,1.610,1.614,1.563,1.649,1.767,1.96));
        items.add(createChartItem("2022-04-26",1.463,1.500,1.470,1.514,1.579,1.664,1.781,1.97));
        items.add(createChartItem("2022-04-25",1.497,1.561,1.499,1.575,1.634,1.695,1.806,1.982));
        items.add(createChartItem("2022-04-22",1.599,1.623,1.606,1.637,1.685,1.721,1.831,1.993));
        items.add(createChartItem("2022-04-21",1.620,1.672,1.632,1.700,1.709,1.749,1.852,2.003));
        items.add(createChartItem("2022-04-20",1.683,1.747,1.688,1.757,1.735,1.776,1.872,2.013));
        items.add(createChartItem("2022-04-19",1.739,1.753,1.747,1.789,1.75,1.8,1.89,2.023));
        items.add(createChartItem("2022-04-18",1.677,1.711,1.753,1.754,1.757,1.822,1.904,2.032));
        items.add(createChartItem("2022-04-15",1.686,1.747,1.726,1.748,1.757,1.841,1.918,2.039));
        items.add(createChartItem("2022-04-14",1.748,1.791,1.759,1.796,1.789,1.869,1.931,2.048));
        items.add(createChartItem("2022-04-13",1.749,1.766,1.763,1.803,1.817,1.885,1.937,2.054));
        items.add(createChartItem("2022-04-12",1.742,1.749,1.782,1.784,1.85,1.899,1.943,2.061));
        items.add(createChartItem("2022-04-11",1.755,1.849,1.757,1.852,1.887,1.916,1.952,2.067));
        items.add(createChartItem("2022-04-08",1.863,1.895,1.883,1.918,1.925,1.941,1.963,2.074));
        items.add(createChartItem("2022-04-07",1.883,1.905,1.899,1.929,1.95,1.955,1.963,2.08));
        items.add(createChartItem("2022-04-06",1.910,1.968,1.928,1.970,1.953,1.969,1.962,2.087));
        items.add(createChartItem("2022-04-01",1.924,1.929,1.966,1.989,1.947,1.981,1.964,2.094));
        items.add(createChartItem("2022-03-31",1.935,1.990,1.950,1.993,1.946,1.986,1.968,2.1));
        items.add(createChartItem("2022-03-30",1.925,1.927,2.005,2.006,1.957,1.994,1.976,2.107));
        items.add(createChartItem("2022-03-29",1.900,1.916,1.914,1.943,1.961,1.992,1.984,2.113));
        items.add(createChartItem("2022-03-28",1.889,1.942,1.902,1.943,1.985,1.989,1.998,2.119));
        items.add(createChartItem("2022-03-25",1.950,2.003,1.958,2.017,2.014,1.988,2.015,2.125));
        items.add(createChartItem("2022-03-24",1.971,2.001,2.008,2.029,2.026,1.988,2.027,2.132));
        items.add(createChartItem("2022-03-23",2.012,2.053,2.024,2.058,2.03,1.984,2.035,2.139));
        items.add(createChartItem("2022-03-22",2.022,2.028,2.035,2.066,2.024,1.97,2.043,2.144));
        items.add(createChartItem("2022-03-21",2.000,2.033,2.047,2.077,1.992,1.955,2.047,2.15));
        items.add(createChartItem("2022-03-18",1.980,2.011,2.016,2.020,1.961,1.947,2.05,2.157));
        items.add(createChartItem("2022-03-17",2.020,2.028,2.030,2.075,1.95,1.95,2.056,2.165));
        items.add(createChartItem("2022-03-16",1.870,1.922,1.990,1.998,1.938,1.958,2.063,2.174));
        items.add(createChartItem("2022-03-15",1.857,1.857,1.877,1.949,1.916,1.976,2.068,2.183));
        items.add(createChartItem("2022-03-14",1.890,1.921,1.892,1.931,1.919,2.008,2.078,2.195));
        items.add(createChartItem("2022-03-11",1.893,1.927,1.961,1.965,1.932,2.041,2.084,2.207));
        items.add(createChartItem("2022-03-10",1.940,1.962,1.970,1.988,1.95,2.066,2.085,2.217));
        items.add(createChartItem("2022-03-09",1.793,1.894,1.882,1.900,1.979,2.085,2.087,2.227));
        items.add(createChartItem("2022-03-08",1.864,1.959,1.889,1.980,2.035,2.116,2.096,2.238));
        items.add(createChartItem("2022-03-07",1.951,2.037,1.960,2.037,2.098,2.139,2.104,2.25));
        items.add(createChartItem("2022-03-04",2.033,2.090,2.049,2.111,2.15,2.154,2.112,2.261));
        items.add(createChartItem("2022-03-03",2.105,2.162,2.114,2.173,2.181,2.163,2.115,2.271));
        items.add(createChartItem("2022-03-02",2.141,2.187,2.162,2.188,2.191,2.167,2.116,2.28));
        items.add(createChartItem("2022-03-01",2.188,2.255,2.203,2.255,2.197,2.159,2.117,2.289));
        items.add(createChartItem("2022-02-28",2.191,2.197,2.223,2.233,2.179,2.148,2.115,2.297));
        items.add(createChartItem("2022-02-25",2.183,2.192,2.204,2.231,2.158,2.126,2.115,2.304));
        items.add(createChartItem("2022-02-24",2.121,2.164,2.162,2.206,2.144,2.104,2.113,2.311));
        items.add(createChartItem("2022-02-23",2.110,2.111,2.193,2.199,2.142,2.09,2.114,2.318));
        items.add(createChartItem("2022-02-22",2.080,2.110,2.115,2.117,2.122,2.077,2.113,2.326));
        items.add(createChartItem("2022-02-21",2.101,2.134,2.117,2.146,2.117,2.07,2.121,2.335));
        items.add(createChartItem("2022-02-18",2.117,2.135,2.133,2.144,2.094,2.071,2.129,2.342));
        items.add(createChartItem("2022-02-17",2.080,2.085,2.154,2.177,2.065,2.067,2.135,2.349));
        items.add(createChartItem("2022-02-16",2.082,2.102,2.090,2.124,2.037,2.065,2.137,2.355));
        items.add(createChartItem("2022-02-15",2.011,2.012,2.090,2.091,2.032,2.074,2.145,2.362));
        items.add(createChartItem("2022-02-14",1.958,1.979,2.004,2.030,2.023,2.082,2.147,2.369));
        items.add(createChartItem("2022-02-11",1.970,2.001,1.986,2.045,2.047,2.103,2.155,2.38));
        items.add(createChartItem("2022-02-10",1.988,2.069,2.013,2.078,2.07,2.122,2.164,2.39));
        items.add(createChartItem("2022-02-09",2.001,2.036,2.067,2.071,2.092,2.138,2.173,2.401));
        items.add(createChartItem("2022-02-08",1.993,2.115,2.045,2.117,2.116,2.15,2.181,2.411));
        items.add(createChartItem("2022-02-07",2.115,2.160,2.123,2.194,2.141,2.172,2.193,2.421));
        items.add(createChartItem("2022-01-28",2.068,2.128,2.102,2.152,2.16,2.188,2.205,2.429));
        items.add(createChartItem("2022-01-27",2.117,2.182,2.125,2.195,2.174,2.202,2.217,2.437));
        items.add(createChartItem("2022-01-26",2.151,2.184,2.186,2.199,2.183,2.21,2.228,2.445));
        items.add(createChartItem("2022-01-25",2.170,2.198,2.170,2.237,2.183,2.216,2.237,2.452));
        items.add(createChartItem("2022-01-24",2.149,2.152,2.215,2.226,2.203,2.212,2.243,2.459));
        items.add(createChartItem("2022-01-21",2.137,2.170,2.173,2.196,2.217,2.207,2.246,2.467));
        items.add(createChartItem("2022-01-20",2.164,2.173,2.172,2.200,2.229,2.205,2.257,2.474));
        items.add(createChartItem("2022-01-19",2.157,2.263,2.187,2.272,2.237,2.209,2.268,2.483));
        items.add(createChartItem("2022-01-18",2.253,2.272,2.268,2.290,2.248,2.211,2.277,2.49));
        items.add(createChartItem("2022-01-17",2.224,2.224,2.284,2.288,2.221,2.215,2.282,2.496));
        items.add(createChartItem("2022-01-14",2.185,2.190,2.236,2.251,2.197,2.222,2.291,2.5));
        items.add(createChartItem("2022-01-13",2.187,2.245,2.208,2.250,2.181,2.233,2.305,2.505));
        items.add(createChartItem("2022-01-12",2.187,2.191,2.242,2.249,2.182,2.246,2.321,2.511));
        items.add(createChartItem("2022-01-11",2.130,2.164,2.137,2.192,2.175,2.258,2.337,2.516));
        items.add(createChartItem("2022-01-10",2.130,2.150,2.163,2.185,2.208,2.274,2.36,2.522));
        items.add(createChartItem("2022-01-07",2.141,2.209,2.156,2.222,2.248,2.285,2.382,2.527));
        items.add(createChartItem("2022-01-06",2.157,2.190,2.211,2.220,2.285,2.308,2.403,2.531));
        items.add(createChartItem("2022-01-05",2.204,2.291,2.207,2.293,2.311,2.326,2.421,2.534));
        items.add(createChartItem("2022-01-04",2.298,2.410,2.303,2.412,2.342,2.343,2.436,2.535));
        items.add(createChartItem("2021-12-31",2.341,2.353,2.361,2.377,2.339,2.35,2.451,2.536));
        items.add(createChartItem("2021-12-30",2.331,2.340,2.343,2.360,2.323,2.36,2.466,2.535));
        items.add(createChartItem("2021-12-29",2.333,2.358,2.341,2.370,2.331,2.377,2.482,2.536));
        items.add(createChartItem("2021-12-28",2.293,2.295,2.360,2.364,2.341,2.396,2.498,2.535));
        items.add(createChartItem("2021-12-27",2.274,2.280,2.291,2.317,2.345,2.416,2.513,2.535));
        items.add(createChartItem("2021-12-24",2.261,2.393,2.280,2.394,2.361,2.446,2.533,2.537));
        items.add(createChartItem("2021-12-23",2.370,2.416,2.381,2.420,2.397,2.478,2.551,2.54));
        items.add(createChartItem("2021-12-22",2.383,2.396,2.394,2.411,2.424,2.497,2.562,2.541));
        items.add(createChartItem("2021-12-21",2.342,2.371,2.377,2.397,2.45,2.515,2.573,2.542));
        items.add(createChartItem("2021-12-20",2.358,2.458,2.371,2.463,2.486,2.53,2.587,2.544));
        items.add(createChartItem("2021-12-17",2.459,2.514,2.460,2.514,2.531,2.552,2.601,2.545));
        items.add(createChartItem("2021-12-16",2.501,2.536,2.518,2.540,2.559,2.572,2.605,2.546));
        items.add(createChartItem("2021-12-15",2.523,2.550,2.525,2.572,2.571,2.587,2.606,2.547));
        items.add(createChartItem("2021-12-14",2.546,2.575,2.558,2.584,2.58,2.6,2.607,2.546));
        items.add(createChartItem("2021-12-13",2.567,2.597,2.592,2.600,2.573,2.611,2.604,2.545));
        items.add(createChartItem("2021-12-10",2.544,2.554,2.602,2.610,2.574,2.621,2.601,2.543));
        items.add(createChartItem("2021-12-09",2.550,2.572,2.576,2.581,2.585,2.625,2.603,2.541));
        items.add(createChartItem("2021-12-08",2.523,2.525,2.574,2.575,2.602,2.628,2.605,2.539));
        items.add(createChartItem("2021-12-07",2.490,2.601,2.520,2.611,2.619,2.631,2.608,2.537));
        items.add(createChartItem("2021-12-06",2.586,2.640,2.596,2.660,2.649,2.644,2.616,2.533));
        items.add(createChartItem("2021-12-03",2.633,2.658,2.660,2.675,2.668,2.651,2.62,2.53));
        items.add(createChartItem("2021-12-02",2.640,2.644,2.662,2.680,2.664,2.639,2.616,2.525));
        items.add(createChartItem("2021-12-01",2.622,2.670,2.656,2.694,2.653,2.626,2.613,2.522));
        items.add(createChartItem("2021-11-30",2.651,2.690,2.673,2.700,2.644,2.613,2.609,2.52));
        items.add(createChartItem("2021-11-29",2.591,2.600,2.688,2.703,2.639,2.596,2.606,2.517));
        items.add(createChartItem("2021-11-26",2.593,2.596,2.642,2.659,2.634,2.582,2.602,2.513));
        items.add(createChartItem("2021-11-25",2.580,2.613,2.604,2.617,2.613,2.581,2.603,2.511));
        items.add(createChartItem("2021-11-24",2.604,2.640,2.612,2.676,2.599,2.583,2.604,2.509));
        items.add(createChartItem("2021-11-23",2.623,2.660,2.651,2.661,2.583,2.584,2.607,2.506));
        items.add(createChartItem("2021-11-22",2.558,2.558,2.660,2.662,2.553,2.588,2.606,2.502));
        items.add(createChartItem("2021-11-19",2.516,2.531,2.538,2.555,2.529,2.589,2.604,2.497));
        items.add(createChartItem("2021-11-18",2.495,2.530,2.532,2.558,2.548,2.593,2.605,2.494));
        items.add(createChartItem("2021-11-17",2.521,2.523,2.534,2.557,2.567,2.601,2.605,2.49));
        items.add(createChartItem("2021-11-16",2.496,2.532,2.503,2.545,2.585,2.604,2.607,2.486));
        items.add(createChartItem("2021-11-15",2.525,2.629,2.539,2.629,2.622,2.616,2.607,2.486));
        items.add(createChartItem("2021-11-12",2.610,2.626,2.634,2.643,2.649,2.623,2.606,2.485));
        items.add(createChartItem("2021-11-11",2.610,2.623,2.626,2.654,2.637,2.625,2.597,2.482));
        items.add(createChartItem("2021-11-10",2.574,2.660,2.623,2.664,2.634,2.625,2.585,2.479));
        items.add(createChartItem("2021-11-09",2.658,2.679,2.687,2.700,2.623,2.63,2.572,2.477));
        items.add(createChartItem("2021-11-08",2.565,2.565,2.676,2.680,2.61,2.624,2.552,2.475));
        items.add(createChartItem("2021-11-05",2.566,2.611,2.572,2.645,2.597,2.62,2.536,2.473));
        items.add(createChartItem("2021-11-04",2.590,2.594,2.611,2.647,2.613,2.617,2.525,2.473));
        items.add(createChartItem("2021-11-03",2.534,2.600,2.571,2.610,2.616,2.609,2.514,2.469));
        items.add(createChartItem("2021-11-02",2.591,2.620,2.622,2.673,2.637,2.609,2.5,2.466));
        items.add(createChartItem("2021-11-01",2.584,2.640,2.610,2.668,2.638,2.599,2.486,null));
        items.add(createChartItem("2021-10-29",2.564,2.618,2.653,2.659,2.642,2.589,2.476,null));
        items.add(createChartItem("2021-10-28",2.600,2.675,2.622,2.716,2.62,2.569,2.466,null));
        items.add(createChartItem("2021-10-27",2.613,2.624,2.680,2.698,2.603,2.545,2.456,null));
        items.add(createChartItem("2021-10-26",2.616,2.687,2.624,2.715,2.58,2.514,2.445,null));
        items.add(createChartItem("2021-10-25",2.538,2.540,2.631,2.633,2.56,2.48,2.438,null));
        items.add(createChartItem("2021-10-22",2.517,2.557,2.541,2.579,2.537,2.452,2.429,null));
        items.add(createChartItem("2021-10-21",2.516,2.565,2.540,2.573,2.519,2.433,2.43,null));
        items.add(createChartItem("2021-10-20",2.491,2.500,2.566,2.599,2.487,2.419,2.43,null));
        items.add(createChartItem("2021-10-19",2.506,2.518,2.521,2.557,2.448,2.391,2.425,null));
        items.add(createChartItem("2021-10-18",2.442,2.462,2.515,2.516,2.401,2.373,2.423,null));
        items.add(createChartItem("2021-10-15",2.345,2.388,2.454,2.463,2.368,2.362,2.422,null));
        items.add(createChartItem("2021-10-14",2.355,2.366,2.377,2.396,2.347,2.362,2.422,null));
        items.add(createChartItem("2021-10-13",2.288,2.288,2.371,2.373,2.351,2.368,2.427,null));
        items.add(createChartItem("2021-10-12",2.258,2.333,2.287,2.354,2.334,2.376,2.43,null));
        items.add(createChartItem("2021-10-11",2.295,2.358,2.350,2.389,2.345,2.395,2.431,null));
        items.add(createChartItem("2021-10-08",2.330,2.440,2.349,2.450,2.356,2.406,2.434,null));
        items.add(createChartItem("2021-09-30",2.296,2.298,2.399,2.405,2.376,2.426,2.436,null));
        items.add(createChartItem("2021-09-29",2.273,2.318,2.283,2.351,2.385,2.441,2.44,null));
        items.add(createChartItem("2021-09-28",2.337,2.398,2.344,2.419,2.418,2.46,2.452,null));
        items.add(createChartItem("2021-09-27",2.357,2.459,2.407,2.487,2.446,2.474,2.459,null));
        items.add(createChartItem("2021-09-24",2.399,2.443,2.449,2.511,2.455,2.481,2.461,null));
        items.add(createChartItem("2021-09-23",2.433,2.475,2.441,2.483,2.476,2.482,2.464,null));
        items.add(createChartItem("2021-09-22",2.428,2.450,2.451,2.469,2.497,2.487,2.467,null));
        items.add(createChartItem("2021-09-17",2.410,2.446,2.481,2.499,2.501,2.483,2.467,null));
        items.add(createChartItem("2021-09-16",2.450,2.540,2.453,2.547,2.502,2.467,2.461,null));
        items.add(createChartItem("2021-09-15",2.516,2.540,2.553,2.576,2.507,2.462,2.459,null));
        items.add(createChartItem("2021-09-14",2.427,2.463,2.547,2.597,2.487,2.446,2.447,null));
        items.add(createChartItem("2021-09-13",2.447,2.502,2.469,2.524,2.477,2.439,2.434,null));
        items.add(createChartItem("2021-09-10",2.441,2.478,2.486,2.495,2.466,2.444,2.428,null));
        items.add(createChartItem("2021-09-09",2.430,2.484,2.481,2.515,2.432,2.445,2.427,null));
        items.add(createChartItem("2021-09-08",2.445,2.489,2.454,2.504,2.417,2.442,2.427,null));
        items.add(createChartItem("2021-09-07",2.402,2.417,2.493,2.507,2.404,2.446,2.429,null));
        items.add(createChartItem("2021-09-06",2.301,2.318,2.417,2.418,2.402,2.447,2.426,null));
        items.add(createChartItem("2021-09-03",2.282,2.399,2.315,2.410,2.422,2.45,2.43,null));
        items.add(createChartItem("2021-09-02",2.375,2.386,2.404,2.424,2.457,2.456,2.442,null));
        items.add(createChartItem("2021-09-01",2.342,2.490,2.390,2.498,2.467,2.455,2.449,null));
        items.add(createChartItem("2021-08-31",2.442,2.515,2.483,2.515,2.489,2.448,2.458,null));
        items.add(createChartItem("2021-08-30",2.475,2.475,2.518,2.573,2.492,2.429,2.451,null));
        items.add(createChartItem("2021-08-27",2.429,2.439,2.492,2.507,2.477,2.412,2.448,null));
        items.add(createChartItem("2021-08-26",2.450,2.501,2.452,2.534,2.454,2.409,null,null));
        items.add(createChartItem("2021-08-25",2.443,2.510,2.498,2.516,2.443,2.412,null,null));
        items.add(createChartItem("2021-08-24",2.440,2.452,2.502,2.538,2.407,2.411,null,null));
        items.add(createChartItem("2021-08-23",2.357,2.385,2.443,2.451,2.366,2.405,null,null));
        items.add(createChartItem("2021-08-20",2.342,2.372,2.377,2.419,2.346,2.41,null,null));
        items.add(createChartItem("2021-08-19",2.296,2.311,2.396,2.425,2.364,2.429,null,null));
        items.add(createChartItem("2021-08-18",2.295,2.306,2.316,2.368,2.382,2.442,null,null));
        items.add(createChartItem("2021-08-17",2.286,2.324,2.300,2.367,2.415,2.467,null,null));
        items.add(createChartItem("2021-08-16",2.328,2.430,2.340,2.436,2.443,2.473,null,null));
        items.add(createChartItem("2021-08-13",2.446,2.461,2.466,2.572,2.474,2.484,null,null));
        items.add(createChartItem("2021-08-12",2.433,2.465,2.486,2.508,2.494,null,null,null));
        items.add(createChartItem("2021-08-11",2.403,2.443,2.483,2.498,2.503,null,null,null));
        items.add(createChartItem("2021-08-10",2.378,2.467,2.442,2.515,2.52,null,null,null));
        items.add(createChartItem("2021-08-09",2.426,2.525,2.491,2.525,2.503,null,null,null));
        items.add(createChartItem("2021-08-06",2.537,2.588,2.568,2.642,2.493,null,null,null));
        items.add(createChartItem("2021-08-05",2.490,2.550,2.530,2.555,null,null,null,null));
        items.add(createChartItem("2021-08-04",2.350,2.350,2.568,2.571,null,null,null,null));
        items.add(createChartItem("2021-08-03",2.334,2.450,2.359,2.460,null,null,null,null));
        items.add(createChartItem("2021-08-02",2.354,2.420,2.442,2.494,null,null,null,null));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
