package eu.hansolo.fx.charts.event;


@FunctionalInterface
public interface ChartDataEventListener {
    void onChartDataEvent(final ChartDataEvent EVENT);
}