package eu.hansolo.fx.charts.series;

import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.data.XYZData;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.List;


public class XYZSeries<T extends XYZData> extends Series {


    // ******************** Constructors **************************************
    public XYZSeries() {
        this(null, ChartType.BUBBLE, "", "");
    }
    public XYZSeries(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "", "");
    }
    public XYZSeries(final List<T> ITEMS, final ChartType TYPE, final String TITLE, final String SUB_TITLE) {
        super(ITEMS, TYPE, TITLE, SUB_TITLE);
    }


    // ******************** Methods *******************************************
    @Override public ObservableList<XYZData> getItems() { return items; }

    public double getMinX() { return ((XYZData) items.stream().min(Comparator.comparingDouble(XYZData::getX)).get()).getX(); }
    public double getMaxX() { return ((XYZData) items.stream().max(Comparator.comparingDouble(XYZData::getX)).get()).getX(); }

    public double getMinY() { return ((XYZData) items.stream().min(Comparator.comparingDouble(XYZData::getY)).get()).getY(); }
    public double getMaxY() { return ((XYZData) items.stream().max(Comparator.comparingDouble(XYZData::getY)).get()).getY(); }

    public double getMinZ() { return ((XYZData) items.stream().min(Comparator.comparingDouble(XYZData::getZ)).get()).getZ(); }
    public double getMaxZ() { return ((XYZData) items.stream().max(Comparator.comparingDouble(XYZData::getZ)).get()).getZ(); }

    public double getRangeX() { return getMaxX() - getMinX(); }
    public double getRangeY() { return getMaxY() - getMinY(); }
    public double getRangeZ() { return getMaxZ() - getMinZ(); }
}

