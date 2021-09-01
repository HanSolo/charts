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

import eu.hansolo.fx.charts.StreamChart.Category;
import eu.hansolo.fx.charts.StreamChart.Type;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.tools.SortDirection;
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


public class StreamChartBuilder<B extends StreamChartBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected StreamChartBuilder() {}


    // ******************** Methods *******************************************
    public static final StreamChartBuilder create() {
        return new StreamChartBuilder();
    }

    public final B category(final Category CATEGORY) {
        properties.put("category", new SimpleObjectProperty<>(CATEGORY));
        return (B)this;
    }

    public final B type(final Type TYPE) {
        properties.put("type", new SimpleObjectProperty<>(TYPE));
        return (B)this;
    }

    public final B items(final ChartItem... ITEMS) {
        properties.put("itemsArray", new SimpleObjectProperty<>(ITEMS));
        return (B)this;
    }

    public final B items(final List<ChartItem> ITEMS) {
        properties.put("itemsList", new SimpleObjectProperty<>(ITEMS));
        return (B)this;
    }

    public final B textColor(final Color COLOR) {
        properties.put("textColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B autoTextColor(final boolean AUTO) {
        properties.put("autoTextColor", new SimpleBooleanProperty(AUTO));
        return (B)this;
    }

    public final B decimals(final int DECIMALS) {
        properties.put("decimals", new SimpleIntegerProperty(DECIMALS));
        return (B)this;
    }

    public final B itemWidth(final int WIDTH) {
        properties.put("itemWidth", new SimpleIntegerProperty(WIDTH));
        return (B)this;
    }

    public final B autoItemWidth(final boolean AUTO) {
        properties.put("autoItemWidth", new SimpleBooleanProperty(AUTO));
        return (B)this;
    }

    public final B itemGap(final int GAP) {
        properties.put("itemGap", new SimpleIntegerProperty(GAP));
        return (B)this;
    }

    public final B autoItemGap(final boolean AUTO) {
        properties.put("autoItemGap", new SimpleBooleanProperty(AUTO));
        return (B)this;
    }

    public final B locale(final Locale LOCALE) {
        properties.put("locale", new SimpleObjectProperty<>(LOCALE));
        return (B)this;
    }

    public final B itemTextThreshold(final double THRESHOLD) {
        properties.put("itemTextThreshold", new SimpleDoubleProperty(THRESHOLD));
        return (B)this;
    }

    public final B itemTextVisible(final boolean VISIBLE) {
        properties.put("itemTextVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B categoryTextColor(final Color COLOR) {
        properties.put("categoryTextColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B selectionColor(final Color COLOR) {
        properties.put("selectionColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B sortDirection(final SortDirection DIRECTION) {
        properties.put("sortDirection", new SimpleObjectProperty<>(DIRECTION));
        return (B)this;
    }

    public final B sortByName(final boolean BY_NAME) {
        properties.put("sortByName", new SimpleBooleanProperty(BY_NAME));
        return (B)this;
    }

    public final B categorySumVisible(final boolean VISIBLE) {
        properties.put("categorySumVisible", new SimpleBooleanProperty(VISIBLE));
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

    public final StreamChart build() {
        final StreamChart CONTROL = new StreamChart();

        if (properties.keySet().contains("itemsArray")) {
            CONTROL.setItems(((ObjectProperty<ChartItem[]>) properties.get("itemsArray")).get());
        } else if(properties.keySet().contains("itemsList")) {
            CONTROL.setItems(((ObjectProperty<List<ChartItem>>) properties.get("itemsList")).get());
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
            } else if ("category".equals(key)) {
                CONTROL.setCategory(((ObjectProperty<Category>) properties.get(key)).get());
            } else if ("type".equals(key)) {
                CONTROL.setType(((ObjectProperty<Type>) properties.get(key)).get());
            } else if ("textColor".equals(key)) {
                CONTROL.setTextColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("autoTextColor".equals(key)) {
                CONTROL.setAutoTextColor(((BooleanProperty) properties.get(key)).get());
            } else if ("selectionColor".equals(key)) {
                CONTROL.setSelectionColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("decimals".equals(key)) {
                CONTROL.setDecimals(((IntegerProperty) properties.get(key)).get());
            } else if ("itemWidth".equals(key)) {
                CONTROL.setItemWidth(((IntegerProperty) properties.get(key)).get());
            } else if ("autoItemWidth".equals(key)) {
                CONTROL.setAutoItemWidth(((BooleanProperty) properties.get(key)).get());
            } else if ("itemGap".equals(key)) {
                CONTROL.setItemGap(((IntegerProperty) properties.get(key)).get());
            } else if ("autoItemGap".equals(key)) {
                CONTROL.setAutoItemGap(((BooleanProperty) properties.get(key)).get());
            } else if ("locale".equals(key)) {
                CONTROL.setLocale(((ObjectProperty<Locale>) properties.get(key)).get());
            } else if ("itemTextThreshold".equals(key)) {
                CONTROL.setItemTextThreshold(((DoubleProperty) properties.get(key)).get());
            } else if ("itemTextVisible".equals(key)) {
                CONTROL.setItemTextVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("categoryTextColor".equals(key)) {
                CONTROL.setCategoryTextColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("sortDirection".equals(key)) {
                CONTROL.setSortDirection(((ObjectProperty<SortDirection>) properties.get(key)).get());
            } else if ("sortByName".equals(key)) {
                CONTROL.setSortByName(((BooleanProperty) properties.get(key)).get());
            } else if ("categorySumVisible".equals(key)) {
                CONTROL.setCategorySumVisible(((BooleanProperty) properties.get(key)).get());
            }
        }
        return CONTROL;
    }
}
