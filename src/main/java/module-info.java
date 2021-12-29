module eu.hansolo.fx.charts {

    // Java
    requires java.base;
    requires java.logging;

    // Java-FX
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.swing;

    // 3rd party
    requires transitive eu.hansolo.toolbox;
    requires transitive eu.hansolo.toolboxfx;
    requires transitive eu.hansolo.fx.heatmap;
    requires transitive eu.hansolo.fx.countries;

    exports eu.hansolo.fx.geometry;
    exports eu.hansolo.fx.geometry.tools;
    exports eu.hansolo.fx.geometry.transform;
    exports eu.hansolo.fx.charts;
    exports eu.hansolo.fx.charts.areaheatmap;
    exports eu.hansolo.fx.charts.color;
    exports eu.hansolo.fx.charts.converter;
    exports eu.hansolo.fx.charts.data;
    exports eu.hansolo.fx.charts.event;
    exports eu.hansolo.fx.charts.forcedirectedgraph;
    exports eu.hansolo.fx.charts.pareto;
    exports eu.hansolo.fx.charts.series;
    exports eu.hansolo.fx.charts.tools;
    exports eu.hansolo.fx.charts.world;
    exports eu.hansolo.fx.charts.voronoi;
}