/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
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

package eu.hansolo.fx.charts.data;

import eu.hansolo.fx.charts.Category;
import eu.hansolo.fx.charts.event.CategoryEvent;
import eu.hansolo.fx.charts.event.CategoryEventListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.paint.Color;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;


public class DayOfWeekCategory extends Category {
    private DayOfWeek dayOfWeek;


    // ******************** Constructors **************************************
    public DayOfWeekCategory(final DayOfWeek dayOfWeek, final Color color) {
        super(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()), color);
        this.dayOfWeek = dayOfWeek;
    }


    // ******************** Methods *******************************************
    @Override public String getName() { return getName(TextStyle.FULL, Locale.getDefault()); }
    public String getName(final TextStyle textStyle) { return getName(textStyle, Locale.getDefault()); }
    public String getName(final TextStyle textStyle, final Locale locale) { return dayOfWeek.getDisplayName(textStyle, locale); }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
}
