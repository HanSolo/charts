package eu.hansolo.fx.charts.unit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Unit {
    public enum Type {
        ACCELERATION,
        ANGLE,
        AREA,
        DATA,
        CURRENT,
        ELECTRIC_CHARGE,
        ENERGY,
        FORCE,
        HUMIDITY,
        LENGTH,
        LUMINANCE,
        LUMINOUS_FLUX,
        MASS,
        PRESSURE,
        SPEED,
        TEMPERATURE,
        TEMPERATURE_GRADIENT,
        TIME,
        TORQUE,
        VOLUME,
        VOLTAGE,
        WORK
    }
    public enum Definition {
        // Length
        KILOMETER(new UnitBean(Type.LENGTH, "km", "Kilometer", new BigDecimal("1000.0"))),
        HECTOMETER(new UnitBean(Type.LENGTH, "hm", "Hectometer", new BigDecimal("100"))),
        METER(new UnitBean(Type.LENGTH, "m", "Meter", new BigDecimal("1.0"))),
        DECIMETER(new UnitBean(Type.LENGTH, "dm", "Decimeter", new BigDecimal("0.1"))),
        CENTIMETER(new UnitBean(Type.LENGTH, "cm", "Centimeter", new BigDecimal("0.01"))),
        MILLIMETER(new UnitBean(Type.LENGTH, "mm", "Millimeter", new BigDecimal("0.0010"))),
        MICROMETER(new UnitBean(Type.LENGTH, "\u00b5m", "Micrometer", new BigDecimal("1.0E-6"))),
        NANOMETER(new UnitBean(Type.LENGTH, "nm", "Nanometer", new BigDecimal("1.0E-9"))),
        ANGSTROM(new UnitBean(Type.LENGTH, "\u00c5", "Angstrom", new BigDecimal("1.0E-10"))),
        PICOMETER(new UnitBean(Type.LENGTH, "pm", "Picometer", new BigDecimal("1.0E-12"))),
        FEMTOMETER(new UnitBean(Type.LENGTH, "fm", "Femtometer", new BigDecimal("1.0E-15"))),
        INCHES(new UnitBean(Type.LENGTH, "in", "Inches", new BigDecimal("0.0254"))),
        MILES(new UnitBean(Type.LENGTH, "mi", "Miles", new BigDecimal("1609.344"))),
        NAUTICAL_MILES(new UnitBean(Type.LENGTH, "nmi", "Nautical Miles", new BigDecimal("1852.0"))),
        FEET(new UnitBean(Type.LENGTH, "ft", "Feet", new BigDecimal("0.3048"))),
        YARD(new UnitBean(Type.LENGTH, "yd", "Yard", new BigDecimal("0.9144"))),
        LIGHT_YEAR(new UnitBean(Type.LENGTH, "l.y.", "Light-Year", new BigDecimal("9.46073E15"))),
        PARSEC(new UnitBean(Type.LENGTH, "pc", "Parsec", new BigDecimal("3.085678E16"))),
        PIXEL(new UnitBean(Type.LENGTH, "px", "Pixel", new BigDecimal("0.000264583"))),
        POINT(new UnitBean(Type.LENGTH, "pt", "Point", new BigDecimal("0.0003527778"))),
        PICA(new UnitBean(Type.LENGTH, "p", "Pica", new BigDecimal("0.0042333333"))),
        EM(new UnitBean(Type.LENGTH, "em", "Quad", new BigDecimal("0.0042175176"))),
        // Mass
        TON(new UnitBean(Type.MASS, "t", "Ton", new BigDecimal("1.0E3"))),
        KILOGRAM(new UnitBean(Type.MASS, "kg", "Kilogram", new BigDecimal("1.0"))),
        GRAM(new UnitBean(Type.MASS, "g", "Gram", new BigDecimal("1.0E-3"))),
        MILLIGRAM(new UnitBean(Type.MASS, "mg", "Milligram", new BigDecimal("1.0E-6"))),
        MICROGRAM(new UnitBean(Type.MASS, "µg", "Mikrogram", new BigDecimal("1.0E-6"))),
        NANOGRAM(new UnitBean(Type.MASS, "ng", "Nanogram", new BigDecimal("1.0E-9"))),
        PICOGRAM(new UnitBean(Type.MASS, "pg", "Picogram", new BigDecimal("1.0E-12"))),
        FEMTOGRAM(new UnitBean(Type.MASS, "fg", "Femtogram", new BigDecimal("1.0E-15"))),
        OUNCE(new UnitBean(Type.MASS, "oz", "Ounce (US)", new BigDecimal("0.028"))),
        POUND(new UnitBean(Type.MASS, "lb", "Pounds (US)", new BigDecimal("0.45359237"))),
        // Time
        WEEK(new UnitBean(Type.TIME, "wk", "Week", new BigDecimal("604800"))),
        DAY(new UnitBean(Type.TIME, "d", "Day", new BigDecimal("86400"))),
        HOUR(new UnitBean(Type.TIME, "h", "Hour", new BigDecimal("3600"))),
        MINUTE(new UnitBean(Type.TIME, "m", "Minute", new BigDecimal("60"))),
        SECOND(new UnitBean(Type.TIME, "s", "Second", new BigDecimal("1.0"))),
        MILLISECOND(new UnitBean(Type.TIME, "ms", "Millisecond", new BigDecimal("1E-3"))),
        MICROSECOND(new UnitBean(Type.TIME, "µs", "Microsecond", new BigDecimal("1E-6"))),
        NANOSECOND(new UnitBean(Type.TIME, "ns", "Nanosecond", new BigDecimal("1E-9"))),
        PICOSECOND(new UnitBean(Type.TIME, "ps", "Picosecond", new BigDecimal("1E-12"))),
        FEMTOSECOND(new UnitBean(Type.TIME, "fs", "Femtosecond", new BigDecimal("1E-15"))),
        // Area
        SQUARE_KILOMETER(new UnitBean(Type.AREA, "km²", "Square Kilometer", new BigDecimal("1.0E6"))),
        SQUARE_METER(new UnitBean(Type.AREA, "m²", "Meter", new BigDecimal("1.0"))),
        SQUARE_CENTIMETER(new UnitBean(Type.AREA, "cm²", "Square Centimeter", new BigDecimal("1.0E-4"))),
        SQUARE_MILLIMETER(new UnitBean(Type.AREA, "mm²", "Square Millimeter", new BigDecimal("1.0E-6"))),
        SQUARE_MICROMETER(new UnitBean(Type.AREA, "µm²", "Square Mikrometer", new BigDecimal("1.0E-12"))),
        SQUARE_NANOMETER(new UnitBean(Type.AREA, "nm²", "Square Nanometer", new BigDecimal("1.0E-18"))),
        SQUARE_ANGSTROM(new UnitBean(Type.AREA, "Å²", "Square Ångstrom", new BigDecimal("1.0E-20"))),
        SQUARE_PICOMETER(new UnitBean(Type.AREA, "pm²", "Square Picometer", new BigDecimal("1.0E-24"))),
        SQUARE_FEMTOMETER(new UnitBean(Type.AREA, "fm²", "Square Femtometer", new BigDecimal("1.0E-30"))),
        HECTARE(new UnitBean(Type.AREA, "ha", "Hectare", new BigDecimal("1.0E5"))),
        ACRE(new UnitBean(Type.AREA, "ac", "Acre", new BigDecimal("4046.8564224"))),
        ARES(new UnitBean(Type.AREA, "a", "Ares", new BigDecimal("100"))),
        SQUARE_INCH(new UnitBean(Type.AREA, "in²", "Square Inch", new BigDecimal("0.00064516"))),
        SQUARE_FOOT(new UnitBean(Type.AREA, "ft²", "Square Foot", new BigDecimal("0.09290304"))),
        // Temperature
        KELVIN(new UnitBean(Type.TEMPERATURE, "K", "Kelvin", new BigDecimal("1.0"))),
        CELSIUS(new UnitBean(Type.TEMPERATURE, "°C", "Celsius", new BigDecimal("1.0"), new BigDecimal("273.15"))),
        FAHRENHEIT(new UnitBean(Type.TEMPERATURE, "°F", "Fahrenheit", new BigDecimal("0.555555555555555"), new BigDecimal("459.67"))),
        // Angle
        DEGREE(new UnitBean(Type.ANGLE, "deg", "Degree", (Math.PI / 180.0))),
        RADIAN(new UnitBean(Type.ANGLE, "rad", "Radian", new BigDecimal("1.0"))),
        GRAD(new UnitBean(Type.ANGLE, "grad", "Gradian", new BigDecimal("0.9"))),
        // Volume
        CUBIC_MILLIMETER(new UnitBean(Type.VOLUME, "mm³", "Cubic Millimeter", new BigDecimal("1.0E-9"))),
        MILLILITER(new UnitBean(Type.VOLUME, "ml", "Milliliter", new BigDecimal("1.0E-6"))),
        LITER(new UnitBean(Type.VOLUME, "l", "Liter", new BigDecimal("1.0E-3"))),
        CUBIC_METER(new UnitBean(Type.VOLUME, "m³", "Cubic Meter", new BigDecimal("1.0E0"))),
        GALLON(new UnitBean(Type.VOLUME, "gal", "US Gallon", new BigDecimal("0.0037854118"))),
        CUBIC_FEET(new UnitBean(Type.VOLUME, "ft³", "Cubic Foot", new BigDecimal("0.0283168466"))),
        CUBIC_INCH(new UnitBean(Type.VOLUME, "in³", "Cubic Inch", new BigDecimal("0.0000163871"))),
        // Voltage
        MILLIVOLT(new UnitBean(Type.VOLTAGE, "mV", "Millivolt", new BigDecimal("1.0E-3"))),
        VOLT(new UnitBean(Type.VOLTAGE, "V", "Volt", new BigDecimal("1.0E0"))),
        KILOVOLT(new UnitBean(Type.VOLTAGE, "kV", "Kilovolt", new BigDecimal("1.0E3"))),
        MEGAVOLT(new UnitBean(Type.VOLTAGE, "MV", "Megavolt", new BigDecimal("1.0E6"))),
        // Current
        PICOAMPERE(new UnitBean(Type.CURRENT, "pA", "Picoampere", new BigDecimal("1.0E-12"))),
        NANOAMPERE(new UnitBean(Type.CURRENT, "nA", "Nanoampere", new BigDecimal("1.0E-9"))),
        MICROAMPERE(new UnitBean(Type.CURRENT, "µA", "Microampere", new BigDecimal("1.0E-6"))),
        MILLIAMPERE(new UnitBean(Type.CURRENT, "mA", "Milliampere", new BigDecimal("1.0E-3"))),
        AMPERE(new UnitBean(Type.CURRENT, "A", "Ampere", new BigDecimal("1.0"))),
        KILOAMPERE(new UnitBean(Type.CURRENT, "kA", "Kiloampere", new BigDecimal("1.0E3"))),
        // Speed
        MILLIMETER_PER_SECOND(new UnitBean(Type.SPEED, "mm/s", "Millimeter per second", new BigDecimal("1.0E-3"))),
        METER_PER_SECOND(new UnitBean(Type.SPEED, "m/s", "Meter per second", new BigDecimal("1.0E0"))),
        KILOMETER_PER_HOUR(new UnitBean(Type.SPEED, "km/h", "Kilometer per hour", new BigDecimal("0.2777777778"))),
        MILES_PER_HOUR(new UnitBean(Type.SPEED, "mph", "Miles per hour", new BigDecimal("0.4472271914"))),
        KNOT(new UnitBean(Type.SPEED, "kt", "Knot", new BigDecimal("0.51444444444444"))),
        // TemperatureGradient
        KELVIN_PER_SECOND(new UnitBean(Type.TEMPERATURE_GRADIENT, "K/s", "Kelvin per second", new BigDecimal("1.0"))),
        KELVIN_PER_MINUTE(new UnitBean(Type.TEMPERATURE_GRADIENT, "K/min", "Kelvin per minute", new BigDecimal("0.0166666667"))),
        KEVLIN_PER_HOUR(new UnitBean(Type.TEMPERATURE_GRADIENT, "K/h", "Kelvin per hour", new BigDecimal("0.0002777778"))),
        // ElectricCharge
        ELEMENTARY_CHARGE(new UnitBean(Type.ELECTRIC_CHARGE, "e-", "Elementary charge", new BigDecimal("1.6022E-19"))),
        PICOCOULOMB(new UnitBean(Type.ELECTRIC_CHARGE, "pC", "Picocoulomb", new BigDecimal("1.0E-12"))),
        NANOCOULOMB(new UnitBean(Type.ELECTRIC_CHARGE, "nC", "Nanocoulomb", new BigDecimal("1.0E-9"))),
        MICROCOULOMB(new UnitBean(Type.ELECTRIC_CHARGE, "µC", "Microcoulomb", new BigDecimal("1.0E-6"))),
        MILLICOULOMB(new UnitBean(Type.ELECTRIC_CHARGE, "mC", "Millicoulomb", new BigDecimal("1.0E-3"))),
        COULOMB(new UnitBean(Type.ELECTRIC_CHARGE, "C", "Coulomb", new BigDecimal("1.0E0"))),
        // Energy
        MILLIJOULE(new UnitBean(Type.ENERGY, "mJ", "Millijoule", new BigDecimal("1.0E-3"))),
        JOULE(new UnitBean(Type.ENERGY, "J", "Joule", new BigDecimal("1.0E0"))),
        KILOJOULE(new UnitBean(Type.ENERGY, "kJ", "Kilojoule", new BigDecimal("1.0E3"))),
        MEGAJOULE(new UnitBean(Type.ENERGY, "MJ", "Megajoule", new BigDecimal("1.0E6"))),
        CALORY(new UnitBean(Type.ENERGY, "cal", "Calory", new BigDecimal("4.1868"))),
        KILOCALORY(new UnitBean(Type.ENERGY, "kcal", "Kilocalory", new BigDecimal("4186.8"))),
        WATT_SECOND(new UnitBean(Type.ENERGY, "W*s", "Watt second", new BigDecimal("1.0E0"))),
        WATT_HOUR(new UnitBean(Type.ENERGY, "W*h", "Watt hour",new BigDecimal("3600"))),
        KILOWATT_SECOND(new UnitBean(Type.ENERGY, "kW*s", "Kilowatt second", new BigDecimal("1000"))),
        KILOWATT_HOUR(new UnitBean(Type.ENERGY, "kW*h", "Kilowatt hour", new BigDecimal("3600000"))),
        // Force
        NEWTON(new UnitBean(Type.FORCE, "N", "Newton", new BigDecimal("1.0"))),
        KILOGRAM_FORCE(new UnitBean(Type.FORCE, "kp", "Kilogram-Force", new BigDecimal("9.80665"))),
        POUND_FORCE(new UnitBean(Type.FORCE, "lbf", "Pound-Force", new BigDecimal("4.4482216153"))),
        // Humidity
        PERCENTAGE(new UnitBean(Type.HUMIDITY, "%", "Percentage", new BigDecimal(1.0))),
        // Acceleration
        METER_PER_SQUARE_SECOND(new UnitBean(Type.ACCELERATION, "m/s²","Meter per squaresecond",new BigDecimal("1.0E0"))),
        INCH_PER_SQUARE_SECOND(new UnitBean(Type.ACCELERATION, "in/s²","Inch per squaresecond",new BigDecimal("0.0254"))),
        GRAVITY(new UnitBean(Type.ACCELERATION, "g","Gravity",new BigDecimal("9.80665"))),
        // Pressure
        MILLIPASCAL(new UnitBean(Type.PRESSURE, "mPa","Millipascal",new BigDecimal("1.0E-3"))),
        PASCAL(new UnitBean(Type.PRESSURE, "Pa","Pascal",new BigDecimal("1.0E0"))),
        HECTOPASCAL(new UnitBean(Type.PRESSURE, "hPa","Hectopascal",new BigDecimal("1.0E2"))),
        KILOPASCAL(new UnitBean(Type.PRESSURE, "kPa","Kilopascal",new BigDecimal("1.0E3"))),
        BAR(new UnitBean(Type.PRESSURE, "bar","Bar",new BigDecimal("1.0E5"))),
        MILLIBAR(new UnitBean(Type.PRESSURE, "mbar","Millibar",new BigDecimal("1.0E2"))),
        TORR(new UnitBean(Type.PRESSURE, "Torr","Torr",new BigDecimal("133.322368421"))),
        PSI(new UnitBean(Type.PRESSURE, "psi","Pound per Square Inch",new BigDecimal("6894.757293178"))),
        PSF(new UnitBean(Type.PRESSURE, "psf","Pound per Square Foot",new BigDecimal("47.88026"))),
        ATMOSPHERE(new UnitBean(Type.PRESSURE, "atm","Atmosphere",new BigDecimal("101325.0"))),
        // Torque
        NEWTON_METER(new UnitBean(Type.TORQUE, "Nm","Newton Meter",new BigDecimal("1.0"))),
        METER_KILOGRAM(new UnitBean(Type.TORQUE, "m kg","Meter Kilogram",new BigDecimal("0.101971621"))),
        FOOT_POUND_FORCE(new UnitBean(Type.TORQUE, "ft lbf","Foot-Pound Force",new BigDecimal("1.3558179483"))),
        INCH_POUND_FORCE(new UnitBean(Type.TORQUE, "in lbf","Inch-Pound Force",new BigDecimal("0.112984829"))),
        // Data
        BIT(new UnitBean(Type.DATA, "b","Bit",new BigDecimal("1.0"))),
        KILOBIT(new UnitBean(Type.DATA, "Kb","KiloBit",new BigDecimal("1024"))),
        MEGABIT(new UnitBean(Type.DATA, "Mb","Megabit",new BigDecimal("1048576"))),
        GIGABIT(new UnitBean(Type.DATA, "Gb","Gigabit",new BigDecimal("1073741824"))),
        BYTE(new UnitBean(Type.DATA, "B","Byte",new BigDecimal("8"))),
        KILOBYTE(new UnitBean(Type.DATA, "KB","Kilobyte",new BigDecimal("8192"))),
        MEGABYTE(new UnitBean(Type.DATA, "MB","Megabyte",new BigDecimal("8388608"))),
        GIGABYTE(new UnitBean(Type.DATA, "GB","Gigabyte",new BigDecimal("8.589934592E9"))),
        TERABYTE(new UnitBean(Type.DATA, "TB","Terabyte",new BigDecimal("8.796093E12"))),
        // Luminance
        CANDELA_SQUAREMETER(new UnitBean(Type.LUMINANCE, "cd/m²","Candela per Square Meter",new BigDecimal("1.0"))),
        CANDELA_SQUARECENTIMETER(new UnitBean(Type.LUMINANCE, "cd/cm²","Candela per Square CentiMeter",new BigDecimal("10000.0"))),
        CANDELA_SQUAREINCH(new UnitBean(Type.LUMINANCE, "cd/in²","Candela per Square Inch",new BigDecimal("1550.0031"))),
        CANDELA_SQAUREFOOT(new UnitBean(Type.LUMINANCE, "cd/ft²","Candela per Square Foot",new BigDecimal("10.7639104167"))),
        LAMBERT(new UnitBean(Type.LUMINANCE, "L","Lambert",new BigDecimal("3183.09886183"))),
        FOOTLAMBERT(new UnitBean(Type.LUMINANCE, "fL","Footlambert",new BigDecimal("3.4262590996"))),
        // Luminous flux
        LUX(new UnitBean(Type.LUMINOUS_FLUX, "lm/m²","Lux",new BigDecimal("1.0"))),
        PHOT(new UnitBean(Type.LUMINOUS_FLUX, "lm/cm²","Phot",new BigDecimal("10000.0"))),
        FOOT_CANDLE(new UnitBean(Type.LUMINOUS_FLUX, "lm/ft²","Footcandle",new BigDecimal("10.7639104167"))),
        LUMEN_SQUARE_INCH(new UnitBean(Type.LUMINOUS_FLUX, "lm/in²","Lumen per Square Inch",new BigDecimal("1550.0031"))),
        // Work
        MILLIWATT(new UnitBean(Type.WORK, "mW","Milliwatt",new BigDecimal("1.0E-3"))),
        WATT(new UnitBean(Type.WORK, "W","Watt",new BigDecimal("1.0E0"))),
        KILOWATT(new UnitBean(Type.WORK, "kW","Kilowatt",new BigDecimal("1.0E3"))),
        MEGAWATT(new UnitBean(Type.WORK, "MW","Megawatt",new BigDecimal("1.0E6"))),
        GIGAWATT(new UnitBean(Type.WORK, "GW","Gigawatt",new BigDecimal("1.0E9"))),
        HORSEPOWER(new UnitBean(Type.WORK, "hp","Horsepower",new BigDecimal("735.49875"))),
        JOULE_PER_SECOND(new UnitBean(Type.WORK, "J/s","Joule per second",new BigDecimal("1.0E0")));


        public final UnitBean BEAN;

        private Definition(final UnitBean BEAN) {
            this.BEAN = BEAN;
        }
    }

    private static final EnumMap<Type, Definition> BASE_UNITS = new EnumMap<Type, Definition>(Type.class) {
        {
            put(Type.ACCELERATION, Definition.METER_PER_SQUARE_SECOND);
            put(Type.ANGLE, Definition.RADIAN);
            put(Type.AREA, Definition.SQUARE_METER);
            put(Type.CURRENT, Definition.AMPERE);
            put(Type.DATA, Definition.BIT);
            put(Type.ELECTRIC_CHARGE, Definition.ELEMENTARY_CHARGE);
            put(Type.ENERGY, Definition.JOULE);
            put(Type.FORCE, Definition.NEWTON);
            put(Type.HUMIDITY, Definition.PERCENTAGE);
            put(Type.LENGTH, Definition.METER);
            put(Type.LUMINANCE, Definition.CANDELA_SQUAREMETER);
            put(Type.LUMINOUS_FLUX, Definition.LUX);
            put(Type.MASS, Definition.KILOGRAM);
            put(Type.PRESSURE, Definition.PASCAL);
            put(Type.SPEED, Definition.METER_PER_SECOND);
            put(Type.TEMPERATURE, Definition.KELVIN);
            put(Type.TEMPERATURE_GRADIENT, Definition.KELVIN_PER_SECOND);
            put(Type.TIME, Definition.SECOND);
            put(Type.TORQUE, Definition.NEWTON_METER);
            put(Type.VOLUME, Definition.CUBIC_MILLIMETER);
            put(Type.VOLTAGE, Definition.VOLT);
            put(Type.WORK, Definition.WATT);
        }
    };
    private Definition baseUnit;
    private UnitBean   bean;


    // ******************** Constructors **************************************
    public Unit(final Type UNIT_TYPE) {
        this(UNIT_TYPE, BASE_UNITS.get(UNIT_TYPE));
    }
    public Unit(final Type UNIT_TYPE, final Definition BASE_UNIT) {
        baseUnit = BASE_UNIT;
        bean     = BASE_UNITS.get(UNIT_TYPE).BEAN;
    }


    // ******************** Methods *******************************************
    public final Type getUnitType() { return bean.getUnitType(); }

    public final Definition getBaseUnit() { return baseUnit; }
    public final void setBaseUnit(final Definition BASE_UNIT) {
        if (BASE_UNIT.BEAN.getUnitType() == getUnitType()) { baseUnit = BASE_UNIT; }
    }

    public final BigDecimal getFactor() { return bean.getFactor(); }

    public final BigDecimal getOffset() { return bean.getOffset(); }

    public final String getUnitName() { return bean.getUnitName(); }

    public final String getUnitShort() { return bean.getUnitShort(); }

    public final boolean isActive() { return bean.isActive(); }
    public final void setActive(final boolean ACTIVE) { bean.setActive(ACTIVE); }

    public final double convert(final double VALUE, final Definition UNIT) {
        if (UNIT.BEAN.getUnitType() != getUnitType()) { throw new IllegalArgumentException("units have to be of the same type"); }
        return ((((VALUE + baseUnit.BEAN.getOffset().doubleValue()) * baseUnit.BEAN.getFactor().doubleValue()) + bean.getOffset().doubleValue()) * bean.getFactor().doubleValue()) / UNIT.BEAN.getFactor().doubleValue() - UNIT.BEAN.getOffset().doubleValue();
    }

    public final double convertToBaseUnit(final double VALUE, final Definition UNIT) {
        return ((((VALUE + UNIT.BEAN.getOffset().doubleValue()) * UNIT.BEAN.getFactor().doubleValue()) + bean.getOffset().doubleValue()) * bean.getFactor().doubleValue()) / baseUnit.BEAN.getFactor().doubleValue() - baseUnit.BEAN.getOffset().doubleValue();
    }

    public final Pattern getPattern() {
        final StringBuilder PATTERN_BUILDER = new StringBuilder();
        PATTERN_BUILDER.append("^([-+]?\\d*\\.?\\d*)\\s?(");

        for (Definition unit : Definition.values()) {
            PATTERN_BUILDER.append(unit.BEAN.getUnitShort().replace("*", "\\*")).append("|");
        }

        PATTERN_BUILDER.deleteCharAt(PATTERN_BUILDER.length() - 1);

        //PATTERN_BUILDER.append("){1}$");
        PATTERN_BUILDER.append(")?$");

        return Pattern.compile(PATTERN_BUILDER.toString());
    }

    public final List<UnitBean> getAvailableUnits(final Type UNIT_TYPE) {
        List<UnitBean> availableUnits = getAllUnitTypes().get(UNIT_TYPE).stream()
                                                         .map(unit -> unit.BEAN)
                                                         .collect(Collectors.toList());
        return availableUnits;
    }

    public final EnumMap<Type, ArrayList<Definition>> getAllUnitTypes() {
        final EnumMap<Type, ArrayList<Definition>> UNIT_TYPES = new EnumMap<>(Type.class);
        final ArrayList<Type> TYPE_LIST = new ArrayList<>(Type.values().length);
        TYPE_LIST.addAll(Arrays.asList(Type.values()));
        TYPE_LIST.forEach(type -> UNIT_TYPES.put(type, new ArrayList<>()));
        for (Definition unit : Definition.values()) {
            UNIT_TYPES.get(unit.BEAN.getUnitType()).add(unit);
        }
        return UNIT_TYPES;
    }

    public final EnumMap<Type, ArrayList<Definition>> getAllActiveUnitTypes() {
        final EnumMap<Type, ArrayList<Definition>> UNIT_TYPES = new EnumMap<>(Type.class);
        final ArrayList<Type> TYPE_LIST = new ArrayList<>(Type.values().length);
        TYPE_LIST.addAll(Arrays.asList(Type.values()));
        TYPE_LIST.forEach(type -> UNIT_TYPES.put(type, new ArrayList<>()));
        for (Definition unit : Definition.values()) {
            if (unit.BEAN.isActive()) { UNIT_TYPES.get(unit.BEAN.getUnitType()).add(unit); }
        }
        return UNIT_TYPES;
    }

    @Override public String toString() { return getUnitType().toString(); }
}
