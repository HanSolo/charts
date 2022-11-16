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

package eu.hansolo.fx.charts.tools;

import eu.hansolo.fx.charts.Axis;
import eu.hansolo.fx.charts.Position;
import javafx.scene.paint.Color;


public class Marker {
    private Axis      axis;
    private double    value;
    private Color     stroke;
    private double    lineWidth;
    private String    text;
    private Color     textFill;
    private String    formatString;
    private LineStyle lineStyle;


    // ******************** Constructors **************************************
    public Marker(final Axis axis, final double value) {
        this(axis, value, Color.RED, 1, LineStyle.SOLID, "", Color.RED, "%.0f");
    }
    public Marker(final Axis axis, final double value, final Color stroke, final double lineWidth, final LineStyle lineStyle, final String text, final Color textFill, final String formatString) {
        if (null == axis) { throw new IllegalArgumentException("Given axis cannot be null"); }
        if ((Position.LEFT != axis.getPosition() && Position.BOTTOM != axis.getPosition())) { throw new IllegalArgumentException("Marker axis position has to be either LEFT or BOTTOM"); }
        this.axis         = axis;
        this.value        = Helper.clamp(axis.getMinValue(), axis.getMaxValue(), value);
        this.stroke       = null == stroke ? Color.RED : stroke;
        this.lineWidth    = Helper.clamp(1, 10, lineWidth);
        this.text         = null == text ? "" : text;
        this.textFill     = null == textFill ? Color.RED : textFill;
        this.formatString = null == formatString || formatString.isEmpty() ? "%.0f" : formatString;
        this.lineStyle    = null == lineStyle ? LineStyle.SOLID : lineStyle;
    }


    // ******************** Methods *******************************************
    public Axis getAxis() { return axis; }

    public double getValue() { return value; }

    public Color getStroke() { return stroke; }
    public void setStroke(final Color stroke) { this.stroke = null == stroke ? Color.RED : stroke; }

    public double getLineWidth() { return lineWidth; }
    public void setLineWidth(final double lineWidth) { this.lineWidth = Helper.clamp(1, 10, lineWidth); }

    public LineStyle getLineStyle() { return lineStyle; }
    public void setLineStyle(final LineStyle lineStyle) { this.lineStyle = null == lineStyle ? LineStyle.SOLID : lineStyle; }

    public String getText() { return text; }
    public void setText(final String text) { this.text = null == text ? "" : text; }

    public Color getTextFill() { return textFill; }
    public void setTextFill(final Color textFill) { this.textFill = null == textFill ? Color.RED: textFill; }

    public String getFormatString() { return formatString; }
    public void setFormatString(final String formatString) { this.formatString = null == formatString || formatString.isEmpty() ? "%.0f" : formatString; }
}
