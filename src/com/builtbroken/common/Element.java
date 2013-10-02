package com.builtbroken.common;

/** List of element from the periodic table of elements for any kind of use
 *
 * @Source http://www.periodictable.com/Properties/A/SpecificHeat.an.html
 * @source http://www.chemicalelements.com/
 * @author Robert Seifert */
public enum Element
{
    HYDROGEN("Hydrogen", "H", 1.00794f, 14300f, 14.009985f, 14.009985f, 0.08988f, 1, 0);

    public float specificHeat, density, atomicMass;

    private Element()
    {

    }

    private Element(String name, String symbol, float atomicMass, float specificHeat, float meltingPointK, float frezingPointK, float density, int numberOfProtons, int numberOfNeturons)
    {

    }

}
