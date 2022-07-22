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

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.tools.Order;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;


public class CoxcombChartBuilder<B extends CoxcombChartBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected CoxcombChartBuilder() {}


    // ******************** Methods *******************************************
    public static final CoxcombChartBuilder create() {
        return new CoxcombChartBuilder();
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

    public final B order(final Order ORDER) {
        properties.put("order", new SimpleObjectProperty<>(ORDER));
        return (B)this;
    }

    public final B autoTextColor(final boolean AUTO) {
        properties.put("autoTextColor", new SimpleBooleanProperty(AUTO));
        return (B)this;
    }

    public final B useChartItemTextFill(final boolean USE) {
        properties.put("useChartItemTextFill", new SimpleBooleanProperty(USE));
        return (B)this;
    }

    public final B equalSegmentAngles(final boolean EQUAL) {
        properties.put("equalSegmentAngles", new SimpleBooleanProperty(EQUAL));
        return (B)this;
    }

    public final B onMousePressed(final EventHandler<MouseEvent> HANDLER) {
        properties.put("onMousePressed", new SimpleObjectProperty<>(HANDLER));
        return (B)this;
    }

    public final B onMouseReleased(final EventHandler<MouseEvent> HANDLER) {
        properties.put("onMouseReleased", new SimpleObjectProperty<>(HANDLER));
        return (B)this;
    }

    public final B onMouseMoved(final EventHandler<MouseEvent> HANDLER) {
        properties.put("onMouseMoved", new SimpleObjectProperty<>(HANDLER));
        return (B)this;
    }

    public final B showPopup(final boolean SHOW) {
        properties.put("showPopup", new SimpleBooleanProperty(SHOW));
        return (B)this;
    }

    public final B formatString(final String FORMAT_STRING) {
        properties.put("formatString", new SimpleStringProperty(FORMAT_STRING));
        return (B)this;
    }

    public final B showItemName(final boolean SHOW) {
        properties.put("showItemName", new SimpleBooleanProperty(SHOW));
        return (B)this;
    }

    public final B selectedItemFill(final Color SELECTED_ITEM_FILL) {
        properties.put("selectedItemFill", new SimpleObjectProperty<>(SELECTED_ITEM_FILL));
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


    public final CoxcombChart build() {
        final CoxcombChart coxcombChart = new CoxcombChart();

        if (properties.keySet().contains("itemsArray")) {
            coxcombChart.setItems(((ObjectProperty<ChartItem[]>) properties.get("itemsArray")).get());
        }
        if(properties.keySet().contains("itemsList")) {
            coxcombChart.setItems(((ObjectProperty<List<ChartItem>>) properties.get("itemsList")).get());
        }

        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"             -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    coxcombChart.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"              -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    coxcombChart.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"              -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    coxcombChart.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"            -> coxcombChart.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"           -> coxcombChart.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"             -> coxcombChart.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"            -> coxcombChart.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"             -> coxcombChart.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"            -> coxcombChart.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"               -> coxcombChart.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"               -> coxcombChart.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"              -> coxcombChart.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"              -> coxcombChart.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"           -> coxcombChart.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"           -> coxcombChart.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"              -> coxcombChart.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "textColor"            -> coxcombChart.setTextColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "order"                -> coxcombChart.setOrder(((ObjectProperty<Order>) properties.get(key)).get());
                case "autoTextColor"        -> coxcombChart.setAutoTextColor(((BooleanProperty) properties.get(key)).get());
                case "useChartItemTextFill" -> coxcombChart.setUseChartItemTextFill(((BooleanProperty) properties.get(key)).get());
                case "equalSegmentAngles"   -> coxcombChart.setEqualSegmentAngles(((BooleanProperty) properties.get(key)).get());
                case "onMousePressed"       -> coxcombChart.onMousePressed(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
                case "onMouseReleased"      -> coxcombChart.onMouseReleased(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
                case "onMouseMoved"         -> coxcombChart.onMouseMoved(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
                case "showPopup"            -> coxcombChart.setShowPopup(((BooleanProperty) properties.get(key)).get());
                case "formatString"         -> coxcombChart.setFormatString(((StringProperty) properties.get(key)).get());
                case "showItemName"         -> coxcombChart.setShowItemName(((BooleanProperty) properties.get(key)).get());
                case "selectedItemFill"     -> coxcombChart.setSelectedItemFill(((ObjectProperty<Color>) properties.get(key)).get());
            }
        }
        return coxcombChart;
    }
}
