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

import eu.hansolo.fx.charts.data.Item;
import javafx.scene.input.MouseEvent;


public class ItemEvent<T extends Item> {
    private final EventType TYPE;
    private final T         ITEM;
    private final T         TARGET_ITEM;
    
    // (nullable) the orginal mouse event with additional information
    private final MouseEvent MOUSE_EVENT;


    // ******************** Constructors **************************************
    public ItemEvent(final EventType TYPE) {
        this(null, null, TYPE, null);
    }
    public ItemEvent(final T ITEM) {
        this(ITEM, null, EventType.UPDATE, null);
    }
    public ItemEvent(final T ITEM, final EventType TYPE) {
        this(ITEM, null, TYPE, null);
    }
    public ItemEvent(final T ITEM, final EventType TYPE, final MouseEvent MOUSE_EVENT) {
        this(ITEM, null, TYPE, MOUSE_EVENT);
    }
    public ItemEvent(final T ITEM, final T TARGET_ITEM, final EventType TYPE) {
    	this(ITEM, TARGET_ITEM, TYPE, null);
    }
    public ItemEvent(final T ITEM, final T TARGET_ITEM, final EventType TYPE, final MouseEvent MOUSE_EVENT) {
        this.ITEM        = ITEM;
        this.TYPE        = TYPE;
        this.TARGET_ITEM = TARGET_ITEM;
        this.MOUSE_EVENT = MOUSE_EVENT;
    }


    // ******************** Methods *******************************************
    public T getItem() { return ITEM; }

    public T getTargetItem() { return TARGET_ITEM; }

    public EventType getEventType() { return TYPE; }

    public MouseEvent getMouseEvent() { return MOUSE_EVENT; }
}
