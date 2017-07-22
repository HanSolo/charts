package eu.hansolo.fx.charts;


import javafx.scene.paint.Color;


/**
 * Created by hansolo on 16.07.17.
 */
public interface Chart {

    ChartType getChartType();
    void      setChartType(ChartType chartType);

    Color getChartBackgroundColor();
    void  setChartBackgroundColor(Color color);
}
