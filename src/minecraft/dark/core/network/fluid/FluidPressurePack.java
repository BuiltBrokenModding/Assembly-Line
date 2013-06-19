package dark.core.network.fluid;

import net.minecraftforge.liquids.LiquidStack;

public class FluidPressurePack implements Cloneable
{
	public LiquidStack liquidStack;
	public double pressure;

	public FluidPressurePack(LiquidStack liquidStack, double voltage)
	{
		this.liquidStack = liquidStack;
		this.pressure = voltage;
	}

	public FluidPressurePack()
	{
		this(new LiquidStack(0, 0, 0), 0);
	}

	@Override
	public FluidPressurePack clone()
	{
		return new FluidPressurePack(this.liquidStack, this.pressure);
	}

	public boolean isEqual(FluidPressurePack electricityPack)
	{
		return this.liquidStack.isLiquidEqual(electricityPack.liquidStack) && this.pressure == electricityPack.pressure;
	}
}
