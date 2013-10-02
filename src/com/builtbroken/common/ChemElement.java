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
    ZERO("ZERO", "ZERO", 0, 0),
    Hydrogen("Hydrogen", "H", 1.00794f, 0.08988f),
    Helium("Helium", "He", 4.002602f, 0.1785f),
    Lithium("Lithium", "Li", 6.941f, 0.53f),
    Beryllium("Beryllium", "Be", 9.012182f, 1.8477f),
    Boron("Boron", "B", 10.811f),
    Carbon("Carbon", "C", 12.0107f),
    Nitrogen("Nitrogen", "N", 14.0067f),
    Oxygen("Oxygen", "O", 15.9994f),
    Fluorine("Fluorine", "F", 18.9994f),
    Neon("Neon", "Ne", 20.1797f),
    Sodium("Sodium", "Na", 22.98976928f),
    Magnesium("Magnesium", "Mg", 24.305f),
    aluminium("aluminium", "Al", 26.9815386f),
    Silicon("Silicon", "Si", 28.0855f),
    Phosphorus("Phosphorus", "P", 30.973762f),
    Sulphur("Sulphur", "S", 32.065f),
    Chlorine("Chlorine", "Cl", 35.453f),
    Argon("Argon", "Ar", 39.948f),
    Potassium("Potassium", "K", 39.0983f),
    Calcium("Calcium", "Ca", 40.078f),
    Scandium("Scandium", "Sc", 44.955912f),
    Titanium("Titanium", "Ti", 47.867f),
    Vanadium("Vanadium", "V", 50.9415f),
    Chromium("Chromium", "Cr", 51.9961f),
    Manganese("Manganese", "Mn", 54.938045f),
    Iron("Iron", "Fe", 55.845f),
    Cobalt("Cobalt", "Co", 58.933195f),
    Nickel("Nickel", "Ni", 58.6934f),
    Copper("Copper", "Cu", 63.546f),
    Zinc("Zinc", "Zn", 65.38f),
    Gallium("Gallium", "Ga", 69.723f),
    Germanium("Germanium", "Ge", 72.64f),
    Arsenic("Arsenic", "As", 74.9216f),
    Selenium("Selenium", "Se", 78.96f),
    Bromine("Bromine", "Br", 79.904f),
    Krypton("Krypton", "Kr", 83.798f),
    Rubidium("Rubidium", "Rb", 85.4678f),
    Strontium("Strontium", "Sr", 87.62f),
    Yttrium("Yttrium", "Y", 88.90585f),
    Zirkonium("Zirkonium", "Zr", 91.224f),
    Niobium("Niobium", "Nb", 92.90638f),
    Molybdaenum("Molybdaenum", "Mo", 95.96f),
    Technetium("Technetium", "Tc", 98f),
    Ruthenium("Ruthenium", "Ru", 101.07f),
    Rhodium("Rhodium", "Rh", 102.9055f),
    Palladium("Palladium", "Pd", 106.42f),
    Silver("Silver", "Ag", 107.8682f),
    Cadmium("Cadmium", "Cd", 112.411f),
    Indium("Indium", "In", 114.818f),
    Tin("Tin", "Sn", 118.71f),
    Antimony("Antimony", "Sb", 121.76f),
    Tellurium("Tellurium", "Te", 127.6f),
    Iodine("Iodine", "I", 126.90447f),
    Xenon("Xenon", "Xe", 131.293f),
    Cesium("Cesium", "Cs", 132.9054519f),
    Barium("Barium", "Ba", 137.327f),
    Lanthanum("Lanthanum", "La", 138.90547f),
    Cerium("Cerium", "Ce", 140.116f),
    Praseodymium("Praseodymium", "Pr", 140.90765f),
    Neodymium("Neodymium", "Nd", 144.242f),
    Promethium("Promethium", "Pm", 145f),
    Samarium("Samarium", "Sm", 150.36f),
    Europium("Europium", "Eu", 151.964f),
    Gadolinium("Gadolinium", "Gd", 157.25f),
    Terbium("Terbium", "Tb", 158.92535f),
    Dysprosium("Dysprosium", "Dy", 162.5001f),
    Holmium("Holmium", "Ho", 164.93032f),
    Erbium("Erbium", "Er", 167.259f),
    Thulium("Thulium", "Tm", 168.93421f),
    Ytterbium("Ytterbium", "Yb", 173.054f),
    Lutetium("Lutetium", "Lu", 174.9668f),
    Hafnium("Hafnium", "Hf", 178.49f),
    Tantalum("Tantalum", "Ta", 180.94788f),
    Tungsten("Tungsten", "W", 183.84f),
    Rhenium("Rhenium", "Re", 186.207f),
    Osmium("Osmium", "Os", 190.23f),
    Iridium("Iridium", "Ir", 192.217f),
    Platinum("Platinum", "Pt", 192.084f),
    Gold("Gold", "Au", 196.966569f),
    Hydrargyrum("Hydrargyrum", "Hg", 200.59f),
    Thallium("Thallium", "Tl", 204.3833f),
    Lead("Lead", "Pb", 207.2f),
    Bismuth("Bismuth", "Bi", 208.980401f),
    Polonium("Polonium", "Po", 210f),
    Astatine("Astatine", "At", 210f),
    Radon("Radon", "Rn", 220f),
    Francium("Francium", "Fr", 223f),
    Radium("Radium", "Ra", 226f),
    Actinium("Actinium", "Ac", 227f),
    Thorium("Thorium", "Th", 232.03806f),
    Protactinium("Protactinium", "Pa", 231.03588f),
    Uranium("Uranium", "U", 238.02891f),
    Neptunium("Neptunium", "Np", 237f),
    Plutonium("Plutonium", "Pu", 244f),
    Americium("Americium", "Am", 243f),
    Curium("Curium", "Cm", 247f),
    Berkelium("Berkelium", "Bk", 247f),
    Californium("Californium", "Cf", 251f),
    Einsteinium("Einsteinium", "Es", 252f),
    Fermium("Fermium", "Fm", 257f),
    Mendelevium("Mendelevium", "Md", 258f),
    Nobelium("Nobelium", "No", 259f),
    Lawrencium("Lawrencium", "Lr", 262f),
    Rutherfordium("Rutherfordium", "Rf", 261f),
    Dubnium("Dubnium", "Db", 262f),
    Seaborgium("Seaborgium", "Sg", 266f),
    Bohrium("Bohrium", "Bh", 264f),
    Hassium("Hassium", "Hs", 277f),
    Meitnerium("Meitnerium", "Mt", 268f),
    Ununnilium("Ununnilium", "Ds", 271f),
    Unununium("Unununium", "Rg", 272f),
    Ununbium("Ununbium", "Uub", 285f),
    Ununtrium("Ununtrium", "Uut", 284f),
    Ununquadium("Ununquadium", "Uuq", 289f),
    Ununpentium("Ununpentium", "Uup", 288f),
    Ununhexium("Ununhexium", "Uuh", 292f);

    /** g/cm^3 */
    public float density;
    /** amu */
    public float atomicMass;

    public String elementName = "element";
    public String elementSymbol = "element";

    private ChemElement(String name, String symbol, float atomicMass)
    {
        this.elementName = name;
        this.elementSymbol = symbol;
        this.atomicMass = atomicMass;
    }

    private ChemElement(String name, String symbol, float atomicMass, float density)
    {
        this(name, symbol, atomicMass);
        this.density = density;
    }

}
