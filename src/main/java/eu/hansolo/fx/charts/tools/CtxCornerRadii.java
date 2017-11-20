package eu.hansolo.fx.charts.tools;


public class CtxCornerRadii {
    private double topLeft;
    private double topRight;
    private double bottomRight;
    private double bottomLeft;


    // ******************** Constructors **************************************
    public CtxCornerRadii() {
        this(0, 0, 0, 0);
    }
    public CtxCornerRadii(final double RADIUS) {
        this(RADIUS, RADIUS, RADIUS, RADIUS);
    }
    public CtxCornerRadii(final double TOP_LEFT, final double TOP_RIGHT,
                          final double BOTTOM_RIGHT, final double BOTTOM_LEFT) {
        topLeft = TOP_LEFT;
        topRight = TOP_RIGHT;
        bottomRight = BOTTOM_RIGHT;
        bottomLeft = BOTTOM_LEFT;
    }


    // ******************** Methods *******************************************
    public double getTopLeft() { return topLeft; }
    public void setTopLeft(final double VALUE) { topLeft = Helper.clamp(0, Double.MAX_VALUE, VALUE); }

    public double getTopRight() { return topRight; }
    public void setTopRight(final double VALUE) { topRight = Helper.clamp(0, Double.MAX_VALUE, VALUE); }

    public double getBottomRight() { return bottomRight; }
    public void setBottomRight(final double VALUE) { bottomRight = Helper.clamp(0, Double.MAX_VALUE, VALUE); }

    public double getBottomLeft() { return bottomLeft; }
    public void setBottomLeft(final double VALUE) { bottomLeft = Helper.clamp(0, Double.MAX_VALUE, VALUE); }
}