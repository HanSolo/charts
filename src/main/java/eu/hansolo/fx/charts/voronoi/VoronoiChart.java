/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.charts.voronoi;

import eu.hansolo.fx.charts.tools.Helper;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


/**
 * User: hansolo
 * Date: 19.08.21
 * Time: 04:25
 */
@DefaultProperty("children")
public class VoronoiChart extends Region {
    public  enum Type { VORONOI, DELAUNY }

    private static final double                   PREFERRED_WIDTH  = 250;
    private static final double                   PREFERRED_HEIGHT = 250;
    private static final double                   MINIMUM_WIDTH    = 50;
    private static final double                   MINIMUM_HEIGHT   = 50;
    private static final double                   MAXIMUM_WIDTH    = 1024;
    private static final double                   MAXIMUM_HEIGHT   = 1024;
    private static final Random                   RND              = new Random();
    private static final double                   POINT_RADIUS     = 3;
    private static       int                      initialSize      = 10000;
    private              String                   userAgentStyleSheet;
    private              double                   width;
    private              double                   height;
    private              Canvas                   canvas;
    private              GraphicsContext          ctx;
    private              Pane                     pane;
    private              ObservableList<VPoint>   points;
    private              Triangle                 initialTriangle;
    private              Triangulation            triangulation;
    private              Map<Object, Color>       colorTable;
    private              boolean                  _pointsVisible;
    private              BooleanProperty          pointsVisible;
    private              Color                    _pointColor;
    private              ObjectProperty<Color>    pointColor;
    private              boolean                  _fillRegions;
    private              BooleanProperty          fillRegions;
    private              Color                    _borderColor;
    private              ObjectProperty<Color>    borderColor;
    private              Type                     _type;
    private              ObjectProperty<Type>     type;
    private              boolean                  _multicolor;
    private              BooleanProperty          multicolor;
    private              Color                    _voronoiColor;
    private              ObjectProperty<Color>    voronoiColor;
    private              Color                    _delaunayColor;
    private              ObjectProperty<Color>    delaunayColor;
    private              boolean                  _interactive;
    private              BooleanProperty          interactive;
    private              EventHandler<MouseEvent> mouseHandler;


    // ******************** Constructors **************************************
    public VoronoiChart() {
        this(new ArrayList<>());
    }
    public VoronoiChart(final List<VPoint> points) {
        this.points          = FXCollections.observableArrayList(points);
        this.initialTriangle = new Triangle(new VPoint(-initialSize, -initialSize), new VPoint(initialSize, -initialSize), new VPoint(0, initialSize));
        this.triangulation   = new Triangulation(initialTriangle);
        this.colorTable      = new HashMap<>();
        this._pointsVisible  = true;
        this._pointColor     = Color.BLACK;
        this._fillRegions    = true;
        this._borderColor    = Color.BLACK;
        this._type           = Type.VORONOI;
        this._multicolor     = true;
        this._voronoiColor   = Color.ORANGERED;
        this._delaunayColor  = Color.YELLOWGREEN;
        this._interactive    = false;
        this.mouseHandler    = event -> addPoint(new VPoint(event.getX(), event.getY()));
        initGraphics();
        registerListeners();

        points.forEach(point -> addPoint(point));
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        getStyleClass().add("voronoi-chart");

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        pane   = new Pane(canvas);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        points.addListener((ListChangeListener<VPoint>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(point -> addPoint(point));
                } else if (c.wasRemoved()) {
                }
            }
            redraw();
        });
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public ObservableList<VPoint> getPoints() { return points; }
    public void setPoints(final List<VPoint> points) { this.points.setAll(points); }

    public boolean getPointsVisible() { return null == pointsVisible ? _pointsVisible : pointsVisible.get(); }
    public void setPointsVisible(final boolean visible) {
        if (null == pointsVisible) {
            _pointsVisible = visible;
            redraw();
        } else {
            pointsVisible.set(visible);
        }
    }
    public BooleanProperty pointsVisibleProperty() {
        if (null == pointsVisible) {
            pointsVisible = new BooleanPropertyBase(_pointsVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return VoronoiChart.this; }
                @Override public String getName() { return "pointsVisible"; }
            };
        }
        return pointsVisible;
    }

    public Color getPointColor() { return null == pointColor ? _pointColor : pointColor.get(); }
    public void setPointColor(final Color color) {
        if (null == pointColor) {
            _pointColor = color;
            redraw();
        } else {
            pointColor.set(color);
        }
    }
    public ObjectProperty<Color> pointColorProperty() {
        if (null == pointColor) {
            pointColor = new ObjectPropertyBase<>(_pointColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return VoronoiChart.this; }
                @Override public String getName() { return "pointColor"; }
            };
            _pointColor = null;
        }
        return pointColor;
    }

    public boolean getFillRegions() { return null == fillRegions ? _fillRegions : fillRegions.get(); }
    public void setFillRegions(final boolean fill) {
        if (null == fillRegions) {
            _fillRegions = fill;
            redraw();
        } else {
            fillRegions.set(fill);
        }
    }
    public BooleanProperty fillRegionsProperty() {
        if (null == fillRegions) {
            fillRegions = new BooleanPropertyBase(_fillRegions) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return VoronoiChart.this; }
                @Override public String getName() { return "fillAreas"; }
            };
        }
        return fillRegions;
    }

    public Color getBorderColor() { return null == borderColor ? _borderColor : borderColor.get(); }
    public void setBorderColor(final Color color) {
        if (null == borderColor) {
            _borderColor = color;
            redraw();
        } else {
            borderColor.set(color);
        }
    }
    public ObjectProperty<Color> borderColorProperty() {
        if (null == borderColor) {
            borderColor = new ObjectPropertyBase<>(_borderColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return VoronoiChart.this; }
                @Override public String getName() { return "borderColor"; }
            };
            _borderColor = null;
        }
        return borderColor;
    }

    public boolean isVoronoi() {
        return true;
    }

    public boolean getMulticolor() { return null == multicolor ? _multicolor : multicolor.get(); }
    public void setMulticolor(final boolean multicolor) {
        if (null == this.multicolor) {
            _multicolor = multicolor;
            redraw();
        } else {
            this.multicolor.set(multicolor);
        }
    }
    public BooleanProperty multicolorProperty() {
        if (null == multicolor) {
            multicolor = new BooleanPropertyBase(_multicolor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return VoronoiChart.this; }
                @Override public String getName() { return "colorful"; }
            };
        }
        return multicolor;
    }

    public Color getVoronoiColor() { return null == voronoiColor ? _voronoiColor : voronoiColor.get(); }
    public void setVoronoiColor(final Color color) {
        if (null == voronoiColor) {
            _voronoiColor = color;
            redraw();
        } else {
            voronoiColor.set(color);
        }
    }
    public ObjectProperty<Color> voronoiColorProperty() {
        if (null == voronoiColor) {
            voronoiColor = new ObjectPropertyBase<>(_voronoiColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return VoronoiChart.this; }
                @Override public String getName() { return "voronoiColor"; }
            };
            _voronoiColor = null;
        }
        return voronoiColor;
    }

    public Color getDelaunayColor() { return null == delaunayColor ? _delaunayColor : delaunayColor.get(); }
    public void setDelaunayColor(final Color color) {
        if (null == delaunayColor) {
            _delaunayColor = color;
            redraw();
        } else {
            delaunayColor.set(color);
        }
    }
    public ObjectProperty<Color> delaunyColorProperty() {
        if (null == delaunayColor) {
            delaunayColor = new ObjectPropertyBase<>(_delaunayColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return VoronoiChart.this; }
                @Override public String getName() { return "delaunyColor"; }
            };
            _delaunayColor = null;
        }
        return delaunayColor;
    }

    public void addPoint(final VPoint point) {
        triangulation.place(point);
        redraw();
    }

    public void clear() {
        triangulation = new Triangulation(initialTriangle);
        redraw();
    }

    public Type getType() { return null == type ? _type : type.get(); }
    public void setType(final Type TYPE) {
        if (null == type) {
            _type = TYPE;
            redraw();
        } else {
            type.set(TYPE);
        }
    }
    public ObjectProperty<Type> typeProperty() {
        if (null == type) {
            type = new ObjectPropertyBase<>(_type) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return VoronoiChart.this; }
                @Override public String getName() { return "type"; }
            };
            _type = null;
        }
        return type;
    }

    public boolean isInteractive() { return null == interactive ? _interactive : interactive.get(); }
    public void setInteractive(final boolean interactive) {
        if (null == this.interactive) {
            if (_interactive == interactive) { return; }
            _interactive = interactive;
            if (interactive) {
                canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
            } else {
                canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
            }
        }
    }
    public BooleanProperty interactiveProperty() {
        if (null == interactive) {
            interactive = new BooleanPropertyBase(_interactive) {
                @Override protected void invalidated() {
                    if (get()) {
                        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
                    } else {
                        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
                    }
                }
                @Override public void set(final boolean INTERACTIVE) {
                    if (get() == INTERACTIVE) { return; }
                    super.set(INTERACTIVE);
                }
                @Override public Object getBean() { return VoronoiChart.this; }
                @Override public String getName() { return "interactive"; }
            };
        }
        return interactive;
    }


    // ******************** Layout *******************************************
    @Override public String getUserAgentStylesheet() {
        if (null == userAgentStyleSheet) {userAgentStyleSheet = VoronoiChart.class.getResource("../chart.css").toExternalForm();}
        return userAgentStyleSheet;
    }

    private Color getColor(final Object item) {
        if (colorTable.containsKey(item)) { return colorTable.get(item); }
        Color color = Helper.hsbToRGB(RND.nextDouble() * 360, 1.0, 1.0);
        colorTable.put(item, color);
        return color;
    }

    private void drawPoint(final VPoint point) {
        double r = POINT_RADIUS;
        double x = (int) point.getCoordinates(0);
        double y = (int) point.getCoordinates(1);
        ctx.setFill(getPointColor());
        ctx.fillOval(x - r, y - r, r + r, r + r);
    }

    private void drawPolygon(final VPoint[] polygon, final Color fillColor) {
        double[] x = new double[polygon.length];
        double[] y = new double[polygon.length];
        for (int i = 0; i < polygon.length; i++) {
            x[i] = polygon[i].getCoordinates(0);
            y[i] = polygon[i].getCoordinates(1);
        }
        if (fillColor != null) {
            Paint temp = ctx.getFill();
            ctx.setFill(fillColor);
            ctx.fillPolygon(x, y, polygon.length);
            ctx.setFill(temp);
        }
        ctx.setStroke(getBorderColor());
        ctx.strokePolygon(x, y, polygon.length);
    }

    private void drawDelaunay() {
        boolean filled = getFillRegions();
        for (Triangle triangle : triangulation) {
            VPoint[] vertices = triangle.toArray(new VPoint[0]);
            drawPolygon(vertices, filled ? getMulticolor() ? getColor(triangle) : getDelaunayColor() : Color.TRANSPARENT);
        }
    }

    private void drawVoronoi() {
        boolean     filled = getFillRegions();
        Set<VPoint> done   = new HashSet<>(initialTriangle);
        for (Triangle triangle : triangulation) {
            for (VPoint point : triangle) {
                if (done.contains(point)) { continue; }
                done.add(point);
                List<Triangle> triangles = triangulation.surroundingTriangles(point, triangle);
                VPoint[]       vertices  = new VPoint[triangles.size()];
                int            counter   = 0;
                for (Triangle tri : triangles) {
                    vertices[counter++] = tri.getCircumCenter();
                }
                drawPolygon(vertices, filled ? getMulticolor() ? getColor(point) : getVoronoiColor() : Color.TRANSPARENT);

                if (getPointsVisible()) { drawPoint(point); }
            }
        }
    }

    private void drawChart() {
        ctx.clearRect(0, 0, width, height);

        if (!isVoronoi()) {
            ctx.setFill(getDelaunayColor());
        } else if (triangulation.contains(initialTriangle)) {
            ctx.setFill(Color.TRANSPARENT);
        } else {
            ctx.setFill(getVoronoiColor());
        }
        ctx.fillRect(0, 0, this.getWidth(), this.getHeight());

        if (!getMulticolor()) { colorTable.clear(); }

        switch(getType()) {
            case VORONOI: drawVoronoi(); break;
            case DELAUNY: drawDelaunay(); break;
        }
    }

    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            canvas.setWidth(width);
            canvas.setHeight(height);

            redraw();
        }
    }

    private void redraw() {
        drawChart();
    }
}
