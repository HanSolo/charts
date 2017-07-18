package eu.hansolo.fx.charts.event;

import eu.hansolo.fx.charts.model.ChartModel;


/**
 * Created by hansolo on 16.07.17.
 */
public class ChartModelEvent<T> {
    private ChartModel<T>       model;
    private ChartModelEventType type;


    // ******************** Constructors **************************************
    public ChartModelEvent(final ChartModel<T> MODEL, final ChartModelEventType TYPE) {
        model = MODEL;
        type  = TYPE;
    }


    // ******************** Methods *******************************************
    public ChartModel<T> getModel() { return model; }

    public ChartModelEventType getType() { return type; }
}
