package fluidmech.common;

import hydraulic.api.ColorCode;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.liquids.LiquidDictionary.LiquidRegisterEvent;

public class FluidEvents 
{
	
	@ForgeSubscribe
	public void onLiquidRegistered(LiquidRegisterEvent event)
	{
		if (event.Name != null)
		{
			if (event.Name.equals("Fuel") && !FluidMech.liquidTypes.contains(ColorCode.get(ColorCode.YELLOW).ordinal()))
			{
				FluidMech.liquidTypes.add(ColorCode.get(ColorCode.YELLOW).ordinal());
			}
			else if (event.Name.equals("Oil") && !FluidMech.liquidTypes.contains(ColorCode.get(ColorCode.BLACK).ordinal()))
			{
				FluidMech.liquidTypes.add(ColorCode.get(ColorCode.BLACK).ordinal());
			}
		}
	}
}
