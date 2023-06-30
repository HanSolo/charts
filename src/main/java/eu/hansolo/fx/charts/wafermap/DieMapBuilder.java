/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2023 Gerrit Grunwald.
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

package eu.hansolo.fx.charts.wafermap;

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
import java.util.Map;


public class DieMapBuilder <B extends DieMapBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected DieMapBuilder() { }


    // ******************** Methods *******************************************
    public static final DieMapBuilder create() {
        return new DieMapBuilder();
    }


    public final B die(final Die die) {
        properties.put("die", new SimpleObjectProperty<>(die));
        return (B)this;
    }

    public final B dieFill(final Color dieFill) {
        properties.put("dieFill", new SimpleObjectProperty<>(dieFill));
        return (B)this;
    }

    public final B dieStroke(final Color dieStroke) {
        properties.put("dieStroke", new SimpleObjectProperty<>(dieStroke));
        return (B)this;
    }

    public final B defectFill(final Color defectFill) {
        properties.put("defectFill", new SimpleObjectProperty<>(defectFill));
        return (B)this;
    }

    public final B defectStroke(final Color defectStroke) {
        properties.put("defectStroke", new SimpleObjectProperty<>(defectStroke));
        return (B)this;
    }

    public final B dieTextFill(final Color dieTextFill) {
        properties.put("dieTextFill", new SimpleObjectProperty<>(dieTextFill));
        return (B)this;
    }

    public final B dieTextVisible(final boolean dieTextVisible) {
        properties.put("dieTextVisible", new SimpleBooleanProperty(dieTextVisible));
        return (B)this;
    }

    public final B densityColorsVisible(final boolean densityColorsVisible) {
        properties.put("densityColorsVisible", new SimpleBooleanProperty(densityColorsVisible));
        return (B)this;
    }

    public final B classConfigMap(final Map<Integer, ClassConfig> classConfigMap) {
        properties.put("classConfigMap", new SimpleObjectProperty<>(classConfigMap));
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


    public final DieMap build() {
        final DieMap dieMap = new DieMap();
        
        if (properties.keySet().contains("die")) { dieMap.setDie(((ObjectProperty<Die>) properties.get("die")).get()); }

        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"             -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    dieMap.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"              -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    dieMap.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"              -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    dieMap.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"            -> dieMap.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"           -> dieMap.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"             -> dieMap.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"            -> dieMap.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"             -> dieMap.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"            -> dieMap.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"               -> dieMap.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"               -> dieMap.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"              -> dieMap.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"              -> dieMap.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"           -> dieMap.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"           -> dieMap.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"              -> dieMap.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());

                case "waferFill"            -> dieMap.setDieFill(((ObjectProperty<Color>) properties.get(key)).get());
                case "waferStroke"          -> dieMap.setDieStroke(((ObjectProperty<Color>) properties.get(key)).get());
                case "defectFill"           -> dieMap.setDefectFill(((ObjectProperty<Color>) properties.get(key)).get());
                case "defectStroke"         -> dieMap.setDefectStroke(((ObjectProperty<Color>) properties.get(key)).get());
                case "dieTextFill"          -> dieMap.setDieTextFill(((ObjectProperty<Color>) properties.get(key)).get());
                case "dieTextVisible"       -> dieMap.setDieTextVisible(((BooleanProperty) properties.get(key)).get());
                case "densityColorsVisible" -> dieMap.setDensityColorsVisible(((BooleanProperty) properties.get(key)).get());
                case "classConfigMap"       -> dieMap.setClassConfigMap(((ObjectProperty<Map<Integer, ClassConfig>>) properties.get(key)).get());
            }
        }
        return dieMap;
    }
}
