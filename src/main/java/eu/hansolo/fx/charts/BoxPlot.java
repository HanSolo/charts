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

import eu.hansolo.fx.charts.color.MaterialDesignColors;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.event.ChartEvt;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.TooltipPopup;
import eu.hansolo.fx.charts.voronoi.VoronoiChart;
import eu.hansolo.toolbox.Statistics;
import eu.hansolo.toolbox.evt.EvtObserver;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@DefaultProperty("children")
public class BoxPlot<T extends ChartItem> extends Region {
    public static final  Color                  DEFAULT_BACKGROUND_COLOR     = Color.TRANSPARENT;
    public static final  Color                  DEFAULT_WHISKER_STROKE_COLOR = Color.BLACK;
    public static final  Color                  DEFAULT_IQR_FILL_COLOR       = Color.TRANSPARENT;
    public static final  Color                  DEFAULT_IQR_STROKE_COLOR     = Color.BLACK;
    public static final  Color                  DEFAULT_MEDIAN_STROKE_COLOR  = Color.RED;
    public static final  Color                  DEFAULT_OUTLIER_FILL_COLOR   = MaterialDesignColors.LIGHT_BLUE_300.get();
    public static final  Color                  DEFAULT_OUTLIER_STROKE_COLOR = Color.TRANSPARENT;
    public static final  Color                  DEFAULT_TEXT_FILL_COLOR      = Color.BLACK;
    private static final double                 PREFERRED_WIDTH              = 50;
    private static final double                 PREFERRED_HEIGHT             = 400;
    private static final double                 MINIMUM_WIDTH                = 50;
    private static final double                 MINIMUM_HEIGHT               = 50;
    private static final double                 MAXIMUM_WIDTH                = 2048;
    private static final double                 MAXIMUM_HEIGHT               = 2048;
    private              String                 userAgentStyleSheet;
    private              double                 width;
    private              double                 height;
    private              Canvas                 canvas;
    private              GraphicsContext        ctx;
    private              ObservableList<T>      items;
    private              EvtObserver<ChartEvt>  itemObserver;
    private              ListChangeListener<T>  itemListListener;
    private              int                    _decimals;
    private              IntegerProperty        decimals;
    private              Locale                 _locale;
    private              ObjectProperty<Locale> locale;
    private              String                 formatString;
    private              TooltipPopup           popup;
    private              double                 median;
    private              double                 q1;          // first quartile
    private              double                 q3;          // third quartile
    private              double                 iqr;         // interquartile range: q3 - q1
    private              double                 iqrFraction; // 1.5 * iqr
    private              double                 minimum;     // q1 - iqrFraction
    private              double                 maximum;     // q3 + iqrFraction
    private              double                 minValue;
    private              double                 maxValue;
    private              double                 min;
    private              double                 max;
    private              List<T>                outliers;
    private              String                 _name;
    private              StringProperty         name;
    private              Color                  _backgroundColor;
    private              ObjectProperty<Color>  backgroundColor;
    private              Color                  _whiskerStrokeColor;
    private              ObjectProperty<Color>  whiskerStrokeColor;
    private              Color                  _iqrFillColor;
    private              ObjectProperty<Color>  iqrFillColor;
    private              Color                  _iqrStrokeColor;
    private              ObjectProperty<Color>  iqrStrokeColor;
    private              Color                  _medianStrokeColor;
    private              ObjectProperty<Color>  medianStrokeColor;
    private              Color                  _outlierFillColor;
    private              ObjectProperty<Color>  outlierFillColor;
    private              Color                  _outlierStrokeColor;
    private              ObjectProperty<Color>  outlierStrokeColor;
    private              boolean                _nameVisible;
    private              BooleanProperty        nameVisible;
    private              Color                  _textFillColor;
    private              ObjectProperty<Color>  textFillColor;
    private              Axis                   _yAxis;
    private              ObjectProperty<Axis>   yAxis;
    private              boolean                sorted;


    // ******************** Constructors **************************************
    public BoxPlot() {
        this("", new ArrayList<>());
    }
    public BoxPlot(final List<T> ITEMS) {
        this("", ITEMS);
    }
    public BoxPlot(final ChartItemSeries<T> SERIES) {
        this(SERIES.getName(), SERIES.getItems());
    }
    public BoxPlot(final String NAME, final List<T> ITEMS) {
        items               = FXCollections.observableArrayList();
        itemObserver        = e -> redraw();
        itemListListener    = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedItem -> addedItem.addChartEvtObserver(ChartEvt.ITEM_UPDATE, itemObserver));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedItem -> removedItem.removeChartEvtObserver(ChartEvt.ITEM_UPDATE, itemObserver));
                }
            }
            final List<Double> values = items.stream().map(item -> item.getValue()).collect(Collectors.toList());
            median      = Statistics.getMedian(values);
            q1          = Statistics.percentile(values, 25);
            q3          = Statistics.percentile(values, 75);
            iqr         = q3 - q1;
            iqrFraction = iqr * 1.5;
            minValue    = items.stream().min(Comparator.comparing(T::getValue)).get().getValue();
            maxValue    = items.stream().max(Comparator.comparing(T::getValue)).get().getValue();
            minimum     = values.stream().filter(v -> v > (q1 - iqrFraction)).min(Comparator.naturalOrder()).get();
            maximum     = values.stream().filter(v -> v < (q3 + iqrFraction)).max(Comparator.naturalOrder()).get();
            sorted      = false;
            outliers.clear();
            outliers.addAll(items.stream().filter(item -> item.getValue() < minimum).collect(Collectors.toList()));
            outliers.addAll(items.stream().filter(item -> item.getValue() > maximum).collect(Collectors.toList()));
            min = Math.min(minValue, minimum);
            max = Math.max(maxValue, maximum);
            if (null != getYAxis()) {
                getYAxis().setMinValue(min);
                getYAxis().setMaxValue(max);
            }
        };
        _name               = NAME;
        _backgroundColor    = DEFAULT_BACKGROUND_COLOR;
        _whiskerStrokeColor = DEFAULT_WHISKER_STROKE_COLOR;
        _iqrFillColor       = DEFAULT_IQR_FILL_COLOR;
        _iqrStrokeColor     = DEFAULT_IQR_STROKE_COLOR;
        _medianStrokeColor  = DEFAULT_MEDIAN_STROKE_COLOR;
        _outlierFillColor   = DEFAULT_OUTLIER_FILL_COLOR;
        _outlierStrokeColor = DEFAULT_OUTLIER_STROKE_COLOR;
        _nameVisible        = false;
        _textFillColor      = DEFAULT_TEXT_FILL_COLOR;
        _decimals           = 0;
        _locale             = Locale.getDefault();
        formatString        = "%." + _decimals + "f";
        popup               = new TooltipPopup("", 3500, true);
        sorted              = false;
        median              = 0;
        q1                  = 0;
        q3                  = 0;
        iqr                 = 0;
        minimum             = 0;
        maximum             = 0;
        minValue            = 0;
        maxValue            = 0;
        outliers            = new ArrayList<>();
        min                 = Double.MAX_VALUE;
        max                 = -Double.MAX_VALUE;
        _yAxis              = null;

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

        getStyleClass().add("box-plot");

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        getChildren().setAll(canvas);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        popup.setOnHiding(e -> popup.setText(""));
        items.addListener(itemListListener);
        canvas.setOnMousePressed(e -> {
            String  tooltipText = new StringBuilder().append("Name    : ").append(getName()).append("\n")
                                                     .append("maxValue: ").append(String.format(getLocale(), formatString, maxValue)).append("\n")
                                                     .append("Maximum : ").append(String.format(getLocale(), formatString, maximum)).append("\n")
                                                     .append("Q3      : ").append(String.format(getLocale(), formatString, q3)).append("\n")
                                                     .append("IQR     : ").append(String.format(getLocale(), formatString, iqr)).append("\n")
                                                     .append("Median  : ").append(String.format(getLocale(), formatString, median)).append("\n")
                                                     .append("Q1      : ").append(String.format(getLocale(), formatString, q1)).append("\n")
                                                     .append("Minimum : ").append(String.format(getLocale(), formatString, minimum)).append("\n")
                                                     .append("minValue: ").append(String.format(getLocale(), formatString, minValue))
                                                     .toString();
            if (!tooltipText.isEmpty()) {
                popup.setX(e.getScreenX() - popup.getWidth() * 0.5);
                popup.setY(e.getScreenY() - 30);
                popup.setText(tooltipText);
                popup.animatedShow(getScene().getWindow());
            }
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

    @Override public ObservableList<Node> getChildren()     { return super.getChildren(); }

    public void dispose() { items.removeListener(itemListListener); }

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
                @Override public Object getBean() { return BoxPlot.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    public List<T> getItems() { return items; }
    public void setItems(final ChartItemSeries<T> SERIES) {
        setItems(SERIES.getItems());
        setName(SERIES.getName());
    }
    public void setItems(final T... ITEMS) { setItems(Arrays.asList(ITEMS)); }
    public void setItems(final List<T> ITEMS) { items.setAll(ITEMS); }
    public void addItem(final T ITEM) {
        if (!items.contains(ITEM)) { items.add(ITEM); }
    }
    public void removeItem(final T ITEM) {
        if (items.contains(ITEM)) { items.remove(ITEM); }
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
                @Override public Object getBean() { return BoxPlot.this; }
                @Override public String getName() { return "decimals"; }
            };
        }
        return decimals;
    }

    public Locale getLocale() { return null == locale ? _locale : locale.get(); }
    public void setLocale(final Locale LOCALE) {
        if (null == locale) {
            _locale = LOCALE;
        } else {
            locale.set(LOCALE);
        }
    }
    public ObjectProperty<Locale> localeProperty() {
        if (null == locale) {
            locale = new ObjectPropertyBase<Locale>(_locale) {
                @Override protected void invalidated() {  }
                @Override public Object getBean() { return BoxPlot.this; }
                @Override public String getName() { return "locale"; }
            };
        }
        _locale = null;
        return locale;
    }

    public Color getBackgroundColor() { return null == backgroundColor ? _backgroundColor : backgroundColor.get(); }
    public void setBackgroundColor(final Color backgroundColor) {
        if (null == this.backgroundColor) {
            _backgroundColor = backgroundColor;
            redraw();
        } else {
            this.backgroundColor.set(backgroundColor);
        }
    }
    public ObjectProperty<Color> backgroundColorProperty() {
        if (null == backgroundColor) {
            backgroundColor = new ObjectPropertyBase<>(_backgroundColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BoxPlot.this; }
                @Override public String getName() { return "backgroundColor"; }
            };
            _backgroundColor = null;
        }
        return backgroundColor;
    }

    public Color getIqrFillColor() { return null == iqrFillColor ? _iqrFillColor : iqrFillColor.get(); }
    public void setIqrFillColor(final Color iqrFillColor) {
        if (null == this.iqrFillColor) {
            _iqrFillColor = iqrFillColor;
            redraw();
        } else {
            this.iqrFillColor.set(iqrFillColor);
        }
    }
    public ObjectProperty<Color> iqrFillColorProperty() {
        if (null == iqrFillColor) {
            iqrFillColor = new ObjectPropertyBase<>(_iqrFillColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BoxPlot.this; }
                @Override public String getName() { return "iqrFillColor"; }
            };
            _iqrFillColor = null;
        }
        return iqrFillColor;
    }

    public Color getIqrStrokeColor() { return null == iqrStrokeColor ? _iqrStrokeColor : iqrStrokeColor.get(); }
    public void setIqrStrokeColor(final Color iqrStrokeColor) {
        if (null == this.iqrStrokeColor) {
            _iqrStrokeColor = iqrStrokeColor;
            redraw();
        } else {
            this.iqrStrokeColor.set(iqrStrokeColor);
        }
    }
    public ObjectProperty<Color> iqrStrokeColorProperty() {
        if (null == iqrStrokeColor) {
            iqrStrokeColor = new ObjectPropertyBase<>(_iqrStrokeColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BoxPlot.this; }
                @Override public String getName() { return "iqrStrokeColor"; }
            };
            _iqrStrokeColor = null;
        }
        return iqrStrokeColor;
    }

    public Color getWhiskerStrokeColor() { return null == whiskerStrokeColor ? _whiskerStrokeColor : whiskerStrokeColor.get(); }
    public void setWhiskerStrokeColor(final Color whiskerStrokeColor) {
        if (null == this.whiskerStrokeColor) {
            _whiskerStrokeColor = whiskerStrokeColor;
            redraw();
        } else {
            this.whiskerStrokeColor.set(whiskerStrokeColor);
        }
    }
    public ObjectProperty<Color> whiskerStrokeColorProperty() {
        if (null == whiskerStrokeColor) {
            whiskerStrokeColor = new ObjectPropertyBase<>(_whiskerStrokeColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BoxPlot.this; }
                @Override public String getName() { return "whiskerStrokeColor"; }
            };
            _whiskerStrokeColor = null;
        }
        return whiskerStrokeColor;
    }

    public Color getMedianStrokeColor() { return null == medianStrokeColor ? _medianStrokeColor : medianStrokeColor.get(); }
    public void setMedianStrokeColor(final Color medianStrokeColor) {
        if (null == this.medianStrokeColor) {
            _medianStrokeColor = medianStrokeColor;
            redraw();
        } else {
            this.medianStrokeColor.set(medianStrokeColor);
        }
    }
    public ObjectProperty<Color> medianStrokeColorProperty() {
        if (null == medianStrokeColor) {
            medianStrokeColor = new ObjectPropertyBase<>(_medianStrokeColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BoxPlot.this; }
                @Override public String getName() { return "medianStrokeColor"; }
            };
            _medianStrokeColor = null;
        }
        return medianStrokeColor;
    }

    public Color getOutlierStrokeColor() { return null == outlierStrokeColor ? _outlierStrokeColor : outlierStrokeColor.get(); }
    public void setOutlierStrokeColor(final Color outlierStrokeColor) {
        if (null == this.outlierStrokeColor) {
            _outlierStrokeColor = outlierStrokeColor;
            redraw();
        } else {
            this.outlierStrokeColor.set(outlierStrokeColor);
        }
    }
    public ObjectProperty<Color> outlierStrokeColorProperty() {
        if (null == outlierStrokeColor) {
            outlierStrokeColor = new ObjectPropertyBase<>(_outlierStrokeColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BoxPlot.this; }
                @Override public String getName() { return "outlierStrokeColor"; }
            };
            _outlierStrokeColor = null;
        }
        return outlierStrokeColor;
    }

    public Color getOutlierFillColor() { return null == outlierFillColor ? _outlierFillColor : outlierFillColor.get(); }
    public void setOutlierFillColor(final Color outlierFillColor) {
        if (null == this.outlierFillColor) {
            _outlierFillColor = outlierFillColor;
            redraw();
        } else {
            this.outlierFillColor.set(outlierFillColor);
        }
    }
    public ObjectProperty<Color> outlierFillColorProperty() {
        if (null == outlierFillColor) {
            outlierFillColor = new ObjectPropertyBase<>(_outlierFillColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BoxPlot.this; }
                @Override public String getName() { return "outlierFillColor"; }
            };
            _outlierFillColor = null;
        }
        return outlierFillColor;
    }

    public boolean getNameVisible() { return null == nameVisible ? _nameVisible : nameVisible.get(); }
    public void setNameVisible(final boolean nameVisible) {
        if (null == this.nameVisible) {
            _nameVisible = nameVisible;
            redraw();
        } else {
            this.nameVisible.set(nameVisible);
        }
    }
    public BooleanProperty nameVisibleProperty() {
        if (null == nameVisible) {
            nameVisible = new BooleanPropertyBase(_nameVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BoxPlot.this; }
                @Override public String getName() { return "nameVisible"; }
            };
        }
        return nameVisible;
    }

    public Color getTextFillColor() { return null == textFillColor ? _textFillColor : textFillColor.get(); }
    public void setTextFillColor(final Color textFillColor) {
        if (null == this.textFillColor) {
            _textFillColor = textFillColor;
            redraw();
        } else {
            this.textFillColor.set(textFillColor);
        }
    }
    public ObjectProperty<Color> textFillColorProperty() {
        if (null == textFillColor) {
            textFillColor = new ObjectPropertyBase<Color>(_textFillColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return BoxPlot.this; }
                @Override public String getName() { return "textFillColor"; }
            };
            _textFillColor = null;
        }
        return textFillColor;
    }

    public void setPopupTimeout(final long milliseconds) { popup.setTimeout(milliseconds); }

    public double getMedian() { return median; }

    public double getQ1() { return q1; }

    public double getQ3() { return q3; }

    public double getIqr() { return iqr; }

    public double getIqrFraction() { return iqrFraction; }

    public double getMinimum() { return minimum; }

    public double getMaximum() { return maximum; }

    public double getMinValue() { return minValue; }

    public double getMaxValue() { return maxValue; }

    public List<T> getOutliers() { return outliers; }

    public Axis getYAxis() { return null == yAxis ? _yAxis : yAxis.get(); }
    public void setYAxis(final Axis yAxis) {
        if (null == this.yAxis) {
            _yAxis = yAxis;
            _yAxis.setMinValue(min);
            _yAxis.setMaxValue(max);
            _yAxis.addChartEvtObserver(ChartEvt.AXIS_RANGE_CHANGED, e -> redraw());
            redraw();
        } else {
            this.yAxis.set(yAxis);
        }
    }
    public ObjectProperty<Axis> yAxisProperty() {
        if (null == yAxis) {
            yAxis = new ObjectPropertyBase<>(_yAxis) {
                @Override protected void invalidated() {
                    _yAxis.setMinValue(min);
                    _yAxis.setMaxValue(max);
                    _yAxis.addChartEvtObserver(ChartEvt.AXIS_RANGE_CHANGED, e -> redraw());
                    redraw();
                }
                @Override public Object getBean() { return BoxPlot.this; }
                @Override public String getName() { return "yAxis"; }
            };
        }
        return yAxis;
    }

    public void resetYAxis() {
        getYAxis().removeAllChartEvtObservers();
        _yAxis = null;
        yAxis  = null;
        min = Math.min(minValue, minimum);
        max = Math.max(maxValue, maximum);
        redraw();
    }

    /**
     * Calling this method will render this chart/plot to a png given of the given width and height
     * @param filename The path and name of the file  /Users/hansolo/Desktop/plot.png
     * @param width The width of the final image in pixels (if &lt; 0 then 400 and if &gt; 4096 then 4096)
     * @param height The height of the final image in pixels (if &lt; 0 then 400 and if &gt; 4096 then 4096)
     * @return True if the procedure was successful, otherwise false
     */
    public boolean renderToImage(final String filename, final int width, final int height) {
        return Helper.renderToImage(BoxPlot.this, width, height, filename);
    }

    /**
     * Calling this method will render this chart/plot to a png given of the given width and height
     * @param width The width of the final image in pixels (if &lt; 0 then 400 and if &gt; 4096 then 4096)
     * @param height The height of the final image in pixels (if &lt; 0 then 400 and if &gt; 4096 then 4096)
     * @return A BufferedImage of this chart in the given dimension
     */
    public BufferedImage renderToImage(final int width, final int height) {
        return Helper.renderToImage(BoxPlot.this, width, height);
    }


    // ******************** Layout ********************************************
    @Override public String getUserAgentStylesheet() {
        if (null == userAgentStyleSheet) { userAgentStyleSheet = BoxPlot.class.getResource("chart.css").toExternalForm(); }
        return userAgentStyleSheet;
    }

    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (width > 0 && height > 0) {
            canvas.setWidth(width);
            canvas.setHeight(height);
            canvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            ctx.setTextBaseline(VPos.CENTER);
        }
        redraw();
    }

    private void redraw() {
        if (!sorted) {
            Collections.sort(items, Comparator.comparing(T::getValue));
            sorted = true;
        }

        ctx.clearRect(0, 0, width, height);
        ctx.setFill(getBackgroundColor());
        ctx.fillRect(0, 0, width, height);

        if (items.isEmpty()) { return; }

        Axis   yAxis = getYAxis();
        double rangeY;
        if (null != yAxis) {
            min = yAxis.getMinValue();
            max = yAxis.getMaxValue();
        }
        rangeY = max - min;

        double insetY          = 10;
        double scaleFactorY    = (height - 2 * insetY) / rangeY;
        double minimumY        = height - insetY - (minimum - min) * scaleFactorY;
        double q3Y             = height - insetY - (q3 - min) * scaleFactorY;
        double iqrY            = iqr * scaleFactorY;
        double maximumY        = height - insetY - (maximum - min) * scaleFactorY;
        double medianY         = height - insetY - (median - min)  * scaleFactorY;
        double outlierDiameter = width * 0.1;
        double outlierRadius   = outlierDiameter * 0.5;
        double centerX         = width * 0.5;
        double fontSize        = width * 0.1;


        // Whisker
        ctx.setStroke(getWhiskerStrokeColor());
        ctx.strokeLine(centerX, minimumY, centerX, maximumY);
        ctx.strokeLine(width * 0.25, minimumY, width * 0.75, minimumY);
        ctx.strokeLine(width * 0.25, maximumY, width * 0.75, maximumY);

        // IQR
        ctx.setStroke(getIqrStrokeColor());
        ctx.setFill(Color.TRANSPARENT == getIqrFillColor() ? getBackgroundColor() : getIqrFillColor());
        ctx.fillRect(0, q3Y, width, iqrY);
        ctx.strokeRect(0, q3Y, width, iqrY);

        // Median
        ctx.setStroke(getMedianStrokeColor());
        ctx.strokeLine(0, medianY, width, medianY);

        // Outliers
        ctx.setStroke(getOutlierStrokeColor());
        ctx.setFill(getOutlierFillColor());
        outliers.forEach(item -> {
            ctx.strokeOval(centerX - outlierRadius, height - insetY - ((item.getValue() - min) * scaleFactorY), outlierDiameter, outlierDiameter);
            ctx.fillOval(centerX - outlierRadius, height - insetY - ((item.getValue() - min) * scaleFactorY), outlierDiameter, outlierDiameter);
        });

        // Name
        if (getNameVisible()) {
            ctx.setTextBaseline(VPos.CENTER);
            ctx.setTextAlign(TextAlignment.CENTER);
            ctx.setFill(getBackgroundColor());
            ctx.fillRect(centerX - 1, minimumY - fontSize * 1.5, 2, fontSize);
            ctx.setFill(getTextFillColor());
            ctx.fillText(getName(), centerX, minimumY - fontSize, width);
        }
    }
}
