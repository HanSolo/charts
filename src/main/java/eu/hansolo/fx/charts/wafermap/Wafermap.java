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

 import eu.hansolo.fx.charts.tools.Helper;
 import eu.hansolo.fx.geometry.Rectangle;
 import javafx.beans.DefaultProperty;
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
 import javafx.scene.paint.Color;
 import javafx.scene.text.Font;
 import javafx.scene.text.TextAlignment;

 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Optional;


 @DefaultProperty("children")
 public class Wafermap extends Region {
     private static final double                              PREFERRED_WIDTH  = 300;
     private static final double                              PREFERRED_HEIGHT = 300;
     private static final double                              MINIMUM_WIDTH    = 50;
     private static final double                              MINIMUM_HEIGHT   = 50;
     private static final double                              MAXIMUM_WIDTH    = 2048;
     private static final double                              MAXIMUM_HEIGHT   = 2048;
     private              double                              size;
     private              double                              width;
     private              double                              height;
     private              Canvas                              canvas;
     private              GraphicsContext                     ctx;
     private              KLA                                 kla;
     private              double                              factor;
     private              double                              defectSize;
     private              double                              halfDefectSize;
     private              Color                               _wafermapFill;
     private              ObjectProperty<Color>               wafermapFill;
     private              Color                               _wafermapStroke;
     private              ObjectProperty<Color>               wafermapStroke;
     private              Color                               _notchFill;
     private              ObjectProperty<Color>               notchFill;
     private              Color                               _defectFill;
     private              ObjectProperty<Color>               defectFill;
     private              Color                               _defectStroke;
     private              ObjectProperty<Color>               defectStroke;
     private              Color                               _dieLabelFill;
     private              ObjectProperty<Color>               dieLabelFill;
     private              Color                               _selectionColor;
     private              ObjectProperty<Color>               selectionColor;
     private              List<Color>                         defectDensityColors;
     private              boolean                             _dieLabelsVisible;
     private              BooleanProperty                     dieLabelsVisible;
     private              boolean                             _densityColorsVisible;
     private              BooleanProperty                     densityColorsVisible;
     private              ObservableMap<Integer, ClassConfig> classConfigMap;
     private              ObjectProperty<Die>                 selectedDie;
     private              Map<String, Rectangle>              dieMap;
     private              EventHandler<MouseEvent>            mouseHandler;


     // ******************** Constructors **************************************
     public Wafermap() {
         this("");
     }
     public Wafermap(final String filename) {
         if (null != filename && !filename.isEmpty()) {
             Optional<KLA> klaOpt = KLAParser.INSTANCE.parse(filename);
             this.kla = klaOpt.isPresent() ? klaOpt.get() : new KLA();
         } else {
             this.kla = new KLA();
         }
         this.kla.createDieMap();

         _wafermapFill         = Constants.DEFAULT_WAFERMAP_FILL;
         _wafermapStroke       = Constants.DEFAULT_WAFERMAP_STROKE;
         _notchFill            = Constants.DEFAULT_NOTCH_FILL;
         _defectFill           = Constants.DEFAULT_DEFECT_FILL;
         _defectStroke         = Constants.DEFAULT_DEFECT_STROKE;
         _dieLabelFill         = Constants.DEFAULT_DIE_LABEL_FILL;
         _selectionColor       = Constants.DEFAULT_SELECTION_COLOR;
         defectDensityColors   = Constants.DEFAULT_DEFECT_DENSITY_COLORS;
         _dieLabelsVisible     = false;
         _densityColorsVisible = false;
         classConfigMap        = FXCollections.observableHashMap();
         selectedDie           = new SimpleObjectProperty<>(null);
         dieMap                = new HashMap<>();
         mouseHandler          = e -> {
             EventType<? extends Event> type = e.getEventType();
             if (MouseEvent.MOUSE_PRESSED.equals(type)) {
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
         ctx    = canvas.getGraphicsContext2D();

         getChildren().setAll(canvas);
     }

     private void registerListeners() {
         widthProperty().addListener(o -> resize());
         heightProperty().addListener(o -> resize());
         canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
         classConfigMap.addListener((MapChangeListener<Integer, ClassConfig>) change -> redraw());
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
         this.kla = kla;
         this.kla.createDieMap();
         resize();
         redraw();
     }

     public Color getWafermapFill() { return null == wafermapFill ? _wafermapFill : wafermapFill.get(); }
     public void setWafermapFill(final Color wafermapFill) {
         if (null == this.wafermapFill) {
             _wafermapFill = wafermapFill;
             redraw();
         } else {
             this.wafermapFill.set(wafermapFill);
         }
     }
     public ObjectProperty<Color> wafermapFillProperty() {
         if (null == wafermapFill) {
             wafermapFill = new ObjectPropertyBase<>(_wafermapFill) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return Wafermap.this; }
                 @Override public String getName() { return "wafermapFill"; }
             };
             _wafermapFill = null;
         }
         return wafermapFill;
     }

     public Color getWafermapStroke() { return null == wafermapStroke ? _wafermapStroke : wafermapStroke.get(); }
     public void setWafermapStroke(final Color wafermapStroke) {
         if (null == this.wafermapStroke) {
             _wafermapStroke = wafermapStroke;
             redraw();
         } else {
             this.wafermapStroke.set(wafermapStroke);
         }
     }
     public ObjectProperty<Color> wafermapStrokeProperty() {
         if (null == wafermapStroke) {
             wafermapStroke = new ObjectPropertyBase<>(_wafermapStroke) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return Wafermap.this; }
                 @Override public String getName() { return "wafermapStroke"; }
             };
             _wafermapStroke = null;
         }
         return wafermapStroke;
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
                 @Override public Object getBean() { return Wafermap.this; }
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
                 @Override public Object getBean() { return Wafermap.this; }
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
                 @Override public Object getBean() { return Wafermap.this; }
                 @Override public String getName() { return "defectStroke"; }
             };
             _defectStroke = null;
         }
         return defectStroke;
     }

     public Color getDieLabelFill() { return null == dieLabelFill ? _dieLabelFill : dieLabelFill.get(); }
     public void setDieLabelFill(final Color dieLabelFill) {
         if (null == this.dieLabelFill) {
             _dieLabelFill = dieLabelFill;
             redraw();
         } else {
             this.dieLabelFill.set(dieLabelFill);
         }
     }
     public ObjectProperty<Color> dieLabelFillProperty() {
         if (null == dieLabelFill) {
             dieLabelFill = new ObjectPropertyBase<>(_dieLabelFill) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return Wafermap.this; }
                 @Override public String getName() { return "dieLabelFill"; }
             };
             _dieLabelFill = null;
         }
         return dieLabelFill;
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
                 @Override public Object getBean() { return Wafermap.this; }
                 @Override public String getName() { return "selectionColor"; }
             };
             _selectionColor = null;
         }
         return selectionColor;
     }

     public boolean getDieLabelsVisible() { return null == dieLabelsVisible ? _dieLabelsVisible : dieLabelsVisible.get(); }
     public void setDieLabelsVisible(final boolean dieLabelsVisible) {
         if (null == this.dieLabelsVisible) {
             _dieLabelsVisible = dieLabelsVisible;
             redraw();
         } else {
             this.dieLabelsVisible.set(dieLabelsVisible);
         }
     }
     public BooleanProperty dieLabelsVisibleProperty() {
         if (null == dieLabelsVisible) {
             dieLabelsVisible = new BooleanPropertyBase(_dieLabelsVisible) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return Wafermap.this; }
                 @Override public String getName() { return "dieLabelsVisible"; }
             };
         }
         return dieLabelsVisible;
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
                 @Override public Object getBean() { return Wafermap.this; }
                 @Override public String getName() { return "densityColorsVisible"; }
             };
         }
         return densityColorsVisible;
     }

     public void setClassConfigMap(final Map<Integer, ClassConfig> classConfigMap) {
         this.classConfigMap.clear();
         classConfigMap.putAll(classConfigMap);
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


     public void dispose() {
         canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
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
             canvas.setWidth(size);
             canvas.setHeight(size);
             canvas.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

             redraw();
         }
     }

     private void redraw() {
         dieMap.clear();

         ctx.clearRect(0, 0, width, height);

         double centerX  = size * 0.5;
         double centerY  = size * 0.5;
         double diameter = kla.getSampleSize() * factor;

         Color waferFill   = getWafermapFill();
         Color waferStroke = getWafermapStroke();

         // Draw wafer
         ctx.setLineWidth(0.5);
         ctx.setFill(waferFill);
         ctx.setStroke(Color.GRAY);
         ctx.fillOval(0, 0, diameter, diameter);
         ctx.strokeOval(0, 0, diameter, diameter);

         // Draw notch
         final double notchSize = diameter / 200;
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
         ctx.setFont(Font.font(3 * factor));
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

             if (getDieLabelsVisible()) {
                 ctx.setFill(getDieLabelFill());
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
                        drawDefect = classConfigMap.get(classNumber).isVisible();
                        ctx.setFill(classConfigMap.get(classNumber).getFill());
                        ctx.setStroke(classConfigMap.get(classNumber).getStroke());
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
 }