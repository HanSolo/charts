/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2023 Gerrit Grunwald.
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

package eu.hansolo.fx.charts.wafermap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Die {
    private final int               indexX;
    private final int               indexY;
    private final int               parentId;
    private final double            originX;
    private final double            originY;
    private final double            sizeX;
    private final double            sizeY;
    private final String            name;
    private final double            offsetX;
    private final double            offsetY;
    private final ArrayList<Defect> defects;
    private       boolean           visible;
    private       int               rectangleArrayID;


    // ******************** Constructor ***************************************
    public Die(final int xIndex, final int yIndex, final int parentId, final double originX, final double originY, final double sizeX, final double sizeY, final String name, final double offsetX, final double offsetY) {
        this.indexX           = xIndex;
        this.indexY           = yIndex;
        this.parentId         = parentId;
        this.originX          = originX;
        this.originY          = originY;
        this.sizeX            = sizeX;
        this.sizeY            = sizeY;
        this.name             = name;
        this.offsetX          = offsetX;
        this.offsetY          = offsetY;
        this.defects          = new ArrayList<>();
        this.visible          = false;
        this.rectangleArrayID = -1;
    }


    // ******************** Methods *******************************************
    public ArrayList<Defect> getDefects() { return new ArrayList<>(defects); }

    public void setDefects(final List<Defect> defects) {
        this.defects.clear();
        this.defects.addAll(defects);
    }
    public void addDefect(final Defect defect) {
        if (defects.contains(defect)) { return; }
        defects.add(defect);
    }
    public void removeDefect(final Defect defect) {
        if (defects.contains(defect)) { defects.remove(defect); }
    }
    public void clearDefects() { defects.clear(); }

    public int getNoOfDefects() { return defects.size(); }

    public Optional<Defect> getDefect(final int index) {
        if (index < 0 || index > defects.size()) { return Optional.empty(); }
        return Optional.of(defects.get(index));
    }
    public Optional<Defect> getDefectById(final int defectId) {
        return defects.stream().filter(defect -> defect.equals(defectId)).findFirst();
    }

    public int getRectangleArrayId() { return rectangleArrayID; }
    public void setRectangleArrayId(final int id) { rectangleArrayID = id; }

    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }

    public int getXIndex() { return indexX; }
    public int getYIndex() { return indexY; }

    public int getParentId() { return parentId; }

    public double getOriginX() { return originX; }
    public double getOriginY() { return originY; }

    public double getSizeX() { return sizeX; }
    public double getSizeY() { return sizeY; }

    public String getName() { return name; }

    public boolean isVisible() { return visible; }
    public void setVisible(final boolean visible) { this.visible = visible; }
}
