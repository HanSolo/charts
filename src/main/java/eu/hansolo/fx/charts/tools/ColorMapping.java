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

package eu.hansolo.fx.charts.tools;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;


public enum ColorMapping {
    LIME_YELLOW_RED(new Stop(0.0, Color.LIME), new Stop(0.8, Color.YELLOW), new Stop(1.0, Color.RED)),
    BLUE_CYAN_GREEN_YELLOW_RED(new Stop(0.0, Color.BLUE), new Stop(0.25, Color.CYAN), new Stop(0.5, Color.LIME), new Stop(0.75, Color.YELLOW), new Stop(1.0, Color.RED)),
    INFRARED_1(new Stop(0.0, Color.BLACK), new Stop(0.1, Color.rgb(25, 20, 126)), new Stop(0.3, Color.rgb(192, 40, 150)), new Stop(0.5, Color.rgb(234, 82, 10)), new Stop(0.85, Color.rgb(255, 220, 25)), new Stop(1.0, Color.WHITE)),
    INFRARED_2(new Stop(0.0, Color.BLACK), new Stop(0.1, Color.rgb(1, 20, 127)), new Stop(0.2, Color.rgb(1, 13, 100)), new Stop(0.4, Color.rgb(95, 172, 68)), new Stop(0.5, Color.rgb(210, 197, 12)), new Stop(0.65, Color.rgb(225, 53, 56)), new Stop(1.0, Color.WHITE)),
    INFRARED_3(new Stop(0.0, Color.BLACK), new Stop(0.25, Color.rgb(142, 6, 10)), new Stop(0.5, Color.rgb(253, 163, 43)), new Stop(0.75, Color.rgb(255, 251, 63)), new Stop(1.0, Color.WHITE)),
    INFRARED_4(new Stop(0.0, Color.BLACK), new Stop(0.14286, Color.rgb(60, 33, 177)), new Stop(0.28571, Color.rgb(165, 36, 174)), new Stop(0.42857, Color.rgb(218, 36, 48)), new Stop(0.57143, Color.rgb(253, 119, 35)), new Stop(0.71429, Color.rgb(253, 184, 48)), new Stop(0.85714, Color.rgb(254, 245, 79)), new Stop(1.0, Color.WHITE)),
    BLUE_GREEN_RED(new Stop(0.0, Color.rgb(12, 51, 250)), new Stop(0.16667, Color.rgb(13, 104, 151)), new Stop(0.33333, Color.rgb(31, 200, 67)), new Stop(0.5, Color.rgb(33, 214, 46)), new Stop(0.66667, Color.rgb(109, 143, 29)), new Stop(0.83333, Color.rgb(186, 72, 21)), new Stop(1.0, Color.rgb(252, 20, 27))),
    BLUE_BLACK_RED(new Stop(0.0, Color.rgb(2, 20, 125)), new Stop(0.25, Color.rgb(10, 47, 234)), new Stop(0.5, Color.rgb(0, 0, 0)), new Stop(0.75, Color.rgb(252, 20, 27)), new Stop(1.0, Color.rgb(142, 6, 10))),
    BLUE_YELLOW_RED(new Stop(0.0, Color.rgb(2, 20, 125)), new Stop(0.25, Color.rgb(10, 47, 234)), new Stop(0.5, Color.rgb(255, 255, 63)), new Stop(0.75, Color.rgb(252, 20, 27)), new Stop(1.0, Color.rgb(142, 6, 10))),
    BLUE_TRANSPARENT_RED(new Stop(0.0, Color.rgb(2, 20, 125)), new Stop(0.25, Color.rgb(10, 47, 234)), new Stop(0.5, Color.TRANSPARENT), new Stop(0.75, Color.rgb(252, 20, 27)), new Stop(1.0, Color.rgb(142, 6, 10))),
    GREEN_BLACK_RED(new Stop(0.0, Color.rgb(9, 98, 16)), new Stop(0.25, Color.rgb(41, 251, 56)), new Stop(0.5, Color.rgb(0, 0, 0)), new Stop(0.75, Color.rgb(252, 20, 27)), new Stop(1.0, Color.rgb(142, 6, 10))),
    GREEN_YELLOW_RED(new Stop(0.0, Color.rgb(9, 98, 16)), new Stop(0.25, Color.rgb(41, 251, 56)), new Stop(0.5, Color.rgb(255, 255, 63)), new Stop(0.75, Color.rgb(252, 20, 27)), new Stop(1.0, Color.rgb(142, 6, 10))),
    RAINBOW(new Stop(0.0, Color.rgb(142, 6, 10)), new Stop(0.125, Color.rgb(252, 20, 27)), new Stop(0.25, Color.rgb(253, 163, 43)), new Stop(0.375, Color.rgb(255, 251, 63)), new Stop(0.5, Color.rgb(41, 251, 56)), new Stop(0.625, Color.rgb(12, 51, 250)), new Stop(0.75, Color.rgb(3, 23, 136)), new Stop(0.875, Color.rgb(74, 22, 127)), new Stop(1.0, Color.rgb(240, 136, 235))),
    BLACK_WHITE(new Stop(0.0, Color.BLACK), new Stop(1.0, Color.WHITE)),
    WHITE_BLACK(new Stop(0.0, Color.WHITE), new Stop(1.0, Color.BLACK));

    private final LinearGradient gradient;

    private final Stop[] stops;

    ColorMapping(final Stop... STOPS) {
        this.stops    = STOPS;
        this.gradient = new LinearGradient(0, 0, 100, 0, false, CycleMethod.NO_CYCLE, STOPS);
    }


    public Stop[] getStops() { return stops; }

    public LinearGradient getGradient() { return gradient; }
}
