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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static eu.hansolo.fx.charts.wafermap.KLAParser.DTF;
import static eu.hansolo.toolbox.Constants.NEW_LINE;


public final class KLA {
    private int                       id;
    private int                       fileVersionMajor;
    private int                       fileVersionMinor;
    private LocalDateTime             fileTimestamp;
    private String                    inspectionStationIdPart1;
    private String                    inspectionStationIdPart2;
    private String                    inspectionStationIdPart3;
    private SampleType                sampleType;
    private LocalDateTime             resultTimestamp;
    private String                    lotID;
    private double                    sampleSize; // [mm]
    private String                    setupIdName;
    private LocalDateTime             setupIdTimestamp;
    private String                    stepID;
    private SampleOrientationMarkType sampleOrientationMarkType;
    private OrientationMarkLocation   orientationMarkLocation;
    private double                    diePitchX;  // [µm]
    private double                    diePitchY;  // [µm]
    private double                    dieOriginX; // [µm]
    private double                    dieOriginY; // [µm]
    private String                    waferId;
    private int                       slot;
    private double                    sampleCenterLocationX;
    private double                    sampleCenterLocationY;
    private int                       inspectionTest;
    private double                    areaPerTest;
    private String                    defectRecordSpec;
    private String                    summarySpec;
    private int                       testNo;
    private int                       numberOfDefects;
    private double                    defectDensity;
    private int                       numberOfDies;
    private int                       numberOfDefectDies;
    private String                    waferStatus;
    private String                    filename;
    private List<DefectClass>         classes;
    private List<SampleTest>          sampleTestPlan;
    private List<Defect>              defects;

    private Map<String, Die>          dies;
    private int                       diesMinX;
    private int                       diesMaxX;
    private int                       diesMinY;
    private int                       diesMaxY;

    private int                       maxDefectsPerDie;


    // ******************** Constructor ***************************************
    public KLA() {
        this.id                        = -1;
        this.fileVersionMajor          = -1;
        this.fileVersionMinor          = -1;
        this.fileTimestamp             = LocalDateTime.MIN;
        this.inspectionStationIdPart1  = "";
        this.inspectionStationIdPart2  = "";
        this.inspectionStationIdPart3  = "";
        this.sampleType                = SampleType.WAFER;
        this.resultTimestamp           = LocalDateTime.MIN;
        this.lotID                     = "";
        this.sampleSize                = -1;
        this.setupIdName               = "";
        this.setupIdTimestamp          = LocalDateTime.MIN;
        this.stepID                    = "";
        this.sampleOrientationMarkType = SampleOrientationMarkType.NOTCH;
        this.orientationMarkLocation   = OrientationMarkLocation.NONE;
        this.diePitchX                 = -1;
        this.diePitchY                 = -1;
        this.dieOriginX                = -1;
        this.dieOriginY                = -1;
        this.waferId                   = "";
        this.slot                      = -1;
        this.sampleCenterLocationX     = -1;
        this.sampleCenterLocationY     = -1;
        this.inspectionTest            = -1;
        this.areaPerTest               = -1;
        this.defectRecordSpec          = "";
        this.summarySpec               = "";
        this.testNo                    = -1;
        this.numberOfDefects           = -1;
        this.defectDensity             = -1;
        this.numberOfDies              = -1;
        this.numberOfDefectDies        = -1;
        this.waferStatus               = "";
        this.filename                  = "";
        this.classes                   = new LinkedList<>();
        this.sampleTestPlan            = new LinkedList<>();
        this.defects                   = new ArrayList<>();
        this.diesMinX                  = 0;
        this.diesMaxX                  = 0;
        this.diesMinY                  = 0;
        this.diesMaxY                  = 0;
        this.dies                      = new HashMap<>();
        this.maxDefectsPerDie          = -1;
    }


    // ******************** Methods *******************************************
    public int getId() { return id; }
    public void setId(final int id) { this.id = id; }

    public int getFileVersionMajor() { return fileVersionMajor; }
    public void setFileVersionMajor(final int fileVersionMajor) { this.fileVersionMajor = fileVersionMajor; }

    public int getFileVersionMinor() { return fileVersionMinor; }
    public void setFileVersionMinor(final int fileVersionMinor) { this.fileVersionMinor = fileVersionMinor; }

    public LocalDateTime getFileTimestamp() { return fileTimestamp; }
    public void setFileTimestamp(final LocalDateTime fileTimestamp) { this.fileTimestamp = fileTimestamp; }

    public String getInspectionStationIdPart1() { return inspectionStationIdPart1; }
    public void setInspectionStationIdPart1(final String inspectionStationIdPart1) { this.inspectionStationIdPart1 = inspectionStationIdPart1; }

    public String getInspectionStationIdPart2() { return inspectionStationIdPart2; }
    public void setInspectionStationIdPart2(final String inspectionStationIdPart2) { this.inspectionStationIdPart2 = inspectionStationIdPart2; }

    public String getInspectionStationIdPart3() { return inspectionStationIdPart3; }
    public void setInspectionStationIdPart3(final String inspectionStationIdPart3) { this.inspectionStationIdPart3 = inspectionStationIdPart3; }

    public SampleType getSampleType() { return sampleType; }
    public void setSampleType(final SampleType sampleType) { this.sampleType = sampleType; }

    public LocalDateTime getResultTimestamp() { return resultTimestamp; }
    public void setResultTimestamp(final LocalDateTime resultTimestamp) { this.resultTimestamp = resultTimestamp; }

    public String getLotID() { return lotID; }
    public void setLotID(final String lotID) { this.lotID = lotID; }

    public double getSampleSize() { return sampleSize; }
    public void setSampleSize(final double sampleSize) { this.sampleSize = sampleSize; }

    public String getSetupIdName() { return setupIdName; }
    public void setSetupIdName(final String setupIdName) { this.setupIdName = setupIdName; }

    public LocalDateTime getSetupIdTimestamp() { return this.setupIdTimestamp; }
    public void setSetupIdTimestamp(final LocalDateTime setupIdTimestamp) { this.setupIdTimestamp = setupIdTimestamp; }

    public String getStepID() { return stepID; }
    public void setStepID(final String stepID) { this.stepID = stepID; }

    public SampleOrientationMarkType getSampleOrientationMarkType() { return sampleOrientationMarkType; }
    public void setSampleOrientationMarkType(final SampleOrientationMarkType sampleOrientationMarkType) { this.sampleOrientationMarkType = sampleOrientationMarkType; }

    public OrientationMarkLocation getOrientationMarkLocation() { return orientationMarkLocation; }
    public void setOrientationMarkLocation(final OrientationMarkLocation orientationMarkLocation) { this.orientationMarkLocation = orientationMarkLocation; }

    public double getDiePitchX() { return diePitchX; }
    public void setDiePitchX(final double diePitchX) { this.diePitchX = diePitchX; }

    public double getDiePitchY() { return diePitchY; }
    public void setDiePitchY(final double diePitchY) { this.diePitchY = diePitchY; }

    public double getDieOriginX() { return dieOriginX; }
    public void setDieOriginX(final double dieOriginX) { this.dieOriginX = dieOriginX; }

    public double getDieOriginY() { return dieOriginY; }
    public void setDieOriginY(final double dieOriginY) { this.dieOriginY = dieOriginY; }

    public String getWaferId() { return waferId; }
    public void setWaferId(final String waferId) { this.waferId = waferId; }

    public int getSlot() { return slot; }
    public void setSlot(final int slot) { this.slot = slot; }

    public double getSampleCenterLocationX() { return sampleCenterLocationX; }
    public void setSampleCenterLocationX(final double sampleCenterLocationX) { this.sampleCenterLocationX = sampleCenterLocationX; }

    public double getSampleCenterLocationY() { return sampleCenterLocationY; }
    public void setSampleCenterLocationY(final double sampleCenterLocationY) { this.sampleCenterLocationY = sampleCenterLocationY; }

    public int getInspectionTest() { return inspectionTest; }
    public void setInspectionTest(final int inspectionTest) { this.inspectionTest = inspectionTest; }

    public double getAreaPerTest() { return this.areaPerTest; }
    public void setAreaPerTest(final double areaPerTest) { this.areaPerTest = areaPerTest ;}

    public String getDefectRecordSpec() { return this.defectRecordSpec; }
    public void setDefectRecordSpec(final String defectRecordSpec) { this.defectRecordSpec = defectRecordSpec; }

    public String getSummarySpec() { return this.summarySpec; }
    public void setSummarySpec(final String summarySpec) { this.summarySpec = summarySpec; }

    public int getTestNo() { return testNo; }
    public void setTestNo(final int testNo) { this.testNo = testNo; }

    public int getNumberOfDefects() { return numberOfDefects; }
    public void setNumberOfDefects(final int numberOfDefects) { this.numberOfDefects = numberOfDefects; }

    public double getDefectDensity() { return defectDensity; }
    public void setDefectDensity(final double defectDensity) { this.defectDensity = defectDensity; }

    public int getNumberOfDies() { return numberOfDies; }
    public void setNumberOfDies(final int numberOfDies) { this.numberOfDies = numberOfDies; }

    public int getNumberOfDefectDies() { return numberOfDefectDies; }
    public void setNumberOfDefectDies(final int numberOfDefectDies) { this.numberOfDefectDies = numberOfDefectDies; }

    public String getWaferStatus() { return waferStatus; }
    public void setWaferStatus(final String waferStatus) { this.waferStatus = waferStatus; }

    public String getFilename() { return filename; }
    public void setFilename(final String filename) { this.filename = filename; }

    public List<DefectClass> getClasses() { return classes; }
    public void setClasses(final List<DefectClass> classes) { this.classes = classes; }

    public List<SampleTest> getSampleTestPlan() { return sampleTestPlan; }
    public void setSampleTestPlan(final List<SampleTest> sampleTestPlan) { this.sampleTestPlan = sampleTestPlan; }

    public List<Defect> getDefects() { return defects; }
    public void setDefects(final List<Defect> defects) { this.defects = defects; }

    public Map<String, Die> getDies() { return dies; }
    public void setDies(final Map<String, Die> dies) { this.dies = dies; }

    public void createDieMap() {
        // Offset between xy and uv coordinate system
        final double offsetX = getSampleCenterLocationX() / 1_000.0;
        final double offsetY = getSampleCenterLocationY() / 1_000.0;

        // Radius of wafer
        final double radius = sampleSize / 2.0;

        // Physical center of wafer
        final double originX = sampleSize / 2.0;
        final double originY = sampleSize / 2.0;

        // Origin of uv system
        final double originU = originX - offsetX;
        final double originV = originY - offsetY;

        // Size of die
        final double dieSizeX = getDiePitchX() / 1_000.0; // [mm]
        final double dieSizeY = getDiePitchY() / 1_000.0; // [mm]

        // Get die coordinates
        double dieUpperLeft;
        double dieUpperRight;
        double dieLowerLeft;
        double dieLowerRight;

        // Number of available dies in each direction
        this.diesMinX = (int) (originU / dieSizeX);
        this.diesMaxX = (int) ((sampleSize - originU) / dieSizeX);
        this.diesMinY = (int) (originV / dieSizeY + dieSizeY);
        this.diesMaxY = (int) ((sampleSize - originV) / dieSizeY);

        // Counter for die indices
        final AtomicInteger dieCountX = new AtomicInteger(-diesMinX);
        final AtomicInteger dieCountY = new AtomicInteger(diesMaxY + 2);

        // Start value for counting from upper left corner
        final double startX = originX - ((double) diesMinX * dieSizeX) - offsetX;
        final double startY = (originY + ((double) diesMaxY * dieSizeY) + offsetY);

        // Keep dies in list
        dies.clear();

        final double maxX = (originU + radius);
        final double maxY = (originV - (diesMinY * dieSizeY) - dieSizeY);

        for (double dieX = startX; dieX <= maxX; dieX += dieSizeX) {
            double dieOriginX = originX - dieX;
            for (double dieY = startY; dieY >= maxY; dieY -= dieSizeY) {
                double dieOriginY = originY - dieY - dieSizeY;
                dieUpperLeft  = Math.sqrt((dieOriginX * dieOriginX) + (dieOriginY * dieOriginY));
                dieUpperRight = Math.sqrt(((dieOriginX - dieSizeX) * (dieOriginX - dieSizeX)) + (dieOriginY * dieOriginY));
                dieLowerLeft  = Math.sqrt((dieOriginX * dieOriginX) + ((dieOriginY - dieSizeY) * (dieOriginY - dieSizeY)));
                dieLowerRight = Math.sqrt(((dieOriginX - dieSizeX) * (dieOriginX - dieSizeX)) + ((dieOriginY - dieSizeY) * (dieOriginY - dieSizeY)));

                if (dieUpperLeft <= radius && dieUpperRight <= radius && dieLowerLeft <= radius && dieLowerRight <= radius) {
                    final String dieLabel = dieCountX + "/" + (-dieCountY.get());
                    final Die    die      = new Die(dieCountX.get(), dieCountY.get(), this.id, dieX, (dieY + dieSizeY), dieSizeX, dieSizeY, dieLabel.toString(), offsetX, offsetY);

                    die.setDefects(defects.stream().filter(defect -> defect.getIndexX() == dieCountX.get() && defect.getIndexY() == dieCountY.get()).collect(Collectors.toList()));
                    dies.put(dieLabel.toString(), die);
                }
                dieCountY.decrementAndGet();
            }
            dieCountY.set(diesMaxY + 2);
            dieCountX.incrementAndGet();
        }
        maxDefectsPerDie = dies.values().isEmpty() ? 0 : Collections.max(dies.values(), Comparator.comparing(d -> d.getNoOfDefects())).getNoOfDefects();

        // Update used classes and absolute defect positions
        for (Defect defect : defects) {
            // Update used classes
            Optional<DefectClass> optDefectClass = classes.stream().filter(clazz -> clazz.getId() == defect.getClassNumber()).findFirst();
            if (optDefectClass.isPresent()) { optDefectClass.get().setUsed(true); }

            // Update absolute defect position
            if (dies.containsKey(defect.getLabel())) {
                double defectXAbsolute = dieSizeX * defect.getIndexX() + defect.getXRel();
                double defectYAbsolute = dieSizeY * defect.getIndexY() + defect.getYRel();
                defect.setXAbsolute(defectXAbsolute);
                defect.setYAbsolute(defectYAbsolute);
            }
        }
    }

    public int getMaxDefectsPerDie() { return this.maxDefectsPerDie; }


    @Override public String toString() {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append(Constants.FIELD_FILE_VERSION).append(Constants.SPACE).append(this.fileVersionMajor).append(Constants.SPACE).append(this.fileVersionMinor).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_FILE_TIME_STAMP).append(Constants.SPACE).append(DTF.format(this.fileTimestamp)).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_INSPECTION_STATION_ID).append(Constants.SPACE).append("\"").append(this.inspectionStationIdPart1).append("\"").append(Constants.SPACE).append("\"").append(this.inspectionStationIdPart2).append("\"").append(Constants.SPACE).append("\"").append(this.inspectionStationIdPart3).append("\"").append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_SAMPLE_TYPE).append(Constants.SPACE).append(sampleType.name()).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_RESULT_TIME_STAMP).append(Constants.SPACE).append(DTF.format(this.resultTimestamp)).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_LOT_ID).append(Constants.SPACE).append("\"").append(lotID).append("\"").append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_SAMPLE_SIZE).append(Constants.SPACE).append("1").append(Constants.SPACE).append(String.format(Locale.US, "%.0f", this.sampleSize)).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_SETUP_ID).append(Constants.SPACE).append("\"").append(this.setupIdName).append("\"").append(Constants.SPACE).append(DTF.format(setupIdTimestamp)).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_STEP_ID).append(Constants.SPACE).append("\"").append(this.stepID).append("\"").append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_SAMPLE_ORIENTATION_MARK_TYPE).append(Constants.SPACE).append(sampleOrientationMarkType.name()).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_ORIENTATION_MARK_LOCATION).append(Constants.SPACE).append(orientationMarkLocation.name()).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_DIE_PITCH).append(Constants.SPACE).append(String.format(Locale.US, "%.10e", this.diePitchX)).append(Constants.SPACE).append(String.format(Locale.US, "%.10e", this.diePitchY)).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_DIE_ORIGIN).append(Constants.SPACE).append(String.format(Locale.US, "%.6f", this.dieOriginX)).append(Constants.SPACE).append(String.format(Locale.US, "%.6f", this.dieOriginY)).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_WAFER_ID).append(Constants.SPACE).append("\"").append(this.waferId).append("\"").append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_SLOT).append(Constants.SPACE).append(this.slot).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_SAMPLE_CENTER_LOCATION).append(Constants.SPACE).append(String.format(Locale.US, "%.10e", this.sampleCenterLocationX)).append(Constants.SPACE).append(String.format(Locale.US, "%.10e", this.sampleCenterLocationY)).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_CLASS_LOOKUP).append(Constants.SPACE).append(classes.size()).append(NEW_LINE);
        classes.forEach(defectClass-> msgBuilder.append(defectClass.toString()).append(NEW_LINE));
        msgBuilder.setLength(msgBuilder.length() - 1);
        msgBuilder.append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_INSPECTION_TEST).append(Constants.SPACE).append(this.inspectionTest).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_SAMPLE_TEST_PLAN).append(Constants.SPACE).append(sampleTestPlan.size()).append(NEW_LINE);
        sampleTestPlan.forEach(sampleTest -> msgBuilder.append(sampleTest.toString()).append(NEW_LINE));
        msgBuilder.setLength(msgBuilder.length() - 1);
        msgBuilder.append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_AREA_PER_TEST).append(Constants.SPACE).append(String.format(Locale.US, "%.10e", this.areaPerTest)).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_DEFECT_RECORD_SPEC).append(Constants.SPACE).append(this.defectRecordSpec).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_DEFECT_LIST).append(NEW_LINE);
        defects.forEach(defect -> msgBuilder.append(defect.toString()).append(NEW_LINE));
        msgBuilder.setLength(msgBuilder.length() - 1);
        msgBuilder.append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_SUMMARY_SPEC).append(Constants.SPACE).append(this.summarySpec).append(Constants.SEMICOLON).append(NEW_LINE)
                  .append(Constants.FIELD_SUMMARY_LIST).append(NEW_LINE)
                  .append(Constants.SPACE).append(testNo).append(Constants.SPACE).append(numberOfDefects).append(Constants.SPACE).append(String.format(Locale.US, "%.6f", defectDensity)).append(Constants.SPACE).append(numberOfDies).append(Constants.SPACE).append(numberOfDefectDies).append(Constants.SEMICOLON).append(NEW_LINE);
        if (!waferStatus.isEmpty()) {
            msgBuilder.append(Constants.FIELD_WAFER_STATUS).append(Constants.SPACE).append(this.waferStatus).append(Constants.SEMICOLON).append(NEW_LINE);
        }
        msgBuilder.append(Constants.FIELD_END_OF_FILE).append(Constants.SEMICOLON);

        return msgBuilder.toString();
    }
}
