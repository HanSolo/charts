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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Map;


@DefaultProperty("children")
public class DieMap extends Region {
    private static final double                              PREFERRED_WIDTH  = 500;
    private static final double                              PREFERRED_HEIGHT = 500;
    private static final double                              MINIMUM_WIDTH    = 50;
    private static final double                              MINIMUM_HEIGHT   = 50;
    private static final double                              MAXIMUM_WIDTH    = 2048;
    private static final double                              MAXIMUM_HEIGHT   = 2048;
    private              double                              size;
    private              double                              width;
    private              double                              height;
    private              Canvas                              canvas;
    private              GraphicsContext                     ctx;
    private              Die                                 die;
    private              double                              factor;
    private              double                              defectSize;
    private              double                              halfDefectSize;
    private              Color                               _dieFill;
    private              ObjectProperty<Color>               dieFill;
    private              Color                               _dieStroke;
    private              ObjectProperty<Color>               dieStroke;
    private              Color                               _defectFill;
    private              ObjectProperty<Color>               defectFill;
    private              Color                               _defectStroke;
    private              ObjectProperty<Color>               defectStroke;
    private              Color                               _dieTextFill;
    private              ObjectProperty<Color>               dieTextFill;
    private              boolean                             _dieTextVisible;
    private              BooleanProperty                     dieTextVisible;
    private              boolean                             _densityColorsVisible;
    private              BooleanProperty                     densityColorsVisible;
    private              List<Color>                         defectDensityColors;
    private              ObservableMap<Integer, ClassConfig> classConfigMap;


    // ******************** Constructors **************************************
    public DieMap() {
        this(null);
    }
    public DieMap(final Die die) {
        this.die                   = die;
        this._dieFill              = Constants.DEFAULT_DIE_FILL;
        this._dieStroke            = Constants.DEFAULT_DIE_STROKE;
        this._defectFill           = Constants.DEFAULT_DEFECT_FILL;
        this._defectStroke         = Constants.DEFAULT_DEFECT_STROKE;
        this._dieTextFill          = Constants.DEFAULT_DIE_LABEL_FILL;
        this._dieTextVisible       = false;
        this._densityColorsVisible = false;
        this.defectDensityColors   = Constants.DEFAULT_DEFECT_DENSITY_COLORS;
        this.classConfigMap        = FXCollections.observableHashMap();

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
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width)  { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height)  { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width)  { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public void setDie(final Die die) {
        this.die = die;
        resize();
        redraw();
    }

    public Color getDieFill() { return null == dieFill ? _dieFill : dieFill.get(); }
    public void setDieFill(final Color dieFill) {
        if (null == this.dieFill) {
            _dieFill = dieFill;
            redraw();
        } else {
            this.dieFill.set(dieFill);
        }
    }
    public ObjectProperty<Color> dieFillProperty() {
        if (null == dieFill) {
            dieFill  = new ObjectPropertyBase<>(_dieFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return DieMap.this; }
                @Override public String getName() { return "dieFill"; }
            };
            _dieFill = null;
        }
        return dieFill;
    }

    public Color getDieStroke() { return null == dieStroke ? _dieStroke : dieStroke.get(); }
    public void setDieStroke(final Color dieStroke) {
        if (null == this.dieStroke) {
            _dieStroke = dieStroke;
            redraw();
        } else {
            this.dieStroke.set(dieStroke);
        }
    }
    public ObjectProperty<Color> dieStrokeProperty() {
        if (null == dieStroke) {
            dieStroke  = new ObjectPropertyBase<>(_dieStroke) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return DieMap.this; }
                @Override public String getName() { return "DieMapStroke"; }
            };
            _dieStroke = null;
        }
        return dieStroke;
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
                @Override public Object getBean() { return DieMap.this; }
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
                @Override public Object getBean() { return DieMap.this; }
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
                @Override public Object getBean() { return DieMap.this; }
                @Override public String getName() { return "dieLabelFill"; }
            };
            _dieTextFill = null;
        }
        return dieTextFill;
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
                @Override public Object getBean() { return DieMap.this; }
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
                @Override public Object getBean() { return DieMap.this; }
                @Override public String getName() { return "densityColorsVisible"; }
            };
        }
        return densityColorsVisible;
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


    // ******************** Layout *******************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
    }

    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            defectSize     = Helper.clamp(1, 5, size / 100);
            halfDefectSize = defectSize * 0.5;
            if (null == die) {
                factor = 1.0;
            } else {
                double maxDieSize = die.getSizeX() > die.getSizeY() ? die.getSizeX() : die.getSizeY();
                factor = size / maxDieSize;
            }
            canvas.setWidth(size);
            canvas.setHeight(size);
            canvas.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            redraw();
        }
    }

    private void redraw() {
        ctx.clearRect(0, 0, width, height);

        double centerX   = size * 0.5;
        double centerY   = size * 0.5;

        double dieWidth  = null == die ? size : die.getSizeX() * factor;
        double dieHeight = null == die ? size : die.getSizeY() * factor;

        double offsetX   = (width - dieWidth) * 0.5;
        double offsetY   = (height - dieHeight) * 0.5;

        Color dieFill     = getDieFill();
        Color dieStroke   = getDieStroke();

        ctx.setLineWidth(1);

        if (null == die) {
            ctx.strokeRect(offsetX, offsetY, dieWidth, dieHeight);
            return;
        }

        if (getDensityColorsVisible()) {
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
        } else {
            ctx.setFill(dieFill);
        }
        ctx.fillRect(offsetX, offsetY, dieWidth, dieHeight);
        ctx.setStroke(dieStroke);
        ctx.strokeRect(offsetX, offsetY, dieWidth, dieHeight);

        ctx.setFont(Font.font(factor));
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);
        if (getDieTextVisible()) {
            ctx.setFill(getDieTextFill());
            ctx.fillText(die.getName(), centerX, centerY);
        }

        // Draw defects
        boolean drawDefect;
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
                double x = offsetX + (defect.getXRel() / 1000) * factor;
                double y = offsetY + (defect.getYRel() / 1000) * factor;
                ctx.fillOval(x - halfDefectSize, y - halfDefectSize, defectSize, defectSize);
                ctx.strokeOval(x - halfDefectSize, y - halfDefectSize, defectSize, defectSize);
            }
        }
    }
}
