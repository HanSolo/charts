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

import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.Helper.Interval;
import eu.hansolo.fx.charts.tools.TickLabelFormat;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static javafx.geometry.Orientation.VERTICAL;
import javafx.util.StringConverter;


/**
 * User: hansolo
 * Date: 22.07.17
 * Time: 08:49
 */
@DefaultProperty("children")
public class Axis extends Region {
    private static final double                               MINIMUM_WIDTH         = 0;
    private static final double                               MINIMUM_HEIGHT        = 0;
    private static final double                               MAXIMUM_WIDTH         = 4096;
    private static final double                               MAXIMUM_HEIGHT        = 4096;
    private static final double                               MIN_MAJOR_LINE_WIDTH  = 1;
    private static final double                               MIN_MEDIUM_LINE_WIDTH = 0.75;
    private static final double                               MIN_MINOR_LINE_WIDTH  = 0.5;
    private              double                               size;
    private              double                               width;
    private              double                               height;
    private              Canvas                               axisCanvas;
    private              GraphicsContext                      axisCtx;
    private              Pane                                 pane;
    private              double                               _minValue;
    private              DoubleProperty                       minValue;
    private              LocalDateTime                        _start;
    private              ObjectProperty<LocalDateTime>        start;
    private              double                               _maxValue;
    private              DoubleProperty                       maxValue;
    private              LocalDateTime                        _end;
    private              ObjectProperty<LocalDateTime>        end;
    private              boolean                              _autoScale;
    private              BooleanProperty                      autoScale;
    private              double                               stepSize;
    private              String                               _title;
    private              StringProperty                       title;
    private              String                               _unit;
    private              StringProperty                       unit;
    private              AxisType                             _type;
    private              ObjectProperty<AxisType>             type;
    private              Orientation                          _orientation;
    private              ObjectProperty<Orientation>          orientation;
    private              Position                             _position;
    private              ObjectProperty<Position>             position;
    private              Color                                _axisBackgroundColor;
    private              ObjectProperty<Color>                axisBackgroundColor;
    private              Color                                _axisColor;
    private              ObjectProperty<Color>                axisColor;
    private              Color                                _tickLabelColor;
    private              ObjectProperty<Color>                tickLabelColor;
    private              Color                                _titleColor;
    private              ObjectProperty<Color>                titleColor;
    private              Color                                _minorTickMarkColor;
    private              ObjectProperty<Color>                minorTickMarkColor;
    private              Color                                _mediumTickMarkColor;
    private              ObjectProperty<Color>                mediumTickMarkColor;
    private              Color                                _majorTickMarkColor;
    private              ObjectProperty<Color>                majorTickMarkColor;
    private              Color                                _zeroColor;
    private              ObjectProperty<Color>                zeroColor;
    private              double                               _zeroPosition;
    private              DoubleProperty                       zeroPosition;
    private              double                               _minorTickSpace;
    private              double                               _majorTickSpace;
    private              boolean                              _majorTickMarksVisible;
    private              BooleanProperty                      majorTickMarksVisible;
    private              boolean                              _mediumTickMarksVisible;
    private              BooleanProperty                      mediumTickMarksVisible;
    private              boolean                              _minorTickMarksVisible;
    private              BooleanProperty                      minorTickMarksVisible;
    private              boolean                              _tickLabelsVisible;
    private              BooleanProperty                      tickLabelsVisible;
    private              boolean                              _onlyFirstAndLastTickLabelVisible;
    private              BooleanProperty                      onlyFirstAndLastTickLabelVisible;
    private              Locale                               _locale;
    private              ObjectProperty<Locale>               locale;
    private              int                                  _decimals;
    private              IntegerProperty                      decimals;
    private              String                               tickLabelFormatString;
    // TFE, 20220329: extend to set specific StringConverter<Number>
    // if stringConverter is set, use it instead of the String.format using tickLabelFormatString
    // don't initialize it anywhere here in the code
    private              StringConverter<Number>              numberFormatter;
    private              TickLabelOrientation                 _tickLabelOrientation;
    private              ObjectProperty<TickLabelOrientation> tickLabelOrientation;
    private              TickLabelFormat                      _tickLabelFormat;
    private              ObjectProperty<TickLabelFormat>      tickLabelFormat;
    private              Font                                 tickLabelFont;
    private              Font                                 titleFont;
    private              boolean                              _autoFontSize;
    private              BooleanProperty                      autoFontSize;
    private              double                               _tickLabelFontSize;
    private              DoubleProperty                       tickLabelFontSize;
    private              double                               _titleFontSize;
    private              DoubleProperty                       titleFontSize;
    private              ZoneId                               _zoneId;
    private              ObjectProperty<ZoneId>               zoneId;
    private              String                               _dateTimeFormatPattern;
    private              StringProperty                       dateTimeFormatPattern;
    private              List<String>                         categories;
    private              DateTimeFormatter                    dateTimeFormatter;
    private              Interval                             currentInterval;


    // ******************** Constructors **************************************
    public Axis() {
        this(0, 100, VERTICAL, AxisType.LINEAR, Position.LEFT, "");
    }
    public Axis(final Orientation ORIENTATION, final Position POSITION) {
        this(0, 100, ORIENTATION, AxisType.LINEAR, POSITION, "");
    }
    public Axis(final Orientation ORIENTATION, final AxisType TYPE, final Position POSITION) {
        this(0, 100, ORIENTATION, TYPE, POSITION, "");
    }
    public Axis(final double MIN_VALUE, final double MAX_VALUE, final Orientation ORIENTATION, final Position POSITION) {
        this(MIN_VALUE, MAX_VALUE, ORIENTATION, AxisType.LINEAR, POSITION, "");
    }
    public Axis(final double MIN_VALUE, final double MAX_VALUE, final Orientation ORIENTATION, final AxisType TYPE, final Position POSITION) {
        this(MIN_VALUE, MAX_VALUE, ORIENTATION, TYPE, POSITION, "");
    }
    public Axis(final double MIN_VALUE, final double MAX_VALUE, final Orientation ORIENTATION, final AxisType TYPE, final Position POSITION, final String TITLE) {
        if (VERTICAL == ORIENTATION) {
            if (Position.LEFT != POSITION && Position.RIGHT != POSITION && Position.CENTER != POSITION) {
                throw new IllegalArgumentException("Wrong combination of orientation and position!");
            }
        } else {
            if (Position.TOP != POSITION && Position.BOTTOM != POSITION && Position.CENTER != POSITION) {
                throw new IllegalArgumentException("Wrong combination of orientation and position!");
            }
        }

        _minValue                         = MIN_VALUE;
        _maxValue                         = MAX_VALUE;
        
        _type                             = TYPE;
        _autoScale                        = true;
        _title                            = TITLE;
        _unit                             = "";
        _orientation                      = ORIENTATION;
        _position                         = POSITION;
        _axisBackgroundColor              = Color.TRANSPARENT;
        _axisColor                        = Color.BLACK;
        _tickLabelColor                   = Color.BLACK;
        _titleColor                       = Color.BLACK;
        _minorTickMarkColor               = Color.BLACK;
        _mediumTickMarkColor              = Color.BLACK;
        _majorTickMarkColor               = Color.BLACK;
        _zeroColor                        = Color.BLACK;
        _zeroPosition                     = 0;
        _minorTickSpace                   = 1;
        _majorTickSpace                   = 10;
        _majorTickMarksVisible            = true;
        _mediumTickMarksVisible           = true;
        _minorTickMarksVisible            = true;
        _tickLabelsVisible                = true;
        _onlyFirstAndLastTickLabelVisible = false;
        _locale                           = Locale.US;
        _decimals                         = 0;
        _tickLabelOrientation             = TickLabelOrientation.HORIZONTAL;
        _tickLabelFormat                  = TickLabelFormat.NUMBER;
        _autoFontSize                     = true;
        _tickLabelFontSize                = 10;
        _titleFontSize                    = 10;
        _zoneId                           = ZoneId.systemDefault();
        _dateTimeFormatPattern            = "dd.MM.YY HH:mm:ss";
        currentInterval                   = Interval.SECOND_1;
        dateTimeFormatter                 = DateTimeFormatter.ofPattern(_dateTimeFormatPattern, _locale);
        categories                        = new LinkedList<>();
        tickLabelFormatString             = new StringBuilder("%.").append(Integer.toString(_decimals)).append("f").toString();

        initGraphics();
        registerListeners();
    }

    public Axis(final LocalDateTime START, final LocalDateTime END, final Orientation ORIENTATION, final Position POSITION) {
        if (VERTICAL == ORIENTATION) {
            if (Position.LEFT != POSITION && Position.RIGHT != POSITION && Position.CENTER != POSITION) {
                throw new IllegalArgumentException("Wrong combination of orientation and position!");
            }
        } else {
            if (Position.TOP != POSITION && Position.BOTTOM != POSITION && Position.CENTER != POSITION) {
                throw new IllegalArgumentException("Wrong combination of orientation and position!");
            }
        }

        getStylesheets().add(Axis.class.getResource("chart.css").toExternalForm());
        _minValue                         = START.toEpochSecond(Helper.getZoneOffset());
        _start                            = START;
        _maxValue                         = END.toEpochSecond(Helper.getZoneOffset());
        _end                              = END;

        _type                             = AxisType.TIME;
        _autoScale                        = true;
        _title                            = "";
        _unit                             = "";
        _orientation                      = ORIENTATION;
        _position                         = POSITION;
        _axisBackgroundColor              = Color.TRANSPARENT;
        _axisColor                        = Color.BLACK;
        _tickLabelColor                   = Color.BLACK;
        _minorTickMarkColor               = Color.BLACK;
        _mediumTickMarkColor              = Color.BLACK;
        _majorTickMarkColor               = Color.BLACK;
        _zeroColor                        = Color.BLACK;
        _zeroPosition                     = 0;
        _minorTickSpace                   = 1;
        _majorTickSpace                   = 10;
        _majorTickMarksVisible            = true;
        _mediumTickMarksVisible           = true;
        _minorTickMarksVisible            = true;
        _tickLabelsVisible                = true;
        _onlyFirstAndLastTickLabelVisible = false;
        _locale                           = Locale.US;
        _decimals                         = 0;
        _tickLabelOrientation             = TickLabelOrientation.HORIZONTAL;
        _tickLabelFormat                  = TickLabelFormat.NUMBER;
        _autoFontSize                     = true;
        _tickLabelFontSize                = 10;
        _titleFontSize                    = 10;
        _zoneId                           = ZoneId.systemDefault();
        _dateTimeFormatPattern            = "dd.MM.YY HH:mm:ss";
        currentInterval                   = Interval.SECOND_1;
        dateTimeFormatter                 = DateTimeFormatter.ofPattern(_dateTimeFormatPattern, _locale);
        tickLabelFormatString             = new StringBuilder("%.").append(Integer.toString(_decimals)).append("f").toString();

        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
            Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() != 0 && getPrefHeight() != 0) {
                if (VERTICAL == getOrientation()) {
                    setPrefSize(20, 250);
                } else {
                    setPrefSize(250, 20);
                }
            }
        }

        getStyleClass().add("axis");

        axisCanvas = new Canvas(width, height);
        axisCtx    = axisCanvas.getGraphicsContext2D();

        pane = new Pane(axisCanvas);

        getChildren().setAll(pane);
    }
    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public double getMinValue() {  return null == minValue ? _minValue : minValue.get();  }
    public void setMinValue(final LocalDateTime START) {
        setMinValue(START.toEpochSecond(Helper.getZoneOffset(getZoneId())));
    }
    public void setMinValue(final double VALUE) {
        if (null == minValue) {
            if (VALUE > getMaxValue()) { setMaxValue(VALUE); }
            _minValue = Helper.clamp(-Double.MAX_VALUE, getMaxValue(), VALUE);
        } else {
            minValue.set(VALUE);
        }
    }
    public DoubleProperty minValueProperty() {
        if (null == minValue) {
            minValue = new DoublePropertyBase(_minValue) {
                @Override protected void invalidated() { if (getValue() > getMaxValue()) setMaxValue(getValue()); }
                @Override public Object getBean() {  return Axis.this;  }
                @Override public String getName() {  return "minValue"; }
            };
        }
        return minValue;
    }

    public LocalDateTime getStart() { return null == start ? _start : start.get(); }
    public void setStart(final long EPOCH_SECONDS) {
        if (0 > EPOCH_SECONDS) { throw new IllegalArgumentException("Epoch seconds cannot be smaller than 0"); }
        setStart(Instant.ofEpochSecond(EPOCH_SECONDS));
    }
    public void setStart(final long EPOCH_SECONDS, final ZoneId ZONE_ID) {
        if (0 > EPOCH_SECONDS || null == ZONE_ID) { throw new IllegalArgumentException("Epoch seconds cannot be smaller than 0 and zone id cannot be null"); }
        setStart(Instant.ofEpochSecond(EPOCH_SECONDS), ZONE_ID);
    }
    public void setStart(final Instant INSTANT) {
        setStart(INSTANT, ZoneId.systemDefault());
    }
    public void setStart(final Instant INSTANT, final ZoneId ZONE_ID) {
        if (null == INSTANT || null == ZONE_ID) { throw new IllegalArgumentException("Instant cannot be null"); }
        setStart(LocalDateTime.ofInstant(INSTANT, ZONE_ID));
    }
    public void setStart(final LocalDateTime DATE_TIME) {
        if (AxisType.TIME != getType()) { throw new IllegalArgumentException("Axis type has to be DATE"); }
        if (null == start) {
            _start = DATE_TIME;
            setMinValue(_start.toEpochSecond(Helper.getZoneOffset()));
        } else {
            start.set(DATE_TIME);
        }
    }
    public ObjectProperty<LocalDateTime> startProperty() {
        if (null == start) {
            start = new ObjectPropertyBase<LocalDateTime>(_start) {
                @Override protected void invalidated() {
                    if (AxisType.TIME != getType()) { throw new IllegalArgumentException("Axis type has to be DATE"); }
                    setMinValue(get().toEpochSecond(Helper.getZoneOffset()));
                }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "start"; }
            };
            _start = null;
        }
        return start;
    }
    
    public double getMaxValue() { return null == maxValue ? _maxValue : maxValue.get(); }
    public void setMaxValue(final LocalDateTime END) {
        setMaxValue(END.toEpochSecond(Helper.getZoneOffset(getZoneId())));
    }
    public void setMaxValue(final double VALUE) {
        if (null == maxValue) {
            if (VALUE < getMinValue()) { setMinValue(VALUE); }
            _maxValue = Helper.clamp(getMinValue(), Double.MAX_VALUE, VALUE);
        } else {
            maxValue.set(VALUE);
        }
    }
    public DoubleProperty maxValueProperty() {
        if (null == maxValue) {
            maxValue = new DoublePropertyBase(_maxValue) {
                @Override protected void invalidated() { if (get() < getMinValue()) setMinValue(get()); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "maxValue"; }
            };
        }
        return maxValue;
    }

    public LocalDateTime getEnd() { return null == end ? _end : end.get(); }
    public void setEnd(final Instant INSTANT) {
        setEnd(INSTANT, ZoneId.systemDefault());
    }
    public void setEnd(final long EPOCH_SECONDS) {
        if (0 > EPOCH_SECONDS) { throw new IllegalArgumentException("Epoch seconds cannot be smaller than 0"); }
        setEnd(Instant.ofEpochSecond(EPOCH_SECONDS));
    }
    public void setEnd(final long EPOCH_SECONDS, final ZoneId ZONE_ID) {
        if (0 > EPOCH_SECONDS || null == ZONE_ID) { throw new IllegalArgumentException("Epoch seconds cannot be smaller than 0 and zone id cannot be null"); }
        setEnd(Instant.ofEpochSecond(EPOCH_SECONDS), ZONE_ID);
    }
    public void setEnd(final Instant INSTANT, final ZoneId ZONE_ID) {
        if (null == INSTANT || null == ZONE_ID) { throw new IllegalArgumentException("Instant cannot be null"); }
        setEnd(LocalDateTime.ofInstant(INSTANT, ZONE_ID));
    }
    public void setEnd(final LocalDateTime DATE_TIME) {
        if (null == end) {
            _end = DATE_TIME;
            setMaxValue(_end.toEpochSecond(Helper.getZoneOffset()));
        } else {
            end.set(DATE_TIME);
        }
    }
    public ObjectProperty<LocalDateTime> endProperty() {
        if (null == end) {
            end = new ObjectPropertyBase<LocalDateTime>(_end) {
                @Override protected void invalidated() { setMaxValue(get().toEpochSecond(Helper.getZoneOffset())); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "end"; }
            };
            _end = null;
        }
        return end;
    }
    
    public boolean isAutoScale() { return null == autoScale ? _autoScale : autoScale.get(); }
    public void setAutoScale(final boolean AUTO_SCALE) {
        if (null == autoScale) {
            _autoScale = AUTO_SCALE;
            redraw();
        } else {
            autoScale.set(AUTO_SCALE);
        }
    }
    public BooleanProperty autoScaleProperty() {
        if (null == autoScale) {
            autoScale = new BooleanPropertyBase(_autoScale) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "autoScale"; }
            };
        }
        return autoScale;
    }

    public String getTitle() {  return null == title ? _title : title.get(); }
    public void setTitle(final String TITLE) {
        if (null == title) {
            _title = TITLE;
            redraw();
        } else {
            title.set(TITLE);
        }
    }
    public StringProperty titleProperty() {
        if (null == title) {
            title = new StringPropertyBase(_title) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() {  return Axis.this;  }
                @Override public String getName() {  return "title";  }
            };
            _title = null;
        }
        return title;
    }

    public String getUnit() { return null == unit ? _unit : unit.get(); }
    public void setUnit(final String UNIT) {
        if (null == unit) {
            _unit = UNIT;
            redraw();
        } else {
            unit.set(UNIT);
        }
    }
    public StringProperty unitProperty() {
        if (null == unit) {
            unit = new StringPropertyBase(_unit) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() {  return Axis.this;  }
                @Override public String getName() {  return "unit";  }
            };
            _unit = null;
        }
        return unit;
    }

    public AxisType getType() { return null == type ? _type : type.get(); }
    public void setType(final AxisType TYPE) {
        if (null == type) {
            _type = TYPE;
            redraw();
        } else {
            type.set(TYPE);
        }
    }
    public ObjectProperty<AxisType> typeProperty() {
        if (null == type) {
            type = new ObjectPropertyBase<AxisType>(_type) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() {  return Axis.this;  }
                @Override public String getName() {  return "axisType";  }
            };
            _type = null;
        }
        return type;
    }

    public Orientation getOrientation() { return null == orientation ? _orientation : orientation.get(); }
    public void setOrientation(final Orientation ORIENTATION) {
        if (null == orientation) {
            _orientation = ORIENTATION;
            redraw();
        } else {
            orientation.set(ORIENTATION);
        }
    }
    public ObjectProperty<Orientation> orientationProperty() {
        if (null == orientation) {
            orientation = new ObjectPropertyBase<Orientation>(_orientation) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() {  return "orientation";  }
            };
            _orientation = null;
        }
        return orientation;
    }

    public Position getPosition() { return null == position ? _position : position.get(); }
    public void setPosition(final Position POSITION) {
        if (null == position) {
            _position = POSITION;
            redraw();
        } else {
            position.set(POSITION);
        }
    }
    public ObjectProperty<Position> positionProperty() {
        if (null == position) {
            position = new ObjectPropertyBase<Position>(_position) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "position"; }
            };
            _position = null;
        }
        return position;
    }

    public Color getAxisBackgroundColor() { return null == axisBackgroundColor ? _axisBackgroundColor : axisBackgroundColor.get(); }
    public void setAxisBackgroundColor(final Color COLOR) {
        if (null == axisBackgroundColor) {
            _axisBackgroundColor = COLOR;
            redraw();
        } else {
            axisBackgroundColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> axisBackgroundColorProperty() {
        if (null == axisBackgroundColor) {
            axisBackgroundColor = new ObjectPropertyBase<Color>(_axisBackgroundColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "axisBackgroundColor"; }
            };
            _axisBackgroundColor = null;
        }
        return axisBackgroundColor;
    }

    public Color getAxisColor() { return null == axisColor ? _axisColor : axisColor.get(); }
    public void setAxisColor(final Color COLOR) {
        if (null == axisColor) {
            _axisColor = COLOR;
            redraw();
        } else {
            axisColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> axisColorProperty() {
        if (null == axisColor) {
            axisColor = new ObjectPropertyBase<Color>(_axisColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "axisColor"; }
            };
            _axisColor = null;
        }
        return axisColor;
    }

    public Color getTickLabelColor() { return null == tickLabelColor ? _tickLabelColor : tickLabelColor.get(); }
    public void setTickLabelColor(final Color COLOR) {
        if (null == tickLabelColor) {
            _tickLabelColor = COLOR;
            redraw();
        } else {
            tickLabelColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> tickLabelColorProperty() {
        if (null == tickLabelColor) {
            tickLabelColor = new ObjectPropertyBase<Color>(_tickLabelColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "tickLabelColor"; }
            };
            _tickLabelColor = null;
        }
        return tickLabelColor;
    }

    public Color getTitleColor() { return null == titleColor ? _titleColor : titleColor.get(); }
    public void setTitleColor(final Color COLOR) {
        if (null == titleColor) {
            _titleColor = COLOR;
            redraw();
        } else {
            titleColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> titleColorProperty() {
        if (null == titleColor) {
            titleColor = new ObjectPropertyBase<Color>(_titleColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "titleColor"; }
            };
            _titleColor = null;
        }
        return titleColor;
    }

    public Color getMinorTickMarkColor() { return null == minorTickMarkColor ? _minorTickMarkColor : minorTickMarkColor.get(); }
    public void setMinorTickMarkColor(final Color COLOR) {
        if (null == minorTickMarkColor) {
            _minorTickMarkColor = COLOR;
            redraw();
        } else {
            minorTickMarkColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> minorTickMarkColorProperty() {
        if (null == minorTickMarkColor) {
            minorTickMarkColor = new ObjectPropertyBase<Color>(_minorTickMarkColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "minorTickMarkColor"; }
            };
            _minorTickMarkColor = null;
        }
        return minorTickMarkColor;
    }

    public Color getMediumTickMarkColor() { return null == mediumTickMarkColor ? _mediumTickMarkColor : mediumTickMarkColor.get(); }
    public void setMediumTickMarkColor(final Color COLOR) {
        if (null == mediumTickMarkColor) {
            _mediumTickMarkColor = COLOR;
            redraw();
        } else {
            mediumTickMarkColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> mediumTickMarkColorProperty() {
        if (null == mediumTickMarkColor) {
            mediumTickMarkColor = new ObjectPropertyBase<Color>(_mediumTickMarkColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "mediumTickMarkColor"; }
            };
            _mediumTickMarkColor = null;
        }
        return mediumTickMarkColor;
    }

    public Color getMajorTickMarkColor() { return null == majorTickMarkColor ? _majorTickMarkColor : majorTickMarkColor.get(); }
    public void setMajorTickMarkColor(final Color COLOR) {
        if (null == majorTickMarkColor) {
            _majorTickMarkColor = COLOR;
            redraw();
        } else {
            majorTickMarkColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> majorTickMarkColorProperty() {
        if (null == majorTickMarkColor) {
            majorTickMarkColor = new ObjectPropertyBase<Color>(_majorTickMarkColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "majorTickMarkColor"; }
            };
            _majorTickMarkColor = null;
        }
        return majorTickMarkColor;
    }

    public Color getZeroColor() { return null == zeroColor ? _zeroColor : zeroColor.get(); }
    public void setZeroColor(final Color COLOR) {
        if (null == zeroColor) {
            _zeroColor = COLOR;
            redraw();
        } else {
            zeroColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> zeroColorProperty() {
        if (null == zeroColor) {
            zeroColor = new ObjectPropertyBase<Color>(_zeroColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "zeroColor"; }
            };
            _zeroColor = null;
        }
        return zeroColor;
    }

    public double getZeroPosition() { return null == zeroPosition ? _zeroPosition : zeroPosition.get(); }
    private void setZeroPosition(final double POSITION) {
        if (null == zeroPosition) {
            _zeroPosition = POSITION;
        } else {
            zeroPosition.set(POSITION);
        }
    }
    public ReadOnlyDoubleProperty zeroPositionProperty() {
        if (null == zeroPosition) {
            zeroPosition = new DoublePropertyBase(_zeroPosition) {
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "zeroPosition"; }
            };
        }
        return zeroPosition;
    }

    protected double getMajorTickSpace() { return _majorTickSpace; }
    protected void setMajorTickSpace(final double SPACE) { _majorTickSpace = SPACE; }

    protected double getMinorTickSpace() { return _minorTickSpace; }
    protected void setMinorTickSpace(final double SPACE) { _minorTickSpace = SPACE; }

    public boolean getMajorTickMarksVisible() { return null == majorTickMarksVisible ? _majorTickMarksVisible : majorTickMarksVisible.get(); }
    public void setMajorTickMarksVisible(final boolean VISIBLE) {
        if (null == majorTickMarksVisible) {
            _majorTickMarksVisible = VISIBLE;
            redraw();
        } else {
            majorTickMarksVisible.set(VISIBLE);
        }
    }
    public BooleanProperty majorTickMarksVisibleProperty() { 
        if (null == majorTickMarksVisible) {
            majorTickMarksVisible = new BooleanPropertyBase(_majorTickMarksVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "majorTickMarksVisible"; }
            };
        }
        return majorTickMarksVisible;
    }

    public boolean getMediumTickMarksVisible() { return null == mediumTickMarksVisible ? _mediumTickMarksVisible : mediumTickMarksVisible.get(); }
    public void setMediumTickMarksVisible(final boolean VISIBLE) {
        if (null == mediumTickMarksVisible) {
            _mediumTickMarksVisible = VISIBLE;
            redraw();
        } else {
            mediumTickMarksVisible.set(VISIBLE);
        }
    }
    public BooleanProperty mediumTickMarksVisibleProperty() {
        if (null == mediumTickMarksVisible) {
            mediumTickMarksVisible = new BooleanPropertyBase(_mediumTickMarksVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "mediumTickMarksVisible"; }
            };
        }
        return mediumTickMarksVisible;
    }

    public boolean getMinorTickMarksVisible() { return null == minorTickMarksVisible ? _minorTickMarksVisible : minorTickMarksVisible.get(); }
    public void setMinorTickMarksVisible(final boolean VISIBLE) {
        if (null == minorTickMarksVisible) {
            _minorTickMarksVisible = VISIBLE;
            redraw();
        } else {
            minorTickMarksVisible.set(VISIBLE);
        }
    }
    public BooleanProperty minorTickMarksVisibleProperty() {
        if (null == minorTickMarksVisible) {
            minorTickMarksVisible = new BooleanPropertyBase(_minorTickMarksVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "minorTickMarksVisible"; }
            };
        }
        return minorTickMarksVisible;
    }

    public boolean getTickLabelsVisible() { return null == tickLabelsVisible ? _tickLabelsVisible : tickLabelsVisible.get(); }
    public void setTickLabelsVisible(final boolean VISIBLE) {
        if (null == tickLabelsVisible) {
            _tickLabelsVisible = VISIBLE;
            redraw();
        } else {
            tickLabelsVisible.set(VISIBLE);
        }
    }
    public BooleanProperty tickLabelsVisibleProperty() {
        if (null == tickLabelsVisible) {
            tickLabelsVisible = new BooleanPropertyBase(_tickLabelsVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "tickLabelsVisible"; }
            };
        }
        return tickLabelsVisible;
    }

    public boolean isOnlyFirstAndLastTickLabelVisible() { return null == onlyFirstAndLastTickLabelVisible ? _onlyFirstAndLastTickLabelVisible : onlyFirstAndLastTickLabelVisible.get(); }
    public void setOnlyFirstAndLastTickLabelVisible(final boolean VISIBLE) {
        if (null == onlyFirstAndLastTickLabelVisible) {
            _onlyFirstAndLastTickLabelVisible = VISIBLE;
            redraw();
        } else {
            onlyFirstAndLastTickLabelVisible.set(VISIBLE);
        }
    }
    public BooleanProperty onlyFirstAndLastTickLabelVisibleProperty() {
        if (null == onlyFirstAndLastTickLabelVisible) {
            onlyFirstAndLastTickLabelVisible = new BooleanPropertyBase(_onlyFirstAndLastTickLabelVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "onlyFirstAndLastTickLabelVisible"; }
            };
        }
        return onlyFirstAndLastTickLabelVisible;
    }

    public Locale getLocale() { return null == locale ? _locale : locale.get(); }
    public void setLocale(final Locale LOCALE) {
        if (null == locale) {
            _locale = LOCALE;
            tickLabelFormatString = new StringBuilder("%.").append(Integer.toString(getDecimals())).append("f").toString();
            redraw();
        } else {
            locale.set(LOCALE);
        }
    }
    public ObjectProperty<Locale> localeProperty() {
        if (null == locale) {
            locale = new ObjectPropertyBase<Locale>(_locale) {
                @Override protected void invalidated() {
                    tickLabelFormatString = new StringBuilder("%.").append(Integer.toString(getDecimals())).append("f").toString();
                    redraw();
                }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "locale"; }
            };
            _locale = null;
        }
        return locale;
    }

    public int getDecimals() { return null == decimals ? _decimals : decimals.get(); }
    public void setDecimals(final int DECIMALS) {
        if (null == decimals) {
            _decimals = Helper.clamp(0, 12, DECIMALS);
            tickLabelFormatString = new StringBuilder("%.").append(Integer.toString(_decimals)).append("f").toString();
            redraw();
        } else {
            decimals.set(DECIMALS);
        }
    }
    public IntegerProperty decimals() {
        if (null == decimals) {
            decimals = new IntegerPropertyBase(_decimals) {
                @Override protected void invalidated() {
                    set(Helper.clamp(0, 12, get()));
                    tickLabelFormatString = new StringBuilder("%.").append(Integer.toString(get())).append("f").toString();
                    redraw();
                }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "decimals"; }
            };
        }
        return decimals;
    }

    public TickLabelOrientation getTickLabelOrientation() { return null == tickLabelOrientation ? _tickLabelOrientation : tickLabelOrientation.get(); }
    public void setTickLabelOrientation(final TickLabelOrientation ORIENTATION) {
        if (null == tickLabelOrientation) {
            _tickLabelOrientation = ORIENTATION;
            redraw();
        } else {
            tickLabelOrientation.set(ORIENTATION);
        }
    }
    public ObjectProperty<TickLabelOrientation> tickLabelOrientationProperty() {
        if (null == tickLabelOrientation) {
            tickLabelOrientation = new ObjectPropertyBase<TickLabelOrientation>(_tickLabelOrientation) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() {  return Axis.this;  }
                @Override public String getName() {  return "tickLabelOrientation";  }
            };
            _tickLabelOrientation = null;
        }
        return tickLabelOrientation;
    }

    public ZoneId getZoneId() { return null == zoneId ? _zoneId : zoneId.get(); }
    public void setZoneId(final ZoneId ZONE_ID) {
        if (null == zoneId) {
            _zoneId = ZONE_ID;
            redraw();
        } else {
            zoneId.set(ZONE_ID);
        }
    }
    public ObjectProperty<ZoneId> zoneIdProperty() {
        if (null == zoneId) {
            zoneId = new ObjectPropertyBase<ZoneId>(_zoneId) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "zoneId"; }
            };
            _zoneId = null;
        }
        return zoneId;
    }

    public String getDateTimeFormatPattern() { return null == dateTimeFormatPattern ? _dateTimeFormatPattern : dateTimeFormatPattern.get();}
    public void setDateTimeFormatPattern(final String PATTERN) {
        if (null == dateTimeFormatPattern) {
            _dateTimeFormatPattern = PATTERN;
            dateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN);
            redraw();
        } else {
            dateTimeFormatPattern.set(PATTERN);
        }
    }
    public StringProperty dateTimeFormatPatternProperty() {
        if (null == dateTimeFormatPattern) {
            dateTimeFormatPattern = new StringPropertyBase(_dateTimeFormatPattern) {
                @Override protected void invalidated() {
                    dateTimeFormatter = DateTimeFormatter.ofPattern(get());
                    redraw();
                }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "dateTimeFormat"; }
            };
            _dateTimeFormatPattern = null;
        }
        return dateTimeFormatPattern;
    }
    
    public StringConverter<Number> getNumberFormatter() { return numberFormatter; }
    public void setNumberFormatter(final StringConverter<Number> FORMATTER) {
        numberFormatter = FORMATTER;
        redraw();
    }

    public TickLabelFormat getTickLabelFormat() { return null == tickLabelFormat ? _tickLabelFormat : tickLabelFormat.get(); }
    public void setTickLabelFormat(final TickLabelFormat FORMAT) {
        if (null == tickLabelFormat) {
            _tickLabelFormat = FORMAT;
            redraw();
        } else {
            tickLabelFormat.set(FORMAT);
        }
    }
    public ObjectProperty<TickLabelFormat> tickLabelFormatProperty() {
        if (null == tickLabelFormat) {
            tickLabelFormat = new ObjectPropertyBase<TickLabelFormat>(_tickLabelFormat) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "tickLabelFormat"; }
            };
            _tickLabelFormat = null;
        }
        return tickLabelFormat;
    }

    public boolean isAutoFontSize() { return null == autoFontSize ? _autoFontSize : autoFontSize.get(); }
    public void setAutoFontSize(final boolean AUTO) {
        if (null == autoFontSize) {
            _autoFontSize = AUTO;
            redraw();
        } else {
            autoFontSize.set(AUTO);
        }
    }
    public BooleanProperty autoFontSizeProperty() {
        if (null == autoFontSize) {
            autoFontSize = new BooleanPropertyBase(_autoFontSize) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "autoFontSize"; }
            };
        }
        return autoFontSize;
    }

    public double getTickLabelFontSize() { return null == tickLabelFontSize ? _tickLabelFontSize : tickLabelFontSize.get(); }
    public void setTickLabelFontSize(final double SIZE) {
        if (null == tickLabelFontSize) {
            _tickLabelFontSize = SIZE;
            tickLabelFont      = Fonts.latoLight(SIZE);
            redraw();
        } else {
            tickLabelFontSize.set(SIZE);
        }
    }
    public DoubleProperty tickLabelFontSizeProperty() {
        if (null == tickLabelFontSize) {
            tickLabelFontSize = new DoublePropertyBase(_tickLabelFontSize) {
                @Override protected void invalidated() {
                    tickLabelFont = Fonts.latoLight(get());
                    redraw();
                }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "tickLabelFontSize"; }
            };
        }
        return tickLabelFontSize;
    }

    public double getTitleFontSize() { return null == titleFontSize ? _titleFontSize : titleFontSize.get(); }
    public void setTitleFontSize(final double SIZE) {
        if (null == titleFontSize) {
            _titleFontSize = SIZE;
            titleFont      = Fonts.latoRegular(getTitleFontSize());
            redraw();
        } else {
            titleFontSize.set(SIZE);
        }
    }
    public DoubleProperty titleFontSizeProperty() {
        if (null == titleFontSize) {
            titleFontSize = new DoublePropertyBase(_titleFontSize) {
                @Override protected void invalidated() {
                    titleFont = Fonts.latoRegular(get());
                    redraw();
                }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "titleFontSize"; }
            };
        }
        return titleFontSize;
    }

    public List<String> getCategories() { return categories; }
    public void setCategories(final String... CATEGORIES) { setCategories(Arrays.asList(CATEGORIES)); }
    public void setCategories(final List<String> CATEGORIES) {
        categories.clear();
        CATEGORIES.forEach(category -> categories.add(category));
        redraw();
    }

    public boolean isValueOnAxis(final Double VALUE) {
        return Double.compare(VALUE, getMinValue()) >= 0 && Double.compare(VALUE, getMaxValue()) <= 0;
    }
    public boolean isValueOnAxis(final LocalDateTime DATE_TIME) {
        return DATE_TIME.isAfter(getStart()) && DATE_TIME.isBefore(getEnd());
    }

    public void setMinMax(final double MIN_VALUE, final double MAX_VALUE) {
        setMinValue(MIN_VALUE);
        setMaxValue(MAX_VALUE);
        resize();
    }

    public double getRange() { return getMaxValue() - getMinValue(); }

    public void setTickMarkColor(final Color COLOR) {
        setMinorTickMarkColor(COLOR);
        setMediumTickMarkColor(COLOR);
        setMajorTickMarkColor(COLOR);
    }

    public void setTickMarksVisible(final boolean VISIBLE) {
        setMinorTickMarksVisible(VISIBLE);
        setMediumTickMarksVisible(VISIBLE);
        setMajorTickMarksVisible(VISIBLE);
    }

    public void shift(final double VALUE) {
        setMinMax(getMinValue() + VALUE, getMaxValue() + VALUE);
    }
    
    public double getValueForDisplay(final double posInAxis) {
        return posInAxis / width * Helper.calcNiceNumber((getMaxValue() - getMinValue()), false) + getMinValue();
    }

    private void calcAutoScale() {
        double maxNoOfMajorTicks = 10;
        double maxNoOfMinorTicks = 10;
        double niceRange         = (Helper.calcNiceNumber((getMaxValue() - getMinValue()), false));
        setMajorTickSpace(Helper.calcNiceNumber(niceRange / (maxNoOfMajorTicks - 1), true));
        setMinorTickSpace(Helper.calcNiceNumber(getMajorTickSpace() / (maxNoOfMinorTicks - 1), true));
        double niceMinValue = (Math.floor(getMinValue() / getMajorTickSpace()) * getMajorTickSpace());
        double niceMaxValue = (Math.ceil(getMaxValue() / getMajorTickSpace()) * getMajorTickSpace());
        setMinValue(niceMinValue);
        setMaxValue(niceMaxValue);
    }

    private void calcScale() {
        double maxNoOfMajorTicks = 10;
        double maxNoOfMinorTicks = 10;

        // TFE, 20220329: overwrites user set values!
//        setMajorTickSpace(Helper.calcNiceNumber(getRange() / (maxNoOfMajorTicks - 1), false));
//        setMinorTickSpace(Helper.calcNiceNumber(getMajorTickSpace() / (maxNoOfMinorTicks - 1), false));
    }

    private double calcTextWidth(final Font FONT, final String TEXT) {
        Text text = new Text(TEXT);
        text.setFont(FONT);
        double width = text.getBoundsInParent().getWidth();
        text = null;
        return width;
    }

    private double toNumericValue(final LocalDateTime DATE) {
        return Helper.toMillis(DATE, Helper.getZoneOffset(getZoneId()));
    }
    private LocalDateTime toRealValue(final double VALUE) {
        return toLocalDateTime((long) VALUE);
    }
    private LocalDateTime toLocalDateTime(final long SECONDS) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(SECONDS), ZoneId.systemDefault());
    }

    private List<LocalDateTime> createTickValues(final double WIDTH, final LocalDateTime START, final LocalDateTime END) {
        List<LocalDateTime> dateList = new ArrayList<>();
        LocalDateTime       dateTime = LocalDateTime.now();

        if (null == START || null == END) return dateList;

        // The preferred gap which should be between two tick marks.
        double majorTickSpace = 100;
        double noOfTicks      = WIDTH / majorTickSpace;

        List<LocalDateTime> previousDateList = new ArrayList<>();
        Interval            previousInterval = Interval.values()[0];

        // Starting with the greatest interval, add one of each dateTime unit.
        for (Interval interval : Interval.values()) {
            // Reset the dateTime.
            dateTime = LocalDateTime.of(START.toLocalDate(), START.toLocalTime());
            // Clear the list.
            dateList.clear();
            previousDateList.clear();
            currentInterval = interval;

            // Loop as long we exceeded the END bound.
            while(dateTime.isBefore(END)) {
                dateList.add(dateTime);
                dateTime = dateTime.plus(interval.getAmount(), interval.getInterval());
            }

            // Then check the size of the list. If it is greater than the amount of ticks, take that list.
            if (dateList.size() > noOfTicks) {
                dateTime = LocalDateTime.of(START.toLocalDate(), START.toLocalTime());
                // Recheck if the previous interval is better suited.
                while(dateTime.isBefore(END) || dateTime.isEqual(END)) {
                    previousDateList.add(dateTime);
                    dateTime = dateTime.plus(previousInterval.getAmount(), previousInterval.getInterval());
                }
                break;
            }

            previousInterval = interval;
        }
        if (previousDateList.size() - noOfTicks > noOfTicks - dateList.size()) {
            dateList = previousDateList;
            currentInterval = previousInterval;
        }

        // At last add the END bound.
        dateList.add(END);

        List<LocalDateTime> evenDateList = makeDatesEven(dateList, dateTime);
        // If there are at least three dates, check if the gap between the START date and the second date is at least half the gap of the second and third date.
        // Do the same for the END bound.
        // If gaps between dates are to small, remove one of them.
        // This can occur, e.g. if the START bound is 25.12.2013 and years are shown. Then the next year shown would be 2014 (01.01.2014) which would be too narrow to 25.12.2013.
        if (evenDateList.size() > 2) {
            LocalDateTime secondDate       = evenDateList.get(1);
            LocalDateTime thirdDate        = evenDateList.get(2);
            LocalDateTime lastDate         = evenDateList.get(dateList.size() - 2);
            LocalDateTime previousLastDate = evenDateList.get(dateList.size() - 3);

            // If the second date is too near by the START bound, remove it.
            if (secondDate.toEpochSecond(ZoneOffset.ofHours(0)) - START.toEpochSecond(ZoneOffset.ofHours(0)) < thirdDate.toEpochSecond(ZoneOffset.ofHours(0)) - secondDate.toEpochSecond(ZoneOffset.ofHours(0))) {
                evenDateList.remove(secondDate);
            }

            // If difference from the END bound to the last date is less than the half of the difference of the previous two dates,
            // we better remove the last date, as it comes to close to the END bound.
            if (END.toEpochSecond(ZoneOffset.ofHours(0)) - lastDate.toEpochSecond(ZoneOffset.ofHours(0)) < ((lastDate.toEpochSecond(ZoneOffset.ofHours(0)) - previousLastDate.toEpochSecond(ZoneOffset.ofHours(0)) * 0.5))) {
                evenDateList.remove(lastDate);
            }
        }
        return evenDateList;
    }
    private List<LocalDateTime> makeDatesEven(List<LocalDateTime> dates, LocalDateTime dateTime) {
        // If the dates contain more dates than just the lower and upper bounds, make the dates in between even.
        if (dates.size() > 2) {
            List<LocalDateTime> evenDates = new ArrayList<>();

            // For each interval, modify the date slightly by a few millis, to make sure they are different days.
            // This is because Axis stores each value and won't update the tick labels, if the value is already known.
            // This happens if you display days and then add a date many years in the future the tick label will still be displayed as day.
            for (int i = 0; i < dates.size(); i++) {
                dateTime = dates.get(i);
                switch (currentInterval.getInterval()) {
                    case YEARS:
                        // If its not the first or last date (lower and upper bound), make the year begin with first month and let the months begin with first day.
                        if (i != 0 && i != dates.size() - 1) {
                            dateTime.withMonth(1);
                            dateTime.withDayOfMonth(1);
                        }
                        dateTime.withHour(0);
                        dateTime.withMinute(0);
                        dateTime.withSecond(0);
                        dateTime.withNano(6000000);
                        break;
                    case MONTHS:
                        // If its not the first or last date (lower and upper bound), make the months begin with first day.
                        if (i != 0 && i != dates.size() - 1) {
                            dateTime.withDayOfMonth(1);
                        }
                        dateTime.withHour(0);
                        dateTime.withMinute(0);
                        dateTime.withSecond(0);
                        dateTime.withNano(5000000);
                        break;
                    case WEEKS:
                        // Make weeks begin with first day of week?
                        dateTime.withHour(0);
                        dateTime.withMinute(0);
                        dateTime.withSecond(0);
                        dateTime.withNano(4000000);
                        break;
                    case DAYS:
                        dateTime.withHour(0);
                        dateTime.withMinute(0);
                        dateTime.withSecond(0);
                        dateTime.withNano(3000000);
                        break;
                    case HOURS:
                        if (i != 0 && i != dates.size() - 1) {
                            dateTime.withMinute(0);
                            dateTime.withSecond(0);
                        }
                        dateTime.withNano(2000000);
                        break;
                    case MINUTES:
                        if (i != 0 && i != dates.size() - 1) {
                            dateTime.withSecond(0);
                        }
                        dateTime.withNano(1000000);
                        break;
                    case SECONDS:
                        dateTime.withSecond(0);
                        break;

                }
                evenDates.add(dateTime);
            }

            return evenDates;
        } else {
            return dates;
        }
    }
    
    private String formatNumber(final Locale locale, final double number) { 
        if (numberFormatter == null) {
            return String.format(locale, tickLabelFormatString, number);
        } else {
            return numberFormatter.toString(number);
        }
    }


    // ******************** Drawing *******************************************
    private void drawAxis() {
        if (Double.compare(stepSize, 0) <= 0) return;

        axisCtx.clearRect(0, 0, width, height);
        axisCtx.setFill(getAxisBackgroundColor());
        axisCtx.fillRect(0, 0, width, height);
        axisCtx.setFont(tickLabelFont);
        axisCtx.setTextBaseline(VPos.CENTER);

        AxisType        axisType                           = getType();
        boolean         isAutoScale                        = isAutoScale();
        double          minValue                           = getMinValue();
        double          maxValue                           = getMaxValue();
        boolean         tickLabelsVisible                  = getTickLabelsVisible();
        boolean         isOnlyFirstAndLastTickLabelVisible = isOnlyFirstAndLastTickLabelVisible();
        double          tickLabelFontSize                  = getTickLabelFontSize();
        TickLabelFormat tickLabelFormat                    = getTickLabelFormat();
        Color           tickLabelColor                     = getTickLabelColor();
        Color           zeroColor                          = getZeroColor();
        Color           majorTickMarkColor                 = getMajorTickMarkColor();
        boolean         majorTickMarksVisible              = getMajorTickMarksVisible();
        Color           mediumTickMarkColor                = getMediumTickMarkColor();
        boolean         mediumTickMarksVisible             = getMediumTickMarksVisible();
        Color           minorTickMarkColor                 = getMinorTickMarkColor();
        boolean         minorTickMarksVisible              = getMinorTickMarksVisible();
        double          majorLineWidth                     = size * 0.007 < MIN_MAJOR_LINE_WIDTH ? MIN_MAJOR_LINE_WIDTH : size * 0.007;
        double          mediumLineWidth                    = size * 0.006 < MIN_MEDIUM_LINE_WIDTH ? MIN_MEDIUM_LINE_WIDTH : size * 0.005;
        double          minorLineWidth                     = size * 0.005 < MIN_MINOR_LINE_WIDTH ? MIN_MINOR_LINE_WIDTH : size * 0.003;
        double          maxMajorTickMarkLength;
        double          maxMediumTickMarkLength;
        double          maxMinorTickMarkLength;
        double          textPosition;
        double          minPosition;
        double          maxPosition;
        if (VERTICAL == getOrientation()) {
            minPosition             = 0;
            maxPosition             = height;
            textPosition            = width * 0.3;
            maxMajorTickMarkLength  = width * 0.2;
            maxMediumTickMarkLength = width * 0.175;
            maxMinorTickMarkLength  = width * 0.1;
        } else {
            minPosition             = 0;
            maxPosition             = width;
            textPosition            = height * 0.5;
            maxMajorTickMarkLength  = height * 0.2;
            maxMediumTickMarkLength = height * 0.175;
            maxMinorTickMarkLength  = height * 0.1;
        }

        Locale      locale            = getLocale();
        Orientation orientation       = getOrientation();
        Position    position          = getPosition();
        double      anchorX           = (Position.LEFT == position || Position.CENTER == position) ? 0 : getZeroPosition();
        double      anchorXPlusOffset = anchorX + width;
        double      anchorY           = (Position.BOTTOM == position || Position.CENTER == position) ? 0 : getZeroPosition();
        double      anchorYPlusOffset = anchorY + height;
        boolean     isMinValue;
        boolean     isZero;
        boolean     isMaxValue;
        double      innerPointX;
        double      innerPointY;
        double      outerPointX;
        double      outerPointY;
        double      mediumPointX;
        double      mediumPointY;
        double      minorPointX;
        double      minorPointY;
        double      textPointX;
        double      textPointY;
        double      maxTextWidth;


        if (AxisType.LINEAR == axisType || AxisType.TEXT == axisType) {
            // ******************** Linear ************************************
            boolean    fullRange        = (minValue < 0 && maxValue > 0);
            double     minorTickSpace   = getMinorTickSpace();
            double     majorTickSpace   = getMajorTickSpace();
            double     tmpStepSize      = minorTickSpace;
            BigDecimal minorTickSpaceBD = BigDecimal.valueOf(minorTickSpace);
            BigDecimal majorTickSpaceBD = BigDecimal.valueOf(majorTickSpace);
            BigDecimal mediumCheck2     = BigDecimal.valueOf(2 * minorTickSpace);
            BigDecimal mediumCheck5     = BigDecimal.valueOf(5 * minorTickSpace);
            BigDecimal counterBD        = BigDecimal.valueOf(minValue);
            double     counter          = minValue;
            double     range            = getRange();
            int        noOfCategories   = categories.size();

            axisCtx.setStroke(getAxisColor());
            axisCtx.setLineWidth(majorLineWidth);

            // Draw axis
            if (VERTICAL == orientation) {
                switch(position) {
                    case LEFT : axisCtx.strokeLine(anchorXPlusOffset, minPosition, anchorXPlusOffset, maxPosition); break;
                    case RIGHT: axisCtx.strokeLine(anchorX, minPosition, anchorX, maxPosition); break;
                    default   : axisCtx.strokeLine(anchorX, minPosition, anchorX, maxPosition); break;
                }
            } else {
                switch(position) {
                    case BOTTOM: axisCtx.strokeLine(minPosition, anchorY, maxPosition, anchorY); break;
                    case TOP   : axisCtx.strokeLine(minPosition, anchorYPlusOffset, maxPosition, anchorYPlusOffset); break;
                    default    : axisCtx.strokeLine(minPosition, anchorY, maxPosition, anchorY); break;
                }
            }


            // Main Loop for tick marks and labels
            BigDecimal tmpStepBD = new BigDecimal(tmpStepSize);
            tmpStepBD = tmpStepBD.setScale(6, RoundingMode.HALF_UP); // newScale == number of decimals taken into account
            double tmpStep          = tmpStepBD.doubleValue();
            int    tickMarkCounter  = 0;
            int    tickLabelCounter = 0;
            for (double i = 0; Double.compare(-range - tmpStep, i) <= 0; i -= tmpStep) {
                double fixedPosition = (counter - minValue) * stepSize;
                if (VERTICAL == orientation) {
                    if (Position.LEFT == position) {
                        innerPointX  = anchorXPlusOffset - maxMajorTickMarkLength;
                        innerPointY  = fixedPosition;
                        mediumPointX = anchorXPlusOffset - maxMediumTickMarkLength;
                        mediumPointY = fixedPosition;
                        minorPointX  = anchorXPlusOffset - maxMinorTickMarkLength;
                        minorPointY  = fixedPosition;
                        outerPointX  = anchorXPlusOffset;
                        outerPointY  = fixedPosition;
                        textPointX   = anchorXPlusOffset - textPosition;
                        textPointY   = fixedPosition;
                        maxTextWidth = 0.6 * width;
                    } else if (Position.RIGHT == position) {
                        innerPointX  = anchorX + maxMajorTickMarkLength;
                        innerPointY  = fixedPosition;
                        mediumPointX = anchorX + maxMediumTickMarkLength;
                        mediumPointY = fixedPosition;
                        minorPointX  = anchorX + maxMinorTickMarkLength;
                        minorPointY  = fixedPosition;
                        outerPointX  = anchorX;
                        outerPointY  = fixedPosition;
                        textPointX   = anchorXPlusOffset;
                        textPointY   = fixedPosition;
                        maxTextWidth = textPosition;
                    } else {
                        innerPointX  = anchorX + maxMajorTickMarkLength;
                        innerPointY  = fixedPosition;
                        mediumPointX = anchorX + maxMediumTickMarkLength;
                        mediumPointY = fixedPosition;
                        minorPointX  = anchorX + maxMinorTickMarkLength;
                        minorPointY  = fixedPosition;
                        outerPointX  = anchorX;
                        outerPointY  = fixedPosition;
                        textPointX   = anchorXPlusOffset;
                        textPointY   = fixedPosition;
                        maxTextWidth = textPosition;
                    }
                } else {
                    if (Position.BOTTOM == position) {
                        innerPointX  = fixedPosition;
                        innerPointY  = anchorY + maxMajorTickMarkLength;
                        mediumPointX = fixedPosition;
                        mediumPointY = anchorY + maxMediumTickMarkLength;
                        minorPointX  = fixedPosition;
                        minorPointY  = anchorY + maxMinorTickMarkLength;
                        outerPointX  = fixedPosition;
                        outerPointY  = anchorY;
                        textPointX   = fixedPosition;
                        textPointY   = innerPointY + textPosition - tickLabelFontSize * 0.8;
                        maxTextWidth = majorTickSpace * stepSize;
                    } else if (Position.TOP == position) {
                        innerPointX  = fixedPosition;
                        innerPointY  = anchorYPlusOffset - maxMajorTickMarkLength;
                        mediumPointX = fixedPosition;
                        mediumPointY = anchorYPlusOffset - maxMediumTickMarkLength;
                        minorPointX  = fixedPosition;
                        minorPointY  = anchorYPlusOffset - maxMinorTickMarkLength;
                        outerPointX  = fixedPosition;
                        outerPointY  = anchorYPlusOffset;
                        textPointX   = fixedPosition;
                        textPointY   = innerPointY - textPosition + tickLabelFontSize * 0.5;
                        maxTextWidth = majorTickSpace * stepSize;
                    } else {
                        innerPointX  = fixedPosition;
                        innerPointY  = anchorY + maxMajorTickMarkLength;
                        mediumPointX = fixedPosition;
                        mediumPointY = anchorY + maxMediumTickMarkLength;
                        minorPointX  = fixedPosition;
                        minorPointY  = anchorY + maxMinorTickMarkLength;
                        outerPointX  = fixedPosition;
                        outerPointY  = anchorY;
                        textPointX   = fixedPosition;
                        textPointY   = innerPointY + textPosition - tickLabelFontSize * 0.8;
                        maxTextWidth = majorTickSpace * stepSize;
                    }
                }

                if (Double.compare(counterBD.setScale(12, RoundingMode.HALF_UP).remainder(majorTickSpaceBD).doubleValue(), 0.0) == 0) {
                    // Draw major tick mark
                    isMinValue = Double.compare(minValue, counter) == 0;
                    isMaxValue = Double.compare(maxValue, counter) == 0;
                    if (VERTICAL == orientation) {
                        isZero = Double.compare(0.0, maxValue - counter + minValue) == 0;
                    } else {
                        isZero = Double.compare(0.0, counter) == 0;
                    }

                    if (isZero) { setZeroPosition(fixedPosition); }

                    if (majorTickMarksVisible) {
                        drawTickMark((fullRange && isZero) ? zeroColor : majorTickMarkColor, majorLineWidth, innerPointX, innerPointY, outerPointX, outerPointY);
                    } else if (minorTickMarksVisible) {
                        drawTickMark((fullRange && isZero) ? zeroColor : minorTickMarkColor, minorLineWidth, minorPointX, minorPointY, outerPointX, outerPointY);
                    }

                    // Draw tick labels
                    if (tickLabelsVisible && tickLabelFontSize > 6) {
                        String tickLabelString;
                        if (AxisType.LINEAR == axisType) {
                            if (TickLabelFormat.NUMBER == tickLabelFormat) {
                                tickLabelString = Orientation.HORIZONTAL == orientation ? formatNumber(locale, (minValue - i)) : formatNumber(locale, maxValue - counter + minValue);
                            } else {
                                tickLabelString = Orientation.HORIZONTAL == orientation ? Helper.secondsToHHMMString(Helper.toSeconds(Helper.toRealValue(minValue - i), Helper.getZoneOffset())) : formatNumber(locale, maxValue - counter + minValue);
                            }
                        } else if (AxisType.TEXT == axisType) {
                            if (tickLabelCounter < noOfCategories) {
                                tickLabelString = categories.get(tickLabelCounter);
                            } else {
                                tickLabelString = "";
                            }
                            if (isAutoScale) {
                                tickLabelCounter += (int) majorTickSpace;
                            } else {
                                tickLabelCounter++;
                            }
                        } else {
                            // Date Axis
                            tickLabelString = dateTimeFormatter.format(toLocalDateTime((long) (minValue - i) * 1000));
                        }
                        drawTickLabel(isOnlyFirstAndLastTickLabelVisible, isZero, isMinValue, isMaxValue, fullRange, zeroColor, tickLabelColor, textPointX, textPointY, maxTextWidth, tickLabelString, orientation);
                    }
                } else if (mediumTickMarksVisible && Double.compare(minorTickSpaceBD.setScale(12, RoundingMode.HALF_UP).remainder(mediumCheck2).doubleValue(), 0.0) != 0.0 &&
                           Double.compare(counterBD.setScale(12, RoundingMode.HALF_UP).remainder(mediumCheck5).doubleValue(), 0.0) == 0.0) {
                    // Draw medium tick mark
                    drawTickMark(mediumTickMarkColor, mediumLineWidth, mediumPointX, mediumPointY, outerPointX, outerPointY);
                } else if (minorTickMarksVisible && Double.compare(counterBD.setScale(12, RoundingMode.HALF_UP).remainder(minorTickSpaceBD).doubleValue(), 0.0) == 0) {
                    // Draw minor tick mark
                    drawTickMark(minorTickMarkColor, minorLineWidth, minorPointX, minorPointY, outerPointX, outerPointY);
                } else if (!isAutoScale && tickMarkCounter % 10 == 0) {
                    // Draw major tick mark based on number of tick marks
                    isMinValue = Double.compare(minValue, counter) == 0;
                    isMaxValue = Double.compare(maxValue, counter) == 0;
                    if (VERTICAL == orientation) {
                        isZero = Double.compare(0.0, maxValue - counter + minValue) == 0;
                    } else {
                        isZero = Double.compare(0.0, counter) == 0;
                    }

                    if (isZero) { setZeroPosition(fixedPosition); }

                    // TFE, 20220328: add visibility checking
                    if (minorTickMarksVisible) {
                        drawTickMark((fullRange && isZero) ? zeroColor : minorTickMarkColor, minorLineWidth, innerPointX, innerPointY, outerPointX, outerPointY);
                    }

                    // Draw tick labels
                    if (tickLabelsVisible) {
                        String tickLabelString;
                        if (TickLabelFormat.NUMBER == getTickLabelFormat()) {
                            tickLabelString = Orientation.HORIZONTAL == orientation ? formatNumber(locale, (minValue - i)) : formatNumber(locale, maxValue - counter + minValue);
                        } else {
                            tickLabelString = Orientation.HORIZONTAL == orientation ? Helper.secondsToHHMMString(Helper.toSeconds(Helper.toRealValue(minValue - i), Helper.getZoneOffset())) : formatNumber(locale, maxValue - counter + minValue);
                        }
                        drawTickLabel(isOnlyFirstAndLastTickLabelVisible, isZero, isMinValue, isMaxValue, fullRange, zeroColor, tickLabelColor, textPointX, textPointY, maxTextWidth, tickLabelString, orientation);
                    }
                } else if (tickMarkCounter % 1 == 0) {
                    // TFE, 20220328: add visibility checking
                    if (minorTickMarksVisible) {
                        drawTickMark(minorTickMarkColor, minorLineWidth, minorPointX, minorPointY, outerPointX, outerPointY);
                    }
                }

                counterBD = counterBD.add(minorTickSpaceBD);
                counter = counterBD.doubleValue();
                if (counter > maxValue) break;
            }
        } else if (AxisType.LOGARITHMIC == axisType){
            // ******************** Logarithmic *******************************
            tickLabelFormatString = "%6.0e";
            double logLowerBound = Math.log10(getMinValue());
            double logUpperBound = Math.log10(getMaxValue());
            double section;

            // Draw axis
            if (VERTICAL == orientation) {
                section = height / logUpperBound;
                if (Position.LEFT == position) {
                    axisCtx.strokeLine(anchorXPlusOffset, minPosition, anchorXPlusOffset, maxPosition);
                } else if (Position.RIGHT == position) {
                    axisCtx.strokeLine(anchorX, minPosition, anchorX, maxPosition);
                }
            } else {
                section = width / logUpperBound;
                if (Position.BOTTOM == position) {
                    axisCtx.strokeLine(minPosition, anchorY, maxPosition, anchorY);
                } else if (Position.TOP == position) {
                    axisCtx.strokeLine(minPosition, anchorYPlusOffset, maxPosition, anchorYPlusOffset);
                }
            }

            for (double i = 0; i <= logUpperBound; i += 1) {
                for (double j = 1; j <= 9; j++) {
                    BigDecimal value = new BigDecimal(j * Math.pow(10, i));
                    double stepSize = i > 0 ? (Math.log10(value.doubleValue()) % i) : Math.log10(value.doubleValue());
                    double fixedPosition;
                    if (VERTICAL == orientation) {
                        isMinValue    = Double.compare(i, logUpperBound) == 0;
                        isMaxValue    = i == 0;
                        fixedPosition = maxPosition - i * section - (stepSize * section);
                        if (Position.LEFT == position) {
                            innerPointX  = anchorXPlusOffset - maxMajorTickMarkLength;
                            innerPointY  = fixedPosition;
                            minorPointX  = anchorXPlusOffset - maxMinorTickMarkLength;
                            minorPointY  = fixedPosition;
                            outerPointX  = anchorXPlusOffset;
                            outerPointY  = fixedPosition;
                            textPointX   = anchorXPlusOffset - textPosition;
                            textPointY   = fixedPosition;
                            maxTextWidth = 0.6 * width;
                        } else {
                            innerPointX  = anchorX + maxMajorTickMarkLength;
                            innerPointY  = fixedPosition;
                            minorPointX  = anchorX + maxMinorTickMarkLength;
                            minorPointY  = fixedPosition;
                            outerPointX  = anchorX;
                            outerPointY  = fixedPosition;
                            textPointX   = anchorXPlusOffset;
                            textPointY   = fixedPosition;
                            maxTextWidth = width;
                        }
                    } else {
                        isMinValue    = i == 0;
                        isMaxValue    = Double.compare(i, logUpperBound) == 0;
                        fixedPosition = i * section + (stepSize * section);
                        if (Position.BOTTOM == position) {
                            innerPointX  = fixedPosition;
                            innerPointY  = anchorY + maxMajorTickMarkLength;
                            minorPointX  = fixedPosition;
                            minorPointY  = anchorY + maxMinorTickMarkLength;
                            outerPointX  = fixedPosition;
                            outerPointY  = anchorY;
                            textPointX   = fixedPosition;
                            textPointY   = anchorY + textPosition - tickLabelFontSize * 0.2;
                            maxTextWidth = section;
                        } else {
                            innerPointX  = fixedPosition;
                            innerPointY  = anchorYPlusOffset - maxMajorTickMarkLength;
                            minorPointX  = fixedPosition;
                            minorPointY  = anchorYPlusOffset - maxMinorTickMarkLength;
                            outerPointX  = fixedPosition;
                            outerPointY  = anchorYPlusOffset;
                            textPointX   = fixedPosition;
                            textPointY   = anchorY - textPosition + tickLabelFontSize * 0.5;
                            maxTextWidth = section;
                        }
                    }

                    if (Helper.isPowerOf10(value.intValue())) {
                        if (majorTickMarksVisible) {
                            drawTickMark(majorTickMarkColor, majorLineWidth, innerPointX, innerPointY, outerPointX, outerPointY);
                        } else if (minorTickMarksVisible) {
                            drawTickMark(minorTickMarkColor, minorLineWidth, minorPointX, minorPointY, outerPointX, outerPointY);
                        }
                        // Draw tick labels
                        if (tickLabelsVisible) {
                            axisCtx.setFill(tickLabelColor);
                            if (VERTICAL == orientation) {
                                axisCtx.setTextAlign(TextAlignment.RIGHT);
                            }
                            drawTickLabel(isOnlyFirstAndLastTickLabelVisible, false, isMinValue, isMaxValue, false, zeroColor, tickLabelColor, textPointX, textPointY, maxTextWidth, formatNumber(locale, value.doubleValue()), orientation);
                        }
                    } else {
                        if (minorTickMarksVisible) {
                            drawTickMark(minorTickMarkColor, minorLineWidth, minorPointX, minorPointY, outerPointX, outerPointY);
                        }
                    }
                }
            }
        }
        drawAxisTitle(orientation, position);
    }
    private void drawTimeAxis() {
        if (Double.compare(stepSize, 0) <= 0) return;

        axisCtx.setFill(getAxisBackgroundColor());
        axisCtx.clearRect(0, 0, width, height);
        axisCtx.setFont(tickLabelFont);
        axisCtx.setTextBaseline(VPos.CENTER);

        double      minValue                           = Helper.toNumericValue(getStart());
        double      maxValue                           = Helper.toNumericValue(getEnd());
        boolean     tickLabelsVisible                  = getTickLabelsVisible();
        boolean     isOnlyFirstAndLastTickLabelVisible = isOnlyFirstAndLastTickLabelVisible();
        double      tickLabelFontSize                  = getTickLabelFontSize();
        Color       tickLabelColor                     = getTickLabelColor();
        Color       majorTickMarkColor                 = getMajorTickMarkColor();
        boolean     majorTickMarksVisible              = getMajorTickMarksVisible();
        Color       mediumTickMarkColor                = getMediumTickMarkColor();
        boolean     mediumTickMarksVisible             = getMediumTickMarksVisible();
        Color       minorTickMarkColor                 = getMinorTickMarkColor();
        boolean     minorTickMarksVisible              = getMinorTickMarksVisible();
        double      majorLineWidth                     = size * 0.007 < MIN_MAJOR_LINE_WIDTH ? MIN_MAJOR_LINE_WIDTH : size * 0.007;
        double      mediumLineWidth                    = size * 0.006 < MIN_MEDIUM_LINE_WIDTH ? MIN_MEDIUM_LINE_WIDTH : size * 0.005;
        double      minorLineWidth                     = size * 0.005 < MIN_MINOR_LINE_WIDTH ? MIN_MINOR_LINE_WIDTH : size * 0.003;
        double      minPosition;
        double      maxPosition;
        if (VERTICAL == getOrientation()) {
            minPosition = 0;
            maxPosition = height;
        } else {
            minPosition = 0;
            maxPosition = width;
        }

        Orientation orientation       = getOrientation();
        Position    position          = getPosition();
        double      anchorX           = Position.LEFT == position ? 0 : getZeroPosition();
        double      anchorXPlusOffset = anchorX + width;
        double      anchorY           = Position.BOTTOM == position ? 0 : getZeroPosition();
        double      anchorYPlusOffset = anchorY + height;
        boolean     isMinValue;
        boolean     isMaxValue;
        double      innerPointX;
        double      innerPointY;
        double      outerPointX;
        double      outerPointY;
        double      mediumPointX;
        double      mediumPointY;
        double      minorPointX;
        double      minorPointY;
        double      textPointX;
        double      textPointY;
        double      maxTextWidth;

        // ******************** Date **************************************
        createTickValues(width, getStart(), getEnd());
        long                minValueInSeconds = getStart().toEpochSecond(Helper.getZoneOffset());
        long                maxValueInSeconds = getEnd().toEpochSecond(Helper.getZoneOffset());
        long                rangeInSeconds    = Duration.between(getStart(), getEnd()).getSeconds();
        double              stepSize          = VERTICAL == orientation ? height / rangeInSeconds : width / rangeInSeconds;
        long                majorTickSpace    = currentInterval.getMajorTickSpace();
        long                mediumTickSpace   = currentInterval.getMediumTickSpace();
        long                minorTickSpace    = currentInterval.getMinorTickSpace();
        long                counter           = minValueInSeconds;

        axisCtx.setLineWidth(majorLineWidth);

        // Draw axis
        if (VERTICAL == orientation) {
            switch(position) {
                case LEFT : axisCtx.strokeLine(anchorXPlusOffset, minPosition, anchorXPlusOffset, maxPosition); break;
                case RIGHT: axisCtx.strokeLine(anchorX, minPosition, anchorX, maxPosition); break;
                default   : axisCtx.strokeLine(anchorX, minPosition, anchorX, maxPosition); break;
            }
        } else {
            switch(position) {
                case BOTTOM: axisCtx.strokeLine(minPosition, anchorY, maxPosition, anchorY); break;
                case TOP   : axisCtx.strokeLine(minPosition, anchorYPlusOffset, maxPosition, anchorYPlusOffset); break;
                default    : axisCtx.strokeLine(minPosition, anchorY, maxPosition, anchorY); break;
            }
        }

        // Main Loop for tick marks and labels
        for (long i = minValueInSeconds; i <= maxValueInSeconds; i++) {
            double fixedPosition = (counter - minValueInSeconds) * stepSize;

            if (VERTICAL == orientation) {
                if (Position.LEFT == position) {
                    innerPointX  = anchorXPlusOffset - 0.5 * width;
                    innerPointY  = fixedPosition;
                    mediumPointX = anchorXPlusOffset - 0.4 * width;
                    mediumPointY = fixedPosition;
                    minorPointX  = anchorXPlusOffset - 0.3 * width;
                    minorPointY  = fixedPosition;
                    outerPointX  = anchorXPlusOffset;
                    outerPointY  = fixedPosition;
                    textPointX   = anchorXPlusOffset - 0.6 * width;
                    textPointY   = fixedPosition;
                    maxTextWidth = 0.6 * width;
                } else if (Position.RIGHT == position) {
                    innerPointX  = anchorX + 0.5 * width;
                    innerPointY  = fixedPosition;
                    mediumPointX = anchorX + 0.4 * width;
                    mediumPointY = fixedPosition;
                    minorPointX  = anchorX + 0.3 * width;
                    minorPointY  = fixedPosition;
                    outerPointX  = anchorX;
                    outerPointY  = fixedPosition;
                    textPointX   = anchorXPlusOffset;
                    textPointY   = fixedPosition;
                    maxTextWidth = width;
                } else {
                    innerPointX  = anchorX - 0.25 * width;
                    innerPointY  = fixedPosition;
                    mediumPointX = anchorX - 0.2 * width;
                    mediumPointY = fixedPosition;
                    minorPointX  = anchorX - 0.15 * width;
                    minorPointY  = fixedPosition;
                    outerPointX  = anchorX;
                    outerPointY  = fixedPosition;
                    textPointX   = anchorXPlusOffset;
                    textPointY   = fixedPosition;
                    maxTextWidth = width;
                }
            } else {
                if (Position.BOTTOM == position) {
                    innerPointX  = fixedPosition;
                    innerPointY  = anchorY + 0.5 * height;
                    mediumPointX = fixedPosition;
                    mediumPointY = anchorY + 0.4 * height;
                    minorPointX  = fixedPosition;
                    minorPointY  = anchorY + 0.3 * height;
                    outerPointX  = fixedPosition;
                    outerPointY  = anchorY;
                    textPointX   = fixedPosition;
                    textPointY   = anchorY + 0.8 * height;
                    maxTextWidth = majorTickSpace * stepSize;
                } else if (Position.TOP == position) {
                    innerPointX  = fixedPosition;
                    innerPointY  = anchorYPlusOffset - 0.5 * height;
                    mediumPointX = fixedPosition;
                    mediumPointY = anchorYPlusOffset - 0.4 * height;
                    minorPointX  = fixedPosition;
                    minorPointY  = anchorYPlusOffset - 0.3 * height;
                    outerPointX  = fixedPosition;
                    outerPointY  = anchorYPlusOffset;
                    textPointX   = fixedPosition;
                    textPointY   = anchorY + 0.2 * height;
                    maxTextWidth = majorTickSpace * stepSize;
                } else {
                    innerPointX  = fixedPosition;
                    innerPointY  = anchorY - 0.25 * height;
                    mediumPointX = fixedPosition;
                    mediumPointY = anchorY - 0.2 * height;
                    minorPointX  = fixedPosition;
                    minorPointY  = anchorY - 0.15 * height;
                    outerPointX  = fixedPosition;
                    outerPointY  = anchorY;
                    textPointX   = fixedPosition;
                    textPointY   = anchorY + 0.2 * height;
                    maxTextWidth = majorTickSpace * stepSize;
                }
            }

            if (i % majorTickSpace == 0) {
                // Draw major tick mark
                isMinValue = i == minValueInSeconds;
                isMaxValue = i == maxValueInSeconds;

                if (majorTickMarksVisible) {
                    axisCtx.setStroke(majorTickMarkColor);
                    axisCtx.setLineWidth(majorLineWidth);
                    axisCtx.strokeLine(innerPointX, innerPointY, outerPointX, outerPointY);
                } else if (minorTickMarksVisible) {
                    axisCtx.setStroke(minorTickMarkColor);
                    axisCtx.setLineWidth(minorLineWidth);
                    axisCtx.strokeLine(minorPointX, minorPointY, outerPointX, outerPointY);
                }

                // Draw tick labels
                if (tickLabelsVisible) {
                    if (!isOnlyFirstAndLastTickLabelVisible) {
                        axisCtx.setFill(tickLabelColor);
                    } else {
                        if (isMinValue || isMaxValue) {
                            axisCtx.setFill(tickLabelColor);
                        } else {
                            axisCtx.setFill(Color.TRANSPARENT);
                        }
                    }

                    if (VERTICAL == orientation) {
                        axisCtx.setTextAlign(TextAlignment.RIGHT);
                        if (isMinValue) {
                            axisCtx.fillText(dateTimeFormatter.format(toLocalDateTime((long) (minValue - i) * 1000)), textPointX, textPointY + size * 0.15, maxTextWidth);
                        } else if (isMaxValue) {
                            axisCtx.fillText(dateTimeFormatter.format(toLocalDateTime((long) (minValue - i) * 1000)), textPointX, textPointY - size * 0.15, maxTextWidth);
                        } else {
                            axisCtx.fillText(dateTimeFormatter.format(toLocalDateTime((long) (minValue - i) * 1000)), textPointX, textPointY, maxTextWidth);
                        }
                    } else {
                        if (isMinValue) {
                            axisCtx.setTextAlign(TextAlignment.LEFT);
                        } else if (isMaxValue) {
                            axisCtx.setTextAlign(TextAlignment.RIGHT);
                        } else {
                            axisCtx.setTextAlign(TextAlignment.CENTER);
                            LocalDateTime currentDateTime = toLocalDateTime(i);
                            double halfLabelWidth = calcTextWidth(tickLabelFont, dateTimeFormatter.format(currentDateTime)) * 0.5;
                            if (textPointX - halfLabelWidth < 0) {
                                textPointX = halfLabelWidth;
                            } else if (textPointX + halfLabelWidth > width) {
                                textPointX = width - halfLabelWidth;
                            }
                        }
                        drawTickLabel(isOnlyFirstAndLastTickLabelVisible, false, isMinValue, isMaxValue, false, majorTickMarkColor, tickLabelColor, textPointX, textPointY, maxTextWidth, dateTimeFormatter.format(toLocalDateTime(i)), orientation);
                    }
                }
            } else if(mediumTickMarksVisible && i % mediumTickSpace == 0) {
                // Draw medium tick mark
                axisCtx.setStroke(mediumTickMarkColor);
                axisCtx.setLineWidth(mediumLineWidth);
                axisCtx.strokeLine(mediumPointX, mediumPointY, outerPointX, outerPointY);
            } else if (minorTickMarksVisible && i % minorTickSpace == 0) {
                // Draw minor tick mark
                axisCtx.setStroke(minorTickMarkColor);
                axisCtx.setLineWidth(minorLineWidth);
                axisCtx.strokeLine(minorPointX, minorPointY, outerPointX, outerPointY);
            }

            counter++; // 1 Second
            if (counter > maxValue) break;
        }
        
        drawAxisTitle(orientation, position);
    }

    private void drawAxisTitle(final Orientation ORIENTATION, final Position POSITION) {
        double titleFontSize = getTitleFontSize();

        // Draw axis title
        axisCtx.setFont(titleFont);
        axisCtx.setFill(getTitleColor());
        axisCtx.setTextAlign(TextAlignment.CENTER);
        axisCtx.setTextBaseline(VPos.CENTER);
        double titleWidth = calcTextWidth(titleFont, getTitle());
        if (Orientation.HORIZONTAL == ORIENTATION) {
            switch(POSITION) {
                case TOP:
                    axisCtx.fillText(getTitle(), (width - titleWidth) * 0.5, titleFontSize * 0.5);
                    break;
                case BOTTOM:
                    axisCtx.fillText(getTitle(), (width - titleWidth) * 0.5, height - titleFontSize * 0.5);
                    break;
            }
        } else {
            switch(POSITION) {
                case LEFT:
                    axisCtx.save();
                    axisCtx.translate(titleFontSize * 0.5, (height - titleFontSize) * 0.5);
                    axisCtx.rotate(270);
                    axisCtx.fillText(getTitle(), 0, 0);
                    axisCtx.restore();
                    break;
                case RIGHT:
                    axisCtx.save();
                    axisCtx.translate(width - titleFontSize * 0.5, (height - titleFontSize) * 0.5);
                    axisCtx.rotate(90);
                    axisCtx.fillText(getTitle(), 0, 0);
                    axisCtx.restore();
                    break;
            }
        }
    }

    private void drawTickMark(final Color COLOR, final double LINE_WIDTH, final double START_X, final double START_Y, final double END_X, final double END_Y) {
        axisCtx.setStroke(COLOR);
        axisCtx.setLineWidth(LINE_WIDTH);
        axisCtx.strokeLine(START_X, START_Y, END_X, END_Y);
    }

    private void drawTickLabel(final boolean ONLY_FIRST_AND_LAST_VISIBLE, final boolean IS_ZERO, final boolean IS_MIN, final boolean IS_MAX, final boolean FULL_RANGE,
                               final Color ZERO_COLOR, final Color COLOR, final double TEXT_X, final double TEXT_Y, final double MAX_WIDTH, final String TEXT, final Orientation ORIENTATION) {
        if (!ONLY_FIRST_AND_LAST_VISIBLE) {
            if (IS_ZERO) {
                axisCtx.setFill(FULL_RANGE ? ZERO_COLOR : COLOR);
            } else {
                axisCtx.setFill(COLOR);
            }
        } else {
            if (IS_MIN || IS_MAX) {
                if (IS_ZERO) {
                    axisCtx.setFill(FULL_RANGE ? ZERO_COLOR : COLOR);
                } else {
                    axisCtx.setFill(COLOR);
                }
            } else {
                axisCtx.setFill(Color.TRANSPARENT);
            }
        }

        if (VERTICAL == ORIENTATION) {
            axisCtx.setTextAlign(TextAlignment.RIGHT);
            // TFE, 20220329: should be tickLabelFontSize...
//            double fontSize = getTitleFontSize();
            double fontSize = getTickLabelFontSize();
            double textY;
            if (TEXT_Y < fontSize) {
                textY = fontSize * 0.5;
            } else if (TEXT_Y > height - fontSize) {
                textY = height - fontSize * 0.5;
            } else {
                textY = TEXT_Y;
            }
            axisCtx.fillText(TEXT, TEXT_X, textY, MAX_WIDTH);
        } else {
            if (IS_MIN) {
                axisCtx.setTextAlign(TextAlignment.LEFT);
            } else if (IS_MAX) {
                axisCtx.setTextAlign(TextAlignment.RIGHT);
            } else {
                axisCtx.setTextAlign(TextAlignment.CENTER);
            }

            double tickLabelWidth = calcTextWidth(tickLabelFont, TEXT);
            if (axisCtx.getTextAlign() == TextAlignment.CENTER && TEXT_X + tickLabelWidth * 0.5 > width) {
                axisCtx.fillText(TEXT, width - tickLabelWidth * 0.5, TEXT_Y, MAX_WIDTH);
            } else {
                axisCtx.fillText(TEXT, TEXT_X, TEXT_Y, MAX_WIDTH);
            }
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        double aspectRatio = width / height;

        if (width > 0 && height > 0) {
            if (isAutoFontSize()) {
                setTickLabelFontSize(Helper.clamp(8, 24, 0.175 * size));
                setTitleFontSize(Helper.clamp(8, 24, 0.175 * size));
            }

            if (VERTICAL == getOrientation()) {
                width    = height * aspectRatio;
                size     = width < height ? width : height;
                stepSize = Math.abs(height / getRange());
            } else {
                height   = width / aspectRatio;
                size     = width < height ? width : height;
                stepSize = Math.abs(width / getRange());
            }
            pane.setMaxSize(width, height);
            pane.setMinSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            axisCanvas.setWidth(width);
            axisCanvas.setHeight(height);

            redraw();
        }
    }

    private void redraw() {
        if (AxisType.TIME == getType()) {
            drawTimeAxis();
        } else {
            if (isAutoScale()) { 
                calcAutoScale(); 
            } else {
                calcScale();
            }
            drawAxis();
        }
    }
}
