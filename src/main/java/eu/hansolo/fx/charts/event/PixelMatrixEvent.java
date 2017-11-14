package eu.hansolo.fx.charts.event;

import eu.hansolo.fx.charts.tools.Point;


public class PixelMatrixEvent {
    private final int    X;
    private final int    Y;
    private final double MOUSE_SCREEN_X;
    private final double MOUSE_SCREEN_Y;


    // ******************** Constructors **************************************
    public PixelMatrixEvent(final int X, final int Y, final double MOUSE_X, final double MOUSE_Y) {
        this.X         = X;
        this.Y         = Y;
        MOUSE_SCREEN_X = MOUSE_X;
        MOUSE_SCREEN_Y = MOUSE_Y;
    }


    // ******************** Methods *******************************************
    public int getX() { return X; }
    public int getY() { return Y; }

    public double getMouseScreenX() { return MOUSE_SCREEN_X; }
    public double getMouseScreenY() { return MOUSE_SCREEN_Y; }
    public Point getMouseScreenPos() { return new Point(MOUSE_SCREEN_X, MOUSE_SCREEN_Y); }
}