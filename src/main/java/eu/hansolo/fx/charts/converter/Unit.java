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

package eu.hansolo.fx.charts.converter;

import eu.hansolo.fx.charts.converter.Converter.Category;

import java.math.BigDecimal;


public class Unit {
    private          Category   category;
    private          String     unitShort;
    private          String     unitName;
    private volatile BigDecimal factor;
    private volatile BigDecimal offset;
    private volatile boolean    active;


    // ******************** Constructors **************************************
    public Unit(final Category CATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final double FACTOR) {
        this(CATEGORY, UNIT_SHORT, UNIT_NAME, FACTOR, 0.0);
    }
    public Unit(final Category CATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final double FACTOR, final boolean ACTIVE) {
        this(CATEGORY, UNIT_SHORT, UNIT_NAME, FACTOR, 0.0, ACTIVE);
    }
    public Unit(final Category CATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final BigDecimal FACTOR) {
        this(CATEGORY, UNIT_SHORT, UNIT_NAME, FACTOR, new BigDecimal("0.0"), true);
    }
    public Unit(final Category CATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final BigDecimal FACTOR, final boolean ACTIVE) {
        this(CATEGORY, UNIT_SHORT, UNIT_NAME, FACTOR, new BigDecimal("0.0"), ACTIVE);
    }
    public Unit(final Category CATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final double FACTOR, final double OFFSET) {
        this(CATEGORY, UNIT_SHORT, UNIT_NAME, new BigDecimal(Double.toString(FACTOR)), new BigDecimal(Double.toString(OFFSET)), true);
    }
    public Unit(final Category CATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final double FACTOR, final double OFFSET, final boolean ACTIVE) {
        this(CATEGORY, UNIT_SHORT, UNIT_NAME, new BigDecimal(Double.toString(FACTOR)), new BigDecimal(Double.toString(OFFSET)), ACTIVE);
    }
    public Unit(final Category CATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final BigDecimal FACTOR_BD, final BigDecimal OFFSET_BD) {
        this(CATEGORY, UNIT_SHORT, UNIT_NAME, FACTOR_BD, OFFSET_BD, true);
    }
    public Unit(final Category CATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final BigDecimal FACTOR_BD, final BigDecimal OFFSET_BD, final boolean ACTIVE) {
        category  = CATEGORY;
        unitShort = UNIT_SHORT;
        unitName  = UNIT_NAME;
        factor    = FACTOR_BD;
        offset    = OFFSET_BD;
        active    = ACTIVE;
    }


    // ******************** Methods *******************************************
    public final Category getCategory() { return category; }

    public final String getUnitShort() { return unitShort; }

    public final String getUnitName() { return unitName; }

    public final BigDecimal getFactor() { return factor; }
    public final void setFactor(final BigDecimal FACTOR) { factor = FACTOR; }
    public final void setFactor(final double FACTOR) { factor = new BigDecimal(Double.toString(FACTOR)); }

    public final BigDecimal getOffset() { return offset; }
    public final void setOffset(final BigDecimal OFFSET) { offset = OFFSET; }
    public final void setOffset(final double OFFSET) { offset =  new BigDecimal(Double.toString(OFFSET)); }

    public final boolean isActive() { return active; }
    public final void setActive(final boolean ACTIVE) { active = ACTIVE; }

    @Override public final String toString() {
        return new StringBuilder().append(category)
                                  .append(" ")
                                  .append(unitShort)
                                  .append(" (")
                                  .append(unitName)
                                  .append(") ")
                                  .append(factor)
                                  .append(", ")
                                  .append(offset).toString();
    }
}
