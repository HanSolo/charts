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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;


public final class Defect {
    private final int    id;
    private final double xRel;
    private final double yRel;
    private final int    indexX;
    private final int    indexY;
    private final double sizeX;
    private final double sizeY;
    private final double defectArea;
    private final double sizeD;
    private final int    classNumber;
    private final int    roughBinNumber;
    private final int    fineBinNumber;
    private final int    test;
    private final int    clusterNumber;
    private       double xAbsolute;
    private       double yAbsolute;
    private int          imageCount;
    private List<String> imageList;


    // ******************** Constructor ***************************************
    public Defect(final int id, final double xRel, final double yRel, final int indexX, final int indexY, final double sizeX, final double sizeY, final double defectArea, final double sizeD, final int classNumber, final int roughBinNumber, final int fineBinNumber, final int test, final int clusterNumber, final int imageCount, final String... imageList) {
        this.id              = id;
        this.xRel            = xRel;
        this.yRel            = yRel;
        this.indexX          = indexX;
        this.indexY          = indexY;
        this.sizeX           = sizeX;
        this.sizeY           = sizeY;
        this.defectArea      = defectArea;
        this.sizeD           = sizeD;
        this.classNumber     = classNumber;
        this.roughBinNumber  = roughBinNumber;
        this.fineBinNumber   = fineBinNumber;
        this.test            = test;
        this.clusterNumber   = clusterNumber;
        this.xAbsolute       = 0;
        this.yAbsolute       = 0;
        this.imageCount      = imageCount;
        this.imageList       = new ArrayList<>(imageCount);
        this.imageList.addAll(Arrays.stream(imageList).toList());
    }


    // ******************** Methods *******************************************
    public int getId() { return id; }

    public double getXRel() { return xRel; }

    public double getYRel() { return yRel; }

    public int getIndexX() { return indexX; }

    public int getIndexY() { return indexY; }

    public double getSizeX() { return sizeX; }

    public double getSizeY() { return sizeY; }

    public double getDefectArea() { return defectArea; }

    public double getSizeD() { return sizeD; }

    public int getClassNumber() { return classNumber; }

    public int getRoughBinNumber() { return roughBinNumber; }

    public int getFineBinNumber() { return fineBinNumber; }

    public int getTest() { return test; }

    public int getClusterNumber() { return clusterNumber; }

    public double getXAbsolute() { return xAbsolute; }
    public void setXAbsolute(final double xAbsolute) { this.xAbsolute = xAbsolute; }

    public double getYAbsolute() { return yAbsolute; }
    public void setYAbsolute(final double yAbsolute) { this.yAbsolute = yAbsolute; }

    public int getImageCount() { return imageCount; }
    public void setImageCount(final int imageCount) { this.imageCount = imageCount; }

    public List<String> getImageList() { return imageList; }
    public void setImageList(final String... imageList) {
        this.imageList.clear();
        this.imageList.addAll(Arrays.stream(imageList).toList());
    }

    public String getLabel() { return indexX + "/" + indexY; }


    @Override public boolean equals(final Object other) {
        if (this == other) { return true; }
        if (other == null || getClass() != other.getClass()) { return false; }
        Defect defect = (Defect) other;
        return id == defect.id && Double.compare(defect.xRel, xRel) == 0 && Double.compare(defect.yRel, yRel) == 0 && indexX == defect.indexX && indexY == defect.indexY && Double.compare(defect.sizeX, sizeX) == 0 &&
               Double.compare(defect.sizeY, sizeY) == 0 && Double.compare(defect.defectArea, defectArea) == 0 && Double.compare(defect.sizeD, sizeD) == 0 && classNumber == defect.classNumber && roughBinNumber == defect.roughBinNumber &&
               fineBinNumber == defect.fineBinNumber && test == defect.test && clusterNumber == defect.clusterNumber && imageCount == defect.imageCount;
    }

    @Override public int hashCode() {
        return Objects.hash(id, xRel, yRel, indexX, indexY, sizeX, sizeY, defectArea, sizeD, classNumber, roughBinNumber, fineBinNumber, test, clusterNumber, imageCount);
    }

    @Override public String toString() {
        return new StringBuilder()
               .append(Constants.SPACE).append(id)
               .append(Constants.SPACE).append(String.format(Locale.US, "%.10e", xRel))
               .append(Constants.SPACE).append(String.format(Locale.US, "%.10e", yRel))
               .append(Constants.SPACE).append(indexX)
               .append(Constants.SPACE).append(indexY)
               .append(Constants.SPACE).append(String.format(Locale.US, "%.6f", sizeX))
               .append(Constants.SPACE).append(String.format(Locale.US, "%.6f", sizeY))
               .append(Constants.SPACE).append(String.format(Locale.US, "%.6f", defectArea))
               .append(Constants.SPACE).append(String.format(Locale.US, "%.10e", sizeD))
               .append(Constants.SPACE).append(classNumber)
               .append(Constants.SPACE).append(roughBinNumber)
               .append(Constants.SPACE).append(fineBinNumber)
               .append(Constants.SPACE).append(test)
               .append(Constants.SPACE).append(clusterNumber)
               .append(Constants.SPACE).append(imageCount)
               .append(Constants.SPACE).append(imageList.stream().collect(Collectors.joining(Constants.SPACE)))
               .toString();
    }
}
