package liquidmechanics.api.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidDictionary.LiquidRegisterEvent;

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

    // public static LiquidData oil; TODO add
    // public static LiquidData fuel;
    /**
     * Called to add the default liquids to the allowed list
     */
    public static void addDefaultLiquids()
    {
        water = new LiquidData("water", LiquidDictionary.getOrCreateLiquid("Water", new LiquidStack(Block.waterStill, 1)), PipeColor.BLUE, false, 32);
        allowedLiquids.add(water);
        lava = new LiquidData("Lava", LiquidDictionary.getOrCreateLiquid("Lava", new LiquidStack(Block.lavaStill, 1)), PipeColor.RED, false, 20);
        allowedLiquids.add(lava);
        unkown = new LiquidData("Unknown", LiquidDictionary.getOrCreateLiquid("Unknown", new LiquidStack(20, 1)), PipeColor.NONE, false, 0);
        allowedLiquids.add(unkown);
    }

    @ForgeSubscribe
    public void liquidRegisterEvent(LiquidRegisterEvent event)
    {
        // TODO use this to add new liquid types to the data list
        // or something along the lines of IDing liquids for use
        if (event.Name.equalsIgnoreCase("methane"))
        {
            this.allowedLiquids.add(new LiquidData("methane", event.Liquid, PipeColor.LIME, true, 100));
        }
        else if (event.Name.equalsIgnoreCase("oil"))
        {
            this.allowedLiquids.add(new LiquidData("oil", event.Liquid, PipeColor.BLACK, true, 50));
        }
        else if (event.Name.equalsIgnoreCase("fuel"))
        {
            this.allowedLiquids.add(new LiquidData("fuel", event.Liquid, PipeColor.YELLOW, true, 50));
        }
        else if (event.Name.equalsIgnoreCase("steam"))
        {
            this.steam = new LiquidData("steam", event.Liquid, PipeColor.ORANGE, true, 100);
        }else if(event.Name.equalsIgnoreCase("Waste"))
        {
            this.waste = new LiquidData("Waste", event.Liquid, PipeColor.BROWN, false, 40);
            this.allowedLiquids.add(waste);
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
     * gets the name of the liquidStack using either LiquidData or
     * running threw the LiquidDirectory mapping
     */
    public static String getName(LiquidStack stack)
    {
        if(get(stack) != unkown)
        {
            return get(stack).getName();
        }else
        {
            Map<String, LiquidStack> l = LiquidDictionary.getLiquids();
            for(Entry<String, LiquidStack> liquid : l.entrySet())
            {
                LiquidStack t = liquid.getValue();
                if(isEqual(t,stack))
                {
                    return liquid.getKey();
                }
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
        if(stack == null){return null;}
        return new LiquidStack(stack.itemID,vol,stack.itemMeta);
    }
    public static int getMeta(LiquidData type)
    {
        if (type == LiquidHandler.steam) return 0;
        if (type == LiquidHandler.water) return 1;
        if (type == LiquidHandler.lava) return 2;
        return 20;
    }

    public static LiquidData getFromMeta(int meta)
    {
        switch (meta)
        {
            case 0:
                return steam;
            case 1:
                return water;
            case 2:
                return lava;
        }
        return unkown;

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
     * compare a stack with a liquid type to see if there the same
     * 
     * @param stack
     * @param type
     * @return
     */
    public static boolean isEqual(LiquidStack stack, LiquidData type)
    {
        if (stack == null || type == null) { return false; }
        if (type.getStack().itemID == stack.itemID && type.getStack().itemMeta == stack.itemMeta) { return true; }
        return false;
    }

    public static boolean isEqual(LiquidStack stack, LiquidStack stack2)
    {
        if (stack == null || stack2 == null)
            return false;
        if (stack2.itemID == stack.itemID && stack2.itemMeta == stack.itemMeta) { return true; }
        return false;
    }

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
