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

package eu.hansolo.fx.charts.forcedirectedgraph;

import eu.hansolo.fx.charts.tools.Helper;
import javafx.animation.AnimationTimer;
import javafx.beans.DefaultProperty;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.function.Consumer;


/**
 * authors: Michael L\u00E4uchli, MLaeuchli (github)
 *          Stefan Mettler, orizion (github)
 */
@DefaultProperty("children")
public class GraphPanel extends Region {
    private static final double                   PREFERRED_WIDTH         = 800;
    private static final double                   PREFERRED_HEIGHT        = 800;
    private static final double                   MINIMUM_WIDTH           = 50;
    private static final double                   MINIMUM_HEIGHT          = 50;
    private static final double                   MAXIMUM_WIDTH           = 1024;
    private static final double                   MAXIMUM_HEIGHT          = 1024;
    private static final long                     REFRESH_PERIOD          = 100_000_000;
    private static final int                      THRESHOLD               = 1;
    private static final int                      BASE_TEMPERATURE        = 100;
    private static final double                   DISTANCE_SCALING_FACTOR = 7;
    private static final double                   MIN_EDGE_WIDTH          = 0.5;
    private static final double                   MIN_FORCE = 0.01;
    private              double                   width;
    private              double                   height;
    private              Canvas                   canvas;
    private              GraphicsContext          ctx;
    private              Pane                     pane;
    private              EventHandler<MouseEvent> mouseHandler;
    private              EventHandler<KeyEvent>   keyHandler;
    private              long                     lastTimerCall;
    private              AnimationTimer           timer;
    private              double                   temp;
    private              double                   area;
    private              double                   k;
    private              NodeEdgeModel            nodeEdgeModel;
    private              GraphNode                selectedNode;
    private              ChangeListener<Point2D>  nodeChangeListener;
    private              SimpleDoubleProperty     distanceScalingFactor;

    private              String                   GROUPING_KEY;
    private              double                   minRadius = 5; //Minimal Radius added to all Nodesizes
    /**Remembers the point at which the drag action started */
    private              Point2D                  pointDragStarted;
    private              Point2D                  pointDragLast;

    private              Color                    _edgeColor;
    private              ObjectProperty<Color>    edgeColor;
    private              double                   _edgeWidthFactor;
    private              DoubleProperty           edgeWidthFactor;
    private              double                   _nodeSizeFactor;
    private              DoubleProperty           nodeSizeFactor;
    private              Color                    _nodeHighlightingColor;
    private              ObjectProperty<Color>    nodeHighlightingColor;
    private              double                   _nodeBorderWidth;
    private              DoubleProperty           nodeBorderWidth;
    private              Color                    _selectedNodeFillColor;
    private              ObjectProperty<Color>    selectedNodeFillColor;
    private              Color                    _selectedNodeBorderColor;
    private              ObjectProperty<Color>    selectedNodeBorderColor;
    private              InfoPopup                popup;

    private              double                   maxXPosition;
    private              double                   maxYPosition;
    private              double                   minXPosition;
    private              double                   minYPosition;
    private              SimpleBooleanProperty    physicsActive;
    private              boolean                  _physicsActive;
    private              SimpleBooleanProperty    forceInverted;
    private              boolean                  _forceInverted;
    private              double                   maxRadius = 20;
    private              double                   scaleOfNodes = 450;

    private              ArrayList<String>        nummericEdgeAttributes;
    private              ArrayList<String>        nummericNodeAttributes;
    private              ArrayList<String>        stringNodeAttributes;

    private              GraphCalculator          graphCalculator;


    // ******************** Constructors **************************************
    public GraphPanel(){
        this(new NodeEdgeModel());
    }
    public GraphPanel(final NodeEdgeModel nodeEdgeModel) {
        this.nodeEdgeModel = nodeEdgeModel;
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        width              = PREFERRED_WIDTH;
        height             = PREFERRED_HEIGHT;
        mouseHandler       = this::handleMouseEvents;
        lastTimerCall      = System.nanoTime();
        _physicsActive     = true;
        _forceInverted     = false;

        setInitialPosition((int)width, (int)height);

        timer              = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + REFRESH_PERIOD) {
                    fruchtermanReingold();
                    lastTimerCall = now;
                    redraw();
                }
            }
        };

        nodeChangeListener    = (o, ov, nv) -> redraw();
        temp                  = BASE_TEMPERATURE;
        area                  = width * height;
        k                     = Math.sqrt(area / nodeEdgeModel.getNodes().size());
        distanceScalingFactor = new SimpleDoubleProperty(DISTANCE_SCALING_FACTOR);
        _edgeColor            = Color.DARKGRAY;
        _edgeWidthFactor = 2;
        //_nodeBorderColor       = Color.WHITE;
        _nodeHighlightingColor = Color.RED;
        _nodeBorderWidth       = 3;
        _selectedNodeFillColor   = Color.BLACK;
        _selectedNodeBorderColor = Color.LIME;
        maxXPosition = 1;
        maxYPosition = 1;
        minXPosition = -1;
        minYPosition = -1;


        if(null != nodeEdgeModel.getCurrentGroupKey()){
            GROUPING_KEY = nodeEdgeModel.getCurrentGroupKey();
        } else{
            GROUPING_KEY = NodeEdgeModel.DEFAULT;
        }

        popup                    = new InfoPopup();


        initGraphics();
        registerListeners();
        timer.start();
    }

    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
            Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        canvas = new Canvas(width, height);
        ctx    = canvas.getGraphicsContext2D();

        pane = new Pane(canvas);
        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        //for(GraphNode node:  nodeEdgeModel.getNodes()) { node.positionProperty().addListener(nodeChangeListener); }
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseHandler);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);


        nodeEdgeModel.isModifiedProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue) {
                redraw();
                nodeEdgeModel.isModifiedProperty().setValue(false);
            }
        }));


        nodeEdgeModel.nodeBorderColorProperty().addListener(((observable, oldValue, newValue) -> redraw()));

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

    public void restart() {
        System.out.println("Restarting simulation");
        lastTimerCall      = System.nanoTime();
        temp                  = BASE_TEMPERATURE;
        setInitialPosition((int)width,(int)height);

        timer.start();
    }

    private double cool(final double TEMPERATURE) { return (TEMPERATURE-1) * .93; }

    private double repulseForce(final double DISTANCE, final double K) { return (calculateScaleFactor() > 0? calculateScaleFactor() :1) * (K * K) / (DISTANCE * DISTANCE); }

    private double attractForce(final double DISTANCE, final double K) {
        return   (calculateScaleFactor() > 0? calculateScaleFactor() :1) *  DISTANCE * DISTANCE / K; }

    private double pointToLength(final Point2D POINT) { return   Math.sqrt(Math.abs(POINT.getX() * POINT.getX() + POINT.getY() * POINT.getY())); }

    private void handleMouseEvents(final MouseEvent EVT) {
        final EventType<? extends MouseEvent> TYPE = EVT.getEventType();
        double                          X    = EVT.getX();
        double                          Y    = EVT.getY();
        if(MouseEvent.MOUSE_PRESSED.equals(TYPE)) {
            if(null != selectedNode ) selectedNode.setSelected(false);

            selectedNode = nodeEdgeModel.getNodeAt(xPositionDrawnToReal(X), yPositionDrawnToReal(Y),
                    calculateNodeAndEdgeScaleFactor()/calculateScaleFactor(),minRadius, getNodeSizeFactor());

            if(null == selectedNode) {
                pointDragStarted = new Point2D(0,0);
            }else {
                pointDragStarted = selectedNode.getPosition();
                nodeEdgeModel.getNodes().forEach(node -> node.setSelected(false));
                selectedNode.setSelected(true);
                updatePopup(selectedNode, EVT);
            }
            redraw();
        } else if(MouseEvent.MOUSE_DRAGGED.equals(TYPE)) {
            if (null == selectedNode) return;
            popup.setOpacity(0);
            popup.animatedHide();

            double radius = getMinNodeDistance(selectedNode.getRadius());
            X = xPositionDrawnToReal(X);
            Y = yPositionDrawnToReal(Y);
            X = Math.min(xPositionDrawnToReal(width-radius), Math.max(xPositionDrawnToReal(radius), X));
            Y = Math.min(yPositionDrawnToReal(height-radius), Math.max(yPositionDrawnToReal(radius), Y));

            selectedNode.setPosition(new Point2D(X, Y));

            if(null == pointDragLast) { pointDragLast = pointDragStarted; }

            temp = pointDragLast.distance(X, Y) / distanceScalingFactor.doubleValue();
            pointDragLast = new Point2D(X, Y);
            fruchtermanReingold();
            fruchtermanReingold();

            redraw();
        } else if(MouseEvent.MOUSE_RELEASED.equals(TYPE)) {
            //temp = BASE_TEMPERATURE;
            if(null == pointDragStarted) return;
            X = xPositionDrawnToReal(X);
            Y = yPositionDrawnToReal(Y);
            temp = pointDragStarted.distance(X,Y) / distanceScalingFactor.doubleValue();

            if(null != selectedNode) timer.start();
            //selectedNode = null;
            pointDragStarted = null;
            pointDragLast = null;
        }
    }

    private void updatePopup(final GraphNode NODE, final MouseEvent EVT) {
       // popup.update(null == NODE.getName() ? "-" : NODE.getName(),NODE.getStringAttribute(nodeEdgeModel.getCurrentGroupKey()),nodeEdgeModel.getCurrentGroupKey());
        String[] names = new String[2];
        String[] values = new String[2];
        names[0] = "Name";
        names[1] = nodeEdgeModel.getCurrentGroupKey();

        values[0] = null == NODE.getName() ? "-" : NODE.getName();
        values[1] = NODE.getStringAttribute(nodeEdgeModel.getCurrentGroupKey());

        popup.update(names,values);
        popup.setX(EVT.getScreenX());
        popup.setY(EVT.getScreenY() - popup.getHeight());
        popup.animatedShow(getScene().getWindow());
    }

    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            canvas.setWidth(width);
            canvas.setHeight(height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            canvas.setWidth(width);
            canvas.setHeight(height);

            if(maxRadius*calculateScaleFactor()!=0 && maxRadius*calculateScaleFactor()<Double.MAX_VALUE && maxRadius*calculateScaleFactor()>Double.MIN_VALUE){
                maxRadius = calculateNodeAndEdgeScaleFactor()*20;
            }
            redraw();
        }
    }

    private void initalizeCalculatorIfNecessary(){
        if(null == graphCalculator){
            graphCalculator = new GraphCalculator();
        }
    }

    /**
     * Translates the x coordinate of the drawn part to where it actually is.
     * The position registered on a node must not be the same as the position at which the node is drawn on screen
     * @param x
     * @return the x coordinate where it actually is
     */
    private double xPositionDrawnToReal(double x){ return (x-maxRadius)/(calculateScaleFactor())+minXPosition; }

    /**
     * Translates the y coordinate of the drawn part to where it actually is.
     * The position registered on a node must not be the same as the position at which the node is drawn on screen
     * @param y
     * @return the y coordinate where it actually is
     */
    private double yPositionDrawnToReal(double y){ return (y-maxRadius)/(calculateScaleFactor())+minYPosition; }

    /**
     * Translates the real x coordinate to where it is drawn.
     * The position registered on a node must not be the same as the position at which the node is drawn on screen
     * @param x
     * @return the x coordinate where it actually is
     */
    private double xPositionRealToDrawn(double x){ return (x-minXPosition) *(calculateScaleFactor()) + maxRadius; }

    /**
     * Translates the real y coordinate to where it is drawn.
     * The position registered on a node must not be the same as the position at which the node is drawn on screen
     * @param y
     * @return the y coordinate where it actually is
     */
    private double yPositionRealToDrawn(double y){ return (y-minYPosition) *(calculateScaleFactor()) + maxRadius;  }

    /**
     * Calculates a Factor to calculate real Positon of a Point to drawn Position
     * @return value to calculate translation factor
     */
    private double calculateScaleFactor(){
        if((width-2*maxRadius)/(maxXPosition-minXPosition)<= (height-2*maxRadius)/(maxYPosition-minYPosition)){
            return (width-2*maxRadius)/(maxXPosition-minXPosition);
        } else{
            return (height-2*maxRadius)/(maxYPosition-minYPosition);
        }
    }

    /**
     * @return Value that scales the Size of Nodes and Edges
     * Takes in account:  actual window size as well as width and height of the real node position
     */
    private double calculateNodeAndEdgeScaleFactor(){
        if(maxXPosition-minXPosition<=maxYPosition-minYPosition){
            if(width/(maxXPosition-minXPosition)<= height/(maxYPosition-minYPosition)){
                return width/scaleOfNodes;
            } else{
                return width/scaleOfNodes/((width/(maxXPosition-minXPosition)/(height/(maxYPosition-minYPosition))));
            }
        }else{
            if(width/(maxXPosition-minXPosition)<= height/(maxYPosition-minYPosition)){
                return height/scaleOfNodes/((height/(maxYPosition-minYPosition)/(width/(maxXPosition-minXPosition))));
            } else{
                return height/scaleOfNodes;
            }
        }
    }

    /**
     * Performs the action of the {@code ingroupConsumer} on all nodes in the group
     * and performs the action of {@code outgroupConsumer} on those that are not
     * @param group The group to process
     * @param ingroupConsumer The action to perform on the ingroup nodes
     * @param outgroupConsumer The action to perform on the outgroup nodes
     */
    public void mapGroup(String group,Consumer<GraphNode> ingroupConsumer, Consumer<GraphNode> outgroupConsumer) {
        for (GraphNode node: nodeEdgeModel.getNodes()){
            if(group.equals(node.getStringAttribute(nodeEdgeModel.getCurrentGroupKey()))) {
                ingroupConsumer.accept(node);
            }else {
                outgroupConsumer.accept(node);
            }
        }
    }

    /**
     * Highlights all nodes in the given group by changing the stroke
     * @param group The group to highlight
     */
    public void highlightGroup(String group) {
        mapGroup(group,
                (node)->node.setStroke(getNodeHighlightingColor()),
                (node)->node.setStroke(nodeEdgeModel.getNodeBorderColor()));
    }

    /**
     * Highlight all nodes which have a group value equal to {@code groupValue}
     * @param groupValue A possible value for the current group
     * @param strokeColor
     * @param fillColor
     */
    public void highlightGroup(String groupValue, Color strokeColor, Color fillColor) {
        mapGroup(groupValue,
                (node)-> {
                    node.setStroke(strokeColor);
                    node.setFill(fillColor);
                },
                (node)-> {
                    node.setStroke(nodeEdgeModel.getNodeBorderColor());
                    node.setFill(nodeEdgeModel.getOrCreateGroupColorScheme().get(node.getStringAttribute(GROUPING_KEY)));
                });
    }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    /**
     * Highlights the node with the default color for highlighting
     * @param node
     */
    public void highlightNode(GraphNode node) {
        highlightNode(node,getNodeHighlightingColor());
    }

    /**
     * Highlights node in the given color
     * @param node
     * @param color
     */
    public void highlightNode(GraphNode node,Color color) {
        node.setStroke(color);
    }

    public double getMinNodeDistance(double radius) {
        return minRadius + radius * getNodeSizeFactor();
    }

    public Color getSelectedNodeFillColor() { return null == selectedNodeFillColor ? _selectedNodeFillColor : selectedNodeFillColor.get(); }

    public void setSelectedNodeFillColor(final Color COLOR) {
        if (null == selectedNodeFillColor) {
            _selectedNodeFillColor = COLOR;
            redraw();
        } else {
            selectedNodeFillColor.set(COLOR);
        }
    }

    public ObjectProperty<Color> selectedNodeFillColorProperty() {
        if (null == selectedNodeFillColor) {
            selectedNodeFillColor = new ObjectPropertyBase<Color>(_selectedNodeFillColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return GraphPanel.this; }
                @Override public String getName() { return "selectedNodeFillColor"; }
            };
            _selectedNodeFillColor = null;
        }
        return selectedNodeFillColor;
    }

    public Color getSelectedNodeBorderColor() { return null == selectedNodeBorderColor ? _selectedNodeBorderColor : selectedNodeBorderColor.get(); }

    public void setSelectedNodeBorderColor(final Color COLOR) {
        if (null == selectedNodeBorderColor) {
            _selectedNodeBorderColor = COLOR;
            redraw();
        } else {
            selectedNodeBorderColor.set(COLOR);
        }
    }

    public ObjectProperty<Color> selectedNodeBorderColorProperty() {
        if (null == selectedNodeBorderColor) {
            selectedNodeBorderColor = new ObjectPropertyBase<Color>(_selectedNodeBorderColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return GraphPanel.this; }
                @Override public String getName() { return "selectedNodeBorderColor"; }
            };
            _selectedNodeBorderColor = null;
        }
        return selectedNodeBorderColor;
    }

    public double getNodeSizeFactor(){return (nodeSizeFactor!=null) ? nodeSizeFactor.doubleValue() : _nodeSizeFactor;}

    public DoubleProperty nodeSizeFactorProperty(){
        if(null == nodeSizeFactor){
            nodeSizeFactor = new SimpleDoubleProperty(_nodeSizeFactor);
            nodeSizeFactor.addListener(observable -> redraw());
        }
        return nodeSizeFactor;
    }

    public void setNodeSizeFactor(double nodeSizeFactor){
        if( null != this.nodeSizeFactor){
            this.nodeSizeFactor.setValue(nodeSizeFactor);
        } else {
            _nodeSizeFactor = nodeSizeFactor;
        }
    }

    /**
     * Executes the Fruchterman-Reingold algorithm, which is responsible for the placement of the nodes
     * depending on their distance and attraction to each other
     */
    public void fruchtermanReingold() {
        if(temp < THRESHOLD  || !isPhysicsActive()) {
            timer.stop();
            return;
        }

        for (GraphNode v : nodeEdgeModel.getNodes()) {
            v.setDisp(Point2D.ZERO);
            for (GraphNode u : nodeEdgeModel.getNodes()) {
                if (u != v) {
                    Point2D positionU = v.getPosition();
                    Point2D positionV = u.getPosition();
                    Point2D vectorUV  = positionU.subtract(positionV);
                    Point2D displacement = vectorUV.normalize()
                                                   .multiply(repulseForce(pointToLength(vectorUV), k));
                    v.setDisp(v.getDisp().add(displacement));
                }
            }
        }

        for (GraphEdge e : nodeEdgeModel.getEdges()) {
            Point2D positionU = e.getU().getPosition();
            Point2D positionV = e.getV().getPosition();
            Point2D vectorUV  = positionV.subtract(positionU);
            double  delta     = pointToLength(vectorUV);
            Point2D addition;
            if(!isForceInverted()){
                addition  = vectorUV.normalize().multiply(attractForce(Math.abs(delta), k)*(e.getForce()+MIN_FORCE));
            } else{
                addition  = vectorUV.normalize().multiply(attractForce(Math.abs(delta), k)/(e.getForce()+MIN_FORCE));
            }
            e.getV().setDisp(e.getV().getDisp().subtract(addition));
            e.getU().setDisp(e.getU().getDisp().add(addition));
        }

        minXPosition = Double.MAX_VALUE;
        minYPosition = Double.MAX_VALUE;
        maxXPosition = Double.MIN_VALUE;
        maxYPosition = Double.MIN_VALUE;

        for (GraphNode v : nodeEdgeModel.getNodes()) {
            Point2D tempPoint = v.getPosition().add(v.getDisp().normalize().multiply(Math.min(pointToLength(v.getDisp()), temp)));
            if(tempPoint.getX()<minXPosition) minXPosition = tempPoint.getX();
            if(tempPoint.getX()>maxXPosition) maxXPosition = tempPoint.getX();
            if(tempPoint.getY()<minYPosition) minYPosition = tempPoint.getY();
            if(tempPoint.getY()>maxYPosition) maxYPosition = tempPoint.getY();
            v.setPosition(new Point2D(tempPoint.getX(), tempPoint.getY()));
        }

        temp = cool(temp);
    }

    public void setNodeEdgeModel(NodeEdgeModel nodeEdgeModel){
        this.nodeEdgeModel = nodeEdgeModel;
        restart();
        this.nodeEdgeModel.isModifiedProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue) {
                redraw();
                this.nodeEdgeModel.isModifiedProperty().setValue(false);
            }
        }));
        System.out.println("The handler is: "+canvas.getOnKeyReleased());
    }

    public void setInitialPosition(int width, int height){
        int q = (int) Math.sqrt( nodeEdgeModel.getNodes().size());
        if (0 == q) { return; }

        int xFactor = width / q;
        int yFactor = height / q;
        int i = 1;
        int j = 1;
        for(GraphNode node: nodeEdgeModel.getNodes()){
            node.setPosition(new Point2D(1+ i*xFactor,1 + j*yFactor));
            i++;
            if(i==q+1){
                j++;
                i=0;
            }
        }
    }


    // ******************** Calculations ******************************************
    public NodeEdgeModel calculateDegreeCentrality(){
        initalizeCalculatorIfNecessary();
        setNodeEdgeModel(graphCalculator.calculateDegreeCentrality(nodeEdgeModel));
        return nodeEdgeModel;
    }

    public NodeEdgeModel calculateDegreeCentralityNormalized(){
        initalizeCalculatorIfNecessary();
        setNodeEdgeModel(graphCalculator.calculateDegreeCentralityNormalized(nodeEdgeModel));
        return nodeEdgeModel;
    }

    public NodeEdgeModel calculateClosenessCentrality(){
        initalizeCalculatorIfNecessary();
        setNodeEdgeModel(graphCalculator.calculateClosenessCentrality(nodeEdgeModel));
        return nodeEdgeModel;
    }

    public NodeEdgeModel calculateClosenessCentralityNormalized(){
        initalizeCalculatorIfNecessary();
        setNodeEdgeModel(graphCalculator.calculateClosenessCentralityNormalized(nodeEdgeModel));
        return nodeEdgeModel;
    }

    public NodeEdgeModel calculateBetweennessCentrality(){
        initalizeCalculatorIfNecessary();
        graphCalculator.calculateBetweennessCentrality(nodeEdgeModel);
        return nodeEdgeModel;
    }

    public void dispose() {
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        canvas.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseHandler);
        canvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);

    }

    public NodeEdgeModel getNodeEdgeModel(){
        return nodeEdgeModel;
    }

    public Color getEdgeColor() { return null == edgeColor ? _edgeColor : edgeColor.get(); }

    public void setEdgeColor(final Color COLOR) {
        if (null == edgeColor) {
            _edgeColor = COLOR;
            redraw();
        } else {
            edgeColor.set(COLOR);
        }
    }

    public ObjectProperty<Color> edgeColorProperty() {
        if (null == edgeColor) {
            edgeColor = new ObjectPropertyBase<Color>(_edgeColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return GraphPanel.this; }
                @Override public String getName() { return "edgeColor"; }
            };
            _edgeColor = null;
        }
        return edgeColor;
    }

    //tag::edgeWidthFactorProperty[]
    public double getEdgeWidthFactor() { return null == edgeWidthFactor ? _edgeWidthFactor : edgeWidthFactor.get(); }

    public void setEdgeWidthFactor(final double WIDTH) {
        if (null == edgeWidthFactor) {
            _edgeWidthFactor = Helper.clamp(1, 10, WIDTH);
            redraw();
        } else {
            edgeWidthFactor.set(WIDTH);
        }
    }

    public DoubleProperty edgeWidthFactorProperty() {
        if (null == edgeWidthFactor) {
            edgeWidthFactor = new DoublePropertyBase(_edgeWidthFactor) {
                @Override protected void invalidated() {
                    set(Helper.clamp(1, 10, get()));
                    redraw();
                }
                @Override public Object getBean() { return GraphPanel.this; }
                @Override public String getName() { return "edgeWidthFactor"; }
            };
            edgeWidthFactor.addListener(observable -> redraw());
        }
        return edgeWidthFactor;
    }
    //end::edgeWidthFactorProperty[]

    public Color getNodeHighlightingColor() { return null == nodeHighlightingColor ? _nodeHighlightingColor : nodeHighlightingColor.get(); }

    public ObjectProperty<Color> nodeHighlightingColorProperty() {
        if (null == nodeHighlightingColor) {
            nodeHighlightingColor = new ObjectPropertyBase<Color>(_nodeHighlightingColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return GraphPanel.this; }
                @Override public String getName() { return "nodeHighlightingColor"; }
            };
            _nodeHighlightingColor = null;
            nodeHighlightingColor.addListener(observable -> redraw());
        }
        return nodeHighlightingColor;
    }

    public void setNodeHighlightingColor(final Color COLOR) {
        if (null == nodeHighlightingColor) {
            _nodeHighlightingColor = COLOR;
            redraw();
        } else {
            nodeHighlightingColor.set(COLOR);
        }
        redraw();
    }

    public double getNodeBorderWidth() { return null == nodeBorderWidth ? _nodeBorderWidth : nodeBorderWidth.get(); }

    public void setNodeBorderWidth(final double WIDTH) {
        if (null == nodeBorderWidth) {
            _nodeBorderWidth = Helper.clamp(1, 10, WIDTH);
            redraw();
        } else {
            nodeBorderWidth.set(WIDTH);
        }
    }

    public DoubleProperty nodeBorderWidthProperty() {
        if (null == nodeBorderWidth) {
            nodeBorderWidth = new DoublePropertyBase(_nodeBorderWidth) {
                @Override protected void invalidated() {
                    set(Helper.clamp(1, 10, get()));
                    redraw();
                }
                @Override public Object getBean() { return GraphPanel.this; }
                @Override public String getName() { return "nodeBorderWidth"; }
            };
            nodeBorderWidth.addListener(observable -> redraw());
        }
        return nodeBorderWidth;
    }

    /**
     * @return Boolean, true if physics simulation is currently active, false otherwise
     */
    public boolean isPhysicsActive() {
        return null != physicsActive ? physicsActive.get() : _physicsActive;
    }

    public BooleanProperty physicsActiveProperty(){
        if (null == physicsActive){
            physicsActive = new SimpleBooleanProperty(_physicsActive);
        }
        return physicsActive;
    }
    /**
     * If set to false, no physical calculations will be performed anymore
     * and Fruchterman-Reingold wont be executed
     * @param physicsActive Boolean, sets if physics simulations should be activated or not
     */
    public void setPhysicsActive(boolean physicsActive) {
        if(null != this.physicsActive){
            this.physicsActive.set(physicsActive);
        } else{
            this._physicsActive = physicsActive;
        }
    }

    /**
     * @return Boolean, true if force is inverted (High Value = small Force), false otherwise
     */
    public boolean isForceInverted() { return null != forceInverted ? forceInverted.get() : _forceInverted; }

    public BooleanProperty forceInvertedProperty(){
        if(null == forceInverted){
            forceInverted = new SimpleBooleanProperty(_forceInverted);
        }
        return forceInverted;
    }

    /**
     * If set True, a high Value of the Edge results in a low attraction
     * @param forceInverted
     */
    public void setForceInverted(boolean forceInverted){
        if(null != this.forceInverted){
            this.forceInverted.set(forceInverted);
        } else{
            this._forceInverted = forceInverted;
        }
    }

    public SimpleDoubleProperty distanceScalingFactorProperty() {
        return distanceScalingFactor;
    }


    // ******************** Redraw ********************************************
    public void redraw() {
        ctx.clearRect(0, 0, width, height);

        ctx.setStroke(getEdgeColor());
        ctx.setLineWidth(getEdgeWidthFactor()* getEdgeWidthFactor());

        for (GraphEdge edge : nodeEdgeModel.getEdges()) {
            ctx.setLineWidth(getEdgeWidthFactor()*calculateNodeAndEdgeScaleFactor()*edge.getWidth() + MIN_EDGE_WIDTH);
            ctx.strokeLine(xPositionRealToDrawn(edge.getU().getPosition().getX()), yPositionRealToDrawn(edge.getU().getPosition().getY()),
                           xPositionRealToDrawn(edge.getV().getPosition().getX()), yPositionRealToDrawn(edge.getV().getPosition().getY()));
        }

        for (GraphNode node : nodeEdgeModel.getNodes()) {
            node.setFill(nodeEdgeModel.getOrCreateGroupColorScheme().get(node.getStringAttribute(nodeEdgeModel.getCurrentGroupKey())));
            ctx.setFill(node.getFill());

            ctx.setLineWidth(2);

            ctx.setStroke(node.getStroke());


            if (node.isSelected()) {
                ctx.save();
                ctx.setFill(getSelectedNodeFillColor());
                ctx.setStroke(getSelectedNodeBorderColor());
                //ctx.setEffect(new DropShadow());
            }
            double val = node.getValue();
            double sizeFactor = getNodeSizeFactor();
            ctx.fillOval(xPositionRealToDrawn(node.getPosition().getX()) - (val*sizeFactor + minRadius)*calculateNodeAndEdgeScaleFactor(),
                         yPositionRealToDrawn(node.getPosition().getY()) - (val*sizeFactor + minRadius)*calculateNodeAndEdgeScaleFactor(),
                         (val * 2*sizeFactor + minRadius*2)*calculateNodeAndEdgeScaleFactor(),
                         (val * 2*sizeFactor + minRadius*2)*calculateNodeAndEdgeScaleFactor());
            ctx.strokeOval(xPositionRealToDrawn(node.getPosition().getX()) - (val*sizeFactor + minRadius)*calculateNodeAndEdgeScaleFactor(),
                           yPositionRealToDrawn(node.getPosition().getY()) - (val*sizeFactor + minRadius)*calculateNodeAndEdgeScaleFactor(),
                           (val * 2*sizeFactor + minRadius*2)*calculateNodeAndEdgeScaleFactor(),
                           (val * 2*sizeFactor + minRadius*2)*calculateNodeAndEdgeScaleFactor());

            if (node.isSelected()) {
                ctx.restore();
            }
        }
    }
}
