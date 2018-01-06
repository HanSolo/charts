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

import eu.hansolo.fx.charts.series.Series;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * User: hansolo
 * Date: 05.01.18
 * Time: 21:04
 */
@DefaultProperty("children")
public class Legend extends FlowPane {
    private static final double                         PREFERRED_WIDTH  = 250;
    private static final double                         PREFERRED_HEIGHT = 250;
    private static final double                         MINIMUM_WIDTH    = 20;
    private static final double                         MINIMUM_HEIGHT   = 18;
    private static final double                         MAXIMUM_WIDTH    = 1024;
    private static final double                         MAXIMUM_HEIGHT   = 1024;
    private              double                         size;
    private              double                         width;
    private              double                         height;
    private              ObservableList<LegendItem>     legendItems;
    private              ListChangeListener<LegendItem> itemListListener;


    // ******************** Constructors **************************************
    public Legend() {
        this(new ArrayList<>());
    }
    public Legend(final LegendItem... LEGEND_ITEMS) {
        this(Arrays.asList(LEGEND_ITEMS));
    }
    public Legend(final List<LegendItem> LEGEND_ITEMS) {
        legendItems  = FXCollections.observableArrayList();
        legendItems.addAll(LEGEND_ITEMS);
        itemListListener = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(item -> getChildren().add(item));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(item -> getChildren().remove(item));
                }
            }
        };
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
        setHgap(3.168);
        setVgap(3.168);
        setOrientation(getOrientation());
        setAlignment(Pos.TOP_LEFT);
        getChildren().addAll(legendItems);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        legendItems.addListener(itemListListener);
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

    public void dispose() {
        legendItems.removeListener(itemListListener);
    }

    public ObservableList<LegendItem> getLegendItems() { return legendItems; }
    public void setLegendItems(final LegendItem... LEGEND_ITEMS) {
        setLegendItems(Arrays.asList(LEGEND_ITEMS));
    }
    public void setLegendItems(final List<LegendItem> LEGEND_ITEMS) {
        legendItems.setAll(LEGEND_ITEMS);
    }
    public void addLegendItem(final LegendItem ITEM) {
        if (!legendItems.contains(ITEM)) { legendItems.add(ITEM); }
    }
    public void removeLegendItem(final LegendItem ITEM) {
        if (legendItems.contains(ITEM)) { legendItems.remove(ITEM); }
    }

    public void createFromListOfSeries(final List<Series> SERIES) {
        legendItems.clear();
        SERIES.forEach(series -> legendItems.add(new LegendItem(series.getSymbol(), series.getName(), series.getSymbolFill(), series.getSymbolStroke())));
    }

    private double getItemHeight() {
        if (legendItems.size() == 0) return 0;
        return legendItems.get(0).getHeight();
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            setHgap(getItemHeight() * 0.22);
            setVgap(getItemHeight() * 0.22);

            redraw();
        }
    }

    private void redraw() {
    }
}
