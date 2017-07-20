package eu.hansolo.fx.charts.data;


/**
 * Created by hansolo on 17.07.17.
 */
public interface XYData extends Data {

    double getX();
    void   setX(double x);

    double getY();
    void   setY(double y);

    double getZ();
    void   setZ(double value);
}
