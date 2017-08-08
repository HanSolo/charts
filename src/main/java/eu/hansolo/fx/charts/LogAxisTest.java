package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.Axis.AxisType;
import eu.hansolo.fx.charts.unit.Unit;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 08.08.17
 * Time: 14:16
 */
public class LogAxisTest extends Application {
    private Axis xAxisBottom;
    private Axis xAxisTop;
    private Axis yAxisLeft;
    private Axis yAxisRight;
    private Axis logAxisX;


    @Override public void init() {
        xAxisBottom = new Axis(0, 1000, Orientation.HORIZONTAL, AxisType.LOGARITHMIC, Position.BOTTOM);
        xAxisBottom.setPrefHeight(20);
        AnchorPane.setLeftAnchor(xAxisBottom, 20d);
        AnchorPane.setRightAnchor(xAxisBottom, 20d);
        AnchorPane.setBottomAnchor(xAxisBottom, 0d);

        yAxisLeft = new Axis(0, 1000, Orientation.VERTICAL, AxisType.LOGARITHMIC, Position.LEFT);
        yAxisLeft.setPrefWidth(20);
        AnchorPane.setLeftAnchor(yAxisLeft, 0d);
        AnchorPane.setTopAnchor(yAxisLeft, 20d);
        AnchorPane.setBottomAnchor(yAxisLeft, 20d);
    }

    @Override public void start(Stage stage) {
        AnchorPane pane = new AnchorPane(xAxisBottom, yAxisLeft);
        pane.setPadding(new Insets(10));
        pane.setPrefSize(400, 400);

        Scene scene = new Scene(pane);

        stage.setTitle("Title");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
