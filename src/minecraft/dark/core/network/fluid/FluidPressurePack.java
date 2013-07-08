package dark.core.network.fluid;

import net.minecraftforge.fluids.FluidStack;

public class FluidPressurePack implements Cloneable
{
	public FluidStack liquidStack;
	public double pressure;

	public FluidPressurePack(FluidStack liquidStack, double voltage)
	{
		this.liquidStack = liquidStack;
		this.pressure = voltage;
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
