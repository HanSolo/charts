package eu.hansolo.fx.charts.event;

import eu.hansolo.fx.charts.series.Series;


/**
 * Created by hansolo on 16.07.17.
 */
public class SeriesEvent<T> {
    private Series<T>       model;
    private SeriesEventType type;


    // ******************** Constructors **************************************
    public SeriesEvent(final Series<T> MODEL, final SeriesEventType TYPE) {
        model = MODEL;
        type  = TYPE;
    }


    // ******************** Methods *******************************************
    public Series<T> getModel() { return model; }

    public SeriesEventType getType() { return type; }
}
