package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.XYZData;
import eu.hansolo.fx.charts.model.XYZChartModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;


public class XYZChart<T extends XYZData> extends Region implements Chart {
    private static final double                PREFERRED_WIDTH  = 250;
    private static final double                PREFERRED_HEIGHT = 250;
    private static final double                MINIMUM_WIDTH    = 0;
    private static final double                MINIMUM_HEIGHT   = 0;
    private static final double                MAXIMUM_WIDTH    = 4096;
    private static final double                MAXIMUM_HEIGHT   = 4096;
    private static       double                aspectRatio;
    private              boolean               keepAspect;
    private              double                size;
    private              double                width;
    private              double                height;
    private              Pane                  pane;
    private              Color                 _chartBackgroundColor;
    private              ObjectProperty<Color> chartBackgroundColor;
    private              XYZChartModel<T>      model;
    private              Canvas                canvas;
    private              GraphicsContext       ctx;
    private              double                scaleX;
    private              double                scaleY;
    private              double                scaleZ;
    private              ChartType             chartType;


    // ******************** Constructors **************************************
    public XYZChart(final XYZChartModel<T> MODEL) {
        this(MODEL, ChartType.SCATTER);
    }
    public XYZChart(final XYZChartModel<T> MODEL, final ChartType TYPE) {
        this(MODEL, TYPE, Color.BLACK, Color.TRANSPARENT);
    }
    public XYZChart(final XYZChartModel<T> MODEL, final ChartType TYPE, final Color STROKE_COLOR, final Color FILL_COLOR) {
        getStylesheets().add(XYChart.class.getResource("chart.css").toExternalForm());
        aspectRatio           = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        keepAspect            = false;
        _chartBackgroundColor = Color.WHITE;
        model                 = MODEL;
        scaleX                = 1;
        scaleY                = 1;
        scaleZ                = 1;
        chartType             = TYPE;

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

        getStyleClass().setAll("chart", "xyz-chart");

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        pane = new Pane(canvas);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());

        model.setOnModelEvent(modelEvent -> redraw());
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT)  { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH)  { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT)  { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH)  { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    @Override public ChartType getChartType() { return chartType; }
    @Override public void setChartType(final ChartType TYPE) {
        chartType = TYPE;
        redraw();
    }

    public Color getChartBackgroundColor() { return null == chartBackgroundColor ? _chartBackgroundColor : chartBackgroundColor.get(); }
    public void setChartBackgroundColor(final Color COLOR) {
        if (null == chartBackgroundColor) {
            _chartBackgroundColor = COLOR;
            redraw();
        } else {
            chartBackgroundColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> chartBackgroundProperty() {
        if (null == chartBackgroundColor) {
            chartBackgroundColor = new ObjectPropertyBase<Color>(_chartBackgroundColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return XYZChart.this; }
                @Override public String getName() { return "chartBackgroundColor"; }
            };
            _chartBackgroundColor = null;
        }
        return chartBackgroundColor;
    }

    public XYZChartModel<T> getModel() { return model; }


    // ******************** Draw Chart ****************************************
    private void drawChart() {
        if (null == model) return;

        ctx.setFill(getChartBackgroundColor());
        ctx.clearRect(0, 0, width, height);
        for (XYZData item : model.getItems()) {
            double x = item.getX() * scaleX;
            double y = item.getY() * scaleY;
            double z = item.getZ() * scaleZ;
            switch(chartType) {
                case BUBBLE:
                    double halfZ = z * 0.5;
                    ctx.setStroke(Color.TRANSPARENT);
                    ctx.setFill(item.getColor());
                    ctx.fillOval(x - halfZ, height - y - halfZ, z, z);
                    break;
                default     :
                    break;
            }
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (keepAspect) {
            if (aspectRatio * width > height) {
                width = 1 / (aspectRatio / height);
            } else if (1 / (aspectRatio / height) > width) {
                height = aspectRatio * width;
            }
        }

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            canvas.setWidth(width);
            canvas.setHeight(height);

            scaleX = width / model.getItems().size();
            scaleY = height / model.getRangeY();

            redraw();
        }
    }

    private void redraw() {
        drawChart();
    }
}
