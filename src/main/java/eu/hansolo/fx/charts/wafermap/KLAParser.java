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

import eu.hansolo.fx.charts.tools.Helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static eu.hansolo.toolbox.Constants.COLON;


public enum KLAParser {

    INSTANCE;

    public  static final DateTimeFormatter DTF                                  = DateTimeFormatter.ofPattern("MM-dd-yy HH:mm:ss");
    private static final Pattern           FILE_VERSION_PATTERN                 = Pattern.compile("^(FileVersion)\\s+([0-9]+)\\s+([0-9]+);");
    private static final Pattern           FILE_TIMESTAMP_PATTERN               = Pattern.compile("^(FileTimestamp)\\s+([0-9]{2})-([0-9]{2})-([0-9]{2})\\s+([0-9]{2}):([0-9]{2}):([0-9]{2});");
    private static final Pattern           INSPECTION_STATION_ID_PATTERN        = Pattern.compile("^(InspectionStationID)\\s+(\\\".*\\\")\\s+(\\\".*\\\")\\s+(\\\".*)\\\";$");
    private static final Pattern           SAMPLE_TYPE_PATTERN                  = Pattern.compile("^(SampleType)\\s+([a-zA-Z0-9]+);");
    private static final Pattern           RESULT_TIMESTAMP_PATTERN             = Pattern.compile("^(ResultTimestamp)\\s+([0-9]{2})-([0-9]{2})-([0-9]{2})\\s+([0-9]{2}):([0-9]{2}):([0-9]{2});");
    private static final Pattern           LOT_ID_PATTERN                       = Pattern.compile("^(LotID)\\s+(.*);");
    private static final Pattern           SAMPLE_SIZE_PATTERN                  = Pattern.compile("^(SampleSize)\\s+([0-9]+)\\s+([0-9]+);");
    private static final Pattern           SETUP_ID_PATTERN                     = Pattern.compile("^(SetupID)\\s+(\\\"\\w+\\\")\\s+([0-9]{2})-([0-9]{2})-([0-9]{2})\\s+([0-9]{2}):([0-9]{2}):([0-9]{2});");
    private static final Pattern           STEP_ID_PATTERN                      = Pattern.compile("^(StepID)\\s+(.*);");
    private static final Pattern           SAMPLE_ORIENTATION_MARK_TYPE_PATTERN = Pattern.compile("^(SampleOrientationMarkType)\\s+([a-zA-Z0-9]+);");
    private static final Pattern           ORIENTATION_MARK_LOCATION_PATTERN    = Pattern.compile("^(OrientationMarkLocation)\\s+([a-zA-Z0-9]+);");
    private static final Pattern           DIE_PITCH_PATTERN                    = Pattern.compile("^(DiePitch)\\s+([+|-]?(\\d+\\.\\d*(e[+|-][0-9]+)?))\\s+([+|-]?(\\d+\\.\\d*(e[+|-|-][0-9]+)?));");
    private static final Pattern           DIE_ORIGIN_PATTERN                   = Pattern.compile("^(DieOrigin)\\s+([+|-]?(\\d+\\.\\d*))\\s+([+|-]?(\\d+\\.\\d*));");
    private static final Pattern           WAFER_ID_PATTERN                     = Pattern.compile("^(WaferID)\\s+(.*);");
    private static final Pattern           SLOT_PATTERN                         = Pattern.compile("^(Slot)\\s+(.*);");
    private static final Pattern           SAMPLE_CENTER_LOCATION_PATTERN       = Pattern.compile("^(SampleCenterLocation)\\s+([+|-]?(\\d+\\.\\d*(e[+|-|-][0-9]+)?))\\s+([+|-]?(\\d+\\.\\d*(e[+|-][0-9]+)?));");
    private static final Pattern           CLASS_PATTERN                        = Pattern.compile("^\\s([0-9]+)\\s+\"(.*)\";?");
    private static final Pattern           INSPECTION_TEST_PATTERN              = Pattern.compile("^(InspectionTest)\\s+(.*);");
    private static final Pattern           SAMPLE_PATTERN                       = Pattern.compile("^\\s+(\\-?\\d+)\\s+(\\-?\\d+);?$");
    private static final Pattern           AREA_PER_TEST_PATTERN                = Pattern.compile("^(AreaPerTest)\\s+(\\d+\\.\\d*(e[+|-|-][0-9]+)?);");
    private static final Pattern           DEFECT_RECORD_SPEC_PATTERN           = Pattern.compile("^(DefectRecordSpec)\\s+(.*);");
    private static       Pattern           defectPattern;
    private static final Pattern           SUMMARY_SPEC_PATTERN                 = Pattern.compile("^(SummarySpec)\\s+(.*);");
    private static final Pattern           SUMMARY_PATTERN                      = Pattern.compile("^\\s([0-9]+)\\s+([0-9]+)\\s+(\\d+.\\d*)\\s+([0-9]+)\\s+([0-9]+);");
    private static final Pattern           WAFER_STATUS_PATTERN                 = Pattern.compile("^(WaferStatus)\\s+(.*);");

    private static final Matcher           FILE_VERSION_MATCHER                 = FILE_VERSION_PATTERN.matcher("");
    private static final Matcher           FILE_TIMESTAMP_MATCHER               = FILE_TIMESTAMP_PATTERN.matcher("");
    private static final Matcher           INSPECTION_STATION_ID_MATCHER        = INSPECTION_STATION_ID_PATTERN.matcher("");
    private static final Matcher           SAMPLE_TYPE_MATCHER                  = SAMPLE_TYPE_PATTERN.matcher("");
    private static final Matcher           RESULT_TIMESTAMP_MATCHER             = RESULT_TIMESTAMP_PATTERN.matcher("");
    private static final Matcher           LOT_ID_MATCHER                       = LOT_ID_PATTERN.matcher("");
    private static final Matcher           SAMPLE_SIZE_MATCHER                  = SAMPLE_SIZE_PATTERN.matcher("");
    private static final Matcher           SETUP_ID_MATCHER                     = SETUP_ID_PATTERN.matcher("");
    private static final Matcher           STEP_ID_MATCHER                      = STEP_ID_PATTERN.matcher("");
    private static final Matcher           SAMPLE_ORIENTATION_MARK_TYPE_MATCHER = SAMPLE_ORIENTATION_MARK_TYPE_PATTERN.matcher("");
    private static final Matcher           ORIENTATION_MARK_LOCATION_MATCHER    = ORIENTATION_MARK_LOCATION_PATTERN.matcher("");
    private static final Matcher           DIE_PITCH_MATCHER                    = DIE_PITCH_PATTERN.matcher("");
    private static final Matcher           DIE_ORIGIN_MATCHER                   = DIE_ORIGIN_PATTERN.matcher("");
    private static final Matcher           WAFER_ID_MATCHER                     = WAFER_ID_PATTERN.matcher("");
    private static final Matcher           SLOT_MATCHER                         = SLOT_PATTERN.matcher("");
    private static final Matcher           SAMPLE_CENTER_LOCATION_MATCHER       = SAMPLE_CENTER_LOCATION_PATTERN.matcher("");
    private static final Matcher           CLASS_MATCHER                        = CLASS_PATTERN.matcher("");
    private static final Matcher           INSPECTION_TEST_MATCHER              = INSPECTION_TEST_PATTERN.matcher("");
    private static final Matcher           SAMPLE_MATCHER                       = SAMPLE_PATTERN.matcher("");
    private static final Matcher           AREA_PER_TEST_MATCHER                = AREA_PER_TEST_PATTERN.matcher("");
    private static final Matcher           DEFECT_RECORD_SPEC_MATCHER           = DEFECT_RECORD_SPEC_PATTERN.matcher("");
    private static       Matcher           defectMatcher;
    private static final Matcher           SUMMARY_SPEC_MATCHER                 = SUMMARY_SPEC_PATTERN.matcher("");
    private static final Matcher           SUMMARY_MATCHER                      = SUMMARY_PATTERN.matcher("");
    private static final Matcher           WAFER_STATUS_MATCHER                 = WAFER_STATUS_PATTERN.matcher("");
    private static final Map<DefectRecordField, Integer> groupOffsets   = new HashMap<>();


    public Optional<KLA> parse(final String filename) {
        if (null == filename || filename.isEmpty()) { return Optional.empty(); }
        if (Helper.isFileReadable(filename) && Helper.check4KLAFormat(filename)) {
            groupOffsets.clear();
            final KLA               kla            = new KLA();
            final List<DefectClass> classes        = new LinkedList<>();
            final List<SampleTest>  sampleTestPlan = new LinkedList<>();
            final List<Defect>      defects        = new ArrayList<>();

            try (Stream<String> stream = Files.lines(Paths.get(filename))) {
                stream.forEach(line -> {
                    // FileVersion
                    FILE_VERSION_MATCHER.reset(line);
                    while (FILE_VERSION_MATCHER.find()) {
                        final int fileVersionMajor = Integer.parseInt(FILE_VERSION_MATCHER.group(2));
                        final int fileVersionMinor = Integer.parseInt(FILE_VERSION_MATCHER.group(3));
                        kla.setFileVersionMajor(fileVersionMajor);
                        kla.setFileVersionMinor(fileVersionMinor);
                    }
                    if (FILE_VERSION_MATCHER.matches()) { return; }

                    // FileTimestamp
                    FILE_TIMESTAMP_MATCHER.reset(line);
                    while(FILE_TIMESTAMP_MATCHER.find()) {
                        final String fileTimestamp = new StringBuilder().append(FILE_TIMESTAMP_MATCHER.group(2))
                                                                        .append("-")
                                                                        .append(FILE_TIMESTAMP_MATCHER.group(3))
                                                                        .append("-")
                                                                        .append(FILE_TIMESTAMP_MATCHER.group(4))
                                                                        .append(Constants.SPACE)
                                                                        .append(FILE_TIMESTAMP_MATCHER.group(5))
                                                                        .append(COLON)
                                                                        .append(FILE_TIMESTAMP_MATCHER.group(6))
                                                                        .append(COLON)
                                                                        .append(FILE_TIMESTAMP_MATCHER.group(7))
                                                                        .toString();
                        kla.setFileTimestamp(LocalDateTime.parse(fileTimestamp, DTF));
                    }
                    if (FILE_TIMESTAMP_MATCHER.matches()) { return; }

                    // InspectionStationID
                    INSPECTION_STATION_ID_MATCHER.reset(line);
                    while (INSPECTION_STATION_ID_MATCHER.find()) {
                        final String inspectionStationIdPart1 = INSPECTION_STATION_ID_MATCHER.group(2).replaceAll("\\\"", "");
                        final String inspectionStationIdPart2 = INSPECTION_STATION_ID_MATCHER.group(3).replaceAll("\\\"", "");
                        final String inspectionStationIdPart3 = INSPECTION_STATION_ID_MATCHER.group(4).replaceAll("\\\"", "");
                        kla.setInspectionStationIdPart1(inspectionStationIdPart1);
                        kla.setInspectionStationIdPart2(inspectionStationIdPart2);
                        kla.setInspectionStationIdPart3(inspectionStationIdPart3);
                    }
                    if (INSPECTION_STATION_ID_MATCHER.matches()) { return; }

                    // SampleType
                    SAMPLE_TYPE_MATCHER.reset(line);
                    while (SAMPLE_TYPE_MATCHER.find()) {
                        final String sampleType = SAMPLE_TYPE_MATCHER.group(2);
                        kla.setSampleType(SampleType.valueOf(sampleType));
                    }
                    if (SAMPLE_TYPE_MATCHER.matches()) { return; }

                    // ResultTimestamp
                    RESULT_TIMESTAMP_MATCHER.reset(line);
                    while(RESULT_TIMESTAMP_MATCHER.find()) {
                        final String resultTimestamp = new StringBuilder().append(RESULT_TIMESTAMP_MATCHER.group(2))
                                                                          .append("-")
                                                                          .append(RESULT_TIMESTAMP_MATCHER.group(3))
                                                                          .append("-")
                                                                          .append(RESULT_TIMESTAMP_MATCHER.group(4))
                                                                          .append(Constants.SPACE)
                                                                          .append(RESULT_TIMESTAMP_MATCHER.group(5))
                                                                          .append(COLON)
                                                                          .append(RESULT_TIMESTAMP_MATCHER.group(6))
                                                                          .append(COLON)
                                                                          .append(RESULT_TIMESTAMP_MATCHER.group(7))
                                                                          .toString();
                        kla.setResultTimestamp(LocalDateTime.parse(resultTimestamp, DTF));
                    }
                    if (RESULT_TIMESTAMP_MATCHER.matches()) { return; }

                    // LotID
                    LOT_ID_MATCHER.reset(line);
                    while (LOT_ID_MATCHER.find()) {
                        final String lotID = LOT_ID_MATCHER.group(2).replaceAll("\\\"", "");
                        kla.setLotID(lotID);
                    }
                    if (LOT_ID_MATCHER.matches()) { return; }

                    // SampleSize
                    SAMPLE_SIZE_MATCHER.reset(line);
                    while (SAMPLE_SIZE_MATCHER.find()) {
                        final String sampleSize = SAMPLE_SIZE_MATCHER.group(3);
                        kla.setSampleSize(Double.parseDouble(sampleSize));
                    }
                    if (SAMPLE_SIZE_MATCHER.matches()) { return; }

                    // SetupID
                    SETUP_ID_MATCHER.reset(line);
                    while (SETUP_ID_MATCHER.find()) {
                        final String setupIdName      = SETUP_ID_MATCHER.group(2).replaceAll("\\\"", "");
                        final String setupIdTimestamp = new StringBuilder().append(SETUP_ID_MATCHER.group(3))
                                                                           .append("-")
                                                                           .append(SETUP_ID_MATCHER.group(4))
                                                                           .append("-")
                                                                           .append(SETUP_ID_MATCHER.group(5))
                                                                           .append(Constants.SPACE)
                                                                           .append(SETUP_ID_MATCHER.group(6))
                                                                           .append(COLON)
                                                                           .append(SETUP_ID_MATCHER.group(7))
                                                                           .append(COLON)
                                                                           .append(SETUP_ID_MATCHER.group(8))
                                                                           .toString();
                        kla.setSetupIdName(setupIdName);
                        kla.setSetupIdTimestamp(LocalDateTime.parse(setupIdTimestamp, DTF));
                    }
                    if (SETUP_ID_MATCHER.matches()) { return; }

                    // StepID
                    STEP_ID_MATCHER.reset(line);
                    while (STEP_ID_MATCHER.find()) {
                        final String stepID = STEP_ID_MATCHER.group(2).replaceAll("\\\"", "");
                        kla.setStepID(stepID);
                    }
                    if (STEP_ID_MATCHER.matches()) { return; }

                    // SampleOrientationMark
                    SAMPLE_ORIENTATION_MARK_TYPE_MATCHER.reset(line);
                    while (SAMPLE_ORIENTATION_MARK_TYPE_MATCHER.find()) {
                        final String sampleOrientationMarkType = SAMPLE_ORIENTATION_MARK_TYPE_MATCHER.group(2);
                        kla.setSampleOrientationMarkType(SampleOrientationMarkType.valueOf(sampleOrientationMarkType));
                    }
                    if (SAMPLE_ORIENTATION_MARK_TYPE_MATCHER.matches()) { return; }

                    // OrientationMarkLocation
                    ORIENTATION_MARK_LOCATION_MATCHER.reset(line);
                    while (ORIENTATION_MARK_LOCATION_MATCHER.find()) {
                        final String orientationMarkLocation = ORIENTATION_MARK_LOCATION_MATCHER.group(2);
                        kla.setOrientationMarkLocation(OrientationMarkLocation.valueOf(orientationMarkLocation));
                    }
                    if (ORIENTATION_MARK_LOCATION_MATCHER.matches()) { return; }

                    // DiePitch
                    DIE_PITCH_MATCHER.reset(line);
                    while (DIE_PITCH_MATCHER.find()) {
                        final String diePitchX = DIE_PITCH_MATCHER.group(2);
                        final String diePitchY = DIE_PITCH_MATCHER.group(5);
                        kla.setDiePitchX(Double.parseDouble(diePitchX));
                        kla.setDiePitchY(Double.parseDouble(diePitchY));
                    }
                    if (DIE_PITCH_MATCHER.matches()) { return; }

                    // DieOrigin
                    DIE_ORIGIN_MATCHER.reset(line);
                    while (DIE_ORIGIN_MATCHER.find()) {
                        final String dieOriginX = DIE_ORIGIN_MATCHER.group(2);
                        final String dieOriginY = DIE_ORIGIN_MATCHER.group(3);
                        kla.setDieOriginX(Double.parseDouble(dieOriginX));
                        kla.setDieOriginY(Double.parseDouble(dieOriginY));
                    }
                    if (DIE_ORIGIN_MATCHER.matches()) { return; }

                    // WaferID
                    WAFER_ID_MATCHER.reset(line);
                    while (WAFER_ID_MATCHER.find()) {
                        final String waferID = WAFER_ID_MATCHER.group(2).replaceAll("\\\"", "");
                        kla.setWaferId(waferID);
                    }
                    if (WAFER_ID_MATCHER.matches()) { return; }

                    // Slot
                    SLOT_MATCHER.reset(line);
                    while (SLOT_MATCHER.find()) {
                        final String slot = SLOT_MATCHER.group(2);
                        kla.setSlot(Integer.parseInt(slot));
                    }
                    if (SLOT_MATCHER.matches()) { return; }

                    // SampleCenterLocation
                    SAMPLE_CENTER_LOCATION_MATCHER.reset(line);
                    while (SAMPLE_CENTER_LOCATION_MATCHER.find()) {
                        final String sampleCenterLocationX = SAMPLE_CENTER_LOCATION_MATCHER.group(2);
                        final String sampleCenterLocationY = SAMPLE_CENTER_LOCATION_MATCHER.group(5);
                        kla.setSampleCenterLocationX(Double.parseDouble(sampleCenterLocationX));
                        kla.setSampleCenterLocationY(Double.parseDouble(sampleCenterLocationY));
                    }
                    if (SAMPLE_CENTER_LOCATION_MATCHER.matches()) { return; }

                    // Classes
                    CLASS_MATCHER.reset(line);
                    while (CLASS_MATCHER.find()) {
                        final Integer classID   = Integer.valueOf(CLASS_MATCHER.group(1));
                        final String  className = CLASS_MATCHER.group(2).replaceAll("\\\"", "");
                        final DefectClass defectClass = new DefectClass(classID, className);
                        classes.add(defectClass);
                    }
                    if (CLASS_MATCHER.matches()) { return; }

                    // Samples
                    SAMPLE_MATCHER.reset(line);
                    while (SAMPLE_MATCHER.find()) {
                        final Integer indexX = Integer.valueOf(SAMPLE_MATCHER.group(1));
                        final Integer indexY = Integer.valueOf(SAMPLE_MATCHER.group(2));
                        sampleTestPlan.add(new SampleTest(indexX, indexY));
                    }
                    if (CLASS_MATCHER.matches()) { return; }

                    // InspectionTest
                    INSPECTION_TEST_MATCHER.reset(line);
                    while (INSPECTION_TEST_MATCHER.find()) {
                        final int inspectionTest = Integer.parseInt(INSPECTION_TEST_MATCHER.group(2));
                        kla.setInspectionTest(inspectionTest);
                    }

                    // AreaPerTest
                    AREA_PER_TEST_MATCHER.reset(line);
                    while (AREA_PER_TEST_MATCHER.find()) {
                        final double areaPerTest = Double.parseDouble(AREA_PER_TEST_MATCHER.group(2));
                        kla.setAreaPerTest(areaPerTest);
                    }
                    if (AREA_PER_TEST_MATCHER.matches()) { return; }

                    // DefectRecordSpec
                    DEFECT_RECORD_SPEC_MATCHER.reset(line);
                    while (DEFECT_RECORD_SPEC_MATCHER.find()) {
                        final String defectRecordSpec = DEFECT_RECORD_SPEC_MATCHER.group(2);
                        Map<Integer, DefectRecordField> defectFieldMap = new HashMap<>();
                        AtomicInteger                   currentOffset  = new AtomicInteger(1);
                        DefectRecordField.getAsList() .forEach(field -> {
                            int index = defectRecordSpec.indexOf(field.name);
                            if (index != -1) { defectFieldMap.put(index, field); }
                        });
                        StringBuilder patternBuilder = new StringBuilder();
                        patternBuilder.append("^");
                        defectFieldMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
                            patternBuilder.append("\\s+").append(entry.getValue().pattern);
                            groupOffsets.put(entry.getValue(), currentOffset.get());
                            currentOffset.set(currentOffset.get() + entry.getValue().groups);
                        });
                        patternBuilder.append(";?");
                        defectPattern = Pattern.compile(patternBuilder.toString());
                        defectMatcher = defectPattern.matcher("");
                    }
                    if (DEFECT_RECORD_SPEC_MATCHER.matches()) { return; }

                    // Defects
                    if (null != defectMatcher) {
                        defectMatcher.reset(line);
                        while (defectMatcher.find()) {
                            final int defectID = Integer.parseInt(defectMatcher.group(groupOffsets.get(DefectRecordField.DEFECT_ID)));

                            DefectBuilder defectBuilder = DefectBuilder.create(defectID);
                            if (groupOffsets.containsKey(DefectRecordField.X_REL)) {
                                defectBuilder.relX(Double.parseDouble(defectMatcher.group(groupOffsets.get(DefectRecordField.X_REL))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.Y_REL)) {
                                defectBuilder.relY(Double.parseDouble(defectMatcher.group(groupOffsets.get(DefectRecordField.Y_REL))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.X_INDEX)) {
                                defectBuilder.indexX(Integer.parseInt(defectMatcher.group(groupOffsets.get(DefectRecordField.X_INDEX))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.Y_INDEX)) {
                                defectBuilder.indexY(Integer.parseInt(defectMatcher.group(groupOffsets.get(DefectRecordField.Y_INDEX))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.X_SIZE)) {
                                defectBuilder.sizeX(Double.parseDouble(defectMatcher.group(groupOffsets.get(DefectRecordField.X_SIZE))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.Y_SIZE)) {
                                defectBuilder.sizeY(Double.parseDouble(defectMatcher.group(groupOffsets.get(DefectRecordField.Y_SIZE))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.DEFECT_AREA)) {
                                defectBuilder.defectArea(Double.parseDouble(defectMatcher.group(groupOffsets.get(DefectRecordField.DEFECT_AREA))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.D_SIZE)) {
                                defectBuilder.sizeD(Double.parseDouble(defectMatcher.group(groupOffsets.get(DefectRecordField.D_SIZE))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.CLASS_NUMBER)) {
                                defectBuilder.classNumber(Integer.parseInt(defectMatcher.group(groupOffsets.get(DefectRecordField.CLASS_NUMBER))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.ROUGH_BIN_NUMBER)) {
                                defectBuilder.fineBinNumber(Integer.parseInt(defectMatcher.group(groupOffsets.get(DefectRecordField.ROUGH_BIN_NUMBER))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.FINE_BIN_NUMBER)) {
                                defectBuilder.fineBinNumber(Integer.parseInt(defectMatcher.group(groupOffsets.get(DefectRecordField.FINE_BIN_NUMBER))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.TEST)) {
                                defectBuilder.test(Integer.parseInt(defectMatcher.group(groupOffsets.get(DefectRecordField.TEST))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.CLUSTER_NUMBER)) {
                                defectBuilder.clusterNumber(Integer.parseInt(defectMatcher.group(groupOffsets.get(DefectRecordField.CLUSTER_NUMBER))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.IMAGE_COUNT)) {
                                defectBuilder.imageCount(Integer.parseInt(defectMatcher.group(groupOffsets.get(DefectRecordField.IMAGE_COUNT))));
                            }
                            if (groupOffsets.containsKey(DefectRecordField.IMAGE_LIST)) {
                                defectBuilder.imageList(defectMatcher.group(groupOffsets.get(DefectRecordField.IMAGE_LIST)).split("\\s+"));
                            }
                            Defect defect = defectBuilder.build();
                            defects.add(defect);
                        }
                        if (defectMatcher.matches()) { return; }
                    }

                    // SummarySpec
                    SUMMARY_SPEC_MATCHER.reset(line);
                    while (SUMMARY_SPEC_MATCHER.find()) {
                        final String summarySpec = SUMMARY_SPEC_MATCHER.group(2);
                        kla.setSummarySpec(summarySpec);
                    }
                    if (SUMMARY_SPEC_MATCHER.matches()) { return; }

                    // Summary
                    SUMMARY_MATCHER.reset(line);
                    while (SUMMARY_MATCHER.find()) {
                        final int    testNo             = Integer.parseInt(SUMMARY_MATCHER.group(1));
                        final int    numberOfDefects    = Integer.parseInt(SUMMARY_MATCHER.group(2));
                        final double defectDensity      = Double.parseDouble(SUMMARY_MATCHER.group(3));
                        final int    numberOfDies       = Integer.parseInt(SUMMARY_MATCHER.group(4));
                        final int    numberOfDefectDies = Integer.parseInt(SUMMARY_MATCHER.group(5));
                        kla.setTestNo(testNo);
                        kla.setNumberOfDefects(numberOfDefects);
                        kla.setDefectDensity(defectDensity);
                        kla.setNumberOfDies(numberOfDies);
                        kla.setNumberOfDefectDies(numberOfDefectDies);
                    }
                    if (SUMMARY_MATCHER.matches()) { return; }

                    // WaferStatus
                    WAFER_STATUS_MATCHER.reset(line);
                    while (WAFER_STATUS_MATCHER.find()) {
                        final String waferStatus = WAFER_STATUS_MATCHER.group(2);
                        kla.setWaferStatus(waferStatus);
                    }
                    if (WAFER_STATUS_MATCHER.matches()) { return; }
                });

                kla.setClasses(classes);
                kla.setSampleTestPlan(sampleTestPlan);
                kla.setDefects(defects);

                return Optional.of(kla);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}
