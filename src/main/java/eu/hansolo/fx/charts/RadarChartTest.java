package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.YDataObject;
import eu.hansolo.fx.charts.series.YSeries;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * User: hansolo
 * Date: 07.08.17
 * Time: 09:29
 */
public class RadarChartTest extends Application {
    private static final Random               RND        = new Random();
    private static final long                 INTERVAL   = 3_000_000_000l;
    private static final double               ANIM_TIME  = INTERVAL / 1_000_000;
    private static final int                  ELEMENTS   = 30;
    private static final ChartType            CHART_TYPE = ChartType.SMOOTH_RADAR_POLYGON;
    private              YSeries<YDataObject> series1;
    private              YSeries<YDataObject> series2;
    private              YSeries<YDataObject> series3;
    private              YChart<YDataObject>  chart;
    private              Timeline             timeline;
    private              long                 lastTimerCall;
    private              AnimationTimer       timer;

    @Override public void init() {
        List<YDataObject> data1 = new ArrayList<>(ELEMENTS);
        List<YDataObject> data2 = new ArrayList<>(ELEMENTS);
        List<YDataObject> data3 = new ArrayList<>(ELEMENTS);
        for (int i = 0 ; i < ELEMENTS ; i++) {
            YDataObject dataPoint;

            dataPoint = new YDataObject(RND.nextDouble() * 100, "P" + i);
            data1.add(dataPoint);

            dataPoint = new YDataObject(RND.nextDouble() * 100, "P" + i);
            data2.add(dataPoint);

            dataPoint = new YDataObject(RND.nextDouble() * 100, "P" + i);
            data3.add(dataPoint);
        }

        series1 = new YSeries(data2, CHART_TYPE, Color.TRANSPARENT, new RadialGradient(0, 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(0, 255, 255, 0.3)), new Stop(0.0, Color.rgb(0, 255, 255, 0.5)), new Stop(1.0, Color.rgb(0, 0, 255, 0.75))));
        series2 = new YSeries(data3, CHART_TYPE, Color.TRANSPARENT, new RadialGradient(0, 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(0, 255, 255, 0.3)), new Stop(0.0, Color.rgb(255, 255, 0, 0.5)), new Stop(1.0, Color.rgb(255, 0, 255, 0.75))));
        series3 = new YSeries(data1, CHART_TYPE, Color.TRANSPARENT, new RadialGradient(0, 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(255, 0, 0, 0.3)), new Stop(0.5, Color.rgb(255, 255, 0, 0.5)), new Stop(1.0, Color.rgb(0, 200, 0, 0.75))));

        chart   = new YChart(new YPane(series1, series2, series3));
        chart.setPrefSize(600, 600);

        timeline      = new Timeline();
        lastTimerCall = System.nanoTime();
        timer         = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + INTERVAL) {
                    animateData();
                    long delta = System.nanoTime() - now;
                    timeline.play();
                    lastTimerCall = now + delta;
                }
            }
        };

        registerListener();
    }

    private void registerListener() {
        timeline.currentTimeProperty().addListener(o -> chart.refresh());
    }

    @Override public void start(Stage stage) {
        StackPane pane  = new StackPane(chart);
        Scene     scene = new Scene(pane);

        stage.setTitle("RadarChart");
        stage.setScene(scene);
        stage.show();

        timer.start();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private void animateData() {
        List<KeyFrame> keyFrames = new ArrayList<>();
        animateSeries(series1, keyFrames);
        animateSeries(series2, keyFrames);
        animateSeries(series3, keyFrames);
        timeline.getKeyFrames().setAll(keyFrames);
    }

    private void animateSeries(final YSeries<YDataObject> SERIES, final List<KeyFrame> KEY_FRAMES) {
        SERIES.getItems().forEach(item -> {
            KeyValue kv0 = new KeyValue(item.yProperty(), item.getY());
            KeyValue kv1 = new KeyValue(item.yProperty(), RND.nextDouble() * 100);
            KeyFrame kf0 = new KeyFrame(Duration.ZERO, kv0);
            KeyFrame kf1 = new KeyFrame(Duration.millis(ANIM_TIME), kv1);
            KEY_FRAMES.add(kf0);
            KEY_FRAMES.add(kf1);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
