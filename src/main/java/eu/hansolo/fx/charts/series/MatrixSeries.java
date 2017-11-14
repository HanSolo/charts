package eu.hansolo.fx.charts.series;

import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.data.MatrixData;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public class MatrixSeries<T extends MatrixData> extends Series {


    // ******************** Constructors **************************************
    public MatrixSeries() {
        this(null, ChartType.MATRIX_HEATMAP, "");
    }
    public MatrixSeries(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "");
    }
    public MatrixSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME) {
        super(ITEMS, TYPE, NAME);
    }


    // ******************** Methods *******************************************
    @Override public ObservableList<T> getItems() { return (ObservableList<T>) items; }

    public int getMinX() { return ((MatrixData) items.stream().min(Comparator.comparingInt(MatrixData::getX)).get()).getX(); }
    public int getMaxX() { return ((MatrixData) items.stream().max(Comparator.comparingInt(MatrixData::getX)).get()).getX(); }

    public int getMinY() { return ((MatrixData) items.stream().min(Comparator.comparingInt(MatrixData::getY)).get()).getY(); }
    public int getMaxY() { return ((MatrixData) items.stream().max(Comparator.comparingInt(MatrixData::getY)).get()).getY(); }

    public double getMinZ() { return ((MatrixData) items.stream().min(Comparator.comparingDouble(MatrixData::getZ)).get()).getZ(); }
    public double getMaxZ() { return ((MatrixData) items.stream().max(Comparator.comparingDouble(MatrixData::getZ)).get()).getZ(); }

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
