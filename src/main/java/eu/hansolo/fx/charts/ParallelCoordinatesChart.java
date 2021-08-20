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
import eu.hansolo.fx.charts.event.ChartEvent;
import eu.hansolo.fx.charts.event.ChartEventListener;
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.CtxBounds;
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
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


@DefaultProperty("children")
public class ParallelCoordinatesChart extends Region {
    private static final double                                   PREFERRED_WIDTH    = 600;
    private static final double                                   PREFERRED_HEIGHT   = 400;
    private static final double                                   MINIMUM_WIDTH      = 50;
    private static final double                                   MINIMUM_HEIGHT     = 50;
    private static final double                                   MAXIMUM_WIDTH      = 2048;
    private static final double                                   MAXIMUM_HEIGHT     = 2048;
    private static final double                                   HEADER_HEIGHT      = 30;
    private static final double                                   AXIS_WIDTH         = 10;
    private static final double                                   MAJOR_TICK_LENGTH  = 6;
    private static final double                                   MEDIUM_TICK_LENGTH = 4;
    private        final ChartEvent                               SELECTION_EVENT    = new ChartEvent(eu.hansolo.fx.charts.event.EventType.SELECTED);
    private              double                                   size;
    private              double                                   width;
    private              double                                   height;
    private              Canvas                                   axisCanvas;
    private              GraphicsContext                          axisCtx;
    private              Canvas                                   connectionCanvas;
    private              GraphicsContext                          connectionCtx;
    private              Color                                    _axisColor;
    private              ObjectProperty<Color>                    axisColor;
    private              Color                                    _headerColor;
    private              ObjectProperty<Color>                    headerColor;
    private              Color                                    _unitColor;
    private              ObjectProperty<Color>                    unitColor;
    private              Color                                    _tickLabelColor;
    private              ObjectProperty<Color>                    tickLabelColor;
    private              Locale                                   _locale;
    private              ObjectProperty<Locale>                   locale;
    private              int                                      _decimals;
    private              IntegerProperty                          decimals;
    private              boolean                                  _tickMarksVisible;
    private              BooleanProperty                          tickMarksVisible;
    private              Color                                    _selectedColor;
    private              ObjectProperty<Color>                    selectedColor;
    private              Color                                    _unselectedColor;
    private              ObjectProperty<Color>                    unselectedColor;
    private              Color                                    _selectionRectColor;
    private              ObjectProperty<Color>                    selectionRectColor;
    private              boolean                                  _smoothConnections;
    private              BooleanProperty                          smoothConnections;
    private              String                                   formatString;
    private              String                                   selectedCategory;
    private              String                                   selectionRectCategory;
    private              double                                   selectionStartX;
    private              double                                   selectionStartY;
    private              double                                   selectionEndY;
    private              CtxBounds                                selectionRect;
    private              Map<String, ChartItem>                   selectedItems;
    private              Set<DataObject>                          selectedObjects;
    private              ObservableList<DataObject>               items;
    private              ArrayList<String>                        categories;
    private              Map<String, List<DataObject>>            categoryObjectMap;
    private              Map<Key, ChartItem>                      categoryObjectItemMap;
    private              ItemEventListener                        itemListener;
    private              ListChangeListener<DataObject>           objectListListener;
    private              EventHandler<MouseEvent>                 mouseHandler;
    private              Rectangle                                rect;
    private              Text                                     dragText;
    private              boolean                                  wasDragged;
    private              CopyOnWriteArrayList<ChartEventListener> listeners;


    // ******************** Constructors **************************************
    public ParallelCoordinatesChart() {
        _axisColor            = Color.BLACK;
        _headerColor          = Color.BLACK;
        _unitColor            = Color.BLACK;
        _tickLabelColor       = Color.BLACK;
        _locale               = Locale.US;
        _decimals             = 0;
        _tickMarksVisible     = true;
        _selectedColor        = Color.BLUE;
        _unselectedColor      = Color.LIGHTGRAY;
        _selectionRectColor   = Color.BLUE;
        _smoothConnections    = false;
        selectionRectCategory = "";
        formatString          = new StringBuilder("%.").append(_decimals).append("f").toString();
        selectedItems         = new HashMap<>();
        selectedObjects       = new LinkedHashSet<>();
        selectionRect         = new CtxBounds();
        items                 = FXCollections.observableArrayList();
        itemListener          = e -> redraw();
        objectListListener    = c -> {
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
        categories            = new ArrayList<>();
        categoryObjectMap     = new HashMap<>();
        categoryObjectItemMap = new HashMap<>();
        wasDragged            = false;
        mouseHandler          = e -> handleMouseEvent(e);
        listeners             = new CopyOnWriteArrayList<>();

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

        axisCanvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        axisCtx    = axisCanvas.getGraphicsContext2D();

        Color selectionRectColor = getSelectionRectColor();
        rect = new Rectangle();
        rect.setMouseTransparent(true);
        rect.setVisible(false);
        rect.setStroke(Helper.getColorWithOpacity(selectionRectColor, 0.5));
        rect.setFill(Helper.getColorWithOpacity(selectionRectColor, 0.25));

        connectionCanvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        connectionCanvas.setMouseTransparent(true);
        connectionCtx = connectionCanvas.getGraphicsContext2D();
        connectionCtx.setTextAlign(TextAlignment.LEFT);
        connectionCtx.setTextBaseline(VPos.CENTER);

        dragText = new Text("");
        dragText.setVisible(false);
        dragText.setTextOrigin(VPos.CENTER);
        dragText.setFill(Helper.getColorWithOpacity(getHeaderColor(), 0.5));


        getChildren().setAll(axisCanvas, rect, connectionCanvas, dragText);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        items.addListener(objectListListener);
        axisCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        axisCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseHandler);
        axisCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
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

    public void dispose() {
        items.forEach(object -> object.getProperties().values().forEach(item -> item.removeItemEventListener(itemListener)));
        axisCanvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        axisCanvas.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseHandler);
        axisCanvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
    }

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
            dragText.setFill(Helper.getColorWithOpacity(_headerColor, 0.5));
            redraw();
        } else {
            headerColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> headerColorProperty() {
        if (null == headerColor) {
            headerColor = new ObjectPropertyBase<Color>(_headerColor) {
                @Override protected void invalidated() {
                    dragText.setFill(Helper.getColorWithOpacity(get(), 0.5));
                    redraw();
                }
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

    public Color getSelectionRectColor() { return null == selectionRectColor ? _selectionRectColor : selectionRectColor.get(); }
    public void setSelectionRectColor(final Color COLOR) {
        if (null == selectionRectColor) {
            _selectionRectColor = COLOR;
            rect.setStroke(Helper.getColorWithOpacity(_selectionRectColor, 0.5));
            rect.setFill(Helper.getColorWithOpacity(_selectionRectColor, 0.25));
            redraw();
        } else {
            selectionRectColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> selectionRectColorProperty() {
        if (null == selectionRectColor) {
            selectionRectColor = new ObjectPropertyBase<Color>(_selectionRectColor) {
                @Override protected void invalidated() {
                    rect.setStroke(Helper.getColorWithOpacity(get(), 0.5));
                    rect.setFill(Helper.getColorWithOpacity(get(), 0.25));
                    redraw();
                }
                @Override public Object getBean() { return ParallelCoordinatesChart.this; }
                @Override public String getName() { return "selectionRectColor"; }
            };
            _selectionRectColor = null;
        }
        return selectionRectColor;
    }

    public boolean getSmoothConnections() { return null == smoothConnections ? _smoothConnections : smoothConnections.get(); }
    public void setSmoothConnections(final boolean SMOOTH) {
        if (null == smoothConnections) {
            _smoothConnections = SMOOTH;
            redraw();
        } else {
            smoothConnections.set(SMOOTH);
        }
    }
    public BooleanProperty smoothConnectionsProperty() {
        if (null == smoothConnections) {
            smoothConnections = new BooleanPropertyBase(_smoothConnections) {
                @Override public Object getBean() { return ParallelCoordinatesChart.this; }
                @Override public String getName() { return "smoothConnections"; }
            };
        }
        return smoothConnections;
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

    public Set<DataObject> getSelectedObjects() { return selectedObjects; }

    public void sortCategory(final String CATEGORY, final List<DataObject> DATA_OBJECTS, final Order ORDER) {
        DATA_OBJECTS.sort(Comparator.comparingDouble(object -> object.getProperties().get(CATEGORY).getValue()));
        if (Order.DESCENDING == ORDER) { Collections.reverse(DATA_OBJECTS); }
    }

    public List<String> getCategories() { return categories; }

    public Map<String, List<DataObject>> getCategoryObjectMap() { return categoryObjectMap; }

    private double[] getMinMax(final String CATEGORY) {
        double min = categoryObjectMap.get(CATEGORY).stream().mapToDouble(obj -> obj.getProperties().get(CATEGORY).getValue()).min().getAsDouble();
        double max = categoryObjectMap.get(CATEGORY).stream().mapToDouble(obj -> obj.getProperties().get(CATEGORY).getValue()).max().getAsDouble();
        return new double[]{ min, max };
    }

    private void prepareData() {
        if (items.isEmpty()) { return; }
        categoryObjectMap.clear();
        List<String> keys = new ArrayList<>(items.get(0).getProperties().keySet());
        if (keys.size() <= 1) { throw new RuntimeException("You need at least 2 categories in your DataObject"); }

        keys.forEach(key -> categoryObjectMap.put(key, new ArrayList<>()));
        keys.forEach(key -> items.forEach(dataObject -> categoryObjectMap.get(key).add(dataObject)));
        keys.forEach(key -> sortCategory(key, categoryObjectMap.get(key), Order.DESCENDING));

        categories.clear();
        categories.addAll(categoryObjectMap.keySet());
    }

    private void shiftCategory(final String CATEGORY, final int INDEX) {
        if (!categories.contains(CATEGORY) ||
            INDEX == categories.indexOf(CATEGORY) ||
            INDEX < 0 ||
            INDEX >= categories.size()) { return; }
        categories.remove(CATEGORY);
        categories.add(INDEX, CATEGORY);
    }

    private void selectObjectsAtCategory(final String CATEGORY, final double MIN_Y, final double MAX_Y) {
        selectedItems.clear();
        categoryObjectItemMap.entrySet()
                             .stream()
                             .filter(entry -> entry.getKey().getCategory().equals(CATEGORY))
                             .filter(entry -> entry.getValue().getY() > MIN_Y && entry.getValue().getY() < MAX_Y)
                             .filter(entry -> entry.getValue().getY() < MAX_Y)
                             .forEach(entry -> selectedItems.put(entry.getKey().getDataObject().getName(), entry.getValue()));

        selectedObjects.clear();
        items.forEach(obj -> categories.forEach(category -> {
            if (selectedItems.size() > 0 && selectedItems.keySet().contains(obj.getName())) {
                selectedObjects.add(obj);
            }
        }));
        if (!selectedObjects.isEmpty()) { fireChartEvent(SELECTION_EVENT); }

        if (getSmoothConnections()) {
            drawSmoothConnections();
        } else {
            drawConnections();
        }
    }

    private String selectCategory(final double X, final double Y) {
        int     noOfCategories = categories.size();
        double  availableWidth = width - AXIS_WIDTH;
        double  spacer         = availableWidth / (noOfCategories - 1);
        double  thirdSpacer    = spacer / 3;
        for (int i = 0 ; i < noOfCategories ; i++) {
            double axisX = i * spacer + AXIS_WIDTH * 0.5;
            if (i == 0 && X < axisX + thirdSpacer) {
                selectionStartX = axisX - 5;
                return categories.get(i);
            } else if (X > axisX - thirdSpacer && X < axisX + thirdSpacer) {
                selectionStartX = axisX - 5;
                return categories.get(i);
            }
        }
        return null;
    }

    private double getAxisXOfCategory(final String CATEGORY) {
        int     noOfCategories = categories.size();
        double  availableWidth = width - AXIS_WIDTH;
        double  spacer         = availableWidth / (noOfCategories - 1);
        for (int i = 0 ; i < noOfCategories ; i++) {
            double axisX = i * spacer + AXIS_WIDTH * 0.5;
            if (categories.get(i).equals(CATEGORY)) { return axisX; }
        }
        return -1;
    }

    private void resizeSelectionRect() {
        rect.setX(selectionRect.getX());
        rect.setY(selectionRect.getY());
        rect.setWidth(selectionRect.getWidth());
        rect.setHeight(selectionRect.getHeight());
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

    private void handleMouseEvent(final MouseEvent EVT) {
        final EventType<? extends MouseEvent> TYPE = EVT.getEventType();
        final double                          X    = EVT.getX();
        final double                          Y    = EVT.getY();

        if (MouseEvent.MOUSE_PRESSED.equals(TYPE)) {
            selectedCategory = selectCategory(X, Y);
            selectionStartY  = null == selectedCategory ? -1 : Y;
            if (selectionStartY >= HEADER_HEIGHT) {
                selectedObjects.clear();
                selectionRectCategory = selectedCategory;
                selectionRect.setX(selectionStartX);
                selectionRect.setY(Y);
                selectionRect.setWidth(0);
                selectionRect.setHeight(0);
                rect.setVisible(true);
                resizeSelectionRect();
            } else {
                rect.setVisible(false);
                /*
                selectionRect.setX(0);
                selectionRect.setY(0);
                selectionRect.setWidth(0);
                selectionRect.setHeight(0);
                resizeSelectionRect();
                */
                dragText.setVisible(true);
                dragText.setText(selectedCategory);
                dragText.setX(X - dragText.getLayoutBounds().getWidth() * 0.5);
                dragText.setY(Y);
            }
            wasDragged = false;
        } else if (MouseEvent.MOUSE_DRAGGED.equals(TYPE)) {
            if (rect.isVisible()) {
                selectionRect.setHeight(Helper.clamp(selectionStartY, height - 0.5, Y) - selectionRect.getY());
                selectionRect.setWidth(AXIS_WIDTH);
                resizeSelectionRect();
            } else if (dragText.isVisible()) {
                dragText.setX(X - dragText.getLayoutBounds().getWidth() * 0.5);
                dragText.setY(Y);
            }
            wasDragged = true;
        } else if (MouseEvent.MOUSE_RELEASED.equals(TYPE)) {
            if (dragText.isVisible() && wasDragged) {
                dragText.setVisible(false);
                String targetCategory = selectCategory(X, Y);
                if (null != targetCategory) {
                    shiftCategory(dragText.getText(), categories.indexOf(targetCategory));
                    selectionRect.setX(getAxisXOfCategory(selectionRectCategory) - AXIS_WIDTH * 0.5);
                    redraw();
                }
            } else if (rect.isVisible() && wasDragged) {
                selectionEndY = null == selectedCategory ? -1 : Helper.clamp(selectionStartY, height - 0.5, Y);
                if (selectionStartY > HEADER_HEIGHT && selectionEndY > -1) {
                    selectionRect.setWidth(10);
                    selectionRect.setY(selectionStartY);
                    selectionRect.setHeight(selectionEndY - selectionStartY);
                    selectObjectsAtCategory(selectedCategory, selectionStartY, selectionEndY);
                } else {
                    selectedItems.clear();
                }
            } else {
                selectedItems.clear();
                if (getSmoothConnections()) {
                    drawSmoothConnections();
                } else {
                    drawConnections();
                }
            }
            wasDragged = false;
        }
    }


    // ******************** Event Handling ************************************
    public void setOnChartEvent(final ChartEventListener LISTENER) { addChartEventListener(LISTENER); }
    public void addChartEventListener(final ChartEventListener LISTENER) { if (!listeners.contains(LISTENER)) listeners.add(LISTENER); }
    public void removeChartEventListener(final ChartEventListener LISTENER) { if (listeners.contains(LISTENER)) listeners.remove(LISTENER); }
    public void removeAllChartEventListeners() { listeners.clear(); }

    public void fireChartEvent(final ChartEvent EVENT) {
        for (ChartEventListener listener : listeners) { listener.onChartEvent(EVENT); }
    }


    
    // ******************** Drawing *******************************************
    private void redraw() {
        drawAxis();
        if (getSmoothConnections()) {
            drawSmoothConnections();
        } else {
            drawConnections();
        }
    }

    private void drawAxis() {
        axisCtx.clearRect(0, 0, width, height);
        axisCtx.setTextBaseline(VPos.CENTER);

        int     noOfCategories   = categories.size();
        double  availableWidth   = width - AXIS_WIDTH;
        double  availableHeight  = height - HEADER_HEIGHT - 0.5;
        double  axisHeight       = height - HEADER_HEIGHT - 0.5;
        double  halfAxisWidth    = AXIS_WIDTH * 0.5;
        double  spacer           = availableWidth / (noOfCategories - 1);
        double  headerFontSize   = size * 0.025;
        double  unitFontSize     = size * 0.015;
        double  axisFontSize     = size * 0.0125;
        boolean tickMarksVisible = isTickMarksVisible();

        // Go through all categories
        for (int i = 0 ; i < noOfCategories ; i++) {
            Locale   locale               = getLocale();
            String   category             = categories.get(i);
            String   unit                 = categoryObjectMap.get(category).get(0).getProperties().get(category).getUnit();
            double   axisX                = i * spacer + AXIS_WIDTH * 0.5;
            double   axisY                = HEADER_HEIGHT;
            double   halfMajorTickLength  = MAJOR_TICK_LENGTH * 0.5;
            double   halfMediumTickLength = MEDIUM_TICK_LENGTH * 0.5;
            double[] minMax               = getMinMax(category);
            double[] axisParam            = calcAutoScale(minMax[0], minMax[1]);
            double   minValue             = axisParam[0];
            double   maxValue             = axisParam[1];
            double   range                = maxValue - minValue;
            double   minorTickSpace       = axisParam[2];
            double   majorTickSpace       = axisParam[3];
            Font     headerFont           = Fonts.opensansRegular(Helper.clamp(8, 24, headerFontSize));

            double   stepSize             = Math.abs(axisHeight / range);
            double   maxY                 = axisY + axisHeight;

            // Draw header and unit
            dragText.setFont(headerFont);

            if (i == 0) {
                axisCtx.setTextAlign(TextAlignment.LEFT);
            } else if (i == (noOfCategories - 1)) {
                axisCtx.setTextAlign(TextAlignment.RIGHT);
            } else {
                axisCtx.setTextAlign(TextAlignment.CENTER);
            }
            axisCtx.setFill(getHeaderColor());
            axisCtx.setFont(headerFont);
            axisCtx.fillText(category, axisX, 5);
            if (!unit.isEmpty()) {
                axisCtx.setFill(getUnitColor());
                axisCtx.setFont(Fonts.opensansRegular(Helper.clamp(8, 24, unitFontSize)));
                axisCtx.fillText(String.join("", "[", unit, "]"), axisX, 18);
            }

            // Draw axis
            axisCtx.setStroke(getAxisColor());
            axisCtx.strokeLine(axisX, axisY, axisX, maxY);

            // TickMarks
            axisCtx.setFont(Fonts.opensansRegular(Helper.clamp(8, 24, axisFontSize)));
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
                tmpStepBD = tmpStepBD.setScale(6, RoundingMode.HALF_UP); // newScale == number of decimals taken into account
                tmpStep = tmpStepBD.doubleValue();
                for (double j = 0; Double.compare(-range - tmpStep, j) <= 0; j -= tmpStep) {
                    double fixedPosition = (counter - minValue) * stepSize + HEADER_HEIGHT;
                    double innerPointX   = axisX - halfMajorTickLength;
                    double innerPointY   = fixedPosition;
                    double outerPointX   = axisX + halfMajorTickLength;
                    double outerPointY   = fixedPosition;

                    if (Double.compare(counterBD.setScale(12, RoundingMode.HALF_UP).remainder(majorTickSpaceBD).doubleValue(), 0.0) == 0) {
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
                            axisCtx.fillText(String.format(locale, formatString, axisValue), axisX - halfAxisWidth, outerPointY + offsetY);
                        } else {
                            axisCtx.setTextAlign(TextAlignment.LEFT);
                            axisCtx.fillText(String.format(locale, formatString, axisValue), axisX + halfAxisWidth, outerPointY + offsetY);
                        }
                    } else if (Double.compare(minorTickSpaceBD.setScale(12, RoundingMode.HALF_UP).remainder(mediumCheck2).doubleValue(), 0.0) != 0.0 &&
                               Double.compare(counterBD.setScale(12, RoundingMode.HALF_UP).remainder(mediumCheck5).doubleValue(), 0.0) == 0.0) {
                        // Draw medium tick mark
                        axisCtx.strokeLine(axisX - halfMediumTickLength, innerPointY, axisX + halfMediumTickLength, outerPointY);
                    }

                    counterBD = counterBD.add(minorTickSpaceBD);
                    counter = counterBD.doubleValue();
                    if (counter > maxValue) break;
                }
            } else {
                // Min
                axisCtx.strokeLine(axisX - 3, maxY, axisX + 3, maxY);

                // Max
                axisCtx.strokeLine(axisX - 3, axisY, axisX + 3, axisY);

                axisCtx.setFont(Fonts.opensansRegular(Helper.clamp(8, 24, axisFontSize)));
                axisCtx.setFill(Color.BLACK);
                if (i == (noOfCategories - 1)) {
                    axisCtx.setTextAlign(TextAlignment.RIGHT);
                    axisCtx.fillText(String.format(locale, formatString, minValue), axisX - halfAxisWidth, maxY - axisFontSize);  // Min
                    axisCtx.fillText(String.format(locale, formatString, maxValue), axisX - halfAxisWidth, axisY + axisFontSize); // Max
                } else {
                    axisCtx.setTextAlign(TextAlignment.LEFT);
                    axisCtx.fillText(String.format(locale, formatString, minValue), axisX + halfAxisWidth, maxY - axisFontSize);  // Min
                    axisCtx.fillText(String.format(locale, formatString, maxValue), axisX + halfAxisWidth, axisY + axisFontSize); // Max
                }

            }

            categoryObjectMap.get(category).forEach(obj -> {
                ChartItem item  = obj.getProperties().get(category);
                double    itemY = (item.getValue() - minValue) * stepSize;
                item.setX(axisX);
                item.setY(maxY - itemY);
                Key key = new Key(category, obj);
                categoryObjectItemMap.put(key, item);
            });
        }
    }
    
    private void drawConnections() {
        connectionCtx.clearRect(0, 0, width, height);
        connectionCtx.setFont(Fonts.opensansRegular(Helper.clamp(8, 24, size * 0.015)));

        int   noOfCategories  = categories.size();
        Color selectedColor   = getSelectedColor();
        Color unselectedColor = getUnselectedColor();
        items.forEach(obj -> {
            Color objStroke = obj.getStroke();
            Key       key   = new Key(categories.get(0), obj);
            ChartItem item  = categoryObjectItemMap.get(key); // Grab the first point
            double    lastX = item.getX();
            double    lastY = item.getY();
            // Loop through the remaining points
            for (int i = 1 ; i < noOfCategories ; i++) {
                String category = categories.get(i);
                key  = new Key(category, obj);
                item = categoryObjectItemMap.get(key);

                if (selectedItems.size() > 0) {
                    connectionCtx.setStroke(selectedItems.keySet().contains(obj.getName()) ? selectedColor : unselectedColor);
                    if (selectedItems.keySet().contains(obj.getName()) && category.equals(categories.get(1))) {
                        connectionCtx.fillText(obj.getName(), 10, lastY);
                    } } else {
                    connectionCtx.setStroke(objStroke);
                }
                connectionCtx.strokeLine(lastX, lastY, item.getX(), item.getY());
                lastX = item.getX();
                lastY = item.getY();
            }
        });
        if (selectedItems.size() > 0) {
            resizeSelectionRect();
            rect.setVisible(true);
        } else {
            rect.setVisible(false);
        }
    }

    private void drawSmoothConnections() {
        int     noOfCategories = categories.size();
        double  availableWidth = width - AXIS_WIDTH;
        double  spacer         = availableWidth / (noOfCategories - 1);

        connectionCtx.clearRect(0, 0, width, height);
        connectionCtx.setFont(Fonts.opensansRegular(Helper.clamp(8, 24, size * 0.015)));

        Color selectedColor   = getSelectedColor();
        Color unselectedColor = getUnselectedColor();
        for (DataObject obj : items) {
            Color     objStroke     = obj.getStroke();
            String    firstCategory = categories.get(0);
            Key       firstKey      = new Key(firstCategory, obj);
            ChartItem firstItem     = categoryObjectItemMap.get(firstKey);
            connectionCtx.beginPath();
            connectionCtx.moveTo(firstItem.getX(), firstItem.getY());
            for (int i = 1 ; i < noOfCategories ; i++) {
                String    lastCategory = categories.get(i - 1);
                String    category     = categories.get(i);
                Key       key          = new Key(category, obj);
                Key       lastKey      = new Key(lastCategory, obj);
                ChartItem item         = categoryObjectItemMap.get(key);
                ChartItem lastItem     = categoryObjectItemMap.get(lastKey);
                if (selectedItems.size() > 0) {
                    connectionCtx.setStroke(selectedItems.keySet().contains(obj.getName()) ? selectedColor : unselectedColor);
                    if (selectedItems.keySet().contains(obj.getName()) && category.equals(categories.get(1))) {
                        connectionCtx.fillText(obj.getName(), 10, lastItem.getY());
                    }
                } else {
                    connectionCtx.setStroke(objStroke);
                }
                connectionCtx.bezierCurveTo(lastItem.getX() + spacer * 0.25, lastItem.getY(),item.getX() - spacer * 0.25, item.getY(), item.getX(), item.getY());
            }
            connectionCtx.stroke();
        }
        if (selectedItems.size() > 0) {
            resizeSelectionRect();
            rect.setVisible(true);
        } else {
            rect.setVisible(false);
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            double rectXFactor = selectionRect.getX() / connectionCanvas.getWidth();
            double rectYFactor = selectionRect.getY() / connectionCanvas.getHeight();
            double rectWFactor = selectionRect.getWidth() / connectionCanvas.getWidth();
            double rectHFactor = selectionRect.getHeight() / connectionCanvas.getHeight();

            axisCanvas.setWidth(width);
            axisCanvas.setHeight(height);
            axisCanvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            connectionCanvas.setWidth(width);
            connectionCanvas.setHeight(height);
            connectionCanvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            selectionRect.setX(width * rectXFactor);
            selectionRect.setY(height * rectYFactor);
            selectionRect.setWidth(width * rectWFactor);
            selectionRect.setHeight(height * rectHFactor);

            redraw();
        }
    }


    // ******************** InnerClasses **************************************
    private class Key {
        private String     category;
        private DataObject dataObject;


        // ******************** Constructors **********************************
        public Key(final String CATEGORY, final DataObject DATA_OBJECT) {
            category   = CATEGORY;
            dataObject = DATA_OBJECT;
        }


        // ******************** Methods ***************************************
        public String getCategory() { return category; }

        public DataObject getDataObject() { return dataObject; }

        @Override public boolean equals(final Object OBJ) {
            if (!(OBJ instanceof Key)) { return false; }
            Key ref = (Key) OBJ;
            return category.equals(ref.getCategory()) && dataObject.equals(ref.getDataObject());
        }

        @Override public int hashCode() { return category.hashCode() ^ dataObject.hashCode(); }
    }
}
