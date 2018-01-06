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

import eu.hansolo.fx.charts.data.XYZItem;
import eu.hansolo.fx.charts.series.XYZSeries;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;


public class XYZPane<T extends XYZItem> extends Region implements ChartArea {
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
    private              List<XYZSeries<T>>    listOfSeries;
    private              Canvas                canvas;
    private              GraphicsContext       ctx;
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
    public XYZPane(final XYZSeries<T>... SERIES) {
        this(Color.TRANSPARENT, SERIES);
    }
    public XYZPane(final Paint BACKGROUND, final XYZSeries<T>... SERIES) {
        getStylesheets().add(XYPane.class.getResource("chart.css").toExternalForm());
        aspectRatio      = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        keepAspect       = false;
        _chartBackground = BACKGROUND;
        listOfSeries     = FXCollections.observableArrayList(SERIES);
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

        getStyleClass().setAll("chart", "xyz-chart");

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        pane = new Pane(canvas);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());

        listOfSeries.forEach(series -> series.setOnSeriesEvent(seriesEvent -> redraw()));
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
                @Override public Object getBean() { return XYZPane.this; }
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
                @Override public Object getBean() { return XYZPane.this; }
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
                @Override public Object getBean() { return XYZPane.this; }
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
                @Override public Object getBean() { return XYZPane.this; }
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
                @Override public Object getBean() { return XYZPane.this; }
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
                @Override public Object getBean() { return XYZPane.this; }
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
                @Override public Object getBean() { return XYZPane.this; }
                @Override public String getName() { return "upperBoundZ"; }
            };
        }
        return upperBoundZ;
    }

    public double getRangeX() {  return getUpperBoundX() - getLowerBoundX();  }
    public double getRangeY() { return getUpperBoundY() - getLowerBoundY(); }
    public double getRangeZ() { return getUpperBoundZ() - getLowerBoundZ(); }

    public double getDataMinX() { return listOfSeries.stream().mapToDouble(XYZSeries::getMinX).min().getAsDouble(); }
    public double getDataMaxX() { return listOfSeries.stream().mapToDouble(XYZSeries::getMaxX).max().getAsDouble(); }

    public double getDataMinY() { return listOfSeries.stream().mapToDouble(XYZSeries::getMinY).min().getAsDouble(); }
    public double getDataMaxY() { return listOfSeries.stream().mapToDouble(XYZSeries::getMaxY).max().getAsDouble(); }

    public double getDataMinZ() { return listOfSeries.stream().mapToDouble(XYZSeries::getMinZ).min().getAsDouble(); }
    public double getDataMaxZ() { return listOfSeries.stream().mapToDouble(XYZSeries::getMaxZ).max().getAsDouble(); }

    public double getDataRangeX() { return getDataMaxX() - getDataMinX(); }
    public double getDataRangeY() { return getDataMaxY() - getDataMinY(); }
    public double getDataRangeZ() { return getDataMaxZ() - getDataMinZ(); }

    public List<XYZSeries<T>> getListOfSeries() { return listOfSeries; }


    // ******************** Draw Chart ****************************************
    private void drawChart() {
        if (null == listOfSeries || listOfSeries.isEmpty()) return;

        ctx.clearRect(0, 0, width, height);
        ctx.setFill(getChartBackground());
        ctx.fillRect(0, 0, width, height);

        listOfSeries.forEach(series -> {
            final ChartType TYPE = series.getChartType();
            switch(TYPE) {
                case BUBBLE: drawBubble(series); break;
            }
        });
    }

    private void drawBubble(final XYZSeries<T> SERIES) {
        final double LOWER_BOUND_X = getLowerBoundX();
        final double LOWER_BOUND_Y = getLowerBoundY();
        final double LOWER_BOUND_Z = getLowerBoundZ();

        Paint  seriesFill   = SERIES.getFill();
        Paint  seriesStroke = SERIES.getStroke();
        for (T item : SERIES.getItems()) {
            double x        = (item.getX() - LOWER_BOUND_X) * scaleX;
            double y        = height - (item.getY() - LOWER_BOUND_Y) * scaleY;
            double diameter = (item.getZ() - LOWER_BOUND_Z) * scaleZ;
            double radius   = diameter * 0.5;

            Symbol itemSymbol = item.getSymbol();
            if (Symbol.NONE == itemSymbol) {
                ctx.setFill(seriesFill);
                ctx.setStroke(seriesStroke);
            } else {
                ctx.setFill(item.getFill());
                ctx.setStroke(item.getStroke());
            }
            ctx.fillOval(x - radius, height - y - radius, diameter, diameter);
        }
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

            canvas.setWidth(width);
            canvas.setHeight(height);

            scaleX = width / getRangeX();
            scaleY = height / getRangeY();

            redraw();
        }
    }

    private void redraw() {
        drawChart();
    }
}
