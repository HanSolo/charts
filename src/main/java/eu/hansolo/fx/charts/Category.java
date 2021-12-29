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

package eu.hansolo.fx.charts;

import eu.hansolo.fx.charts.event.ChartEvt;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolboxfx.evt.type.LocationChangeEvt;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class Category implements Comparable<Category> {
    private final ChartEvt                                  UPDATE_EVT = new ChartEvt(Category.this, ChartEvt.UPDATE);
    private final String                                    name;
    private       Color                                     _fill;
    private       ObjectProperty<Color>                     fill;
    private       Color                                     _stroke;
    private       ObjectProperty<Color>                     stroke;
    private       Color                                     _textFill;
    private       ObjectProperty<Color>                     textFill;
    private       double                                    _value;
    private       DoubleProperty                            value;
    private       Map<EvtType, List<EvtObserver<ChartEvt>>> observers;


    // ******************** Constructors **************************************
    public Category(final String name) { this(name, Color.LIGHTGRAY, Color.TRANSPARENT, Color.BLACK); }
    public Category(final String name, final Color fill) {
        this(name, fill, Color.TRANSPARENT, Color.BLACK);
    }
    public Category(final String name, final Color fill, final Color stroke, final Color textFill) {
        this.name      = name;
        this._fill     = fill;
        this._stroke   = stroke;
        this._textFill = textFill;
        this._value    = 0;
        this.observers = new ConcurrentHashMap<>();
    }


    // ******************** Methods *******************************************
    public String getName() { return name; }

    public Color getFill() { return null == fill ? _fill : fill.get(); }
    public void setFill(final Color fill) {
        if (null == this.fill) {
            _fill = fill;
            fireChartEvt(UPDATE_EVT);
        } else {
            this.fill.set(fill);
        }
    }
    public ObjectProperty<Color> fillProperty() {
        if (null == fill) {
            fill  = new ObjectPropertyBase<>(_fill) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVT); }
                @Override public Object getBean() { return Category.this; }
                @Override public String getName() { return "fill"; }
            };
            _fill = null;
        }
        return fill;
    }

    public Color getStroke() { return null == stroke ? _stroke : stroke.get(); }
    public void setStroke(final Color stroke) {
        if (null == this.stroke) {
            _stroke = stroke;
            fireChartEvt(UPDATE_EVT);
        } else {
            this.stroke.set(stroke);
        }
    }
    public ObjectProperty<Color> strokeProperty() {
        if (null == stroke) {
            stroke = new ObjectPropertyBase<>(_stroke) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVT); }
                @Override public Object getBean() { return Category.this; }
                @Override public String getName() { return "stroke"; }
            };
            _stroke = null;
        }
        return stroke;
    }

    public Color getTextFill() { return null == textFill ? _textFill : textFill.get(); }
    public void setTextFill(final Color textFill) {
        if (null == this.textFill) {
            _textFill = textFill;
            fireChartEvt(UPDATE_EVT);
        } else {
            this.textFill.set(textFill);
        }
    }
    public ObjectProperty<Color> textFillProperty() {
        if (null == textFill) {
            textFill = new ObjectPropertyBase<>(_textFill) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVT); }
                @Override public Object getBean() { return Category.this; }
                @Override public String getName() { return "textFill"; }
            };
            _textFill = null;
        }
        return textFill;
    }

    public double getValue() { return null == value ? _value : value.get(); }
    public void setValue(final double value) {
        if (null == this.value) {
            _value = value;
            fireChartEvt(UPDATE_EVT);
        } else {
            this.value.set(value);
        }
    }
    public DoubleProperty valueProperty() {
        if (null == value) {
            value = new DoublePropertyBase(_value) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVT); }
                @Override public Object getBean() { return Category.this; }
                @Override public String getName() { return "value"; }
            };
        }
        return value;
    }

    @Override public int compareTo(final Category other) {
        return getName().compareTo(other.getName());
    }


    // ******************** Event handling ************************************
    public void addChartEvtObserver(final EvtType type, final EvtObserver<ChartEvt> observer) {
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public void removeChartEvtObserver(final EvtType type, final EvtObserver<ChartEvt> observer) {
        if (observers.containsKey(type)) {
            if (observers.get(type).contains(observer)) {
                observers.get(type).remove(observer);
            }
        }
    }
    public void removeAllChartEvtObservers() { observers.clear(); }

    public void fireChartEvt(final ChartEvt evt) {
        final EvtType type = evt.getEvtType();
        observers.entrySet().stream().filter(entry -> entry.getKey().equals(LocationChangeEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (observers.containsKey(type)) {
            observers.get(type).forEach(observer -> observer.handle(evt));
        }
    }
}
