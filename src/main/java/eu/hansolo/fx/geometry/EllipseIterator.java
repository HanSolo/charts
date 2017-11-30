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


class EllipseIterator implements PathIterator {
    double x, y, w, h;
    BaseTransform transform;
    int           index;

    // ArcIterator.btan(Math.PI/2)
    public static final double CtrlVal = 0.5522847498307933;

    private static final double pcv = 0.5 + CtrlVal * 0.5;
    private static final double ncv = 0.5 - CtrlVal * 0.5;
    private static final double ctrlpts[][] = {
        {  1.0,  pcv,  pcv,  1.0,  0.5,  1.0 },
        {  ncv,  1.0,  0.0,  pcv,  0.0,  0.5 },
        {  0.0,  ncv,  ncv,  0.0,  0.5,  0.0 },
        {  pcv,  0.0,  1.0,  ncv,  1.0,  0.5 }
    };


    EllipseIterator(final Ellipse ELLIPSE, final BaseTransform TRANSFORM) {
        x = ELLIPSE.x;
        y = ELLIPSE.y;
        w = ELLIPSE.width;
        h = ELLIPSE.height;
        transform = TRANSFORM;
        if (w < 0.0 || h < 0.0) { index = 6; }
    }


    public boolean isDone() { return index > 5; }

    public void next() { ++index; }

    public WindingRule getWindingRule() { return WindingRule.WIND_NON_ZERO; }

    public int currentSegment(final double[] COORDS) {
        if (isDone()) { throw new NoSuchElementException("ellipse iterator out of bounds"); }
        if (index == 5) { return CLOSE; }
        if (index == 0) {
            double ctrls[] = ctrlpts[3];
            COORDS[0] =  (x + ctrls[4] * w);
            COORDS[1] =  (y + ctrls[5] * h);
            if (transform != null) { transform.transform(COORDS, 0, COORDS, 0, 1); }
            return MOVE_TO;
        }
        double ctrls[] = ctrlpts[index - 1];
        COORDS[0] =  (x + ctrls[0] * w);
        COORDS[1] =  (y + ctrls[1] * h);
        COORDS[2] =  (x + ctrls[2] * w);
        COORDS[3] =  (y + ctrls[3] * h);
        COORDS[4] =  (x + ctrls[4] * w);
        COORDS[5] =  (y + ctrls[5] * h);
        if (transform != null) { transform.transform(COORDS, 0, COORDS, 0, 3); }
        return BEZIER_TO;
    }
}
