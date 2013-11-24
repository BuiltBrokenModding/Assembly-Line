package com.builtbroken.common.science;

public enum ChemicalCompound
{
    /** http://encyclopedia.airliquide.com/encyclopedia.asp?GasID=8#GeneralData */
    BUTANE("Butane", "C4H10", MatterPhase.gas, 58.12f, 2.48f, new HeatingData(133f, 274f, 1379.23f, 6634.23f, 88f)),
    METHANE("Methane", "CH4", MatterPhase.gas, 16.043f, 1.819f, new HeatingData(90.65f, 111.55f, 3656.67f, 31789.56f, 27f)),
    WATER("Water", "H20", MatterPhase.liquid, 18.01528f, 1000f, new HeatingData(274.15f, 373.13f, 18539.817f, 126004.1476f, 4.24f)),
    AIR("Air", "", MatterPhase.gas, 29f, .125f, null);

    /** Formula */
    public final String formula;
    /** IUPAC ID */
    public final String compoundName;
    /** g/mol */
    public final float molarMass;
    /** g/cm^3 */
    public final float density;

    public final MatterPhase defaultPhase;

    public final HeatingData heatingData;

    private ChemicalCompound(String name, String formula, MatterPhase phase, float molarMass, float density, HeatingData data)
    {
        this.compoundName = name;
        this.formula = formula;
        this.molarMass = molarMass;
        this.density = density;
        this.defaultPhase = phase;
        this.heatingData = data;
    }
}
