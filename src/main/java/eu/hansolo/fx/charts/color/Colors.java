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

import javafx.scene.paint.Color;


public interface Colors {

    /**
     * Returns the corresponding JavaFX color.
     *
     * @return the corresponding JavaFX color.
     */
    public Color get();

    /**
     * Returns a String expression from the color with the format: colorToRGB(12, 121, 15)
     *
     * @return the String expression.
     */
    public String rgb();

    /**
     * Returns a String expression from the color and opacity with the format: colorToRGBA(12, 121, 15, 0.5)
     *
     * @return the String expression.
     */
    public String rgba(final double OPACITY);

    /**
     * Returns a String expression from the color with the format: #AB12CD
     *
     * @return the String expression.
     */
    public String web();
}