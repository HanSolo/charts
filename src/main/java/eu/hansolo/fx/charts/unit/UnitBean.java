package eu.hansolo.fx.charts.unit;

import java.math.BigDecimal;


public class UnitBean {
    private          Unit.Type  unitType;
    private          String     unitShort;
    private          String     unitName;
    private volatile BigDecimal factor;
    private volatile BigDecimal offset;
    private volatile boolean    active;


    // ******************** Constructors **************************************
    public UnitBean(final Unit.Type UNIT_TYPE, final String UNIT_SHORT, final String UNIT_NAME, final double FACTOR) {
        this(UNIT_TYPE, UNIT_SHORT, UNIT_NAME, FACTOR, 0.0);
    }
    public UnitBean(final Unit.Type UNIT_TYPE, final String UNIT_SHORT, final String UNIT_NAME, final double FACTOR, final boolean ACTIVE) {
        this(UNIT_TYPE, UNIT_SHORT, UNIT_NAME, FACTOR, 0.0, ACTIVE);
    }
    public UnitBean(final Unit.Type UNIT_TYPE, final String UNIT_SHORT, final String UNIT_NAME, final BigDecimal FACTOR) {
        this(UNIT_TYPE, UNIT_SHORT, UNIT_NAME, FACTOR, new BigDecimal("0.0"), true);
    }
    public UnitBean(final Unit.Type UNIT_TYPE, final String UNIT_SHORT, final String UNIT_NAME, final BigDecimal FACTOR, final boolean ACTIVE) {
        this(UNIT_TYPE, UNIT_SHORT, UNIT_NAME, FACTOR, new BigDecimal("0.0"), ACTIVE);
    }
    public UnitBean(final Unit.Type UNIT_TYPE, final String UNIT_SHORT, final String UNIT_NAME, final double FACTOR, final double OFFSET) {
        this(UNIT_TYPE, UNIT_SHORT, UNIT_NAME, new BigDecimal(Double.toString(FACTOR)), new BigDecimal(Double.toString(OFFSET)), true);
    }
    public UnitBean(final Unit.Type UNIT_TYPE, final String UNIT_SHORT, final String UNIT_NAME, final double FACTOR, final double OFFSET, final boolean ACTIVE) {
        this(UNIT_TYPE, UNIT_SHORT, UNIT_NAME, new BigDecimal(Double.toString(FACTOR)), new BigDecimal(Double.toString(OFFSET)), ACTIVE);
    }
    public UnitBean(final Unit.Type UNIT_TYPE, final String UNIT_SHORT, final String UNIT_NAME, final BigDecimal FACTOR_BD, final BigDecimal OFFSET_BD) {
        this(UNIT_TYPE, UNIT_SHORT, UNIT_NAME, FACTOR_BD, OFFSET_BD, true);
    }
    public UnitBean(final Unit.Type UNIT_TYPE, final String UNIT_SHORT, final String UNIT_NAME, final BigDecimal FACTOR_BD, final BigDecimal OFFSET_BD, final boolean ACTIVE) {
        unitType  = UNIT_TYPE;
        unitShort = UNIT_SHORT;
        unitName  = UNIT_NAME;
        factor    = FACTOR_BD;
        offset    = OFFSET_BD;
        active    = ACTIVE;
    }


    // ******************** Methods *******************************************
    public final Unit.Type getUnitType() { return unitType; }

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
        return new StringBuilder().append(unitType)
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
