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
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.Locale;


/**
 * User: hansolo
 * Date: 22.07.17
 * Time: 08:49
 */
@DefaultProperty("children")
public class Axis extends Region {
    public               enum                        AxisType { LINEAR, LOGARITHMIC }
    private static final double                      MINIMUM_WIDTH  = 0;
    private static final double                      MINIMUM_HEIGHT = 0;
    private static final double                      MAXIMUM_WIDTH  = 4096;
    private static final double                      MAXIMUM_HEIGHT = 4096;
    private              double                      size;
    private              double                      width;
    private              double                      height;
    private              Canvas                      canvas;
    private              GraphicsContext             ctx;
    private              Pane                        pane;
    private              double                      _minValue;
    private              DoubleProperty              minValue;
    private              double                      _maxValue;
    private              DoubleProperty              maxValue;
    private              boolean                     _autoScale;
    private              BooleanProperty             autoScale;
    private              double                      stepSize;
    private              String                      _title;
    private              StringProperty              title;
    private              String                      _unit;
    private              StringProperty              unit;
    private              AxisType                    _type;
    private              ObjectProperty<AxisType>    type;
    private              Orientation                 _orientation;
    private              ObjectProperty<Orientation> orientation;
    private              Pos                         _position;
    private              ObjectProperty<Pos>         position;
    private              Color                       _axisBackgroundColor;
    private              ObjectProperty<Color>       axisBackgroundColor;
    private              Color                       _axisColor;
    private              ObjectProperty<Color>       axisColor;
    private              Color                       _tickLabelColor;
    private              ObjectProperty<Color>       tickLabelColor;
    private              Color                       _tickMarkColor;
    private              ObjectProperty<Color>       tickMarkColor;
    private              double                      _majorTickSpace;
    private              double                      _minorTickSpace;
    private              Locale                      _locale;
    private              ObjectProperty<Locale>      locale;
    private              int                         _decimals;
    private              IntegerProperty             decimals;
    private              String                      tickLabelFormatString;


    // ******************** Constructors **************************************
    public Axis() {
        this(Orientation.VERTICAL, Pos.CENTER_LEFT);
    }
    public Axis(final Orientation ORIENTATION, final Pos POSITION) {
        if (Orientation.VERTICAL == ORIENTATION) {
            if (Pos.CENTER_LEFT != POSITION &&
                Pos.CENTER_RIGHT != POSITION) {
                throw new IllegalArgumentException("Wrong combination of orientation and position!");
            }
        }

        getStylesheets().add(Axis.class.getResource("chart.css").toExternalForm());
        _minValue             = 0;
        _maxValue             = 100;
        _autoScale            = true;
        _title                = "";
        _unit                 = "";
        _type                 = AxisType.LINEAR;
        _orientation          = ORIENTATION;
        _position             = POSITION;
        _axisBackgroundColor  = Color.WHITE;
        _axisColor            = Color.BLACK;
        _tickLabelColor       = Color.BLACK;
        _tickMarkColor        = Color.BLACK;
        _locale               = Locale.US;
        _decimals             = 0;
        tickLabelFormatString = new StringBuilder("%.").append(Integer.toString(_decimals)).append("f").toString();

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

        canvas = new Canvas(width, height);
        ctx    = canvas.getGraphicsContext2D();

        pane   = new Pane(canvas);

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

    public Pos getPosition() { return null == position ? _position : position.get(); }
    public void setPosition(final Pos POSITION) {
        if (null == position) {
            _position = POSITION;
            redraw();
        } else {
            position.set(POSITION);
        }
    }
    public ObjectProperty<Pos> positionProperty() {
        if (null == position) {
            position = new ObjectPropertyBase<Pos>(_position) {
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

    public Color getTickMarkColor() { return null == tickMarkColor ? _tickMarkColor : tickMarkColor.get(); }
    public void setTickMarkColor(final Color COLOR) {
        if (null == tickMarkColor) {
            _tickMarkColor = COLOR;
            redraw();
        } else {
            tickMarkColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> tickMarkColorProperty() {
        if (null == tickMarkColor) {
            tickMarkColor = new ObjectPropertyBase<Color>(_tickMarkColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "tickMarkColor"; }
            };
            _tickMarkColor = null;
        }
        return tickMarkColor;
    }

    private double getMajorTickSpace() { return _majorTickSpace; }
    private void setMajorTickSpace(final double SPACE) { _majorTickSpace = SPACE; }

    private double getMinorTickSpace() { return _minorTickSpace; }
    private void setMinorTickSpace(final double SPACE) { _minorTickSpace = SPACE; }

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

    private void calcAutoScale() {
        double maxNoOfMajorTicks = 10;
        double maxNoOfMinorTicks = 10;
        double niceRange         = (Helper.calcNiceNumber(getRange(), false));
        setMajorTickSpace(Helper.calcNiceNumber(niceRange / (maxNoOfMajorTicks - 1), true));
        setMinorTickSpace(Helper.calcNiceNumber(getMajorTickSpace() / (maxNoOfMinorTicks - 1), true));
        double niceMinValue = (Math.floor(getMinValue() / getMajorTickSpace()) * getMajorTickSpace());
        double niceMaxValue = (Math.ceil(getMaxValue() / getMajorTickSpace()) * getMajorTickSpace());
        setMinValue(niceMinValue);
        setMaxValue(niceMaxValue);
    }

    private void drawAxis() {
        if (Double.compare(stepSize, 0) <= 0) return;

        ctx.setFill(getAxisBackgroundColor());
        ctx.clearRect(0, 0, width, height);

        ctx.setFont(Fonts.latoLight(0.4 * size));
        ctx.setStroke(getTickMarkColor());
        ctx.setFill(getTickLabelColor());

        Point2D innerPoint;
        Point2D innerMediumPoint;
        Point2D innerMinorPoint;
        Point2D outerPoint;
        Point2D textPoint;

        double minPosition;
        double maxPosition;
        if (Orientation.VERTICAL == getOrientation()) {
            minPosition = getLayoutY();
            maxPosition = getLayoutY() + getLayoutBounds().getHeight();
        } else {
            minPosition = getLayoutX();
            maxPosition = getLayoutX() + getLayoutBounds().getWidth();
        }

        double anchorX        = getLayoutX();
        double anchorY        = getLayoutY();
        double majorTickSpace = getMajorTickSpace();
        double minorTickSpace = getMinorTickSpace();
        double minValue       = getMinValue();
        double maxValue       = getMaxValue();

        int counter = 0;
        ctx.setStroke(getTickMarkColor());
        ctx.setLineWidth(size * 0.007);
        for (double i = minPosition ; Double.compare(i, maxPosition + 1) <= 0 ; i += stepSize) {
            if (Orientation.VERTICAL == getOrientation()) {
                if (Pos.CENTER_LEFT == getPosition()) {
                    innerPoint       = new Point2D(anchorX + getLayoutBounds().getWidth() - 0.5 * width, i);
                    innerMediumPoint = new Point2D(anchorX + getLayoutBounds().getWidth() - 0.4 * width, i);
                    innerMinorPoint  = new Point2D(anchorX + getLayoutBounds().getWidth() - 0.3 * width, i);
                    outerPoint       = new Point2D(anchorX + getLayoutBounds().getWidth(), i);
                    textPoint        = new Point2D(anchorX + getLayoutBounds().getWidth() - 0.6 * width, i);

                    ctx.strokeLine(anchorX + getLayoutBounds().getWidth(), anchorY, anchorX + getLayoutBounds().getWidth(), anchorY + getLayoutBounds().getHeight());
                } else {
                    innerPoint       = new Point2D(anchorX + 0.5 * width, i);
                    innerMediumPoint = new Point2D(anchorX + 0.4 * width, i);
                    innerMinorPoint  = new Point2D(anchorX + 0.3 * width, i);
                    outerPoint       = new Point2D(anchorX, i);
                    textPoint        = new Point2D(anchorX + 0.8 * width, i);

                    ctx.strokeLine(anchorX, anchorY, anchorX, anchorY + getLayoutBounds().getHeight());
                }
            } else {
                if (Pos.BOTTOM_CENTER == getPosition()) {
                    innerPoint       = new Point2D(i, anchorY + 0.5 * height);
                    innerMediumPoint = new Point2D(i, anchorY + 0.4 * height);
                    innerMinorPoint  = new Point2D(i, anchorY + 0.3 * height);
                    outerPoint       = new Point2D(i, anchorY);
                    textPoint        = new Point2D(i, anchorY + 0.8 * height);

                    ctx.strokeLine(anchorX, anchorY, anchorX + getLayoutBounds().getWidth(), anchorY);
                } else {
                    innerPoint       = new Point2D(i, anchorY + getLayoutBounds().getHeight() - 0.5 * height);
                    innerMediumPoint = new Point2D(i, anchorY + getLayoutBounds().getHeight() - 0.4 * height);
                    innerMinorPoint  = new Point2D(i, anchorY + getLayoutBounds().getHeight() - 0.3 * height);
                    outerPoint       = new Point2D(i, anchorY + getLayoutBounds().getHeight());
                    textPoint        = new Point2D(i, anchorY + 0.2 * height);

                    ctx.strokeLine(anchorX, anchorY + getLayoutBounds().getHeight(), anchorX + getLayoutBounds().getWidth(), anchorY + getLayoutBounds().getHeight());
                }
            }

            if (counter % majorTickSpace == 0) {
                // Draw major tickmark
                ctx.setLineWidth(size * 0.007);
                ctx.strokeLine(innerPoint.getX(), innerPoint.getY(), outerPoint.getX(), outerPoint.getY());

                // Draw text
                ctx.setTextBaseline(VPos.CENTER);
                if (Orientation.VERTICAL == getOrientation()) {
                    ctx.setTextAlign(TextAlignment.RIGHT);
                    ctx.fillText(String.format(getLocale(), tickLabelFormatString, (maxValue -= majorTickSpace) + majorTickSpace), textPoint.getX(), textPoint.getY());
                } else {
                    ctx.setTextAlign(TextAlignment.CENTER);
                    ctx.fillText(String.format(getLocale(), tickLabelFormatString, (minValue += majorTickSpace) - majorTickSpace), textPoint.getX(), textPoint.getY());
                }
            } else if (minorTickSpace % 2 != 0 && counter % 5 == 0) {
                ctx.setLineWidth(size * 0.006);
                ctx.strokeLine(innerMediumPoint.getX(), innerMediumPoint.getY(), outerPoint.getX(), outerPoint.getY());
            } else if (counter % minorTickSpace == 0) {
                ctx.setLineWidth(size * 0.005);
                ctx.strokeLine(innerMinorPoint.getX(), innerMinorPoint.getY(), outerPoint.getX(), outerPoint.getY());
            }
            counter++;
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

            canvas.setWidth(width);
            canvas.setHeight(height);

            redraw();
        }
    }

    private void redraw() {
        if (isAutoScale()) { calcAutoScale(); }
        drawAxis();
    }
}
