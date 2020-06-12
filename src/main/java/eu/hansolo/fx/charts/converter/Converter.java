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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Converter {
    public enum Category {
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
    public enum UnitDefinition {
        // Length
        KILOMETER(new Unit(Category.LENGTH, "km", "Kilometer", new BigDecimal("1000.0"))),
        HECTOMETER(new Unit(Category.LENGTH, "hm", "Hectometer", new BigDecimal("100"))),
        METER(new Unit(Category.LENGTH, "m", "Meter", new BigDecimal("1.0"))),
        DECIMETER(new Unit(Category.LENGTH, "dm", "Decimeter", new BigDecimal("0.1"))),
        CENTIMETER(new Unit(Category.LENGTH, "cm", "Centimeter", new BigDecimal("0.01"))),
        MILLIMETER(new Unit(Category.LENGTH, "mm", "Millimeter", new BigDecimal("0.0010"))),
        MICROMETER(new Unit(Category.LENGTH, "\u00b5m", "Micrometer", new BigDecimal("1.0E-6"))),
        NANOMETER(new Unit(Category.LENGTH, "nm", "Nanometer", new BigDecimal("1.0E-9"))),
        ANGSTROM(new Unit(Category.LENGTH, "\u00c5", "Angstrom", new BigDecimal("1.0E-10"))),
        PICOMETER(new Unit(Category.LENGTH, "pm", "Picometer", new BigDecimal("1.0E-12"))),
        FEMTOMETER(new Unit(Category.LENGTH, "fm", "Femtometer", new BigDecimal("1.0E-15"))),
        INCHES(new Unit(Category.LENGTH, "in", "Inches", new BigDecimal("0.0254"))),
        MILES(new Unit(Category.LENGTH, "mi", "Miles", new BigDecimal("1609.344"))),
        NAUTICAL_MILES(new Unit(Category.LENGTH, "nmi", "Nautical Miles", new BigDecimal("1852.0"))),
        FEET(new Unit(Category.LENGTH, "ft", "Feet", new BigDecimal("0.3048"))),
        YARD(new Unit(Category.LENGTH, "yd", "Yard", new BigDecimal("0.9144"))),
        LIGHT_YEAR(new Unit(Category.LENGTH, "l.y.", "Light-Year", new BigDecimal("9.46073E15"))),
        PARSEC(new Unit(Category.LENGTH, "pc", "Parsec", new BigDecimal("3.085678E16"))),
        PIXEL(new Unit(Category.LENGTH, "px", "Pixel", new BigDecimal("0.000264583"))),
        POINT(new Unit(Category.LENGTH, "pt", "Point", new BigDecimal("0.0003527778"))),
        PICA(new Unit(Category.LENGTH, "p", "Pica", new BigDecimal("0.0042333333"))),
        EM(new Unit(Category.LENGTH, "em", "Quad", new BigDecimal("0.0042175176"))),

        // Mass
        TON(new Unit(Category.MASS, "t", "Ton", new BigDecimal("1.0E3"))),
        KILOGRAM(new Unit(Category.MASS, "kg", "Kilogram", new BigDecimal("1.0"))),
        GRAM(new Unit(Category.MASS, "g", "Gram", new BigDecimal("1.0E-3"))),
        MILLIGRAM(new Unit(Category.MASS, "mg", "Milligram", new BigDecimal("1.0E-6"))),
        MICROGRAM(new Unit(Category.MASS, "\u03bcg", "Mikrogram", new BigDecimal("1.0E-6"))),
        NANOGRAM(new Unit(Category.MASS, "ng", "Nanogram", new BigDecimal("1.0E-9"))),
        PICOGRAM(new Unit(Category.MASS, "pg", "Picogram", new BigDecimal("1.0E-12"))),
        FEMTOGRAM(new Unit(Category.MASS, "fg", "Femtogram", new BigDecimal("1.0E-15"))),
        OUNCE(new Unit(Category.MASS, "oz", "Ounce (US)", new BigDecimal("0.028"))),
        POUND(new Unit(Category.MASS, "lb", "Pounds (US)", new BigDecimal("0.45359237"))),

        // Time
        WEEK(new Unit(Category.TIME, "wk", "Week", new BigDecimal("604800"))),
        DAY(new Unit(Category.TIME, "d", "Day", new BigDecimal("86400"))),
        HOUR(new Unit(Category.TIME, "h", "Hour", new BigDecimal("3600"))),
        MINUTE(new Unit(Category.TIME, "m", "Minute", new BigDecimal("60"))),
        SECOND(new Unit(Category.TIME, "s", "Second", new BigDecimal("1.0"))),
        MILLISECOND(new Unit(Category.TIME, "ms", "Millisecond", new BigDecimal("1E-3"))),
        MICROSECOND(new Unit(Category.TIME, "\u03bcs", "Microsecond", new BigDecimal("1E-6"))),
        NANOSECOND(new Unit(Category.TIME, "ns", "Nanosecond", new BigDecimal("1E-9"))),
        PICOSECOND(new Unit(Category.TIME, "ps", "Picosecond", new BigDecimal("1E-12"))),
        FEMTOSECOND(new Unit(Category.TIME, "fs", "Femtosecond", new BigDecimal("1E-15"))),

        // Area
        SQUARE_KILOMETER(new Unit(Category.AREA, "km\u00B2", "Square Kilometer", new BigDecimal("1.0E6"))),
        SQUARE_METER(new Unit(Category.AREA, "m\u00B2", "Meter", new BigDecimal("1.0"))),
        SQUARE_CENTIMETER(new Unit(Category.AREA, "cm\u00B2", "Square Centimeter", new BigDecimal("1.0E-4"))),
        SQUARE_MILLIMETER(new Unit(Category.AREA, "mm\u00B2", "Square Millimeter", new BigDecimal("1.0E-6"))),
        SQUARE_MICROMETER(new Unit(Category.AREA, "\u03bcm\u00B2", "Square Mikrometer", new BigDecimal("1.0E-12"))),
        SQUARE_NANOMETER(new Unit(Category.AREA, "nm\u00B2", "Square Nanometer", new BigDecimal("1.0E-18"))),
        SQUARE_ANGSTROM(new Unit(Category.AREA, "\u00C5\u00B2", "Square \u00C5ngstrom", new BigDecimal("1.0E-20"))),
        SQUARE_PICOMETER(new Unit(Category.AREA, "pm\u00B2", "Square Picometer", new BigDecimal("1.0E-24"))),
        SQUARE_FEMTOMETER(new Unit(Category.AREA, "fm\u00B2", "Square Femtometer", new BigDecimal("1.0E-30"))),
        HECTARE(new Unit(Category.AREA, "ha", "Hectare", new BigDecimal("1.0E5"))),
        ACRE(new Unit(Category.AREA, "ac", "Acre", new BigDecimal("4046.8564224"))),
        ARES(new Unit(Category.AREA, "a", "Ares", new BigDecimal("100"))),
        SQUARE_INCH(new Unit(Category.AREA, "in\u00B2", "Square Inch", new BigDecimal("0.00064516"))),
        SQUARE_FOOT(new Unit(Category.AREA, "ft\u00B2", "Square Foot", new BigDecimal("0.09290304"))),

        // Temperature
        KELVIN(new Unit(Category.TEMPERATURE, "K", "Kelvin", new BigDecimal("1.0"))),
        CELSIUS(new Unit(Category.TEMPERATURE, "\u00B0C", "Celsius", new BigDecimal("1.0"), new BigDecimal("273.15"))),
        FAHRENHEIT(new Unit(Category.TEMPERATURE, "\u00B0F", "Fahrenheit", new BigDecimal("0.555555555555555"), new BigDecimal("459.67"))),

        // Angle
        DEGREE(new Unit(Category.ANGLE, "deg", "Degree", (Math.PI / 180.0))),
        RADIAN(new Unit(Category.ANGLE, "rad", "Radian", new BigDecimal("1.0"))),
        GRAD(new Unit(Category.ANGLE, "grad", "Gradian", new BigDecimal("0.9"))),

        // Volume
        CUBIC_MILLIMETER(new Unit(Category.VOLUME, "mm\u00B3", "Cubic Millimeter", new BigDecimal("1.0E-9"))),
        MILLILITER(new Unit(Category.VOLUME, "ml", "Milliliter", new BigDecimal("1.0E-6"))),
        LITER(new Unit(Category.VOLUME, "l", "Liter", new BigDecimal("1.0E-3"))),
        CUBIC_METER(new Unit(Category.VOLUME, "m\u00B3", "Cubic Meter", new BigDecimal("1.0E0"))),
        GALLON(new Unit(Category.VOLUME, "gal", "US Gallon", new BigDecimal("0.0037854118"))),
        CUBIC_FEET(new Unit(Category.VOLUME, "ft\u00B3", "Cubic Foot", new BigDecimal("0.0283168466"))),
        CUBIC_INCH(new Unit(Category.VOLUME, "in\u00B3", "Cubic Inch", new BigDecimal("0.0000163871"))),

        // Voltage
        MILLIVOLT(new Unit(Category.VOLTAGE, "mV", "Millivolt", new BigDecimal("1.0E-3"))),
        VOLT(new Unit(Category.VOLTAGE, "V", "Volt", new BigDecimal("1.0E0"))),
        KILOVOLT(new Unit(Category.VOLTAGE, "kV", "Kilovolt", new BigDecimal("1.0E3"))),
        MEGAVOLT(new Unit(Category.VOLTAGE, "MV", "Megavolt", new BigDecimal("1.0E6"))),

        // Current
        PICOAMPERE(new Unit(Category.CURRENT, "pA", "Picoampere", new BigDecimal("1.0E-12"))),
        NANOAMPERE(new Unit(Category.CURRENT, "nA", "Nanoampere", new BigDecimal("1.0E-9"))),
        MICROAMPERE(new Unit(Category.CURRENT, "\u03bcA", "Microampere", new BigDecimal("1.0E-6"))),
        MILLIAMPERE(new Unit(Category.CURRENT, "mA", "Milliampere", new BigDecimal("1.0E-3"))),
        AMPERE(new Unit(Category.CURRENT, "A", "Ampere", new BigDecimal("1.0"))),
        KILOAMPERE(new Unit(Category.CURRENT, "kA", "Kiloampere", new BigDecimal("1.0E3"))),

        // Speed
        MILLIMETER_PER_SECOND(new Unit(Category.SPEED, "mm/s", "Millimeter per second", new BigDecimal("1.0E-3"))),
        METER_PER_SECOND(new Unit(Category.SPEED, "m/s", "Meter per second", new BigDecimal("1.0E0"))),
        KILOMETER_PER_HOUR(new Unit(Category.SPEED, "km/h", "Kilometer per hour", new BigDecimal("0.2777777778"))),
        MILES_PER_HOUR(new Unit(Category.SPEED, "mph", "Miles per hour", new BigDecimal("0.4472271914"))),
        KNOT(new Unit(Category.SPEED, "kt", "Knot", new BigDecimal("0.51444444444444"))),
        MACH(new Unit(Category.SPEED, "M", "Mach", new BigDecimal("0.00293866995797"))),


        // TemperatureGradient
        KELVIN_PER_SECOND(new Unit(Category.TEMPERATURE_GRADIENT, "K/s", "Kelvin per second", new BigDecimal("1.0"))),
        KELVIN_PER_MINUTE(new Unit(Category.TEMPERATURE_GRADIENT, "K/min", "Kelvin per minute", new BigDecimal("0.0166666667"))),
        KEVLIN_PER_HOUR(new Unit(Category.TEMPERATURE_GRADIENT, "K/h", "Kelvin per hour", new BigDecimal("0.0002777778"))),

        // ElectricCharge
        ELEMENTARY_CHARGE(new Unit(Category.ELECTRIC_CHARGE, "e-", "Elementary charge", new BigDecimal("1.6022E-19"))),
        PICOCOULOMB(new Unit(Category.ELECTRIC_CHARGE, "pC", "Picocoulomb", new BigDecimal("1.0E-12"))),
        NANOCOULOMB(new Unit(Category.ELECTRIC_CHARGE, "nC", "Nanocoulomb", new BigDecimal("1.0E-9"))),
        MICROCOULOMB(new Unit(Category.ELECTRIC_CHARGE, "\u03bcC", "Microcoulomb", new BigDecimal("1.0E-6"))),
        MILLICOULOMB(new Unit(Category.ELECTRIC_CHARGE, "mC", "Millicoulomb", new BigDecimal("1.0E-3"))),
        COULOMB(new Unit(Category.ELECTRIC_CHARGE, "C", "Coulomb", new BigDecimal("1.0E0"))),

        // Energy
        MILLIJOULE(new Unit(Category.ENERGY, "mJ", "Millijoule", new BigDecimal("1.0E-3"))),
        JOULE(new Unit(Category.ENERGY, "J", "Joule", new BigDecimal("1.0E0"))),
        KILOJOULE(new Unit(Category.ENERGY, "kJ", "Kilojoule", new BigDecimal("1.0E3"))),
        MEGAJOULE(new Unit(Category.ENERGY, "MJ", "Megajoule", new BigDecimal("1.0E6"))),
        CALORY(new Unit(Category.ENERGY, "cal", "Calory", new BigDecimal("4.1868"))),
        KILOCALORY(new Unit(Category.ENERGY, "kcal", "Kilocalory", new BigDecimal("4186.8"))),
        WATT_SECOND(new Unit(Category.ENERGY, "W*s", "Watt second", new BigDecimal("1.0E0"))),
        WATT_HOUR(new Unit(Category.ENERGY, "W*h", "Watt hour", new BigDecimal("3600"))),
        KILOWATT_SECOND(new Unit(Category.ENERGY, "kW*s", "Kilowatt second", new BigDecimal("1000"))),
        KILOWATT_HOUR(new Unit(Category.ENERGY, "kW*h", "Kilowatt hour", new BigDecimal("3600000"))),

        // Force
        NEWTON(new Unit(Category.FORCE, "N", "Newton", new BigDecimal("1.0"))),
        KILOGRAM_FORCE(new Unit(Category.FORCE, "kp", "Kilogram-Force", new BigDecimal("9.80665"))),
        POUND_FORCE(new Unit(Category.FORCE, "lbf", "Pound-Force", new BigDecimal("4.4482216153"))),

        // Humidity
        PERCENTAGE(new Unit(Category.HUMIDITY, "%", "Percentage", new BigDecimal(1.0))),

        // Acceleration
        METER_PER_SQUARE_SECOND(new Unit(Category.ACCELERATION, "m/s\u00B2", "Meter per squaresecond", new BigDecimal("1.0E0"))),
        INCH_PER_SQUARE_SECOND(new Unit(Category.ACCELERATION, "in/s\u00B2", "Inch per squaresecond", new BigDecimal("0.0254"))),
        GRAVITY(new Unit(Category.ACCELERATION, "g", "Gravity", new BigDecimal("9.80665"))),

        // Pressure
        MILLIPASCAL(new Unit(Category.PRESSURE, "mPa", "Millipascal", new BigDecimal("1.0E-3"))),
        PASCAL(new Unit(Category.PRESSURE, "Pa", "Pascal", new BigDecimal("1.0E0"))),
        HECTOPASCAL(new Unit(Category.PRESSURE, "hPa", "Hectopascal", new BigDecimal("1.0E2"))),
        KILOPASCAL(new Unit(Category.PRESSURE, "kPa", "Kilopascal", new BigDecimal("1.0E3"))),
        BAR(new Unit(Category.PRESSURE, "bar", "Bar", new BigDecimal("1.0E5"))),
        MILLIBAR(new Unit(Category.PRESSURE, "mbar", "Millibar", new BigDecimal("1.0E2"))),
        TORR(new Unit(Category.PRESSURE, "Torr", "Torr", new BigDecimal("133.322368421"))),
        PSI(new Unit(Category.PRESSURE, "psi", "Pound per Square Inch", new BigDecimal("6894.757293178"))),
        PSF(new Unit(Category.PRESSURE, "psf", "Pound per Square Foot", new BigDecimal("47.88026"))),
        ATMOSPHERE(new Unit(Category.PRESSURE, "atm", "Atmosphere", new BigDecimal("101325.0"))),

        // Torque
        NEWTON_METER(new Unit(Category.TORQUE, "Nm", "Newton Meter", new BigDecimal("1.0"))),
        METER_KILOGRAM(new Unit(Category.TORQUE, "m kg", "Meter Kilogram", new BigDecimal("0.101971621"))),
        FOOT_POUND_FORCE(new Unit(Category.TORQUE, "ft lbf", "Foot-Pound Force", new BigDecimal("1.3558179483"))),
        INCH_POUND_FORCE(new Unit(Category.TORQUE, "in lbf", "Inch-Pound Force", new BigDecimal("0.112984829"))),

        // Data
        BIT(new Unit(Category.DATA, "b", "Bit", new BigDecimal("1.0"))),
        KILOBIT(new Unit(Category.DATA, "Kb", "KiloBit", new BigDecimal("1024"))),
        MEGABIT(new Unit(Category.DATA, "Mb", "Megabit", new BigDecimal("1048576"))),
        GIGABIT(new Unit(Category.DATA, "Gb", "Gigabit", new BigDecimal("1073741824"))),
        BYTE(new Unit(Category.DATA, "B", "Byte", new BigDecimal("8"))),
        KILOBYTE(new Unit(Category.DATA, "KB", "Kilobyte", new BigDecimal("8192"))),
        MEGABYTE(new Unit(Category.DATA, "MB", "Megabyte", new BigDecimal("8388608"))),
        GIGABYTE(new Unit(Category.DATA, "GB", "Gigabyte", new BigDecimal("8.589934592E9"))),
        TERABYTE(new Unit(Category.DATA, "TB", "Terabyte", new BigDecimal("8.796093E12"))),

        // Luminance
        CANDELA_SQUARE_METER(new Unit(Category.LUMINANCE, "cd/m\u00B2", "Candela per Square Meter", new BigDecimal("1.0"))),
        CANDELA_SQUARE_CENTIMETER(new Unit(Category.LUMINANCE, "cd/cm\u00B2", "Candela per Square CentiMeter", new BigDecimal("10000.0"))),
        CANDELA_SQUARE_INCH(new Unit(Category.LUMINANCE, "cd/in\u00B2", "Candela per Square Inch", new BigDecimal("1550.0031"))),
        CANDELA_SQAURE_FOOT(new Unit(Category.LUMINANCE, "cd/ft\u00B2", "Candela per Square Foot", new BigDecimal("10.7639104167"))),
        LAMBERT(new Unit(Category.LUMINANCE, "L", "Lambert", new BigDecimal("3183.09886183"))),
        FOOT_LAMBERT(new Unit(Category.LUMINANCE, "fL", "Footlambert", new BigDecimal("3.4262590996"))),

        // Luminous flux
        LUX(new Unit(Category.LUMINOUS_FLUX, "lm/m\u00B2", "Lux", new BigDecimal("1.0"))),
        PHOT(new Unit(Category.LUMINOUS_FLUX, "lm/cm\u00B2", "Phot", new BigDecimal("10000.0"))),
        FOOT_CANDLE(new Unit(Category.LUMINOUS_FLUX, "lm/ft\u00B2", "Footcandle", new BigDecimal("10.7639104167"))),
        LUMEN_SQUARE_INCH(new Unit(Category.LUMINOUS_FLUX, "lm/in\u00B2", "Lumen per Square Inch", new BigDecimal("1550.0031"))),

        // Work
        MILLIWATT(new Unit(Category.WORK, "mW", "Milliwatt", new BigDecimal("1.0E-3"))),
        WATT(new Unit(Category.WORK, "W", "Watt", new BigDecimal("1.0E0"))),
        KILOWATT(new Unit(Category.WORK, "kW", "Kilowatt", new BigDecimal("1.0E3"))),
        MEGAWATT(new Unit(Category.WORK, "MW", "Megawatt", new BigDecimal("1.0E6"))),
        GIGAWATT(new Unit(Category.WORK, "GW", "Gigawatt", new BigDecimal("1.0E9"))),
        HORSEPOWER(new Unit(Category.WORK, "hp", "Horsepower", new BigDecimal("735.49875"))),
        JOULE_PER_SECOND(new Unit(Category.WORK, "J/s", "Joule per second", new BigDecimal("1.0E0")));

        public final Unit UNIT;

        UnitDefinition(final Unit UNIT) {
            this.UNIT = UNIT;
        }
    }

    public  static final int                               MAX_NO_OF_DECIMALS = 12;
    private static final EnumMap<Category, UnitDefinition> BASE_UNITS         = new EnumMap<Category, UnitDefinition>(Category.class) {
        {
            put(Category.ACCELERATION, UnitDefinition.METER_PER_SQUARE_SECOND);
            put(Category.ANGLE, UnitDefinition.RADIAN);
            put(Category.AREA, UnitDefinition.SQUARE_METER);
            put(Category.CURRENT, UnitDefinition.AMPERE);
            put(Category.DATA, UnitDefinition.BIT);
            put(Category.ELECTRIC_CHARGE, UnitDefinition.ELEMENTARY_CHARGE);
            put(Category.ENERGY, UnitDefinition.JOULE);
            put(Category.FORCE, UnitDefinition.NEWTON);
            put(Category.HUMIDITY, UnitDefinition.PERCENTAGE);
            put(Category.LENGTH, UnitDefinition.METER);
            put(Category.LUMINANCE, UnitDefinition.CANDELA_SQUARE_METER);
            put(Category.LUMINOUS_FLUX, UnitDefinition.LUX);
            put(Category.MASS, UnitDefinition.KILOGRAM);
            put(Category.PRESSURE, UnitDefinition.PASCAL);
            put(Category.SPEED, UnitDefinition.METER_PER_SECOND);
            put(Category.TEMPERATURE, UnitDefinition.KELVIN);
            put(Category.TEMPERATURE_GRADIENT, UnitDefinition.KELVIN_PER_SECOND);
            put(Category.TIME, UnitDefinition.SECOND);
            put(Category.TORQUE, UnitDefinition.NEWTON_METER);
            put(Category.VOLUME, UnitDefinition.CUBIC_MILLIMETER);
            put(Category.VOLTAGE, UnitDefinition.VOLT);
            put(Category.WORK, UnitDefinition.WATT);
        }
    };

    private UnitDefinition                 baseUnitDefinition;
    private Unit                           bean;
    private Locale                         locale;
    private int                            decimals;
    private String                         formatString;


    // ******************** Constructors **************************************
    public Converter(final Category UNIT_TYPE) {
        this(UNIT_TYPE, BASE_UNITS.get(UNIT_TYPE));
    }
    public Converter(final Category UNIT_TYPE, final UnitDefinition BASE_UNIT_DEFINITION) {
        baseUnitDefinition = BASE_UNIT_DEFINITION;
        bean               = BASE_UNITS.get(UNIT_TYPE).UNIT;
        locale             = Locale.US;
        decimals           = 2;
        formatString       = "%.2f";
    }


    // ******************** Methods *******************************************
    public Category getUnitType() { return bean.getCategory(); }

    public UnitDefinition getBaseUnitDefinition() { return baseUnitDefinition; }
    public void setBaseUnitDefinition(final UnitDefinition BASE_UNIT_DEFINITION) {
        if (BASE_UNIT_DEFINITION.UNIT.getCategory() == getUnitType()) { baseUnitDefinition = BASE_UNIT_DEFINITION; }
    }

    public BigDecimal getFactor() { return bean.getFactor(); }

    public BigDecimal getOffset() { return bean.getOffset(); }

    public String getUnitName() { return bean.getUnitName(); }

    public String getUnitShort() { return bean.getUnitShort(); }

    public Locale getLocale() { return locale; }
    public void setLocale(final Locale LOCALE) { locale = LOCALE; }

    public int getDecimals() { return decimals; }
    public void setDecimals(final int DECIMALS) {
        if (DECIMALS < 0 ) {
            decimals = 0;
        } else if (DECIMALS > MAX_NO_OF_DECIMALS) {
            decimals = MAX_NO_OF_DECIMALS;
        } else {
            decimals = DECIMALS;
        }
        formatString = new StringBuilder("%.").append(decimals).append("f").toString();
    }

    public String getFormatString() { return formatString; }

    public boolean isActive() { return bean.isActive(); }
    public void setActive(final boolean ACTIVE) { bean.setActive(ACTIVE); }

    public double convert(final double VALUE, final UnitDefinition UNIT_DEFINITION) {
        if (UNIT_DEFINITION.UNIT.getCategory() != getUnitType()) { throw new IllegalArgumentException("units have to be of the same type"); }
        return ((((VALUE + baseUnitDefinition.UNIT.getOffset().doubleValue()) * baseUnitDefinition.UNIT.getFactor().doubleValue()) + bean.getOffset().doubleValue()) * bean.getFactor().doubleValue()) / UNIT_DEFINITION.UNIT
            .getFactor().doubleValue() - UNIT_DEFINITION.UNIT.getOffset().doubleValue();
    }

    public String convertToString(final double VALUE, final UnitDefinition UNIT_DEFINITION) {
        return String.join(" ", String.format(locale, formatString, convert(VALUE, UNIT_DEFINITION)), UNIT_DEFINITION.UNIT.getUnitShort());
    }

    public double convertToBaseUnit(final double VALUE, final UnitDefinition UNIT_DEFINITION) {
        return ((((VALUE + UNIT_DEFINITION.UNIT.getOffset().doubleValue()) * UNIT_DEFINITION.UNIT.getFactor().doubleValue()) + bean.getOffset().doubleValue()) * bean.getFactor().doubleValue()) / baseUnitDefinition.UNIT
            .getFactor().doubleValue() - baseUnitDefinition.UNIT.getOffset().doubleValue();
    }

    public Pattern getPattern() {
        final StringBuilder PATTERN_BUILDER = new StringBuilder();
        PATTERN_BUILDER.append("^([-+]?\\d*\\.?\\d*)\\s?(");

        for (UnitDefinition unitDefinition : UnitDefinition.values()) {
            PATTERN_BUILDER.append(unitDefinition.UNIT.getUnitShort().replace("*", "\\*")).append("|");
        }

        PATTERN_BUILDER.deleteCharAt(PATTERN_BUILDER.length() - 1);

        //PATTERN_BUILDER.append("){1}$");
        PATTERN_BUILDER.append(")?$");

        return Pattern.compile(PATTERN_BUILDER.toString());
    }

    public List<Unit> getAvailableUnits(final Category UNIT_DEFINITION) {
        return getAllUnitDefinitions().get(UNIT_DEFINITION).stream().map(unitDefinition -> unitDefinition.UNIT).collect(Collectors.toList());
    }

    public EnumMap<Category, ArrayList<UnitDefinition>> getAllUnitDefinitions() {
        final EnumMap<Category, ArrayList<UnitDefinition>> UNIT_TYPES    = new EnumMap<>(Category.class);
        final ArrayList<Category>                          CATEGORY_LIST = new ArrayList<>(Category.values().length);
        CATEGORY_LIST.addAll(Arrays.asList(Category.values()));
        CATEGORY_LIST.forEach(category -> UNIT_TYPES.put(category, new ArrayList<>()));
        for (UnitDefinition unitDefinition : UnitDefinition.values()) {
            UNIT_TYPES.get(unitDefinition.UNIT.getCategory()).add(unitDefinition);
        }
        return UNIT_TYPES;
    }

    public EnumMap<Category, ArrayList<UnitDefinition>> getAllActiveUnitDefinitions() {
        final EnumMap<Category, ArrayList<UnitDefinition>> UNIT_DEFINITIONS = new EnumMap<>(Category.class);
        final ArrayList<Category>                          CATEGORY_LIST    = new ArrayList<>(Category.values().length);
        CATEGORY_LIST.addAll(Arrays.asList(Category.values()));
        CATEGORY_LIST.forEach(category -> UNIT_DEFINITIONS.put(category, new ArrayList<>()));
        for (UnitDefinition unitDefinition : UnitDefinition.values()) {
            if (unitDefinition.UNIT.isActive()) { UNIT_DEFINITIONS.get(unitDefinition.UNIT.getCategory()).add(unitDefinition); }
        }
        return UNIT_DEFINITIONS;
    }

    @Override public String toString() { return getUnitType().toString(); }
}
