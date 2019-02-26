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

import eu.hansolo.fx.charts.data.DataObject;
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
import java.util.Locale;


/**
 * User: hansolo
 * Date: 31.01.18
 * Time: 15:49
 */
public class ParallelCoordinatesChartBuilder<B extends ParallelCoordinatesChartBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected ParallelCoordinatesChartBuilder() {}


    // ******************** Methods *******************************************
    public static final ParallelCoordinatesChartBuilder create() {
        return new ParallelCoordinatesChartBuilder();
    }

    public final B items(final DataObject... ITEMS) {
        properties.put("itemArray", new SimpleObjectProperty<>(ITEMS));
        return (B) this;
    }

    public final B items(final List<DataObject> ITEMS) {
        properties.put("itemList", new SimpleObjectProperty<>(ITEMS));
        return (B) this;
    }

    public final B axisColor(final Color COLOR) {
        properties.put("axisColor", new SimpleObjectProperty<>(COLOR));
        return (B) this;
    }

    public final B headerColor(final Color COLOR) {
        properties.put("headerColor", new SimpleObjectProperty<>(COLOR));
        return (B) this;
    }

    public final B unitColor(final Color COLOR) {
        properties.put("unitColor", new SimpleObjectProperty<>(COLOR));
        return (B) this;
    }

    public final B tickLabelColor(final Color COLOR) {
        properties.put("tickLabelColor", new SimpleObjectProperty<>(COLOR));
        return (B) this;
    }

    public final B locale(final Locale LOCALE) {
        properties.put("locale", new SimpleObjectProperty<>(LOCALE));
        return (B)this;
    }

    public final B decimals(final int DECIMALS) {
        properties.put("decimals", new SimpleIntegerProperty(DECIMALS));
        return (B)this;
    }

    public final B tickMarksVisible(final boolean VISIBLE) {
        properties.put("tickMarksVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B selectedColor(final Color COLOR) {
        properties.put("selectedColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B unselectedColor(final Color COLOR) {
        properties.put("unselectedColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B selectionRectColor(final Color COLOR) {
        properties.put("selectionRectColor", new SimpleObjectProperty<>(COLOR));
        return (B) this;
    }

    public final B smoothConnections(final boolean SMOOTH) {
        properties.put("smoothConnections", new SimpleBooleanProperty(SMOOTH));
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


    public final ParallelCoordinatesChart build() {
        final ParallelCoordinatesChart CONTROL = new ParallelCoordinatesChart();

        if (properties.keySet().contains("itemArray")) {
            CONTROL.setItems(((ObjectProperty<DataObject[]>) properties.get("itemArray")).get());
        }
        if (properties.keySet().contains("itemList")) {
            CONTROL.setItems(((ObjectProperty<List<DataObject>>) properties.get("itemList")).get());
        }

        for (String key : properties.keySet()) {
            if ("prefSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if ("minSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setMinSize(dim.getWidth(), dim.getHeight());
            } else if ("maxSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setMaxSize(dim.getWidth(), dim.getHeight());
            } else if ("prefWidth".equals(key)) {
                CONTROL.setPrefWidth(((DoubleProperty) properties.get(key)).get());
            } else if ("prefHeight".equals(key)) {
                CONTROL.setPrefHeight(((DoubleProperty) properties.get(key)).get());
            } else if ("minWidth".equals(key)) {
                CONTROL.setMinWidth(((DoubleProperty) properties.get(key)).get());
            } else if ("minHeight".equals(key)) {
                CONTROL.setMinHeight(((DoubleProperty) properties.get(key)).get());
            } else if ("maxWidth".equals(key)) {
                CONTROL.setMaxWidth(((DoubleProperty) properties.get(key)).get());
            } else if ("maxHeight".equals(key)) {
                CONTROL.setMaxHeight(((DoubleProperty) properties.get(key)).get());
            } else if ("scaleX".equals(key)) {
                CONTROL.setScaleX(((DoubleProperty) properties.get(key)).get());
            } else if ("scaleY".equals(key)) {
                CONTROL.setScaleY(((DoubleProperty) properties.get(key)).get());
            } else if ("layoutX".equals(key)) {
                CONTROL.setLayoutX(((DoubleProperty) properties.get(key)).get());
            } else if ("layoutY".equals(key)) {
                CONTROL.setLayoutY(((DoubleProperty) properties.get(key)).get());
            } else if ("translateX".equals(key)) {
                CONTROL.setTranslateX(((DoubleProperty) properties.get(key)).get());
            } else if ("translateY".equals(key)) {
                CONTROL.setTranslateY(((DoubleProperty) properties.get(key)).get());
            } else if ("padding".equals(key)) {
                CONTROL.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
            } // Control specific properties
            else if ("axisColor".equals(key)) {
                CONTROL.setAxisColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("headerColor".equals(key)) {
                CONTROL.setHeaderColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("unitColor".equals(key)) {
                CONTROL.setUnitColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("tickLabelColor".equals(key)) {
                CONTROL.setTickLabelColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("locale".equals(key)) {
                CONTROL.setLocale(((ObjectProperty<Locale>) properties.get(key)).get());
            } else if ("decimals".equals(key)) {
                CONTROL.setDecimals(((IntegerProperty) properties.get(key)).get());
            } else if ("tickMarksVisible".equals(key)) {
                CONTROL.setTickMarksVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("selectedColor".equals(key)) {
                CONTROL.setSelectedColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("unselectedColor".equals(key)) {
                CONTROL.setUnselectedColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("selectionRectColor".equals(key)) {
                CONTROL.setSelectionRectColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("smoothConnections".equals(key)) {
                CONTROL.setSmoothConnections(((BooleanProperty) properties.get(key)).get());
            }
        }
        return CONTROL;
    }
}
