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

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.tools.NumberFormat;
import eu.hansolo.fx.charts.tools.Order;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.HashMap;
import java.util.List;


public class BarChartBuilder <B extends BarChartBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected BarChartBuilder() { }


    // ******************** Methods *******************************************
    public static final BarChartBuilder create() {
        return new BarChartBuilder();
    }


    public final B items(final ChartItem... items) {
        properties.put("itemsArray", new SimpleObjectProperty(items));
        return (B)this;
    }
    public final B items(final List<ChartItem> items) {
        properties.put("itemsList", new SimpleObjectProperty<>(items));
        return (B)this;
    }

    public final B orientation(final Orientation orientation) {
        properties.put("orientation", new SimpleObjectProperty<>(orientation));
        return (B)this;
    }

    public final B backgroundFill(final Paint backgroundFill) {
        properties.put("backgroundFill", new SimpleObjectProperty<>(backgroundFill));
        return (B)this;
    }

    public final B namesBackgroundFill(final Paint namesBackgroundFill) {
        properties.put("namesBackgroundFill", new SimpleObjectProperty<>(namesBackgroundFill));
        return (B)this;
    }

    public final B barBackgroundFill(final Color barBackgroundFill) {
        properties.put("barBackgroundFill", new SimpleObjectProperty<>(barBackgroundFill));
        return (B)this;
    }

    public final B seriesFill(final Paint seriesFill) {
        properties.put("seriesFill", new SimpleObjectProperty<>(seriesFill));
        return (B)this;
    }

    public final B textFill(final Color textFill) {
        properties.put("textFill", new SimpleObjectProperty<>(textFill));
        return (B)this;
    }

    public final B namesTextFill(final Color namesTextFill) {
        properties.put("namesTextFill", new SimpleObjectProperty<>(namesTextFill));
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

    public final B numberFormat(final NumberFormat numberFormat) {
        properties.put("numberFormat", new SimpleObjectProperty(numberFormat));
        return (B)this;
    }

    public final B useItemFill(final boolean useItemFill) {
        properties.put("useItemFill", new SimpleBooleanProperty(useItemFill));
        return (B)this;
    }

    public final B useItemTextFill(final boolean useItemTextFill) {
        properties.put("useItemTextFill", new SimpleBooleanProperty(useItemTextFill));
        return (B)this;
    }

    public final B useNamesTextFill(final boolean useNamesTextFill) {
        properties.put("useNamesTextFill", new SimpleBooleanProperty(useNamesTextFill));
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

    public final B animated(final boolean animated) {
        properties.put("animated", new SimpleBooleanProperty(animated));
        return (B)this;
    }

    public final B animationDuration(final long animationDuration) {
        properties.put("animationDuration", new SimpleLongProperty(animationDuration));
        return (B)this;
    }

    public final B minNumberOfBars(final int minNumberOfBars) {
        properties.put("minNumberOfBars", new SimpleIntegerProperty(minNumberOfBars));
        return (B)this;
    }

    public final B useMinNumberOfBars(final boolean useMinNumberOfBars) {
        properties.put("useMinNumberOfBars", new SimpleBooleanProperty(useMinNumberOfBars));
        return (B)this;
    }

    public final B useGivenColors(final boolean useGivenColors) {
        properties.put("useGivenColors", new SimpleBooleanProperty(useGivenColors));
        return (B)this;
    }

    public final B colors(final List<Color> colors) {
        properties.put("colors", new SimpleObjectProperty<>(colors));
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


    public final BarChart build() {
        final BarChart barChart = new BarChart();

        if (properties.keySet().contains("itemsArray")) {
            barChart.setItems(((ObjectProperty<? extends ChartItem[]>) properties.get("itemsArray")).get());
        }
        if(properties.keySet().contains("itemsList")) {
            barChart.setItems(((ObjectProperty<List<? extends ChartItem>>) properties.get("itemsList")).get());
        }

        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"             -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    barChart.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"              -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    barChart.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"              -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    barChart.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"            -> barChart.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"           -> barChart.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"             -> barChart.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"            -> barChart.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"             -> barChart.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"            -> barChart.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"               -> barChart.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"               -> barChart.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"              -> barChart.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"              -> barChart.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"           -> barChart.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"           -> barChart.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"              -> barChart.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "orientation"          -> barChart.setOrientation(((ObjectProperty<Orientation>) properties.get(key)).get());
                case "backgroundFill"       -> barChart.setBackgroundFill(((ObjectProperty<Paint>) properties.get(key)).get());
                case "namesBackgroundFill"  -> barChart.setNamesBackgroundFill(((ObjectProperty<Paint>) properties.get(key)).get());
                case "barBackgroundFill"    -> barChart.setBarBackgroundFill(((ObjectProperty<Color>) properties.get(key)).get());
                case "seriesFill"           -> barChart.setSeriesFill(((ObjectProperty<Paint>) properties.get(key)).get());
                case "textFill"             -> barChart.setTextFill(((ObjectProperty<Color>) properties.get(key)).get());
                case "namesTextFill"        -> barChart.setNamesTextFill(((ObjectProperty<Color>) properties.get(key)).get());
                case "barBackgroundVisible" -> barChart.setBarBackgroundVisible(((BooleanProperty) properties.get(key)).get());
                case "shadowsVisible"       -> barChart.setShadowsVisible(((BooleanProperty) properties.get(key)).get());
                case "numberFormat"         -> barChart.setNumberFormat(((ObjectProperty<NumberFormat>) properties.get(key)).get());
                case "useItemFill"          -> barChart.setUseItemFill(((BooleanProperty) properties.get(key)).get());
                case "useItemTextFill"      -> barChart.setUseItemTextFill(((BooleanProperty) properties.get(key)).get());
                case "useNamesTextFill"     -> barChart.setUseNamesTextFill(((BooleanProperty) properties.get(key)).get());
                case "shortenNumbers"       -> barChart.setShortenNumbers(((BooleanProperty) properties.get(key)).get());
                case "sorted"               -> barChart.setSorted(((BooleanProperty) properties.get(key)).get());
                case "order"                -> barChart.setOrder(((ObjectProperty<Order>) properties.get(key)).get());
                case "animated"             -> barChart.setAnimated(((BooleanProperty) properties.get(key)).get());
                case "animationDuration"    -> barChart.setAnimationDuration(((LongProperty) properties.get(key)).get());
                case "minNumberOfBars"      -> barChart.setMinNumberOfBars(((IntegerProperty) properties.get(key)).get());
                case "useMinNumberOfBars"   -> barChart.setUseMinNumberOfBars(((BooleanProperty) properties.get(key)).get());
                case "useGivenColors"       -> barChart.setUseGivenColors(((BooleanProperty) properties.get(key)).get());
                case "colors"               -> barChart.setColors(((ObjectProperty<List<Color>>) properties.get(key)).get());
            }
        }
        return barChart;
    }
}
