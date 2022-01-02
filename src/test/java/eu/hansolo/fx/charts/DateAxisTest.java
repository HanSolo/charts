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

import eu.hansolo.fx.charts.data.TYChartItem;
import eu.hansolo.fx.charts.series.XYSeries;
import eu.hansolo.fx.charts.tools.Helper.Interval;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DateAxisTest extends Application {
    private static final DateTimeFormatter     DTF        = DateTimeFormatter.ofPattern("dd.MM");
    private static final double                AXIS_WIDTH = 25;
    private              XYSeries<TYChartItem> series;
    private              XYChart<TYChartItem>  chart;
    private              Axis                  xAxis;
    private              Axis                  yAxis;


    @Override public void init() {
        LocalDateTime     start = LocalDateTime.of(2021, 11, 1, 12, 0, 0);
        LocalDateTime     end   = LocalDateTime.now();
        List<TYChartItem> items = new ArrayList<>();

        items.add(new TYChartItem(start, 0, DTF.format(start), Color.RED));

        series = new XYSeries(items, ChartType.LINE, Color.RED);
        series.setSymbolsVisible(false);

        // XYChart
        xAxis = createBottomTimeAxis(start, end, "dd.MM", true);
        xAxis.setSameTickMarkLength(true);
        xAxis.setMediumTimeAxisTickLabelsVisible(true);

        xAxis = AxisBuilder.create(Orientation.HORIZONTAL, Position.BOTTOM)
                           .type(AxisType.TIME)
                           .dateTimeFormatPattern("dd.MM")
                           .autoScale(true)
                           .sameTickMarkLength(true)
                           .mediumTimeAxisTickLabelsVisible(true)
                           .start(LocalDateTime.now().minusDays(5))
                           .end(LocalDateTime.now().plusDays(5))
                           .rightAnchor(25d)
                           .bottomAnchor(0d)
                           .leftAnchor(25d)
                           .build();

        yAxis = createLeftYAxis(0, 20, true);
        chart = new XYChart<>(new XYPane(series), yAxis, xAxis);
        chart.setPrefSize(400, 200);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(chart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(new StackPane(pane));

        stage.setTitle("DateAxis Test");
        stage.setScene(scene);
        stage.show();

        updateChart();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private void updateChart() {
        Map<LocalDateTime, Long> data = getData();
        LocalDateTime            start                  = data.keySet().stream().min(Comparator.naturalOrder()).get();
        LocalDateTime            end                    = data.keySet().stream().max(Comparator.naturalOrder()).get();
        long                     maxNoOfDownloadsPerDay = data.values().stream().max(Comparator.comparingLong(Long::longValue)).get();
        List<TYChartItem>        items                = new ArrayList<>(data.size());
        data.entrySet().stream().forEach(entry -> items.add(new TYChartItem(entry.getKey(), entry.getValue().doubleValue(), DTF.format(entry.getKey()), Color.rgb(0, 212, 244, 0.5))));
        Collections.sort(items);

        Platform.runLater(() -> {
            xAxis.setStart(start);
            xAxis.setEnd(end);
            xAxis.resize();
            yAxis.setMaxValue(maxNoOfDownloadsPerDay);
            yAxis.resize();
            series.getItems().setAll(items);
        });
    }

    private Map<LocalDateTime, Long> getData() {
        Map<Long,Long> rawData = new HashMap<>();
        rawData.put(1637236800l, 243l);
        rawData.put(1637323200l, 223l);
        rawData.put(1637064000l, 186l);
        rawData.put(1637150400l, 221l);
        rawData.put(1637582400l, 354l);
        rawData.put(1637668800l, 399l);
        rawData.put(1637409600l, 100l);
        rawData.put(1637496000l, 140l);
        rawData.put(1637928000l, 247l);
        rawData.put(1638014400l, 191l);
        rawData.put(1637755200l, 212l);
        rawData.put(1637841600l, 348l);
        rawData.put(1638273600l, 442l);
        rawData.put(1638100800l, 382l);
        rawData.put(1638187200l, 353l);
        rawData.put(1641038400l, 27l);
        rawData.put(1636977600l, 87l);
        rawData.put(1639828800l, 319l);
        rawData.put(1639915200l, 434l);
        rawData.put(1639656000l, 315l);
        rawData.put(1639742400l, 232l);
        rawData.put(1640174400l, 264l);
        rawData.put(1640260800l, 297l);
        rawData.put(1640001600l, 210l);
        rawData.put(1640088000l, 322l);
        rawData.put(1640520000l, 134l);
        rawData.put(1640606400l, 226l);
        rawData.put(1640347200l, 167l);
        rawData.put(1640433600l, 112l);
        rawData.put(1640865600l, 222l);
        rawData.put(1640952000l, 291l);
        rawData.put(1640692800l, 293l);
        rawData.put(1640779200l, 183l);
        rawData.put(1638446400l, 675l);
        rawData.put(1638532800l, 349l);
        rawData.put(1638360000l, 429l);
        rawData.put(1638792000l, 297l);
        rawData.put(1638878400l, 284l);
        rawData.put(1638619200l, 418l);
        rawData.put(1638705600l, 380l);
        rawData.put(1639137600l, 451l);
        rawData.put(1639224000l, 224l);
        rawData.put(1638964800l, 273l);
        rawData.put(1639051200l, 412l);
        rawData.put(1639483200l, 341l);
        rawData.put(1639569600l, 351l);
        rawData.put(1639310400l, 186l);
        rawData.put(1639396800l, 461l);
        Map<LocalDateTime, Long> data = new HashMap<>();
        rawData.entrySet().stream().forEach(entry -> data.put(LocalDateTime.ofEpochSecond(entry.getKey(), 0, ZoneOffset.UTC), entry.getValue()));
        return data;
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

    private Axis createBottomTimeAxis(final LocalDateTime START, final LocalDateTime END, final String PATTERN, final boolean AUTO_SCALE) {
        Axis axis = new Axis(START, END, Orientation.HORIZONTAL, Position.BOTTOM);
        axis.setDateTimeFormatPattern(PATTERN);
        axis.setPrefHeight(AXIS_WIDTH);
        axis.setAutoScale(AUTO_SCALE);
        axis.setType(AxisType.TIME);

        AnchorPane.setBottomAnchor(axis, 0d);
        AnchorPane.setLeftAnchor(axis, 25d);
        AnchorPane.setRightAnchor(axis, 25d);

        return axis;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
