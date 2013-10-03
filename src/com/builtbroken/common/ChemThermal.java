package com.builtbroken.common;

/** List of elements and thier themal properties
 *
 * @Source http://www.periodictable.com/Properties/A/SpecificHeat.an.html
 * @source http://www.chemicalelements.com/
 * @source http://www.lenntech.com/periodic/periodic-chart.htm
 * @author Robert Seifert */
public enum ChemThermal
{
    /** Placeholder so that hydrogen starts as number one */
    ZERO(),
    Hydrogen(14.01f, 20.28f, 0.558f, 0.558f, 14300f),
    Helium(0, 4.22f, 0.02f, 0.083f, 5193.1f),
    Lithium(543.69f, 1615f, 3f, 147f, 3570f),
    Beryllium(),
    Boron(),
    Carbon(),
    Nitrogen(),
    Oxygen();

    public float meltingPointKelvin;
    public float boilingPointKelvin;
    /** kJ/mol */
    public float heatOfFusion;
    /** kJ/mol */
    public float heatOfVaporization;
    /** J/(kg K) */
    public float specificHeat;
    /** W/(m K) */
    public float thermalConductivity;
    public float thermalExpansion;

    private ChemThermal()
    {

    }

    private ChemThermal(float meltingPoint, float boilingPoint, float fisionHeat, float vaporHeat, float specificHeat)
    {
        this.meltingPointKelvin = meltingPoint;
        this.boilingPointKelvin = boilingPoint;
        this.heatOfFusion = fisionHeat;
        this.heatOfVaporization = vaporHeat;
        this.specificHeat = specificHeat;
    }

}
