package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.model.XYChartModel;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * Created by hansolo on 16.07.17.
 */
public class XYChartTest extends Application {
    private static final Random RND              = new Random();
    private static final int    NUMBER_OF_VALUES = 16000;
    private XYChartModel<DataObject> chartModel;
    private XYChart<DataObject>      chart;
    private long                     lastTimerCall;
    private AnimationTimer           timer;


    @Override public void init() {
        List<DataObject> data = new ArrayList<>(NUMBER_OF_VALUES);
        for (int i = 0 ; i < NUMBER_OF_VALUES ; i++) {
            data.add(new DataObject(i, RND.nextDouble() * 20, "P" + i, Color.RED));
        }

        chartModel = new XYChartModel(data);
        chart      = new XYChart(chartModel);

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall) {
                    List<DataObject> items = chartModel.getItems();
                    items.forEach(item -> item.setY(RND.nextDouble() * 20));
                    chartModel.refresh();
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(chart);

        Scene scene = new Scene(pane);

        stage.setTitle("Title");
        stage.setScene(scene);
        stage.show();

        timer.start();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}