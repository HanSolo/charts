/*
 * Copyright (c) 2017 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.PixelMatrix.PixelShape;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import java.util.Random;


/**
 * User: hansolo
 * Date: 07.12.17
 * Time: 14:51
 */
public class MatrixPlotTest extends Application {
    private static final int            COLS        = 108;
    private static final int            ROWS        = 40;
    private static final Random         RND         = new Random();
    private static final int            OFF         = PixelMatrix.convertToInt(Color.TRANSPARENT);
    private static final int            DARK_RED    = PixelMatrix.convertToInt(Color.web("#D04625"));
    private static final int            RED         = PixelMatrix.convertToInt(Color.web("#F3522C"));
    private static final int            ORANGE      = PixelMatrix.convertToInt(Color.web("#FCA300"));
    private static final int            YELLOW      = PixelMatrix.convertToInt(Color.web("#FFD824"));
    private static final int            BEIGE       = PixelMatrix.convertToInt(Color.web("#EFE3BC"));
    private static final int            LIGHT_GREEN = PixelMatrix.convertToInt(Color.web("#C5E3B9"));
    private static final int            GREEN       = PixelMatrix.convertToInt(Color.web("#A1D490"));
    private static final int            DARK_GREEN  = PixelMatrix.convertToInt(Color.web("#62BD4A"));
    private static final int            BLUE        = PixelMatrix.convertToInt(Color.web("#0197DE"));
    private              PixelMatrix    pixelMatrix;
    private              int[]          values;
    private              int            counter;
    private              long           lastTimerCall;
    private              AnimationTimer timer;


    @Override public void init() {
        pixelMatrix = PixelMatrixBuilder.create()
                                    .prefSize(600, 300)
                                    .colsAndRows(COLS, ROWS)
                                    .pixelOnColor(Color.RED)
                                    .pixelOffColor(Color.TRANSPARENT)
                                    .pixelShape(PixelShape.ROUND)
                                    .squarePixels(true)
                                    .build();

        createRandomData();

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 10_000_000) {
                    createRandomData();
                    lastTimerCall = now;
                }
            }
        };
    }

    private void createRandomData() {
        pixelMatrix.setAllPixelsOff();

        final int MIN_VALUE = -20;
        final int MAX_VALUE = 20;
        for (int x = 0  ; x < COLS ; x++) {
            int high = (RND.nextInt(MAX_VALUE) - MAX_VALUE) * -1;
            int low  = (RND.nextInt(MIN_VALUE * -1)) + (MIN_VALUE * -1);
            for (int y = 0 ; y < ROWS ; y++) {
                if (y >= high && y <= low) {
                    if (y >= 0 && y < 4) {
                        pixelMatrix.setPixel(x, y, high <= y ? BLUE : OFF);
                    } else if (y >= 4 && y < 8) {
                        pixelMatrix.setPixel(x, y, high <= y ? DARK_GREEN : OFF);
                    } else if (y >= 8 && y < 12) {
                        pixelMatrix.setPixel(x, y, high <= y ? GREEN : OFF);
                    } else if (y >= 12 && y < 20) {
                        pixelMatrix.setPixel(x, y, high <= y ? LIGHT_GREEN : OFF);
                    } else if (y >= 20 && y < 28) {
                        pixelMatrix.setPixel(x, y, high <= y ? BEIGE : OFF);
                    } else if (y >= 28 && y < 32) {
                        pixelMatrix.setPixel(x, y, high <= y ? YELLOW : OFF);
                    } else if (y >= 32 && y < 36) {
                        pixelMatrix.setPixel(x, y, high <= y ? ORANGE : OFF);
                    } else if (y >= 36 && y < 40) {
                        pixelMatrix.setPixel(x, y, high <= y ? RED : OFF);
                    } else {
                        pixelMatrix.setPixel(x, y, high <= y ? DARK_RED : OFF);
                    }
                }
            }
        }
        pixelMatrix.drawMatrix();
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(pixelMatrix);
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("MatrixPlot");
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
