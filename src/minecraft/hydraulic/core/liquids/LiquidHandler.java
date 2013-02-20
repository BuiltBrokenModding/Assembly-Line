package hydraulic.core.liquids;

import hydraulic.core.implement.ColorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.common.FMLLog;

public class LiquidHandler
{
    // Active list of all Liquid that can be used//
    public static List<LiquidData> allowedLiquids = new ArrayList<LiquidData>();
    // PreDefinned Liquids//
    public static LiquidData steam;
    public static LiquidData water;
    public static LiquidData lava;
    public static LiquidData unkown;
    public static LiquidData waste;
    public static LiquidData milk;

    public static Logger FMLog = Logger.getLogger("LiquidHandler");

    /**
     * Called to add the default liquids to the allowed list
     */
    public static void addDefaultLiquids()
    {
        FMLog.setParent(FMLLog.getLogger());
        water = new LiquidData("water", LiquidDictionary.getOrCreateLiquid("Water", new LiquidStack(Block.waterStill, 1)), ColorCode.BLUE, false, 60);
        allowedLiquids.add(water);

        lava = new LiquidData("Lava", LiquidDictionary.getOrCreateLiquid("Lava", new LiquidStack(Block.lavaStill, 1)), ColorCode.RED, false, 40);
        allowedLiquids.add(lava);

        unkown = new LiquidData("Unknown", LiquidDictionary.getOrCreateLiquid("Unknown", new LiquidStack(20, 1)), ColorCode.NONE, false, 32);
        allowedLiquids.add(unkown);
        
        FMLog.setParent(FMLLog.getLogger());
        for (LiquidData data : allowedLiquids)
        {
            FMLog.info(data.getName() + " registered as a liquid");
        }

    }

    @ForgeSubscribe
    public void liquidRegisterEvent(LiquidDictionary.LiquidRegisterEvent event)
    {
        if (event.Name.equalsIgnoreCase("methane"))
        {
            allowedLiquids.add(new LiquidData("methane", event.Liquid, ColorCode.LIME, true, 100));
        }
        else if (event.Name.equalsIgnoreCase("oil"))
        {
            allowedLiquids.add(new LiquidData("oil", event.Liquid, ColorCode.BLACK, true, 50));
        }
        else if (event.Name.equalsIgnoreCase("fuel"))
        {
            allowedLiquids.add(new LiquidData("fuel", event.Liquid, ColorCode.YELLOW, true, 50));
        }
        else if (event.Name.equalsIgnoreCase("steam"))
        {
            steam = new LiquidData("steam", event.Liquid, ColorCode.ORANGE, true, 100);
            allowedLiquids.add(steam);
        }
        else if (event.Name.equalsIgnoreCase("Waste"))
        {
            waste = new LiquidData("Waste", event.Liquid, ColorCode.BROWN, false, 40);
            allowedLiquids.add(waste);
        }
        else if (event.Name.equalsIgnoreCase("Milk"))
        {
            milk = new LiquidData("Milk", event.Liquid, ColorCode.WHITE, false, 50);
            allowedLiquids.add(milk);
        }
    }

    /**
     * Gets the LiquidData linked to the liquid by name
     * 
     * @param name
     *            - String name, not case sensitive
     */
    public static LiquidData get(String name)
    {
        for (LiquidData data : LiquidHandler.allowedLiquids)
        {
            if (data.getName().equalsIgnoreCase(name)) { return data; }
        }
        return unkown;
    }

    public static LiquidData get(LiquidStack stack)
    {
        for (LiquidData data : LiquidHandler.allowedLiquids)
        {
            if (isEqual(stack, data)) { return data; }
        }
        return unkown;
    }

    /**
     * gets the name of the liquidStack using either LiquidData or running threw
     * the LiquidDirectory mapping
     */
    public static String getName(LiquidStack stack)
    {
        if (get(stack) != unkown)
        {
            return get(stack).getName();
        }
        else
        {
            Map<String, LiquidStack> l = LiquidDictionary.getLiquids();
            for (Entry<String, LiquidStack> liquid : l.entrySet())
            {
                LiquidStack t = liquid.getValue();
                if (isEqual(t, stack)) { return liquid.getKey(); }
            }
        }
        return "unkown";
    }

    /**
     * creates a new LiquidStack using type and vol
     */
    public static LiquidStack getStack(LiquidData type, int vol)
    {
        if (type == null) return null;
        return new LiquidStack(type.getStack().itemID, vol, type.getStack().itemMeta);
    }

    /**
     * creates a new LiquidStack using a liquidStack and vol
     */
    public static LiquidStack getStack(LiquidStack stack, int vol)
    {
        if (stack == null) { return null; }
        return new LiquidStack(stack.itemID, vol, stack.itemMeta);
    }

    public static int getMeta(LiquidData stack)
    {
        if (stack != null && stack != unkown) { return stack.getColor().ordinal(); }
        return 15;
    }

    public static LiquidData getFromMeta(int meta)
    {
        return ColorCode.get(meta).getLiquidData();
    }

    public static LiquidData getFromBlockID(int id)
    {
        for (LiquidData data : allowedLiquids)
        {
            if (data.getStack().itemID == id) { return data; }
        }
        return unkown;
    }

    /**
     * Compares a liquidStack to a sample stack stored in the LiquidData
     */
    public static boolean isEqual(LiquidStack stack, LiquidData type)
    {
        if (stack == null || type == null) { return false; }
        if (type.getStack().itemID == stack.itemID && type.getStack().itemMeta == stack.itemMeta) { return true; }
        return false;
    }
    /**
     * Compares one liquidStack to another LiquidStack
     */
    public static boolean isEqual(LiquidStack stack, LiquidStack stack2)
    {
        if (stack == null || stack2 == null)
            return false;
        if (stack2.itemID == stack.itemID && stack2.itemMeta == stack.itemMeta) { return true; }
        return false;
    }
    /**
     * Consumes one item of a the ItemStack
     */
    public static ItemStack consumeItem(ItemStack stack)
    {
        if (stack.stackSize == 1)
        {
            if (stack.getItem().hasContainerItem()) return stack.getItem().getContainerItemStack(stack);
            else return null;
        }
        else
        {
            stack.splitStack(1);
            return stack;
        }
    }
}
