package eu.hansolo.fx.charts.tools;

public class Point {
    private double x, y;


    // ******************** Constructors **************************************
    public Point() {
        this(0.0, 0.0);
    }
    public Point(final double X, final double Y) {
        x = X;
        y = Y;
    }


    // ******************** Methods *******************************************
    public double getX() { return x; }
    public void setX(final double X) { x = X; }

    public double getY() { return y; }
    public void setY(final double Y) { y = Y; }

    @Override public String toString() {
        return new StringBuilder().append("x: ").append(x).append(", y: ").append(y).toString();
    }
}
