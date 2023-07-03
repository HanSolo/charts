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

import java.util.Arrays;
import java.util.List;


public enum DefectRecordField {
    DEFECT_ID("DEFECTID", Constants.INT_PATTERN, 1),               // A unique identification number [int]
    X_REL("XREL", Constants.DOUBLE_SCIENTIFIC_PATTERN, 2),         // Distance from die origin to center of the defect in x direction [double_scientific]
    Y_REL("YREL", Constants.DOUBLE_SCIENTIFIC_PATTERN, 2),         // Distance from die origin to center of the defect in y direction [double_scientific]
    X_INDEX("XINDEX", Constants.INT_PATTERN, 1),                   // Location of the die in the array in x direction [int]
    Y_INDEX("YINDEX", Constants.INT_PATTERN, 1),                   // Location of the die in the array in y direction [int]
    X_SIZE("XSIZE", Constants.DOUBLE_PATTERN, 1),                  // Size of the defect in x direction [double]
    Y_SIZE("YSIZE", Constants.DOUBLE_PATTERN, 1),                  // Size of the defect in y direction [double]
    DEFECT_AREA("DEFECTAREA", Constants.DOUBLE_PATTERN, 1),        // Area of the defectr region [double]
    D_SIZE("DSIZE", Constants.DOUBLE_SCIENTIFIC_PATTERN, 2),       // Size of the dfect [double_scientific]
    CLASS_NUMBER("CLASSNUMBER", Constants.INT_PATTERN, 1),         // The defect index in the class lookup table [int]
    TEST("TEST", Constants.INT_PATTERN, 1),                        // Definition of the inspection test [int]
    CLUSTER_NUMBER("CLUSTERNUMBER", Constants.INT_PATTERN, 1),     // Not needed for map display [int]
    ROUGH_BIN_NUMBER("ROUGHBINNUMBER", Constants.INT_PATTERN, 1),  // Not needed for map display [int]
    FINE_BIN_NUMBER("FINEBINNUMBER", Constants.INT_PATTERN, 1),    // Not needed for map display [int]
    REVIEW_SAMPLE("REVIEWSAMPLE", "", 1),                   // Not needed for map display
    IMAGE_COUNT("IMAGECOUNT", Constants.INT_PATTERN, 1),           // Number of images associated with this defect [int]
    IMAGE_LIST("IMAGELIST", Constants.ALL_PATTERN, 1);             // List of image files associated with this defect [string_filename]

    public final String name;
    public final String pattern;
    public final int    groups;


    DefectRecordField(final String name, final String pattern, final int groups) {
        this.name    = name;
        this.pattern = pattern;
        this.groups  = groups;
    }


    public static List<DefectRecordField> getAsList() { return Arrays.asList(values()); }
}
