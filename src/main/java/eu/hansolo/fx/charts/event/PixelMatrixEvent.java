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

import eu.hansolo.fx.charts.tools.Point;


public class PixelMatrixEvent {
    private final int       X;
    private final int       Y;
    private final double    MOUSE_SCREEN_X;
    private final double    MOUSE_SCREEN_Y;
    private final EventType TYPE;


    // ******************** Constructors **************************************
    public PixelMatrixEvent(final int X, final int Y, final double MOUSE_X, final double MOUSE_Y) {
        this(X, Y, MOUSE_X, MOUSE_Y, EventType.UPDATE);
    }
    public PixelMatrixEvent(final int X, final int Y, final double MOUSE_X, final double MOUSE_Y, final EventType TYPE) {
        this.X              = X;
        this.Y              = Y;
        this.MOUSE_SCREEN_X = MOUSE_X;
        this.MOUSE_SCREEN_Y = MOUSE_Y;
        this.TYPE           = TYPE;
    }


    // ******************** Methods *******************************************
    public int getX() { return X; }
    public int getY() { return Y; }

    public double getMouseScreenX() { return MOUSE_SCREEN_X; }
    public double getMouseScreenY() { return MOUSE_SCREEN_Y; }
    public Point getMouseScreenPos() { return new Point(MOUSE_SCREEN_X, MOUSE_SCREEN_Y); }

    public EventType getEventType() { return TYPE; }
}