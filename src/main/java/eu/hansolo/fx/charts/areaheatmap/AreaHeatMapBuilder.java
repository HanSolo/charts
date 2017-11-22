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

package eu.hansolo.fx.charts.areaheatmap;

import eu.hansolo.fx.charts.areaheatmap.AreaHeatMap.Quality;
import eu.hansolo.fx.charts.data.DataPoint;
import eu.hansolo.fx.charts.tools.ColorMapping;
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

import java.util.HashMap;
import java.util.List;


public class AreaHeatMapBuilder<B extends AreaHeatMapBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected AreaHeatMapBuilder() {
    }


    // ******************** Methods *******************************************
    public final static AreaHeatMapBuilder create() {
        return new AreaHeatMapBuilder();
    }

    public final B prefSize(final double WIDTH, final double HEIGHT) {
        return prefSize(new Dimension2D(WIDTH, HEIGHT));
    }

    public final B prefSize(final Dimension2D PREF_SIZE) {
        properties.put("prefSize", new SimpleObjectProperty<>(PREF_SIZE));
        return (B)this;
    }

    public final B dataPoints(final DataPoint... POINTS) {
        properties.put("dataPointsArray", new SimpleObjectProperty<>(POINTS));
        return (B)this;
    }

    public final B dataPoints(final List<DataPoint> POINTS) {
        properties.put("dataPointsList", new SimpleObjectProperty<>(POINTS));
        return (B)this;
    }

    public final B colorMapping(final ColorMapping COLOR_MAPPING) {
        properties.put("colorMapping", new SimpleObjectProperty<>(COLOR_MAPPING));
        return (B)this;
    }

    public final B useColorMapping(final boolean USE) {
        properties.put("useColorMapping", new SimpleBooleanProperty(USE));
        return (B)this;
    }

    public final B quality(final Quality QUALITY) {
        return quality(QUALITY.getFactor());
    }
    public final B quality(final int QUALITY) {
        properties.put("quality", new SimpleIntegerProperty(QUALITY));
        return (B)this;
    }

    public final B dataPointsVisible(final boolean VISIBLE) {
        properties.put("dataPointsVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B smoothedHull(final boolean SMOOTHED) {
        properties.put("smoothedHull", new SimpleBooleanProperty(SMOOTHED));
        return (B)this;
    }

    public final B discreteColors(final boolean DISCRETE_COLORS) {
        properties.put("discreteColors", new SimpleBooleanProperty(DISCRETE_COLORS));
        return (B)this;
    }

    public final B heatMapOpacity(final double HEAT_MAP_OPACITY) {
        properties.put("heatMapOpacity", new SimpleDoubleProperty(HEAT_MAP_OPACITY));
        return (B)this;
    }

    public final B noOfCloserInfluentPoints(final int NO_OF_POINTS) {
        properties.put("noOfCloserInfluentPoints", new SimpleIntegerProperty(NO_OF_POINTS));
        return (B)this;
    }


    public final AreaHeatMap build() {
        final AreaHeatMap CONTROL = new AreaHeatMap();
        for (String key : properties.keySet()) {
            if ("prefSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if ("colorMapping".equals(key)) {
                CONTROL.setColorMapping(((ObjectProperty<ColorMapping>) properties.get(key)).get());
            } else if("useColorMapping".equals(key)) {
                CONTROL.setUseColorMapping(((BooleanProperty) properties.get(key)).get());
            } else if ("quality".equals(key)) {
                CONTROL.setQuality(((IntegerProperty) properties.get(key)).get());
            } else if ("heatMapOpacity".equals(key)) {
                CONTROL.setHeatMapOpacity(((DoubleProperty) properties.get(key)).get());
            } else if ("dataPointsVisible".equals(key)) {
                CONTROL.setDataPointsVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("smoothedHull".equals(key)) {
                CONTROL.setSmoothedHull(((BooleanProperty) properties.get(key)).get());
            } else if ("discreteColors".equals(key)) {
                CONTROL.setDiscreteColors(((BooleanProperty) properties.get(key)).get());
            } else if ("noOfCloserInfluentPoints".equals(key)) {
                CONTROL.setNoOfCloserInfluentPoints(((IntegerProperty) properties.get(key)).get());
            }
        }
        if (properties.keySet().contains("dataPointsArray")) {
            CONTROL.setDataPoints(((ObjectProperty<DataPoint[]>) properties.get("dataPointsArray")).get());
        }
        if(properties.keySet().contains("dataPointsList")) {
            CONTROL.setDataPoints(((ObjectProperty<List<DataPoint>>) properties.get("dataPointsList")).get());
        }
        return CONTROL;
    }
}
