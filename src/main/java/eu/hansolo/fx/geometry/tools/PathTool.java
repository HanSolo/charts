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

import eu.hansolo.fx.geometry.FlatteningPathIterator;
import eu.hansolo.fx.geometry.Path.WindingRule;
import eu.hansolo.fx.geometry.PathIterator;
import eu.hansolo.fx.geometry.Shape;
import eu.hansolo.fx.geometry.transform.Affine;

import java.util.ArrayList;
import java.util.List;


public class PathTool {
    protected Shape             path;
    protected List<PathSegment> segments;
    protected int[]             segmentIndexes;
    protected double            pathLength;
    protected boolean           initialized;


    public PathTool(Shape path) {
        setPath(path);
    }

    public Shape getPath() {
        return path;
    }
    public void setPath(final Shape SHAPE) {
        path = SHAPE;
        initialized = false;
    }

    private void init() {
        pathLength = 0;

        PathIterator              pathIterator              = path.getPathIterator(new Affine());
        SingleSegmentPathIterator singleSegmentPathIterator = new SingleSegmentPathIterator();
        segments = new ArrayList(20);
        List     indexes   = new ArrayList(20);
        int      index     = 0;
        int      origIndex = -1;
        double   lastMoveX = 0;
        double   lastMoveY = 0;
        double   currentX  = 0;
        double   currentY  = 0;
        double[] seg       = new double[6];
        int      segType;

        segments.add(new PathSegment(PathIterator.MOVE_TO, 0, 0, 0, origIndex));

        while (!pathIterator.isDone()) {
            origIndex++;
            indexes.add(index);
            segType = pathIterator.currentSegment(seg);
            switch (segType) {
                case PathIterator.MOVE_TO:
                    segments.add(new PathSegment(segType, seg[0], seg[1], pathLength, origIndex));
                    currentX  = seg[0];
                    currentY  = seg[1];
                    lastMoveX = currentX;
                    lastMoveY = currentY;
                    index++;
                    pathIterator.next();
                    break;
                case PathIterator.LINE_TO:
                    pathLength += Point.distance(currentX, currentY, seg[0], seg[1]);
                    segments.add(new PathSegment(segType, seg[0], seg[1], pathLength, origIndex));
                    currentX = seg[0];
                    currentY = seg[1];
                    index++;
                    pathIterator.next();
                    break;
                case PathIterator.CLOSE:
                    pathLength += Point.distance(currentX, currentY, lastMoveX, lastMoveY);
                    segments.add(new PathSegment(PathIterator.LINE_TO, lastMoveX, lastMoveY, pathLength, origIndex));
                    currentX = lastMoveX;
                    currentY = lastMoveY;
                    index++;
                    pathIterator.next();
                    break;
                default:
                    singleSegmentPathIterator.setPathIterator(pathIterator, currentX, currentY);
                    FlatteningPathIterator fpi = new FlatteningPathIterator(singleSegmentPathIterator, 0.01);
                    while (!fpi.isDone()) {
                        segType = fpi.currentSegment(seg);
                        if (segType == PathIterator.LINE_TO) {
                            pathLength += Point.distance(currentX, currentY, seg[0], seg[1]);
                            segments.add(new PathSegment(segType, seg[0], seg[1], pathLength, origIndex));
                            currentX = seg[0];
                            currentY = seg[1];
                            index++;
                        }
                        fpi.next();
                    }
            }
        }
        segmentIndexes = new int[indexes.size()];
        for (int i = 0; i < segmentIndexes.length; i++) {
            segmentIndexes[i] = ((Integer) indexes.get(i)).intValue();
        }
        initialized = true;
    }

    private int findUpperIndex(final double LENGTH) {
        if (!initialized) { init(); }

        if (LENGTH < 0 || LENGTH > pathLength) { return -1; }

        int lowerBound = 0;
        int upperBound = segments.size() - 1;

        while (lowerBound != upperBound) {
            int curr = (lowerBound + upperBound) >> 1;
            PathSegment pathSegment = segments.get(curr);
            if (pathSegment.getLength() >= LENGTH) {
                upperBound = curr;
            } else {
                lowerBound = curr + 1;
            }
        }
        for (;;) {
            PathSegment pathSegment = segments.get(upperBound);
            if (pathSegment.getSegmentType() != PathIterator.MOVE_TO || upperBound == segments.size() - 1) {
                break;
            }
            upperBound++;
        }

        int upperIndex   = -1;
        int currentIndex = 0;
        int numSegments  = segments.size();

        while (upperIndex <= 0 && currentIndex < numSegments) {
            PathSegment ps = segments.get(currentIndex);
            if (ps.getLength() >= LENGTH && ps.getSegmentType() != PathIterator.MOVE_TO) {
                upperIndex = currentIndex;
            }
            currentIndex++;
        }
        return upperIndex;
    }

    public double getLengthOfPath() {
        if (!initialized) { init(); }
        return pathLength;
    }

    public Point getSegmentPointAtLength(final double LENGTH) {
        int upperIndex = findUpperIndex(LENGTH);
        if (upperIndex == -1) { return null; }

        PathSegment upper = segments.get(upperIndex);

        if (upperIndex == 0) { return new Point(upper.getX(), upper.getY()); }

        PathSegment lower = segments.get(upperIndex - 1);

        double offset = LENGTH - lower.getLength();

        double theta = Math.atan2(upper.getY() - lower.getY(), upper.getX() - lower.getX());

        double xPoint = (lower.getX() + offset * Math.cos(theta));
        double yPoint = (lower.getY() + offset * Math.sin(theta));

        return new Point(xPoint, yPoint);
    }

    public Point getPointAtLength(final double LENGTH) {
        PathIterator              pathIterator              = path.getPathIterator(new Affine());
        SingleSegmentPathIterator singleSegmentPathIterator = new SingleSegmentPathIterator();
        double   pathLength = 0;
        double   lastMoveX  = 0;
        double   lastMoveY  = 0;
        double   currentX   = 0;
        double   currentY   = 0;
        double[] seg        = new double[6];
        int      segType;
        double   lastX;
        double   lastY;

        while (!pathIterator.isDone()) {
            segType = pathIterator.currentSegment(seg);
            switch (segType) {
                case PathIterator.MOVE_TO:
                    lastX     = currentX;
                    lastY     = currentY;
                    currentX  = seg[0];
                    currentY  = seg[1];
                    lastMoveX = currentX;
                    lastMoveY = currentY;
                    pathIterator.next();
                    if (pathLength > LENGTH) { return new Point(lastX, lastY); }
                    break;
                case PathIterator.LINE_TO:
                    double segmentLength = Point.distance(currentX, currentY, seg[0], seg[1]);
                    double angle         = Math.atan2(seg[1] - currentY, seg[0] - currentX);
                    double step          = segmentLength * 0.001;
                    for (double i = 0 ; i < segmentLength ; i += step) {
                        lastX = currentX;
                        lastY = currentY;
                        currentX += step * Math.cos(angle);
                        currentY += step * Math.sin(angle);
                        pathLength += step;
                        if (Double.compare(pathLength, LENGTH) >= 0) { return new Point(lastX, lastY); }
                    }

                    //lastX    = currentX;
                    //lastY    = currentY;
                    //currentX = seg[0];
                    //currentY = seg[1];
                    pathIterator.next();
                    //if (pathLength > LENGTH) { return new Point(lastX, lastY); }
                    break;
                case PathIterator.CLOSE:
                    pathLength += Point.distance(currentX, currentY, lastMoveX, lastMoveY);
                    lastX      = currentX;
                    lastY      = currentY;
                    currentX   = lastMoveX;
                    currentY   = lastMoveY;
                    pathIterator.next();
                    if (pathLength > LENGTH) { return new Point(lastX, lastY); }
                    break;
                default:
                    singleSegmentPathIterator.setPathIterator(pathIterator, currentX, currentY);
                    FlatteningPathIterator fpi = new FlatteningPathIterator(singleSegmentPathIterator, 0.001);
                    while (!fpi.isDone()) {
                        segType = fpi.currentSegment(seg);
                        if (segType == PathIterator.LINE_TO) {
                            pathLength += Point.distance(currentX, currentY, seg[0], seg[1]);
                            lastX      = currentX;
                            lastY      = currentY;
                            currentX   = seg[0];
                            currentY   = seg[1];
                            if (pathLength > LENGTH) { return new Point(lastX, lastY); }
                        }
                        fpi.next();
                    }
            }
        }
        return null;
    }


    // ******************** Inner Classes *************************************
    protected static class SingleSegmentPathIterator implements PathIterator {
        protected PathIterator pathIterator;
        protected boolean      done;
        protected boolean      moveDone;
        protected double       x;
        protected double       y;


        public void setPathIterator(final PathIterator PATH_ITERATOR, final double X, final double Y) {
            pathIterator = PATH_ITERATOR;
            x            = X;
            y            = Y;
            done         = false;
            moveDone     = false;
        }

        public int currentSegment(final double[] COORDINATES) {
            int type = pathIterator.currentSegment(COORDINATES);
            if (!moveDone) {
                COORDINATES[0] = x;
                COORDINATES[1] = y;
                return MOVE_TO;
            }
            return type;
        }

        public WindingRule getWindingRule() { return pathIterator.getWindingRule(); }

        public boolean isDone() { return done || pathIterator.isDone(); }

        public void next() {
            if (!done) {
                if (!moveDone) {
                    moveDone = true;
                } else {
                    pathIterator.next();
                    done = true;
                }
            }
        }
    }

    protected static class PathSegment {
        protected final int segmentType;
        protected double    x;
        protected double    y;
        protected double    length;
        protected int       index;


        PathSegment(final int SEGMENT_TYPE, final double X, final double Y, final double LENGTH, final int INDEX) {
            segmentType = SEGMENT_TYPE;
            x           = X;
            y           = Y;
            length      = LENGTH;
            index       = INDEX;
        }

        public int getSegmentType() { return segmentType; }

        public double getX() { return x; }
        public void setX(final double X) { x = X; }

        public double getY() { return y; }
        public void setY(final double Y) { y = Y; }

        public double getLength() { return length; }
        public void setLength(final double LENGTH) { length = LENGTH; }

        public int getIndex() { return index; }
        public void setIndex(final int INDEX) { index = INDEX; }
    }
}