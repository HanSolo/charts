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


public enum MetroColors implements Colors {
    /**
     * The color LIGHT GREEN with an RGB value of rgb(153,180,51).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(153,180,51);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    LIGHT_GREEN(153,180,51),

    /**
     * The color GREEN with an RGB value of rgb(0,163,0).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(0,163,0);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    GREEN(0,163,0),

    /**
     * The color DARK GREEN with an RGB value of rgb(30,113,69).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(30,113,69);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    DARK_GREEN(30,113,69),

    /**
     * The color MAGENTA with an RGB value of rgb(255,0,151).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(255,0,151);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    MAGENTA(255,0,151),

    /**
     * The color LIGHT PURPLE with an RGB value of rgb(159,0,167).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(159,0,167);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    LIGHT_PURPLE(159,0,167),

    /**
     * The color PURPLE with an RGB value of rgb(126,56,120).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(126,56,120);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    PURPLE(126,56,120),

    /**
     * The color DARK PURPLE with an RGB value of rgb(96,60,186).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(96,60,186);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    DARK_PURPLE(96,60,186),

    /**
     * The color DARKEN with an RGB value of rgb(29,29,29).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(29,29,29);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    DARKEN(29,29,29),

    /**
     * The color TEAL with an RGB value of rgb(0,171,169).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(0,171,169);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    TEAL(0,171,169),

    /**
     * The color LIGHT BLUE with an RGB value of rgb(239,244,255).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(239,244,255);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    LIGHT_BLUE(239,244,255),

    /**
     * The color BLUE with an RGB value of rgb(45,137,239).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(45,137,239);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    BLUE(45,137,239),

    /**
     * The color DARK BLUE with an RGB value of rgb(43,87,151).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(43,87,151);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    DARK_BLUE(43,87,151),

    /**
     * The color YELLOW with an RGB value of rgb(255,196,13).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(255,196,13);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    YELLOW(255,196,13),

    /**
     * The color ORANGE with an RGB value of rgb(227,162,26).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(227,162,26);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    ORANGE(227,162,26),

    /**
     * The color DARK ORANGE with an RGB value of rgb(218,83,44).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(218,83,44);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    DARK_ORANGE(218,83,44),

    /**
     * The color RED with an RGB value of rgb(238,17,17).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(238,17,17);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    RED(238,17,17),

    /**
     * The color DARK RED with an RGB value of rgb(185,29,71).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:rgb(185,29,71);float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    DARK_RED(185,29,71);

    private final Color COLOR;

    MetroColors(final int R, final int G, final int B) {
        COLOR = Color.rgb(R, G, B);
    }

    @Override public Color get() { return COLOR; }

    @Override public String rgb() { return Helper.rgb(COLOR); }

    @Override public String rgba(final double OPACITY) { return Helper.rgba(COLOR, OPACITY); }

    @Override public String web() { return Helper.web(COLOR); }
}
