/*
 * Copyright (c) 2020 by Gerrit Grunwald
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

import eu.hansolo.fx.charts.data.PlotItem;
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


public class ArcChartBuilder<B extends ArcChartBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected ArcChartBuilder() {}


    // ******************** Methods *******************************************
    public static final ArcChartBuilder create() {
        return new ArcChartBuilder();
    }

    public final B items(final PlotItem... ITEMS) {
        properties.put("itemsArray", new SimpleObjectProperty<>(ITEMS));
        return (B)this;
    }

    public final B items(final List<PlotItem> ITEMS) {
        properties.put("itemsList", new SimpleObjectProperty<>(ITEMS));
        return (B)this;
    }

    public final B textColor(final Color COLOR) {
        properties.put("textColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B decimals(final int DECIMALS) {
        properties.put("decimals", new SimpleIntegerProperty(DECIMALS));
        return (B)this;
    }

    public final B connectionOpacity(final double OPACITY) {
        properties.put("connectionOpacity", new SimpleDoubleProperty(OPACITY));
        return (B)this;
    }

    public final B coloredConnections(final boolean COLORED) {
        properties.put("coloredConnections", new SimpleBooleanProperty(COLORED));
        return (B)this;
    }

    public final B connectionColor(final Color COLOR) {
        properties.put("connectionColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B selectionColor(final Color COLOR) {
        properties.put("selectionColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B sortByCluster(final boolean SORT) {
        properties.put("sortByCluster", new SimpleBooleanProperty(SORT));
        return (B)this;
    }

    public final B useFullCircle(final boolean USE) {
        properties.put("useFullCircle", new SimpleBooleanProperty(USE));
        return (B)this;
    }

    public final B weightConnections(final boolean WEIGHT) {
        properties.put("weightConnections", new SimpleBooleanProperty(WEIGHT));
        return (B)this;
    }

    public final B weightDots(final boolean WEIGHT) {
        properties.put("weightDots", new SimpleBooleanProperty(WEIGHT));
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

    
    public final ArcChart build() {
        final ArcChart arcChart = new ArcChart();

        if (properties.keySet().contains("itemsArray")) {
            arcChart.setItems(((ObjectProperty<PlotItem[]>) properties.get("itemsArray")).get());
        }
        if(properties.keySet().contains("itemsList")) {
            arcChart.setItems(((ObjectProperty<List<PlotItem>>) properties.get("itemsList")).get());
        }

        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"           -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    arcChart.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"            -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    arcChart.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"            -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    arcChart.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"          -> arcChart.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"         -> arcChart.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"           -> arcChart.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"          -> arcChart.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"           -> arcChart.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"          -> arcChart.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"             -> arcChart.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"             -> arcChart.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"            -> arcChart.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"            -> arcChart.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"         -> arcChart.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"         -> arcChart.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"            -> arcChart.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "tickMarkColor"      -> arcChart.setTickMarkColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "textColor"          -> arcChart.setTextColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "decimals"           -> arcChart.setDecimals(((IntegerProperty) properties.get(key)).get());
                case "connectionOpactiy"  -> arcChart.setConnectionOpacity(((DoubleProperty) properties.get(key)).get());
                case "coloredConnections" -> arcChart.setColoredConnections(((BooleanProperty) properties.get(key)).get());
                case "connectionColor"    -> arcChart.setConnectionColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "selectionColor"     -> arcChart.setSelectionColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "sortByCluster"      -> arcChart.setSortByCluster(((BooleanProperty) properties.get(key)).get());
                case "useFullCircle"      -> arcChart.setUseFullCircle(((BooleanProperty) properties.get(key)).get());
                case "weightConnections"  -> arcChart.setWeightConnections(((BooleanProperty) properties.get(key)).get());
                case "weightDots"         -> arcChart.setWeightDots(((BooleanProperty) properties.get(key)).get());
            }
        }
        return arcChart;
    }
}
