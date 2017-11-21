/*
 * Copyright (c) 2017 by Gerrit Grunwald
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

package eu.hansolo.fx.charts.heatmap;

public enum OpacityDistribution {
    EXPONENTIAL(0.90, 0.37, 0.14, 0.05, 0.02, 0.006, 0.002, 0.001, 0.0003, 0.0001, 0.0),
    TAN_HYP(0.90, 0.53, 0.24, 0.10, 0.04, 0.01, 0.005, 0.002, 0.0007, 0.0002, 0.0),
    CUSTOM(0.90, 0.56, 0.40, 0.28, 0.20, 0.14, 0.10, 0.07, 0.05, 0.03, 0.0),
    LINEAR(0.90, 0.81, 0.72, 0.63, 0.54, 0.45, 0.36, 0.27, 0.18, 0.09, 0.0);

    private double[] distribution;


    // ******************** Constructors **************************************
    OpacityDistribution(final double... DISTRIBUTION) {
        distribution = DISTRIBUTION;
    }


    // ******************** Methods *******************************************
    public double[] getDistribution() { return distribution; }
}