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
        final CoxcombChart CONTROL = new CoxcombChart();

        if (properties.keySet().contains("itemsArray")) {
            CONTROL.setItems(((ObjectProperty<ChartItem[]>) properties.get("itemsArray")).get());
        }
        if(properties.keySet().contains("itemsList")) {
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
            } else if ("textColor".equals(key)) {
                CONTROL.setTextColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("order".equals(key)) {
                CONTROL.setOrder(((ObjectProperty<Order>) properties.get(key)).get());
            } else if ("autoTextColor".equals(key)) {
                CONTROL.setAutoTextColor(((BooleanProperty) properties.get(key)).get());
            } else if ("useChartItemTextFill".equals(key)) {
                CONTROL.setUseChartItemTextFill(((BooleanProperty) properties.get(key)).get());
            } else if ("equalSegmentAngles".equals(key)) {
                CONTROL.setEqualSegmentAngles(((BooleanProperty) properties.get(key)).get());
            } else if ("onMousePressed".equals(key)) {
                CONTROL.onMousePressed(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
            } else if ("onMouseReleased".equals(key)) {
                CONTROL.onMouseReleased(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
            } else if ("onMouseMoved".equals(key)) {
                CONTROL.onMouseMoved(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
            } else if ("showPopup".equals(key)) {
                CONTROL.setShowPopup(((BooleanProperty) properties.get(key)).get());
            } else if ("formatString".equals(key)) {
                CONTROL.setFormatString(((StringProperty) properties.get(key)).get());
            } else if ("showItemName".equals(key)) {
                CONTROL.setShowItemName(((BooleanProperty) properties.get(key)).get());
            } else if ("selectedItemFill".equals(key)) {
                CONTROL.setSelectedItemFill(((ObjectProperty<Color>) properties.get(key)).get());
            }
        }
        return CONTROL;
    }
}
