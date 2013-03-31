package hydraulic.core.liquidNetwork;

import hydraulic.api.ColorCode;

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
	@Deprecated
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
		if (event.Name.equalsIgnoreCase("UraniumHexafluoride"))
		{
			allowedLiquids.add(new LiquidData("UraniumHexafluoride", event.Liquid, ColorCode.GREEN, true, 100));
		}
		else if (event.Name.equalsIgnoreCase("methane"))
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
	 * @param name - String name, not case sensitive
	 * 
	 * @return the data
	 * 
	 * Note: @LiquidHandler.unkown is the same as null and should be treated that way.
	 */
	public static LiquidData get(String name)
	{
		for (LiquidData data : LiquidHandler.allowedLiquids)
		{
			if (data.getName().equalsIgnoreCase(name))
			{
				return data;
			}
		}
		return unkown;
	}

	/**
	 * Gets the LiquidData linked to the liquidStack
	 * 
	 * @param stack - @LiquidStack
	 * @return the data
	 * 
	 * Note: @LiquidHandler.unkown is the same as null and should be treated that way.
	 */
	public static LiquidData get(LiquidStack stack)
	{
		for (LiquidData data : LiquidHandler.allowedLiquids)
		{
			if (stack.isLiquidEqual(data.getStack()))
			{
				return data;
			}
		}
		return unkown;
	}

	/**
	 * Gets the name of a LiquidStack using the LiquidData name or Value hidden in the LiquidStack
	 * map stored at @LiquidDictionary
	 * 
	 * @param stack - @LiquidStack
	 * @return - (String) Name of the Stack or unkown if one couldn't be found
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
				if (liquid.getValue().isLiquidEqual(stack))
				{
					return liquid.getKey();
				}
			}
		}
		return "unkown";
	}

	/**
	 * Creates a new LiquidStack using the sample stack from the data
	 * 
	 * @param liquidData - liquidData being used to create the stack
	 * @param vol - amount or volume of the stack
	 * @return a new @LiquidStack
	 */
	public static LiquidStack getStack(LiquidData liquidData, int vol)
	{
		if (liquidData == null)
		{
			return null;
		}
		return new LiquidStack(liquidData.getStack().itemID, vol, liquidData.getStack().itemMeta);
	}

	/**
	 * Creates a new LiquidStack using the sample stack
	 * 
	 * @param stack - liquidLiquid being used to create the stack
	 * @param vol - amount or volume of the stack
	 * @return a new @LiquidStack
	 */
	public static LiquidStack getStack(LiquidStack stack, int vol)
	{
		if (stack == null)
		{
			return null;
		}
		return new LiquidStack(stack.itemID, vol, stack.itemMeta);
	}

	/**
	 * gets the LiquidData from the blockID and metadata
	 * 
	 * @param blockID - id used to reference the block in @Block
	 * @param blockMeta - the blocks sub id from 0-15. -1 will ignore metadata
	 * @return LiquidData if there is one for this block or unkown in place of null/not found
	 */
	public static LiquidData getFromBlockAndMetadata(int blockID, int blockMeta)
	{
		for (LiquidData data : allowedLiquids)
		{
			if (data.getStack().itemID == blockID)
			{
				return data;
			}
		}
		return unkown;
	}

	/**
	 * Consumes one item of a the ItemStack
	 */
	public static ItemStack consumeItem(ItemStack stack)
	{
		if (stack.stackSize == 1)
		{
			if (stack.getItem().hasContainerItem())
			{
				return stack.getItem().getContainerItemStack(stack);
			}
			else
			{
				return null;
			}
		}
		else
		{
			stack.splitStack(1);
			return stack;
		}
	}
}
