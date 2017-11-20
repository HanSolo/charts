package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.tools.CatmullRom;
import eu.hansolo.fx.charts.tools.CtxBounds;
import eu.hansolo.fx.charts.tools.CtxCornerRadii;
import eu.hansolo.fx.charts.tools.Point;
import javafx.animation.Interpolator;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;

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

    public static final void saveAsPng(final Node NODE, final String FILE_NAME) {
        final WritableImage SNAPSHOT = NODE.snapshot(new SnapshotParameters(), null);
        final String        NAME     = FILE_NAME.replace("\\.[a-zA-Z]{3,4}", "");
        final File          FILE     = new File(NAME + ".png");

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(SNAPSHOT, null), "png", FILE);
        } catch (IOException exception) {
            // handle exception here
        }
    }

    public static final List<Point> subdividePoints(final List<Point> POINTS, final int SUB_DIVISIONS) {
        Point[] points = POINTS.toArray(new Point[0]);
        return Arrays.asList(subdividePoints(points, SUB_DIVISIONS));
    }

    public static final Point[] subdividePoints(final Point[] POINTS, final int SUB_DIVISIONS) {
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

    public static final Point[] subdividePointsLinear(final Point[] POINTS, final int SUB_DIVISIONS) {
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

    public static final Point calcIntermediatePoint(final Point LEFT_POINT, final Point RIGHT_POINT, final double INTERVAL_X) {
        double m = (RIGHT_POINT.getY() - LEFT_POINT.getY()) / (RIGHT_POINT.getX() - LEFT_POINT.getX());
        double x = INTERVAL_X;
        double y = m * x;
        return new Point(LEFT_POINT.getX() + x, LEFT_POINT.getY() + y);
    }

    public static final Point calcIntersectionPoint(final Point LEFT_POINT, final Point RIGHT_POINT, final double INTERSECTION_Y) {
        double[] xy = calculateInterSectionPoint(LEFT_POINT.getX(), LEFT_POINT.getY(), RIGHT_POINT.getX(), RIGHT_POINT.getY(), INTERSECTION_Y);
        return new Point(xy[0], xy[1]);
    }
    public static final double[] calculateInterSectionPoint(final Point LEFT_POINT, final Point RIGHT_POINT, final double INTERSECTION_Y) {
        return calculateInterSectionPoint(LEFT_POINT.getX(), LEFT_POINT.getY(), RIGHT_POINT.getX(), RIGHT_POINT.getY(), INTERSECTION_Y);
    }
    public static final double[] calculateInterSectionPoint(final double X1, final double Y1, final double X2, final double Y2, final double INTERSECTION_Y) {
        double m = (Y2 - Y1) / (X2 - X1);
        double interSectionX = (INTERSECTION_Y - Y1) / m;
        return new double[] { X1 + interSectionX, INTERSECTION_Y };
    }

    public static final double[] rotate(final double PX, double PY, final double RX, final double RY, final double ANGLE) {
        double x = RX + (Math.cos(ANGLE) * (PX - RX) - Math.sin(ANGLE) * (PY - RY));
        double y = RY + (Math.sin(ANGLE) * (PX - RX) + Math.cos(ANGLE) * (PY - RY));
        return new double[] {x, y};
    }
    public static final Point rotate(final Point P1, final Point ROTATION_CENTER, final double ANGLE) {
        double[] xy = rotate(P1.getX(), P1.getY(), ROTATION_CENTER.getX(), ROTATION_CENTER.getY(), ANGLE);
        return new Point(xy[0], xy[1]);
    }

    public static final Color getColorWithOpacity(final Color COLOR, final double OPACITY) {
        double red     = COLOR.getRed();
        double green   = COLOR.getGreen();
        double blue    = COLOR.getBlue();
        double opacity = clamp(0, 1, OPACITY);
        return Color.color(red, green, blue, opacity);
    }

    public static final boolean isPowerOf10(final double VALUE) {
        double value = VALUE;
        while(value > 9 && value % 10 == 0) { value /= 10; }
        return value == 1;
    }

    public static final void rotateCtx(final GraphicsContext CTX, final double X, final double Y, final double ANGLE) {
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

    public static final LinearGradient createColorVariationGradient(final Color COLOR, final int NO_OF_COLORS) {
        List<Color> colorVariations = createColorVariations(COLOR, NO_OF_COLORS);
        List<Stop>  stops = new ArrayList<>(NO_OF_COLORS);
        double step = 1.0 / NO_OF_COLORS;
        for (int i = 0 ; i < NO_OF_COLORS ; i++) {
            stops.add(new Stop(i * step, colorVariations.get(i)));
        }
        return new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
    }

    public static double[] colorToYUV(final Color COLOR) {
        final double WEIGHT_FACTOR_RED   = 0.299;
        final double WEIGHT_FACTOR_GREEN = 0.587;
        final double WEIGHT_FACTOR_BLUE  = 0.144;
        final double U_MAX               = 0.436;
        final double V_MAX               = 0.615;
        double y = clamp(0, 1, WEIGHT_FACTOR_RED * COLOR.getRed() + WEIGHT_FACTOR_GREEN * COLOR.getGreen() + WEIGHT_FACTOR_BLUE * COLOR.getBlue());
        double u = clamp(-U_MAX, U_MAX, U_MAX * ((COLOR.getBlue() - y) / (1 - WEIGHT_FACTOR_BLUE)));
        double v = clamp(-V_MAX, V_MAX, V_MAX * ((COLOR.getRed() - y) / (1 - WEIGHT_FACTOR_RED)));
        return new double[] { y, u, v };
    }

    public static final boolean isBright(final Color COLOR) { return Double.compare(colorToYUV(COLOR)[0], 0.5) >= 0.0; }
    public static final boolean isDark(final Color COLOR) { return colorToYUV(COLOR)[0] < 0.5; }

    public static final boolean isInRectangle(final double X, final double Y,
                                              final double MIN_X, final double MIN_Y,
                                              final double MAX_X, final double MAX_Y) {
        return (Double.compare(X, MIN_X) >= 0 &&
                Double.compare(X, MAX_X) <= 0 &&
                Double.compare(Y, MIN_Y) >= 0 &&
                Double.compare(Y, MAX_Y) <= 0);
    }

    public static final boolean isInEllipse(final double X, final double Y,
                                            final double ELLIPSE_CENTER_X, final double ELLIPSE_CENTER_Y,
                                            final double ELLIPSE_RADIUS_X, final double ELLIPSE_RADIUS_Y) {
        return Double.compare(((((X - ELLIPSE_CENTER_X) * (X - ELLIPSE_CENTER_X)) / (ELLIPSE_RADIUS_X * ELLIPSE_RADIUS_X)) +
                               (((Y - ELLIPSE_CENTER_Y) * (Y - ELLIPSE_CENTER_Y)) / (ELLIPSE_RADIUS_Y * ELLIPSE_RADIUS_Y))), 1) <= 0.0;
    }

    public static final boolean isInPolygon(final double X, final double Y, final Polygon POLYGON) {
        List<Double> points              = POLYGON.getPoints();
        int          noOfPointsInPolygon = POLYGON.getPoints().size() / 2;
        double[]     pointsX             = new double[noOfPointsInPolygon];
        double[]     pointsY             = new double[noOfPointsInPolygon];
        int          pointCounter        = 0;
        for (int i = 0 ; i < points.size() ; i++) {
            if (i % 2 == 0) {
                pointsX[i] = points.get(pointCounter);
            } else {
                pointsY[i] = points.get(pointCounter);
                pointCounter++;
            }
        }
        return isInPolygon(X, Y, noOfPointsInPolygon, pointsX, pointsY);
    }
    public static final boolean isInPolygon(final double X, final double Y, final int NO_OF_POINTS_IN_POLYGON, final double[] POINTS_X, final double[] POINTS_Y) {
        if (NO_OF_POINTS_IN_POLYGON != POINTS_X.length || NO_OF_POINTS_IN_POLYGON != POINTS_Y.length) { return false; }
        boolean inside = false;
        for (int i = 0, j = NO_OF_POINTS_IN_POLYGON - 1; i < NO_OF_POINTS_IN_POLYGON ; j = i++) {
            if (((POINTS_Y[i] > Y) != (POINTS_Y[j] > Y)) && (X < (POINTS_X[j] - POINTS_X[i]) * (Y - POINTS_Y[i]) / (POINTS_Y[j] - POINTS_Y[i]) + POINTS_X[i])) {
                inside = !inside;
            }
        }
        return inside;
    }

    public static final boolean isInRingSegment(final double X, final double Y,
                                                final double CENTER_X, final double CENTER_Y,
                                                final double OUTER_RADIUS, final double INNER_RADIUS,
                                                final double START_ANGLE, final double SEGMENT_ANGLE) {
        double angleOffset = 90.0;
        double pointRadius = Math.sqrt((X - CENTER_X) * (X - CENTER_X) + (Y - CENTER_Y) * (Y - CENTER_Y));
        double pointAngle  = getAngleFromXY(X, Y, CENTER_X, CENTER_Y, angleOffset);
        double startAngle  = angleOffset - START_ANGLE;
        double endAngle    = startAngle + SEGMENT_ANGLE;

        return (Double.compare(pointRadius, INNER_RADIUS) >= 0 &&
                Double.compare(pointRadius, OUTER_RADIUS) <= 0 &&
                Double.compare(pointAngle, startAngle) >= 0 &&
                Double.compare(pointAngle, endAngle) <= 0);
    }

    public static final double getAngleFromXY(final double X, final double Y, final double CENTER_X, final double CENTER_Y) {
        return getAngleFromXY(X, Y, CENTER_X, CENTER_Y, 90.0);
    }
    public static final double getAngleFromXY(final double X, final double Y, final double CENTER_X, final double CENTER_Y, final double ANGLE_OFFSET) {
        // For ANGLE_OFFSET =  0 -> Angle of 0 is at 3 o'clock
        // For ANGLE_OFFSET = 90  ->Angle of 0 is at 12 o'clock
        double deltaX      = X - CENTER_X;
        double deltaY      = Y - CENTER_Y;
        double radius      = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        double nx          = deltaX / radius;
        double ny          = deltaY / radius;
        double theta       = Math.atan2(ny, nx);
        theta              = Double.compare(theta, 0.0) >= 0 ? Math.toDegrees(theta) : Math.toDegrees((theta)) + 360.0;
        double angle       = (theta + ANGLE_OFFSET) % 360;
        return angle;
    }

    public static final void drawRoundedRect(final GraphicsContext CTX, final CtxBounds BOUNDS, final CtxCornerRadii RADII) {
        double x           = BOUNDS.getX();
        double y           = BOUNDS.getY();
        double width       = BOUNDS.getWidth();
        double height      = BOUNDS.getHeight();
        double xPlusWidth  = x + width;
        double yPlusHeight = y + height;

        CTX.beginPath();
        CTX.moveTo(x + RADII.getTopLeft(), y);
        CTX.lineTo(xPlusWidth - RADII.getTopRight(), y);
        CTX.quadraticCurveTo(xPlusWidth, y, xPlusWidth, y + RADII.getTopRight());
        CTX.lineTo(xPlusWidth, yPlusHeight - RADII.getBottomRight());
        CTX.quadraticCurveTo(xPlusWidth, yPlusHeight, xPlusWidth - RADII.getBottomRight(), yPlusHeight);
        CTX.lineTo(x + RADII.getBottomLeft(), yPlusHeight);
        CTX.quadraticCurveTo(x, yPlusHeight, x, yPlusHeight - RADII.getBottomLeft());
        CTX.lineTo(x, y + RADII.getTopLeft());
        CTX.quadraticCurveTo(x, y, x + RADII.getTopLeft(), y);
        CTX.closePath();
    }

    public static final Color getColorAt(final LinearGradient GRADIENT, final double FRACTION) {
        List<Stop> stops     = GRADIENT.getStops();
        double     fraction  = FRACTION < 0f ? 0f : (FRACTION > 1 ? 1 : FRACTION);
        Stop       lowerStop = new Stop(0.0, stops.get(0).getColor());
        Stop       upperStop = new Stop(1.0, stops.get(stops.size() - 1).getColor());

        for (Stop stop : stops) {
            double currentFraction = stop.getOffset();
            if (Double.compare(currentFraction, fraction) == 0) {
                return stop.getColor();
            } else if (Double.compare(currentFraction, fraction) < 0) {
                lowerStop = new Stop(currentFraction, stop.getColor());
            } else {
                upperStop = new Stop(currentFraction, stop.getColor());
                break;
            }
        }

        double interpolationFraction = (fraction - lowerStop.getOffset()) / (upperStop.getOffset() - lowerStop.getOffset());
        return (Color) Interpolator.LINEAR.interpolate(lowerStop.getColor(), upperStop.getColor(), interpolationFraction);
    }
}
