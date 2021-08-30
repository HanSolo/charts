/*
 * Copyright (c) 2019 by Gerrit Grunwald
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

import eu.hansolo.fx.charts.pareto.ParetoBar;
import eu.hansolo.fx.charts.pareto.ParetoModel;
import eu.hansolo.fx.charts.pareto.ParetoPanel;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;


public class ParetoTest extends Application {
    private ParetoPanel panel;


    @Override public void init() {
        panel = new ParetoPanel(createTestData1());
        panel.addAndActivatedColorTheme("testTheme1", createColorTheme1());
    }

    @Override public void start(Stage stage) {

        PresentationPareto pres = new PresentationPareto();
        init();
        StackPane pane  = new StackPane(pres);
        pane.setPadding(new Insets(10));
        Scene     scene = new Scene(pane);


        stage.setTitle("Pareto Chart");
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }


    public List<Color> createColorTheme1() {
        ArrayList<Color> theme = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            theme.add(Color.color(0.05 * i, 1 - 0.05 * i, 0.5));
        }
        return theme;
    }

    public ParetoModel createTestData1(){

        ParetoModel paretoModel = new ParetoModel();

        ArrayList<ParetoBar> woodList = new ArrayList<>();
        woodList.add(new ParetoBar("oak",217.0));
        woodList.add(new ParetoBar("willow",200.0));
        ParetoBar woods = new ParetoBar("Delivery",woodList);
        woods.setFillColor(Color.RED);

        ArrayList<ParetoBar> unitList = new ArrayList<>();
        unitList.add(new ParetoBar("Quality Certificate error",200.0));
        unitList.add(new ParetoBar("Quality Certificate missing",300.0));
        unitList.add(new ParetoBar("cavalry",100.0));
        ParetoBar units = new ParetoBar("Documents",unitList);

        paretoModel.getData().add(new ParetoBar("Product Quality",103.0));
        paretoModel.getData().add(new ParetoBar("Packaging",217.0));
        paretoModel.getData().add(woods);
        paretoModel.getData().add(units);

        return paretoModel;
    }


    private class PresentationPareto extends BorderPane {
        private ParetoPanel            paretoPanel;
        private ColorPicker            circleColor;
        private ColorPicker            graphColor;
        private ColorPicker            fontColor;
        private ArrayList<ColorPicker> barColors;
        private ArrayList<Color>       exampeColorTheme;

        private CheckBox               smoothing;
        private CheckBox               realColor;
        private CheckBox               showSubBars;
        private CheckBox               singeSubBarCentered;

        private Slider                 circleSize;
        private Slider                 valueHeight;
        private Slider                 textHeight;
        private Slider                 pathHeight;
        private Slider                 barSpacing;

        private ComboBox<String>       colorTheme;

        private Button                 backButton;

        private HBox                   mainBox;
        private Pane                   pane;
        private VBox                   menu;

        public PresentationPareto(){
            initParts();
            setUpBindings();
            layoutParts();
            setUpListener();
        }

        private void initParts(){
            paretoPanel = new ParetoPanel(createTestData1());

            circleColor = new ColorPicker();
            circleColor.setValue(Color.BLUE);
            graphColor = new ColorPicker();
            graphColor.setValue(Color.BLACK);
            fontColor = new ColorPicker();
            fontColor.setValue(Color.BLACK);

            smoothing = new CheckBox("Smoothing");
            realColor = new CheckBox("AutoSubColor");
            showSubBars = new CheckBox("ShowSubBars");
            singeSubBarCentered = new CheckBox("SingleSubBarCenterd");

            circleSize = new Slider(1,60,20);
            valueHeight = new Slider(0,80,20);
            textHeight = new Slider(0,80,40);
            barSpacing = new Slider(1,50,5);
            pathHeight = new Slider(0,80,65);

            barColors = new ArrayList<>();

            backButton = new Button("Back to last layer");

            exampeColorTheme = createRandomColorTheme(20);

            paretoPanel.addColorTheme("example",exampeColorTheme);
            colorTheme = new ComboBox<>();
            colorTheme.getItems().addAll(paretoPanel.getColorThemeKeys());

            mainBox = new HBox();
            menu = new VBox();
            pane = new Pane();

        }


        private void setUpBindings(){
            paretoPanel.dataDotSizeProperty().bind(circleSize.valueProperty());
            paretoPanel.useCalculatedSubBarColorsProperty().bind(realColor.selectedProperty());
            paretoPanel.percentageLineColorProperty().bind(graphColor.valueProperty());
            paretoPanel.percentageLineDataDotColorProperty().bind(circleColor.valueProperty());
            paretoPanel.smoothPercentageCurveProperty().bind(smoothing.selectedProperty());
            paretoPanel.identifierFontYPositionProperty().bind(textHeight.valueProperty());
            paretoPanel.pathFontYPositionProperty().bind(pathHeight.valueProperty());
            paretoPanel.valueFontYPositionProperty().bind(valueHeight.valueProperty());
            paretoPanel.barSpacingProperty().bind(barSpacing.valueProperty());
            paretoPanel.fontColorProperty().bind(fontColor.valueProperty());
            showSubBars.setSelected(true);
            paretoPanel.showSubBarsProperty().bind(showSubBars.selectedProperty());
            singeSubBarCentered.setSelected(true);
            paretoPanel.singleSubBarCenteredProperty().bind(singeSubBarCentered.selectedProperty());
        }

        private void layoutParts(){
            menu.getChildren().addAll(
                new Label("Bar spacing"), barSpacing,
                new Label("Circle size"), circleSize,
                new Label("Circle Color"), circleColor,
                new Label("Graph Color"), graphColor,
                new Label("Bottom value text Y pos."), textHeight,
                new Label("Bottom identifier text Y pos"), valueHeight,
                new Label("Bottom path Y pos"), pathHeight,
                new Label("Bootm text Color"), fontColor,
                smoothing,
                realColor,
                showSubBars, singeSubBarCentered,
                backButton,
                new Label ("ColorThemes"), colorTheme,
                new Label("Bar Colors")
                                     );

            updateBarColorPicker();
            menu.setPadding(new Insets(0, 10, 0, 10));
            this.setCenter(paretoPanel);

            this.setRight(menu);
        }

        private void setUpListener(){
            backButton.setOnAction(event -> paretoPanel.returnToPreviousLayer());
            colorTheme.valueProperty().addListener(((observable, oldValue, newValue) -> paretoPanel.activateColorTheme(newValue)));
        }

        public void updateBarColorPicker(){
            menu.getChildren().removeAll(barColors);
            barColors.clear();
            for(ParetoBar bar: paretoPanel.getParetoModel().getData()){
                ColorPicker temp = new ColorPicker(bar.getFillColor());
                barColors.add(temp);
                bar.fillColorProperty().bindBidirectional(temp.valueProperty());
            }
            menu.getChildren().addAll(barColors);
        }


        public ArrayList<Color> createRandomColorTheme(int amountOfColors){
            ArrayList<Color> theme = new ArrayList<>();
            if(amountOfColors < 1){
                theme.add(Color.BLUE);
            } else{
                for(int i=0; i<amountOfColors; i++){
                    theme.add(Color.color(Math.random(), Math.random(), Math.random()));
                }
            }
            return theme;
        }
    }
}
