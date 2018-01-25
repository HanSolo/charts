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

import eu.hansolo.fx.charts.data.MatrixChartItem;
import eu.hansolo.fx.charts.series.MatrixItemSeries;
import eu.hansolo.fx.charts.tools.ColorMapping;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * User: hansolo
 * Date: 14.11.17
 * Time: 04:19
 */
public class MatrixHeatmapTest extends Application {
    private static final Random RND         = new Random();
    private static final double TWO_PI      = 2 * Math.PI;
    private static final int    NO_OF_CELLS = 100;
    private static final double STEP        = TWO_PI / NO_OF_CELLS;

    private MatrixItemSeries<MatrixChartItem> matrixItemSeries1;
    private MatrixPane<MatrixChartItem>       matrixHeatMap1;
    private double                            factor;

    private MatrixItemSeries<MatrixChartItem> matrixItemSeries2;
    private MatrixPane<MatrixChartItem>       matrixHeatMap2;

    private MatrixItemSeries<MatrixChartItem> matrixItemSeries3;
    private MatrixPane<MatrixChartItem>       matrixHeatMap3;

    private long                           lastTimerCall;
    private AnimationTimer                 timer;


    @Override public void init() {
        int                   cellX       = 0;
        int                   cellY       = 0;
        List<MatrixChartItem> matrixData1 = new ArrayList<>();
        for (double y = 0 ; y < TWO_PI ; y += STEP) {
            cellX = 0;
            for (double x = 0 ; x < TWO_PI ; x += STEP) {
                matrixData1.add(new MatrixChartItem(cellX, cellY, (Math.cos(y * TWO_PI * 0.125) * Math.sin(x * TWO_PI * 0.125) + 1) * 0.5));
                cellX++;
            }
            cellY++;
        }

        matrixItemSeries1 = new MatrixItemSeries(matrixData1, ChartType.MATRIX_HEATMAP);

        matrixHeatMap1 = new MatrixPane(matrixItemSeries1);
        matrixHeatMap1.setColorMapping(ColorMapping.INFRARED_1);
        matrixHeatMap1.getMatrix().setUseSpacer(false);
        matrixHeatMap1.getMatrix().setColsAndRows(NO_OF_CELLS, NO_OF_CELLS);
        matrixHeatMap1.setPrefSize(400, 400);


        LinearGradient matrixGradient = Helper.createColorVariationGradient(Color.BLUE, 5);

        List<MatrixChartItem> matrixData2 = new ArrayList<>();
        for (int y = 0 ; y < 6 ; y++) {
            for (int x = 0 ; x < 8 ; x++) {
                matrixData2.add(new MatrixChartItem(x, y, RND.nextDouble()));
            }
        }

        matrixItemSeries2 = new MatrixItemSeries(matrixData2, ChartType.MATRIX_HEATMAP);

        matrixHeatMap2 = new MatrixPane(matrixItemSeries2);
        //matrixHeatMap2.setColorMapping(ColorMapping.BLUE_TRANSPARENT_RED);
        matrixHeatMap2.setMatrixGradient(matrixGradient);
        matrixHeatMap2.getMatrix().setUseSpacer(true);
        matrixHeatMap2.getMatrix().setColsAndRows(8, 6);
        matrixHeatMap2.setPrefSize(400, 300);



        List<MatrixChartItem> matrixData3 = new ArrayList<>();
        for (int x = 0 ; x <108 ; x++) {
            int start = RND.nextInt(5);
            int stop  = RND.nextInt(35 + 5);
            for (int y = 0 ; y < 40 ; y++) {
                MatrixChartItem mdo = new MatrixChartItem(x, y, RND.nextDouble());
            }
        }

        matrixItemSeries3 = new MatrixItemSeries<>(matrixData3, ChartType.MATRIX_HEATMAP);

        matrixHeatMap3 = new MatrixPane<>(matrixItemSeries3);
        matrixHeatMap3.setMatrixGradient(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                                            new Stop(0.0, Color.web("#0085D9")),
                                                            new Stop(0.125, Color.web("#52B53D")),
                                                            new Stop(0.25, Color.web("#93CE7D")),
                                                            new Stop(0.375, Color.web("#BBDFAC")),
                                                            new Stop(0.5, Color.web("#EADEAC")),
                                                            new Stop(0.625, Color.web("#FFD01F")),
                                                            new Stop(0.75, Color.web("#FC9200")),
                                                            new Stop(0.875, Color.web("#EC3A21")),
                                                            new Stop(1.0, Color.web("#C4311D"))));
        matrixHeatMap3.getMatrix().setUseSpacer(false);
        matrixHeatMap3.getMatrix().setSquarePixels(false);
        matrixHeatMap3.getMatrix().setColsAndRows(108,40);
        matrixHeatMap3.setPrefSize(900, 400);


        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 10_000_000l) {
                    int cellX;
                    int cellY = 0;
                    for (double y = 0 ; y < TWO_PI ; y += STEP) {
                        if (Double.compare(factor, Math.PI * 2.55) >= 0) { factor = 0; }
                        cellX = 0;
                        for (double x = factor ; x < TWO_PI + factor ; x += STEP) {
                            double variance = Math.abs(Math.cos(x/100.0) + (RND.nextDouble() - 0.5) / 10.0);
                            double value = ((Math.cos(y * TWO_PI * 0.125) * Math.sin(x * TWO_PI * 0.125) + 1) * 0.5) * variance;
                            matrixHeatMap1.setValueAt(cellX, cellY, value);
                            cellX++;
                        }
                        cellY++;
                    }
                    matrixHeatMap1.getMatrix().drawMatrix();
                    factor += STEP;
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        VBox pane = new VBox(10, matrixHeatMap1, matrixHeatMap2);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("MatrixHeatMap");
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
