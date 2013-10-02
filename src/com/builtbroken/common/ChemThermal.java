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
    Hydrogen(),
    Helium(),
    Lithium(),
    Beryllium(),
    Boron(),
    Carbon(),
    Nitrogen(),
    Oxygen();

    public Phase phase = Phase.Solid;
    public float meltingPointKelvin;
    public float boilingPointKelvin;
    public float heatOfFusion;
    public float heatOfVaporization;
    public float specificHeatSolid;
    public float specificHeatLiquid;
    public float specificHeatGas;
    public float thermalConductivity;
    public float thermalExpansion;

    private ChemThermal()
    {

    }

    public static enum Phase
    {
        Solid(), Liquid(), Gas(), Plasma();
    }

}
