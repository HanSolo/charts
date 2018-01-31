/*
 * Copyright (c) 2018 by Gerrit Grunwald
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


import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.DataObject;
import eu.hansolo.fx.charts.data.PlotItem;
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.Order;
import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;


@DefaultProperty("children")
public class ParallelCoordinatesChart extends Region {
    public enum StreamFillMode { COLOR, GRADIENT }
    private static final double                         PREFERRED_WIDTH  = 600;
    private static final double                         PREFERRED_HEIGHT = 400;
    private static final double                         MINIMUM_WIDTH    = 50;
    private static final double                         MINIMUM_HEIGHT   = 50;
    private static final double                         MAXIMUM_WIDTH    = 2048;
    private static final double                         MAXIMUM_HEIGHT   = 2048;
    private              double                         size;
    private              double                         width;
    private              double                         height;
    private              Canvas                         canvas;
    private              GraphicsContext                ctx;
    private              ObservableList<DataObject>     items;
    private              ArrayList<String>              categories;
    private              Map<String,List<DataObject>>   categoryMap;
    private              ItemEventListener              itemListener;
    private              ListChangeListener<DataObject> objectListListener;


    // ******************** Constructors **************************************
    public ParallelCoordinatesChart() {
        items              = FXCollections.observableArrayList();
        itemListener       = e -> redraw();
        objectListListener = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedObject -> addedObject.getProperties().values().forEach(item -> item.setOnItemEvent(itemListener)));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedObject -> removedObject.getProperties().values().forEach(item -> item.removeItemEventListener(itemListener)));
                }
            }
            prepareData();
            redraw();
        };
        categories  = new ArrayList<>();
        categoryMap = new HashMap<>();

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
        items.addListener(objectListListener);
        canvas.setOnMouseClicked(e -> {

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

    public void dispose() { items.forEach(object -> object.getProperties().values().forEach(item -> item.removeItemEventListener(itemListener))); }

    public List<DataObject> getItems() { return items; }
    public void setItems(final DataObject... ITEMS) { setItems(Arrays.asList(ITEMS)); }
    public void setItems(final List<DataObject> ITEMS) {
        items.setAll(ITEMS);
    }
    public void addItem(final DataObject ITEM) {
        if (!items.contains(ITEM)) { items.add(ITEM); }
    }
    public void removeItem(final DataObject ITEM) {
        if (items.contains(ITEM)) { items.remove(ITEM); }
    }

    public void sortCategory(final String CATEGORY, final List<DataObject> DATA_OBJECTS, final Order ORDER) {
        DATA_OBJECTS.sort(Comparator.comparingDouble(object -> object.getProperties().get(CATEGORY).getValue()));
        if (Order.DESCENDING == ORDER) { Collections.reverse(DATA_OBJECTS); }
    }

    public List<String> getCategories() { return categories; }

    public Map<String, List<DataObject>> getCategoryMap() { return categoryMap; }

    private double[] getMinMax(final String CATEGORY) {
        double min = categoryMap.get(CATEGORY).stream().mapToDouble(obj -> obj.getProperties().get(CATEGORY).getValue()).min().getAsDouble();
        double max = categoryMap.get(CATEGORY).stream().mapToDouble(obj -> obj.getProperties().get(CATEGORY).getValue()).max().getAsDouble();
        return new double[]{ min, max };
    }

    private void prepareData() {
        if (items.isEmpty()) { return; }
        categoryMap.clear();
        List<String> keys = new ArrayList<>(items.get(0).getProperties().keySet());
        if (keys.size() <= 1) { throw new RuntimeException("You need at least 2 categories in your DataObject"); }

        keys.forEach(key -> categoryMap.put(key, new ArrayList<>()));
        keys.forEach(key -> items.forEach(dataObject -> categoryMap.get(key).add(dataObject)));
        keys.forEach(key -> sortCategory(key, categoryMap.get(key), Order.DESCENDING));

        categories.clear();
        categories.addAll(categoryMap.keySet());
    }

    private void shiftCategory(final String CATEGORY, final int INDEX) {
        if (!categories.contains(CATEGORY) || INDEX == categories.indexOf(CATEGORY)) { return; }
        categories.remove(CATEGORY);
        categories.add(INDEX, CATEGORY);
    }

    private double[] calcAutoScale(final double MIN_VALUE, final double MAX_VALUE) {
        double maxNoOfMajorTicks = 10;
        double maxNoOfMinorTicks = 10;
        double niceRange         = (Helper.calcNiceNumber((MAX_VALUE - MIN_VALUE), false));
        double majorTickSpace    = Helper.calcNiceNumber(niceRange / (maxNoOfMajorTicks - 1), true);
        double minorTickSpace    = Helper.calcNiceNumber(majorTickSpace / (maxNoOfMinorTicks - 1), true);
        double niceMinValue      = (Math.floor(MIN_VALUE / majorTickSpace) * majorTickSpace);
        double niceMaxValue      = (Math.ceil(MAX_VALUE / majorTickSpace) * majorTickSpace);
        return new double[] { niceMinValue, niceMaxValue, minorTickSpace, majorTickSpace };
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            canvas.setWidth(width);
            canvas.setHeight(height);
            canvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            ctx.setTextBaseline(VPos.CENTER);

            redraw();
        }
    }

    private void redraw() {
        shiftCategory("consumption", 0);
        ctx.clearRect(0, 0, width, height);
        ctx.setTextBaseline(VPos.CENTER);

        int    noOfCategories  = categories.size();
        double headerHeight    = 30;
        double axisWidth       = 10;
        double axisHeight      = height - headerHeight - 0.5;
        double availableWidth  = width - axisWidth;
        double availableHeight = height - headerHeight;
        double spacer          = availableWidth / (noOfCategories - 1);
        double headerFontSize  = size * 0.025;
        double unitFontSize    = size * 0.015;
        double axisFontSize    = size * 0.0125;

        // Go through all categories
        for (int i = 0 ; i < noOfCategories ; i++) {
            String   category         = categories.get(i);
            String   unit             = categoryMap.get(category).get(0).getProperties().get(category).getUnit();
            double   axisX            = i * spacer + axisWidth * 0.5;
            double   axisY            = headerHeight;
            double[] minMax           = getMinMax(category);
            double[] axisParam        = calcAutoScale(minMax[0], minMax[1]);
            double   minValue         = axisParam[0];
            double   maxValue         = axisParam[1];
            double   range            = maxValue - minValue;
            double   minorTickSpace   = axisParam[2];
            double   majorTickSpace   = axisParam[3];

            double   stepSize         = Math.abs(axisHeight / range);
            double   maxY             = axisY + axisHeight;

            // Draw header and unit
            ctx.setFill(Color.BLACK);
            if (i == 0) {
                ctx.setTextAlign(TextAlignment.LEFT);
            } else if (i == (noOfCategories - 1)) {
                ctx.setTextAlign(TextAlignment.RIGHT);
            } else {
                ctx.setTextAlign(TextAlignment.CENTER);
            }
            ctx.setFont(Font.font(Helper.clamp(8, 24, headerFontSize)));
            ctx.fillText(category, axisX, 5);
            ctx.setFont(Font.font(Helper.clamp(8, 24, unitFontSize)));
            ctx.fillText(String.join("", "[", unit, "]"), axisX, 18);

            // Draw axis
            ctx.setStroke(Color.BLACK);
            ctx.strokeLine(axisX, axisY, axisX, maxY);

            // TickMarks
            ctx.setFont(Font.font(Helper.clamp(8, 24, axisFontSize)));
            ctx.setFill(Color.BLACK);
            double     tmpStep          = minorTickSpace;
            BigDecimal minorTickSpaceBD = BigDecimal.valueOf(minorTickSpace);
            BigDecimal majorTickSpaceBD = BigDecimal.valueOf(majorTickSpace);
            BigDecimal mediumCheck2     = BigDecimal.valueOf(2 * minorTickSpace);
            BigDecimal mediumCheck5     = BigDecimal.valueOf(5 * minorTickSpace);
            BigDecimal counterBD        = BigDecimal.valueOf(minValue);
            double     counter          = minValue;

            // Main Loop for tick marks and labels
            BigDecimal tmpStepBD = new BigDecimal(tmpStep);
            tmpStepBD = tmpStepBD.setScale(6, BigDecimal.ROUND_HALF_UP); // newScale == number of decimals taken into account
            tmpStep   = tmpStepBD.doubleValue();
            for (double j = 0; Double.compare(-range - tmpStep, j) <= 0; j -= tmpStep) {
                double fixedPosition = (counter - minValue) * stepSize + headerHeight;
                double innerPointX   = axisX - 3;
                double innerPointY   = fixedPosition;
                double outerPointX   = axisX + 3;
                double outerPointY   = fixedPosition;

                if (Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(majorTickSpaceBD).doubleValue(), 0.0) == 0) {
                    // Draw major tick mark
                    ctx.setStroke(Color.BLACK);
                    ctx.setLineWidth(1);
                    ctx.strokeLine(innerPointX, innerPointY, outerPointX, outerPointY);

                    double  axisValue  = maxValue - counter + minValue;
                    boolean isMinValue = Double.compare(minValue, axisValue) == 0;
                    boolean isMaxValue = Double.compare(maxValue, axisValue) == 0;
                    double  offsetY    = 0;
                    if (isMinValue) {
                        offsetY = -axisFontSize;
                    } else if (isMaxValue) {
                        offsetY = axisFontSize;
                    }

                    if (i == (noOfCategories - 1)) {
                        ctx.setTextAlign(TextAlignment.RIGHT);
                        ctx.fillText(String.format(Locale.US, "%.0f", axisValue), axisX - 5, outerPointY + offsetY);
                    } else {
                        ctx.setTextAlign(TextAlignment.LEFT);
                        ctx.fillText(String.format(Locale.US, "%.0f", axisValue), axisX + 5, outerPointY + offsetY);
                    }
                } else if (Double.compare(minorTickSpaceBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(mediumCheck2).doubleValue(), 0.0) != 0.0 &&
                           Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(mediumCheck5).doubleValue(), 0.0) == 0.0) {
                    // Draw medium tick mark
                    ctx.strokeLine(innerPointX + 1, innerPointY, outerPointX - 1, outerPointY);
                } /*else if (Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(minorTickSpaceBD).doubleValue(), 0.0) == 0) {
                    // Draw minor tick mark
                    ctx.strokeLine(innerPointX + 2, innerPointY, outerPointX - 2, outerPointY);
                }*/

                counterBD = counterBD.add(minorTickSpaceBD);
                counter   = counterBD.doubleValue();
                if (counter > maxValue) break;
            }

            categoryMap.get(category).forEach(obj -> {
                ChartItem item  = obj.getProperties().get(category);
                double    itemY = (item.getValue() - minValue) * stepSize;
                item.setX(axisX);
                item.setY(maxY - itemY);
                ctx.setStroke(obj.getStroke());
            });
        }

        // Draw connections
        Map<DataObject, List<Double[]>> lines = new HashMap<>();
        categories.forEach(category -> {
            for (DataObject obj : categoryMap.get(category)) {
                if (lines.keySet().contains(obj)) {
                    lines.get(obj).add(new Double[] { obj.getProperties().get(category).getX(), obj.getProperties().get(category).getY() });
                } else {
                    lines.put(obj, new ArrayList<>());
                    lines.get(obj).add(new Double[] { obj.getProperties().get(category).getX(), obj.getProperties().get(category).getY() });
                }
                lines.entrySet().forEach(e -> {
                    ctx.setStroke(e.getKey().getStroke());
                    ctx.beginPath();
                    for (Double[] xy : e.getValue()) { ctx.lineTo(xy[0], xy[1]); }
                    ctx.stroke();
                });
            }
        });
    }
}
