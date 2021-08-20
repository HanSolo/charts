/*
 * Copyright (c) 2019 by Gerrit Grunwald
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

package eu.hansolo.fx.charts.pareto;

import eu.hansolo.fx.charts.Axis;
import eu.hansolo.fx.charts.Position;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.InfoPopup;
import eu.hansolo.fx.charts.tools.Point;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observer;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;


/**
 * authors: Michael L\u00E4uchli, MLaeuchli (github)
 *          Stefan Mettler, orizion (github)
 */
public class ParetoPanel extends Region {
    private static final double                           PREFERRED_WIDTH         = 600;
    private static final double                           PREFERRED_HEIGHT        = 600;
    private static final double                           MINIMUM_WIDTH           = 50;
    private static final double                           MINIMUM_HEIGHT          = 50;
    private static final double                           MAXIMUM_WIDTH           = 2048;
    private static final double                           MAXIMUM_HEIGHT          = 1024;
    private              double                           width;
    private              double                           height;
    private              Canvas                           canvas;
    private              GraphicsContext                  ctx;

    private              EventHandler<MouseEvent>         mouseHandler;
    private              ParetoModel                      paretoModel;

    private              DoubleProperty                   barSpacing;
    private              double                           _barSpacing;

    private              double                           yZero;
    private              int                              chartFontSize;
    private              double                           _dataDotSize;
    private              DoubleProperty                   dataDotSize;
    private              Stack<ParetoModel>               modelStack;

    private              BooleanProperty                  smoothPercentageCurve;
    private              boolean                          _smoothPercentageCurve;
    private              BooleanProperty                  useCalculatedSubBarColors;
    private              boolean                          _useCalculatedSubBarColors;
    private              BooleanProperty                  showSubBars;
    private              boolean                          _showSubBars;
    private              BooleanProperty                  singleSubBarCentered;
    private              boolean                          _singelSubBarCentered;
    private              int                              decimals;
    private              String                           numberFormat;
    private              double                           fontSize;

    private              double                           _valueFontYPosition;
    private              DoubleProperty                   valueFontYPosition;
    private              double                           _identifierFontYPosition;
    private              DoubleProperty                   identifierFontYPosition;
    private              double                           _pathFontYPositon;
    private              DoubleProperty                   pathFontYPosition;

    private              Axis                             yAxisLeft;
    private              Axis                             yAxisRight;
    private              List<DataDot>                    dataDots;
    private              ParetoInfoPopup                  popup;

    private              ObjectProperty<Axis>             yAxisRightProperty;
    private              ObjectProperty<Axis>             yAxisLeftProperty;
    private              DoubleProperty                   maxValue;
    private              AnchorPane                       textPane;
    private              BorderPane                       bPane;
    private              Canvas                           labelingCanvas;
    private              GraphicsContext                  labelingCtx;
    private              Color                            _labelingColor;
    private              ObjectProperty<Color>            labelingColor;
    private              Font                             _labelingFont;
    private              ObjectProperty<Font>             labelingFont;
    private              Color                             _percentageLineDataDotColor;
    private              ObjectProperty<Color>             percentageLineDataDotColor;
    private              Color                             _percentageLineColor;
    private              ObjectProperty<Color>             percentageLineColor;

    private              HashMap<String, ArrayList<Color>> colorThemes;
    private              boolean                           delayRedraw;

    private              ArrayList<Observer>               observerList;


    // ******************** Constructors **************************************
    public ParetoPanel(final ParetoModel MODEL){
        decimals     = 1;
        numberFormat = new StringBuilder("%.").append(decimals).append("f").toString();
        _labelingFont = Fonts.opensansRegular(10);
        paretoModel  = MODEL;
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        modelStack = new Stack<>();
        dataDots = new ArrayList<>();
        popup = new ParetoInfoPopup();
        mouseHandler       = this::handleMouseEvents;
        modelStack.push(paretoModel);

        _labelingColor = Color.BLACK;

        _singelSubBarCentered = true;

        initGraphics();
        registerListeners();

        colorThemes = new HashMap<>();
        ArrayList<Color> defaultColorTheme = new ArrayList<>();
        defaultColorTheme.add(Color.BLUE);
        colorThemes.putIfAbsent("Default", defaultColorTheme);

        _valueFontYPosition         = 20;
        _identifierFontYPosition    = 40;
        _pathFontYPositon           = 65;
        _percentageLineDataDotColor = Color.BLACK;
        _percentageLineColor        = Color.BLACK;
        _useCalculatedSubBarColors  = true;
        _smoothPercentageCurve      = false;
        _showSubBars                = true;

        textPane   = new AnchorPane();

        yAxisLeft  = Helper.createLeftAxis(0, paretoModel.getTotal(), false, 80d);
        yAxisRight = Helper.createRightAxis(0,100,true,80d);

        maxValue   = new DoublePropertyBase(paretoModel.getTotal()) {
            @Override public Object getBean() { return ParetoPanel.this; }
            @Override public String getName() { return "maxValue"; }
        };

        yAxisLeft.setTitle(paretoModel.getTitle());
        yAxisLeft.setTitleFontSize(30);

        //yAxisRight.setTitle("Percentage"); //title written into the numbers of scale
        yAxisRight.setTitleFontSize(30);

        yAxisRight.setUnit("\\u0025");

        yAxisLeft.maxValueProperty().bindBidirectional(maxValue);

        yAxisRight.setPosition(Position.CENTER);
        bPane = new BorderPane();
        bPane.setPrefWidth(yAxisRight.getWidth()+ canvas.getWidth()+ yAxisRight.getWidth());
        bPane.setCenter(canvas);

        yAxisRightProperty = new ObjectPropertyBase<Axis>(yAxisRight) {
            @Override public Object getBean() { return ParetoPanel.this; }
            @Override public String getName() { return "yAxisRight"; }
        };
        yAxisLeftProperty  = new ObjectPropertyBase<Axis>(yAxisLeft) {
            @Override public Object getBean() { return ParetoPanel.this; }
            @Override public String getName() { return "yAxisLeft"; }
        };

        bPane.rightProperty().bind(yAxisRightProperty);
        bPane.leftProperty().bind(yAxisLeftProperty);

        _barSpacing = 5;

        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        bPane.setMaxSize(width, height);
        bPane.setPrefSize(width, height);

        canvas.setWidth(width-yAxisRight.getWidth()-yAxisLeft.getWidth());

        textPane.setMaxHeight(80);
        textPane.setMinHeight(80);

        StackPane test = new StackPane();
        test.setPrefHeight(80d);
        labelingCanvas = new Canvas();

        labelingCanvas.setHeight(80);
        labelingCtx = labelingCanvas.getGraphicsContext2D();

        delayRedraw = false;

        bPane.setBottom(labelingCanvas);

        getChildren().setAll(bPane);

        drawParetoChart();
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
        width  = PREFERRED_WIDTH;
        height = PREFERRED_HEIGHT;

        canvas = new Canvas();
        ctx    = canvas.getGraphicsContext2D();

        _smoothPercentageCurve = true;

        _dataDotSize = 20;
        chartFontSize = 20;

        yZero                   = height;//+horizontalChartMargin;
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseHandler);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
        canvas.setFocusTraversable(true); //needed, else Key Events wont register

        addChangeListenerToParetobarRecursiv(paretoModel.getData());
    }

    private void addChangeListenerToParetobarRecursiv(List<ParetoBar> bars){
        for(ParetoBar bar: bars){
            if(null != bar.getBars()&& !bar.getBars().isEmpty()){
                addChangeListenerToParetobarRecursiv(bar.getBars());
            }
            bar.fillColorProperty().addListener(observable -> {
                if(!delayRedraw) {drawParetoChart();}
            });
        }
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
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        canvas.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseHandler);
        canvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
    }


    /**
     * Detects if the point is inside the given ParetoBar
     * @param X coordinate
     * @param Y coordinate
     * @param BAR the ParetoBar to demo against
     * @return
     */
    private boolean insideBar(final double X, final double Y, final ParetoBar BAR) {
        return BAR.getX() <= X && X <= BAR.getX()+ BAR.getWidth()
               && BAR.getY() <= Y && Y <= BAR.getY()+ BAR.getHeight();
    }

    /**
     * Detects if a mouse press is inside one of the DataDots on the percentage line
     * @param X
     * @param Y
     * @param DATA_DOT
     * @return
     */
    private boolean insideDataDot(final double X, final double Y, final DataDot DATA_DOT){
        return ( (X-DATA_DOT.getX()) * (X-DATA_DOT.getX()) + (Y-DATA_DOT.getY()) * (Y-DATA_DOT.getY()) )
               < (getDataDotSize() /2)*(getDataDotSize() /2);
    }

    /**
     * Handles the clicks on DataDots and ParetoBars
     * @param EVT
     */
    private void handleMouseEvents(final MouseEvent EVT) {
        final EventType<? extends MouseEvent> TYPE = EVT.getEventType();
        double                                X    = EVT.getX();
        double                                Y    = EVT.getY();

        if(MouseEvent.MOUSE_PRESSED.equals(TYPE)) {
            for (DataDot dot: dataDots) {
                if(insideDataDot(X,Y,dot)) {
                    showPopup(EVT.getScreenX(),EVT.getScreenY(),dot);
                    return;
                }
            }
            for(ParetoBar bar: paretoModel.getData()) {
                if(insideBar(X,Y,bar)) {
                    if(EVT.isShiftDown()){
                        filterChartByBar(bar);
                    }else {
                        cascadeIntoBar(bar);
                    }
                    break;
                }
            }

        }
    }

    /**
     * Shows the popup for the given DataDot at the given coordinates
     * @param X The x-coordinate for the popup
     * @param Y The y-coordinate for the popup
     * @param DATA_DOT The DataDot from which you want the information for the popup
     */
    private void showPopup(final double X, final double Y,final DataDot DATA_DOT) {
        String[] labels = new String[2];
        labels[0] = "Total Prozent";
        labels[1] = "Total Wert";

        String[] values = new String[2];
        double percentage = (DATA_DOT.value/paretoModel.getTotal())*100;
        values[0] = Double.toString(Math.round(percentage*100d)/100d);
        values[1] = Double.toString(Math.round(DATA_DOT.value*100d)/100d);

        popup.update(labels, values);

        popup.setX(X+15);
        popup.setY(Y);
        popup.animatedShow(getScene().getWindow());
    }

    private void hidePopup() {
        popup.setOpacity(0);
        popup.animatedHide();
    }

    /**
     * Replaces the current ParetoModel with a ParetoModel containing all ParetoBars,
     * except the one passed to this function
     * @param BAR
     */
    private void filterChartByBar(final ParetoBar BAR) {
        modelStack.push(paretoModel);
        ParetoModel model = new ParetoModel(paretoModel);

        List<ParetoBar> bars = model.getData().stream().filter(bar2  -> bar2 != BAR).collect(Collectors.toList());

        model.setData(bars);
        paretoModel = model;
        //yAxisLeft.setMaxValue(paretoModel.getTotal());
        maxValue.setValue(paretoModel.getTotal());

        drawParetoChart();

    }

    /**
     * Display the nested bars of the chosen BAR
     * @param BAR
     */
    private void cascadeIntoBar(final ParetoBar BAR) {
        if(null == BAR.getBars() || BAR.getBars().isEmpty()) return;
        modelStack.push(paretoModel);
        ParetoModel model = new ParetoModel(paretoModel);
        model.setData(BAR.getBars());
        model.setTitle(paretoModel.getTitle() + " / " + BAR.getName());
        paretoModel = model;
        //yAxisLeft.setMaxValue(paretoModel.getTotal());
        maxValue.setValue(paretoModel.getTotal());

        drawParetoChart();
        modelChanged();
    }

    /**
     * Draws the ParetoBars and the percentage line
     */
    private void drawBarsAndLine() {
        List<ParetoBar> entryList = paretoModel.getData();
        double total = yAxisLeft.getMaxValue();
        double xStartPosition = canvas.getWidth()/ (paretoModel.getData().size())/100* getBarSpacing();
        double barWidth = canvas.getWidth()/paretoModel.getData().size() - canvas.getWidth()/ (paretoModel.getData().size())/100* getBarSpacing() /paretoModel.getData().size()*(paretoModel.getData().size()+1);
        //double barWidth = ((canvas.getWidth() -verticalChartMargin) / paretoModel.getData().size())  - verticalBarMargin/paretoModel.getData().size();
        double heightPerUnit = (height / Math.ceil(total)) ;

        entryList.sort((c1,c2)  -> {
            if(c1.getValue() == c2.getValue()) return 0;
            return c1.getValue() > c2.getValue() ? -1 : 1;
        });

        Iterator it = entryList.iterator();

        Point  points[] = new Point[entryList.size()];
        double sumUp    = 0;
        int    i        =0;
        while(it.hasNext()) {
            ParetoBar bar = (ParetoBar) it.next();
            double barHeight = heightPerUnit * bar.getValue();
            sumUp += bar.getValue();

            //draw bars
            Color barColor = bar.getFillColor();
            ctx.save();
            ctx.setFill(barColor);
            ctx.strokeRect(xStartPosition,yZero-barHeight,barWidth,barHeight);
            ctx.fillRect(xStartPosition,yZero-barHeight,barWidth,barHeight);
            ctx.restore();

            //Draw nested bars
            if (null != bar.getBars() && !bar.getBars().isEmpty()) {
                double noOfSubBars = bar.getBars().size();
                double subBarWidth = barWidth / noOfSubBars;
                double stepX = 0;
                Color subBarColor = barColor;
                //tag::coloringSubBars[]
                if(isShowSubBars()) {
                    for (ParetoBar subBar : bar.getBars()) {
                        double subBarHeight = heightPerUnit * subBar.getValue();
                        ctx.save();
                        if (isUseCalculatedSubBarColors()) {
                            subBarColor = Helper.isBright(barColor)
                                          ? Color.hsb(subBarColor.getHue(),
                                                      Helper.clamp(0, 1, subBarColor.getSaturation() * 1.75),
                                                      Helper.clamp(0, 1, subBarColor.getBrightness() * 0.75))
                                          : Color.hsb(subBarColor.getHue(),
                                                      Helper.clamp(0, 1, subBarColor.getSaturation() * 0.75),
                                                      Helper.clamp(0, 1, subBarColor.getBrightness() * 1.75));
                            ctx.setFill(subBarColor);
                        } else {
                            ctx.setFill(subBar.getFillColor());
                        }
                        if(bar.getBars().size()>1 || !getSingleSubBarCentered()) {
                            ctx.fillRect(xStartPosition + stepX, yZero - subBarHeight, subBarWidth, subBarHeight);
                            ctx.restore();
                            stepX += subBarWidth;
                        } else{
                            ctx.fillRect(xStartPosition + stepX +subBarWidth/4, yZero -subBarHeight, subBarWidth/2, subBarHeight);
                        }
                    }
                }
                //end::coloringSubBars[]
            }


            //remember its position and size, for click interactions
            bar.setX(xStartPosition);
            bar.setY(yZero-barHeight);
            bar.setWidth(barWidth);
            bar.setHeight(barHeight);

            double minValueTextHeight = yZero- barHeight/2;
            if(minValueTextHeight > yZero- chartFontSize *2) minValueTextHeight = yZero- chartFontSize *2;
            //print text on bars
            /*ctx.save();
            ctx.setFill(bar.getTextColor());
            ctx.setFont(Fonts.latoRegular(height * 0.035));
            ctx.fillText(String.format(Locale.US, numberFormat, bar.getValue()),
                    xStartPosition + barWidth / 2,yZero - barHeight + fontSize,barWidth);
            ctx.fillText(bar.getName(),xStartPosition+barWidth/2,yZero - height * 0.01,barWidth);
            ctx.restore();
            */
            labelingCtx.setFill((labelingColor != null) ? labelingColor.getValue() : _labelingColor);
            labelingCtx.setFont((labelingFont != null) ? labelingFont.getValue() : _labelingFont);
            labelingCtx.setTextAlign(TextAlignment.CENTER);
            labelingCtx.fillText(String.format(Locale.US, numberFormat, bar.getValue()),
                                          xStartPosition + 80 + barWidth/2 , getValueFontYPosition(), barWidth);
            labelingCtx.fillText(bar.getName(), xStartPosition + 80 + barWidth / 2, getIdentifierFontYPosition(), barWidth);


            //Draw the percentage line
            points[i] = new Point(xStartPosition+barWidth/2,yZero - height * (sumUp/total));

            //Save position and value of data dots
            dataDots.add(new DataDot(points[i].getX(),
                                     points[i].getY(),sumUp));
            i++;

            //xStartPosition += (2*verticalBarMargin + barWidth);
            xStartPosition += canvas.getWidth()/ (paretoModel.getData().size())/100* getBarSpacing() + barWidth;
        }

        labelingCtx.setFont((labelingFont != null) ? labelingFont.getValue() : _labelingFont);
        labelingCtx.fillText(paretoModel.getTitle(), width / 2, getPathFontYPositon(), width);

        //draws the percentage line
        ctx.setLineWidth(3);
        ctx.setStroke(getPercentageLineColor());

        if(isSmoothPercentageCurve()) {
            Point[] newPoints =  Helper.subdividePoints(points,10);
            for (int j = 1; j < newPoints.length; j++) {
                ctx.strokeLine(newPoints[j-1].getX(),newPoints[j-1].getY(),newPoints[j].getX(),newPoints[j].getY());
            }
        }else {
            for (int j = 1; j < points.length; j++) {
                ctx.strokeLine(points[j-1].getX(),points[j-1].getY(),points[j].getX(),points[j].getY());
            }

        }


        //Draw data dots
        ctx.setFill(getPercentageLineDataDotColor());
        for (int j = 0; j < points.length; j++) {
            ctx.fillOval(points[j].getX()- (getDataDotSize() /2),
                         points[j].getY()- (getDataDotSize() /2), getDataDotSize(), getDataDotSize());
        }
    }

    private class DataDot {
        private double x;
        private double y;
        private double value;

        DataDot(double x, double y, double value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }

        double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }

    private void drawParetoChart() {
        ctx.clearRect(0,0,width,height);

        labelingCtx.clearRect(0, 0, labelingCanvas.getWidth(), labelingCanvas.getHeight());
        ctx.strokeRect(0,0,width,height);

        ctx.setStroke(Color.BLACK);
        ctx.setFill(Color.BLUE);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setFont(new Font(chartFontSize));

        //****************************Draw bars and percentage line*********************************

        drawBarsAndLine();

    }

    private void modelChanged(){
        if(null != observerList && !observerList.isEmpty()){
            for(Observer observer: observerList){
                observer.update(null, null);
            }
        }
    }
    private void activateColorThemeRecursive(final List<ParetoBar> BARS, final String NAME){
        int i = 0;
        for(ParetoBar bar : BARS){
            if(i>= colorThemes.get(NAME).size()) i=0;
            bar.setFillColor(colorThemes.get(NAME).get(i++));
            if(null != bar.getBars() && !bar.getBars().isEmpty()){
                activateColorThemeRecursive(bar.getBars(), NAME);
            }
        }
    }

    //tag::returnToPreviousLayer[]
    public void returnToPreviousLayer() {
        if(!modelStack.empty()) {
            paretoModel = modelStack.pop();
            maxValue.setValue(paretoModel.getTotal());

            drawParetoChart();
            modelChanged();
        }
    }
    //end::returnToPreviousLayer[]

    public void addColorTheme(final String NAME, final List<Color> COLOR_THEME){
        if(null == colorThemes){ colorThemes = new HashMap<>(); }
        colorThemes.put(NAME, new ArrayList<>(COLOR_THEME));
    }
    public Set<String> getColorThemeKeys() { return colorThemes.keySet(); }
    public void activateColorTheme(final String NAME){
        if (colorThemes.containsKey(NAME)) {
            delayRedraw = true;
            activateColorThemeRecursive(paretoModel.getData(), NAME);
            delayRedraw = false;
            drawParetoChart();
        } else {
            delayRedraw = true;
            activateColorThemeRecursive(paretoModel.getData(), NAME);
            delayRedraw = false;
            drawParetoChart();
        }
    }

    public Color getFontColor(){ return (null != labelingColor) ? labelingColor.getValue() : _labelingColor; }
    public void setFontColor(final Color COLOR){
        if(null != labelingColor){
            _labelingColor = COLOR;
            drawParetoChart();
        } else {
            labelingColor.setValue(COLOR);
        }
    }
    public ObjectProperty<Color> fontColorProperty(){
        if(null == labelingColor){
            labelingColor = new SimpleObjectProperty<>(_labelingColor);
            labelingColor.addListener(observable -> drawParetoChart());
        }
        return labelingColor;
    }

    public Font getFont() { return (null != labelingFont) ? labelingFont.getValue() : _labelingFont; }
    public void setFont(final Font FONT){
        if(null == labelingFont){
            _labelingFont = FONT;
            drawParetoChart();
        } else {
            labelingFont.setValue(FONT);
        }
    }
    public ObjectProperty<Font> fontProperty(){
        if(null == labelingFont){
            labelingFont = new SimpleObjectProperty<>(_labelingFont);
            labelingFont.addListener(observable -> drawParetoChart());
        }
        return labelingFont;
    }

    public boolean isSmoothPercentageCurve(){ return ( null != smoothPercentageCurve) ? smoothPercentageCurve.getValue() : _smoothPercentageCurve; }
    public void setSmoothPercentageCurve(final boolean SMOOTHED){
        if( null == smoothPercentageCurve){
            _smoothPercentageCurve = SMOOTHED;
            drawParetoChart();
        } else {
            smoothPercentageCurve.setValue(SMOOTHED);
        }
    }
    public BooleanProperty smoothPercentageCurveProperty(){
        if( null == smoothPercentageCurve){
            smoothPercentageCurve = new SimpleBooleanProperty(_smoothPercentageCurve);
            smoothPercentageCurve.addListener(observable -> drawParetoChart());
        }
        return smoothPercentageCurve;
    }

    //tag::getValueFontYPosition[]
    public double getValueFontYPosition() {
        return (null != valueFontYPosition)
               ? valueFontYPosition.doubleValue()
               : _valueFontYPosition;
    }
    public void setValueFontYPosition(final double POSITION){
        if( null == valueFontYPosition){
            _valueFontYPosition = POSITION;
            drawParetoChart();
        } else {
            valueFontYPosition.setValue(POSITION);
        }
    }
    public DoubleProperty valueFontYPositionProperty(){
        if( null == valueFontYPosition){
            valueFontYPosition = new SimpleDoubleProperty(_valueFontYPosition);
            valueFontYPosition.addListener(observable -> drawParetoChart());
        }
        return valueFontYPosition;
    }
    //end::getValueFontYPosition[]

    public double getIdentifierFontYPosition(){ return (null != identifierFontYPosition) ? identifierFontYPosition.doubleValue() : _identifierFontYPosition;    }
    public void setIdentifierFontYPosition(final double POSITION){
        if( null == identifierFontYPosition){
            _identifierFontYPosition = POSITION;
            drawParetoChart();
        } else {
            identifierFontYPosition.setValue(POSITION);
        }
    }
    public DoubleProperty identifierFontYPositionProperty(){
        if( null == identifierFontYPosition){
            identifierFontYPosition = new SimpleDoubleProperty(_identifierFontYPosition);
            identifierFontYPosition.addListener(observable -> drawParetoChart());
        }
        return identifierFontYPosition;
    }

    public double getPathFontYPositon(){ return (null!=pathFontYPosition) ? pathFontYPosition.getValue() : _pathFontYPositon;    }
    public void setPathFontYPosition(final double POSITION){
        if( null == pathFontYPosition){
            _pathFontYPositon = POSITION;
            drawParetoChart();
        } else {
            pathFontYPosition.setValue(POSITION);
        }
    }
    public DoubleProperty pathFontYPositionProperty(){
        if(null == pathFontYPosition){
            pathFontYPosition = new SimpleDoubleProperty(_pathFontYPositon);
            pathFontYPosition.addListener(observable -> drawParetoChart());
        }
        return pathFontYPosition;
    }

    public double getDataDotSize(){ return (null != dataDotSize) ? dataDotSize.doubleValue() : _dataDotSize;    }
    public void setDataDotSize(final double DOT_SIZE){
        if (null == dataDotSize) {
            _dataDotSize = DOT_SIZE;
            drawParetoChart();
        } else {
            dataDotSize.setValue(DOT_SIZE);
        }
    }
    public DoubleProperty dataDotSizeProperty() {
        if( null == dataDotSize){
            dataDotSize = new SimpleDoubleProperty(_dataDotSize);
            dataDotSize.addListener(observable -> drawParetoChart());
        }
        return dataDotSize;
    }

    public ParetoModel getParetoModel(){
        return paretoModel;
    }

    public double getBarSpacing(){ return (null != barSpacing) ? barSpacing.doubleValue() : _barSpacing; }
    public void setBarSpacing(final double SPACING){
        if (null == barSpacing) {
            _barSpacing = SPACING;
            drawParetoChart();
        } else {
            barSpacing.setValue(SPACING);
        }
    }
    public DoubleProperty barSpacingProperty(){
        if(null == barSpacing){
            barSpacing = new SimpleDoubleProperty(_barSpacing);
            barSpacing.addListener(observable -> drawParetoChart());
        }
        return barSpacing;
    }

    public Color getPercentageLineDataDotColor() { return (null != percentageLineDataDotColor) ? percentageLineDataDotColor.getValue() : _percentageLineDataDotColor;    }
    public void setPercentageLineDataDotColor(final Color COLOR) {
        if( null == percentageLineDataDotColor){
            _percentageLineDataDotColor = COLOR;
            drawParetoChart();
        } else {
            percentageLineDataDotColor.setValue(COLOR);
        }
    }
    public ObjectProperty<Color> percentageLineDataDotColorProperty(){
        if( null == percentageLineDataDotColor){
            percentageLineDataDotColor = new ObjectPropertyBase<Color>(_percentageLineDataDotColor) {
                @Override public Object getBean() { return ParetoPanel.this; }
                @Override public String getName() { return "percentageLineDataDotColor"; }
            };
            _percentageLineDataDotColor = null;
            percentageLineDataDotColor.addListener(observable -> drawParetoChart());
        }
        return percentageLineDataDotColor;
    }

    public Color getPercentageLineColor() { return (null != percentageLineColor) ? percentageLineColor.getValue() : _percentageLineColor;    }
    public void setPercentageLineColor(final Color COLOR) {
        if( null == percentageLineColor){
            _percentageLineColor = COLOR;
            drawParetoChart();
        } else{
            percentageLineColor.setValue(COLOR);
        }
    }
    public ObjectProperty<Color> percentageLineColorProperty() {
        if( null == percentageLineColor){
            percentageLineColor = new ObjectPropertyBase<Color>() {
                @Override public Object getBean() { return ParetoPanel.this; }
                @Override public String getName() { return "percentageLineColor"; }
            };
            _percentageLineColor = null;
            percentageLineColor.addListener(observable -> drawParetoChart());
        }
        return percentageLineColor;
    }

    public boolean isUseCalculatedSubBarColors(){ return (null != useCalculatedSubBarColors) ? useCalculatedSubBarColors.getValue() : _useCalculatedSubBarColors; }
    public BooleanProperty useCalculatedSubBarColorsProperty(){
        if( null == useCalculatedSubBarColors){
            useCalculatedSubBarColors = new BooleanPropertyBase(_useCalculatedSubBarColors) {
                @Override public Object getBean() { return ParetoPanel.this; }
                @Override public String getName() { return "useCalculatedSubBarColors"; }
            };
            useCalculatedSubBarColors.addListener(observable -> drawParetoChart());
        }
        return useCalculatedSubBarColors;
    }

    public boolean isShowSubBars(){ return ( null!=showSubBars) ? showSubBars.getValue() : _showSubBars;}
    public void setShowSubBars(final boolean SHOW){
        if(null != this.showSubBars){
            this.showSubBars.setValue(SHOW);
        } else {
            _showSubBars = SHOW;
            drawParetoChart();
        }
    }
    public BooleanProperty showSubBarsProperty(){
        if( null == showSubBars){
            showSubBars = new SimpleBooleanProperty(_showSubBars);
            showSubBars.addListener(observable -> drawParetoChart());
        }
        return showSubBars;
    }


    public boolean getSingleSubBarCentered(){ return (null != singleSubBarCentered) ? singleSubBarCentered.getValue() : _singelSubBarCentered;}
    public void setSingleSubBarCentered(final boolean CENTERED){
        if(null == singleSubBarCentered){
            _singelSubBarCentered = CENTERED;
        } else {
            singleSubBarCentered.setValue(CENTERED);
        }
    }
    public BooleanProperty singleSubBarCenteredProperty(){
        if(null == singleSubBarCentered){
            singleSubBarCentered = new BooleanPropertyBase(_singelSubBarCentered) {
                @Override public Object getBean() { return ParetoPanel.this; }
                @Override public String getName() { return "singleSubBarCentered"; }
            };
            singleSubBarCentered.addListener(observable -> drawParetoChart());
        }
        return singleSubBarCentered;
    }

    public void addAndActivatedColorTheme(final String NAME, final List<Color> THEME) {
        addColorTheme(NAME, THEME);
        activateColorTheme(NAME);
    }

    public void setUseCalculatedSubBarColors(final boolean USE){
        if( null == useCalculatedSubBarColors) {
            _useCalculatedSubBarColors = USE;
            drawParetoChart();
        } else{
            useCalculatedSubBarColors.setValue(USE);
        }
    }

    public void addObserver(Observer observer){
        if(null == observerList){
            observerList = new ArrayList<>();
        }
        observerList.add(observer);
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom()-80;

        if (width > 0 && height > 0) {
            bPane.setMaxSize(width, height);
            bPane.setPrefSize(width, height);
            canvas.setWidth(width-yAxisRight.getWidth()-yAxisLeft.getWidth());
            labelingCanvas.setWidth(width);
            canvas.setHeight(height);

            yZero = height;
            fontSize = height * 0.035;
            drawParetoChart();
        }
    }
}
