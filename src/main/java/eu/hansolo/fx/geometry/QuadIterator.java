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


class QuadIterator implements PathIterator {
    QuadCurve     quadCurve;
    BaseTransform transform;
    int           index;


    QuadIterator(final QuadCurve QUAD_CURVE, final BaseTransform TRANSFORM) {
        quadCurve = QUAD_CURVE;
        transform = TRANSFORM;
    }


    public WindingRule getWindingRule() { return WindingRule.WIND_NON_ZERO; }

    public boolean isDone() { return (index > 1); }

    public void next() { ++index; }

    public int currentSegment(double[] coords) {
        if (isDone()) { throw new NoSuchElementException("quadCurve iterator iterator out of bounds"); }
        int type;
        if (index == 0) {
            coords[0] = quadCurve.x1;
            coords[1] = quadCurve.y1;
            type = MOVE_TO;
        } else {
            coords[0] = quadCurve.ctrlx;
            coords[1] = quadCurve.ctrly;
            coords[2] = quadCurve.x2;
            coords[3] = quadCurve.y2;
            type = QUAD_TO;
        }
        if (transform != null) { transform.transform(coords, 0, coords, 0, index == 0 ? 1 : 2); }
        return type;
    }
}