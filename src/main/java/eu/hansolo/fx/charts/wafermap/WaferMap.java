 /*
  * SPDX-License-Identifier: Apache-2.0
  *
  * Copyright 2016-2023 Gerrit Grunwald.
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

 package eu.hansolo.fx.charts.wafermap;

 import eu.hansolo.fx.charts.SankeyPlot;
 import eu.hansolo.fx.charts.XYChart;
 import eu.hansolo.fx.charts.tools.Helper;
 import eu.hansolo.fx.geometry.Rectangle;
 import eu.hansolo.fx.heatmap.ColorMapping;
 import eu.hansolo.fx.heatmap.HeatMap;
 import eu.hansolo.fx.heatmap.HeatMapBuilder;
 import eu.hansolo.fx.heatmap.OpacityDistribution;
 import eu.hansolo.toolboxfx.geom.Point;
 import javafx.beans.DefaultProperty;
 import javafx.beans.binding.Bindings;
 import javafx.beans.binding.BooleanBinding;
 import javafx.beans.property.BooleanProperty;
 import javafx.beans.property.BooleanPropertyBase;
 import javafx.beans.property.ObjectProperty;
 import javafx.beans.property.ObjectPropertyBase;
 import javafx.beans.property.ReadOnlyObjectProperty;
 import javafx.beans.property.SimpleObjectProperty;
 import javafx.collections.FXCollections;
 import javafx.collections.MapChangeListener;
 import javafx.collections.ObservableList;
 import javafx.collections.ObservableMap;
 import javafx.event.Event;
 import javafx.event.EventHandler;
 import javafx.event.EventType;
 import javafx.geometry.VPos;
 import javafx.scene.Node;
 import javafx.scene.canvas.Canvas;
 import javafx.scene.canvas.GraphicsContext;
 import javafx.scene.input.MouseEvent;
 import javafx.scene.layout.Region;
 import javafx.scene.layout.StackPane;
 import javafx.scene.paint.Color;
 import javafx.scene.text.Font;
 import javafx.scene.text.TextAlignment;

 import java.awt.image.BufferedImage;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Optional;


 @DefaultProperty("children")
 public class WaferMap extends Region {
     private static final double                              PREFERRED_WIDTH  = 500;
     private static final double                              PREFERRED_HEIGHT = 500;
     private static final double                              MINIMUM_WIDTH    = 50;
     private static final double                              MINIMUM_HEIGHT   = 50;
     private static final double                              MAXIMUM_WIDTH    = 2048;
     private static final double                              MAXIMUM_HEIGHT   = 2048;
     private              double                              size;
     private              double                              width;
     private              double                              height;
     private              BooleanBinding                      showing;
     private              Canvas                              canvas;
     private              GraphicsContext                     ctx;
     private              HeatMap                             heatmap;
     private              StackPane                           pane;
     private              KLA                                 kla;
     private              double                              factor;
     private              double                              defectSize;
     private              double                              halfDefectSize;
     private              double                              centerX;
     private              double                              centerY;
     private              double                              diameter;
     private              double                              notchSize;
     private              double                              minDieSize;
     private              double                              fontSize;
     private              Color                               _waferFill;
     private              ObjectProperty<Color>               waferFill;
     private              Color                               _waferStroke;
     private              ObjectProperty<Color>               waferStroke;
     private              Color                               _notchFill;
     private              ObjectProperty<Color>               notchFill;
     private              Color                               _defectFill;
     private              ObjectProperty<Color>               defectFill;
     private              Color                               _defectStroke;
     private              ObjectProperty<Color>               defectStroke;
     private              Color                               _dieTextFill;
     private              ObjectProperty<Color>               dieTextFill;
     private              Color                               _selectionColor;
     private              ObjectProperty<Color>               selectionColor;
     private              List<Color>                         defectDensityColors;
     private              boolean                             _dieTextVisible;
     private              BooleanProperty                     dieTextVisible;
     private              boolean                             _densityColorsVisible;
     private              BooleanProperty                     densityColorsVisible;
     private              boolean                             _legendVisible;
     private              BooleanProperty                     legendVisible;
     private              boolean                             _defectsVisible;
     private              BooleanProperty                     defectsVisible;
     private              boolean                             _heatmapVisible;
     private              BooleanProperty                     heatmapVisible;
     private              ObservableMap<Integer, ClassConfig> classConfigMap;
     private              ObjectProperty<Die>                 selectedDie;
     private              Map<String, Rectangle>              dieMap;
     private              EventHandler<MouseEvent>            mouseHandler;


     // ******************** Constructors **************************************
     public WaferMap() {
         this("");
     }
     public WaferMap(final String filename) {
         if (null != filename && !filename.isEmpty()) {
             Optional<KLA> klaOpt = KLAParser.INSTANCE.parse(filename);
             this.kla = klaOpt.isPresent() ? klaOpt.get() : new KLA();
         } else {
             this.kla = new KLA();
         }
         this.kla.createDieMap();

         this._waferFill            = Constants.DEFAULT_WAFER_FILL;
         this._waferStroke          = Constants.DEFAULT_WAFER_STROKE;
         this._notchFill            = Constants.DEFAULT_NOTCH_FILL;
         this._defectFill           = Constants.DEFAULT_DEFECT_FILL;
         this._defectStroke         = Constants.DEFAULT_DEFECT_STROKE;
         this._dieTextFill          = Constants.DEFAULT_DIE_LABEL_FILL;
         this._selectionColor       = Constants.DEFAULT_SELECTION_COLOR;
         this.defectDensityColors   = Constants.DEFAULT_DEFECT_DENSITY_COLORS;
         this._dieTextVisible       = false;
         this._densityColorsVisible = false;
         this._legendVisible        = false;
         this._defectsVisible       = true;
         this._heatmapVisible       = false;
         this.classConfigMap        = FXCollections.observableHashMap();
         this.selectedDie           = new SimpleObjectProperty<>(null);
         this.dieMap                = new HashMap<>();
         this.mouseHandler          = e -> {
             EventType<? extends Event> type = e.getEventType();
             if (MouseEvent.MOUSE_PRESSED.equals(type)) {
                 mousePressed(e);
             }
         };

         initGraphics();
         registerListeners();
         initBindings();
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
         ctx    = canvas.getGraphicsContext2D();

         heatmap  = HeatMapBuilder.create()
                                  .prefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT)
                                  .colorMapping(ColorMapping.BLUE_CYAN_GREEN_YELLOW_RED)
                                  .spotRadius(10)
                                  .fadeColors(true)
                                  .opacityDistribution(OpacityDistribution.LINEAR)
                                  .heatMapOpacity(0.75)
                                  .build();
         heatmap.setVisible(false);
         heatmap.setManaged(false);
         heatmap.setMouseTransparent(true);

         pane = new StackPane(canvas, heatmap);

         getChildren().setAll(pane);
     }

     private void registerListeners() {
         widthProperty().addListener(o -> resize());
         heightProperty().addListener(o -> resize());
         canvas.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);

         classConfigMap.addListener((MapChangeListener<Integer, ClassConfig>) change -> redraw());
     }

     private void initBindings() {
         showing = Bindings.selectBoolean(sceneProperty(), "window", "showing");
         showing.addListener((o, ov, nv) -> {
             if (nv) {
                 // Do something once the scene was rendered
             }
         });
     }


     // ******************** Methods *******************************************
     @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
     @Override protected double computeMinHeight(final double width)  { return MINIMUM_HEIGHT; }
     @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
     @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
     @Override protected double computeMaxWidth(final double height)  { return MAXIMUM_WIDTH; }
     @Override protected double computeMaxHeight(final double width)  { return MAXIMUM_HEIGHT; }

     @Override public ObservableList<Node> getChildren()              { return super.getChildren(); }

     public void setKla(final KLA kla) {
         this.heatmap.clearHeatMap();
         this.kla = kla;
         this.kla.createDieMap();
         resize();
         redraw();
     }

     public Color getWaferFill() { return null == waferFill ? _waferFill : waferFill.get(); }
     public void setWaferFill(final Color waferFill) {
         if (null == this.waferFill) {
             _waferFill = waferFill;
             redraw();
         } else {
             this.waferFill.set(waferFill);
         }
     }
     public ObjectProperty<Color> waferFillProperty() {
         if (null == waferFill) {
             waferFill  = new ObjectPropertyBase<>(_waferFill) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return WaferMap.this; }
                 @Override public String getName() { return "waferdsFill"; }
             };
             _waferFill = null;
         }
         return waferFill;
     }

     public Color getWaferStroke() { return null == waferStroke ? _waferStroke : waferStroke.get(); }
     public void setWaferStroke(final Color waferStroke) {
         if (null == this.waferStroke) {
             _waferStroke = waferStroke;
             redraw();
         } else {
             this.waferStroke.set(waferStroke);
         }
     }
     public ObjectProperty<Color> waferStrokeProperty() {
         if (null == waferStroke) {
             waferStroke  = new ObjectPropertyBase<>(_waferStroke) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return WaferMap.this; }
                 @Override public String getName() { return "waferStroke"; }
             };
             _waferStroke = null;
         }
         return waferStroke;
     }

     public Color getNotchFill() { return null == notchFill ? _notchFill : notchFill.get(); }
     public void setNotchFill(final Color notchFill) {
         if (null == this.notchFill) {
             _notchFill = notchFill;
             redraw();
         } else {
             this.notchFill.set(notchFill);
         }
     }
     public ObjectProperty<Color> notchFillProperty() {
         if (null == notchFill) {
             notchFill = new ObjectPropertyBase<>(_notchFill) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return WaferMap.this; }
                 @Override public String getName() { return "notchFill"; }
             };
             _notchFill = null;
         }
         return notchFill;
     }

     public Color getDefectFill() { return null == defectFill ? _defectFill : defectFill.get(); }
     public void setDefectFill(final Color defectFill) {
         if (null == this.defectFill) {
             _defectFill = defectFill;
             redraw();
         } else {
             this.defectFill.set(defectFill);
         }
     }
     public ObjectProperty<Color> defectFillProperty() {
         if (null == defectFill) {
             defectFill = new ObjectPropertyBase<>(_defectFill) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return WaferMap.this; }
                 @Override public String getName() { return "defectFill"; }
             };
             _defectFill = null;
         }
         return defectFill;
     }

     public Color getDefectStroke() { return null == defectStroke ? _defectStroke : defectStroke.get(); }
     public void setDefectStroke(final Color defectStroke) {
         if (null == this.defectStroke) {
             _defectStroke = defectStroke;
             redraw();
         } else {
             this.defectStroke.set(defectStroke);
         }
     }
     public ObjectProperty<Color> defectStrokeProperty() {
         if (null == defectStroke) {
             defectStroke = new ObjectPropertyBase<>(_defectStroke) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return WaferMap.this; }
                 @Override public String getName() { return "defectStroke"; }
             };
             _defectStroke = null;
         }
         return defectStroke;
     }

     public Color getDieTextFill() { return null == dieTextFill ? _dieTextFill : dieTextFill.get(); }
     public void setDieTextFill(final Color dieTextFill) {
         if (null == this.dieTextFill) {
             _dieTextFill = dieTextFill;
             redraw();
         } else {
             this.dieTextFill.set(dieTextFill);
         }
     }
     public ObjectProperty<Color> dieTextFillProperty() {
         if (null == dieTextFill) {
             dieTextFill  = new ObjectPropertyBase<>(_dieTextFill) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return WaferMap.this; }
                 @Override public String getName() { return "dieLabelFill"; }
             };
             _dieTextFill = null;
         }
         return dieTextFill;
     }

     public Color getSelectionColor() { return null == selectionColor ? _selectionColor : selectionColor.get(); }
     public void setSelectionColor(final Color selectionColor) {
         if (null == this.selectionColor) {
             _selectionColor = selectionColor;
             redraw();
         } else {
             this.selectionColor.set(selectionColor);
         }
     }
     public ObjectProperty<Color> selectionColorProperty() {
         if (null == selectionColor) {
             selectionColor = new ObjectPropertyBase<>(_selectionColor) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return WaferMap.this; }
                 @Override public String getName() { return "selectionColor"; }
             };
             _selectionColor = null;
         }
         return selectionColor;
     }

     public boolean getDieTextVisible() { return null == dieTextVisible ? _dieTextVisible : dieTextVisible.get(); }
     public void setDieTextVisible(final boolean dieTextVisible) {
         if (null == this.dieTextVisible) {
             _dieTextVisible = dieTextVisible;
             redraw();
         } else {
             this.dieTextVisible.set(dieTextVisible);
         }
     }
     public BooleanProperty dieTextVisibleProperty() {
         if (null == dieTextVisible) {
             dieTextVisible = new BooleanPropertyBase(_dieTextVisible) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return WaferMap.this; }
                 @Override public String getName() { return "dieLabelsVisible"; }
             };
         }
         return dieTextVisible;
     }

     public boolean getDensityColorsVisible() { return null == densityColorsVisible ? _densityColorsVisible : densityColorsVisible.get(); }
     public void setDensityColorsVisible(final boolean densityColorsVisible) {
         if (null == this.densityColorsVisible) {
             _densityColorsVisible = densityColorsVisible;
             redraw();
         } else {
             this.densityColorsVisible.set(densityColorsVisible);
         }
     }
     public BooleanProperty densityColorsVisibleProperty() {
         if (null == densityColorsVisible) {
             densityColorsVisible = new BooleanPropertyBase(_densityColorsVisible) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return WaferMap.this; }
                 @Override public String getName() { return "densityColorsVisible"; }
             };
         }
         return densityColorsVisible;
     }

     public boolean getLegendVisible() { return null == legendVisible ? _legendVisible : legendVisible.get(); }
     public void setLegendVisible(final boolean legendVisible) {
         if (null == this.legendVisible) {
             _legendVisible = legendVisible;
             redraw();
         } else {
             this.legendVisible.set(legendVisible);
         }
     }
     public BooleanProperty legendVisibleProperty() {
         if (null == legendVisible) {
             legendVisible = new BooleanPropertyBase(_legendVisible) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return WaferMap.this; }
                 @Override public String getName() { return "legendVisible"; }
             };
         }
         return legendVisible;
     }

     public boolean getDefectsVisible() { return null == defectsVisible ? _defectsVisible : defectsVisible.get(); }
     public void setDefectsVisible(final boolean defectsVisible) {
         if (null == this.defectsVisible) {
             _defectsVisible = defectsVisible;
             redraw();
         } else {
             this.defectsVisible.set(defectsVisible);
         }
     }
     public BooleanProperty defectsVisibleProperty() {
         if (null == defectsVisible) {
             defectsVisible = new BooleanPropertyBase(_defectsVisible) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return WaferMap.this; }
                 @Override public String getName() { return "defectsVisible"; }
             };
         }
         return defectsVisible;
     }

     public boolean getHeatmapVisible() { return null == heatmapVisible ? _heatmapVisible : heatmapVisible.get(); }
     public void setHeatmapVisible(final boolean heatmapVisible) {
         if (null == this.heatmapVisible) {
             _heatmapVisible = heatmapVisible;
             this.heatmap.setVisible(heatmapVisible);
             this.heatmap.setManaged(heatmapVisible);
             if (heatmapVisible) {
                 this.heatmap.clearHeatMap();
                 resize();
             }
             resize();
         } else {
             this.heatmapVisible.set(heatmapVisible);
         }
     }
     public BooleanProperty heatmapVisibleProperty() {
         if (null == heatmapVisible) {
             heatmapVisible = new BooleanPropertyBase(_heatmapVisible) {
                 @Override protected void invalidated() {
                     heatmap.setVisible(get());
                     heatmap.setManaged(get());
                     if (get()) {
                         heatmap.clearHeatMap();
                         resize();
                     }
                 }
                 @Override public Object getBean() { return WaferMap.this; }
                 @Override public String getName() { return "heatmapVisible"; }
             };
         }
         return heatmapVisible;
     }

     public void setHeatmapColorMapping(final ColorMapping colorMapping) {
         heatmap.setColorMapping(colorMapping);
         if (getHeatmapVisible()) {
             heatmap.clearHeatMap();
             resize();
         }
     }

     public void setHeatmapSpotRadius(final double spotRadius) {
         heatmap.setSpotRadius(Helper.clamp(1, 20, spotRadius));
         if (getHeatmapVisible()) {
             heatmap.clearHeatMap();
             resize();
         }
     }

     public void setHeatmapOpacity(final double opacity) {
         heatmap.setOpacity(Helper.clamp(0.0, 1.0, opacity));
     }

     public void setClassConfigMap(final Map<Integer, ClassConfig> classConfigMap) {
         this.classConfigMap.clear();
         this.classConfigMap.putAll(classConfigMap);
     }
     public void setClassConfig(final int classNumber, final ClassConfig classConfig) {
         if (classNumber < 0) { throw new IllegalArgumentException("ClassNumber cannot be smaller than 0"); }
         classConfigMap.put(classNumber, classConfig);
     }
     public void removeClassConfig(final int classNumber) {
         if (classConfigMap.containsKey(classNumber)) {
             classConfigMap.remove(classNumber);
         }
     }
     public void clearClassConfig() {
         classConfigMap.clear();
         redraw();
     }

     public Die getSelectedDie() { return selectedDie.get(); }
     public ReadOnlyObjectProperty<Die> selectedDieProperty() { return selectedDie; }

     /**
      * Calling this method will render this chart/plot to a png given of the given width and height
      * @param filename The path and name of the file  /Users/hansolo/Desktop/plot.png
      * @param width The width of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
      * @param height The height of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
      * @return True if the procedure was successful, otherwise false
      */
     public boolean renderToImage(final String filename, final int width, final int height) {
         return Helper.renderToImage(WaferMap.this, width, height, filename);
     }

     /**
      * Calling this method will render this chart/plot to a png given of the given width and height
      * @param width The width of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
      * @param height The height of the final image in pixels (if < 0 then 400 and if > 4096 then 4096)
      * @return A BufferedImage of this chart in the given dimension
      */
     public BufferedImage renderToImage(final int width, final int height) {
         return Helper.renderToImage(WaferMap.this, width, height);
     }

     public void dispose() {
         canvas.removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);
     }


     private void createHeatmap() {
         heatmap.clearHeatMap();
         List<Point> spots = new ArrayList<>();
         for (Die die : kla.getDies().values()) {
             for (Defect defect : die.getDefects()) {
                 double tmpXAbsolute = (die.getOriginX() + ((die.getSizeX() * die.getXIndex() + defect.getXRel())) / 1000) * factor;
                 double tmpYAbsolute = (die.getOriginY() + ((die.getSizeY() * die.getYIndex() + defect.getYRel())) / 1000) * factor;
                 spots.add(new Point(tmpXAbsolute, tmpYAbsolute));
             }
         }
         heatmap.setSpots(spots);
     }

     private void mousePressed(final MouseEvent e) {
         Optional<Rectangle> optRect = dieMap.values().stream().filter(rect -> rect.contains(e.getX(), e.getY())).findFirst();
         if (optRect.isPresent()) {
             Optional<String> optDieName = Helper.getKeysByValue(dieMap, optRect.get()).stream().findFirst();
             if (optDieName.isPresent()) {
                 final Die selected = kla.getDies().get(optDieName.get());
                 if (null == selectedDie.get()) {
                     selectedDie.set(selected);
                 } else {
                     selectedDie.set(selectedDie.get().equals(selected) ? null : selected);
                 }
                 redraw();
             }
         }
     }


     // ******************** Layout *******************************************
     @Override public void layoutChildren() {
         super.layoutChildren();
     }

     private void resize() {
         width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
         height = getHeight() - getInsets().getTop() - getInsets().getBottom();
         size   = width < height ? width : height;

         if (width > 0 && height > 0) {
             factor         = size / kla.getSampleSize();
             defectSize     = Helper.clamp(1, 3, size / 150);
             halfDefectSize = defectSize * 0.5;
             centerX        = size * 0.5;
             centerY        = size * 0.5;
             diameter       = kla.getSampleSize() * factor;
             notchSize      = diameter / 200;
             minDieSize     = Math.min(kla.getDiePitchX(), kla.getDiePitchY()) / 2000;
             fontSize       = minDieSize * factor;

             canvas.setWidth(size);
             canvas.setHeight(size);
             canvas.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

             heatmap.setFitWidth(size);
             heatmap.setFitHeight(size);
             heatmap.relocate((getWidth() - canvas.getWidth()) * 0.5, (getHeight() - canvas.getHeight()) * 0.5);

             pane.setPrefSize(size, size);
             pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

             if (getHeatmapVisible() && heatmap.getSpots().isEmpty()) { createHeatmap(); }

             redraw();
         }
     }

     private void redraw() {
         dieMap.clear();

         ctx.clearRect(0, 0, width, height);

         Color waferFill   = getWaferFill();
         Color waferStroke = getWaferStroke();

         // Draw wafer
         ctx.setLineWidth(0.5);
         ctx.setFill(waferFill);
         ctx.setStroke(waferStroke);
         ctx.fillOval(0, 0, diameter, diameter);
         ctx.strokeOval(0, 0, diameter, diameter);

         // Draw notch
         switch (kla.getOrientationMarkLocation()) {
             case UP    -> {
                 ctx.setFill(getNotchFill());
                 ctx.fillOval(centerX - notchSize, notchSize * 1.5, notchSize * 1.5, notchSize * 3);
             }
             case RIGHT -> {
                 ctx.setFill(getNotchFill());
                 ctx.fillOval(diameter - notchSize * 1.5, centerY - notchSize, notchSize * 3, notchSize * 1.5);
             }
             case DOWN  -> {
                 ctx.setFill(getNotchFill());
                 ctx.fillOval(centerX - notchSize, diameter - notchSize * 1.5, notchSize * 1.5, notchSize * 3);
             }
             case LEFT  -> {
                 ctx.setFill(getNotchFill());
                 ctx.fillOval(notchSize * 1.5, centerY - notchSize, notchSize * 3, notchSize * 1.5);
             }
             default -> {

             }
         }

         // Draw dies
         ctx.setFont(Font.font(fontSize));
         ctx.setTextAlign(TextAlignment.CENTER);
         ctx.setTextBaseline(VPos.CENTER);
         kla.getDies().entrySet().forEach(entry -> {
             String name = entry.getKey();
             Die    die  = entry.getValue();
             double x = die.getOriginX() * factor;
             double y = die.getOriginY() * factor;
             double w = die.getSizeX() * factor;
             double h = die.getSizeY() * factor;

             if (getDensityColorsVisible()) {
                 ctx.save();
                 int d = die.getNoOfDefects();
                 if (d > 100) {
                     ctx.setFill(defectDensityColors.get(6));
                 } else if (d > 60) {
                     ctx.setFill(defectDensityColors.get(5));
                 } else if (d > 40) {
                     ctx.setFill(defectDensityColors.get(4));
                 } else if (d > 20) {
                     ctx.setFill(defectDensityColors.get(3));
                 } else if (d > 10) {
                     ctx.setFill(defectDensityColors.get(2));
                 } else if (d > 0) {
                     ctx.setFill(defectDensityColors.get(1));
                 } else {
                     ctx.setFill(Color.TRANSPARENT);
                 }
                 ctx.fillRect(x, y, w, h);
                 ctx.restore();
             }
             ctx.setStroke(waferStroke);
             ctx.strokeRect(x, y, w, h);

             dieMap.put(die.getName(), new Rectangle(x, y, w, h));

             if ((fontSize > 5)  && getDieTextVisible()) {
                 ctx.setFill(getDieTextFill());
                 ctx.fillText(name, x + w * 0.5, y + h * 0.5, w);
             }
         });

         // Draw selected die
         if (null != getSelectedDie()) {
             ctx.save();
             ctx.setLineWidth(1);
             Rectangle selectionRect = dieMap.get(getSelectedDie().getName());
             ctx.setStroke(getSelectionColor());
             ctx.strokeRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
             ctx.restore();
         }

         // Draw defects
         if (getDefectsVisible()) {
             boolean drawDefect;
             for (Die die : kla.getDies().values()) {
                 for (Defect defect : die.getDefects()) {
                     if (classConfigMap.isEmpty()) {
                         ctx.setFill(getDefectFill());
                         ctx.setStroke(getDefectStroke());
                         drawDefect = true;
                     } else {
                         int classNumber = defect.getClassNumber();
                         if (classConfigMap.containsKey(classNumber)) {
                             drawDefect = classConfigMap.get(classNumber).visible();
                             ctx.setFill(classConfigMap.get(classNumber).fill());
                             ctx.setStroke(classConfigMap.get(classNumber).stroke());
                         } else {
                             drawDefect = false;
                             ctx.setFill(getDefectFill());
                             ctx.setStroke(getDefectStroke());
                         }
                     }
                     if (drawDefect) {
                         if (kla.getClasses().stream().filter(clazz -> clazz.getId() == defect.getClassNumber()).count() > 0) {
                             double tmpXAbsolute = (die.getOriginX() + ((die.getSizeX() * die.getXIndex() + defect.getXRel())) / 1000) * factor;
                             double tmpYAbsolute = (die.getOriginY() + ((die.getSizeY() * die.getYIndex() + defect.getYRel())) / 1000) * factor;
                             ctx.fillOval(tmpXAbsolute - halfDefectSize, tmpYAbsolute - halfDefectSize, defectSize, defectSize);
                             ctx.strokeOval(tmpXAbsolute - halfDefectSize, tmpYAbsolute - halfDefectSize, defectSize, defectSize);
                         }
                     }
                 }
             }
         }

         // Draw legend
         if (size > 320 && getLegendVisible()) {
             double boxSize = 10.0 / 500.0 * size;
             for (int i = 6; i > 0; i--) {
                 double y = size - 10 - i * (boxSize + 5);
                 ctx.setFill(defectDensityColors.get(i));
                 ctx.fillRect(10, y, boxSize, boxSize);
                 ctx.setFill(Color.BLACK);
                 switch (i) {
                     case 6 -> ctx.fillText(" > 100", 3 * boxSize, y + boxSize * 0.5);
                     case 5 -> ctx.fillText(" >  60", 3 * boxSize, y + boxSize * 0.5);
                     case 4 -> ctx.fillText(" >  40", 3 * boxSize, y + boxSize * 0.5);
                     case 3 -> ctx.fillText(" >  20", 3 * boxSize, y + boxSize * 0.5);
                     case 2 -> ctx.fillText(" >  10", 3 * boxSize, y + boxSize * 0.5);
                     case 1 -> ctx.fillText(" >   0", 3 * boxSize, y + boxSize * 0.5);
                 }
             }
         }
     }
 }