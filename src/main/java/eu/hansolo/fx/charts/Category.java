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

import eu.hansolo.fx.charts.event.CategoryEvent;
import eu.hansolo.fx.charts.event.CategoryEventListener;
import eu.hansolo.fx.charts.event.EventType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Category implements Comparable<Category> {
    private static final CategoryEvent               UPDATE_EVT = new CategoryEvent(EventType.UPDATE);
    private        final String                      name;
    private              Color                       _fill;
    private              ObjectProperty<Color>       fill;
    private              Color                       _stroke;
    private              ObjectProperty<Color>       stroke;
    private              Color                       _textFill;
    private              ObjectProperty<Color>       textFill;
    private              List<CategoryEventListener> listeners;


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
        this.listeners = new CopyOnWriteArrayList<>();
    }


    // ******************** Methods *******************************************
    public String getName() { return name; }

    public Color getFill() { return null == fill ? _fill : fill.get(); }
    public void setFill(final Color fill) {
        if (null == this.fill) {
            _fill = fill;
            fireCategoryEvent(UPDATE_EVT);
        } else {
            this.fill.set(fill);
        }
    }
    public ObjectProperty<Color> fillProperty() {
        if (null == fill) {
            fill  = new ObjectPropertyBase<>(_fill) {
                @Override protected void invalidated() { fireCategoryEvent(UPDATE_EVT); }
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
            fireCategoryEvent(UPDATE_EVT);
        } else {
            this.stroke.set(stroke);
        }
    }
    public ObjectProperty<Color> strokeProperty() {
        if (null == stroke) {
            stroke = new ObjectPropertyBase<>(_stroke) {
                @Override protected void invalidated() { fireCategoryEvent(UPDATE_EVT); }
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
            fireCategoryEvent(UPDATE_EVT);
        } else {
            this.textFill.set(textFill);
        }
    }
    public ObjectProperty<Color> textFillProperty() {
        if (null == textFill) {
            textFill = new ObjectPropertyBase<>(_textFill) {
                @Override protected void invalidated() { fireCategoryEvent(UPDATE_EVT); }
                @Override public Object getBean() { return Category.this; }
                @Override public String getName() { return "textFill"; }
            };
            _textFill = null;
        }
        return textFill;
    }

    @Override public int compareTo(final Category other) {
        return getName().compareTo(other.getName());
    }


    // ******************** Event handling ************************************
    public void addCategoryEventListener(final CategoryEventListener listener) {
        if (listeners.contains(listener)) { return; }
        listeners.add(listener);
    }

    public void removeCategoryEventListener(final CategoryEventListener listener) {
        if (listeners.contains(listener)) { listeners.remove(listener); }
    }

    public void removeAllListeners() { listeners.clear(); }

    public void fireCategoryEvent(CategoryEvent event) {
        listeners.forEach(listener -> listener.onCategoryEvent(event));
    }
}
