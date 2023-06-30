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


import javafx.scene.paint.Color;

import java.util.List;


public class Constants {
    public static final String      SPACE                              = " ";
    public static final String      SEMICOLON                          = ";";

    public static final String      FIELD_FILE_VERSION                 = "FileVersion";
    public static final String      FIELD_FILE_TIME_STAMP              = "FileTimestamp";
    public static final String      FIELD_INSPECTION_STATION_ID        = "InspectionStationID";
    public static final String      FIELD_SAMPLE_TYPE                  = "SampleType";
    public static final String      FIELD_RESULT_TIME_STAMP            = "ResultTimestamp";
    public static final String      FIELD_LOT_ID                       = "LotID";
    public static final String      FIELD_SAMPLE_SIZE                  = "SampleSize";
    public static final String      FIELD_SETUP_ID                     = "SetupID";
    public static final String      FIELD_STEP_ID                      = "StepID";
    public static final String      FIELD_SAMPLE_ORIENTATION_MARK_TYPE = "SampleOrientationMarkType";
    public static final String      FIELD_ORIENTATION_MARK_LOCATION    = "OrientationMarkLocation";
    public static final String      FIELD_DIE_PITCH                    = "DiePitch";
    public static final String      FIELD_DIE_ORIGIN                   = "DieOrigin";
    public static final String      FIELD_WAFER_ID                     = "WaferID";
    public static final String      FIELD_SLOT                         = "Slot";
    public static final String      FIELD_SAMPLE_CENTER_LOCATION       = "SampleCenterLocation";
    public static final String      FIELD_CLASS_LOOKUP                 = "ClassLookup";
    public static final String      FIELD_AREA_PER_TEST                = "AreaPerTest";
    public static final String      FIELD_DEFECT_RECORD_SPEC           = "DefectRecordSpec";
    public static final String      FIELD_SAMPLE_TEST_PLAN             = "SampleTestPlan";

    public static final String      FIELD_DEFECT_LIST                  = "DefectList";
    public static final String      FIELD_SUMMARY_SPEC                 = "SummarySpec";
    public static final String      FIELD_SUMMARY_LIST                 = "SummaryList";
    public static final String      FIELD_END_OF_FILE                  = "EndOfFile";

    public static final String      FIELD_INSPECTION_TEST              = "InspectionTest";
    public static final String      FIELD_WAFER_STATUS                 = "WaferStatus";

    public static final Color       DEFAULT_WAFER_FILL                 = Color.rgb(230, 230, 230);
    public static final Color       DEFAULT_WAFER_STROKE               = Color.rgb(128, 128, 128);
    public static final Color       DEFAULT_NOTCH_FILL                 = Color.BLACK;
    public static final Color       DEFAULT_DEFECT_FILL                = Color.BLACK;
    public static final Color       DEFAULT_DEFECT_STROKE              = Color.TRANSPARENT;
    public static final Color       DEFAULT_DIE_LABEL_FILL             = Color.rgb(150, 150, 150);
    public static final Color       DEFAULT_SELECTION_COLOR            = Color.rgb(0, 0, 255);
    public static final Color       DEFAULT_DIE_FILL                   = Color.rgb(230, 230, 230);
    public static final Color       DEFAULT_DIE_STROKE                 = Color.rgb(128, 128, 128);
    public static final List<Color> DEFAULT_DEFECT_DENSITY_COLORS      = List.of(Color.TRANSPARENT,
                                                                                 Color.rgb(151, 196, 232),
                                                                                 Color.rgb(128, 255, 128),
                                                                                 Color.rgb(220, 255, 128),
                                                                                 Color.rgb(255, 239, 128),
                                                                                 Color.rgb(255, 194, 128),
                                                                                 Color.rgb(255, 128, 128));

}
