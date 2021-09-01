/*
 * Copyright (c) 2017 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.charts.tools;

import eu.hansolo.fx.charts.Axis;
import eu.hansolo.fx.charts.AxisBuilder;
import eu.hansolo.fx.charts.Position;
import eu.hansolo.fx.charts.TickLabelOrientation;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.DataPoint;
import eu.hansolo.fx.charts.data.XYChartItem;
import javafx.animation.Interpolator;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;


public class Helper {
    public static final double   MIN_FONT_SIZE        = 5;
    public static final double   MAX_TICK_MARK_LENGTH = 0.125;
    public static final double   MAX_TICK_MARK_WIDTH  = 0.02;
    public static final String[] ABBREVIATIONS        = { "k", "M", "G", "T", "P", "E", "Z", "Y" };

    public enum Interval {
        DECADE(ChronoUnit.DECADES, 1, 311_040_000, 155_520_000, 31_104_000), // 10 years
        YEAR(ChronoUnit.YEARS, 1, 31_104_000, 15_552_000, 2_592_000),        // 360 days
        MONTH_6(ChronoUnit.MONTHS, 6, 15_552_000, 2_592_000, 2_592_000),     // 180 days
        MONTH_3(ChronoUnit.MONTHS, 3, 7_776_000, 2_592_000, 864_000),        // 90 days
        MONTH_1(ChronoUnit.MONTHS, 1, 2_592_000, 864_000, 86_400),           // 30 days
        DAY(ChronoUnit.DAYS, 1, 86_400, 43_200, 3_600),
        HOUR_12(ChronoUnit.HOURS, 12, 43_200, 3_600, 1_800),
        HOUR_6(ChronoUnit.HOURS, 6, 21_600, 3_600, 900),
        HOUR_3(ChronoUnit.HOURS, 3, 10_800, 3_600, 300),
        HOUR_1(ChronoUnit.HOURS, 1, 3_600, 900, 300),
        MINUTE_15(ChronoUnit.MINUTES, 15, 900, 300, 60),
        MINUTE_5(ChronoUnit.MINUTES, 5, 300, 60, 10),
        MINUTE_1(ChronoUnit.MINUTES, 1, 60, 5, 1),
        SECOND_15(ChronoUnit.SECONDS, 15, 15, 5, 1),
        SECOND_5(ChronoUnit.SECONDS, 5, 5, 1, 1),
        SECOND_1(ChronoUnit.SECONDS, 1, 1, 1, 1);
        //MILLISECOND(ChronoUnit.MILLIS, 1);

        private final ChronoUnit INTERVAL;
        private final int        AMOUNT;
        private final long       MAJOR_TICK_SPACE;
        private final long       MEDIUM_TICK_SPACE;
        private final long       MINOR_TICK_SPACE;


        Interval(final ChronoUnit INTERVAL, final int AMOUNT, final long MAJOR_TICK_SPACE, final long MEDIUM_TICK_SPACE, final long MINOR_TICK_SPACE) {
            this.INTERVAL          = INTERVAL;
            this.AMOUNT            = AMOUNT;
            this.MAJOR_TICK_SPACE  = MAJOR_TICK_SPACE;
            this.MEDIUM_TICK_SPACE = MEDIUM_TICK_SPACE;
            this.MINOR_TICK_SPACE  = MINOR_TICK_SPACE;
        }


        public ChronoUnit getInterval() { return INTERVAL; }

        public int getAmount() { return AMOUNT; }

        public long getMajorTickSpace() { return MAJOR_TICK_SPACE; }
        public long getMediumTickSpace() { return MEDIUM_TICK_SPACE; }
        public long getMinorTickSpace() { return MINOR_TICK_SPACE; }
    }

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
    public static final Instant clamp(final Instant MIN, final Instant MAX, final Instant VALUE) {
        if (VALUE.isBefore(MIN)) return MIN;
        if (VALUE.isAfter(MAX)) return MAX;
        return VALUE;
    }
    public static final LocalDateTime clamp(final LocalDateTime MIN, final LocalDateTime MAX, final LocalDateTime VALUE) {
        if (VALUE.isBefore(MIN)) return MIN;
        if (VALUE.isAfter(MAX)) return MAX;
        return VALUE;
    }
    public static final LocalDate clamp(final LocalDate MIN, final LocalDate MAX, final LocalDate VALUE) {
        if (VALUE.isBefore(MIN)) return MIN;
        if (VALUE.isAfter(MAX)) return MAX;
        return VALUE;
    }

    public static final double clampMin(final double MIN, final double VALUE) {
        if (VALUE < MIN) return MIN;
        return VALUE;
    }

    public static final double clampMax(final double MAX, final double VALUE) {
        if (VALUE > MAX) return MAX;
        return VALUE;
    }

    public static final double nearest(final double LESS, final double VALUE, final double MORE) {
        double lessDiff = VALUE - LESS;
        double moreDiff = MORE - VALUE;
        return lessDiff < moreDiff ? LESS : MORE;
    }

    public static final double[] calcAutoScale(final double MIN_VALUE, final double MAX_VALUE) {
        double maxNoOfMajorTicks = 10;
        double maxNoOfMinorTicks = 10;
        double minorTickSpace    = 1;
        double majorTickSpace    = 10;
        double niceRange         = (Helper.calcNiceNumber((MAX_VALUE - MIN_VALUE), false));
        majorTickSpace           = Helper.calcNiceNumber(niceRange / (maxNoOfMajorTicks - 1), true);
        minorTickSpace           = Helper.calcNiceNumber(majorTickSpace / (maxNoOfMinorTicks - 1), true);
        double niceMinValue      = (Math.floor(MIN_VALUE / majorTickSpace) * majorTickSpace);
        double niceMaxValue      = (Math.ceil(MAX_VALUE / majorTickSpace) * majorTickSpace);

        return new double[] { minorTickSpace, majorTickSpace, niceMinValue, niceMaxValue };
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

    public static final void rotateCtx(final GraphicsContext CTX, final double X, final double Y, final double ANGLE) {
        CTX.translate(X, Y);
        CTX.rotate(ANGLE);
        CTX.translate(-X, -Y);
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

    public static final double[] rotatePointAroundRotationCenter(final double X, final double Y, final double RX, final double RY, final double ANGLE) {
        final double rad = Math.toRadians(ANGLE);
        final double sin = Math.sin(rad);
        final double cos = Math.cos(rad);
        final double nX  = RX + (X - RX) * cos - (Y - RY) * sin;
        final double nY  = RY + (X - RX) * sin + (Y - RY) * cos;
        return new double[] { nX, nY };
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

    public static final Point[] subdividePointsRadial(final Point[] POINTS, final int SUB_DIVISIONS){
        assert POINTS != null;
        assert POINTS.length >= 3;
        int    noOfPoints = POINTS.length;

        Point[] subdividedPoints = new Point[((noOfPoints - 1) * SUB_DIVISIONS) + 1];

        double increments = 1.0 / (double) SUB_DIVISIONS;

        for (int i = 0 ; i < noOfPoints - 1 ; i++) {
            Point p0 = i == 0 ? POINTS[noOfPoints - 2] : POINTS[i - 1];
            Point p1 = POINTS[i];
            Point p2 = POINTS[i + 1];
            Point p3 = (i == (noOfPoints - 2)) ? POINTS[1] : POINTS[i + 2];

            CatmullRom<Point> crs = new CatmullRom<>(p0, p1, p2, p3);

            for (int j = 0 ; j <= SUB_DIVISIONS ; j++) {
                subdividedPoints[(i * SUB_DIVISIONS) + j] = crs.q(j * increments);
            }
        }

        return subdividedPoints;
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

            CatmullRom<Point> crs = new CatmullRom<>(p0, p1, p2, p3);

            for (int j = 0 ; j <= SUB_DIVISIONS ; j++) {
                Point subPoint = crs.q(j * increments);
                subPoint.setEmpty(p1.isEmpty() || p2.isEmpty());
                subdividedPoints[(i * SUB_DIVISIONS) + j] = subPoint;
            }
        }

        return subdividedPoints;
    }

    public static final List<DataPoint> subdivideDataPoints(final List<DataPoint> POINTS, final int SUB_DIVISIONS) {
        DataPoint[] points = POINTS.toArray(new DataPoint[0]);
        return Arrays.asList(subdividePoints(points, SUB_DIVISIONS));
    }
    public static final DataPoint[] subdividePoints(final DataPoint[] POINTS, final int SUB_DIVISIONS) {
        assert POINTS != null;
        assert POINTS.length >= 3;
        int    noOfPoints = POINTS.length;

        DataPoint[] subdividedPoints = new DataPoint[((noOfPoints - 1) * SUB_DIVISIONS) + 1];

        double increments = 1.0 / (double) SUB_DIVISIONS;

        for (int i = 0 ; i < noOfPoints - 1 ; i++) {
            DataPoint p0 = i == 0 ? POINTS[i] : POINTS[i - 1];
            DataPoint p1 = POINTS[i];
            DataPoint p2 = POINTS[i + 1];
            DataPoint p3 = (i+2 == noOfPoints) ? POINTS[i + 1] : POINTS[i + 2];

            CatmullRom<DataPoint> crs = new CatmullRom<>(p0, p1, p2, p3);

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

    public static final Point calcIntersectionOfTwoLines(Point A, Point B, Point C, Point D) {
        return calcIntersectionOfTwoLines(A.getX(), A.getY(), B.getX(), B.getY(), C.getX(), C.getY(), D.getX(), D.getY());
    }
    public static final Point calcIntersectionOfTwoLines(final double X1, final double Y1, final double X2, final double Y2,
                                                         final double X3, final double Y3, final double X4, final double Y4) {

        // Line AB represented as a1x + b1y = c1
        double a1 = Y2 - Y1;
        double b1 = X1 - X2;
        double c1 = a1 * X1 + b1 * Y1;

        // Line CD represented as a2x + b2y = c2
        double a2 = Y4 - Y3;
        double b2 = X3 - X4;
        double c2 = a2 * X3 + b2 * Y3;

        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) { // Lines are parallel
            return new Point(Double.MAX_VALUE, Double.MAX_VALUE);
        } else {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;
            return new Point(x, y);
        }
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

    public static <K, V extends Comparable<V>> V getMaxValueInMap(final Map<K, V> MAP) {
        Entry<K, V> maxEntry = Collections.max(MAP.entrySet(), Comparator.comparing(Entry::getValue));
        return maxEntry.getValue();
    }
    public static <K, V extends Comparable<V>> K getKeyWithMaxValueInMap(final Map<K, V> MAP) {
        Entry<K, V> maxEntry = Collections.max(MAP.entrySet(), Comparator.comparing(Entry::getValue));
        return maxEntry.getKey();
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
        if (NO_OF_COLORS > 25) { throw new IllegalArgumentException("Not more than 25 number of colors are allowed"); }
        int    noOfColors  = clamp(1, 25, NO_OF_COLORS);
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

    public static final double[] colorToYUV(final Color COLOR) {
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

    public static final Color getContrastColor(final Color COLOR) {
        return COLOR.getBrightness() > 0.5 ? Color.BLACK : Color.WHITE;
    }

    public static final double adjustTextSize(final Text TEXT, final double MAX_WIDTH, final double FONT_SIZE) {
        final String FONT_NAME          = TEXT.getFont().getName();
        double       adjustableFontSize = FONT_SIZE;

        while (TEXT.getLayoutBounds().getWidth() > MAX_WIDTH && adjustableFontSize > MIN_FONT_SIZE) {
            adjustableFontSize -= 0.1;
            TEXT.setFont(new Font(FONT_NAME, adjustableFontSize));
        }
        return adjustableFontSize;
    }
    public static final void adjustTextSize(final Label TEXT, final double MAX_WIDTH, final double FONT_SIZE) {
        final String FONT_NAME          = TEXT.getFont().getName();
        double       adjustableFontSize = FONT_SIZE;

        while (TEXT.getLayoutBounds().getWidth() > MAX_WIDTH && adjustableFontSize > MIN_FONT_SIZE) {
            adjustableFontSize -= 0.1;
            TEXT.setFont(new Font(FONT_NAME, adjustableFontSize));
        }
    }

    public static final boolean isInRectangle(final double X, final double Y,
                                              final double MIN_X, final double MIN_Y,
                                              final double MAX_X, final double MAX_Y) {
        return (Double.compare(X, MIN_X) >= 0 &&
                Double.compare(X, MAX_X) <= 0 &&
                Double.compare(Y, MIN_Y) >= 0 &&
                Double.compare(Y, MAX_Y) <= 0);
    }

    public static final boolean isInCircle(final double x, final double y, final double centerX, final double centerY, final double radius) {
        double deltaX = centerX - x;
        double deltaY = centerY - y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY) <= radius;
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

    public static final boolean isInSector(final double X, final double Y, final double CENTER_X, final double CENTER_Y, final double RADIUS, final double START_ANGLE, final double SEGMENT_ANGLE) {
        return isInRingSegment(X, Y, CENTER_X, CENTER_Y, RADIUS, 0, START_ANGLE, SEGMENT_ANGLE);
    }

    public static final boolean isInRingSegment(final double MOUSE_X, final double MOUSE_Y, final double X, final double Y, final double WIDTH, final double HEIGHT, final double START_ANGLE, final double SEGMENT_ANGLE, final double LINE_WIDTH) {
        double centerX     = X + WIDTH * 0.5;
        double centerY     = Y + HEIGHT * 0.5;
        double size        = WIDTH < HEIGHT ? WIDTH : HEIGHT;
        double outerRadius = (size + LINE_WIDTH) * 0.5;
        double innerRadius = (size - LINE_WIDTH) * 0.5;
        return isInRingSegment(MOUSE_X, MOUSE_Y, centerX, centerY, outerRadius, innerRadius, START_ANGLE, SEGMENT_ANGLE);
    }
    public static final boolean isInRingSegment(final double X, final double Y,
                                                final double CENTER_X, final double CENTER_Y,
                                                final double OUTER_RADIUS, final double INNER_RADIUS,
                                                final double START_ANGLE, final double SEGMENT_ANGLE) {
        double angleOffset = 90.0;
        double pointRadius = Math.sqrt((X - CENTER_X) * (X - CENTER_X) + (Y - CENTER_Y) * (Y - CENTER_Y));
        double pointAngle  = getAngleFromXY(X, Y, CENTER_X, CENTER_Y, angleOffset);
        return (Double.compare(pointRadius, INNER_RADIUS) >= 0 &&
                Double.compare(pointRadius, OUTER_RADIUS) <= 0 &&
                Double.compare(pointAngle, START_ANGLE) >= 0 &&
                Double.compare(pointAngle, (START_ANGLE + SEGMENT_ANGLE)) <= 0);
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

    public static final String format(final double NUMBER, final int DECIMALS) {
        return format(NUMBER, clamp(0, 12, DECIMALS), Locale.US);
    }
    public static final String format(final double NUMBER, final int DECIMALS, final Locale LOCALE) {
        String formatString = new StringBuilder("%.").append(clamp(0, 12, DECIMALS)).append("f").toString();
        double value;
        for(int i = ABBREVIATIONS.length - 1 ; i >= 0; i--) {
            value = Math.pow(1000, i+1);
            if (Double.compare(NUMBER, -value) <= 0 || Double.compare(NUMBER, value) >= 0) {
                return String.format(LOCALE, formatString, (NUMBER / value)) + ABBREVIATIONS[i];
            }
        }
        return String.format(LOCALE, formatString, NUMBER);
    }

    public static final <T extends Point> boolean isInPolygon(final double X, final double Y, final List<T> POLYGON) {
        int      noOfPointsInPolygon = POLYGON.size();
        double[] pointsX             = new double[noOfPointsInPolygon];
        double[] pointsY             = new double[noOfPointsInPolygon];
        for (int i = 0 ; i < noOfPointsInPolygon ; i++) {
            pointsX[i] = POLYGON.get(i).getX();
            pointsY[i] = POLYGON.get(i).getY();
        }
        return isInPolygon(X, Y, noOfPointsInPolygon, pointsX, pointsY);
    }

    public static final <T extends Point> double squareDistance(final T P1, final T P2) {
        return squareDistance(P1.getX(), P1.getY(), P2.getX(), P2.getY());
    }
    public static final double squareDistance(final double X1, final double Y1, final double X2, final double Y2) {
        double deltaX = (X1 - X2);
        double deltaY = (Y1 - Y2);
        return (deltaX * deltaX) + (deltaY * deltaY);
    }

    public static final double distance(final double X1, final double Y1, final double X2, final double Y2) {
        return Math.sqrt((X2 - X1) * (X2 - X1) + (Y2 - Y1) * (Y2 - Y1));
    }

    public static final <T extends Point> Point getMidPoint(final T P1, final T P2) {
        return new Point((P1.getX() + P2.getX()) / 2.0, (P1.getY() + P2.getY()) / 2.0);
    }
    public static final double[] getMidPoint(final double X1, final double Y1, final double X2, final double Y2) {
        return new double[] { (X1 + X2) / 2.0, (Y1 + Y2) / 2.0 };
    }

    public static final double[] toHSL(final Color COLOR) {
        return rgbToHSL(COLOR.getRed(), COLOR.getGreen(), COLOR.getBlue());
    }
    public static final double[] rgbToHSL(final double RED, final double GREEN, final double BLUE) {
        //	Minimum and Maximum RGB values are used in the HSL calculations
        double min = Math.min(RED, Math.min(GREEN, BLUE));
        double max = Math.max(RED, Math.max(GREEN, BLUE));

        //  Calculate the Hue
        double hue = 0;

        if (max == min) {
            hue = 0;
        } else if (max == RED) {
            hue = ((60 * (GREEN - BLUE) / (max - min)) + 360) % 360;
        } else if (max == GREEN) {
            hue = (60 * (BLUE - RED) / (max - min)) + 120;
        } else if (max == BLUE) {
            hue = (60 * (RED - GREEN) / (max - min)) + 240;
        }

        //  Calculate the Luminance
        double luminance = (max + min) / 2;

        //  Calculate the Saturation
        double saturation = 0;
        if (Double.compare(max, min)  == 0) {
            saturation = 0;
        } else if (luminance <= .5) {
            saturation = (max - min) / (max + min);
        } else {
            saturation = (max - min) / (2 - max - min);
        }

        return new double[] { hue, saturation, luminance};
    }

    public static final Color hslToRGB(double hue, double saturation, double luminance) {
        return hslToRGB(hue, saturation, luminance, 1);
    }
    public static Color hslToRGB(double hue, double saturation, double luminance, double opacity) {
        saturation = clamp(0, 1, saturation);
        luminance  = clamp(0, 1, luminance);
        opacity    = clamp(0, 1, opacity);

        hue = hue % 360.0;
        hue /= 360;

        double q = luminance < 0.5 ? luminance * (1 + saturation) : (luminance + saturation) - (saturation * luminance);
        double p = 2 * luminance - q;

        double r = clamp(0, 1, hueToRGB(p, q, hue + (1.0/3.0)));
        double g = clamp(0, 1, hueToRGB(p, q, hue));
        double b = clamp(0, 1, hueToRGB(p, q, hue - (1.0/3.0)));

        return Color.color(r, g, b, opacity);
    }
    private static final double hueToRGB(double p, double q, double t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (6 * t < 1) { return p + ((q - p) * 6 * t); }
        if (2 * t < 1) { return q; }
        if (3 * t < 2) { return p + ((q - p) * 6 * ((2.0/3.0) - t)); }
        return p;
    }

    public static Color hsbToRGB(final double hue, final double saturation, final double brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            double h = (hue - Math.floor(hue)) * 6.0;
            double f = h - Math.floor(h);
            double p = brightness * (1.0 - saturation);
            double q = brightness * (1.0 - saturation * f);
            double t = brightness * (1.0 - (saturation * (1.0 - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0 + 0.5);
                    g = (int) (t * 255.0 + 0.5);
                    b = (int) (p * 255.0 + 0.5);
                    break;
                case 1:
                    r = (int) (q * 255.0 + 0.5);
                    g = (int) (brightness * 255.0 + 0.5);
                    b = (int) (p * 255.0 + 0.5);
                    break;
                case 2:
                    r = (int) (p * 255.0 + 0.5);
                    g = (int) (brightness * 255.0 + 0.5);
                    b = (int) (t * 255.0 + 0.5);
                    break;
                case 3:
                    r = (int) (p * 255.0 + 0.5);
                    g = (int) (q * 255.0 + 0.5);
                    b = (int) (brightness * 255.0 + 0.5);
                    break;
                case 4:
                    r = (int) (t * 255.0 + 0.5);
                    g = (int) (p * 255.0 + 0.5);
                    b = (int) (brightness * 255.0 + 0.5);
                    break;
                case 5:
                    r = (int) (brightness * 255.0 + 0.5);
                    g = (int) (p * 255.0 + 0.5);
                    b = (int) (q * 255.0 + 0.5);
                    break;
            }
        }
        return Color.rgb(r, g, b);
    }

    public static double[] ColorToHSB(final Color color) {
        int      r         = (int) (color.getRed() * 255.0);
        int      g         = (int) (color.getGreen() * 255.0);
        int      b         = (int) (color.getBlue() * 255.0);
        double[] hsbValues = new double[3];
        double   hue;
        double   saturation;
        double   brightness;

        int cmax = (r > g) ? r : g;
        if (b > cmax) { cmax = b; }
        int cmin = (r < g) ? r : g;
        if (b < cmin) { cmin = b; }

        brightness = ((double) cmax) / 255.0;
        if (cmax != 0) { saturation = ((float) (cmax - cmin)) / ((double) cmax); } else { saturation = 0; }
        if (saturation == 0) {
            hue = 0;
        } else {
            double redc   = ((double) (cmax - r)) / ((double) (cmax - cmin));
            double greenc = ((double) (cmax - g)) / ((double) (cmax - cmin));
            double bluec  = ((double) (cmax - b)) / ((double) (cmax - cmin));
            if (r == cmax) { hue = bluec - greenc; } else if (g == cmax) { hue = 2.0 + redc - bluec; } else { hue = 4.0 + greenc - redc; }
            hue = hue / 6.0;
            if (hue < 0) { hue = hue + 1.0; }
        }
        hsbValues[0] = hue;
        hsbValues[1] = saturation;
        hsbValues[2] = brightness;
        return hsbValues;
    }

    public static final String colorToRGB(final Color COLOR) {
        String hex      = COLOR.toString().replace("0x", "");
        String hexRed   = hex.substring(0, 2).toUpperCase();
        String hexGreen = hex.substring(2, 4).toUpperCase();
        String hexBlue  = hex.substring(4, 6).toUpperCase();

        String intRed   = Integer.toString(Integer.parseInt(hexRed, 16));
        String intGreen = Integer.toString(Integer.parseInt(hexGreen, 16));
        String intBlue  = Integer.toString(Integer.parseInt(hexBlue, 16));

        return String.join("", "colorToRGB(", intRed, ", ", intGreen, ", ", intBlue, ")");
    }

    public static final String colorToRGBA(final Color COLOR) { return colorToRGBA(COLOR, COLOR.getOpacity()); }
    public static final String colorToRGBA(final Color COLOR, final double ALPHA) {
        String hex      = COLOR.toString().replace("0x", "");
        String hexRed   = hex.substring(0, 2).toUpperCase();
        String hexGreen = hex.substring(2, 4).toUpperCase();
        String hexBlue  = hex.substring(4, 6).toUpperCase();

        String intRed   = Integer.toString(Integer.parseInt(hexRed, 16));
        String intGreen = Integer.toString(Integer.parseInt(hexGreen, 16));
        String intBlue  = Integer.toString(Integer.parseInt(hexBlue, 16));
        String alpha    = String.format(Locale.US, "%.3f", clamp(0, 1, ALPHA));

        return String.join("", "colorToRGBA(", intRed, ", ", intGreen, ", ", intBlue, ",", alpha, ")");
    }

    public static final String colorToWeb(final Color COLOR) { return COLOR.toString().replace("0x", "#").substring(0, 7); }

    public static final List<DataPoint> createSmoothedHull(final List<DataPoint> POINTS, final int SUB_DIVISIONS) {
        List<DataPoint> hullPolygon = createHull(POINTS);
        return subdivideDataPoints(hullPolygon, SUB_DIVISIONS);
    }
    public static final <T extends Point> List<T> createHull(final List<T> POINTS) {
        List<T> convexHull = new ArrayList<>();
        if (POINTS.size() < 3) { return new ArrayList<T>(POINTS); }

        int minDataPoint = -1;
        int maxDataPoint = -1;
        int minX         = Integer.MAX_VALUE;
        int maxX         = Integer.MIN_VALUE;

        for (int i = 0; i < POINTS.size(); i++) {
            if (POINTS.get(i).getX() < minX) {
                minX     = (int) POINTS.get(i).getX();
                minDataPoint = i;
            }
            if (POINTS.get(i).getX() > maxX) {
                maxX     = (int) POINTS.get(i).getX();
                maxDataPoint = i;
            }
        }
        T minPoint = POINTS.get(minDataPoint);
        T maxPoint = POINTS.get(maxDataPoint);
        convexHull.add(minPoint);
        convexHull.add(maxPoint);
        POINTS.remove(minPoint);
        POINTS.remove(maxPoint);

        List<T> leftSet  = new ArrayList<>();
        List<T> rightSet = new ArrayList<>();

        for (int i = 0; i < POINTS.size(); i++) {
            T p = POINTS.get(i);
            if (pointLocation(minPoint, maxPoint, p) == -1) { leftSet.add(p); } else if (pointLocation(minPoint, maxPoint, p) == 1) rightSet.add(p);
        }
        hullSet(minPoint, maxPoint, rightSet, convexHull);
        hullSet(maxPoint, minPoint, leftSet, convexHull);

        return convexHull;
    }
    private static final <T extends Point> double distance(final T P1, final T P2, final T P3) {
        double deltaX = P2.getX() - P1.getX();
        double deltaY = P2.getY() - P1.getY();
        double num = deltaX * (P1.getY() - P3.getY()) - deltaY * (P1.getX() - P3.getX());
        return Math.abs(num);
    }
    private static final <T extends Point> void hullSet(final T P1, final T P2, final List<T> POINTS, final List<T> HULL) {
        int insertPosition = HULL.indexOf(P2);

        if (POINTS.size() == 0) { return; }

        if (POINTS.size() == 1) {
            T point = POINTS.get(0);
            POINTS.remove(point);
            HULL.add(insertPosition, point);
            return;
        }

        int dist              = Integer.MIN_VALUE;
        int furthestDataPoint = -1;
        for (int i = 0; i < POINTS.size(); i++) {
            T point    = POINTS.get(i);
            double distance = distance(P1, P2, point);
            if (distance > dist) {
                dist          = (int) distance;
                furthestDataPoint = i;
            }
        }
        T point = POINTS.get(furthestDataPoint);
        POINTS.remove(furthestDataPoint);
        HULL.add(insertPosition, point);

        // Determine who's to the left of AP
        ArrayList<T> leftSetAP = new ArrayList<>();
        for (int i = 0; i < POINTS.size(); i++) {
            T M = POINTS.get(i);
            if (pointLocation(P1, point, M) == 1) { leftSetAP.add(M); }
        }

        // Determine who's to the left of PB
        ArrayList<T> leftSetPB = new ArrayList<>();
        for (int i = 0; i < POINTS.size(); i++) {
            T M = POINTS.get(i);
            if (pointLocation(point, P2, M) == 1) { leftSetPB.add(M); }
        }
        hullSet(P1, point, leftSetAP, HULL);
        hullSet(point, P2, leftSetPB, HULL);
    }
    private static final <T extends Point> int pointLocation(final T P1, final T P2, final T P3) {
        double cp1 = (P2.getX() - P1.getX()) * (P3.getY() - P1.getY()) - (P2.getY() - P1.getY()) * (P3.getX() - P1.getX());
        return cp1 > 0 ? 1 : Double.compare(cp1, 0) == 0 ? 0 : -1;
    }

    public static final Color interpolateColor(final Color COLOR1, final Color COLOR2, final double FRACTION) {
        return interpolateColor(COLOR1, COLOR2, FRACTION, -1);
    }
    public static final Color getColorWithOpacityAt(final LinearGradient GRADIENT, final double FRACTION, final double TARGET_OPACITY) {
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
        return interpolateColor(lowerStop.getColor(), upperStop.getColor(), interpolationFraction, TARGET_OPACITY);
    }
    public static final Color interpolateColor(final Color COLOR1, final Color COLOR2, final double FRACTION, final double TARGET_OPACITY) {
        double fraction       = clamp(0, 1, FRACTION);
        double targetOpacity  = TARGET_OPACITY < 0 ? TARGET_OPACITY : clamp(0, 1, FRACTION);

        final double RED1     = COLOR1.getRed();
        final double GREEN1   = COLOR1.getGreen();
        final double BLUE1    = COLOR1.getBlue();
        final double OPACITY1 = COLOR1.getOpacity();

        final double RED2     = COLOR2.getRed();
        final double GREEN2   = COLOR2.getGreen();
        final double BLUE2    = COLOR2.getBlue();
        final double OPACITY2 = COLOR2.getOpacity();

        final double DELTA_RED     = RED2 - RED1;
        final double DELTA_GREEN   = GREEN2 - GREEN1;
        final double DELTA_BLUE    = BLUE2 - BLUE1;
        final double DELTA_OPACITY = OPACITY2 - OPACITY1;

        double red     = RED1 + (DELTA_RED * fraction);
        double green   = GREEN1 + (DELTA_GREEN * fraction);
        double blue    = BLUE1 + (DELTA_BLUE * fraction);
        double opacity = targetOpacity < 0 ? OPACITY1 + (DELTA_OPACITY * fraction) : targetOpacity;

        red     = clamp(0, 1, red);
        green   = clamp(0, 1, green);
        blue    = clamp(0, 1, blue);
        opacity = clamp(0, 1, opacity);

        return Color.color(red, green, blue, opacity);
    }

    public static final <T> Predicate<T> not(Predicate<T> predicate) { return predicate.negate(); }

    public static final ZoneOffset getZoneOffset() { return getZoneOffset(ZoneId.systemDefault()); }
    public static final ZoneOffset getZoneOffset(final ZoneId ZONE_ID) { return ZONE_ID.getRules().getOffset(Instant.now()); }

    public static final long toMillis(final LocalDateTime DATE_TIME, final ZoneOffset ZONE_OFFSET) { return toSeconds(DATE_TIME, ZONE_OFFSET) * 1000; }
    public static final long toSeconds(final LocalDateTime DATE_TIME, final ZoneOffset ZONE_OFFSET) { return DATE_TIME.toEpochSecond(ZONE_OFFSET); }

    public static final double toNumericValue(final LocalDateTime DATE) { return toNumericValue(DATE, ZoneId.systemDefault()); }
    public static final double toNumericValue(final LocalDateTime DATE, final ZoneId ZONE_ID) { return Helper.toSeconds(DATE, Helper.getZoneOffset(ZONE_ID)); }

    public static final LocalDateTime toRealValue(final double VALUE) { return secondsToLocalDateTime((long) VALUE); }
    public static final LocalDateTime toRealValue(final double VALUE, final ZoneId ZONE_ID) { return secondsToLocalDateTime((long) VALUE, ZONE_ID); }

    public static final LocalDateTime secondsToLocalDateTime(final long SECONDS) { return LocalDateTime.ofInstant(Instant.ofEpochSecond(SECONDS), ZoneId.systemDefault()); }
    public static final LocalDateTime secondsToLocalDateTime(final long SECONDS, final ZoneId ZONE_ID) { return LocalDateTime.ofInstant(Instant.ofEpochSecond(SECONDS), ZONE_ID); }

    public static final String secondsToHHMMString(final long SECONDS) {
        long[] hhmmss = secondsToHHMMSS(SECONDS);
        return String.format("%02d:%02d:%02d", hhmmss[0], hhmmss[1], hhmmss[2]);
    }
    public static final long[] secondsToHHMMSS(final long SECONDS) {
        long seconds = SECONDS % 60;
        long minutes = (SECONDS / 60) % 60;
        long hours   = (SECONDS / (60 * 60)) % 24;
        return new long[] { hours, minutes, seconds };
    }

    public static final void enableNode(final Node NODE, final boolean ENABLE) {
        NODE.setVisible(ENABLE);
        NODE.setManaged(ENABLE);
    }

    public static final void orderChartItems(final List<ChartItem> ITEMS, final Order ORDER) {
        if (Order.ASCENDING == ORDER) {
            Collections.sort(ITEMS, Comparator.comparingDouble(ChartItem::getValue));
        } else {
            Collections.sort(ITEMS, Comparator.comparingDouble(ChartItem::getValue).reversed());
        }
    }

    public static final void orderXYChartItemsByX(final List<XYChartItem> ITEMS, final Order ORDER) {
        if (Order.ASCENDING == ORDER) {
            Collections.sort(ITEMS, Comparator.comparingDouble(XYChartItem::getX));
        } else {
            Collections.sort(ITEMS, Comparator.comparingDouble(XYChartItem::getX).reversed());
        }
    }

    public static final CtxDimension getTextDimension(final String TEXT, final Font FONT) {
        Text text = new Text(TEXT);
        text.setFont(FONT);
        double textWidth  = text.getBoundsInLocal().getWidth();
        double textHeight = text.getBoundsInLocal().getHeight();
        text = null;
        CtxDimension dim = new CtxDimension(textWidth, textHeight);
        return dim;
    }

    public static final double bearing(final Point P1, final Point P2) {
        return bearing(P1.getX(), P1.getY(), P2.getX(), P2.getY());
    }
    public static final double bearing(final double X1, final double Y1, final double X2, final double Y2) {
        double bearing = Math.toDegrees(Math.atan2(Y2 - Y1, X2 - X1)) + 90;
        if (bearing < 0) { bearing += 360.0; }
        return bearing;
    }

    /**
     * @param START_POINT
     * @param CONTROL_POINT_1
     * @param CONTROL_POINT_2
     * @param END_POINT
     * @param DISTANCE in % (0-1)
     * @return
     */
    public static final Point getCubicBezierXYatT(final Point START_POINT, final Point CONTROL_POINT_1, final Point CONTROL_POINT_2, final Point END_POINT, final double DISTANCE) {
        final double x = cubicN(DISTANCE, START_POINT.getX(), CONTROL_POINT_1.getX(), CONTROL_POINT_2.getX(), END_POINT.getX());
        final double y = cubicN(DISTANCE, START_POINT.getY(), CONTROL_POINT_1.getY(), CONTROL_POINT_2.getY(), END_POINT.getY());
        return new Point(x, y);
    }
    public static final double[] getCubicBezierXYatT(final double START_POINT_X, final double START_POINT_Y,
                                                  final double CONTROL_POINT_1_X, final double CONTROL_POINT_1_Y,
                                                  final double CONTROL_POINT_2_X, final double CONTROL_POINT_2_Y,
                                                  final double END_POINT_X, final double END_POINT_Y, final double DISTANCE) {
        final double x = cubicN(DISTANCE, START_POINT_X, CONTROL_POINT_1_X, CONTROL_POINT_2_X, END_POINT_X);
        final double y = cubicN(DISTANCE, START_POINT_Y, CONTROL_POINT_1_Y, CONTROL_POINT_2_Y, END_POINT_Y);
        return new double[] { x, y };
    }
    private static double cubicN(final double DISTANCE, final double A, final double B, final double C, final double D) {
        final double t2 = DISTANCE * DISTANCE;
        final double t3 = t2 * DISTANCE;
        return A + (-A * 3 + DISTANCE * (3 * A - A * DISTANCE)) * DISTANCE + (3 * B + DISTANCE * (-6 * B + B * 3 * DISTANCE)) * DISTANCE + (C * 3 - C * 3 * DISTANCE) * t2 + D * t3;
    }

    public static final void drawTextWithBackground(final GraphicsContext CTX, final String TEXT, final Font FONT, final Color TEXT_BACKGROUND, final Color TEXT_FILL, final double X, final double Y) {
        CtxDimension dim = getTextDimension(TEXT, FONT);
        double textWidth  = dim.getWidth() * 1.2;
        double textHeight = dim.getHeight();
        CTX.save();
        CTX.setFont(FONT);
        CTX.setTextBaseline(VPos.CENTER);
        CTX.setTextAlign(TextAlignment.CENTER);
        CTX.setFill(TEXT_BACKGROUND);
        CTX.fillRect(X - textWidth * 0.5, Y - textHeight * 0.5, textWidth, textHeight);
        CTX.setFill(TEXT_FILL);
        CTX.fillText(TEXT, X, Y);
        CTX.restore();
    }

    public static final Axis createLeftAxis(final double MIN, final double MAX, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, "", true, AXIS_WIDTH, AXIS_WIDTH, Orientation.VERTICAL, Position.LEFT);
    }
    public static final Axis createLeftAxis(final double MIN, final double MAX, final String TITLE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, TITLE, true, AXIS_WIDTH, AXIS_WIDTH, Orientation.VERTICAL, Position.LEFT);
    }
    public static final Axis createLeftAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, "", AUTO_SCALE, AXIS_WIDTH, AXIS_WIDTH, Orientation.VERTICAL, Position.LEFT);
    }
    public static final Axis createLeftAxis(final double MIN, final double MAX, final String TITLE, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, TITLE, AUTO_SCALE, AXIS_WIDTH, AXIS_WIDTH, Orientation.VERTICAL, Position.LEFT);
    }

    public static final Axis createCenterYAxis(final double MIN, final double MAX, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, "", true, AXIS_WIDTH, AXIS_WIDTH, Orientation.VERTICAL, Position.CENTER);
    }
    public static final Axis createCenterYAxis(final double MIN, final double MAX, final String TITLE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, TITLE, true, AXIS_WIDTH, AXIS_WIDTH, Orientation.VERTICAL, Position.CENTER);
    }
    public static final Axis createCenterYAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, "", AUTO_SCALE, AXIS_WIDTH, AXIS_WIDTH, Orientation.VERTICAL, Position.CENTER);
    }
    public static final Axis createCenterYAxis(final double MIN, final double MAX, final String TITLE, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, TITLE, AUTO_SCALE, AXIS_WIDTH, AXIS_WIDTH, Orientation.VERTICAL, Position.CENTER);
    }

    public static final Axis createRightAxis(final double MIN, final double MAX, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, "", true, AXIS_WIDTH, AXIS_WIDTH, Orientation.VERTICAL, Position.RIGHT);
    }
    public static final Axis createRightAxis(final double MIN, final double MAX, final String TITLE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, TITLE,true, AXIS_WIDTH, AXIS_WIDTH, Orientation.VERTICAL, Position.RIGHT);
    }
    public static final Axis createRightAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, "", AUTO_SCALE, AXIS_WIDTH, AXIS_WIDTH, Orientation.VERTICAL, Position.RIGHT);
    }
    public static final Axis createRightAxis(final double MIN, final double MAX, final String TITLE, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, TITLE, AUTO_SCALE, AXIS_WIDTH, AXIS_WIDTH, Orientation.VERTICAL, Position.RIGHT);
    }

    public static final Axis createTopAxis(final double MIN, final double MAX, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, "", true, AXIS_WIDTH, AXIS_WIDTH, Orientation.HORIZONTAL, Position.TOP);
    }
    public static final Axis createTopAxis(final double MIN, final double MAX, final String TITLE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, TITLE,true, AXIS_WIDTH, AXIS_WIDTH, Orientation.HORIZONTAL, Position.TOP);
    }
    public static final Axis createTopAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, "", AUTO_SCALE, AXIS_WIDTH, AXIS_WIDTH, Orientation.HORIZONTAL, Position.TOP);
    }
    public static final Axis createTopAxis(final double MIN, final double MAX, final String TITLE, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, TITLE, AUTO_SCALE, AXIS_WIDTH, AXIS_WIDTH, Orientation.HORIZONTAL, Position.TOP);
    }

    public static final Axis createCenterXAxis(final double MIN, final double MAX, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, "", true, AXIS_WIDTH, AXIS_WIDTH, Orientation.HORIZONTAL, Position.CENTER);
    }
    public static final Axis createCenterXAxis(final double MIN, final double MAX, final String TITLE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, TITLE,true, AXIS_WIDTH, AXIS_WIDTH, Orientation.HORIZONTAL, Position.CENTER);
    }
    public static final Axis createCenterXAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, "", AUTO_SCALE, AXIS_WIDTH, AXIS_WIDTH, Orientation.HORIZONTAL, Position.CENTER);
    }
    public static final Axis createCenterXAxis(final double MIN, final double MAX, final String TITLE, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, TITLE, AUTO_SCALE, AXIS_WIDTH, AXIS_WIDTH, Orientation.HORIZONTAL, Position.CENTER);
    }

    public static final Axis createBottomAxis(final double MIN, final double MAX, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, "", true, AXIS_WIDTH, AXIS_WIDTH, Orientation.HORIZONTAL, Position.BOTTOM);
    }
    public static final Axis createBottomAxis(final double MIN, final double MAX, final String TITLE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, TITLE, true, AXIS_WIDTH, AXIS_WIDTH, Orientation.HORIZONTAL, Position.BOTTOM);
    }
    public static final Axis createBottomAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, "", AUTO_SCALE, AXIS_WIDTH, AXIS_WIDTH, Orientation.HORIZONTAL, Position.BOTTOM);
    }
    public static final Axis createBottomAxis(final double MIN, final double MAX, final String TITLE, final boolean AUTO_SCALE, final double AXIS_WIDTH) {
        return createAxis(MIN, MAX, TITLE, AUTO_SCALE, AXIS_WIDTH, AXIS_WIDTH, Orientation.HORIZONTAL, Position.BOTTOM);
    }

    public static final Axis createAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH, final Orientation ORIENTATION, final Position POSITION) {
        return createAxis(MIN, MAX, "", AUTO_SCALE, AXIS_WIDTH, AXIS_WIDTH, ORIENTATION, POSITION);
    }
    public static final Axis createAxis(final double MIN, final double MAX, final boolean AUTO_SCALE, final double AXIS_WIDTH, final double ANCHOR, final Orientation ORIENTATION, final Position POSITION) {
        return createAxis(MIN, MAX, "", AUTO_SCALE, AXIS_WIDTH, ANCHOR, ORIENTATION, POSITION);
    }
    public static final Axis createAxis(final double MIN, final double MAX, final String TITLE, final boolean AUTO_SCALE, final double AXIS_WIDTH, final double ANCHOR, final Orientation ORIENTATION, final Position POSITION) {
        Axis axis = AxisBuilder.create(ORIENTATION, POSITION)
                               .minValue(MIN)
                               .maxValue(MAX)
                               .title(TITLE)
                               .autoScale(AUTO_SCALE)
                               .build();

        if (Orientation.HORIZONTAL == ORIENTATION) {
            axis.setPrefHeight(AXIS_WIDTH);
        } else {
            axis.setPrefWidth(AXIS_WIDTH);
        }

        switch(POSITION) {
            case LEFT:
                AnchorPane.setTopAnchor(axis, 0d);
                AnchorPane.setBottomAnchor(axis, ANCHOR);
                AnchorPane.setLeftAnchor(axis, 0d);
                break;
            case CENTER:
                break;
            case RIGHT:
                AnchorPane.setRightAnchor(axis, 0d);
                AnchorPane.setTopAnchor(axis, 0d);
                AnchorPane.setBottomAnchor(axis, ANCHOR);
                break;
            case TOP:
                AnchorPane.setTopAnchor(axis, ANCHOR);
                AnchorPane.setLeftAnchor(axis, ANCHOR);
                AnchorPane.setRightAnchor(axis, ANCHOR);
                break;
            case BOTTOM:
                AnchorPane.setBottomAnchor(axis, 0d);
                AnchorPane.setLeftAnchor(axis, ANCHOR);
                AnchorPane.setRightAnchor(axis, ANCHOR);
                break;
        }
        return axis;
    }


    public static final ColorInput createColorMask(final Image sourceImage, final Color color) { return new ColorInput(0, 0, sourceImage.getWidth(), sourceImage.getHeight(), color); }
    public static final Blend createColorBlend(final Image sourceImage, final Color color) {
        final ColorInput mask  = createColorMask(sourceImage, color);
        final Blend      blend = new Blend(BlendMode.MULTIPLY);
        blend.setTopInput(mask);
        return blend;
    }
    public static final WritableImage getRedChannel(final Image sourceImage) { return getColorChannel(sourceImage, Color.RED);  }
    public static final WritableImage getGreenChannel(final Image sourceImage) { return getColorChannel(sourceImage, Color.LIME); }
    public static final WritableImage getBlueChannel(final Image sourceImage) { return getColorChannel(sourceImage, Color.BLUE); }
    private static final WritableImage getColorChannel(final Image sourceImage, final Color color) {
        final Node  imageView = new ImageView(sourceImage);
        final Blend blend     = createColorBlend(sourceImage, color);
        imageView.setEffect(blend);

        final SnapshotParameters params = new SnapshotParameters();
        final WritableImage      result = imageView.snapshot(params, null);
        return result;
    }


    public static final String readTextFile(final String filename) {
        if (null == filename || !new File(filename).exists()) { throw new IllegalArgumentException("File: " + filename + " not found or null"); }
        try {
            Path   fileName = Path.of(filename);
            return Files.readString(fileName);
        } catch (IOException e) {
            return "";
        }
    }
}
