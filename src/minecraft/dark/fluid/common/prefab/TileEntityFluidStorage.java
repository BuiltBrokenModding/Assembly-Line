package dark.fluid.common.prefab;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import dark.api.IColorCoded;
import dark.helpers.FluidHelper;
import dark.helpers.FluidRestrictionHandler;

public abstract class TileEntityFluidStorage extends TileEntityFluidDevice implements IFluidHandler, IColorCoded
{
	/* INTERNAL TANK */
	public FluidTank tank = new FluidTank(this.getTankSize());

	/** gets the max storage limit of the tank */
	public abstract int getTankSize();

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side, EnumTools tool)
	{
		if (tool != EnumTools.PIPE_GUAGE)
		{
			return null;
		}
		if (this.tank.getFluid() == null)
		{
			return "Empty";
		}
		return String.format("%d/%d %S Stored", tank.getFluid().amount / FluidContainerRegistry.BUCKET_VOLUME, tank.getCapacity() / FluidContainerRegistry.BUCKET_VOLUME, tank.getFluid().getFluid().getLocalizedName());
	}

	@Override
	public boolean canTileConnect(TileEntity entity, ForgeDirection dir)
	{
		return entity instanceof IFluidHandler;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if (resource == null || resource.getFluid() == null || !FluidRestrictionHandler.isValidLiquid(getColor(), resource.getFluid()))
		{
			return 0;
		}
		else if (this.tank.getFluid() != null && !resource.isFluidEqual(this.tank.getFluid()))
		{
			return 0;
		}
		return this.tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		if (this.tank.getFluid() == null)
		{
			return null;
		}
		FluidStack stack = this.tank.getFluid();
		if (maxDrain < stack.amount)
		{
			stack = FluidHelper.getStack(stack, maxDrain);
		}
		return this.tank.drain(maxDrain, doDrain);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		if (this.tank != null)
		{
			return new FluidTankInfo[] { new FluidTankInfo(this.tank) };
		}
		return new FluidTankInfo[1];
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("FluidTank"));
		if (nbt.hasKey("stored"))
		{
			NBTTagCompound tag = nbt.getCompoundTag("stored");
			String name = tag.getString("LiquidName");
			int amount = nbt.getInteger("Amount");
			Fluid fluid = FluidRegistry.getFluid(name);
			if (fluid != null)
			{
				liquid = new FluidStack(fluid, amount);
			}
		}
		if (liquid != null)
		{
			tank.setFluid(liquid);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if (this.tank != null && this.tank.getFluid() != null)
		{
			nbt.setTag("FluidTank", this.tank.getFluid().writeToNBT(new NBTTagCompound()));
		}
	}

	/** Is the internal tank full */
	public boolean isFull()
	{
		return this.tank.getFluid() != null && this.tank.getFluid().amount >= this.tank.getCapacity();
	}

	/** gets the liquidStack stored in the internal tank */
	public FluidStack getStoredLiquid()
	{
		return this.tank.getFluid();
	}

}
