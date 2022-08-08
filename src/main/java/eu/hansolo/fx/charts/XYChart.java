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

package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.XYItem;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * User: hansolo
 * Date: 26.07.17
 * Time: 16:52
 */
@DefaultProperty("children")
public class XYChart<T extends XYItem> extends Region {
    private static final double                    PREFERRED_WIDTH  = 400;
    private static final double                    PREFERRED_HEIGHT = 250;
    private static final double                    MINIMUM_WIDTH    = 50;
    private static final double                    MINIMUM_HEIGHT   = 50;
    private static final double                    MAXIMUM_WIDTH    = 4096;
    private static final double                    MAXIMUM_HEIGHT   = 4096;
    private              double                    width;
    private              double                    height;
    private              ObservableList<XYPane<T>> xyPanes;
    private              List<Axis>                axis;
    private              Axis                      yAxisL;
    private              Axis                      yAxisC;
    private              Axis                      yAxisR;
    private              Axis                      xAxisT;
    private              Axis                      xAxisC;
    private              Axis                      xAxisB;
    private              double                    topAxisHeight;
    private              double                    rightAxisWidth;
    private              double                    bottomAxisHeight;
    private              double                    leftAxisWidth;
    private              Grid                      grid;
    private              boolean                   hasLeftYAxis;
    private              boolean                   hasCenterYAxis;
    private              boolean                   hasRightYAxis;
    private              boolean                   hasTopXAxis;
    private              boolean                   hasCenterXAxis;
    private              boolean                   hasBottomXAxis;
    private              String                    _title;
    private              StringProperty            title;
    private              String                    _subTitle;
    private              StringProperty            subTitle;
    private              AnchorPane                pane;
    private              BooleanBinding            showing;


    // ******************** Constructors **************************************
    public XYChart(final XYPane<T> XY_PANE, final Axis... AXIS) {
        this(List.of(XY_PANE), null, AXIS);
    }
    public XYChart(final XYPane<T> XY_PANE, final Grid GRID, final Axis... AXIS) {
        this(List.of(XY_PANE), GRID, AXIS);
    }
    public XYChart(final List<XYPane<T>> XY_PANES, final Axis... AXIS) {
        this(XY_PANES, null, AXIS);
    }
    public XYChart(final List<XYPane<T>> XY_PANES, final Grid GRID, final Axis... AXIS) {
        if (null == XY_PANES) { throw new IllegalArgumentException("XYPanes cannot be null"); }
        long noOfPolarCharts = XY_PANES.stream().filter(xyPane -> xyPane.containsPolarChart()).count();
        if (noOfPolarCharts > 0) { throw new IllegalArgumentException("XYPane contains Polar chart type"); }
        xyPanes = FXCollections.observableList(new LinkedList<>(XY_PANES));
        axis   = Arrays.asList(AXIS);
        grid   = GRID;
        width  = PREFERRED_WIDTH;
        height = PREFERRED_HEIGHT;

        checkReferenceZero();
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

        if (xyPanes.size() > 1) { xyPanes.forEach(xyPane -> xyPane.setChartBackground(Color.TRANSPARENT)); }

        adjustChartRange();

        adjustAxisAnchors();

        pane = new AnchorPane();
        xyPanes.forEach(xyPane -> pane.getChildren().add(xyPane));

        pane.getChildren().addAll(axis);
        setGrid(grid);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        xyPanes.addListener((ListChangeListener<XYPane<T>>) c -> {
            if (xyPanes.size() > 1) { xyPanes.forEach(xyPane -> xyPane.setChartBackground(Color.TRANSPARENT)); }
            checkReferenceZero();
            refresh();
        });
        if (null != getScene()) {
            setupBinding();
        } else {
            sceneProperty().addListener((o1, ov1, nv1) -> {
                if (null == nv1) { return; }
                if (null != getScene().getWindow()) {
                    setupBinding();
                } else {
                    sceneProperty().get().windowProperty().addListener((o2, ov2, nv2) -> {
                        if (null == nv2) { return; }
                        setupBinding();
                    });
                }
            });
        }
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public void dispose() {
        xyPanes.forEach(xyPane -> xyPane.dispose());
    }

    public String getTitle() { return null == title ? _title : title.get(); }
    public void setTitle(final String TITLE) {
        if (null == title) {
            _title = TITLE;
            xyPanes.forEach(xyPane -> xyPane.redraw());
        } else {
            title.set(TITLE);
        }
    }
    public StringProperty titleProperty() {
        if (null == title) {
            title = new StringPropertyBase(_title) {
                @Override protected void invalidated() { xyPanes.forEach(xyPane -> xyPane.redraw()); }
                @Override public Object getBean() { return XYChart.this; }
                @Override public String getName() { return "title"; }
            };
            _title = null;
        }
        return title;
    }

    public String getSubTitle() { return null == subTitle ? _subTitle : subTitle.get(); }
    public void setSubTitle(final String SUB_TITLE) {
        if (null == subTitle) {
            _subTitle = SUB_TITLE;
            xyPanes.forEach(xyPane -> xyPane.redraw());
        } else {
            subTitle.set(SUB_TITLE);
        }
    }
    public StringProperty subTitleProperty() {
        if (null == subTitle) {
            subTitle = new StringPropertyBase(_subTitle) {
                @Override protected void invalidated() { xyPanes.forEach(xyPane -> xyPane.redraw()); }
                @Override public Object getBean() { return XYChart.this; }
                @Override public String getName() { return "subTitle"; }
            };
            _subTitle = null;
        }
        return subTitle;
    }

    public boolean isReferenceZero() {
        if (xyPanes.size() > 0) {
            return xyPanes.get(0).isReferenceZero();
        } else {
            return true;
        }
    }
    public void setReferenceZero(final boolean IS_ZERO) {
        xyPanes.forEach(xyPane -> xyPane.setReferenceZero(IS_ZERO));
    }

    public void setGrid(final Grid GRID) {
        if (null == GRID) return;
        if (null != grid) { pane.getChildren().remove(grid); }
        grid = GRID;
        pane.getChildren().add(0, grid);
        adjustGridAnchors();
    }

    public XYPane<T> getXYPane() {
        return xyPanes.size() > 0 ? xyPanes.get(0) : null;
    }

    public List<XYPane<T>> getXYPanes() { return xyPanes; }

    public void addXYPane(final XYPane<T> xyPane) {
        xyPanes.add(xyPane);
    }
    public void removeXYPane(final XYPane<T> xyPane) {
        xyPanes.remove(xyPane);
    }

    public void refresh() { xyPanes.forEach(xyPane -> xyPane.redraw()); }

    private void checkForAxis() {
        axis.forEach(axis -> {
            Position position = axis.getPosition();
            switch (axis.getOrientation()) {
                case HORIZONTAL:
                    switch(position) {
                        case TOP:
                            hasTopXAxis    = true;
                            topAxisHeight  = axis.getPrefHeight();
                            xAxisT         = axis;
                            break;
                        case CENTER:
                            hasCenterXAxis = true;
                            xAxisC         = axis;
                            break;
                        case BOTTOM:
                            hasBottomXAxis   = true;
                            bottomAxisHeight = axis.getPrefHeight();
                            xAxisB           = axis;
                            break;
                        default:
                            hasTopXAxis    = false;
                            hasCenterXAxis = false;
                            hasBottomXAxis = false;
                            break;
                    }
                    break;
                case VERTICAL:
                    switch(position) {
                        case LEFT:
                            hasLeftYAxis   = true;
                            leftAxisWidth  = axis.getPrefWidth();
                            yAxisL         = axis;
                            break;
                        case CENTER:
                            hasCenterYAxis = true;
                            yAxisC         = axis;
                            break;
                        case RIGHT:
                            hasRightYAxis  = true;
                            rightAxisWidth = axis.getPrefWidth();
                            yAxisR         = axis;
                            break;
                        default:
                            hasLeftYAxis   = false;
                            hasCenterYAxis = false;
                            hasRightYAxis  = false;
                            break;
                    }
                    break;
            }
        });
    }

    private void adjustChartRange() {
        xyPanes.forEach(xyPane -> {
            // TFE, 2020331: bind properties to keep track of changes to axes
            if (hasBottomXAxis) {
                xyPane.lowerBoundXProperty().bind(xAxisB.minValueProperty());
                xyPane.upperBoundXProperty().bind(xAxisB.maxValueProperty());
            } else if (hasTopXAxis) {
                xyPane.lowerBoundXProperty().bind(xAxisT.minValueProperty());
                xyPane.upperBoundXProperty().bind(xAxisT.maxValueProperty());
            } else if (hasCenterXAxis) {
                xyPane.lowerBoundXProperty().bind(xAxisC.minValueProperty());
                xyPane.upperBoundXProperty().bind(xAxisC.maxValueProperty());
            }

            if (hasLeftYAxis) {
                xyPane.lowerBoundYProperty().bind(yAxisL.minValueProperty());
                xyPane.upperBoundYProperty().bind(yAxisL.maxValueProperty());
            } else if (hasRightYAxis) {
                xyPane.lowerBoundYProperty().bind(yAxisR.minValueProperty());
                xyPane.upperBoundYProperty().bind(yAxisR.maxValueProperty());
            } else if (hasCenterYAxis) {
                xyPane.lowerBoundYProperty().bind(yAxisC.minValueProperty());
                xyPane.upperBoundYProperty().bind(yAxisC.maxValueProperty());
            }
        });
    }

    private void adjustAxisAnchors() {
        xyPanes.forEach(xyPane -> {
            axis.forEach(axis -> {
                if (Orientation.HORIZONTAL == axis.getOrientation()) {
                    AnchorPane.setLeftAnchor(axis, hasLeftYAxis ? leftAxisWidth : 0d);
                    AnchorPane.setRightAnchor(axis, hasRightYAxis ? rightAxisWidth : 0d);

                    AnchorPane.setLeftAnchor(xyPane, hasLeftYAxis ? leftAxisWidth : 0d);
                    AnchorPane.setRightAnchor(xyPane, hasRightYAxis ? rightAxisWidth : 0d);
                } else {
                    AnchorPane.setTopAnchor(axis, hasTopXAxis ? topAxisHeight : 0d);
                    AnchorPane.setBottomAnchor(axis, hasBottomXAxis ? bottomAxisHeight : 0d);

                    AnchorPane.setTopAnchor(xyPane, hasTopXAxis ? topAxisHeight : 0d);
                    AnchorPane.setBottomAnchor(xyPane, hasBottomXAxis ? bottomAxisHeight : 0d);
                }
            });
        });
    }

    private void adjustCenterAxisAnchors() {
        if (hasCenterYAxis) {
            if (hasBottomXAxis) {
                if (hasLeftYAxis) {
                    AnchorPane.setLeftAnchor(yAxisC, xAxisB.getZeroPosition() + yAxisL.getWidth());
                } else if (hasRightYAxis) {
                    AnchorPane.setLeftAnchor(yAxisC, xAxisB.getZeroPosition());
                } else if (hasCenterXAxis) {
                    AnchorPane.setLeftAnchor(yAxisC, xAxisC.getZeroPosition());
                }
            } else {
                if (hasLeftYAxis) {
                    AnchorPane.setLeftAnchor(yAxisC, xAxisT.getZeroPosition() + yAxisL.getWidth());
                } else if (hasRightYAxis) {
                    AnchorPane.setLeftAnchor(yAxisC, xAxisT.getZeroPosition());
                }  else if (hasCenterXAxis) {
                    AnchorPane.setLeftAnchor(yAxisC, xAxisC.getZeroPosition());
                }
            }
        }
        if (hasCenterXAxis) {
            if (hasLeftYAxis) {
                if (hasTopXAxis) {
                    AnchorPane.setTopAnchor(xAxisC, yAxisL.getZeroPosition() + xAxisT.getHeight());
                } else if (hasBottomXAxis) {
                    AnchorPane.setTopAnchor(xAxisC, yAxisL.getZeroPosition());
                } else if (hasCenterYAxis) {
                    AnchorPane.setTopAnchor(xAxisC, yAxisC.getZeroPosition());
                }
            } else {
                if (hasTopXAxis) {
                    AnchorPane.setTopAnchor(xAxisC, yAxisR.getZeroPosition() + xAxisT.getHeight());
                } else if (hasBottomXAxis) {
                    AnchorPane.setTopAnchor(xAxisC, yAxisR.getZeroPosition());
                } else if (hasCenterYAxis) {
                    AnchorPane.setTopAnchor(xAxisC, yAxisC.getZeroPosition());
                }
            }
        }
    }

    private void adjustGridAnchors() {
        if (null == grid) return;
        AnchorPane.setLeftAnchor(grid, hasLeftYAxis ? leftAxisWidth : 0d);
        AnchorPane.setRightAnchor(grid, hasRightYAxis ? rightAxisWidth : 0d);
        AnchorPane.setTopAnchor(grid, hasTopXAxis ? topAxisHeight : 0d);
        AnchorPane.setBottomAnchor(grid, hasBottomXAxis ? bottomAxisHeight : 0d);
    }

    private void setupBinding() {
        showing = Bindings.selectBoolean(sceneProperty(), "window", "showing");
        showing.addListener((o, ov, nv) -> { if (nv) { adjustCenterAxisAnchors(); } });
    }

    private void checkReferenceZero() {
        boolean isReferenceZero = true;
        if (xyPanes.size() > 0) {
            isReferenceZero = xyPanes.get(0).isReferenceZero();
        }
        setReferenceZero(isReferenceZero);
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            adjustCenterAxisAnchors();
        }
    }
}
