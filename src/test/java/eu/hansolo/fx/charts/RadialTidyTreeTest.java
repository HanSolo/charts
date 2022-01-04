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


import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.TreeNode;
import eu.hansolo.fx.charts.tools.VisibleData;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;


public class RadialTidyTreeTest extends Application {
    private static final Color PETROL_0 = Color.rgb(0, 96, 100);
    private static final Color PETROL_1 = Color.rgb(0, 151, 167);
    private static final Color PINK_0   = Color.rgb(136, 14, 79);
    private static final Color PINK_1   = Color.rgb(194, 24, 91);
    private static final Color YELLOW_0 = Color.rgb(245, 127, 23);
    private static final Color YELLOW_1 = Color.rgb(251, 192, 45);
    private static final Color GREEN_0  = Color.rgb(27, 94, 32);
    private static final Color GREEN_1  = Color.rgb(56, 142, 60);

    private static       int           noOfNodes = 0;

    private TreeNode<ChartItem> tree;
    private RadialTidyTree      radialTidyTree;

    @Override public void init() {
        tree = new TreeNode<>(new ChartItem(Year.now().toString(), Color.BLACK));
        TreeNode<ChartItem>firstQuarter  = new TreeNode<>(new ChartItem("1st", PETROL_0), tree);
        TreeNode<ChartItem>secondQuarter = new TreeNode<>(new ChartItem("2nd", PINK_0),   tree);
        TreeNode<ChartItem>thirdQuarter  = new TreeNode<>(new ChartItem("3rd", YELLOW_0), tree);
        TreeNode<ChartItem>fourthQuarter = new TreeNode<>(new ChartItem("4th", GREEN_0),  tree);

        TreeNode<ChartItem>jan = new TreeNode<>(new ChartItem("January", PETROL_1), firstQuarter);
        TreeNode<ChartItem>feb = new TreeNode<>(new ChartItem("February", PETROL_1), firstQuarter);
        TreeNode<ChartItem>mar = new TreeNode<>(new ChartItem("March", PETROL_1), firstQuarter);

        TreeNode<ChartItem>apr = new TreeNode<>(new ChartItem("April", PINK_1), secondQuarter);
        TreeNode<ChartItem>may = new TreeNode<>(new ChartItem("May", PINK_1), secondQuarter);
        TreeNode<ChartItem>jun = new TreeNode<>(new ChartItem("June", PINK_1), secondQuarter);

        TreeNode<ChartItem>jul = new TreeNode<>(new ChartItem("July", YELLOW_1), thirdQuarter);
        TreeNode<ChartItem>aug = new TreeNode<>(new ChartItem("August", YELLOW_1), thirdQuarter);
        TreeNode<ChartItem>sep = new TreeNode<>(new ChartItem("September", YELLOW_1), thirdQuarter);

        TreeNode<ChartItem>oct = new TreeNode<>(new ChartItem("October", GREEN_1), fourthQuarter);
        TreeNode<ChartItem>nov = new TreeNode<>(new ChartItem("November", GREEN_1), fourthQuarter);
        TreeNode<ChartItem>dec = new TreeNode<>(new ChartItem("December", GREEN_1), fourthQuarter);

        for (LocalDate date = LocalDate.of(Year.now().getValue(), 1, 1); date.isBefore(LocalDate.of(Year.now().getValue() + 1, 1, 1)) ; date = date.plusDays(1)) {
            Month month = date.getMonth();
            switch (month.getValue()) {
                case  1 -> new TreeNode<>(new ChartItem(Integer.toString(date.getDayOfMonth())), jan);
                case  2 -> new TreeNode<>(new ChartItem(Integer.toString(date.getDayOfMonth())), feb);
                case  3 -> new TreeNode<>(new ChartItem(Integer.toString(date.getDayOfMonth())), mar);
                case  4 -> new TreeNode<>(new ChartItem(Integer.toString(date.getDayOfMonth())), apr);
                case  5 -> new TreeNode<>(new ChartItem(Integer.toString(date.getDayOfMonth())), may);
                case  6 -> new TreeNode<>(new ChartItem(Integer.toString(date.getDayOfMonth())), jun);
                case  7 -> new TreeNode<>(new ChartItem(Integer.toString(date.getDayOfMonth())), jul);
                case  8 -> new TreeNode<>(new ChartItem(Integer.toString(date.getDayOfMonth())), aug);
                case  9 -> new TreeNode<>(new ChartItem(Integer.toString(date.getDayOfMonth())), sep);
                case 10 -> new TreeNode<>(new ChartItem(Integer.toString(date.getDayOfMonth())), oct);
                case 11 -> new TreeNode<>(new ChartItem(Integer.toString(date.getDayOfMonth())), nov);
                case 12 -> new TreeNode<>(new ChartItem(Integer.toString(date.getDayOfMonth())), dec);
            }
        }

        radialTidyTree = RadialTidyTreeBuilder.create()
                                              .prefSize(600, 600)
                                              .tree(tree)
                                              .useColorFromParent(true)
                                              .visibleData(VisibleData.NAME)
                                              .backgroundColor(Color.WHITE)
                                              //.textColor(Color.BLACK)
                                              .autoTextColor(true)
                                              .useChartItemTextColor(false)
                                              .decimals(1)
                                              .build();
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(radialTidyTree);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Radial Tidy Tree");
        stage.setScene(scene);
        stage.show();

        // Calculate number of nodes
        calcNoOfNodes(radialTidyTree);
        System.out.println(noOfNodes + " Nodes in Radial Tidy Tree");
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
