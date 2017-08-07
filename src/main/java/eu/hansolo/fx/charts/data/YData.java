package eu.hansolo.fx.charts.data;

import javafx.beans.property.DoubleProperty;


public interface YData extends Data {

    double         getY();
    void           setY(double y);
    DoubleProperty yProperty();
}
