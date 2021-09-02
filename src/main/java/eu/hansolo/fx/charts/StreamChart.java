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
import eu.hansolo.fx.charts.event.EventType;
import eu.hansolo.fx.charts.event.ItemEvent;
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.CtxBounds;
import eu.hansolo.fx.charts.tools.FontMetrix;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.Point;
import eu.hansolo.fx.charts.tools.SortDirection;
import eu.hansolo.fx.charts.tools.TooltipPopup;
import eu.hansolo.fx.geometry.Path;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByKey;


/**
 * User: hansolo
 * Date: 08.01.18
 * Time: 04:13
 */
@DefaultProperty("children")
public class StreamChart extends Region {
    public enum Category {
        DAY(TemporalAdjusters.ofDateAdjuster(d -> d), DateTimeFormatter.ofPattern("dd MMM YYYY")),
        WEEK(TemporalAdjusters.previousOrSame(DayOfWeek.of(1)), DateTimeFormatter.ofPattern("w")),
        MONTH(TemporalAdjusters.firstDayOfMonth(), DateTimeFormatter.ofPattern("MMM")),
        YEAR(TemporalAdjusters.firstDayOfYear(), DateTimeFormatter.ofPattern("YYYY"));

        private TemporalAdjuster  adjuster;
        private DateTimeFormatter formatter;

        Category(final TemporalAdjuster ADJUSTER, final DateTimeFormatter FORMATTER) {
            adjuster  = ADJUSTER;
            formatter = FORMATTER;
        }

        public TemporalAdjuster adjuster() { return adjuster; }

        public DateTimeFormatter formatter() { return formatter; }
    }
    public enum Type {
        STACKED, CENTERED
    }
    private static final double                            PREFERRED_WIDTH         = 600;
    private static final double                            PREFERRED_HEIGHT        = 400;
    private static final double                            MINIMUM_WIDTH           = 50;
    private static final double                            MINIMUM_HEIGHT          = 50;
    private static final double                            MAXIMUM_WIDTH           = 2048;
    private static final double                            MAXIMUM_HEIGHT          = 2048;
    private static final Color                             DEFAULT_ITEM_COLOR      = Color.rgb(164, 164, 164);
    private static final Color                             DEFAULT_SELECTION_COLOR = Color.rgb(128, 0, 0, 0.25);
    private static final Color                             UNSELECTED_COLOR        = Color.rgb(128, 128, 128, 0.2);
    private static final int                               DEFAULT_ITEM_WIDTH      = 80;
    private static final int                               DEFAULT_NODE_GAP        = 20;
    private static final double                            DEFAULT_OPACITY         = 0.55;
    private static final int                               MAX_ITEM_WIDTH          = 100;
    private              double                            size;
    private              double                            width;
    private              double                            height;
    private              double                            reducedHeight;
    private              Canvas                            canvas;
    private              GraphicsContext                   ctx;
    private              Category                          _category;
    private              ObjectProperty<Category>          category;
    private              Type                              _type;
    private              ObjectProperty<Type>              type;
    private              ObservableList<ChartItem>         items;
    private              Map<LocalDate, List<ChartItem>>   chartItems;
    private              Map<Integer, List<ChartItemData>> itemsPerCategory;
    private              Map<Integer, Double>              sumsPerCategory;
    private              ItemEventListener                 itemListener;
    private              ListChangeListener<ChartItem>     itemListListener;
    private              double                            scaleY;
    private              Color                             _textColor;
    private              ObjectProperty<Color>             textColor;
    private              boolean                           _autoTextColor;
    private              BooleanProperty                   autoTextColor;
    private              Color                             _categoryTextColor;
    private              ObjectProperty<Color>             categoryTextColor;
    private              Color                             _selectionColor;
    private              ObjectProperty<Color>             selectionColor;
    private              int                               _itemWidth;
    private              IntegerProperty                   itemWidth;
    private              boolean                           _autoItemWidth;
    private              BooleanProperty                   autoItemWidth;
    private              int                               _itemGap;
    private              IntegerProperty                   itemGap;
    private              boolean                           _autoItemGap;
    private              BooleanProperty                   autoItemGap;
    private              int                               _decimals;
    private              IntegerProperty                   decimals;
    private              Locale                            _locale;
    private              ObjectProperty<Locale>            locale;
    private              double                            _itemTextThreshold;
    private              DoubleProperty                    itemTextThreshold;
    private              boolean                           _itemTextVisible;
    private              BooleanProperty                   itemTextVisible;
    private              SortDirection                     _sortDirection;
    private              ObjectProperty<SortDirection>     sortDirection;
    private              boolean                           _sortByName;
    private              BooleanProperty                   sortByName;
    private              boolean                           _categorySumVisible;
    private              BooleanProperty                   categorySumVisible;
    private              String                            formatString;
    private              Font                              itemFont;
    private              Font                              categoryFont;
    private              FontMetrix                        itemFontMetrix;
    private              List<Path>                        selectedPaths;
    private              Map<Path, ChartItem>              bezierPaths;
    private              TooltipPopup                      popup;


    // ******************** Constructors **************************************
    public StreamChart() {
        this(Category.DAY, Type.STACKED, new ArrayList<>());
    }
    public StreamChart(final Category CATEGORY, final ChartItem... ITEMS) {
        this(CATEGORY, Type.STACKED, Arrays.asList(ITEMS));
    }
    public StreamChart(final Type TYPE, final ChartItem... ITEMS) {
        this(Category.DAY, TYPE, Arrays.asList(ITEMS));
    }
    public StreamChart(final Category CATEGORY, final List<ChartItem> ITEMS) {
        this(CATEGORY, Type.STACKED, ITEMS);
    }
    public StreamChart(final Category CATEGORY, final Type TYPE, final List<ChartItem> ITEMS) {
        items               = FXCollections.observableArrayList();
        chartItems          = new LinkedHashMap<>();
        itemsPerCategory    = new LinkedHashMap<>();
        sumsPerCategory     = new LinkedHashMap<>();
        itemListener        = e -> redraw();
        itemListListener    = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedItem -> addedItem.setOnItemEvent(itemListener));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedItem -> removedItem.removeItemEventListener(itemListener));
                }
            }
            groupBy(getCategory());
        };
        _category           = CATEGORY;
        _type               = TYPE;
        _textColor          = Color.BLACK;
        _autoTextColor      = false;
        _categoryTextColor  = Color.BLACK;
        _selectionColor     = DEFAULT_SELECTION_COLOR;
        _itemWidth          = DEFAULT_ITEM_WIDTH;
        _autoItemWidth      = true;
        _itemGap            = DEFAULT_NODE_GAP;
        _autoItemGap        = true;
        _decimals           = 0;
        _locale             = Locale.getDefault();
        _itemTextThreshold  = 1;
        _itemTextVisible    = true;
        _sortDirection      = SortDirection.ASCENDING;
        _sortByName         = false;
        _categorySumVisible = false;
        itemFont            = Fonts.latoRegular(10);
        categoryFont        = Fonts.latoRegular(10);
        itemFontMetrix      = new FontMetrix(itemFont);
        formatString        = "%." + _decimals + "f";
        selectedPaths       = new LinkedList<>();
        bezierPaths         = new LinkedHashMap<>();
        popup               = new TooltipPopup(2000);

        items.setAll(null == ITEMS ? new ArrayList<>() : ITEMS);

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
        popup.setOnHiding(e -> popup.setText(""));
        items.addListener(itemListListener);
        canvas.setOnMouseMoved(e -> {
            bezierPaths.forEach((path, chartItem) -> {
                double eventX = e.getX();
                double eventY = e.getY();
                if (path.contains(eventX, eventY)) {
                    String  tooltipText = chartItem.getName() + ": " + chartItem.getValue();
                    if (!tooltipText.isEmpty()) {
                        popup.setX(e.getScreenX() - popup.getWidth() * 0.5);
                        popup.setY(e.getScreenY() - 30);
                        popup.setText(tooltipText);
                        popup.animatedShow(getScene().getWindow());
                }
                }
            });
            });
        canvas.setOnMousePressed(e -> {
            if (Type.CENTERED == getType()) { return; }
            selectedPaths.clear();
            bezierPaths.forEach((path, chartItem) -> {
                double eventX = e.getX();
                double eventY = e.getY();
                if (path.contains(eventX, eventY)) {
                    chartItem.fireItemEvent(new ItemEvent(chartItem, EventType.SELECTED));
                    selectedPaths.addAll(bezierPaths.entrySet()
                                                    .parallelStream()
                                                    .filter(entry -> entry.getValue().getName().equals(chartItem.getName()))
                                                    .collect(Collectors.toList())
                                                    .stream()
                                                    .map(entry -> entry.getKey())
                                                    .collect(Collectors.toList()));
                    redraw();
                }
            });
        });
        canvas.setOnMouseReleased(e -> {
            if (Type.CENTERED == getType()) { return; }
            selectedPaths.clear();
            redraw();
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

    public Category getCategory() { return null == category ? _category : category.get(); }
    public void setCategory(final Category CATEGORY) {
        if (null == category) {
            _category = CATEGORY;
            redraw();
        } else {
            category.set(CATEGORY);
        }
    }
    public ObjectProperty<Category> categoryProperty() {
        if (null == category) {
            category = new ObjectPropertyBase<Category>(_category) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "category"; }
            };
            _category = null;
        }
        return category;
    }

    public Type getType() { return null == type ? _type : type.get(); }
    public void setType(final Type TYPE) {
        if (null == type) {
            _type = TYPE;
            prepareData();
        } else {
            type.set(TYPE);
        }
    }
    public ObjectProperty<Type> typeProperty() {
        if (null == type) {
            type = new ObjectPropertyBase<>(_type) {
                @Override protected void invalidated() { prepareData(); }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "type"; }
            };
            _type = null;
        }
        return type;
    }

    public List<ChartItem> getItems() { return items; }
    public void setItems(final ChartItem... ITEMS) { setItems(Arrays.asList(ITEMS)); }
    public void setItems(final List<ChartItem> ITEMS) { items.setAll(ITEMS); }
    public void addItem(final ChartItem ITEM) { if (!items.contains(ITEM)) { items.add(ITEM); } }
    public void removeItem(final ChartItem ITEM) { if (items.contains(ITEM)) { items.remove(ITEM); } }

    public Color getTextColor() { return null == textColor ? _textColor : textColor.get(); }
    public void setTextColor(final Color COLOR) {
        if (null == textColor) {
            _textColor = COLOR;
            redraw();
        } else {
            textColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> textColorProperty() {
        if (null == textColor) {
            textColor = new ObjectPropertyBase<>(_textColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "textColor"; }
            };
            _textColor = null;
        }
        return textColor;
    }

    public Color getSelectionColor() { return null == selectionColor ? _selectionColor : selectionColor.get(); }
    public void setSelectionColor(final Color COLOR) {
        if (null == selectionColor) {
            _selectionColor = COLOR;
            redraw();
        } else {
            selectionColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> selectionColorProperty() {
        if (null == selectionColor) {
            selectionColor = new ObjectPropertyBase<>(_selectionColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "selectionColor"; }
            };
            _selectionColor = null;
        }
        return selectionColor;
    }

    public Color getCategoryTextColor() { return null == categoryTextColor ? _categoryTextColor : categoryTextColor.get(); }
    public void setCategoryTextColor(final Color COLOR) {
        if (null == categoryTextColor) {
            _categoryTextColor = COLOR;
            redraw();
        } else {
            categoryTextColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> categoryTextColorProperty() {
        if (null == categoryTextColor) {
            categoryTextColor = new ObjectPropertyBase<>(_categoryTextColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "categoryTextColor"; }
            };
            _categoryTextColor = null;
        }
        return categoryTextColor;
    }

    public boolean isAutoTextColor() { return null == autoTextColor ? _autoTextColor : autoTextColor.get(); }
    public void setAutoTextColor(final boolean AUTO) {
        if (null == autoTextColor) {
            _autoTextColor = AUTO;
            redraw();
        } else {
            autoTextColor.set(AUTO);
        }
    }
    public BooleanProperty autoTextColorProperty() {
        if (null == autoTextColor) {
            autoTextColor = new BooleanPropertyBase(_autoTextColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "autoTextColor"; }
            };
        }
        return autoTextColor;
    }

    public int getItemWidth() { return null == itemWidth ? _itemWidth : itemWidth.get(); }
    public void setItemWidth(final int WIDTH) {
        if (null == itemWidth) {
            _itemWidth = Helper.clamp(2, MAX_ITEM_WIDTH, WIDTH);
            prepareData();
        } else {
            itemWidth.set(WIDTH);
        }
    }
    public IntegerProperty itemWidthProperty() {
        if (null == itemWidth) {
            itemWidth = new IntegerPropertyBase(_itemWidth) {
                @Override protected void invalidated() {
                    set(Helper.clamp(2, MAX_ITEM_WIDTH, get()));
                    prepareData();
                }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "itemWidth"; }
            };
        }
        return itemWidth;
    }

    public boolean isAutoItemWidth() { return null == autoItemWidth ? _autoItemWidth : autoItemWidth.get(); }
    public void setAutoItemWidth(final boolean AUTO) {
        if (null == autoItemWidth) {
            _autoItemWidth = AUTO;
            prepareData();
        } else {
            autoItemWidth.set(AUTO);
        }
    }
    public BooleanProperty autoItemWidthProperty() {
        if (null == autoItemWidth) {
            autoItemWidth = new BooleanPropertyBase(_autoItemWidth) {
                @Override protected void invalidated() { prepareData(); }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "autoItemWidth"; }
            };
        }
        return autoItemWidth;
    }

    public int getItemGap() { return null == itemGap ? _itemGap : itemGap.get(); }
    public void setItemGap(final int GAP) {
        if (null == itemGap) {
            _itemGap = Helper.clamp(0, 100, GAP);
            prepareData();
        } else {
            itemGap.set(GAP);
        }
    }
    public IntegerProperty itemGapProperty() {
        if (null == itemGap) {
            itemGap = new IntegerPropertyBase(_itemGap) {
                @Override protected void invalidated() {
                    set(Helper.clamp(0, 100, get()));
                    prepareData();
                }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "itemGap"; }
            };
        }
        return itemGap;
    }

    public boolean isAutoItemGap() { return null == autoItemGap ? _autoItemGap : autoItemGap.get(); }
    public void setAutoItemGap(final boolean AUTO) {
        if (null == autoItemGap) {
            _autoItemGap = AUTO;
            prepareData();
        } else {
            autoItemGap.set(AUTO);
        }
    }
    public BooleanProperty autoItemGapProperty() {
        if (null == autoItemGap) {
            autoItemGap = new BooleanPropertyBase(_autoItemGap) {
                @Override protected void invalidated() { prepareData(); }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "autoItemGap"; }
            };
        }
        return autoItemGap;
    }

    public int getDecimals() { return null == decimals ? _decimals : decimals.get(); }
    public void setDecimals(final int DECIMALS) {
        if (null == decimals) {
            _decimals = Helper.clamp(0, 6, DECIMALS);
            formatString = new StringBuilder("%.").append(getDecimals()).append("f").toString();
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
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "decimals"; }
            };
        }
        return decimals;
    }

    public Locale getLocale() { return null == locale ? _locale : locale.get(); }
    public void setLocale(final Locale LOCALE) {
        if (null == locale) {
            _locale = LOCALE;
            prepareData();
        } else {
            locale.set(LOCALE);
        }
    }
    public ObjectProperty<Locale> localeProperty() {
        if (null == locale) {
            locale = new ObjectPropertyBase<Locale>(_locale) {
                @Override protected void invalidated() { prepareData(); }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "locale"; }
            };
        }
        _locale = null;
        return locale;
    }

    public double getItemTextThreshold() { return null == itemTextThreshold ? _itemTextThreshold : itemTextThreshold.get(); }
    public void setItemTextThreshold(final double ITEM_TEXT_THRESHOLD) {
        if (null == itemTextThreshold) {
            _itemTextThreshold = Helper.clamp(1, Double.MAX_VALUE, ITEM_TEXT_THRESHOLD);
            redraw();
        } else {
            itemTextThreshold.set(ITEM_TEXT_THRESHOLD);
        }
    }
    public DoubleProperty itemTextThresholdProperty() {
        if (null == itemTextThreshold) {
            itemTextThreshold = new DoublePropertyBase(_itemTextThreshold) {
                @Override protected void invalidated() {
                    set(Helper.clamp(1, Double.MAX_VALUE, get()));
                    redraw();
                }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "textItemThreshold"; }
            };
        }
        return itemTextThreshold;
    }

    public boolean isItemTextVisible() { return null == itemTextVisible ? _itemTextVisible : itemTextVisible.get(); }
    public void setItemTextVisible(final boolean VISIBLE) {
        if (null == itemTextVisible) {
            _itemTextVisible = VISIBLE;
            redraw();
        } else {
            itemTextVisible.set(VISIBLE);
        }
    }
    public BooleanProperty itemTextVisibleProperty() {
        if (null == itemTextVisible) {
            itemTextVisible = new BooleanPropertyBase(_itemTextVisible) {
                @Override protected void invalidated() { redraw();}
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "itemTextVisible"; }
            };
        }
        return itemTextVisible;
    }

    public SortDirection getSortDirection() { return null == sortDirection ? _sortDirection : sortDirection.get(); }
    public void setSortDirection(final SortDirection DIRECTION) {
        if (null == sortDirection) {
            _sortDirection = DIRECTION;
            groupBy(getCategory());
        } else {
            sortDirection.set(DIRECTION);
        }
    }
    public ObjectProperty<SortDirection> sortDirectionProperty() {
        if (null == sortDirection) {
            sortDirection = new ObjectPropertyBase<>(_sortDirection) {
                @Override protected void invalidated() {
                    groupBy(getCategory());
                }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "sortDirection"; }
            };
            _sortDirection = null;
        }
        return sortDirection;
    }

    public boolean isSortByName() { return null == sortByName ? _sortByName : sortByName.get(); }
    public void setSortByName(final boolean BY_NAME) {
        if (null == sortByName) {
            _sortByName = BY_NAME;
            groupBy(getCategory());
        } else {
            sortByName.set(BY_NAME);
        }
    }
    public BooleanProperty sortByNameProperty() {
        if (null == sortByName) {
            sortByName = new BooleanPropertyBase(_sortByName) {
                @Override protected void invalidated() { groupBy(getCategory()); }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "sortByName"; }
            };
        }
        return sortByName;
    }

    public boolean isCategorySumVisible() { return null == categorySumVisible ? _categorySumVisible : categorySumVisible.get(); }
    public void setCategorySumVisible(final boolean VISIBLE) {
        if (null == categorySumVisible) {
            _categorySumVisible = VISIBLE;
            redraw();
        } else {
            categorySumVisible.set(VISIBLE);
        }
    }
    public BooleanProperty categorySumVisibleProperty() {
        if (null == categorySumVisible) {
            categorySumVisible = new BooleanPropertyBase(_categorySumVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "categorySumVisible"; }
            };
        }
        return categorySumVisible;
    }

    public void groupBy(final Category CATEGORY) {
        chartItems.clear();
        // Group items by category
        Map<LocalDate, List<ChartItem>> groupedItems = items.stream()
                                                            .collect(Collectors.groupingBy(item -> item.getTimestampAsLocalDate()
                                                                                                       .with(CATEGORY.adjuster())));

        // Sort map by key
        Map<LocalDate, List<ChartItem>> sorted = groupedItems.entrySet()
                                                             .stream()
                                                             .sorted(Collections.reverseOrder(comparingByKey()))
                                                             .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                       Map.Entry::getValue,
                                                                                       (oldValue, newValue) -> oldValue,
                                                                                       LinkedHashMap::new));

        // Compact items with same name into one item
        sorted.entrySet().stream().forEach(entry -> {
            Map<String, ChartItem> compactedItems = new LinkedHashMap<>();
            for (ChartItem item : entry.getValue()) {
                if (compactedItems.keySet().contains(item.getName())) {
                    compactedItems.get(item.getName()).setValue(compactedItems.get(item.getName()).getValue() + item.getValue());
                } else {
                    compactedItems.put(item.getName(), item);
                }
            }
            List<ChartItem> compacted = new ArrayList<>(compactedItems.values());
            switch (getSortDirection()) {
                case ASCENDING : sortItemsAscending(compacted); break;
                case DESCENDING: sortItemsDescending(compacted); break;
            }

            chartItems.put(entry.getKey(), compacted);
        });

        prepareData();
    }

    private void sortItemsAscending(final List<ChartItem> ITEMS) {
        if (isSortByName()) {
            Collections.sort(ITEMS, Comparator.comparing(ChartItem::getName));
        } else {
            Collections.sort(ITEMS);
        }
    }
    private void sortItemsDescending(final List<ChartItem> ITEMS) {
        if (isSortByName()) {
            Collections.sort(ITEMS, Comparator.comparing(ChartItem::getName).reversed());
        } else {
            Collections.sort(ITEMS, Collections.reverseOrder());
        }
    }

    public double getSumOfItems() { return items.stream().mapToDouble(ChartItem::getValue).sum(); }

    private void prepareData() {
        if (chartItems.isEmpty()) { return; }

        Type type = getType();

        // Split all items to categories
        itemsPerCategory.clear();
        int cat = chartItems.size() - 1;

        for (LocalDate key : chartItems.keySet()) {
            List<ChartItemData> itemDataList = new ArrayList<>();
            chartItems.get(key).forEach(item -> itemDataList.add(new ChartItemData(item)));
            itemsPerCategory.put(cat, itemDataList);
            cat--;
        }

        // No of categories
        int noOfCategories = chartItems.size();

        // Reverse items in at each category
        chartItems.forEach((localDate, items) -> Collections.reverse(items));

        // Get max sum of all category
        double maxSum   = chartItems.entrySet().stream().mapToDouble(entry -> entry.getValue().stream().mapToDouble(ChartItem::getValue).sum()).max().getAsDouble();
        int    maxItems = chartItems.entrySet().stream().mapToInt(entry -> entry.getValue().size()).reduce(0, Integer::max);

        sumsPerCategory.clear();

        // Define drawing parameters
        double itemWidth     = isAutoItemWidth() ? size * 0.1 : getItemWidth();
        double verticalGap   = isAutoItemGap() ? size * 0.005 : getItemGap();
        double horizontalGap = (width - itemWidth) / (chartItems.keySet().size() - 1);
        scaleY               = (reducedHeight - (maxItems - 1) * verticalGap) / maxSum;
        double spacerX;
        double spacerY;
        for (int category = 0 ; category < noOfCategories ; category++) {
            spacerY = 0;
            spacerX = horizontalGap * category;

            double sum = 0;
            for (ChartItemData itemData : itemsPerCategory.get(category)) {
                ChartItem     item        = itemData.getChartItem();
                double        itemHeight  = item.getValue() * scaleY;
                double        textOffsetX = 2;
                itemData.setBounds(spacerX , (reducedHeight - itemHeight) - spacerY, itemWidth, itemHeight);
                itemData.setTextPoint(spacerX + textOffsetX, (reducedHeight - itemHeight) - spacerY + ctx.getFont().getSize());
                if (Type.STACKED == type) {
                    spacerY += itemHeight + verticalGap;
                }
                sum += item.getValue();
            }
            sumsPerCategory.put(category, sum);
        }

        createPaths();

        redraw();
    }

    private <K, V> K getKeyByValue(final Map<K,V> MAP, final V VALUE) {
        return MAP.keySet().stream().filter(key -> VALUE.equals(MAP.get(key))).findFirst().get();
    }


    // ******************** Layout ********************************************
    private void resize() {
        width         = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height        = getHeight() - getInsets().getTop() - getInsets().getBottom();
        reducedHeight = height - height * 0.05;
        size          = width < height ? width : height;

        if (width > 0 && height > 0) {
            canvas.setWidth(width);
            canvas.setHeight(height);
            canvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            ctx.setTextBaseline(VPos.CENTER);
            itemFont           = Fonts.latoRegular(Helper.clamp(8, 20, size * 0.025));
            categoryFont       = Fonts.latoRegular(Helper.clamp(8, 20, size * 0.025));
            itemFontMetrix     = new FontMetrix(itemFont);

            groupBy(getCategory());
        }
    }

    private void createPaths() {
        bezierPaths.clear();
        int    noOfCategories = chartItems.size();
        Type   type           = getType();
        double halfItemWidth  = getItemWidth() * 0.5;
        double offsetY        = Type.STACKED == type ? 0 : height * 0.5;

        // Draw bezier curves between items
        for (int category = 0 ; category < noOfCategories ; category++) {
            List<ChartItemData> itemDataInCategory = itemsPerCategory.get(category);
            int nextCategory = category + 1;

            if (null == itemDataInCategory) { continue; }
            // Go through all item data of the current category
            for (ChartItemData itemData : itemDataInCategory) {
                ChartItem item   = itemData.getChartItem();
                CtxBounds bounds = itemData.getBounds();

                // Create path if current item is also present in next category
                if (category < noOfCategories) {
                    List<ChartItemData> nextCategoryItemDataList = itemsPerCategory.get(nextCategory);
                    if (null == nextCategoryItemDataList) continue;
                    Optional<ChartItemData> targetItemDataOptional = nextCategoryItemDataList.stream().filter(id -> {
                        if (null == id.getChartItem().getName() || null == item.getName()) { return false; }
                        return id.getChartItem().getName().equals(item.getName());
                    }).findFirst();
                    if (!targetItemDataOptional.isPresent()) { continue; }

                    ChartItemData targetItemData   = targetItemDataOptional.get();
                    CtxBounds     targetItemBounds = targetItemData.getBounds();

                    // Calculate the offset in x direction for the bezier curve control points
                    double ctrlPointOffsetX = (targetItemBounds.getMinX() - bounds.getMaxX()) * 0.5;

                    // Create Path
                    Path path = new Path();

                    // Set path fill to item fill
                    path.setFill(item.getFill());
                    path.setStroke(item.getFill());

                    // Draw the bezier curve
                    if (Type.STACKED == type) {
                        path.moveTo(bounds.getCenterX(), bounds.getMinY());
                        path.lineTo(bounds.getCenterX() + halfItemWidth, bounds.getMinY());
                        path.bezierCurveTo(bounds.getCenterX() + halfItemWidth + ctrlPointOffsetX, bounds.getMinY(),
                                           targetItemBounds.getCenterX() - halfItemWidth - ctrlPointOffsetX, targetItemBounds.getMinY(),
                                           targetItemBounds.getCenterX() - halfItemWidth, targetItemBounds.getMinY());
                        path.lineTo(targetItemBounds.getCenterX(), targetItemBounds.getMinY());
                        path.lineTo(targetItemBounds.getCenterX(), targetItemBounds.getMaxY());
                        path.lineTo(targetItemBounds.getCenterX() - halfItemWidth, targetItemBounds.getMaxY());
                        path.bezierCurveTo(targetItemBounds.getCenterX() - halfItemWidth - ctrlPointOffsetX, targetItemBounds.getMaxY(),
                                           bounds.getCenterX() + halfItemWidth + ctrlPointOffsetX, bounds.getMaxY(),
                                           bounds.getCenterX() + halfItemWidth, bounds.getMaxY());
                        path.lineTo(bounds.getCenterX(), bounds.getMaxY());
                        path.lineTo(bounds.getCenterX(), bounds.getMinY());
                        path.closePath();
                    } else {
                        double halfItemHeight       = bounds.getHeight() * 0.5;
                        double halfTargetItemHeight = targetItemBounds.getHeight() * 0.5;

                        path.moveTo(bounds.getCenterX(), offsetY - halfItemHeight);
                        path.lineTo(bounds.getCenterX() + halfItemWidth, offsetY - halfItemHeight);
                        path.bezierCurveTo(bounds.getCenterX() + halfItemWidth + ctrlPointOffsetX, offsetY - halfItemHeight,
                                           targetItemBounds.getCenterX() - halfItemWidth - ctrlPointOffsetX, offsetY - halfTargetItemHeight,
                                           targetItemBounds.getCenterX() - halfItemWidth, offsetY - halfTargetItemHeight);
                        path.lineTo(targetItemBounds.getCenterX(), offsetY - halfTargetItemHeight);
                        path.lineTo(targetItemBounds.getCenterX(), offsetY + halfTargetItemHeight);
                        path.lineTo(targetItemBounds.getCenterX() - halfItemWidth, offsetY + halfTargetItemHeight);
                        path.bezierCurveTo(targetItemBounds.getCenterX() - halfItemWidth - ctrlPointOffsetX, offsetY + halfTargetItemHeight,
                                           bounds.getCenterX() + halfItemWidth + ctrlPointOffsetX, offsetY + halfItemHeight,
                                           bounds.getCenterX() + halfItemWidth, offsetY + halfItemHeight);
                        path.lineTo(bounds.getCenterX(), offsetY + halfItemHeight);
                        path.lineTo(bounds.getCenterX(), offsetY - halfItemHeight);
                        path.closePath();
                    }
                    bezierPaths.put(path, item);
                }
            }
        }
    }

    private void redraw() {
        Color             textColor      = getTextColor();
        boolean           autoTextColor  = isAutoTextColor();
        int               noOfCategories = chartItems.size();
        DateTimeFormatter formatter      = getCategory().formatter();
        Color             selectionColor = getSelectionColor();

        ctx.clearRect(0, 0, width, height);

        // Draw bezier paths
        if (selectedPaths.isEmpty()) {
        bezierPaths.forEach((path, plotItem) -> path.draw(ctx, true, true));
        } else {
            bezierPaths.forEach((path, plotItem) -> path.draw(ctx, true, UNSELECTED_COLOR, false, Color.TRANSPARENT));
            selectedPaths.forEach(path -> path.draw(ctx, true, selectionColor, true, selectionColor));
        }

        for (int category = 0 ; category < noOfCategories ; category++) {
            List<ChartItemData> itemDataInCategory = itemsPerCategory.get(category);
            if (null == itemDataInCategory || itemDataInCategory.isEmpty()) { continue; }
            // Go through all item data of the current category
            for (ChartItemData itemData : itemDataInCategory) {
                ChartItem item      = itemData.getChartItem();
                CtxBounds bounds    = itemData.getBounds();

                // Draw item text
                if (isItemTextVisible() && item.getValue() > getItemTextThreshold()) {
                    ctx.setFill(autoTextColor ? Helper.isDark(item.getFill()) ? Color.WHITE: Color.BLACK : textColor);
                    itemFontMetrix.computeStringWidth(item.getName());
                    if (itemFontMetrix.computeStringWidth(item.getName()) < MAX_ITEM_WIDTH &&
                        itemFontMetrix.getLineHeight() < bounds.getHeight()) {
                        if (category == 0) {
                            ctx.setTextAlign(TextAlignment.LEFT);
                        ctx.fillText(item.getName(), bounds.getCenterX(), bounds.getCenterY());
                        } else if (category == noOfCategories - 1) {
                            ctx.setTextAlign(TextAlignment.RIGHT);
                            ctx.fillText(item.getName(), bounds.getCenterX(), bounds.getCenterY());
                        } else {
                            ctx.setTextAlign(TextAlignment.CENTER);
                            ctx.fillText(item.getName(), bounds.getCenterX(), bounds.getCenterY());
                        }
                    }
                }
            }

            // Draw category text
            ChartItemData firstItem = itemDataInCategory.get(0);
            ctx.setFill(getCategoryTextColor());
            if (isCategorySumVisible()) {
                ctx.fillText("\u03a3 " + String.format(getLocale(), formatString, sumsPerCategory.get(category)), firstItem.getBounds().getCenterX(), 15, MAX_ITEM_WIDTH);
            }
            ctx.fillText(formatter.format(firstItem.getLocalDate()), firstItem.getBounds().getCenterX(), reducedHeight + size * 0.02, MAX_ITEM_WIDTH);
        }
    }


    // ******************** Inner Classes *************************************
    private class ChartItemData {
        private ChartItem chartItem;
        private CtxBounds bounds;           // bounds of the item rectangle
        private Point     textPoint;        // point where text will be drawn
        private double    value;


        // ******************** Constructors **********************************
        public ChartItemData(final ChartItem ITEM) {
            chartItem = ITEM;
            bounds    = new CtxBounds();
            textPoint = new Point();
            value     = 0;
        }


        // ******************** Methods *******************************************
        public ChartItem getChartItem() { return chartItem; }

        public LocalDate getLocalDate() { return chartItem.getTimestampAsLocalDate(ZoneId.systemDefault()); }

        public CtxBounds getBounds() { return bounds; }
        public void setBounds(final double X, final double Y, final double WIDTH, final double HEIGHT) {
            bounds.set(X, Y, WIDTH, HEIGHT);
        }

        public Point getTextPoint() { return textPoint; }
        public void setTextPoint(final double X, final double Y) { textPoint.set(X, Y); }

        public double getValue() { return value; }
        public void setValue(final double VALUE) { value = VALUE; }
    }
}