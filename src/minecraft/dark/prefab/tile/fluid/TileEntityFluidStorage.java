package dark.prefab.tile.fluid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import dark.core.api.IColorCoded;
import dark.core.hydraulic.helpers.FluidHelper;
import dark.core.hydraulic.helpers.FluidRestrictionHandler;

public abstract class TileEntityFluidStorage extends TileEntityFluidDevice implements ITankContainer, IColorCoded
{
	/* INTERNAL TANK */
	public LiquidTank tank = new LiquidTank(this.getTankSize());

	/**
	 * gets the max storage limit of the tank
	 */
	public abstract int getTankSize();

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		if (this.tank.getLiquid() == null)
		{
			return "Empty";
		}
		return String.format("%d/%d %S Stored", tank.getLiquid().amount / LiquidContainerRegistry.BUCKET_VOLUME, tank.getCapacity() / LiquidContainerRegistry.BUCKET_VOLUME, LiquidDictionary.findLiquidName(tank.getLiquid()));
	}

	@Override
	public boolean canTileConnect(TileEntity entity, ForgeDirection dir)
	{
		if (entity instanceof ITankContainer)
		{
			return true;
		}
		return false;
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		return this.fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (resource == null || tankIndex != 0)
		{
			return 0;
		}
		else if (!FluidRestrictionHandler.isValidLiquid(getColor(),resource))
		{
			return 0;
		}
		else if (this.tank.getLiquid() != null && !resource.isLiquidEqual(this.tank.getLiquid()))
		{
			return 0;
		}
		return this.tank.fill(resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return this.drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		if (tankIndex != 0 || this.tank.getLiquid() == null)
		{
			return null;
		}
		LiquidStack stack = this.tank.getLiquid();
		if (maxDrain < stack.amount)
		{
			stack = FluidHelper.getStack(stack, maxDrain);
		}
		return this.tank.drain(maxDrain, doDrain);
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection dir)
	{
		return new ILiquidTank[] { this.tank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection dir, LiquidStack type)
	{
		if (type == null)
		{
			return null;
		}
		if (type.isLiquidEqual(this.tank.getLiquid()))
		{
			return this.tank;
		}
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		LiquidStack liquid = LiquidStack.loadLiquidStackFromNBT(nbt.getCompoundTag("stored"));
		if (liquid != null)
		{
			tank.setLiquid(liquid);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if (this.tank.containsValidLiquid())
		{
			nbt.setTag("stored", this.tank.getLiquid().writeToNBT(new NBTTagCompound()));
		}
	}

	/**
	 * Is the internal tank full
	 */
	public boolean isFull()
	{
		if (this.tank.getLiquid() == null || this.tank.getLiquid().amount < this.tank.getCapacity())
		{
			return false;
		}
		return true;
	}

	/**
	 * gets the liquidStack stored in the internal tank
	 */
	public LiquidStack getStoredLiquid()
	{
		return this.tank.getLiquid();
	}

}
