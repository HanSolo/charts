package eu.hansolo.fx.charts.data;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;


public interface MatrixData extends Data {

    int            getX();
    void           setX(int x);
    IntegerProperty xProperty();

    int            getY();
    void           setY(int y);
    IntegerProperty yProperty();

    double         getZ();
    void           setZ(double value);
    DoubleProperty zProperty();
}
