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

package eu.hansolo.fx.charts.series;

import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.data.XYItem;
import eu.hansolo.fx.charts.data.XYChartItem;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Comparator;
import java.util.List;


/**
 * Created by hansolo on 16.07.17.
 */
public class XYSeries<T extends XYItem> extends Series {
    private boolean _showPoints;

    // ******************** Constructors **************************************
    public XYSeries() {
        this(null, ChartType.SCATTER, "", Color.BLACK, Color.TRANSPARENT, true);
    }
    public XYSeries(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "", Color.BLACK, Color.TRANSPARENT, true);
    }
    public XYSeries(final List<T> ITEMS, final ChartType TYPE, final boolean SHOW_POINTS) {
        this(ITEMS, TYPE, "", Color.BLACK, Color.TRANSPARENT, SHOW_POINTS);
    }
    public XYSeries(final List<T> ITEMS, final ChartType TYPE, final Paint STROKE) {
        this(ITEMS, TYPE, "", STROKE, Color.TRANSPARENT, true);
    }
    public XYSeries(final List<T> ITEMS, final ChartType TYPE, final Paint STROKE, final Paint FILL) {
        this(ITEMS, TYPE, "", STROKE, FILL, true);
    }
    public XYSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME) {
        this(ITEMS, TYPE, NAME, Color.BLACK, Color.TRANSPARENT, true);
    }
    public XYSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME, final Paint STROKE, final Paint FILL, final boolean SHOW_POINTS) {
        super(ITEMS, TYPE, NAME, STROKE, FILL);
        _showPoints = SHOW_POINTS;
    }


    // ******************** Methods *******************************************
    @Override public ObservableList<T> getItems() { return items; }

    public double getMinX() { return getItems().stream().min(Comparator.comparingDouble(T::getX)).get().getX(); }
    public double getMaxX() { return getItems().stream().max(Comparator.comparingDouble(T::getX)).get().getX(); }

    public double getMinY() { return getItems().stream().min(Comparator.comparingDouble(T::getY)).get().getY(); }
    public double getMaxY() { return getItems().stream().max(Comparator.comparingDouble(T::getY)).get().getY(); }

    public double getRangeX() { return getMaxX() - getMinX(); }
    public double getRangeY() { return getMaxY() - getMinY(); }

    public double getSumOfXValues() { return getItems().stream().mapToDouble(T::getX).sum(); }
    public double getSumOfYValues() { return getItems().stream().mapToDouble(T::getY).sum(); }

    public boolean isShowPoints() { return _showPoints; }
    public void setShowPoints(final boolean SHOW) { _showPoints = SHOW; }

    @Override public void setPointColor(final Color COLOR) {
        if (getItems().isEmpty()) { return; }
        getItems().forEach(item -> ((XYChartItem) item).setFill(COLOR));
    }
}
