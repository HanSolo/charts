/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2022 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.charts;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;


public class CubeChartTest extends Application {
    private static final Random RND = new Random();
    private CubeChart      chart1;
    private CubeChart      chart2;
    private CubeChart      chart3;
    private CubeChart      chart4;
    private long           lastTimerCall;
    private AnimationTimer timer;
    private Timeline       timeline;


    @Override public void init() {
        chart1 = CubeChartBuilder.create()
                                 .leftFill(CubeChart.RED_ORANGE_LEFT_FILL)
                                 .rightFill(CubeChart.RED_ORANGE_RIGHT_FILL)
                                 .leftText("OF CODEBASES CONTAINED OPEN SOURCE")
                                 .rightText("OF CODE IN CODEBASES WAS OPEN SOURCE")
                                 .build();
        chart2 = CubeChartBuilder.create()
                                 .leftFill(CubeChart.ORANGE_GREEN_LEFT_FILL)
                                 .rightFill(CubeChart.ORANGE_GREEN_RIGHT_FILL)
                                 .build();
        chart3 = CubeChartBuilder.create()
                                 .leftFill(CubeChart.GREEN_BLUE_LEFT_FILL)
                                 .rightFill(CubeChart.GREEN_BLUE_RIGHT_FILL)
                                 .build();
        chart4 = CubeChartBuilder.create()
                                 .leftFill(CubeChart.BLUE_PURPLE_LEFT_FILL)
                                 .rightFill(CubeChart.BLUE_PURPLE_RIGHT_FILL)
                                 .build();
        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 1_500_000_000) {
                    timeline.stop();
                    KeyValue kv1_1 = new KeyValue(chart1.leftValueProperty(), chart1.getLeftValue(), Interpolator.EASE_BOTH);
                    KeyValue kv2_1 = new KeyValue(chart1.leftValueProperty(), RND.nextDouble(), Interpolator.EASE_BOTH);
                    KeyValue kv3_1 = new KeyValue(chart1.rightValueProperty(), chart1.getRightValue(), Interpolator.EASE_BOTH);
                    KeyValue kv4_1 = new KeyValue(chart1.rightValueProperty(), RND.nextDouble(), Interpolator.EASE_BOTH);

                    KeyValue kv1_2 = new KeyValue(chart2.leftValueProperty(), chart2.getLeftValue(), Interpolator.EASE_BOTH);
                    KeyValue kv2_2 = new KeyValue(chart2.leftValueProperty(), RND.nextDouble(), Interpolator.EASE_BOTH);
                    KeyValue kv3_2 = new KeyValue(chart2.rightValueProperty(), chart2.getRightValue(), Interpolator.EASE_BOTH);
                    KeyValue kv4_2 = new KeyValue(chart2.rightValueProperty(), RND.nextDouble(), Interpolator.EASE_BOTH);

                    KeyValue kv1_3 = new KeyValue(chart3.leftValueProperty(), chart3.getLeftValue(), Interpolator.EASE_BOTH);
                    KeyValue kv2_3 = new KeyValue(chart3.leftValueProperty(), RND.nextDouble(), Interpolator.EASE_BOTH);
                    KeyValue kv3_3 = new KeyValue(chart3.rightValueProperty(), chart3.getRightValue(), Interpolator.EASE_BOTH);
                    KeyValue kv4_3 = new KeyValue(chart3.rightValueProperty(), RND.nextDouble(), Interpolator.EASE_BOTH);

                    KeyValue kv1_4 = new KeyValue(chart4.leftValueProperty(), chart4.getLeftValue(), Interpolator.EASE_BOTH);
                    KeyValue kv2_4 = new KeyValue(chart4.leftValueProperty(), RND.nextDouble(), Interpolator.EASE_BOTH);
                    KeyValue kv3_4 = new KeyValue(chart4.rightValueProperty(), chart4.getRightValue(), Interpolator.EASE_BOTH);
                    KeyValue kv4_4 = new KeyValue(chart4.rightValueProperty(), RND.nextDouble(), Interpolator.EASE_BOTH);

                    KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1_1, kv3_1, kv1_2, kv3_2, kv1_3, kv3_3, kv1_4, kv3_4);
                    KeyFrame kf2 = new KeyFrame(Duration.millis(800), kv2_1, kv4_1, kv2_2, kv4_2, kv2_3, kv4_3, kv2_4, kv4_4);
                    timeline.getKeyFrames().setAll(kf1, kf2);
                    timeline.play();
                    lastTimerCall = now;
                }
            }
        };
        timeline = new Timeline();
    }

    @Override public void start(Stage stage) {
        HBox pane = new HBox(0, chart1, chart2, chart3, chart4);
        pane.setPadding(new Insets(10));
        //pane.setBackground(new Background(new BackgroundFill(Color.rgb(100, 100, 100), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("Cube Chart");
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
