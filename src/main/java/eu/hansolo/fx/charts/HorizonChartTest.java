package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.data.XYData;
import eu.hansolo.fx.charts.data.XYDataObject;
import eu.hansolo.fx.charts.series.XYSeries;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * User: hansolo
 * Date: 13.11.17
 * Time: 09:27
 */
public class HorizonChartTest extends Application {
    private static final Random                 RND            = new Random();
    private static final int                    NO_OF_X_VALUES = 1500;
    private              XYSeries<XYDataObject> xySeries;
    private              XYPane                 xyPane;


    @Override public void init() {
        int                bands      = 4;
        int                noOfValues = 1500;
        List<XYDataObject> xyData     = new ArrayList<>(noOfValues);
        for (int i = 0 ; i < noOfValues; i++) {
            double value = Math.abs(Math.cos(i/100.0) + (RND.nextDouble() - 0.5) / 10.0); // Only positive data
            xyData.add(new XYDataObject(i, value, "P" + i));
        }

        double minY = xyData.stream().mapToDouble(XYData::getY).min().getAsDouble();
        double maxY = xyData.stream().mapToDouble(XYData::getY).max().getAsDouble();

        xySeries = new XYSeries<>(xyData, ChartType.HORIZON);

        xyPane = new XYPane(bands, xySeries);
        xyPane.setPrefSize(400, 30);
        xyPane.setLowerBoundX(0);
        xyPane.setUpperBoundX(NO_OF_X_VALUES);
        xyPane.setLowerBoundY(minY);
        xyPane.setUpperBoundY(maxY);

    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(xyPane);

        Scene scene = new Scene(pane);

        stage.setTitle("Horizon Chart");
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
