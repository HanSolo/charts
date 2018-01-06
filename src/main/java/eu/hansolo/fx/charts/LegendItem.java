/*
 * Copyright (c) 2018 by Gerrit Grunwald
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

package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.CtxDimension;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


/**
 * User: hansolo
 * Date: 05.01.18
 * Time: 20:33
 */
@DefaultProperty("children")
public class LegendItem extends Region {
    private static final double                 PREFERRED_WIDTH  = 100;
    private static final double                 PREFERRED_HEIGHT = 18;
    private static final double                 MINIMUM_WIDTH    = 20;
    private static final double                 MINIMUM_HEIGHT   = 8;
    private static final double                 MAXIMUM_WIDTH    = 1024;
    private static final double                 MAXIMUM_HEIGHT   = 1024;
    private              double                 size;
    private              double                 width;
    private              double                 height;
    private              Symbol                 _symbol;
    private              ObjectProperty<Symbol> symbol;
    private              String                 _text;
    private              StringProperty         text;
    private              Color                  _symbolFill;
    private              ObjectProperty<Color>  symbolFill;
    private              Color                  _symbolStroke;
    private              ObjectProperty<Color>  symbolStroke;
    private              Color                  _textColor;
    private              ObjectProperty<Color>  textColor;
    private              double                 symbolSize;
    private              Canvas                 canvas;
    private              GraphicsContext        ctx;
    private              Font                   font;
    private              CtxDimension           textDim;
    private              Pane                   pane;


    // ******************** Constructors **************************************
    public LegendItem(final String TEXT, final Color SYMBOL_COLOR) {
        this(Symbol.CIRCLE, TEXT, SYMBOL_COLOR, Color.WHITE, Color.BLACK);
    }
    public LegendItem(final Symbol SYMBOL, final String TEXT, final Color SYMBOL_FILL) {
        this(SYMBOL, TEXT, SYMBOL_FILL, Color.WHITE, Color.BLACK);
    }
    public LegendItem(final Symbol SYMBOL, final String TEXT, final Color SYMBOL_FILL, final Color SYMBOL_STROKE) {
        this(SYMBOL, TEXT, SYMBOL_FILL, SYMBOL_STROKE, Color.BLACK);
    }
    public LegendItem(final Symbol SYMBOL, final String TEXT, final Color SYMBOL_FILL, final Color SYMBOL_STROKE, final Color TEXT_COLOR) {
        _symbol       = SYMBOL;
        _text         = TEXT;
        _symbolFill   = SYMBOL_FILL;
        _symbolStroke = SYMBOL_STROKE;
        _textColor    = TEXT_COLOR;
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

        canvas = new Canvas(PREFERRED_HEIGHT, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();
        ctx.setTextAlign(TextAlignment.LEFT);
        ctx.setTextBaseline(VPos.CENTER);

        pane   = new Pane(canvas);

        getChildren().setAll(pane);
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

    private void handleControlPropertyChanged(final String PROPERTY) {
        if ("".equals(PROPERTY)) {

        }
    }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public Symbol getSymbol() { return null == symbol ? _symbol : symbol.get(); }
    public void setSymbol(final Symbol SYMBOL) {
        if (null == symbol) {
            _symbol = SYMBOL;
            redraw();
        } else {
            symbol.set(SYMBOL);
        }
    }
    public ObjectProperty<Symbol> symbolProperty() {
        if (null == symbol) {
            symbol = new ObjectPropertyBase<Symbol>(_symbol) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return LegendItem.this; }
                @Override public String getName() { return "symbol"; }
            };
            _symbol = null;
        }
        return symbol;
    }

    public String getText() { return null == text ? _text : text.get(); }
    public void setText(final String TEXT) {
        if (null == text) {
            _text = TEXT;
            resize();
        } else {
            text.set(TEXT);
        }
    }
    public StringProperty textProperty() {
        if (null == text) {
            text = new StringPropertyBase(_text) {
                @Override protected void invalidated() { resize(); }
                @Override public Object getBean() { return LegendItem.this; }
                @Override public String getName() { return "text"; }
            };
            _text = null;
        }
        return text;
    }

    public Color getSymbolFill() { return null == symbolFill ? _symbolFill : symbolFill.get(); }
    public void setSymbolFill(final Color COLOR) {
        if (null == symbolFill) {
            _symbolFill = COLOR;
            redraw();
        } else {
            symbolFill.set(COLOR);
        }
    }
    public ObjectProperty<Color> symbolFillProperty() {
        if (null == symbolFill) {
            symbolFill = new ObjectPropertyBase<Color>(_symbolFill) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return LegendItem.this; }
                @Override public String getName() { return "symbolFill"; }
            };
            _symbolFill = null;
        }
        return symbolFill;
    }

    public Color getSymbolStroke() { return null == symbolStroke ? _symbolStroke : symbolStroke.get(); }
    public void setSymbolStroke(final Color COLOR) {
        if (null == symbolStroke) {
            _symbolStroke = COLOR;
            redraw();
        } else {
            symbolStroke.set(COLOR);
        }
    }
    public ObjectProperty<Color> symbolStrokeProperty() {
        if (null == symbolStroke) {
            symbolStroke = new ObjectPropertyBase<Color>(_symbolStroke) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return LegendItem.this; }
                @Override public String getName() { return "symbolStroke"; }
            };
            _symbolStroke = null;
        }
        return symbolStroke;
    }

    public Color getTextColor() { return null == textColor ? _textColor : textColor.get(); }
    public void setTextColor(final Color COLOR) {
        if (null == textColor) {
            _textColor = COLOR;
            redraw();
        } else {
            textColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> textColorProperty() {
        if (null == textColor) {
            textColor = new ObjectPropertyBase<Color>(_textColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return LegendItem.this; }
                @Override public String getName() { return "textColor"; }
            };
            _textColor = null;
        }
        return textColor;
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            font    = Fonts.latoRegular(size * 0.8);
            textDim = Helper.getTextDimension(getText(), font);
            double requiredWidth = height + height * 0.22 + textDim.getWidth();
            pane.setMaxSize(requiredWidth, height);
            pane.setPrefSize(requiredWidth, height);
            pane.relocate((getWidth() - requiredWidth) * 0.5, (getHeight() - height) * 0.5);

            canvas.setWidth(requiredWidth);
            canvas.setHeight(height);

            symbolSize = height * 0.8;

            redraw();

            setMaxWidth(requiredWidth);
        }
    }

    private void redraw() {
        ctx.clearRect(0, 0, width, height);

        drawSymbol(height * 0.5, height * 0.5, getSymbolFill(), getSymbolStroke(), getSymbol());

        ctx.setFill(getTextColor());
        ctx.setFont(font);
        ctx.fillText(getText(), height + height * 0.22, height * 0.5);
    }

    private void drawSymbol(final double X, final double Y, final Paint FILL, final Paint STROKE, final Symbol SYMBOL) {
        double halfSymbolSize = symbolSize * 0.5;
        ctx.save();
        switch(SYMBOL) {
            case NONE:
                break;
            case SQUARE:
                ctx.setStroke(STROKE);
                ctx.setFill(FILL);
                ctx.fillRect(X - halfSymbolSize, Y - halfSymbolSize, symbolSize, symbolSize);
                ctx.strokeRect(X - halfSymbolSize, Y - halfSymbolSize, symbolSize, symbolSize);
                break;
            case TRIANGLE:
                ctx.setStroke(STROKE);
                ctx.setFill(FILL);
                ctx.beginPath();
                ctx.moveTo(X, Y - halfSymbolSize);
                ctx.lineTo(X + halfSymbolSize, Y + halfSymbolSize);
                ctx.lineTo(X - halfSymbolSize, Y + halfSymbolSize);
                ctx.lineTo(X, Y - halfSymbolSize);
                ctx.closePath();
                ctx.fill();
                ctx.stroke();
                break;
            case STAR:
                ctx.setStroke(STROKE);
                ctx.setFill(null);
                ctx.strokeLine(X - halfSymbolSize, Y, X + halfSymbolSize, Y);
                ctx.strokeLine(X, Y - halfSymbolSize, X, Y + halfSymbolSize);
                ctx.strokeLine(X - halfSymbolSize, Y - halfSymbolSize, X + halfSymbolSize, Y + halfSymbolSize);
                ctx.strokeLine(X + halfSymbolSize, Y - halfSymbolSize, X - halfSymbolSize, Y + halfSymbolSize);
                break;
            case CROSS:
                ctx.setStroke(STROKE);
                ctx.setFill(null);
                ctx.strokeLine(X - halfSymbolSize, Y, X + halfSymbolSize, Y);
                ctx.strokeLine(X, Y - halfSymbolSize, X, Y + halfSymbolSize);
                break;
            case CIRCLE:
            default    :
                ctx.setStroke(STROKE);
                ctx.setFill(FILL);
                ctx.fillOval(X - halfSymbolSize, Y - halfSymbolSize, symbolSize, symbolSize);
                ctx.strokeOval(X - halfSymbolSize, Y - halfSymbolSize, symbolSize, symbolSize);
                break;
        }
        ctx.restore();
    }
}
