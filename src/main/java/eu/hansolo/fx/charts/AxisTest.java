package eu.hansolo.fx.charts;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 22.07.17
 * Time: 14:12
 */
public class AxisTest extends Application {
    private Axis xAxis;
    private Axis yAxis;

    @Override public void init() {
        xAxis = new Axis(Orientation.HORIZONTAL, Pos.BOTTOM_CENTER);
        xAxis.setPrefHeight(20);
        AnchorPane.setLeftAnchor(xAxis, 20d);
        AnchorPane.setRightAnchor(xAxis, 20d);
        AnchorPane.setBottomAnchor(xAxis, 0d);

        yAxis = new Axis(Orientation.VERTICAL, Pos.CENTER_LEFT);
        yAxis.setPrefWidth(20);
        AnchorPane.setLeftAnchor(yAxis, 0d);
        AnchorPane.setTopAnchor(yAxis, 20d);
        AnchorPane.setBottomAnchor(yAxis, 20d);
    }

    @Override public void start(Stage stage) {
        AnchorPane pane = new AnchorPane(xAxis, yAxis);
        pane.setPrefSize(400, 400);
        pane.setPadding(new Insets(10));

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
