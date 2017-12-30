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

package eu.hansolo.fx.charts.event;


import eu.hansolo.fx.charts.data.ChartItem;


public class ChartItemEvent {
    public enum EventType { UPDATE, FINISHED }

    private ChartItem item;
    private EventType type;


    // ******************** Constructors **************************************
    public ChartItemEvent(final EventType TYPE, final ChartItem ITEM) {
        type = TYPE;
        item = ITEM;
    }


    // ******************** Methods *******************************************
    public EventType getType() { return type; }

    public ChartItem getItem() { return item; }
}
