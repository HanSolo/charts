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
    private static final double                      PREFERRED_WIDTH  = 250;
    private static final double                      PREFERRED_HEIGHT = 250;
    private static final double                      MINIMUM_WIDTH    = 50;
    private static final double                      MINIMUM_HEIGHT   = 50;
    private static final double                      MAXIMUM_WIDTH    = 1024;
    private static final double                      MAXIMUM_HEIGHT   = 1024;
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
    private              DoubleProperty              majorTickSpace;
    private              double                      _minorTickSpace;
    private              DoubleProperty              minorTickSpace;
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
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        getStyleClass().add("axis");

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        pane   = new Pane(canvas);

        getChildren().setAll(pane);
    }
    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        // add listeners to your propertes like
        //value.addListener(o -> handleControlPropertyChanged("VALUE"));
    }


    // ******************** Methods *******************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
    }

    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    private void handleControlPropertyChanged(final String PROPERTY) {
        if ("".equals(PROPERTY)) {

        }
    }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public double getMinValue() {  return null == minValue ? _minValue : minValue.get();  }
    public void setMinValue(final double MIN_VALUE) {
        if (null == minValue) {
            _minValue = MIN_VALUE;
            redraw();
        } else {
            minValue.set(MIN_VALUE);
        }
    }
    public DoubleProperty minValueProperty() {
        if (null == minValue) {
            minValue = new DoublePropertyBase(_minValue) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() {  return Axis.this;  }
                @Override public String getName() {  return "minValue"; }
            };
        }
        return minValue;
    }

    public double getMaxValue() { return null == maxValue ? _maxValue : maxValue.get(); }
    public void setMaxValue(final double MAX_VALUE) {
        if (null == maxValue) {
            _maxValue = MAX_VALUE;
            redraw();
        } else {
            maxValue.set(MAX_VALUE);
        }
    }
    public DoubleProperty maxValueProperty() {
        if (null == maxValue) {
            maxValue = new DoublePropertyBase(_maxValue) {
                @Override protected void invalidated() { redraw(); }
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
    public void set_autoScale(final boolean AUTO_SCALE) {
        if (null == autoScale) {
            _autoScale = AUTO_SCALE;
            recalc();
        } else {
            autoScale.set(AUTO_SCALE);
        }
    }
    public BooleanProperty autoScaleProperty() {
        if (null == autoScale) {
            autoScale = new BooleanPropertyBase(_autoScale) {
                @Override protected void invalidated() { recalc(); }
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
            recalc();
        } else {
            type.set(TYPE);
        }
    }
    public ObjectProperty<AxisType> typeProperty() {
        if (null == type) {
            type = new ObjectPropertyBase<AxisType>(_type) {
                @Override protected void invalidated() { recalc(); }
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

    public double getMajorTickSpace() { return null == majorTickSpace ? _majorTickSpace : majorTickSpace.get(); }
    public void setMajorTickSpace(final double SPACE) {
        if (null == majorTickSpace) {
            _majorTickSpace = SPACE;
            recalc();
        } else {
            majorTickSpace.set(SPACE);
        }
    }
    public DoubleProperty majorTickSpaceProperty() {
        if (null == majorTickSpace) {
            majorTickSpace = new DoublePropertyBase(_majorTickSpace) {
                @Override protected void invalidated() { recalc(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "majorTickSpace"; }
            };
        }
        return majorTickSpace;
    }

    public double getMinorTickSpace() { return null == minorTickSpace ? _minorTickSpace : minorTickSpace.get(); }
    public void setMinorTickSpace(final double SPACE) {
        if (null == minorTickSpace) {
            _minorTickSpace = SPACE;
            recalc();
        } else {
            minorTickSpace.set(SPACE);
        }
    }
    public DoubleProperty minorTickSpaceProperty() {
        if (null == minorTickSpace) {
            minorTickSpace = new DoublePropertyBase(_minorTickSpace) {
                @Override protected void invalidated() { recalc(); }
                @Override public Object getBean() { return Axis.this; }
                @Override public String getName() { return "minorTickSpace"; }
            };
        }
        return minorTickSpace;
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

    private void calcAutoScale() {
        double maxNoOfMajorTicks = 10;
        double maxNoOfMinorTicks = 10;
        double niceRange         = (Helper.calcNiceNumber(getRange(), false));
        setMajorTickSpace(Helper.calcNiceNumber(niceRange / (maxNoOfMajorTicks - 1), true));
        double niceMinValue = (Math.floor(getMinValue() / getMajorTickSpace()) * getMajorTickSpace());
        double niceMaxValue = (Math.ceil(getMaxValue() / getMajorTickSpace()) * getMajorTickSpace());
        setMinorTickSpace(Helper.calcNiceNumber(getMajorTickSpace() / (maxNoOfMinorTicks - 1), true));
        setMinValue(niceMinValue);
        setMaxValue(niceMaxValue);
    }

    private void recalc() {
        if (isAutoScale()) { calcAutoScale(); }
        redraw();
    }

    private void drawAxis(final GraphicsContext CTX) {
        if (Double.compare(stepSize, 0) <= 0) return;

        CTX.setFont(Fonts.latoLight(0.06 * size));
        CTX.setStroke(getTickMarkColor());
        CTX.setFill(getTickLabelColor());

        Point2D innerPoint;
        Point2D innerMediumPoint;
        Point2D innerMinorPoint;
        Point2D outerPoint;
        Point2D textPoint;

        double minPosition;
        double maxPosition;
        if (Orientation.VERTICAL == getOrientation()) {
            minPosition = getLayoutY() + size * 0.0035;
            maxPosition = getLayoutY() + getLayoutBounds().getHeight();
        } else {
            minPosition = getLayoutX();
            maxPosition = getLayoutX() + getLayoutBounds().getWidth();
        }

        double anchorX        = getLayoutX() - 0.075 * width;
        double anchorY        = getLayoutY() + getHeight() + 0.075 * height;
        double majorTickSpace = getMajorTickSpace();
        double minorTickSpace = getMinorTickSpace();
        double minValue       = getMinValue();
        double maxValue       = getMaxValue();

        int counter = 0;
        for (double i = minPosition ; Double.compare(i, maxPosition + 1) <= 0 ; i += stepSize) {
            if (Orientation.VERTICAL == getOrientation()) {
                innerPoint       = new Point2D(anchorX, i);
                innerMediumPoint = new Point2D(anchorX + 0.015 * width, i);
                innerMinorPoint  = new Point2D(anchorX + 0.03 * width, i);
                outerPoint       = new Point2D(anchorX + 0.05 * width, i);
                textPoint        = new Point2D(anchorX - 0.02 * width, i);
            } else {
                innerPoint       = new Point2D(i, anchorY);
                innerMediumPoint = new Point2D(i, anchorY - 0.015 * height);
                innerMinorPoint  = new Point2D(i, anchorY - 0.03 * height);
                outerPoint       = new Point2D(i, anchorY - 0.05 * height);
                textPoint        = new Point2D(i, anchorY + 0.05 * height);
            }

            if (counter % majorTickSpace == 0) {
                // Draw major tickmark
                CTX.setLineWidth(size * 0.007);
                CTX.strokeLine(innerPoint.getX(), innerPoint.getY(), outerPoint.getX(), outerPoint.getY());

                // Draw text
                CTX.setTextBaseline(VPos.CENTER);
                if (Orientation.VERTICAL == getOrientation()) {
                    CTX.setTextAlign(TextAlignment.RIGHT);
                    CTX.fillText(String.format(getLocale(), tickLabelFormatString, (maxValue -= majorTickSpace) + majorTickSpace), textPoint.getX(), textPoint.getY());
                } else {
                    CTX.setTextAlign(TextAlignment.CENTER);
                    CTX.fillText(String.format(getLocale(), tickLabelFormatString, (minValue += majorTickSpace) - majorTickSpace), textPoint.getX(), textPoint.getY());
                }
            } else if (minorTickSpace % 2 != 0 && counter % 5 == 0) {
                CTX.setLineWidth(size * 0.006);
                CTX.strokeLine(innerMediumPoint.getX(), innerMediumPoint.getY(), outerPoint.getX(), outerPoint.getY());
            } else if (counter % minorTickSpace == 0) {
                CTX.setLineWidth(size * 0.005);
                CTX.strokeLine(innerMinorPoint.getX(), innerMinorPoint.getY(), outerPoint.getX(), outerPoint.getY());
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
                width    = height / aspectRatio;
                size     = width < height ? width : height;
                stepSize = Math.abs((0.66793 * height) / getRange());
            } else {
                height   = width / aspectRatio;
                size     = width < height ? width : height;
                stepSize = Math.abs(0.9 * width / getRange());
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
        drawAxis(ctx);
    }
}
