package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.font.Fonts;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.math.BigDecimal;
import java.util.Locale;


/**
 * User: hansolo
 * Date: 22.07.17
 * Time: 08:49
 */
@DefaultProperty("children")
public class Axis extends Region {
    public               enum                                 AxisType { LINEAR, LOGARITHMIC }
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
    private              double                               _maxValue;
    private              DoubleProperty                       maxValue;
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
    private              TickLabelOrientation                 _tickLabelOrientation;
    private              ObjectProperty<TickLabelOrientation> tickLabelOrientation;


    // ******************** Constructors **************************************
    public Axis() {
        this(Orientation.VERTICAL, Position.LEFT);
    }
    public Axis(final Orientation ORIENTATION, final Position POSITION) {
        if (Orientation.VERTICAL == ORIENTATION) {
            if (Position.LEFT != POSITION && Position.RIGHT != POSITION) {
                throw new IllegalArgumentException("Wrong combination of orientation and position!");
            }
        }

        getStylesheets().add(Axis.class.getResource("chart.css").toExternalForm());
        _minValue                         = 0;
        _maxValue                         = 100;
        _autoScale                        = true;
        _title                            = "";
        _unit                             = "";
        _type                             = AxisType.LINEAR;
        _orientation                      = ORIENTATION;
        _position                         = POSITION;
        _axisBackgroundColor              = Color.WHITE;
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
        tickLabelFormatString             = new StringBuilder("%.").append(Integer.toString(_decimals)).append("f").toString();

        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
            Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() != 0 && getPrefHeight() != 0) {
                if (Orientation.VERTICAL == getOrientation()) {
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

    public double getMaxValue() { return null == maxValue ? _maxValue : maxValue.get(); }
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

    public double getRange() {
        double min = null == minValue ? _minValue : minValue.get();
        double max = null == maxValue ? _maxValue : maxValue.get();
        return (max - min);
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

    public AxisType getType() { return type.get(); }
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
            _decimals = DECIMALS;
            redraw();
        } else {
            decimals.set(DECIMALS);
        }
    }
    public IntegerProperty decimals() {
        if (null == decimals) {
            decimals = new IntegerPropertyBase(_decimals) {
                @Override protected void invalidated() { redraw(); }
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

    private double calcLabelWidth(final double VALUE) {
        Text text = new Text(String.format(getLocale(), tickLabelFormatString, VALUE));
        text.setFont(Fonts.latoLight(0.3 * size));
        return text.getBoundsInParent().getWidth();
    }

    private void drawAxis() {
        if (Double.compare(stepSize, 0) <= 0) return;

        axisCtx.setFill(getAxisBackgroundColor());
        axisCtx.clearRect(0, 0, width, height);
        axisCtx.setFont(Fonts.latoLight(0.3 * size));
        axisCtx.setTextBaseline(VPos.CENTER);

        double minPosition;
        double maxPosition;
        if (Orientation.VERTICAL == getOrientation()) {
            minPosition = 0;
            maxPosition = height;
        } else {
            minPosition = 0;
            maxPosition = width;
        }

        Locale      locale                             = getLocale();
        Orientation orientation                        = getOrientation();
        Position    position                           = getPosition();
        double      anchorX                            = 0;
        double      anchorXPlusOffset                  = anchorX + width;
        double      anchorY                            = 0;
        double      anchorYPlusOffset                  = anchorY + height;
        double      minValue                           = getMinValue();
        double      maxValue                           = getMaxValue();
        boolean     tickLabelsVisible                  = getTickLabelsVisible();
        boolean     isOnlyFirstAndLastTickLabelVisible = isOnlyFirstAndLastTickLabelVisible();
        Color       tickLabelColor                     = getTickLabelColor();
        Color       zeroColor                          = getZeroColor();
        Color       minorTickMarkColor                 = getMinorTickMarkColor();
        Color       mediumTickMarkColor                = getMediumTickMarkColor();
        Color       majorTickMarkColor                 = getMajorTickMarkColor();
        boolean     majorTickMarksVisible              = getMajorTickMarksVisible();
        boolean     mediumTickMarksVisible             = getMediumTickMarksVisible();
        boolean     minorTickMarksVisible              = getMinorTickMarksVisible();
        boolean     fullRange                          = (minValue < 0 && minValue > 0);
        double      minorTickSpace                     = getMinorTickSpace();
        double      majorTickSpace                     = getMajorTickSpace();
        double      tmpStepSize                        = minorTickSpace;
        BigDecimal  minorTickSpaceBD                   = BigDecimal.valueOf(minorTickSpace);
        BigDecimal  majorTickSpaceBD                   = BigDecimal.valueOf(majorTickSpace);
        BigDecimal  mediumCheck2                       = BigDecimal.valueOf(2 * minorTickSpace);
        BigDecimal  mediumCheck5                       = BigDecimal.valueOf(5 * minorTickSpace);
        BigDecimal  counterBD                          = BigDecimal.valueOf(minValue);
        double      counter                            = minValue;
        double      range                              = getRange();
        double      majorLineWidth                     = size * 0.007 < MIN_MAJOR_LINE_WIDTH ? MIN_MAJOR_LINE_WIDTH : size * 0.007;
        double      mediumLineWidth                    = size * 0.006 < MIN_MEDIUM_LINE_WIDTH ? MIN_MEDIUM_LINE_WIDTH : size * 0.005;
        double      minorLineWidth                     = size * 0.005 < MIN_MINOR_LINE_WIDTH ? MIN_MINOR_LINE_WIDTH : size * 0.003;
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

        axisCtx.setLineWidth(majorLineWidth);

        // Draw axis
        if (Orientation.VERTICAL == orientation) {
            if (Position.LEFT == position) {
                axisCtx.strokeLine(anchorXPlusOffset, minPosition, anchorXPlusOffset, maxPosition);
            } else if (Position.RIGHT == position) {
                axisCtx.strokeLine(anchorX, minPosition, anchorX, maxPosition);
            }
        } else {
            if (Position.BOTTOM == position) {
                axisCtx.strokeLine(minPosition, anchorY, maxPosition, anchorY);
            } else if (Position.TOP == position){
                axisCtx.strokeLine(minPosition, anchorYPlusOffset, maxPosition, anchorYPlusOffset);
            }
        }

        // Main Loop for tick marks and labels
        BigDecimal tmpStepBD = new BigDecimal(tmpStepSize);
        tmpStepBD            = tmpStepBD.setScale(3, BigDecimal.ROUND_HALF_UP);
        double     tmpStep   = tmpStepBD.doubleValue();
        for (double i = 0 ; Double.compare(-range - tmpStep, i) <= 0 ; i -= tmpStep) {
            double fixedPosition = (counter - minValue) * stepSize;
            if (Orientation.VERTICAL == orientation) {
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
                } else {
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
                } else {
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
                }
            }

            if (Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(majorTickSpaceBD).doubleValue(), 0.0) == 0) {
                // Draw major tick mark
                isMinValue = Double.compare(minValue, counter) == 0;
                isZero     = Double.compare(0.0, counter) == 0;
                isMaxValue = Double.compare(maxValue, counter) == 0;

                if (isZero) { setZeroPosition(fixedPosition); }

                if (fullRange && isZero) {
                    axisCtx.setFill(zeroColor);
                    axisCtx.setStroke(zeroColor);
                }
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
                        if (isZero) {
                            axisCtx.setFill(fullRange ? zeroColor : tickLabelColor);
                        } else {
                            axisCtx.setFill(tickLabelColor);
                        }
                    } else {
                        if (isMinValue || isMaxValue) {
                            if (isZero) {
                                axisCtx.setFill(fullRange ? zeroColor : tickLabelColor);
                            } else {
                                axisCtx.setFill(tickLabelColor);
                            }
                        } else {
                            axisCtx.setFill(Color.TRANSPARENT);
                        }
                    }

                    if (Orientation.VERTICAL == orientation) {
                        axisCtx.setTextAlign(TextAlignment.RIGHT);
                        if (isMinValue) {
                            axisCtx.fillText(String.format(locale, tickLabelFormatString, maxValue - counter + minValue), textPointX, textPointY + size * 0.15, maxTextWidth);
                        } else if (isMaxValue) {
                            axisCtx.fillText(String.format(locale, tickLabelFormatString, maxValue - counter + minValue), textPointX, textPointY - size * 0.15, maxTextWidth);
                        } else {
                            axisCtx.fillText(String.format(locale, tickLabelFormatString, maxValue - counter + minValue), textPointX, textPointY, maxTextWidth);
                        }
                    } else {
                        if (isMinValue) {
                            axisCtx.setTextAlign(TextAlignment.LEFT);
                        } else if (isMaxValue) {
                            axisCtx.setTextAlign(TextAlignment.RIGHT);
                        } else {
                            axisCtx.setTextAlign(TextAlignment.CENTER);
                        }
                        axisCtx.fillText(String.format(locale, tickLabelFormatString, (minValue - i)), textPointX, textPointY, maxTextWidth);
                    }
                }
            } else if (mediumTickMarksVisible &&
                       Double.compare(minorTickSpaceBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(mediumCheck2).doubleValue(), 0.0) != 0.0 &&
                       Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(mediumCheck5).doubleValue(), 0.0) == 0.0) {
                // Draw medium tick mark
                axisCtx.setStroke(mediumTickMarkColor);
                axisCtx.setLineWidth(mediumLineWidth);
                axisCtx.strokeLine(mediumPointX, mediumPointY, outerPointX, outerPointY);
            } else if (minorTickMarksVisible && Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(minorTickSpaceBD).doubleValue(), 0.0) == 0) {
                // Draw minor tick mark
                axisCtx.setStroke(minorTickMarkColor);
                axisCtx.setLineWidth(minorLineWidth);
                axisCtx.strokeLine(minorPointX, minorPointY, outerPointX, outerPointY);
            }

            counterBD = counterBD.add(minorTickSpaceBD);
            counter   = counterBD.doubleValue();
            if (counter > maxValue) break;
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        double aspectRatio = width / height;

        if (width > 0 && height > 0) {
            if (Orientation.VERTICAL == getOrientation()) {
                width    = height * aspectRatio;
                size     = width < height ? width : height;
                stepSize = Math.abs(height / getRange());
            } else {
                height   = width / aspectRatio;
                size     = width < height ? width : height;
                stepSize = Math.abs(width / getRange());
            }
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            axisCanvas.setWidth(width);
            axisCanvas.setHeight(height);

            redraw();
        }
    }

    private void redraw() {
        if (isAutoScale()) { calcAutoScale(); }
        drawAxis();
    }
}
