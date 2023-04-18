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

package eu.hansolo.fx.charts.data;

import eu.hansolo.fx.charts.Symbol;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.LinkedHashMap;


public class XYZChartItemBuilder<B extends XYZChartItemBuilder<B>> {
    private HashMap<String, Property> properties = new LinkedHashMap<>();


    // ******************** Constructors **************************************
    protected XYZChartItemBuilder() { }


    // ******************** Methods *******************************************
    public static final XYZChartItemBuilder create() {
        return new XYZChartItemBuilder();
    }

    public final B x(final double x) {
        properties.put("x", new SimpleDoubleProperty(x));
        return (B) this;
    }

    public final B y(final double y) {
        properties.put("y", new SimpleDoubleProperty(y));
        return (B) this;
    }

    public final B z(final double z) {
        properties.put("z", new SimpleDoubleProperty(z));
        return (B) this;
    }

    public final B name(final String name) {
        properties.put("name", new SimpleStringProperty(name));
        return (B) this;
    }

    public final B fill(final Color fill) {
        properties.put("fill", new SimpleObjectProperty<>(fill));
        return (B) this;
    }

    public final B stroke(final Color fill) {
        properties.put("stroke", new SimpleObjectProperty<>(fill));
        return (B) this;
    }

    public final B symbol(final Symbol symbol) {
        properties.put("symbol", new SimpleObjectProperty<>(symbol));
        return (B) this;
    }

    public final B isEmpty(final boolean isEmpty) {
        properties.put("isEmpty", new SimpleBooleanProperty(isEmpty));
        return (B) this;
    }


    public final XYZChartItem build() {
        final XYZChartItem item = new XYZChartItem();
        for (String key : properties.keySet()) {
            switch(key) {
                case "x"       -> item.setX(((DoubleProperty) properties.get(key)).get());
                case "y"       -> item.setY(((DoubleProperty) properties.get(key)).get());
                case "z"       -> item.setZ(((DoubleProperty) properties.get(key)).get());
                case "name"    -> item.setName(((StringProperty) properties.get(key)).get());
                case "fill"    -> item.setFill(((ObjectProperty<Color>) properties.get(key)).get());
                case "stroke"  -> item.setStroke(((ObjectProperty<Color>) properties.get(key)).get());
                case "isEmpty" -> item.setIsEmpty(((BooleanProperty) properties.get(key)).get());
            }
        }
        return item;
    }
}
