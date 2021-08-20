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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.hansolo.fx.charts.data.Connection;
import eu.hansolo.fx.charts.data.PlotItem;
import eu.hansolo.fx.charts.event.ConnectionEvent;
import eu.hansolo.fx.charts.event.EventType;
import eu.hansolo.fx.charts.event.ItemEvent;
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.Point;
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
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


/**
 * User: hansolo
 * Date: 17.11.17
 * Time: 10:41
 */
@DefaultProperty("children")
public class CircularPlot extends Region {
    private static final double                       PREFERRED_WIDTH            = 500;
    private static final double                       PREFERRED_HEIGHT           = 500;
    private static final double                       MINIMUM_WIDTH              = 50;
    private static final double                       MINIMUM_HEIGHT             = 50;
    private static final double                       MAXIMUM_WIDTH              = 4096;
    private static final double                       MAXIMUM_HEIGHT             = 4096;
    private static final double                       DEFAULT_SEGMENT_GAP        = 4;
    private static final double                       DEFAULT_CONNECTION_OPACITY = 0.65;
    private static final double                       MAJOR_TICK_MARK_LENGTH     = 0.0125;
    private static final double                       MEDIUM_TICK_MARK_LENGTH    = 0.01;
    private static final double                       MINOR_TICK_MARK_LENGTH     = 0.0075;
    private static final double                       TICK_MARK_WIDTH            = 0.001;
    private static final double                       ANGLE_OFFSET               = 90;
    private              double                       size;
    private              double                       width;
    private              double                       height;
    private              Canvas                       canvas;
    private              GraphicsContext              ctx;
    private              double                       mainLineWidth;
    private              double                       outgoingLineWidth;
    private              double                       tickMarkWidth;
    private              double                       chartSize;
    private              double                       chartOffset;
    private              double                       innerChartSize;
    private              double                       innerChartOffset;
    private              double                       centerX;
    private              double                       centerY;
    private              Color                        _tickMarkColor;
    private              ObjectProperty<Color>        tickMarkColor;
    private              Color                        _textColor;
    private              ObjectProperty<Color>        textColor;
    private              int                          _decimals;
    private              IntegerProperty              decimals;
    private              double                       _segmentGap;
    private              DoubleProperty               segmentGap;
    private              boolean                      _showFlowDirection;
    private              BooleanProperty              showFlowDirection;
    private              boolean                      _minorTickMarksVisible;
    private              boolean                      _mediumTickMarksVisible;
    private              boolean                      _majorTickMarksVisible;
    private              boolean                      _tickLabelsVisible;
    private              TickLabelOrientation         _tickLabelOrientation;
    private              boolean                      _onlyFirstAndLastTickLabelVisible;
    private              double                       _connectionOpacity;
    private              DoubleProperty               connectionOpacity;
    private              Locale                       _locale;
    private              ObjectProperty<Locale>       locale;
    private              ObservableList<PlotItem>     items;
    private              ItemEventListener            itemListener;
    private              ListChangeListener<PlotItem> itemListListener;
    private              Map<Path, PlotItem>          itemPaths;
    private              Map<Path, Connection>        paths;
    private              Map<Path, PlotItem[]>        connectionMap;
    private              Tooltip                      tooltip;
    private              String                       formatString;
    private              ObservableList<Connection>   connections;

    // ******************** Constructors **************************************
    public CircularPlot() {
        _tickMarkColor                    = Color.BLACK;
        _textColor                        = Color.BLACK;
        _segmentGap                       = DEFAULT_SEGMENT_GAP;
        _decimals                         = 0;
        _showFlowDirection                = false;
        _minorTickMarksVisible            = true;
        _mediumTickMarksVisible           = true;
        _majorTickMarksVisible            = true;
        _tickLabelsVisible                = true;
        _tickLabelOrientation             = TickLabelOrientation.TANGENT;
        _onlyFirstAndLastTickLabelVisible = true;
        _connectionOpacity                = DEFAULT_CONNECTION_OPACITY;
        _locale                           = Locale.getDefault();
        items                             = FXCollections.observableArrayList();
        itemListener                      = e -> redraw();
        itemListListener                  = c -> {
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

        formatString                      = "%." + _decimals + "f";      
        connections                       = FXCollections.observableArrayList();
        itemPaths                         = new LinkedHashMap<>();
        paths                             = new LinkedHashMap<>();
        connectionMap                     = new LinkedHashMap<>();

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
        canvas.setOnMouseClicked(e -> {
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
                        //PlotItem item0 = connectionMap.get(path)[0];
                        //PlotItem item1 = connectionMap.get(path)[1];
                        Platform.runLater(() -> {
                            //item0.fireItemEvent(new ItemEvent(item0, EventType.CONNECTION_SELECTED_FROM));
                            //item1.fireItemEvent(new ItemEvent(item1, EventType.CONNECTION_SELECTED_TO));
                        	
                        	// ConectionEvent with original mouseEvent attached for further information (isCtrlDown ...)
                        	// and plot for redraw plot after connection has been selected and properties may have changed
                            connection.fireConnectionEvent(new ConnectionEvent(connection, EventType.CONNECTION_SELECTED, e));
                       });
                    }
                }
            });
            itemPaths.forEach((itemPath, plotItem) -> {
                double eventX = e.getX();
                double eventY = e.getY();
                if (itemPath.contains(eventX, eventY)) {
                	// ItemEvent with original mouseEvent attached for further information (isCtrlDown ...)
                    Platform.runLater(() -> plotItem.fireItemEvent(new ItemEvent(plotItem, EventType.SELECTED, e)));
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
                @Override public Object getBean() { return CircularPlot.this; }
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
                @Override public Object getBean() { return CircularPlot.this; }
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
                @Override public Object getBean() { return CircularPlot.this; }
                @Override public String getName() { return "decimals"; }
            };
        }
        return decimals;
    }

    public double getSegmentGap() { return null == segmentGap ? _segmentGap : segmentGap.get(); }
    public void setSegmentGap(final double GAP) {
        if (null == segmentGap) {
            _segmentGap = Helper.clamp(0, 10, GAP);
            redraw();
        } else {
            segmentGap.set(GAP);
        }
    }
    public DoubleProperty segmentGapProperty() {
        if (null == segmentGap) {
            segmentGap = new DoublePropertyBase(_segmentGap) {
                @Override protected void invalidated() {
                    set(Helper.clamp(0, 10, get()));
                    redraw();
                }
                @Override public Object getBean() { return CircularPlot.this; }
                @Override public String getName() { return "segmentGap"; }
            };
        }
        return segmentGap;
    }

    public boolean getShowFlowDirection() { return null == showFlowDirection ? _showFlowDirection : showFlowDirection.get(); }
    public void setShowFlowDirection(final boolean SHOW) {
        if (null == showFlowDirection) {
            _showFlowDirection = SHOW;
            drawChart();
        } else {
            showFlowDirection.set(SHOW);
        }
    }
    public BooleanProperty showFlowDirectionProperty() {
        if (null == showFlowDirection) {
            showFlowDirection = new BooleanPropertyBase(_showFlowDirection) {
                @Override protected void invalidated() { drawChart(); }
                @Override public Object getBean() { return CircularPlot.this; }
                @Override public String getName() { return "showFlowDirection"; }
            };
        }
        return showFlowDirection;
    }

    public boolean getMinorTickMarksVisible() { return _minorTickMarksVisible; }
    public void setMinorTickMarksVisible(final boolean VISIBLE) {
        _minorTickMarksVisible = VISIBLE;
        redraw();
    }

    public boolean getMediumTickMarksVisible() { return _mediumTickMarksVisible; }
    public void setMediumTickMarksVisible(final boolean VISIBLE) {
        _mediumTickMarksVisible = VISIBLE;
        redraw();
    }

    public boolean getMajorTickMarksVisible() { return _majorTickMarksVisible; }
    public void setMajorTickMarksVisible(final boolean VISIBLE) {
        _majorTickMarksVisible = VISIBLE;
        redraw();
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

    public boolean isOnlyFirstAndLastTickLabelVisible() { return _onlyFirstAndLastTickLabelVisible; }
    public void setOnlyFirstAndLastTickLabelVisible(final boolean VISIBLE) {
        _onlyFirstAndLastTickLabelVisible = VISIBLE;
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
                @Override public Object getBean() { return CircularPlot.this; }
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
                @Override public Object getBean() { return CircularPlot.this; }
                @Override public String getName() { return "locale"; }
            };
        }
        _locale = null;
        return locale;
    }

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

    protected void validateData() {
        connections.clear();
        Map<PlotItem, Double> incoming = new HashMap<>(getItems().size());
        for (PlotItem item : getItems()) {
            item.getOutgoing().forEach((outgoingItem, value) -> {
                if (incoming.containsKey(outgoingItem)) {
                    //incoming.put(outgoingItem, incoming.get(outgoingItem) + value);
                	connections.add(createConnection(item, outgoingItem, item.getOutgoing().get(outgoingItem), Color.TRANSPARENT));
                } else {
                    //incoming.put(outgoingItem, value);
                	connections.add(createConnection(outgoingItem, item, item.getOutgoing().get(outgoingItem), Color.TRANSPARENT));
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
    
    /**
     * Overrideable connection factory method
     * 
     * @param INCOMING_ITEM
     * @param OUTGOING_ITEM
     * @param VALUE
     * @param FILL
     * @return (not null) Connection
     */
    protected Connection createConnection(final PlotItem INCOMING_ITEM, final PlotItem OUTGOING_ITEM, final double VALUE, final Color FILL) {
    	return new Connection(INCOMING_ITEM, OUTGOING_ITEM, VALUE, FILL);
    }

    /**
     * Overrideable drawChart() method
     */
    protected void drawChart() {
        itemPaths.clear();
        paths.clear();
        connectionMap.clear();

        TickLabelOrientation tickLabelOrientation = getTickLabelOrientation();
        if (TickLabelOrientation.ORTHOGONAL == tickLabelOrientation) {
            chartSize         = size * 0.75;
            mainLineWidth     = chartSize * 0.045;
            outgoingLineWidth = chartSize * 0.015;
            tickMarkWidth     = chartSize * TICK_MARK_WIDTH;
            chartOffset       = (size - chartSize) * 0.5;
            innerChartOffset  = chartOffset + chartSize * 0.032;
            innerChartSize    = chartSize - chartSize * 0.064;
            centerX           = size * 0.5;
            centerY           = size * 0.5;
        }

        ctx.clearRect(0, 0, size, size);

        double sum         = items.stream().mapToDouble(PlotItem::getValue).sum();
        int    noOfItems   = items.size();
        double innerRadius = chartSize * 0.5 - mainLineWidth * 0.5;
        double outerRadius = chartSize * 0.5 + mainLineWidth * 0.5;

        Map<PlotItem, ChartItemParameter> parameterMap = new HashMap<>(items.size());

        // Draw outer circle segments and tickmarks
        double angleStep = (360.0 - (noOfItems * getSegmentGap())) / sum;
        double angle     = -ANGLE_OFFSET;
        for (int i = 0 ; i < noOfItems ; i++) {
            PlotItem item          = items.get(i);
            double   angleRange    = item.getValue() * angleStep;
            double   sumOfOutgoing = item.getOutgoing().values().stream().mapToDouble(Double::doubleValue).sum();

            // Store item specific angle and angleRange for later use
            parameterMap.put(item, new ChartItemParameter(angle + ANGLE_OFFSET, angleRange));

            // Draw outer circle segments
            ctx.setLineWidth(mainLineWidth);
            ctx.setStroke(item.getFill());
            ctx.strokeArc(chartOffset, chartOffset, chartSize, chartSize, -angle, -angleRange, ArcType.OPEN);

            // Create paths for click detection
            double[] xy1 = Helper.rotatePointAroundRotationCenter(centerX - outerRadius, centerY, centerX, centerY, angle - 180);
            double[] xy2 = Helper.rotatePointAroundRotationCenter(centerX - outerRadius, centerY, centerX, centerY, angle + angleRange - 180);
            double[] xy3 = Helper.rotatePointAroundRotationCenter(centerX - innerRadius, centerY, centerX, centerY, angle + angleRange - 180);
            double[] xy4 = Helper.rotatePointAroundRotationCenter(centerX - innerRadius, centerY, centerX, centerY, angle - 180);

            Path itemPath = new Path();
            itemPath.setFill(Color.TRANSPARENT);
            itemPath.moveTo(xy1[0], xy1[1]);
            itemPath.arcTo(outerRadius, outerRadius, angle + angleRange - 180, false, true, xy2[0], xy2[1]);
            itemPath.lineTo(xy3[0], xy3[1]);
            itemPath.arcTo(innerRadius, innerRadius, -angle - angleRange - 180, false, false, xy4[0], xy4[1]);
            itemPath.lineTo(xy1[0], xy1[1]);
            itemPath.closePath();
            itemPath.draw(ctx, true, false);

            itemPaths.put(itemPath, item);

            // Draw sum of outgoing at the end of the segment
            double outgoingAngleRange = sumOfOutgoing * angleStep;
            ctx.setLineWidth(outgoingLineWidth);
            ctx.strokeArc(innerChartOffset, innerChartOffset, innerChartSize, innerChartSize, -angle - angleRange + outgoingAngleRange, -outgoingAngleRange, ArcType.OPEN);

            // Draw tickmarks
            ctx.setLineWidth(tickMarkWidth);
            ctx.setStroke(getTickMarkColor());
            ctx.strokeArc(chartOffset - mainLineWidth * 0.5, chartOffset - mainLineWidth * 0.5, chartSize + mainLineWidth, chartSize + mainLineWidth, -angle, -angleRange, ArcType.OPEN);
            drawTickMarks(item, angle, angleRange);

            // Increment angle
            angle += angleRange + getSegmentGap();
        }

        // Draw incoming and outgoing
        double sinValue;
        double cosValue;
        double innerRingRadius  = chartSize * 0.462;
        double innerRingRadius2 = chartSize * 0.475;
        double outerPointRadius = chartSize * 0.26;
        double innerPointRadius = chartSize * 0.20;
        for (int i = 0 ; i < noOfItems ; i++) {
            PlotItem           item           = items.get(i);
            ChartItemParameter itemParameter  = parameterMap.get(item);
            double             itemStartAngle = itemParameter.getStartAngle();
            double             itemAngleRange = itemParameter.getAngleRange();
            double             itemEndAngle   = itemParameter.getEndAngle();

            // Draw item name
            ctx.save();
            ctx.setFill(Color.TRANSPARENT.equals(item.getTextColor()) ? getTextColor() : item.getTextColor());
            if (item.getFont().getName().equals(Font.getDefault().getName())) {
            ctx.setFont(Fonts.latoRegular(size * 0.02));
            } else {
                ctx.setFont(Fonts.latoRegular(size * 0.02));
            }
            ctx.setTextAlign(TextAlignment.CENTER);
            ctx.setTextBaseline(VPos.CENTER);
            sinValue = Math.sin(Math.toRadians(-itemStartAngle - itemAngleRange * 0.5 - 180));
            cosValue = Math.cos(Math.toRadians(-itemStartAngle - itemAngleRange * 0.5 - 180));
            double itemNamePointX = centerX + chartSize * 0.56 * sinValue;
            double itemNamePointY = centerY + chartSize * 0.56 * cosValue;

            if (TickLabelOrientation.ORTHOGONAL == tickLabelOrientation) {
                Font font = Fonts.latoRegular(size * 0.02);
                Text measureText = new Text(item.getName());
                measureText.setFont(font);
                double textWidth = measureText.getLayoutBounds().getWidth();
                itemNamePointX += textWidth * 0.33 * sinValue;
                itemNamePointY += textWidth * 0.33 * cosValue;
            }

            ctx.translate(itemNamePointX, itemNamePointY);
            rotateContextForText(ctx, -itemStartAngle, -itemAngleRange * 0.5 + ANGLE_OFFSET, tickLabelOrientation);
            ctx.fillText(item.getName(), 0, 0);
            ctx.restore();

            // Draw connections between items
            for (PlotItem outgoingItem : item.getOutgoing().keySet()) {
                ChartItemParameter outgoingItemParameter = parameterMap.get(outgoingItem);
                double             outgoingValue         = item.getOutgoing().get(outgoingItem);
                double             outgoingAngleRange    = outgoingValue * angleStep;

                int indexDelta   = items.indexOf(item) - items.indexOf(outgoingItem);
                outerPointRadius = outerPointRadius / (Math.abs(indexDelta) + 0.75);
                innerPointRadius = innerPointRadius / (Math.abs(indexDelta) + 0.75);

                // Points in source chart item
                sinValue = Math.sin(Math.toRadians(-itemEndAngle + 180 + itemParameter.getNextOutgoingStartAngle()));
                cosValue = Math.cos(Math.toRadians(-itemEndAngle + 180 + itemParameter.getNextOutgoingStartAngle()));
                Point p0 = new Point(centerX + innerRingRadius * sinValue, centerY + innerRingRadius * cosValue);

                sinValue = Math.sin(Math.toRadians(-itemEndAngle + 180 + outgoingAngleRange + itemParameter.getNextOutgoingStartAngle()));
                cosValue = Math.cos(Math.toRadians(-itemEndAngle + 180 + outgoingAngleRange + itemParameter.getNextOutgoingStartAngle()));
                Point p1 = new Point(centerX + innerRingRadius * sinValue, centerY + innerRingRadius * cosValue);

                // Point between p0 and p1
                sinValue  = Math.sin(Math.toRadians(-itemEndAngle + outgoingAngleRange * 0.5 + 180 + itemParameter.getNextOutgoingStartAngle()));
                cosValue  = Math.cos(Math.toRadians(-itemEndAngle + outgoingAngleRange * 0.5 + 180 + itemParameter.getNextOutgoingStartAngle()));
                Point p01 = new Point(centerX + innerRingRadius * sinValue, centerY + innerRingRadius * cosValue);

                // Points in target chart item
                sinValue = Math.sin(Math.toRadians(-outgoingItemParameter.getNextIncomingStartAngle() + 180));
                cosValue = Math.cos(Math.toRadians(-outgoingItemParameter.getNextIncomingStartAngle() + 180));
                Point p2 = new Point(centerX + innerRingRadius * sinValue, centerY + innerRingRadius * cosValue);

                sinValue = Math.sin(Math.toRadians(-outgoingItemParameter.getNextIncomingStartAngle() - outgoingAngleRange + 180));
                cosValue = Math.cos(Math.toRadians(-outgoingItemParameter.getNextIncomingStartAngle() - outgoingAngleRange + 180));
                Point p3 = new Point(centerX + innerRingRadius * sinValue, centerY + innerRingRadius * cosValue);

                // Point between p2 and p3
                sinValue  = Math.sin(Math.toRadians(-outgoingItemParameter.getNextIncomingStartAngle() - outgoingAngleRange * 0.5 + 180));
                cosValue  = Math.cos(Math.toRadians(-outgoingItemParameter.getNextIncomingStartAngle() - outgoingAngleRange * 0.5 + 180));
                Point p23;
                if (getShowFlowDirection()) {
                    p23 = new Point(centerX + innerRingRadius2 * sinValue, centerY + innerRingRadius2 * cosValue);
                } else {
                    p23 = new Point(centerX + innerRingRadius * sinValue, centerY + innerRingRadius * cosValue);
                }

                // Points between source and target chart item
                sinValue = Math.sin(Math.toRadians((-itemEndAngle - outgoingItemParameter.getNextIncomingStartAngle()) * 0.5 + 180 + itemParameter.getNextOutgoingStartAngle()));
                cosValue = Math.cos(Math.toRadians((-itemEndAngle - outgoingItemParameter.getNextIncomingStartAngle()) * 0.5 + 180 + itemParameter.getNextOutgoingStartAngle()));
                Point p4, p5;
                if (indexDelta < 0) {
                    p4 = new Point(centerX + outerPointRadius * sinValue, centerY + outerPointRadius * cosValue);
                    p5 = new Point(centerX + innerPointRadius * sinValue, centerY + innerPointRadius * cosValue);
                } else {
                    p4 = new Point(centerX + innerPointRadius * sinValue, centerY + innerPointRadius * cosValue);
                    p5 = new Point(centerX + outerPointRadius * sinValue, centerY + outerPointRadius * cosValue);
                }

                // Store next incoming start angle
                outgoingItemParameter.setNextIncomingStartAngle(outgoingItemParameter.getNextIncomingStartAngle() + outgoingAngleRange);

                // Store next outgoing start angle
                itemParameter.setNextOutgoingStartAngle(itemParameter.getNextOutgoingStartAngle() + outgoingAngleRange);

                Color      connectionFill;
                Connection connection = getConnection(item, outgoingItem);
                if (null != connection && !connection.getFill().equals(Color.TRANSPARENT)) {
                    connectionFill = Helper.getColorWithOpacity(connection.getFill(), getConnectionOpacity());
                } else {
                    connectionFill = Helper.getColorWithOpacity(item.getFill(), getConnectionOpacity());
                }
      
                // Draw flow
                Path path = new Path();
                //path.setFill(Helper.getColorWithOpacity(item.getFill(), getConnectionOpacity()));
                path.setFill(connectionFill);
                path.moveTo(p0.getX(), p0.getY());
                path.quadraticCurveTo(p4.getX(), p4.getY(), p2.getX(), p2.getY());             // curve from p4 -> p4 -> p2
                if (getShowFlowDirection()) {
                    path.lineTo(p23.getX(), p23.getY());                                       // line from p2 -> p23
                    path.lineTo(p3.getX(), p3.getY());                                         // line from p23 -> p3
                } else {
                    path.quadraticCurveTo(p23.getX(), p23.getY(), p3.getX(), p3.getY());       // curve from p2 -> p23 -> p3
                }
                path.quadraticCurveTo(p5.getX(), p5.getY(), p1.getX(), p1.getY());             // curve from p3 -> p5 -> p1
                path.quadraticCurveTo(p01.getX(), p01.getY(), p0.getX(), p0.getY());           // curve from p1 -> p01 -> p0
                path.closePath();
                path.draw(ctx, true, false);

                String tooltipText = new StringBuilder().append(item.getName())
                                                        .append(" -> ")
                                                        .append(outgoingItem.getName())
                                                        .append(" ")
                                                        .append(String.format(getLocale(), formatString, outgoingValue))
                                                        .toString();
                if (null != connection) {
                    connection.setTooltipText(tooltipText);
                    paths.put(path, connection);
                    connectionMap.put(path, new PlotItem[] { item, outgoingItem });
                }

                /*
                ctx.setFill(Helper.getColorWithOpacity(item.getFillColor(), getConnectionOpacity()));
                ctx.beginPath();
                ctx.moveTo(p0.getX(), p0.getY());
                ctx.quadraticCurveTo(p4.getX(), p4.getY(), p2.getX(), p2.getY());             // curve from p4 -> p4 -> p2
                if (getShowFlowDirection()) {
                    ctx.lineTo(p23.getX(), p23.getY());                                       // line from p2 -> p23
                    ctx.lineTo(p3.getX(), p3.getY());                                         // line from p23 -> p3
                } else {
                    ctx.quadraticCurveTo(p23.getX(), p23.getY(), p3.getX(), p3.getY());       // curve from p2 -> p23 -> p3
                }
                ctx.quadraticCurveTo(p5.getX(), p5.getY(), p1.getX(), p1.getY());             // curve from p3 -> p5 -> p1
                ctx.quadraticCurveTo(p01.getX(), p01.getY(), p0.getX(), p0.getY());           // curve from p1 -> p01 -> p0
                ctx.closePath();
                ctx.fill();
                */
            }
        }
    }

    /**
     * Overrideable drawTickMarks() method
     */
    protected void drawTickMarks(final PlotItem ITEM, final double START_ANGLE, final double ANGLE_RANGE) {
        double        sinValue;
        double        cosValue;
        double[]      scaleParameters              = Helper.calcAutoScale(0, ITEM.getValue());
        double        minorTickSpace               = scaleParameters[0];
        double        majorTickSpace               = scaleParameters[1];
        double        minValue                     = 0; //scaleParameters[2];
        double        maxValue                     = ITEM.getValue(); //scaleParameters[3];
        double        range                        = maxValue - minValue;
        double        angleStep                    = (ANGLE_RANGE / range) * minorTickSpace;
        BigDecimal    minorTickSpaceBD             = BigDecimal.valueOf(minorTickSpace);
        BigDecimal    majorTickSpaceBD             = BigDecimal.valueOf(majorTickSpace);
        BigDecimal    mediumCheck2                 = BigDecimal.valueOf(2 * minorTickSpace);
        BigDecimal    mediumCheck5                 = BigDecimal.valueOf(5 * minorTickSpace);
        BigDecimal    counterBD                    = BigDecimal.valueOf(0);
        double        counter                      = 0;

        boolean       majorTickMarksVisible        = getMajorTickMarksVisible();
        boolean       mediumTickMarksVisible       = getMediumTickMarksVisible();
        boolean       minorTickMarksVisible        = getMinorTickMarksVisible();
        boolean       tickLabelsVisible            = getTickLabelsVisible();
        boolean       onlyFirstAndLastLabelVisible = isOnlyFirstAndLastTickLabelVisible();

        double        orthTextFactor               = 0.542;
        double        tickLabelFontSize            = getDecimals() == 0 ? 0.018 * chartSize : 0.017 * chartSize;
        double        tickLabelOrientationFactor   = TickLabelOrientation.HORIZONTAL == getTickLabelOrientation() ? 0.9 : 1.0;
        Font          tickLabelFont                = Fonts.latoRegular(tickLabelFontSize * tickLabelOrientationFactor);

        // Variables needed for tickmarks
        double innerPointX, innerPointY;
        double outerPointX, outerPointY;
        double outerMediumPointX, outerMediumPointY;
        double outerMinorPointX, outerMinorPointY;
        double textPointX, textPointY;

        // Set the general tickmark color
        ctx.setStroke(getTickMarkColor());
        ctx.setFill(getTickMarkColor());
        ctx.setLineCap(StrokeLineCap.BUTT);
        ctx.setLineWidth(size * TICK_MARK_WIDTH);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);

        // Main loop
        BigDecimal tmpStepBD = new BigDecimal(angleStep);
        tmpStepBD            = tmpStepBD.setScale(3, RoundingMode.HALF_UP);
        double tmpStep       = tmpStepBD.doubleValue();
        double angle         = 0;

        for (double i = 0 ; Double.compare(-ANGLE_RANGE - tmpStep, i) <= 0 ; i -= tmpStep) {
            sinValue          = Math.sin(Math.toRadians(-START_ANGLE + angle + ANGLE_OFFSET));
            cosValue          = Math.cos(Math.toRadians(-START_ANGLE + angle + ANGLE_OFFSET));

            innerPointX       = centerX + chartSize * 0.5225 * sinValue;
            innerPointY       = centerY + chartSize * 0.5225 * cosValue;
            outerPointX       = centerX + chartSize * (0.5175 + MAJOR_TICK_MARK_LENGTH) * sinValue;
            outerPointY       = centerY + chartSize * (0.5175 + MAJOR_TICK_MARK_LENGTH) * cosValue;
            outerMediumPointX = centerX + chartSize * (0.5175 + MEDIUM_TICK_MARK_LENGTH) * sinValue;
            outerMediumPointY = centerY + chartSize * (0.5175 + MEDIUM_TICK_MARK_LENGTH) * cosValue;
            outerMinorPointX  = centerX + chartSize * (0.5175 + MINOR_TICK_MARK_LENGTH) * sinValue;
            outerMinorPointY  = centerY + chartSize * (0.5175 + MINOR_TICK_MARK_LENGTH) * cosValue;
            textPointX        = centerX + chartSize * orthTextFactor * sinValue;
            textPointY        = centerY + chartSize * orthTextFactor * cosValue;

            if (Double.compare(counterBD.remainder(majorTickSpaceBD).doubleValue(), 0.0) == 0) {
                // Draw major tick mark
                if (majorTickMarksVisible) {
                    ctx.strokeLine(innerPointX, innerPointY, outerPointX, outerPointY);
                } else if (minorTickMarksVisible) {
                    ctx.strokeLine(innerPointX, innerPointY, outerMinorPointX, outerMinorPointY);
                }

                // Draw tick label text
                if (tickLabelsVisible) {
                    ctx.save();
                    ctx.translate(textPointX, textPointY);

                    rotateContextForText(ctx, -START_ANGLE, angle, getTickLabelOrientation());
                    ctx.setFont(tickLabelFont);

                    if (!onlyFirstAndLastLabelVisible) {
                        ctx.setFill(getTextColor());
                    } else {
                        if ((Double.compare(counter, minValue) == 0 ||
                             counter + majorTickSpace > maxValue)) {
                             //Double.compare(counter, maxValue) == 0)) { // only if nice min-max values will be used
                            ctx.setFill(getTextColor());
                        } else {
                            ctx.setFill(Color.TRANSPARENT);
                        }
                    }
                    ctx.fillText(Helper.format(counter, getDecimals(), getLocale()), 0, 0);
                    ctx.restore();
                }
            } else if (mediumTickMarksVisible &&
                       Double.compare(minorTickSpaceBD.remainder(mediumCheck2).doubleValue(), 0.0) != 0.0 &&
                       Double.compare(counterBD.remainder(mediumCheck5).doubleValue(), 0.0) == 0.0) {
                // Draw medium tick mark
                ctx.strokeLine(innerPointX, innerPointY, outerMediumPointX, outerMediumPointY);
            } else if (minorTickMarksVisible && Double.compare(counterBD.remainder(minorTickSpaceBD).doubleValue(), 0.0) == 0) {
                // Draw minor tick mark
                ctx.strokeLine(innerPointX, innerPointY, outerMinorPointX, outerMinorPointY);
            }
            counterBD = counterBD.add(minorTickSpaceBD);
            counter   = counterBD.doubleValue();
            if (counter > maxValue) break;
            angle = (angle - angleStep);
        }
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
        width             = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height            = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size              = width < height ? width : height;
        chartSize         = size * 0.85;
        mainLineWidth     = chartSize * 0.045;
        outgoingLineWidth = chartSize * 0.015;
        tickMarkWidth     = chartSize * TICK_MARK_WIDTH;
        chartOffset       = (size - chartSize) * 0.5;
        innerChartOffset  = chartOffset + chartSize * 0.032;
        innerChartSize    = chartSize - chartSize * 0.064;
        centerX           = size * 0.5;
        centerY           = size * 0.5;

        if (width > 0 && height > 0) {
            canvas.setWidth(size);
            canvas.setHeight(size);
            canvas.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            redraw();
        }
    }

    /**
     * Overrideable redraw()
     */
    public void redraw() {
        drawChart();
    }


    // ******************** Inner Classes *************************************
    private class ChartItemParameter {
        private double startAngle;
        private double angleRange;
        private double endAngle;
        private double nextIncomingStartAngle;
        private double nextOutgoingStartAngle;


        // ******************** Constructors **********************************
        public ChartItemParameter() {
            this(0, 0);
        }
        public ChartItemParameter(final double START_ANGLE, final double ANGLE_RANGE) {
            startAngle             = START_ANGLE;
            angleRange             = ANGLE_RANGE;
            endAngle               = START_ANGLE + ANGLE_RANGE;
            nextIncomingStartAngle = START_ANGLE;
            nextOutgoingStartAngle = 0;
        }


        // ******************** Methods ***************************************
        public double getStartAngle() { return startAngle; }
        public void setStartAngle(final double START_ANGLE) {
            startAngle             = START_ANGLE;
            endAngle               = startAngle + getAngleRange();
            nextIncomingStartAngle = startAngle;
            nextOutgoingStartAngle = 0;
        }

        public double getAngleRange() { return angleRange; }
        public void setAngleRange(final double ANGLE_RANGE) {
            angleRange             = ANGLE_RANGE;
            endAngle               = getStartAngle() + angleRange;
            nextOutgoingStartAngle = 0;
        }

        public double getEndAngle() { return endAngle; }

        public double getNextIncomingStartAngle() { return nextIncomingStartAngle; }
        public void setNextIncomingStartAngle(final double ANGLE) {
            nextIncomingStartAngle = ANGLE;
        }

        public double getNextOutgoingStartAngle() { return nextOutgoingStartAngle; }
        public void setNextOutgoingStartAngle(final double ANGLE) { nextOutgoingStartAngle = ANGLE; }
    }
}
