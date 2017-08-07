package eu.hansolo.fx.charts.data;

import javafx.beans.property.DoubleProperty;


public interface XYZData extends Data {

    double         getX();
    void           setX(double x);
    DoubleProperty xProperty();

    double         getY();
    void           setY(double y);
    DoubleProperty yProperty();

    double         getZ();
    void           setZ(double value);
    DoubleProperty zProperty();
}
