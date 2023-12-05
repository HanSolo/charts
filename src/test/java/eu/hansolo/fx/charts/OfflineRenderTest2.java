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

        testRenderToFile();
        //testRenderToBufferedImage();
    }

    private void init() {
        PlotItem coal        = new PlotItem("Coal", Colors.GREEN.get(), 0);
        PlotItem naturalGas  = new PlotItem("Natural Gas", Colors.ORANGE.get().darker(), 0);
        PlotItem oil         = new PlotItem("Oil", Colors.ORANGE.get(), 0);
        PlotItem fossilFuels = new PlotItem("Fossil Fuels", Colors.BLUE.get(), 1);
        PlotItem electricity = new PlotItem("Electricity", Colors.LIGHT_BLUE.get(), 1);
        PlotItem energy      = new PlotItem("Energy", Color.GRAY, 2);

        coal.addToOutgoing(fossilFuels, 25);
        coal.addToOutgoing(electricity, 25);
        naturalGas.addToOutgoing(fossilFuels, 20);
        oil.addToOutgoing(fossilFuels, 15);
        fossilFuels.addToOutgoing(energy, 60);
        electricity.addToOutgoing(energy, 25);

        sankeyPlot = SankeyPlotBuilder.create()
                                      .items(coal, naturalGas, oil, fossilFuels, electricity, energy)
                                      .streamFillMode(StreamFillMode.GRADIENT)
                                      .build();
    }

    private void testRenderToFile() {
        sankeyPlot.renderToImage("sankeyplot", 800, 600);
        stop();
    }

    private void testRenderToBufferedImage() {
        BufferedImage bufferedImage = sankeyPlot.renderToImage(800, 600);
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
