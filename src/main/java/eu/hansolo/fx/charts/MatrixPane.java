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

import eu.hansolo.fx.charts.PixelMatrix.PixelShape;
import eu.hansolo.fx.charts.data.MatrixItem;
import eu.hansolo.fx.charts.series.MatrixItemSeries;
import eu.hansolo.fx.charts.tools.ColorMapping;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;


public class MatrixPane<T extends MatrixItem> extends Region implements ChartArea {
    private static final double                PREFERRED_WIDTH  = 250;
    private static final double                PREFERRED_HEIGHT = 250;
    private static final double                MINIMUM_WIDTH    = 0;
    private static final double                MINIMUM_HEIGHT   = 0;
    private static final double                MAXIMUM_WIDTH    = 4096;
    private static final double                MAXIMUM_HEIGHT   = 4096;
    private static       double                aspectRatio;
    private              boolean               keepAspect;
    private              double                size;
    private              double                width;
    private              double                height;
    private              Pane                  pane;
    private              Paint                 _chartBackground;
    private              ObjectProperty<Paint> chartBackground;
    private              MatrixItemSeries<T>   series;
    private              PixelMatrix           matrix;
    private              LinearGradient        matrixGradient;
    private              double                minZ;
    private              double                maxZ;
    private              double                rangeZ;
    private              double                scaleX;
    private              double                scaleY;
    private              double                scaleZ;
    private              double                _lowerBoundX;
    private              DoubleProperty        lowerBoundX;
    private              double                _upperBoundX;
    private              DoubleProperty        upperBoundX;
    private              double                _lowerBoundY;
    private              DoubleProperty        lowerBoundY;
    private              double                _upperBoundY;
    private              DoubleProperty        upperBoundY;
    private              double                _lowerBoundZ;
    private              DoubleProperty        lowerBoundZ;
    private              double                _upperBoundZ;
    private              DoubleProperty        upperBoundZ;


    // ******************** Constructors **************************************
    public MatrixPane(final MatrixItemSeries<T> SERIES) {
        this(Color.WHITE, SERIES);
    }
    public MatrixPane(final Paint BACKGROUND, final MatrixItemSeries<T> SERIES) {
        getStylesheets().add(XYPane.class.getResource("chart.css").toExternalForm());
        aspectRatio      = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        keepAspect       = false;
        _chartBackground = BACKGROUND;
        series           = SERIES;
        matrixGradient   = ColorMapping.BLUE_CYAN_GREEN_YELLOW_RED.getGradient();
        scaleX           = 1;
        scaleY           = 1;
        scaleZ           = 1;
        _lowerBoundX     = 0;
        _upperBoundX     = 100;
        _lowerBoundY     = 0;
        _upperBoundY     = 100;
        _lowerBoundZ     = 0;
        _upperBoundZ     = 100;

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

        getStyleClass().setAll("chart", "matrix-chart");

        matrix = PixelMatrixBuilder.create()
                                   .prefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT)
                                   .pixelShape(PixelShape.SQUARE)
                                   .useSpacer(true)
                                   .squarePixels(false)
                                   .pixelOnColor(Color.BLACK)
                                   .pixelOffColor(Color.TRANSPARENT)
                                   .build();

        pane = new Pane(matrix);
        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());

        series.setOnSeriesEvent(seriesEvent -> redraw());
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT)  { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH)  { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT)  { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH)  { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public Paint getChartBackground() { return null == chartBackground ? _chartBackground : chartBackground.get(); }
    public void setChartBackground(final Paint PAINT) {
        if (null == chartBackground) {
            _chartBackground = PAINT;
            redraw();
        } else {
            chartBackground.set(PAINT);
        }
    }
    public ObjectProperty<Paint> chartBackgroundProperty() {
        if (null == chartBackground) {
            chartBackground = new ObjectPropertyBase<Paint>(_chartBackground) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return MatrixPane.this; }
                @Override public String getName() { return "chartBackground"; }
            };
            _chartBackground = null;
        }
        return chartBackground;
    }

    public double getLowerBoundX() { return null == lowerBoundX ? _lowerBoundX : lowerBoundX.get(); }
    public void setLowerBoundX(final double VALUE) {
        if (null == lowerBoundX) {
            _lowerBoundX = VALUE;
            redraw();
        } else {
            lowerBoundX.set(VALUE);
        }
    }
    public DoubleProperty lowerBoundXProperty() {
        if (null == lowerBoundX) {
            lowerBoundX = new DoublePropertyBase(_lowerBoundX) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return MatrixPane.this; }
                @Override public String getName() { return "lowerBoundX"; }
            };
        }
        return lowerBoundX;
    }

    public double getUpperBoundX() { return null == upperBoundX ? _upperBoundX : upperBoundX.get(); }
    public void setUpperBoundX(final double VALUE) {
        if (null == upperBoundX) {
            _upperBoundX = VALUE;
            redraw();
        } else {
            upperBoundX.set(VALUE);
        }
    }
    public DoubleProperty upperBoundXProperty() {
        if (null == upperBoundX) {
            upperBoundX = new DoublePropertyBase(_upperBoundX) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return MatrixPane.this; }
                @Override public String getName() { return "upperBoundX"; }
            };
        }
        return upperBoundX;
    }

    public double getLowerBoundY() { return null == lowerBoundY ? _lowerBoundY : lowerBoundY.get(); }
    public void setLowerBoundY(final double VALUE) {
        if (null == lowerBoundY) {
            _lowerBoundY = VALUE;
            redraw();
        } else {
            lowerBoundY.set(VALUE);
        }
    }
    public DoubleProperty lowerBoundYProperty() {
        if (null == lowerBoundY) {
            lowerBoundY = new DoublePropertyBase(_lowerBoundY) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return MatrixPane.this; }
                @Override public String getName() { return "lowerBoundY"; }
            };
        }
        return lowerBoundY;
    }

    public double getUpperBoundY() { return null == upperBoundY ? _upperBoundY : upperBoundY.get(); }
    public void setUpperBoundY(final double VALUE) {
        if (null == upperBoundY) {
            _upperBoundY = VALUE;
            redraw();
        } else {
            upperBoundY.set(VALUE);
        }
    }
    public DoubleProperty upperBoundYProperty() {
        if (null == upperBoundY) {
            upperBoundY = new DoublePropertyBase(_upperBoundY) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return MatrixPane.this; }
                @Override public String getName() { return "upperBoundY"; }
            };
        }
        return upperBoundY;
    }

    public double getLowerBoundZ() { return null == lowerBoundZ ? _lowerBoundZ : lowerBoundZ.get(); }
    public void setLowerBoundZ(final double VALUE) {
        if (null == lowerBoundZ) {
            _lowerBoundZ = VALUE;
            redraw();
        } else {
            lowerBoundZ.set(VALUE);
        }
    }
    public DoubleProperty lowerBoundZProperty() {
        if (null == lowerBoundZ) {
            lowerBoundZ = new DoublePropertyBase(_lowerBoundZ) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return MatrixPane.this; }
                @Override public String getName() { return "lowerBoundZ"; }
            };
        }
        return lowerBoundZ;
    }

    public double getUpperBoundZ() { return null == upperBoundZ ? _upperBoundZ : upperBoundZ.get(); }
    public void setUpperBoundZ(final double VALUE) {
        if (null == upperBoundZ) {
            _upperBoundZ = VALUE;
            redraw();
        } else {
            upperBoundZ.set(VALUE);
        }
    }
    public DoubleProperty upperBoundZProperty() {
        if (null == upperBoundZ) {
            upperBoundZ = new DoublePropertyBase(_upperBoundZ) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return MatrixPane.this; }
                @Override public String getName() { return "upperBoundZ"; }
            };
        }
        return upperBoundZ;
    }

    public double getRangeX() {  return getUpperBoundX() - getLowerBoundX();  }
    public double getRangeY() { return getUpperBoundY() - getLowerBoundY(); }
    public double getRangeZ() { return getUpperBoundZ() - getLowerBoundZ(); }

    public double getValueAt(final int X, final int Y) throws Exception {
        if (null == getSeries()) { throw new Exception("Series is null"); }
        if (getSeries().getItems().isEmpty()) { throw new Exception("Series is empty"); }
        return getSeries().getAt(X, Y);
    }
    public void setValueAt(final int X, final int Y, final double Z) {
        if (null != getSeries()) {
            minZ   = Math.min(minZ, Z);
            maxZ   = Math.max(maxZ, Z);
            rangeZ = maxZ - minZ;

            Color color = Helper.getColorAt(matrixGradient, Z / rangeZ);
            matrix.setPixel(X, Y, color);
        }
    }

    public MatrixItemSeries<T> getSeries() { return series; }

    public PixelMatrix getMatrix() { return matrix; }

    public void setColorMapping(final ColorMapping MAPPING) { setMatrixGradient(MAPPING.getGradient()); }

    public LinearGradient getMatrixGradient() { return matrixGradient; }
    public void setMatrixGradient(final LinearGradient GRADIENT) {
        matrixGradient = GRADIENT;
        drawChart();
    }


    // ******************** Draw Chart ****************************************
    private void drawChart() {
        if (null == series || series.getItems().isEmpty()) return;

        final ChartType TYPE = series.getChartType();

        switch(TYPE) {
            case MATRIX_HEATMAP: drawMatrixHeatMap(series); break;
        }
    }

    private void drawMatrixHeatMap(final MatrixItemSeries<T> SERIES) {
        minZ   = SERIES.getItems().stream().mapToDouble(MatrixItem::getZ).min().getAsDouble();
        maxZ   = SERIES.getItems().stream().mapToDouble(MatrixItem::getZ).max().getAsDouble();
        rangeZ = maxZ - minZ;

        SERIES.getItems().forEach(data -> {
            Color color = Helper.getColorAt(matrixGradient, data.getZ() / rangeZ);
            matrix.setPixel(data.getX(), data.getY(), color);
        });
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (keepAspect) {
            if (aspectRatio * width > height) {
                width = 1 / (aspectRatio / height);
            } else if (1 / (aspectRatio / height) > width) {
                height = aspectRatio * width;
            }
        }

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            matrix.setPrefSize(width, height);

            scaleX = width / getRangeX();
            scaleY = height / getRangeY();

            redraw();
        }
    }

    private void redraw() {
        drawChart();
    }
}
