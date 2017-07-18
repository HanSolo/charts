package eu.hansolo.fx.charts.model;

import eu.hansolo.fx.charts.data.YData;

import java.util.Comparator;
import java.util.List;


public class DonutChartModel<T extends YData> extends ChartModel {

    // ******************** Constructors **************************************
    public DonutChartModel() {
        this(null, "", "");
    }
    public DonutChartModel(final List<T> ITEMS) {
        this(ITEMS, "", "");
    }
    public DonutChartModel(final List<T> ITEMS, final String TITLE, final String SUB_TITLE) {
        super(ITEMS, TITLE, SUB_TITLE);
    }


    // ******************** Methods *******************************************
    public double getMin() { return ((YData) items.stream().min(Comparator.comparingDouble(YData::getY)).get()).getY(); }
    public double getMax() { return ((YData) items.stream().max(Comparator.comparingDouble(YData::getY)).get()).getY(); }
}
