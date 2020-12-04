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

package eu.hansolo.fx.charts.event;

import eu.hansolo.fx.charts.data.MapConnection;


public class MapConnectionEvent<T extends MapConnection> {
    private final EventType TYPE;
    private final T         MAP_CONNECTION;


    // ******************** Constructors **************************************
    public MapConnectionEvent(final EventType TYPE) {
        this(null, TYPE);
    }
    public MapConnectionEvent(final T MAP_CONNECTION) {
        this(MAP_CONNECTION, EventType.UPDATE);
    }
    public MapConnectionEvent(final T MAP_CONNECTION, final EventType TYPE) {
        this.MAP_CONNECTION = MAP_CONNECTION;
        this.TYPE           = TYPE;
    }


    // ******************** Methods *******************************************
    public T getMapConnection() { return MAP_CONNECTION; }

    public EventType getEventType() { return TYPE; }
}

