package com.builtbroken.common.science.units;

/** Metric measurement system units
 * 
 * @author Robert Seifert */
public enum MetricUnit
{
    MICRO("Micro", "u", 0.000001f),
    MILLI("Milli", "m", 0.001f),
    BASE("", "", 1),
    KILO("Kilo", "k", 1000f),
    MEGA("Mega", "M", 1000000f),
    GIGA("Giga", "G", 1000000000f),
    TERA("Tera", "T", 1000000000000f),
    PETA("Peta", "P", 1000000000000000f),
    EXA("Exa", "E", 1000000000000000000f),
    ZETTA("Zetta", "Z", 1000000000000000000000f),
    YOTTA("Yotta", "Y", 1000000000000000000000000f);

    /** long name for the unit */
    public String name;
    /** short unit version of the unit */
    public String symbol;
    /** Point by which a number is consider to be of this unit */
    public float value;

    private MetricUnit(String name, String symbol, float value)
    {
        this.name = name;
        this.symbol = symbol;
        this.value = value;
    }

    public String getName(boolean getShort)
    {
        if (getShort)
        {
            return symbol;
        }
        else
        {
            return name;
        }
    }

    /** Divides the value by the unit value start */
    public double process(double value)
    {
        return value / this.value;
    }

    /** Checks if a value is above the unit value start */
    public boolean isAbove(float value)
    {
        return value > this.value;
    }

    /** Checks if a value is lower than the unit value start */
    public boolean isBellow(float value)
    {
        return value < this.value;
    }

    public static float convert(MetricUnit a, MetricUnit b, float value)
    {
        value *= a.value;
        value /= b.value;
        return value;
    }

    public float convert(MetricUnit unit, float value)
    {
        return MetricUnit.convert(this, unit, value);
    }

    public static String applyUnits(float value, int decimalPlaces, float multiplier)
    {
        String prefix = "";
        if (value < 0)
        {
            value = Math.abs(value);
            prefix = "-";
        }
        value *= multiplier;

        if (value == 0)
        {
            return value + " ";
        }
        else
        {
            for (int i = 0; i < MetricUnit.values().length; i++)
            {
                MetricUnit lowerMeasure = MetricUnit.values()[i];
                if (lowerMeasure.isBellow(value) && lowerMeasure.ordinal() == 0)
                {
                    return prefix + roundDecimals(lowerMeasure.process(value), decimalPlaces);
                }
                if (lowerMeasure.ordinal() + 1 >= MetricUnit.values().length)
                {
                    return prefix + roundDecimals(lowerMeasure.process(value), decimalPlaces);
                }
                MetricUnit upperMeasure = MetricUnit.values()[i + 1];
                if ((lowerMeasure.isAbove(value) && upperMeasure.isBellow(value)) || lowerMeasure.value == value)
                {
                    return prefix + roundDecimals(lowerMeasure.process(value), decimalPlaces);
                }
            }
        }

        return prefix + roundDecimals(value, decimalPlaces);
    }

    /** Rounds a number to a specific number place places
     * 
     * @param The number
     * @return The rounded number */
    public static double roundDecimals(double d, int decimalPlaces)
    {
        int j = (int) (d * Math.pow(10, decimalPlaces));
        return j / Math.pow(10, decimalPlaces);
    }
}
