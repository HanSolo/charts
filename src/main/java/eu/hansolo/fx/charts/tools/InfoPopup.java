/*
 * Copyright (c) 2017 by Gerrit Grunwald
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

package eu.hansolo.fx.charts.tools;

import eu.hansolo.fx.charts.data.BubbleGridChartItem;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.Item;
import eu.hansolo.fx.charts.event.SelectionEvent;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.LongProperty;
import javafx.beans.property.LongPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.Locale;


public class InfoPopup extends Popup {
    public  static long           MAX_TIMEOUT              = 10_000;
    public  static int            MAX_DECIMALS             = 6;
    public  static Color          DEFAULT_BACKGROUND_COLOR = Color.rgb(0, 0, 0, 0.75);
    public  static Color          DEFAULT_TEXT_COLOR       = Color.WHITE;
    private HBox                  hBox;
    private Text                  seriesText;
    private Text                  seriesSumText;
    private Text                  itemText;
    private Text                  valueText;
    private Line                  line;
    private Text                  seriesNameText;
    private Text                  seriesValueText;
    private Text                  itemNameText;
    private Text                  itemValueText;
    private FadeTransition        fadeIn;
    private FadeTransition        fadeOut;
    private PauseTransition       delay;
    private Color                 _backgroundColor;
    private ObjectProperty<Color> backgroundColor;
    private Color                 _textColor;
    private ObjectProperty<Color> textColor;
    private long                  _timeout;
    private LongProperty          timeout;
    private int                   _decimals;
    private IntegerProperty       decimals;
    private String                _unit;
    private StringProperty        unit;
    private String                formatString;


    // ******************** Constructors **************************************
    public InfoPopup() {
        super();
        _backgroundColor = DEFAULT_BACKGROUND_COLOR;
        _textColor       = DEFAULT_TEXT_COLOR;
        _timeout         = 4000;
        _decimals        = 0;
        _unit            = "";
        formatString     = new StringBuilder("%.").append(_decimals).append("f ").append(_unit).toString();
        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        setAutoFix(true);

        fadeIn = new FadeTransition(Duration.millis(200), hBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(0.75);

        fadeOut = new FadeTransition(Duration.millis(200), hBox);
        fadeOut.setFromValue(0.75);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> hide());

        delay = new PauseTransition(Duration.millis(_timeout));
        delay.setOnFinished(e -> animatedHide());
    }

    private void initGraphics() {
        Font regularFont = Fonts.latoRegular(10);
        Font lightFont   = Fonts.latoLight(10);

        seriesText = new Text("SERIES");
        seriesText.setFill(_textColor);
        seriesText.setFont(regularFont);

        seriesNameText = new Text("-");
        seriesNameText.setFill(_textColor);
        seriesNameText.setFont(lightFont);

        seriesSumText = new Text("SUM");
        seriesSumText.setFill(_textColor);
        seriesSumText.setFont(regularFont);

        seriesValueText = new Text("-");
        seriesValueText.setFill(_textColor);
        seriesValueText.setFont(lightFont);

        itemText = new Text("ITEM");
        itemText.setFill(_textColor);
        itemText.setFont(regularFont);

        itemNameText = new Text("-");
        itemNameText.setFill(_textColor);
        itemNameText.setFont(lightFont);

        valueText = new Text("VALUE");
        valueText.setFill(_textColor);
        valueText.setFont(regularFont);

        itemValueText = new Text("-");
        itemValueText.setFill(_textColor);
        itemValueText.setFont(lightFont);

        line = new Line(0, 0, 0, 56);
        line.setStroke(_textColor);

        VBox vBoxTitles = new VBox(2, seriesText, seriesSumText, itemText, valueText);
        vBoxTitles.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(itemText, new Insets(3, 0, 0, 0));

        VBox vBoxValues = new VBox(2, seriesNameText, seriesValueText, itemNameText, itemValueText);
        vBoxValues.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(itemNameText, new Insets(3, 0, 0, 0));
        HBox.setHgrow(vBoxValues, Priority.ALWAYS);

        hBox = new HBox(5, vBoxTitles, line, vBoxValues);
        hBox.setPrefSize(120, 69);
        hBox.setPadding(new Insets(5));
        hBox.setBackground(new Background(new BackgroundFill(_backgroundColor, new CornerRadii(3), Insets.EMPTY)));
        hBox.setMouseTransparent(true);

        getContent().addAll(hBox);
    }

    private void registerListeners() {

    }


    // ******************** Methods *******************************************
    public void animatedShow(final Window WINDOW) {
        show(WINDOW);
        fadeIn.play();
        delay.playFromStart();
    }
    public void animatedHide() {
        fadeOut.play();
    }

    public Color getBackgroundColor() { return null == backgroundColor ? _backgroundColor : backgroundColor.get(); }
    public void setBackgroundColor(final Color COLOR) {
        if (null == backgroundColor) {
            _backgroundColor = COLOR;
            hBox.setBackground(new Background(new BackgroundFill(_backgroundColor, new CornerRadii(3), Insets.EMPTY)));
        } else {
            backgroundColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> backgroundColorProperty() {
        if (null == backgroundColor) {
            backgroundColor = new ObjectPropertyBase<Color>(_backgroundColor) {
                @Override protected void invalidated() {
                    hBox.setBackground(new Background(new BackgroundFill(get(), new CornerRadii(3), Insets.EMPTY)));
                }
                @Override public Object getBean() { return InfoPopup.this; }
                @Override public String getName() { return "backgroundColor"; }
            };
            _backgroundColor = null;
        }
        return backgroundColor;
    }

    public Color getTextColor() { return null == textColor ? _textColor : textColor.get(); }
    public void setTextColor(final Color COLOR) {
        if (null == textColor) {
            _textColor = COLOR;
            updateTextColor(_textColor);
        } else {
            textColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> textColorProperty() {
        if (null == textColor) {
            textColor = new ObjectPropertyBase<Color>(_textColor) {
                @Override protected void invalidated() { updateTextColor(get()); }
                @Override public Object getBean() { return InfoPopup.this; }
                @Override public String getName() { return "textColor"; }
            };
            _textColor = null;
        }
        return textColor;
    }

    public long getTimeout() { return null == timeout ? _timeout : timeout.get(); }
    public void setTimeout(final long TIMEOUT) {
        if (null == timeout) {
            _timeout = Helper.clamp(0, MAX_TIMEOUT, TIMEOUT);
        } else {
            timeout.set(TIMEOUT);
        }
    }
    public LongProperty timeoutProperty() {
        if (null == timeout) {
            timeout = new LongPropertyBase(_timeout) {
                @Override protected void invalidated() { Helper.clamp(0, MAX_TIMEOUT, get()); }
                @Override public Object getBean() { return InfoPopup.this; }
                @Override public String getName() { return "timeout"; }
            };
        }
        return timeout;
    }

    public int getDecimals() { return null == decimals ? _decimals : decimals.get(); }
    public void setDecimals(final int DECIMALS) {
        if (null == decimals) {
            _decimals = Helper.clamp(0, 6, DECIMALS);
            formatString = new StringBuilder("%.").append(_decimals).append("f ").append(getUnit()).toString();
        } else {
            decimals.set(DECIMALS);
        }
    }
    public IntegerProperty decimalsProperty() {
        if (null == decimals) {
            decimals = new IntegerPropertyBase(_decimals) {
                @Override protected void invalidated() {
                    set(Helper.clamp(0, MAX_DECIMALS, get()));
                    formatString = new StringBuilder("%.").append(get()).append("f ").append(_unit).toString();
                }
                @Override public Object getBean() { return InfoPopup.this; }
                @Override public String getName() { return "decimals"; }
            };
        }
        return decimals;
    }

    public String getUnit() { return null == unit ? _unit : unit.get(); }
    public void setUnit(final String UNIT) {
        if (null == unit) {
            _unit = UNIT;
            formatString = new StringBuilder("%.").append(getDecimals()).append("f ").append(_unit).toString();
        } else {
            unit.set(UNIT);
        }
    }
    public StringProperty unitProperty() {
        if (null == unit) {
            unit = new StringPropertyBase(_unit) {
                @Override protected void invalidated() {
                    formatString = new StringBuilder("%.").append(getDecimals()).append("f ").append(get()).toString();
                }
                @Override public Object getBean() { return InfoPopup.this; }
                @Override public String getName() { return "unit"; }
            };
            _unit = null;
        }
        return unit;
    }

    public void update(final SelectionEvent EVENT) {
        ChartItemSeries series = EVENT.getSeries();
        ChartItem       item   = EVENT.getItem();
        if (null == series && null == item) {
            this.setOpacity(0);
        } else {
            this.setOpacity(1);
        }
        if (null == series) {
            Helper.enableNode(seriesText, false);
            Helper.enableNode(seriesSumText, false);
            Helper.enableNode(seriesNameText, false);
            Helper.enableNode(seriesValueText, false);
            setTo2Rows();
        } else {
            seriesNameText.setText(EVENT.getSeries().getName());
            seriesValueText.setText(String.format(Locale.US, formatString, EVENT.getSeries().getSumOfAllItems()));
            Helper.enableNode(seriesText, true);
            Helper.enableNode(seriesSumText, true);
            Helper.enableNode(seriesNameText, true);
            Helper.enableNode(seriesValueText, true);
            setTo4Rows();
        }
        if (null == item) {
            Helper.enableNode(itemText, false);
            Helper.enableNode(valueText, false);
            Helper.enableNode(itemNameText, false);
            Helper.enableNode(itemValueText, false);
            VBox.setMargin(itemNameText, new Insets(0, 0, 0, 0));
            setTo2Rows();
        } else {
            itemNameText.setText(null == EVENT.getItem() ? "-" : EVENT.getItem().getName());
            itemValueText.setText(null == EVENT.getItem() ? "-" : String.format(Locale.US, formatString, EVENT.getItem().getValue()));
            Helper.enableNode(itemText, true);
            Helper.enableNode(valueText, true);
            Helper.enableNode(itemNameText, true);
            Helper.enableNode(itemValueText, true);
            VBox.setMargin(itemNameText, new Insets(3, 0, 0, 0));
            if (null == series) {
                VBox.setMargin(itemText, new Insets(0, 0, 0, 0));
                VBox.setMargin(itemNameText, new Insets(0, 0, 0, 0));
            } else {
                VBox.setMargin(itemText, new Insets(3, 0, 0, 0));
                VBox.setMargin(itemNameText, new Insets(3, 0, 0, 0));
                setTo4Rows();
            }
        }
    }
    public void update(final Item item) {
        update(item, -1);
    }
    public void update(final Item item, final boolean useDescription) {
        update(item, -1, useDescription);
    }
    public void update(final Item item, final double sum) {
        update(item, sum, false);
    }
    public void update(final Item item, final double sum, final boolean useDescription) {
        Helper.enableNode(seriesText, false);
        Helper.enableNode(seriesSumText, false);
        Helper.enableNode(seriesNameText, false);
        Helper.enableNode(seriesValueText, false);
        setTo2Rows();

        final String text;
        final String value;

        if (item instanceof BubbleGridChartItem) {
            BubbleGridChartItem i = (BubbleGridChartItem) item;
            text  = item.getName();
            if (sum != -1) {
                value = String.join("/", String.format(Locale.US, formatString, i.getValue()), String.format(Locale.US, "%.0f%%", (i.getValue() / sum * 100)));
            } else {
                value = String.format(Locale.US, formatString, i.getValue());
            }
        } else if (item instanceof ChartItem) {
            ChartItem i = (ChartItem) item;
            text = useDescription ? i.getDescription() : item.getName();
            if (sum != -1) {
                value = String.join("/", String.format(Locale.US, formatString, i.getValue()), String.format(Locale.US, "%.0f%%", (i.getValue() / sum * 100)));
            } else {
                value = String.format(Locale.US, formatString, i.getValue());
            }
        } else {
            text  = "-";
            value = "-";
        }

        itemNameText.setText(null == text   ? "-" : text);
        itemValueText.setText(null == value ? "-" : value);
        Helper.enableNode(itemText, true);
        Helper.enableNode(valueText, true);
        Helper.enableNode(itemNameText, true);
        Helper.enableNode(itemValueText, true);
        VBox.setMargin(itemNameText, new Insets(3, 0, 0, 0));

        VBox.setMargin(itemText, new Insets(0, 0, 0, 0));
        VBox.setMargin(itemNameText, new Insets(0, 0, 0, 0));

    }

    private void setTo2Rows() {
        setHeight(38);
        line.setEndY(24);
        hBox.setPrefHeight(36);
        hBox.setMinHeight(36);
        hBox.setMaxHeight(36);
    }
    private void setTo4Rows() {
        setHeight(71);
        line.setEndY(56);
        hBox.setPrefHeight(69);
        hBox.setMinHeight(69);
        hBox.setMaxHeight(69);
    }

    private void updateTextColor(final Color COLOR) {
        seriesText.setFill(COLOR);
        seriesSumText.setFill(COLOR);
        itemText.setFill(COLOR);
        valueText.setFill(COLOR);
        line.setStroke(COLOR);
        seriesNameText.setFill(COLOR);
        seriesValueText.setFill(COLOR);
        itemNameText.setFill(COLOR);
        itemValueText.setFill(COLOR);
    }
}
