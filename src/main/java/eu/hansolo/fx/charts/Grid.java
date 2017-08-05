package eu.hansolo.fx.charts;

import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.math.BigDecimal;


/**
 * User: hansolo
 * Date: 02.08.17
 * Time: 17:06
 */
@DefaultProperty("children")
public class Grid extends Region {
    private static final double    PREFERRED_WIDTH       = 250;
    private static final double    PREFERRED_HEIGHT      = 250;
    private static final double    MINIMUM_WIDTH         = 50;
    private static final double    MINIMUM_HEIGHT        = 50;
    private static final double    MAXIMUM_WIDTH         = 4096;
    private static final double    MAXIMUM_HEIGHT        = 4096;
    private static final double    MIN_MAJOR_LINE_WIDTH  = 1;
    private static final double    MIN_MEDIUM_LINE_WIDTH = 0.75;
    private static final double    MIN_MINOR_LINE_WIDTH  = 0.5;
    private        double          size;
    private        double          width;
    private        double          height;
    private        Axis            xAxis;
    private        Axis            yAxis;
    private        double          _gridOpacity;
    private        DoubleProperty  gridOpacity;
    private        Canvas          canvas;
    private        GraphicsContext ctx;
    private        Pane            pane;


    // ******************** Constructors **************************************
    public Grid(final Axis X_AXIS, final Axis Y_AXIS) {
        xAxis        = X_AXIS;
        yAxis        = Y_AXIS;
        _gridOpacity = 0.25;
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
            Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        pane   = new Pane(canvas);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        // add listeners to your propertes like
        //value.addListener(o -> handleControlPropertyChanged("VALUE"));
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public double getGridOpacity() { return null == gridOpacity ? _gridOpacity : gridOpacity.get(); }
    public void setGridOpacity(final double OPACITY) {
        if (null == gridOpacity) {
            _gridOpacity = Helper.clamp(0, 1, OPACITY);
            drawGrid();
        } else {
            gridOpacity.set(OPACITY);
        }
    }
    public DoubleProperty gridOpacityProperty() {
        if (null == gridOpacity) {
            gridOpacity = new DoublePropertyBase(_gridOpacity) {
                @Override protected void invalidated() { drawGrid(); }
                @Override public Object getBean() { return Grid.this; }
                @Override public String getName() { return "gridOpacity"; }
            };
        }
        return gridOpacity;
    }

    private void drawGrid() {
        ctx.clearRect(0, 0, width, height);

        double majorLineWidth  = 25 * 0.007 < MIN_MAJOR_LINE_WIDTH ? MIN_MAJOR_LINE_WIDTH : 25 * 0.007;
        double mediumLineWidth = 25 * 0.006 < MIN_MEDIUM_LINE_WIDTH ? MIN_MEDIUM_LINE_WIDTH : 25 * 0.005;
        double minorLineWidth  = 25 * 0.005 < MIN_MINOR_LINE_WIDTH ? MIN_MINOR_LINE_WIDTH : 25 * 0.003;

        Color minorHGridColor  = Helper.getColorWithOpacity(yAxis.getMinorTickMarkColor(), getGridOpacity());
        Color mediumHGridColor = Helper.getColorWithOpacity(yAxis.getMediumTickMarkColor(), getGridOpacity());
        Color majorHGridColor  = Helper.getColorWithOpacity(yAxis.getMajorTickMarkColor(), getGridOpacity());

        Color minorVGridColor  = Helper.getColorWithOpacity(xAxis.getMinorTickMarkColor(), getGridOpacity());
        Color mediumVGridColor = Helper.getColorWithOpacity(xAxis.getMediumTickMarkColor(), getGridOpacity());
        Color majorVGridColor  = Helper.getColorWithOpacity(xAxis.getMajorTickMarkColor(), getGridOpacity());

        double minX            = xAxis.getMinValue();
        double maxX            = xAxis.getMaxValue();
        double minorTickSpaceX = xAxis.getMinorTickSpace();
        double majorTickSpaceX = xAxis.getMajorTickSpace();
        double rangeX          = xAxis.getRange();
        double stepSizeX       = Math.abs(width / rangeX);
        double zeroPositionX   = xAxis.getZeroPosition();

        double minY            = yAxis.getMinValue();
        double maxY            = yAxis.getMaxValue();
        double minorTickSpaceY = yAxis.getMinorTickSpace();
        double majorTickSpaceY = yAxis.getMajorTickSpace();
        double rangeY          = yAxis.getRange();
        double stepSizeY       = Math.abs(height / rangeY);
        double zeroPositionY   = yAxis.getZeroPosition();

        BigDecimal minorTickSpaceBD = BigDecimal.valueOf(minorTickSpaceX);
        BigDecimal majorTickSpaceBD = BigDecimal.valueOf(majorTickSpaceX);
        BigDecimal mediumCheck2     = BigDecimal.valueOf(2 * minorTickSpaceX);
        BigDecimal mediumCheck5     = BigDecimal.valueOf(5 * minorTickSpaceX);

        // Main Loop for grid lines
        boolean    isZero;
        double     tmpStepSize = minorTickSpaceX;
        BigDecimal counterBD   = BigDecimal.valueOf(minX);
        double     counter     = minX;
        BigDecimal tmpStepBD   = new BigDecimal(tmpStepSize);
        tmpStepBD              = tmpStepBD.setScale(3, BigDecimal.ROUND_HALF_UP);
        double     tmpStep     = tmpStepBD.doubleValue();
        for (double i = 0 ; Double.compare(-rangeX - tmpStep, i) <= 0 ; i -= tmpStep) {
            double startPointX   = width + i * stepSizeX;
            double startPointY   = 0;
            double endPointX     = startPointX;
            double endPointY     = height;

            if (Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(majorTickSpaceBD).doubleValue(), 0.0) == 0) {
                // Draw major tick grid line
                isZero = Double.compare(0.0, counter) == 0;

                if (isZero) {  }

                if (xAxis.getMajorTickMarksVisible()) {
                    ctx.setStroke(majorVGridColor);
                    ctx.setLineWidth(majorLineWidth);
                    ctx.strokeLine(startPointX, startPointY, endPointX, endPointY);
                } else if (xAxis.getMinorTickMarksVisible()) {
                    ctx.setStroke(minorVGridColor);
                    ctx.setLineWidth(minorLineWidth);
                    ctx.strokeLine(startPointX, startPointY, endPointX, endPointY);
                }
            } else if (xAxis.getMediumTickMarksVisible() &&
                       Double.compare(minorTickSpaceBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(mediumCheck2).doubleValue(), 0.0) != 0.0 &&
                       Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(mediumCheck5).doubleValue(), 0.0) == 0.0) {
                // Draw medium tick grid line
                ctx.setStroke(mediumVGridColor);
                ctx.setLineWidth(mediumLineWidth);
                ctx.strokeLine(startPointX, startPointY, endPointX, endPointY);
            } else if (xAxis.getMinorTickMarksVisible() && Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(minorTickSpaceBD).doubleValue(), 0.0) == 0) {
                // Draw minor tick grid line
                ctx.setStroke(minorVGridColor);
                ctx.setLineWidth(minorLineWidth);
                ctx.strokeLine(startPointX, startPointY, endPointX, endPointY);
            }

            counterBD = counterBD.add(minorTickSpaceBD);
            counter   = counterBD.doubleValue();
            if (counter > maxX) break;
        }

        minorTickSpaceBD = BigDecimal.valueOf(minorTickSpaceY);
        majorTickSpaceBD = BigDecimal.valueOf(majorTickSpaceY);
        mediumCheck2     = BigDecimal.valueOf(2 * minorTickSpaceY);
        mediumCheck5     = BigDecimal.valueOf(5 * minorTickSpaceY);

        tmpStepSize = minorTickSpaceY;
        counterBD   = BigDecimal.valueOf(minY);
        counter     = minY;
        tmpStepBD   = new BigDecimal(tmpStepSize);
        tmpStepBD   = tmpStepBD.setScale(3, BigDecimal.ROUND_HALF_UP);
        tmpStep     = tmpStepBD.doubleValue();
        for (double i = 0 ; Double.compare(-rangeY - tmpStep, i) <= 0 ; i -= tmpStep) {
            double startPointX   = 0;
            double startPointY   = height + i * stepSizeY;
            double endPointX     = width;
            double endPointY     = startPointY;

            if (Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(majorTickSpaceBD).doubleValue(), 0.0) == 0) {
                // Draw major tick grid line
                isZero = Double.compare(0.0, counter) == 0;

                if (isZero) {  }

                if (yAxis.getMajorTickMarksVisible()) {
                    ctx.setStroke(majorHGridColor);
                    ctx.setLineWidth(majorLineWidth);
                    ctx.strokeLine(startPointX, startPointY, endPointX, endPointY);
                } else if (yAxis.getMinorTickMarksVisible()) {
                    ctx.setStroke(minorHGridColor);
                    ctx.setLineWidth(minorLineWidth);
                    ctx.strokeLine(startPointX, startPointY, endPointX, endPointY);
                }
            } else if (yAxis.getMediumTickMarksVisible() &&
                       Double.compare(minorTickSpaceBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(mediumCheck2).doubleValue(), 0.0) != 0.0 &&
                       Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(mediumCheck5).doubleValue(), 0.0) == 0.0) {
                // Draw medium tick grid line
                ctx.setStroke(mediumHGridColor);
                ctx.setLineWidth(mediumLineWidth);
                ctx.strokeLine(startPointX, startPointY, endPointX, endPointY);
            } else if (yAxis.getMinorTickMarksVisible() && Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(minorTickSpaceBD).doubleValue(), 0.0) == 0) {
                // Draw minor tick grid line
                ctx.setStroke(minorHGridColor);
                ctx.setLineWidth(minorLineWidth);
                ctx.strokeLine(startPointX, startPointY, endPointX, endPointY);
            }

            counterBD = counterBD.add(minorTickSpaceBD);
            counter   = counterBD.doubleValue();
            if (counter > maxY) break;
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            canvas.setWidth(width);
            canvas.setHeight(height);

            drawGrid();
        }
    }
}
