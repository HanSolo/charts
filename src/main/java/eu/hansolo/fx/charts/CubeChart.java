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

import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.fx.charts.tools.P2d;
import eu.hansolo.fx.charts.tools.P3d;
import eu.hansolo.fx.charts.tools.PMatrix;
import eu.hansolo.toolboxfx.FontMetrix;
import eu.hansolo.toolboxfx.font.Fonts;
import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@DefaultProperty("children")
public class CubeChart extends Region {
    public static final  Color                 DEFAULT_CHART_BACKGROUND   = Color.TRANSPARENT;
    public static final  Color                 DEFAULT_CUBE_COLOR         = Color.WHITE;
    public static final  Color                 DEFAULT_CUBE_FRAME_COLOR   = Color.rgb(228, 228, 228);
    public static final  LinearGradient        RED_ORANGE_LEFT_FILL       = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(210, 84, 66)), new Stop(1.0, Color.rgb(224, 130, 66)));
    public static final  LinearGradient        RED_ORANGE_RIGHT_FILL      = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(224, 130, 66)), new Stop(1.0, Color.rgb(237, 181, 75)));
    public static final  LinearGradient        ORANGE_GREEN_LEFT_FILL     = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(213, 184, 79)), new Stop(1.0, Color.rgb(177, 189, 87)));
    public static final  LinearGradient        ORANGE_GREEN_RIGHT_FILL    = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(167, 191, 89)), new Stop(1.0, Color.rgb(125, 184, 123)));
    public static final  LinearGradient        GREEN_BLUE_LEFT_FILL       = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(100, 179, 153)), new Stop(1.0, Color.rgb(79, 166, 182)));
    public static final  LinearGradient        GREEN_BLUE_RIGHT_FILL      = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(79, 154, 179)), new Stop(1.0, Color.rgb(103, 124, 166)));
    public static final  LinearGradient        BLUE_PURPLE_LEFT_FILL      = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(130, 100, 158)), new Stop(1.0, Color.rgb(101, 63, 129)));
    public static final  LinearGradient        BLUE_PURPLE_RIGHT_FILL     = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(87, 47, 118)), new Stop(1.0, Color.rgb(45, 5, 83)));
    public static final  Color                 DEFAULT_TEXT_COLOR         = Color.WHITE;
    public static final  Color                 DEFAULT_EMPTY_TEXT_COLOR   = Color.rgb(128, 128, 128);
    private static final double                PREFERRED_WIDTH            = 400;
    private static final double                PREFERRED_HEIGHT           = 400;
    private static final double                MINIMUM_WIDTH              = 50;
    private static final double                MINIMUM_HEIGHT             = 50;
    private static final double                MAXIMUM_WIDTH              = 4096;
    private static final double                MAXIMUM_HEIGHT             = 4096;
    private              double                width;
    private              double                height;
    private              double                size;
    private              Canvas                canvas;
    private              GraphicsContext       ctx;
    private              Paint                 _chartBackground;
    private              ObjectProperty<Paint> chartBackground;
    private              Color                 _cubeColor;
    private              ObjectProperty<Color> cubeColor;
    private              Color                 _cubeFrameColor;
    private              ObjectProperty<Color> cubeFrameColor;
    private              Paint                 _leftFill;
    private              ObjectProperty<Paint> leftFill;
    private              Paint                 _rightFill;
    private              ObjectProperty<Paint> rightFill;
    private              Color                 _textColor;
    private              ObjectProperty<Color> textColor;
    private              Color                 _emptyTextColor;
    private              ObjectProperty<Color> emptyTextColor;
    private              DoubleProperty        leftValue;
    private              DoubleProperty        rightValue;
    private              StringProperty        leftText;
    private              StringProperty        rightText;
    private              double                lineWidth;
    private              double                cubeSize;
    private              double                dataAreaSize;
    private              Font                  valueFont;
    private              Font                  textFont;
    private              PMatrix               pMatrix;
    private              List<P3d>             cubePoints;
    private              List<P3d>             leftPoints;
    private              List<P3d>             rightPoints;



    // ******************** Constructors **************************************
    public CubeChart() {
        _chartBackground  = DEFAULT_CHART_BACKGROUND;
        _cubeColor        = DEFAULT_CUBE_COLOR;
        _cubeFrameColor   = DEFAULT_CUBE_FRAME_COLOR;
        _leftFill         = RED_ORANGE_LEFT_FILL;
        _rightFill        = RED_ORANGE_RIGHT_FILL;
        _textColor        = DEFAULT_TEXT_COLOR;
        _emptyTextColor   = DEFAULT_EMPTY_TEXT_COLOR;
        leftValue         = new DoublePropertyBase(0) {
            @Override protected void invalidated() {
                set(Helper.clamp(0.0, 1.0, get()));
                redraw();
            }
            @Override public Object getBean() { return CubeChart.this; }
            @Override public String getName() { return "leftValue"; }
        };
        rightValue        = new DoublePropertyBase(0) {
            @Override protected void invalidated() {
                set(Helper.clamp(0.0, 1.0, get()));
                redraw();
            }
            @Override public Object getBean() { return CubeChart.this; }
            @Override public String getName() { return "leftValue"; }
        };
        leftText          = new StringPropertyBase("") {
            @Override protected void invalidated() { redraw(); }
            @Override public Object getBean() { return CubeChart.this; }
            @Override public String getName() { return "leftText"; }
        };
        rightText         = new StringPropertyBase("") {
            @Override protected void invalidated() { redraw(); }
            @Override public Object getBean() { return CubeChart.this; }
            @Override public String getName() { return "rightText"; }
        };
        lineWidth         = 10;
        cubeSize          = 100;
        dataAreaSize      = cubeSize *  0.4;
        valueFont         = Fonts.mazzardsoftlBoldName(44);
        textFont          = Fonts.mazzardsoftlBoldName(12);

        cubePoints = new ArrayList<>();
        cubePoints.add(new P3d(-cubeSize, -cubeSize, -cubeSize)); // lower top left     index 0
        cubePoints.add(new P3d(cubeSize, -cubeSize, -cubeSize));  // lower top right    index 1
        cubePoints.add(new P3d(cubeSize, cubeSize, -cubeSize));   // lower bottom right index 2
        cubePoints.add(new P3d(-cubeSize, cubeSize, -cubeSize));  // lower bottom left  index 3
        cubePoints.add(new P3d(-cubeSize, -cubeSize, cubeSize));  // upper top left     index 4
        cubePoints.add(new P3d(cubeSize, -cubeSize, cubeSize));   // upper top right    index 5
        cubePoints.add(new P3d(cubeSize, cubeSize, cubeSize));    // upper bottom right index 6
        cubePoints.add(new P3d(-cubeSize, cubeSize, cubeSize));   // upper  bottom left index 7

        leftPoints = new ArrayList<>();
        leftPoints.add(new P3d(-dataAreaSize, -dataAreaSize, -dataAreaSize)); // lower top left      index 0
        leftPoints.add(new P3d(dataAreaSize, -dataAreaSize, -dataAreaSize));  // lower top right     index 1
        leftPoints.add(new P3d(dataAreaSize, dataAreaSize, -dataAreaSize));   // lower bottom right  index 2
        leftPoints.add(new P3d(-dataAreaSize, dataAreaSize, -dataAreaSize));  // lower bottom left   index 3
        leftPoints.add(new P3d(-dataAreaSize, -dataAreaSize, dataAreaSize));  // upper top left      index 4
        leftPoints.add(new P3d(dataAreaSize, -dataAreaSize, dataAreaSize));   // upper top right     index 5
        leftPoints.add(new P3d(dataAreaSize, dataAreaSize, dataAreaSize));    // upper bottom right  index 6
        leftPoints.add(new P3d(-dataAreaSize, dataAreaSize, dataAreaSize));   // upper bottom left   index 7

        rightPoints = new ArrayList<>();
        rightPoints.add(new P3d(-dataAreaSize, -dataAreaSize, -dataAreaSize)); // lower top left      index 0
        rightPoints.add(new P3d(dataAreaSize, -dataAreaSize, -dataAreaSize));  // lower top right     index 1
        rightPoints.add(new P3d(dataAreaSize, dataAreaSize, -dataAreaSize));   // lower bottom right  index 2
        rightPoints.add(new P3d(-dataAreaSize, dataAreaSize, -dataAreaSize));  // lower bottom left   index 3
        rightPoints.add(new P3d(-dataAreaSize, -dataAreaSize, dataAreaSize));  // upper top left      index 4
        rightPoints.add(new P3d(dataAreaSize, -dataAreaSize, dataAreaSize));   // upper top right     index 5
        rightPoints.add(new P3d(dataAreaSize, dataAreaSize, dataAreaSize));    // upper bottom right  index 6
        rightPoints.add(new P3d(-dataAreaSize, dataAreaSize, dataAreaSize));   // upper bottom left   index 7

        pMatrix = new PMatrix(new P2d(0, 0), new P2d(0, 0), new P2d(0, 0), new P3d(0, 0, 0), new P2d(width * 0.5, height * 0.5));
        pMatrix.setProjection(Helper.ISOMETRIC);

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

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        ctx.setLineCap(StrokeLineCap.BUTT);

        getChildren().setAll(canvas);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
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

    }

    public Paint getChartBackground() { return null == chartBackground ? _chartBackground : chartBackground.get(); }
    public void setChartBackground(final Paint paint) {
        if (null == chartBackground) {
            _chartBackground = paint;
            redraw();
        } else {
            chartBackground.set(paint);
        }
    }
    public ObjectProperty<Paint> chartBackgroundProperty() {
        if (null == chartBackground) {
            chartBackground = new ObjectPropertyBase<Paint>(_chartBackground) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CubeChart.this; }
                @Override public String getName() { return "chartBackground"; }
            };
            _chartBackground = null;
        }
        return chartBackground;
    }

    public Color getCubeColor() { return null == cubeColor ? _cubeColor : cubeColor.get(); }
    public void setCubeColor(final Color cubeColor) {
        if (null == this.cubeColor) {
            _cubeColor = cubeColor;
            redraw();
        } else {
            this.cubeColor.set(cubeColor);
        }
    }
    public ObjectProperty<Color> cubeColorProperty() {
        if (null == cubeColor) {
            cubeColor = new ObjectPropertyBase<>(_cubeColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CubeChart.this; }
                @Override public String getName() { return "cubeColor"; }
            };
            _cubeColor = null;
        }
        return cubeColor;
    }

    public Color getCubeFrameColor() { return null == cubeFrameColor ? _cubeFrameColor : cubeFrameColor.get(); }
    public void setCubeFrameColor(final Color cubeFrameColor) {
        if (null == this.cubeFrameColor) {
            _cubeFrameColor = cubeFrameColor;
            redraw();
        } else {
            this.cubeFrameColor.set(cubeFrameColor);
        }
    }
    public ObjectProperty<Color> cubeFrameColorProperty() {
        if (null == cubeFrameColor) {
            cubeFrameColor = new ObjectPropertyBase<>(_cubeFrameColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CubeChart.this; }
                @Override public String getName() { return "cubeFrameColor"; }
            };
            _cubeFrameColor = null;
        }
        return cubeFrameColor;
    }

    public Paint getLeftFill() { return null == leftFill ? _leftFill : leftFill.get(); }
    public void setLeftFill(final Paint leftFill) {
        if (null == this.leftFill) {
            _leftFill = leftFill;
            redraw();
        } else {
            this.leftFill.set(leftFill);
        }
    }
    public ObjectProperty<Paint> leftFillProperty() {
        if (null == leftFill) {
            leftFill = new ObjectPropertyBase<>(_leftFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CubeChart.this; }
                @Override public String getName() { return "leftFill"; }
            };
            _leftFill = null;
        }
        return leftFill;
    }

    public Paint getRightFill() { return null == rightFill ? _rightFill : rightFill.get(); }
    public void setRightFill(final Paint rightFill) {
        if (null == this.rightFill) {
            _rightFill = rightFill;
            redraw();
        } else {
            this.rightFill.set(rightFill);
        }
    }
    public ObjectProperty<Paint> rightFillProperty() {
        if (null == rightFill) {
            rightFill = new ObjectPropertyBase<>(_rightFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CubeChart.this; }
                @Override public String getName() { return "rightFill"; }
            };
            _rightFill = null;
        }
        return rightFill;
    }
    
    public Color getTextColor() { return null == textColor ? _textColor : textColor.get(); }
    public void setTextColor(final Color color) {
        if (null == textColor) {
            _textColor = color;
            redraw();
        } else {
            textColor.set(color);
        }
    }
    public ObjectProperty<Color> textColorProperty() {
        if (null == textColor) {
            textColor = new ObjectPropertyBase<>(_textColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CubeChart.this; }
                @Override public String getName() { return "textColor"; }
            };
            _textColor = null;
        }
        return textColor;
    }

    public Color getEmptyTextColor() { return null == emptyTextColor ? _emptyTextColor : emptyTextColor.get(); }
    public void setEmptyTextColor(final Color color) {
        if (null == emptyTextColor) {
            _emptyTextColor = color;
            redraw();
        } else {
            emptyTextColor.set(color);
        }
    }
    public ObjectProperty<Color> emptyTextColorProperty() {
        if (null == emptyTextColor) {
            emptyTextColor = new ObjectPropertyBase<>(_emptyTextColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CubeChart.this; }
                @Override public String getName() { return "emptyTextColor"; }
            };
            _emptyTextColor = null;
        }
        return emptyTextColor;
    }

    public double getLeftValue() { return leftValue.get(); }
    public void setLeftValue(final double leftValue) {
        this.leftValue.set(Helper.clamp(0.0, 1.0, leftValue));
    }
    public DoubleProperty leftValueProperty() { return leftValue; }

    public double getRightValue() { return rightValue.get(); }
    public void setRightValue(final double rightValue) { this.rightValue.set(Helper.clamp(0.0, 1.0, rightValue)); }
    public DoubleProperty rightValueProperty() { return rightValue; }

    public String getLeftText() { return leftText.get(); }
    public void setLeftText(final String leftText) { this.leftText.set(leftText); }
    public StringProperty leftTextProperty() { return leftText; }

    public String getRightText() { return rightText.get(); }
    public void setRightText(final String rightText) { this.rightText.set(rightText); }
    public StringProperty rightTextProperty() { return rightText; }


    private void wrapText(final GraphicsContext ctx, final String text, final double x, final double y, final double maxWidth, final double lineHeight, final int maxNoOfLines) {
        double        tx          = x;
        double        ty          = y;
        double        noOfLines   = 1;
        String[]      words       = text.split(" ");
        String        line        = "";
        FontMetrix    metrix      = new FontMetrix(textFont);
        for(int n = 0 ; n < words.length ; n++) {
            var testLine = line + words[n] + " ";
            var testWidth = metrix.computeStringWidth(testLine);
            if (testWidth > maxWidth && n > 0) {
                ctx.fillText(line, tx, ty);
                line = words[n] + ' ';
                if (noOfLines == maxNoOfLines) { break; }
                ty += lineHeight;
                noOfLines++;
            } else {
                line = testLine;
            }
        }
        ctx.fillText(line, tx, ty);
    }



    // ******************** Drawing *******************************************
    /**
     * Overrideable drawChart() method
     */
    protected void drawChart() {
        ctx.clearRect(0, 0, width, height);
        ctx.setFill(getChartBackground());
        ctx.fillRect(0, 0, width, height);

        double leftValue  = getLeftValue();
        double rightValue = getRightValue();

        // Adjust vertices to new size
        for (int i = 0; i < cubePoints.size() ; i++) {
            P3d point = cubePoints.get(i);
            switch(i) {
                case 0 -> { point.x = -cubeSize; point.y = -cubeSize; point.z = -cubeSize; } // lower top left  index 0
                case 1 -> { point.x = cubeSize;  point.y = -cubeSize; point.z = -cubeSize; } // lower top right
                case 2 -> { point.x = cubeSize;  point.y = cubeSize;  point.z = -cubeSize; } // lower bottom right
                case 3 -> { point.x = -cubeSize; point.y = cubeSize;  point.z = -cubeSize; } // lower bottom left
                case 4 -> { point.x = -cubeSize; point.y = -cubeSize; point.z = cubeSize; }  // upper top left  index 4
                case 5 -> { point.x = cubeSize;  point.y = -cubeSize; point.z = cubeSize; }  // upper top right
                case 6 -> { point.x = cubeSize;  point.y = cubeSize;  point.z = cubeSize; }  // upper bottom right
                case 7 -> { point.x = -cubeSize; point.y = cubeSize;  point.z = cubeSize; }  // upper  bottom left index 7
            }
        }

        // Adjust left data vertices to new size
        for (int i = 0; i < leftPoints.size() ; i++) {
            P3d point = leftPoints.get(i);
            switch(i) {
                case 0 -> { point.x = -dataAreaSize; point.y = -dataAreaSize; point.z = -dataAreaSize; } // lower top left  index 0
                case 1 -> { point.x = dataAreaSize;  point.y = -dataAreaSize; point.z = -dataAreaSize; } // lower top right
                case 2 -> { point.x = dataAreaSize;  point.y = dataAreaSize;  point.z = -dataAreaSize; } // lower bottom right
                case 3 -> { point.x = -dataAreaSize; point.y = dataAreaSize;  point.z = -dataAreaSize; } // lower bottom left
                case 4 -> { point.x = -dataAreaSize; point.y = -dataAreaSize; point.z = dataAreaSize; }  // upper top left  index 4
                case 5 -> { point.x = dataAreaSize;  point.y = -dataAreaSize; point.z = dataAreaSize; }  // upper top right
                case 6 -> { point.x = dataAreaSize;  point.y = dataAreaSize;  point.z = dataAreaSize - (2 * dataAreaSize * (1.0 - leftValue)); }  // upper bottom right
                case 7 -> { point.x = -dataAreaSize; point.y = dataAreaSize;  point.z = dataAreaSize - (2 * dataAreaSize * (1.0 - leftValue)); }  // upper  bottom left index 7
            }
        }

        // Adjust right data vertices to new size
        for (int i = 0; i < rightPoints.size() ; i++) {
            P3d point = rightPoints.get(i);
            switch(i) {
                case 0 -> { point.x = -dataAreaSize; point.y = -dataAreaSize; point.z = -dataAreaSize; } // lower top left  index 0
                case 1 -> { point.x = dataAreaSize;  point.y = -dataAreaSize; point.z = -dataAreaSize; } // lower top right
                case 2 -> { point.x = dataAreaSize;  point.y = dataAreaSize;  point.z = -dataAreaSize; } // lower bottom right
                case 3 -> { point.x = -dataAreaSize; point.y = dataAreaSize;  point.z = -dataAreaSize; } // lower bottom left
                case 4 -> { point.x = -dataAreaSize; point.y = -dataAreaSize; point.z = dataAreaSize;  }  // upper top left  index 4
                case 5 -> { point.x = dataAreaSize;  point.y = -dataAreaSize; point.z = dataAreaSize - (2 * dataAreaSize * (1.0 - rightValue)); }  // upper top right
                case 6 -> { point.x = dataAreaSize;  point.y = dataAreaSize;  point.z = dataAreaSize - (2 * dataAreaSize * (1.0 - rightValue)); }  // upper bottom right
                case 7 -> { point.x = -dataAreaSize; point.y = dataAreaSize;  point.z = dataAreaSize;  }  // upper  bottom left index 7
            }
        }

        // Projection cube
        pMatrix.origin.x = width * 0.5;
        pMatrix.origin.y = height * 0.5;
        List<P3d> projectedPoints = cubePoints.stream().map(point -> pMatrix.project(point)).collect(Collectors.toList());

        // Projection left data area
        pMatrix.origin.x = width * 0.46;
        pMatrix.origin.y = height * 0.52;
        List<P3d> projectedLeftDataPoints = leftPoints.stream().map(point -> pMatrix.project(point)).collect(Collectors.toList());

        // Projection right data area
        pMatrix.origin.x = width * 0.54;
        pMatrix.origin.y = height * 0.52;
        List<P3d> projectedRightDataPoints = rightPoints.stream().map(point -> pMatrix.project(point)).collect(Collectors.toList());

        // Draw
        ctx.setLineWidth(lineWidth);
        ctx.setLineCap(StrokeLineCap.ROUND);
        ctx.setLineJoin(StrokeLineJoin.ROUND);
        ctx.setStroke(getCubeFrameColor());

        // Areas
        // top 4 5 6 7 4
        ctx.setFill(getCubeColor());
        ctx.beginPath();
        ctx.moveTo(projectedPoints.get(4).x, projectedPoints.get(4).y);
        ctx.lineTo(projectedPoints.get(5).x, projectedPoints.get(5).y);
        ctx.lineTo(projectedPoints.get(6).x, projectedPoints.get(6).y);
        ctx.lineTo(projectedPoints.get(7).x, projectedPoints.get(7).y);
        ctx.closePath();
        ctx.fill();
        // front left 2 6 7 3 2
        ctx.beginPath();
        ctx.moveTo(projectedPoints.get(2).x, projectedPoints.get(2).y);
        ctx.lineTo(projectedPoints.get(6).x, projectedPoints.get(6).y);
        ctx.lineTo(projectedPoints.get(7).x, projectedPoints.get(7).y);
        ctx.lineTo(projectedPoints.get(3).x, projectedPoints.get(3).y);
        ctx.closePath();
        ctx.fill();
        // front right 1 5 6 2 1
        ctx.beginPath();
        ctx.moveTo(projectedPoints.get(1).x, projectedPoints.get(1).y);
        ctx.lineTo(projectedPoints.get(5).x, projectedPoints.get(5).y);
        ctx.lineTo(projectedPoints.get(6).x, projectedPoints.get(6).y);
        ctx.lineTo(projectedPoints.get(2).x, projectedPoints.get(2).y);
        ctx.closePath();
        ctx.fill();

        // Stroke border
        // top 4 5 6 7 4
        ctx.strokeLine(projectedPoints.get(4).x, projectedPoints.get(4).y, projectedPoints.get(5).x, projectedPoints.get(5).y);
        ctx.strokeLine(projectedPoints.get(5).x, projectedPoints.get(5).y, projectedPoints.get(6).x, projectedPoints.get(6).y);
        ctx.strokeLine(projectedPoints.get(6).x, projectedPoints.get(6).y, projectedPoints.get(7).x, projectedPoints.get(7).y);
        ctx.strokeLine(projectedPoints.get(7).x, projectedPoints.get(7).y, projectedPoints.get(4).x, projectedPoints.get(4).y);
        // front left 2 6 7 3 2
        ctx.strokeLine(projectedPoints.get(2).x, projectedPoints.get(2).y, projectedPoints.get(6).x, projectedPoints.get(6).y);
        ctx.strokeLine(projectedPoints.get(6).x, projectedPoints.get(6).y, projectedPoints.get(7).x, projectedPoints.get(7).y);
        ctx.strokeLine(projectedPoints.get(7).x, projectedPoints.get(7).y, projectedPoints.get(3).x, projectedPoints.get(3).y);
        ctx.strokeLine(projectedPoints.get(3).x, projectedPoints.get(3).y, projectedPoints.get(2).x, projectedPoints.get(2).y);
        // front right 1 5 6 2 1
        ctx.strokeLine(projectedPoints.get(1).x, projectedPoints.get(1).y, projectedPoints.get(5).x, projectedPoints.get(5).y);
        ctx.strokeLine(projectedPoints.get(5).x, projectedPoints.get(5).y, projectedPoints.get(6).x, projectedPoints.get(6).y);
        ctx.strokeLine(projectedPoints.get(6).x, projectedPoints.get(6).y, projectedPoints.get(2).x, projectedPoints.get(2).y);
        ctx.strokeLine(projectedPoints.get(2).x, projectedPoints.get(2).y, projectedPoints.get(1).x, projectedPoints.get(1).y);

        // Data Area
        // Left data area
        // front left 2 6 7 3 2
        ctx.setFill(getLeftFill());
        ctx.beginPath();
        ctx.moveTo(projectedLeftDataPoints.get(2).x, projectedLeftDataPoints.get(2).y);
        ctx.lineTo(projectedLeftDataPoints.get(6).x, projectedLeftDataPoints.get(6).y);
        ctx.lineTo(projectedLeftDataPoints.get(7).x, projectedLeftDataPoints.get(7).y);
        ctx.lineTo(projectedLeftDataPoints.get(3).x, projectedLeftDataPoints.get(3).y);
        ctx.closePath();
        ctx.fill();


        // Right data area
        // front right 1 5 6 2 1
        ctx.setFill(getRightFill());
        ctx.beginPath();
        ctx.moveTo(projectedRightDataPoints.get(1).x, projectedRightDataPoints.get(1).y);
        ctx.lineTo(projectedRightDataPoints.get(5).x, projectedRightDataPoints.get(5).y);
        ctx.lineTo(projectedRightDataPoints.get(6).x, projectedRightDataPoints.get(6).y);
        ctx.lineTo(projectedRightDataPoints.get(2).x, projectedRightDataPoints.get(2).y);
        ctx.closePath();
        ctx.fill();

        // Text
        double ty;
        ctx.setTextBaseline(VPos.TOP);
        ctx.setTextAlign(TextAlignment.CENTER);
        // Left Text
        ctx.save();
        ctx.setFont(valueFont);
        ctx.setFill(leftValue < 0.16 ? getEmptyTextColor() : getTextColor());
        ctx.setTransform(1,0.6,0.025,1,0,0);
        ty = (leftValue < 0.29 || getLeftText().isEmpty()) ? size * 0.525 : size * 0.63 - (dataAreaSize * 2 * leftValue);
        ctx.fillText(String.format(Locale.US, "%.0f%%", leftValue * 100), size * 0.3, ty);
        if (leftValue > 0.69) {
            ctx.setFont(textFont);
            wrapText(ctx, getLeftText(), size * 0.29, ty + valueFont.getSize(), dataAreaSize * 1.7, textFont.getSize() * 1.25, 3);
        }
        ctx.restore();
        // Right Text
        ctx.save();
        ctx.setFont(valueFont);
        ctx.setFill(rightValue < 0.16 ? getEmptyTextColor() : getTextColor());
        ctx.setTransform(1,-0.6,0.025,1,0,0);
        ty = (rightValue < 0.29 || getRightText().isEmpty()) ? size * 1.1 : size * 1.205 - (dataAreaSize * 2 * rightValue);
        ctx.fillText(String.format(Locale.US, "%.0f%%", rightValue * 100), size * 0.67, ty);
        if (rightValue > 0.69) {
            ctx.setFont(textFont);
            wrapText(ctx, getRightText(), size * 0.67, ty + valueFont.getSize(), dataAreaSize * 1.7, textFont.getSize() * 1.25, 3);
        }
        ctx.restore();
    }


    // ******************** Resizing ******************************************
    private void resize() {
        double w = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double h = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = w < h ? w : h;
        width  = size;
        height = size;

        if (width > 0 && height > 0) {
            lineWidth        = size * 0.03;
            cubeSize         = size * 0.225;
            dataAreaSize     = cubeSize * 0.8;
            valueFont        = Fonts.mazzardsoftlBoldName(size * 0.12);
            textFont         = Fonts.mazzardsoftlSemiBoldName(size * 0.035);
            pMatrix.origin.x = width * 0.5;
            pMatrix.origin.y = height * 0.5;

            canvas.setWidth(size);
            canvas.setHeight(size);
            canvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            redraw();
        }
    }

    /**
     * Overrideable redraw()
     */
    public void redraw() {
        drawChart();
    }
}
