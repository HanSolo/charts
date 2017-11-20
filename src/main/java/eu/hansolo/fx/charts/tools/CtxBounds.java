package eu.hansolo.fx.charts.tools;


public class CtxBounds {
    private double x;
    private double y;
    private double width;
    private double height;


    // ******************** Constructors **************************************
    public CtxBounds() {
        this(0, 0, 0, 0);
    }
    public CtxBounds(final double WIDTH, final double HEIGHT) {
        this(0, 0, WIDTH, HEIGHT);
    }
    public CtxBounds(final double X, final double Y, final double WIDTH, final double HEIGHT) {
        x      = X;
        y      = Y;
        width  = WIDTH;
        height = HEIGHT;
    }


    // ******************** Methods *******************************************
    public double getX() { return x; }
    public void setX(final double X) { x = X; }

    public double getY() { return y; }
    public void setY(final double Y) { y = Y; }

    public double getWidth() { return width; }
    public void setWidth(final double WIDTH) { width = Helper.clamp(0, Double.MAX_VALUE, WIDTH); }

    public double getHeight() { return height; }
    public void setHeight(final double HEIGHT) { height = Helper.clamp(0, Double.MAX_VALUE, HEIGHT); }
}