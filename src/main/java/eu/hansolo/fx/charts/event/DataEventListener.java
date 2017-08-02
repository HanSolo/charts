package eu.hansolo.fx.charts.event;

@FunctionalInterface
public interface DataEventListener {
    void onDataEvent(final DataEvent EVENT);
}