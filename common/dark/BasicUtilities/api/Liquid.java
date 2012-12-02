package dark.BasicUtilities.api;

import net.minecraft.src.Block;

/**
 * System too easily reference a liquid type and its info
 * 
 * @author Rseifert
 * 
 */
public enum Liquid
{
    // -1 == null || unused
    STEAM("Steam", false, true, true, -1, -1, 100),
    WATER("Water", false, false, true, Block.waterStill.blockID, Block.waterMoving.blockID, 32),
    LAVA("Lava", false, false, true, Block.lavaStill.blockID, Block.lavaMoving.blockID, 20),
    OIL("Oil", true, false, true, -1, -1, 32), // BasicComponents.oilStill.blockID,BasicComponents.oilMoving.blockID),
    Fuel("Fuel", true, false, true, -1, -1, 40),
    Air("Air", false, true, false, 0, -1, 100),
    Methain("Methain", true, true, false, -1, -1, 100),
    BioFuel("BioFuel", true, false, false, -1, -1, 40),
    Coolent("Coolent", false, false, false, -1, -1, 40),
    NukeWaste("NukeWaste", false, false, false, -1, -1, 20),
    Ether("Ether", false, false, false, -1, -1, 100),
    HEAT("HEAT", false, false, false, -1, -1, -1),
    DEFUALT("Empty", false, false, false, -1, -1, 0);
    public final boolean flamable;// can it catch on fire, not used but might be
    public final boolean isGas;// is it a gas, used to find if it floats
    public final boolean showMenu;
    public final int Still;// if there is a block of still liquid linked to this
    public final int Moving;// if there is a block of moving liquid linked to
                            // this
    public final String lName;// Default name for the liquid
    public final int defaultPresure;// default pressure output of the liquid

    private Liquid(String name, boolean flame, boolean gas, boolean show, int block, int Moving, int dPressure)
    {
        this.flamable = flame;
        this.showMenu = show;
        this.isGas = gas;
        this.Still = block;
        this.Moving = Moving;
        this.lName = name;
        this.defaultPresure = dPressure;
    }

    /**
     * Only use this if you are converting from the old system Or have a special
     * need for it
     * 
     * @param id
     *            of liquid
     * @return Liquid Object
     */
    public static Liquid getLiquid(int id)
    {
        if (id >= 0 && id < Liquid.values().length) { return Liquid.values()[id]; }
        return DEFUALT;
    }

    public static Liquid getLiquidByBlock(int bBlock)
    {
        for (int i = 0; i < Liquid.values().length; i++)
        {
            Liquid selected = Liquid.getLiquid(i);
            if (bBlock == selected.Still) { return selected; }
        }
        return Liquid.DEFUALT;
    }
}
