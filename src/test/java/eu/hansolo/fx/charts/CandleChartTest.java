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
import eu.hansolo.fx.charts.event.ChartEvt;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class CandleChartTest extends Application {
    private static final double   INSET = 20;
    private CandleChart           candleChart;
    private List<CandleChartItem> items;
    private Axis                  xAxisBottom;
    private Axis                  yAxisLeft;


    @Override public void init() {
        items = new ArrayList<>();

        // Prepare AAPL historic data for CandleChart
        prepareData();

        double minValue = items.stream().min(Comparator.comparing(CandleChartItem::getLow)).get().getLow();
        double maxValue = items.stream().max(Comparator.comparing(CandleChartItem::getHigh)).get().getHigh();

        Instant       startInstant = items.stream().min(Comparator.comparing(CandleChartItem::getTimestamp)).get().getTimestamp();
        Instant       endInstant   = items.stream().max(Comparator.comparing(CandleChartItem::getTimestamp)).get().getTimestamp();
        LocalDateTime start        = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault());
        LocalDateTime end          = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault());

        xAxisBottom = Helper.createBottomTimeAxis(start, end, "MM:yyyy", true, INSET, INSET, 0d);
        xAxisBottom.setAxisColor(Color.WHITE);
        xAxisBottom.setTickMarkColor(Color.WHITE);
        xAxisBottom.setTickLabelColor(Color.WHITE);

        yAxisLeft   = Helper.createLeftAxis(minValue, maxValue, INSET);
        yAxisLeft.setAxisColor(Color.WHITE);
        yAxisLeft.setTickMarkColor(Color.WHITE);
        yAxisLeft.setTickLabelColor(Color.WHITE);

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
                                        .build();

        AnchorPane.setTopAnchor(candleChart, 0d);
        AnchorPane.setRightAnchor(candleChart, 0d);
        AnchorPane.setBottomAnchor(candleChart, INSET);
        AnchorPane.setLeftAnchor(candleChart, INSET);
    }

    @Override public void start(Stage stage) {
        AnchorPane pane = new AnchorPane();
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(48, 48, 48), CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setPadding(new Insets(10));
        pane.getChildren().addAll(candleChart, xAxisBottom, yAxisLeft);

        Scene scene = new Scene(pane);

        stage.setTitle("Candle Chart");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private CandleChartItem createChartItem(final String DATE, final double LOW, final double OPEN, final double CLOSE, final double HIGH) {

        String[] dateParts = DATE.split("-");
        final int MONTH = Integer.parseInt(dateParts[0]);
        final int DAY   = Integer.parseInt(dateParts[1]);
        final int YEAR  = Integer.parseInt(dateParts[2]);
        return CandleChartItemBuilder.create()
                                     .name("AAPL")
                                     .timestamp(ZonedDateTime.of(YEAR, MONTH, DAY, 0, 00, 00, 00, ZoneId.systemDefault()))
                                     .low(LOW)
                                     .open(OPEN)
                                     .close(CLOSE)
                                     .high(HIGH)
                                     .build();
    }

    private void prepareData() {
        items.add(createChartItem("12-14-2010", 45.5714, 45.9614, 45.7557, 46.0771));
        items.add(createChartItem("12-13-2010", 45.8571, 46.3386, 45.9528, 46.4371));
        items.add(createChartItem("12-10-2010", 45.5143, 45.6643, 45.7943, 45.8643));
        items.add(createChartItem("12-09-2010", 45.5743, 46.0188, 45.6796, 46.0714));
        items.add(createChartItem("12-08-2010", 45.3014, 45.6614, 45.8586, 45.86));
        items.add(createChartItem("12-07-2010", 45.4457, 46.2571, 45.4586, 46.2843));
        items.add(createChartItem("12-06-2010", 45.4886, 45.52, 45.7357, 46.0471));
        items.add(createChartItem("12-03-2010", 45.1914, 45.2871, 45.3486, 45.5214));
        items.add(createChartItem("12-02-2010", 44.9843, 45.3614, 45.45, 45.5714));
        items.add(createChartItem("12-01-2010", 45, 45.0386, 45.2, 45.3928));
        items.add(createChartItem("11-30-2010", 44.41, 44.7914, 44.45, 44.9086));
        items.add(createChartItem("11-29-2010", 44.4828, 45.0714, 45.2671, 45.3543));
        items.add(createChartItem("11-26-2010", 44.7057, 44.82, 45, 45.3857));
        items.add(createChartItem("11-24-2010", 44.5357, 44.5714, 44.9707, 45.0571));
        items.add(createChartItem("11-23-2010", 43.7943, 44.35, 44.1043, 44.5357));
        items.add(createChartItem("11-22-2010", 43.6957, 43.8114, 44.7657, 44.7657));
        items.add(createChartItem("11-19-2010", 43.6057, 43.9957, 43.8186, 44.0571));
        items.add(createChartItem("11-18-2010", 43.5271, 43.6, 44.0614, 44.2386));
        items.add(createChartItem("11-17-2010", 42.5371, 43.0286, 42.9286, 43.4271));
        items.add(createChartItem("11-16-2010", 42.76, 43.6743, 43.0843, 43.9428));
        items.add(createChartItem("11-15-2010", 43.7528, 44.0657, 43.8621, 44.3628));
        items.add(createChartItem("11-12-2010", 43.3757, 45.1428, 44.0043, 45.2143));
        items.add(createChartItem("11-11-2010", 44.8928, 45, 45.2364, 45.4857));
        items.add(createChartItem("11-10-2010", 44.7928, 45.2343, 45.4328, 45.5386));
        items.add(createChartItem("11-09-2010", 44.9286, 45.8643, 45.1543, 45.9));
        items.add(createChartItem("11-08-2010", 45.2514, 45.3143, 45.5171, 45.6814));
        items.add(createChartItem("11-05-2010", 45.25, 45.4271, 45.3043, 45.6528));
        items.add(createChartItem("11-04-2010", 45.0043, 45.0643, 45.4671, 45.74));
        items.add(createChartItem("11-03-2010", 44.0757, 44.4814, 44.6857, 44.6971));
        items.add(createChartItem("11-02-2010", 43.8571, 43.8571, 44.1943, 44.3128));
        items.add(createChartItem("11-01-2010", 43.1714, 43.1743, 43.4543, 43.6571));
        items.add(createChartItem("10-29-2010", 42.9814, 43.4614, 42.9971, 43.6971));
        items.add(createChartItem("10-28-2010", 42.9857, 43.9928, 43.6057, 44));
        items.add(createChartItem("10-27-2010", 43.6571, 43.95, 43.9757, 44.2714));
        items.add(createChartItem("10-26-2010", 43.6643, 43.8386, 44.0071, 44.2486));
        items.add(createChartItem("10-25-2010", 44.0628, 44.1557, 44.12, 44.5143));
        items.add(createChartItem("10-22-2010", 43.7571, 44.1528, 43.9243, 44.2914));
        items.add(createChartItem("10-21-2010", 43.8286, 44.6228, 44.2171, 44.9628));
        items.add(createChartItem("10-20-2010", 43.8386, 44.1428, 44.3614, 44.8928));
        items.add(createChartItem("10-19-2010", 42.86, 43.3428, 44.2128, 44.8243));
        items.add(createChartItem("10-18-2010", 44.8986, 45.4957, 45.4286, 45.5714));
        items.add(createChartItem("10-15-2010", 43.5586, 43.92, 44.9628, 45));
        items.add(createChartItem("10-14-2010", 42.9143, 43.0986, 43.1871, 43.21));
        items.add(createChartItem("10-13-2010", 42.8286, 42.8857, 42.8771, 43.1371));
        items.add(createChartItem("10-12-2010", 41.7843, 42.2014, 42.6486, 42.7857));
        items.add(createChartItem("10-11-2010", 42.0857, 42.1057, 42.1943, 42.4628));
        items.add(createChartItem("10-08-2010", 41.4286, 41.6733, 42.01, 42.0714));
        items.add(createChartItem("10-07-2010", 40.9871, 41.4766, 41.3171, 41.4971));
        items.add(createChartItem("10-06-2010", 40.7514, 41.37, 41.3128, 41.7128));
        items.add(createChartItem("10-05-2010", 40.2601, 40.2857, 41.2771, 41.35));
        items.add(createChartItem("10-04-2010", 39.6814, 40.2286, 39.8057, 40.4143));
        items.add(createChartItem("10-01-2010", 40.1928, 40.878, 40.36, 40.94));
        items.add(createChartItem("09-30-2010", 40.1786, 41.2857, 40.5357, 41.4286));
        items.add(createChartItem("09-29-2010", 40.8571, 41.0328, 41.0528, 41.4014));
        items.add(createChartItem("09-28-2010", 39.2857, 41.6814, 40.98, 41.6814));
        items.add(createChartItem("09-27-2010", 41.5728, 41.9964, 41.5948, 42.1043));
        items.add(createChartItem("09-24-2010", 41.5071, 41.7286, 41.76, 41.9328));
        items.add(createChartItem("09-23-2010", 40.8571, 40.9043, 41.2743, 41.8228));
        items.add(createChartItem("09-22-2010", 40.3443, 40.3871, 41.1071, 41.14));
        items.add(createChartItem("09-21-2010", 40.3986, 40.5514, 40.5386, 41.05));
        items.add(createChartItem("09-20-2010", 39.4071, 39.44, 40.4614, 40.54));
        items.add(createChartItem("09-17-2010", 39.0971, 39.67, 39.3386, 39.7086));
        items.add(createChartItem("09-16-2010", 38.5, 38.6057, 39.51, 39.5243));
        items.add(createChartItem("09-15-2010", 38.2628, 38.31, 38.6028, 38.6257));
        items.add(createChartItem("09-14-2010", 37.9314, 38.03, 38.2943, 38.4528));
        items.add(createChartItem("09-13-2010", 37.9657, 37.9743, 38.1486, 38.3257));
        items.add(createChartItem("09-10-2010", 37.3428, 37.5986, 37.63, 37.7857));
        items.add(createChartItem("09-09-2010", 37.56, 37.8628, 37.5814, 38.0743));
        items.add(createChartItem("09-08-2010", 37.0143, 37.1114, 37.56, 37.77));
        items.add(createChartItem("09-07-2010", 36.6071, 36.6628, 36.83, 37.0757));
        items.add(createChartItem("09-03-2010", 36.3571, 36.4414, 36.9671, 36.9686));
        items.add(createChartItem("09-02-2010", 35.51, 35.8946, 36.0243, 36.0243));
        items.add(createChartItem("09-01-2010", 35.1828, 35.3528, 35.7614, 35.9228));
        items.add(createChartItem("08-31-2010", 34.3357, 34.55, 34.7286, 34.9371));
        items.add(createChartItem("08-30-2010", 34.3829, 34.3943, 34.6428, 35.1071));
        items.add(createChartItem("08-27-2010", 33.6514, 34.5357, 34.5171, 34.6586));
        items.add(createChartItem("08-26-2010", 34.3254, 35.0643, 34.3257, 35.1071));
        items.add(createChartItem("08-25-2010", 33.8857, 34.0057, 34.6986, 34.8557));
        items.add(createChartItem("08-24-2010", 34.0928, 34.6671, 34.2757, 34.7143));
        items.add(createChartItem("08-23-2010", 35.0357, 35.97, 35.1143, 36));
        items.add(createChartItem("08-20-2010", 35.5714, 35.6271, 35.6628, 36.2743));
        items.add(createChartItem("08-19-2010", 35.5257, 36.12, 35.6971, 36.2114));
        items.add(createChartItem("08-18-2010", 35.94, 36.0514, 36.1528, 36.3814));
        items.add(createChartItem("08-17-2010", 35.6, 35.7257, 35.9957, 36.3757));
        items.add(createChartItem("08-16-2010", 35.2314, 35.3686, 35.3771, 35.7157));
        items.add(createChartItem("08-13-2010", 35.5843, 35.95, 35.5857, 35.9828));
        items.add(createChartItem("08-12-2010", 35.16, 35.2414, 35.97, 36.1571));
        items.add(createChartItem("08-11-2010", 35.6871, 36.4857, 35.7414, 36.5271));
        items.add(createChartItem("08-10-2010", 36.7928, 37.1214, 37.0586, 37.2071));
        items.add(createChartItem("08-09-2010", 37.0814, 37.3543, 37.3928, 37.45));
        items.add(createChartItem("08-06-2010", 36.8043, 37.1114, 37.1558, 37.3557));
        items.add(createChartItem("08-05-2010", 37.2214, 37.39, 37.3857, 37.5971));
        items.add(createChartItem("08-04-2010", 37.1873, 37.5486, 37.5686, 37.7543));
        items.add(createChartItem("08-03-2010", 37.06, 37.2871, 37.4186, 37.6086));
        items.add(createChartItem("08-02-2010", 37.0886, 37.2057, 37.4071, 37.5128));
        items.add(createChartItem("07-30-2010", 36.4143, 36.5561, 36.75, 37.1));
        items.add(createChartItem("07-29-2010", 36.5857, 37.2443, 36.8728, 37.5214));
        items.add(createChartItem("07-28-2010", 37.1786, 37.6671, 37.28, 37.9986));
        items.add(createChartItem("07-27-2010", 37.1857, 37.2671, 37.7257, 37.8286));
        items.add(createChartItem("07-26-2010", 36.8157, 37.1428, 37.04, 37.1571));
        items.add(createChartItem("07-23-2010", 36.6114, 36.7271, 37.1343, 37.1971));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
