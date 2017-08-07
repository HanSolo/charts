package eu.hansolo.fx.charts.data;


import javafx.beans.property.DoubleProperty;


/**
 * Created by hansolo on 17.07.17.
 */
public interface XYData extends Data {

    double         getX();
    void           setX(double x);
    DoubleProperty xProperty();

    double         getY();
    void           setY(double y);
    DoubleProperty yProperty();
}
