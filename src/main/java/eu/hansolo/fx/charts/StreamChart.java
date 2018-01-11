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
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.tools.CtxBounds;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.Point;
import eu.hansolo.fx.geometry.Path;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
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
import javafx.scene.control.Tooltip;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toMap;


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
    private static final double                            PREFERRED_WIDTH    = 600;
    private static final double                            PREFERRED_HEIGHT   = 400;
    private static final double                            MINIMUM_WIDTH      = 50;
    private static final double                            MINIMUM_HEIGHT     = 50;
    private static final double                            MAXIMUM_WIDTH      = 2048;
    private static final double                            MAXIMUM_HEIGHT     = 2048;
    private static final Color                             DEFAULT_ITEM_COLOR = Color.rgb(164, 164, 164);
    private static final int                               DEFAULT_ITEM_WIDTH = 80;
    private static final int                               DEFAULT_NODE_GAP   = 20;
    private static final double                            DEFAULT_OPACITY    = 0.55;
    private              double                            size;
    private              double                            width;
    private              double                            height;
    private              double                            reducedHeight;
    private              Canvas                            canvas;
    private              GraphicsContext                   ctx;
    private              Category                          _category;
    private              ObjectProperty<Category>          category;
    private              ObservableList<ChartItem>         items;
    private              Map<LocalDate, List<ChartItem>>   chartItems;
    private              Map<Integer, List<ChartItemData>> itemsPerCategory;
    private              ItemEventListener                 itemListener;
    private              ListChangeListener<ChartItem>     itemListListener;
    private              double                            scaleY;
    private              Color                             _textColor;
    private              ObjectProperty<Color>             textColor;
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
    private              String                            formatString;
    private              Map<Path, String>                 paths;
    private              Tooltip                           tooltip;


    // ******************** Constructors **************************************
    public StreamChart() {
        this(Category.DAY, new ArrayList<ChartItem>());
    }
    public StreamChart(final Category CATEGORY, final ChartItem... ITEMS) {
        this(CATEGORY, Arrays.asList(ITEMS));
    }
    public StreamChart(final Category CATEGORY, final List<ChartItem> ITEMS) {
        items            = FXCollections.observableArrayList();
        chartItems       = new LinkedHashMap<>();
        itemsPerCategory = new LinkedHashMap<>();
        itemListener     = e -> redraw();
        itemListListener = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedItem -> addedItem.setOnItemEvent(itemListener));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedItem -> removedItem.removeItemEventListener(itemListener));
                }
            }
            groupBy(getCategory());
        };
        _category        = CATEGORY;
        _textColor       = Color.BLACK;
        _itemWidth       = DEFAULT_ITEM_WIDTH;
        _autoItemWidth   = true;
        _itemGap         = DEFAULT_NODE_GAP;
        _autoItemGap     = true;
        _decimals        = 0;
        _locale          = Locale.getDefault();
        formatString     = "%." + _decimals + "f";
        paths            = new LinkedHashMap<>();

        items.setAll(ITEMS);

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

        tooltip = new Tooltip();
        tooltip.setAutoHide(true);

        getChildren().setAll(canvas);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        items.addListener(itemListListener);
        canvas.setOnMouseClicked(e -> {
            paths.forEach((path, tooltipText) -> {
                double eventX = e.getX();
                double eventY = e.getY();
                if (path.contains(eventX, eventY)) {
                    double tooltipX = eventX + canvas.getScene().getX() + canvas.getScene().getWindow().getX();
                    double tooltipY = eventY + canvas.getScene().getY() + canvas.getScene().getWindow().getY() - 25;
                    tooltip.setText(tooltipText);
                    tooltip.setX(tooltipX);
                    tooltip.setY(tooltipY);
                    tooltip.show(getScene().getWindow());
                }
            });
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

    public List<ChartItem> getItems() { return items; }
    public void setItems(final ChartItem... ITEMS) { setItems(Arrays.asList(ITEMS)); }
    public void setItems(final List<ChartItem> ITEMS) {
        items.setAll(ITEMS);
        prepareData();
    }
    public void addItem(final ChartItem ITEM) {
        if (!items.contains(ITEM)) { items.add(ITEM); }
        prepareData();
    }
    public void removeItem(final ChartItem ITEM) {
        if (items.contains(ITEM)) { items.remove(ITEM); }
        prepareData();
    }

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
            textColor = new ObjectPropertyBase<Color>(_textColor) {
                @Override protected void invalidated() { prepareData(); }
                @Override public Object getBean() { return StreamChart.this; }
                @Override public String getName() { return "textColor"; }
            };
            _textColor = null;
        }
        return textColor;
    }

    public int getItemWidth() { return null == itemWidth ? _itemWidth : itemWidth.get(); }
    public void setItemWidth(final int WIDTH) {
        if (null == itemWidth) {
            _itemWidth = Helper.clamp(2, 50, WIDTH);
            prepareData();
        } else {
            itemWidth.set(WIDTH);
        }
    }
    public IntegerProperty itemWidthProperty() {
        if (null == itemWidth) {
            itemWidth = new IntegerPropertyBase(_itemWidth) {
                @Override protected void invalidated() {
                    set(Helper.clamp(2, 50, get()));
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
            sortItemsAscending(compacted);
            chartItems.put(entry.getKey(), compacted);
        });

        prepareData();
    }

    private void sortItemsAscending(final List<ChartItem> ITEMS) { Collections.sort(ITEMS); }
    private void sortItemsDescending(final List<ChartItem> ITEMS) { Collections.sort(ITEMS, Collections.reverseOrder()); }

    private double getSumOfItems(final List<ChartItem> ITEMS) { return ITEMS.stream().mapToDouble(ChartItem::getValue).sum(); }

    private void prepareData() {
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

            for (ChartItemData itemData : itemsPerCategory.get(category)) {
                ChartItem item        = itemData.getChartItem();
                double    itemHeight  = item.getValue() * scaleY;
                double    textOffsetX = 2;
                itemData.setBounds(spacerX , (reducedHeight - itemHeight) - spacerY, itemWidth, itemHeight);
                itemData.setTextPoint(spacerX + textOffsetX, (reducedHeight - itemHeight) - spacerY + ctx.getFont().getSize());
                spacerY += itemHeight + verticalGap;
            }
        }

        createPaths();
        redraw();
    }

    private <K, V> K getKeyByValue(final Map<K,V> MAP, final V VALUE) {
        return MAP.keySet().stream().filter(key -> VALUE.equals(MAP.get(key))).findFirst().get();
    }


    // ******************** Resizing ******************************************
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
            ctx.setFont(Font.font(Helper.clamp(8, 24, size * 0.025)));

            groupBy(getCategory());
        }
    }

    private void createPaths() {
        paths.clear();

        int noOfCategories = chartItems.size();

        // Draw bezier curves between items
        for (int category = 0 ; category < noOfCategories ; category++) {
            List<ChartItemData> itemDataInCategory = itemsPerCategory.get(category);
            int nextCategory = category + 1;

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
                    ChartItem     targetItem       = targetItemData.getChartItem();

                    // Calculate the offset in x direction for the bezier curve control points
                    double ctrlPointOffsetX = (targetItemBounds.getMinX() - bounds.getMaxX()) * 0.5;

                    // Calculate the value of the current item in y direction
                    double value = item.getValue();

                    // Create Path
                    Path path = new Path();

                    // Set path fill to item fill
                    path.setFill(item.getFill());
                    path.setStroke(item.getFill());

                    // Draw the bezier curve
                    path.moveTo(bounds.getMaxX(), bounds.getMinY());
                    path.bezierCurveTo(bounds.getMaxX() + ctrlPointOffsetX, bounds.getMinY(),
                                       targetItemBounds.getMinX() - ctrlPointOffsetX, targetItemBounds.getMinY(),
                                       targetItemBounds.getMinX(), targetItemBounds.getMinY());
                    path.lineTo(targetItemBounds.getMinX(), targetItemBounds.getMaxY());
                    path.bezierCurveTo(targetItemBounds.getMinX() - ctrlPointOffsetX, targetItemBounds.getMaxY(),
                                       bounds.getMaxX() + ctrlPointOffsetX, bounds.getMaxY(),
                                       bounds.getMaxX(), bounds.getMaxY());
                    path.lineTo(bounds.getMaxX(), bounds.getMinY());
                    path.closePath();

                    String tooltipText = new StringBuilder().append(item.getName())
                                                            .append(": ")
                                                            .append(String.format(getLocale(), formatString, value))
                                                            .append(" -> ")
                                                            .append(" ")
                                                            .append(String.format(getLocale(), formatString, targetItem.getValue()))
                                                            .toString();
                    paths.put(path, tooltipText);
                }
            }
        }
    }

    private void redraw() {
        ctx.clearRect(0, 0, width, height);
        paths.forEach((path, plotItem) -> path.draw(ctx, true, true));
        Color             textColor      = getTextColor();
        int               noOfCategories = chartItems.size();
        DateTimeFormatter formatter      = getCategory().formatter();

        for (int category = 0 ; category < noOfCategories ; category++) {
            List<ChartItemData> itemDataInCategory = itemsPerCategory.get(category);

            // Go through all item data of the current category
            for (ChartItemData itemData : itemDataInCategory) {
                ChartItem item      = itemData.getChartItem();
                CtxBounds bounds    = itemData.getBounds();
                Color     itemColor = item.getFill();

                // Draw item boxes with their labels
                ctx.setFill(itemColor);
                ctx.fillRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
                ctx.setLineWidth(0);
                ctx.setStroke(itemColor);
                ctx.strokeRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());

                if (item.getValue() > 1) {
                    ctx.setFill(textColor);
                    ctx.setTextAlign(category == noOfCategories ? TextAlignment.RIGHT : TextAlignment.LEFT);
                    ctx.fillText(item.getName(), itemData.getTextPoint().getX(), itemData.getTextPoint().getY(), bounds.getWidth());
                }
            }
            // Draw category text
            ChartItemData firstItem = itemDataInCategory.get(0);
            ctx.fillText(formatter.format(firstItem.getLocalDate()), firstItem.getTextPoint().getX(), reducedHeight + size * 0.02, firstItem.bounds.getWidth());
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