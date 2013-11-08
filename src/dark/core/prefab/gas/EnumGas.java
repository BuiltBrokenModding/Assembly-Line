package dark.core.prefab.gas;

import com.builtbroken.common.science.ChemElement;
import com.builtbroken.common.science.ChemicalCompound;

/** Enum of gases used to create all the gas fluids
 *
 * @author DarkGuardsman */
public enum EnumGas
{
    C2O("Carbon DiOxide"),
    O2("Oxygen"),
    C4H10(ChemicalCompound.BUTANE),
    METHANE("Methane"),
    NATURAL_GAS("Natural Gas"),
    PROPANE("Propane");
    /** Name used when creating this as a fluid */
    final String fluidName;
    /** Name used to display to the players */
    final String name;
    /** Object data reference that was used to create this gas, can be a ChemicalCompound, Element,
     * or Fluid */
    final Object data;

    private EnumGas(String name)
    {
        this.fluidName = "gas:" + name.replace(" ", "").toLowerCase();
        this.name = name;
        data = null;
    }

    private EnumGas(ChemicalCompound compound)
    {
        this.fluidName = "gas:" + compound.compoundName.replace(" ", "").toLowerCase();
        this.name = compound.compoundName;
        data = compound;
    }

    private EnumGas(ChemElement element)
    {
        this.fluidName = "gas:" + element.elementName.replace(" ", "").toLowerCase();
        this.name = element.elementName;
        data = element;
    }
}
