package fluidmech.common.pump;

import hydraulic.fluidnetwork.IFluidNetworkPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.tile.TileEntityElectricityRunnable;

public class TileEntityConstructionPump extends TileEntityElectricityRunnable implements ITankContainer
{
	/* ENERGY PER TICK TO TRY TO PUMP */
	public static final double WATTS_PER_TICK = 100;
	/* LIQUID FLOW CONNECTION SIDES */
	private ForgeDirection outputSide = ForgeDirection.UNKNOWN;
	private ForgeDirection inputSide = ForgeDirection.UNKNOWN;
	/* Fake Internal Tank */
	private LiquidTank fakeTank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME);
	private int liquidRequest = 1;

	@Override
	public void updateEntity()
	{
		if (!worldObj.isRemote && this.wattsReceived >= this.WATTS_PER_TICK)
		{
			if (this.ticks % 10 == 0)
			{
				boolean called = false;
				TileEntity inputTile = VectorHelper.getTileEntityFromSide(worldObj, new Vector3(this), inputSide);
				TileEntity outputTile = VectorHelper.getTileEntityFromSide(worldObj, new Vector3(this), outputSide);
				if (inputTile instanceof IFluidNetworkPart)
				{
					if (outputTile instanceof ITankContainer)
					{
						for (ITankContainer tank : ((IFluidNetworkPart) inputTile).getNetwork().fluidTanks)
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
		return direction != outputSide && direction != inputSide;
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		if (from != inputSide)
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
		TileEntity entity = VectorHelper.getTileEntityFromSide(this.worldObj, new Vector3(this), outputSide);
		if (entity instanceof ITankContainer)
		{
			return ((ITankContainer) entity).fill(outputSide.getOpposite(), resource, doFill);
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
		if (direction == this.inputSide)
		{
			return new ILiquidTank[] { fakeTank };
		}
		return null;
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
	{
		if (direction == this.inputSide)
		{
			return fakeTank;
		}
		return null;
	}

}
