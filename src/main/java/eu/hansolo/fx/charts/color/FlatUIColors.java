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

package eu.hansolo.fx.charts.color;

import eu.hansolo.fx.charts.tools.Helper;
import javafx.scene.paint.Color;


public enum FlatUIColors implements Colors {
    /**
     * The color TURQOISE with an RGB value of colorToRGB(31, 188, 156).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(31, 188, 156);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    TURQOISE(31, 188, 156),

    /**
     * The color GREEN SEA with an RGB value of colorToRGB(26, 160, 133).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(26, 160, 133);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    GREEN_SEA(26, 160, 133),

    /**
     * The color EMERLAND with an RGB value of colorToRGB(87, 214, 141).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(87, 214, 141);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    EMERLAND(87, 214, 141),

    /**
     * The color NEPHRITIS with an RGB value of colorToRGB(39, 174, 96).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(39, 174, 96);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    NEPHRITIS(39, 174, 96),

    /**
     * The color PETER RIVER with an RGB value of colorToRGB(92, 172, 226).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(92, 172, 226);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    PETER_RIVER(92, 172, 226),

    /**
     * The color BELIZE_HOLE with an RGB value of colorToRGB(83, 153, 198).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(83, 153, 198);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    BELIZE_HOLE(83, 153, 198),

    /**
     * The color AMETHYST with an RGB value of colorToRGB(175, 122, 196).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(175, 122, 196);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    AMETHYST(175, 122, 196),

    /**
     * The color WISTERIA with an RGB value of colorToRGB(142, 68, 173).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(142, 68, 173);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    WISTERIA(142, 68, 173),

    /**
     * The color SUNFLOWER with an RGB value of colorToRGB(241, 196, 40).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(241, 196, 40);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    SUNFLOWER(241, 196, 40),

    /**
     * The color ORANGE with an RGB value of colorToRGB(245, 175, 65).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(245, 175, 65);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    ORANGE(245, 175, 65),

    /**
     * The color CARROT with an RGB value of colorToRGB(245, 175, 65).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(245, 175, 65);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    CARROT(245, 175, 65),

    /**
     * The color PUMPKIN with an RGB value of colorToRGB(211, 85, 25).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(211, 85, 25);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    PUMPKIN(211, 85, 25),

    /**
     * The color ALIZARIN with an RGB value of colorToRGB(234, 111, 99).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(234, 111, 99);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    ALIZARIN(234, 111, 99),

    /**
     * The color POMEGRANATE with an RGB value of colorToRGB(204, 96, 85).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(204, 96, 85);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    POMEGRANATE(204, 96, 85),

    /**
     * The color CLOUDS with an RGB value of colorToRGB(239, 243, 243).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(239, 243, 243);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    CLOUDS(239, 243, 243),

    /**
     * The color SILVER with an RGB value of colorToRGB(189, 195, 199).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(189, 195, 199);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    SILVER(189, 195, 199),

    /**
     * The color CONCRETE with an RGB value of colorToRGB(149, 165, 166).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(149, 165, 166);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    CONCRETE(149, 165, 166),

    /**
     * The color ASBESTOS with an RGB value of colorToRGB(127, 140, 141).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(127, 140, 141);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    ASBESTOS(127, 140, 141),

    /**
     * The color WET ASPHALT with an RGB value of colorToRGB(52, 73, 94).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(52, 73, 94);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    WET_ASPHALT(52, 73, 94),

    /**
     * The color MIDNIGHT BLUE with an RGB value of colorToRGB(44, 62, 80).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(44, 62, 80);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    MIDNIGHT_BLUE(44, 62, 80);

    private final Color COLOR;

    FlatUIColors(final int R, final int G, final int B) {
        COLOR = Color.rgb(R, G, B);
    }

    @Override public Color get() { return COLOR; }

    @Override public String rgb() { return Helper.colorToRGB(COLOR); }

    @Override public String rgba(final double OPACITY) { return Helper.colorToRGBA(COLOR, OPACITY); }

    @Override public String web() { return Helper.colorToWeb(COLOR); }
}
