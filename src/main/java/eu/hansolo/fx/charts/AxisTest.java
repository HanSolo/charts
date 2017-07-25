package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.unit.Unit;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 22.07.17
 * Time: 14:12
 */
public class AxisTest extends Application {
    private Axis xAxisBottom;
    private Axis xAxisTop;
    private Axis yAxisLeft;
    private Axis yAxisRight;


    @Override public void init() {
        xAxisBottom = new Axis(Orientation.HORIZONTAL, Pos.BOTTOM_CENTER);
        xAxisBottom.setPrefHeight(20);
        xAxisBottom.setMinValue(-20);
        xAxisBottom.setMaxValue(20);
        AnchorPane.setLeftAnchor(xAxisBottom, 20d);
        AnchorPane.setRightAnchor(xAxisBottom, 20d);
        AnchorPane.setBottomAnchor(xAxisBottom, 0d);

        xAxisTop = new Axis(Orientation.HORIZONTAL, Pos.TOP_CENTER);
        xAxisTop.setPrefHeight(20);
        xAxisTop.setMaxValue(100);
        AnchorPane.setLeftAnchor(xAxisTop, 20d);
        AnchorPane.setRightAnchor(xAxisTop, 20d);
        AnchorPane.setTopAnchor(xAxisTop, 0d);

        yAxisLeft = new Axis(Orientation.VERTICAL, Pos.CENTER_LEFT);
        yAxisLeft.setPrefWidth(20);
        yAxisLeft.setMinValue(-20);
        yAxisLeft.setMaxValue(20);
        AnchorPane.setLeftAnchor(yAxisLeft, 0d);
        AnchorPane.setTopAnchor(yAxisLeft, 20d);
        AnchorPane.setBottomAnchor(yAxisLeft, 20d);

        Unit tempUnit = new Unit(Unit.Type.TEMPERATURE, Unit.Definition.CELSIUS); // Type Temperature with BaseUnit Celsius
        double tempFahrenheitMin = tempUnit.convert(-20, Unit.Definition.FAHRENHEIT);
        double tempFahrenheitMax = tempUnit.convert(20, Unit.Definition.FAHRENHEIT);

        yAxisRight = new Axis(Orientation.VERTICAL, Pos.CENTER_RIGHT);
        yAxisRight.setPrefWidth(20);
        yAxisRight.setAutoScale(false);
        yAxisRight.setMinValue(tempFahrenheitMin);
        yAxisRight.setMaxValue(tempFahrenheitMax);
        AnchorPane.setRightAnchor(yAxisRight, 0d);
        AnchorPane.setTopAnchor(yAxisRight, 20d);
        AnchorPane.setBottomAnchor(yAxisRight, 20d);
    }

    @Override public void start(Stage stage) {
        AnchorPane pane = new AnchorPane(xAxisBottom, xAxisTop, yAxisLeft, yAxisRight);
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
