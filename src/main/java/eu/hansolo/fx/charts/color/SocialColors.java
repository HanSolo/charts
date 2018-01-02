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


public enum SocialColors implements Colors {
    /**
     * The color FACEBOOK with an RGB value of colorToRGB(59,89,153).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(59,89,153);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    FACEBOOK(59,89,153),

    /**
     * The color TWITTER with an RGB value of colorToRGB(85,172,238).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(85,172,238);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    TWITTER(85,172,238),

    /**
     * The color LINKED IN with an RGB value of colorToRGB(0,119,181).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(0,119,181);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    LINKED_IN(0,119,181),

    /**
     * The color TUMBLR with an RGB value of colorToRGB(52,70,93).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(52,70,93);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    TUMBLR(52,70,93),

    /**
     * The color YAHOO with an RGB value of colorToRGB(65,0,147).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(65,0,147);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    YAHOO(65,0,147),

    /**
     * The color INSTAGRAM with an RGB value of colorToRGB().
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB();float:right;margin: 0 10px 0 0"></div><br><br>
     */
    INSTAGRAM(63,114,155),

    /**
     * The color SKYPE with an RGB value of colorToRGB(0,175,240).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(0,175,240);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    SKYPE(0,175,240),

    /**
     * The color WORDPRESS with an RGB value of colorToRGB(33,117,155).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(33,117,155);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    WORDPRESS(33,117,155),

    /**
     * The color VIMEO with an RGB value of colorToRGB(26,183,234).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(26,183,234);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    VIMEO(26,183,234),

    /**
     * The color VK with an RGB value of colorToRGB(76,117,163).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(76,117,163);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    VK(76,117,163),

    /**
     * The color SLIDE_SHARE with an RGB value of colorToRGB(0,119,181).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(0,119,181);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    SLIDE_SHARE(0,119,181),

    /**
     * The color GOOGLE PLUS with an RGB value of colorToRGB(220,78,65).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(220,78,65);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    GOOGLE_PLUS(220,78,65),

    /**
     * The color PINTEREST with an RGB value of colorToRGB(189,8,28).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(189,8,28);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    PINTEREST(189,8,28),

    /**
     * The color YOUTUBE with an RGB value of colorToRGB(229,45,39).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(229,45,39);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    YOUTUBE(229,45,39),

    /**
     * The color STUMBLE UPON with an RGB value of colorToRGB(235,73,36).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(235,73,36);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    STUMBLE_UPON(235,73,36),

    /**
     * The color REDDIT with an RGB value of colorToRGB(255,87,0).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(255,87,0);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    REDDIT(255,87,0),

    /**
     * The color QUORA with an RGB value of colorToRGB(185,43,39).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(185,43,39);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    QUORA(185,43,39),

    /**
     * The color WEIBO with an RGB value of colorToRGB(223,32,41).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(223,32,41);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    WEIBO(223,32,41),

    /**
     * The color PRODUCT HUNT with an RGB value of colorToRGB(218,85,47).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(218,85,47);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    PRODUCT_HUNT(218,85,47),

    /**
     * The color HACKER NEWS with an RGB value of colorToRGB(255,102,0).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(255,102,0);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    HACKER_NEWS(255,102,0),

    /**
     * The color WHATS APP with an RGB value of colorToRGB(37,211,102).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(37,211,102);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    WHATS_APP(37,211,102),

    /**
     * The color WE CHAT with an RGB value of colorToRGB(9,184,62).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(9,184,62);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    WE_CHAT(9,184,62),

    /**
     * The color MEDIUM with an RGB value of colorToRGB(2,184,117).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(2,184,117);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    MEDIUM(2,184,117),

    /**
     * The color VINE with an RGB value of colorToRGB(0,180,137).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(0,180,137);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    VINE(0,180,137),

    /**
     * The color SLACK with an RGB value of colorToRGB(58,175,133).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(58,175,133);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    SLACK(58,175,133),

    /**
     * The color DRIBBLE with an RGB value of colorToRGB(234,76,137).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(234,76,137);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    DRIBBLE(234,76,137),

    /**
     * The color FLICKR with an RGB value of colorToRGB(255,0,132).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(255,0,132);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    FLICKR(255,0,132),

    /**
     * The color FOUR SQUARE with an RGB value of colorToRGB(249,72,119).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(249,72,119);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    FOUR_SQUARE(249,72,119),

    /**
     * The color BEHANCE with an RGB value of colorToRGB(19,20,24).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(19,20,24);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    BEHANCE(19,20,24),

    /**
     * The color SNAP CHAT with an RGB value of colorToRGB(255,252,0).
     * <div style="border:1px solid black;width:40px;height:20px;background-color:colorToRGB(255,252,0);float:right;margin: 0 10px 0 0"></div><br><br>
     */
    SNAP_CHAT(255,252,0);

    private final Color COLOR;

    SocialColors(final int R, final int G, final int B) {
        COLOR = Color.rgb(R, G, B);
    }

    @Override public Color get() { return COLOR; }

    @Override public String rgb() { return Helper.colorToRGB(COLOR); }

    @Override public String rgba(final double OPACITY) { return Helper.colorToRGBA(COLOR, OPACITY); }

    @Override public String web() { return Helper.colorToWeb(COLOR); }
}
