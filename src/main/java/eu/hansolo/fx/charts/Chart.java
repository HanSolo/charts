package eu.hansolo.fx.charts;

import javafx.scene.paint.Paint;


/**
 * Created by hansolo on 16.07.17.
 */
public interface Chart {

    ChartType getChartType();
    void      setChartType(ChartType chartType);

    Paint getChartBackgroundPaint();
    void  setChartBackgroundPaint(Paint paint);
}
