package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.unit.Converter;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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
        xAxisBottom = new Axis(-20, 20, Orientation.HORIZONTAL, Position.BOTTOM);
        xAxisBottom.setPrefHeight(20);
        AnchorPane.setLeftAnchor(xAxisBottom, 20d);
        AnchorPane.setRightAnchor(xAxisBottom, 20d);
        AnchorPane.setBottomAnchor(xAxisBottom, 0d);

        xAxisTop = new Axis(0, 100, Orientation.HORIZONTAL, Position.TOP);
        xAxisTop.setPrefHeight(20);
        AnchorPane.setLeftAnchor(xAxisTop, 20d);
        AnchorPane.setRightAnchor(xAxisTop, 20d);
        AnchorPane.setTopAnchor(xAxisTop, 0d);

        yAxisLeft = new Axis(-20, 20, Orientation.VERTICAL, Position.LEFT);
        yAxisLeft.setPrefWidth(20);
        AnchorPane.setLeftAnchor(yAxisLeft, 0d);
        AnchorPane.setTopAnchor(yAxisLeft, 20d);
        AnchorPane.setBottomAnchor(yAxisLeft, 20d);

        Converter tempConverter     = new Converter(Converter.Type.TEMPERATURE, Converter.Definition.CELSIUS); // Type Temperature with BaseUnit Celsius
        double    tempFahrenheitMin = tempConverter.convert(-20, Converter.Definition.FAHRENHEIT);
        double    tempFahrenheitMax = tempConverter.convert(20, Converter.Definition.FAHRENHEIT);

        yAxisRight = new Axis(tempFahrenheitMin, tempFahrenheitMax, Orientation.VERTICAL, Position.RIGHT);
        yAxisRight.setPrefWidth(20);
        yAxisRight.setAutoScale(false);
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
