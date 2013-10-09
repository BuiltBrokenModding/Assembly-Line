package com.builtbroken.common.science.units;

/** @Source http://en.wikipedia.org/wiki/Imperial_units
 * @author Robert Seifert */
public enum ImperialUnits
{
    thou("thou", "th", (1 / 12000)),
    inch("inch", "in", (1 / 12)),
    foot("foot", "ft", 1),
    yard("yard", "yd", 3),
    chain("chain", "ch", 66),
    furlong("furlong", "fur", 660),
    mile("mile", "mi", 5280),

    /** Not official used anymore */
    league("league", "lea", 15840),

    /** Maritime units */
    fathom("fathom", "ftm", 6.08f),
    /** Maritime units */
    cable("cable", "", 608),
    /** Maritime units */
    nautical("nautical mile", "", 6080),

    /** Gunter's sruvey unit */
    link("link", "", (66 / 100)),
    /** Gunter's sruvey unit */
    rod("rod", "", (66 / 4));

    public String name, symbol;
    float toFeet;

    public static final ImperialUnits[] mainUnits = { inch, foot, yard, mile };

    private ImperialUnits(String name, String symbol, float toFeet)
    {
        this.name = name;
        this.symbol = symbol;
        this.toFeet = toFeet;
    }

    public static float convert(ImperialUnits a, ImperialUnits b, float value)
    {
        value *= a.toFeet;
        value /= b.toFeet;
        return value;
    }

    public float convert(ImperialUnits unit, float value)
    {
        return ImperialUnits.convert(this, unit, value);
    }

}
