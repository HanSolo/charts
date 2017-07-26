package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.XYData;
import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.util.Arrays;
import java.util.List;


/**
 * User: hansolo
 * Date: 26.07.17
 * Time: 16:52
 */
@DefaultProperty("children")
public class XYChart<T extends XYData> extends Region {
    private static final double     PREFERRED_WIDTH  = 400;
    private static final double     PREFERRED_HEIGHT = 250;
    private static final double     MINIMUM_WIDTH    = 50;
    private static final double     MINIMUM_HEIGHT   = 50;
    private static final double     MAXIMUM_WIDTH    = 4096;
    private static final double     MAXIMUM_HEIGHT   = 4096;
    private static final Double     AXIS_WIDTH       = 25d;
    private              double     width;
    private              double     height;
    private              XYPane<T>  xyPane;
    private              List<Axis> axis;
    private              Axis       yAxisL;
    private              Axis       yAxisR;
    private              Axis       xAxisT;
    private              Axis       xAxisB;
    private              boolean    hasLeftYAxis;
    private              boolean    hasRightYAxis;
    private              boolean    hasTopXAxis;
    private              boolean    hasBottomXAxis;
    private              AnchorPane pane;


    // ******************** Constructors **************************************
    public XYChart(final XYPane<T> XY_PANE, final Axis... AXIS) {
        if (null == XY_PANE) { throw new IllegalArgumentException("XYPane has not to be null"); }
        xyPane = XY_PANE;
        axis   = Arrays.asList(AXIS);
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

        checkForAxis();

        adjustChartRange();

        adjustAxisAnchors();

        pane = new AnchorPane(xyPane);
        pane.getChildren().addAll(axis);

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

    public XYPane<T> getXYPane() { return xyPane; }

    private void checkForAxis() {
        axis.forEach(axis -> {
            Position position = axis.getPosition();
            switch (axis.getOrientation()) {
                case HORIZONTAL:
                    switch(position) {
                        case TOP:
                            hasTopXAxis    = true;
                            xAxisT         = axis;
                            break;
                        case BOTTOM:
                            hasBottomXAxis = true;
                            xAxisB         = axis;
                            break;
                        default:
                            hasTopXAxis    = false;
                            hasBottomXAxis = false;
                            break;
                    }
                    break;
                case VERTICAL:
                    switch(position) {
                        case LEFT:
                            hasLeftYAxis  = true;
                            yAxisL        = axis;
                            break;
                        case RIGHT:
                            hasRightYAxis = true;
                            yAxisR        = axis;
                            break;
                        default:
                            hasLeftYAxis  = false;
                            hasRightYAxis = false;
                            break;
                    }
                    break;
            }
        });
    }

    private void adjustChartRange() {
        if (hasBottomXAxis) {
            xyPane.setRangeX(xAxisB.getRange());
        } else if (hasTopXAxis) {
            xyPane.setRangeX(xAxisT.getRange());
        }

        if (hasLeftYAxis) {
            xyPane.setRangeY(yAxisL.getRange());
        } else if (hasRightYAxis) {
            xyPane.setRangeY(yAxisR.getRange());
        }
    }

    private void adjustAxisAnchors() {
        axis.forEach(axis -> {
            if (Orientation.HORIZONTAL == axis.getOrientation()) {
                AnchorPane.setLeftAnchor(axis, hasLeftYAxis ? AXIS_WIDTH : 0d);
                AnchorPane.setRightAnchor(axis, hasRightYAxis ? AXIS_WIDTH : 0d);

                AnchorPane.setLeftAnchor(xyPane, hasLeftYAxis ? AXIS_WIDTH : 0d);
                AnchorPane.setRightAnchor(xyPane, hasRightYAxis ? AXIS_WIDTH : 0d);
            } else {
                AnchorPane.setTopAnchor(axis, hasTopXAxis ? AXIS_WIDTH : 0d);
                AnchorPane.setBottomAnchor(axis, hasBottomXAxis ? AXIS_WIDTH : 0d);

                AnchorPane.setTopAnchor(xyPane, hasTopXAxis ? AXIS_WIDTH : 0d);
                AnchorPane.setBottomAnchor(xyPane, hasBottomXAxis ? AXIS_WIDTH : 0d);
            }
        });
    }

    // ******************** Resizing ******************************************
    private void resize() {
        width = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);
        }
    }
}
