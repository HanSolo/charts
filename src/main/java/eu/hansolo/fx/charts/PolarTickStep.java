/*
 * Copyright (c) 2018 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.charts;

public enum PolarTickStep {
    FIVE(5, 72),
    TEN(10, 36),
    FIFTEEN(15, 24),
    TWENTY(20 ,18),
    THIRTY(30, 12),
    FOURTY_FIVE(45, 8),
    SIXTY(60, 6),
    SEVENTY_TWO(72, 5),
    NINETY(90, 4),
    HUNDRED_TWENTY(120, 3);

    private final double value;
    private final double noOfSectors;
    private final double angleStep;

    PolarTickStep(final double value, final double noOfSectors) {
        this.value       = value;
        this.noOfSectors = noOfSectors;
        this.angleStep   = 360.0 / noOfSectors;
    }

    public double get() { return value; }

    public double getNoOfSectors() { return noOfSectors; }

    public double getAngleStep() { return angleStep; }
}
