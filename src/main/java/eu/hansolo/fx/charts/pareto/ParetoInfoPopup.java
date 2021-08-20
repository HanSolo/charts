/*
 * Copyright (c) 2019 by Gerrit Grunwald
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

package eu.hansolo.fx.charts.pareto;

import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.tools.Helper;
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
import javafx.scene.Node;
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


/**
 * authors: Michael L\u00E4uchli, MLaeuchli (github)
 *          Stefan Mettler, orizion (github)
 */
public class ParetoInfoPopup extends Popup {
    public  static long                  MAX_TIMEOUT              = 10_000;
    public  static int                   MAX_DECIMALS             = 6;
    public  static Color                 DEFAULT_BACKGROUND_COLOR = Color.rgb(0, 0, 0, 0.75);
    public  static Color                 DEFAULT_TEXT_COLOR       = Color.WHITE;
    private        HBox                  hBox;
    private        Text                  itemText;
    private        Text                  valueText;
    private        Line                  line;
    private        Text                  itemNameText;
    private        Text                  itemValueText;
    private        FadeTransition        fadeIn;
    private        FadeTransition        fadeOut;
    private        PauseTransition       delay;
    private        Color                 _backgroundColor;
    private        ObjectProperty<Color> backgroundColor;
    private        Color                 _textColor;
    private        ObjectProperty<Color> textColor;
    private        long                  _timeout;
    private        LongProperty          timeout;
    private        int                   _decimals;
    private        IntegerProperty       decimals;
    private        String                _unit;
    private        StringProperty        unit;
    private        String                formatString;
    private        Font                  regularFont;
    private        Font                  lightFont;
    private        VBox                  vBoxTitles;
    private        VBox                  vBoxValues;
    private        int                   rowCount;


    // ******************** Constructors **************************************
    public ParetoInfoPopup() {
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
        rowCount = 2;

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
        regularFont = Fonts.opensansRegular(9);
        lightFont   = Fonts.opensansLight(9);

        itemText = new Text("NAME");
        itemText.setFill(_textColor);
        itemText.setFont(regularFont);

        itemNameText = new Text("-");
        itemNameText.setFill(_textColor);
        itemNameText.setFont(regularFont);

        line = new Line(0, 0, 0, 56);
        line.setStroke(_textColor);

        vBoxTitles = new VBox(2, itemText);
        vBoxTitles.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(itemText, new Insets(3, 0, 0, 0));

        vBoxValues = new VBox(2, itemNameText);

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

    private void addLine(String title, String value) {
        rowCount++;
        Text titleText = new Text(title);
        titleText.setFill(_textColor);
        titleText.setFont(regularFont);

        Text valueText = new Text(value);
        valueText.setFill(_textColor);
        valueText.setFont(regularFont);

        vBoxTitles.getChildren().add(titleText);
        vBoxValues.getChildren().add(valueText);

        Helper.enableNode(titleText, true);
        Helper.enableNode(valueText, true);
    }

    public void clearPopup() {
        vBoxTitles.getChildren().clear();
        vBoxValues.getChildren().clear();
        rowCount = 0;
    }

    private void registerListeners() {

    }

    private void setToRows() {
        setHeight(rowCount*19);
        line.setEndY(rowCount*18 - 12);
        hBox.setPrefHeight(rowCount*18);
        hBox.setMinHeight(rowCount*18);
        hBox.setMaxHeight(rowCount*18);
    }

    private void updateTextColor(final Color COLOR) {
        itemText.setFill(COLOR);
        valueText.setFill(COLOR);
        line.setStroke(COLOR);
        itemNameText.setFill(COLOR);
        itemValueText.setFill(COLOR);
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
                @Override public Object getBean() { return ParetoInfoPopup.this; }
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
                @Override public Object getBean() { return ParetoInfoPopup.this; }
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
                @Override public Object getBean() { return ParetoInfoPopup.this; }
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
                @Override public Object getBean() { return ParetoInfoPopup.this; }
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
                @Override public Object getBean() { return ParetoInfoPopup.this; }
                @Override public String getName() { return "unit"; }
            };
            _unit = null;
        }
        return unit;
    }

    /**
     * Updates the displayed text according to the Value of the GraphNode passed
     */
    public void update(String names[], String values[]) {
        if (names.length ==0) {
            this.setOpacity(0);
        } else {
            clearPopup();
            for(int i =0; i < names.length; i++) {
                addLine(names[i],values[i]);
            }
            setToRows();

            int i=0;

            for(Node node: vBoxTitles.getChildren() ) {
                if(i < names.length) {
                    ((Text)node).setText(names[i]);
                    i++;
                }
            }
            i=0;
            for(Node node: vBoxValues.getChildren() ) {
                if(i < values.length) {
                    ((Text)node).setText(values[i]);
                    i++;
                }
            }

            /*Helper.enableNode(itemText, true);
            Helper.enableNode(valueText, true);
            Helper.enableNode(itemNameText, true);
            Helper.enableNode(itemValueText, true);$
                    */
           /* VBox.setMargin(itemNameText, new Insets(3, 0, 0, 0));
            VBox.setMargin(itemText, new Insets(0, 0, 0, 0));
            VBox.setMargin(itemNameText, new Insets(0, 0, 0, 0));*/
            this.setOpacity(1);
        }
    }

    public void update(String name, String value) {
        String[] names = new String[1];
        names[0] = name;
        String[] values = new String[1];
        values[0] = value;
        update(names, values);
    }
}
