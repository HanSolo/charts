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

import javafx.scene.paint.Color;

import java.time.DayOfWeek;
import java.time.Month;


public class Categories {
    public static final MonthCategory     JANUARY   = new MonthCategory(Month.JANUARY, Color.web("#03acc4"));
    public static final MonthCategory     FEBRUARY  = new MonthCategory(Month.FEBRUARY, Color.web("#e65667"));
    public static final MonthCategory     MARCH     = new MonthCategory(Month.MARCH, Color.web("#1eab58"));
    public static final MonthCategory     APRIL     = new MonthCategory(Month.APRIL, Color.web("#f5b55b"));
    public static final MonthCategory     MAY       = new MonthCategory(Month.MAY, Color.web("#ea94b9"));
    public static final MonthCategory     JUNE      = new MonthCategory(Month.JUNE, Color.web("#e5d64f"));
    public static final MonthCategory     JULY      = new MonthCategory(Month.JULY, Color.web("#ea8381"));
    public static final MonthCategory     AUGUST    = new MonthCategory(Month.AUGUST, Color.web("#e41a24"));
    public static final MonthCategory     SEPTEMBER = new MonthCategory(Month.SEPTEMBER, Color.web("#f5d514"));
    public static final MonthCategory     OCTOBER   = new MonthCategory(Month.OCTOBER, Color.web("#c76b17"));
    public static final MonthCategory     NOVEMBER  = new MonthCategory(Month.NOVEMBER, Color.web("#754203"));
    public static final MonthCategory     DECEMBER  = new MonthCategory(Month.DECEMBER, Color.web("#024639"));

    public static final DayOfWeekCategory MONDAY    = new DayOfWeekCategory(DayOfWeek.MONDAY, Color.LIGHTGRAY);
    public static final DayOfWeekCategory TUESDAY   = new DayOfWeekCategory(DayOfWeek.TUESDAY, Color.LIGHTGRAY);
    public static final DayOfWeekCategory WEDNESDAY = new DayOfWeekCategory(DayOfWeek.WEDNESDAY, Color.LIGHTGRAY);
    public static final DayOfWeekCategory THURSDAY  = new DayOfWeekCategory(DayOfWeek.THURSDAY, Color.LIGHTGRAY);
    public static final DayOfWeekCategory FRIDAY    = new DayOfWeekCategory(DayOfWeek.FRIDAY, Color.LIGHTGRAY);
    public static final DayOfWeekCategory SATURDAY  = new DayOfWeekCategory(DayOfWeek.SATURDAY, Color.RED);
    public static final DayOfWeekCategory SUNDAY    = new DayOfWeekCategory(DayOfWeek.SUNDAY, Color.RED);
}
