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

import eu.hansolo.fx.charts.tools.TickLabelFormat;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;


/**
 * User: hansolo
 * Date: 07.01.18
 * Time: 06:32
 */
public class AxisBuilder<B extends AxisBuilder<B>> {
    private HashMap<String, Property> properties = new LinkedHashMap<>();
    private Orientation               orientation;
    private Position                  position;


    // ******************** Constructors **************************************
    protected AxisBuilder(final Orientation ORIENTATION, final Position POSITION) {
        orientation = ORIENTATION;
        position    = POSITION;
    }


    // ******************** Methods *******************************************
    public static final AxisBuilder create(final Orientation ORIENTATION, final Position POSITION) {
        return new AxisBuilder(ORIENTATION, POSITION);
    }

    public final B minValue(final double MIN_VALUE) {
        properties.put("minValue", new SimpleDoubleProperty(MIN_VALUE));
        return (B) this;
    }

    public final B maxValue(final double MAX_VALUE) {
        properties.put("maxValue", new SimpleDoubleProperty(MAX_VALUE));
        return (B) this;
    }

    public final B setStart(final long EPOCH_SECONDS) {
        if (0 > EPOCH_SECONDS) { throw new IllegalArgumentException("Epoch seconds cannot be smaller than 0"); }
        properties.put("start", new SimpleObjectProperty<>(LocalDateTime.ofInstant(Instant.ofEpochSecond(EPOCH_SECONDS), ZoneId.systemDefault())));
        return (B) this;
    }
    public final B setStart(final long EPOCH_SECONDS, final ZoneId ZONE_ID) {
        if (0 > EPOCH_SECONDS || null == ZONE_ID) { throw new IllegalArgumentException("Epoch seconds cannot be smaller than 0 and zone id cannot be null"); }
        properties.put("start", new SimpleObjectProperty<>(LocalDateTime.ofInstant(Instant.ofEpochSecond(EPOCH_SECONDS), ZONE_ID)));
        return (B) this;
    }
    public final B setStart(final Instant INSTANT) {
        if (null == INSTANT) { throw new IllegalArgumentException("Instant cannot be null"); }
        properties.put("start", new SimpleObjectProperty<>(LocalDateTime.ofInstant(INSTANT, ZoneId.systemDefault())));
        return (B) this;
    }
    public final B setStart(final Instant INSTANT, final ZoneId ZONE_ID) {
        if (null == INSTANT || null == ZONE_ID) { throw new IllegalArgumentException("Instant or zone id cannot be null"); }
        properties.put("start", new SimpleObjectProperty<>(LocalDateTime.ofInstant(INSTANT, ZONE_ID)));
        return (B) this;
    }
    public final B start(final LocalDateTime DATE_TIME) {
        properties.put("start", new SimpleObjectProperty<>(DATE_TIME));
        return (B) this;
    }

    public final B setEnd(final long EPOCH_SECONDS) {
        if (0 > EPOCH_SECONDS) { throw new IllegalArgumentException("Epoch seconds cannot be smaller than 0"); }
        properties.put("end", new SimpleObjectProperty<>(LocalDateTime.ofInstant(Instant.ofEpochSecond(EPOCH_SECONDS), ZoneId.systemDefault())));
        return (B) this;
    }
    public final B setEnd(final long EPOCH_SECONDS, final ZoneId ZONE_ID) {
        if (0 > EPOCH_SECONDS || null == ZONE_ID) { throw new IllegalArgumentException("Epoch seconds cannot be smaller than 0 and zone id cannot be null"); }
        properties.put("end", new SimpleObjectProperty<>(LocalDateTime.ofInstant(Instant.ofEpochSecond(EPOCH_SECONDS), ZONE_ID)));
        return (B) this;
    }
    public final B setEnd(final Instant INSTANT) {
        if (null == INSTANT) { throw new IllegalArgumentException("Instant cannot be null"); }
        properties.put("end", new SimpleObjectProperty<>(LocalDateTime.ofInstant(INSTANT, ZoneId.systemDefault())));
        return (B) this;
    }
    public final B setEnd(final Instant INSTANT, final ZoneId ZONE_ID) {
        if (null == INSTANT || null == ZONE_ID) { throw new IllegalArgumentException("Instant or zone id cannot be null"); }
        properties.put("end", new SimpleObjectProperty<>(LocalDateTime.ofInstant(INSTANT, ZONE_ID)));
        return (B) this;
    }
    public final B end(final LocalDateTime DATE_TIME) {
        properties.put("end", new SimpleObjectProperty<>(DATE_TIME));
        return (B) this;
    }

    public final B autoScale(final boolean AUTO) {
        properties.put("autoScale", new SimpleBooleanProperty(AUTO));
        return (B) this;
    }

    public final B title(final String TITLE) {
        properties.put("title", new SimpleStringProperty(TITLE));
        return (B) this;
    }

    public final B unit(final String UNIT) {
        properties.put("unit", new SimpleStringProperty(UNIT));
        return (B) this;
    }

    public final B type(final AxisType TYPE) {
        properties.put("axisType", new SimpleObjectProperty<>(TYPE));
        return (B)this;
    }

    public final B axisBackgroundColor(final Color COLOR) {
        properties.put("axisBackgroundColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B axisColor(final Color COLOR) {
        properties.put("axisColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B tickLabelColor(final Color COLOR) {
        properties.put("tickLabelColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B titleColor(final Color COLOR) {
        properties.put("titleColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B tickMarkColor(final Color COLOR) {
        properties.put("tickMarkColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B minorTickMarkColor(final Color COLOR) {
        properties.put("minorTickMarkColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B mediumTickMarkColor(final Color COLOR) {
        properties.put("mediumTickMarkColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B majorTickMarkColor(final Color COLOR) {
        properties.put("majorTickMarkColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B tickMarksVisible(final boolean VISIBLE) {
        properties.put("tickMarksVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B minorTickMarksVisible(final boolean VISIBLE) {
        properties.put("minorTickMarksVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B mediumTickMarksVisible(final boolean VISIBLE) {
        properties.put("mediumTickMarksVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B majorTickMarksVisible(final boolean VISIBLE) {
        properties.put("majorTickMarksVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B sameTickMarkLength(final boolean SAME_LENGTH) {
        properties.put("sameTickMarkLength", new SimpleBooleanProperty(SAME_LENGTH));
        return (B)this;
    }

    public final B zeroColor(final Color COLOR) {
        properties.put("zeroColor", new SimpleObjectProperty<>(COLOR));
        return (B)this;
    }

    public final B minorTickSpace(final double SPACE) {
        properties.put("minorTickSpace", new SimpleDoubleProperty(SPACE));
        return (B)this;
    }

    public final B majorTickSpace(final double SPACE) {
        properties.put("majorTickSpace", new SimpleDoubleProperty(SPACE));
        return (B)this;
    }

    public final B tickLabelsVisible(final boolean VISIBLE) {
        properties.put("tickLabelsVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B mediumTimeAxisTickLabelsVisible(final boolean VISIBLE) {
        properties.put("mediumTimeAxisTickLabelsVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B onlyFirstAndLastTickLabelVisible(final boolean VISIBLE) {
        properties.put("onlyFirstAndLastTickLabelVisible", new SimpleBooleanProperty(VISIBLE));
        return (B)this;
    }

    public final B locale(final Locale LOCALE) {
        properties.put("locale", new SimpleObjectProperty<>(LOCALE));
        return (B)this;
    }

    public final B decimals(final int DECIMALS) {
        properties.put("decimals", new SimpleIntegerProperty(DECIMALS));
        return (B)this;
    }

    public final B tickLabelOrientation(final TickLabelOrientation ORIENTATION) {
        properties.put("tickLabelOrientation", new SimpleObjectProperty<>(ORIENTATION));
        return (B)this;
    }

    public final B tickLabelFormat(final TickLabelFormat FORMAT) {
        properties.put("tickLabelFormat", new SimpleObjectProperty<>(FORMAT));
        return (B)this;
    }

    public final B autoTitleFontSize(final boolean AUTO) {
        properties.put("autoTitleFontSize", new SimpleBooleanProperty(AUTO));
        return (B)this;
    }

    public final B autoFontSize(final boolean AUTO) {
        properties.put("autoFontSize", new SimpleBooleanProperty(AUTO));
        return (B)this;
    }

    public final B tickLabelFontSize(final double SIZE) {
        properties.put("tickLabelFontSize", new SimpleDoubleProperty(SIZE));
        return (B)this;
    }

    public final B titleFontSize(final double SIZE) {
        properties.put("titleFontSize", new SimpleDoubleProperty(SIZE));
        return (B)this;
    }

    public final B zoneId(final ZoneId  ID) {
        properties.put("zoneId", new SimpleObjectProperty<>(ID));
        return (B)this;
    }

    public final B dateTimeFormatPattern(final String PATTERN) {
        properties.put("dateTimeFormatPattern", new SimpleStringProperty(PATTERN));
        return (B)this;
    }

    public final B numberFormatter(final StringConverter<Number> FORMATTER) {
        properties.put("numberFormatter", new SimpleObjectProperty<>(FORMATTER));
        return (B)this;
    }

    public final B categories(final String... CATEGORIES) {
        properties.put("categoriesArray", new SimpleObjectProperty<>(CATEGORIES));
        return (B)this;
    }

    public final B categories(final List<String> CATEGORIES) {
        properties.put("categoriesList", new SimpleObjectProperty(CATEGORIES));
        return (B)this;
    }


    // General properties
    public final B prefSize(final double WIDTH, final double HEIGHT) {
        properties.put("prefSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B) this;
    }
    public final B minSize(final double WIDTH, final double HEIGHT) {
        properties.put("minSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B) this;
    }
    public final B maxSize(final double WIDTH, final double HEIGHT) {
        properties.put("maxSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B) this;
    }

    public final B prefWidth(final double PREF_WIDTH) {
        properties.put("prefWidth", new SimpleDoubleProperty(PREF_WIDTH));
        return (B) this;
    }
    public final B prefHeight(final double PREF_HEIGHT) {
        properties.put("prefHeight", new SimpleDoubleProperty(PREF_HEIGHT));
        return (B) this;
    }

    public final B minWidth(final double MIN_WIDTH) {
        properties.put("minWidth", new SimpleDoubleProperty(MIN_WIDTH));
        return (B) this;
    }
    public final B minHeight(final double MIN_HEIGHT) {
        properties.put("minHeight", new SimpleDoubleProperty(MIN_HEIGHT));
        return (B) this;
    }

    public final B maxWidth(final double MAX_WIDTH) {
        properties.put("maxWidth", new SimpleDoubleProperty(MAX_WIDTH));
        return (B) this;
    }
    public final B maxHeight(final double MAX_HEIGHT) {
        properties.put("maxHeight", new SimpleDoubleProperty(MAX_HEIGHT));
        return (B) this;
    }

    public final B scaleX(final double SCALE_X) {
        properties.put("scaleX", new SimpleDoubleProperty(SCALE_X));
        return (B) this;
    }
    public final B scaleY(final double SCALE_Y) {
        properties.put("scaleY", new SimpleDoubleProperty(SCALE_Y));
        return (B) this;
    }

    public final B layoutX(final double LAYOUT_X) {
        properties.put("layoutX", new SimpleDoubleProperty(LAYOUT_X));
        return (B) this;
    }
    public final B layoutY(final double LAYOUT_Y) {
        properties.put("layoutY", new SimpleDoubleProperty(LAYOUT_Y));
        return (B) this;
    }

    public final B translateX(final double TRANSLATE_X) {
        properties.put("translateX", new SimpleDoubleProperty(TRANSLATE_X));
        return (B) this;
    }
    public final B translateY(final double TRANSLATE_Y) {
        properties.put("translateY", new SimpleDoubleProperty(TRANSLATE_Y));
        return (B) this;
    }

    public final B padding(final Insets INSETS) {
        properties.put("padding", new SimpleObjectProperty<>(INSETS));
        return (B) this;
    }

    public final B topAnchor(final double VALUE) {
        properties.put("topAnchor", new SimpleDoubleProperty(VALUE));
        return (B)this;
    }
    public final B rightAnchor(final double VALUE) {
        properties.put("rightAnchor", new SimpleDoubleProperty(VALUE));
        return (B)this;
    }
    public final B bottomAnchor(final double VALUE) {
        properties.put("bottomAnchor", new SimpleDoubleProperty(VALUE));
        return (B)this;
    }
    public final B leftAnchor(final double VALUE) {
        properties.put("leftAnchor", new SimpleDoubleProperty(VALUE));
        return (B)this;
    }


    public final Axis build() {
        final Axis axis = new Axis(orientation, position);

        if (properties.keySet().contains("categoriesArray")) {
            axis.setCategories(((ObjectProperty<String[]>) properties.get("categoriesArray")).get());
        }
        if(properties.keySet().contains("categoriesList")) {
            axis.setCategories(((ObjectProperty<List<String>>) properties.get("categoriesList")).get());
        }

        if (properties.keySet().contains("axisType")) {
            AxisType type = ((ObjectProperty<AxisType>) properties.get("axisType")).get();
            axis.setType(type);
            if (AxisType.TIME == type) {
                LocalDateTime start = null;
                LocalDateTime end   = null;
                if (properties.keySet().contains("start")) { start = ((ObjectProperty<LocalDateTime>) properties.get("start")).get(); }
                if (properties.keySet().contains("end"))   { end   = ((ObjectProperty<LocalDateTime>) properties.get("end")).get(); }
                if (null == start || null == end) {
                    throw new IllegalArgumentException("Start and end have to be defined for axis type TIME");
                }
                if (end.isBefore(start)) { throw new IllegalArgumentException("End cannot be before start"); }
                if (start.isAfter(end)) { throw new IllegalArgumentException("Start cannot be after end"); }
                axis.setStart(start);
                axis.setEnd(end);
            }
        }

        for (String key : properties.keySet()) {
            switch(key) {
                case "prefSize"                        -> {
                    Dimension2D dimPrefSize = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    axis.setPrefSize(dimPrefSize.getWidth(), dimPrefSize.getHeight());
                }
                case "minSize"                         -> {
                    Dimension2D dimMinSize = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    axis.setMinSize(dimMinSize.getWidth(), dimMinSize.getHeight());
                }
                case "maxSize"                         -> {
                    Dimension2D dimMaxSize = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    axis.setMaxSize(dimMaxSize.getWidth(), dimMaxSize.getHeight());
                }
                case "prefWidth"                       -> axis.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                case "prefHeight"                      -> axis.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                case "minWidth"                        -> axis.setMinWidth(((DoubleProperty) properties.get(key)).get());
                case "minHeight"                       -> axis.setMinHeight(((DoubleProperty) properties.get(key)).get());
                case "maxWidth"                        -> axis.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                case "maxHeight"                       -> axis.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                case "scaleX"                          -> axis.setScaleX(((DoubleProperty) properties.get(key)).get());
                case "scaleY"                          -> axis.setScaleY(((DoubleProperty) properties.get(key)).get());
                case "layoutX"                         -> axis.setLayoutX(((DoubleProperty) properties.get(key)).get());
                case "layoutY"                         -> axis.setLayoutY(((DoubleProperty) properties.get(key)).get());
                case "translateX"                      -> axis.setTranslateX(((DoubleProperty) properties.get(key)).get());
                case "translateY"                      -> axis.setTranslateY(((DoubleProperty) properties.get(key)).get());
                case "padding"                         -> axis.setPadding(((ObjectProperty<Insets>) properties.get(key)).get());
                // Control specific properties
                case "minValue"                        -> axis.setMinValue(((DoubleProperty) properties.get(key)).get());
                case "maxValue"                        -> axis.setMaxValue(((DoubleProperty) properties.get(key)).get());
                case "autoScale"                       -> axis.setAutoScale(((BooleanProperty) properties.get(key)).get());
                case "title"                           -> axis.setTitle(((StringProperty) properties.get(key)).get());
                case "unit"                            -> axis.setUnit(((StringProperty) properties.get(key)).get());
                case "axisBackgroundColor"             -> axis.setAxisBackgroundColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "axisColor"                       -> axis.setAxisColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "tickLabelColor"                  -> axis.setTickLabelColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "titleColor"                      -> axis.setTitleColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "tickMarkColor"                   -> axis.setTickMarkColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "minorTickMarkColor"              -> axis.setMinorTickMarkColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "mediumTickMarkColor"             -> axis.setMediumTickMarkColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "majorTickMarkColor"              -> axis.setMajorTickMarkColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "tickMarksVisible"                -> axis.setTickMarksVisible(((BooleanProperty) properties.get(key)).get());
                case "minorTickMarksVisible"           -> axis.setMinorTickMarksVisible(((BooleanProperty) properties.get(key)).get());
                case "mediumTickMarksVisible"          -> axis.setMediumTickMarksVisible(((BooleanProperty) properties.get(key)).get());
                case "majorTickMarksVisible"           -> axis.setMajorTickMarksVisible(((BooleanProperty) properties.get(key)).get());
                case "sameTickMarkLength"              -> axis.setSameTickMarkLength(((BooleanProperty) properties.get(key)).get());
                case "zeroColor"                       -> axis.setZeroColor(((ObjectProperty<Color>) properties.get(key)).get());
                case "minorTickSpace"                  -> axis.setMinorTickSpace(((DoubleProperty) properties.get(key)).get());
                case "majorTickSpace"                  -> axis.setMajorTickSpace(((DoubleProperty) properties.get(key)).get());
                case "tickLabelsVisible"               -> axis.setTickLabelsVisible(((BooleanProperty) properties.get(key)).get());
                case "mediumTimeAxisTickLabelsVisible" -> axis.setMediumTimeAxisTickLabelsVisible(((BooleanProperty) properties.get(key)).get());
                case "onlyFirstAndLastTickLabel"       -> axis.setOnlyFirstAndLastTickLabelVisible(((BooleanProperty) properties.get(key)).get());
                case "local"                           -> axis.setLocale(((ObjectProperty<Locale>) properties.get(key)).get());
                case "decimals"                        -> axis.setDecimals(((IntegerProperty) properties.get(key)).get());
                case "tickLabelOrientation"            -> axis.setTickLabelOrientation(((ObjectProperty<TickLabelOrientation>) properties.get(key)).get());
                case "tickLabelFormat"                 -> axis.setTickLabelFormat(((ObjectProperty<TickLabelFormat>) properties.get(key)).get());
                case "autoTitleFontSize"               -> axis.setAutoTitleFontSize(((BooleanProperty) properties.get(key)).get());
                case "autoFontSize"                    -> axis.setAutoFontSize(((BooleanProperty) properties.get(key)).get());
                case "tickLabelFontSize"               -> axis.setTickLabelFontSize(((DoubleProperty) properties.get(key)).get());
                case "titleFontSize"                   -> axis.setTitleFontSize(((DoubleProperty) properties.get(key)).get());
                case "zoneId"                          -> axis.setZoneId(((ObjectProperty<ZoneId>) properties.get(key)).get());
                case "dateTimeFormatPattern"           -> axis.setDateTimeFormatPattern(((StringProperty) properties.get(key)).get());
                case "numberFormatter"                 -> axis.setNumberFormatter(((ObjectProperty<StringConverter>) properties.get(key)).get());
                case "topAnchor"                       -> AnchorPane.setTopAnchor(axis, ((DoubleProperty) properties.get(key)).get());
                case "rightAnchor"                     -> AnchorPane.setRightAnchor(axis, ((DoubleProperty) properties.get(key)).get());
                case "bottomAnchor"                    -> AnchorPane.setBottomAnchor(axis, ((DoubleProperty) properties.get(key)).get());
                case "leftAnchor"                      -> AnchorPane.setLeftAnchor(axis, ((DoubleProperty) properties.get(key)).get());
            }
        }
        return axis;
    }
}