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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Category {
    private final String                      name;
    private       Color                       _color;
    private       ObjectProperty<Color>       color;
    private       List<CategoryEventListener> listeners;


    // ******************** Constructors **************************************
    public Category(final String name) { this(name, Color.LIGHTGRAY); }
    public Category(final String name, final Color color) {
        this.name      = name;
        this._color    = color;
        this.listeners = new CopyOnWriteArrayList<>();
    }


    // ******************** Methods *******************************************
    public String getName() { return name; }

    public Color getColor() { return null == color ? _color : color.get(); }
    public void setColor(final Color color) {
        if (null == this.color) {
            _color = color;
        } else {
            this.color.set(color);
        }
    }
    public ObjectProperty<Color> colorProperty() {
        if (null == color) {
            color = new ObjectPropertyBase<>(_color) {
                @Override protected void invalidated() {}
                @Override public Object getBean() { return Category.this; }
                @Override public String getName() { return "color"; }
            };
            _color = null;
        }
        return color;
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
