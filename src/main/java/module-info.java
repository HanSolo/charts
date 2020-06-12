module charts {

    // Java
    requires java.base;
    requires java.logging;

    // Java-FX
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.swing;

    exports eu.hansolo.fx.geometry;
    exports eu.hansolo.fx.charts;
}