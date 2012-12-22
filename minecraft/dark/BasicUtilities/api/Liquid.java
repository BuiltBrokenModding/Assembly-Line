package dark.BasicUtilities.api;

import dark.BasicUtilities.BasicUtilitiesMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

/**
 * System too easily reference a liquid type and its info
 * 
 * @author Rseifert
 * 
 */
public enum Liquid
{
    // -1 == null || unused
    STEAM("Steam", LiquidDictionary.getOrCreateLiquid("steam", new LiquidStack(BasicUtilitiesMain.SteamBlock, 1)), true, 100),
    WATER("Water", LiquidDictionary.getOrCreateLiquid("water", new LiquidStack(Block.waterStill, 1)), false, 32),
    LAVA("Lava", LiquidDictionary.getOrCreateLiquid("lava", new LiquidStack(Block.lavaStill, 1)), false, 20),
    OIL("Oil", LiquidDictionary.getOrCreateLiquid("oil", new LiquidStack(BasicUtilitiesMain.oilStill, 1)), true, 32),
    Fuel("Fuel", LiquidDictionary.getOrCreateLiquid("oil", new LiquidStack(BasicUtilitiesMain.oilStill, 1)), false, 40),
    DEFUALT("Empty", LiquidDictionary.getOrCreateLiquid("air", new LiquidStack(0, 1)), false, 0);

    public final boolean doesFlaot;
    public final String displayerName;
    public final int defaultPresure;
    public final LiquidStack liquid;

    private Liquid(String name, LiquidStack stack, boolean gas, int dPressure)
    {
        this.displayerName = name;
        this.liquid = stack;
        this.doesFlaot = gas;
        this.defaultPresure = dPressure;
    }

    public static LiquidStack getStack(Liquid type, int vol)
    {
        return new LiquidStack(type.liquid.itemID, vol, type.liquid.itemMeta);
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

    /**
     * get the liquid type by its block ID
     * 
     * @param bBlock
     * @return
     */
    public static Liquid getLiquidByBlock(int bBlock)
    {
        for (int i = 0; i < Liquid.values().length - 1; i++)
        {
            Liquid selected = Liquid.getLiquid(i);
            if (bBlock == selected.liquid.itemID) { return selected; }
        }
        return Liquid.DEFUALT;
    }
    /**
     * Used to compare a liquidStack to a liquid type
     * @param stack
     * @param type
     * @return
     */
    public static boolean isStackEqual(LiquidStack stack, Liquid type)
    {
        if(type.liquid.itemID == stack.itemID && type.liquid.itemMeta == stack.itemMeta)
        {
            return true;
        }
        return false;
    }
}
