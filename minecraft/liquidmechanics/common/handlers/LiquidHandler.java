package liquidmechanics.common.handlers;

import java.util.ArrayList;
import java.util.List;

import liquidmechanics.common.LiquidMechanics;
import net.minecraft.block.Block;
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
    //public static LiquidData oil; TODO add
    //public static LiquidData fuel;
    /**
     * Called to add the default liquids to the allowed list
     */
    public static void addDefaultLiquids()
    {
        steam = new LiquidData(LiquidDictionary.getOrCreateLiquid("Steam", new LiquidStack(LiquidMechanics.blockSteamBlock, 1)), true, 100);
        allowedLiquids.add(steam);
        water = new LiquidData(LiquidDictionary.getOrCreateLiquid("Water", new LiquidStack(Block.waterStill, 1)), false, 32);
        allowedLiquids.add(water);
        lava = new LiquidData(LiquidDictionary.getOrCreateLiquid("Lava", new LiquidStack(Block.lavaStill, 1)), false, 20);
        allowedLiquids.add(lava);
    }

    @ForgeSubscribe
    public void liquidRegisterEvent(LiquidRegisterEvent event)
    {
        // TODO use this to add new liquid types to the data list
        // or something along the lines of IDing liquids for use
        boolean used = false;
        for (LiquidData dta : allowedLiquids)
        {

        } 
        LiquidData data = new LiquidData(event.Liquid, false, 32);
        if (!used && !allowedLiquids.contains(data))
        {
           allowedLiquids.add(data);
        }

    }
}
