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

import eu.hansolo.fx.charts.wafermap.KLA;
import eu.hansolo.fx.charts.wafermap.KLAParser;
import eu.hansolo.fx.charts.wafermap.Wafermap;
import eu.hansolo.fx.charts.wafermap.WafermapBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.Random;


public class WafermapTest extends Application {
    private static final Random   RND = new Random();
    private              Wafermap wafermap;


    @Override public void init() {
        String        filename = WafermapTest.class.getResource("12.KLA").toString().replace("file:", "");
        Optional<KLA> klaOpt   = KLAParser.INSTANCE.parse(filename);

        wafermap = WafermapBuilder.create()
                                  .kla(klaOpt.get())
                                  .dieTextVisible(true)
                                  .densityColorsVisible(true)
                                  .wafermapFill(Color.rgb(240, 240, 240))
                                  .wafermapStroke(Color.GRAY)
                                  .dieTextFill(Color.BLACK)
                                  .build();

        registerListener();
    }

    private void registerListener() {

    }

    @Override public void start(Stage stage) {
        StackPane pane  = new StackPane(wafermap);
        Scene     scene = new Scene(pane);

        stage.setTitle("Wafermap");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        wafermap.dispose();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
