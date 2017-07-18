package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.XYData;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;


/**
 * Created by hansolo on 16.07.17.
 */
public class XYDataObject implements XYData {
    private DoubleProperty        x;
    private DoubleProperty        y;
    private StringProperty        name;
    private ObjectProperty<Color> color;


    // ******************** Constructors **********************************
    public XYDataObject() {
        this(0, 0, "", Color.RED);
    }
    public XYDataObject(final double X, final double Y, final String NAME, final Color COLOR) {
        x    = new DoublePropertyBase(X) {
            @Override public Object getBean() { return XYDataObject.this; }
            @Override public String getName() { return "x"; }
        };
        y    = new DoublePropertyBase(Y) {
            @Override public Object getBean() { return XYDataObject.this; }
            @Override public String getName() { return "y"; }
        };
        name = new StringPropertyBase(NAME) {
            @Override public Object getBean() { return XYDataObject.this; }
            @Override public String getName() { return "name"; }
        };
        color = new ObjectPropertyBase<Color>(COLOR) {
            @Override public Object getBean() { return XYDataObject.this; }
            @Override public String getName() { return "color"; }
        };
    }


    // ******************** Methods ***************************************
    @Override public double getX() { return x.get(); }
    public void setX(final double X) { x.set(X); }
    public DoubleProperty xProperty() { return x; }

    @Override public double getY() { return y.get(); }
    public void setY(final double Y) { y.set(Y); }
    public DoubleProperty yProperty() { return y; }

    @Override public String getName() { return name.get(); }
    public void setName(final String NAME) { name.set(NAME); }
    public StringProperty nameProperty() { return name; }

    @Override public Color getColor() { return color.get(); }
    public void setColor(final Color COLOR) { color.set(COLOR); }
    public ObjectProperty<Color> colorProperty() { return color; }

    @Override public String toString() {
        return new StringBuilder().append("{\n")
                                  .append("  \"name\":").append(getName()).append(",\n")
                                  .append("  \"x\":").append(getX()).append(",\n")
                                  .append("  \"y\":").append(getY()).append("\n")
                                  .append("  \"color\":").append(getColor().toString().replace("0x", "#")).append("\n")
                                  .append("}")
                                  .toString();
    }
}
