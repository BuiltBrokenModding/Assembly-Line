package fluidmech.common.pump;

import hydraulic.helpers.FluidHelper;
import hydraulic.prefab.tile.TileEntityFluidDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;

public class TileEntityDrain extends TileEntityFluidDevice implements ITankContainer
{
	private ForgeDirection face = ForgeDirection.UNKNOWN;
	private boolean drainSources = true;

	/* LIST OF PUMPS AND THERE REQUESTS FOR THIS DRAIN */
	private HashMap<TileEntityConstructionPump, LiquidStack> requestMap = new HashMap<TileEntityConstructionPump, LiquidStack>();

	private List<Vector3> targetSources = new ArrayList<Vector3>();

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		return (drainSources ? "Draining" : "");
	}

	@Override
	public void updateEntity()
	{

		if (!this.worldObj.isRemote && this.ticks % 10 == 0)
		{
			/* MAIN LOGIC PATH FOR DRAINING BODIES OF LIQUID */
			if (this.drainSources)
			{
				TileEntity entity = VectorHelper.getTileEntityFromSide(worldObj, new Vector3(this), this.face.getOpposite());
				if (entity instanceof ITankContainer)
				{
					if (this.requestMap.size() > 0)
					{
						this.getNextFluidBlock();
						int blocksDrained = 0;
						for (Vector3 loc : targetSources)
						{
							if (blocksDrained >= 5 && FluidHelper.isStillLiquid(this.worldObj, loc))
							{
								LiquidStack stack = FluidHelper.getLiquidFromBlockId(loc.getBlockID(this.worldObj));
								if (stack != null)
								{
									for (Entry<TileEntityConstructionPump, LiquidStack> pump : requestMap.entrySet())
									{
										LiquidStack requestStack = pump.getValue();
										if (requestStack != null && (requestStack.isLiquidEqual(stack) || requestStack.itemID == -1))
										{
											if (((ITankContainer) entity).fill(0, stack, false) >= stack.amount)
											{
												((ITankContainer) entity).fill(0, stack, true);
												loc.setBlock(this.worldObj, 0);
												blocksDrained++;
											}
										}
									}
								}
							}
						}
					}
				}
			}// END OF DRAIN
			else
			{
				// TODO time to have fun finding a place for this block to exist
			}
		}
	}

	/**
	 * uses the LiquidStack to fill the area bellow the drain
	 * 
	 * @param stack - liquidStack
	 * @return amount of liquid consumed
	 */
	public int fillArea(LiquidStack stack)
	{
		return 0;
	}

	@Override
	public boolean canPipeConnect(TileEntity entity, ForgeDirection dir)
	{
		return dir == face.getOpposite();
	}

	/**
	 * Requests that this drain give the pump this liquid. The pump will have to decide if it can
	 * accept, request, and maintain this demand
	 * 
	 * @param pump - requesting pump
	 * @param stack - liquid this pump wants for this request
	 */
	public void requestLiquid(TileEntityConstructionPump pump, LiquidStack stack)
	{
		this.requestMap.put(pump, stack);
	}

	/**
	 * Request that this drain no longer supply the pump with a volume. By default a request will be
	 * removed from the request map after being filled. However, this can be used too stop a request
	 * short if the pump becomes full before the request is filled
	 * 
	 * @param pump - requesting pump
	 */
	public void stopRequesting(TileEntityConstructionPump pump)
	{
		if (this.requestMap.containsKey(pump))
		{
			this.requestMap.remove(pump);
		}
	}

	/**
	 * Finds at least 10 more liquid possible targets for the pump to pump
	 */
	public void getNextFluidBlock()
	{
		int blockID = Block.waterStill.blockID;
		int y = this.yCoord + 1;
		while (blockID == Block.waterStill.blockID)
		{
			blockID = worldObj.getBlockId(xCoord, y, zCoord);
			if (blockID == Block.waterStill.blockID)
			{
				y++;
			}

		}
		PathfinderCheckerLiquid pathFinder = new PathfinderCheckerLiquid(worldObj, 10, null, (Vector3[]) this.targetSources.toArray());
		pathFinder.init(new Vector3(xCoord, y, zCoord));
		for (Vector3 loc : pathFinder.closedSet)
		{
			if (!this.targetSources.contains(loc))
			{
				this.targetSources.add(loc);
			}
		}
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
