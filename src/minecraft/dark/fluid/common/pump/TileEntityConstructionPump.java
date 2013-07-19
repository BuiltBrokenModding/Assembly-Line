package dark.fluid.common.pump;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import dark.api.fluid.INetworkPipe;
import dark.core.api.ITileConnector;
import dark.core.network.fluid.HydraulicNetworkHelper;
import dark.core.network.fluid.NetworkFluidTiles;
import dark.helpers.MetaGroup;
import dark.library.machine.TileEntityRunnableMachine;

public class TileEntityConstructionPump extends TileEntityRunnableMachine implements IFluidHandler, ITileConnector
{
	/* LIQUID FLOW CONNECTION SIDES */
	/** Internal tank for interaction but not real storage */
	private FluidTank fakeTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
	private int liquidRequest = 5;
	public int rotation = 0;

	public TileEntityConstructionPump()
	{
		super(100);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initiate()
	{
		// TODO if use wrench to change rotation have it call this
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
	}

	public ForgeDirection getFacing(boolean input)
	{
		int meta = 0;
		if (worldObj != null)
		{
			meta = MetaGroup.getFacingMeta(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		}
		if (input)
		{
			return ForgeDirection.getOrientation(meta);
		}
		else
		{
			return ForgeDirection.getOrientation(meta).getOpposite();
		}

	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if (!worldObj.isRemote)
		{
			if (this.ticks % 10 == 0 && this.canRun())
			{
				this.rotation += 1;
				if (rotation >= 7)
				{
					rotation = 0;
				}
				boolean called = false;

				TileEntity inputTile = VectorHelper.getTileEntityFromSide(worldObj, new Vector3(this), getFacing(true));
				TileEntity outputTile = VectorHelper.getTileEntityFromSide(worldObj, new Vector3(this), getFacing(false));
				if (inputTile instanceof INetworkPipe && ((INetworkPipe) inputTile).getTileNetwork() instanceof NetworkFluidTiles)
				{
					if (outputTile instanceof IFluidHandler)
					{
						for (IFluidHandler tank : ((NetworkFluidTiles) ((INetworkPipe) inputTile).getTileNetwork()).connectedTanks)
						{
							if (tank instanceof TileEntityDrain)
							{
								((TileEntityDrain) tank).requestLiquid(this, null, liquidRequest * FluidContainerRegistry.BUCKET_VOLUME);
								called = true;
							}

						}
					}
				}

			}
		}
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return direction != getFacing(true) && direction != getFacing(false);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if (from != getFacing(true))
		{
			return 0;
		}
		TileEntity entity = VectorHelper.getTileEntityFromSide(this.worldObj, new Vector3(this), getFacing(false));
		if (entity instanceof IFluidHandler)
		{
			return ((IFluidHandler) entity).fill(getFacing(false).getOpposite(), resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection direction)
	{
		if (direction == this.getFacing(false) && this.fakeTank != null)
		{
			return new FluidTankInfo[] { new FluidTankInfo(fakeTank.getFluid(), fakeTank.getCapacity()) };
		}
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return from != getFacing(true);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return false;
	}

	@Override
	public boolean canTileConnect(TileEntity entity, ForgeDirection dir)
	{
		return entity instanceof IFluidHandler && (dir == this.getFacing(false) || dir == this.getFacing(true));
	}

	@Override
	public void invalidate()
	{
		super.invalidate();
		HydraulicNetworkHelper.invalidate(this);
	}

}
