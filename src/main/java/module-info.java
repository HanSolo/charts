module eu.hansolo.fx.charts {

    // Java
    requires java.base;
    requires java.logging;

    // Java-FX
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.swing;

    exports eu.hansolo.fx.geometry;
    exports eu.hansolo.fx.geometry.tools;
    exports eu.hansolo.fx.geometry.transform;
    exports eu.hansolo.fx.charts;
    exports eu.hansolo.fx.charts.areaheatmap;
    exports eu.hansolo.fx.charts.color;
    exports eu.hansolo.fx.charts.converter;
    exports eu.hansolo.fx.charts.data;
    exports eu.hansolo.fx.charts.event;
    exports eu.hansolo.fx.charts.font;
    exports eu.hansolo.fx.charts.forcedirectedgraph;
    exports eu.hansolo.fx.charts.heatmap;
    exports eu.hansolo.fx.charts.pareto;
    exports eu.hansolo.fx.charts.series;
    exports eu.hansolo.fx.charts.tools;
    exports eu.hansolo.fx.charts.world;
    exports eu.hansolo.fx.charts.voronoi;
    exports eu.hansolo.fx.charts.panelbarchart;
}