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

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.series.Series;
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
import java.util.List;


public class ConcentricRingChartBuilder<B extends ConcentricRingChartBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected ConcentricRingChartBuilder() {}


    // ******************** Methods *******************************************
    public static final ConcentricRingChartBuilder create() {
        return new ConcentricRingChartBuilder();
    }

    public final B series(final Series<ChartItem> SERIES) {
        properties.put("series", new SimpleObjectProperty(SERIES));
        return (B) this;
    }

    public final B items(final ChartItem... ITEMS) {
        properties.put("itemArray", new SimpleObjectProperty<>(ITEMS));
        return (B) this;
    }

    public final B items(final List<ChartItem> ITEMS) {
        properties.put("itemList", new SimpleObjectProperty<>(ITEMS));
        return (B) this;
    }

    public final B barBackgroundFill(final Color COLOR) {
        properties.put("barBackgroundFill", new SimpleObjectProperty<>(COLOR));
        return (B) this;
    }

    public final B sorted(final boolean SORTED) {
        properties.put("sorted", new SimpleBooleanProperty(SORTED));
        return (B) this;
    }

    public final B order(final Order ORDER) {
        properties.put("order", new SimpleObjectProperty<>(ORDER));
        return (B) this;
    }

    public final B numberFormat(final NumberFormat FORMAT) {
        properties.put("numberFormat", new SimpleObjectProperty(FORMAT));
        return (B)this;
    }
    
    public final B itemLabelFill(final Color FILL) {
        properties.put("itemLabelFill", new SimpleObjectProperty<>(FILL));
        return (B)this;
    }

    public final B shortenNumbers(final boolean SHORTEN) {
        properties.put("shortenNumbers", new SimpleBooleanProperty(SHORTEN));
        return (B)this;
    }

    public final B valueVisible(final boolean VISIBLE) {
        properties.put("valueVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    // General properties
    public final B prefSize(final double WIDTH, final double HEIGHT) {
        properties.put("prefSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B) this;
    }
    public final B minSize(final double WIDTH, final double HEIGHT) {
        properties.put("minSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B) this;
    }
    public final B maxSize(final double WIDTH, final double HEIGHT) {
        properties.put("maxSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B) this;
    }

    public final B prefWidth(final double PREF_WIDTH) {
        properties.put("prefWidth", new SimpleDoubleProperty(PREF_WIDTH));
        return (B) this;
    }
    public final B prefHeight(final double PREF_HEIGHT) {
        properties.put("prefHeight", new SimpleDoubleProperty(PREF_HEIGHT));
        return (B) this;
    }

    public final B minWidth(final double MIN_WIDTH) {
        properties.put("minWidth", new SimpleDoubleProperty(MIN_WIDTH));
        return (B) this;
    }
    public final B minHeight(final double MIN_HEIGHT) {
        properties.put("minHeight", new SimpleDoubleProperty(MIN_HEIGHT));
        return (B) this;
    }

    public final B maxWidth(final double MAX_WIDTH) {
        properties.put("maxWidth", new SimpleDoubleProperty(MAX_WIDTH));
        return (B) this;
    }
    public final B maxHeight(final double MAX_HEIGHT) {
        properties.put("maxHeight", new SimpleDoubleProperty(MAX_HEIGHT));
        return (B) this;
    }

    public final B scaleX(final double SCALE_X) {
        properties.put("scaleX", new SimpleDoubleProperty(SCALE_X));
        return (B) this;
    }
    public final B scaleY(final double SCALE_Y) {
        properties.put("scaleY", new SimpleDoubleProperty(SCALE_Y));
        return (B) this;
    }

    public final B layoutX(final double LAYOUT_X) {
        properties.put("layoutX", new SimpleDoubleProperty(LAYOUT_X));
        return (B) this;
    }
    public final B layoutY(final double LAYOUT_Y) {
        properties.put("layoutY", new SimpleDoubleProperty(LAYOUT_Y));
        return (B) this;
    }

    public final B translateX(final double TRANSLATE_X) {
        properties.put("translateX", new SimpleDoubleProperty(TRANSLATE_X));
        return (B) this;
    }
    public final B translateY(final double TRANSLATE_Y) {
        properties.put("translateY", new SimpleDoubleProperty(TRANSLATE_Y));
        return (B) this;
    }

    public final B padding(final Insets INSETS) {
        properties.put("padding", new SimpleObjectProperty<>(INSETS));
        return (B) this;
    }


    public final ConcentricRingChart build() {
        final ConcentricRingChart concentricRingChart = new ConcentricRingChart();

        if (properties.keySet().contains("series")) {
            concentricRingChart.setItems(((ObjectProperty<Series<ChartItem>>) properties.get("series")).get());
        }
        if (properties.keySet().contains("itemArray")) {
            concentricRingChart.setItems(((ObjectProperty<ChartItem[]>) properties.get("itemArray")).get());
        }
        if (properties.keySet().contains("itemList")) {
            concentricRingChart.setItems(((ObjectProperty<List<ChartItem>>) properties.get("itemList")).get());
        }

        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"          -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    concentricRingChart.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"           -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    concentricRingChart.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"           -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    concentricRingChart.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"         -> concentricRingChart.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"        -> concentricRingChart.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"          -> concentricRingChart.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"         -> concentricRingChart.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"          -> concentricRingChart.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"         -> concentricRingChart.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"            -> concentricRingChart.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"            -> concentricRingChart.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"           -> concentricRingChart.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"           -> concentricRingChart.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"        -> concentricRingChart.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"        -> concentricRingChart.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"           -> concentricRingChart.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "barBackgroundFill" -> concentricRingChart.setBarBackgroundFill(((ObjectProperty<Color>) properties.get(key)).get());
                case "sorted"            -> concentricRingChart.setSorted(((BooleanProperty) properties.get(key)).get());
                case "order"             -> concentricRingChart.setOrder(((ObjectProperty<Order>) properties.get(key)).get());
                case "numberFormat"      -> concentricRingChart.setNumberFormat(((ObjectProperty<NumberFormat>) properties.get(key)).get());
                case "itemLabelFill"     -> concentricRingChart.setItemLabelFill(((ObjectProperty<Color>) properties.get(key)).get());
                case "shortenNumbers"    -> concentricRingChart.setShortenNumbers(((BooleanProperty) properties.get(key)).get());
                case "valueVisible"      -> concentricRingChart.setValueVisible(((BooleanProperty) properties.get(key)).get());
            }
        }
        return concentricRingChart;
    }
}