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


import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.event.TreeNodeEvent;
import eu.hansolo.fx.charts.event.TreeNodeEventListener;
import eu.hansolo.fx.charts.event.TreeNodeEventType;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.data.TreeNode;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static eu.hansolo.fx.charts.tools.Helper.clamp;


@DefaultProperty("children")
public class SunburstChart<T extends ChartItem> extends Region {
    public enum TextOrientation {
        HORIZONTAL(12),
        TANGENT(8),
        ORTHOGONAL(12);

        private double maxAngle;
        TextOrientation(final double MAX_ANGLE) {
            maxAngle = MAX_ANGLE;
        }

        public double getMaxAngle() { return maxAngle; }
    }
    public enum VisibleData {
        NONE, NAME, VALUE, NAME_VALUE
    }

    private static final double                          PREFERRED_WIDTH   = 250;
    private static final double                          PREFERRED_HEIGHT  = 250;
    private static final double                          MINIMUM_WIDTH     = 50;
    private static final double                          MINIMUM_HEIGHT    = 50;
    private static final double                          MAXIMUM_WIDTH     = 2048;
    private static final double                          MAXIMUM_HEIGHT    = 2048;
    private static final Color                           BRIGHT_TEXT_COLOR = Color.WHITE;
    private static final Color                           DARK_TEXT_COLOR   = Color.BLACK;
    private              double                          size;
    private              double                          width;
    private              double                          height;
    private              double                          center;
    private              Pane                            segmentPane;
    private              Canvas                          chartCanvas;
    private              GraphicsContext                 chartCtx;
    private              Pane                            pane;
    private              Paint                           backgroundPaint;
    private              Paint                           borderPaint;
    private              double                          borderWidth;
    private              List<Path>                      segments;
    private              VisibleData                     _visibleData;
    private              ObjectProperty<VisibleData>     visibleData;
    private              TextOrientation                 _textOrientation;
    private              ObjectProperty<TextOrientation> textOrientation;
    private              Color                           _backgroundColor;
    private              ObjectProperty<Color>           backgroundColor;
    private              Color                           _textColor;
    private              ObjectProperty<Color>           textColor;
    private              boolean                         _useColorFromParent;
    private              BooleanProperty                 useColorFromParent;
    private              int                             _decimals;
    private              IntegerProperty                 decimals;
    private              boolean                         _interactive;
    private              BooleanProperty                 interactive;
    private              boolean                         _autoTextColor;
    private              BooleanProperty                 autoTextColor;
    private              Color                           _brightTextColor;
    private              ObjectProperty<Color>           brightTextColor;
    private              Color                           _darkTextColor;
    private              ObjectProperty<Color>           darkTextColor;
    private              boolean                         _useChartItemTextColor;
    private              BooleanProperty                 useChartItemTextColor;
    private              String                          formatString;
    private              TreeNode<T>                     tree;
    private              TreeNode<T>                     root;
    private              int                             maxLevel;
    private              Map<Integer, List<TreeNode<T>>> levelMap;
    private              InvalidationListener            sizeListener;
    private        final TreeNodeEventListener<T>           treeNodeListener;



    // ******************** Constructors **************************************
    public SunburstChart() {
        this(new TreeNode(new ChartItem()));
    }
    public SunburstChart(final TreeNode<T> TREE) {
        backgroundPaint        = Color.TRANSPARENT;
        borderPaint            = Color.TRANSPARENT;
        borderWidth            = 0d;
        segments               = new ArrayList<>(64);
        _visibleData           = VisibleData.NAME;
        _textOrientation       = TextOrientation.TANGENT;
        _backgroundColor       = Color.WHITE;
        _textColor             = Color.BLACK;
        _useColorFromParent    = false;
        _decimals              = 0;
        _interactive           = false;
        _autoTextColor         = true;
        _brightTextColor       = BRIGHT_TEXT_COLOR;
        _darkTextColor         = DARK_TEXT_COLOR;
        _useChartItemTextColor = false;
        formatString           = "%.0f";
        tree                   = TREE;
        levelMap               = new HashMap<>(8);
        sizeListener           = o -> resize();
        treeNodeListener       = EVENT -> {
            if(EVENT.getType()!= TreeNodeEventType.NODE_SELECTED){
                redraw();
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

        segmentPane = new Pane();

        chartCanvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        chartCanvas.setMouseTransparent(true);

        chartCtx    = chartCanvas.getGraphicsContext2D();

        pane = new Pane(segmentPane, chartCanvas);
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth))));

        getChildren().setAll(pane);

        prepareData();
    }

    private void registerListeners() {
        widthProperty().addListener(sizeListener);
        heightProperty().addListener(sizeListener);
        tree.setOnTreeNodeEvent(treeNodeListener);
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
        widthProperty().removeListener(sizeListener);
        heightProperty().removeListener(sizeListener);
        tree.removeTreeNodeEventListener(treeNodeListener);
    }

    /**
     * Returns the data that should be visualized in the chart segments
     * @return the data that should be visualized in the chart segments
     */
    public VisibleData getVisibleData() { return null == visibleData ? _visibleData : visibleData.get(); }
    /**
     * Defines the data that should be visualized in the chart segments
     * @param VISIBLE_DATA
     */
    public void setVisibleData(final VisibleData VISIBLE_DATA) {
        if (null == visibleData) {
            _visibleData = VISIBLE_DATA;
            redraw();
        } else {
            visibleData.set(VISIBLE_DATA);
        }
    }
    public ObjectProperty<VisibleData> visibleDataProperty() {
        if (null == visibleData) {
            visibleData = new ObjectPropertyBase<VisibleData>(_visibleData) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return SunburstChart.this; }
                @Override public String getName() { return "visibleData"; }
            };
            _visibleData = null;
        }
        return visibleData;
    }

    /**
     * Returns the orientation the text will be drawn in the segments
     * @return the orientation the text will be drawn in the segments
     */
    public TextOrientation getTextOrientation() { return null == textOrientation ? _textOrientation : textOrientation.get(); }
    /**
     * Defines the orientation the text will be drawn in the segments
     * @param ORIENTATION
     */
    public void setTextOrientation(final TextOrientation ORIENTATION) {
        if (null == textOrientation) {
            _textOrientation = ORIENTATION;
            redraw();
        } else {
            textOrientation.set(ORIENTATION);
        }
    }
    public ObjectProperty<TextOrientation> textOrientationProperty() {
        if (null == textOrientation) {
            textOrientation = new ObjectPropertyBase<TextOrientation>(_textOrientation) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return SunburstChart.this; }
                @Override public String getName() { return "textOrientation"; }
            };
            _textOrientation = null;
        }
        return textOrientation;
    }

    /**
     * Returns the color that will be used to fill the background of the chart
     * @return the color that will be used to fill the background of the chart
     */
    public Color getBackgroundColor() { return null == backgroundColor ? _backgroundColor : backgroundColor.get(); }
    /**
     * Defines the color that will be used to fill the background of the chart
     * @param COLOR
     */
    public void setBackgroundColor(final Color COLOR) {
        if (null == backgroundColor) {
            _backgroundColor = COLOR;
            redraw();
        } else {
            backgroundColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> backgroundColorProperty() {
        if (null == backgroundColor) {
            backgroundColor = new ObjectPropertyBase<Color>(_backgroundColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return SunburstChart.this; }
                @Override public String getName() { return "backgroundColor"; }
            };
            _backgroundColor = null;
        }
        return backgroundColor;
    }

    /**
     * Returns the color that will be used to draw text in segments if useChartItemTextColor == false
     * @return the color that will be used to draw text in segments if useChartItemTextColor == false
     */
    public Color getTextColor() { return null == textColor ? _textColor : textColor.get(); }
    /**
     * Defines the color that will be used to draw text in segments if useChartItemTextColor == false
     * @param COLOR
     */
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
                @Override public Object getBean() { return SunburstChart.this; }
                @Override public String getName() { return "textColor"; }
            };
            _textColor = null;
        }
        return textColor;
    }

    /**
     * Returns true if the color of all chart segments in one group should be filled with the color
     * of the groups root node or by the color defined in the chart data elements
     * @return
     */
    public boolean getUseColorFromParent() { return null == useColorFromParent ? _useColorFromParent : useColorFromParent.get(); }
    /**
     * Defines if tthe color of all chart segments in one group should be filled with the color
     * of the groups root node or by the color defined in the chart data elements
     * @param USE
     */
    public void setUseColorFromParent(final boolean USE) {
        if (null == useColorFromParent) {
            _useColorFromParent = USE;
            redraw();
        } else {
            useColorFromParent.set(USE);
        }
    }
    public BooleanProperty useColorFromParentProperty() {
        if (null == useColorFromParent) {
            useColorFromParent = new BooleanPropertyBase(_useColorFromParent) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return SunburstChart.this; }
                @Override public String getName() { return "useColorFromParent"; }
            };
        }
        return useColorFromParent;
    }

    /**
     * Returns the number of decimals that will be used to format the values in the tooltip
     * @return
     */
    public int getDecimals() { return null == decimals ? _decimals : decimals.get(); }
    /**
     * Defines the number of decimals that will be used to format the values in the tooltip
     * @param DECIMALS
     */
    public void setDecimals(final int DECIMALS) {
        if (null == decimals) {
            _decimals    = clamp(0, 5, DECIMALS);
            formatString = "%." + _decimals + "f";
            redraw();
        } else {
            decimals.set(DECIMALS);
        }
    }
    public IntegerProperty decimalsProperty() {
        if (null == decimals) {
            decimals = new IntegerPropertyBase(_decimals) {
                @Override protected void invalidated() {
                    set(clamp(0, 5, get()));
                    formatString = new StringBuilder("%.").append(get()).append("f").toString();
                    redraw();
                }
                @Override public Object getBean() { return SunburstChart.this; }
                @Override public String getName() { return "decimals"; }
            };
        }
        return decimals;
    }

    /**
     * Returns true if the chart is drawn using Path elements, fire ChartItemEvents and show tooltips on segments.
     * @return
     */
    public boolean isInteractive() { return null == interactive ? _interactive : interactive.get(); }
    /**
     * Defines if the chart should be drawn using Path elements, fire ChartItemEvents and shows tooltips on segments or
     * if the the chart should be drawn using one Canvas node.
     * @param INTERACTIVE
     */
    public void setInteractive(final boolean INTERACTIVE) {
        if (null == interactive) {
            _interactive = INTERACTIVE;
            redraw();
        } else {
            interactive.set(INTERACTIVE);
        }
    }
    public BooleanProperty interactiveProperty() {
        if (null == interactive) {
            interactive = new BooleanPropertyBase(_interactive) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return SunburstChart.this; }
                @Override public String getName() { return "interactive"; }
            };
        }
        return interactive;
    }

    /**
     * Returns true if the text color of the chart data should be adjusted according to the chart data fill color.
     * e.g. if the fill color is dark the text will be set to the defined brightTextColor and vice versa.
     * @return true if the text color of the chart data should be adjusted according to the chart data fill color
     */
    public boolean isAutoTextColor() { return null == autoTextColor ? _autoTextColor : autoTextColor.get(); }
    /**
     * Defines if the text color of the chart data should be adjusted according to the chart data fill color
     * @param AUTOMATIC
     */
    public void setAutoTextColor(final boolean AUTOMATIC) {
        if (null == autoTextColor) {
            _autoTextColor = AUTOMATIC;
            adjustTextColors();
            redraw();
        } else {
            autoTextColor.set(AUTOMATIC);
        }
    }
    public BooleanProperty autoTextColorProperty() {
        if (null == autoTextColor) {
            autoTextColor = new BooleanPropertyBase(_autoTextColor) {
                @Override protected void invalidated() {
                    adjustTextColors();
                    redraw();
                }
                @Override public Object getBean() { return SunburstChart.this; }
                @Override public String getName() { return "autoTextColor"; }
            };
        }
        return autoTextColor;
    }

    /**
     * Returns the color that will be used by the autoTextColor feature as the bright text on dark segment fill colors
     * @return the color that will be used by the autoTextColor feature as the bright text on dark segment fill colors
     */
    public Color getBrightTextColor() { return null == brightTextColor ? _brightTextColor : brightTextColor.get(); }

    /**
     * Defines the color that will be used by the autoTextColor feature as the bright text on dark segment fill colors
     * @param COLOR
     */
    public void setBrightTextColor(final Color COLOR) {
        if (null == brightTextColor) {
            _brightTextColor = COLOR;
            if (isAutoTextColor()) {
                adjustTextColors();
                redraw();
            }
        } else {
            brightTextColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> brightTextColorProperty() {
        if (null == brightTextColor) {
            brightTextColor = new ObjectPropertyBase<Color>(_brightTextColor) {
                @Override protected void invalidated() {
                    if (isAutoTextColor()) {
                        adjustTextColors();
                        redraw();
                    }
                }
                @Override public Object getBean() { return SunburstChart.this; }
                @Override public String getName() { return "brightTextColor"; }
            };
            _brightTextColor = null;
        }
        return brightTextColor;
    }

    /**
     * Returns the color that will be used by the autoTextColor feature as the dark text on bright segment fill colors
     * @return the color that will be used by the autoTextColor feature as the dark text on bright segment fill colors
     */
    public Color getDarkTextColor() { return null == darkTextColor ? _darkTextColor : darkTextColor.get(); }
    /**
     * Defines the color that will be used by the autoTextColor feature as the dark text on bright segment fill colors
     * @param COLOR
     */
    public void setDarkTextColor(final Color COLOR) {
        if (null == darkTextColor) {
            _darkTextColor = COLOR;
            if (isAutoTextColor()) {
                adjustTextColors();
                redraw();
            }
        } else {
            darkTextColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> darkTextColorProperty() {
        if (null == darkTextColor) {
            darkTextColor = new ObjectPropertyBase<Color>(_darkTextColor) {
                @Override protected void invalidated() {
                    if (isAutoTextColor()) {
                        adjustTextColors();
                        redraw();
                    }
                }
                @Override public Object getBean() { return SunburstChart.this; }
                @Override public String getName() { return "darkTextColor"; }
            };
            _darkTextColor = null;
        }
        return darkTextColor;
    }

    /**
     * Returns true if the text color of the ChartItem elements should be used to
     * fill the text in the segments
     * @return true if the text color of the segments will be taken from the ChartItem elements
     */
    public boolean getUseChartItemTextColor() { return null == useChartItemTextColor ? _useChartItemTextColor : useChartItemTextColor.get(); }
    /**
     * Defines if the text color of the segments should be taken from the ChartItem elements
     * @param USE
     */
    public void setUseChartItemTextColor(final boolean USE) {
        if (null == useChartItemTextColor) {
            _useChartItemTextColor = USE;
            redraw();
        } else {
            useChartItemTextColor.set(USE);
        }
    }
    public BooleanProperty useChartItemTextColor() {
        if (null == useChartItemTextColor) {
            useChartItemTextColor = new BooleanPropertyBase(_useChartItemTextColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return SunburstChart.this; }
                @Override public String getName() { return "useChartItemTextColor"; }
            };
        }
        return useChartItemTextColor;
    }

    /**
     * Defines the root element of the tree
     * @param TREE
     */
    public void setTree(final TreeNode<T> TREE) {
        if (null != tree) {
            tree.removeTreeNodeEventListener(treeNodeListener);
        }
        tree = TREE;
        tree.setOnTreeNodeEvent(treeNodeListener);
        prepareData();
        if (isAutoTextColor()) { adjustTextColors(); }
        drawChart();
    }

    private void adjustTextColors() {
        Color brightColor = getBrightTextColor();
        Color darkColor   = getDarkTextColor();
        root.stream().forEach(node -> {
            T       item          = node.getItem();
            boolean darkFillColor = Helper.isDark(item.getFill());
            boolean darkTextColor = Helper.isDark(item.getTextFill());
            if (darkFillColor && darkTextColor) { item.setTextFill(brightColor); }
            if (!darkFillColor && !darkTextColor) { item.setTextFill(darkColor); }
        });
    }

    private void prepareData() {
        root     = tree.getTreeRoot();
        maxLevel = root.getMaxLevel();

        // Create map of all nodes per level
        levelMap.clear();
        for (int i = 0 ; i <= maxLevel ; i++) { levelMap.put(i, new ArrayList<>()); }
        root.stream().forEach(node -> levelMap.get(node.getDepth()).add(node));

    }

    private void drawChart() {
        levelMap.clear();
        for (int i = 0 ; i <= maxLevel ; i++) { levelMap.put(i, new ArrayList<>()); }
        root.stream().forEach(node -> levelMap.get(node.getDepth()).add(node));
        boolean         isInteractive      = isInteractive();
        double          ringRadiusStep     = (size / 2) / (maxLevel + 0.5);
        double          innerCircle        = ringRadiusStep * 0.5;
        double          barWidth           = isInteractive ? ringRadiusStep : ringRadiusStep * 0.98;
        double          segmentStrokeWidth = ringRadiusStep * 0.02;
        Color           bkgColor           = getBackgroundColor();
        Color           textColor          = getTextColor();
        TextOrientation textOrientation    = getTextOrientation();
        double          maxTextWidth       = barWidth * 0.9;

        chartCtx.clearRect(0, 0, size, size);
        chartCtx.setFill(isInteractive ? Color.TRANSPARENT : bkgColor);
        chartCtx.fillRect(0, 0, size, size);

        chartCtx.setFont(Fonts.latoRegular(barWidth * 0.2));
        chartCtx.setTextBaseline(VPos.CENTER);
        chartCtx.setTextAlign(TextAlignment.CENTER);
        chartCtx.setLineCap(StrokeLineCap.BUTT);

        segments.clear();

        Map<TreeNode<T>, Double> startAngles = new HashMap<>();
        Map<TreeNode<T>, Double> angles      = new HashMap<>();
        double                   levelOneSum = Double.NaN;
        if(maxLevel >= 1){
            levelOneSum = levelMap.get(1).stream().map(TreeNode::getItem).mapToDouble(T::getValue).sum();
        }

        double outerRadius = 0;
        double innerRadius;
        for (int level = 1 ; level <= maxLevel ; level++) {
            List<TreeNode<T>> nodesAtLevel = levelMap.get(level);
            innerRadius  = level == 1? innerCircle : outerRadius;
            outerRadius  = ringRadiusStep * level + innerCircle;

            double   segmentStartAngle;
            double   segmentEndAngle = 0;
            TreeNode<T> currentParent   = null;

            for (TreeNode<T> node : nodesAtLevel) {
                ChartItem segmentData  = node.getItem();
                double    segmentPercentage;
                double    segmentAngle;
                Paint     segmentColor = getUseColorFromParent() ? node.getMyRoot().getItem().getFill() : segmentData.getFill();

                // Assuming level 0 is a pseudo-root with no data and level one is the first level with relevant data
                if(level == 1){
                    // The percentage is relevant to all siblings
                    segmentPercentage = segmentData.getValue() / levelOneSum;
                    segmentAngle      =  segmentPercentage * 360;
                    segmentStartAngle = segmentEndAngle;
                }else {
                    assert node.getParent() != null;
                    if (!node.getParent().equals(currentParent)) {
                        currentParent = node.getParent();
                        // Start each segment from the same point as the parent
                        segmentStartAngle = startAngles.get(currentParent);
                    } else {
                        // Start the segment relative to the previous sibling
                        segmentStartAngle =segmentEndAngle;
                    }
                    // The percentage is relative to the parent
                    segmentPercentage = segmentData.getValue() / currentParent.getItem().getValue();
                    segmentAngle      = angles.get(currentParent) * segmentPercentage;
                }
                segmentEndAngle = segmentStartAngle + segmentAngle;
                // Save the startAngle as entry-point for the children
                assert !Double.isNaN(segmentStartAngle);
                startAngles.put(node,segmentStartAngle);
                // Store the angle of each segment separably from each node, because it is purely graphically
                angles.put(node,segmentAngle);

                // Only draw if segment fill color is not TRANSPARENT
                if (!Color.TRANSPARENT.equals(segmentData.getFill())) {
                    double value = segmentData.getValue();

                    if (isInteractive) {
                        segments.add(createSegment(segmentStartAngle, segmentEndAngle, innerRadius, outerRadius, segmentColor, bkgColor, node));
                    } else {
                        double xy = center - ringRadiusStep * level;
                        double wh = ringRadiusStep * level * 2;
                        // Segment Fill
                        chartCtx.setLineWidth(barWidth);
                        chartCtx.setStroke(segmentColor);
                        chartCtx.strokeArc(xy, xy, wh, wh, -segmentStartAngle+90, -segmentAngle, ArcType.OPEN);

                        // Segment Stroke
                        double radStart = Math.toRadians(-segmentStartAngle+90);
                        double cosStart = Math.cos(radStart);
                        double sinStart = Math.sin(radStart);
                        double x1       = center + innerRadius * cosStart;
                        double y1       = center - innerRadius * sinStart;
                        double x2       = center + outerRadius * cosStart;
                        double y2       = center - outerRadius * sinStart;

                        chartCtx.setLineWidth(segmentStrokeWidth);
                        chartCtx.setStroke(bkgColor);
                        chartCtx.strokeLine(x1, y1, x2, y2);
                    }

                    // Visible Data
                    if (getVisibleData() != VisibleData.NONE && segmentAngle > textOrientation.getMaxAngle()) {
                        double radText    = Math.toRadians(segmentStartAngle + (segmentAngle * 0.5));
                        double cosText    = Math.cos(radText);
                        double sinText    = Math.sin(radText);
                        double textRadius = ringRadiusStep * level;
                        double textX      = center + textRadius * sinText;
                        double textY      = center - textRadius * cosText;

                        chartCtx.setFill(getUseChartItemTextColor() ? segmentData.getTextFill() : textColor);

                        chartCtx.save();
                        chartCtx.translate(textX, textY);

                        rotateContextForText(chartCtx, segmentStartAngle, (segmentAngle * 0.5), textOrientation);

                        switch (getVisibleData()) {
                            case VALUE:
                                chartCtx.fillText(String.format(Locale.US, formatString, value), 0, 0, maxTextWidth);
                                break;
                            case NAME:
                                chartCtx.fillText(segmentData.getName(), 0, 0, maxTextWidth);
                                break;
                            case NAME_VALUE:
                                chartCtx.fillText(String.join("", segmentData.getName(), " (", String.format(Locale.US, formatString, value),")"), 0, 0, maxTextWidth);
                                break;
                        }
                        chartCtx.restore();
                    }
                }
            }
        }

        segmentPane.getChildren().setAll(segments);
    }

    private Path createSegment(final double START_ANGLE, final double END_ANGLE, final double INNER_RADIUS, final double OUTER_RADIUS, final Paint FILL, final Color STROKE, final TreeNode<T> NODE) {
        double  startAngleRad = Math.toRadians(START_ANGLE);
        double  endAngleRad   = Math.toRadians(END_ANGLE);
        boolean largeAngle    = Math.abs(END_ANGLE - START_ANGLE) > 180.0;

        double x1 = center + INNER_RADIUS * Math.sin(startAngleRad);
        double y1 = center - INNER_RADIUS * Math.cos(startAngleRad);

        double x2 = center + OUTER_RADIUS * Math.sin(startAngleRad);
        double y2 = center - OUTER_RADIUS * Math.cos(startAngleRad);

        double x3 = center + OUTER_RADIUS * Math.sin(endAngleRad);
        double y3 = center - OUTER_RADIUS * Math.cos(endAngleRad);

        double x4 = center + INNER_RADIUS * Math.sin(endAngleRad);
        double y4 = center - INNER_RADIUS * Math.cos(endAngleRad);

        // Simulate full circles
        if(Math.abs(x2 - x3) <= 0.001 && Math.abs(y3 - y2) <= 0.001 && END_ANGLE > 0){
            x3 -= 1;
            x4 -= 1;
        }
        MoveTo moveTo1 = new MoveTo(x1, y1);
        LineTo lineTo2 = new LineTo(x2, y2);
        ArcTo  arcTo3  = new ArcTo(OUTER_RADIUS, OUTER_RADIUS, 0, x3, y3, largeAngle, true);
        LineTo lineTo4 = new LineTo(x4, y4);
        ArcTo  arcTo1  = new ArcTo(INNER_RADIUS, INNER_RADIUS, 0, x1, y1, largeAngle, false);

        Path path = new Path(moveTo1, lineTo2, arcTo3, lineTo4, arcTo1);

        path.setFill(FILL);
        path.setStroke(STROKE);

        String tooltipText = new StringBuilder(NODE.getItem().getName()).append("\n").append(String.format(Locale.US, formatString, ((ChartItem) NODE.getItem()).getValue())).toString();
        Tooltip.install(path, new Tooltip(tooltipText));

        path.setOnMousePressed(e -> NODE.getTreeRoot().fireTreeNodeEvent(new TreeNodeEvent(NODE, TreeNodeEventType.NODE_SELECTED)));

        return path;
    }

    private static void rotateContextForText(final GraphicsContext CTX, final double START_ANGLE, final double ANGLE, final TextOrientation ORIENTATION) {
        switch (ORIENTATION) {
            case TANGENT:
                if (START_ANGLE + ANGLE < 180) {
                    CTX.rotate((START_ANGLE + ANGLE -90) % 360);
                } else {
                    CTX.rotate((START_ANGLE + ANGLE + 90) % 360);
                }
                break;
            case ORTHOGONAL:
                if (START_ANGLE + ANGLE < 270) {
                    CTX.rotate((START_ANGLE + ANGLE + 180) % 360);
                } else {
                    CTX.rotate(START_ANGLE + ANGLE);
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
        size   = Math.min(width, height);

        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            segmentPane.setPrefSize(size, size);

            chartCanvas.setWidth(size);
            chartCanvas.setHeight(size);

            center = size * 0.5;

            redraw();
        }
    }

    private void redraw() {
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth / PREFERRED_WIDTH * size))));

        segmentPane.setBackground(new Background(new BackgroundFill(getBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        segmentPane.setManaged(isInteractive());
        segmentPane.setVisible(isInteractive());

        drawChart();
    }
}
