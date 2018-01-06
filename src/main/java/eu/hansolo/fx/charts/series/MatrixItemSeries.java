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
import eu.hansolo.fx.charts.Symbol;
import eu.hansolo.fx.charts.data.MatrixItem;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public class MatrixItemSeries<T extends MatrixItem> extends Series {

    // ******************** Constructors **************************************
    public MatrixItemSeries() {
        this(null, ChartType.MATRIX_HEATMAP, "");
    }
    public MatrixItemSeries(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "");
    }
    public MatrixItemSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME) {
        super(ITEMS, TYPE, NAME, Symbol.NONE);
    }


    // ******************** Methods *******************************************
    @Override public ObservableList<T> getItems() { return (ObservableList<T>) items; }

    public int getMinX() { return getItems().stream().min(Comparator.comparingInt(T::getX)).get().getX(); }
    public int getMaxX() { return getItems().stream().max(Comparator.comparingInt(T::getX)).get().getX(); }

    public int getMinY() { return getItems().stream().min(Comparator.comparingInt(T::getY)).get().getY(); }
    public int getMaxY() { return getItems().stream().max(Comparator.comparingInt(T::getY)).get().getY(); }

    public double getMinZ() { return getItems().stream().min(Comparator.comparingDouble(T::getZ)).get().getZ(); }
    public double getMaxZ() { return getItems().stream().max(Comparator.comparingDouble(T::getZ)).get().getZ(); }

    public int getRangeX() { return getMaxX() - getMinX(); }
    public int getRangeY() { return getMaxY() - getMinY(); }
    public double getRangeZ() { return getMaxZ() - getMinZ(); }

    public double getAt(final int X, final int Y) {
        Optional<T> selectedItem = getItems().stream().filter(item -> item.getX() == X).filter(item -> item.getY() == Y).findFirst();
        return selectedItem.isPresent() ? selectedItem.get().getZ() : 0;
    }
    public void setAt(final int X, final int Y, final double Z) {
        Optional<T> selectedItem = getItems().stream().filter(item -> item.getX() == X).filter(item -> item.getY() == Y).findFirst();
        if (selectedItem.isPresent()) { selectedItem.get().setZ(Z); }
    }
}
