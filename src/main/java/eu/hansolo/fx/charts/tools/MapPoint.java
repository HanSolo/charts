/*
 * Copyright (c) 2020 by Gerrit Grunwald
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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;


public class MapPoint extends Point {
    private String                _name;
    private StringProperty        name;
    private Color                 _fill;
    private ObjectProperty<Color> fill;


    public MapPoint() {
        super();
        this._name = "";
        this._fill = Color.BLACK;
    }
    public MapPoint(final String NAME, final double LAT, final double LON) {
        this(NAME, Color.BLACK, LAT, LON);
    }
    public MapPoint(final String NAME, final Color FILL, final double LAT, final double LON) {
        super(LAT, LON);
        this._name = NAME;
        this._fill = FILL;
    }


    public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) {
        if (null == this.name) {
            _name = NAME;
        } else {
            this.name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override protected void invalidated() {}
                @Override public Object getBean() { return MapPoint.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    public Color getFill() { return null == fill ? _fill : fill.get(); }
    public void setFill(final Color FILL) {
        if (null == fill) {
            _fill = FILL;
        } else {
            fill.set(FILL);
        }
    }
    public ObjectProperty<Color> fillProperty() {
        if (null == fill) {
            fill = new ObjectPropertyBase<>(_fill) {
                @Override protected void invalidated() {}
                @Override public Object getBean() { return MapPoint.this; }
                @Override public String getName() { return "fill"; }
            };
            _fill = null;
        }
        return fill;
    }
}
