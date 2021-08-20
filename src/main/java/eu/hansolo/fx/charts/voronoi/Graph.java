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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Graph<N> {
    private Map<N, Set<N>> neighbours = new HashMap<>();
    private Set<N>         nodes      = Collections.unmodifiableSet(neighbours.keySet());


    public Set<N> getNeighbours(final N node) throws NullPointerException {
        return Collections.unmodifiableSet(neighbours.get(node));
    }

    public Set<N> getNodes() {
        return nodes;
    }

    public void addNode(final N node) {
        if (neighbours.containsKey(node)) { return; }
        neighbours.put(node, new ArraySet<N>());
    }
    public void removeNode(final N node) {
        if (!neighbours.containsKey(node)) { return; }
        for (N neighbor : neighbours.get(node)) {
            neighbours.get(neighbor).remove(node);
        }
        neighbours.get(node).clear();
        neighbours.remove(node);
    }

    public void addConnection(final N nodeA, final N nodeB) throws NullPointerException {
        neighbours.get(nodeA).add(nodeB);
        neighbours.get(nodeB).add(nodeA);
    }
    public void removeConnection(final N nodeA, final N nodeB) throws NullPointerException {
        neighbours.get(nodeA).remove(nodeB);
        neighbours.get(nodeB).remove(nodeA);
    }
}
