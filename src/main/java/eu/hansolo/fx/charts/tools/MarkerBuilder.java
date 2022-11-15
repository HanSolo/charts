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

package eu.hansolo.fx.charts.tools;

import eu.hansolo.fx.charts.Axis;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.HashMap;


public class MarkerBuilder<B extends MarkerBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();
    private Axis                      axis;
    private double                    value;


    // ******************** Constructors **************************************
    protected MarkerBuilder(final Axis axis, final double value) {
        this.axis  = axis;
        this.value = value;
    }


    // ******************** Methods *******************************************
    public static final MarkerBuilder create(final Axis axis, final double value) {
        return new MarkerBuilder(axis, value);
    }


    public final B stroke(final Color stroke) {
        properties.put("stroke", new SimpleObjectProperty<>(stroke));
        return (B)this;
    }

    public final B lineWidth(final double lineWidth) {
        properties.put("lineWidth", new SimpleDoubleProperty(lineWidth));
        return (B)this;
    }

    public final B text(final String text) {
        properties.put("text", new SimpleStringProperty(text));
        return (B)this;
    }

    public final B textFill(final Color textFill) {
        properties.put("textFill", new SimpleObjectProperty<>(textFill));
        return (B)this;
    }

    public final B fromatString(final String formatString) {
        properties.put("formatString", new SimpleStringProperty(formatString));
        return (B)this;
    }

    public final B lineStyle(final LineStyle lineStyle) {
        properties.put("lineStyle", new SimpleObjectProperty<>(lineStyle));
        return (B)this;
    }

    public final Marker build() {
        final Marker marker = new Marker(axis, value);
        for (String key : properties.keySet()) {
            switch (key) {
                case "stroke"       -> marker.setStroke(((ObjectProperty<Color>) properties.get(key)).get());
                case "lineWidth"    -> marker.setLineWidth(((DoubleProperty) properties.get(key)).get());
                case "text"         -> marker.setText(((StringProperty) properties.get(key)).get());
                case "textFill"     -> marker.setTextFill(((ObjectProperty<Color>) properties.get(key)).get());
                case "formatString" -> marker.setFormatString(((StringProperty) properties.get(key)).get());
                case "lineStyle"    -> marker.setLineStyle(((ObjectProperty<LineStyle>) properties.get(key)).get());
            }
        }
        return marker;
    }
}
