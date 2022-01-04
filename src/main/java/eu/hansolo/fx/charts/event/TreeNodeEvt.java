/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2022 Gerrit Grunwald.
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

package eu.hansolo.fx.charts.event;

import eu.hansolo.fx.charts.data.Item;
import eu.hansolo.fx.charts.data.TreeNode;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.ChangeEvt;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class TreeNodeEvt<T extends Item> extends ChangeEvt {
    public static final EvtType<TreeNodeEvt> ANY              = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<TreeNodeEvt> PARENT_SET       = new EvtType<>(TreeNodeEvt.ANY, "PARENT_SET");
    public static final EvtType<TreeNodeEvt> PARENT_REMOVED   = new EvtType<>(TreeNodeEvt.ANY, "PARENT_REMOVED");
    public static final EvtType<TreeNodeEvt> CHILDREN_CHANGED = new EvtType<>(TreeNodeEvt.ANY, "CHILDREN_CHANGED");
    public static final EvtType<TreeNodeEvt> NODE_SELECTED    = new EvtType<>(TreeNodeEvt.ANY, "NODE_SELECTED");

    private final T                              item;
    private final EvtType<? extends TreeNodeEvt> type;


    // ******************** Constructors **************************************
    public TreeNodeEvt(final TreeNode<T> src, final T item) {
        this(src, TreeNodeEvt.NODE_SELECTED, item);
    }
    public TreeNodeEvt(final TreeNode<T> src, final EvtType<? extends TreeNodeEvt> type, final T item) {
        super(src, type);
        this.item = item;
        this.type = type;
    }


    // ******************** Methods *******************************************
    public T getItem() { return item; }

    @Override public EvtType<? extends TreeNodeEvt> getEvtType() {
        return type;
    }

    @Override public String toString() {
            return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                      .append(QUOTES).append("item").append(QUOTES).append(COLON).append(QUOTES).append(item.getName()).append(QUOTES)
                                      .append(CURLY_BRACKET_CLOSE).toString();
    }
}