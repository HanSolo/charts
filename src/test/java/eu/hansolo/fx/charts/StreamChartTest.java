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

import eu.hansolo.fx.charts.StreamChart.Category;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.ChartItemBuilder;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.ZoneId;
import java.time.ZonedDateTime;


public class StreamChartTest extends Application {
    private StreamChart streamChart;
    private enum Colors {
        LIGHT_BLUE(Color.web("#a6cee3")),
        ORANGE(Color.web("#fdbf6f")),
        LIGHT_RED(Color.web("#fb9a99")),
        LIGHT_GREEN(Color.web("#b2df8a")),
        YELLOW(Color.web("#ffff99")),
        PURPLE(Color.web("#cab2d6")),
        BLUE(Color.web("#1f78b4")),
        GREEN(Color.web("#33a02c")),
        DARK_ORCHID(Color.web("#9520c9")),
        DARK_PASTEL_GREEN(Color.web("#1faf44")),
        SPICY_PINK(Color.web("#f022b2")),
        STARSHIP(Color.web("#dafd38")),
        LIMA(Color.web("#74ce28"));

        private Color color;
        private Color translucentColor;

        Colors(final Color COLOR) {
            color = COLOR;
            translucentColor = Helper.getColorWithOpacity(color, 0.75);
        }

        public Color get() { return color; }
        public Color getTranslucent() { return translucentColor; }
    }


    @Override public void init() {
        ChartItem[] items = {
            createChartItem("Gerrit", 8, 1, 1, Colors.LIMA.color),
            createChartItem("Gerrit", 5, 2, 1, Colors.LIMA.color),
            createChartItem("Gerrit", 3, 3, 1, Colors.LIMA.color),
            createChartItem("Gerrit", 2, 4, 1, Colors.LIMA.color),
            createChartItem("Gerrit", 1, 5, 1, Colors.LIMA.color),

            createChartItem("Sandra", 6, 1, 1, Colors.DARK_ORCHID.color),
            createChartItem("Sandra", 4, 2, 1, Colors.DARK_ORCHID.color),
            createChartItem("Sandra", 3, 3, 1, Colors.DARK_ORCHID.color),
            createChartItem("Sandra", 2, 4, 1, Colors.DARK_ORCHID.color),
            createChartItem("Sandra", 3, 5, 1, Colors.DARK_ORCHID.color),
            createChartItem("Sandra", 2, 6, 1, Colors.DARK_ORCHID.color),
            createChartItem("Sandra", 2, 7, 1, Colors.DARK_ORCHID.color),

            //createChartItem("Lilli", 2, 1, 1, Colors.SPICY_PINK.color),
            createChartItem("Lilli", 4, 2, 1, Colors.SPICY_PINK.color),
            createChartItem("Lilli", 3, 3, 1, Colors.SPICY_PINK.color),
            createChartItem("Lilli", 3, 4, 1, Colors.SPICY_PINK.color),
            createChartItem("Lilli", 2, 5, 1, Colors.SPICY_PINK.color),
            createChartItem("Lilli", 1, 6, 1, Colors.SPICY_PINK.color),
            createChartItem("Lilli", 2, 7, 1, Colors.SPICY_PINK.color),

            //createChartItem("Anton", 2, 1, 1, Colors.SPICY_PINK.color),
            //createChartItem("Anton", 3, 2, 1, Colors.DARK_PASTEL_GREEN.color),
            //createChartItem("Anton", 2, 3, 1, Colors.DARK_PASTEL_GREEN.color),
            createChartItem("Anton", 3, 4, 1, Colors.DARK_PASTEL_GREEN.color),
            createChartItem("Anton", 4, 5, 1, Colors.DARK_PASTEL_GREEN.color),
            createChartItem("Anton", 3, 6, 1, Colors.DARK_PASTEL_GREEN.color),
            createChartItem("Anton", 2, 7, 1, Colors.DARK_PASTEL_GREEN.color)
            };

        streamChart = StreamChartBuilder.create()
                                        .items(items)
                                        .category(Category.DAY)
                                        .autoItemWidth(true)
                                        .itemWidth(80)
                                        .itemGap(10)
                                        .autoItemGap(true)
                                        .build();
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(streamChart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Stream Chart");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private ChartItem createChartItem(final String NAME, final double VALUE, final int DAY, final int MONTH, final Color COLOR) {
        return ChartItemBuilder.create()
                               .name(NAME)
                               .timestamp(ZonedDateTime.of(2018, MONTH, DAY, 8, 00, 00, 00, ZoneId.systemDefault()))
                               .value(VALUE)
                               .fill(COLOR)
                               .build();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

