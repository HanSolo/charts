 /*
  * SPDX-License-Identifier: Apache-2.0
  *
  * Copyright 2016-2021 Gerrit Grunwald.
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
 import eu.hansolo.fx.charts.event.ChartEvt;
 import eu.hansolo.fx.charts.event.SelectionEvt;
 import eu.hansolo.toolbox.evt.Evt;
 import eu.hansolo.toolbox.evt.EvtObserver;
 import eu.hansolo.toolbox.evt.EvtType;
 import eu.hansolo.toolboxfx.font.Fonts;
 import eu.hansolo.fx.charts.series.ChartItemSeries;
 import eu.hansolo.fx.charts.series.Series;
 import eu.hansolo.fx.charts.tools.Helper;
 import eu.hansolo.fx.charts.tools.InfoPopup;
 import eu.hansolo.fx.charts.tools.NumberFormat;
 import eu.hansolo.fx.charts.tools.Order;
 import eu.hansolo.fx.geometry.Rectangle;
 import javafx.beans.DefaultProperty;
 import javafx.beans.property.BooleanProperty;
 import javafx.beans.property.BooleanPropertyBase;
 import javafx.beans.property.ObjectProperty;
 import javafx.beans.property.ObjectPropertyBase;
 import javafx.collections.ListChangeListener;
 import javafx.collections.ObservableList;
 import javafx.event.EventHandler;
 import javafx.geometry.VPos;
 import javafx.scene.Node;
 import javafx.scene.canvas.Canvas;
 import javafx.scene.canvas.GraphicsContext;
 import javafx.scene.effect.BlurType;
 import javafx.scene.effect.DropShadow;
 import javafx.scene.input.MouseEvent;
 import javafx.scene.layout.Pane;
 import javafx.scene.layout.Region;
 import javafx.scene.paint.Color;
 import javafx.scene.paint.CycleMethod;
 import javafx.scene.paint.LinearGradient;
 import javafx.scene.paint.Paint;
 import javafx.scene.paint.Stop;
 import javafx.scene.shape.StrokeLineCap;
 import javafx.scene.text.Font;
 import javafx.scene.text.TextAlignment;

 import java.awt.image.BufferedImage;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.Optional;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.CopyOnWriteArrayList;
 import java.util.stream.Collectors;


 /**
  * User: hansolo
  * Date: 10.12.21
  * Time: 09:10
  */
 @DefaultProperty("children")
 public class ComparisonBarChart extends Region {
     private static final double                                       PREFERRED_WIDTH  = 250;
     private static final double                                       PREFERRED_HEIGHT = 250;
     private static final double                                       MINIMUM_WIDTH    = 50;
     private static final double                                       MINIMUM_HEIGHT   = 50;
     private static final double                                       MAXIMUM_WIDTH    = 4096;
     private static final double                                       MAXIMUM_HEIGHT   = 4096;
     private              double                                       size;
     private              double                                       width;
     private              double                                       height;
     private              Canvas                                       canvas;
     private              GraphicsContext                              ctx;
     private              Pane                                         pane;
     private              ChartItemSeries<ChartItem>                   series1;
     private              ChartItemSeries<ChartItem>                   series2;
     private              Paint                                        _backgroundFill;
     private              ObjectProperty<Paint>                        backgroundFill;
     private              Paint                                        _categoryBackgroundFill;
     private              ObjectProperty<Paint>                        categoryBackgroundFill;
     private              Color                                        _barBackgroundFill;
     private              ObjectProperty<Color>                        barBackgroundFill;
     private              Color                                        _textFill;
     private              ObjectProperty<Color>                        textFill;
     private              Color                                        _categoryTextFill;
     private              ObjectProperty<Color>                        categoryTextFill;
     private              Color                                        _betterDarkerColor;
     private              ObjectProperty<Color>                        betterDarkerColor;
     private              Color                                        _betterBrighterColor;
     private              ObjectProperty<Color>                        betterBrighterColor;
     private              Color                                        _poorerDarkerColor;
     private              ObjectProperty<Color>                        poorerDarkerColor;
     private              Color                                        _poorerBrighterColor;
     private              ObjectProperty<Color>                        poorerBrighterColor;
     private              boolean                                      _barBackgroundVisible;
     private              BooleanProperty                              barBackgroundVisible;
     private              boolean                                      _shadowsVisible;
     private              BooleanProperty                              shadowsVisible;
     private              boolean                                      _categorySumVisible;
     private              BooleanProperty                              categorySumVisible;
     private              NumberFormat                                 _numberFormat;
     private              ObjectProperty<NumberFormat>                 numberFormat;
     private              boolean                                      _doCompare;
     private              BooleanProperty                              doCompare;
     private              boolean                                      _useItemTextFill;
     private              BooleanProperty                              useItemTextFill;
     private              boolean                                      _useCategoryTextFill;
     private              BooleanProperty                              useCategoryTextFill;
     private              boolean                                      _shortenNumbers;
     private              BooleanProperty                              shortenNumbers;
     private              boolean                                      _sorted;
     private              BooleanProperty                              sorted;
     private              Order                                        _order;
     private              ObjectProperty<Order>                        order;
     private              Map<Category, Double>                        categoryValueMap;
     private              Map<Rectangle, ChartItem>                    rectangleItemMap;
     private              ListChangeListener<ChartItem>                chartItemListener;
     private              EvtObserver<ChartEvt>                        observer;
     private              EventHandler<MouseEvent>                     mouseHandler;
     private              Map<EvtType, List<EvtObserver<ChartEvt>>>    observers;
     private              InfoPopup                                    popup;


     // ******************** Constructors **************************************
     public ComparisonBarChart(final ChartItemSeries series1, final ChartItemSeries series2) {
         if (null == series1 || series1.getItems().isEmpty()) { throw new IllegalArgumentException("Series 1 cannot be null or empty"); }
         if (null == series2 || series2.getItems().isEmpty()) { throw new IllegalArgumentException("Series 2 cannot be null or empty"); }
         this.series1 = series1;
         this.series2 = series2;
         if (!validate()) { throw new IllegalArgumentException("Please make sure the categories of the items in series 1 and 2 are the same and not null or empty"); }
         _backgroundFill         = Color.TRANSPARENT;
         _barBackgroundFill      = Color.rgb(230, 230, 230);
         _categoryBackgroundFill = Color.TRANSPARENT;
         _textFill               = Color.WHITE;
         _categoryTextFill       = Color.BLACK;
         _betterDarkerColor      = Color.rgb(51, 178, 75);
         _betterBrighterColor    = Color.rgb(163, 206, 53);
         _poorerDarkerColor      = Color.rgb(252, 79, 55);
         _poorerBrighterColor    = Color.rgb(252, 132, 36);
         _barBackgroundVisible   = false;
         _shadowsVisible         = false;
         _categorySumVisible     = false;
         _numberFormat           = NumberFormat.NUMBER;
         _doCompare              = false;
         _useItemTextFill        = false;
         _useCategoryTextFill    = false;
         _shortenNumbers         = false;
         _sorted                 = false;
         _order                  = Order.DESCENDING;
         observers               = new ConcurrentHashMap<>();
         popup                   = new InfoPopup();
         categoryValueMap        = new HashMap<>();
         rectangleItemMap        = new HashMap<>();
         observer                = evt -> {
            EvtType<? extends Evt> type = evt.getEvtType();
            if (type.equals(ChartEvt.ITEM_UPDATE) || type.equals(ChartEvt.FINISHED)) {
                drawChart();
            }
         };
         chartItemListener = c -> {
             while (c.next()) {
                 if (c.wasAdded()) {
                     c.getAddedSubList().forEach(addedItem -> addedItem.addChartEvtObserver(ChartEvt.ANY, observer));
                 } else if (c.wasRemoved()) {
                     c.getRemoved().forEach(removedItem -> removedItem.removeChartEvtObserver(ChartEvt.ANY, observer));
                 }
             }
             drawChart();
         };
         mouseHandler            = e -> handleMouseEvents(e);
         prepareSeries(this.series1);
         prepareSeries(this.series2);
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

         getStyleClass().add("comparison-ring-chart");

         canvas = new Canvas(size * 0.9, 0.9);
         ctx    = canvas.getGraphicsContext2D();

         pane = new Pane(canvas);

         getChildren().setAll(pane);
     }

     private void registerListeners() {
         widthProperty().addListener(o -> resize());
         heightProperty().addListener(o -> resize());

         series1.getItems().forEach(item -> item.addChartEvtObserver(ChartEvt.ANY, observer));
         series2.getItems().forEach(item -> item.addChartEvtObserver(ChartEvt.ANY, observer));

         series1.getItems().addListener(chartItemListener);
         series2.getItems().addListener(chartItemListener);

         canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
         addChartEvtObserver(SelectionEvt.ANY, e -> {
             popup.update((SelectionEvt) e);
             popup.animatedShow(getScene().getWindow());
         });
     }


     // ******************** Methods *******************************************
     @Override public void layoutChildren() {
         super.layoutChildren();
     }

     @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
     @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
     @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
     @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
     @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
     @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

     @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

     public Paint getBackgroundFill() { return null == backgroundFill ? _backgroundFill : backgroundFill.get(); }
     public void setBackgroundFill(final Paint backgroundFill) {
         if (null == this.backgroundFill) {
             _backgroundFill = backgroundFill;
             redraw();
         } else {
             this.backgroundFill.set(backgroundFill);
         }
     }
     public ObjectProperty<Paint> backgroundFillProperty() {
         if (null == backgroundFill) {
             backgroundFill = new ObjectPropertyBase<>(_backgroundFill) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "backgroundFill"; }
             };
             _backgroundFill = null;
         }
         return backgroundFill;
     }

     public Paint getCategoryBackgroundFill() { return null == categoryBackgroundFill ? _categoryBackgroundFill : categoryBackgroundFill.get(); }
     public void setCategoryBackgroundFill(final Paint categoryBackgroundFill) {
         if (null == this.categoryBackgroundFill) {
             _categoryBackgroundFill = categoryBackgroundFill;
             redraw();
         } else {
             this.categoryBackgroundFill.set(categoryBackgroundFill);
         }
     }
     public ObjectProperty<Paint> categoryBackgroundFillProperty() {
         if (null == categoryBackgroundFill) {
             categoryBackgroundFill = new ObjectPropertyBase<>(_categoryBackgroundFill) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "categoryBackgroundFill"; }
             };
             _categoryBackgroundFill = null;
         }
         return categoryBackgroundFill;
     }

     public Color getBarBackgroundFill() { return null == barBackgroundFill ? _barBackgroundFill : barBackgroundFill.get(); }
     public void setBarBackgroundFill(final Color barBackgroundFill) {
         if (null == this.barBackgroundFill) {
             _barBackgroundFill = barBackgroundFill;
             redraw();
         } else {
             this.barBackgroundFill.set(barBackgroundFill);
         }
     }
     public ObjectProperty<Color> barBackgroundFillProperty() {
         if (null == barBackgroundFill) {
             barBackgroundFill = new ObjectPropertyBase<>(_barBackgroundFill) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "barBackgroundFill"; }
             };
             _barBackgroundFill = null;
         }
         return barBackgroundFill;
     }

     public Color getTextFill() { return null == textFill ? _textFill : textFill.get(); }
     public void setTextFill(final Color textFill) {
         if (null == this.textFill) {
             _textFill = textFill;
             redraw();
         } else {
             this.textFill.set(textFill);
         }
     }
     public ObjectProperty<Color> textFillProperty() {
         if (null == textFill) {
             textFill = new ObjectPropertyBase<>(_textFill) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "textFill"; }
             };
             _textFill = null;
         }
         return textFill;
     }

     public Color getCategoryTextFill() { return null == categoryTextFill ? _categoryTextFill : categoryTextFill.get(); }
     public void setCategoryTextFill(final Color categoryTextFill) {
         if (null == this.categoryTextFill) {
             _categoryTextFill = categoryTextFill;
             redraw();
         } else {
             this.categoryTextFill.set(categoryTextFill);
         }
     }
     public ObjectProperty<Color> categoryTextFillProperty() {
         if (null == categoryTextFill) {
             categoryTextFill = new ObjectPropertyBase<>(_categoryTextFill) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "categoryTextFill"; }
             };
             _categoryTextFill = null;
         }
         return categoryTextFill;
     }

     public void setBetterColor(final Color betterColor) {
         setBetterDarkerColor(betterColor.darker().darker());
         setBetterBrighterColor(betterColor.brighter().brighter());
     }
     public void setPoorerColor(final Color poorerColor) {
         setPoorerDarkerColor(poorerColor.darker().darker());
         setPoorerBrighterColor(poorerColor.brighter().brighter());
     }

     public Color getBetterDarkerColor() { return null == betterDarkerColor ? _betterDarkerColor : betterDarkerColor.get(); }
     public void setBetterDarkerColor(final Color betterDarkerColor) {
         if (null == this.betterDarkerColor) {
             _betterDarkerColor = betterDarkerColor;
             redraw();
         } else {
             this.betterDarkerColor.set(betterDarkerColor);
         }
     }
     public ObjectProperty<Color> betterDarkerColorProperty() {
         if (null == betterDarkerColor) {
             betterDarkerColor = new ObjectPropertyBase<>(_betterDarkerColor) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "betterDarkerColor"; }
             };
             _betterDarkerColor = null;
         }
         return betterDarkerColor;
     }

     public Color getBetterBrighterColor() { return null == betterBrighterColor ? _betterBrighterColor : betterBrighterColor.get(); }
     public void setBetterBrighterColor(final Color betterBrighterColor) {
         if (null == this.betterBrighterColor) {
             _betterBrighterColor = betterBrighterColor;
             redraw();
         } else {
             this.betterBrighterColor.set(betterBrighterColor);
         }
     }
     public ObjectProperty<Color> betterBrighterColorProperty() {
         if (null == betterBrighterColor) {
             betterBrighterColor = new ObjectPropertyBase<>(_betterBrighterColor) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "betterBrighterColor"; }
             };
             _betterBrighterColor = null;
         }
         return betterBrighterColor;
     }

     public Color getPoorerDarkerColor() { return null == poorerDarkerColor ? _poorerDarkerColor : poorerDarkerColor.get(); }
     public void setPoorerDarkerColor(final Color poorerDarkerColor) {
         if (null == this.poorerDarkerColor) {
             _poorerDarkerColor = poorerDarkerColor;
             redraw();
         } else {
             this.poorerDarkerColor.set(poorerDarkerColor);
         }
     }
     public ObjectProperty<Color> poorerDarkerColorProperty() {
         if (null == poorerDarkerColor) {
             poorerDarkerColor = new ObjectPropertyBase<>(_poorerDarkerColor) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "poorerDarkerColor"; }
             };
             _poorerDarkerColor = null;
         }
         return poorerDarkerColor;
     }

     public Color getPoorerBrighterColor() { return null == poorerBrighterColor ? _poorerBrighterColor : poorerBrighterColor.get(); }
     public void setPoorerBrighterColor(final Color poorerBrighterColor) {
         if (null == this.poorerBrighterColor) {
             _poorerBrighterColor = poorerBrighterColor;
             redraw();
         } else {
             this.poorerBrighterColor.set(poorerBrighterColor);
         }
     }
     public ObjectProperty<Color> poorerBrighterColorProperty() {
         if (null == poorerBrighterColor) {
             poorerBrighterColor = new ObjectPropertyBase<>(_poorerBrighterColor) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "poorerBrighterColor"; }
             };
             _poorerBrighterColor = null;
         }
         return poorerBrighterColor;
     }

     public boolean getBarBackgroundVisible() { return null == barBackgroundVisible ? _barBackgroundVisible : barBackgroundVisible.get(); }
     public void setBarBackgroundVisible(final boolean barBackgroundVisible) {
         if (null == this.barBackgroundVisible) {
             _barBackgroundVisible = barBackgroundVisible;
             redraw();
         } else {
             this.barBackgroundVisible.set(barBackgroundVisible);
         }
     }
     public BooleanProperty barBackgroundVisibleProperty() {
         if (null == barBackgroundVisible) {
             barBackgroundVisible = new BooleanPropertyBase(_barBackgroundVisible) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "barBackgroundVisible"; }
             };
         }
         return barBackgroundVisible;
     }

     public boolean getShadowsVisible() { return null == shadowsVisible ? _shadowsVisible : shadowsVisible.get(); }
     public void setShadowsVisible(final boolean shadowsVisible) {
         if (null == this.shadowsVisible) {
             _shadowsVisible = shadowsVisible;
             redraw();
         } else {
             this.shadowsVisible.set(shadowsVisible);
         }
     }
     public BooleanProperty shadowsVisibleProperty() {
         if (null == shadowsVisible) {
             shadowsVisible = new BooleanPropertyBase(_shadowsVisible) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "shadowsVisible"; }
             };
         }
         return shadowsVisible;
     }

     public boolean getCategorySumVisible() { return null == categorySumVisible ? _categorySumVisible : categorySumVisible.get(); }
     public void setCategorySumVisible(final boolean categorySumVisible) {
         if (null == this.categorySumVisible) {
             _categorySumVisible = categorySumVisible;
             redraw();
         } else {
             this.categorySumVisible.set(categorySumVisible);
         }
     }
     public BooleanProperty categorySumVisibleProperty() {
         if (null == categorySumVisible) {
             categorySumVisible = new BooleanPropertyBase(_categorySumVisible) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "categorySumVisible"; }
             };
         }
         return categorySumVisible;
     }

     public NumberFormat getNumberFormat() { return null == numberFormat ? _numberFormat : numberFormat.get(); }
     public void setNumberFormat(final NumberFormat FORMAT) {
         if (null == numberFormat) {
             _numberFormat = FORMAT;
             updatePopup();
             redraw();
         } else {
             numberFormat.set(FORMAT);
         }
     }
     public ObjectProperty<NumberFormat> numberFormatProperty() {
         if (null == numberFormat) {
             numberFormat = new ObjectPropertyBase<NumberFormat>(_numberFormat) {
                 @Override protected void invalidated() {
                     updatePopup();
                     redraw();
                 }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "numberFormat"; }
             };
             _numberFormat = null;
         }
         return numberFormat;
     }

     public boolean getDoCompare() { return null == doCompare ? _doCompare : doCompare.get(); }
     public void setDoCompare(final boolean doCompare) {
         if (null == this.doCompare) {
             _doCompare = doCompare;
             redraw();
         } else {
             this.doCompare.set(doCompare);
         }
     }
     public BooleanProperty doCompareProperty() {
         if (null == doCompare) {
             doCompare = new BooleanPropertyBase(_doCompare) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "doCompare"; }
             };
         }
         return doCompare;
     }

     public boolean getUseItemTextFill() { return null == useItemTextFill ? _useItemTextFill : useItemTextFill.get(); }
     public void setUseItemTextFill(final boolean useItemTextFill) {
         if (null == this.useItemTextFill) {
             _useItemTextFill = useItemTextFill;
             redraw();
         } else {
             this.useItemTextFill.set(useItemTextFill);
         }
     }
     public BooleanProperty useItemTextFillProperty() {
         if (null == useItemTextFill) {
             useItemTextFill = new BooleanPropertyBase(_useItemTextFill) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "useItemTextFill"; }
             };
         }
         return useItemTextFill;
     }

     public boolean getUseCategoryTextFill() { return null == useCategoryTextFill ? _useCategoryTextFill : useCategoryTextFill.get(); }
     public void setUseCategoryTextFill(final boolean useCategoryTextFill) {
         if (null == this.useCategoryTextFill) {
             _useCategoryTextFill = useCategoryTextFill;
             redraw();
         } else {
             this.useCategoryTextFill.set(useCategoryTextFill);
         }
     }
     public BooleanProperty useCategoryTextFillProperty() {
         if (null == useCategoryTextFill) {
             useCategoryTextFill = new BooleanPropertyBase(_useCategoryTextFill) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "useCategoryTextFill"; }
             };
         }
         return useCategoryTextFill;
     }

     public boolean getShortenNumbers() { return null == shortenNumbers ? _shortenNumbers : shortenNumbers.get(); }
     public void setShortenNumbers(final boolean SHORTEN) {
         if (null == shortenNumbers) {
             _shortenNumbers = SHORTEN;
             redraw();
         } else {
             shortenNumbers.set(SHORTEN);
         }
     }
     public BooleanProperty shortenNumbersProperty() {
         if (null == shortenNumbers) {
             shortenNumbers = new BooleanPropertyBase(_shortenNumbers) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "shortenNumbers"; }
             };
         }
         return shortenNumbers;
     }

     public boolean getSorted() { return null == sorted ? _sorted : sorted.get(); }
     public void setSorted(final boolean sorted) {
         if (null == this.sorted) {
             _sorted = sorted;
             redraw();
         } else {
             this.sorted.set(sorted);
         }
     }
     public BooleanProperty sortedProperty() {
         if (null == sorted) {
             sorted = new BooleanPropertyBase() {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "sorted"; }
             };
         }
         return sorted;
     }

     public Order getOrder() { return null == order ? _order : order.get(); }
     public void setOrder(final Order order) {
         if (null == this.order) {
             _order = order;
             redraw();
         } else {
             this.order.set(order);
         }
     }
     public ObjectProperty<Order> orderProperty() {
         if (null == order) {
             order = new ObjectPropertyBase<>(_order) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return ComparisonBarChart.this; }
                 @Override public String getName() { return "order"; }
             };
             _order = null;
         }
         return order;
     }

     private boolean validate() {
         if (series1.getItems().size() != series2.getItems().size()) { return false; }
         if (series1.getItems().stream().filter(item -> item.getCategory() == null).count() > 0) { return false; }
         if (series2.getItems().stream().filter(item -> item.getCategory() == null).count() > 0) { return false; }
         if (series1.getItems().stream().filter(item -> item.getCategory().getName() == null).count() > 0) { return false; }
         if (series2.getItems().stream().filter(item -> item.getCategory().getName() == null).count() > 0) { return false; }
         if (series1.getItems().stream().filter(item -> item.getCategory().getName().isEmpty()).count() > 0) { return false; }
         if (series2.getItems().stream().filter(item -> item.getCategory().getName().isEmpty()).count() > 0) { return false; }
         List<Category> categories1 = series1.getItems().stream().map(item -> item.getCategory()).collect(Collectors.toList());
         List<Category> categories2 = series2.getItems().stream().map(item -> item.getCategory()).collect(Collectors.toList());
         if (categories1.isEmpty()) { return false; }
         if (categories2.isEmpty()) { return false; }
         if (categories1.size() != categories2.size()) { return false; }
         if (!categories1.stream().map(category -> category.getName()).anyMatch(categories2.stream().map(category -> category.getName()).collect(Collectors.toSet())::contains)) { return false; }

         return true;
     }

     private void handleMouseEvents(final MouseEvent evt) {
         final double x = evt.getX();
         final double y = evt.getY();

         Optional<Entry<Rectangle, ChartItem>> opt = rectangleItemMap.entrySet().stream().filter(entry -> entry.getKey().contains(x, y)).findFirst();
         if (opt.isPresent()) {
             popup.setX(evt.getScreenX());
             popup.setY(evt.getScreenY() - popup.getHeight());
             ChartItem selectedItem = opt.get().getValue();
             if (series1.getItems().contains(selectedItem)) {
                 fireChartEvt(new SelectionEvt(series1, opt.get().getValue()));
             } else {
                 fireChartEvt(new SelectionEvt(series2, opt.get().getValue()));
             }
         }
     }

     private void updatePopup() {
         switch(getNumberFormat()) {
             case NUMBER:
                 popup.setDecimals(0);
                 break;
             case FLOAT_1_DECIMAL:
                 popup.setDecimals(1);
                 break;
             case FLOAT_2_DECIMALS:
                 popup.setDecimals(2);
                 break;
             case FLOAT:
                 popup.setDecimals(8);
                 break;
             case PERCENTAGE          :
                 popup.setDecimals(0);
                 break;
             case PERCENTAGE_1_DECIMAL:
                 popup.setDecimals(1);
                 break;
         }
     }

     /**
      * Calling this method will render this chart/plot to a png given of the given width and height
      * @param filename The path and name of the file  /Users/hansolo/Desktop/plot.png
      * @param width The width of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
      * @param height The height of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
      * @return True if the procedure was successful, otherwise false
      */
     public boolean renderToImage(final String filename, final int width, final int height) {
         return Helper.renderToImage(ComparisonBarChart.this, width, height, filename);
     }

     /**
      * Calling this method will render this chart/plot to a png given of the given width and height
      * @param width The width of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
      * @param height The height of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
      * @return A BufferedImage of this chart in the given dimension
      */
     public BufferedImage renderToImage(final int width, final int height) {
         return Helper.renderToImage(ComparisonBarChart.this, width, height);
     }


     // ******************** Event Handling ************************************
     public void addChartEvtObserver(final EvtType type, final EvtObserver<ChartEvt> observer) {
         if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
         if (observers.get(type).contains(observer)) { return; }
         observers.get(type).add(observer);
     }
     public void removeChartEvtObserver(final EvtType type, final EvtObserver<ChartEvt> observer) {
         if (observers.containsKey(type)) {
             if (observers.get(type).contains(observer)) {
                 observers.get(type).remove(observer);
             }
         }
     }
     public void removeAllChartEvtObservers() { observers.clear(); }

     public void fireChartEvt(final ChartEvt evt) {
         final EvtType type = evt.getEvtType();
         observers.entrySet().stream().filter(entry -> entry.getKey().equals(ChartEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
         if (observers.containsKey(type) && !type.equals(ChartEvt.ANY)) {
             observers.get(type).forEach(observer -> observer.handle(evt));
         }
     }


     // ******************** Drawing *******************************************
     private void prepareSeries(final Series<ChartItem> SERIES) {
         boolean animated          = SERIES.isAnimated();
         long    animationDuration = SERIES.getAnimationDuration();
         SERIES.getItems().forEach(item -> {
             if (animated) { item.setAnimated(animated); }
             item.setAnimationDuration(animationDuration);
         });
     }

     private void drawChart() {
         categoryValueMap.clear();
         rectangleItemMap.clear();
         double          inset                = 5;
         double          chartWidth           = this.width - 2 * inset;
         double          chartHeight          = this.height - 2 * inset;
         List<Category>  categories           = series1.getItems().stream().map(item -> item.getCategory()).sorted().collect(Collectors.toList());
         List<ChartItem> items1               = series1.getItems();
         List<ChartItem> items2               = series2.getItems();
         double          noOfCategories       = categories.size();
         double          maxBarWidth          = chartWidth * 0.4;
         double          categoryWidth        = chartWidth * 0.2;
         double          barHeight            = chartHeight / (noOfCategories + (noOfCategories * 0.4));
         double          cornerRadius         = barHeight * 0.75;
         double          barSpacer            = (chartHeight - (noOfCategories * barHeight)) / (noOfCategories - 1);
         double          maxValue             = Math.max(series1.getMaxValue(), series2.getMaxValue());
         NumberFormat    numberFormat         = getNumberFormat();
         Color           valueTextFill        = getTextFill();
         Color           categoryTextFill     = getCategoryTextFill();
         boolean         useItemTextFill      = getUseItemTextFill();
         boolean         useCategoryTextFill  = getUseCategoryTextFill();
         String          formatString         = numberFormat.formatString();
         Paint           leftFill             = series1.getFill();
         Paint           rightFill            = series2.getFill();
         boolean         shortenNumbers       = getShortenNumbers();
         boolean         barBackgroundVisible = getBarBackgroundVisible();
         Color           barBackgroundFill    = getBarBackgroundFill();
         Paint           categoryBackgroundFill = getCategoryBackgroundFill().equals(Color.TRANSPARENT) ? getBackgroundFill() : getCategoryBackgroundFill();
         boolean         shadowsVisible       = getShadowsVisible();
         boolean         categorySumVisible   = getCategorySumVisible();
         Font            valueFont            = Fonts.latoRegular(barHeight * 0.5);
         Font            categoryFont         = categorySumVisible ? Fonts.latoRegular(barHeight * 0.4) : Fonts.latoRegular(barHeight * 0.5);
         Font            categorySumFont      = Fonts.latoRegular(barHeight * 0.3);
         DropShadow      leftShadow           = new DropShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.15), barHeight * 0.1, 0.0, -1, barHeight * 0.1);
         DropShadow      rightShadow          = new DropShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.15), barHeight * 0.1, 0.0, 1, barHeight * 0.1);

         if (getSorted()) {
             categories.forEach(category -> {
                 double value1 = series1.getItems().stream().filter(item -> item.getCategory().getName().equals(category.getName())).findFirst().get().getValue();
                 double value2 = series2.getItems().stream().filter(item -> item.getCategory().getName().equals(category.getName())).findFirst().get().getValue();
                 categoryValueMap.put(category, value1 + value2);
             });
             categories = categoryValueMap.entrySet().stream().sorted(Comparator.comparing(Entry::getValue)).map(entry -> entry.getKey()).collect(Collectors.toList());
             if (Order.DESCENDING == getOrder()) { Collections.reverse(categories); }
         }

         LinearGradient  leftBetterFill  = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, getBetterBrighterColor()), new Stop(1, getBetterDarkerColor()));
         LinearGradient  rightBetterFill = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, getBetterDarkerColor()), new Stop(1, getBetterBrighterColor()));
         LinearGradient  leftPoorerFill  = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, getPoorerBrighterColor()), new Stop(1, getPoorerDarkerColor()));
         LinearGradient  rightPoorerFill = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, getPoorerDarkerColor()), new Stop(1, getPoorerBrighterColor()));

         ctx.clearRect(0, 0, width, height);
         ctx.setFill(getBackgroundFill());
         ctx.fillRect(0, 0, width, height);
         ctx.setLineCap(StrokeLineCap.BUTT);
         ctx.setTextAlign(TextAlignment.RIGHT);
         ctx.setTextBaseline(VPos.CENTER);
         ctx.setFont(valueFont);

         // Draw bars
         for (int i = 0 ; i < noOfCategories ; i++) {
             Category  category      = categories.get(i);

             ChartItem leftItem      = items1.stream().filter(it -> it.getCategory().getName().equals(category.getName())).findFirst().get();
             double    leftValue     = Helper.clamp(0, Double.MAX_VALUE, leftItem.getValue());
             double    leftBarWidth  = leftValue / maxValue * maxBarWidth;
             double    leftBarX      = inset + maxBarWidth - leftBarWidth;
             double    leftBarY      = inset + (i * barHeight) + (i * barSpacer);

             ChartItem rightItem     = items2.stream().filter(it -> it.getCategory().getName().equals(category.getName())).findFirst().get();
             double    rightValue    = Helper.clamp(0, Double.MAX_VALUE, rightItem.getValue());
             double    rightBarWidth = rightValue / maxValue * maxBarWidth;
             double    rightBarX     = inset + maxBarWidth + categoryWidth;
             double    rightBarY     = inset + (i * barHeight) + (i * barSpacer);

             double    categoryX     = inset + maxBarWidth + (categoryWidth * 0.5);
             double    categoryY     = inset + (i * barHeight) + (i * barSpacer);

             category.setValue(leftItem.getValue() + rightItem.getValue());

             // Left Bar
             if (barBackgroundVisible) {
                 ctx.setFill(barBackgroundFill);
                 ctx.beginPath();
                 ctx.moveTo(inset + maxBarWidth, leftBarY);
                 ctx.lineTo(inset + maxBarWidth, leftBarY + barHeight);
                 ctx.lineTo(inset + cornerRadius, leftBarY + barHeight);
                 ctx.bezierCurveTo(inset, leftBarY + barHeight, inset, leftBarY, inset + cornerRadius, leftBarY);
                 ctx.lineTo(inset + maxBarWidth, leftBarY);
                 ctx.closePath();
                 ctx.fill();
             }
             if (getDoCompare()) {
                 leftFill = leftValue > rightValue ? leftBetterFill : leftValue < rightValue ? leftPoorerFill : series1.getFill();
             }
             ctx.save();
             if (shadowsVisible) { ctx.setEffect(leftShadow); }
             ctx.setFill(leftFill);
             ctx.beginPath();
             ctx.moveTo(leftBarX + leftBarWidth, leftBarY);
             ctx.lineTo(leftBarX + leftBarWidth, leftBarY + barHeight);
             if (leftBarWidth < cornerRadius) {
                 ctx.lineTo(leftBarX + leftBarWidth, leftBarY + barHeight);
                 ctx.bezierCurveTo(leftBarX, leftBarY + barHeight, leftBarX, leftBarY, leftBarX + leftBarWidth, leftBarY);
             } else {
                 ctx.lineTo(leftBarX + cornerRadius, leftBarY + barHeight);
                 ctx.bezierCurveTo(leftBarX, leftBarY + barHeight, leftBarX, leftBarY, leftBarX + cornerRadius, leftBarY);
             }
             ctx.lineTo(leftBarX + leftBarWidth, leftBarY);
             ctx.closePath();
             ctx.fill();
             ctx.restore();
             rectangleItemMap.put(new Rectangle(leftBarX, leftBarY, leftBarWidth, barHeight), leftItem);

             // Left Value
             ctx.setTextAlign(TextAlignment.RIGHT);
             ctx.setFill(useItemTextFill ? leftItem.getTextFill() : valueTextFill);
             if (shortenNumbers) {
                 ctx.fillText(Helper.shortenNumber((long) leftValue), inset + maxBarWidth - 5, leftBarY + barHeight * 0.5);
             } else {
                 if (NumberFormat.PERCENTAGE == numberFormat || NumberFormat.PERCENTAGE_1_DECIMAL == numberFormat) {
                     ctx.fillText(String.format(Locale.US, numberFormat.formatString(), leftValue / maxValue * 100), inset + maxBarWidth - 5, leftBarY + barHeight * 0.5);
                 } else {
                     ctx.fillText(String.format(Locale.US, numberFormat.formatString(), leftValue), inset + maxBarWidth - 5, leftBarY + barHeight * 0.5);
                 }
             }

             // Right Bar
             if (barBackgroundVisible) {
                 ctx.setFill(barBackgroundFill);
                 ctx.beginPath();
                 ctx.moveTo(rightBarX, rightBarY);
                 ctx.lineTo(rightBarX + maxBarWidth - cornerRadius, rightBarY);
                 ctx.bezierCurveTo(rightBarX + maxBarWidth, rightBarY, rightBarX + maxBarWidth, rightBarY + barHeight, rightBarX + maxBarWidth - cornerRadius, rightBarY + barHeight);
                 ctx.lineTo(rightBarX, rightBarY + barHeight);
                 ctx.lineTo(rightBarX, rightBarY);
                 ctx.closePath();
                 ctx.fill();
             }
             if (getDoCompare()) {
                 rightFill = rightValue > leftValue ? rightBetterFill : rightValue < leftValue ? rightPoorerFill : series2.getFill();
             }
             ctx.save();
             if (shadowsVisible) { ctx.setEffect(rightShadow); }
             ctx.setFill(rightFill);
             ctx.beginPath();
             ctx.moveTo(rightBarX, rightBarY);
             if (rightBarWidth < cornerRadius) {
                 ctx.bezierCurveTo(rightBarX + rightBarWidth, rightBarY, rightBarX + rightBarWidth, rightBarY + barHeight, rightBarX, rightBarY + barHeight);
             } else {
                 ctx.lineTo(rightBarX + rightBarWidth - cornerRadius, rightBarY);
                 ctx.bezierCurveTo(rightBarX + rightBarWidth, rightBarY, rightBarX + rightBarWidth, rightBarY + barHeight, rightBarX + rightBarWidth - cornerRadius, rightBarY + barHeight);
             }
             ctx.lineTo(rightBarX, rightBarY + barHeight);
             ctx.lineTo(rightBarX, rightBarY);
             ctx.closePath();
             ctx.fill();
             ctx.restore();
             rectangleItemMap.put(new Rectangle(rightBarX, rightBarY, rightBarWidth, barHeight), rightItem);

             // Right Value
             ctx.setTextAlign(TextAlignment.LEFT);
             ctx.setFill(useItemTextFill ? rightItem.getTextFill() : valueTextFill);
             if (shortenNumbers) {
                 ctx.fillText(Helper.shortenNumber((long) rightValue), rightBarX + 5, rightBarY + barHeight * 0.5);
             } else {
                 if (NumberFormat.PERCENTAGE == numberFormat || NumberFormat.PERCENTAGE_1_DECIMAL == numberFormat) {
                     ctx.fillText(String.format(Locale.US, formatString, rightValue / maxValue * 100), rightBarX + 5, rightBarY + barHeight * 0.5);
                 } else {
                     ctx.fillText(String.format(Locale.US, formatString, rightValue), rightBarX + 5, rightBarY + barHeight * 0.5);
                 }
             }
         }

         // Draw categories
         ctx.setFill(categoryBackgroundFill);
         ctx.fillRect(inset + maxBarWidth, inset, categoryWidth, chartHeight);
         for (int i = 0 ; i < noOfCategories ; i++) {
             Category category  = categories.get(i);
             double   categoryX = inset + maxBarWidth + (categoryWidth * 0.5);
             double   categoryY = inset + (i * barHeight) + (i * barSpacer);
             // Draw categories
             ctx.setTextAlign(TextAlignment.CENTER);
             ctx.setFill(useCategoryTextFill ? category.getTextFill() : categoryTextFill);
             if (categorySumVisible) {
                 ctx.setFont(categoryFont);
                 ctx.fillText(category.getName(), categoryX, categoryY + barHeight * 0.3, categoryWidth);
                 ctx.setFont(categorySumFont);
                 if (shortenNumbers) {
                     ctx.fillText(Helper.shortenNumber((long) category.getValue()), categoryX, categoryY + barHeight * 0.7, categoryWidth);
                 } else {
                     ctx.fillText(String.format(Locale.US, "%.0f", category.getValue()), categoryX, categoryY + barHeight * 0.7, categoryWidth);
                 }
             } else {
                 ctx.setFont(categoryFont);
                 ctx.fillText(category.getName(), categoryX, categoryY + barHeight * 0.5, categoryWidth);
             }
         }

         if (shadowsVisible) {
             ctx.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.TRANSPARENT), new Stop(1.0, Color.rgb(0, 0, 0, 0.25))));
             ctx.fillRect(inset + maxBarWidth - 6, inset, 6, chartHeight);
             ctx.setFill(new LinearGradient(1, 0, 0, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.TRANSPARENT), new Stop(1.0, Color.rgb(0, 0, 0, 0.25))));
             ctx.fillRect(inset + maxBarWidth + categoryWidth, inset, 6, chartHeight);
         }
     }


     // ******************** Resizing ******************************************
     private void resize() {
         width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
         height = getHeight() - getInsets().getTop() - getInsets().getBottom();
         size   = width < height ? width : height;

         if (width > 0 && height > 0) {
             pane.setMaxSize(width, height);
             pane.setPrefSize(width, height);
             pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

             canvas.setWidth(width);
             canvas.setHeight(height);

             redraw();
         }
     }

     private void redraw() {
         drawChart();
     }
 }