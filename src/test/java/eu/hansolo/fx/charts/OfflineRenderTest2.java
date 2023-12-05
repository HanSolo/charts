/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2023 Gerrit Grunwald.
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

import eu.hansolo.fx.charts.SankeyPlot.StreamFillMode;
import eu.hansolo.fx.charts.data.PlotItem;
import eu.hansolo.fx.charts.event.ChartEvt;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;


public class OfflineRenderTest2 {
    private SankeyPlot sankeyPlot;
    private enum Colors {
        LIGHT_BLUE(Color.web("#a6cee3")),
        ORANGE(Color.web("#fdbf6f")),
        LIGHT_RED(Color.web("#fb9a99")),
        LIGHT_GREEN(Color.web("#b2df8a")),
        YELLOW(Color.web("#ffff99")),
        PURPLE(Color.web("#cab2d6")),
        BLUE(Color.web("#1f78b4")),
        GREEN(Color.web("#33a02c"));

        private Color color;
        private Color translucentColor;

        Colors(final Color COLOR) {
            color = COLOR;
            translucentColor = Helper.getColorWithOpacity(color, 0.75);
        }

        public Color get() { return color; }
        public Color getTranslucent() { return translucentColor; }
    }

    public OfflineRenderTest2() {
        Helper.initFXPlatform();
        init();

        testRenderToBufferedImage();
    }

    private void init() {
        // Setup chart items
        PlotItem brazil = new PlotItem("Brazil", Colors.LIGHT_BLUE.get(), 0);
        PlotItem mexico = new PlotItem("Mexico", Colors.ORANGE.get(), 0);
        PlotItem usa    = new PlotItem("USA", Colors.ORANGE.get(), 0);
        PlotItem canada = new PlotItem("Canada", Colors.LIGHT_RED.get(), 0);

        PlotItem germany     = new PlotItem("Germany", Color.web("#FF48C6"), 1);

        PlotItem portugal    = new PlotItem("Portugal", Colors.LIGHT_BLUE.get(), 1);
        PlotItem spain       = new PlotItem("Spain", Colors.LIGHT_GREEN.get(), 1);
        PlotItem england     = new PlotItem("England", Colors.LIGHT_RED.get(), 1);
        PlotItem france      = new PlotItem("France", Colors.LIGHT_GREEN.get(), 1);

        PlotItem southAfrica = new PlotItem("South Africa", Colors.YELLOW.get(), 2);
        PlotItem angola      = new PlotItem("Angola", Colors.PURPLE.get(), 2);
        PlotItem morocco     = new PlotItem("Morocco", Colors.YELLOW.get(), 2);
        PlotItem senegal     = new PlotItem("Senegal", Colors.PURPLE.get(), 2);
        PlotItem mali        = new PlotItem("Mali", Colors.BLUE.get(), 2);

        PlotItem china       = new PlotItem("China", Colors.BLUE.get(), 3);
        PlotItem japan       = new PlotItem("Japan", Colors.GREEN.get(), 3);
        PlotItem india       = new PlotItem("India", Colors.GREEN.get(), 3);

        PlotItem australia   = new PlotItem("Australia", Colors.ORANGE.get(), 4);

        // Setup vertical text positions
        //australia.setVerticalTextPosition(Position.TOP);
        germany.setVerticalTextPosition(Position.BOTTOM);

        // Setup item text color
        australia.setTextColor(Color.RED);
        germany.setTextColor(Color.MAGENTA);

        // Setup flows
        brazil.addToOutgoing(portugal, 5);
        brazil.addToOutgoing(france, 1);
        brazil.addToOutgoing(spain, 1);
        brazil.addToOutgoing(england, 1);
        canada.addToOutgoing(portugal, 1);
        canada.addToOutgoing(france, 5);
        canada.addToOutgoing(england, 1);
        mexico.addToOutgoing(portugal, 1);
        mexico.addToOutgoing(france, 1);
        mexico.addToOutgoing(spain, 5);
        mexico.addToOutgoing(england, 1);
        usa.addToOutgoing(portugal, 1);
        usa.addToOutgoing(france, 1);
        usa.addToOutgoing(spain, 1);
        usa.addToOutgoing(england, 5);

        germany.addToOutgoing(southAfrica, 5);

        portugal.addToOutgoing(angola, 2);
        portugal.addToOutgoing(senegal, 1);
        portugal.addToOutgoing(morocco, 1);
        portugal.addToOutgoing(southAfrica, 3);
        france.addToOutgoing(angola, 1);
        france.addToOutgoing(senegal, 3);
        france.addToOutgoing(mali, 3);
        france.addToOutgoing(morocco, 3);
        france.addToOutgoing(southAfrica, 1);
        spain.addToOutgoing(senegal, 1);
        spain.addToOutgoing(morocco, 3);
        spain.addToOutgoing(southAfrica, 1);
        england.addToOutgoing(angola, 1);
        england.addToOutgoing(senegal, 1);
        england.addToOutgoing(morocco, 2);
        england.addToOutgoing(southAfrica, 7);
        southAfrica.addToOutgoing(china, 5);
        southAfrica.addToOutgoing(india, 1);
        southAfrica.addToOutgoing(japan, 3);
        angola.addToOutgoing(china, 5);
        angola.addToOutgoing(india, 1);
        angola.addToOutgoing(japan, 3);
        senegal.addToOutgoing(china, 5);
        senegal.addToOutgoing(india, 1);
        senegal.addToOutgoing(japan, 3);
        mali.addToOutgoing(china, 5);
        mali.addToOutgoing(india, 1);
        mali.addToOutgoing(japan, 3);
        morocco.addToOutgoing(china, 5);
        morocco.addToOutgoing(india, 1);
        morocco.addToOutgoing(japan, 3);

        china.addToOutgoing(australia, 3);
        //japan.addToOutgoing(australia, 1);
        india.addToOutgoing(australia, 2);

        sankeyPlot = SankeyPlotBuilder.create()
                                      .prefSize(600, 400)
                                      .items(brazil, mexico, usa, canada,
                                             germany,
                                             portugal, spain, england, france,
                                             southAfrica, angola, morocco, senegal, mali,
                                             china, japan, india,
                                             australia)
                                      //.useItemColor(false)
                                      //.itemColor(Color.RED)
                                      .streamFillMode(StreamFillMode.GRADIENT)
                                      .selectionColor(Color.rgb(0, 100, 240, 0.5))
                                      //.streamColor(Color.rgb(200, 0, 0, 0.25))
                                      //.textColor(Color.RED)
                                      //.autoItemWidth(false)
                                      //.itemWidth(5)
                                      //.autoItemGap(false)
                                      //.itemGap(10)
                                      //.showFlowDirection(true)
                                      .useCustomFont(true)
                                      .useItemTextColor(true)
                                      .autoAdjustVerticalTextPosition(true)
                                      .build();
        sankeyPlot.getItems().forEach(item -> item.addChartEvtObserver(ChartEvt.ANY, e -> {
            if (ChartEvt.ITEM_SELECTED.equals(e.getEvtType())) {
                PlotItem sourceItem = (PlotItem) e.getSource();
                PlotItem targetItem = (PlotItem) e.getTarget();
                if (null != sourceItem && null != targetItem) {
                    double value = sourceItem.getOutgoing().get(targetItem);
                    System.out.println(sourceItem.getName() + " -> " + targetItem.getName() + " => connection value: " + value);
                } else if (null != sourceItem) {
                    double value = sourceItem.getSumOfOutgoing();
                    System.out.println(sourceItem.getName() + " => sum of outgoing values: " + value);
                }
            }
        }));
    }

    private void testRenderToFile() {
        sankeyPlot.renderToImage("sankeyplot", 800, 500);
        stop();
    }

    private void testRenderToBufferedImage() {
        BufferedImage bufferedImage = sankeyPlot.renderToImage(800, 500);
        System.out.println(bufferedImage.getWidth() + " x " + bufferedImage.getHeight());
        stop();
    }

    private void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        new OfflineRenderTest2();
    }
}
