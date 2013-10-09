package com.builtbroken.common.science.units;

/** Enum that stores common units used to measure pressure.
 * 
 * @source http://en.wikipedia.org/wiki/Atmosphere_(unit)
 * @source http://en.wikipedia.org/wiki/Dyne
 * @Source http://en.wikipedia.org/wiki/Kilogram-force
 * @Source http://www.statman.info/conversions/pressure.html
 * @author Robert Seifer */
public enum PressureUnit
{
    Pa("Pascal", "Pa", "N/m^2", "Newtons over meter squared", 1f),
    Bar("Bar", "bar", "", 100000f),
    at("Technical atmoshphere", "at", "kp/cm^2", "kilopound over centimeter squared", 98066.5f),
    atm("Standard atmosphere", "atm", "p", 101325f),
    Torr("Torr", "Torr", "mmHg", "milimeters of mercury", 133.3224f),
    psi("Pounds per square inch", "psi", "Ibf/in^2", "poundforce per square inch", 6894.8f);

    String units;
    String unitsDetaled;
    String symbol;
    String name;
    float conversionToPa;

    private PressureUnit(String name, String symbol, String units, float conversionToPa)
    {
        this.name = name;
        this.symbol = symbol;
        this.units = units;
        this.conversionToPa = conversionToPa;
    }

    private PressureUnit(String name, String symbol, String units, String detailedUnits, float conversionToPa)
    {
        this(name, symbol, units, conversionToPa);
        this.unitsDetaled = detailedUnits;
    }

    public static float convert(PressureUnit a, PressureUnit b, float pressure)
    {
        pressure *= a.conversionToPa;
        pressure /= b.conversionToPa;
        return pressure;
    }

    public float convert(PressureUnit unit, float pressure)
    {
        return PressureUnit.convert(this, unit, pressure);
    }

    public String toString(float pressure, boolean shortName)
    {
        return MetricUnit.applyUnits(pressure, 2, 1) + " " + (shortName ? this.symbol : this.name);
    }

}
