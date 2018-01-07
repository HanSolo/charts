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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.paint.Paint;

import java.util.HashMap;


public class GridBuilder<B extends GridBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();
    private Axis xAxis;
    private Axis yAxis;


    // ******************** Constructors **************************************
    protected GridBuilder(final Axis X_AXIS, final Axis Y_AXIS) {
        xAxis = X_AXIS;
        yAxis = Y_AXIS;
    }


    // ******************** Methods *******************************************
    public static final GridBuilder create(final Axis X_AXIS, final Axis Y_AXIS) {
        return new GridBuilder(X_AXIS, Y_AXIS);
    }

    public final B majorHGridLinePaint(final Paint PAINT) {
        properties.put("majorHGridLinePaint", new SimpleObjectProperty<>(PAINT));
        return (B)this;
    }

    public final B mediumHGridLinePaint(final Paint PAINT) {
        properties.put("mediumHGridLinePaint", new SimpleObjectProperty<>(PAINT));
        return (B)this;
    }

    public final B minorHGridLinePaint(final Paint PAINT) {
        properties.put("minorHGridLinePaint", new SimpleObjectProperty<>(PAINT));
        return (B)this;
    }

    public final B majorVGridLinePaint(final Paint PAINT) {
        properties.put("majorVGridLinePaint", new SimpleObjectProperty<>(PAINT));
        return (B)this;
    }

    public final B mediumVGridLinePaint(final Paint PAINT) {
        properties.put("mediumVGridLinePaint", new SimpleObjectProperty<>(PAINT));
        return (B)this;
    }

    public final B minorVGridLinePaint(final Paint PAINT) {
        properties.put("minorVGridLinePaint", new SimpleObjectProperty<>(PAINT));
        return (B)this;
    }

    public final B gridLinePaint(final Paint PAINT) {
        properties.put("gridLinePaint", new SimpleObjectProperty<>(PAINT));
        return (B)this;
    }

    public final B majorHGridLinesVisible(final boolean VISIBLE) {
        properties.put("majorHGridLinesVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B mediumHGridLinesVisible(final boolean VISIBLE) {
        properties.put("mediumHGridLinesVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B minorHGridLinesVisible(final boolean VISIBLE) {
        properties.put("minorHGridLinesVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B majorVGridLinesVisible(final boolean VISIBLE) {
        properties.put("majorVGridLinesVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B mediumVGridLinesVisible(final boolean VISIBLE) {
        properties.put("mediumVGridLinesVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B minorVGridLinesVisible(final boolean VISIBLE) {
        properties.put("minorVGridLinesVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B gridOpacity(final double OPACITY) {
        properties.put("opacity", new SimpleDoubleProperty(OPACITY));
        return (B)this;
    }

    public final B gridLineDashes(final double... DASHES) {
        properties.put("dashesArray", new SimpleObjectProperty<>(DASHES));
        return (B)this;
    }

    // General properties
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


    public final Grid build() {
        final Grid CONTROL = new Grid(xAxis, yAxis);

        if (properties.keySet().contains("dashesArray")) {
            CONTROL.setGridLineDashes(((ObjectProperty<double[]>) properties.get("dashesArray")).get());
        }

        for (String key : properties.keySet()) {
            if ("prefSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if("minSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setMinSize(dim.getWidth(), dim.getHeight());
            } else if("maxSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setMaxSize(dim.getWidth(), dim.getHeight());
            } else if("prefWidth".equals(key)) {
                CONTROL.setPrefWidth(((DoubleProperty) properties.get(key)).get());
            } else if("prefHeight".equals(key)) {
                CONTROL.setPrefHeight(((DoubleProperty) properties.get(key)).get());
            } else if("minWidth".equals(key)) {
                CONTROL.setMinWidth(((DoubleProperty) properties.get(key)).get());
            } else if("minHeight".equals(key)) {
                CONTROL.setMinHeight(((DoubleProperty) properties.get(key)).get());
            } else if("maxWidth".equals(key)) {
                CONTROL.setMaxWidth(((DoubleProperty) properties.get(key)).get());
            } else if("maxHeight".equals(key)) {
                CONTROL.setMaxHeight(((DoubleProperty) properties.get(key)).get());
            } else if("scaleX".equals(key)) {
                CONTROL.setScaleX(((DoubleProperty) properties.get(key)).get());
            } else if("scaleY".equals(key)) {
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
              else if ("gridLinePaint".equals(key)) {
                CONTROL.setGridLinePaint(((ObjectProperty<Paint>) properties.get(key)).get());
            } else if ("majorHGridLinePaint".equals(key)) {
                CONTROL.setMajorHGridLinePaint(((ObjectProperty<Paint>) properties.get(key)).get());
            } else if ("mediumHGridLinePaint".equals(key)) {
                CONTROL.setMediumHGridLinePaint(((ObjectProperty<Paint>) properties.get(key)).get());
            } else if ("minorHGridLinePaint".equals(key)) {
                CONTROL.setMinorHGridLinePaint(((ObjectProperty<Paint>) properties.get(key)).get());
            } else if ("majorVGridLinePaint".equals(key)) {
                CONTROL.setMajorVGridLinePaint(((ObjectProperty<Paint>) properties.get(key)).get());
            } else if ("mediumVGridLinePaint".equals(key)) {
                CONTROL.setMediumVGridLinePaint(((ObjectProperty<Paint>) properties.get(key)).get());
            } else if ("minorVGridLinePaint".equals(key)) {
                CONTROL.setMinorVGridLinePaint(((ObjectProperty<Paint>) properties.get(key)).get());
            } else if ("majorHGridLinesVisible".equals(key)) {
                CONTROL.setMajorHGridLinesVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("mediumHGridLinesVisible".equals(key)) {
                CONTROL.setMediumHGridLinesVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("minorHGridLinesVisible".equals(key)) {
                CONTROL.setMinorHGridLinesVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("majorVGridLinesVisible".equals(key)) {
                CONTROL.setMajorVGridLinesVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("mediumVGridLinesVisible".equals(key)) {
                CONTROL.setMediumVGridLinesVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("minorVGridLinesVisible".equals(key)) {
                CONTROL.setMinorVGridLinesVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("gridOpacity".equals(key)) {
                CONTROL.setGridOpacity(((DoubleProperty) properties.get(key)).get());
            }
        }
        return CONTROL;
    }
}
