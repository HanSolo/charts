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

import eu.hansolo.fx.charts.forcedirectedgraph.GraphEdge;
import eu.hansolo.fx.charts.forcedirectedgraph.GraphNode;
import eu.hansolo.fx.charts.forcedirectedgraph.GraphPanel;
import eu.hansolo.fx.charts.forcedirectedgraph.NodeEdgeModel;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * authors: Michael L\u00E4uchli, MLaeuchli (github)
 *          Stefan Mettler, orizion (github)
 */
public class ForceDirectedGraphTest extends Application {

    private GraphPanel        graph;
    private PresentationPanel presentationPanel;

    @Override public void init() {
        graph = new GraphPanel();
        presentationPanel = new PresentationPanel();
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(presentationPanel);
        //StackPane pane = new StackPane(graph);
        Scene scene = new Scene(pane);

        stage.setTitle("Force Directed Graph");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }


    class PresentationPanel extends BorderPane {
        private GraphPanel graphPanel;

        private VBox sliderBox;

        private Slider nodeSizeFactor;
        private Slider edgeWidthFactor;

        private ComboBox<String> dataChanger;

        private ComboBox<String> groupComboBox;
        private ComboBox<String> colorSchemeComboBox;
        private ComboBox<String> nodeValueComboBox;

        private ComboBox<String> edgeForceValueComboBox;
        private ComboBox<String> edgeWidthValueComboBox;

        private ColorPicker borderColorPicker;
        private ColorPicker edgeColorPicker;

        private RadioButton alwaysNormalize;
        private RadioButton neverNormalize;
        private RadioButton normalizeIfBetweenZeroAndOne;

        private ToggleGroup normalizeGroup;

        private CheckBox physic;
        private CheckBox invertedForce;

        private Button calculateDegreeCentrality;
        private Button calculateClosenessCentrality;
        private Button calculateBetweennessCentrality;
        private Button restartAnimation;

        private NodeEdgeModel nodeEdgeModel;
        private DataGenerator dataGenerator;

        private VBox colors;

        public PresentationPanel(){
            initializeParts();
            layoutParts();
            setupBindings();
            setupListener();
            layoutSelf();

        }

        private void initializeParts(){
            dataGenerator = new DataGenerator();
            dataGenerator.generateGraphWithMinimalInformation();

            nodeEdgeModel = new NodeEdgeModel(dataGenerator.getNodes(), dataGenerator.getEdges());
            nodeEdgeModel.addColorTheme(createExampleColorTheme(),"Example_1");
            nodeEdgeModel.addColorTheme(createExampleColorTheme2(), "Example_2");
            nodeEdgeModel.setCurrentColorThemeKey("Kelly Color");
            graphPanel = new GraphPanel(nodeEdgeModel);


            sliderBox = new VBox();

            nodeSizeFactor  = new Slider(5,30,10);
            edgeWidthFactor = new Slider(0.5,10,5);

            groupComboBox = new ComboBox<>();
            nodeValueComboBox = new ComboBox<>();
            edgeForceValueComboBox = new ComboBox<>();
            edgeWidthValueComboBox = new ComboBox<>();

            colorSchemeComboBox = new ComboBox<>();
            colorSchemeComboBox.getItems().addAll(graphPanel.getNodeEdgeModel().getColorThemeKeys());

            normalizeGroup = new ToggleGroup();
            alwaysNormalize = new RadioButton("Always Normalize");
            neverNormalize = new RadioButton("Never Normalize");
            normalizeIfBetweenZeroAndOne = new RadioButton("Normalize if Between 0 and 1");
            alwaysNormalize.setToggleGroup(normalizeGroup);
            neverNormalize.setToggleGroup(normalizeGroup);
            normalizeIfBetweenZeroAndOne.setToggleGroup(normalizeGroup);
            alwaysNormalize.setSelected(true);
            physic = new CheckBox("Disable Physics");
            physic.selectedProperty().setValue(true);
            invertedForce = new CheckBox("inverted");

            calculateDegreeCentrality = new Button("Calculate Degreecentrality");
            calculateClosenessCentrality = new Button("Calculate ClossenessCentrality");
            calculateBetweennessCentrality = new Button("Calculate BetweennessCentrality");
            restartAnimation = new Button("Restart Animation");

            borderColorPicker = new ColorPicker();
            edgeColorPicker = new ColorPicker();
            edgeColorPicker.setValue(Color.LIGHTGRAY);

            groupComboBox.getItems().addAll(graphPanel.getNodeEdgeModel().getStringAttributeKeysOfNodes());
            nodeValueComboBox.getItems().addAll(graphPanel.getNodeEdgeModel().getNumericAttributeKeysOfNodes());
            edgeForceValueComboBox.getItems().addAll(graphPanel.getNodeEdgeModel().getNumericAttributeKeysOfEdges());
            edgeWidthValueComboBox.getItems().addAll(graphPanel.getNodeEdgeModel().getNumericAttributeKeysOfEdges());

            //edgeForceValueComboBox.getItems().addAll(nodeEdgeModel)

            dataChanger = new ComboBox<>();
            dataChanger.getItems().addAll("D3Example","FoodExport");

            colors = new VBox();




            refreshColorPickers();


        }

        private void layoutParts(){
            sliderBox.getChildren().clear();
            sliderBox.getChildren().addAll(new Label("Size Factor"), nodeSizeFactor,
                                           new Label("Edge With Factor:"), edgeWidthFactor,
                                           new Label("Grouped By"), groupComboBox,
                                           new Label("NodeBorderColor"), borderColorPicker,
                                           new Label("Group Colors"), colors,
                                           new Label("ColorTheme"), colorSchemeComboBox,
                                           new Label("Edge Color"), edgeColorPicker,
                                           new Label("Node Size Attribute"), nodeValueComboBox,
                                           new Label("Edge Width Attribute"), edgeWidthValueComboBox,
                                           new Label("Edge Force Attribute"), edgeForceValueComboBox,
                                           new Label("Force inverted"), invertedForce,
                                           new Label("Normalization Behavior:"), alwaysNormalize, neverNormalize, normalizeIfBetweenZeroAndOne,
                                           new Label("Enable Phisics"), physic,
                                           new Label("Calculations"), calculateDegreeCentrality, calculateClosenessCentrality, calculateBetweennessCentrality, restartAnimation,
                                           new Label("Select Data"), dataChanger);

        }

        private void setupBindings(){
            graphPanel.nodeSizeFactorProperty().bindBidirectional(nodeSizeFactor.valueProperty());

            graphPanel.edgeWidthFactorProperty().bindBidirectional(edgeWidthFactor.valueProperty());
            graphPanel.forceInvertedProperty().bindBidirectional(invertedForce.selectedProperty());
            graphPanel.physicsActiveProperty().bindBidirectional(physic.selectedProperty());
            graphPanel.getNodeEdgeModel().alwaysNormalizeProperty().bindBidirectional(alwaysNormalize.selectedProperty());
            graphPanel.getNodeEdgeModel().neverNormalizeProperty().bindBidirectional(neverNormalize.selectedProperty());
            graphPanel.getNodeEdgeModel().normalizeIfNotBetweenZeroAndOneProperty().bindBidirectional(normalizeIfBetweenZeroAndOne.selectedProperty());
            graphPanel.getNodeEdgeModel().nodeBorderColorProperty().bindBidirectional(borderColorPicker.valueProperty());
            graphPanel.edgeColorProperty().bindBidirectional(edgeColorPicker.valueProperty());

        }

        private void setupListener(){
            dataChanger.valueProperty().addListener((observable, oldValue, newValue) -> {
                switch(newValue){
                    case "D3Example":
                        dataGenerator.generateGraphWithDiffrentNodeSizes();
                        nodeEdgeModel = new NodeEdgeModel(dataGenerator.getNodes(), dataGenerator.getEdges());
                        graphPanel.setNodeEdgeModel(nodeEdgeModel);
                        graphPanel.getNodeEdgeModel().addColorTheme(createExampleColorTheme(), "Example1");
                        graphPanel.getNodeEdgeModel().addColorTheme(createExampleColorTheme2(), "Example2");
                        graphPanel.getNodeEdgeModel().setCurrentColorThemeKey("Kelly Color");
                        setDefault(graphPanel.getNodeEdgeModel());

                        break;
                    case "FoodExport":
                        dataGenerator.generateGraphWithMinimalInformation();
                        nodeEdgeModel = new NodeEdgeModel(dataGenerator.getNodes(), dataGenerator.getEdges());
                        nodeEdgeModel.setCurrentColorThemeKey("Kelly Color");
                        setDefault(nodeEdgeModel);
                        graphPanel.setNodeEdgeModel(nodeEdgeModel);

                        break;
                    default :
                        System.err.println("DataKey invalid");
                }
                setupBindings();
                refreshGui();
            });

            groupComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                graphPanel.getNodeEdgeModel().setGroupColors(newValue);
                refreshColorPickers();
            });

            colorSchemeComboBox.valueProperty().addListener((Observable, oldValue, newValue) -> {
                graphPanel.getNodeEdgeModel().instantiateColorScheme(newValue);
                refreshColorPickers();
            });

            nodeValueComboBox.valueProperty().addListener((observable, oldValue, newValue) -> graphPanel.getNodeEdgeModel().setNodeSizeKey(newValue));
            edgeWidthValueComboBox.valueProperty().addListener((observable, oldValue, newValue) -> graphPanel.getNodeEdgeModel().setEdgeWidthFromAttributeNormalized(newValue));
            edgeForceValueComboBox.valueProperty().addListener((observable, oldValue, newValue) -> graphPanel.getNodeEdgeModel().setEdgeForceFromAttributeNormalized(newValue));

            calculateDegreeCentrality.setOnAction(event -> {
                graphPanel.calculateDegreeCentrality();
                refreshGui();
            });
            calculateBetweennessCentrality.setOnAction(event -> {
                graphPanel.calculateBetweennessCentrality();
                refreshGui();
            });
            calculateClosenessCentrality.setOnAction(event ->{
                graphPanel.calculateClosenessCentrality();
                refreshGui();
            });

            restartAnimation.setOnAction(event -> graphPanel.restart());
        }

        private void layoutSelf(){
            this.setCenter(graphPanel);
            this.setRight(sliderBox);
            //super.getChildren().addAll(graphPanel, sliderBox);

        }

        private void refreshGui(){
            String groupValue = groupComboBox.getValue();
            String nodeValue = nodeValueComboBox.getValue();
            String edgeForceValue = edgeForceValueComboBox.getValue();
            String edgeWidthValue = edgeWidthValueComboBox.getValue();
            String colorShemeValue = colorSchemeComboBox.getValue();

            groupComboBox.setItems(FXCollections.observableArrayList(graphPanel.getNodeEdgeModel().getStringAttributeKeysOfNodes()));
            nodeValueComboBox.setItems(FXCollections.observableArrayList(graphPanel.getNodeEdgeModel().getNumericAttributeKeysOfNodes()));
            ObservableList list = FXCollections.observableArrayList(graphPanel.getNodeEdgeModel().getNumericAttributeKeysOfEdges());
            edgeForceValueComboBox.setItems(list);
            edgeWidthValueComboBox.setItems(list);
            colorSchemeComboBox.setItems(FXCollections.observableArrayList(graphPanel.getNodeEdgeModel().getColorThemeKeys()));
            refreshColorPickers();

            groupComboBox.setValue(groupValue);
            nodeValueComboBox.setValue(nodeValue);
            edgeForceValueComboBox.setValue(edgeForceValue);
            edgeWidthValueComboBox.setValue(edgeWidthValue);
            colorSchemeComboBox.setValue(colorShemeValue);
        }

        private void refreshColorPickers(){
            colors.getChildren().clear();
            for(String s: graphPanel.getNodeEdgeModel().getDistinctValuesPerGroupKey(graphPanel.getNodeEdgeModel().getCurrentGroupKey())) {
                HBox                  temp = new HBox();
                ColorPickerWithString cp   = new ColorPickerWithString(s);
                cp.setValue(graphPanel.getNodeEdgeModel().getGroupValueColor(s, graphPanel.getNodeEdgeModel().getCurrentGroupKey()));
                temp.getChildren().addAll(cp, new Label(s));
                addChangeListenerToColorPicker(cp);
                colors.getChildren().add(temp);
            }
        }

        private void addChangeListenerToColorPicker(ColorPickerWithString cp){
            cp.valueProperty().addListener(((observable, oldValue, newValue) -> graphPanel.getNodeEdgeModel().setGroupValueColor(cp.getKey(),graphPanel.getNodeEdgeModel().getCurrentGroupKey(), newValue)));

        }

        private void setDefault(NodeEdgeModel nodeEdgeModel){
            nodeSizeFactor.setValue(graphPanel.getNodeSizeFactor());
            edgeWidthFactor.setValue(graphPanel.getEdgeWidthFactor());
            alwaysNormalize.setSelected(nodeEdgeModel.isAlwaysNormalize());
            neverNormalize.setSelected(nodeEdgeModel.isNeverNormalize());
            normalizeIfBetweenZeroAndOne.setSelected(nodeEdgeModel.isNormalizeIfNotBetweenZeroAndOne());
        }

        private ArrayList<Color> createExampleColorTheme(){
            ArrayList<Color> colorTheme = new ArrayList();
            for(int i=0; i<20; i++){
                colorTheme.add(Color.color(0.5, 0.5, 1-0.05*i));
            }
            return colorTheme;
        }

        private ArrayList<Color> createExampleColorTheme2(){
            ArrayList<Color> colorTheme = new ArrayList();
            for(int i=0; i<20; i++){
                colorTheme.add(Color.color(1-0.05*i, 0.05*i, 1));
            }
            return colorTheme;
        }

    }

    class ColorPickerWithString extends ColorPicker {
        private String key;

        public ColorPickerWithString(String key){
            super();
            this.key = key;
        }

        public String getKey() { return key; }
        public void setKey(final String KEY) { key = KEY; }
    }

    class DataGenerator {

        private ArrayList<GraphNode>     nodes;
        private ArrayList<GraphEdge>     edges;
        private HashMap<String, Color>   colorsheme;
        private int                      index;
        private HashMap<String, Integer> indexMap;

        public DataGenerator(){
            nodes = new ArrayList<>();
            edges = new ArrayList<>();
            colorsheme = new HashMap<>();
        }


        public void generateGraphWithMinimalInformation(){
            setColorshemeToMinimum();
            nodes.clear();
            nodes.add(new GraphNode(new HashMap<>(), new HashMap<>()));
            nodes.add(new GraphNode(new HashMap<>(), new HashMap<>()));
            nodes.add(new GraphNode(new HashMap<>(), new HashMap<>()));
            nodes.add(new GraphNode(new HashMap<>(), new HashMap<>()));
            nodes.add(new GraphNode(new HashMap<>(), new HashMap<>()));
            nodes.add(new GraphNode(new HashMap<>(), new HashMap<>()));
            nodes.add(new GraphNode(new HashMap<>(), new HashMap<>()));
            nodes.add(new GraphNode(new HashMap<>(), new HashMap<>()));
            nodes.add(new GraphNode(new HashMap<>(), new HashMap<>()));

            edges.clear();
            edges.add(new GraphEdge(nodes.get(0), nodes.get(1), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(1), nodes.get(2), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(2), nodes.get(3), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(3), nodes.get(4), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(4), nodes.get(5), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(5), nodes.get(6), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(6), nodes.get(7), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(7), nodes.get(8), new HashMap<>()));
        }

        public void generateGraphWithDiffrentNodeSizes(){
            setColorshemeToMinimum();
            nodes.clear();
            edges.clear();

            String sizeKey = "size";
            HashMap<String, Double> temp;

            temp = new HashMap<String, Double>();
            temp.put(sizeKey, 200.0);
            nodes.add(new GraphNode(temp, new HashMap<>()));
            temp = new HashMap<String, Double>();
            temp.put(sizeKey, 400.0);
            nodes.add(new GraphNode(temp, new HashMap<>()));
            temp = new HashMap<String, Double>();
            temp.put(sizeKey, 150.0);
            nodes.add(new GraphNode(temp, new HashMap<>()));
            temp = new HashMap<String, Double>();
            temp.put(sizeKey, 600.0);
            nodes.add(new GraphNode(temp, new HashMap<>()));
            temp = new HashMap<String, Double>();
            temp.put(sizeKey, 250.0);
            nodes.add(new GraphNode(temp, new HashMap<>()));
            temp = new HashMap<String, Double>();
            temp.put(sizeKey, 300.0);
            nodes.add(new GraphNode(temp, new HashMap<>()));

            edges.add(new GraphEdge(nodes.get(0), nodes.get(1), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(1), nodes.get(2), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(2), nodes.get(3), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(3), nodes.get(4), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(4), nodes.get(5), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(5), nodes.get(2), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(4), nodes.get(2), new HashMap<>()));

        }

        public void generateGraphWithGroupingAndDifferentNodeSizes(){
            setColorshemeToMinimum();
            nodes.clear();
            edges.clear();

            String sizeKey = "size";
            HashMap<String, Double> temp;
            String colorKey = "color";
            HashMap<String, String> temp2;

            temp = new HashMap<String, Double>();
            temp.put(sizeKey, 200.0);
            temp2 = new HashMap<String,String>();
            temp2.put(colorKey, "Group1");
            nodes.add(new GraphNode(temp, temp2));
            temp = new HashMap<String, Double>();
            temp.put(sizeKey, 400.0);
            temp2 = new HashMap<String,String>();
            temp2.put(colorKey, "Group1");
            nodes.add(new GraphNode(temp, temp2));
            temp = new HashMap<String, Double>();
            temp.put(sizeKey, 150.0);
            temp2 = new HashMap<String,String>();
            temp2.put(colorKey, "Group2");
            nodes.add(new GraphNode(temp, temp2));
            temp = new HashMap<String, Double>();
            temp.put(sizeKey, 600.0);
            temp2 = new HashMap<String,String>();
            temp2.put(colorKey, "Group2");
            nodes.add(new GraphNode(temp, temp2));
            temp = new HashMap<String, Double>();
            temp.put(sizeKey, 250.0);
            temp2 = new HashMap<String,String>();
            temp2.put(colorKey, "Group1");
            nodes.add(new GraphNode(temp, temp2));
            temp = new HashMap<String, Double>();
            temp.put(sizeKey, 300.0);
            temp2 = new HashMap<String,String>();
            temp2.put(colorKey, "Group3");
            nodes.add(new GraphNode(temp, temp2));

            edges.add(new GraphEdge(nodes.get(0), nodes.get(1), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(1), nodes.get(2), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(2), nodes.get(3), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(3), nodes.get(4), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(4), nodes.get(5), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(5), nodes.get(2), new HashMap<>()));
            edges.add(new GraphEdge(nodes.get(4), nodes.get(2), new HashMap<>()));

            colorsheme.put("Group1", Color.GREEN);
            colorsheme.put("Group2", Color.BLUE);
            colorsheme.put("Group3", Color.YELLOW);

        }

        public void generateRandomGraph(){
            int amountOfNodes = (int) (Math.random()*29) +2;
            int amountOfEdges = (amountOfNodes -1) + ((int) (Math.random() * (((amountOfNodes * (amountOfNodes-1))/2)) - amountOfNodes -1));
            generateGraphWithSetAmountOfNodesAndEdges(amountOfNodes, amountOfEdges);
        }

        public void generateGraphWithSetAmountOfNodes(int amountOfNodes){
            int amountOfEdges = (amountOfNodes -1) + ((int) (Math.random() * (((amountOfNodes * (amountOfNodes-1))/2)) - amountOfNodes -1));
            generateGraphWithSetAmountOfNodesAndEdges(amountOfNodes, amountOfEdges);
        }

        public void generateGrapheWitNodeRange(int min, int max){
            if(min>max){
                int temp = max;
                max = min;
                min = temp;
            }
            int amountOfNodes = (int) (Math.random()*(max-min) )+min;
            int amountOfEdges = (amountOfNodes -1) + ((int) (Math.random() * (((amountOfNodes * (amountOfNodes-1))/2)) - amountOfNodes -1));
            generateGraphWithSetAmountOfNodesAndEdges(amountOfNodes, amountOfEdges);
        }

        public void generateGraphWithSetAmountOfNodesAndEdges(int amountOfNodes, int amountOfEdges){
            int amountOfGroups = (int) (Math.random() * (amountOfNodes/2)) +1;
            generateGraphWithSetAmountOfNodesEdgesAndGroups(amountOfNodes, amountOfEdges, amountOfGroups);
        }

        public NodeEdgeModel generateGraphWithSetAmountOfNodesEdgesAndGroups(int amountOfNodes, int amountOfEdges, int amountOfGroups){
            setColorshemeToMinimum();
            nodes.clear();
            edges.clear();


            int groups = amountOfGroups;

            String sizeKey = "size";
            HashMap<String, Double> temp;
            String colorKey = "color";
            HashMap<String, String> temp2;

            for(int i=0; i<groups; i++){
                temp = new HashMap<>();
                temp.put(sizeKey, Math.random() * 300.0 + 100.0);
                temp2 = new HashMap<>();
                temp2.put(colorKey, "Group" + i);
                GraphNode gn = new GraphNode(temp, temp2);
                gn.setSizeKey(sizeKey);
                nodes.add(gn);
            }
            System.out.println("Datagenerator: exit Nodes static grouping");

            for(int i=0; i<amountOfNodes-groups;i++){
                temp = new HashMap<>();
                temp.put(sizeKey, Math.random()*300.0 + 100.0);
                temp2 = new HashMap<>();
                temp2.put(colorKey, "Group" + (int) (Math.random()*groups));
                GraphNode gn = new GraphNode(temp, temp2);
                gn.setSizeKey(sizeKey);
                nodes.add(gn);
            }
            System.out.println("Datagenerator: exit Nodes random grouping");

            ArrayList<Integer> notConnected = new ArrayList<>();
            ArrayList<Integer> connected = new ArrayList<>();
            for(int i=0;i<amountOfNodes; i++){
                notConnected.add(i);
            }


            int currentNode1 = (int) (Math.random()*amountOfNodes);
            int currentNode2 = (int) (Math.random()*amountOfNodes);
            while( currentNode1 == currentNode2){
                currentNode2 = (int) (Math.random()*amountOfNodes);
            }


            edges.add(new GraphEdge(nodes.get(currentNode1),nodes.get(currentNode2), new HashMap<>()));

            connected.add(currentNode1);
            connected.add(currentNode2);
            if(currentNode1 > currentNode2){
                notConnected.remove(currentNode1);
                notConnected.remove(currentNode2);
            } else{
                notConnected.remove(currentNode2);
                notConnected.remove(currentNode1);
            }


            for(int i=0;i< amountOfNodes-2; i++){
                currentNode1 = (int) (Math.random()*notConnected.size());
                currentNode2 = (int) (Math.random()*connected.size());
                edges.add(new GraphEdge(nodes.get(notConnected.get(currentNode1)),
                                        nodes.get(connected.get(currentNode2)),
                                        new HashMap<>()));
                connected.add(notConnected.get(currentNode1));
                notConnected.remove(currentNode1);
            }

            System.out.println("Datagenerator: exit Edge controlled connection");

            boolean newEdge;

            int counter;

            for(int i=0;i<amountOfEdges-(amountOfNodes-1);i++){
                counter = 0;
                do {
                    counter ++;
                    currentNode1 = (int) (Math.random() * amountOfNodes);
                    currentNode2 = (int) (Math.random() * amountOfNodes);
                    while (currentNode1 == currentNode2) {
                        currentNode2 = (int) (Math.random() * amountOfNodes);
                    }
                    int counter2 = 0;
                    newEdge = true;
                    int j =0;
                    while(j<edges.size() && newEdge){
                        counter2++;
                        if((edges.get(j).getU() == nodes.get(currentNode1)&& edges.get(j).getV() == nodes.get(currentNode2))
                           || (edges.get(j).getU() ==nodes.get(currentNode2)&& edges.get(j).getV() == nodes.get(currentNode1))){
                            newEdge = false;
                        }
                        j++;
                    }
                    if(newEdge){
                        edges.add(new GraphEdge(nodes.get(currentNode1), nodes.get(currentNode2), new HashMap<>()));
                    }
                    if(counter>30){
                        newEdge = true;
                        System.out.println("max Amount of Edge connection tries reached");
                    }
                }while (!newEdge);
            }

            for(int i=0; i<groups; i++){
                colorsheme.put("Group" + i, Color.color(Math.random(),Math.random(),Math.random()));
            }
            return new NodeEdgeModel(nodes, edges);

        }

        private void createNodeWithNameAndGroup(String name, HashMap<String,String> group){
            nodes.add(new GraphNode(name,new HashMap<>(),group));
            indexMap.put(name, index);
            index++;
        }


        public ArrayList<GraphNode> getNodes() {
            if(nodes.size()<1){
                generateGraphWithMinimalInformation();
            }
            return nodes;
        }

        public ArrayList<GraphEdge> getEdges() {
            if(edges.size()<1){
                generateGraphWithMinimalInformation();
            }
            return edges;
        }

        public HashMap<String, Color> getColorScheme(){
            return colorsheme;
        }

        private void setColorshemeToMinimum(){
            colorsheme.clear();
            colorsheme.put("None", Color.GRAY);
        }
    }
}
