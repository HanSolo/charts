/*
 * Copyright (c) 2017 by Gerrit Grunwald
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
import eu.hansolo.fx.charts.series.ChartItemSeries;
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
import java.util.List;


public class NestedBarChartBuilder<B extends NestedBarChartBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected NestedBarChartBuilder() {}


    // ******************** Methods *******************************************
    public static final NestedBarChartBuilder create() {
        return new NestedBarChartBuilder();
    }

    public final B series(final ChartItemSeries<ChartItem>... SERIES) {
        properties.put("seriesArray", new SimpleObjectProperty<>(SERIES));
        return (B)this;
    }

    public final B series(final List<ChartItemSeries<ChartItem>> SERIES) {
        properties.put("seriesList", new SimpleObjectProperty<>(SERIES));
        return (B)this;
    }

    public final B order(final Order ORDER) {
        properties.put("order", new SimpleObjectProperty<>(ORDER));
        return (B)this;
    }

    public final B chartBackground(final Paint BACKGROUND) {
        properties.put("chartBackground", new SimpleObjectProperty<>(BACKGROUND));
        return (B)this;
    }

    public final B spacer(final double SPACER) {
        properties.put("spacer", new SimpleDoubleProperty(SPACER));
        return (B)this;
    }

    public final B seriesTitleVisible(final boolean VISIBLE) {
        properties.put("seriesTitleVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B seriesTitleColor(final Color COLOR) {
        properties.put("seriesTitleColor", new SimpleObjectProperty<>(COLOR));
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


    public final NestedBarChart build() {
        final NestedBarChart nestedBarChart = new NestedBarChart();

        if (properties.keySet().contains("seriesArray")) {
            nestedBarChart.setSeries(((ObjectProperty<ChartItemSeries<ChartItem>[]>) properties.get("seriesArray")).get());
        }
        if(properties.keySet().contains("seriesList")) {
            nestedBarChart.setSeries(((ObjectProperty<List<ChartItemSeries<ChartItem>>>) properties.get("seriesList")).get());
        }

        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"           -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    nestedBarChart.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"            -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    nestedBarChart.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"            -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    nestedBarChart.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"          -> nestedBarChart.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"         -> nestedBarChart.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"           -> nestedBarChart.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"          -> nestedBarChart.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"           -> nestedBarChart.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"          -> nestedBarChart.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"             -> nestedBarChart.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"             -> nestedBarChart.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"            -> nestedBarChart.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"            -> nestedBarChart.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"         -> nestedBarChart.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"         -> nestedBarChart.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"            -> nestedBarChart.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "order"              -> nestedBarChart.setOrder(((ObjectProperty<Order>) properties.get(key)).get());
                case "chartBackground"    -> nestedBarChart.setChartBackground(((ObjectProperty<Paint>) properties.get(key)).get());
                case "spacer"             -> nestedBarChart.setSpacer(((DoubleProperty) properties.get(key)).get());
                case "seriesTitleColor"   -> nestedBarChart.setSeriesTitleColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "seriesTitleVisible" -> nestedBarChart.setSeriesTitleVisible(((BooleanProperty) properties.get(key)).get());
            }
        }
        return nestedBarChart;
    }
}
