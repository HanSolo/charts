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

package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.DayOfWeekCategory;
import eu.hansolo.fx.charts.data.MonthCategory;
import eu.hansolo.fx.charts.event.SeriesEventListener;
import eu.hansolo.toolboxfx.font.Fonts;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.series.Series;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.InfoPopup;
import eu.hansolo.fx.geometry.Rectangle;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


/**
 * User: hansolo
 * Date: 14.11.21
 * Time: 13:33
 */
@DefaultProperty("children")
public class PanelBarChart extends Region {
    private static final double                                         PREFERRED_WIDTH  = 600;
    private static final double                                         PREFERRED_HEIGHT = 400;
    private static final double                                         MINIMUM_WIDTH    = 50;
    private static final double                                         MINIMUM_HEIGHT   = 50;
    private static final double                                         MAXIMUM_WIDTH    = 2048;
    private static final double                                         MAXIMUM_HEIGHT   = 2048;
    private              double                                         width;
    private              double                                         height;
    private              Canvas                                         canvas;
    private              GraphicsContext                                ctx;
    private              Pane                                           pane;
    private              ObservableList<ChartItemSeries<ChartItem>>     listOfSeries;
    private              ObservableList<ChartItemSeries<ChartItem>>     comparisonListOfSeries;
    private              SeriesEventListener                            seriesEvtListener;
    private              ObservableList<? extends Category>             categories;
    private              Locale                                         _locale;
    private              ObjectProperty<Locale>                         locale;
    private              Paint                                          _chartBackground;
    private              ObjectProperty<Paint>                          chartBackground;
    private              Color                                          _categoryNameColor;
    private              ObjectProperty<Color>                          categoryNameColor;
    private              String                                         _name;
    private              StringProperty                                 name;
    private              Color                                          _nameColor;
    private              ObjectProperty<Color>                          nameColor;
    private              Color                                          _seriesNameColor;
    private              ObjectProperty<Color>                          seriesNameColor;
    private              Color                                          _categorySumColor;
    private              ObjectProperty<Color>                          categorySumColor;
    private              Color                                          _seriesSumColor;
    private              ObjectProperty<Color>                          seriesSumColor;
    private              boolean                                        _comparisonEnabled;
    private              BooleanProperty                                comparisonEnabled;
    private              String                                         _comparisonName;
    private              StringProperty                                 comparisonName;
    private              Color                                          _comparisonNameColor;
    private              ObjectProperty<Color>                          comparisonNameColor;
    private              Color                                          _comparisonSeriesNameColor;
    private              ObjectProperty<Color>                          comparisonSeriesNameColor;
    private              Color                                          _comparisonCategorySumColor;
    private              ObjectProperty<Color>                          comparisonCategorySumColor;
    private              Color                                          _comparisonSeriesSumColor;
    private              ObjectProperty<Color>                          comparisonSeriesSumColor;
    private              Color                                          _gridColor;
    private              ObjectProperty<Color>                          gridColor;
    private              boolean                                        _colorByCategory;
    private              BooleanProperty                                colorByCategory;
    private              Map<Category, Double>                          sumsPerCategory;
    private              Map<Category, Double>                          comparisonSumsPerCategory;
    private              Map<Rectangle, ChartItem>                      itemMap;
    private              Map<Rectangle, ChartItem>                      comparisonItemMap;
    private              EventHandler<MouseEvent>                       mouseHandler;
    private              ListChangeListener<Category>                   categoryListener;
    private              ListChangeListener<ChartItemSeries<ChartItem>> seriesListener;
    private              InfoPopup                                      popup;


    // ******************** Constructors **************************************
    public PanelBarChart(final List<? extends Category> categories) {
        this(categories, new ArrayList<>());
    }
    public PanelBarChart(final List<? extends Category> categories, final ChartItemSeries<ChartItem>... series) {
        this(categories, Arrays.asList(series));
    }
    public PanelBarChart(final List<? extends Category> categories, final List<ChartItemSeries<ChartItem>> series) {
        this.listOfSeries                = FXCollections.observableArrayList(series);
        this.comparisonListOfSeries      = FXCollections.observableArrayList();
        this.seriesEvtListener           = evt -> redraw();
        this.categories                  = FXCollections.observableArrayList(categories);
        this._locale                     = Locale.getDefault();
        this._chartBackground            = Color.TRANSPARENT;
        this._categoryNameColor          = Color.BLACK;
        this._seriesNameColor            = Color.BLACK;
        this._categorySumColor           = Color.BLACK;
        this._seriesSumColor             = Color.BLACK;
        this._comparisonSeriesNameColor  = Color.BLACK;
        this._comparisonCategorySumColor = Color.BLACK;
        this._comparisonSeriesSumColor   = Color.BLACK;
        this._gridColor                  = Color.LIGHTGRAY;
        this._colorByCategory            = false;
        this._comparisonEnabled          = false;
        this._name                       = "";
        this._nameColor                  = Color.BLACK;
        this._comparisonName             = "";
        this._comparisonNameColor        = Color.BLACK;
        this.sumsPerCategory             = new ConcurrentHashMap<>();
        this.comparisonSumsPerCategory   = new ConcurrentHashMap<>();
        this.itemMap                     = new ConcurrentHashMap<>();
        this.comparisonItemMap           = new ConcurrentHashMap<>();
        this.mouseHandler                = e -> handleMouseEvent(e);
        this.categoryListener            = c -> redraw();
        this.seriesListener              = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(series1 -> series1.addSeriesEventListener(seriesEvtListener));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(series1 -> series1.removeSeriesEventListener(seriesEvtListener));
                }
            }
            recalc();
            redraw();
        };
        this.popup                       = new InfoPopup();
        recalc();
        initGraphics();
        registerListeners();
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

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        pane = new Pane(canvas);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        categories.addListener(categoryListener);
        listOfSeries.addListener(seriesListener);
        comparisonListOfSeries.addListener(seriesListener);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public ObservableList<? extends Category> getCategories() { return categories; }

    public ObservableList<ChartItemSeries<ChartItem>> getListOfSeries() { return listOfSeries; }
    public void setListOfSeries(final ChartItemSeries<ChartItem>... arrayOfSeries) {
        setListOfSeries(Arrays.asList(arrayOfSeries));
    }
    public void setListOfSeries(final List<ChartItemSeries<ChartItem>> listOfSeries) { this.listOfSeries.setAll(listOfSeries); }

    public void addSeries(final ChartItemSeries<ChartItem> series) {
        if (listOfSeries.contains(series)) { return; }
        listOfSeries.add(series);
    }

    public void removeSeries(final Series<ChartItem> series) {
        if (!listOfSeries.contains(series)) { return; }
        listOfSeries.remove(series);
    }

    public ObservableList<ChartItemSeries<ChartItem>> getComparisonListOfSeries() { return comparisonListOfSeries; }
    public void setComparisonListOfSeries(final ChartItemSeries<ChartItem>... arrayOfSeries) { setComparisonListOfSeries(Arrays.asList(arrayOfSeries)); }
    public void setComparisonListOfSeries(final List<ChartItemSeries<ChartItem>> listOfOfSeries) { this.comparisonListOfSeries.setAll(listOfOfSeries); }

    public void addComparisonSeries(final ChartItemSeries<ChartItem> series) {
        if (comparisonListOfSeries.contains(series)) { return; }
        comparisonListOfSeries.add(series);
    }

    public void removeComparisonSeries(final ChartItemSeries<ChartItem> series) {
        if (comparisonListOfSeries.contains(series)) {
            comparisonListOfSeries.remove(series);
        }
    }

    public Locale getLocale() { return null == locale ? _locale : locale.get(); }
    public void setLocale(final Locale locale) {
        if (null == this.locale) {
            _locale = locale;
            redraw();
        } else {
            this.locale.set(locale);
        }
    }
    public ObjectProperty<Locale> localeProperty() {
        if (null == locale) {
            locale = new ObjectPropertyBase<>(_locale) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "locale"; }
            };
            _locale = null;
        }
        return locale;
    }

    public Paint getChartBackground() { return null == chartBackground ? _chartBackground : chartBackground.get(); }
    public void setChartBackground(final Paint chartBackground) {
        if (null == this.chartBackground) {
            _chartBackground = chartBackground;
            redraw();
        } else {
            this.chartBackground.set(chartBackground);
        }
    }
    public ObjectProperty<Paint> chartBackgroundProperty() {
        if (null == chartBackground) {
            chartBackground = new ObjectPropertyBase<>(_chartBackground) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "chartBackground"; }
            };
            _chartBackground = null;
        }
        return chartBackground;
    }

    public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String name) {
        if (null == this.name) {
            _name = name;
            redraw();
        } else {
            this.name.set(name);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    public Color getNameColor() { return null == nameColor ? _nameColor : nameColor.get(); }
    public void setNameColor(final Color color) {
        if (null == this.nameColor) {
            _nameColor = color;
            redraw();
        } else {
            this.nameColor.set(color);
        }
    }
    public ObjectProperty<Color> nameColorProperty() {
        if (null == nameColor) {
            nameColor = new ObjectPropertyBase<>(_nameColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "nameColor"; }
            };
            _nameColor = null;
        }
        return nameColor;
    }

    public Color getCategoryNameColor() { return null == categoryNameColor ? _categoryNameColor : categoryNameColor.get(); }
    public void setCategoryNameColor(final Color color) {
        if (null == this.categoryNameColor) {
            _categoryNameColor = color;
            redraw();
        } else {
            this.categoryNameColor.set(color);
        }
    }
    public ObjectProperty<Color> categoryNameColorProperty() {
        if (null == categoryNameColor) {
            categoryNameColor = new ObjectPropertyBase<>(_categoryNameColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "categoryNameColor"; }
            };
            _categoryNameColor = null;
        }
        return categoryNameColor;
    }

    public Color getSeriesNameColor() { return null == seriesNameColor ? _seriesNameColor : seriesNameColor.get(); }
    public void setSeriesNameColor(final Color color) {
        if (null == this.seriesNameColor) {
            _seriesNameColor = color;
            redraw();
        } else {
            this.seriesNameColor.set(color);
        }
    }
    public ObjectProperty<Color> seriesNameColorProperty() {
        if (null == seriesNameColor) {
            seriesNameColor = new ObjectPropertyBase<>(_seriesNameColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "seriesNameColor"; }
            };
            _seriesNameColor = null;
        }
        return seriesNameColor;
    }

    public Color getCategorySumColor() { return null == categorySumColor ? _categorySumColor : categorySumColor.get(); }
    public void setCategorySumColor(final Color color) {
        if (null == categorySumColor) {
            _categorySumColor = color;
            redraw();
        } else {
            categorySumColor.set(color);
        }
    }
    public ObjectProperty<Color> categorySumColorProperty() {
        if (null == categorySumColor) {
            categorySumColor = new ObjectPropertyBase<>(_categorySumColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "categorySumColor"; }
            };
            _categorySumColor = null;
        }
        return categorySumColor;
    }

    public Color getSeriesSumColor() { return null == seriesSumColor ? _seriesSumColor : seriesSumColor.get(); }
    public void setSeriesSumColor(final Color color) {
        if (null == seriesSumColor) {
            _seriesSumColor = color;
            redraw();
        } else {
            seriesSumColor.set(color);
        }
    }
    public ObjectProperty<Color> seriesSumColorProperty() {
        if (null == seriesSumColor) {
            seriesSumColor = new ObjectPropertyBase<>(_seriesSumColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "seriesSumColor"; }
            };
            _seriesSumColor = null;
        }
        return seriesSumColor;
    }

    public Color getGridColor() { return null == gridColor ? _gridColor : gridColor.get(); }
    public void setGridColor(final Color color) {
        if (null == gridColor) {
            _gridColor = color;
            redraw();
        } else {
            gridColor.set(color);
        }
    }
    public ObjectProperty<Color> gridColorProperty() {
        if (null == gridColor) {
            gridColor = new ObjectPropertyBase<>() {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "gridColor"; }
            };
            _gridColor = null;
        }
        return gridColor;
    }

    public boolean getColorByCategory() { return null == colorByCategory ? _colorByCategory : colorByCategory.get(); }
    public void setColorByCategory(final boolean colorByCategory) {
        if (null == this.colorByCategory) {
            _colorByCategory = colorByCategory;
            redraw();
        } else {
            this.colorByCategory.set(colorByCategory);
        }
    }
    public BooleanProperty colorByCategoryProperty() {
        if (null == colorByCategory) {
            colorByCategory = new BooleanPropertyBase(_colorByCategory) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "colorByCategory"; }
            };
        }
        return colorByCategory;
    }

    public boolean getComparisonEnabled() { return null == comparisonEnabled ? _comparisonEnabled : comparisonEnabled.get(); }
    public void setComparisonEnabled(final boolean comparisonEnabled) {
        if (null == this.comparisonEnabled) {
            _comparisonEnabled = comparisonEnabled;
            redraw();
        } else {
            this.comparisonEnabled.set(comparisonEnabled);
        }
    }
    public BooleanProperty comparisonEnabledProperty() {
        if (null == comparisonEnabled) {
            comparisonEnabled = new BooleanPropertyBase(_comparisonEnabled) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "comparisonEnabled"; }
            };
        }
        return comparisonEnabled;
    }

    public String getComparisonName() { return null == comparisonName ? _comparisonName : comparisonName.get(); }
    public void setComparisonName(final String name) {
        if (null == this.comparisonName) {
            _comparisonName = name;
            redraw();
        } else {
            comparisonName.set(name);
        }
    }
    public StringProperty comparisonNameProperty() {
        if (null == comparisonName) {
            comparisonName = new StringPropertyBase(_comparisonName) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "comparisonName"; }
            };
            _comparisonName = null;
        }
        return comparisonName;
    }

    public Color getComparisonNameColor() { return null == comparisonNameColor ? _comparisonNameColor : comparisonNameColor.get(); }
    public void setComparisonNameColor(final Color color) {
        if (null == this.comparisonNameColor) {
            _comparisonNameColor = color;
            redraw();
        } else {
            this.comparisonNameColor.set(color);
        }
    }
    public ObjectProperty<Color> comparisonNameColorProperty() {
        if (null == comparisonNameColor) {
            comparisonNameColor = new ObjectPropertyBase<>(_comparisonNameColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "comparisonNameColor"; }
            };
            _comparisonNameColor = null;
        }
        return comparisonNameColor;
    }

    public Color getComparisonCategorySumColor() { return null == comparisonCategorySumColor ? _comparisonCategorySumColor : comparisonCategorySumColor.get(); }
    public void setComparisonCategorySumColor(final Color color) {
        if (null == comparisonCategorySumColor) {
            _comparisonCategorySumColor = color;
            redraw();
        } else {
            comparisonCategorySumColor.set(color);
        }
    }
    public ObjectProperty<Color> comparisonCategorySumColorProperty() {
        if (null == comparisonCategorySumColor) {
            comparisonCategorySumColor = new ObjectPropertyBase<>(_comparisonCategorySumColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "comparisonCategorySumColor"; }
            };
            _comparisonCategorySumColor = null;
        }
        return comparisonCategorySumColor;
    }

    public Color getComparisonSeriesNameColor() { return null == comparisonSeriesNameColor ? _comparisonSeriesNameColor : comparisonSeriesNameColor.get(); }
    public void setComparisonSeriesNameColor(final Color color) {
        if (null == this.comparisonSeriesNameColor) {
            _comparisonSeriesNameColor = color;
            redraw();
        } else {
            this.comparisonSeriesNameColor.set(color);
        }
    }
    public ObjectProperty<Color> comparisonSeriesNameColorProperty() {
        if (null == comparisonSeriesNameColor) {
            comparisonSeriesNameColor = new ObjectPropertyBase<>(_comparisonSeriesNameColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "comparisonSeriesNameColor"; }
            };
            _comparisonSeriesNameColor = null;
        }
        return comparisonSeriesNameColor;
    }

    public Color getComparisonSeriesSumColor() { return null == comparisonSeriesSumColor ? _comparisonSeriesSumColor : comparisonSeriesSumColor.get(); }
    public void setComparisonSeriesSumColor(final Color color) {
        if (null == comparisonSeriesSumColor) {
            _comparisonSeriesSumColor = color;
            redraw();
        } else {
            comparisonSeriesSumColor.set(color);
        }
    }
    public ObjectProperty<Color> comparisonSeriesSumColorProperty() {
        if (null == comparisonSeriesSumColor) {
            comparisonSeriesSumColor = new ObjectPropertyBase<>(_comparisonSeriesSumColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return PanelBarChart.this; }
                @Override public String getName() { return "comparisonSeriesSumColor"; }
            };
            _comparisonSeriesSumColor = null;
        }
        return comparisonSeriesSumColor;
    }

    /**
     * Calling this method will render this chart/plot to a png given of the given width and height
     * @param filename The path and name of the file  /Users/hansolo/Desktop/plot.png
     * @param width The width of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @param height The height of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @return True if the procedure was successful, otherwise false
     */
    public boolean renderToImage(final String filename, final int width, final int height) {
        return Helper.renderToImage(PanelBarChart.this, width, height, filename);
    }

    public void removeAllData() {
        listOfSeries.clear();
        comparisonListOfSeries.clear();
        redraw();
    }

    public void dispose() {
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        categories.removeListener(categoryListener);
        listOfSeries.forEach(series -> series.removeSeriesEventListener(seriesEvtListener));
        listOfSeries.removeListener(seriesListener);
        comparisonListOfSeries.forEach(series -> series.removeSeriesEventListener(seriesEvtListener));
        comparisonListOfSeries.removeListener(seriesListener);
    }

    private void recalc() {
        sumsPerCategory.clear();
        comparisonSumsPerCategory.clear();
        // Calculate sums per category
        for (Category category : categories) {
            listOfSeries.stream()
                        .mapToDouble(series -> series.getItems().parallelStream().filter(item -> item.getCategory().getName().equals(category.getName())).map(ChartItem::getValue).reduce(0.0, Double::sum))
                        .forEach(seriesSum -> {
                            double sum = sumsPerCategory.getOrDefault(category, 0.0);
                            sumsPerCategory.put(category, sum + seriesSum);
                        });
            comparisonListOfSeries.stream()
                                  .mapToDouble(comparisonSeries -> comparisonSeries.getItems().parallelStream().filter(item -> item.getCategory().getName().equals(category.getName())).map(ChartItem::getValue).reduce(0.0, Double::sum))
                                  .forEach(comparisonSeriesSum -> {
                                      double comparisonSum = comparisonSumsPerCategory.getOrDefault(category, 0.0);
                                      comparisonSumsPerCategory.put(category, comparisonSum + comparisonSeriesSum);
                                  });
        }
    }

    private void handleMouseEvent(final MouseEvent evt) {
        final EventType<? extends Event> type = evt.getEventType();
        final double                     x    = evt.getX();
        final double                     y    = evt.getY();

        if (type.equals(MouseEvent.MOUSE_PRESSED)) {
            Optional<Entry<Rectangle, ChartItem>> optionalItem = itemMap.entrySet().parallelStream().filter(entry -> entry.getKey().contains(x, y)).findFirst();
            if (optionalItem.isPresent()) {
                popup.setX(evt.getScreenX());
                popup.setY(evt.getScreenY() - popup.getHeight());
                if (optionalItem.get().getValue().getDescription().isEmpty()) {
                    popup.update(optionalItem.get().getValue());
                } else {
                    popup.update(optionalItem.get().getValue(), true);
                }
                popup.animatedShow(getScene().getWindow());
            } else {
                Optional<Entry<Rectangle, ChartItem>> optionalComparisonItem = comparisonItemMap.entrySet().parallelStream().filter(entry -> entry.getKey().contains(x, y)).findFirst();
                if (optionalComparisonItem.isPresent()) {
                    popup.setX(evt.getScreenX());
                    popup.setY(evt.getScreenY() - popup.getHeight());
                    if (optionalComparisonItem.get().getValue().getDescription().isEmpty()) {
                        popup.update(optionalComparisonItem.get().getValue());
                    } else {
                        popup.update(optionalComparisonItem.get().getValue(), true);
                    }
                    popup.animatedShow(getScene().getWindow());
                }
            }
        }
    }


    // ******************** Layout *******************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
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
        ctx.clearRect(0, 0, width, height);
        ctx.setFill(getChartBackground());
        ctx.fillRect(0, 0, width, height);

        ctx.setTextAlign(TextAlignment.LEFT);
        ctx.setTextBaseline(VPos.CENTER);
        if (!listOfSeries.isEmpty()) {
            if (getComparisonEnabled() && !comparisonListOfSeries.isEmpty() && listOfSeries.size() == comparisonListOfSeries.size()) {
                // Draw items of both series
                final int     noOfSeries             = listOfSeries.size();
                final int     noOfCategories         = categories.size();
                final double  nameColumnWidth        = width * 0.125;
                final double  sumColumnWidth         = width * 0.06125;
                final double  spaceBetweenCategories = width * 0.00625;
                final double  spaceBetweenSeries     = height * 0.0075;
                final double  cellWidth              = (width - ((noOfCategories - 1) * spaceBetweenCategories) - nameColumnWidth - sumColumnWidth) / (noOfCategories);
                final double  cellHeight             = (height - ((noOfSeries + 1) * spaceBetweenSeries)) / (noOfSeries + 2);
                final double  maxCellValue           = Math.max(listOfSeries.parallelStream().max(Comparator.comparingDouble(ChartItemSeries::getMaxValue)).get().getMaxValue(), comparisonListOfSeries.parallelStream().max(Comparator.comparingDouble(ChartItemSeries::getMaxValue)).get().getMaxValue());
                final double  scaleFactorX           = cellWidth / maxCellValue;
                final double  itemHeight             = cellHeight * 0.4;
                final double  itemOffsetY            = cellHeight * 0.1;
                final double  spaceBetweenItems      = cellHeight * 0.05;
                final boolean useCategoryColor       = getColorByCategory();
                final double  completeSum            = sumsPerCategory.values().stream().reduce(0.0, (a, b) -> a + b);
                final double  comparisonCompleteSum  = comparisonSumsPerCategory.values().stream().reduce(0.0, (a, b) -> a + b);

                // Draw name and comparisonName
                ctx.setFont(Fonts.opensansSemibold(cellHeight * 0.25));
                ctx.setFill(getNameColor());
                ctx.fillText(getName(), 0, cellHeight * 0.25, cellWidth);
                ctx.setFill(getComparisonNameColor());
                ctx.fillText(getComparisonName(), 0, cellHeight * 0.75, cellWidth * 0.95);

                // Draw complete sum
                ctx.setTextAlign(TextAlignment.RIGHT);
                ctx.setFill(getCategorySumColor());
                ctx.fillText(Helper.shortenNumber((long) completeSum), width, cellHeight * 0.25, sumColumnWidth);
                ctx.setFill(getComparisonCategorySumColor());
                ctx.fillText(Helper.shortenNumber((long) comparisonCompleteSum), width, cellHeight * 0.75, sumColumnWidth * 0.95);

                // Draw series names and their sums
                for (int y = 0; y < noOfSeries; y++) {
                    final ChartItemSeries<ChartItem> series      = listOfSeries.get(y);
                    final String                     seriesName  = series.getName();
                    final String                     sumOfSeries = Helper.shortenNumber((long) series.getSumOfAllItems());
                    final double                     posY        = y * (cellHeight + spaceBetweenSeries) + cellHeight + cellHeight * 0.1;
                    ctx.setFont(Fonts.opensansRegular(cellHeight * 0.5));
                    ctx.setTextAlign(TextAlignment.LEFT);
                    ctx.setFill(getSeriesNameColor());
                    ctx.fillText(seriesName, 0, posY + (cellHeight * 0.5), nameColumnWidth * 0.95);

                    ctx.setFont(Fonts.opensansRegular(cellHeight * 0.25));
                    ctx.setTextAlign(TextAlignment.RIGHT);
                    ctx.setFill(getSeriesSumColor());
                    ctx.fillText(sumOfSeries, width, posY + (itemHeight * 0.5), sumColumnWidth * 0.95);

                    final ChartItemSeries<ChartItem> comparisonSeries      = comparisonListOfSeries.get(y);
                    final String                     comparisonSumOfSeries = Helper.shortenNumber((long) comparisonSeries.getSumOfAllItems());
                    final double                     comparisonPosY        = y * (cellHeight + spaceBetweenSeries) + cellHeight + itemHeight + spaceBetweenItems + cellHeight * 0.1;
                    ctx.setFill(getComparisonSeriesSumColor());
                    ctx.fillText(comparisonSumOfSeries, width, comparisonPosY + (itemHeight * 0.5), sumColumnWidth * 0.95);
                }

                // Draw category names and their sums
                ctx.setTextAlign(TextAlignment.LEFT);
                final Locale locale = getLocale();
                for (int x = 0; x < noOfCategories; x++) {
                    ctx.setFont(Fonts.opensansRegular(cellHeight * 0.25));
                    final double   posX          = nameColumnWidth + x * (cellWidth + spaceBetweenCategories);
                    final Category category      = categories.get(x);
                    final double   sum           = sumsPerCategory.get(category);
                    final double   comparisonSum = comparisonSumsPerCategory.get(category);
                    final String   categoryName;
                    if (category instanceof MonthCategory) {
                        MonthCategory monthCategory = (MonthCategory) category;
                        categoryName = monthCategory.getName(TextStyle.SHORT, locale);
                    } else if (category instanceof DayOfWeekCategory) {
                        DayOfWeekCategory dayOfWeekCategory = (DayOfWeekCategory) category;
                        categoryName = dayOfWeekCategory.getName(TextStyle.SHORT, locale);
                    } else {
                        categoryName = category.getName();
                    }
                    ctx.setFill(getCategorySumColor());
                    ctx.fillText(Helper.shortenNumber((long) sum), posX, cellHeight * 0.25, cellWidth * 0.95);
                    ctx.setFill(getComparisonCategorySumColor());
                    ctx.fillText(Helper.shortenNumber((long) comparisonSum), posX, cellHeight * 0.75, cellWidth * 0.95);
                    ctx.setFont(Fonts.opensansRegular(cellHeight * 0.5));
                    ctx.setFill(getCategoryNameColor());
                    ctx.fillText(categoryName, posX, height - cellHeight * 0.5, cellWidth * 0.95);
                }

                // Draw items
                ctx.setFont(Fonts.opensansRegular(cellHeight * 0.25));
                itemMap.clear();
                ctx.setStroke(getGridColor());
                for (int y = 0; y < noOfSeries; y++) {
                    final ChartItemSeries<ChartItem> series = listOfSeries.get(y);
                    final double                     posY   = y * (cellHeight + spaceBetweenSeries) + cellHeight;
                    for (int x = 0; x < noOfCategories; x++) {
                        final double posX = nameColumnWidth + x * (cellWidth + spaceBetweenCategories);
                        ctx.strokeLine(posX, cellHeight, posX, ((noOfSeries) * (cellHeight + spaceBetweenSeries) + cellHeight));
                        final Category            category     = categories.get(x);
                        final String              categoryName = category.getName();
                        final Optional<ChartItem> chartItem    = series.getItems().stream().filter(item -> item.getCategory().getName().equals(categoryName)).findFirst();
                        if (chartItem.isPresent()) {
                            final ChartItem item      = chartItem.get();
                            final double    itemWidth = item.getValue() * scaleFactorX;
                            ctx.setFill(useCategoryColor ? category.getFill() : item.getFill());
                            ctx.fillRect(posX, posY + itemOffsetY, itemWidth, itemHeight);
                            itemMap.put(new Rectangle(posX, posY + itemOffsetY, itemWidth, itemHeight), item);
                        }
                    }

                    final ChartItemSeries<ChartItem> comparisonSeries = comparisonListOfSeries.get(y);
                    final double                     comparisonPosY   = y * (cellHeight + spaceBetweenSeries) + cellHeight + itemHeight + spaceBetweenItems;
                    for (int x = 0; x < noOfCategories; x++) {
                        final double posX = nameColumnWidth + x * (cellWidth + spaceBetweenCategories);
                        ctx.strokeLine(posX, cellHeight, posX, ((noOfSeries) * (cellHeight + spaceBetweenSeries) + cellHeight));
                        final Category            category     = categories.get(x);
                        final String              categoryName = category.getName();
                        final Optional<ChartItem> chartItem    = comparisonSeries.getItems().stream().filter(item -> item.getCategory().getName().equals(categoryName)).findFirst();
                        if (chartItem.isPresent()) {
                            final ChartItem item      = chartItem.get();
                            final double    itemWidth = item.getValue() * scaleFactorX;
                            ctx.setFill(useCategoryColor ? category.getFill() : item.getFill());
                            ctx.fillRect(posX, comparisonPosY + itemOffsetY, itemWidth, itemHeight);
                            itemMap.put(new Rectangle(posX, comparisonPosY + itemOffsetY, itemWidth, itemHeight), item);
                        }
                    }
                }
            } else {
                // Draw items of standard series
                final int     noOfSeries             = listOfSeries.size();
                final int     noOfCategories         = categories.size();
                final double  nameColumnWidth        = width * 0.125;
                final double  sumColumnWidth         = width * 0.06125;
                final double  spaceBetweenCategories = width * 0.00625;
                final double  spaceBetweenSeries     = height * 0.0075;
                final double  cellWidth              = (width - ((noOfCategories - 1) * spaceBetweenCategories) - nameColumnWidth - sumColumnWidth) / (noOfCategories);
                final double  cellHeight             = (height - ((noOfSeries + 1) * spaceBetweenSeries)) / (noOfSeries + 2);
                final double  maxCellValue           = listOfSeries.parallelStream().max(Comparator.comparingDouble(ChartItemSeries::getMaxValue)).get().getMaxValue();
                final double  scaleFactorX           = cellWidth / maxCellValue;
                final double  itemHeight             = cellHeight * 0.8;
                final double  itemOffsetY            = cellHeight * 0.1;
                final boolean useCategoryColor       = getColorByCategory();
                final double  completeSum            = sumsPerCategory.values().stream().reduce(0.0, (a,b) -> a + b);

                // Draw name
                ctx.setFont(Fonts.opensansSemibold(cellHeight * 0.5));
                ctx.setFill(getNameColor());
                ctx.fillText(getName(), 0, cellHeight * 0.5, cellWidth * 0.95);

                // Draw complete sum
                ctx.setTextAlign(TextAlignment.RIGHT);
                ctx.setFill(getCategoryNameColor());
                ctx.fillText(Helper.shortenNumber((long) completeSum), width, cellHeight * 0.5, sumColumnWidth * 0.95);

                ctx.setFont(Fonts.opensansRegular(cellHeight * 0.5));

                // Draw series names and their sums
                for (int y = 0; y < noOfSeries; y++) {
                    final ChartItemSeries<ChartItem> series      = listOfSeries.get(y);
                    final String                     seriesName  = series.getName();
                    final String                     sumOfSeries = Helper.shortenNumber((long) series.getSumOfAllItems());
                    final double                     posY        = y * (cellHeight + spaceBetweenSeries) + cellHeight;
                    ctx.setTextAlign(TextAlignment.LEFT);
                    ctx.setFill(getSeriesNameColor());
                    ctx.fillText(seriesName, 0, posY + (cellHeight * 0.5), nameColumnWidth * 0.95);
                    ctx.setTextAlign(TextAlignment.RIGHT);
                    ctx.setFill(getSeriesSumColor());
                    ctx.fillText(sumOfSeries, width, posY + (cellHeight * 0.5), sumColumnWidth * 0.95);
                }

                // Draw category names and their sums
                ctx.setTextAlign(TextAlignment.LEFT);
                final Locale locale = getLocale();
                for (int x = 0; x < noOfCategories; x++) {
                    final double   posX     = nameColumnWidth + x * (cellWidth + spaceBetweenCategories);
                    final Category category = categories.get(x);
                    final double   sum      = sumsPerCategory.get(category);
                    final String   categoryName;
                    if (category instanceof MonthCategory) {
                        MonthCategory monthCategory = (MonthCategory) category;
                        categoryName = monthCategory.getName(TextStyle.SHORT, locale);
                    } else if (category instanceof DayOfWeekCategory) {
                        DayOfWeekCategory dayOfWeekCategory = (DayOfWeekCategory) category;
                        categoryName = dayOfWeekCategory.getName(TextStyle.SHORT, locale);
                    } else {
                        categoryName = category.getName();
                    }
                    ctx.setFill(getCategorySumColor());
                    ctx.fillText(Helper.shortenNumber((long) sum), posX, cellHeight * 0.5, cellWidth * 0.95);
                    ctx.setFill(getCategoryNameColor());
                    ctx.fillText(categoryName, posX, height - cellHeight * 0.5, cellWidth * 0.95);
                }

                // Draw items
                itemMap.clear();
                ctx.setStroke(getGridColor());
                for (int y = 0; y < noOfSeries; y++) {
                    final ChartItemSeries<ChartItem> series = listOfSeries.get(y);
                    final double                     posY   = y * (cellHeight + spaceBetweenSeries) + cellHeight;
                    for (int x = 0; x < noOfCategories; x++) {
                        final double posX = nameColumnWidth + x * (cellWidth + spaceBetweenCategories);
                        ctx.strokeLine(posX, cellHeight, posX, ((noOfSeries) * (cellHeight + spaceBetweenSeries) + cellHeight));
                        final Category            category     = categories.get(x);
                        final String              categoryName = category.getName();
                        final Optional<ChartItem> chartItem    = series.getItems().stream().filter(item -> item.getCategory().getName().equals(categoryName)).findFirst();
                        if (chartItem.isPresent()) {
                            final ChartItem item      = chartItem.get();
                            final double    itemWidth = item.getValue() * scaleFactorX;
                            ctx.setFill(useCategoryColor ? category.getFill() : item.getFill());
                            ctx.fillRect(posX, posY + itemOffsetY, itemWidth, itemHeight);
                            itemMap.put(new Rectangle(posX, posY + itemOffsetY, itemWidth, itemHeight), item);
                        }
                    }
                }
            }
        }
    }
}