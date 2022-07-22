/*
 * Copyright (c) 2016 by Gerrit Grunwald
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

package eu.hansolo.fx.charts.world;

import eu.hansolo.fx.charts.data.MapConnection;
import eu.hansolo.fx.charts.data.WeightedMapPoints;
import eu.hansolo.fx.charts.tools.MapPoint;
import eu.hansolo.fx.charts.tools.MapPointSize;
import eu.hansolo.fx.charts.world.World.Resolution;
import eu.hansolo.toolboxfx.geom.Location;
import eu.hansolo.fx.heatmap.ColorMapping;
import eu.hansolo.fx.heatmap.Mapping;
import eu.hansolo.fx.heatmap.OpacityDistribution;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;


/**
 * Created by hansolo on 21.11.16.
 */
public class WorldBuilder<B extends WorldBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();
    private Resolution                resolution = Resolution.HI_RES;


    // ******************** Constructors **************************************
    protected WorldBuilder() {}


    // ******************** Methods *******************************************
    public static final WorldBuilder create() { return new WorldBuilder(); }

    public final B resolution(final Resolution RESOLUTION) {
        resolution = RESOLUTION;
        return (B)this;
    }

    public final B backgroundColor(final Color COLOR) {
        properties.put("backgroundColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B fillColor(final Color COLOR) {
        properties.put("fillColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B strokeColor(final Color COLOR) {
        properties.put("strokeColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B hoverColor(final Color COLOR) {
        properties.put("hoverColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B pressedColor(final Color COLOR) {
        properties.put("pressedColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B selectedColor(final Color COLOR) {
        properties.put("selectedColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B locationColor(final Color COLOR) {
        properties.put("locationColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B hoverEnabled(final boolean ENABLED) {
        properties.put("hoverEnabled", new SimpleBooleanProperty(ENABLED));
        return (B)this;
    }

    public final B selectionEnabled(final boolean ENABLED) {
        properties.put("selectionEnabled", new SimpleBooleanProperty(ENABLED));
        return (B)this;
    }

    public final B zoomEnabled(final boolean ENABLED) {
        properties.put("zoomEnabled", new SimpleBooleanProperty(ENABLED));
        return (B)this;
    }

    public final B mouseEnterHandler(final EventHandler<MouseEvent> HANDLER) {
        properties.put("mouseEnterHandler", new SimpleObjectProperty(HANDLER));
        return (B)this;
    }

    public final B mousePressHandler(final EventHandler<MouseEvent> HANDLER) {
        properties.put("mousePressHandler", new SimpleObjectProperty(HANDLER));
        return (B)this;
    }

    public final B mouseReleaseHandler(final EventHandler<MouseEvent> HANDLER) {
        properties.put("mouseReleaseHandler", new SimpleObjectProperty(HANDLER));
        return (B)this;
    }

    public final B mouseExitHandler(final EventHandler<MouseEvent> HANDLER) {
        properties.put("mouseExitHandler", new SimpleObjectProperty(HANDLER));
        return (B)this;
    }

    public final B locations(final Location... LOCATIONS) {
        properties.put("locations", new SimpleObjectProperty(LOCATIONS));
        return (B)this;
    }

    public final B showLocations(final boolean VISIBLE) {
        properties.put("showLocations", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B colorMapping(final Mapping COLOR_MAPPING) {
        properties.put("colorMapping", new SimpleObjectProperty<>(COLOR_MAPPING));
        return (B) this;
    }

    public final B eventRadius(final double EVENT_RADIUS) {
        properties.put("eventRadius", new SimpleDoubleProperty(EVENT_RADIUS));
        return (B) this;
    }

    public final B fadeColors(final boolean FADE_COLORS) {
        properties.put("fadeColors", new SimpleBooleanProperty(FADE_COLORS));
        return (B)this;
    }

    public final B heatMapOpacity(final double HEAT_MAP_OPACITY) {
        properties.put("heatMapOpacity", new SimpleDoubleProperty(HEAT_MAP_OPACITY));
        return (B) this;
    }

    public final B opacityDistribution(final OpacityDistribution OPACITY_DISTRIBUTION) {
        properties.put("opacityDistribution", new SimpleObjectProperty<>(OPACITY_DISTRIBUTION));
        return (B) this;
    }

    public final B mapPoints(final List<MapPoint> MAP_POINTS) {
        properties.put("mapPoints", new SimpleObjectProperty<>(MAP_POINTS));
        return (B)this;
    }

    public final B mapConnections(final List<MapConnection> MAP_CONNECTIONS) {
        properties.put("mapConnections", new SimpleObjectProperty<>(MAP_CONNECTIONS));
        return (B)this;
    }

    public final B weightedMapPoints(final WeightedMapPoints WEIGHTED) {
        properties.put("weightedMapPoints", new SimpleObjectProperty<>(WEIGHTED));
        return (B)this;
    }

    public final B weightedMapConnections(final boolean WEIGHTED) {
        properties.put("weightedMapConnections", new SimpleBooleanProperty(WEIGHTED));
        return (B)this;
    }

    public final B mapPointSize(final MapPointSize SIZE) {
        properties.put("mapPointSize", new SimpleObjectProperty<>(SIZE));
        return (B)this;
    }

    public final B mapPointsVisible(final boolean VISIBLE) {
        properties.put("mapPointsVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B mapPointTextVisible(final boolean VISIBLE) {
        properties.put("mapPointTextVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B textColor(final Color COLOR) {
        properties.put("textColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B connectionWidth(final double WIDTH) {
        properties.put("connectionWidth", new SimpleDoubleProperty(WIDTH));
        return (B)this;
    }

    public final B arrowsVisible(final boolean VISIBLE) {
        properties.put("arrowsVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B drawImagePath(final boolean DRAW) {
        properties.put("drawImagePath", new SimpleBooleanProperty(DRAW));
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


    public final World build() {
        Mapping             colorMapping        = properties.containsKey("colorMapping") ? ((ObjectProperty<Mapping>) properties.get("colorMapping")).get() : ColorMapping.INFRARED_3;
        double              eventRadius         = properties.containsKey("eventRadius") ? ((DoubleProperty) properties.get("eventRadius")).get() : 5;
        boolean             fadeColors          = properties.containsKey("fadeColors") ? ((BooleanProperty) properties.get("fadeColors")).get() : false;
        double              heatMapOpacity      = properties.containsKey("heatMapOpacity") ? ((DoubleProperty) properties.get("heatMapOpacity")).get() : 0.5;
        OpacityDistribution opacityDistribution = properties.containsKey("opacityDistribution") ? ((ObjectProperty<OpacityDistribution>) properties.get("opacityDistribution")).get() : OpacityDistribution.EXPONENTIAL;

        final World world = new World(resolution, colorMapping, eventRadius, fadeColors, opacityDistribution, heatMapOpacity);

        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize"               -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    world.setPrefSize(dim.getWidth(), dim.getHeight());
                }
                case "minSize"                -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    world.setMinSize(dim.getWidth(), dim.getHeight());
                }
                case "maxSize"                -> {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    world.setMaxSize(dim.getWidth(), dim.getHeight());
                }
                case "prefWidth"              -> world.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"             -> world.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"               -> world.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"              -> world.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"               -> world.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"              -> world.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"                 -> world.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"                 -> world.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"                -> world.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"                -> world.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"             -> world.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"             -> world.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"                -> world.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                case "backgroundColor"        -> world.setBackgroundColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "fillColor"              -> world.setFillColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "strokeColor"            -> world.setStrokeColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "hoverColor"             -> world.setHoverColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "pressedColor"           -> world.setPressedColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "selectedColor"          -> world.setSelectedColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "locationColor"          -> world.setLocationColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "hoverEnabled"           -> world.setHoverEnabled(((BooleanProperty) properties.get(key)).get());
                case "selectionEnabled"       -> world.setSelectionEnabled(((BooleanProperty) properties.get(key)).get());
                case "zoomEnabled"            -> world.setZoomEnabled(((BooleanProperty) properties.get(key)).get());
                case "mouseEnterHandler"      -> world.setMouseEnterHandler(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
                case "mousePressHandler"      -> world.setMousePressHandler(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
                case "mouseReleaseHandler"    -> world.setMouseReleaseHandler(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
                case "mouseExitHandler"       -> world.setMouseExitHandler(((ObjectProperty<EventHandler<MouseEvent>>) properties.get(key)).get());
                case "locations"              -> world.addLocations(((ObjectProperty<Location[]>) properties.get(key)).get());
                case "showLocations"          -> world.showLocations(((BooleanProperty) properties.get(key)).get());
                case "mapPoints"              -> world.setMapPoints(((ObjectProperty<List<MapPoint>>) properties.get(key)).get());
                case "mapConnections"         -> world.setMapConnections(((ObjectProperty<List<MapConnection>>) properties.get(key)).get());
                case "weightedMapPoints"      -> world.setWeightedMapPoints(((ObjectProperty<WeightedMapPoints>) properties.get(key)).get());
                case "weightedMapConnections" -> world.setWeightedMapConnections(((BooleanProperty) properties.get(key)).get());
                case "mapPointSize"           -> world.setMapPointSize(((ObjectProperty<MapPointSize>) properties.get(key)).get());
                case "mapPointsVisible"       -> world.setMapPointsVisible(((BooleanProperty) properties.get(key)).get());
                case "mapPointTextVisible"    -> world.setMapPointTextVisible(((BooleanProperty) properties.get(key)).get());
                case "textColor"              -> world.setTextColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "connectionWidth"        -> world.setConnectionWidth(((DoubleProperty) properties.get(key)).get());
                case "arrowsVisible"          -> world.setArrowsVisible(((BooleanProperty) properties.get(key)).get());
                case "drawImagePath"          -> world.setDrawImagePath(((BooleanProperty) properties.get(key)).get());
            }
        }
        return world;
    }
}
