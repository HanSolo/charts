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


public class BubbleGridChartItemBuilder<B extends BubbleGridChartItemBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected BubbleGridChartItemBuilder() {}


    // ******************** Methods *******************************************
    public static final BubbleGridChartItemBuilder create() {
        return new BubbleGridChartItemBuilder();
    }

    public final B name(final String NAME) {
        properties.put("name", new SimpleStringProperty(NAME));
        return (B)this;
    }

    public final B value(final double VALUE) {
        properties.put("value", new SimpleDoubleProperty(VALUE));
        return (B)this;
    }

    public final B fill(final Color COLOR) {
        properties.put("fill", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B stroke(final Color COLOR) {
        properties.put("stroke", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B symbol(final Symbol SYMBOL) {
        properties.put("symbol", new SimpleObjectProperty<>(SYMBOL));
        return (B)this;
    }

    public final B categoryXItem(final ChartItem categoryX) {
        properties.put("categoryX", new SimpleObjectProperty<>(categoryX));
        return (B)this;
    }

    public final B categoryYItem(final ChartItem categoryY) {
        properties.put("categoryY", new SimpleObjectProperty<>(categoryY));
        return (B)this;
    }

    public final B isEmpty() {
        properties.put("isEmpty", new SimpleBooleanProperty(true));
        return (B)this;
    }

    public final BubbleGridChartItem build() {
        final BubbleGridChartItem ITEM = new BubbleGridChartItem();
        for (String key : properties.keySet()) {
            if ("name".equals(key)) {
                ITEM.setName(((StringProperty) properties.get(key)).get());
            } else if ("value".equals(key)) {
                ITEM.setValue(((DoubleProperty) properties.get(key)).get());
            } else if("fill".equals(key)) {
                ITEM.setFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("stroke".equals(key)) {
                ITEM.setStroke(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("symbol".equals(key)) {
                ITEM.setSymbol(((ObjectProperty<Symbol>) properties.get(key)).get());
            } else if("categoryX".equals(key)) {
                ITEM.setCategoryX(((ObjectProperty<ChartItem>) properties.get(key)).get());
            } else if("categoryY".equals(key)) {
                ITEM.setCategoryY(((ObjectProperty<ChartItem>) properties.get(key)).get());
            } else if ("isEmpty".equals(key)) {
                ITEM.setIsEmpty(((BooleanProperty) properties.get(key)).get());
            }
        }
        return ITEM;
    }
}
