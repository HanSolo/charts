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

import eu.hansolo.fx.charts.PixelMatrix.PixelShape;
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


public class PixelMatrixBuilder<B extends PixelMatrixBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected PixelMatrixBuilder() {}


    // ******************** Methods *******************************************
    public static final PixelMatrixBuilder create() {
        return new PixelMatrixBuilder();
    }

    public final B colsAndRows(final int[] COLS_AND_ROWS) { return colsAndRows(COLS_AND_ROWS[0], COLS_AND_ROWS[1]); }
    public final B colsAndRows(final int COLS, final int ROWS) {
        properties.put("cols", new SimpleIntegerProperty(COLS));
        properties.put("rows", new SimpleIntegerProperty(ROWS));
        return (B)this;
    }

    public final B pixelOnColor(final Color COLOR) {
        properties.put("pixelOnColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }
    public final B pixelOffColor(final Color COLOR) {
        properties.put("pixelOffColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B pixelShape(final PixelShape SHAPE) {
        properties.put("pixelShape", new SimpleObjectProperty(SHAPE));
        return (B)this;
    }

    public final B useSpacer(final boolean USE) {
        properties.put("useSpacer", new SimpleBooleanProperty(USE));
        return (B)this;
    }

    public final B squarePixels(final boolean SQUARE) {
        properties.put("squarePixels", new SimpleBooleanProperty(SQUARE));
        return (B)this;
    }

    public final B spacerSizeFactor(final double FACTOR) {
        properties.put("spacerSizeFactor", new SimpleDoubleProperty(FACTOR));
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


    public final PixelMatrix build() {
        final PixelMatrix pixelMatrix;
        if (properties.keySet().contains("cols") && properties.keySet().contains("rows")) {
            int cols = ((IntegerProperty) properties.get("cols")).get();
            int rows = ((IntegerProperty) properties.get("rows")).get();
            pixelMatrix = new PixelMatrix(cols, rows);
        } else {
            pixelMatrix = new PixelMatrix();
        }

        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"         -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    pixelMatrix.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"          -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    pixelMatrix.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"          -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    pixelMatrix.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"        -> pixelMatrix.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"       -> pixelMatrix.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"         -> pixelMatrix.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"        -> pixelMatrix.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"         -> pixelMatrix.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"        -> pixelMatrix.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"           -> pixelMatrix.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"           -> pixelMatrix.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"          -> pixelMatrix.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"          -> pixelMatrix.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"       -> pixelMatrix.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"       -> pixelMatrix.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"          -> pixelMatrix.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "pixelOnColor"     -> pixelMatrix.setPixelOnColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "pixelOffColor"    -> pixelMatrix.setPixelOffColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "pixelShape"       -> pixelMatrix.setPixelShape(((ObjectProperty<PixelShape>) properties.get(key)).get());
                case "useSpacer"        -> pixelMatrix.setUseSpacer(((BooleanProperty) properties.get(key)).get());
                case "spacerSizeFactor" -> pixelMatrix.setSpacerSizeFactor(((DoubleProperty) properties.get(key)).get());
                case "squarePixels"     -> pixelMatrix.setSquarePixels(((BooleanProperty) properties.get(key)).get());
            }
        }
        return pixelMatrix;
    }
}