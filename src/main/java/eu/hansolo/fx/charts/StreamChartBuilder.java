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
import eu.hansolo.fx.charts.tools.Order;
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

    public final B order(final Order ORDER) {
        properties.put("order", new SimpleObjectProperty<>(ORDER));
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
        final StreamChart streamChart = new StreamChart();

        if (properties.keySet().contains("itemsArray")) {
            streamChart.setItems(((ObjectProperty<ChartItem[]>) properties.get("itemsArray")).get());
        } else if(properties.keySet().contains("itemsList")) {
            streamChart.setItems(((ObjectProperty<List<ChartItem>>) properties.get("itemsList")).get());
        }

        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"           -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    streamChart.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"            -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    streamChart.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"            -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    streamChart.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"          -> streamChart.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"         -> streamChart.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"           -> streamChart.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"          -> streamChart.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"           -> streamChart.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"          -> streamChart.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"             -> streamChart.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"             -> streamChart.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"            -> streamChart.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"            -> streamChart.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"         -> streamChart.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"         -> streamChart.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"            -> streamChart.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "category"           -> streamChart.setCategory(((ObjectProperty<Category>) properties.get(key)).get());
                case "type"               -> streamChart.setType(((ObjectProperty<Type>) properties.get(key)).get());
                case "textColor"          -> streamChart.setTextColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "autoTextColor"      -> streamChart.setAutoTextColor(((BooleanProperty) properties.get(key)).get());
                case "selectionColor"     -> streamChart.setSelectionColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "decimals"           -> streamChart.setDecimals(((IntegerProperty) properties.get(key)).get());
                case "itemWidth"          -> streamChart.setItemWidth(((IntegerProperty) properties.get(key)).get());
                case "autoItemWidth"      -> streamChart.setAutoItemWidth(((BooleanProperty) properties.get(key)).get());
                case "itemGap"            -> streamChart.setItemGap(((IntegerProperty) properties.get(key)).get());
                case "autoItemGap"        -> streamChart.setAutoItemGap(((BooleanProperty) properties.get(key)).get());
                case "locale"             -> streamChart.setLocale(((ObjectProperty<Locale>) properties.get(key)).get());
                case "itemTextThreshold"  -> streamChart.setItemTextThreshold(((DoubleProperty) properties.get(key)).get());
                case "itemTextVisible"    -> streamChart.setItemTextVisible(((BooleanProperty) properties.get(key)).get());
                case "categoryTextColor"  -> streamChart.setCategoryTextColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "order"              -> streamChart.setOrder(((ObjectProperty<Order>) properties.get(key)).get());
                case "sortByName"         -> streamChart.setSortByName(((BooleanProperty) properties.get(key)).get());
                case "categorySumVisible" -> streamChart.setCategorySumVisible(((BooleanProperty) properties.get(key)).get());
            }
        }
        return streamChart;
    }
}
