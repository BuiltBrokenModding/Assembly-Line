package fluidmech.common.pump;

import hydraulic.api.IDrain;
import hydraulic.fluidnetwork.IFluidNetworkPart;
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

public class TileEntityDrain extends TileEntityFluidDevice implements ITankContainer, IDrain
{
	private boolean drainSources = true;
	/* MAX BLOCKS DRAINED PER 1/2 SECOND */
	public static int MAX_DRAIN_PER_PROCESS = 15;
	private int currentDrains = 0;
	/* LIST OF PUMPS AND THERE REQUESTS FOR THIS DRAIN */
	private HashMap<TileEntityConstructionPump, LiquidStack> requestMap = new HashMap<TileEntityConstructionPump, LiquidStack>();

	private List<Vector3> targetSources = new ArrayList<Vector3>();

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		return (drainSources ? "Draining" : "Filling");
	}

	public ForgeDirection getFacing()
	{
		int meta = 0;
		if (worldObj != null)
		{
			meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		}
		return ForgeDirection.getOrientation(meta);
	}

	@Override
	public void updateEntity()
	{

		if (!this.worldObj.isRemote && this.ticks % 10 == 0)
		{
			this.currentDrains = 0;
			/* MAIN LOGIC PATH FOR DRAINING BODIES OF LIQUID */
			if (this.drainSources)
			{
				TileEntity pipe = VectorHelper.getTileEntityFromSide(worldObj, new Vector3(this), this.getFacing().getOpposite());

				if (pipe instanceof IFluidNetworkPart)
				{
					if (this.requestMap.size() > 0)
					{
						this.getNextFluidBlock();

						for (Entry<TileEntityConstructionPump, LiquidStack> request : requestMap.entrySet())
						{
							if (this.currentDrains >= MAX_DRAIN_PER_PROCESS)
							{
								break;
							}
							if (((IFluidNetworkPart) pipe).getNetwork().isConnected(request.getKey()))
							{
								for (Vector3 loc : targetSources)
								{
									if (this.currentDrains >= MAX_DRAIN_PER_PROCESS)
									{
										break;
									}

									if (FluidHelper.isLiquidBlock(this.worldObj, loc))
									{
										LiquidStack stack = FluidHelper.getLiquidFromBlockId(loc.getBlockID(this.worldObj));
										LiquidStack requestStack = request.getValue();

										if (stack != null && requestStack != null && (requestStack.isLiquidEqual(stack) || requestStack.itemID == -1))
										{
											if (request.getKey().fill(0, stack, false) >= stack.amount)
											{
												int requestAmmount = requestStack.amount - request.getKey().fill(0, stack, true);
												if (requestAmmount <= 0)
												{
													this.requestMap.remove(request);
												}
												else
												{
													request.setValue(FluidHelper.getStack(requestStack, requestAmmount));
												}

												loc.setBlock(this.worldObj, 0);
												this.currentDrains++;
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

	@Override
	public int fillArea(LiquidStack stack, boolean doFill)
	{
		if (!this.drainSources)
		{

		}
		return 0;
	}

	@Override
	public boolean canPipeConnect(TileEntity entity, ForgeDirection dir)
	{
		return dir == getFacing().getOpposite();
	}

	@Override
	public void requestLiquid(TileEntityConstructionPump pump, LiquidStack stack)
	{
		this.requestMap.put(pump, stack);
	}

	@Override
	public void stopRequesting(TileEntityConstructionPump pump)
	{
		if (this.requestMap.containsKey(pump))
		{
			this.requestMap.remove(pump);
		}
	}

	/**
	 * Finds more liquid blocks using a path finder to be drained
	 */
	public void getNextFluidBlock()
	{
		/* FIND HIGHEST DRAIN POINT FIRST */
		PathfinderFindHighestBlock path = new PathfinderFindHighestBlock(this.worldObj, Block.waterStill.blockID);
		path.init(new Vector3(xCoord + this.getFacing().offsetX, yCoord + this.getFacing().offsetY, zCoord + this.getFacing().offsetZ));
		int y = path.highestY;

		/* FIND 10 UNMARKED SOURCES */
		PathfinderCheckerLiquid pathFinder = new PathfinderCheckerLiquid(worldObj, this.MAX_DRAIN_PER_PROCESS, null, (Vector3[]) this.targetSources.toArray());
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
		if (this.drainSources || from != this.getFacing().getOpposite())
		{
			return 0;
		}
		return this.fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (resource == null || tankIndex != 0)
		{
			return 0;
		}
		return this.fillArea(resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		if (from != this.getFacing().getOpposite())
		{
			return null;
		}
		return this.drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction)
	{
		return null;
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
	{
		return null;
	}
}
