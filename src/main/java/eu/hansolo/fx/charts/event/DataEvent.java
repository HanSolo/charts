package eu.hansolo.fx.charts.event;

import eu.hansolo.fx.charts.data.Data;


public class DataEvent<T extends Data> {
    private T data;


    // ******************** Constructors **************************************
    public DataEvent(final T DATA) {
        data = DATA;
    }


    // ******************** Methods *******************************************
    public T getData() { return data; }
}
