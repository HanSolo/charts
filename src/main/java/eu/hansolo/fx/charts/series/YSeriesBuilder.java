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

package eu.hansolo.fx.charts.series;

import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.Symbol;
import eu.hansolo.fx.charts.data.YItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.HashMap;
import java.util.List;


public class YSeriesBuilder<B extends YSeriesBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected YSeriesBuilder() {}


    // ******************** Methods *******************************************
    public static final YSeriesBuilder create() {
        return new YSeriesBuilder();
    }

    public final B items(final YItem... ITEMS) {
        properties.put("itemsArray", new SimpleObjectProperty<>(ITEMS));
        return (B)this;
    }

    public final B items(final List<YItem> ITEMS) {
        properties.put("itemsList", new SimpleObjectProperty<>(ITEMS));
        return (B)this;
    }

    public final B name(final String NAME) {
        properties.put("name", new SimpleStringProperty(NAME));
        return (B)this;
    }

    public final B fill(final Paint FILL) {
        properties.put("fill", new SimpleObjectProperty<>(FILL));
        return (B)this;
    }

    public final B stroke(final Paint STROKE) {
        properties.put("stroke", new SimpleObjectProperty<>(STROKE));
        return (B)this;
    }

    public final B symbolFill(final Color FILL) {
        properties.put("symbolFill", new SimpleObjectProperty<>(FILL));
        return (B)this;
    }

    public final B symbolStroke(final Color STROKE) {
        properties.put("symbolStroke", new SimpleObjectProperty<>(STROKE));
        return (B)this;
    }

    public final B symbol(final Symbol SYMBOL) {
        properties.put("symbol", new SimpleObjectProperty<>(SYMBOL));
        return (B)this;
    }

    public final B chartType(final ChartType TYPE) {
        properties.put("chartType", new SimpleObjectProperty<>(TYPE));
        return (B)this;
    }

    
    public final YSeries build() {
        final YSeries SERIES = new YSeries();

        if (properties.keySet().contains("itemsArray")) {
            SERIES.setItems(((ObjectProperty<YItem[]>) properties.get("itemsArray")).get());
        }
        if(properties.keySet().contains("itemsList")) {
            SERIES.setItems(((ObjectProperty<List<YItem>>) properties.get("itemsList")).get());
        }

        for (String key : properties.keySet()) {
            if ("name".equals(key)) {
                SERIES.setName(((StringProperty) properties.get(key)).get());
            } else if ("fill".equals(key)) {
                SERIES.setFill(((ObjectProperty<Paint>) properties.get(key)).get());
            } else if ("stroke".equals(key)) {
                SERIES.setStroke(((ObjectProperty<Paint>) properties.get(key)).get());
            } else if ("symbolFill".equals(key)) {
                SERIES.setSymbolFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("symbolStroke".equals(key)) {
                SERIES.setSymbolStroke(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("symbol".equals(key)) {
                SERIES.setSymbol(((ObjectProperty<Symbol>) properties.get(key)).get());
            } else if ("chartType".equals(key)) {
                SERIES.setChartType(((ObjectProperty<ChartType>) properties.get(key)).get());
            }
        }
        return SERIES;
    }
}
