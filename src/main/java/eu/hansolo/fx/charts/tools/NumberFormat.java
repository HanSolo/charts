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

package eu.hansolo.fx.charts.tools;

public enum NumberFormat {
    NUMBER("%.0f"),
    FLOAT_1_DECIMAL("%.1f"),
    FLOAT_2_DECIMALS("%.2f"),
    FLOAT("%.8f"),
    PERCENTAGE("%.0f%%"),
    PERCENTAGE_1_DECIMAL("%.1f%%");

    private final String FORMAT_STRING;

    NumberFormat(final String FORMAT_STRING) {
        this.FORMAT_STRING = FORMAT_STRING;
    }


    public String formatString() { return FORMAT_STRING; }
}
