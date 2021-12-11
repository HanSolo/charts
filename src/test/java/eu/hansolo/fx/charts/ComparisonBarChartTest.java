/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
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

import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.ChartItemBuilder;
import eu.hansolo.fx.charts.font.Fonts;
import eu.hansolo.fx.charts.series.ChartItemSeries;
import eu.hansolo.fx.charts.series.ChartItemSeriesBuilder;
import eu.hansolo.fx.charts.tools.NumberFormat;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ComparisonBarChartTest extends Application {
    private static final Random                   RND = new Random();
    private              List<Category>           categories;
    private              Map<Category, ChartItem> optionsProductA;
    private              Map<Category, ChartItem> optionsProductB;
    private              ComparisonBarChart       chart;
    private              long                     lastTimerCall;
    private              AnimationTimer           timer;


    @Override public void init() {
        categories      = new LinkedList<>();
        optionsProductA = new HashMap<>();
        optionsProductB = new HashMap<>();

        for (int i = 0 ; i < 5 ; i++) {
            Category category = new Category("Option " + i);
            categories.add(category);
            optionsProductA.put(category, ChartItemBuilder.create().name("Product A (Option " + i + ")").category(category).value(0).build());
            optionsProductB.put(category, ChartItemBuilder.create().name("Product B (Option " + i + ")").category(category).value(0).build());
        }

        optionsProductA.get(categories.get(0)).setValue(72);
        optionsProductA.get(categories.get(1)).setValue(60);
        optionsProductA.get(categories.get(2)).setValue(100);
        optionsProductA.get(categories.get(3)).setValue(38);
        optionsProductA.get(categories.get(4)).setValue(80);

        optionsProductB.get(categories.get(0)).setValue(95);
        optionsProductB.get(categories.get(1)).setValue(83);
        optionsProductB.get(categories.get(2)).setValue(50);
        optionsProductB.get(categories.get(3)).setValue(100);
        optionsProductB.get(categories.get(4)).setValue(75);

        ChartItemSeries<ChartItem> series1 = ChartItemSeriesBuilder.create()
                                                                   .name("Product A")
                                                                   .items(new ArrayList<>(optionsProductA.values()))
                                                                   .fill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(255, 105, 91)), new Stop(1, Color.rgb(217, 41, 76))))
                                                                   .textFill(Color.WHITE)
                                                                   .animated(true)
                                                                   .animationDuration(1000)
                                                                   .build();

        ChartItemSeries<ChartItem> series2 = ChartItemSeriesBuilder.create()
                                                                   .name("Product B")
                                                                   .items(new ArrayList<>(optionsProductB.values()))
                                                                   .fill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(0, 150, 235)), new Stop(1, Color.rgb(0, 212, 244))))
                                                                   .textFill(Color.WHITE)
                                                                   .animated(true)
                                                                   .animationDuration(1000)
                                                                   .build();

        chart = ComparisonBarChartBuilder.create(series1, series2)
                                         .prefSize(600, 300)
                                         .doCompare(false)
                                         .backgroundFill(Color.rgb(244, 250, 255))
                                         .categoryBackgroundFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(244, 250, 255)), new Stop(0.05, Color.WHITE), new Stop(0.95, Color.WHITE), new Stop(1.0, Color.rgb(244, 250, 255))))
                                         .barBackgroundFill(Color.rgb(232, 240, 252))
                                         .barBackgroundVisible(true)
                                         .shadowsVisible(true)
                                         .textFill(Color.WHITE)
                                         .categoryTextFill(Color.rgb(64, 66, 100))
                                         .shortenNumbers(false)
                                         .numberFormat(NumberFormat.PERCENTAGE)
                                         .sorted(false)
                                         .build();

        AnchorPane.setTopAnchor(chart, 100d);
        AnchorPane.setRightAnchor(chart, 10d);
        AnchorPane.setBottomAnchor(chart, 10d);
        AnchorPane.setLeftAnchor(chart, 10d);

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 3_000_000_000l) {
                    categories.forEach(category -> {
                        optionsProductA.get(category).setValue(RND.nextDouble() * 75 + 25);
                        optionsProductB.get(category).setValue(RND.nextDouble() * 75 + 25);
                    });

                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        HBox productABox = createProductBox("PRODUCT A", "Some text to describe product A", "A", new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(255, 105, 91)), new Stop(1, Color.rgb(217, 41, 76))), Color.rgb(217, 41, 76), true);
        productABox.setFillHeight(true);
        productABox.setAlignment(Pos.CENTER);
        HBox.setHgrow(productABox, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.SOMETIMES);

        HBox productBBox = createProductBox("PRODUCT B", "Some text to describe product B", "B", new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(0, 150, 235)), new Stop(1, Color.rgb(0, 212, 244))), Color.rgb(0, 150, 235), false);
        productBBox.setFillHeight(true);
        productBBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(productBBox, Priority.ALWAYS);

        HBox header = new HBox(productABox, spacer, productBBox);
        AnchorPane.setTopAnchor(header, 10d);
        AnchorPane.setRightAnchor(header, 10d);
        AnchorPane.setLeftAnchor(header, 10d);

        AnchorPane pane = new AnchorPane(header, chart);
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(244, 250, 255), CornerRadii.EMPTY, Insets.EMPTY)));

        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("ComparisonBarChart");
        stage.setScene(scene);
        stage.show();

        timer.start();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private HBox createProductBox(final String name, final String desc, final String shortForm, final Paint background, final Color color, final boolean left) {
        Label header = new Label(name);
        header.setAlignment(Pos.CENTER);
        header.setFont(Fonts.opensansSemibold(24));
        header.setTextFill(color);
        Label description   = new Label(desc);
        description.setAlignment(left ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        description.setTextAlignment(left ? TextAlignment.RIGHT : TextAlignment.LEFT);
        description.setFont(Fonts.opensansLight(12));
        description.setTextFill(Color.rgb(64, 66, 100));
        VBox vbox = new VBox(10, header, description);
        vbox.setFillWidth(true);
        vbox.setAlignment(Pos.CENTER);
        Label shortFormLabel = new Label(shortForm);
        shortFormLabel.setMinSize(64, 64);
        shortFormLabel.setMaxSize(64, 64);
        shortFormLabel.setPrefSize(64, 64);
        shortFormLabel.setTextFill(Color.WHITE);
        shortFormLabel.setFont(Fonts.opensansSemibold(36));
        shortFormLabel.setAlignment(Pos.CENTER);
        shortFormLabel.setPadding(new Insets(-3, 0, 0, 0));
        shortFormLabel.setBackground(new Background(new BackgroundFill(background, new CornerRadii(100), Insets.EMPTY)));
        shortFormLabel.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.15), 5, 0.0, 0, 5));
        HBox hbox;
        if (left) {
            hbox = new HBox(20, vbox, shortFormLabel);
        } else {
            hbox = new HBox(20, shortFormLabel, vbox);
        }

        hbox.setFillHeight(true);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
