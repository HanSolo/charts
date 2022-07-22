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

import eu.hansolo.fx.charts.data.BubbleGridChartItem;
import eu.hansolo.fx.charts.tools.Order;
import eu.hansolo.fx.charts.tools.Topic;
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
import javafx.scene.paint.LinearGradient;

import java.util.HashMap;
import java.util.List;


public class BubbleGridChartBuilder<B extends BubbleGridChartBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected BubbleGridChartBuilder() {}


    // ******************** Methods *******************************************
    public static final BubbleGridChartBuilder create() {
        return new BubbleGridChartBuilder();
    }

    public final B items(final BubbleGridChartItem... ITEMS) {
        properties.put("itemsArray", new SimpleObjectProperty<>(ITEMS));
        return (B)this;
    }

    public final B items(final List<BubbleGridChartItem> ITEMS) {
        properties.put("itemsList", new SimpleObjectProperty<>(ITEMS));
        return (B)this;
    }

    public final B chartBackground(final Color COLOR) {
        properties.put("chartBackground", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B gridColor(final Color COLOR) {
        properties.put("gridColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B textColor(final Color COLOR) {
        properties.put("textColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B autoBubbleTextColor(final boolean AUTO) {
        properties.put("autoBubbleTextColor", new SimpleBooleanProperty(AUTO));
        return (B)this;
    }

    public final B showGrid(final boolean SHOW) {
        properties.put("showGrid", new SimpleBooleanProperty(SHOW));
        return (B)this;
    }

    public final B showValues(final boolean SHOW) {
        properties.put("showValues", new SimpleBooleanProperty(SHOW));
        return (B)this;
    }

    public final B showPercentage(final boolean SHOW) {
        properties.put("showPercentage", new SimpleBooleanProperty(SHOW));
        return (B)this;
    }

    public final B useXCategoryFill() {
        properties.put("useXCategoryFill", null);
        return (B)this;
    }

    public final B useYCategoryFill() {
        properties.put("useXCategoryFill", null);
        return (B)this;
    }

    public final B sortCategoryX(final Topic TOPIC, final Order ORDER) {
        properties.put("sortCategoryXTopic", new SimpleObjectProperty<>(TOPIC));
        properties.put("sortCategoryXOrder", new SimpleObjectProperty<>(ORDER));
        return (B) this;
    }

    public final B sortCategoryY(final Topic TOPIC, final Order ORDER) {
        properties.put("sortCategoryYTopic", new SimpleObjectProperty<>(TOPIC));
        properties.put("sortCategoryYOrder", new SimpleObjectProperty<>(ORDER));
        return (B) this;
    }

    public final B useGradientFill(final boolean USE) {
        properties.put("useGradientFill", new SimpleBooleanProperty(USE));
        return (B)this;
    }

    public final B shortenNumbers(final boolean SHORTEN) {
        properties.put("shortenNumbers", new SimpleBooleanProperty(SHORTEN));
        return (B)this;
    }

    public final B minColor(final Color MIN_COLOR) {
        properties.put("minColor", new SimpleObjectProperty<>(MIN_COLOR));
        return (B)this;
    }

    public final B maxColor(final Color MAX_COLOR) {
        properties.put("maxColor", new SimpleObjectProperty<>(MAX_COLOR));
        return (B)this;
    }

    public final B gradient(final LinearGradient GRADIENT) {
        properties.put("gradient", new SimpleObjectProperty<>(GRADIENT));
        return (B)this;
    }

    public final B prefSize(final double WIDTH, final double HEIGHT) {
        properties.put("prefSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B)this;
    }
    public final B minSize(final double WIDTH, final double HEIGHT) {
        properties.put("minSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B)this;
    }
    public final B maxSize(final double WIDTH, final double HEIGHT) {
        properties.put("maxSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B)this;
    }

    public final B prefWidth(final double PREF_WIDTH) {
        properties.put("prefWidth", new SimpleDoubleProperty(PREF_WIDTH));
        return (B)this;
    }
    public final B prefHeight(final double PREF_HEIGHT) {
        properties.put("prefHeight", new SimpleDoubleProperty(PREF_HEIGHT));
        return (B)this;
    }

    public final B minWidth(final double MIN_WIDTH) {
        properties.put("minWidth", new SimpleDoubleProperty(MIN_WIDTH));
        return (B)this;
    }
    public final B minHeight(final double MIN_HEIGHT) {
        properties.put("minHeight", new SimpleDoubleProperty(MIN_HEIGHT));
        return (B)this;
    }

    public final B maxWidth(final double MAX_WIDTH) {
        properties.put("maxWidth", new SimpleDoubleProperty(MAX_WIDTH));
        return (B)this;
    }
    public final B maxHeight(final double MAX_HEIGHT) {
        properties.put("maxHeight", new SimpleDoubleProperty(MAX_HEIGHT));
        return (B)this;
    }

    public final B scaleX(final double SCALE_X) {
        properties.put("scaleX", new SimpleDoubleProperty(SCALE_X));
        return (B)this;
    }
    public final B scaleY(final double SCALE_Y) {
        properties.put("scaleY", new SimpleDoubleProperty(SCALE_Y));
        return (B)this;
    }

    public final B layoutX(final double LAYOUT_X) {
        properties.put("layoutX", new SimpleDoubleProperty(LAYOUT_X));
        return (B)this;
    }
    public final B layoutY(final double LAYOUT_Y) {
        properties.put("layoutY", new SimpleDoubleProperty(LAYOUT_Y));
        return (B)this;
    }

    public final B translateX(final double TRANSLATE_X) {
        properties.put("translateX", new SimpleDoubleProperty(TRANSLATE_X));
        return (B)this;
    }
    public final B translateY(final double TRANSLATE_Y) {
        properties.put("translateY", new SimpleDoubleProperty(TRANSLATE_Y));
        return (B)this;
    }

    public final B padding(final Insets INSETS) {
        properties.put("padding", new SimpleObjectProperty<>(INSETS));
        return (B)this;
    }


    public final BubbleGridChart build() {
        final BubbleGridChart bubbleGridChart = new BubbleGridChart();

        if (properties.keySet().contains("itemsArray")) {
            bubbleGridChart.setItems(((ObjectProperty<BubbleGridChartItem[]>) properties.get("itemsArray")).get());
        }
        if(properties.keySet().contains("itemsList")) {
            bubbleGridChart.setItems(((ObjectProperty<List<BubbleGridChartItem>>) properties.get("itemsList")).get());
        }

        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"            -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    bubbleGridChart.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"             -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    bubbleGridChart.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"             -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    bubbleGridChart.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"           -> bubbleGridChart.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"          -> bubbleGridChart.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"            -> bubbleGridChart.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"           -> bubbleGridChart.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"            -> bubbleGridChart.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"           -> bubbleGridChart.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"              -> bubbleGridChart.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"              -> bubbleGridChart.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"             -> bubbleGridChart.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"             -> bubbleGridChart.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"          -> bubbleGridChart.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"          -> bubbleGridChart.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"             -> bubbleGridChart.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "chartBackground"     -> bubbleGridChart.setChartBackground(((ObjectProperty<Color>) properties.get(key)).get());
                case "textColor"           -> bubbleGridChart.setTextColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "autoBubbleTextColor" -> bubbleGridChart.setAutoBubbleTextColor(((BooleanProperty) properties.get(key)).get());
                case "gridColor"           -> bubbleGridChart.setGridColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "showGrid"            -> bubbleGridChart.setShowGrid(((BooleanProperty) properties.get(key)).get());
                case "showValues"          -> bubbleGridChart.setShowValues(((BooleanProperty) properties.get(key)).get());
                case "showPercentage"      -> bubbleGridChart.setShowPercentage(((BooleanProperty) properties.get(key)).get());
                case "useXCategoryFill"    -> bubbleGridChart.useXCategoryFill();
                case "useYCategoryFill"    -> bubbleGridChart.useYCategoryFill();
                case "sortCategoryXTopic"  -> bubbleGridChart.sortCategoryX(((ObjectProperty<Topic>) properties.get("sortCategoryXTopic")).get(), ((ObjectProperty<Order>) properties.get("sortCategoryXOrder")).get());
                case "sortCategoryYTopic"  -> bubbleGridChart.sortCategoryY(((ObjectProperty<Topic>) properties.get("sortCategoryYTopic")).get(), ((ObjectProperty<Order>) properties.get("sortCategoryYOrder")).get());
                case "useGradientFill"     -> bubbleGridChart.setUseGradientFill(((BooleanProperty) properties.get(key)).get());
                case "shortenNumbers"      -> bubbleGridChart.setShortenNumbers(((BooleanProperty) properties.get(key)).get());
                case "minColor"            -> bubbleGridChart.setMinColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "maxColor"            -> bubbleGridChart.setMaxColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "gradient"            -> bubbleGridChart.setGradient(((ObjectProperty<LinearGradient>) properties.get(key)).get());
            }
        }
        return bubbleGridChart;
    }
}
