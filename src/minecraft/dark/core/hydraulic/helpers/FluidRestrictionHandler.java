package dark.core.hydraulic.helpers;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidRegistry.FluidRegisterEvent;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import dark.core.api.ColorCode;

public class FluidRestrictionHandler
{
	private static BiMap<ColorCode, Fluid> restrictedStacks = HashBiMap.create();

	static
	{
		/* ADD DEFAULT LIQUIDS */
		restrictedStacks.put(ColorCode.BLUE, FluidRegistry.WATER);
		restrictedStacks.put(ColorCode.RED, FluidRegistry.LAVA);
	}

	@ForgeSubscribe
	public void onLiquidRegistered(FluidRegisterEvent event)
	{
		if (event != null && event.fluidName != null)
		{
			Fluid fluid = FluidRegistry.getFluid(event.fluidName);
			if (event.fluidName.equalsIgnoreCase("Fuel") && !restrictedStacks.containsKey(ColorCode.YELLOW))
			{
				restrictedStacks.put(ColorCode.YELLOW, fluid);
			}
			else if (event.fluidName.equalsIgnoreCase("Oil") && !restrictedStacks.containsKey(ColorCode.BLACK))
			{
				restrictedStacks.put(ColorCode.BLACK, fluid);
			}
			else if (event.fluidName.equalsIgnoreCase("Milk") && !restrictedStacks.containsKey(ColorCode.WHITE))
			{
				restrictedStacks.put(ColorCode.WHITE, fluid);
			}
		}
	}

	/** Checks too see if a color has a restricted stack */
	public static boolean hasRestrictedStack(int meta)
	{
		return restrictedStacks.containsKey(ColorCode.get(meta));
	}

	public static boolean hasRestrictedStack(Fluid stack)
	{
		return stack != null && restrictedStacks.inverse().containsKey(stack);
	}

	/** gets the liquid stack that is restricted to this color */
	public static Fluid getStackForColor(ColorCode color)
	{
		return restrictedStacks.get(color);
	}

	/** checks to see if the liquidStack is valid for the given color */
	public static boolean isValidLiquid(ColorCode color, Fluid stack)
	{
		if (stack == null)
		{
			return false;
		}
		if (!FluidRestrictionHandler.hasRestrictedStack(color.ordinal()))
		{
			return true;
		}
		return FluidRestrictionHandler.hasRestrictedStack(color.ordinal()) && FluidRestrictionHandler.getStackForColor(color).equals(stack);
	}
}
