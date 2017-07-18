package eu.hansolo.fx.charts.model;

import eu.hansolo.fx.charts.XYData;

import java.util.Comparator;
import java.util.List;


/**
 * Created by hansolo on 16.07.17.
 */
public class XYChartModel<T extends XYData> extends ChartModel {


    // ******************** Constructors **************************************
    public XYChartModel() {
        this(null, "", "");
    }
    public XYChartModel(final List<T> ITEMS) {
        this(ITEMS, "", "");
    }
    public XYChartModel(final List<T> ITEMS, final String TITLE, final String SUB_TITLE) {
        super(ITEMS, TITLE, SUB_TITLE);
    }


    // ******************** Methods *******************************************
    public double getMinX() { return ((XYData) items.stream().min(Comparator.comparingDouble(XYData::getX)).get()).getX(); }
    public double getMaxX() { return ((XYData) items.stream().max(Comparator.comparingDouble(XYData::getX)).get()).getX(); }

    public double getMinY() { return ((XYData) items.stream().min(Comparator.comparingDouble(XYData::getY)).get()).getY(); }
    public double getMaxY() { return ((XYData) items.stream().max(Comparator.comparingDouble(XYData::getY)).get()).getY(); }

    public double getRangeX() { return getMaxX() - getMinX(); }
    public double getRangeY() { return getMaxY() - getMinY(); }
}
