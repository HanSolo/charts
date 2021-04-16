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

import eu.hansolo.fx.charts.SunburstChart.TextOrientation;
import eu.hansolo.fx.charts.SunburstChart.VisibleData;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.TreeNode;
import eu.hansolo.fx.charts.event.EventType;
import eu.hansolo.fx.charts.event.TreeNodeEventType;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 13.11.17
 * Time: 20:55
 */
public class SunburstChartTest extends Application {
    private static final     Color PETROL_0 = Color.rgb(0, 96, 100);
    private static final Color PETROL_1 = Color.rgb(0, 151, 167);
    private static final Color PETROL_2 = Color.rgb(0, 188, 212);
    private static final Color PINK_0   = Color.rgb(136, 14, 79);
    private static final Color PINK_1   = Color.rgb(194, 24, 91);
    private static final Color PINK_2   = Color.rgb(233, 30, 99);
    private static final Color YELLOW_0 = Color.rgb(245, 127, 23);
    private static final Color YELLOW_1 = Color.rgb(251, 192, 45);
    private static final Color         YELLOW_2 = Color.rgb(255, 235, 59);
    private static final Color         GREEN_0  = Color.rgb(27, 94, 32);
    private static final Color         GREEN_1  = Color.rgb(56, 142, 60);
    private static final Color         GREEN_2  = Color.rgb(76, 175, 80);

    private static       int           noOfNodes = 0;

    private TreeNode<ChartItem>      tree;
    private SunburstChart nonInteractiveSunburstChart;
    private SunburstChart interactiveSunburstChart;

    @Override public void init() {
        tree            = new TreeNode<>(new ChartItem("ROOT"));
        TreeNode<ChartItem>first  = new TreeNode<>(new ChartItem("1st", 8.3, PETROL_0), tree);
        TreeNode<ChartItem>second = new TreeNode<>(new ChartItem("2nd", 2.2, PINK_0), tree);
        TreeNode<ChartItem>third  = new TreeNode<>(new ChartItem("3rd", 1.4, YELLOW_0), tree);
        TreeNode<ChartItem>fourth = new TreeNode<>(new ChartItem("4th", 1.2, GREEN_0), tree);

        TreeNode<ChartItem>jan = new TreeNode<>(new ChartItem("Jan", 3.5, PETROL_1), first);
        TreeNode<ChartItem>feb = new TreeNode<>(new ChartItem("Feb", 3.1, PETROL_1), first);
        TreeNode<ChartItem>mar = new TreeNode<>(new ChartItem("Mar", 1.7, PETROL_1), first);
        TreeNode<ChartItem>apr = new TreeNode<>(new ChartItem("Apr", 1.1, PINK_1), second);
        TreeNode<ChartItem>may = new TreeNode<>(new ChartItem("May", 0.8, PINK_1), second);
        TreeNode<ChartItem>jun = new TreeNode<>(new ChartItem("Jun", 0.3, PINK_1), second);
        TreeNode<ChartItem>jul = new TreeNode<>(new ChartItem("Jul", 0.7, YELLOW_1), third);
        TreeNode<ChartItem>aug = new TreeNode<>(new ChartItem("Aug", 0.6, YELLOW_1), third);
        TreeNode<ChartItem>oct = new TreeNode<>(new ChartItem("Oct", 0.5, GREEN_1), fourth);
        TreeNode<ChartItem>nov = new TreeNode<>(new ChartItem("Nov", 0.4, GREEN_1), fourth);
        TreeNode<ChartItem>dec = new TreeNode<>(new ChartItem("Dec", 0.3, GREEN_1), fourth);

        TreeNode<ChartItem>week5 = new TreeNode<>(new ChartItem("Week 5", 1.2, PETROL_2), feb);
        TreeNode<ChartItem>week6 = new TreeNode<>(new ChartItem("Week 6", 0.8, PETROL_2), feb);
        TreeNode<ChartItem>week7 = new TreeNode<>(new ChartItem("Week 7", 0.6, PETROL_2), feb);
        TreeNode<ChartItem>week8 = new TreeNode<>(new ChartItem("Week 8", 0.5, PETROL_2), feb);

        TreeNode<ChartItem>week19 = new TreeNode<>(new ChartItem("Week 19", 0.3, PINK_2), may);

        tree.setOnTreeNodeEvent(e -> {
            TreeNodeEventType type = e.getType();
            if (TreeNodeEventType.NODE_SELECTED == type) {
                TreeNode<ChartItem> segment = e.getSource();
                System.out.println(segment.getItem().getName() + ": " + segment.getItem().getValue());
            }
        });

        nonInteractiveSunburstChart = SunburstChartBuilder.create()
                                                          .prefSize(400, 400)
                                                          .tree(tree)
                                                          .textOrientation(TextOrientation.TANGENT)
                                                          .useColorFromParent(false)
                                                          .visibleData(VisibleData.NAME)
                                                          .backgroundColor(Color.WHITE)
                                                          //.textColor(Color.WHITE)
                                                          .autoTextColor(true)
                                                          .useChartItemTextColor(true)
                                                          .decimals(1)
                                                          .interactive(false)
                                                          .build();

        interactiveSunburstChart = SunburstChartBuilder.create()
                                                       .prefSize(400, 400)
                                                       .tree(tree)
                                                       .textOrientation(TextOrientation.TANGENT)
                                                       .useColorFromParent(false)
                                                       .visibleData(VisibleData.NAME)
                                                       .backgroundColor(Color.WHITE)
                                                       //.textColor(Color.WHITE)
                                                       .autoTextColor(true)
                                                       .useChartItemTextColor(true)
                                                       .decimals(1)
                                                       .interactive(true)
                                                       .build();
    }

    @Override public void start(Stage stage) {
        GridPane pane           = new GridPane();
        Label    nonInteractive = new Label("Non Interactive");
        Label    interactive    = new Label("Interactive");

        pane.add(nonInteractive, 0, 0);
        pane.add(nonInteractiveSunburstChart, 0, 1);
        pane.add(interactive, 1, 0);
        pane.add(interactiveSunburstChart, 1, 1);

        GridPane.setHalignment(nonInteractive, HPos.CENTER);
        GridPane.setHalignment(interactive, HPos.CENTER);

        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Sunburst Chart");
        stage.setScene(scene);
        stage.show();

        interactiveSunburstChart.setAutoTextColor(true);


        // Calculate number of nodes
        calcNoOfNodes(nonInteractiveSunburstChart);
        System.out.println(noOfNodes + " Nodes in non interactive Sunburst Chart");

        noOfNodes = 0;
        calcNoOfNodes(interactiveSunburstChart);
        System.out.println(noOfNodes + " Nodes in interactive Sunburst Chart");
    }

    @Override public void stop() {
        System.exit(0);
    }


    // ******************** Misc **********************************************
    private static void calcNoOfNodes(Node node) {
        if (node instanceof Parent) {
            if (((Parent) node).getChildrenUnmodifiable().size() != 0) {
                ObservableList<Node> tempChildren = ((Parent) node).getChildrenUnmodifiable();
                noOfNodes += tempChildren.size();
                for (Node n : tempChildren) { calcNoOfNodes(n); }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
