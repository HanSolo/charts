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


import eu.hansolo.fx.charts.SankeyPlot.StreamFillMode;
import eu.hansolo.fx.charts.data.PlotItem;
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


public class SankeyPlotBuilder<B extends SankeyPlotBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected SankeyPlotBuilder() {}


    // ******************** Methods *******************************************
    public static final SankeyPlotBuilder create() {
        return new SankeyPlotBuilder();
    }

    public final B items(final PlotItem... ITEMS) {
        properties.put("itemsArray", new SimpleObjectProperty<>(ITEMS));
        return (B)this;
    }

    public final B items(final List<PlotItem> ITEMS) {
        properties.put("itemsList", new SimpleObjectProperty<>(ITEMS));
        return (B)this;
    }

    public final B streamFillMode(final StreamFillMode MODE) {
        properties.put("streamFillMode", new SimpleObjectProperty<>(MODE));
        return (B)this;
    }

    public final B streamColor(final Color COLOR) {
        properties.put("streamColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B textColor(final Color COLOR) {
        properties.put("textColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B selectionColor(final Color COLOR) {
        properties.put("selectionColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B decimals(final int DECIMALS) {
        properties.put("decimals", new SimpleIntegerProperty(DECIMALS));
        return (B)this;
    }

    public final B showFlowDirection(final boolean SHOW) {
        properties.put("showFlowDirection", new SimpleBooleanProperty(SHOW));
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

    public final B useItemColor(final boolean USE) {
        properties.put("useItemColor", new SimpleBooleanProperty(USE));
        return (B)this;
    }

    public final B itemColor(final Color COLOR) {
        properties.put("itemColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B connectionOpacity(final double OPACITY) {
        properties.put("connectionOpacity", new SimpleDoubleProperty(OPACITY));
        return (B)this;
    }

    public final B locale(final Locale LOCALE) {
        properties.put("locale", new SimpleObjectProperty<>(LOCALE));
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


    public final SankeyPlot build() {
        final SankeyPlot sankeyPlot = new SankeyPlot();

        if (properties.keySet().contains("itemsArray")) {
            sankeyPlot.setItems(((ObjectProperty<PlotItem[]>) properties.get("itemsArray")).get());
        }
        if(properties.keySet().contains("itemsList")) {
            sankeyPlot.setItems(((ObjectProperty<List<PlotItem>>) properties.get("itemsList")).get());
        }

        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"          -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    sankeyPlot.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"           -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    sankeyPlot.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"           -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    sankeyPlot.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"         -> sankeyPlot.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"        -> sankeyPlot.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"          -> sankeyPlot.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"         -> sankeyPlot.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"          -> sankeyPlot.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"         -> sankeyPlot.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"            -> sankeyPlot.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"            -> sankeyPlot.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"           -> sankeyPlot.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"           -> sankeyPlot.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"        -> sankeyPlot.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"        -> sankeyPlot.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"           -> sankeyPlot.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "streamFillMode"    -> sankeyPlot.setStreamFillMode(((ObjectProperty<StreamFillMode>) properties.get(key)).get());
                case "streamColor"       -> sankeyPlot.setStreamColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "textColor"         -> sankeyPlot.setTextColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "selectionColor"    -> sankeyPlot.setSelectionColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "decimals"          -> sankeyPlot.setDecimals(((IntegerProperty) properties.get(key)).get());
                case "showFlowDirection" -> sankeyPlot.setShowFlowDirection(((BooleanProperty) properties.get(key)).get());
                case "itemWidth"         -> sankeyPlot.setItemWidth(((IntegerProperty) properties.get(key)).get());
                case "autoItemWidth"     -> sankeyPlot.setAutoItemWidth(((BooleanProperty) properties.get(key)).get());
                case "itemGap"           -> sankeyPlot.setItemGap(((IntegerProperty) properties.get(key)).get());
                case "autoItemGap"       -> sankeyPlot.setAutoItemGap(((BooleanProperty) properties.get(key)).get());
                case "useItemColor"      -> sankeyPlot.setUseItemColor(((BooleanProperty) properties.get(key)).get());
                case "itemColor"         -> sankeyPlot.setItemColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "connectionOpacity" -> sankeyPlot.setConnectionOpacity(((DoubleProperty) properties.get(key)).get());
                case "locale"            -> sankeyPlot.setLocale(((ObjectProperty<Locale>) properties.get(key)).get());
            }
        }
        return sankeyPlot;
    }
}
