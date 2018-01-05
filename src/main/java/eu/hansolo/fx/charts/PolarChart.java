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

import eu.hansolo.fx.charts.data.XYItem;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.util.Arrays;
import java.util.List;


public class PolarChart<T extends XYItem> extends Region {
    private static final double         PREFERRED_WIDTH  = 400;
    private static final double         PREFERRED_HEIGHT = 400;
    private static final double         MINIMUM_WIDTH    = 50;
    private static final double         MINIMUM_HEIGHT   = 50;
    private static final double         MAXIMUM_WIDTH    = 4096;
    private static final double         MAXIMUM_HEIGHT   = 4096;
    private              double         size;
    private              double         width;
    private              double         height;
    private              XYPane<T>      xyPane;
    private              String         _title;
    private              StringProperty title;
    private              String         _subTitle;
    private              StringProperty subTitle;
    private              AnchorPane     pane;


    // ******************** Constructors **************************************
    public PolarChart(final XYPane<T> XY_PANE) {
        if (null == XY_PANE) { throw new IllegalArgumentException("XYPane has not to be null"); }
        if (!XY_PANE.containsPolarChart()) { throw new IllegalArgumentException("No Polar chart in XYPane"); }
        xyPane = XY_PANE;
        width  = PREFERRED_WIDTH;
        height = PREFERRED_HEIGHT;
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

        pane = new AnchorPane(xyPane);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public String getTitle() { return null == title ? _title : title.get(); }
    public void setTitle(final String TITLE) {
        if (null == title) {
            _title = TITLE;
            xyPane.redraw();
        } else {
            title.set(TITLE);
        }
    }
    public StringProperty titleProperty() {
        if (null == title) {
            title = new StringPropertyBase(_title) {
                @Override protected void invalidated() { xyPane.redraw(); }
                @Override public Object getBean() { return PolarChart.this; }
                @Override public String getName() { return "title"; }
            };
            _title = null;
        }
        return title;
    }

    public String getSubTitle() { return null == subTitle ? _subTitle : subTitle.get(); }
    public void setSubTitle(final String SUB_TITLE) {
        if (null == subTitle) {
            _subTitle = SUB_TITLE;
            xyPane.redraw();
        } else {
            subTitle.set(SUB_TITLE);
        }
    }
    public StringProperty subTitleProperty() {
        if (null == subTitle) {
            subTitle = new StringPropertyBase(_subTitle) {
                @Override protected void invalidated() { xyPane.redraw(); }
                @Override public Object getBean() { return PolarChart.this; }
                @Override public String getName() { return "subTitle"; }
            };
            _subTitle = null;
        }
        return subTitle;
    }

    public XYPane<T> getXYPane() { return xyPane; }

    public void refresh() { xyPane.redraw(); }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);
            xyPane.setMaxSize(size, size);
            xyPane.setPrefSize(size, size);
        }
    }
}
