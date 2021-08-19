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
import java.util.Collection;
import java.util.Iterator;


public class ArraySet<E> extends AbstractSet<E> {

    private ArrayList<E> items;

    public ArraySet() {
        this(new ArrayList<>(3));
    }
    public ArraySet(final int initialCapacity) {
        this(new ArrayList<>(initialCapacity));
    }
    public ArraySet(Collection<? extends E> collection) {
        items = new ArrayList<E>(collection.size());
        for (E item : collection) {
            if (!items.contains(item)) { items.add(item); }
        }
    }


    public E get(final int index) throws IndexOutOfBoundsException { return items.get(index); }

    public boolean containsAny(final Collection<?> collection) {
        return collection.stream().filter(item -> this.contains(item)).findFirst().isPresent();
    }

    @Override public boolean add(E item) {
        if (items.contains(item)) { return false; }
        return items.add(item);
    }

    @Override public Iterator<E> iterator() {
        return items.iterator();
    }

    @Override public int size() {
        return items.size();
    }
}
