package com.builtbroken.common;


/** List of element from the periodic table of elements for any kind of use. Is not complete for all
 * parts but each element should have a listed names, symbol, and atomic mass. Atomic number should
 * be the placement # in the list. Var ZERO should not be used as its designed to offset the
 * placement of all elements by one
 *
 * @Source http://www.periodictable.com/Properties/A/SpecificHeat.an.html
 * @source http://www.chemicalelements.com/
 * @source http://www.lenntech.com/periodic/periodic-chart.htm
 * @author Robert Seifert */
public enum ChemElement
{
    /** Placeholder so that hydrogen starts as number one */
    ZERO("ZERO", "ZERO", 0, 0, null, null),
    Hydrogen("Hydrogen", "H", 1.00794f, 0.08988f, ElementClassifications.nonmetal, MatterPhase.gas),
    Helium("Helium", "He", 4.002602f, 0.1785f, ElementClassifications.inertGas, MatterPhase.gas),
    Lithium("Lithium", "Li", 6.941f, 0.53f, ElementClassifications.inertGas, MatterPhase.gas),
    Beryllium("Beryllium", "Be", 9.012182f, 1.8477f, ElementClassifications.inertGas, MatterPhase.gas),
    Boron("Boron", "B", 10.811f, ElementClassifications.inertGas, MatterPhase.gas),
    Carbon("Carbon", "C", 12.0107f, ElementClassifications.inertGas, MatterPhase.gas),
    Nitrogen("Nitrogen", "N", 14.0067f, ElementClassifications.inertGas, MatterPhase.gas),
    Oxygen("Oxygen", "O", 15.9994f, ElementClassifications.inertGas, MatterPhase.gas),
    Fluorine("Fluorine", "F", 18.9994f, ElementClassifications.inertGas, MatterPhase.gas),
    Neon("Neon", "Ne", 20.1797f, ElementClassifications.inertGas, MatterPhase.gas),
    Sodium("Sodium", "Na", 22.98976928f, ElementClassifications.alkaliMetal, MatterPhase.solid),
    Magnesium("Magnesium", "Mg", 24.305f, ElementClassifications.alkalineEarthMetal, MatterPhase.solid),
    aluminium("aluminium", "Al", 26.9815386f, ElementClassifications.otherMetal, MatterPhase.solid),
    Silicon("Silicon", "Si", 28.0855f, ElementClassifications.otherMetal, MatterPhase.solid),
    Phosphorus("Phosphorus", "P", 30.973762f, ElementClassifications.nonmetal, MatterPhase.solid),
    Sulphur("Sulphur", "S", 32.065f, ElementClassifications.nonmetal, MatterPhase.solid),
    Chlorine("Chlorine", "Cl", 35.453f, ElementClassifications.halogen, MatterPhase.gas),
    Argon("Argon", "Ar", 39.948f, ElementClassifications.inertGas, MatterPhase.gas),
    Potassium("Potassium", "K", 39.0983f, ElementClassifications.alkaliMetal, MatterPhase.solid),
    Calcium("Calcium", "Ca", 40.078f, ElementClassifications.alkalineEarthMetal, MatterPhase.solid),
    Scandium("Scandium", "Sc", 44.955912f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Titanium("Titanium", "Ti", 47.867f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Vanadium("Vanadium", "V", 50.9415f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Chromium("Chromium", "Cr", 51.9961f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Manganese("Manganese", "Mn", 54.938045f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Iron("Iron", "Fe", 55.845f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Cobalt("Cobalt", "Co", 58.933195f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Nickel("Nickel", "Ni", 58.6934f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Copper("Copper", "Cu", 63.546f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Zinc("Zinc", "Zn", 65.38f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Gallium("Gallium", "Ga", 69.723f, ElementClassifications.otherMetal, MatterPhase.solid),
    Germanium("Germanium", "Ge", 72.64f, ElementClassifications.semimetallic, MatterPhase.solid),
    Arsenic("Arsenic", "As", 74.9216f, ElementClassifications.semimetallic, MatterPhase.solid),
    Selenium("Selenium", "Se", 78.96f, ElementClassifications.nonmetal, MatterPhase.solid),
    Bromine("Bromine", "Br", 79.904f, ElementClassifications.halogen, MatterPhase.liquid),
    Krypton("Krypton", "Kr", 83.798f, ElementClassifications.inertGas, MatterPhase.gas),
    Rubidium("Rubidium", "Rb", 85.4678f, ElementClassifications.alkaliMetal, MatterPhase.solid),
    Strontium("Strontium", "Sr", 87.62f, ElementClassifications.alkalineEarthMetal, MatterPhase.solid),
    Yttrium("Yttrium", "Y", 88.90585f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Zirkonium("Zirkonium", "Zr", 91.224f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Niobium("Niobium", "Nb", 92.90638f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Molybdaenum("Molybdaenum", "Mo", 95.96f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Technetium("Technetium", "Tc", 98f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ruthenium("Ruthenium", "Ru", 101.07f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Rhodium("Rhodium", "Rh", 102.9055f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Palladium("Palladium", "Pd", 106.42f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Silver("Silver", "Ag", 107.8682f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Cadmium("Cadmium", "Cd", 112.411f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Indium("Indium", "In", 114.818f, ElementClassifications.otherMetal, MatterPhase.solid),
    Tin("Tin", "Sn", 118.71f, ElementClassifications.otherMetal, MatterPhase.solid),
    Antimony("Antimony", "Sb", 121.76f, ElementClassifications.semimetallic, MatterPhase.solid),
    Tellurium("Tellurium", "Te", 127.6f, ElementClassifications.semimetallic, MatterPhase.solid),
    Iodine("Iodine", "I", 126.90447f, ElementClassifications.halogen, MatterPhase.solid),
    Xenon("Xenon", "Xe", 131.293f, ElementClassifications.inertGas, MatterPhase.gas),
    Cesium("Cesium", "Cs", 132.9054519f, ElementClassifications.alkaliMetal, MatterPhase.solid),
    Barium("Barium", "Ba", 137.327f, ElementClassifications.alkalineEarthMetal, MatterPhase.solid),
    Lanthanum("Lanthanum", "La", 138.90547f, ElementClassifications.lanthanide, MatterPhase.solid),
    Cerium("Cerium", "Ce", 140.116f, ElementClassifications.lanthanide, MatterPhase.solid),
    Praseodymium("Praseodymium", "Pr", 140.90765f, ElementClassifications.lanthanide, MatterPhase.solid),
    Neodymium("Neodymium", "Nd", 144.242f, ElementClassifications.lanthanide, MatterPhase.solid),
    Promethium("Promethium", "Pm", 145f, ElementClassifications.lanthanide, MatterPhase.solid),
    Samarium("Samarium", "Sm", 150.36f, ElementClassifications.lanthanide, MatterPhase.solid),
    Europium("Europium", "Eu", 151.964f, ElementClassifications.lanthanide, MatterPhase.solid),
    Gadolinium("Gadolinium", "Gd", 157.25f, ElementClassifications.lanthanide, MatterPhase.solid),
    Terbium("Terbium", "Tb", 158.92535f, ElementClassifications.lanthanide, MatterPhase.solid),
    Dysprosium("Dysprosium", "Dy", 162.5001f, ElementClassifications.lanthanide, MatterPhase.solid),
    Holmium("Holmium", "Ho", 164.93032f, ElementClassifications.lanthanide, MatterPhase.solid),
    Erbium("Erbium", "Er", 167.259f, ElementClassifications.lanthanide, MatterPhase.solid),
    Thulium("Thulium", "Tm", 168.93421f, ElementClassifications.lanthanide, MatterPhase.solid),
    Ytterbium("Ytterbium", "Yb", 173.054f, ElementClassifications.lanthanide, MatterPhase.solid),
    Lutetium("Lutetium", "Lu", 174.9668f, ElementClassifications.lanthanide, MatterPhase.solid),
    Hafnium("Hafnium", "Hf", 178.49f, ElementClassifications.lanthanide, MatterPhase.solid),
    Tantalum("Tantalum", "Ta", 180.94788f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Tungsten("Tungsten", "W", 183.84f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Rhenium("Rhenium", "Re", 186.207f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Osmium("Osmium", "Os", 190.23f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Iridium("Iridium", "Ir", 192.217f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Platinum("Platinum", "Pt", 192.084f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Gold("Gold", "Au", 196.966569f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Hydrargyrum("Hydrargyrum", "Hg", 200.59f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Thallium("Thallium", "Tl", 204.3833f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Lead("Lead", "Pb", 207.2f, ElementClassifications.otherMetal, MatterPhase.solid),
    Bismuth("Bismuth", "Bi", 208.980401f, ElementClassifications.otherMetal, MatterPhase.solid),
    Polonium("Polonium", "Po", 210f, ElementClassifications.semimetallic, MatterPhase.solid),
    Astatine("Astatine", "At", 210f, ElementClassifications.halogen, MatterPhase.solid),
    Radon("Radon", "Rn", 220f, ElementClassifications.inertGas, MatterPhase.gas),
    Francium("Francium", "Fr", 223f, ElementClassifications.alkaliMetal, MatterPhase.solid),
    Radium("Radium", "Ra", 226f, ElementClassifications.alkalineEarthMetal, MatterPhase.solid),
    Actinium("Actinium", "Ac", 227f, ElementClassifications.actinide, MatterPhase.solid),
    Thorium("Thorium", "Th", 232.03806f, ElementClassifications.actinide, MatterPhase.solid),
    Protactinium("Protactinium", "Pa", 231.03588f, ElementClassifications.actinide, MatterPhase.solid),
    Uranium("Uranium", "U", 238.02891f, ElementClassifications.actinide, MatterPhase.solid),
    Neptunium("Neptunium", "Np", 237f, ElementClassifications.actinide, MatterPhase.solid),
    Plutonium("Plutonium", "Pu", 244f, ElementClassifications.actinide, MatterPhase.solid),
    Americium("Americium", "Am", 243f, ElementClassifications.actinide, MatterPhase.solid),
    Curium("Curium", "Cm", 247f, ElementClassifications.actinide, MatterPhase.solid),
    Berkelium("Berkelium", "Bk", 247f, ElementClassifications.actinide, MatterPhase.solid),
    Californium("Californium", "Cf", 251f, ElementClassifications.actinide, MatterPhase.solid),
    Einsteinium("Einsteinium", "Es", 252f, ElementClassifications.actinide, MatterPhase.solid),
    Fermium("Fermium", "Fm", 257f, ElementClassifications.actinide, MatterPhase.solid),
    Mendelevium("Mendelevium", "Md", 258f, ElementClassifications.actinide, MatterPhase.solid),
    Nobelium("Nobelium", "No", 259f, ElementClassifications.actinide, MatterPhase.solid),
    Lawrencium("Lawrencium", "Lr", 262f, ElementClassifications.actinide, MatterPhase.solid),
    Rutherfordium("Rutherfordium", "Rf", 261f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Dubnium("Dubnium", "Db", 262f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Seaborgium("Seaborgium", "Sg", 266f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Bohrium("Bohrium", "Bh", 264f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Hassium("Hassium", "Hs", 277f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Meitnerium("Meitnerium", "Mt", 268f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ununnilium("Ununnilium", "Ds", 271f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Unununium("Unununium", "Rg", 272f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ununbium("Ununbium", "Uub", 285f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ununtrium("Ununtrium", "Uut", 284f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ununquadium("Ununquadium", "Uuq", 289f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ununpentium("Ununpentium", "Uup", 288f, ElementClassifications.transitionMetal, MatterPhase.solid),
    Ununhexium("Ununhexium", "Uuh", 292f, ElementClassifications.transitionMetal, MatterPhase.solid);

    /** g/cm^3 */
    public float density;
    /** amu */
    public float atomicMass;

    public String elementName = "element";
    public String elementSymbol = "element";

    private ChemElement(String name, String symbol, float atomicMass, ElementClassifications type, MatterPhase defaultPhase)
    {
        this.elementName = name;
        this.elementSymbol = symbol;
        this.atomicMass = atomicMass;
    }

    private ChemElement(String name, String symbol, float atomicMass, float density, ElementClassifications type, MatterPhase defaultPhase)
    {
        this(name, symbol, atomicMass, type, defaultPhase);
        this.density = density;
    }

}
