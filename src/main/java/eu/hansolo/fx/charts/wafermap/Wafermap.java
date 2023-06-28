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
 import javafx.beans.DefaultProperty;
 import javafx.beans.property.BooleanProperty;
 import javafx.beans.property.BooleanPropertyBase;
 import javafx.beans.property.ObjectProperty;
 import javafx.beans.property.ObjectPropertyBase;
 import javafx.collections.ObservableList;
 import javafx.geometry.VPos;
 import javafx.scene.Node;
 import javafx.scene.canvas.Canvas;
 import javafx.scene.canvas.GraphicsContext;
 import javafx.scene.input.MouseEvent;
 import javafx.scene.layout.Region;
 import javafx.scene.paint.Color;
 import javafx.scene.text.Font;
 import javafx.scene.text.TextAlignment;

 import java.util.List;
 import java.util.Map;
 import java.util.Optional;


 @DefaultProperty("children")
 public class Wafermap extends Region {
     private static final double                PREFERRED_WIDTH  = 300;
     private static final double                PREFERRED_HEIGHT = 300;
     private static final double                MINIMUM_WIDTH    = 50;
     private static final double                MINIMUM_HEIGHT   = 50;
     private static final double                MAXIMUM_WIDTH    = 2048;
     private static final double                MAXIMUM_HEIGHT   = 2048;
     private              double                size;
     private              double                width;
     private              double                height;
     private              Canvas                canvas;
     private              GraphicsContext       ctx;
     private              KLA                   kla;
     private              double                factor;
     private              double                defectSize;
     private              double                halfDefectSize;
     private              Color                 _wafermapFill;
     private              ObjectProperty<Color> wafermapFill;
     private              Color                 _wafermapStroke;
     private              ObjectProperty<Color> wafermapStroke;
     private              Color                 _dieLabelFill;
     private              ObjectProperty<Color> dieLabelFill;
     private              List<Color>           defectDensityColors;
     private              boolean               _dieLabelsVisible;
     private              BooleanProperty       dieLabelsVisible;
     private              boolean               _densityColorsVisible;
     private              BooleanProperty       densityColorsVisible;


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
         _dieLabelFill         = Constants.DEFAULT_DIE_LABEL_FILL;
         defectDensityColors   = Constants.DEFAULT_DEFECT_DENSITY_COLORS;
         _dieLabelsVisible     = false;
         _densityColorsVisible = false;

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
         canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {

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
         ctx.clearRect(0, 0, width, height);

         int maxDefectsPerDie = kla.getMaxDefectsPerDie();
         double densityFactor = maxDefectsPerDie == 0 ? 1 : 100 / maxDefectsPerDie;

         double centerX = size * 0.5;
         double centerY = size * 0.5;

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
                 ctx.setFill(Color.BLACK);
                 ctx.fillOval(centerX - notchSize, notchSize * 1.5, notchSize * 1.5, notchSize * 3);
             }
             case RIGHT -> {
                 ctx.setFill(Color.BLACK);
                 ctx.fillOval(diameter - notchSize * 1.5, centerY - notchSize, notchSize * 3, notchSize * 1.5);
             }
             case DOWN  -> {
                 ctx.setFill(Color.BLACK);
                 ctx.fillOval(centerX - notchSize, diameter - notchSize * 1.5, notchSize * 1.5, notchSize * 3);
             }
             case LEFT  -> {
                 ctx.setFill(Color.BLACK);
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

             if (getDieLabelsVisible()) {
                 ctx.setFill(getDieLabelFill());
                 ctx.fillText(name, x + w * 0.5, y + h * 0.5, w);
             }
         });

         // Draw defects
         ctx.setFill(Color.BLACK);
         for (Die die : kla.getDies().values()) {
             for (Defect defect : die.getDefects()) {
                 if (kla.getClasses().stream().filter(clazz -> clazz.getId() == defect.getClassNumber()).count() > 0) {
                     double tmpXAbsolute = (die.getOriginX() + ((die.getSizeX() * die.getXIndex() + defect.getXRel())) / 1000) * factor;
                     double tmpYAbsolute = (die.getOriginY() + ((die.getSizeY() * die.getYIndex() + defect.getYRel())) / 1000) * factor;
                     ctx.fillOval(tmpXAbsolute - halfDefectSize, tmpYAbsolute - halfDefectSize, defectSize, defectSize);
                 }
             }
         }
     }
 }