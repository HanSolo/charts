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

import java.util.NoSuchElementException;


public class FlatteningPathIterator implements PathIterator {
    static final int GROW_SIZE = 24;              // Multiple of bezierCurve & quadCurve curve size
    volatile     double hold[] = new double[14];  // The cache of interpolated coords

    PathIterator src;           // The source iterator
    double       flatness;      // Flatness parameter
    double       squareflat;    // Square of the flatness parameter for testing against squared lengths
    int          limit;         // Maximum number of recursion levels hold field
    double       curx, cury;    // The ending x,y of the last segment
    double       movx, movy;    // The x,y of the last move segment
    int          holdType;      // The type of the curve being held for interpolation
    int          holdEnd;       // The index of the last curve segment being held for interpolation
    int          holdIndex;     // The index of the curve segment that was last interpolated.  This is the curve segment ready to be returned in the next call to currentSegment().
    int          levels[];      // The recursion level at which each curve being held in storage was generated.
    int          levelIndex;    // The index of the entry in the levels array of the curve segment at the holdIndex
    boolean      done;          // True when iteration is done


    public FlatteningPathIterator(PathIterator src, double flatness) {
        this(src, flatness, 10);
    }
    public FlatteningPathIterator(PathIterator src, double flatness, int limit) {
        if (flatness < 0) { throw new IllegalArgumentException("flatness must be >= 0"); }
        if (limit < 0)    { throw new IllegalArgumentException("limit must be >= 0"); }
        this.src = src;
        this.flatness   = flatness;
        this.squareflat = flatness * flatness;
        this.limit      = limit;
        this.levels     = new int[limit + 1];
        // prime the first path segment
        next(false);
    }

    public double getFlatness() { return Math.sqrt(squareflat); }

    public int getRecursionLimit() { return limit; }

    void ensureHoldCapacity(final int WANT) {
        if (holdIndex - WANT < 0) {
            int    have      = hold.length - holdIndex;
            int    newsize   = hold.length + GROW_SIZE;
            double newhold[] = new double[newsize];
            System.arraycopy(hold, holdIndex, newhold, holdIndex + GROW_SIZE, have);
            hold = newhold;
            holdIndex += GROW_SIZE;
            holdEnd += GROW_SIZE;
        }
    }

    public boolean isDone() { return done; }

    public void next() { next(true); }
    private void next(final boolean DO_NEXT) {
        int level;

        if (holdIndex >= holdEnd) {
            if (DO_NEXT) { src.next(); }
            if (src.isDone()) {
                done = true;
                return;
            }
            holdType   = src.currentSegment(hold);
            levelIndex = 0;
            levels[0]  = 0;
        }

        switch (holdType) {
            case MOVE_TO:
            case LINE_TO:
                curx = hold[0];
                cury = hold[1];
                if (holdType == MOVE_TO) {
                    movx = curx;
                    movy = cury;
                }
                holdIndex = 0;
                holdEnd   = 0;
                break;
            case CLOSE:
                curx = movx;
                cury = movy;
                holdIndex = 0;
                holdEnd   = 0;
                break;
            case QUAD_TO:
                if (holdIndex >= holdEnd) {
                    holdIndex = hold.length - 6;
                    holdEnd   = hold.length - 2;
                    hold[holdIndex + 0] = curx;
                    hold[holdIndex + 1] = cury;
                    hold[holdIndex + 2] = hold[0];
                    hold[holdIndex + 3] = hold[1];
                    hold[holdIndex + 4] = curx = hold[2];
                    hold[holdIndex + 5] = cury = hold[3];
                }

                level = levels[levelIndex];
                while (level < limit) {
                    if (QuadCurve.getFlatnessSq(hold, holdIndex) < squareflat) { break; }

                    ensureHoldCapacity(4);
                    QuadCurve.subdivide(hold, holdIndex, hold, holdIndex - 4, hold, holdIndex);
                    holdIndex -= 4;

                    level++;
                    levels[levelIndex] = level;
                    levelIndex++;
                    levels[levelIndex] = level;
                }

                holdIndex += 4;
                levelIndex--;
                break;
            case BEZIER_TO:
                if (holdIndex >= holdEnd) {
                    holdIndex           = hold.length - 8;
                    holdEnd             = hold.length - 2;
                    hold[holdIndex + 0] = curx;
                    hold[holdIndex + 1] = cury;
                    hold[holdIndex + 2] = hold[0];
                    hold[holdIndex + 3] = hold[1];
                    hold[holdIndex + 4] = hold[2];
                    hold[holdIndex + 5] = hold[3];
                    hold[holdIndex + 6] = curx = hold[4];
                    hold[holdIndex + 7] = cury = hold[5];
                }

                level = levels[levelIndex];
                while (level < limit) {
                    if (BezierCurve.getFlatnessSq(hold, holdIndex) < squareflat) { break; }

                    ensureHoldCapacity(6);
                    BezierCurve.subdivide(hold, holdIndex, hold, holdIndex - 6, hold, holdIndex);
                    holdIndex -= 6;

                    level++;
                    levels[levelIndex] = level;
                    levelIndex++;
                    levels[levelIndex] = level;
                }

                holdIndex += 6;
                levelIndex--;
                break;
        }
    }

    public WindingRule getWindingRule() { return src.getWindingRule(); }

    public int currentSegment(final double[] COORDS) {
        if (isDone()) { throw new NoSuchElementException("flattening iterator out of bounds"); }
        int type = holdType;
        if (type != CLOSE) {
            COORDS[0] = hold[holdIndex + 0];
            COORDS[1] = hold[holdIndex + 1];
            if (type != MOVE_TO) { type = LINE_TO; }
        }
        return type;
    }
}
