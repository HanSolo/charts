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
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.Order;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
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
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@DefaultProperty("children")
public class ParallelCoordinatesChart extends Region {
    private static final double                         PREFERRED_WIDTH  = 600;
    private static final double                         PREFERRED_HEIGHT = 400;
    private static final double                         MINIMUM_WIDTH    = 50;
    private static final double                         MINIMUM_HEIGHT   = 50;
    private static final double                         MAXIMUM_WIDTH    = 2048;
    private static final double                         MAXIMUM_HEIGHT   = 2048;
    private              double                         size;
    private              double                         width;
    private              double                         height;
    private              Canvas                         axisCanvas;
    private              GraphicsContext                axisCtx;
    private              Canvas                         connectionCanvas;
    private              GraphicsContext                connectionCtx;
    private              Color                          _axisColor;
    private              ObjectProperty<Color>          axisColor;
    private              Color                          _headerColor;
    private              ObjectProperty<Color>          headerColor;
    private              Color                          _unitColor;
    private              ObjectProperty<Color>          unitColor;
    private              Color                          _tickLabelColor;
    private              ObjectProperty<Color>          tickLabelColor;
    private              Locale                         _locale;
    private              ObjectProperty<Locale>         locale;
    private              int                            _decimals;
    private              IntegerProperty                decimals;
    private              boolean                        _tickMarksVisible;
    private              BooleanProperty                tickMarksVisible;
    private              Color                          _selectedColor;
    private              ObjectProperty<Color>          selectedColor;
    private              Color                          _unselectedColor;
    private              ObjectProperty<Color>          unselectedColor;
    private              String                         formatString;
    private              boolean                        selectionStarted;
    private              List<ChartItem>                selectedItems;
    private              ObservableList<DataObject>     items;
    private              ArrayList<String>              categories;
    private              Map<String,List<DataObject>>   categoryMap;
    private              ItemEventListener              itemListener;
    private              ListChangeListener<DataObject> objectListListener;


    // ******************** Constructors **************************************
    public ParallelCoordinatesChart() {
        _axisColor         = Color.BLACK;
        _headerColor       = Color.BLACK;
        _unitColor         = Color.BLACK;
        _tickLabelColor    = Color.BLACK;
        _locale            = Locale.US;
        _decimals          = 0;
        _tickMarksVisible  = true;
        _selectedColor     = Color.BLUE;
        _unselectedColor   = Color.LIGHTGRAY;
        formatString       = new StringBuilder("%.").append(_decimals).append("f").toString();
        selectionStarted   = false;
        selectedItems      = new ArrayList<>();
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
        categories         = new ArrayList<>();
        categoryMap        = new HashMap<>();

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

        axisCanvas       = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        axisCtx          = axisCanvas.getGraphicsContext2D();

        connectionCanvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        connectionCanvas.setMouseTransparent(true);
        connectionCtx    = connectionCanvas.getGraphicsContext2D();

        getChildren().setAll(axisCanvas, connectionCanvas);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        items.addListener(objectListListener);
        axisCanvas.setOnMouseClicked(e -> {

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

    public Color getAxisColor() { return null == axisColor ? _axisColor : axisColor.get(); }
    public void setAxisColor(final Color COLOR) {
        if (null == axisColor) {
            _axisColor = COLOR;
            redraw();
        } else {
            axisColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> axisColorProperty() {
        if (null == axisColor) {
            axisColor = new ObjectPropertyBase<Color>(_axisColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ParallelCoordinatesChart.this; }
                @Override public String getName() { return "axisColor"; }
            };
            _axisColor = null;
        }
        return axisColor;
    }

    public Color getHeaderColor() { return null == headerColor ? _headerColor : headerColor.get(); }
    public void setHeaderColor(final Color COLOR) {
        if (null == headerColor) {
            _headerColor = COLOR;
            redraw();
        } else {
            headerColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> headerColorProperty() {
        if (null == headerColor) {
            headerColor = new ObjectPropertyBase<Color>(_headerColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ParallelCoordinatesChart.this; }
                @Override public String getName() { return "headerColor"; }
            };
            _headerColor = null;
        }
        return headerColor;
    }

    public Color getUnitColor() { return null == unitColor ? _unitColor : unitColor.get(); }
    public void setUnitColor(final Color COLOR) {
        if (null == unitColor) {
            _unitColor = COLOR;
            redraw();
        } else {
            unitColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> unitColorProperty() {
        if (null == unitColor) {
            unitColor = new ObjectPropertyBase<Color>(_unitColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ParallelCoordinatesChart.this; }
                @Override public String getName() { return "unitColor"; }
            };
            _unitColor = null;
        }
        return unitColor;
    }

    public Color getTickLabelColor() { return null == tickLabelColor ? _tickLabelColor : tickLabelColor.get(); }
    public void setTickLabelColor(final Color COLOR) {
        if (null == tickLabelColor) {
            _tickLabelColor = COLOR;
            redraw();
        } else {
            tickLabelColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> tickLabelColorProperty() {
        if (null == tickLabelColor) {
            tickLabelColor = new ObjectPropertyBase<Color>(_tickLabelColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ParallelCoordinatesChart.this; }
                @Override public String getName() { return "tickLabelColor"; }
            };
            _tickLabelColor = null;
        }
        return tickLabelColor;
    }

    public Locale getLocale() { return null == locale ? _locale : locale.get(); }
    public void setLocale(final Locale LOCALE) {
        if (null == locale) {
            _locale = LOCALE;
            redraw();
        } else {
            locale.set(LOCALE);
        }
    }
    public ObjectProperty<Locale> localeProperty() {
        if (null == locale) {
            locale = new ObjectPropertyBase<Locale>(_locale) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ParallelCoordinatesChart.this; }
                @Override public String getName() { return "locale"; }
            };
            _locale = null;
        }
        return locale;
    }

    public int getDecimals() { return null == decimals ? _decimals : decimals.get(); }
    public void setDecimals(final int DECIMALS) {
        if (null == decimals) {
            _decimals    = Helper.clamp(0, 6, DECIMALS);
            formatString = new StringBuilder("%.").append(_decimals).append("f").toString();
            redraw();
        } else {
            decimals.set(DECIMALS);
        }
    }
    public IntegerProperty decimalsProperty() {
        if (null == decimals) {
            decimals = new IntegerPropertyBase(_decimals) {
                @Override protected void invalidated() {
                    set(Helper.clamp(0, 6, get()));
                    formatString = new StringBuilder("%.").append(get()).append("f").toString();
                    redraw();
                }
                @Override public Object getBean() { return ParallelCoordinatesChart.this; }
                @Override public String getName() { return "decimals"; }
            };
        }
        return decimals;
    }

    public boolean isTickMarksVisible() { return null == tickMarksVisible ? _tickMarksVisible : tickMarksVisible.get(); }
    public void setTickMarksVisible(final boolean VISIBLE) {
        if (null == tickMarksVisible) {
            _tickMarksVisible = VISIBLE;
            redraw();
        } else {
            tickMarksVisible.set(VISIBLE);
        }
    }
    public BooleanProperty tickMarksVisibleProperty() {
        if (null == tickMarksVisible) {
            tickMarksVisible = new BooleanPropertyBase(_tickMarksVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ParallelCoordinatesChart.this; }
                @Override public String getName() { return "tickMarksVisible"; }
            };
        }
        return tickMarksVisible;
    }

    public Color getSelectedColor() { return null == selectedColor ? _selectedColor : selectedColor.get(); }
    public void setSelectedColor(final Color COLOR) {
        if (null == selectedColor) {
            _selectedColor = COLOR;
            redraw();
        } else {
            selectedColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> selectedColorProperty() {
        if (null == selectedColor) {
            selectedColor = new ObjectPropertyBase<Color>(_selectedColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ParallelCoordinatesChart.this; }
                @Override public String getName() { return "selectedColor"; }
            };
            _selectedColor = null;
        }
        return selectedColor;
    }

    public Color getUnselectedColor() { return null == unselectedColor ? _unselectedColor : unselectedColor.get(); }
    public void setUnselectedColor(final Color COLOR) {
        if (null == unselectedColor) {
            _unselectedColor = COLOR;
            redraw();
        } else {
            unselectedColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> unselectedColorProperty() {
        if (null == unselectedColor) {
            unselectedColor = new ObjectPropertyBase<Color>(_unselectedColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ParallelCoordinatesChart.this; }
                @Override public String getName() { return "unselectedColor"; }
            };
            _unselectedColor = null;
        }
        return unselectedColor;
    }
    
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

    
    // ******************** Drawing *******************************************
    private void drawAxis() {
        axisCtx.clearRect(0, 0, width, height);
        axisCtx.setTextBaseline(VPos.CENTER);

        int     noOfCategories   = categories.size();
        double  headerHeight     = 30;
        double  axisWidth        = 10;
        double  axisHeight       = height - headerHeight - 0.5;
        double  availableWidth   = width - axisWidth;
        double  availableHeight  = height - headerHeight;
        double  spacer           = availableWidth / (noOfCategories - 1);
        double  headerFontSize   = size * 0.025;
        double  unitFontSize     = size * 0.015;
        double  axisFontSize     = size * 0.0125;
        boolean tickMarksVisible = isTickMarksVisible();

        // Go through all categories
        for (int i = 0 ; i < noOfCategories ; i++) {
            Locale   locale           = getLocale();
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
            if (i == 0) {
                axisCtx.setTextAlign(TextAlignment.LEFT);
            } else if (i == (noOfCategories - 1)) {
                axisCtx.setTextAlign(TextAlignment.RIGHT);
            } else {
                axisCtx.setTextAlign(TextAlignment.CENTER);
            }
            axisCtx.setFill(getHeaderColor());
            axisCtx.setFont(Font.font(Helper.clamp(8, 24, headerFontSize)));
            axisCtx.fillText(category, axisX, 5);
            if (!unit.isEmpty()) {
                axisCtx.setFill(getUnitColor());
                axisCtx.setFont(Font.font(Helper.clamp(8, 24, unitFontSize)));
                axisCtx.fillText(String.join("", "[", unit, "]"), axisX, 18);
            }

            // Draw axis
            axisCtx.setStroke(getAxisColor());
            axisCtx.strokeLine(axisX, axisY, axisX, maxY);

            // TickMarks
            axisCtx.setFont(Font.font(Helper.clamp(8, 24, axisFontSize)));
            axisCtx.setFill(getTickLabelColor());
            double     tmpStep          = minorTickSpace;
            BigDecimal minorTickSpaceBD = BigDecimal.valueOf(minorTickSpace);
            BigDecimal majorTickSpaceBD = BigDecimal.valueOf(majorTickSpace);
            BigDecimal mediumCheck2     = BigDecimal.valueOf(2 * minorTickSpace);
            BigDecimal mediumCheck5     = BigDecimal.valueOf(5 * minorTickSpace);
            BigDecimal counterBD        = BigDecimal.valueOf(minValue);
            double     counter          = minValue;

            // Main Loop for tick marks and labels
            if (tickMarksVisible) {
                BigDecimal tmpStepBD = new BigDecimal(tmpStep);
                tmpStepBD = tmpStepBD.setScale(6, BigDecimal.ROUND_HALF_UP); // newScale == number of decimals taken into account
                tmpStep = tmpStepBD.doubleValue();
                for (double j = 0; Double.compare(-range - tmpStep, j) <= 0; j -= tmpStep) {
                    double fixedPosition = (counter - minValue) * stepSize + headerHeight;
                    double innerPointX   = axisX - 3;
                    double innerPointY   = fixedPosition;
                    double outerPointX   = axisX + 3;
                    double outerPointY   = fixedPosition;

                    if (Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(majorTickSpaceBD).doubleValue(), 0.0) == 0) {
                        // Draw major tick mark
                        axisCtx.setStroke(Color.BLACK);
                        axisCtx.setLineWidth(1);
                        axisCtx.strokeLine(innerPointX, innerPointY, outerPointX, outerPointY);

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
                            axisCtx.setTextAlign(TextAlignment.RIGHT);
                            axisCtx.fillText(String.format(locale, formatString, axisValue), axisX - 5, outerPointY + offsetY);
                        } else {
                            axisCtx.setTextAlign(TextAlignment.LEFT);
                            axisCtx.fillText(String.format(locale, formatString, axisValue), axisX + 5, outerPointY + offsetY);
                        }
                    } else if (Double.compare(minorTickSpaceBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(mediumCheck2).doubleValue(), 0.0) != 0.0 &&
                               Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(mediumCheck5).doubleValue(), 0.0) == 0.0) {
                        // Draw medium tick mark
                        axisCtx.strokeLine(innerPointX + 1, innerPointY, outerPointX - 1, outerPointY);
                    } /*else if (Double.compare(counterBD.setScale(12, BigDecimal.ROUND_HALF_UP).remainder(minorTickSpaceBD).doubleValue(), 0.0) == 0) {
                    // Draw minor tick mark
                    axisCtx.strokeLine(innerPointX + 2, innerPointY, outerPointX - 2, outerPointY);
                }*/

                    counterBD = counterBD.add(minorTickSpaceBD);
                    counter = counterBD.doubleValue();
                    if (counter > maxValue) break;
                }
            } else {
                // Min
                axisCtx.strokeLine(axisX - 3, maxY, axisX + 3, maxY);

                // Max
                axisCtx.strokeLine(axisX - 3, axisY, axisX + 3, axisY);

                axisCtx.setFont(Font.font(Helper.clamp(8, 24, axisFontSize)));
                axisCtx.setFill(Color.BLACK);
                if (i == (noOfCategories - 1)) {
                    axisCtx.setTextAlign(TextAlignment.RIGHT);
                    axisCtx.fillText(String.format(locale, formatString, minValue), axisX - 5, maxY - axisFontSize);  // Min
                    axisCtx.fillText(String.format(locale, formatString, maxValue), axisX - 5, axisY + axisFontSize); // Max
                } else {
                    axisCtx.setTextAlign(TextAlignment.LEFT);
                    axisCtx.fillText(String.format(locale, formatString, minValue), axisX + 5, maxY - axisFontSize);  // Min
                    axisCtx.fillText(String.format(locale, formatString, maxValue), axisX + 5, axisY + axisFontSize); // Max
                }

            }

            categoryMap.get(category).forEach(obj -> {
                ChartItem item  = obj.getProperties().get(category);
                double    itemY = (item.getValue() - minValue) * stepSize;
                item.setX(axisX);
                item.setY(maxY - itemY);
            });
        }
    }
    
    private void drawConnections() {
        connectionCtx.clearRect(0, 0, width, height);

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
                    if (selectedItems.size() > 0) {
                        if (selectedItems.contains(e.getKey())) {
                            connectionCtx.setStroke(getSelectedColor());
                        } else {
                            connectionCtx.setStroke(getUnselectedColor());
                        }
                    } else {
                        connectionCtx.setStroke(e.getKey().getStroke());
                    }
                    connectionCtx.beginPath();
                    for (Double[] xy : e.getValue()) { connectionCtx.lineTo(xy[0], xy[1]); }
                    connectionCtx.stroke();
                });
            }
        });
    }
    

    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            axisCanvas.setWidth(width);
            axisCanvas.setHeight(height);
            axisCanvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            connectionCanvas.setWidth(width);
            connectionCanvas.setHeight(height);
            connectionCanvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);
            

            redraw();
        }
    }

    private void redraw() {
        drawAxis();
        drawConnections();
    }
}
