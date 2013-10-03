package com.builtbroken.common;

/** List of element from the periodic table of elements for any kind of use. Is not complete for all
 * parts but each element should have a listed names, symbol, and atomic mass. Atomic number should
 * be the placement # in the list. Var ZERO should not be used as its designed to offset the
 * placement of all elements by one. As well is returned instead of null.
 *
 * @Source http://www.periodictable.com/Properties/A/SpecificHeat.an.html
 * @source http://www.chemicalelements.com/
 * @source http://www.lenntech.com/periodic/periodic-chart.htm
 * @author Robert Seifert */
public enum ChemElement
{
    /** Placeholder so that hydrogen starts as number one */
    ZERO("ZERO", "ZERO", 0, 0, null, null),
    Hydrogen("Hydrogen", "H", 1.00794f, 0.08988f, ElementClassifications.nonmetal, MatterPhase.gas, 14.01f, 20.28f, 0.558f, 0.558f, 14300f),
    Helium("Helium", "He", 4.002602f, 0.1785f, ElementClassifications.inertGas, MatterPhase.gas, 0, 4.22f, 0.02f, 0.083f, 5193.1f),
    Lithium("Lithium", "Li", 6.941f, 0.53f, ElementClassifications.inertGas, MatterPhase.gas, 543.69f, 1615f, 3f, 147f, 3570f),

    Beryllium("Beryllium", "Be", 9.012182f, 1.8477f, ElementClassifications.inertGas, MatterPhase.gas),
    Boron("Boron", "B", 10.811f, 2.46f, ElementClassifications.inertGas, MatterPhase.gas),
    Carbon("Carbon", "C", 12.0107f, 2.26f, ElementClassifications.inertGas, MatterPhase.gas),
    Nitrogen("Nitrogen", "N", 14.0067f, 1.251f, ElementClassifications.inertGas, MatterPhase.gas),
    Oxygen("Oxygen", "O", 15.9994f, 1.429f, ElementClassifications.inertGas, MatterPhase.gas),
    Fluorine("Fluorine", "F", 18.9994f, 1.696f, ElementClassifications.inertGas, MatterPhase.gas),
    Neon("Neon", "Ne", 20.1797f, 0.9f, ElementClassifications.inertGas, MatterPhase.gas),
    Sodium("Sodium", "Na", 22.98976928f, 0.968f, ElementClassifications.alkaliMetal, MatterPhase.solid),
    Magnesium("Magnesium", "Mg", 24.305f, 1.738f, ElementClassifications.alkalineEarthMetal, MatterPhase.solid),
    aluminium("aluminium", "Al", 26.9815386f, 2.7f, ElementClassifications.otherMetal, MatterPhase.solid),
    Silicon("Silicon", "Si", 28.0855f, 2.33f, ElementClassifications.otherMetal, MatterPhase.solid),
    Phosphorus("Phosphorus", "P", 30.973762f, 1.823f, ElementClassifications.nonmetal, MatterPhase.solid),
    Sulphur("Sulphur", "S", 32.065f, 1.96f, ElementClassifications.nonmetal, MatterPhase.solid),
    Chlorine("Chlorine", "Cl", 35.453f, 3.214f, ElementClassifications.halogen, MatterPhase.gas),
    Argon("Argon", "Ar", 39.948f, 1.784f, ElementClassifications.inertGas, MatterPhase.gas),
    Potassium("Potassium", "K", 39.0983f, 0.856f, ElementClassifications.alkaliMetal, MatterPhase.solid),
    Calcium("Calcium", "Ca", 40.078f, 1.55f, ElementClassifications.alkalineEarthMetal, MatterPhase.solid),
    Scandium("Scandium", "Sc", 44.955912f, 2.985f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Titanium("Titanium", "Ti", 47.867f, 4.507f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Vanadium("Vanadium", "V", 50.9415f, 6.11f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Chromium("Chromium", "Cr", 51.9961f, 7.14f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Manganese("Manganese", "Mn", 54.938045f, 7.47f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Iron("Iron", "Fe", 55.845f, 7.874f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Cobalt("Cobalt", "Co", 58.933195f, 8.9f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Nickel("Nickel", "Ni", 58.6934f, 8.908f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Copper("Copper", "Cu", 63.546f, 8.92f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Zinc("Zinc", "Zn", 65.38f, 7.14f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Gallium("Gallium", "Ga", 69.723f, 5.904f, ElementClassifications.otherMetal, MatterPhase.solid),
    Germanium("Germanium", "Ge", 72.64f, 5.323f, ElementClassifications.semimetallic, MatterPhase.solid),
    Arsenic("Arsenic", "As", 74.9216f, 5.727f, ElementClassifications.semimetallic, MatterPhase.solid),
    Selenium("Selenium", "Se", 78.96f, 4.819f, ElementClassifications.nonmetal, MatterPhase.solid),
    Bromine("Bromine", "Br", 79.904f, 3.12f, ElementClassifications.halogen, MatterPhase.liquid),
    Krypton("Krypton", "Kr", 83.798f, 3.75f, ElementClassifications.inertGas, MatterPhase.gas),
    Rubidium("Rubidium", "Rb", 85.4678f, 1.532f, ElementClassifications.alkaliMetal, MatterPhase.solid),
    Strontium("Strontium", "Sr", 87.62f, 2.63f, ElementClassifications.alkalineEarthMetal, MatterPhase.solid),
    Yttrium("Yttrium", "Y", 88.90585f, 4.472f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Zirkonium("Zirkonium", "Zr", 91.224f, 6.511f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Niobium("Niobium", "Nb", 92.90638f, 8.57f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Molybdaenum("Molybdaenum", "Mo", 95.96f, 10.28f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Technetium("Technetium", "Tc", 98f, 11.5f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ruthenium("Ruthenium", "Ru", 101.07f, 12.37f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Rhodium("Rhodium", "Rh", 102.9055f, 12.45f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Palladium("Palladium", "Pd", 106.42f, 12.023f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Silver("Silver", "Ag", 107.8682f, 10.49f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Cadmium("Cadmium", "Cd", 112.411f, 8.65f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Indium("Indium", "In", 114.818f, 7.31f, ElementClassifications.otherMetal, MatterPhase.solid),
    Tin("Tin", "Sn", 118.71f, 7.31f, ElementClassifications.otherMetal, MatterPhase.solid),
    Antimony("Antimony", "Sb", 121.76f, 6.697f, ElementClassifications.semimetallic, MatterPhase.solid),
    Tellurium("Tellurium", "Te", 127.6f, 6.24f, ElementClassifications.semimetallic, MatterPhase.solid),
    Iodine("Iodine", "I", 126.90447f, 4.94f, ElementClassifications.halogen, MatterPhase.solid),
    Xenon("Xenon", "Xe", 131.293f, 5.9f, ElementClassifications.inertGas, MatterPhase.gas),
    Cesium("Cesium", "Cs", 132.9054519f, 1.879f, ElementClassifications.alkaliMetal, MatterPhase.solid),
    Barium("Barium", "Ba", 137.327f, 3.51f, ElementClassifications.alkalineEarthMetal, MatterPhase.solid),
    Lanthanum("Lanthanum", "La", 138.90547f, 6.146f, ElementClassifications.lanthanide, MatterPhase.solid),
    Cerium("Cerium", "Ce", 140.116f, 6.689f, ElementClassifications.lanthanide, MatterPhase.solid),
    Praseodymium("Praseodymium", "Pr", 140.90765f, 6.64f, ElementClassifications.lanthanide, MatterPhase.solid),
    Neodymium("Neodymium", "Nd", 144.242f, 7.01f, ElementClassifications.lanthanide, MatterPhase.solid),
    Promethium("Promethium", "Pm", 145f, 7.264f, ElementClassifications.lanthanide, MatterPhase.solid),
    Samarium("Samarium", "Sm", 150.36f, 7.353f, ElementClassifications.lanthanide, MatterPhase.solid),
    Europium("Europium", "Eu", 151.964f, 5.244f, ElementClassifications.lanthanide, MatterPhase.solid),
    Gadolinium("Gadolinium", "Gd", 157.25f, 7.901f, ElementClassifications.lanthanide, MatterPhase.solid),
    Terbium("Terbium", "Tb", 158.92535f, 8.219f, ElementClassifications.lanthanide, MatterPhase.solid),
    Dysprosium("Dysprosium", "Dy", 162.5001f, 8.551f, ElementClassifications.lanthanide, MatterPhase.solid),
    Holmium("Holmium", "Ho", 164.93032f, 8.795f, ElementClassifications.lanthanide, MatterPhase.solid),
    Erbium("Erbium", "Er", 167.259f, 9.066f, ElementClassifications.lanthanide, MatterPhase.solid),
    Thulium("Thulium", "Tm", 168.93421f, 9.321f, ElementClassifications.lanthanide, MatterPhase.solid),
    Ytterbium("Ytterbium", "Yb", 173.054f, 6.57f, ElementClassifications.lanthanide, MatterPhase.solid),
    Lutetium("Lutetium", "Lu", 174.9668f, 9.841f, ElementClassifications.lanthanide, MatterPhase.solid),
    Hafnium("Hafnium", "Hf", 178.49f, 13.31f, ElementClassifications.lanthanide, MatterPhase.solid),
    Tantalum("Tantalum", "Ta", 180.94788f, 16.65f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Tungsten("Tungsten", "W", 183.84f, 19.25f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Rhenium("Rhenium", "Re", 186.207f, 21.02f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Osmium("Osmium", "Os", 190.23f, 22.59f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Iridium("Iridium", "Ir", 192.217f, 22.56f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Platinum("Platinum", "Pt", 192.084f, 21.09f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Gold("Gold", "Au", 196.966569f, 19.3f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Hydrargyrum("Hydrargyrum", "Hg", 200.59f, 13.534f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Thallium("Thallium", "Tl", 204.3833f, 11.85f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Lead("Lead", "Pb", 207.2f, 11.34f, ElementClassifications.otherMetal, MatterPhase.solid),
    Bismuth("Bismuth", "Bi", 208.980401f, 9.78f, ElementClassifications.otherMetal, MatterPhase.solid),
    Polonium("Polonium", "Po", 210f, 9.196f, ElementClassifications.semimetallic, MatterPhase.solid),
    Astatine("Astatine", "At", 210f, 0, ElementClassifications.halogen, MatterPhase.solid),
    Radon("Radon", "Rn", 220f, 9.73f, ElementClassifications.inertGas, MatterPhase.gas),
    Francium("Francium", "Fr", 223f, 0f, ElementClassifications.alkaliMetal, MatterPhase.solid),
    Radium("Radium", "Ra", 226f, 5f, ElementClassifications.alkalineEarthMetal, MatterPhase.solid),
    Actinium("Actinium", "Ac", 227f, 10.07f, ElementClassifications.actinide, MatterPhase.solid),
    Thorium("Thorium", "Th", 232.03806f, 11.724f, ElementClassifications.actinide, MatterPhase.solid),
    Protactinium("Protactinium", "Pa", 231.03588f, 15.37f, ElementClassifications.actinide, MatterPhase.solid),
    Uranium("Uranium", "U", 238.02891f, 19.05f, ElementClassifications.actinide, MatterPhase.solid),
    Neptunium("Neptunium", "Np", 237f, 20.45f, ElementClassifications.actinide, MatterPhase.solid),
    Plutonium("Plutonium", "Pu", 244f, 19.816f, ElementClassifications.actinide, MatterPhase.solid),
    Americium("Americium", "Am", 243f, 13.67f, ElementClassifications.actinide, MatterPhase.solid),
    Curium("Curium", "Cm", 247f, 3.51f, ElementClassifications.actinide, MatterPhase.solid),
    Berkelium("Berkelium", "Bk", 247f, 14.78f, ElementClassifications.actinide, MatterPhase.solid),
    Californium("Californium", "Cf", 251f, 15.1f, ElementClassifications.actinide, MatterPhase.solid),
    Einsteinium("Einsteinium", "Es", 252f, 0, ElementClassifications.actinide, MatterPhase.solid),
    Fermium("Fermium", "Fm", 257f, 0, ElementClassifications.actinide, MatterPhase.solid),
    Mendelevium("Mendelevium", "Md", 0, 258f, ElementClassifications.actinide, MatterPhase.solid),
    Nobelium("Nobelium", "No", 259f, 0, ElementClassifications.actinide, MatterPhase.solid),
    Lawrencium("Lawrencium", "Lr", 262f, 0, ElementClassifications.actinide, MatterPhase.solid),
    Rutherfordium("Rutherfordium", "Rf", 261f, 0, ElementClassifications.transitionMetal, MatterPhase.solid),
    Dubnium("Dubnium", "Db", 262f, 0, ElementClassifications.transitionMetal, MatterPhase.solid),
    Seaborgium("Seaborgium", "Sg", 266f, 0, ElementClassifications.transitionMetal, MatterPhase.solid),
    Bohrium("Bohrium", "Bh", 264f, 0, ElementClassifications.transitionMetal, MatterPhase.solid),
    Hassium("Hassium", "Hs", 277f, 0, ElementClassifications.transitionMetal, MatterPhase.solid),
    Meitnerium("Meitnerium", "Mt", 268f, 0, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ununnilium("Ununnilium", "Ds", 271f, 0, ElementClassifications.transitionMetal, MatterPhase.solid),
    Unununium("Unununium", "Rg", 272f, 0, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ununbium("Ununbium", "Uub", 285f, 0, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ununtrium("Ununtrium", "Uut", 284f, 0, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ununquadium("Ununquadium", "Uuq", 289f, 0, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ununpentium("Ununpentium", "Uup", 288f, 0, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ununhexium("Ununhexium", "Uuh", 292f, 0, ElementClassifications.transitionMetal, MatterPhase.solid);

    /** value units are (g/cm^3) aka grams per centimeter cubed */
    public float density;
    /** value units are (amu) aka atomic mass unit */
    public float atomicMass;

    public String elementName = "element";
    public String elementSymbol = "element";

    public ElementClassifications classification;

    public MatterPhase normalPhase;

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

    private ChemElement(String name, String symbol, float atomicMass, float density, ElementClassifications type, MatterPhase defaultPhase)
    {
        this.elementName = name;
        this.elementSymbol = symbol;
        this.atomicMass = atomicMass;
        this.classification = type;
        this.normalPhase = defaultPhase;
        this.density = density;
    }

    private ChemElement(String name, String symbol, float atomicMass, float density, ElementClassifications type, MatterPhase defaultPhase, float meltingPoint, float boilingPoint, float fisionHeat, float vaporHeat, float specificHeat)
    {
        this(name, symbol, atomicMass, density, type, defaultPhase);

        this.meltingPointKelvin = meltingPoint;
        this.boilingPointKelvin = boilingPoint;
        this.heatOfFusion = fisionHeat;
        this.heatOfVaporization = vaporHeat;
        this.specificHeat = specificHeat;
    }

}
