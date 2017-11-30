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

package eu.hansolo.fx.geometry.tools;

import eu.hansolo.fx.geometry.Rectangle;


public interface Segment extends Cloneable {

    double minX();
    double maxX();

    double minY();
    double maxY();

    Rectangle getBounds2D();

    Point evalDt(double t);
    Point eval(double t);

    Segment getSegment(double t0, double t1);

    SplitResults split(double y);
    Segment splitBefore(double t);
    Segment splitAfter(double t);

    void subdivide(Segment s0, Segment s1);
    void subdivide(double t, Segment s0, Segment s1);

    double getLength();
    double getLength(double maxErr);


    class SplitResults {
        Segment[] above;
        Segment[] below;
        SplitResults(Segment[] below, Segment[] above) {
            this.below = below;
            this.above = above;
        }

        Segment[] getBelow() {
            return below;
        }
        Segment[] getAbove() {
            return above;
        }
    }
}
