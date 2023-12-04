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
import eu.hansolo.fx.charts.data.TreeNode;
import eu.hansolo.fx.charts.event.ChartEvt;
import eu.hansolo.fx.charts.event.TreeNodeEvt;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.InfoPopup;
import eu.hansolo.fx.charts.tools.TextOrientation;
import eu.hansolo.fx.charts.tools.VisibleData;
import eu.hansolo.fx.geometry.Circle;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolboxfx.FontMetrix;
import eu.hansolo.toolboxfx.font.Fonts;
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
import javafx.scene.input.MouseEvent;
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
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import static eu.hansolo.fx.charts.tools.Helper.clamp;


public class RadialTidyTree<T extends ChartItem> extends Region {
    private static final double                                         PREFERRED_WIDTH   = 250;
    private static final double                                         PREFERRED_HEIGHT  = 250;
    private static final double                                         MINIMUM_WIDTH     = 50;
    private static final double                                         MINIMUM_HEIGHT    = 50;
    private static final double                                         MAXIMUM_WIDTH     = 2048;
    private static final double                                         MAXIMUM_HEIGHT    = 2048;
    private static final Color                                          BRIGHT_TEXT_COLOR = Color.WHITE;
    private static final Color                                          DARK_TEXT_COLOR   = Color.BLACK;
    private static final double                                         MAX_ANGLE_RANGE   = 180;
    private              double                                         size;
    private              double                                         width;
    private              double                                         height;
    private              double                                         center;
    private              double                                         nodeDotRadius;
    private              Canvas                                         canvas;
    private              GraphicsContext                                ctx;
    private              Pane                                           pane;
    private              Paint                                          backgroundPaint;
    private              Paint                                          borderPaint;
    private              double                                         borderWidth;
    private              VisibleData                                    _visibleData;
    private              ObjectProperty<VisibleData>                    visibleData;
    private              Color                                          _backgroundColor;
    private              ObjectProperty<Color>                          backgroundColor;
    private              Color                                          _textColor;
    private              ObjectProperty<Color>                          textColor;
    private              boolean                                        _useColorFromParent;
    private              BooleanProperty                                useColorFromParent;
    private              int                                            _decimals;
    private              IntegerProperty                                decimals;
    private              boolean                                        _autoTextColor;
    private              BooleanProperty                                autoTextColor;
    private              Color                                          _brightTextColor;
    private              ObjectProperty<Color>                          brightTextColor;
    private              Color                                          _darkTextColor;
    private              ObjectProperty<Color>                          darkTextColor;
    private              boolean                                        _useChartItemTextColor;
    private              BooleanProperty                                useChartItemTextColor;
    private              String                                         formatString;
    private              TreeNode<T>                                    tree;
    private              TreeNode<T>                                    root;
    private              int                                            maxLevel;
    private              Map<Integer, List<TreeNode<T>>>                levelMap;
    private              Map<Integer, Double>                           angleStepMap;
    private              InvalidationListener                           sizeListener;
    private              Map<Circle, T>                                 circleMap;
    private              InfoPopup                                      popup;
    private        final EvtObserver<TreeNodeEvt<T>>                    treeNodeEvtObserver;


    // ******************** Constructors **************************************
    public RadialTidyTree() {
        this(new TreeNode(new ChartItem()));
    }
    public RadialTidyTree(final TreeNode<T> TREE) {
        backgroundPaint        = Color.TRANSPARENT;
        borderPaint            = Color.TRANSPARENT;
        borderWidth            = 0d;
        _visibleData           = VisibleData.NAME;
        _backgroundColor       = Color.WHITE;
        _textColor             = Color.BLACK;
        _useColorFromParent    = false;
        _decimals              = 0;
        _autoTextColor         = true;
        _brightTextColor       = BRIGHT_TEXT_COLOR;
        _darkTextColor         = DARK_TEXT_COLOR;
        _useChartItemTextColor = false;
        formatString           = "%.0f";
        tree                   = TREE;
        levelMap               = new HashMap<>(8);
        angleStepMap           = new HashMap<>(8);
        sizeListener           = o -> resize();
        circleMap              = new HashMap<>();
        popup                  = new InfoPopup();
        treeNodeEvtObserver    = evt -> {
            if (evt.getEvtType().equals(TreeNodeEvt.NODE_SELECTED)) { redraw(); }
        };
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

        ctx = canvas.getGraphicsContext2D();

        pane = new Pane(canvas);
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth))));

        getChildren().setAll(pane);

        prepareData();
    }

    private void registerListeners() {
        widthProperty().addListener(sizeListener);
        heightProperty().addListener(sizeListener);
        tree.addTreeNodeEvtObserver(TreeNodeEvt.NODE_SELECTED, treeNodeEvtObserver);

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            Optional<Entry<Circle,T>> optionalEntry = circleMap.entrySet().stream().filter(entry -> entry.getKey().contains(e.getX(), e.getY())).findFirst();
            if (optionalEntry.isPresent()) {
                T item = optionalEntry.get().getValue();
                popup.setX(e.getScreenX());
                popup.setY(e.getScreenY() - popup.getHeight());
                popup.update(item);
                popup.animatedShow(getScene().getWindow());
                item.fireChartEvt(new ChartEvt(item, ChartEvt.ITEM_SELECTED));
            }
        });
    }


    // ******************** Methods *******************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
    }

    @Override protected double computeMinWidth(final double HEIGHT)  { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH)  { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT)  { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH)  { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren()              { return super.getChildren(); }

    public void dispose() {
        widthProperty().removeListener(sizeListener);
        heightProperty().removeListener(sizeListener);
        tree.getChildren().forEach(child -> child.removeAllTreeNodeEvtObservers());
        tree.removeAllTreeNodeEvtObservers();
    }

    /**
     * Returns the data that should be visualized in the chart segments
     *
     * @return the data that should be visualized in the chart segments
     */
    public VisibleData getVisibleData() { return null == visibleData ? _visibleData : visibleData.get(); }
    /**
     * Defines the data that should be visualized in the chart segments
     *
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
            visibleData  = new ObjectPropertyBase<>(_visibleData) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return RadialTidyTree.this; }
                @Override public String getName() { return "visibleData"; }
            };
            _visibleData = null;
        }
        return visibleData;
    }

    /**
     * Returns the color that will be used to fill the background of the chart
     *
     * @return the color that will be used to fill the background of the chart
     */
    public Color getBackgroundColor() { return null == backgroundColor ? _backgroundColor : backgroundColor.get(); }
    /**
     * Defines the color that will be used to fill the background of the chart
     *
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
            backgroundColor  = new ObjectPropertyBase<Color>(_backgroundColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return RadialTidyTree.this; }
                @Override public String getName() { return "backgroundColor"; }
            };
            _backgroundColor = null;
        }
        return backgroundColor;
    }

    /**
     * Returns the color that will be used to draw text in segments if useChartItemTextColor == false
     *
     * @return the color that will be used to draw text in segments if useChartItemTextColor == false
     */
    public Color getTextColor() { return null == textColor ? _textColor : textColor.get(); }
    /**
     * Defines the color that will be used to draw text in segments if useChartItemTextColor == false
     *
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
            textColor  = new ObjectPropertyBase<Color>(_textColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return RadialTidyTree.this; }
                @Override public String getName() { return "textColor"; }
            };
            _textColor = null;
        }
        return textColor;
    }

    /**
     * Returns true if the color of all chart segments in one group should be filled with the color
     * of the groups root node or by the color defined in the chart data elements
     *
     * @return
     */
    public boolean getUseColorFromParent() { return null == useColorFromParent ? _useColorFromParent : useColorFromParent.get(); }
    /**
     * Defines if tthe color of all chart segments in one group should be filled with the color
     * of the groups root node or by the color defined in the chart data elements
     *
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
                @Override public Object getBean() { return RadialTidyTree.this; }
                @Override public String getName() { return "useColorFromParent"; }
            };
        }
        return useColorFromParent;
    }

    /**
     * Returns the number of decimals that will be used to format the values in the tooltip
     *
     * @return
     */
    public int getDecimals() { return null == decimals ? _decimals : decimals.get(); }
    /**
     * Defines the number of decimals that will be used to format the values in the tooltip
     *
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
                @Override public Object getBean() { return RadialTidyTree.this; }
                @Override public String getName() { return "decimals"; }
            };
        }
        return decimals;
    }

    /**
     * Returns true if the text color of the chart data should be adjusted according to the chart data fill color.
     * e.g. if the fill color is dark the text will be set to the defined brightTextColor and vice versa.
     *
     * @return true if the text color of the chart data should be adjusted according to the chart data fill color
     */
    public boolean isAutoTextColor() { return null == autoTextColor ? _autoTextColor : autoTextColor.get(); }
    /**
     * Defines if the text color of the chart data should be adjusted according to the chart data fill color
     *
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
                @Override public Object getBean() { return RadialTidyTree.this; }
                @Override public String getName() { return "autoTextColor"; }
            };
        }
        return autoTextColor;
    }

    /**
     * Returns the color that will be used by the autoTextColor feature as the bright text on dark segment fill colors
     *
     * @return the color that will be used by the autoTextColor feature as the bright text on dark segment fill colors
     */
    public Color getBrightTextColor() { return null == brightTextColor ? _brightTextColor : brightTextColor.get(); }

    /**
     * Defines the color that will be used by the autoTextColor feature as the bright text on dark segment fill colors
     *
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
            brightTextColor  = new ObjectPropertyBase<Color>(_brightTextColor) {
                @Override protected void invalidated() {
                    if (isAutoTextColor()) {
                        adjustTextColors();
                        redraw();
                    }
                }
                @Override public Object getBean() { return RadialTidyTree.this; }
                @Override public String getName() { return "brightTextColor"; }
            };
            _brightTextColor = null;
        }
        return brightTextColor;
    }

    /**
     * Returns the color that will be used by the autoTextColor feature as the dark text on bright segment fill colors
     *
     * @return the color that will be used by the autoTextColor feature as the dark text on bright segment fill colors
     */
    public Color getDarkTextColor() { return null == darkTextColor ? _darkTextColor : darkTextColor.get(); }
    /**
     * Defines the color that will be used by the autoTextColor feature as the dark text on bright segment fill colors
     *
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
            darkTextColor  = new ObjectPropertyBase<Color>(_darkTextColor) {
                @Override protected void invalidated() {
                    if (isAutoTextColor()) {
                        adjustTextColors();
                        redraw();
                    }
                }
                @Override public Object getBean() { return RadialTidyTree.this; }
                @Override public String getName() { return "darkTextColor"; }
            };
            _darkTextColor = null;
        }
        return darkTextColor;
    }

    /**
     * Returns true if the text color of the ChartItem elements should be used to
     * fill the text in the segments
     *
     * @return true if the text color of the segments will be taken from the ChartItem elements
     */
    public boolean getUseChartItemTextColor() { return null == useChartItemTextColor ? _useChartItemTextColor : useChartItemTextColor.get(); }
    /**
     * Defines if the text color of the segments should be taken from the ChartItem elements
     *
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
                @Override public Object getBean() { return RadialTidyTree.this; }
                @Override public String getName() { return "useChartItemTextColor"; }
            };
        }
        return useChartItemTextColor;
    }

    /**
     * Defines the root element of the tree
     *
     * @param TREE
     */
    public void setTree(final TreeNode<T> TREE) {
        if (null != tree) {
            tree.removeTreeNodeEvtObserver(TreeNodeEvt.NODE_SELECTED, treeNodeEvtObserver);
        }
        tree = TREE;
        tree.addTreeNodeEvtObserver(TreeNodeEvt.NODE_SELECTED, treeNodeEvtObserver);
        if (isAutoTextColor()) { adjustTextColors(); }
        //drawChart();
        drawChart();
    }

    /**
     * Calling this method will render this chart/plot to a png given of the given width and height
     * @param filename The path and name of the file  /Users/hansolo/Desktop/plot.png
     * @param width The width of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @param height The height of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
     * @return True if the procedure was successful, otherwise false
     */
    public boolean renderToImage(final String filename, final int width, final int height) {
        return Helper.renderToImage(RadialTidyTree.this, width, height, filename);
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

        root.setX(center);
        root.setY(center);

        // Create map of all nodes per level
        levelMap.clear();
        for (int i = 0; i <= maxLevel; i++) { levelMap.put(i, new ArrayList<>()); }
        root.stream().forEach(node -> levelMap.get(node.getDepth()).add(node));

        angleStepMap.clear();
        angleStepMap.put(0, 360.0 / levelMap.get(1).size());
        for (int level = 0 ; level < maxLevel ; level++) {
            List<TreeNode<T>> nodesInCurrentLevel       = levelMap.get(level);
            int               noOfNodesInCurrentLevel   = nodesInCurrentLevel.size();
            int               noOfParentsOfCurrentLevel = nodesInCurrentLevel.stream().map(node -> node.getParent()).collect(Collectors.toList()).size();
            double            availableAngleSpace       = 0 == level ? (360.0 / noOfNodesInCurrentLevel) : (360.0 / noOfParentsOfCurrentLevel);
            //double            maxAngleStep              = 0 == level ? (360.0 / noOfNodesInCurrentLevel) : (360.0 / noOfParentsOfCurrentLevel);

            double            maxAngleStep              = 360.0 / noOfNodesInCurrentLevel;

            angleStepMap.put(level, maxAngleStep);
            for (int i = 0; i < nodesInCurrentLevel.size(); i++) {
                TreeNode<T> currentNode     = nodesInCurrentLevel.get(i);
                TreeNode<T> parentNode      = currentNode.getParent();
                double      noOfChildNodes  = null == parentNode ? 0 : parentNode.getNoOfChildren();
                double      angleStep       = null == parentNode ? 360.0 / levelMap.get(1).size() : Math.min(availableAngleSpace / (noOfChildNodes - 1), angleStepMap.get(level));

                if (level > 0) {
                    angleStep = angleStepMap.get(level - 1) / (noOfChildNodes - 1);
                }


                if (level == maxLevel) {
                    angleStep = maxAngleStep;
                }

                // Get number of nodes in level
                // Devide 360 / number of nodes in level -> max. available angle step
                // Get current node
                // Get parent of current node
                // Get number of child nodes in parent
                // Compare max. available angle step per node by number of child nodes in parent -> min angle step

                angleStepMap.put(level, angleStep);
            }
        }
        List<TreeNode<T>> maxLevelNodes = levelMap.get(maxLevel);
        int noOfParentNodes = levelMap.get(maxLevel - 1).size();
        double availableAngleSpacePerMaxLevelNode = 360.0 / noOfParentNodes;
        double maxAngleStep = 360.0 / maxLevelNodes.size();
        double angleStep = maxAngleStep;
        for (TreeNode<T> node : levelMap.get(maxLevel - 1)) {
            double currentAngleStep = node.isLeaf() ? maxAngleStep : availableAngleSpacePerMaxLevelNode / node.getNoOfChildren();
            if (currentAngleStep < angleStep) { angleStep = currentAngleStep; }
        }
        angleStepMap.put(maxLevel, angleStep);

        double currentAngle = 0;
        for (TreeNode<T> node : levelMap.get(1)) {
            node.setAngle(currentAngle);
            currentAngle += angleStepMap.get(1);
        }
    }

    private void drawChart() {
        circleMap.clear();
        double          ringRadiusStep  = (size / 2) / (maxLevel + 0.5);
        double          innerCircle     = ringRadiusStep * 0.65;
        double          levelWidth      = ringRadiusStep * 0.98;
        Color           bkgColor        = getBackgroundColor();
        double          nodeDotDiameter = nodeDotRadius * 2.0;
        Color           textColor       = getTextColor();
        double          maxTextWidth    = levelWidth * 0.9;
        Font            font            = Fonts.latoRegular(levelWidth * 0.075);
        FontMetrix      metrix          = new FontMetrix(font);

        ctx.clearRect(0, 0, size, size);
        ctx.setFill(bkgColor);
        ctx.fillRect(0, 0, size, size);

        ctx.setFont(font);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setTextAlign(TextAlignment.LEFT);
        ctx.setLineCap(StrokeLineCap.BUTT);

        // Center Dot
        ctx.setLineWidth(1.0);
        ctx.setFill(root.getItem().getFill());
        ctx.fillOval(center - nodeDotRadius, center - nodeDotRadius , nodeDotDiameter, nodeDotDiameter);

        root.setX(center);
        root.setY(center);

        for (int level = 1 ; level <= maxLevel ; level++) {
            List<TreeNode<T>> nodesInCurrentLevel = levelMap.get(level);
            int               nodeInParentCounter = 0;
            TreeNode<T>       previousParent      = null;
            for (int i = 0; i < nodesInCurrentLevel.size(); i++) {
                TreeNode<T> currentNode = nodesInCurrentLevel.get(i);
                TreeNode<T> parentNode  = currentNode.getParent();
                T           item        = currentNode.getItem();
                Paint       paint       = getUseColorFromParent() ? parentNode.getItem().getFill() : item.getFill();
                double      angleStep   = currentNode.isLeaf() ? angleStepMap.get(level) : angleStepMap.get(parentNode.getDepth());
                double      angleRange  = Helper.clamp(0, MAX_ANGLE_RANGE, angleStep * (parentNode.getNoOfChildren() - 1));
                if (null == previousParent || !previousParent.equals(parentNode)) {
                    previousParent      = parentNode;
                    nodeInParentCounter = 0;
                }

                double startAngle = parentNode.getAngle() - angleRange * 0.5;
                double angle      = startAngle + nodeInParentCounter * angleStep;

                if (level < maxLevel && currentNode.isLeaf()) { angle += angleRange * 0.5; }
                currentNode.setAngle(angle);

                // Calculations
                double   previousLevelRadius  = level == 1 ? 0 : ringRadiusStep * (level - 1) - innerCircle;
                double   levelRadius          = ringRadiusStep * level - innerCircle;
                double   controlPointDistance = (levelRadius - previousLevelRadius);
                double[] xy                   = Helper.rotatePointAroundRotationCenter(center + levelRadius, center - levelRadius, center, center, angle + 90);
                currentNode.setX(xy[0]);
                currentNode.setY(xy[1]);

                // Calculate bezier curve
                double[] c1xy = { parentNode.getX() + controlPointDistance * Math.cos(Math.toRadians(parentNode.getAngle() + 45)), parentNode.getY() + controlPointDistance * Math.sin(Math.toRadians(parentNode.getAngle() + 45)) };
                double[] c2xy = { currentNode.getX() - controlPointDistance * Math.cos(Math.toRadians(currentNode.getAngle() + 45)), currentNode.getY() - controlPointDistance * Math.sin(Math.toRadians(currentNode.getAngle() + 45)) };

                // Draw connection
                ctx.setStroke(paint);
                ctx.beginPath();
                ctx.moveTo(parentNode.getX(), parentNode.getY());
                ctx.bezierCurveTo(c1xy[0], c1xy[1], c2xy[0], c2xy[1], currentNode.getX(), currentNode.getY());
                ctx.stroke();

                // Draw dot
                ctx.setFill(paint);
                ctx.fillOval(currentNode.getX() - nodeDotRadius, currentNode.getY() - nodeDotRadius, nodeDotDiameter, nodeDotDiameter);
                circleMap.put(new Circle(currentNode.getX(), currentNode.getY(), nodeDotRadius), item);

                // Visible Data
                if (getVisibleData() != VisibleData.NONE) {
                    String text = "";
                    switch (getVisibleData()) {
                        case VALUE      -> text = String.format(Locale.US, formatString, item.getValue());
                        case NAME       -> text = item.getName();
                        case NAME_VALUE -> text = String.join("", item.getName(), " (", String.format(Locale.US, formatString, item.getValue()), ")");
                    }

                    ctx.setTextAlign((angle + 135 < 180) ? TextAlignment.LEFT : TextAlignment.RIGHT);
                    double offset = currentNode.isLeaf() ? size * 0.00625 : -metrix.computeStringWidth(text) - size * 0.00625;
                    double textX = currentNode.getX() + offset * Math.cos(Math.toRadians(angle + 45));
                    double textY = currentNode.getY() + offset * Math.sin(Math.toRadians(angle + 45));

                    ctx.setFill(getUseChartItemTextColor() ? item.getTextFill() : textColor);
                    ctx.save();
                    ctx.translate(textX, textY);
                    rotateContextForText(ctx, angle + 135, 0, TextOrientation.TANGENT);
                    ctx.fillText(text, 0, 0, maxTextWidth);
                    ctx.restore();
                }

                nodeInParentCounter++;
            }
        }
    }

    private static void rotateContextForText(final GraphicsContext CTX, final double START_ANGLE, final double ANGLE, final TextOrientation ORIENTATION) {
        switch (ORIENTATION) {
            case TANGENT:
                if (START_ANGLE + ANGLE < 180) {
                    CTX.rotate((START_ANGLE + ANGLE - 90) % 360);
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

            canvas.setWidth(size);
            canvas.setHeight(size);

            center        = size * 0.5;
            nodeDotRadius = size * 0.0025;

            redraw();
        }
    }

    private void redraw() {
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth / PREFERRED_WIDTH * size))));

        //drawChart();
        drawChart();
    }
}
