package eu.hansolo.fx.charts.series;

import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.data.YData;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Comparator;
import java.util.List;


public class YSeries<T extends YData> extends Series {

    // ******************** Constructors **************************************
    public YSeries() {
        this(null, ChartType.DONUT, "", Color.BLACK, Color.TRANSPARENT);
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "", Color.BLACK, Color.TRANSPARENT);
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME) {
        this(ITEMS, TYPE, NAME, Color.BLACK, Color.TRANSPARENT);
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE, final Paint STROKE, final Paint FILL) {
        this(ITEMS, TYPE, "", STROKE, FILL);
    }
    public YSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME, final Paint STROKE, final Paint FILL) {
        super(ITEMS, TYPE, NAME, STROKE, FILL);
    }


    // ******************** Methods *******************************************
    @Override public ObservableList<YData> getItems() { return items; }

    public double getMin() { return ((YData) items.stream().min(Comparator.comparingDouble(YData::getY)).get()).getY(); }
    public double getMax() { return ((YData) items.stream().max(Comparator.comparingDouble(YData::getY)).get()).getY(); }
}
