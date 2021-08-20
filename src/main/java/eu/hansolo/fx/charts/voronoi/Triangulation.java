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

package eu.hansolo.fx.charts.voronoi;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class Triangulation extends AbstractSet<Triangle> {
    private Triangle        mostRecent;
    private Graph<Triangle> triGraph;


    public Triangulation(final Triangle triangle) {
        triGraph = new Graph<>();
        triGraph.addNode(triangle);
        mostRecent = triangle;
    }


    public boolean contains(final Triangle triangle) {
        return triGraph.getNodes().contains(triangle);
    }

    public Triangle neighborOpposite(final VPoint site, final Triangle triangle) {
        if (!triangle.contains(site)) { throw new IllegalArgumentException("Bad vertex; not in triangle"); }
        for (Triangle neighbor : triGraph.getNeighbours(triangle)) {
            if (!neighbor.contains(site)) { return neighbor; }
        }
        return null;
    }

    public Set<Triangle> neighbors(final Triangle triangle) {
        return triGraph.getNeighbours(triangle);
    }

    public List<Triangle> surroundingTriangles(final VPoint site, Triangle triangle) {
        if (!triangle.contains(site)) { throw new IllegalArgumentException("Site not in triangle"); }
        List<Triangle> list  = new ArrayList<>();
        Triangle       start = triangle;
        VPoint         guide = triangle.getVertexButNot(site);
        while (true) {
            list.add(triangle);
            Triangle previous = triangle;
            triangle = this.neighborOpposite(guide, triangle);
            guide    = previous.getVertexButNot(site, guide);
            if (triangle == start) { break; }
        }
        return list;
    }

    public Triangle locate(final VPoint point) {
        Triangle triangle = mostRecent;
        if (!this.contains(triangle)) { triangle = null; }

        Set<Triangle> visited = new HashSet<>();
        while (triangle != null) {
            if (visited.contains(triangle)) { // This should never happen
                System.out.println("Warning: Caught in a locate loop");
                break;
            }
            visited.add(triangle);
            // Corner opposite point
            VPoint corner = point.isOutside(triangle.toArray(new VPoint[0]));
            if (corner == null) { return triangle; }
            triangle = this.neighborOpposite(corner, triangle);
        }
        // No luck; try brute force
        System.out.println("Warning: Checking all triangles for " + point);
        for (Triangle tri : this) {
            if (point.isOutside(tri.toArray(new VPoint[0])) == null) { return tri; }
        }
        // No such triangle
        System.out.println("Warning: No triangle holds " + point);
        return null;
    }

    public void place(final VPoint point) {
        Triangle triangle = locate(point);

        if (triangle == null) { throw new IllegalArgumentException("No containing triangle"); }
        if (triangle.contains(point)) { return; }

        Set<Triangle> cavity = getCavity(point, triangle);
        mostRecent = update(point, cavity);
    }

    private Set<Triangle> getCavity(final VPoint site, Triangle triangle) {
        Set<Triangle>   encroached  = new HashSet<>();
        Queue<Triangle> toBeChecked = new LinkedList<>();
        Set<Triangle>   marked      = new HashSet<>();
        toBeChecked.add(triangle);
        marked.add(triangle);
        while (!toBeChecked.isEmpty()) {
            triangle = toBeChecked.remove();
            if (site.vsCircumCircle(triangle.toArray(new VPoint[0])) == 1) {
                continue;
            }
            encroached.add(triangle);

            for (Triangle neighbor : triGraph.getNeighbours(triangle)) {
                if (marked.contains(neighbor)) { continue; }
                marked.add(neighbor);
                toBeChecked.add(neighbor);
            }
        }
        return encroached;
    }

    private Triangle update(final VPoint site, final Set<Triangle> cavity) {
        Set<Set<VPoint>> boundary     = new HashSet<>();
        Set<Triangle>    theTriangles = new HashSet<>();

        for (Triangle triangle : cavity) {
            theTriangles.addAll(neighbors(triangle));
            for (VPoint vertex : triangle) {
                Set<VPoint> facet = triangle.facetOpposite(vertex);
                if (boundary.contains(facet)) {
                    boundary.remove(facet);
                } else {
                    boundary.add(facet);
                }
            }
        }
        theTriangles.removeAll(cavity);

        for (Triangle triangle : cavity) {
            triGraph.removeNode(triangle);
        }

        Set<Triangle> newTriangles = new HashSet<>();
        for (Set<VPoint> vertices : boundary) {
            vertices.add(site);
            Triangle tri = new Triangle(vertices);
            triGraph.addNode(tri);
            newTriangles.add(tri);
        }

        theTriangles.addAll(newTriangles);
        for (Triangle triangle : newTriangles) {
            for (Triangle other : theTriangles) {
                if (triangle.isNeighbor(other)) {
                    triGraph.addConnection(triangle, other);
                }
            }
        }

        return newTriangles.iterator().next();
    }

    @Override public Iterator<Triangle> iterator() {
        return triGraph.getNodes().iterator();
    }

    @Override public int size() {
        return triGraph.getNodes().size();
    }

    @Override public String toString() {
        return "Triangulation with " + size() + " triangles";
    }
}
