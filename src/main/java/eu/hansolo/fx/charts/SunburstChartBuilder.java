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


import eu.hansolo.fx.charts.data.TreeNode;
import eu.hansolo.fx.charts.tools.TextOrientation;
import eu.hansolo.fx.charts.tools.VisibleData;
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


public class SunburstChartBuilder<B extends SunburstChartBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected SunburstChartBuilder() {}


    // ******************** Methods *******************************************
    public static final SunburstChartBuilder create() {
        return new SunburstChartBuilder();
    }

    public final B tree(final TreeNode TREE) {
        properties.put("tree", new SimpleObjectProperty(TREE));
        return (B)this;
    }

    public final B visibleData(final VisibleData VISIBLE_DATA) {
        properties.put("visibleData", new SimpleObjectProperty(VISIBLE_DATA));
        return (B)this;
    }

    public final B textOrientation(final TextOrientation ORIENTATION) {
        properties.put("textOrientation", new SimpleObjectProperty(ORIENTATION));
        return (B)this;
    }

    public final B backgroundColor(final Color COLOR) {
        properties.put("backgroundColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B textColor(final Color COLOR) {
        properties.put("textColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B useColorFromParent(final boolean USE) {
        properties.put("useColorFromParent", new SimpleBooleanProperty(USE));
        return (B)this;
    }

    public final B decimals(final int DECIMALS) {
        properties.put("decimals", new SimpleIntegerProperty(DECIMALS));
        return (B)this;
    }

    public final B interactive(final boolean INTERACTIVE) {
        properties.put("interactive", new SimpleBooleanProperty(INTERACTIVE));
        return (B)this;
    }

    public final B autoTextColor(final boolean AUTOMATIC) {
        properties.put("autoTextColor", new SimpleBooleanProperty(AUTOMATIC));
        return (B)this;
    }

    public final B brightTextColor(final Color COLOR) {
        properties.put("brightTextColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B darkTextColor(final Color COLOR) {
        properties.put("darkTextColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B useChartItemTextColor(final boolean USE) {
        properties.put("useChartItemTextColor", new SimpleBooleanProperty(USE));
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


    public final SunburstChart build() {
        final SunburstChart sunburstChart;
        if (properties.containsKey("tree")) {
            sunburstChart = new SunburstChart(((ObjectProperty<TreeNode>) properties.get("tree")).get());
        } else {
            sunburstChart = new SunburstChart();
        }
        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"              -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    sunburstChart.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"               -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    sunburstChart.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"               -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    sunburstChart.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"             -> sunburstChart.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"            -> sunburstChart.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"              -> sunburstChart.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"             -> sunburstChart.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"              -> sunburstChart.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"             -> sunburstChart.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"                -> sunburstChart.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"                -> sunburstChart.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"               -> sunburstChart.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"               -> sunburstChart.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"            -> sunburstChart.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"            -> sunburstChart.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"               -> sunburstChart.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "visibleData"           -> sunburstChart.setVisibleData(((ObjectProperty<VisibleData>) properties.get(key)).get());
                case "textOrientation"       -> sunburstChart.setTextOrientation(((ObjectProperty<TextOrientation>) properties.get(key)).get());
                case "backgroundColor"       -> sunburstChart.setBackgroundColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "textColor"             -> sunburstChart.setTextColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "useColorFromParent"    -> sunburstChart.setUseColorFromParent(((BooleanProperty) properties.get(key)).get());
                case "decimals"              -> sunburstChart.setDecimals(((IntegerProperty) properties.get(key)).get());
                case "interactive"           -> sunburstChart.setInteractive(((BooleanProperty) properties.get(key)).get());
                case "autoTextColor"         -> sunburstChart.setAutoTextColor(((BooleanProperty) properties.get(key)).get());
                case "brightTextColor"       -> sunburstChart.setBrightTextColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "darkTextColor"         -> sunburstChart.setDarkTextColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "useChartItemTextColor" -> sunburstChart.setUseChartItemTextColor(((BooleanProperty) properties.get(key)).get());
            }
        }
        return sunburstChart;
    }
}