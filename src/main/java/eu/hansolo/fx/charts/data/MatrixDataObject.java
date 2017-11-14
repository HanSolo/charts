package eu.hansolo.fx.charts.data;

import eu.hansolo.fx.charts.Symbol;
import eu.hansolo.fx.charts.event.DataEvent;
import eu.hansolo.fx.charts.event.DataEventListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;

import java.util.concurrent.CopyOnWriteArrayList;


public class MatrixDataObject implements MatrixData {
    private final DataEvent                         DATA_EVENT = new DataEvent(MatrixDataObject.this);
    private CopyOnWriteArrayList<DataEventListener> listeners;
    private int                                     _x;
    private IntegerProperty                         x;
    private int                                     _y;
    private IntegerProperty                         y;
    private double                                  _z;
    private DoubleProperty                          z;
    private String                                  _name;
    private StringProperty                          name;
    private Color                                   _color;
    private ObjectProperty<Color>                   color;
    private Symbol                                  _symbol;
    private ObjectProperty<Symbol>                  symbol;


    // ******************** Constructors **********************************
    public MatrixDataObject() {
        this(0, 0, 0, "", Color.RED);
    }
    public MatrixDataObject(final int X, final int Y, final double Z) {
        this(X, Y, Z, "", Color.RED);
    }
    public MatrixDataObject(final int X, final int Y, final double Z, final String NAME) {
        this(X, Y, Z, NAME, Color.RED);
    }
    public MatrixDataObject(final int X, final int Y, final double Z, final String NAME, final Color COLOR) {
        _x        = X;
        _y        = Y;
        _z        = Z;
        _name     = NAME;
        _color    = COLOR;
        _symbol   = Symbol.NONE;
        listeners = new CopyOnWriteArrayList<>();
    }


    // ******************** Methods ***************************************
    @Override public int getX() { return null == x ? _x : x.get(); }
    @Override public void setX(final int X) {
        if (null == x) {
            _x = X;
            fireDataEvent(DATA_EVENT);
        } else {
            x.set(X);
        }
    }
    public IntegerProperty xProperty() {
        if (null == x) {
            x = new IntegerPropertyBase(_x) {
                @Override protected void invalidated() { fireDataEvent(DATA_EVENT); }
                @Override public Object getBean() { return MatrixDataObject.this; }
                @Override public String getName() { return "x"; }
            };
        }
        return x;
    }

    @Override public int getY() { return null == y ? _y : y.get(); }
    @Override public void setY(final int Y) {
        if (null == y) {
            _y = Y;
            fireDataEvent(DATA_EVENT);
        } else {
            y.set(Y);
        }
    }
    @Override public IntegerProperty yProperty() {
        if (null == y) {
            y = new IntegerPropertyBase(_y) {
                @Override protected void invalidated() { fireDataEvent(DATA_EVENT); }
                @Override public Object getBean() { return MatrixDataObject.this; }
                @Override public String getName() { return "y"; }
            };
        }
        return y;
    }

    @Override public double getZ() { return null == z ? _z : z.get(); }
    @Override public void setZ(final double Z) {
        if (null == z) {
            _z = Z;
            fireDataEvent(DATA_EVENT);
        } else {
            z.set(Z);
        }
    }
    @Override public DoubleProperty zProperty() {
        if (null == z) {
            z = new DoublePropertyBase(_z) {
                @Override protected void invalidated() { fireDataEvent(DATA_EVENT); }
                @Override public Object getBean() { return MatrixDataObject.this; }
                @Override public String getName() { return "z"; }
            };
        }
        return z;
    }

    @Override public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) {
        if (null == name) {
            _name = NAME;
            fireDataEvent(DATA_EVENT);
        } else {
            name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override protected void invalidated() { fireDataEvent(DATA_EVENT); }
                @Override public Object getBean() { return MatrixDataObject.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    @Override public Color getColor() { return null == color ? _color : color.get(); }
    public void setColor(final Color COLOR) {
        if (null == color) {
            _color = COLOR;
            fireDataEvent(DATA_EVENT);
        } else {
            color.set(COLOR);
        }
    }
    public ObjectProperty<Color> colorProperty() {
        if (null == color) {
            color = new ObjectPropertyBase<Color>(_color) {
                @Override protected void invalidated() { fireDataEvent(DATA_EVENT); }
                @Override public Object getBean() { return MatrixDataObject.this; }
                @Override public String getName() { return "color"; }
            };
            _color = null;
        }
        return color;
    }

    @Override public Symbol getSymbol() { return Symbol.NONE; }
    public void setSymbol(final Symbol SYMBOL) {}
    public ObjectProperty<Symbol> symbolProperty() {
        if (null == symbol) {
            symbol = new ObjectPropertyBase<Symbol>(_symbol) {
                @Override protected void invalidated() { fireDataEvent(DATA_EVENT); }
                @Override public Object getBean() {  return MatrixDataObject.this;  }
                @Override public String getName() {  return "symbol";  }
            };
            _symbol = null;
        }
        return symbol;
    }


    // ******************** Event handling ************************************
    public void setOnDataEvent(final DataEventListener LISTENER) { addDataEventListener(LISTENER); }
    public void addDataEventListener(final DataEventListener LISTENER) { if (!listeners.contains(LISTENER)) listeners.add(LISTENER); }
    public void removeDataEventListener(final DataEventListener LISTENER) { if (listeners.contains(LISTENER)) listeners.remove(LISTENER); }

    public void fireDataEvent(final DataEvent EVENT) {
        for (DataEventListener listener : listeners) { listener.onDataEvent(EVENT); }
    }


    @Override public String toString() {
        return new StringBuilder().append("{\n")
                                  .append("  \"name\":\"").append(getName()).append("\",\n")
                                  .append("  \"x\":").append(getX()).append(",\n")
                                  .append("  \"y\":").append(getY()).append(",\n")
                                  .append("  \"z\":").append(getZ()).append(",\n")
                                  .append("  \"color\":\"").append(getColor().toString().replace("0x", "#")).append("\",\n")
                                  .append("  \"symbol\":\"").append(getSymbol().name()).append("\"\n")
                                  .append("}")
                                  .toString();
    }
}
