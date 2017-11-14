package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.MatrixDataObject;
import eu.hansolo.fx.charts.series.MatrixSeries;
import eu.hansolo.fx.charts.tools.ColorMapping;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
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

    private MatrixSeries<MatrixDataObject> matrixSeries1;
    private MatrixPane<MatrixDataObject>   matrixHeatMap1;
    private double                         factor;

    private MatrixSeries<MatrixDataObject> matrixSeries2;
    private MatrixPane<MatrixDataObject>   matrixHeatMap2;

    private long                           lastTimerCall;
    private AnimationTimer                 timer;


    @Override public void init() {
        int cellX = 0;
        int cellY = 0;
        List<MatrixDataObject> matrixData1 = new ArrayList<>();
        for (double y = 0 ; y < TWO_PI ; y += STEP) {
            cellX = 0;
            for (double x = 0 ; x < TWO_PI ; x += STEP) {
                matrixData1.add(new MatrixDataObject(cellX, cellY, (Math.cos(y * TWO_PI * 0.125) * Math.sin(x * TWO_PI * 0.125) + 1) * 0.5));
                cellX++;
            }
            cellY++;
        }

        matrixSeries1  = new MatrixSeries(matrixData1, ChartType.MATRIX_HEATMAP);

        matrixHeatMap1 = new MatrixPane(matrixSeries1);
        matrixHeatMap1.setColorMapping(ColorMapping.BLUE_TRANSPARENT_RED);
        matrixHeatMap1.getMatrix().setUseSpacer(false);
        matrixHeatMap1.getMatrix().setColsAndRows(NO_OF_CELLS, NO_OF_CELLS);
        matrixHeatMap1.setPrefSize(400, 400);


        LinearGradient matrixGradient = Helper.createColorVariationGradient(Color.BLUE, 5);

        List<MatrixDataObject> matrixData2 = new ArrayList<>();
        for (int y = 0 ; y < 6 ; y++) {
            for (int x = 0 ; x < 8 ; x++) {
                matrixData2.add(new MatrixDataObject(x, y, RND.nextDouble()));
            }
        }

        matrixSeries2  = new MatrixSeries(matrixData2, ChartType.MATRIX_HEATMAP);

        matrixHeatMap2 = new MatrixPane(matrixSeries2);
        //matrixHeatMap2.setColorMapping(ColorMapping.BLUE_TRANSPARENT_RED);
        matrixHeatMap2.setMatrixGradient(matrixGradient);
        matrixHeatMap2.getMatrix().setUseSpacer(true);
        matrixHeatMap2.getMatrix().setColsAndRows(8, 6);
        matrixHeatMap2.setPrefSize(400, 300);


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
