package eu.hansolo.fx.charts.tools;

public class CatmullRomSpline2D {
    private CatmullRomSpline splineXValues;
    private CatmullRomSpline splineYValues;


    // ******************** Constructors **************************************
    public CatmullRomSpline2D(final Point P0, final Point P1, final Point P2, final Point P3) {
        assert P0 != null : "p0 cannot be null";
        assert P1 != null : "p1 cannot be null";
        assert P2 != null : "p2 cannot be null";
        assert P3 != null : "p3 cannot be null";

        splineXValues = new CatmullRomSpline(P0.getX(), P1.getX(), P2.getX(), P3.getX());
        splineYValues = new CatmullRomSpline(P0.getY(), P1.getY(), P2.getY(), P3.getY());
    }


    // ******************** Methods *******************************************
    public Point q(final double T) { return new Point(splineXValues.q(T), splineYValues.q(T)); }
}
