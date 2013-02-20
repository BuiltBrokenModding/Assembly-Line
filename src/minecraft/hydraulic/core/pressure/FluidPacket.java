package hydraulic.core.pressure;

import net.minecraftforge.liquids.LiquidStack;

/**
 * A simple way to store electrical data.
 * 
 * @author Calclavia
 * 
 */
public class FluidPacket implements Cloneable
{
	public LiquidStack liquidStack;
	public double pressure;

	public FluidPacket(double pressure, LiquidStack stack)
	{
		this.liquidStack = stack;
		this.pressure = pressure;
	}

	public FluidPacket()
	{
		this(0, new LiquidStack(0,0,0));
	}
	@Override
	public String toString()
	{
		return "FluidPacket [Vol:" + this.liquidStack + " Psi:" + this.pressure + "]";
	}

	@Override
	public FluidPacket clone()
	{
		return new FluidPacket( this.pressure,this.liquidStack);
	}

	public boolean isEquals(FluidPacket electricityPack)
	{
		return this.liquidStack == electricityPack.liquidStack && this.pressure == electricityPack.pressure;
	}
}
