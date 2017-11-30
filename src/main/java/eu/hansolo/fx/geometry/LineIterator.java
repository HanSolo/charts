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

package eu.hansolo.fx.geometry;

import eu.hansolo.fx.geometry.Path.WindingRule;
import eu.hansolo.fx.geometry.transform.BaseTransform;

import java.util.NoSuchElementException;


class LineIterator implements PathIterator {
    Line          line;
    BaseTransform transform;
    int           index;


    LineIterator(final Line LINE, final BaseTransform TRANSFORM) {
        line      = LINE;
        transform = TRANSFORM;
    }


    public boolean isDone() { return (index > 1); }

    public void next() { ++index; }

    public WindingRule getWindingRule() { return WindingRule.WIND_NON_ZERO; }

    public int currentSegment(final double[] COORDS) {
        if (isDone()) { throw new NoSuchElementException("line iterator out of bounds"); }
        int type;
        if (index == 0) {
            COORDS[0] = line.x1;
            COORDS[1] = line.y1;
            type = MOVE_TO;
        } else {
            COORDS[0] = line.x2;
            COORDS[1] = line.y2;
            type = LINE_TO;
        }
        if (transform != null) { transform.transform(COORDS, 0, COORDS, 0, 1); }
        return type;
    }
}