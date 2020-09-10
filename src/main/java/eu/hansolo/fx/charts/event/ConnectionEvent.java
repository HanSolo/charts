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


import eu.hansolo.fx.charts.data.Connection;


public class ConnectionEvent<T extends Connection> {
    private final EventType TYPE;
    private final T         CONNECTION;


    // ******************** Constructors **************************************
    public ConnectionEvent(final EventType TYPE) {
        this(null, TYPE);
    }
    public ConnectionEvent(final T CONNECTION) {
        this(CONNECTION, EventType.UPDATE);
    }
    public ConnectionEvent(final T CONNECTION, final EventType TYPE) {
        this.CONNECTION = CONNECTION;
        this.TYPE = TYPE;
    }


    // ******************** Methods *******************************************
    public T getConnection() { return CONNECTION; }

    public EventType getEventType() { return TYPE; }
}

