package dark.fluid.common.pump;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import dark.core.api.ITileConnector;
import dark.core.network.fluid.HydraulicNetworkHelper;
import dark.core.network.fluid.NetworkFluidTiles;
import dark.fluid.api.INetworkPipe;
import dark.helpers.MetaGroup;
import dark.library.machine.TileEntityRunnableMachine;

public class TileEntityConstructionPump extends TileEntityRunnableMachine implements ITankContainer, ITileConnector
{
	/* ENERGY PER TICK TO TRY TO PUMP */
	public static final double WATTS_PER_TICK = 100;
	/* LIQUID FLOW CONNECTION SIDES */
	/* Fake Internal Tank */
	private LiquidTank fakeTank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME);
	private int liquidRequest = 5;
	public int rotation = 0;

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
			if (this.ticks % 10 == 0 && this.wattsReceived >= this.WATTS_PER_TICK) // TODO add
																					// electric
																					// Drain
			{
				this.wattsReceived -= this.WATTS_PER_TICK;
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
					if (outputTile instanceof ITankContainer)
					{
						for (ITankContainer tank : ((NetworkFluidTiles) ((INetworkPipe) inputTile).getTileNetwork()).connectedTanks)
						{
							if (tank instanceof TileEntityDrain)
							{
								((TileEntityDrain) tank).requestLiquid(this, new LiquidStack(-1, liquidRequest * LiquidContainerRegistry.BUCKET_VOLUME));
								called = true;
							}

						}
					}
				}

			}
		}
	}

	@Override
	public ElectricityPack getRequest()
	{
		return new ElectricityPack(WATTS_PER_TICK / this.getVoltage(), this.getVoltage());
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return direction != getFacing(true) && direction != getFacing(false);
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		if (from != getFacing(true))
		{
			return 0;
		}
		return this.fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (tankIndex != 0 || resource == null)
		{
			return 0;
		}
		TileEntity entity = VectorHelper.getTileEntityFromSide(this.worldObj, new Vector3(this), getFacing(false));
		if (entity instanceof ITankContainer)
		{
			return ((ITankContainer) entity).fill(getFacing(false).getOpposite(), resource, doFill);
		}
		return 0;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		// TODO maybe have it make a request for liquid if something tries to drain it
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction)
	{
		if (direction == this.getFacing(false))
		{
			return new ILiquidTank[] { fakeTank };
		}
		return null;
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
	{
		if (direction == this.getFacing(false))
		{
			return fakeTank;
		}
		return null;
	}

	@Override
	public boolean canTileConnect(TileEntity entity, ForgeDirection dir)
	{
		return entity instanceof ITankContainer && (dir == this.getFacing(false) || dir == this.getFacing(true));
	}

	@Override
	public void invalidate()
	{
		super.invalidate();
		HydraulicNetworkHelper.invalidate(this);
	}
}
