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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;


public class SectorChartBuilder<B extends SectorChartBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected SectorChartBuilder() {}


    // ******************** Methods *******************************************
    public static final SectorChartBuilder create() {
        return new SectorChartBuilder();
    }

    public final B allSeries(final List<ChartItemSeries<ChartItem>> ALL_SERIES) {
        properties.put("allSeries", new SimpleObjectProperty<>(ALL_SERIES));
        return (B)this;
    }

    public final B itemTextVisible(final boolean VISIBLE) {
        properties.put("itemTextVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B seriesTextVisible(final boolean VISIBLE) {
        properties.put("seriesTextVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B seriesSumTextVisible(final boolean VISIBLE) {
        properties.put("seriesSumTextVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B thresholdVisible(final boolean VISIBLE) {
        properties.put("thresholdVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B threshold(final double THRESHOLD) {
        properties.put("threshold", new SimpleDoubleProperty(THRESHOLD));
        return (B)this;
    }

    public final B decimals(final int DECIMALS) {
        properties.put("decimals", new SimpleIntegerProperty(DECIMALS));
        return (B)this;
    }

    public final B gridColor(final Color COLOR) {
        properties.put("gridColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B thresholdColor(final Color COLOR) {
        properties.put("thresholdColor", new SimpleObjectProperty<>(COLOR));
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


    public final SectorChart build() {
        final SectorChart sectorChart;

        if (properties.containsKey("allSeries")) {
            sectorChart = new SectorChart(((ObjectProperty<List<ChartItemSeries<ChartItem>>>) properties.get("allSeries")).get());
        } else {
            sectorChart = new SectorChart();
        }

        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"             -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    sectorChart.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"              -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    sectorChart.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"              -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    sectorChart.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"            -> sectorChart.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"           -> sectorChart.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"             -> sectorChart.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"            -> sectorChart.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"             -> sectorChart.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"            -> sectorChart.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"               -> sectorChart.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"               -> sectorChart.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"              -> sectorChart.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"              -> sectorChart.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"           -> sectorChart.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"           -> sectorChart.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"              -> sectorChart.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "itemTextVisible"      -> sectorChart.setItemTextVisible(((BooleanProperty) properties.get(key)).get());
                case "seriesTextVisible"    -> sectorChart.setSeriesTextVisible(((BooleanProperty) properties.get(key)).get());
                case "seriesSumTextVisible" -> sectorChart.setSeriesSumTextVisible(((BooleanProperty) properties.get(key)).get());
                case "thresholdVisible"     -> sectorChart.setThresholdVisible(((BooleanProperty) properties.get(key)).get());
                case "threshold"            -> sectorChart.setThreshold(((DoubleProperty) properties.get(key)).get());
                case "decimals"             -> sectorChart.setDecimals(((IntegerProperty) properties.get(key)).get());
                case "gridColor"            -> sectorChart.setGridColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "thresholdColor"       -> sectorChart.setThresholdColor(((ObjectProperty<Color>) properties.get(key)).get());
            }
        }
        return sectorChart;
    }
}
