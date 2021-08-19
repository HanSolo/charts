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

package eu.hansolo.fx.charts.voronoi;

import eu.hansolo.fx.charts.voronoi.VoronoiChart.Type;
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


public class VoronoiChartBuilder<B extends VoronoiChartBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected VoronoiChartBuilder() {}


    // ******************** Methods *******************************************
    public static final VoronoiChartBuilder create() {
        return new VoronoiChartBuilder();
    }

    public final B points(final List<VPoint> POINTS) {
        properties.put("points", new SimpleObjectProperty<>(POINTS));
        return (B)this;
    }

    public final B pointsVisible(final boolean VISIBLE) {
        properties.put("pointsVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B pointColor(final Color COLOR) {
        properties.put("pointColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B fillRegions(final boolean FILL) {
        properties.put("fillRegions", new SimpleBooleanProperty(FILL));
        return (B)this;
    }

    public final B borderColor(final Color COLOR) {
        properties.put("borderColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B type(final Type TYPE) {
        properties.put("type", new SimpleObjectProperty<>(TYPE));
        return (B)this;
    }

    public final B multiColor(final boolean MULTICOLOR) {
        properties.put("multiColor", new SimpleBooleanProperty(MULTICOLOR));
        return (B)this;
    }

    public final B voronoiColor(final Color COLOR) {
        properties.put("voronoiColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B delaunayColor(final Color COLOR) {
        properties.put("delaunayColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B interactive(final boolean INTERACTIVE) {
        properties.put("interactive", new SimpleBooleanProperty(INTERACTIVE));
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

    public final VoronoiChart build() {
        final VoronoiChart chart;

        if (properties.containsKey("points")) {
            chart = new VoronoiChart(((ObjectProperty<List<VPoint>>) properties.get("points")).get());
        } else {
            chart = new VoronoiChart();
        }

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
            } else if ("pointsVisible".equals(key)) {
                chart.setPointsVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("pointColor".equals(key)) {
                chart.setPointColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("fillRegions".equals(key)) {
                chart.setFillRegions(((BooleanProperty) properties.get(key)).get());
            } else if ("borderColor".equals(key)) {
                chart.setBorderColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("type".equals(key)) {
                chart.setType(((ObjectProperty<Type>) properties.get(key)).get());
            } else if ("multiColor".equals(key)) {
                chart.setMulticolor(((BooleanProperty) properties.get(key)).get());
            } else if ("voronoiColor".equals(key)) {
                chart.setVoronoiColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("delaunayColor".equals(key)) {
                chart.setDelaunayColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("interactive".equals(key)) {
                chart.setInteractive(((BooleanProperty) properties.get(key)).get());
            }
        }
        return chart;
    }
}
