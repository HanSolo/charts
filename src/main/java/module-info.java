module eu.hansolo.fx.charts {

    // Java
    requires java.base;
    requires java.logging;

    // Java-FX
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.swing;

    // 3rd party
    requires ch.qos.logback.classic;
    requires org.slf4j;
    requires transitive eu.hansolo.toolbox;
    requires transitive eu.hansolo.toolboxfx;
    requires transitive eu.hansolo.fx.heatmap;
    requires transitive eu.hansolo.fx.countries;

    opens eu.hansolo.fx.geometry to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.geometry.tools to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.geometry.transform to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.charts to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.charts.areaheatmap to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.charts.color to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.charts.data to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.charts.event to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.charts.forcedirectedgraph to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.charts.pareto to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.charts.series to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.charts.tools to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.charts.world to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.charts.voronoi to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;
    opens eu.hansolo.fx.charts.wafermap to eu.hansolo.toolbox, eu.hansolo.toolboxfx, eu.hansolo.fx.heatmap, eu.hansolo.fx.countries;

    exports eu.hansolo.fx.geometry;
    exports eu.hansolo.fx.geometry.tools;
    exports eu.hansolo.fx.geometry.transform;
    exports eu.hansolo.fx.charts;
    exports eu.hansolo.fx.charts.areaheatmap;
    exports eu.hansolo.fx.charts.color;
    exports eu.hansolo.fx.charts.data;
    exports eu.hansolo.fx.charts.event;
    exports eu.hansolo.fx.charts.forcedirectedgraph;
    exports eu.hansolo.fx.charts.pareto;
    exports eu.hansolo.fx.charts.series;
    exports eu.hansolo.fx.charts.tools;
    exports eu.hansolo.fx.charts.world;
    exports eu.hansolo.fx.charts.voronoi;
    exports eu.hansolo.fx.charts.wafermap;

}
