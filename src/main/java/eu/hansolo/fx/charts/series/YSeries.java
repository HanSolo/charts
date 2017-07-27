package eu.hansolo.fx.charts.series;

import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.data.YData;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.List;


public class YSeries<T extends YData> extends Series {

    // ******************** Constructors **************************************
    public YSeries() {
        this(null, ChartType.DONUT, "", "");
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "", "");
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE, final String TITLE, final String SUB_TITLE) {
        super(ITEMS, TYPE, TITLE, SUB_TITLE);
    }


    // ******************** Methods *******************************************
    @Override public ObservableList<YData> getItems() { return items; }

    public double getMin() { return ((YData) items.stream().min(Comparator.comparingDouble(YData::getY)).get()).getY(); }
    public double getMax() { return ((YData) items.stream().max(Comparator.comparingDouble(YData::getY)).get()).getY(); }
}
