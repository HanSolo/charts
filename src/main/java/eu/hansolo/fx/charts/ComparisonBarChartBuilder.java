/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
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

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.tools.NumberFormat;
import eu.hansolo.fx.charts.tools.Order;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.HashMap;


public class ComparisonBarChartBuilder <B extends ComparisonBarChartBuilder<B>> {
    private HashMap<String, Property>  properties = new HashMap<>();
    private ChartItemSeries<ChartItem> series1;
    private ChartItemSeries<ChartItem> series2;


    // ******************** Constructors **************************************
    protected ComparisonBarChartBuilder(final ChartItemSeries<ChartItem> SERIES_1, final ChartItemSeries<ChartItem> SERIES_2) {
        series1 = SERIES_1;
        series2 = SERIES_2;
    }


    // ******************** Methods *******************************************
    public static final ComparisonBarChartBuilder create(final ChartItemSeries<ChartItem> series1, final ChartItemSeries<ChartItem> series2) {
        return new ComparisonBarChartBuilder(series1, series2);
    }

    public final B backgroundFill(final Paint backgroundFill) {
        properties.put("backgroundFill", new SimpleObjectProperty<>(backgroundFill));
        return (B)this;
    }

    public final B categoryBackgroundFill(final Paint categoryBackgroundFill) {
        properties.put("categoryBackgroundFill", new SimpleObjectProperty<>(categoryBackgroundFill));
        return (B)this;
    }

    public final B barBackgroundFill(final Color barBackgroundFill) {
        properties.put("barBackgroundFill", new SimpleObjectProperty<>(barBackgroundFill));
        return (B)this;
    }

    public final B textFill(final Color textFill) {
        properties.put("textFill", new SimpleObjectProperty<>(textFill));
        return (B)this;
    }

    public final B categoryTextFill(final Color categoryTextFill) {
        properties.put("categoryTextFill", new SimpleObjectProperty<>(categoryTextFill));
        return (B)this;
    }

    public final B betterColor(final Color betterColor) {
        properties.put("betterColor", new SimpleObjectProperty<>(betterColor));
        return (B)this;
    }

    public final B poorerColor(final Color poorerColor) {
        properties.put("poorerColor", new SimpleObjectProperty<>(poorerColor));
        return (B)this;
    }

    public final B betterDarkerColor(final Color betterDarkerColor) {
        properties.put("betterDarkerColor", new SimpleObjectProperty<>(betterDarkerColor));
        return (B)this;
    }

    public final B betterBrighterColor(final Color betterBrighterColor) {
        properties.put("betterBrighterColor", new SimpleObjectProperty<>(betterBrighterColor));
        return (B)this;
    }

    public final B poorerDarkerColor(final Color poorerDarkerColor) {
        properties.put("poorerDarkerColor", new SimpleObjectProperty<>(poorerDarkerColor));
        return (B)this;
    }

    public final B poorerBrighterColor(final Color poorerBrighterColor) {
        properties.put("poorerBrighterColor", new SimpleObjectProperty<>(poorerBrighterColor));
        return (B)this;
    }

    public final B barBackgroundVisible(final boolean barBackgroundVisible) {
        properties.put("barBackgroundVisible", new SimpleBooleanProperty(barBackgroundVisible));
        return (B)this;
    }

    public final B shadowsVisible(final boolean shadowsVisible) {
        properties.put("shadowsVisible", new SimpleBooleanProperty(shadowsVisible));
        return (B)this;
    }

    public final B categorySumVisible(final boolean categorySumVisible) {
        properties.put("categorySumVisible", new SimpleBooleanProperty(categorySumVisible));
        return (B)this;
    }

    public final B numberFormat(final NumberFormat numberFormat) {
        properties.put("numberFormat", new SimpleObjectProperty(numberFormat));
        return (B)this;
    }

    public final B doCompare(final boolean doCompare) {
        properties.put("doCompare", new SimpleBooleanProperty(doCompare));
        return (B)this;
    }

    public final B useItemTextFill(final boolean useItemTextFill) {
        properties.put("useItemTextFill", new SimpleBooleanProperty(useItemTextFill));
        return (B)this;
    }

    public final B useCategoryTextFill(final boolean useCategoryTextFill) {
        properties.put("useCategoryTextFill", new SimpleBooleanProperty(useCategoryTextFill));
        return (B)this;
    }

    public final B shortenNumbers(final boolean shortenNumbers) {
        properties.put("shortenNumbers", new SimpleBooleanProperty(shortenNumbers));
        return (B)this;
    }

    public final B sorted(final boolean sorted) {
        properties.put("sorted", new SimpleBooleanProperty(sorted));
        return (B)this;
    }

    public final B order(final Order order) {
        properties.put("order", new SimpleObjectProperty<>(order));
        return (B)this;
    }

    // General properties
    public final B prefSize(final double width, final double height) {
        properties.put("prefSize", new SimpleObjectProperty<>(new Dimension2D(width, height)));
        return (B) this;
    }
    public final B minSize(final double width, final double height) {
        properties.put("minSize", new SimpleObjectProperty<>(new Dimension2D(width, height)));
        return (B) this;
    }
    public final B maxSize(final double width, final double height) {
        properties.put("maxSize", new SimpleObjectProperty<>(new Dimension2D(width, height)));
        return (B) this;
    }

    public final B prefWidth(final double prefWidth) {
        properties.put("prefWidth", new SimpleDoubleProperty(prefWidth));
        return (B) this;
    }
    public final B prefHeight(final double prefHeight) {
        properties.put("prefHeight", new SimpleDoubleProperty(prefHeight));
        return (B) this;
    }

    public final B minWidth(final double minWidth) {
        properties.put("minWidth", new SimpleDoubleProperty(minWidth));
        return (B) this;
    }
    public final B minHeight(final double minHeight) {
        properties.put("minHeight", new SimpleDoubleProperty(minHeight));
        return (B) this;
    }

    public final B maxWidth(final double maxWidth) {
        properties.put("maxWidth", new SimpleDoubleProperty(maxWidth));
        return (B) this;
    }
    public final B maxHeight(final double maxHeight) {
        properties.put("maxHeight", new SimpleDoubleProperty(maxHeight));
        return (B) this;
    }

    public final B scaleX(final double scaleX) {
        properties.put("scaleX", new SimpleDoubleProperty(scaleX));
        return (B) this;
    }
    public final B scaleY(final double scaleY) {
        properties.put("scaleY", new SimpleDoubleProperty(scaleY));
        return (B) this;
    }

    public final B layoutX(final double layoutX) {
        properties.put("layoutX", new SimpleDoubleProperty(layoutX));
        return (B) this;
    }
    public final B layoutY(final double layoutY) {
        properties.put("layoutY", new SimpleDoubleProperty(layoutY));
        return (B) this;
    }

    public final B translateX(final double translateX) {
        properties.put("translateX", new SimpleDoubleProperty(translateX));
        return (B) this;
    }
    public final B translateY(final double translateY) {
        properties.put("translateY", new SimpleDoubleProperty(translateY));
        return (B) this;
    }

    public final B padding(final Insets insets) {
        properties.put("padding", new SimpleObjectProperty<>(insets));
        return (B) this;
    }


    public final ComparisonBarChart build() {
        final ComparisonBarChart comparisonBarChart = new ComparisonBarChart(series1, series2);

        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"               -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    comparisonBarChart.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"                -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    comparisonBarChart.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"                -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    comparisonBarChart.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"              -> comparisonBarChart.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"             -> comparisonBarChart.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"               -> comparisonBarChart.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"              -> comparisonBarChart.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"               -> comparisonBarChart.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"              -> comparisonBarChart.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"                 -> comparisonBarChart.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"                 -> comparisonBarChart.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"                -> comparisonBarChart.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"                -> comparisonBarChart.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"             -> comparisonBarChart.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"             -> comparisonBarChart.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"                -> comparisonBarChart.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "backgroundFill"         -> comparisonBarChart.setBackgroundFill(((ObjectProperty<Paint>) properties.get(key)).get());
                case "categoryBackgroundFill" -> comparisonBarChart.setCategoryBackgroundFill(((ObjectProperty<Paint>) properties.get(key)).get());
                case "barBackgroundFill"      -> comparisonBarChart.setBarBackgroundFill(((ObjectProperty<Color>) properties.get(key)).get());
                case "textFill"               -> comparisonBarChart.setTextFill(((ObjectProperty<Color>) properties.get(key)).get());
                case "categoryTextFill"       -> comparisonBarChart.setCategoryTextFill(((ObjectProperty<Color>) properties.get(key)).get());
                case "betterColor"            -> comparisonBarChart.setBetterColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "poorer"                 -> comparisonBarChart.setPoorerColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "betterDarkerColor"      -> comparisonBarChart.setBetterDarkerColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "betterBrighterColor"    -> comparisonBarChart.setBetterBrighterColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "poorerDarkerColor"      -> comparisonBarChart.setPoorerDarkerColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "poorerBrighterColor"    -> comparisonBarChart.setPoorerBrighterColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "barBackgroundVisible"   -> comparisonBarChart.setBarBackgroundVisible(((BooleanProperty) properties.get(key)).get());
                case "shadowsVisible"         -> comparisonBarChart.setShadowsVisible(((BooleanProperty) properties.get(key)).get());
                case "categorySumVisible"     -> comparisonBarChart.setCategorySumVisible(((BooleanProperty) properties.get(key)).get());
                case "numberFormat"           -> comparisonBarChart.setNumberFormat(((ObjectProperty<NumberFormat>) properties.get(key)).get());
                case "doCompare"              -> comparisonBarChart.setDoCompare(((BooleanProperty) properties.get(key)).get());
                case "useItemTextFill"        -> comparisonBarChart.setUseItemTextFill(((BooleanProperty) properties.get(key)).get());
                case "useCategoryTextFill"    -> comparisonBarChart.setUseCategoryTextFill(((BooleanProperty) properties.get(key)).get());
                case "shortenNumbers"         -> comparisonBarChart.setShortenNumbers(((BooleanProperty) properties.get(key)).get());
                case "sorted"                 -> comparisonBarChart.setSorted(((BooleanProperty) properties.get(key)).get());
                case "order"                  -> comparisonBarChart.setOrder(((ObjectProperty<Order>) properties.get(key)).get());
            }
        }
        return comparisonBarChart;
    }
}
