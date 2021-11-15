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

package eu.hansolo.fx.charts.panelbarchart;

import eu.hansolo.fx.charts.Category;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class PanelBarChartBuilder<B extends PanelBarChartBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();
    private List<? extends Category>  categories;


    // ******************** Constructors **************************************
    protected PanelBarChartBuilder(final List<? extends Category> categories) {
        this.categories = categories;
    }


    // ******************** Methods *******************************************
    public static final PanelBarChartBuilder create(List<? extends Category> categories) {
        if (null == categories) { throw new IllegalArgumentException("categories cannot be null"); }
        return new PanelBarChartBuilder(categories);
    }


    public final B series(final ChartItemSeries<ChartItem>... SERIES) {
        return series(Arrays.asList(SERIES));
    }

    public final B series(final List<ChartItemSeries<ChartItem>> SERIES) {
        properties.put("chartItemSeriesList", new SimpleObjectProperty<>(SERIES));
        return (B)this;
    }

    public final B chartBackground(final Color COLOR) {
        properties.put("chartBackground", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B categoryNameColor(final Color COLOR) {
        properties.put("categoryNameColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B categorySumColor(final Color COLOR) {
        properties.put("categorySumColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B seriesNameColor(final Color COLOR) {
        properties.put("seriesNameColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B seriesSumColor(final Color COLOR) {
        properties.put("seriesNameColor", new SimpleObjectProperty<>(COLOR));
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

    public final PanelBarChart build() {
        List<ChartItemSeries<ChartItem>> series = new ArrayList<>();
        if(properties.keySet().contains("chartItemSeriesList")) {
            series.addAll(((ObjectProperty<List<ChartItemSeries<ChartItem>>>) properties.get("chartItemSeriesList")).get());
        }

        final PanelBarChart chart = new PanelBarChart(categories, series);

        for (String key : properties.keySet()) {
            if ("prefSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                chart.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if("minSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                chart.setMinSize(dim.getWidth(), dim.getHeight());
            } else if("maxSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                chart.setMaxSize(dim.getWidth(), dim.getHeight());
            } else if("prefWidth".equals(key)) {
                chart.setPrefWidth(((DoubleProperty) properties.get(key)).get());
            } else if("prefHeight".equals(key)) {
                chart.setPrefHeight(((DoubleProperty) properties.get(key)).get());
            } else if("minWidth".equals(key)) {
                chart.setMinWidth(((DoubleProperty) properties.get(key)).get());
            } else if("minHeight".equals(key)) {
                chart.setMinHeight(((DoubleProperty) properties.get(key)).get());
            } else if("maxWidth".equals(key)) {
                chart.setMaxWidth(((DoubleProperty) properties.get(key)).get());
            } else if("maxHeight".equals(key)) {
                chart.setMaxHeight(((DoubleProperty) properties.get(key)).get());
            } else if("scaleX".equals(key)) {
                chart.setScaleX(((DoubleProperty) properties.get(key)).get());
            } else if("scaleY".equals(key)) {
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
            } else if ("chartBackground".equals(key)) {
                chart.setChartBackground(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("categoryNameColor".equals(key)) {
                chart.setCategoryNameColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("categorySumColor".equals(key)) {
                chart.setCategorySumColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("seriesNameColor".equals(key)) {
                chart.setSeriesNameColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("seriesSumColor".equals(key)) {
                chart.setSeriesSumColor(((ObjectProperty<Color>) properties.get(key)).get());
            }
        }
        return chart;
    }
}
