package hydraulic.api;

import hydraulic.core.liquidNetwork.LiquidData;
import hydraulic.core.liquidNetwork.LiquidHandler;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.liquids.LiquidStack;

public enum ColorCode
{
	BLACK("Black"), RED("Red"), GREEN("Green"), BROWN("Brown"), BLUE("Blue"), PURPLE("Purple"), CYAN("Cyan"), SILVER("Silver"), GREY("Grey"), PINK("Pink"), LIME("Lime"), YELLOW("Yellow"), LIGHTBLUE("LightBlue"), WHITE("White"), ORANGE("Orange"), NONE("");

	String name;

	private ColorCode(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	/**
	 * gets a ColorCode from any of the following
	 * 
	 * @param obj - Integer,String,LiquidData,ColorCode
	 * @return Color NONE if it can't find it
	 */
	public static ColorCode get(Object obj)
	{
		if (obj instanceof Integer && ((Integer) obj) < ColorCode.values().length)
		{
			return ColorCode.values()[((Integer) obj)];
		}
		else if (obj instanceof LiquidData)
		{
			return ((LiquidData) obj).getColor();
		}
		else if (obj instanceof LiquidStack)
		{
			return LiquidHandler.get((LiquidStack) obj).getColor();
		}
		else if (obj instanceof ColorCode)
		{
			return (ColorCode) obj;
		}
		else if (obj instanceof String)
		{
			for (int i = 0; i < ColorCode.values().length; i++)
			{
				if (((String) obj).equalsIgnoreCase(ColorCode.get(i).getName()))
				{
					return ColorCode.get(i);
				}
			}
		}
		return NONE;
	}

	/**
	 * gets the liquidData linked with this color
	 */
	public LiquidData getLiquidData()
	{
		for (LiquidData data : LiquidHandler.allowedLiquids)
		{
			if (data.getColor() == this)
			{
				return data;
			}
		}
		return LiquidHandler.unkown;
	}

	/**
	 * Gets a list of LiquidData that are linked with the color
	 */
	public List<LiquidData> getAllLiquidData()
	{
		List<LiquidData> validLiquids = new ArrayList<LiquidData>();
		for (LiquidData data : LiquidHandler.allowedLiquids)
		{
			if (data.getColor() == this && !validLiquids.contains(data))
			{
				validLiquids.add(data);
			}
		}
		return validLiquids;
	}
	
	public List<LiquidStack> getAllLiquidStack()
	{
		List<LiquidStack> validStacks = new ArrayList<LiquidStack>();
		for (LiquidData data : getAllLiquidData())
		{
				validStacks.add(data.getStack());
		}
		return validStacks;
	}
	
	public LiquidStack[] getArrayLiquidStacks()
	{
		List<LiquidStack> validStacks = new ArrayList<LiquidStack>();
		for (LiquidData data : getAllLiquidData())
		{
				validStacks.add(data.getStack());
		}
		LiquidStack[] stacks = new LiquidStack[validStacks.size()];
		for(int i =0; i < validStacks.size();i++)
		{
			stacks[i] = validStacks.get(i);
		}
		return stacks;
	}

	/**
	 * checks to see if the liquidStack is valid for the given color
	 */
	public boolean isValidLiquid(LiquidStack stack)
	{
		if (this == NONE || this.getAllLiquidData().size() == 0)
		{
			return true;
		}
		else if (stack == null)
		{
			return false;
		}

		for (LiquidData data : LiquidHandler.allowedLiquids)
		{
			if (data.getStack().isLiquidEqual(stack) && data.getColor() == this)
			{
				return true;
			}
		}
		return false;
	}
}
