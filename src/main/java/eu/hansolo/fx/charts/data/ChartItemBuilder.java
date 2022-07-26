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

package eu.hansolo.fx.charts.data;

import eu.hansolo.fx.charts.Symbol;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashMap;


public class ChartItemBuilder<B extends ChartItemBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected ChartItemBuilder() {}


    // ******************** Methods *******************************************
    public static final ChartItemBuilder create() {
        return new ChartItemBuilder();
    }

    public final B index(final int INDEX) {
        properties.put("index", new SimpleIntegerProperty(INDEX));
        return (B)this;
    }

    public final B name(final String NAME) {
        properties.put("name", new SimpleStringProperty(NAME));
        return (B)this;
    }

    public final B unit(final String UNIT) {
        properties.put("unit", new SimpleStringProperty(UNIT));
        return (B)this;
    }

    public final B description(final String DESCRIPTION) {
        properties.put("description", new SimpleStringProperty(DESCRIPTION));
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

    public final B textFill(final Color COLOR) {
        properties.put("textFill", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B timestamp(final ZonedDateTime DATE_TIME) {
        properties.put("timestampDateTime", new SimpleObjectProperty<>(DATE_TIME));
        return (B)this;
    }

    public final B timestamp(final Instant TIMESTAMP) {
        properties.put("timestamp", new SimpleObjectProperty<>(TIMESTAMP));
        return (B)this;
    }

    public final B symbol(final Symbol SYMBOL) {
        properties.put("symbol", new SimpleObjectProperty<>(SYMBOL));
        return (B)this;
    }

    public final B animated(final boolean AUTO) {
        properties.put("animated", new SimpleBooleanProperty(AUTO));
        return (B)this;
    }

    public final B x(final double X) {
        properties.put("x", new SimpleDoubleProperty(X));
        return (B)this;
    }

    public final B y(final double Y) {
        properties.put("y", new SimpleDoubleProperty(Y));
        return (B)this;
    }

    public final B isEmpty() {
        properties.put("isEmpty", new SimpleBooleanProperty(true));
        return (B)this;
    }

    public final B metadata(final Metadata METADATA) {
        properties.put("metadata", new SimpleObjectProperty<>(METADATA));
        return (B)this;
    }

    public final B animationDuration(final long DURATION) {
        properties.put("animationDuration", new SimpleLongProperty(DURATION));
        return (B)this;
    }

    public final ChartItem build() {
        final ChartItem ITEM = new ChartItem();
        for (String key : properties.keySet()) {
            if ("index".equals(key)) {
                ITEM.setIndex(((IntegerProperty) properties.get(key)).get());
            } else if ("name".equals(key)) {
                ITEM.setName(((StringProperty) properties.get(key)).get());
            } else if ("value".equals(key)) {
                ITEM.setValue(((DoubleProperty) properties.get(key)).get());
            } else if ("unit".equals(key)) {
                ITEM.setUnit(((StringProperty) properties.get(key)).get());
            } else if ("description".equals(key)) {
                ITEM.setDescription(((StringProperty) properties.get(key)).get());
            } else if("fill".equals(key)) {
                ITEM.setFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("stroke".equals(key)) {
                ITEM.setStroke(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("textFill".equals(key)) {
                ITEM.setTextFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("timestamp".equals(key)) {
                ITEM.setTimestamp(((ObjectProperty<Instant>) properties.get(key)).get());
            } else if ("timestampDateTime".equals(key)) {
                ITEM.setTimestamp(((ObjectProperty<ZonedDateTime>) properties.get(key)).get());
            } else if("symbol".equals(key)) {
                ITEM.setSymbol(((ObjectProperty<Symbol>) properties.get(key)).get());
            } else if("animated".equals(key)) {
                ITEM.setAnimated(((BooleanProperty) properties.get(key)).get());
            } else if("animationDuration".equals(key)) {
                ITEM.setAnimationDuration(((LongProperty) properties.get(key)).get());
            } else if ("x".equals(key)) {
                ITEM.setX(((DoubleProperty) properties.get(key)).get());
            } else if ("y".equals(key)) {
                ITEM.setY(((DoubleProperty) properties.get(key)).get());
            } else if ("isEmpty".equals(key)) {
                ITEM.setIsEmpty(((BooleanProperty) properties.get(key)).get());
            } else if ("metadata".equals(key)) {
                ITEM.setMetadata(((ObjectProperty<Metadata>) properties.get(key)).get());
            }
        }
        return ITEM;
    }
}
