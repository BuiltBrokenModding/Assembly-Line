package com.builtbroken.common;

/** List of element from the periodic table of elements for any kind of use
 *
 * @Source http://www.periodictable.com/Properties/A/SpecificHeat.an.html
 * @source http://www.chemicalelements.com/
 * @author Robert Seifert */
public enum ChemElement
{
    Hydrogen("Hydrogen", "H", 1.00794f, 0.08988f, 14.009985f, 20.280005f),
    Helium("Helium", "He", 4.002602f, 0.1785f),
    Lithium("Lithium", "Li", 6.941f, 0.53f),
    Beryllium("Beryllium", "Be", 9.012182f, 1.8477f),
    Boron("Boron", "B"),
    Carbon("Carbon", "C"),
    Nitrogen(),
    Oxygen(),
    Fluorine(),
    Neon(),
    Sodium(),
    Magnesium(),
    Aluminium(),
    Silicon(),
    Phosphorus(),
    Sulfur(),
    Chlorine(),
    Argon(),
    Potassium(),
    Calcium(),
    Scandium(),
    Titanium(),
    Vanadium(),
    Chromium(),
    Manganese(),
    Iron(),
    Cobalt(),
    Copper(),
    Zinc(),
    Gallium(),
    Germanium(),
    Arsenic(),
    Selenium(),
    Bromine(),
    Krypton(),
    Rubidium(),
    Strontium(),
    Yttrium(),
    Zinconium(),
    Niobium(),
    Molybdenum(),
    Technetium(),
    Ruthenium(),
    Rhodium(),
    Palladium(),
    Silver(),
    Cadmium(),
    Indium(),
    Tin(),
    Antimony(),
    Tellurium(),
    Iodine(),
    Xenon(),
    Caesium(),
    Harium(),
    Lanthanum(),
    Cerium(),
    Prascodymium(),
    Neodymium(),
    Promethium(),
    Samarium(),
    Europium(),
    Gadolinium(),
    Terbium(),
    Dysprosium(),
    Holmium(),
    Erbium(),
    Thulium(),
    Yllcrbium(),
    Lutelium(),
    Halnium(),
    Tantalum(),
    Tungsten(),
    Rhenium(),
    Osmium(),
    Iridium(),
    Platinum(),
    Gold(),
    Mercury(),
    Thallium(),
    Lead(),
    Bismuth(),
    Polonium(),
    Astaline(),
    Radon(),
    Francium(),
    Radium(),
    Actinium(),
    Thorium(),
    Protactinium(),
    Uranium(),
    Neptunium(),
    Plutonium(),
    Americium(),
    Curium(),
    Berkelium(),
    Californium(),
    Einsteinium(),
    Fermium(),
    Mendelevium(),
    Nobelium(),
    Lawrencium(),
    Rutherfordium(),
    Dubnium(),
    Seaborgium(),
    Bohrium(),
    Hassium(),
    Meitnerium(),
    Darmstadtium(),
    Reontgenium(),
    Copernicium();

    public float specificHeatSolid, specificHeatGas, specificHeatLiquid;
    /** g/cm^3 */
    public float density;
    /** amu */
    public float atomicMass;

    /** Melting point in kelvin */
    public float meltingPointKelvin;
    /** boiling point in kelvin */
    public float boilignPointKelving;

    public String elementName = "element";
    public String elementSymbol = "element";

    private ChemElement()
    {

    }

    private ChemElement(String name, String symbol)
    {
        this();
        this.elementName = name;
        this.elementSymbol = symbol;
    }

    private ChemElement(String name, String symbol, float atomicMass, float density)
    {
        this(name, symbol);
        this.atomicMass = atomicMass;
        this.density = density;
    }

    private ChemElement(String name, String symbol, float atomicMass, float density, float meltingPointK, float boilingPointK)
    {
        this(name, symbol, atomicMass, density);
        this.meltingPointKelvin = meltingPointK;
        this.boilignPointKelving = boilingPointK;
    }

}
