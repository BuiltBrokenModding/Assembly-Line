package fluidmech.common.machines.pipes;

import hydraulic.api.ColorCode;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;

public class TileEntityGenericPipe extends TileEntityPipe
{
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		if (resource == null)
		{
			return 0;
		}
		return this.getNetwork().addFluidToNetwork(worldObj.getBlockTileEntity(xCoord + from.offsetX, yCoord + from.offsetY, zCoord + from.offsetZ), resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (tankIndex != 0 || resource == null)
		{
			return 0;
		}
		return this.getNetwork().addFluidToNetwork(this, resource, doFill);
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
	{
		return this.fakeTank;
	}

	@Override
	public ColorCode getColor()
	{
		return ColorCode.NONE;
	}
	@Override
	public boolean canPipeConnect(TileEntity entity, ForgeDirection dir)
	{
		return this.subEntities[dir.ordinal()] == null && entity.getClass().equals(this.getClass());
	}
	
}
