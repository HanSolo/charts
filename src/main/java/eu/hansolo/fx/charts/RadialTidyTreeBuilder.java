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

import eu.hansolo.fx.charts.data.TreeNode;
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


public class RadialTidyTreeBuilder<B extends RadialTidyTreeBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected RadialTidyTreeBuilder() {}


    // ******************** Methods *******************************************
    public static final RadialTidyTreeBuilder create() {
        return new RadialTidyTreeBuilder();
    }

    public final B tree(final TreeNode TREE) {
        properties.put("tree", new SimpleObjectProperty(TREE));
        return (B)this;
    }

    public final B visibleData(final VisibleData VISIBLE_DATA) {
        properties.put("visibleData", new SimpleObjectProperty(VISIBLE_DATA));
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


    public final RadialTidyTree build() {
        final RadialTidyTree radialTidyTree;
        if (properties.containsKey("tree")) {
            radialTidyTree = new RadialTidyTree(((ObjectProperty<TreeNode>) properties.get("tree")).get());
        } else {
            radialTidyTree = new RadialTidyTree();
        }
        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"              -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    radialTidyTree.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"               -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    radialTidyTree.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"               -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    radialTidyTree.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"             -> radialTidyTree.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"            -> radialTidyTree.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"              -> radialTidyTree.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"             -> radialTidyTree.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"              -> radialTidyTree.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"             -> radialTidyTree.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"                -> radialTidyTree.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"                -> radialTidyTree.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"               -> radialTidyTree.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"               -> radialTidyTree.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"            -> radialTidyTree.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"            -> radialTidyTree.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"               -> radialTidyTree.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "visibleData"           -> radialTidyTree.setVisibleData(((ObjectProperty<VisibleData>) properties.get(key)).get());
                case "backgroundColor"       -> radialTidyTree.setBackgroundColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "textColor"             -> radialTidyTree.setTextColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "useColorFromParent"    -> radialTidyTree.setUseColorFromParent(((BooleanProperty) properties.get(key)).get());
                case "decimals"              -> radialTidyTree.setDecimals(((IntegerProperty) properties.get(key)).get());
                case "autoTextColor"         -> radialTidyTree.setAutoTextColor(((BooleanProperty) properties.get(key)).get());
                case "brightTextColor"       -> radialTidyTree.setBrightTextColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "darkTextColor"         -> radialTidyTree.setDarkTextColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "useChartItemTextColor" -> radialTidyTree.setUseChartItemTextColor(((BooleanProperty) properties.get(key)).get());
            }
        }
        return radialTidyTree;
    }
}
