/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2022 Gerrit Grunwald.
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

package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.event.ChartEvt;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.TooltipPopup;
import eu.hansolo.fx.geometry.Circle;
import eu.hansolo.toolbox.evt.EvtObserver;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class BubbleChart<T extends ChartItem> extends Region {
    public static final  Color                 DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;
    private static final double                PREFERRED_WIDTH          = 400;
    private static final double                PREFERRED_HEIGHT         = 400;
    private static final double                MINIMUM_WIDTH            = 50;
    private static final double                MINIMUM_HEIGHT           = 50;
    private static final double                MAXIMUM_WIDTH            = 2048;
    private static final double                MAXIMUM_HEIGHT           = 2048;
    private              double                width;
    private              double                height;
    private              double                chartCenterX;
    private              double                chartCenterY;
    private              Canvas                canvas;
    private              GraphicsContext       ctx;
    private              Color                 _backgroundColor;
    private              ObjectProperty<Color> backgroundColor;
    private              TooltipPopup          popup;
    private              ObservableList<T>     items;
    private              EvtObserver<ChartEvt> itemObserver;
    private              ListChangeListener<T> itemListListener;
    private              List<BubbleNode>      nodes;
    private              double                max;
    private              long                  lastTimerCall;
    private              AnimationTimer        timer;
    


    // ******************** Constructors **************************************
    public BubbleChart() {
        this(new ArrayList<>());
    }
    public BubbleChart(final List<T> ITEMS) {
        items               = FXCollections.observableArrayList();
        itemObserver        = e -> redraw();
        itemListListener    = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedItem -> addedItem.addChartEvtObserver(ChartEvt.ITEM_UPDATE, itemObserver));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedItem -> removedItem.removeChartEvtObserver(ChartEvt.ITEM_UPDATE, itemObserver));
                }
            }
            max = items.stream().max(Comparator.comparingDouble(ChartItem::getValue)).get().getValue();
        };
        nodes               = new ArrayList<>();
        _backgroundColor    = DEFAULT_BACKGROUND_COLOR;
        popup               = new TooltipPopup("", 3500, true);

        items.setAll(null == ITEMS ? new ArrayList<>() : ITEMS);
        items.forEach(item -> nodes.add(new BubbleNode(item)));

        lastTimerCall = System.nanoTime();
        timer         = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 100_000_000) {
                    update();
                    lastTimerCall = now;
                }
            }
        };

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

        getChildren().setAll(canvas);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        items.addListener(itemListListener);
        canvas.setOnMousePressed(e -> {
            String  tooltipText = new StringBuilder().toString();
            if (!tooltipText.isEmpty()) {
                popup.setX(e.getScreenX() - popup.getWidth() * 0.5);
                popup.setY(e.getScreenY() - 30);
                popup.setText(tooltipText);
                popup.animatedShow(getScene().getWindow());
            }
        });
    }


    // ******************** Methods *******************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
    }

    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public void dispose() { items.removeListener(itemListListener); }

    public List<T> getItems() { return items; }
    public void setItems(final T... ITEMS) { setItems(Arrays.asList(ITEMS)); }
    public void setItems(final List<T> ITEMS) { items.setAll(ITEMS); }
    public void addItem(final T ITEM) {
        if (!items.contains(ITEM)) { items.add(ITEM); }
    }
    public void removeItem(final T ITEM) {
        if (items.contains(ITEM)) { items.remove(ITEM); }
    }

    public Color getBackgroundColor() { return null == backgroundColor ? _backgroundColor : backgroundColor.get(); }
    public void setBackgroundColor(final Color backgroundColor) {
        if (null == this.backgroundColor) {
            _backgroundColor = backgroundColor;
            redraw();
        } else {
            this.backgroundColor.set(backgroundColor);
        }
    }
    public ObjectProperty<Color> backgroundColorProperty() {
        if (null == backgroundColor) {
            backgroundColor = new ObjectPropertyBase<>(_backgroundColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BubbleChart.this; }
                @Override public String getName() { return "backgroundColor"; }
            };
            _backgroundColor = null;
        }
        return backgroundColor;
    }

    public void setPopupTimeout(final long milliseconds) { popup.setTimeout(milliseconds); }

    public void start() { timer.start(); }
    public void stop() { timer.stop(); }


    // ******************** Layout ********************************************
    private void update() {
        nodes.forEach(node -> {
            double distanceToCenter = node.distanceToCenter();
            if (distanceToCenter > node.getRadius() * 0.5) {
                double force = node.force();
                double[] xy = node.stepToCenter(force);
                node.setCenter(xy[0], xy[1]);
            }
        });
        redraw();
    }

    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        chartCenterX = width * 0.5;
        chartCenterY = height * 0.5;

        if (width > 0 && height > 0) {
            canvas.setWidth(width);
            canvas.setHeight(height);
            canvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            ctx.setTextBaseline(VPos.CENTER);
        }
        redraw();
    }

    private void redraw() {
        ctx.clearRect(0, 0, width, height);
        ctx.setFill(getBackgroundColor());
        ctx.fillRect(0, 0, width, height);

        if (items.isEmpty()) { return; }

        double min = items.stream().min(Comparator.comparingDouble(ChartItem::getValue)).get().getValue();
        double max = items.stream().max(Comparator.comparingDouble(ChartItem::getValue)).get().getValue();

        double maxRadius   = 50;
        double range       = max - min;
        double scaleFactor = maxRadius / range;

        nodes.forEach(node -> {
            node.setScaleFactor(scaleFactor);
            ctx.setFill(node.getFill());
            ctx.fillOval(node.getX(), node.getY(), node.getWidth(), node.getHeight());
        });
    }


    // ******************** Inner Classes *************************************
    public class BubbleNode {
        private ChartItem item;
        private Circle    circle;
        private double    scaleFactor;


        public BubbleNode(final T item) {
            this.item        = item;
            this.circle      = new Circle(0, 0, item.getValue());
            this.scaleFactor = 1.0;
        }


        public double getValue() { return item.getValue(); }

        public Circle getCircle() { return circle; }

        public double getX() { return circle.getX(); }
        public double getY() { return circle.getY(); }

        public double getWidth() { return circle.getWidth(); }
        public double getHeight() { return circle.getHeight(); }

        public double getRadius() { return circle.getRadius(); }

        public double getCenterX() { return circle.getCenterX(); }
        public void setCenterX(final double centerX) { circle.setCenterX(centerX); }

        public void setCenter(final double centerX, final double centerY) {
            circle.setCenter(centerX, centerY);
        }

        public double getCenterY() { return circle.getCenterY(); }
        public void setCenterY(final double centerY) { circle.setCenterY(centerY); }

        public Color getFill() { return item.getFill(); }
        public void setFill(final Color fill) { item.setFill(fill); }

        public boolean contains(final double x, final double y) { return circle.contains(x, y); }

        public boolean intersects(final BubbleNode other) {
            final double d = Math.sqrt((getCenterX() - other.getCenterX()) * (getCenterX() - other.getCenterX()) + (getCenterY() - other.getCenterY()) * (getCenterY() - other.getCenterY()));
            if (((d <= circle.getRadius() - other.getRadius() || d <= other.getRadius() - circle.getRadius()) || d < circle.getRadius() + other.getRadius()) || d == circle.getRadius() + other.getRadius()) {
                return true;
            } else {
                return false;
            }
        }

        public double distanceTo(final BubbleNode other) {
            return Helper.distance(getCenterX(), getCenterY(), other.getCenterX(), other.getCenterY());
        }

        public double distanceToCenter() {
            return Helper.distance(getCenterX(), getCenterY(), chartCenterX, chartCenterY);
        }

        public double[] stepToCenter(final double step) {
            double x = getCenterX() + ((step / distanceToCenter()) * (chartCenterX - getCenterX()));
            double y = getCenterY() + ((step / distanceToCenter()) * (chartCenterY - getCenterY()));
            return new double[] { x, y };
        }

        public double getScaleFactor() { return scaleFactor; }
        public void setScaleFactor(final double scaleFactor) {
            this.scaleFactor = scaleFactor;
            this.circle.setRadius(this.item.getValue() * scaleFactor);
        }

        public double force() {
            double g = 6.67 / 1e11;
            double distance = distanceToCenter() * 1000;
            double f = (g * getValue()) / (distance * distance);
            return f;
        }
    }
}
