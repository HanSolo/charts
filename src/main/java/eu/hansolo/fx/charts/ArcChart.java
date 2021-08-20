/*
 * Copyright (c) 2020 by Gerrit Grunwald
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

import eu.hansolo.fx.charts.data.Connection;
import eu.hansolo.fx.charts.data.PlotItem;
import eu.hansolo.fx.charts.event.EventType;
import eu.hansolo.fx.charts.event.ItemEvent;
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.Point;
import eu.hansolo.fx.geometry.Circle;
import eu.hansolo.fx.geometry.Path;
import javafx.application.Platform;
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
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


@DefaultProperty("children")
public class ArcChart extends Region {
    private static final double                       PREFERRED_WIDTH            = 500;
    private static final double                       PREFERRED_HEIGHT           = 500;
    private static final double                       MINIMUM_WIDTH              = 50;
    private static final double                       MINIMUM_HEIGHT             = 50;
    private static final double                       MAXIMUM_WIDTH              = 4096;
    private static final double                       MAXIMUM_HEIGHT             = 4096;
    private static final double                       DEFAULT_SEGMENT_GAP        = 4;
    private static final double                       DEFAULT_CONNECTION_OPACITY = 0.65;
    private static final Color                        DEFAULT_SELECTION_COLOR    = Color.rgb(128, 0, 0, 0.25);
    private              double                       size;
    private              double                       width;
    private              double                       height;
    private              Canvas                       canvas;
    private              GraphicsContext              ctx;
    private              Color                        _tickMarkColor;
    private              ObjectProperty<Color>        tickMarkColor;
    private              Color                        _textColor;
    private              ObjectProperty<Color>        textColor;
    private              int                          _decimals;
    private              IntegerProperty              decimals;
    private              boolean                      _tickLabelsVisible;
    private              TickLabelOrientation         _tickLabelOrientation;
    private              double                       _connectionOpacity;
    private              DoubleProperty               connectionOpacity;
    private              Locale                       _locale;
    private              ObjectProperty<Locale>       locale;
    private              boolean                      _coloredConnections;
    private              BooleanProperty              coloredConnections;
    private              Color                        _connectionColor;
    private              ObjectProperty<Color>        connectionColor;
    private              Color                        _selectionColor;
    private              ObjectProperty<Color>        selectionColor;
    private              boolean                      _sortByCluster;
    private              BooleanProperty              sortByCluster;
    private              boolean                      _useFullCircle;
    private              BooleanProperty              useFullCircle;
    private              boolean                      _weightConnections;
    private              BooleanProperty              weightConnections;
    private              boolean                      _weightDots;
    private              BooleanProperty              weightDots;
    private              ObservableList<PlotItem>     items;
    private              ItemEventListener            itemListener;
    private              ListChangeListener<PlotItem> itemListListener;
    private              Map<Circle, PlotItem>        itemPaths;
    private              Map<Path, Connection>        paths;
    private              Map<Path, PlotItem[]>        connectionMap;
    private              PlotItem                     selectedItem;
    private              Tooltip                      tooltip;
    private              String                       formatString;
    private              ObservableList<Connection>   connections;


    // ******************** Constructors **************************************
    public ArcChart() {
        _tickMarkColor        = Color.BLACK;
        _textColor            = Color.BLACK;
        _decimals             = 0;
        _tickLabelsVisible    = true;
        _tickLabelOrientation = TickLabelOrientation.ORTHOGONAL;
        _connectionOpacity    = DEFAULT_CONNECTION_OPACITY;
        _locale               = Locale.getDefault();
        _coloredConnections   = false;
        _connectionColor      = Color.rgb(128, 128, 128, 0.25);
        _selectionColor       = DEFAULT_SELECTION_COLOR;
        _sortByCluster        = false;
        _useFullCircle        = false;
        _weightConnections    = false;
        _weightDots           = false;
        items                 = FXCollections.observableArrayList();
        itemListener          = e -> redraw();
        itemListListener      = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedItem -> addedItem.setOnItemEvent(itemListener));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedItem -> removedItem.removeItemEventListener(itemListener));
                }
            }
            validateData();
            redraw();
        };

        formatString          = "%." + _decimals + "f";

        connections           = FXCollections.observableArrayList();

        itemPaths             = new LinkedHashMap<>();
        paths                 = new LinkedHashMap<>();
        connectionMap         = new LinkedHashMap<>();

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

        ctx.setLineCap(StrokeLineCap.BUTT);

        tooltip = new Tooltip();
        tooltip.setAutoHide(true);

        getChildren().setAll(canvas);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        items.addListener(itemListListener);
        canvas.setOnMousePressed(e -> {
            /*
            paths.forEach((path, connection) -> {
                double eventX = e.getX();
                double eventY = e.getY();
                if (path.contains(eventX, eventY)) {
                    double tooltipX = eventX + canvas.getScene().getX() + canvas.getScene().getWindow().getX();
                    double tooltipY = eventY + canvas.getScene().getY() + canvas.getScene().getWindow().getY() - 25;
                    tooltip.setText(connection.getTooltipText());
                    tooltip.setX(tooltipX);
                    tooltip.setY(tooltipY);
                    tooltip.show(getScene().getWindow());

                    if (connectionMap.get(path).length > 1) {
                        Platform.runLater(() -> connection.fireConnectionEvent(new ConnectionEvent(connection, EventType.CONNECTION_SELECTED)));
                    }
                }
            });
            */
            itemPaths.forEach((itemPath, plotItem) -> {
                double eventX = e.getX();
                double eventY = e.getY();
                if (itemPath.contains(eventX, eventY)) {
                    Platform.runLater(() -> {
                        plotItem.fireItemEvent(new ItemEvent(plotItem, EventType.SELECTED));
                        selectedItem = plotItem;
                        redraw();
                    });
                }
            });
        });
        canvas.setOnMouseReleased(e -> {
            selectedItem = null;
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

    public void dispose() {
        items.forEach(item -> item.removeItemEventListener(itemListener));
        items.removeListener(itemListListener);
    }

    public Color getTickMarkColor() { return null == tickMarkColor ? _tickMarkColor : tickMarkColor.get(); }
    public void setTickMarkColor(final Color COLOR) {
        if (null == tickMarkColor) {
            _tickMarkColor = COLOR;
            redraw();
        } else {
            tickMarkColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> tickMarkColorProperty() {
        if (null == tickMarkColor) {
            tickMarkColor = new ObjectPropertyBase<Color>(_tickMarkColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ArcChart.this; }
                @Override public String getName() { return "tickMarkColor"; }
            };
            _tickMarkColor = null;
        }
        return tickMarkColor;
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
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ArcChart.this; }
                @Override public String getName() { return "textColor"; }
            };
            _textColor = null;
        }
        return textColor;
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
                @Override public Object getBean() { return ArcChart.this; }
                @Override public String getName() { return "decimals"; }
            };
        }
        return decimals;
    }

    public boolean getTickLabelsVisible() { return _tickLabelsVisible; }
    public void setTickLabelsVisible(final boolean VISIBLE) {
        _tickLabelsVisible = VISIBLE;
        redraw();
    }

    public TickLabelOrientation getTickLabelOrientation() { return _tickLabelOrientation; }
    public void setTickLabelOrientation(final TickLabelOrientation ORIENTATION) {
        _tickLabelOrientation = ORIENTATION;
        redraw();
    }

    public double getConnectionOpacity() { return null == connectionOpacity ? _connectionOpacity : connectionOpacity.get(); }
    public void setConnectionOpacity(final double OPACITY) {
        if (null == connectionOpacity) {
            _connectionOpacity = Helper.clamp(0.1, 1.0, OPACITY);
            redraw();
        } else {
            connectionOpacity.set(OPACITY);
        }
    }
    public DoubleProperty connectionOpacityProperty() {
        if (null == connectionOpacity) {
            connectionOpacity = new DoublePropertyBase(_connectionOpacity) {
                @Override protected void invalidated() {
                    set(Helper.clamp(0.1, 1.0, get()));
                    redraw();
                }
                @Override public Object getBean() { return ArcChart.this; }
                @Override public String getName() { return "connectionOpacity"; }
            };
        }
        return connectionOpacity;
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
                @Override public Object getBean() { return ArcChart.this; }
                @Override public String getName() { return "locale"; }
            };
        }
        _locale = null;
        return locale;
    }

    public boolean getColoredConnections() { return null == coloredConnections ? _coloredConnections : coloredConnections.get(); }
    public void setColoredConnections(final boolean COLORED) {
        if (null == coloredConnections) {
            _coloredConnections = COLORED;
            redraw();
        } else {
            coloredConnections.set(COLORED);
        }
    }
    public BooleanProperty coloredConnectionsProperty() {
        if (null == coloredConnections) {
            coloredConnections = new BooleanPropertyBase(_coloredConnections) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ArcChart.this; }
                @Override public String getName() { return "coloredConnections"; }
            };
        }
        return coloredConnections;
    }

    public Color getConnectionColor() { return null == connectionColor ? _connectionColor : connectionColor.get(); }
    public void setConnectionColor(final Color COLOR) {
        if (null == connectionColor) {
            _connectionColor = COLOR;
            redraw();
        } else {
            connectionColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> connectionColorProperty() {
        if (null == connectionColor) {
            connectionColor = new ObjectPropertyBase<>(_connectionColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ArcChart.this; }
                @Override public String getName() { return "connectionColor"; }
            };
            _connectionColor = null;
        }
        return connectionColor;
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
                @Override protected void invalidated() { redraw();}
                @Override public Object getBean() { return ArcChart.this; }
                @Override public String getName() { return "selectionColor"; }
            };
            _selectionColor = null;
        }
        return selectionColor;
    }

    public boolean getSortByCluster() { return null == sortByCluster ? _sortByCluster : sortByCluster.get(); }
    public void setSortByCluster(final boolean SORT) {
        if (null == sortByCluster) {
            _sortByCluster = SORT;
            redraw();
        } else {
            sortByCluster.set(SORT);
        }
    }
    public BooleanProperty sortByClusterProperty() {
        if (null == sortByCluster) {
            sortByCluster = new BooleanPropertyBase(_sortByCluster) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ArcChart.this; }
                @Override public String getName() { return "sortByCluster"; }
            };
        }
        return sortByCluster;
    }

    public boolean getUseFullCircle() { return null == useFullCircle ? _useFullCircle : useFullCircle.get(); }
    public void setUseFullCircle(final boolean USE) {
        if (null == useFullCircle) {
            _useFullCircle = USE;
            redraw();
        } else {
            useFullCircle.set(USE);
        }
    }
    public BooleanProperty useFullCircleProperty() {
        if (null == useFullCircle) {
            useFullCircle = new BooleanPropertyBase(_useFullCircle) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ArcChart.this; }
                @Override public String getName() { return "useFullCircle"; }
            };
        }
        return useFullCircle;
    }
    
    public boolean getWeightConnections() { return null == weightConnections ? _weightConnections : weightConnections.get(); }
    public void setWeightConnections(final boolean WEIGHT) {
        if (null == weightConnections) {
            _weightConnections = WEIGHT;
            redraw();
        } else {
            weightConnections.set(WEIGHT);
        }
    }
    public BooleanProperty weightConnectionsProperty() {
        if (null == weightConnections) {
            weightConnections = new BooleanPropertyBase(_weightConnections) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ArcChart.this;}
                @Override public String getName() { return "weightConnections"; }
            };
        }
        return weightConnections;
    }

    public boolean getWeightDots() { return null == weightDots ? _weightDots : weightDots.get(); }
    public void setWeightDots(final boolean WEIGHT) {
        if (null == weightDots) {
            _weightDots = WEIGHT;
            redraw();
        } else {
            weightDots.set(WEIGHT);
        }
    }
    public BooleanProperty weightDotsProperty() {
        if (null == weightDots) {
            weightDots = new BooleanPropertyBase(_weightDots) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return ArcChart.this;}
                @Override public String getName() { return "weightDots"; }
            };
        }
        return weightDots;
    }
    
    public PlotItem getSelectedItem() { return selectedItem; }
    public void setSelectedItem(final PlotItem SELECTED_ITEM) {
        selectedItem = SELECTED_ITEM;
        redraw();
    }
    public void resetSelectedItem() { setSelectedItem(null); }

    public List<PlotItem> getItems() { return items; }
    public void setItems(final PlotItem... ITEMS) { setItems(Arrays.asList(ITEMS)); }
    public void setItems(final List<PlotItem> ITEMS) {
        items.setAll(ITEMS);
        validateData();
    }
    public void addItem(final PlotItem ITEM) {
        if (!items.contains(ITEM)) { items.add(ITEM); }
        validateData();
    }
    public void removeItem(final PlotItem ITEM) { if (items.contains(ITEM)) { items.remove(ITEM); } }

    public void sortAscending() {
        Collections.sort(getItems(), Comparator.comparingDouble(PlotItem::getValue));
    }
    public void sortDescending() {
        Collections.sort(getItems(), (item1, item2) -> Double.compare(item2.getValue(), item1.getValue()));
    }

    public ObservableList<Connection> getConnections() { return connections; }

    public Connection getConnection(final PlotItem FROM, final PlotItem TO) {
        return connections.stream().filter(connection -> connection.getOutgoingItem().equals(FROM) && connection.getIncomingItem().equals(TO)).findFirst().orElse(null);
    }

    private void validateData() {
        connections.clear();
        Map<PlotItem, Double> incoming = new HashMap<>(getItems().size());
        for (PlotItem item : getItems()) {
            item.getOutgoing().forEach((outgoingItem, value) -> {
                if (incoming.containsKey(outgoingItem)) {
                    //incoming.put(outgoingItem, incoming.get(outgoingItem) + value);
                    connections.add(new Connection(item, outgoingItem, item.getOutgoing().get(outgoingItem), Color.TRANSPARENT));
                } else {
                    //incoming.put(outgoingItem, value);
                    connections.add(new Connection(outgoingItem, item, item.getOutgoing().get(outgoingItem), Color.TRANSPARENT));
                }
            });
        }
        for (PlotItem item : getItems()) {
            if (incoming.containsKey(item)) {
                double sumOfIncoming = incoming.get(item);
                if (item.getValue() < sumOfIncoming) {
                    item.setValue(sumOfIncoming);
                }
            }
        }
    }

    private void drawChart() {
        itemPaths.clear();
        paths.clear();
        connectionMap.clear();

        final int      noOfItems             = items.size();
        final double   centerY               = size * 0.5;
        final PlotItem itemWithMaxOutgoing   = items.stream().max(Comparator.comparingDouble(PlotItem::getSumOfOutgoing)).get();
        final double   minItemSize           = size * 0.02; // min diameter of item circle => 3% of height
        final double   maxItemSize           = size * 0.05; // max diameter of item circle => 10% of height
        final double   itemSizeFactor        = maxItemSize / itemWithMaxOutgoing.getSumOfOutgoing();
        final double   maxConnectionWidth    = maxItemSize;
        final double   connectionWidthFactor = maxConnectionWidth / Helper.getMaxValueInMap(itemWithMaxOutgoing.getOutgoing());
        final double   insetX                = maxItemSize;
        //final double   stepSizeX             = (size - 2 * insetX) / (noOfItems - 1);
        final double   stepSizeX             = (size - 2 * insetX) / (noOfItems);

        // Sort items by max sum of outgoing reversed
        List<PlotItem> sortedItems;
        if (getSortByCluster()) {
            sortedItems = new LinkedList<>();
            TreeSet<Cluster> clusters = new TreeSet<>(items.stream().filter(item -> null != item.getCluster()).map(PlotItem::getCluster).collect(Collectors.toSet()));
            clusters.forEach(cluster -> sortedItems.addAll(cluster.getSortedItems()));
            if (sortedItems.isEmpty()) { sortedItems.addAll(items); }
        } else {
            sortedItems = new LinkedList<>(items);
            Collections.sort(sortedItems, Comparator.comparingDouble(PlotItem::getSumOfOutgoing).reversed());
        }

        ctx.clearRect(0, 0, size, size);

        Map<PlotItem, Point> itemPoints = new LinkedHashMap<>();
        for (int i = 0 ; i < noOfItems ; i++) {
            PlotItem item     = sortedItems.get(i);
            //double   itemSize = Helper.clamp(minItemSize, maxItemSize, item.getSumOfOutgoing() * itemSizeFactor);
            double   itemX    = insetX + i * stepSizeX;
            double   itemY    = centerY;
            itemPoints.put(item, new Point(itemX, itemY));
        }

        // Draw incoming and outgoing
        sortedItems.forEach(item -> {
            Point    itemPoint = itemPoints.get(item);
            ctx.setLineCap(StrokeLineCap.BUTT);
            item.getOutgoing().forEach((outgoingItem, value) -> {
                Point  outgoingItemPoint = itemPoints.get(outgoingItem);
                double connectionWidth   = getWeightConnections() ? Helper.clamp(2, maxConnectionWidth, value * connectionWidthFactor) : 2;
                double arcWidth          = outgoingItemPoint.getX() - itemPoint.getX();

                Color  connectionStroke;
                Connection connection = getConnection(item, outgoingItem);
                if (getColoredConnections()) {
                    if (getSortByCluster() && null != item.getCluster()) {
                        connectionStroke = Helper.getColorWithOpacity(item.getCluster().getFill(), getConnectionOpacity());
                    } else if (null != connection && !connection.getFill().equals(Color.TRANSPARENT)) {
                        connectionStroke = Helper.getColorWithOpacity(connection.getFill(), getConnectionOpacity());
                    } else {
                        connectionStroke = Helper.getColorWithOpacity(item.getFill(), getConnectionOpacity());
                    }
                } else {
                    connectionStroke = getConnectionColor();
                }

                ctx.setStroke(connectionStroke);
                ctx.setLineWidth(connectionWidth);

                Path path = new Path();
                path.setStroke(connectionStroke);
                path.moveTo(itemPoint.getX(), itemPoint.getY());
                if (getUseFullCircle()) {
                    path.arcTo(arcWidth * 0.5, arcWidth * 0.5, 180, false, true, outgoingItemPoint.getX(), outgoingItemPoint.getY());
                } else {
                    if (arcWidth < 0) {
                        path.setStroke(Helper.getColorWithOpacity(connectionStroke, getConnectionOpacity() * 0.5));
                        path.arcTo(arcWidth * 0.5, arcWidth * 0.5, -180, false, false, outgoingItemPoint.getX(), outgoingItemPoint.getY());
                    } else {
                        path.arcTo(arcWidth * 0.5, arcWidth * 0.5, 180, false, true, outgoingItemPoint.getX(), outgoingItemPoint.getY());
                    }
                }
                path.draw(ctx, false, true);

                String tooltipText = new StringBuilder().append(item.getName())
                                                        .append(" -> ")
                                                        .append(outgoingItem.getName())
                                                        .append(" ")
                                                        .append(String.format(getLocale(), formatString, value))
                                                        .toString();

                if (null != connection) {
                    connection.setTooltipText(tooltipText);
                    paths.put(path, connection);
                    connectionMap.put(path, new PlotItem[] { item, outgoingItem });
                }
            });
        });

        if (null != selectedItem) {
            selectedItem.getOutgoing().forEach((outgoingItem, value) -> {
                Point    itemPoint = itemPoints.get(selectedItem);
                Point  outgoingItemPoint = itemPoints.get(outgoingItem);
                double connectionWidth   = getWeightConnections() ? Helper.clamp(2, maxConnectionWidth, value * connectionWidthFactor) : 2;
                double arcWidth          = outgoingItemPoint.getX() - itemPoint.getX();
                Color  connectionStroke = getConnectionColor();
                Connection connection = getConnection(selectedItem, outgoingItem);
                if (connection.getIncomingItem().equals(selectedItem) || connection.getOutgoingItem().equals(selectedItem)) {
                    if (getColoredConnections()) {
                        connectionStroke = getSelectionColor();
                    } else {
                        if (getSortByCluster() && null != selectedItem.getCluster()) {
                            connectionStroke = Helper.getColorWithOpacity(selectedItem.getCluster().getFill(), getConnectionOpacity());
                        } else {
                            connectionStroke = Helper.getColorWithOpacity(selectedItem.getFill(), getConnectionOpacity());
                        }
                    }
                }

                ctx.setStroke(connectionStroke);
                ctx.setLineWidth(connectionWidth);

                Path path = new Path();
                path.setStroke(connectionStroke);
                path.moveTo(itemPoint.getX(), itemPoint.getY());
                if (getUseFullCircle()) {
                    path.arcTo(arcWidth * 0.5, arcWidth * 0.5, 180, false, true, outgoingItemPoint.getX(), outgoingItemPoint.getY());
                } else {
                    if (arcWidth < 0) {
                        path.setStroke(Helper.getColorWithOpacity(connectionStroke, getConnectionOpacity() * 0.5));
                        path.arcTo(arcWidth * 0.5, arcWidth * 0.5, -180, false, false, outgoingItemPoint.getX(), outgoingItemPoint.getY());
                    } else {
                        path.arcTo(arcWidth * 0.5, arcWidth * 0.5, 180, false, true, outgoingItemPoint.getX(), outgoingItemPoint.getY());
                    }
                }
                path.draw(ctx, false, true);
            });
        }

        // Draw item dots
        Collections.reverse(sortedItems);
        sortedItems.forEach(item -> {
            double itemSize  = getWeightDots() ? Helper.clamp(minItemSize, maxItemSize, item.getSumOfOutgoing() * itemSizeFactor) : minItemSize;
            Point  itemPoint = itemPoints.get(item);
            double itemX     = itemPoint.getX();
            double itemY     = itemPoint.getY();
            if (getSortByCluster()) {
                if (null == item.getCluster()) {
                    ctx.setFill(item.getFill());
                } else {
                    ctx.setFill(item.getCluster().getFill());
                }
            } else {
                ctx.setFill(item.getFill());
            }
            ctx.fillOval(itemX - itemSize * 0.5, itemY - itemSize * 0.5, itemSize, itemSize);

            Circle dot = new Circle(itemX, itemY, itemSize * 0.5);
            itemPaths.put(dot, item);

            // Draw item name
            if (getTickLabelsVisible()) {
                ctx.save();
                ctx.setFill(getTextColor());
                if (null == selectedItem) {
                    ctx.setFont(Fonts.opensansRegular(size * 0.016));
                } else {
                    ctx.setFont(selectedItem.getName().equals(item.getName()) ? Fonts.opensansRegular(size * 0.022) : Fonts.opensansRegular(size * 0.016));
                }
                ctx.setTextAlign(TickLabelOrientation.ORTHOGONAL == getTickLabelOrientation() ? TextAlignment.LEFT : TextAlignment.CENTER);
                ctx.setTextBaseline(VPos.CENTER);
                double offsetY = getWeightDots() ? maxItemSize * 0.75 : minItemSize * 0.75;
                double itemNamePointX = itemPoint.getX();
                double itemNamePointY = itemPoint.getY() + (TickLabelOrientation.ORTHOGONAL == getTickLabelOrientation() ? offsetY : 0);

                ctx.translate(itemNamePointX, itemNamePointY);
                rotateContextForText(ctx, 0, 270, getTickLabelOrientation());
                ctx.fillText(item.getName(), 0, 0);
                ctx.restore();
            }
        });
    }

    private void rotateContextForText(final GraphicsContext CTX, final double START_ANGLE, final double TEXT_ANGLE, final TickLabelOrientation ORIENTATION) {
        switch (ORIENTATION) {
            case ORTHOGONAL:
                if ((360 - START_ANGLE - TEXT_ANGLE) % 360 > 90 && (360 - START_ANGLE - TEXT_ANGLE) % 360 < 270) {
                    CTX.rotate((180 - START_ANGLE - TEXT_ANGLE) % 360);
                } else {
                    CTX.rotate((360 - START_ANGLE - TEXT_ANGLE) % 360);
                }
                break;
            case TANGENT:
                if ((360 - START_ANGLE - TEXT_ANGLE - 90) % 360 > 90 && (360 - START_ANGLE - TEXT_ANGLE - 90) % 360 < 270) {
                    CTX.rotate((90 - START_ANGLE - TEXT_ANGLE) % 360);
                } else {
                    CTX.rotate((270 - START_ANGLE - TEXT_ANGLE) % 360);
                }
                break;
            case HORIZONTAL:
            default:
                break;
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            canvas.setWidth(size);
            canvas.setHeight(size);
            canvas.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            redraw();
        }
    }

    private void redraw() {
        drawChart();
    }
}
