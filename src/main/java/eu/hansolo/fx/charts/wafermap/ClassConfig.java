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

package eu.hansolo.fx.charts.wafermap;

import javafx.scene.paint.Color;


public class ClassConfig {
    private Color   fill;
    private Color   stroke;
    private boolean visible;


    // ******************** Constructors **************************************
    public ClassConfig(final Color fill) {
        this(fill, Color.TRANSPARENT, true);
    }
    public ClassConfig(final Color fill, final Color stroke, final boolean visible) {
        if (null == fill) { throw new IllegalArgumentException("Fill cannot be null"); }
        if (null == stroke) { throw new IllegalArgumentException("Stroke cannot be null"); }
        this.fill    = fill;
        this.stroke  = stroke;
        this.visible = visible;
    }


    // ******************** Methods *******************************************
    public Color getFill() { return this.fill; }
    public void setFill(final Color fill) {
        if (null == fill) { throw new IllegalArgumentException("Fill cannot be null"); }
        this.fill = fill;
    }

    public Color getStroke() { return this.stroke; }
    public void setStroke(final Color stroke) {
        if (null == stroke) { throw new IllegalArgumentException("Stroke cannot be null"); }
        this.stroke = stroke;
    }

    public Boolean isVisible() { return this.visible; }
    public void setVisible(final boolean visible) { this.visible = visible; }
}
