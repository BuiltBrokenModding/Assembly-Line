package com.builtbroken.common.science;

/** Used to store values related to temperature and heat of a material. Temperatures are in kelvin,
 * and heat in joules
 *
 * @author DarkGuardsman */
public class HeatingData
{

    public float meltingPoint, boilingPoint, fisionHeat, vaporHeat, specificHeat, thermalExpasion, thermalConductivity;

    public HeatingData(float meltingPoint, float boilingPoint, float fisionHeat, float vaporHeat, float specificHeat)
    {
        this.meltingPoint = meltingPoint;
        this.boilingPoint = boilingPoint;
        this.fisionHeat = fisionHeat;
        this.vaporHeat = vaporHeat;
        this.specificHeat = specificHeat;
    }

    public HeatingData(float meltingPoint, float boilingPoint, float fisionHeat, float vaporHeat, float specificHeat, float thermalExpansion, float thermalConductivity)
    {
        this(meltingPoint, boilingPoint, fisionHeat, vaporHeat, specificHeat);
        this.thermalConductivity = thermalConductivity;
        this.thermalExpasion = thermalExpansion;
    }
}
