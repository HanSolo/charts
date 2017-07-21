package eu.hansolo.fx.charts.data;

import eu.hansolo.fx.charts.Symbol;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;


public class XYZDataObject implements XYZData {
    private DoubleProperty         x;
    private DoubleProperty         y;
    private DoubleProperty         z;
    private StringProperty         name;
    private ObjectProperty<Color>  color;
    private ObjectProperty<Symbol> symbol;


    // ******************** Constructors **********************************
    public XYZDataObject() {
        this(0, 0, 0, "", Color.RED, Symbol.CIRCLE);
    }
    public XYZDataObject(final double X, final double Y, final double Z, final String NAME) {
        this(X, Y, Z, NAME, Color.RED, Symbol.CIRCLE);
    }
    public XYZDataObject(final double X, final double Y, final double Z, final String NAME, final Color COLOR) {
        this(X, Y, Z, NAME, COLOR, Symbol.CIRCLE);
    }
    public XYZDataObject(final double X, final double Y, final double Z, final String NAME, final Color COLOR, final Symbol SYMBOL) {
        x      = new DoublePropertyBase(X) {
            @Override public Object getBean() { return XYZDataObject.this; }
            @Override public String getName() { return "x"; }
        };
        y      = new DoublePropertyBase(Y) {
            @Override public Object getBean() { return XYZDataObject.this; }
            @Override public String getName() { return "y"; }
        };
        z      = new DoublePropertyBase(Z) {
            @Override public Object getBean() { return XYZDataObject.this; }
            @Override public String getName() { return "value"; }
        };
        name   = new StringPropertyBase(NAME) {
            @Override public Object getBean() { return XYZDataObject.this; }
            @Override public String getName() { return "name"; }
        };
        color  = new ObjectPropertyBase<Color>(COLOR) {
            @Override public Object getBean() { return XYZDataObject.this; }
            @Override public String getName() { return "color"; }
        };
        symbol = new ObjectPropertyBase<Symbol>(SYMBOL) {
            @Override public Object getBean() {  return XYZDataObject.this;  }
            @Override public String getName() {  return "symbol";  }
        };
    }


    // ******************** Methods ***************************************
    @Override public double getX() { return x.get(); }
    @Override public void setX(final double X) { x.set(X); }
    public DoubleProperty xProperty() { return x; }

    @Override public double getY() { return y.get(); }
    @Override public void setY(final double Y) { y.set(Y); }
    public DoubleProperty yProperty() { return y; }

    @Override public double getZ() { return z.get(); }
    @Override public void setZ(final double Z) { z.set(Z); }
    public DoubleProperty zProperty() { return z; }

    @Override public String getName() { return name.get(); }
    public void setName(final String NAME) { name.set(NAME); }
    public StringProperty nameProperty() { return name; }

    @Override public Color getColor() { return color.get(); }
    public void setColor(final Color COLOR) { color.set(COLOR); }
    public ObjectProperty<Color> colorProperty() { return color; }

    @Override public Symbol getSymbol() { return symbol.get(); }
    public void setSymbol(final Symbol SYMBOL) { symbol.set(SYMBOL); }
    public ObjectProperty<Symbol> symbolProperty() {  return symbol; }

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
