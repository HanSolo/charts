package eu.hansolo.fx.charts.series;

import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.data.XYData;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Comparator;
import java.util.List;


/**
 * Created by hansolo on 16.07.17.
 */
public class XYSeries<T extends XYData> extends Series {
    private boolean _showPoints;

    // ******************** Constructors **************************************
    public XYSeries() {
        this(null, ChartType.SCATTER, "", "");
    }
    public XYSeries(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "", "");
    }
    public XYSeries(final List<T> ITEMS, final ChartType TYPE, final String TITLE, final String SUB_TITLE) {
        this(ITEMS, TYPE, TITLE, SUB_TITLE, Color.BLACK, Color.TRANSPARENT);
    }
    public XYSeries(final List<T> ITEMS, final ChartType TYPE, final String TITLE, final String SUB_TITLE, final Paint STROKE, final Paint FILL) {
        super(ITEMS, TYPE, TITLE, SUB_TITLE, STROKE, FILL);
        _showPoints = true;
    }


    // ******************** Methods *******************************************
    @Override public ObservableList<XYData> getItems() { return items; }

    public double getMinX() { return ((XYData) items.stream().min(Comparator.comparingDouble(XYData::getX)).get()).getX(); }
    public double getMaxX() { return ((XYData) items.stream().max(Comparator.comparingDouble(XYData::getX)).get()).getX(); }

    public double getMinY() { return ((XYData) items.stream().min(Comparator.comparingDouble(XYData::getY)).get()).getY(); }
    public double getMaxY() { return ((XYData) items.stream().max(Comparator.comparingDouble(XYData::getY)).get()).getY(); }

    public double getRangeX() { return getMaxX() - getMinX(); }
    public double getRangeY() { return getMaxY() - getMinY(); }

    public boolean isShowPoints() { return _showPoints; }
    public void setShowPoints(final boolean SHOW) { _showPoints = SHOW; }
}
