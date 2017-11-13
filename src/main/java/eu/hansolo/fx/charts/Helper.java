package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.tools.CatmullRom;
import eu.hansolo.fx.charts.tools.Point;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Helper {
    public static final double MAX_TICK_MARK_LENGTH = 0.125;
    public static final double MAX_TICK_MARK_WIDTH  = 0.02;

    public static final int clamp(final int MIN, final int MAX, final int VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }
    public static final long clamp(final long MIN, final long MAX, final long VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }
    public static final double clamp(final double MIN, final double MAX, final double VALUE) {
        if (Double.compare(VALUE, MIN) < 0) return MIN;
        if (Double.compare(VALUE, MAX) > 0) return MAX;
        return VALUE;
    }

    public static final double calcNiceNumber(final double RANGE, final boolean ROUND) {
        double niceFraction;
        double exponent = Math.floor(Math.log10(RANGE));   // exponent of range
        double fraction = RANGE / Math.pow(10, exponent);  // fractional part of range

        if (ROUND) {
            if (Double.compare(fraction, 1.5) < 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 3)  < 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 7) < 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        } else {
            if (Double.compare(fraction, 1) <= 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 2) <= 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 5) <= 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        }
        return niceFraction * Math.pow(10, exponent);
    }

    public static final void rotateContextForText(final GraphicsContext CTX, final double START_ANGLE, final double ANGLE, final TickLabelOrientation ORIENTATION) {
        switch (ORIENTATION) {
            case ORTHOGONAL:
                if ((360 - START_ANGLE - ANGLE) % 360 > 90 && (360 - START_ANGLE - ANGLE) % 360 < 270) {
                    CTX.rotate((180 - START_ANGLE - ANGLE) % 360);
                } else {
                    CTX.rotate((360 - START_ANGLE - ANGLE) % 360);
                }
                break;
            case TANGENT:
                if ((360 - START_ANGLE - ANGLE - 90) % 360 > 90 && (360 - START_ANGLE - ANGLE - 90) % 360 < 270) {
                    CTX.rotate((90 - START_ANGLE - ANGLE) % 360);
                } else {
                    CTX.rotate((270 - START_ANGLE - ANGLE) % 360);
                }
                break;
            case HORIZONTAL:
            default:
                break;
        }
    }

    public static void saveAsPng(final Node NODE, final String FILE_NAME) {
        final WritableImage SNAPSHOT = NODE.snapshot(new SnapshotParameters(), null);
        final String        NAME     = FILE_NAME.replace("\\.[a-zA-Z]{3,4}", "");
        final File          FILE     = new File(NAME + ".png");

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(SNAPSHOT, null), "png", FILE);
        } catch (IOException exception) {
            // handle exception here
        }
    }

    public static List<Point> subdividePoints(final List<Point> POINTS, final int SUB_DIVISIONS) {
        Point[] points = POINTS.toArray(new Point[0]);
        return Arrays.asList(subdividePoints(points, SUB_DIVISIONS));
    }

    public static Point[] subdividePoints(final Point[] POINTS, final int SUB_DIVISIONS) {
        assert POINTS != null;
        assert POINTS.length >= 3;
        int    noOfPoints = POINTS.length;

        Point[] subdividedPoints = new Point[((noOfPoints - 1) * SUB_DIVISIONS) + 1];

        double increments = 1.0 / (double) SUB_DIVISIONS;

        for (int i = 0 ; i < noOfPoints - 1 ; i++) {
            Point p0 = i == 0 ? POINTS[i] : POINTS[i - 1];
            Point p1 = POINTS[i];
            Point p2 = POINTS[i + 1];
            Point p3 = (i+2 == noOfPoints) ? POINTS[i + 1] : POINTS[i + 2];

            CatmullRom crs = new CatmullRom(p0, p1, p2, p3);

            for (int j = 0 ; j <= SUB_DIVISIONS ; j++) {
                subdividedPoints[(i * SUB_DIVISIONS) + j] = crs.q(j * increments);
            }
        }

        return subdividedPoints;
    }

    public static Point[] subdividePointsLinear(final Point[] POINTS, final int SUB_DIVISIONS) {
        assert  POINTS != null;
        assert  POINTS.length >= 3;

        int     noOfPoints       = POINTS.length;
        Point[] subdividedPoints = new Point[((noOfPoints - 1) * SUB_DIVISIONS) + 1];
        double  stepSize         = (POINTS[1].getX() - POINTS[0].getX()) / SUB_DIVISIONS;
        for (int i = 0 ; i < noOfPoints - 1 ; i++) {
            for (int j = 0 ; j <= SUB_DIVISIONS ; j++) {
                subdividedPoints[(i * SUB_DIVISIONS) + j] = calcIntermediatePoint(POINTS[i], POINTS[i+1], stepSize * j);
            }
        }
        return subdividedPoints;
    }

    public static Point calcIntermediatePoint(final Point LEFT_POINT, final Point RIGHT_POINT, final double INTERVAL_X) {
        double m = (RIGHT_POINT.getY() - LEFT_POINT.getY()) / (RIGHT_POINT.getX() - LEFT_POINT.getX());
        double x = INTERVAL_X;
        double y = m * x;
        return new Point(LEFT_POINT.getX() + x, LEFT_POINT.getY() + y);
    }

    public static Point calcIntersectionPoint(final Point LEFT_POINT, final Point RIGHT_POINT, final double INTERSECTION_Y) {
        double[] xy = calculateInterSectionPoint(LEFT_POINT.getX(), LEFT_POINT.getY(), RIGHT_POINT.getX(), RIGHT_POINT.getY(), INTERSECTION_Y);
        return new Point(xy[0], xy[1]);
    }
    public static double[] calculateInterSectionPoint(final Point LEFT_POINT, final Point RIGHT_POINT, final double INTERSECTION_Y) {
        return calculateInterSectionPoint(LEFT_POINT.getX(), LEFT_POINT.getY(), RIGHT_POINT.getX(), RIGHT_POINT.getY(), INTERSECTION_Y);
    }
    public static double[] calculateInterSectionPoint(final double X1, final double Y1, final double X2, final double Y2, final double INTERSECTION_Y) {
        double m = (Y2 - Y1) / (X2 - X1);
        double interSectionX = (INTERSECTION_Y - Y1) / m;
        return new double[] { X1 + interSectionX, INTERSECTION_Y };
    }

    public static double[] rotate(final double PX, double PY, final double RX, final double RY, final double ANGLE) {
        double x = RX + (Math.cos(ANGLE) * (PX - RX) - Math.sin(ANGLE) * (PY - RY));
        double y = RY + (Math.sin(ANGLE) * (PX - RX) + Math.cos(ANGLE) * (PY - RY));
        return new double[] {x, y};
    }
    public static Point rotate(final Point P1, final Point ROTATION_CENTER, final double ANGLE) {
        double[] xy = rotate(P1.getX(), P1.getY(), ROTATION_CENTER.getX(), ROTATION_CENTER.getY(), ANGLE);
        return new Point(xy[0], xy[1]);
    }

    public static Color getColorWithOpacity(final Color COLOR, final double OPACITY) {
        double red     = COLOR.getRed();
        double green   = COLOR.getGreen();
        double blue    = COLOR.getBlue();
        double opacity = clamp(0, 1, OPACITY);
        return Color.color(red, green, blue, opacity);
    }

    public static boolean isPowerOf10(final double VALUE) {
        double value = VALUE;
        while(value > 9 && value % 10 == 0) { value /= 10; }
        return value == 1;
    }

    public static void rotateCtx(final GraphicsContext CTX, final double X, final double Y, final double ANGLE) {
        CTX.translate(X, Y);
        CTX.rotate(ANGLE);
        CTX.translate(-X, -Y);
    }

    public static final List<Color> createColorPalette(final Color FROM_COLOR, final Color TO_COLOR, final int NO_OF_COLORS) {
        int    steps        = clamp(1, 50, NO_OF_COLORS) - 1;
        double step         = 1.0 / steps;
        double deltaRed     = (TO_COLOR.getRed()     - FROM_COLOR.getRed())     * step;
        double deltaGreen   = (TO_COLOR.getGreen()   - FROM_COLOR.getGreen())   * step;
        double deltaBlue    = (TO_COLOR.getBlue()    - FROM_COLOR.getBlue())    * step;
        double deltaOpacity = (TO_COLOR.getOpacity() - FROM_COLOR.getOpacity()) * step;

        List<Color> palette      = new ArrayList<>(NO_OF_COLORS);
        Color       currentColor = FROM_COLOR;
        palette.add(currentColor);
        for (int i = 0 ; i < steps ; i++) {
            double red     = clamp(0d, 1d, (currentColor.getRed()     + deltaRed));
            double green   = clamp(0d, 1d, (currentColor.getGreen()   + deltaGreen));
            double blue    = clamp(0d, 1d, (currentColor.getBlue()    + deltaBlue));
            double opacity = clamp(0d, 1d, (currentColor.getOpacity() + deltaOpacity));
            currentColor   = Color.color(red, green, blue, opacity);
            palette.add(currentColor);
        }
        return palette;
    }

    public static final Color getComplementaryColor(final Color COLOR) {
        return Color.hsb(COLOR.getHue() + 180, COLOR.getSaturation(), COLOR.getBrightness());
    }

    public static final Color[] getColorRangeMinMax(final Color COLOR, final int STEPS) {
        double hue            = COLOR.getHue();
        double saturation     = COLOR.getSaturation();
        double brightness     = COLOR.getBrightness();
        double saturationStep = saturation / STEPS;
        double brightnessStep = brightness / STEPS;
        double halfSteps      = STEPS / 2;
        Color fromColor       = COLOR.hsb(hue, saturation, clamp(0, 1, brightness + brightnessStep * halfSteps));
        Color toColor         = COLOR.hsb(hue, saturation, clamp(0, 1, brightness - brightnessStep * halfSteps));
        return new Color[] { fromColor, toColor };
    }

    public static final List<Color> createColorVariations(final Color COLOR, final int NO_OF_COLORS) {
        int    noOfColors  = clamp(1, 5, NO_OF_COLORS);
        double step        = 0.8 / noOfColors;
        double hue         = COLOR.getHue();
        double brg         = COLOR.getBrightness();
        List<Color> colors = new ArrayList<>(noOfColors);
        for (int i = 0 ; i < noOfColors ; i++) { colors.add(Color.hsb(hue, 0.2 + i * step, brg)); }
        return colors;
    }
}
