package eu.hansolo.fx.charts.data;

public interface XYZData extends Data {

    double getX();
    void   setX(double x);

    double getY();
    void   setY(double y);

    double getZ();
    void   setZ(double value);
}
