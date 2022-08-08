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

package eu.hansolo.fx.charts.tools;

import eu.hansolo.fx.charts.font.Fonts;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.property.LongProperty;
import javafx.beans.property.LongPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;


public class TooltipPopup extends Popup {
    public static long                  MAX_TIMEOUT              = 10_000;
    public static Color                 DEFAULT_BACKGROUND_COLOR = Color.rgb(0, 0, 0, 0.75);
    public static Color                 DEFAULT_TEXT_COLOR       = Color.WHITE;
    private final static String         STYLE_CLASS = "charts-tooltip";
    private       StackPane             pane;
    private       Label                 tooltipText;
    private       FadeTransition        fadeIn;
    private       FadeTransition        fadeOut;
    private       PauseTransition       delay;
    private       Color                 _backgroundColor;
    private       ObjectProperty<Color> backgroundColor;
    private       long                  _timeout;
    private       LongProperty          timeout;


    // ******************** Constructors **************************************
    public TooltipPopup() {
        this("", 4000);
    }
    public TooltipPopup(final String text) {
        this(text, 4000);
    }
    public TooltipPopup(final long timeout) {
        this("", timeout);
    }
    public TooltipPopup(final String text, final long timeout) {
        super();
        _backgroundColor = DEFAULT_BACKGROUND_COLOR;
        _timeout         = timeout;
        tooltipText      = new Label(text);
        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        setAutoFix(true);

        fadeIn = new FadeTransition(Duration.millis(200), pane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(0.75);

        fadeOut = new FadeTransition(Duration.millis(200), pane);
        fadeOut.setFromValue(0.75);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> hide());

        delay = new PauseTransition(Duration.millis(_timeout));
        delay.setOnFinished(e -> animatedHide());
    }

    private void initGraphics() {
        Font regularFont = Fonts.opensansRegular(10);
        Font lightFont   = Fonts.opensansLight(10);

        tooltipText.setTextFill(DEFAULT_TEXT_COLOR);
        tooltipText.setFont(lightFont);
        tooltipText.setMouseTransparent(true);

        pane = new StackPane(tooltipText);
        pane.setPadding(new Insets(5));
        pane.setBackground(new Background(new BackgroundFill(_backgroundColor, new CornerRadii(3), Insets.EMPTY)));
        pane.setMouseTransparent(true);
        // give users the chance to style things a bit
        pane.getStyleClass().add(STYLE_CLASS);

        getContent().addAll(pane);
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
            pane.setBackground(new Background(new BackgroundFill(_backgroundColor, new CornerRadii(3), Insets.EMPTY)));
        } else {
            backgroundColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> backgroundColorProperty() {
        if (null == backgroundColor) {
            backgroundColor = new ObjectPropertyBase<Color>(_backgroundColor) {
                @Override protected void invalidated() {
                    pane.setBackground(new Background(new BackgroundFill(get(), new CornerRadii(3), Insets.EMPTY)));
                }
                @Override public Object getBean() { return TooltipPopup.this; }
                @Override public String getName() { return "backgroundColor"; }
            };
            _backgroundColor = null;
        }
        return backgroundColor;
    }

    public Paint getTextFill() { return tooltipText.getTextFill(); }
    public void setTextFill(final Paint fill) { tooltipText.setTextFill(fill); }
    public ObjectProperty<Paint> textFillProperty() { return tooltipText.textFillProperty(); }

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
                @Override public Object getBean() { return TooltipPopup.this; }
                @Override public String getName() { return "timeout"; }
            };
        }
        return timeout;
    }

    public String getText() { return tooltipText.getText(); }
    public void setText(final String text) { tooltipText.setText(text); }
    public StringProperty textProperty() { return tooltipText.textProperty(); }
}
