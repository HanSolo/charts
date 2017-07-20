package eu.hansolo.fx.charts.model;

import eu.hansolo.fx.charts.data.YData;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.List;


public class YChartModel<T extends YData> extends ChartModel {

    // ******************** Constructors **************************************
    public YChartModel() {
        this(null, "", "");
    }
    public YChartModel(final List<T> ITEMS) {
        this(ITEMS, "", "");
    }
    public YChartModel(final List<T> ITEMS, final String TITLE, final String SUB_TITLE) {
        super(ITEMS, TITLE, SUB_TITLE);
    }


    // ******************** Methods *******************************************
    @Override public ObservableList<YData> getItems() { return items; }

    public double getMin() { return ((YData) items.stream().min(Comparator.comparingDouble(YData::getY)).get()).getY(); }
    public double getMax() { return ((YData) items.stream().max(Comparator.comparingDouble(YData::getY)).get()).getY(); }
}
