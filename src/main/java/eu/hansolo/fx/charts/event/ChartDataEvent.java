package eu.hansolo.fx.charts.event;


import eu.hansolo.fx.charts.data.ChartData;


public class ChartDataEvent {
    public enum EventType { UPDATE, FINISHED }

    private ChartData data;
    private EventType type;


    // ******************** Constructors **************************************
    public ChartDataEvent(final EventType TYPE, final ChartData DATA) {
        type = TYPE;
        data = DATA;
    }


    // ******************** Methods *******************************************
    public EventType getType() { return type; }

    public ChartData getData() { return data; }
}
