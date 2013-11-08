package com.builtbroken.common.science;

public enum ChemicalCompound
{
    /** http://encyclopedia.airliquide.com/encyclopedia.asp?GasID=8#GeneralData */
    BUTANE("butane", "C4H10", MatterPhase.gas, 58.12f, 2.48f, new HeatingData(133f, 274f, 1379.23f, 6634.23f, 88f));

    /** Formula */
    public final String formula;
    /** IUPAC ID */
    public final String compoundName;
    /** g/mol */
    public final float molarMass;
    /** g/cm³ */
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
