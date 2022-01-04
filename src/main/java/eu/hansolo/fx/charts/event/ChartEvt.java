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

package eu.hansolo.fx.charts.event;

import eu.hansolo.toolbox.evt.EvtPriority;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.ChangeEvt;
import javafx.scene.input.MouseEvent;


public class ChartEvt extends ChangeEvt {
    public static final EvtType<ChartEvt> ANY                      = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<ChartEvt> UPDATE                   = new EvtType<>(ChartEvt.ANY, "UPDATE");
    public static final EvtType<ChartEvt> FINISHED                 = new EvtType<>(ChartEvt.ANY, "FINISHED");
    public static final EvtType<ChartEvt> SELECTED                 = new EvtType<>(ChartEvt.ANY, "SELECTED");
    public static final EvtType<ChartEvt> CONNECTION_SELECTED_FROM = new EvtType<>(ChartEvt.ANY, "CONNECTION_SELECTED_FROM");
    public static final EvtType<ChartEvt> CONNECTION_SELECTED_TO   = new EvtType<>(ChartEvt.ANY, "CONNECTION_SELECTED_TO");
    public static final EvtType<ChartEvt> CONNECTION_SELECTED      = new EvtType<>(ChartEvt.ANY, "CONNECTION_SELECTED");
    public static final EvtType<ChartEvt> CONNECTION_UPDATE        = new EvtType<>(ChartEvt.ANY, "CONNECTION_UPDATE");
    public static final EvtType<ChartEvt> ITEM_UPDATE              = new EvtType<>(ChartEvt.ANY, "ITEM_UPDATE");
    public static final EvtType<ChartEvt> ITEM_SELECTED            = new EvtType<>(ChartEvt.ANY, "ITEM_SELECTED");
    public static final EvtType<ChartEvt> SERIES_SELECTED          = new EvtType<>(ChartEvt.ANY, "SERIES_SELECTED");
    public static final EvtType<ChartEvt> ITEM_AND_SERIES_SELECTED = new EvtType<>(ChartEvt.ANY, "ITEM_AND_SERIES_SELECTED");
    public static final EvtType<ChartEvt> AXIS_RANGE_CHANGED       = new EvtType<>(ChartEvt.ANY, "AXIS_RANGE_CHANGED");


    private final Object     target;
    private final MouseEvent mouseEvent;


    // ******************** Constructors **************************************
    public ChartEvt(final Object src, final EvtType<? extends ChartEvt> evtType) {
        super(src, evtType);
        this.target     = null;
        this.mouseEvent = null;
    }
    public ChartEvt(final Object src, final EvtType<? extends ChartEvt> evtType, final EvtPriority priority) {
        super(src, evtType, priority);
        this.target     = null;
        this.mouseEvent = null;
    }
    public ChartEvt(final Object src, final EvtType<? extends ChartEvt> evtType, final MouseEvent mouseEvent) {
        super(src, evtType);
        this.target     = null;
        this.mouseEvent = mouseEvent;
    }
    public ChartEvt(final Object src, final EvtType<? extends ChartEvt> evtType, final EvtPriority priority, final MouseEvent mouseEvent) {
        super(src, evtType, priority);
        this.target     = null;
        this.mouseEvent = mouseEvent;
    }

    public ChartEvt(final Object src, final Object target, final EvtType<? extends ChartEvt> evtType) {
        super(src, evtType);
        this.target     = target;
        this.mouseEvent = null;
    }
    public ChartEvt(final Object src, final Object target, final EvtType<? extends ChartEvt> evtType, final MouseEvent mouseEvent) {
        super(src, evtType);
        this.target     = target;
        this.mouseEvent = mouseEvent;
    }


    // ******************** Methods *******************************************
    public Object getTarget() { return target; }

    public MouseEvent getMouseEvent() { return mouseEvent; }
}
