package com.builtbroken.common.science.units;

/** Units of measure for work with electricity
 * 
 * @author Robert Seifert */
public enum ElectricUnit
{
    AMPERE("Amp", "I"),
    AMP_HOUR("Amp Hour", "Ah"),
    VOLTAGE("Volt", "V"),
    WATT("Watt", "W"),
    WATT_HOUR("Watt Hour", "Wh"),
    RESISTANCE("Ohm", "R"),
    CONDUCTANCE("Siemen", "S"),
    JOULES("Joule", "J");

    public String name;
    public String symbol;

    private ElectricUnit(String name, String symbol)
    {
        this.name = name;
        this.symbol = symbol;
    }

    public String getPlural()
    {
        return this.name + "s";
    }
}
