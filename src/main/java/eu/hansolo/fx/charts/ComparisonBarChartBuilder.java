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

    public final B backgroundColor(final Color backgroundColor) {
        properties.put("backgroundColor", new SimpleObjectProperty<>(backgroundColor));
        return (B) this;
    }

    public final B textFill(final Color textFill) {
        properties.put("textFill", new SimpleObjectProperty<>(textFill));
        return (B) this;
    }

    public final B categoryTextFill(final Color categoryTextFill) {
        properties.put("categoryTextFill", new SimpleObjectProperty<>(categoryTextFill));
        return (B) this;
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
        final ComparisonBarChart chart = new ComparisonBarChart(series1, series2);

        for (String key : properties.keySet()) {
            if ("prefSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                chart.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if ("minSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                chart.setMinSize(dim.getWidth(), dim.getHeight());
            } else if ("maxSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                chart.setMaxSize(dim.getWidth(), dim.getHeight());
            } else if ("prefWidth".equals(key)) {
                chart.setPrefWidth(((DoubleProperty) properties.get(key)).get());
            } else if ("prefHeight".equals(key)) {
                chart.setPrefHeight(((DoubleProperty) properties.get(key)).get());
            } else if ("minWidth".equals(key)) {
                chart.setMinWidth(((DoubleProperty) properties.get(key)).get());
            } else if ("minHeight".equals(key)) {
                chart.setMinHeight(((DoubleProperty) properties.get(key)).get());
            } else if ("maxWidth".equals(key)) {
                chart.setMaxWidth(((DoubleProperty) properties.get(key)).get());
            } else if ("maxHeight".equals(key)) {
                chart.setMaxHeight(((DoubleProperty) properties.get(key)).get());
            } else if ("scaleX".equals(key)) {
                chart.setScaleX(((DoubleProperty) properties.get(key)).get());
            } else if ("scaleY".equals(key)) {
                chart.setScaleY(((DoubleProperty) properties.get(key)).get());
            } else if ("layoutX".equals(key)) {
                chart.setLayoutX(((DoubleProperty) properties.get(key)).get());
            } else if ("layoutY".equals(key)) {
                chart.setLayoutY(((DoubleProperty) properties.get(key)).get());
            } else if ("translateX".equals(key)) {
                chart.setTranslateX(((DoubleProperty) properties.get(key)).get());
            } else if ("translateY".equals(key)) {
                chart.setTranslateY(((DoubleProperty) properties.get(key)).get());
            } else if ("padding".equals(key)) {
                chart.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
            } // Control specific properties
            else if ("backgroundColor".equals(key)) {
                chart.setBackgroundFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("textFill".equals(key)) {
                chart.setTextFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("categoryTextFill".equals(key)) {
                chart.setCategoryTextFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("betterDarkerColor".equals(key)) {
                chart.setBetterDarkerColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("betterBrighterColor".equals(key)) {
                chart.setBetterBrighterColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("poorerDarkerColor".equals(key)) {
                chart.setPoorerDarkerColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("poorerBrighterColor".equals(key)) {
                chart.setPoorerBrighterColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("numberFormat".equals(key)) {
                chart.setNumberFormat(((ObjectProperty<NumberFormat>) properties.get(key)).get());
            } else if ("doCompare".equals(key)) {
                chart.setDoCompare(((BooleanProperty) properties.get(key)).get());
            } else if ("useItemTextFill".equals(key)) {
                chart.setUseItemTextFill(((BooleanProperty) properties.get(key)).get());
            } else if ("useCategoryTextFill".equals(key)) {
                chart.setUseCategoryTextFill(((BooleanProperty) properties.get(key)).get());
            } else if ("shortenNumbers".equals(key)) {
                chart.setShortenNumbers(((BooleanProperty) properties.get(key)).get());
            } else if ("sorted".equals(key)) {
                chart.setSorted(((BooleanProperty) properties.get(key)).get());
            } else if ("order".equals(key)) {
                chart.setOrder(((ObjectProperty<Order>) properties.get(key)).get());
            }
        }
        return chart;
    }
}
