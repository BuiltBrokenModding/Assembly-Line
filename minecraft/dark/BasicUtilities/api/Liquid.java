package dark.BasicUtilities.api;

import dark.BasicUtilities.BasicUtilitiesMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraftforge.liquids.ILiquid;
import net.minecraftforge.liquids.LiquidContainerRegistry;
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
    FUEL("Fuel", LiquidDictionary.getOrCreateLiquid("oil", new LiquidStack(BasicUtilitiesMain.oilStill, 1)), false, 40),
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

    /**
     * creates a new liquid stack using basic liquid type and the volume needed
     */
    public static LiquidStack getStack(Liquid type, int vol)
    {
        return new LiquidStack(type.liquid.itemID, vol, type.liquid.itemMeta);
    }

    /**
     * gets a liquid type from a liquidStack
     */
    public static Liquid getLiquid(LiquidStack stack)
    {
        for (int i = 0; i < Liquid.values().length - 1; i++)
        {
            if (Liquid.isStackEqual(stack, Liquid.values()[i])) { return Liquid.values()[i]; }
        }
        return Liquid.DEFUALT;
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
    public static Liquid getLiquidTypeByBlock(int bBlock)
    {
        if(bBlock == Block.waterMoving.blockID) return Liquid.DEFUALT;
        if(bBlock == Block.lavaMoving.blockID) return Liquid.DEFUALT;
        for (int i = 0; i < Liquid.values().length - 1; i++)
        {
            Liquid selected = Liquid.getLiquid(i);
            if (bBlock == selected.liquid.itemID) { return selected; }
        }
        return Liquid.DEFUALT;
    }

    public static LiquidStack getLiquidFromBlock(int blockId)
    {
        if (blockId == Block.waterStill.blockID) { return new LiquidStack(Block.waterStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 0); }
        if (blockId == Block.lavaStill.blockID) { return new LiquidStack(Block.lavaStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 0); }
        if (Block.blocksList[blockId] instanceof ILiquid)
        {
            ILiquid liquid = (ILiquid) Block.blocksList[blockId];
            if (liquid.isMetaSensitive()) return new LiquidStack(liquid.stillLiquidId(), LiquidContainerRegistry.BUCKET_VOLUME, liquid.stillLiquidMeta());
            else return new LiquidStack(liquid.stillLiquidId(), LiquidContainerRegistry.BUCKET_VOLUME, 0);
        }
        return null;
    }

    /**
     * Used to compare a liquidStack to a liquid type
     * 
     * @param stack
     * @param type
     * @return
     */
    public static boolean isStackEqual(LiquidStack stack, Liquid type)
    {
        if (stack == null) return false;
        if (type.liquid.itemID == stack.itemID && type.liquid.itemMeta == stack.itemMeta) { return true; }
        return false;
    }

    public static boolean isStackEqual(LiquidStack stack, LiquidStack type)
    {
        if (stack == null || type == null) return false;
        if (type.itemID == stack.itemID && type.itemMeta == stack.itemMeta) { return true; }
        return false;
    }
}
