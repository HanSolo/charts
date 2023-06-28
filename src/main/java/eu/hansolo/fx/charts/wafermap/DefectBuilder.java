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

import eu.hansolo.toolbox.properties.DoubleProperty;
import eu.hansolo.toolbox.properties.IntegerProperty;
import eu.hansolo.toolbox.properties.ReadOnlyProperty;
import eu.hansolo.toolbox.properties.StringProperty;

import java.util.HashMap;


public class DefectBuilder<B extends DefectBuilder<B>> {
    private final HashMap<String, ReadOnlyProperty> properties = new HashMap<>();
    private final int                               id;
    private final double                            xRel;
    private final double                            yRel;


    // ******************** Constructors **************************************
    protected DefectBuilder(final int id, final double xRel, final double yRel) {
        this.id  = id;
        this.xRel = xRel;
        this.yRel = yRel;
    }


    // ******************** Methods *******************************************
    public static final DefectBuilder create(final int id, final double xRel, final double yRel) {
        return new DefectBuilder(id, xRel, yRel);
    }


    public final B relXY(final double xRel, final double yRel) {
        properties.put("xRel", new DoubleProperty(xRel));
        properties.put("yRel", new DoubleProperty(yRel));
        return (B) this;
    }

    public final B indexXY(final int indexX, final int indexY) {
        properties.put("indexX", new IntegerProperty(indexX));
        properties.put("indexY", new IntegerProperty(indexY));
        return (B) this;
    }

    public final B sizeXY(final double sizeX, final double sizeY) {
        properties.put("sizeX", new DoubleProperty(sizeX));
        properties.put("sizeY", new DoubleProperty(sizeY));
        return (B) this;
    }

    public final B absoluteXY(final double absoluteX, final double absoluteY) {
        properties.put("absoluteX", new DoubleProperty(absoluteX));
        properties.put("absoluteY", new DoubleProperty(absoluteY));
        return (B) this;
    }

    public final B defectArea(final double defectArea) {
        properties.put("defectArea", new DoubleProperty(defectArea));
        return (B) this;
    }

    public final B sizeD(final double sizeD) {
        properties.put("sizeD", new DoubleProperty(sizeD));
        return (B) this;
    }

    public final B classNumber(final int classNumber) {
        properties.put("classNumber", new IntegerProperty(classNumber));
        return (B) this;
    }

    public final B fineBinNumber(final int fineBinNumber) {
        properties.put("fineBinNumber", new IntegerProperty(fineBinNumber));
        return (B) this;
    }

    public final B test(final int test) {
        properties.put("test", new IntegerProperty(test));
        return (B) this;
    }

    public final B clusterNumber(final int clusterNumber) {
        properties.put("clusterBinNumber", new IntegerProperty(clusterNumber));
        return (B) this;
    }

    public final B imageCount(final int imageCount) {
        properties.put("imageCount", new IntegerProperty(imageCount));
        return (B) this;
    }

    public final B marker(final int marker) {
        properties.put("marker", new IntegerProperty(marker));
        return (B) this;
    }

    public final B imageFilename(final String imageFilename) {
        properties.put("imageFilename", new StringProperty(imageFilename));
        return (B) this;
    }


    public final Defect build() {
        final double xRel          = properties.containsKey("xRel")          ? (((DoubleProperty) properties.get("xRel")).get())           : this.xRel;
        final double yRel          = properties.containsKey("yRel")          ? (((DoubleProperty) properties.get("yRel")).get())           : this.yRel;
        final int    indexX        = properties.containsKey("indexX")        ? (((IntegerProperty) properties.get("indexX")).get())        : -1;
        final int    indexY        = properties.containsKey("indexY")        ? (((IntegerProperty) properties.get("indexY")).get())        : -1;
        final double absoluteX     = properties.containsKey("absoluteX")     ? (((DoubleProperty) properties.get("absoluteX")).get())      : -1;
        final double absoluteY     = properties.containsKey("absoluteY")     ? (((DoubleProperty) properties.get("absoluteY")).get())      : -1;
        final double sizeX         = properties.containsKey("sizeX")         ? (((DoubleProperty) properties.get("sizeX")).get())          : -1;
        final double sizeY         = properties.containsKey("sizeY")         ? (((DoubleProperty) properties.get("sizeY")).get())          : -1;
        final double defectArea    = properties.containsKey("defectArea")    ? (((DoubleProperty) properties.get("defectArea")).get())     : -1;
        final double sizeD         = properties.containsKey("sizeD")         ? (((DoubleProperty) properties.get("sizeD")).get())          : -1;
        final int    classNumber   = properties.containsKey("classNumber")   ? (((IntegerProperty) properties.get("classNumber")).get())   : 0;
        final int    fineBinNumber = properties.containsKey("fineBinNumber") ? (((IntegerProperty) properties.get("fineBinNumber")).get()) : 0;
        final int    test          = properties.containsKey("test")          ? (((IntegerProperty) properties.get("test")).get())          : 0;
        final int    clusterNumber = properties.containsKey("clusterNumber") ? (((IntegerProperty) properties.get("clusterNumber")).get()) : 0;
        final int    imageCount    = properties.containsKey("imageCount")    ? (((IntegerProperty) properties.get("imageCount")).get())    : 0;
        final String imageFilename = properties.containsKey("imageFilename") ? (((StringProperty) properties.get("imageFilename")).get())  : "";

        final Defect defect = new Defect(this.id, xRel / 1000, yRel / 1000, indexX, indexY, sizeX, sizeY, defectArea, sizeD, classNumber, fineBinNumber, test, clusterNumber, imageCount, imageFilename);
        return defect;
    }
}
