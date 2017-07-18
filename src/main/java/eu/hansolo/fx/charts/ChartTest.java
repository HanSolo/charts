package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.model.DonutChartModel;
import eu.hansolo.fx.charts.model.XYChartModel;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by hansolo on 16.07.17.
 */
public class ChartTest extends Application {
    private static final Color[]         COLORS           = { Color.RED, Color.BLUE, Color.CYAN, Color.LIME };
    private static final Random          RND              = new Random();
    private static final int             NUMBER_OF_VALUES = 160;
    private XYChartModel<XYDataObject>   xyChartModel;
    private XYChart<XYDataObject>        xyChart;
    private DonutChartModel<YDataObject> donutChartModel;
    private DonutChart<YDataObject>      donutChart;

    private long                         lastTimerCall;
    private AnimationTimer               timer;


    @Override public void init() {
        List<XYDataObject> xyData = new ArrayList<>(NUMBER_OF_VALUES);
        List<YDataObject>  yData  = new ArrayList<>(NUMBER_OF_VALUES);
        for (int i = 0 ; i < NUMBER_OF_VALUES ; i++) {
            xyData.add(new XYDataObject(i, RND.nextDouble() * 20, "P" + i, COLORS[RND.nextInt(3)]));
            yData.add(new YDataObject(RND.nextDouble() * 10, "P" + i, COLORS[RND.nextInt(3)]));
        }

        xyChartModel = new XYChartModel(xyData);
        xyChart      = new XYChart(xyChartModel);

        donutChartModel = new DonutChartModel(yData);
        donutChart      = new DonutChart(donutChartModel);

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall) {
                    List<XYDataObject> xyItems = xyChartModel.getItems();
                    xyItems.forEach(item -> item.setY(RND.nextDouble() * 20));

                    List<YDataObject> yItems =donutChartModel.getItems();
                    yItems.forEach(item -> item.setY(RND.nextDouble() * 20));

                    xyChartModel.refresh();
                    donutChartModel.refresh();
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        HBox pane = new HBox(10, xyChart, donutChart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Charts");
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